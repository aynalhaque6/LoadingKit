package com.aynal.loadingkit.render;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.SpinKitStyle;
import com.github.ybq.android.spinkit.SpinKitView;

// styles
import com.github.ybq.android.spinkit.style.Wave;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.WanderingCubes;

public class SpinKitRenderer implements LoadingRenderer {

    private SpinKitView view;

    @Override
    public View create(Context context, LoadingOptions opts) {
        view = new SpinKitView(context);
        view.setIndeterminateDrawable(toDrawable(opts.spinkitStyle));
        bind(opts);

        FrameLayout wrap = new FrameLayout(context);
        int px = dp(context, opts.sizeDp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(px, px);
        wrap.addView(view, lp);
        return wrap;
    }

    @Override
    public void bind(LoadingOptions opts) {
        if (view == null) return;

        if (opts.accentColor != 0) view.setColor(opts.accentColor);
        if (opts.spinkitStyle != null) view.setIndeterminateDrawable(toDrawable(opts.spinkitStyle));
    }

    @Override public void start() {}
    @Override public void stop() {}

    private Drawable toDrawable(SpinKitStyle style) {
        if (style == null) return new Wave();

        switch (style) {
            case THREE_BOUNCE: return new ThreeBounce();
            case CHASING_DOTS: return new ChasingDots();
            case CIRCLE: return new Circle();
            case DOUBLE_BOUNCE: return new DoubleBounce();
            case FADING_CIRCLE: return new FadingCircle();
            case FOLDING_CUBE: return new FoldingCube();
            case ROTATING_PLANE: return new RotatingPlane();
            case PULSE: return new Pulse();
            case WANDERING_CUBES: return new WanderingCubes();
            case WAVE:
            default:
                return new Wave();
        }
    }

    private int dp(Context c, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }
}
