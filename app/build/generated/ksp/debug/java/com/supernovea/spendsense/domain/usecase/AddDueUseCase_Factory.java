package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.CategoryRepository;
import com.supernovea.spendsense.domain.repository.DueRepository;
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
public final class AddDueUseCase_Factory implements Factory<AddDueUseCase> {
  private final Provider<DueRepository> dueRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public AddDueUseCase_Factory(Provider<DueRepository> dueRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.dueRepositoryProvider = dueRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public AddDueUseCase get() {
    return newInstance(dueRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static AddDueUseCase_Factory create(Provider<DueRepository> dueRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new AddDueUseCase_Factory(dueRepositoryProvider, categoryRepositoryProvider);
  }

  public static AddDueUseCase newInstance(DueRepository dueRepository,
      CategoryRepository categoryRepository) {
    return new AddDueUseCase(dueRepository, categoryRepository);
  }
}
