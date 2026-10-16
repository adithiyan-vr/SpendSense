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
import java.io.File
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseKeyManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DatabaseKeyManager {

    private val associatedData = "spendsense-db-key-v1".toByteArray(Charsets.UTF_8)
    private val keyFile = File(context.noBackupFilesDir, "spendsense_db_key.enc")

    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetManager = AndroidKeysetManager.Builder()
            .withSharedPref(context, "spendsense_db_keyset", "spendsense_security_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://spendsense_master_key_v1")
            .build()
        keysetManager.keysetHandle.getPrimitive(Aead::class.java)
    }

    @Synchronized
    override fun getDatabasePassphrase(): ByteArray {
        val rawKey = if (keyFile.exists()) {
            val encrypted = keyFile.readBytes()
            aead.decrypt(encrypted, associatedData)
        } else {
            val key = ByteArray(32).apply {
                SecureRandom().nextBytes(this)
            }
            val encrypted = aead.encrypt(key, associatedData)
            keyFile.writeBytes(encrypted)
            key
        }
        val hex = rawKey.joinToString("") { "%02x".format(it) }
        rawKey.fill(0)
        return "x'$hex'".toByteArray(Charsets.UTF_8)
    }

    override fun hasKey(): Boolean = keyFile.exists()

    @Synchronized
    override fun wipeKey() {
        if (keyFile.exists()) {
            keyFile.delete()
        }
        val prefsFile = File(context.applicationInfo.dataDir, "shared_prefs/spendsense_security_prefs.xml")
        if (prefsFile.exists()) {
            prefsFile.delete()
        }
    }
}
