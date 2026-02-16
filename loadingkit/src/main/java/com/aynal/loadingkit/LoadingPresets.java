package com.aynal.loadingkit;

import androidx.annotation.ColorInt;

/** Helper for applying presets to LoadingOptions. */
public final class LoadingPresets {
    private LoadingPresets() {}

    public static LoadingOptions apply(LoadingPreset preset) {
        return apply(preset, new LoadingOptions());
    }

    public static LoadingOptions apply(LoadingPreset preset, LoadingOptions o) {
        if (preset == null) return o;
        switch (preset) {

            case MODERN_SPINKIT_FADING_CIRCLE:
                o.loader(LoadingType.SPINKIT)
                        .spinkitStyle(SpinKitStyle.FADING_CIRCLE)
                        .sizeDp(72)
                        .contentPaddingDp(16)
                        .gapDp(10);
                return o;

            case MODERN_CUSTOM_RING:
                o.loader(LoadingType.CUSTOM)
                        .customStyle(CustomStyle.RING_PULSE)
                        .sizeDp(76)
                        .contentPaddingDp(16)
                        .gapDp(10);
                return o;

            case MODERN_CUSTOM_ORBIT:
                o.loader(LoadingType.CUSTOM)
                        .customStyle(CustomStyle.DOTS_ORBIT)
                        .sizeDp(80)
                        .contentPaddingDp(16)
                        .gapDp(10);
                return o;

            case MODERN_TEXT_DOTS:
                o.loader(LoadingType.TEXT)
                        .textMode(TextMode.DOTS)
                        .textAnimDuration(650)
                        .contentPaddingDp(18)
                        .messageMaxWidthDp(300)
                        .messageMaxLines(3);
                // TEXT runs on lkMessage; showLoader(true) already via loader(TEXT)
                return o;

            case MODERN_TEXT_TYPING:
                o.loader(LoadingType.TEXT)
                        .textMode(TextMode.TYPING)
                        .textAnimDuration(900)
                        .contentPaddingDp(18)
                        .messageMaxWidthDp(320)
                        .messageMaxLines(2);
                return o;

            case MODERN_LOGO_ROTATE:
                o.loader(LoadingType.LOGO_PULSE)
                        .logoMode(LogoMode.ROTATE)
                        .sizeDp(72)
                        .contentPaddingDp(18)
                        .gapDp(10);
                return o;

            case MODERN_LOGO_RIPPLE:
                o.loader(LoadingType.LOGO_PULSE)
                        .logoMode(LogoMode.RIPPLE)
                        .sizeDp(84)
                        .contentPaddingDp(18)
                        .gapDp(10);
                return o;


            case MODERN_GIF_DOTS:
                o.loader(LoadingType.GIF)
                        .gifAsset("loading_dots.gif", true)
                        .sizeDp(76)
                        .contentPaddingDp(16)
                        .gapDp(10);
                return o;

            case MODERN_SPINKIT_WAVE:
            default:
                o.loader(LoadingType.SPINKIT)
                        .spinkitStyle(SpinKitStyle.WAVE)
                        .sizeDp(72)
                        .contentPaddingDp(16)
                        .gapDp(10);
                return o;
        }
    }

    /** Quick theme helper: set both accent + message color. */
    public static LoadingOptions brandBlue(LoadingOptions o, @ColorInt int blue) {
        if (o == null) return null;
        o.accentColor(blue);
        o.messageTextColor(blue);
        return o;
    }
}
