package com.aynal.loadingkit.render;

import android.animation.ValueAnimator;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.TextMode;

public final class TextAnimator {

    private static final int TAG_DOTS = 0x7f0a1001;
    private static final int TAG_TYPING = 0x7f0a1002;
    private static final int TAG_SHIMMER = 0x7f0a1003;

    private TextAnimator() {}

    public static void start(TextView tv, LoadingOptions opts) {
        stop(tv);

        String msg = opts.message;
        if (msg == null || msg.trim().isEmpty()) msg = "Loading...";

        tv.setSingleLine(false);
        tv.setMaxLines(Math.max(1, opts.messageMaxLines));
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        if (opts.messageMaxWidthDp > 0) {
            tv.setMaxWidth(dp(tv, opts.messageMaxWidthDp));
        }

        int color = (opts.messageTextColor != 0) ? opts.messageTextColor : opts.accentColor;
        if (color != 0) tv.setTextColor(color);

        TextMode mode = (opts.textMode != null) ? opts.textMode : TextMode.DOTS;

        if (mode == TextMode.DOTS) {
            final String clean = msg.replaceAll("\\.+$", "");
            ValueAnimator a = ValueAnimator.ofInt(0, 3);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setDuration(Math.max(300, opts.textAnimDurationMs));
            a.setInterpolator(new LinearInterpolator());
            a.addUpdateListener(v -> {
                int n = (int) v.getAnimatedValue();
                String dots = n == 0 ? "" : (n == 1 ? "." : (n == 2 ? ".." : "..."));
                tv.setText(clean + dots);
            });
            tv.setTag(TAG_DOTS, a);
            a.start();
            return;
        }

        if (mode == TextMode.TYPING) {
            tv.setEllipsize(null);
            tv.setText("");

            final String text = msg;
            long perChar = Math.max(35, opts.textAnimDurationMs / Math.max(1, text.length()));
            long typeDuration = perChar * text.length();
            long holdDuration = 700;
            long total = typeDuration + holdDuration;

            ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setRepeatMode(ValueAnimator.RESTART);
            a.setDuration(total);
            a.setInterpolator(new LinearInterpolator());
            a.addUpdateListener(v -> {
                float f = (float) v.getAnimatedValue();
                long t = (long) (f * total);

                int count = (t <= typeDuration)
                        ? (int) Math.min(text.length(), t / Math.max(1, perChar))
                        : text.length();

                tv.setText(text.substring(0, Math.min(count, text.length())));
            });

            tv.setTag(TAG_TYPING, a);
            a.start();
            return;
        }

        tv.setText(msg);
        final Matrix m = new Matrix();
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setDuration(Math.max(600, opts.shimmerDurationMs));
        a.setInterpolator(new LinearInterpolator());

        a.addUpdateListener(v -> {
            int w = tv.getWidth();
            if (w <= 0) return;

            int base = tv.getCurrentTextColor();
            int highlight = brighten(base, 0.55f);

            LinearGradient shader = new LinearGradient(
                    -w, 0, 0, 0,
                    new int[]{base, highlight, base},
                    new float[]{0f, 0.5f, 1f},
                    Shader.TileMode.CLAMP
            );

            float p = (float) v.getAnimatedValue();
            float translate = -w + (2f * w * p);
            m.setTranslate(translate, 0);
            shader.setLocalMatrix(m);
            tv.getPaint().setShader(shader);
            tv.invalidate();
        });

        tv.setTag(TAG_SHIMMER, a);
        a.start();
    }

    public static void stop(TextView tv) {
        cancelTag(tv, TAG_DOTS);
        cancelTag(tv, TAG_TYPING);
        cancelTag(tv, TAG_SHIMMER);
        tv.getPaint().setShader(null);
    }

    private static void cancelTag(TextView tv, int key) {
        Object o = tv.getTag(key);
        if (o instanceof ValueAnimator) ((ValueAnimator) o).cancel();
        tv.setTag(key, null);
    }

    private static int brighten(int color, float amount) {
        int a = (color >>> 24) & 0xFF;
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = (color) & 0xFF;

        r = clamp255((int) (r + (255 - r) * amount));
        g = clamp255((int) (g + (255 - g) * amount));
        b = clamp255((int) (b + (255 - b) * amount));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clamp255(int v) { return Math.max(0, Math.min(255, v)); }

    private static int dp(TextView tv, int dp) {
        float d = tv.getResources().getDisplayMetrics().density;
        return (int) (dp * d + 0.5f);
    }
}
