package com.supernovea.spendsense.core.di;

import com.supernovea.spendsense.data.local.dao.IncomeDao;
import com.supernovea.spendsense.data.local.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DatabaseModule_ProvideIncomeDaoFactory implements Factory<IncomeDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideIncomeDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IncomeDao get() {
    return provideIncomeDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideIncomeDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideIncomeDaoFactory(databaseProvider);
  }

  public static IncomeDao provideIncomeDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIncomeDao(database));
  }
}
