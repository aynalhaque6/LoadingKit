package com.aynal.loadingkit;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.annotation.DrawableRes;

public class LoadingOptions {

    // ✅ default: nothing
    public LoadingType type = LoadingType.NONE;

    // Common
    public boolean cancelable = false;

    // ✅ default OFF (আপনি ON করলে দেখাবে)
    public boolean showLoader = false;
    public boolean showMessage = false;

    // Interaction
    public boolean blockTouches = true;

    // Text
    public String message = "Loading...";
    @ColorInt public int accentColor = 0xFF1E88E5;        // brand blue
    @ColorInt public int messageTextColor = 0xFF1E88E5;

    // Size (for non-text loaders)
    public int sizeDp = 72;

    // Layout / spacing (applies to popup + overlay + fullscreen + in-view)
    // ✅ You can control size/padding/margin from code.
    public int contentPaddingDp = 14;      // padding around content container
    public int gapDp = 10;                // space between loader and message
    public int messageMaxWidthDp = 260;   // wrap width for long texts
    public int messageMaxLines = 4;       // max wrap lines

    // Overlay visuals
    public float dimAmount = 0.55f;                      // 0..1
    @ColorInt public int backgroundColor = 0xFFFFFFFF;   // fullscreen default white

    // Lottie
    @Nullable public String lottieAssetName = null;
    public boolean lottieLoop = true;

    // GIF
    @Nullable public String gifAssetName = null;
    @RawRes public int gifRawResId = 0;
    public boolean gifLoop = true;

    // Fallback (mostly for lottie/gif decode fail)
    public LoadingType fallbackType = LoadingType.SPINKIT;

    // SpinKit
    public SpinKitStyle spinkitStyle = SpinKitStyle.WAVE;

    // Custom loaders
    public CustomStyle customStyle = CustomStyle.RING_PULSE;

    // Logo-only loader
    @DrawableRes public int logoResId = 0;
    public LogoMode logoMode = LogoMode.PULSE;

    // Text loader (DOTS / TYPING / SHIMMER) — runs on lkMessage when type=TEXT
    public TextMode textMode = TextMode.DOTS;
    public long textAnimDurationMs = 700;     // dots / typing speed base
    public long shimmerDurationMs = 1100;

    public LoadingOptions() {}

    // ---------- Core setters ----------
    public LoadingOptions cancelable(boolean v) { this.cancelable = v; return this; }

    public LoadingOptions showLoader(boolean v) { this.showLoader = v; return this; }
    public LoadingOptions showMessage(boolean v) { this.showMessage = v; return this; }

    public LoadingOptions blockTouches(boolean v) { this.blockTouches = v; return this; }

    public LoadingOptions accentColor(@ColorInt int c) { this.accentColor = c; return this; }
    public LoadingOptions messageTextColor(@ColorInt int c) { this.messageTextColor = c; return this; }

    public LoadingOptions sizeDp(int dp) { this.sizeDp = dp; return this; }

    public LoadingOptions contentPaddingDp(int dp) {
        if (dp >= 0) this.contentPaddingDp = dp;
        return this;
    }

    public LoadingOptions gapDp(int dp) {
        if (dp >= 0) this.gapDp = dp;
        return this;
    }

    public LoadingOptions messageMaxWidthDp(int dp) {
        if (dp > 0) this.messageMaxWidthDp = dp;
        return this;
    }

    public LoadingOptions messageMaxLines(int lines) {
        if (lines > 0) this.messageMaxLines = lines;
        return this;
    }

    /** Copy layout-related values from another options object. */
    public LoadingOptions layoutFrom(LoadingOptions other) {
        if (other == null) return this;
        this.sizeDp = other.sizeDp;
        this.contentPaddingDp = other.contentPaddingDp;
        this.gapDp = other.gapDp;
        this.messageMaxWidthDp = other.messageMaxWidthDp;
        this.messageMaxLines = other.messageMaxLines;
        return this;
    }

    public LoadingOptions dimAmount(float v) { this.dimAmount = clamp01(v); return this; }
    public LoadingOptions backgroundColor(@ColorInt int c) { this.backgroundColor = c; return this; }

    /**
     * Convenience: set loader type and automatically turn loader ON.
     * You can still call showLoader(false) after if you want to keep it OFF.
     */
    public LoadingOptions loader(LoadingType t) {
        this.type = (t != null) ? t : LoadingType.NONE;
        this.showLoader = (this.type != LoadingType.NONE);
        return this;
    }

    /**
     * Message fallback rule:
     * - null/empty -> default "Loading..."
     * - DOES NOT auto-enable showMessage (আপনি ON করলে দেখাবে)
     */
    public LoadingOptions message(String v) {
        if (v == null || v.trim().isEmpty()) {
            this.message = "Loading...";
        } else {
            this.message = v;
        }
        return this;
    }

    // ---------- Lottie ----------
    public LoadingOptions lottieAsset(String assetName, boolean loop) {
        this.lottieAssetName = assetName;
        this.lottieLoop = loop;
        return this;
    }

    // ---------- GIF ----------
    public LoadingOptions gifAsset(String assetName, boolean loop) {
        this.gifAssetName = assetName;
        this.gifRawResId = 0;
        this.gifLoop = loop;
        return this;
    }

    public LoadingOptions gifRaw(@RawRes int rawResId, boolean loop) {
        this.gifRawResId = rawResId;
        this.gifAssetName = null;
        this.gifLoop = loop;
        return this;
    }

    public LoadingOptions fallbackType(LoadingType t) {
        this.fallbackType = (t != null) ? t : LoadingType.SPINKIT;
        return this;
    }

    // ---------- SpinKit ----------
    public LoadingOptions spinkitStyle(SpinKitStyle style) {
        if (style != null) this.spinkitStyle = style;
        return this;
    }

    // ---------- Custom ----------
    public LoadingOptions customStyle(CustomStyle style) {
        if (style != null) this.customStyle = style;
        return this;
    }

    // ---------- Logo ----------
    public LoadingOptions logoRes(@DrawableRes int resId) {
        this.logoResId = resId;
        return this;
    }

    public LoadingOptions logoMode(LogoMode mode) {
        if (mode != null) this.logoMode = mode;
        return this;
    }

    // ---------- Preset ----------
    public LoadingOptions preset(LoadingPreset preset) {
        LoadingPresets.apply(preset, this);
        return this;
    }

    // ---------- Text ----------
    public LoadingOptions textMode(TextMode mode) {
        if (mode != null) this.textMode = mode;
        return this;
    }

    public LoadingOptions textAnimDuration(long ms) {
        if (ms > 100) this.textAnimDurationMs = ms;
        return this;
    }

    public LoadingOptions shimmerDuration(long ms) {
        if (ms > 200) this.shimmerDurationMs = ms;
        return this;
    }

    private float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
