/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.security

/**
 * Manages the generation, secure storage, and retrieval of the 256-bit (32-byte)
 * AES database encryption key for SQLCipher.
 */
interface DatabaseKeyManager {
    /**
     * Returns a fresh copy of the 32-byte database encryption passphrase.
     * Callers should zero out the returned array when done with it.
     */
    fun getDatabasePassphrase(): ByteArray

    /**
     * Returns true if an encrypted database passphrase has already been generated and persisted.
     */
    fun hasKey(): Boolean

    /**
     * Wipes the persisted encrypted key and master key keyset during secure data deletion.
     */
    fun wipeKey()
}
