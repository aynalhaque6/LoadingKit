package com.aynal.loadingkit.render;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.LogoMode;

public class LogoPulseRenderer implements LoadingRenderer {

    private FrameLayout wrap;
    private View main;
    private View ripple1;
    private View ripple2;
    private AnimatorSet set;

    @Override
    public View create(Context context, LoadingOptions opts) {
        wrap = new FrameLayout(context);

        int px = dp(context, opts.sizeDp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(px, px);

        main = createMainView(context, opts.logoResId, opts.accentColor);
        wrap.addView(main, lp);

        applyMode(context, opts);

        return wrap;
    }

    private View createMainView(Context context, @DrawableRes int resId, int accentColor) {
        if (resId != 0) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(resId);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return iv;
        }

        View v = new View(context);
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(accentColor != 0 ? accentColor : 0xFF1E88E5);
        v.setBackground(d);
        return v;
    }

    private View createRipple(Context context, int accentColor) {
        View v = new View(context);
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(accentColor != 0 ? accentColor : 0xFF1E88E5);
        d.setAlpha(60);
        v.setBackground(d);
        return v;
    }

    private void applyMode(Context context, LoadingOptions opts) {
        if (set != null) set.cancel();
        set = new AnimatorSet();

        LogoMode mode = (opts.logoMode != null) ? opts.logoMode : LogoMode.PULSE;

        if (ripple1 != null) wrap.removeView(ripple1);
        if (ripple2 != null) wrap.removeView(ripple2);
        ripple1 = null; ripple2 = null;

        switch (mode) {
            case ROTATE: {
                ObjectAnimator rot = ObjectAnimator.ofFloat(main, View.ROTATION, 0f, 360f);
                rot.setDuration(1100);
                rot.setInterpolator(new LinearInterpolator());
                rot.setRepeatCount(ObjectAnimator.INFINITE);

                ObjectAnimator a = ObjectAnimator.ofFloat(main, View.ALPHA, 0.85f, 1f, 0.85f);
                a.setDuration(900);
                a.setRepeatCount(ObjectAnimator.INFINITE);
                a.setInterpolator(new AccelerateDecelerateInterpolator());

                set.playTogether(rot, a);
                break;
            }

            case BOUNCE: {
                ObjectAnimator ty = ObjectAnimator.ofFloat(main, View.TRANSLATION_Y, 0f, -dp(context, 6), 0f);
                ty.setDuration(650);
                ty.setRepeatCount(ObjectAnimator.INFINITE);
                ty.setInterpolator(new AccelerateDecelerateInterpolator());

                ObjectAnimator sx = ObjectAnimator.ofFloat(main, View.SCALE_X, 0.92f, 1.06f, 0.92f);
                ObjectAnimator sy = ObjectAnimator.ofFloat(main, View.SCALE_Y, 0.92f, 1.06f, 0.92f);
                sx.setDuration(650);
                sy.setDuration(650);
                sx.setRepeatCount(ObjectAnimator.INFINITE);
                sy.setRepeatCount(ObjectAnimator.INFINITE);
                sx.setInterpolator(new AccelerateDecelerateInterpolator());
                sy.setInterpolator(new AccelerateDecelerateInterpolator());

                set.playTogether(ty, sx, sy);
                break;
            }

            case GLOW: {
                ObjectAnimator alpha = ObjectAnimator.ofFloat(main, View.ALPHA, 0.6f, 1f, 0.6f);
                alpha.setDuration(800);
                alpha.setRepeatCount(ObjectAnimator.INFINITE);
                alpha.setInterpolator(new AccelerateDecelerateInterpolator());

                ObjectAnimator sx = ObjectAnimator.ofFloat(main, View.SCALE_X, 0.9f, 1.08f, 0.9f);
                ObjectAnimator sy = ObjectAnimator.ofFloat(main, View.SCALE_Y, 0.9f, 1.08f, 0.9f);
                sx.setDuration(800);
                sy.setDuration(800);
                sx.setRepeatCount(ObjectAnimator.INFINITE);
                sy.setRepeatCount(ObjectAnimator.INFINITE);
                sx.setInterpolator(new AccelerateDecelerateInterpolator());
                sy.setInterpolator(new AccelerateDecelerateInterpolator());

                set.playTogether(alpha, sx, sy);
                break;
            }

            case RIPPLE: {
                ripple1 = createRipple(context, opts.accentColor);
                ripple2 = createRipple(context, opts.accentColor);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(context, opts.sizeDp), dp(context, opts.sizeDp));
                wrap.addView(ripple1, 0, lp);
                wrap.addView(ripple2, 0, lp);

                ObjectAnimator r1sx = ObjectAnimator.ofFloat(ripple1, View.SCALE_X, 0.9f, 1.55f);
                ObjectAnimator r1sy = ObjectAnimator.ofFloat(ripple1, View.SCALE_Y, 0.9f, 1.55f);
                ObjectAnimator r1a  = ObjectAnimator.ofFloat(ripple1, View.ALPHA, 0.35f, 0f);

                ObjectAnimator r2sx = ObjectAnimator.ofFloat(ripple2, View.SCALE_X, 0.9f, 1.55f);
                ObjectAnimator r2sy = ObjectAnimator.ofFloat(ripple2, View.SCALE_Y, 0.9f, 1.55f);
                ObjectAnimator r2a  = ObjectAnimator.ofFloat(ripple2, View.ALPHA, 0.35f, 0f);

                for (Animator a : new Animator[]{r1sx, r1sy, r1a, r2sx, r2sy, r2a}) {
                    ((ObjectAnimator) a).setRepeatCount(ObjectAnimator.INFINITE);
                    a.setInterpolator(new LinearInterpolator());
                    a.setDuration(950);
                }
                r2sx.setStartDelay(450);
                r2sy.setStartDelay(450);
                r2a.setStartDelay(450);

                ObjectAnimator mainPulseX = ObjectAnimator.ofFloat(main, View.SCALE_X, 0.92f, 1.02f, 0.92f);
                ObjectAnimator mainPulseY = ObjectAnimator.ofFloat(main, View.SCALE_Y, 0.92f, 1.02f, 0.92f);
                mainPulseX.setDuration(950);
                mainPulseY.setDuration(950);
                mainPulseX.setRepeatCount(ObjectAnimator.INFINITE);
                mainPulseY.setRepeatCount(ObjectAnimator.INFINITE);
                mainPulseX.setInterpolator(new AccelerateDecelerateInterpolator());
                mainPulseY.setInterpolator(new AccelerateDecelerateInterpolator());

                set.playTogether(r1sx, r1sy, r1a, r2sx, r2sy, r2a, mainPulseX, mainPulseY);
                break;
            }

            case PULSE:
            default: {
                ObjectAnimator sx = ObjectAnimator.ofFloat(main, View.SCALE_X, 0.88f, 1.06f, 0.88f);
                ObjectAnimator sy = ObjectAnimator.ofFloat(main, View.SCALE_Y, 0.88f, 1.06f, 0.88f);
                sx.setDuration(850);
                sy.setDuration(850);
                sx.setRepeatCount(ObjectAnimator.INFINITE);
                sy.setRepeatCount(ObjectAnimator.INFINITE);
                sx.setInterpolator(new AccelerateDecelerateInterpolator());
                sy.setInterpolator(new AccelerateDecelerateInterpolator());

                set.playTogether(sx, sy);
                break;
            }
        }
    }

    @Override
    public void bind(LoadingOptions opts) {
        if (wrap != null && main != null) {
            applyMode(wrap.getContext(), opts);
        }
    }

    @Override public void start() { if (set != null) set.start(); }
    @Override public void stop()  { if (set != null) set.cancel(); }

    private int dp(Context c, int dp) {
        float density = c.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
