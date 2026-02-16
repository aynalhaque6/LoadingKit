package com.aynal.loadingkit.render;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.aynal.loadingkit.CustomStyle;
import com.aynal.loadingkit.LoadingOptions;

public class CustomRenderer implements LoadingRenderer {

    private View view;

    @Override
    public View create(Context context, LoadingOptions opts) {
        FrameLayout wrap = new FrameLayout(context);
        int px = dp(context, opts.sizeDp);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(px, px);

        CustomStyle style = (opts.customStyle != null) ? opts.customStyle : CustomStyle.RING_PULSE;

        switch (style) {
            case BARS_WAVE:
                view = new BarsWaveView(context, opts);
                break;
            case DOTS_ORBIT:
                view = new DotsOrbitView(context, opts);
                break;
            case ARC_SPINNER:
                view = new ArcSpinnerView(context, opts);
                break;
            case RING_PULSE:
            default:
                view = new RingPulseView(context, opts);
                break;
        }

        wrap.addView(view, lp);
        return wrap;
    }

    @Override public void bind(LoadingOptions opts) {
        if (view instanceof CustomAnimView) ((CustomAnimView) view).bind(opts);
    }

    @Override public void start() {
        if (view instanceof CustomAnimView) ((CustomAnimView) view).start();
    }

    @Override public void stop() {
        if (view instanceof CustomAnimView) ((CustomAnimView) view).stop();
    }

    private int dp(Context c, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    // ------------------------------------------------------------
    // Base
    // ------------------------------------------------------------
    private interface CustomAnimView {
        void bind(LoadingOptions opts);
        void start();
        void stop();
    }

    private static abstract class BaseAnimView extends View implements CustomAnimView {
        protected final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        protected final Paint p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        protected final RectF r = new RectF();

        protected ValueAnimator anim;
        protected float t = 0f;

        protected int color;

        BaseAnimView(Context c, LoadingOptions opts) {
            super(c);
            bind(opts);
        }

        @Override
        public void bind(LoadingOptions opts) {
            color = (opts.accentColor != 0) ? opts.accentColor : 0xFF1E88E5;
            p.setColor(color);
            p2.setColor(color);
            invalidate();
        }

        @Override
        public void start() {
            stop();
            anim = ValueAnimator.ofFloat(0f, 1f);
            anim.setDuration(900);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.setInterpolator(new LinearInterpolator());
            anim.addUpdateListener(a -> {
                t = (float) a.getAnimatedValue();
                invalidate();
            });
            anim.start();
        }

        @Override
        public void stop() {
            if (anim != null) {
                anim.cancel();
                anim = null;
            }
        }
    }

    // ------------------------------------------------------------
    // RING_PULSE
    // ------------------------------------------------------------
    private static class RingPulseView extends BaseAnimView {
        RingPulseView(Context c, LoadingOptions opts) {
            super(c, opts);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            float cx = w / 2f, cy = h / 2f;
            float min = Math.min(w, h);
            float baseR = min * 0.28f;
            float pulse = (float) (Math.sin(t * Math.PI * 2) * 0.5f + 0.5f); // 0..1
            float r1 = baseR + pulse * (min * 0.12f);
            float r2 = baseR + (1f - pulse) * (min * 0.18f);

            p.setStrokeWidth(min * 0.08f);
            p.setAlpha((int) (180 + 75 * (1f - pulse)));
            canvas.drawCircle(cx, cy, r1, p);

            p.setStrokeWidth(min * 0.04f);
            p.setAlpha((int) (70 + 130 * pulse));
            canvas.drawCircle(cx, cy, r2, p);
        }
    }

    // ------------------------------------------------------------
    // BARS_WAVE
    // ------------------------------------------------------------
    private static class BarsWaveView extends BaseAnimView {
        BarsWaveView(Context c, LoadingOptions opts) {
            super(c, opts);
            p.setStyle(Paint.Style.FILL);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            float barW = w / 9f;
            float gap = barW * 0.5f;
            float startX = (w - (5 * barW + 4 * gap)) / 2f;
            float baseY = h * 0.75f;
            for (int i = 0; i < 5; i++) {
                float phase = (t + i * 0.12f) % 1f;
                float s = (float) (Math.sin(phase * Math.PI * 2) * 0.5f + 0.5f); // 0..1
                float barH = h * (0.18f + 0.35f * s);
                float x = startX + i * (barW + gap);
                float y = baseY - barH;
                p.setAlpha((int) (120 + 135 * s));
                canvas.drawRoundRect(x, y, x + barW, baseY, barW/2f, barW/2f, p);
            }
        }
    }

    // ------------------------------------------------------------
    // DOTS_ORBIT
    // ------------------------------------------------------------
    private static class DotsOrbitView extends BaseAnimView {
        DotsOrbitView(Context c, LoadingOptions opts) {
            super(c, opts);
            p.setStyle(Paint.Style.FILL);
            p2.setStyle(Paint.Style.FILL);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            float cx = w/2f, cy = h/2f;
            float min = Math.min(w, h);
            float orbit = min * 0.28f;
            float dot = min * 0.07f;

            p2.setAlpha(70);
            canvas.drawCircle(cx, cy, dot * 0.7f, p2);

            for (int i=0;i<3;i++){
                float phase = (t + i*0.18f) % 1f;
                float ang = (float) (phase * Math.PI * 2);
                float x = (float) (cx + Math.cos(ang) * orbit);
                float y = (float) (cy + Math.sin(ang) * orbit);
                float s = (float) (Math.sin(phase * Math.PI) * 0.5f + 0.5f);
                p.setAlpha((int)(90 + 165*s));
                canvas.drawCircle(x, y, dot*(0.75f+0.35f*s), p);
            }
        }
    }

    // ------------------------------------------------------------
    // ARC_SPINNER
    // ------------------------------------------------------------
    private static class ArcSpinnerView extends BaseAnimView {
        ArcSpinnerView(Context c, LoadingOptions opts) {
            super(c, opts);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            float min = Math.min(w,h);
            float stroke = min * 0.08f;
            p.setStrokeWidth(stroke);

            float pad = stroke;
            r.set(pad, pad, w - pad, h - pad);

            float start = 360f * t;
            float sweep = 80f + 220f * (float)(Math.sin(t * Math.PI * 2)*0.5f+0.5f);
            p.setAlpha(255);
            canvas.drawArc(r, start, sweep, false, p);
        }
    }
}
