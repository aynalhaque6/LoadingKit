package com.aynal.loadingkit.render;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aynal.loadingkit.LoadingOptions;

public class TextDotsRenderer implements LoadingRenderer {

    private TextView tv;
    private ValueAnimator animator;
    private String base = "Loading";

    @Override
    public View create(Context context, LoadingOptions opts) {
        tv = new TextView(context);
        tv.setTextSize(18);
        tv.setText(base);
        bind(opts);

        animator = ValueAnimator.ofInt(0, 3);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(600);
        animator.addUpdateListener(a -> {
            int v = (int) a.getAnimatedValue();
            String dots = v == 0 ? "" : (v == 1 ? "." : (v == 2 ? ".." : "..."));
            tv.setText(base + dots);
        });
        return tv;
    }

    @Override
    public void bind(LoadingOptions opts) {
        if (tv != null) {
            if (opts.accentColor != 0) tv.setTextColor(opts.accentColor);
            if (opts.message != null && !opts.message.trim().isEmpty()) base = opts.message;
        }
    }

    @Override public void start() { if (animator != null) animator.start(); }
    @Override public void stop()  { if (animator != null) animator.cancel(); }
}
