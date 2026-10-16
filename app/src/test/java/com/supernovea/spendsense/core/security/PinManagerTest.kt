/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.AesGcmJce
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.security.SecureRandom

class PinManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val context = mockk<Context>(relaxed = true)
    private lateinit var fakeAead: Aead
    private lateinit var pinManager: PinManager

    @Before
    fun setUp() {
        val testKey = ByteArray(32).apply { SecureRandom().nextBytes(this) }
        fakeAead = AesGcmJce(testKey)

        every { context.noBackupFilesDir } returns tempFolder.root

        pinManager = PinManager(
            context = context,
            customAead = fakeAead,
        )
    }

    @Test
    fun `isPinSet returns false when no pin is stored`() {
        assertFalse(pinManager.isPinSet())
    }

    @Test
    fun `setPin saves valid 4 digit pin and enables isPinSet`() {
        pinManager.setPin("1234")
        assertTrue(pinManager.isPinSet())
        assertEquals(4, pinManager.getPinLength())
    }

    @Test
    fun `setPin saves valid 6 digit pin and enables isPinSet`() {
        pinManager.setPin("987654")
        assertTrue(pinManager.isPinSet())
        assertEquals(6, pinManager.getPinLength())
    }

    @Test
    fun `setPin rejects invalid pins`() {
        assertThrows(IllegalArgumentException::class.java) {
            pinManager.setPin("12") // Too short
        }
        assertThrows(IllegalArgumentException::class.java) {
            pinManager.setPin("1234567") // Too long
        }
        assertThrows(IllegalArgumentException::class.java) {
            pinManager.setPin("12ab") // Non-digits
        }
    }

    @Test
    fun `verifyPin returns true for correct pin and false for incorrect pin`() {
        pinManager.setPin("4321")

        assertTrue(pinManager.verifyPin("4321"))
        assertFalse(pinManager.verifyPin("1234"))
        assertFalse(pinManager.verifyPin("4320"))
        assertFalse(pinManager.verifyPin("43210"))
    }

    @Test
    fun `clearPin removes stored pin file`() {
        pinManager.setPin("5555")
        assertTrue(pinManager.isPinSet())

        pinManager.clearPin()
        assertFalse(pinManager.isPinSet())
        assertFalse(pinManager.verifyPin("5555"))
    }
}
