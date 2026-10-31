package com.supernovea.spendsense.data.local.repository;

import com.supernovea.spendsense.data.local.dao.CategoryDao;
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
public final class CategoryRepositoryImpl_Factory implements Factory<CategoryRepositoryImpl> {
  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public CategoryRepositoryImpl_Factory(Provider<CategoryDao> categoryDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public CategoryRepositoryImpl get() {
    return newInstance(categoryDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static CategoryRepositoryImpl_Factory create(Provider<CategoryDao> categoryDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new CategoryRepositoryImpl_Factory(categoryDaoProvider, ioDispatcherProvider);
  }

  public static CategoryRepositoryImpl newInstance(CategoryDao categoryDao,
      CoroutineDispatcher ioDispatcher) {
    return new CategoryRepositoryImpl(categoryDao, ioDispatcher);
  }
}
