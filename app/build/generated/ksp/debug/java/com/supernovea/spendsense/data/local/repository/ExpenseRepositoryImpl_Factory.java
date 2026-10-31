package com.supernovea.spendsense.data.local.repository;

import com.supernovea.spendsense.data.local.dao.ExpenseDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.supernovea.spendsense.core.di.IoDispatcher")
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
public final class ExpenseRepositoryImpl_Factory implements Factory<ExpenseRepositoryImpl> {
  private final Provider<ExpenseDao> expenseDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public ExpenseRepositoryImpl_Factory(Provider<ExpenseDao> expenseDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.expenseDaoProvider = expenseDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public ExpenseRepositoryImpl get() {
    return newInstance(expenseDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static ExpenseRepositoryImpl_Factory create(Provider<ExpenseDao> expenseDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new ExpenseRepositoryImpl_Factory(expenseDaoProvider, ioDispatcherProvider);
  }

  public static ExpenseRepositoryImpl newInstance(ExpenseDao expenseDao,
      CoroutineDispatcher ioDispatcher) {
    return new ExpenseRepositoryImpl(expenseDao, ioDispatcher);
  }
}
