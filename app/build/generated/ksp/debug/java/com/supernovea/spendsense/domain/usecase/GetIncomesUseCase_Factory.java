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
public final class GetIncomesUseCase_Factory implements Factory<GetIncomesUseCase> {
  private final Provider<IncomeRepository> incomeRepositoryProvider;

  public GetIncomesUseCase_Factory(Provider<IncomeRepository> incomeRepositoryProvider) {
    this.incomeRepositoryProvider = incomeRepositoryProvider;
  }

  @Override
  public GetIncomesUseCase get() {
    return newInstance(incomeRepositoryProvider.get());
  }

  public static GetIncomesUseCase_Factory create(
      Provider<IncomeRepository> incomeRepositoryProvider) {
    return new GetIncomesUseCase_Factory(incomeRepositoryProvider);
  }

  public static GetIncomesUseCase newInstance(IncomeRepository incomeRepository) {
    return new GetIncomesUseCase(incomeRepository);
  }
}
