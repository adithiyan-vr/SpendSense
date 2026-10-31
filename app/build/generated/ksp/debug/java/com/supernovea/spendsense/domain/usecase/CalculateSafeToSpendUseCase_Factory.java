package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.DueRepository;
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
public final class CalculateSafeToSpendUseCase_Factory implements Factory<CalculateSafeToSpendUseCase> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<IncomeRepository> incomeRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<DueRepository> dueRepositoryProvider;

  public CalculateSafeToSpendUseCase_Factory(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<DueRepository> dueRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.incomeRepositoryProvider = incomeRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.dueRepositoryProvider = dueRepositoryProvider;
  }

  @Override
  public CalculateSafeToSpendUseCase get() {
    return newInstance(settingsRepositoryProvider.get(), incomeRepositoryProvider.get(), expenseRepositoryProvider.get(), dueRepositoryProvider.get());
  }

  public static CalculateSafeToSpendUseCase_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<DueRepository> dueRepositoryProvider) {
    return new CalculateSafeToSpendUseCase_Factory(settingsRepositoryProvider, incomeRepositoryProvider, expenseRepositoryProvider, dueRepositoryProvider);
  }

  public static CalculateSafeToSpendUseCase newInstance(SettingsRepository settingsRepository,
      IncomeRepository incomeRepository, ExpenseRepository expenseRepository,
      DueRepository dueRepository) {
    return new CalculateSafeToSpendUseCase(settingsRepository, incomeRepository, expenseRepository, dueRepository);
  }
}
