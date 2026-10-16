/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.supernovea.spendsense.core.security.DatabaseEncryptionMigrator
import com.supernovea.spendsense.core.security.DatabaseKeyManager
import com.supernovea.spendsense.data.local.dao.BudgetDao
import com.supernovea.spendsense.data.local.dao.CategoryDao
import com.supernovea.spendsense.data.local.dao.ExpenseDao
import com.supernovea.spendsense.data.local.dao.IncomeDao
import com.supernovea.spendsense.data.local.dao.IncomeSourceDao
import com.supernovea.spendsense.data.local.entity.BudgetEntity
import com.supernovea.spendsense.data.local.entity.CategoryEntity
import com.supernovea.spendsense.data.local.entity.ExpenseEntity
import com.supernovea.spendsense.data.local.entity.IncomeEntity
import com.supernovea.spendsense.data.local.entity.IncomeSourceEntity
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        IncomeSourceEntity::class,
        IncomeEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun incomeSourceDao(): IncomeSourceDao
    abstract fun incomeDao(): IncomeDao

    companion object {
        const val DATABASE_NAME = "spendsense.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Alter categories table
                db.execSQL("ALTER TABLE categories ADD COLUMN default_key TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE categories ADD COLUMN group_key TEXT NOT NULL DEFAULT 'other'")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_categories_default_key ON categories (default_key)")

                // 2. Map existing 14 defaults by name
                mapExistingCategoryDefaults(db)

                // 3. Insert missing default categories
                insertMissingDefaultCategories(db)

                // 4. Create income_sources table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS income_sources (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        default_key TEXT,
                        name TEXT NOT NULL,
                        icon_key TEXT NOT NULL,
                        color_hex TEXT NOT NULL,
                        is_archived INTEGER NOT NULL DEFAULT 0,
                        is_default INTEGER NOT NULL DEFAULT 0,
                        sort_order INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_income_sources_name ON income_sources (name)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_income_sources_default_key ON income_sources (default_key)")

                // 5. Seed default income sources
                seedDefaultIncomeSources(db)

                // 6. Create incomes table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS incomes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount_minor INTEGER NOT NULL,
                        source_id INTEGER NOT NULL,
                        note TEXT,
                        received_at INTEGER NOT NULL,
                        received_date INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL,
                        deleted_at INTEGER,
                        recurring_rule_id INTEGER,
                        FOREIGN KEY(source_id) REFERENCES income_sources(id) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_incomes_source_id ON incomes (source_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_incomes_received_date ON incomes (received_date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_incomes_source_id_received_date ON incomes (source_id, received_date)")
            }
        }

        fun build(context: Context, databaseKeyManager: DatabaseKeyManager): AppDatabase {
            System.loadLibrary("sqlcipher")
            DatabaseEncryptionMigrator.ensureEncrypted(context, DATABASE_NAME, databaseKeyManager)

            val passphrase = databaseKeyManager.getDatabasePassphrase()
            val openHelperFactory = SupportOpenHelperFactory(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME,
            ).openHelperFactory(openHelperFactory)
            .addMigrations(MIGRATION_1_2)
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    seedDefaultCategories(db)
                    seedDefaultIncomeSources(db)
                }
            }).build()
        }

        fun seedDefaultCategories(db: SupportSQLiteDatabase) {
            db.beginTransaction()
            val stmt = db.compileStatement(
                "INSERT INTO categories (default_key, group_key, name, icon_key, color_hex, default_type, is_archived, is_default, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            )
            try {
                for (cat in DefaultCategories.items) {
                    stmt.clearBindings()
                    if (cat.defaultKey != null) stmt.bindString(1, cat.defaultKey) else stmt.bindNull(1)
                    stmt.bindString(2, cat.groupKey)
                    stmt.bindString(3, cat.name)
                    stmt.bindString(4, cat.iconKey)
                    stmt.bindString(5, cat.colorHex)
                    stmt.bindString(6, cat.defaultType)
                    stmt.bindLong(7, if (cat.isArchived) 1L else 0L)
                    stmt.bindLong(8, if (cat.isDefault) 1L else 0L)
                    stmt.bindLong(9, cat.sortOrder.toLong())
                    stmt.executeInsert()
                }
                db.setTransactionSuccessful()
            } finally {
                stmt.close()
                db.endTransaction()
            }
        }

        fun seedDefaultIncomeSources(db: SupportSQLiteDatabase) {
            db.beginTransaction()
            val stmt = db.compileStatement(
                "INSERT INTO income_sources (default_key, name, icon_key, color_hex, is_archived, is_default, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?)"
            )
            try {
                for (source in DefaultIncomeSources.items) {
                    stmt.clearBindings()
                    if (source.defaultKey != null) stmt.bindString(1, source.defaultKey) else stmt.bindNull(1)
                    stmt.bindString(2, source.name)
                    stmt.bindString(3, source.iconKey)
                    stmt.bindString(4, source.colorHex)
                    stmt.bindLong(5, if (source.isArchived) 1L else 0L)
                    stmt.bindLong(6, if (source.isDefault) 1L else 0L)
                    stmt.bindLong(7, source.sortOrder.toLong())
                    stmt.executeInsert()
                }
                db.setTransactionSuccessful()
            } finally {
                stmt.close()
                db.endTransaction()
            }
        }

        private fun mapExistingCategoryDefaults(db: SupportSQLiteDatabase) {
            val legacyMapping = listOf(
                Triple("Food", "food_meals", "food_drink"),
                Triple("Groceries", "groceries", "food_drink"),
                Triple("Transport", "transport", "transport"),
                Triple("Education", "education_fees", "education_work"),
                Triple("Rent / Hostel", "rent_hostel", "housing_bills"),
                Triple("Bills and Recharge", "bills_recharge", "housing_bills"),
                Triple("Health", "health", "health_personal"),
                Triple("Snacks and Treats", "snacks", "food_drink"),
                Triple("Eating Out", "eating_out", "food_drink"),
                Triple("Entertainment", "entertainment", "lifestyle_fun"),
                Triple("Gaming", "gaming", "lifestyle_fun"),
                Triple("Shopping", "shopping", "lifestyle_fun"),
                Triple("Subscriptions", "subscriptions", "lifestyle_fun"),
                Triple("Other", "other", "other"),
            )

            for ((name, defaultKey, groupKey) in legacyMapping) {
                val stmt = db.compileStatement(
                    "UPDATE categories SET default_key = ?, group_key = ? WHERE name = ? AND default_key IS NULL"
                )
                stmt.bindString(1, defaultKey)
                stmt.bindString(2, groupKey)
                stmt.bindString(3, name)
                stmt.executeUpdateDelete()
            }
        }

        private fun insertMissingDefaultCategories(db: SupportSQLiteDatabase) {
            for (cat in DefaultCategories.items) {
                val defaultKey = cat.defaultKey ?: continue
                val cursor = db.query(
                    "SELECT id FROM categories WHERE default_key = ? LIMIT 1",
                    arrayOf(defaultKey)
                )
                val exists = cursor.moveToFirst()
                cursor.close()

                if (!exists) {
                    // Check if a category with this name already exists (user might have created it)
                    val nameCursor = db.query(
                        "SELECT id FROM categories WHERE name = ? LIMIT 1",
                        arrayOf(cat.name)
                    )
                    val nameExists = nameCursor.moveToFirst()
                    nameCursor.close()

                    if (nameExists) {
                        // User created or edited a category with this name: just associate default_key
                        val updateStmt = db.compileStatement(
                            "UPDATE categories SET default_key = ?, group_key = ? WHERE name = ? AND default_key IS NULL"
                        )
                        updateStmt.bindString(1, defaultKey)
                        updateStmt.bindString(2, cat.groupKey)
                        updateStmt.bindString(3, cat.name)
                        updateStmt.executeUpdateDelete()
                    } else {
                        // Insert new category
                        val insertStmt = db.compileStatement(
                            "INSERT INTO categories (default_key, group_key, name, icon_key, color_hex, default_type, is_archived, is_default, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )
                        insertStmt.bindString(1, defaultKey)
                        insertStmt.bindString(2, cat.groupKey)
                        insertStmt.bindString(3, cat.name)
                        insertStmt.bindString(4, cat.iconKey)
                        insertStmt.bindString(5, cat.colorHex)
                        insertStmt.bindString(6, cat.defaultType)
                        insertStmt.bindLong(7, if (cat.isArchived) 1L else 0L)
                        insertStmt.bindLong(8, if (cat.isDefault) 1L else 0L)
                        insertStmt.bindLong(9, cat.sortOrder.toLong())
                        insertStmt.executeInsert()
                    }
                }
            }
        }
    }
}
