package com.aynal.loadingkit.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.aynal.loadingkit.LoadingOptions;

import java.io.IOException;
import java.io.InputStream;

public class GifRenderer implements LoadingRenderer {

    private View root;
    private ImageView imageView;
    private GifMovieView movieView;
    private boolean failed = false;

    @Override
    public View create(Context context, LoadingOptions opts) {
        failed = false;

        FrameLayout wrap = new FrameLayout(context);
        int px = dp(context, opts.sizeDp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(px, px);

        if (Build.VERSION.SDK_INT >= 28) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);

            boolean ok = loadAnimatedDrawable28(context, opts);
            if (!ok) {
                failed = true;
                imageView.setVisibility(View.GONE);
            }

            wrap.addView(imageView, lp);
            root = wrap;
            return wrap;
        }

        movieView = new GifMovieView(context);
        boolean ok = movieView.load(context, opts);
        if (!ok) {
            failed = true;
            movieView.setVisibility(View.GONE);
        }

        wrap.addView(movieView, lp);
        root = wrap;
        return wrap;
    }

    @Override
    public void bind(LoadingOptions opts) {
    }

    @Override
    public void start() {
        if (failed) return;

        if (Build.VERSION.SDK_INT >= 28 && imageView != null) {
            Drawable d = imageView.getDrawable();
            if (d instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) d).start();
            }
        } else if (movieView != null) {
            movieView.start();
        }
    }

    @Override
    public void stop() {
        if (Build.VERSION.SDK_INT >= 28 && imageView != null) {
            Drawable d = imageView.getDrawable();
            if (d instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) d).stop();
            }
        } else if (movieView != null) {
            movieView.stop();
        }
    }

    public boolean isFailed() {
        return failed;
    }

    private boolean loadAnimatedDrawable28(Context context, LoadingOptions opts) {
        try {
            Drawable drawable = null;

            if (opts.gifRawResId != 0) {
                drawable = android.graphics.ImageDecoder.decodeDrawable(
                        android.graphics.ImageDecoder.createSource(context.getResources(), opts.gifRawResId)
                );
            } else if (opts.gifAssetName != null && !opts.gifAssetName.trim().isEmpty()) {
                drawable = android.graphics.ImageDecoder.decodeDrawable(
                        android.graphics.ImageDecoder.createSource(context.getAssets(), opts.gifAssetName)
                );
            }

            if (!(drawable instanceof AnimatedImageDrawable)) {
                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
                    return true;
                }
                return false;
            }

            AnimatedImageDrawable a = (AnimatedImageDrawable) drawable;
            a.setRepeatCount(opts.gifLoop ? AnimatedImageDrawable.REPEAT_INFINITE : 0);
            imageView.setImageDrawable(a);
            return true;

        } catch (Throwable t) {
            return false;
        }
    }

    private int dp(Context c, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    // ---------- Movie-based view for API < 28 ----------
    private static class GifMovieView extends View {

        private Movie movie;
        private long startMs;
        private boolean running = false;
        private boolean loop = true;
        private int durationMs = 1000;
        public GifMovieView(Context context) {
            super(context);
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        public boolean load(Context context, LoadingOptions opts) {
            loop = opts.gifLoop;

            InputStream is = null;
            try {
                if (opts.gifRawResId != 0) {
                    is = context.getResources().openRawResource(opts.gifRawResId);
                } else if (opts.gifAssetName != null && !opts.gifAssetName.trim().isEmpty()) {
                    is = context.getAssets().open(opts.gifAssetName);
                } else {
                    return false;
                }

                movie = Movie.decodeStream(is);
                if (movie == null) return false;

                int d = movie.duration();
                if (d > 0) durationMs = d;

                return true;

            } catch (Throwable t) {
                return false;
            } finally {
                if (is != null) {
                    try { is.close(); } catch (IOException ignore) {}
                }
            }
        }

        public void start() {
            if (movie == null) return;
            running = true;
            startMs = SystemClock.uptimeMillis();
            invalidate();
        }

        public void stop() {
            running = false;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (movie == null) return;

            if (!running) {
                drawFrame(canvas, 0);
                return;
            }

            long now = SystemClock.uptimeMillis();
            long elapsed = now - startMs;
            int rel = (int) (elapsed % durationMs);

            if (!loop && elapsed >= durationMs) {
                running = false;
                rel = durationMs;
            }

            drawFrame(canvas, rel);

            if (running) invalidate();
        }

        private void drawFrame(Canvas canvas, int timeMs) {
            int vw = getWidth();
            int vh = getHeight();
            int mw = movie.width();
            int mh = movie.height();

            if (mw <= 0 || mh <= 0 || vw <= 0 || vh <= 0) return;

            float sx = vw * 1f / mw;
            float sy = vh * 1f / mh;
            float scale = Math.min(sx, sy);

            float dx = (vw - mw * scale) / 2f;
            float dy = (vh - mh * scale) / 2f;

            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale, scale);

            movie.setTime(Math.max(0, Math.min(timeMs, durationMs)));
            movie.draw(canvas, 0, 0);

            canvas.restore();
        }
    }
}
