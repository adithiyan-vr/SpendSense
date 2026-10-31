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
public final class GetDuesUseCase_Factory implements Factory<GetDuesUseCase> {
  private final Provider<DueRepository> dueRepositoryProvider;

  public GetDuesUseCase_Factory(Provider<DueRepository> dueRepositoryProvider) {
    this.dueRepositoryProvider = dueRepositoryProvider;
  }

  @Override
  public GetDuesUseCase get() {
    return newInstance(dueRepositoryProvider.get());
  }

  public static GetDuesUseCase_Factory create(Provider<DueRepository> dueRepositoryProvider) {
    return new GetDuesUseCase_Factory(dueRepositoryProvider);
  }

  public static GetDuesUseCase newInstance(DueRepository dueRepository) {
    return new GetDuesUseCase(dueRepository);
  }
}
