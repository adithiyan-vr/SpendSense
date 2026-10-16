/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinManager internal constructor(
    private val context: Context,
    private val customAead: Aead? = null,
) {
    @Inject
    constructor(@ApplicationContext context: Context) : this(context, null)

    private val associatedData = "spendsense-pin-auth-v1".toByteArray(Charsets.UTF_8)
    private val pinFile = File(context.noBackupFilesDir, "spendsense_pin.enc")

    companion object {
        private const val PBKDF2_ITERATIONS = 65536
        private const val HASH_BYTES = 32
        private const val SALT_BYTES = 16
    }

    private val aead: Aead by lazy {
        if (customAead != null) {
            return@lazy customAead
        }
        AeadConfig.register()
        val keysetManager = AndroidKeysetManager.Builder()
            .withSharedPref(context, "spendsense_pin_keyset", "spendsense_security_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://spendsense_master_key_v1")
            .build()
        keysetManager.keysetHandle.getPrimitive(Aead::class.java)
    }

    @Synchronized
    fun isPinSet(): Boolean = pinFile.exists()

    @Synchronized
    fun getPinLength(): Int {
        if (!pinFile.exists()) return 4
        return try {
            val encrypted = pinFile.readBytes()
            val decrypted = aead.decrypt(encrypted, associatedData)
            DataInputStream(ByteArrayInputStream(decrypted)).use { dis ->
                dis.readInt() // iterations
                val len = dis.readInt()
                if (len in 4..6) len else 4
            }
        } catch (_: Exception) {
            4
        }
    }

    @Synchronized
    fun setPin(pin: String) {
        require(pin.length in 4..6 && pin.all { it.isDigit() }) {
            "PIN must be 4 to 6 digits"
        }

        val salt = ByteArray(SALT_BYTES).apply {
            SecureRandom().nextBytes(this)
        }

        val hash = hashPin(pin, salt, PBKDF2_ITERATIONS)

        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).use { dos ->
            dos.writeInt(PBKDF2_ITERATIONS)
            dos.writeInt(pin.length)
            dos.writeInt(salt.size)
            dos.write(salt)
            dos.writeInt(hash.size)
            dos.write(hash)
        }

        val encrypted = aead.encrypt(byteStream.toByteArray(), associatedData)
        pinFile.writeBytes(encrypted)
    }

    @Synchronized
    fun verifyPin(pin: String): Boolean {
        if (!pinFile.exists()) return false

        return try {
            val encrypted = pinFile.readBytes()
            val decrypted = aead.decrypt(encrypted, associatedData)

            DataInputStream(ByteArrayInputStream(decrypted)).use { dis ->
                val iterations = dis.readInt()
                val pinLength = dis.readInt()
                val saltLength = dis.readInt()
                val salt = ByteArray(saltLength).also { dis.readFully(it) }
                val hashLength = dis.readInt()
                val expectedHash = ByteArray(hashLength).also { dis.readFully(it) }

                val actualHash = hashPin(pin, salt, iterations)
                constantTimeEquals(actualHash, expectedHash)
            }
        } catch (_: Exception) {
            false
        }
    }

    @Synchronized
    fun clearPin() {
        if (pinFile.exists()) {
            pinFile.delete()
        }
    }

    private fun hashPin(pin: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, HASH_BYTES * 8)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }
}
