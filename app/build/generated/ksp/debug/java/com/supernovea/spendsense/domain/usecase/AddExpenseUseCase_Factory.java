package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.CategoryRepository;
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
public final class AddExpenseUseCase_Factory implements Factory<AddExpenseUseCase> {
  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public AddExpenseUseCase_Factory(Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public AddExpenseUseCase get() {
    return newInstance(expenseRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static AddExpenseUseCase_Factory create(
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new AddExpenseUseCase_Factory(expenseRepositoryProvider, categoryRepositoryProvider);
  }

  public static AddExpenseUseCase newInstance(ExpenseRepository expenseRepository,
      CategoryRepository categoryRepository) {
    return new AddExpenseUseCase(expenseRepository, categoryRepository);
  }
}
