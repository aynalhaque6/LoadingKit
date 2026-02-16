package com.aynal.loadingkit;

/**
 * Loader renderer type.
 *
 * Notes:
 * - TEXT means: the animation runs on the message TextView (lkMessage) itself.
 * - CUSTOM means: built-in custom loaders provided by LoadingKit (no external libs).
 */
public enum LoadingType {
    NONE,
    SPINKIT,
    LOTTIE,
    GIF,
    CUSTOM,
    TEXT,
    LOGO_PULSE
}
