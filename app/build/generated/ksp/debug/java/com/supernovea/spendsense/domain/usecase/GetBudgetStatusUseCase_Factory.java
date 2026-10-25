package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.BudgetRepository;
import com.supernovea.spendsense.domain.repository.ExpenseRepository;
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
public final class GetBudgetStatusUseCase_Factory implements Factory<GetBudgetStatusUseCase> {
  private final Provider<BudgetRepository> budgetRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public GetBudgetStatusUseCase_Factory(Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.budgetRepositoryProvider = budgetRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public GetBudgetStatusUseCase get() {
    return newInstance(budgetRepositoryProvider.get(), expenseRepositoryProvider.get());
  }

  public static GetBudgetStatusUseCase_Factory create(
      Provider<BudgetRepository> budgetRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GetBudgetStatusUseCase_Factory(budgetRepositoryProvider, expenseRepositoryProvider);
  }

  public static GetBudgetStatusUseCase newInstance(BudgetRepository budgetRepository,
      ExpenseRepository expenseRepository) {
    return new GetBudgetStatusUseCase(budgetRepository, expenseRepository);
  }
}
