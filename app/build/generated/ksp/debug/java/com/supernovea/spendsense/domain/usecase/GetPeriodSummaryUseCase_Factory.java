package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.BudgetRepository;
import com.supernovea.spendsense.domain.repository.ExpenseRepository;
import com.supernovea.spendsense.domain.repository.IncomeRepository;
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
public final class GetPeriodSummaryUseCase_Factory implements Factory<GetPeriodSummaryUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<IncomeRepository> incomeRepositoryProvider;

  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public GetPeriodSummaryUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.incomeRepositoryProvider = incomeRepositoryProvider;
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public GetPeriodSummaryUseCase get() {
    return newInstance(expenseRepositoryProvider.get(), incomeRepositoryProvider.get(), budgetRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static GetPeriodSummaryUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new GetPeriodSummaryUseCase_Factory(expenseRepositoryProvider, incomeRepositoryProvider, budgetRepositoryProvider, settingsRepositoryProvider);
  }

  public static GetPeriodSummaryUseCase newInstance(ExpenseRepository expenseRepository,
      IncomeRepository incomeRepository, BudgetRepository budgetRepository,
      SettingsRepository settingsRepository) {
    return new GetPeriodSummaryUseCase(expenseRepository, incomeRepository, budgetRepository, settingsRepository);
  }
}
