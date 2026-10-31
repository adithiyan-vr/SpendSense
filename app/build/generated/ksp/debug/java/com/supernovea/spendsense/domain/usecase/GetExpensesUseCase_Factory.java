package com.supernovea.spendsense.domain.usecase;

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
public final class GetExpensesUseCase_Factory implements Factory<GetExpensesUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public GetExpensesUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public GetExpensesUseCase get() {
    return newInstance(expenseRepositoryProvider.get());
  }

  public static GetExpensesUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new GetExpensesUseCase_Factory(expenseRepositoryProvider);
  }

  public static GetExpensesUseCase newInstance(ExpenseRepository expenseRepository) {
    return new GetExpensesUseCase(expenseRepository);
  }
}
