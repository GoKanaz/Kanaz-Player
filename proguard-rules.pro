# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityComponentManager_ActivityComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_FragmentComponentManager_FragmentComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_FragmentComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewComponentManager_ViewComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewCreatorComponentManager_ViewCreatorComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewCreatorComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory$1 { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory$ViewModelModule { *; }
-keep class dagger.hilt.android.internal.builders.{ActivityComponentBuilder,FragmentComponentBuilder,ViewComponentBuilder,ServiceComponentBuilder,ViewModelComponentBuilder} { *; }
-keep class dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_LifecycleEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ActivityComponentManager_ActivityComponentBuilderEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ActivityComponentManager_LifecycleEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_FragmentComponentManager_FragmentComponentBuilderEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_FragmentComponentManager_LifecycleEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ViewComponentManager_ViewComponentBuilderEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ViewComponentManager_LifecycleEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ViewCreatorComponentManager_ViewCreatorComponentBuilderEntryPoint { *; }
-keep class dagger.hilt.android.internal.managers.HiltWrapper_ViewCreatorComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityComponentManager_ActivityComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ActivityComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_FragmentComponentManager_FragmentComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_FragmentComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewComponentManager_ViewComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewComponentManager_LifecycleEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewCreatorComponentManager_ViewCreatorComponentBuilderEntryPoint { *; }
-keep class * extends dagger.hilt.android.internal.managers.HiltWrapper_ViewCreatorComponentManager_LifecycleEntryPoint { *; }
-keep class dagger.hilt.internal.GeneratedComponentManagerHolder { *; }

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-keep class com.google.android.exoplayer2.** { *; }
-keep class * implements androidx.media3.common.Player {
    public <methods>;
}
-keep class * implements androidx.media3.exoplayer.source.MediaSource {
    public <methods>;
}
-keep class * implements androidx.media3.datasource.DataSource {
    public <methods>;
}
-keep class * implements androidx.media3.extractor.Extractor {
    public <methods>;
}
-keep class * implements androidx.media3.decoder.Decoder {
    public <methods>;
}
-keep class * implements androidx.media3.session.MediaSession {
    public <methods>;
}
-keep class * implements androidx.media3.session.MediaSessionService {
    public <methods>;
}
-keep class * implements androidx.media3.common.MediaItem {
    public <methods>;
}
-keep class * implements androidx.media3.common.MediaMetadata {
    public <methods>;
}
-keepattributes InnerClasses, Signature
-dontwarn androidx.media3.**

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.android.** { *; }

# Keep model classes
-keep class com.gokanaz.kanazplayer.core.data.model.** { *; }
-keep class com.gokanaz.kanazplayer.core.domain.model.** { *; }

# Keep ViewModel
-keep class com.gokanaz.kanazplayer.**.ViewModel { *; }
-keep class com.gokanaz.kanazplayer.**.*ViewModel { *; }

# Keep Composables
-keep @androidx.compose.runtime.Composable class * {
    <methods>;
}

# Serializable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# General rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepclassmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keep @androidx.annotation.Keep class * { *; }
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}