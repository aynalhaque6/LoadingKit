package com.aynal.loadingkit.render;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.aynal.loadingkit.LoadingOptions;

public class LottieRenderer implements LoadingRenderer {

    private LottieAnimationView view;
    private boolean failed = false;

    public boolean isFailed() { return failed; }

    @Override
    public View create(Context context, LoadingOptions opts) {
        view = new LottieAnimationView(context);

        view.setRepeatCount(opts.lottieLoop ? Integer.MAX_VALUE : 0);
        view.setRepeatMode(LottieDrawable.RESTART);

        view.setFailureListener(result -> {
            failed = true;
            view.cancelAnimation();
            view.setVisibility(View.GONE);
        });

        if (opts.lottieAssetName != null && !opts.lottieAssetName.trim().isEmpty()) {
            view.setAnimation(opts.lottieAssetName);
        } else {
            failed = true;
            view.setVisibility(View.GONE);
        }

        FrameLayout wrap = new FrameLayout(context);
        int px = dp(context, opts.sizeDp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(px, px);
        wrap.addView(view, lp);

        return wrap;
    }

    @Override public void bind(LoadingOptions opts) {}

    @Override public void start() { if (view != null && !failed) view.playAnimation(); }

    @Override public void stop() { if (view != null) view.cancelAnimation(); }

    private int dp(Context c, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }
}
