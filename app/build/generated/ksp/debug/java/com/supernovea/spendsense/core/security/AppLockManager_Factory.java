package com.supernovea.spendsense.core.security;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppLockManager_Factory implements Factory<AppLockManager> {
  private final Provider<PinManager> pinManagerProvider;

  public AppLockManager_Factory(Provider<PinManager> pinManagerProvider) {
    this.pinManagerProvider = pinManagerProvider;
  }

  @Override
  public AppLockManager get() {
    return newInstance(pinManagerProvider.get());
  }

  public static AppLockManager_Factory create(Provider<PinManager> pinManagerProvider) {
    return new AppLockManager_Factory(pinManagerProvider);
  }

  public static AppLockManager newInstance(PinManager pinManager) {
    return new AppLockManager(pinManager);
  }
}
