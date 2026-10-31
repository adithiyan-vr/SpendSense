package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.CategoryRepository;
import com.supernovea.spendsense.domain.repository.DueRepository;
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
public final class MarkDuePaidUseCase_Factory implements Factory<MarkDuePaidUseCase> {
  private final Provider<DueRepository> dueRepositoryProvider;

  private final Provider<ExpenseRepository> expenseRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public MarkDuePaidUseCase_Factory(Provider<DueRepository> dueRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.dueRepositoryProvider = dueRepositoryProvider;
    this.expenseRepositoryProvider = expenseRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public MarkDuePaidUseCase get() {
    return newInstance(dueRepositoryProvider.get(), expenseRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static MarkDuePaidUseCase_Factory create(Provider<DueRepository> dueRepositoryProvider,
      Provider<ExpenseRepository> expenseRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new MarkDuePaidUseCase_Factory(dueRepositoryProvider, expenseRepositoryProvider, categoryRepositoryProvider);
  }

  public static MarkDuePaidUseCase newInstance(DueRepository dueRepository,
      ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
    return new MarkDuePaidUseCase(dueRepository, expenseRepository, categoryRepository);
  }
}
