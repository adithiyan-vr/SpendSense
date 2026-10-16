/*
 * SpendSense
 * Copyright (c) 2026 Supernovea
 * Developed by Adithiyan V R
 */

package com.supernovea.spendsense.core.di

import android.content.Context
import com.supernovea.spendsense.core.security.DatabaseKeyManager
import com.supernovea.spendsense.data.local.dao.BudgetDao
import com.supernovea.spendsense.data.local.dao.CategoryDao
import com.supernovea.spendsense.data.local.dao.ExpenseDao
import com.supernovea.spendsense.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseKeyManager: DatabaseKeyManager,
    ): AppDatabase {
        return AppDatabase.build(context, databaseKeyManager)
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Provides
    fun provideIncomeSourceDao(database: AppDatabase): com.supernovea.spendsense.data.local.dao.IncomeSourceDao {
        return database.incomeSourceDao()
    }

    @Provides
    fun provideIncomeDao(database: AppDatabase): com.supernovea.spendsense.data.local.dao.IncomeDao {
        return database.incomeDao()
    }
}
