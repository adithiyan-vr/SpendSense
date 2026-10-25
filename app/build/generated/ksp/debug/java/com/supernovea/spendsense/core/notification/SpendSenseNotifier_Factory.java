package com.supernovea.spendsense.core.notification;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SpendSenseNotifier_Factory implements Factory<SpendSenseNotifier> {
  private final Provider<Context> contextProvider;

  public SpendSenseNotifier_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SpendSenseNotifier get() {
    return newInstance(contextProvider.get());
  }

  public static SpendSenseNotifier_Factory create(Provider<Context> contextProvider) {
    return new SpendSenseNotifier_Factory(contextProvider);
  }

  public static SpendSenseNotifier newInstance(Context context) {
    return new SpendSenseNotifier(context);
  }
}
