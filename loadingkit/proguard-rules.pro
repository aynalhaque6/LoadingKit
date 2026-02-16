# ---- Obfuscation hardening for LIBRARY build ----
-optimizationpasses 5
-dontpreverify
-repackageclasses
-overloadaggressively
-allowaccessmodification

# Keep library public API names stable (যেগুলো বাইরে থেকে ব্যবহার হবে)
-keep class com.aynal.loadingkit.LoadingKit { *; }
-keep class com.aynal.loadingkit.LoadingOptions { *; }
-keep class com.aynal.loadingkit.LoadingType { *; }
-keep class com.aynal.loadingkit.TextMode { *; }
-keep class com.aynal.loadingkit.SpinKitStyle { *; }
-keep class com.aynal.loadingkit.LoadingPreset { *; }

# Keep public widgets referenced from XML
-keep class com.aynal.loadingkit.widgets.** { <init>(...); *; }

# Everything else: allow obfuscation (default)

# Remove logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Avoid warnings
-dontwarn com.airbnb.lottie.**
-dontwarn com.github.ybq.android.spinkit.**
