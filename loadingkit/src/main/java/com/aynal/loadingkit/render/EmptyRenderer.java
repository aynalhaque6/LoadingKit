package com.aynal.loadingkit.render;

import android.content.Context;
import android.view.View;

import com.aynal.loadingkit.LoadingOptions;

public class EmptyRenderer implements LoadingRenderer {
    @Override public View create(Context context, LoadingOptions opts) {
        View v = new View(context);
        v.setLayoutParams(new android.view.ViewGroup.LayoutParams(1,1));
        v.setVisibility(View.GONE);
        return v;
    }
    @Override public void bind(LoadingOptions opts) {}
    @Override public void start() {}
    @Override public void stop() {}
}
