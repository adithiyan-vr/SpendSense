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
public final class DeleteExpenseUseCase_Factory implements Factory<DeleteExpenseUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  public DeleteExpenseUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
  }

  @Override
  public DeleteExpenseUseCase get() {
    return newInstance(expenseRepositoryProvider.get());
  }

  public static DeleteExpenseUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider) {
    return new DeleteExpenseUseCase_Factory(expenseRepositoryProvider);
  }

  public static DeleteExpenseUseCase newInstance(ExpenseRepository expenseRepository) {
    return new DeleteExpenseUseCase(expenseRepository);
  }
}
