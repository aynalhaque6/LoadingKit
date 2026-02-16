package com.aynal.loadingkit.render;

import android.content.Context;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.LoadingType;

public final class RendererFactory {
    private RendererFactory() {}

    public static LoadingRenderer create(Context context, LoadingOptions opts) {
        LoadingType t = opts.type;

        if (t == LoadingType.NONE) return new EmptyRenderer();
        if (t == LoadingType.LOTTIE) return new LottieRenderer();
        if (t == LoadingType.GIF) return new GifRenderer();
        if (t == LoadingType.CUSTOM) return new CustomRenderer();
        if (t == LoadingType.TEXT) return new TextRenderer();
        if (t == LoadingType.LOGO_PULSE) return new LogoPulseRenderer();
        return new SpinKitRenderer();
    }

}
