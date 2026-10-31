package com.supernovea.spendsense.core.di;

import android.content.Context;
import com.supernovea.spendsense.core.security.DatabaseKeyManager;
import com.supernovea.spendsense.data.local.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideAppDatabaseFactory implements Factory<AppDatabase> {
  private final Provider<Context> contextProvider;

  private final Provider<DatabaseKeyManager> databaseKeyManagerProvider;

  public DatabaseModule_ProvideAppDatabaseFactory(Provider<Context> contextProvider,
      Provider<DatabaseKeyManager> databaseKeyManagerProvider) {
    this.contextProvider = contextProvider;
    this.databaseKeyManagerProvider = databaseKeyManagerProvider;
  }

  @Override
  public AppDatabase get() {
    return provideAppDatabase(contextProvider.get(), databaseKeyManagerProvider.get());
  }

  public static DatabaseModule_ProvideAppDatabaseFactory create(Provider<Context> contextProvider,
      Provider<DatabaseKeyManager> databaseKeyManagerProvider) {
    return new DatabaseModule_ProvideAppDatabaseFactory(contextProvider, databaseKeyManagerProvider);
  }

  public static AppDatabase provideAppDatabase(Context context,
      DatabaseKeyManager databaseKeyManager) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAppDatabase(context, databaseKeyManager));
  }
}
