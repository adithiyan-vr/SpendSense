package com.supernovea.spendsense.data.local.datastore;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public SettingsRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public SettingsRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), ioDispatcherProvider.get());
  }

  public static SettingsRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new SettingsRepositoryImpl_Factory(dataStoreProvider, ioDispatcherProvider);
  }

  public static SettingsRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      CoroutineDispatcher ioDispatcher) {
    return new SettingsRepositoryImpl(dataStore, ioDispatcher);
  }
}
