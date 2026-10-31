package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.CategoryRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ManageCategoriesUseCase_Factory implements Factory<ManageCategoriesUseCase> {
  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public ManageCategoriesUseCase_Factory(Provider<CategoryRepository> categoryRepositoryProvider) {
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public ManageCategoriesUseCase get() {
    return newInstance(categoryRepositoryProvider.get());
  }

  public static ManageCategoriesUseCase_Factory create(
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new ManageCategoriesUseCase_Factory(categoryRepositoryProvider);
  }

  public static ManageCategoriesUseCase newInstance(CategoryRepository categoryRepository) {
    return new ManageCategoriesUseCase(categoryRepository);
  }
}
