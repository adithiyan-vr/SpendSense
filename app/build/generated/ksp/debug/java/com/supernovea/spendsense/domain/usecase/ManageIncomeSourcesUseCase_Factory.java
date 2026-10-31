package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.IncomeSourceRepository;
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
public final class ManageIncomeSourcesUseCase_Factory implements Factory<ManageIncomeSourcesUseCase> {
  private final Provider<IncomeSourceRepository> incomeSourceRepositoryProvider;

  public ManageIncomeSourcesUseCase_Factory(
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider) {
    this.incomeSourceRepositoryProvider = incomeSourceRepositoryProvider;
  }

  @Override
  public ManageIncomeSourcesUseCase get() {
    return newInstance(incomeSourceRepositoryProvider.get());
  }

  public static ManageIncomeSourcesUseCase_Factory create(
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider) {
    return new ManageIncomeSourcesUseCase_Factory(incomeSourceRepositoryProvider);
  }

  public static ManageIncomeSourcesUseCase newInstance(
      IncomeSourceRepository incomeSourceRepository) {
    return new ManageIncomeSourcesUseCase(incomeSourceRepository);
  }
}
