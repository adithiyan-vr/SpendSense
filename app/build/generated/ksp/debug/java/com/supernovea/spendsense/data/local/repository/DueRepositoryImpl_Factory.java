package com.supernovea.spendsense.data.local.repository;

import com.supernovea.spendsense.data.local.dao.DueDao;
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
public final class DueRepositoryImpl_Factory implements Factory<DueRepositoryImpl> {
  private final Provider<DueDao> dueDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public DueRepositoryImpl_Factory(Provider<DueDao> dueDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.dueDaoProvider = dueDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public DueRepositoryImpl get() {
    return newInstance(dueDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static DueRepositoryImpl_Factory create(Provider<DueDao> dueDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new DueRepositoryImpl_Factory(dueDaoProvider, ioDispatcherProvider);
  }

  public static DueRepositoryImpl newInstance(DueDao dueDao, CoroutineDispatcher ioDispatcher) {
    return new DueRepositoryImpl(dueDao, ioDispatcher);
  }
}
