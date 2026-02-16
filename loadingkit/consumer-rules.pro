# ---- Rules that the CONSUMER app should apply ----

# Keep your API (so consumer minify won't break calls)
-keep class com.aynal.loadingkit.LoadingKit { *; }
-keep class com.aynal.loadingkit.LoadingOptions { *; }
-keep class com.aynal.loadingkit.LoadingType { *; }
-keep class com.aynal.loadingkit.TextMode { *; }
-keep class com.aynal.loadingkit.SpinKitStyle { *; }
-keep class com.aynal.loadingkit.LoadingPreset { *; }

# Keep custom views used in XML
-keep class com.aynal.loadingkit.widgets.** { <init>(...); *; }

# Third-party warnings
-dontwarn com.airbnb.lottie.**
-dontwarn com.github.ybq.android.spinkit.**
