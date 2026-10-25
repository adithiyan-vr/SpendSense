package com.supernovea.spendsense.domain.usecase;

import com.supernovea.spendsense.domain.repository.IncomeRepository;
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
public final class DeleteIncomeUseCase_Factory implements Factory<DeleteIncomeUseCase> {
  private final Provider<IncomeRepository> incomeRepositoryProvider;

  public DeleteIncomeUseCase_Factory(Provider<IncomeRepository> incomeRepositoryProvider) {
    this.incomeRepositoryProvider = incomeRepositoryProvider;
  }

  @Override
  public DeleteIncomeUseCase get() {
    return newInstance(incomeRepositoryProvider.get());
  }

  public static DeleteIncomeUseCase_Factory create(
      Provider<IncomeRepository> incomeRepositoryProvider) {
    return new DeleteIncomeUseCase_Factory(incomeRepositoryProvider);
  }

  public static DeleteIncomeUseCase newInstance(IncomeRepository incomeRepository) {
    return new DeleteIncomeUseCase(incomeRepository);
  }
}
