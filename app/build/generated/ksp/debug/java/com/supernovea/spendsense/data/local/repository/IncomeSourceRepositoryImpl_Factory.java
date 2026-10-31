package com.supernovea.spendsense.data.local.repository;

import com.supernovea.spendsense.data.local.dao.IncomeSourceDao;
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
public final class IncomeSourceRepositoryImpl_Factory implements Factory<IncomeSourceRepositoryImpl> {
  private final Provider<IncomeSourceDao> incomeSourceDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public IncomeSourceRepositoryImpl_Factory(Provider<IncomeSourceDao> incomeSourceDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.incomeSourceDaoProvider = incomeSourceDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public IncomeSourceRepositoryImpl get() {
    return newInstance(incomeSourceDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static IncomeSourceRepositoryImpl_Factory create(
      Provider<IncomeSourceDao> incomeSourceDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new IncomeSourceRepositoryImpl_Factory(incomeSourceDaoProvider, ioDispatcherProvider);
  }

  public static IncomeSourceRepositoryImpl newInstance(IncomeSourceDao incomeSourceDao,
      CoroutineDispatcher ioDispatcher) {
    return new IncomeSourceRepositoryImpl(incomeSourceDao, ioDispatcher);
  }
}
