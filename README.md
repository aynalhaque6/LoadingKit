# LoadingKit (`com.aynal.loadingkit`)

A modern, brandable **Android Loading Library (Java)** with **Popup / Overlay / Fullscreen / InView** support.

**Default policy:** everything is **OFF** by default (loader + message). Only what you explicitly turn **ON** will be shown.

---

## Features

### Hosts (where to show)
- ✅ **Popup** (Custom Dialog)
- ✅ **Overlay** (Dim overlay view)
- ✅ **Fullscreen** (Solid screen overlay)
- ✅ **InView** (attach to any `ViewGroup`)

### Loaders (what to show)
- ✅ **SpinKit** (Android-SpinKit)
- ✅ **Lottie** (asset)
- ✅ **GIF** (asset or `res/raw`; API 28+ smooth, lower API fallback)
- ✅ **TEXT** loader (typing/dots/shimmer) → animates in **`lkMessage`**
- ✅ **Logo** loader (Pulse / Rotate / Bounce / Glow / Ripple)
- ✅ **Custom loaders** (no external lib): **RingPulse, BarsWave, DotsOrbit, ArcSpinner**

### Layout customization
- loader size  
- content padding  
- loader ↔ message gap  
- message maxWidth + maxLines  

---

## Quick Start (Java)

### Popup
```java
LoadingKit.popup().show(this,
    new LoadingOptions()
        .loader(LoadingType.SPINKIT)
        .showLoader(true)
        .showMessage(true)
        .message("Loading...")
        .spinkitStyle(SpinKitStyle.WAVE)
);
```

### Overlay
```java
LoadingKit.overlay().show(this,
    new LoadingOptions()
        .loader(LoadingType.CUSTOM)
        .customStyle(CustomStyle.DOTS_ORBIT)
        .showLoader(true)
        .showMessage(true)
        .message("Processing...")
        .dimAmount(0.55f)
);
```

### Fullscreen
```java
LoadingKit.fullscreen().show(this,
    new LoadingOptions()
        .loader(LoadingType.LOTTIE)
        .lottieAsset("lottie_loading.json", true)
        .showLoader(true)
        .showMessage(false)
        .backgroundColor(0xFFFFFFFF)
);
```

### InView
```java
ViewGroup container = findViewById(R.id.container);
LoadingKit.inView().attach(container,
    new LoadingOptions()
        .preset(LoadingPreset.MODERN_TEXT_TYPING)
        .showLoader(true)
        .showMessage(true)
        .message("Verifying...")
);
```

---

## Presets (one-line)

```java
LoadingOptions opts = new LoadingOptions()
    .preset(LoadingPreset.MODERN_LOGO_RIPPLE)
    .logoRes(R.mipmap.ic_launcher)
    .showLoader(true)
    .showMessage(true)
    .message("Please wait...");
```

---

## Layout / Size controls

```java
new LoadingOptions()
    .loader(LoadingType.GIF)
    .gifAsset("loading.gif", true)
    .showLoader(true)
    .showMessage(true)
    .message("Uploading…")
    .sizeDp(96)              // loader size
    .contentPaddingDp(18)    // card padding
    .gapDp(12)               // space between loader and text
    .messageMaxWidthDp(320)  // wrap width
    .messageMaxLines(5);
```

---

## Common Lottie Error

**`FileNotFoundException: lottie_loading.json`** usually means:
- The JSON file is not inside the `assets/` folder, or the filename is wrong.

✅ Fix:
1. Put the file here: `loadingkit/src/main/assets/your_file.json`
2. Use:
```java
.lottieAsset("your_file.json", true)
```
---

## Demo App

The `app` module includes a **Showcase UI** (Preset + Host + sliders).  
Run the **app** module to quickly test all loaders.
