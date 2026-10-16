/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import net.zetetic.database.sqlcipher.SQLiteDatabase
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.SecureRandom

class DatabaseEncryptionMigratorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isPlaintextDatabase returns false when file does not exist`() {
        val nonExistent = File(tempFolder.root, "does_not_exist.db")
        assertFalse(DatabaseEncryptionMigrator.isPlaintextDatabase(nonExistent))
    }

    @Test
    fun `isPlaintextDatabase returns false for empty file`() {
        val emptyFile = tempFolder.newFile("empty.db")
        assertFalse(DatabaseEncryptionMigrator.isPlaintextDatabase(emptyFile))
    }

    @Test
    fun `isPlaintextDatabase returns false for file shorter than 16 bytes`() {
        val shortFile = tempFolder.newFile("short.db")
        FileOutputStream(shortFile).use { it.write("SQLite".toByteArray(Charsets.US_ASCII)) }
        assertFalse(DatabaseEncryptionMigrator.isPlaintextDatabase(shortFile))
    }

    @Test
    fun `isPlaintextDatabase returns true for valid SQLite header`() {
        val sqliteFile = tempFolder.newFile("plaintext.db")
        val header = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
        val dummyData = ByteArray(100) { 1 }
        FileOutputStream(sqliteFile).use {
            it.write(header)
            it.write(dummyData)
        }

        assertTrue(DatabaseEncryptionMigrator.isPlaintextDatabase(sqliteFile))
    }

    @Test
    fun `isPlaintextDatabase returns false for encrypted high-entropy database`() {
        val encryptedFile = tempFolder.newFile("encrypted.db")
        val randomHeader = ByteArray(1024).apply {
            SecureRandom().nextBytes(this)
        }
        FileOutputStream(encryptedFile).use { it.write(randomHeader) }

        assertFalse(DatabaseEncryptionMigrator.isPlaintextDatabase(encryptedFile))
    }

    @Test
    fun `isPlaintextDatabase returns false when header differs by single character`() {
        val corruptFile = tempFolder.newFile("corrupt.db")
        // "SQLite format 4\0" instead of 3
        val header = "SQLite format 4\u0000".toByteArray(Charsets.US_ASCII)
        FileOutputStream(corruptFile).use { it.write(header) }

        assertFalse(DatabaseEncryptionMigrator.isPlaintextDatabase(corruptFile))
    }

    @Test
    fun `rollback restores original plaintext database on failure`() {
        val dbFile = tempFolder.newFile("test_spendsense.db")
        val header = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
        val originalPayload = "TEST_PLAINTEXT_DATA_PAYLOAD".toByteArray(Charsets.UTF_8)
        FileOutputStream(dbFile).use {
            it.write(header)
            it.write(originalPayload)
        }

        val backupFile = File(tempFolder.root, "test_spendsense.db.bak")
        val tempEncryptedFile = File(tempFolder.root, "test_spendsense.db.tmp")

        mockkStatic(SQLiteDatabase::class)
        every {
            SQLiteDatabase.openOrCreateDatabase(any<File>(), any<ByteArray>(), any(), any())
        } throws IOException("Simulated disk error during encryption")

        val dummyPassphrase = "x'0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef'".toByteArray(Charsets.UTF_8)

        var thrown = false
        try {
            DatabaseEncryptionMigrator.migratePlaintextToEncrypted(
                dbFile = dbFile,
                tempEncryptedFile = tempEncryptedFile,
                backupFile = backupFile,
                passphrase = dummyPassphrase,
            )
        } catch (_: Exception) {
            thrown = true
        }

        assertTrue("Expected migration to fail gracefully", thrown)
        assertTrue("Original database file must exist after rollback", dbFile.exists())
        assertTrue("Original database must still be detected as plaintext", DatabaseEncryptionMigrator.isPlaintextDatabase(dbFile))

        // Confirm payload preserved
        val restoredBytes = dbFile.readBytes()
        val expected = header + originalPayload
        assertArrayEquals("Payload must be preserved exactly", expected, restoredBytes)
        assertFalse("Temporary encrypted file must be cleaned up", tempEncryptedFile.exists())
    }
}
