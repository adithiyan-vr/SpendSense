package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.IncomeRepository;
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
public final class AddIncomeUseCase_Factory implements Factory<AddIncomeUseCase> {
  private final Provider<IncomeRepository> incomeRepositoryProvider;

  private final Provider<IncomeSourceRepository> incomeSourceRepositoryProvider;

  public AddIncomeUseCase_Factory(Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider) {
    this.incomeRepositoryProvider = incomeRepositoryProvider;
    this.incomeSourceRepositoryProvider = incomeSourceRepositoryProvider;
  }

  @Override
  public AddIncomeUseCase get() {
    return newInstance(incomeRepositoryProvider.get(), incomeSourceRepositoryProvider.get());
  }

  public static AddIncomeUseCase_Factory create(Provider<IncomeRepository> incomeRepositoryProvider,
      Provider<IncomeSourceRepository> incomeSourceRepositoryProvider) {
    return new AddIncomeUseCase_Factory(incomeRepositoryProvider, incomeSourceRepositoryProvider);
  }

  public static AddIncomeUseCase newInstance(IncomeRepository incomeRepository,
      IncomeSourceRepository incomeSourceRepository) {
    return new AddIncomeUseCase(incomeRepository, incomeSourceRepository);
  }
}
