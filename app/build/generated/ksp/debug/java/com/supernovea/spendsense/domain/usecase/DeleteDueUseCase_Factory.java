package com.supernovea.spendsense.domain.usecase;

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
public final class DeleteDueUseCase_Factory implements Factory<DeleteDueUseCase> {
  private final Provider<DueRepository> dueRepositoryProvider;

  public DeleteDueUseCase_Factory(Provider<DueRepository> dueRepositoryProvider) {
    this.dueRepositoryProvider = dueRepositoryProvider;
  }

  @Override
  public DeleteDueUseCase get() {
    return newInstance(dueRepositoryProvider.get());
  }

  public static DeleteDueUseCase_Factory create(Provider<DueRepository> dueRepositoryProvider) {
    return new DeleteDueUseCase_Factory(dueRepositoryProvider);
  }

  public static DeleteDueUseCase newInstance(DueRepository dueRepository) {
    return new DeleteDueUseCase(dueRepository);
  }
}
