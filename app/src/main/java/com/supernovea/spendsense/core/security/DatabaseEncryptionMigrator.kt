/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.content.Context
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object DatabaseEncryptionMigrator {

    private val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)

    /**
     * Checks if a database file is an unencrypted standard SQLite database.
     * Standard SQLite files begin with the 16-byte ASCII string "SQLite format 3\0".
     * SQLCipher encrypted databases have high-entropy random bytes (salt) instead.
     */
    fun isPlaintextDatabase(dbFile: File): Boolean {
        if (!dbFile.exists() || !dbFile.isFile || dbFile.length() < 16) {
            return false
        }
        return try {
            FileInputStream(dbFile).use { input ->
                val header = ByteArray(16)
                val bytesRead = input.read(header)
                if (bytesRead < 16) false else header.contentEquals(SQLITE_HEADER)
            }
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Ensures that the database file is encrypted with SQLCipher.
     * If the database does not exist or is already encrypted, this is a no-op.
     * If an existing plaintext database is detected, it is migrated to SQLCipher
     * with schema and row-count verification before replacing the original file.
     */
    @Synchronized
    fun ensureEncrypted(
        context: Context,
        databaseName: String,
        databaseKeyManager: DatabaseKeyManager,
    ) {
        val dbFile = context.getDatabasePath(databaseName)
        if (!dbFile.exists() || dbFile.length() == 0L) {
            // New database: passphrase will be used directly on creation
            return
        }

        if (!isPlaintextDatabase(dbFile)) {
            // Already encrypted
            return
        }

        System.loadLibrary("sqlcipher")

        val backupFile = File(context.noBackupFilesDir, "$databaseName.plaintext.bak")
        val tempEncryptedFile = File(context.noBackupFilesDir, "$databaseName.encrypted.tmp")

        val passphrase = databaseKeyManager.getDatabasePassphrase()
        try {
            migratePlaintextToEncrypted(
                dbFile = dbFile,
                tempEncryptedFile = tempEncryptedFile,
                backupFile = backupFile,
                passphrase = passphrase,
            )
        } finally {
            passphrase.fill(0)
        }
    }

    /**
     * Migrates a plaintext SQLite database to an encrypted SQLCipher database.
     *
     * 1. Creates a safety backup of the plaintext database.
     * 2. Opens the target temporary database with SQLCipher and the provided passphrase.
     * 3. Attaches the plaintext database with an empty key.
     * 4. Copies PRAGMA user_version to preserve Room's schema version.
     * 5. Exports all schemas and data using `sqlcipher_export`.
     * 6. Detaches the plaintext database.
     * 7. Verifies row counts for core tables across plaintext and encrypted databases.
     * 8. Atomically replaces the plaintext file with the verified encrypted file.
     * 9. Cleans up temporary and WAL/SHM files.
     */
    fun migratePlaintextToEncrypted(
        dbFile: File,
        tempEncryptedFile: File,
        backupFile: File,
        passphrase: ByteArray,
    ) {
        // Step 1: Safety backup
        dbFile.copyTo(backupFile, overwrite = true)

        if (tempEncryptedFile.exists()) {
            tempEncryptedFile.delete()
        }

        var encryptedDb: SQLiteDatabase? = null
        try {
            // Step 2: Open target encrypted database
            encryptedDb = SQLiteDatabase.openOrCreateDatabase(tempEncryptedFile, passphrase, null, null)

            // Step 3: Attach plaintext database
            val plaintextPath = dbFile.absolutePath.replace("'", "''")
            encryptedDb.rawExecSQL("ATTACH DATABASE '$plaintextPath' AS plaintext KEY '';")

            // Step 4: Preserve Room user_version (sqlcipher_export does not copy user_version)
            var userVersion = 0
            val noSelectionArgs: Array<String>? = null
            encryptedDb.rawQuery("PRAGMA plaintext.user_version;", noSelectionArgs).use { cursor ->
                if (cursor.moveToFirst()) {
                    userVersion = cursor.getInt(0)
                }
            }
            encryptedDb.rawExecSQL("PRAGMA user_version = $userVersion;")

            // Step 5: Read plaintext row counts
            val plainExpenseCount = countRows(encryptedDb, "plaintext", "expenses")
            val plainCategoryCount = countRows(encryptedDb, "plaintext", "categories")
            val plainBudgetCount = countRows(encryptedDb, "plaintext", "budgets")

            // Step 6: Export schema and data
            encryptedDb.rawExecSQL("SELECT sqlcipher_export('main', 'plaintext');")

            // Step 7: Detach plaintext database
            encryptedDb.rawExecSQL("DETACH DATABASE plaintext;")

            // Step 8: Verify row counts in the encrypted database
            val encExpenseCount = countRows(encryptedDb, "main", "expenses")
            val encCategoryCount = countRows(encryptedDb, "main", "categories")
            val encBudgetCount = countRows(encryptedDb, "main", "budgets")

            if (plainExpenseCount >= 0 && encExpenseCount != plainExpenseCount) {
                throw IllegalStateException(
                    "Expense count mismatch after encryption migration: " +
                        "expected $plainExpenseCount, got $encExpenseCount"
                )
            }
            if (plainCategoryCount >= 0 && encCategoryCount != plainCategoryCount) {
                throw IllegalStateException(
                    "Category count mismatch after encryption migration: " +
                        "expected $plainCategoryCount, got $encCategoryCount"
                )
            }
            if (plainBudgetCount >= 0 && encBudgetCount != plainBudgetCount) {
                throw IllegalStateException(
                    "Budget count mismatch after encryption migration: " +
                        "expected $plainBudgetCount, got $encBudgetCount"
                )
            }

            // Close encrypted database before swapping files
            encryptedDb.close()
            encryptedDb = null

            // Step 9: Remove plaintext journal/wal files
            val walFile = File("${dbFile.absolutePath}-wal")
            val shmFile = File("${dbFile.absolutePath}-shm")
            val journalFile = File("${dbFile.absolutePath}-journal")
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()
            if (journalFile.exists()) journalFile.delete()

            // Step 10: Replace original plaintext file with verified encrypted file
            if (dbFile.exists()) {
                dbFile.delete()
            }
            if (!tempEncryptedFile.renameTo(dbFile)) {
                tempEncryptedFile.copyTo(dbFile, overwrite = true)
                tempEncryptedFile.delete()
            }

            // Verify the new file is no longer plaintext
            if (isPlaintextDatabase(dbFile)) {
                throw IllegalStateException("Database file is still plaintext after migration")
            }

            // Migration successful: remove safety backup
            if (backupFile.exists()) {
                backupFile.delete()
            }
        } catch (e: Exception) {
            // Close DB if open
            try {
                encryptedDb?.close()
            } catch (_: Exception) {}

            // Clean up temporary encrypted file
            if (tempEncryptedFile.exists()) {
                tempEncryptedFile.delete()
            }

            // Restore from backup if dbFile was deleted or corrupted
            if ((!dbFile.exists() || isPlaintextDatabase(backupFile)) && backupFile.exists()) {
                backupFile.copyTo(dbFile, overwrite = true)
            }

            throw IllegalStateException("Failed to migrate plaintext database to SQLCipher: ${e.message}", e)
        }
    }

    private fun countRows(db: SQLiteDatabase, schema: String, table: String): Long {
        return try {
            val noSelectionArgs: Array<String>? = null
            db.rawQuery("SELECT count(*) FROM $schema.$table;", noSelectionArgs).use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else 0L
            }
        } catch (e: Exception) {
            -1L // Table may not exist in earlier schemas
        }
    }
}
