package com.supernovea.spendsense.data.local.repository;

import com.supernovea.spendsense.data.local.dao.IncomeDao;
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
public final class IncomeRepositoryImpl_Factory implements Factory<IncomeRepositoryImpl> {
  private final Provider<IncomeDao> incomeDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public IncomeRepositoryImpl_Factory(Provider<IncomeDao> incomeDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.incomeDaoProvider = incomeDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public IncomeRepositoryImpl get() {
    return newInstance(incomeDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static IncomeRepositoryImpl_Factory create(Provider<IncomeDao> incomeDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new IncomeRepositoryImpl_Factory(incomeDaoProvider, ioDispatcherProvider);
  }

  public static IncomeRepositoryImpl newInstance(IncomeDao incomeDao,
      CoroutineDispatcher ioDispatcher) {
    return new IncomeRepositoryImpl(incomeDao, ioDispatcher);
  }
}
