/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.backup

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

enum class PasswordStrength {
    TOO_SHORT,
    WEAK,
    FAIR,
    GOOD,
    STRONG,
}

@Singleton
class EncryptedBackupManager @Inject constructor(
    private val jsonBackupManager: JsonBackupManager,
) {
    companion object {
        val MAGIC_BYTES = "SSBACKUP".toByteArray(Charsets.US_ASCII)
        const val FORMAT_VERSION: Byte = 1
        const val DEFAULT_ITERATIONS = 100_000
        const val SALT_LENGTH = 16
        const val NONCE_LENGTH = 12
        const val KEY_LENGTH_BITS = 256
        const val GCM_TAG_LENGTH_BITS = 128
        const val MIN_PASSWORD_LENGTH = 8
    }

    fun evaluatePasswordStrength(password: String): PasswordStrength {
        if (password.length < MIN_PASSWORD_LENGTH) return PasswordStrength.TOO_SHORT
        var score = 0
        if (password.length >= 12) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when (score) {
            0, 1 -> PasswordStrength.WEAK
            2 -> PasswordStrength.FAIR
            3 -> PasswordStrength.GOOD
            else -> PasswordStrength.STRONG
        }
    }

    suspend fun exportEncryptedBackup(password: String, outputStream: OutputStream) {
        require(password.length >= MIN_PASSWORD_LENGTH) {
            "Password must be at least $MIN_PASSWORD_LENGTH characters"
        }

        val jsonString = jsonBackupManager.exportJson()
        val plainBytes = jsonString.toByteArray(Charsets.UTF_8)

        val salt = ByteArray(SALT_LENGTH).apply { SecureRandom().nextBytes(this) }
        val nonce = ByteArray(NONCE_LENGTH).apply { SecureRandom().nextBytes(this) }

        val headerStream = ByteArrayOutputStream()
        DataOutputStream(headerStream).use { dos ->
            dos.write(MAGIC_BYTES)
            dos.writeByte(FORMAT_VERSION.toInt())
            dos.writeInt(DEFAULT_ITERATIONS)
            dos.write(salt)
            dos.write(nonce)
        }
        val headerBytes = headerStream.toByteArray()

        val secretKey = deriveKey(password, salt, DEFAULT_ITERATIONS)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        cipher.updateAAD(headerBytes)

        val cipherBytes = cipher.doFinal(plainBytes)

        outputStream.write(headerBytes)
        outputStream.write(cipherBytes)
        outputStream.flush()
    }

    suspend fun importEncryptedBackup(
        inputStream: InputStream,
        password: String,
        mode: ImportMode,
    ): ImportResult {
        require(password.isNotBlank()) { "Password cannot be empty" }

        val allBytes = inputStream.readBytes()
        val minHeaderSize = MAGIC_BYTES.size + 1 + 4 + SALT_LENGTH + NONCE_LENGTH
        if (allBytes.size < minHeaderSize + (GCM_TAG_LENGTH_BITS / 8)) {
            throw IllegalArgumentException("Invalid backup file: file is too small or corrupted")
        }

        val dis = DataInputStream(ByteArrayInputStream(allBytes))
        val magic = ByteArray(MAGIC_BYTES.size)
        dis.readFully(magic)
        if (!magic.contentEquals(MAGIC_BYTES)) {
            throw IllegalArgumentException("Invalid backup file: unrecognized magic header")
        }

        val version = dis.readByte()
        if (version != FORMAT_VERSION) {
            throw IllegalArgumentException("Unsupported backup format version: $version")
        }

        val iterations = dis.readInt()
        if (iterations < 1000 || iterations > 1_000_000) {
            throw IllegalArgumentException("Invalid KDF iteration count: $iterations")
        }

        val salt = ByteArray(SALT_LENGTH).also { dis.readFully(it) }
        val nonce = ByteArray(NONCE_LENGTH).also { dis.readFully(it) }

        val headerBytes = allBytes.copyOfRange(0, minHeaderSize)
        val ciphertextBytes = allBytes.copyOfRange(minHeaderSize, allBytes.size)

        val secretKey = deriveKey(password, salt, iterations)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        cipher.updateAAD(headerBytes)

        val decryptedBytes = try {
            cipher.doFinal(ciphertextBytes)
        } catch (e: Exception) {
            throw SecurityException("Decryption failed: incorrect password or corrupted file", e)
        }

        val jsonString = String(decryptedBytes, Charsets.UTF_8)
        return jsonBackupManager.importJson(jsonString, mode)
    }

    private fun deriveKey(password: String, salt: ByteArray, iterations: Int): SecretKeySpec {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val rawKey = factory.generateSecret(spec).encoded
        return SecretKeySpec(rawKey, "AES")
    }
}
