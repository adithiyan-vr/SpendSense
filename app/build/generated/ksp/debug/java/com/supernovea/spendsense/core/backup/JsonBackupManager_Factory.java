package com.supernovea.spendsense.core.backup;

import com.supernovea.spendsense.domain.repository.BudgetRepository;
import com.supernovea.spendsense.domain.repository.CategoryRepository;
import com.supernovea.spendsense.domain.repository.ExpenseRepository;
import com.supernovea.spendsense.domain.repository.IncomeRepository;
import com.supernovea.spendsense.domain.repository.IncomeSourceRepository;
import com.supernovea.spendsense.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class JsonBackupManager_Factory implements Factory<JsonBackupManager> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<IncomeRepository> incomeRepositoryProvider;

  private final Provider<IncomeSourceRepository> incomeSourceRepositoryProvider;

  public JsonBackupManager_Factory(Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.incomeRepositoryProvider = incomeRepositoryProvider;
    this.incomeSourceRepositoryProvider = incomeSourceRepositoryProvider;
  }

  @Override
  public JsonBackupManager get() {
    return newInstance(expenseRepositoryProvider.get(), categoryRepositoryProvider.get(), budgetRepositoryProvider.get(), settingsRepositoryProvider.get(), incomeRepositoryProvider.get(), incomeSourceRepositoryProvider.get());
  }

  public static JsonBackupManager_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider) {
    return new JsonBackupManager_Factory(expenseRepositoryProvider, categoryRepositoryProvider, budgetRepositoryProvider, settingsRepositoryProvider, incomeRepositoryProvider, incomeSourceRepositoryProvider);
  }

  public static JsonBackupManager newInstance(ExpenseRepository expenseRepository,
      CategoryRepository categoryRepository, BudgetRepository budgetRepository,
      SettingsRepository settingsRepository, IncomeRepository incomeRepository,
      IncomeSourceRepository incomeSourceRepository) {
    return new JsonBackupManager(expenseRepository, categoryRepository, budgetRepository, settingsRepository, incomeRepository, incomeSourceRepository);
  }
}
