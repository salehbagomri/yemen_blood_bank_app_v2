# Proguard Rules for Yemen Blood Bank App

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class * {
    <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Supabase & Ktor
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }
-dontwarn io.github.jan.supabase.**
-keep class io.github.jan.supabase.** { *; }

# Data Models
-keep class com.bagomri.yemenbloodbank.data.model.** { *; }
-keepclassmembers class com.bagomri.yemenbloodbank.data.model.** { *; }

# OkHttp & Netty
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**
