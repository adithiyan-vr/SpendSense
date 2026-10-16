/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.di

import com.supernovea.spendsense.core.security.DatabaseKeyManager
import com.supernovea.spendsense.core.security.DatabaseKeyManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindDatabaseKeyManager(
        impl: DatabaseKeyManagerImpl,
    ): DatabaseKeyManager
}
