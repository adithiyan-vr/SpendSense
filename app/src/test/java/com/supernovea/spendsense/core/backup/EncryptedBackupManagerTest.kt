/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.crypto.AEADBadTagException

class EncryptedBackupManagerTest {

    private val jsonBackupManager = mockk<JsonBackupManager>()
    private lateinit var encryptedBackupManager: EncryptedBackupManager

    private val sampleJson = """
        {
            "version": 1,
            "exportTimestamp": 1729000000000,
            "expenses": [
                {
                    "id": 1,
                    "amountMinor": 25000,
                    "categoryId": 1,
                    "date": "2026-10-15",
                    "type": "NEED",
                    "note": "Test Note"
                }
            ],
            "categories": [],
            "budgets": [],
            "settings": {
                "currencySymbol": "₹"
            }
        }
    """.trimIndent()

    @Before
    fun setUp() {
        encryptedBackupManager = EncryptedBackupManager(jsonBackupManager)
        coEvery { jsonBackupManager.exportJson() } returns sampleJson
        coEvery { jsonBackupManager.importJson(any(), any()) } returns ImportResult(
            expensesRestoredCount = 1,
            categoriesRestoredCount = 0,
            budgetsRestoredCount = 0,
            incomesRestoredCount = 0,
            incomeSourcesRestoredCount = 0,
        )
    }

    @Test
    fun `evaluatePasswordStrength returns appropriate strength tier`() {
        assertEquals(PasswordStrength.TOO_SHORT, encryptedBackupManager.evaluatePasswordStrength("short"))
        assertEquals(PasswordStrength.WEAK, encryptedBackupManager.evaluatePasswordStrength("12345678"))
        assertEquals(PasswordStrength.FAIR, encryptedBackupManager.evaluatePasswordStrength("12345678aA"))
        assertEquals(PasswordStrength.GOOD, encryptedBackupManager.evaluatePasswordStrength("12345678aA!"))
        assertEquals(PasswordStrength.STRONG, encryptedBackupManager.evaluatePasswordStrength("VeryStrongPassword123!@#"))
    }

    @Test
    fun `exportEncryptedBackup writes valid header and encrypted payload`() = runTest {
        val outputStream = ByteArrayOutputStream()
        encryptedBackupManager.exportEncryptedBackup("StrongPassword123!", outputStream)

        val bytes = outputStream.toByteArray()
        assertTrue("Output should be larger than header", bytes.size > 41)

        val magic = bytes.copyOfRange(0, 8).toString(Charsets.US_ASCII)
        assertEquals("SSBACKUP", magic)
        assertEquals(1.toByte(), bytes[8]) // Version 1
    }

    @Test
    fun `export and import roundtrip successfully restores json payload`() = runTest {
        val password = "SuperSecretPassword123!"
        val outputStream = ByteArrayOutputStream()
        encryptedBackupManager.exportEncryptedBackup(password, outputStream)

        val encryptedBytes = outputStream.toByteArray()
        val inputStream = ByteArrayInputStream(encryptedBytes)

        val result = encryptedBackupManager.importEncryptedBackup(inputStream, password, ImportMode.MERGE)

        assertEquals(1, result.expensesRestoredCount)
        coVerify(exactly = 1) {
            jsonBackupManager.importJson(sampleJson, ImportMode.MERGE)
        }
    }

    @Test
    fun `importEncryptedBackup with wrong password throws exception and aborts`() = runTest {
        val outputStream = ByteArrayOutputStream()
        encryptedBackupManager.exportEncryptedBackup("CorrectPassword123!", outputStream)

        val encryptedBytes = outputStream.toByteArray()
        val inputStream = ByteArrayInputStream(encryptedBytes)

        try {
            encryptedBackupManager.importEncryptedBackup(inputStream, "WrongPassword456!", ImportMode.MERGE)
            org.junit.Assert.fail("Expected exception on wrong password")
        } catch (_: Exception) {
            // Expected
        }

        coVerify(exactly = 0) {
            jsonBackupManager.importJson(any(), any())
        }
    }

    @Test
    fun `importEncryptedBackup with invalid magic header throws IllegalArgumentException`() = runTest {
        val corruptedBytes = "NOTBACKUP".toByteArray(Charsets.US_ASCII) + ByteArray(100)
        val inputStream = ByteArrayInputStream(corruptedBytes)

        try {
            encryptedBackupManager.importEncryptedBackup(inputStream, "AnyPassword123!", ImportMode.MERGE)
            org.junit.Assert.fail("Expected IllegalArgumentException on invalid magic header")
        } catch (_: IllegalArgumentException) {
            // Expected
        }
    }

    @Test
    fun `importEncryptedBackup with tampered header fails AAD authentication`() = runTest {
        val password = "Password1234!"
        val outputStream = ByteArrayOutputStream()
        encryptedBackupManager.exportEncryptedBackup(password, outputStream)

        val bytes = outputStream.toByteArray()
        // Tamper with salt byte
        bytes[20] = (bytes[20] + 1).toByte()

        try {
            encryptedBackupManager.importEncryptedBackup(ByteArrayInputStream(bytes), password, ImportMode.MERGE)
            org.junit.Assert.fail("Expected exception on tampered header")
        } catch (_: Exception) {
            // Expected
        }
    }

    @Test
    fun `exportEncryptedBackup with short password throws IllegalArgumentException`() = runTest {
        val outputStream = ByteArrayOutputStream()
        try {
            encryptedBackupManager.exportEncryptedBackup("short", outputStream)
            org.junit.Assert.fail("Expected IllegalArgumentException on short password")
        } catch (_: IllegalArgumentException) {
            // Expected
        }
    }
}
