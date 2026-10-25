package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.core.security.AppLockManager;
import com.supernovea.spendsense.core.security.LockoutManager;
import com.supernovea.spendsense.core.security.PinManager;
import com.supernovea.spendsense.domain.repository.BudgetRepository;
import com.supernovea.spendsense.domain.repository.CategoryRepository;
import com.supernovea.spendsense.domain.repository.DueRepository;
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

@ScopeMetadata
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
public final class DeleteAllDataUseCase_Factory implements Factory<DeleteAllDataUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<IncomeRepository> incomeRepositoryProvider;

  private final Provider<IncomeSourceRepository> incomeSourceRepositoryProvider;

  private final Provider<DueRepository> dueRepositoryProvider;

  private final Provider<PinManager> pinManagerProvider;

  private final Provider<LockoutManager> lockoutManagerProvider;

  private final Provider<AppLockManager> appLockManagerProvider;

  public DeleteAllDataUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider,
      Provider<DueRepository> dueRepositoryProvider, Provider<PinManager> pinManagerProvider,
      Provider<LockoutManager> lockoutManagerProvider,
      Provider<AppLockManager> appLockManagerProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.incomeRepositoryProvider = incomeRepositoryProvider;
    this.incomeSourceRepositoryProvider = incomeSourceRepositoryProvider;
    this.dueRepositoryProvider = dueRepositoryProvider;
    this.pinManagerProvider = pinManagerProvider;
    this.lockoutManagerProvider = lockoutManagerProvider;
    this.appLockManagerProvider = appLockManagerProvider;
  }

  @Override
  public DeleteAllDataUseCase get() {
    return newInstance(expenseRepositoryProvider.get(), categoryRepositoryProvider.get(), budgetRepositoryProvider.get(), settingsRepositoryProvider.get(), incomeRepositoryProvider.get(), incomeSourceRepositoryProvider.get(), dueRepositoryProvider.get(), pinManagerProvider.get(), lockoutManagerProvider.get(), appLockManagerProvider.get());
  }

  public static DeleteAllDataUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider,
      Provider<DueRepository> dueRepositoryProvider, Provider<PinManager> pinManagerProvider,
      Provider<LockoutManager> lockoutManagerProvider,
      Provider<AppLockManager> appLockManagerProvider) {
    return new DeleteAllDataUseCase_Factory(expenseRepositoryProvider, categoryRepositoryProvider, budgetRepositoryProvider, settingsRepositoryProvider, incomeRepositoryProvider, incomeSourceRepositoryProvider, dueRepositoryProvider, pinManagerProvider, lockoutManagerProvider, appLockManagerProvider);
  }

  public static DeleteAllDataUseCase newInstance(ExpenseRepository expenseRepository,
      CategoryRepository categoryRepository, BudgetRepository budgetRepository,
      SettingsRepository settingsRepository, IncomeRepository incomeRepository,
      IncomeSourceRepository incomeSourceRepository, DueRepository dueRepository,
      PinManager pinManager, LockoutManager lockoutManager, AppLockManager appLockManager) {
    return new DeleteAllDataUseCase(expenseRepository, categoryRepository, budgetRepository, settingsRepository, incomeRepository, incomeSourceRepository, dueRepository, pinManager, lockoutManager, appLockManager);
  }
}
