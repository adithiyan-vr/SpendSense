package com.supernovea.spendsense.data.local.repository;

import com.supernovea.spendsense.data.local.dao.BudgetDao;
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
public final class BudgetRepositoryImpl_Factory implements Factory<BudgetRepositoryImpl> {
  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public BudgetRepositoryImpl_Factory(Provider<BudgetDao> budgetDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.budgetDaoProvider = budgetDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public BudgetRepositoryImpl get() {
    return newInstance(budgetDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static BudgetRepositoryImpl_Factory create(Provider<BudgetDao> budgetDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new BudgetRepositoryImpl_Factory(budgetDaoProvider, ioDispatcherProvider);
  }

  public static BudgetRepositoryImpl newInstance(BudgetDao budgetDao,
      CoroutineDispatcher ioDispatcher) {
    return new BudgetRepositoryImpl(budgetDao, ioDispatcher);
  }
}
