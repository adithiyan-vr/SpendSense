# SpendSense ProGuard / R8 Rules
# Copyright (c) 2026 Supernovea
# Developed by Adithiyan V R

# Kotlinx Serialization
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    static ** INSTANCE;
}

-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer();
}

-keep @kotlinx.serialization.Serializable class * { *; }
-keep class com.supernovea.spendsense.core.backup.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# WorkManager & Hilt
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.hilt.work.HiltWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
