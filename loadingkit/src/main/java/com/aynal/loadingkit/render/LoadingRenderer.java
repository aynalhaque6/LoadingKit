package com.aynal.loadingkit.render;

import android.content.Context;
import android.view.View;

import com.aynal.loadingkit.LoadingOptions;

public interface LoadingRenderer {
    View create(Context context, LoadingOptions opts);
    void bind(LoadingOptions opts);
    void start();
    void stop();
}
