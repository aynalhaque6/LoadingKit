package com.aynal.loadingkit.render;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.TextMode;

public class TextRenderer implements LoadingRenderer {

    private TextView tv;

    private ValueAnimator dotsAnimator;
    private ValueAnimator typingAnimator;
    private ValueAnimator shimmerAnimator;

    private LinearGradient shimmerShader;
    private Matrix shimmerMatrix;

    private String baseText = "Loading...";

    @Override
    public View create(Context context, LoadingOptions opts) {
        tv = new TextView(context);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        bind(opts);
        setupAnimators(opts);

        return tv;
    }

    @Override
    public void bind(LoadingOptions opts) {
        if (tv == null) return;

        String msg = opts.message;
        if (msg == null || msg.trim().isEmpty()) msg = "Loading...";
        baseText = msg;

        int color = (opts.messageTextColor != 0) ? opts.messageTextColor : opts.accentColor;
        if (color != 0) tv.setTextColor(color);

        tv.setSingleLine(false);
        tv.setMaxLines(3);
        tv.setEllipsize(TextUtils.TruncateAt.END);

        TextMode mode = (opts.textMode != null) ? opts.textMode : TextMode.DOTS;
        if (mode == TextMode.TYPING) {
            tv.setEllipsize(null);
            tv.setText("");
        } else if (mode == TextMode.DOTS) {
            tv.setSingleLine(true);
            tv.setMaxLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setText(stripTrailingDots(baseText));
        } else {
            tv.setText(baseText);
        }
    }

    @Override
    public void start() {
        if (tv == null) return;
        stop();

        if (dotsAnimator != null) dotsAnimator.start();
        if (typingAnimator != null) typingAnimator.start();
        if (shimmerAnimator != null) shimmerAnimator.start();
    }

    @Override
    public void stop() {
        if (dotsAnimator != null) dotsAnimator.cancel();
        if (typingAnimator != null) typingAnimator.cancel();
        if (shimmerAnimator != null) shimmerAnimator.cancel();

        if (tv != null) tv.getPaint().setShader(null);
    }

    private void setupAnimators(LoadingOptions opts) {
        dotsAnimator = null;
        typingAnimator = null;
        shimmerAnimator = null;

        TextMode mode = (opts.textMode != null) ? opts.textMode : TextMode.DOTS;

        if (mode == TextMode.DOTS) setupDots(opts);
        else if (mode == TextMode.TYPING) setupTyping(opts);
        else setupShimmer(opts);
    }

    private void setupDots(LoadingOptions opts) {
        final String clean = stripTrailingDots(baseText);

        dotsAnimator = ValueAnimator.ofInt(0, 3);
        dotsAnimator.setRepeatCount(ValueAnimator.INFINITE);
        dotsAnimator.setDuration(Math.max(300, opts.textAnimDurationMs));
        dotsAnimator.setInterpolator(new LinearInterpolator());
        dotsAnimator.addUpdateListener(a -> {
            int v = (int) a.getAnimatedValue();
            String dots = v == 0 ? "" : (v == 1 ? "." : (v == 2 ? ".." : "..."));
            if (tv != null) tv.setText(clean + dots);
        });
    }

    private void setupTyping(LoadingOptions opts) {
        final String text = baseText;

        long perChar = Math.max(35, opts.textAnimDurationMs / Math.max(1, text.length()));
        long typeDuration = perChar * text.length();
        long holdDuration = 700;
        long total = typeDuration + holdDuration;

        typingAnimator = ValueAnimator.ofFloat(0f, 1f);
        typingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        typingAnimator.setRepeatMode(ValueAnimator.RESTART);
        typingAnimator.setDuration(total);
        typingAnimator.setInterpolator(new LinearInterpolator());

        typingAnimator.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            long t = (long) (f * total);

            int count;
            if (t <= typeDuration) {
                count = (int) Math.min(text.length(), t / Math.max(1, perChar));
            } else {
                count = text.length();
            }

            if (tv != null) tv.setText(text.substring(0, Math.min(count, text.length())));
        });
    }

    private void setupShimmer(LoadingOptions opts) {
        shimmerMatrix = new Matrix();

        shimmerAnimator = ValueAnimator.ofFloat(0f, 1f);
        shimmerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        shimmerAnimator.setInterpolator(new LinearInterpolator());
        shimmerAnimator.setDuration(Math.max(600, opts.shimmerDurationMs));

        shimmerAnimator.addUpdateListener(a -> {
            float p = (float) a.getAnimatedValue();
            if (tv == null) return;

            int w = tv.getWidth();
            if (w <= 0) return;

            ensureShader(w);
            float translate = -w + (2f * w * p);
            shimmerMatrix.setTranslate(translate, 0);
            shimmerShader.setLocalMatrix(shimmerMatrix);
            tv.invalidate();
        });

        tv.post(() -> {
            if (tv == null) return;
            int w = tv.getWidth();
            if (w > 0) ensureShader(w);
        });
    }

    private void ensureShader(int width) {
        if (tv == null) return;

        int base = tv.getCurrentTextColor();
        int highlight = brighten(base, 0.55f);

        shimmerShader = new LinearGradient(
                -width, 0, 0, 0,
                new int[]{base, highlight, base},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
        );
        tv.getPaint().setShader(shimmerShader);
    }

    private int brighten(int color, float amount) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = (color) & 0xFF;

        r = clamp255((int) (r + (255 - r) * amount));
        g = clamp255((int) (g + (255 - g) * amount));
        b = clamp255((int) (b + (255 - b) * amount));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int clamp255(int v) { return Math.max(0, Math.min(255, v)); }

    private String stripTrailingDots(String s) {
        if (s == null) return "Loading";
        return s.replaceAll("\\.+$", "");
    }
}
