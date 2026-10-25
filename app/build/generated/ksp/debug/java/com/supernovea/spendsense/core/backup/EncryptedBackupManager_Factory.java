package com.supernovea.spendsense.core.backup;

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
public final class EncryptedBackupManager_Factory implements Factory<EncryptedBackupManager> {
  private final Provider<JsonBackupManager> jsonBackupManagerProvider;

  public EncryptedBackupManager_Factory(Provider<JsonBackupManager> jsonBackupManagerProvider) {
    this.jsonBackupManagerProvider = jsonBackupManagerProvider;
  }

  @Override
  public EncryptedBackupManager get() {
    return newInstance(jsonBackupManagerProvider.get());
  }

  public static EncryptedBackupManager_Factory create(
      Provider<JsonBackupManager> jsonBackupManagerProvider) {
    return new EncryptedBackupManager_Factory(jsonBackupManagerProvider);
  }

  public static EncryptedBackupManager newInstance(JsonBackupManager jsonBackupManager) {
    return new EncryptedBackupManager(jsonBackupManager);
  }
}
