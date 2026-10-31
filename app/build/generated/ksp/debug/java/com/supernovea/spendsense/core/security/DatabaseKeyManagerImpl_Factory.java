package com.supernovea.spendsense.core.security;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseKeyManagerImpl_Factory implements Factory<DatabaseKeyManagerImpl> {
  private final Provider<Context> contextProvider;

  public DatabaseKeyManagerImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DatabaseKeyManagerImpl get() {
    return newInstance(contextProvider.get());
  }

  public static DatabaseKeyManagerImpl_Factory create(Provider<Context> contextProvider) {
    return new DatabaseKeyManagerImpl_Factory(contextProvider);
  }

  public static DatabaseKeyManagerImpl newInstance(Context context) {
    return new DatabaseKeyManagerImpl(context);
  }
}
