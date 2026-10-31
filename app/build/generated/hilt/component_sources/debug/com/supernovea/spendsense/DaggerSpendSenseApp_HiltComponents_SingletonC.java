package com.supernovea.spendsense;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.supernovea.spendsense.core.backup.EncryptedBackupManager;
import com.supernovea.spendsense.core.backup.JsonBackupManager;
import com.supernovea.spendsense.core.di.DataStoreModule_ProvidePreferencesDataStoreFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideBudgetDaoFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideCategoryDaoFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideDueDaoFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideExpenseDaoFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideIncomeDaoFactory;
import com.supernovea.spendsense.core.di.DatabaseModule_ProvideIncomeSourceDaoFactory;
import com.supernovea.spendsense.core.di.DispatcherModule_ProvideIoDispatcherFactory;
import com.supernovea.spendsense.core.notification.SpendSenseNotifier;
import com.supernovea.spendsense.core.security.AppLockManager;
import com.supernovea.spendsense.core.security.DatabaseKeyManagerImpl;
import com.supernovea.spendsense.core.security.LockoutManager;
import com.supernovea.spendsense.core.security.PinManager;
import com.supernovea.spendsense.data.local.dao.BudgetDao;
import com.supernovea.spendsense.data.local.dao.CategoryDao;
import com.supernovea.spendsense.data.local.dao.DueDao;
import com.supernovea.spendsense.data.local.dao.ExpenseDao;
import com.supernovea.spendsense.data.local.dao.IncomeDao;
import com.supernovea.spendsense.data.local.dao.IncomeSourceDao;
import com.supernovea.spendsense.data.local.datastore.SettingsRepositoryImpl;
import com.supernovea.spendsense.data.local.db.AppDatabase;
import com.supernovea.spendsense.data.local.repository.BudgetRepositoryImpl;
import com.supernovea.spendsense.data.local.repository.CategoryRepositoryImpl;
import com.supernovea.spendsense.data.local.repository.DueRepositoryImpl;
import com.supernovea.spendsense.data.local.repository.ExpenseRepositoryImpl;
import com.supernovea.spendsense.data.local.repository.IncomeRepositoryImpl;
import com.supernovea.spendsense.data.local.repository.IncomeSourceRepositoryImpl;
import com.supernovea.spendsense.domain.usecase.AddDueUseCase;
import com.supernovea.spendsense.domain.usecase.AddExpenseUseCase;
import com.supernovea.spendsense.domain.usecase.AddIncomeUseCase;
import com.supernovea.spendsense.domain.usecase.CalculateSafeToSpendUseCase;
import com.supernovea.spendsense.domain.usecase.DeleteAllDataUseCase;
import com.supernovea.spendsense.domain.usecase.DeleteDueUseCase;
import com.supernovea.spendsense.domain.usecase.DeleteExpenseUseCase;
import com.supernovea.spendsense.domain.usecase.DeleteIncomeUseCase;
import com.supernovea.spendsense.domain.usecase.GetBudgetStatusUseCase;
import com.supernovea.spendsense.domain.usecase.GetDuesUseCase;
import com.supernovea.spendsense.domain.usecase.GetExpensesUseCase;
import com.supernovea.spendsense.domain.usecase.GetIncomesUseCase;
import com.supernovea.spendsense.domain.usecase.GetPeriodSummaryUseCase;
import com.supernovea.spendsense.domain.usecase.ManageCategoriesUseCase;
import com.supernovea.spendsense.domain.usecase.ManageIncomeSourcesUseCase;
import com.supernovea.spendsense.domain.usecase.MarkDuePaidUseCase;
import com.supernovea.spendsense.domain.usecase.RestoreExpenseUseCase;
import com.supernovea.spendsense.domain.usecase.RestoreIncomeUseCase;
import com.supernovea.spendsense.domain.usecase.SkipDueUseCase;
import com.supernovea.spendsense.domain.usecase.UpdateDueUseCase;
import com.supernovea.spendsense.domain.usecase.UpdateExpenseUseCase;
import com.supernovea.spendsense.domain.usecase.UpdateIncomeUseCase;
import com.supernovea.spendsense.feature.addexpense.AddExpenseViewModel;
import com.supernovea.spendsense.feature.addexpense.AddExpenseViewModel_HiltModules;
import com.supernovea.spendsense.feature.analysis.AnalysisViewModel;
import com.supernovea.spendsense.feature.analysis.AnalysisViewModel_HiltModules;
import com.supernovea.spendsense.feature.budget.BudgetViewModel;
import com.supernovea.spendsense.feature.budget.BudgetViewModel_HiltModules;
import com.supernovea.spendsense.feature.categories.CategoriesViewModel;
import com.supernovea.spendsense.feature.categories.CategoriesViewModel_HiltModules;
import com.supernovea.spendsense.feature.history.HistoryViewModel;
import com.supernovea.spendsense.feature.history.HistoryViewModel_HiltModules;
import com.supernovea.spendsense.feature.home.HomeViewModel;
import com.supernovea.spendsense.feature.home.HomeViewModel_HiltModules;
import com.supernovea.spendsense.feature.lock.LockViewModel;
import com.supernovea.spendsense.feature.lock.LockViewModel_HiltModules;
import com.supernovea.spendsense.feature.onboarding.OnboardingViewModel;
import com.supernovea.spendsense.feature.onboarding.OnboardingViewModel_HiltModules;
import com.supernovea.spendsense.feature.plan.PlanViewModel;
import com.supernovea.spendsense.feature.plan.PlanViewModel_HiltModules;
import com.supernovea.spendsense.feature.settings.PrivacySecurityViewModel;
import com.supernovea.spendsense.feature.settings.PrivacySecurityViewModel_HiltModules;
import com.supernovea.spendsense.feature.settings.SettingsViewModel;
import com.supernovea.spendsense.feature.settings.SettingsViewModel_HiltModules;
import com.supernovea.spendsense.work.DailyReminderWorker;
import com.supernovea.spendsense.work.DailyReminderWorker_AssistedFactory;
import com.supernovea.spendsense.work.DuesReminderWorker;
import com.supernovea.spendsense.work.DuesReminderWorker_AssistedFactory;
import com.supernovea.spendsense.work.MonthlySummaryWorker;
import com.supernovea.spendsense.work.MonthlySummaryWorker_AssistedFactory;
import com.supernovea.spendsense.work.PurgeDeletedWorker;
import com.supernovea.spendsense.work.PurgeDeletedWorker_AssistedFactory;
import com.supernovea.spendsense.work.WeeklySummaryWorker;
import com.supernovea.spendsense.work.WeeklySummaryWorker_AssistedFactory;
import com.supernovea.spendsense.work.WorkScheduler;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerSpendSenseApp_HiltComponents_SingletonC {
  private DaggerSpendSenseApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public SpendSenseApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements SpendSenseApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements SpendSenseApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements SpendSenseApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements SpendSenseApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements SpendSenseApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements SpendSenseApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements SpendSenseApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public SpendSenseApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends SpendSenseApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends SpendSenseApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends SpendSenseApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends SpendSenseApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(12).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_addexpense_AddExpenseViewModel, AddExpenseViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_analysis_AnalysisViewModel, AnalysisViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_budget_BudgetViewModel, BudgetViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_categories_CategoriesViewModel, CategoriesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_history_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_lock_LockViewModel, LockViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_MainViewModel, MainViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_plan_PlanViewModel, PlanViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_settings_PrivacySecurityViewModel, PrivacySecurityViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_supernovea_spendsense_feature_onboarding_OnboardingViewModel = "com.supernovea.spendsense.feature.onboarding.OnboardingViewModel";

      static String com_supernovea_spendsense_feature_history_HistoryViewModel = "com.supernovea.spendsense.feature.history.HistoryViewModel";

      static String com_supernovea_spendsense_MainViewModel = "com.supernovea.spendsense.MainViewModel";

      static String com_supernovea_spendsense_feature_settings_PrivacySecurityViewModel = "com.supernovea.spendsense.feature.settings.PrivacySecurityViewModel";

      static String com_supernovea_spendsense_feature_addexpense_AddExpenseViewModel = "com.supernovea.spendsense.feature.addexpense.AddExpenseViewModel";

      static String com_supernovea_spendsense_feature_home_HomeViewModel = "com.supernovea.spendsense.feature.home.HomeViewModel";

      static String com_supernovea_spendsense_feature_categories_CategoriesViewModel = "com.supernovea.spendsense.feature.categories.CategoriesViewModel";

      static String com_supernovea_spendsense_feature_analysis_AnalysisViewModel = "com.supernovea.spendsense.feature.analysis.AnalysisViewModel";

      static String com_supernovea_spendsense_feature_plan_PlanViewModel = "com.supernovea.spendsense.feature.plan.PlanViewModel";

      static String com_supernovea_spendsense_feature_settings_SettingsViewModel = "com.supernovea.spendsense.feature.settings.SettingsViewModel";

      static String com_supernovea_spendsense_feature_budget_BudgetViewModel = "com.supernovea.spendsense.feature.budget.BudgetViewModel";

      static String com_supernovea_spendsense_feature_lock_LockViewModel = "com.supernovea.spendsense.feature.lock.LockViewModel";

      @KeepFieldType
      OnboardingViewModel com_supernovea_spendsense_feature_onboarding_OnboardingViewModel2;

      @KeepFieldType
      HistoryViewModel com_supernovea_spendsense_feature_history_HistoryViewModel2;

      @KeepFieldType
      MainViewModel com_supernovea_spendsense_MainViewModel2;

      @KeepFieldType
      PrivacySecurityViewModel com_supernovea_spendsense_feature_settings_PrivacySecurityViewModel2;

      @KeepFieldType
      AddExpenseViewModel com_supernovea_spendsense_feature_addexpense_AddExpenseViewModel2;

      @KeepFieldType
      HomeViewModel com_supernovea_spendsense_feature_home_HomeViewModel2;

      @KeepFieldType
      CategoriesViewModel com_supernovea_spendsense_feature_categories_CategoriesViewModel2;

      @KeepFieldType
      AnalysisViewModel com_supernovea_spendsense_feature_analysis_AnalysisViewModel2;

      @KeepFieldType
      PlanViewModel com_supernovea_spendsense_feature_plan_PlanViewModel2;

      @KeepFieldType
      SettingsViewModel com_supernovea_spendsense_feature_settings_SettingsViewModel2;

      @KeepFieldType
      BudgetViewModel com_supernovea_spendsense_feature_budget_BudgetViewModel2;

      @KeepFieldType
      LockViewModel com_supernovea_spendsense_feature_lock_LockViewModel2;
    }
  }

  private static final class ViewModelCImpl extends SpendSenseApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AddExpenseViewModel> addExpenseViewModelProvider;

    private Provider<AnalysisViewModel> analysisViewModelProvider;

    private Provider<BudgetViewModel> budgetViewModelProvider;

    private Provider<CategoriesViewModel> categoriesViewModelProvider;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LockViewModel> lockViewModelProvider;

    private Provider<MainViewModel> mainViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PlanViewModel> planViewModelProvider;

    private Provider<PrivacySecurityViewModel> privacySecurityViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private AddExpenseUseCase addExpenseUseCase() {
      return new AddExpenseUseCase(singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.categoryRepositoryImplProvider.get());
    }

    private UpdateExpenseUseCase updateExpenseUseCase() {
      return new UpdateExpenseUseCase(singletonCImpl.expenseRepositoryImplProvider.get());
    }

    private GetExpensesUseCase getExpensesUseCase() {
      return new GetExpensesUseCase(singletonCImpl.expenseRepositoryImplProvider.get());
    }

    private ManageCategoriesUseCase manageCategoriesUseCase() {
      return new ManageCategoriesUseCase(singletonCImpl.categoryRepositoryImplProvider.get());
    }

    private AddIncomeUseCase addIncomeUseCase() {
      return new AddIncomeUseCase(singletonCImpl.incomeRepositoryImplProvider.get(), singletonCImpl.incomeSourceRepositoryImplProvider.get());
    }

    private UpdateIncomeUseCase updateIncomeUseCase() {
      return new UpdateIncomeUseCase(singletonCImpl.incomeRepositoryImplProvider.get(), singletonCImpl.incomeSourceRepositoryImplProvider.get());
    }

    private GetIncomesUseCase getIncomesUseCase() {
      return new GetIncomesUseCase(singletonCImpl.incomeRepositoryImplProvider.get());
    }

    private ManageIncomeSourcesUseCase manageIncomeSourcesUseCase() {
      return new ManageIncomeSourcesUseCase(singletonCImpl.incomeSourceRepositoryImplProvider.get());
    }

    private GetBudgetStatusUseCase getBudgetStatusUseCase() {
      return new GetBudgetStatusUseCase(singletonCImpl.budgetRepositoryImplProvider.get(), singletonCImpl.expenseRepositoryImplProvider.get());
    }

    private DeleteExpenseUseCase deleteExpenseUseCase() {
      return new DeleteExpenseUseCase(singletonCImpl.expenseRepositoryImplProvider.get());
    }

    private RestoreExpenseUseCase restoreExpenseUseCase() {
      return new RestoreExpenseUseCase(singletonCImpl.expenseRepositoryImplProvider.get());
    }

    private DeleteIncomeUseCase deleteIncomeUseCase() {
      return new DeleteIncomeUseCase(singletonCImpl.incomeRepositoryImplProvider.get());
    }

    private RestoreIncomeUseCase restoreIncomeUseCase() {
      return new RestoreIncomeUseCase(singletonCImpl.incomeRepositoryImplProvider.get());
    }

    private GetDuesUseCase getDuesUseCase() {
      return new GetDuesUseCase(singletonCImpl.dueRepositoryImplProvider.get());
    }

    private MarkDuePaidUseCase markDuePaidUseCase() {
      return new MarkDuePaidUseCase(singletonCImpl.dueRepositoryImplProvider.get(), singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.categoryRepositoryImplProvider.get());
    }

    private CalculateSafeToSpendUseCase calculateSafeToSpendUseCase() {
      return new CalculateSafeToSpendUseCase(singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.incomeRepositoryImplProvider.get(), singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.dueRepositoryImplProvider.get());
    }

    private AddDueUseCase addDueUseCase() {
      return new AddDueUseCase(singletonCImpl.dueRepositoryImplProvider.get(), singletonCImpl.categoryRepositoryImplProvider.get());
    }

    private UpdateDueUseCase updateDueUseCase() {
      return new UpdateDueUseCase(singletonCImpl.dueRepositoryImplProvider.get(), singletonCImpl.categoryRepositoryImplProvider.get());
    }

    private DeleteDueUseCase deleteDueUseCase() {
      return new DeleteDueUseCase(singletonCImpl.dueRepositoryImplProvider.get());
    }

    private SkipDueUseCase skipDueUseCase() {
      return new SkipDueUseCase(singletonCImpl.dueRepositoryImplProvider.get());
    }

    private DeleteAllDataUseCase deleteAllDataUseCase() {
      return new DeleteAllDataUseCase(singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.categoryRepositoryImplProvider.get(), singletonCImpl.budgetRepositoryImplProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.incomeRepositoryImplProvider.get(), singletonCImpl.incomeSourceRepositoryImplProvider.get(), singletonCImpl.dueRepositoryImplProvider.get(), singletonCImpl.pinManagerProvider.get(), singletonCImpl.lockoutManagerProvider.get(), singletonCImpl.appLockManagerProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.addExpenseViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.analysisViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.budgetViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.categoriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.lockViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.planViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.privacySecurityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(12).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_addexpense_AddExpenseViewModel, ((Provider) addExpenseViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_analysis_AnalysisViewModel, ((Provider) analysisViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_budget_BudgetViewModel, ((Provider) budgetViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_categories_CategoriesViewModel, ((Provider) categoriesViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_history_HistoryViewModel, ((Provider) historyViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_lock_LockViewModel, ((Provider) lockViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_MainViewModel, ((Provider) mainViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_plan_PlanViewModel, ((Provider) planViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_settings_PrivacySecurityViewModel, ((Provider) privacySecurityViewModelProvider)).put(LazyClassKeyProvider.com_supernovea_spendsense_feature_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_supernovea_spendsense_feature_onboarding_OnboardingViewModel = "com.supernovea.spendsense.feature.onboarding.OnboardingViewModel";

      static String com_supernovea_spendsense_feature_plan_PlanViewModel = "com.supernovea.spendsense.feature.plan.PlanViewModel";

      static String com_supernovea_spendsense_feature_categories_CategoriesViewModel = "com.supernovea.spendsense.feature.categories.CategoriesViewModel";

      static String com_supernovea_spendsense_feature_settings_SettingsViewModel = "com.supernovea.spendsense.feature.settings.SettingsViewModel";

      static String com_supernovea_spendsense_feature_addexpense_AddExpenseViewModel = "com.supernovea.spendsense.feature.addexpense.AddExpenseViewModel";

      static String com_supernovea_spendsense_feature_settings_PrivacySecurityViewModel = "com.supernovea.spendsense.feature.settings.PrivacySecurityViewModel";

      static String com_supernovea_spendsense_feature_analysis_AnalysisViewModel = "com.supernovea.spendsense.feature.analysis.AnalysisViewModel";

      static String com_supernovea_spendsense_feature_budget_BudgetViewModel = "com.supernovea.spendsense.feature.budget.BudgetViewModel";

      static String com_supernovea_spendsense_feature_home_HomeViewModel = "com.supernovea.spendsense.feature.home.HomeViewModel";

      static String com_supernovea_spendsense_feature_lock_LockViewModel = "com.supernovea.spendsense.feature.lock.LockViewModel";

      static String com_supernovea_spendsense_MainViewModel = "com.supernovea.spendsense.MainViewModel";

      static String com_supernovea_spendsense_feature_history_HistoryViewModel = "com.supernovea.spendsense.feature.history.HistoryViewModel";

      @KeepFieldType
      OnboardingViewModel com_supernovea_spendsense_feature_onboarding_OnboardingViewModel2;

      @KeepFieldType
      PlanViewModel com_supernovea_spendsense_feature_plan_PlanViewModel2;

      @KeepFieldType
      CategoriesViewModel com_supernovea_spendsense_feature_categories_CategoriesViewModel2;

      @KeepFieldType
      SettingsViewModel com_supernovea_spendsense_feature_settings_SettingsViewModel2;

      @KeepFieldType
      AddExpenseViewModel com_supernovea_spendsense_feature_addexpense_AddExpenseViewModel2;

      @KeepFieldType
      PrivacySecurityViewModel com_supernovea_spendsense_feature_settings_PrivacySecurityViewModel2;

      @KeepFieldType
      AnalysisViewModel com_supernovea_spendsense_feature_analysis_AnalysisViewModel2;

      @KeepFieldType
      BudgetViewModel com_supernovea_spendsense_feature_budget_BudgetViewModel2;

      @KeepFieldType
      HomeViewModel com_supernovea_spendsense_feature_home_HomeViewModel2;

      @KeepFieldType
      LockViewModel com_supernovea_spendsense_feature_lock_LockViewModel2;

      @KeepFieldType
      MainViewModel com_supernovea_spendsense_MainViewModel2;

      @KeepFieldType
      HistoryViewModel com_supernovea_spendsense_feature_history_HistoryViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.supernovea.spendsense.feature.addexpense.AddExpenseViewModel 
          return (T) new AddExpenseViewModel(viewModelCImpl.savedStateHandle, viewModelCImpl.addExpenseUseCase(), viewModelCImpl.updateExpenseUseCase(), viewModelCImpl.getExpensesUseCase(), viewModelCImpl.manageCategoriesUseCase(), viewModelCImpl.addIncomeUseCase(), viewModelCImpl.updateIncomeUseCase(), viewModelCImpl.getIncomesUseCase(), viewModelCImpl.manageIncomeSourcesUseCase(), singletonCImpl.settingsRepositoryImplProvider.get());

          case 1: // com.supernovea.spendsense.feature.analysis.AnalysisViewModel 
          return (T) new AnalysisViewModel(singletonCImpl.getPeriodSummaryUseCase(), singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.incomeRepositoryImplProvider.get());

          case 2: // com.supernovea.spendsense.feature.budget.BudgetViewModel 
          return (T) new BudgetViewModel(viewModelCImpl.getBudgetStatusUseCase(), singletonCImpl.budgetRepositoryImplProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get());

          case 3: // com.supernovea.spendsense.feature.categories.CategoriesViewModel 
          return (T) new CategoriesViewModel(viewModelCImpl.manageCategoriesUseCase());

          case 4: // com.supernovea.spendsense.feature.history.HistoryViewModel 
          return (T) new HistoryViewModel(viewModelCImpl.getExpensesUseCase(), viewModelCImpl.deleteExpenseUseCase(), viewModelCImpl.restoreExpenseUseCase(), viewModelCImpl.getIncomesUseCase(), viewModelCImpl.deleteIncomeUseCase(), viewModelCImpl.restoreIncomeUseCase(), viewModelCImpl.manageCategoriesUseCase());

          case 5: // com.supernovea.spendsense.feature.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.getPeriodSummaryUseCase(), viewModelCImpl.getExpensesUseCase(), viewModelCImpl.getIncomesUseCase(), viewModelCImpl.getDuesUseCase(), viewModelCImpl.markDuePaidUseCase(), singletonCImpl.settingsRepositoryImplProvider.get());

          case 6: // com.supernovea.spendsense.feature.lock.LockViewModel 
          return (T) new LockViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.appLockManagerProvider.get(), singletonCImpl.pinManagerProvider.get(), singletonCImpl.lockoutManagerProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get());

          case 7: // com.supernovea.spendsense.MainViewModel 
          return (T) new MainViewModel(singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.appLockManagerProvider.get());

          case 8: // com.supernovea.spendsense.feature.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.settingsRepositoryImplProvider.get());

          case 9: // com.supernovea.spendsense.feature.plan.PlanViewModel 
          return (T) new PlanViewModel(viewModelCImpl.calculateSafeToSpendUseCase(), viewModelCImpl.getDuesUseCase(), viewModelCImpl.addDueUseCase(), viewModelCImpl.updateDueUseCase(), viewModelCImpl.deleteDueUseCase(), viewModelCImpl.markDuePaidUseCase(), viewModelCImpl.skipDueUseCase(), singletonCImpl.categoryRepositoryImplProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get());

          case 10: // com.supernovea.spendsense.feature.settings.PrivacySecurityViewModel 
          return (T) new PrivacySecurityViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.pinManagerProvider.get(), singletonCImpl.lockoutManagerProvider.get(), singletonCImpl.appLockManagerProvider.get(), singletonCImpl.encryptedBackupManagerProvider.get(), viewModelCImpl.deleteAllDataUseCase());

          case 11: // com.supernovea.spendsense.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.settingsRepositoryImplProvider.get(), viewModelCImpl.deleteAllDataUseCase(), singletonCImpl.jsonBackupManagerProvider.get(), singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.incomeRepositoryImplProvider.get(), singletonCImpl.workSchedulerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends SpendSenseApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends SpendSenseApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends SpendSenseApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<DataStore<Preferences>> providePreferencesDataStoreProvider;

    private Provider<SettingsRepositoryImpl> settingsRepositoryImplProvider;

    private Provider<SpendSenseNotifier> spendSenseNotifierProvider;

    private Provider<DailyReminderWorker_AssistedFactory> dailyReminderWorker_AssistedFactoryProvider;

    private Provider<DatabaseKeyManagerImpl> databaseKeyManagerImplProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<DueRepositoryImpl> dueRepositoryImplProvider;

    private Provider<DuesReminderWorker_AssistedFactory> duesReminderWorker_AssistedFactoryProvider;

    private Provider<ExpenseRepositoryImpl> expenseRepositoryImplProvider;

    private Provider<IncomeRepositoryImpl> incomeRepositoryImplProvider;

    private Provider<BudgetRepositoryImpl> budgetRepositoryImplProvider;

    private Provider<MonthlySummaryWorker_AssistedFactory> monthlySummaryWorker_AssistedFactoryProvider;

    private Provider<PurgeDeletedWorker_AssistedFactory> purgeDeletedWorker_AssistedFactoryProvider;

    private Provider<WeeklySummaryWorker_AssistedFactory> weeklySummaryWorker_AssistedFactoryProvider;

    private Provider<WorkScheduler> workSchedulerProvider;

    private Provider<CategoryRepositoryImpl> categoryRepositoryImplProvider;

    private Provider<IncomeSourceRepositoryImpl> incomeSourceRepositoryImplProvider;

    private Provider<PinManager> pinManagerProvider;

    private Provider<AppLockManager> appLockManagerProvider;

    private Provider<LockoutManager> lockoutManagerProvider;

    private Provider<JsonBackupManager> jsonBackupManagerProvider;

    private Provider<EncryptedBackupManager> encryptedBackupManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private DueDao dueDao() {
      return DatabaseModule_ProvideDueDaoFactory.provideDueDao(provideAppDatabaseProvider.get());
    }

    private ExpenseDao expenseDao() {
      return DatabaseModule_ProvideExpenseDaoFactory.provideExpenseDao(provideAppDatabaseProvider.get());
    }

    private IncomeDao incomeDao() {
      return DatabaseModule_ProvideIncomeDaoFactory.provideIncomeDao(provideAppDatabaseProvider.get());
    }

    private BudgetDao budgetDao() {
      return DatabaseModule_ProvideBudgetDaoFactory.provideBudgetDao(provideAppDatabaseProvider.get());
    }

    private GetPeriodSummaryUseCase getPeriodSummaryUseCase() {
      return new GetPeriodSummaryUseCase(expenseRepositoryImplProvider.get(), incomeRepositoryImplProvider.get(), budgetRepositoryImplProvider.get(), settingsRepositoryImplProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(5).put("com.supernovea.spendsense.work.DailyReminderWorker", ((Provider) dailyReminderWorker_AssistedFactoryProvider)).put("com.supernovea.spendsense.work.DuesReminderWorker", ((Provider) duesReminderWorker_AssistedFactoryProvider)).put("com.supernovea.spendsense.work.MonthlySummaryWorker", ((Provider) monthlySummaryWorker_AssistedFactoryProvider)).put("com.supernovea.spendsense.work.PurgeDeletedWorker", ((Provider) purgeDeletedWorker_AssistedFactoryProvider)).put("com.supernovea.spendsense.work.WeeklySummaryWorker", ((Provider) weeklySummaryWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideAppDatabaseProvider.get());
    }

    private IncomeSourceDao incomeSourceDao() {
      return DatabaseModule_ProvideIncomeSourceDaoFactory.provideIncomeSourceDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.providePreferencesDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 2));
      this.settingsRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepositoryImpl>(singletonCImpl, 1));
      this.spendSenseNotifierProvider = DoubleCheck.provider(new SwitchingProvider<SpendSenseNotifier>(singletonCImpl, 3));
      this.dailyReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<DailyReminderWorker_AssistedFactory>(singletonCImpl, 0));
      this.databaseKeyManagerImplProvider = DoubleCheck.provider(new SwitchingProvider<DatabaseKeyManagerImpl>(singletonCImpl, 7));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 6));
      this.dueRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<DueRepositoryImpl>(singletonCImpl, 5));
      this.duesReminderWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<DuesReminderWorker_AssistedFactory>(singletonCImpl, 4));
      this.expenseRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ExpenseRepositoryImpl>(singletonCImpl, 9));
      this.incomeRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<IncomeRepositoryImpl>(singletonCImpl, 10));
      this.budgetRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<BudgetRepositoryImpl>(singletonCImpl, 11));
      this.monthlySummaryWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<MonthlySummaryWorker_AssistedFactory>(singletonCImpl, 8));
      this.purgeDeletedWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<PurgeDeletedWorker_AssistedFactory>(singletonCImpl, 12));
      this.weeklySummaryWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<WeeklySummaryWorker_AssistedFactory>(singletonCImpl, 13));
      this.workSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<WorkScheduler>(singletonCImpl, 14));
      this.categoryRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CategoryRepositoryImpl>(singletonCImpl, 15));
      this.incomeSourceRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<IncomeSourceRepositoryImpl>(singletonCImpl, 16));
      this.pinManagerProvider = DoubleCheck.provider(new SwitchingProvider<PinManager>(singletonCImpl, 18));
      this.appLockManagerProvider = DoubleCheck.provider(new SwitchingProvider<AppLockManager>(singletonCImpl, 17));
      this.lockoutManagerProvider = DoubleCheck.provider(new SwitchingProvider<LockoutManager>(singletonCImpl, 19));
      this.jsonBackupManagerProvider = DoubleCheck.provider(new SwitchingProvider<JsonBackupManager>(singletonCImpl, 21));
      this.encryptedBackupManagerProvider = DoubleCheck.provider(new SwitchingProvider<EncryptedBackupManager>(singletonCImpl, 20));
    }

    @Override
    public void injectSpendSenseApp(SpendSenseApp spendSenseApp) {
      injectSpendSenseApp2(spendSenseApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private SpendSenseApp injectSpendSenseApp2(SpendSenseApp instance) {
      SpendSenseApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      SpendSenseApp_MembersInjector.injectWorkScheduler(instance, workSchedulerProvider.get());
      SpendSenseApp_MembersInjector.injectSettingsRepository(instance, settingsRepositoryImplProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.supernovea.spendsense.work.DailyReminderWorker_AssistedFactory 
          return (T) new DailyReminderWorker_AssistedFactory() {
            @Override
            public DailyReminderWorker create(Context appContext, WorkerParameters workerParams) {
              return new DailyReminderWorker(appContext, workerParams, singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.spendSenseNotifierProvider.get());
            }
          };

          case 1: // com.supernovea.spendsense.data.local.datastore.SettingsRepositoryImpl 
          return (T) new SettingsRepositoryImpl(singletonCImpl.providePreferencesDataStoreProvider.get(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 2: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) DataStoreModule_ProvidePreferencesDataStoreFactory.providePreferencesDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.supernovea.spendsense.core.notification.SpendSenseNotifier 
          return (T) new SpendSenseNotifier(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.supernovea.spendsense.work.DuesReminderWorker_AssistedFactory 
          return (T) new DuesReminderWorker_AssistedFactory() {
            @Override
            public DuesReminderWorker create(Context appContext2, WorkerParameters workerParams2) {
              return new DuesReminderWorker(appContext2, workerParams2, singletonCImpl.dueRepositoryImplProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.spendSenseNotifierProvider.get());
            }
          };

          case 5: // com.supernovea.spendsense.data.local.repository.DueRepositoryImpl 
          return (T) new DueRepositoryImpl(singletonCImpl.dueDao(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 6: // com.supernovea.spendsense.data.local.db.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.databaseKeyManagerImplProvider.get());

          case 7: // com.supernovea.spendsense.core.security.DatabaseKeyManagerImpl 
          return (T) new DatabaseKeyManagerImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.supernovea.spendsense.work.MonthlySummaryWorker_AssistedFactory 
          return (T) new MonthlySummaryWorker_AssistedFactory() {
            @Override
            public MonthlySummaryWorker create(Context appContext3,
                WorkerParameters workerParams3) {
              return new MonthlySummaryWorker(appContext3, workerParams3, singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.getPeriodSummaryUseCase(), singletonCImpl.spendSenseNotifierProvider.get());
            }
          };

          case 9: // com.supernovea.spendsense.data.local.repository.ExpenseRepositoryImpl 
          return (T) new ExpenseRepositoryImpl(singletonCImpl.expenseDao(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 10: // com.supernovea.spendsense.data.local.repository.IncomeRepositoryImpl 
          return (T) new IncomeRepositoryImpl(singletonCImpl.incomeDao(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 11: // com.supernovea.spendsense.data.local.repository.BudgetRepositoryImpl 
          return (T) new BudgetRepositoryImpl(singletonCImpl.budgetDao(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 12: // com.supernovea.spendsense.work.PurgeDeletedWorker_AssistedFactory 
          return (T) new PurgeDeletedWorker_AssistedFactory() {
            @Override
            public PurgeDeletedWorker create(Context appContext4, WorkerParameters workerParams4) {
              return new PurgeDeletedWorker(appContext4, workerParams4, singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.incomeRepositoryImplProvider.get());
            }
          };

          case 13: // com.supernovea.spendsense.work.WeeklySummaryWorker_AssistedFactory 
          return (T) new WeeklySummaryWorker_AssistedFactory() {
            @Override
            public WeeklySummaryWorker create(Context appContext5, WorkerParameters workerParams5) {
              return new WeeklySummaryWorker(appContext5, workerParams5, singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.getPeriodSummaryUseCase(), singletonCImpl.spendSenseNotifierProvider.get());
            }
          };

          case 14: // com.supernovea.spendsense.work.WorkScheduler 
          return (T) new WorkScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 15: // com.supernovea.spendsense.data.local.repository.CategoryRepositoryImpl 
          return (T) new CategoryRepositoryImpl(singletonCImpl.categoryDao(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 16: // com.supernovea.spendsense.data.local.repository.IncomeSourceRepositoryImpl 
          return (T) new IncomeSourceRepositoryImpl(singletonCImpl.incomeSourceDao(), DispatcherModule_ProvideIoDispatcherFactory.provideIoDispatcher());

          case 17: // com.supernovea.spendsense.core.security.AppLockManager 
          return (T) new AppLockManager(singletonCImpl.pinManagerProvider.get());

          case 18: // com.supernovea.spendsense.core.security.PinManager 
          return (T) new PinManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 19: // com.supernovea.spendsense.core.security.LockoutManager 
          return (T) new LockoutManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 20: // com.supernovea.spendsense.core.backup.EncryptedBackupManager 
          return (T) new EncryptedBackupManager(singletonCImpl.jsonBackupManagerProvider.get());

          case 21: // com.supernovea.spendsense.core.backup.JsonBackupManager 
          return (T) new JsonBackupManager(singletonCImpl.expenseRepositoryImplProvider.get(), singletonCImpl.categoryRepositoryImplProvider.get(), singletonCImpl.budgetRepositoryImplProvider.get(), singletonCImpl.settingsRepositoryImplProvider.get(), singletonCImpl.incomeRepositoryImplProvider.get(), singletonCImpl.incomeSourceRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
