package com.supernovea.spendsense.core.di;

import com.supernovea.spendsense.data.local.dao.DueDao;
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
public final class DatabaseModule_ProvideDueDaoFactory implements Factory<DueDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideDueDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DueDao get() {
    return provideDueDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideDueDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDueDaoFactory(databaseProvider);
  }

  public static DueDao provideDueDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDueDao(database));
  }
}
