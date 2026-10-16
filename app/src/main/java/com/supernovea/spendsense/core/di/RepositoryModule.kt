/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.di

import com.supernovea.spendsense.data.local.datastore.SettingsRepositoryImpl
import com.supernovea.spendsense.data.local.repository.BudgetRepositoryImpl
import com.supernovea.spendsense.data.local.repository.CategoryRepositoryImpl
import com.supernovea.spendsense.data.local.repository.ExpenseRepositoryImpl
import com.supernovea.spendsense.domain.repository.BudgetRepository
import com.supernovea.spendsense.domain.repository.CategoryRepository
import com.supernovea.spendsense.domain.repository.ExpenseRepository
import com.supernovea.spendsense.domain.repository.SettingsRepository
import com.supernovea.spendsense.data.local.repository.IncomeRepositoryImpl
import com.supernovea.spendsense.data.local.repository.IncomeSourceRepositoryImpl
import com.supernovea.spendsense.domain.repository.IncomeRepository
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl,
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl,
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        impl: BudgetRepositoryImpl,
    ): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindIncomeRepository(
        impl: IncomeRepositoryImpl,
    ): IncomeRepository

    @Binds
    @Singleton
    abstract fun bindIncomeSourceRepository(
        impl: IncomeSourceRepositoryImpl,
    ): IncomeSourceRepository
}
