/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.data.local.db

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.supernovea.spendsense.domain.model.CategoryGroup
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration1To2Test {

    @Test
    fun `migration versions are 1 to 2`() {
        assertEquals(1, AppDatabase.MIGRATION_1_2.startVersion)
        assertEquals(2, AppDatabase.MIGRATION_1_2.endVersion)
    }

    @Test
    fun `default categories have unique non-null defaultKeys and valid groups`() {
        val keys = DefaultCategories.items.mapNotNull { it.defaultKey }
        assertEquals(DefaultCategories.items.size, keys.size)
        assertEquals("All defaultKeys must be unique", keys.size, keys.toSet().size)

        for (cat in DefaultCategories.items) {
            val group = CategoryGroup.fromKey(cat.groupKey)
            assertNotNull(group)
            assertTrue("Name must not be empty", cat.name.isNotBlank())
            assertTrue("Icon key must not be empty", cat.iconKey.isNotBlank())
            assertTrue("Color hex must start with #", cat.colorHex.startsWith("#"))
        }
    }

    @Test
    fun `default income sources have unique keys and names`() {
        assertEquals(14, DefaultIncomeSources.items.size)
        val keys = DefaultIncomeSources.items.mapNotNull { it.defaultKey }
        assertEquals(14, keys.size)
        assertEquals("All income source defaultKeys must be unique", keys.size, keys.toSet().size)

        val names = DefaultIncomeSources.items.map { it.name }
        assertEquals("All income source names must be unique", names.size, names.toSet().size)
    }

    @Test
    fun `migration executes alter table and creates income sources table`() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)
        val executedSql = mutableListOf<String>()

        every { db.execSQL(capture(executedSql)) } returns Unit

        val mockStatement = mockk<SupportSQLiteStatement>(relaxed = true)
        every { db.compileStatement(any()) } returns mockStatement

        // Cursor mocking for exists check
        val emptyCursor = mockk<android.database.Cursor>(relaxed = true)
        every { emptyCursor.moveToFirst() } returns false
        every { db.query(any<String>(), any()) } returns emptyCursor

        AppDatabase.MIGRATION_1_2.migrate(db)

        // Verify ALTER TABLE categories
        assertTrue(
            "Must add default_key column",
            executedSql.any { it.contains("ALTER TABLE categories ADD COLUMN default_key TEXT") }
        )
        assertTrue(
            "Must add group_key column",
            executedSql.any { it.contains("ALTER TABLE categories ADD COLUMN group_key TEXT") }
        )
        assertTrue(
            "Must create index on default_key",
            executedSql.any { it.contains("index_categories_default_key") }
        )

        // Verify CREATE TABLE income_sources
        assertTrue(
            "Must create income_sources table",
            executedSql.any { it.contains("CREATE TABLE IF NOT EXISTS income_sources") }
        )
        assertTrue(
            "Must create index on income_sources name",
            executedSql.any { it.contains("index_income_sources_name") }
        )
        assertTrue(
            "Must create index on income_sources default_key",
            executedSql.any { it.contains("index_income_sources_default_key") }
        )

        // Verify CREATE TABLE incomes
        assertTrue(
            "Must create incomes table",
            executedSql.any { it.contains("CREATE TABLE IF NOT EXISTS incomes") }
        )
        assertTrue(
            "Must create index on incomes source_id",
            executedSql.any { it.contains("index_incomes_source_id") }
        )
        assertTrue(
            "Must create index on incomes received_date",
            executedSql.any { it.contains("index_incomes_received_date") }
        )
    }

    @Test
    fun `migration binds legacy mapping for all 14 original default categories`() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)
        val updateStatements = mutableListOf<String>()
        val boundStrings = mutableListOf<String>()

        val mockUpdateStmt = mockk<SupportSQLiteStatement>(relaxed = true)
        every { mockUpdateStmt.bindString(any(), any()) } answers {
            boundStrings.add(secondArg())
        }

        every { db.compileStatement(capture(updateStatements)) } returns mockUpdateStmt

        val emptyCursor = mockk<android.database.Cursor>(relaxed = true)
        every { emptyCursor.moveToFirst() } returns true // Simulate already exists so no inserts
        every { db.query(any<String>(), any()) } returns emptyCursor

        AppDatabase.MIGRATION_1_2.migrate(db)

        // Verify the 14 legacy keys are mapped
        val expectedLegacyKeys = listOf(
            "food_meals", "groceries", "transport", "education_fees",
            "rent_hostel", "bills_recharge", "health", "snacks",
            "eating_out", "entertainment", "gaming", "shopping",
            "subscriptions", "other"
        )

        for (key in expectedLegacyKeys) {
            assertTrue("Expected legacy key $key to be bound", boundStrings.contains(key))
        }
    }
}
