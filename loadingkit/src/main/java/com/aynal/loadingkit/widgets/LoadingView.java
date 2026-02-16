package com.aynal.loadingkit.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aynal.loadingkit.CustomStyle;
import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.LoadingPreset;
import com.aynal.loadingkit.LoadingPresets;
import com.aynal.loadingkit.LoadingType;
import com.aynal.loadingkit.LogoMode;
import com.aynal.loadingkit.R;
import com.aynal.loadingkit.render.LoadingRenderer;
import com.aynal.loadingkit.render.RendererFactory;
import com.aynal.loadingkit.render.TextAnimator;

public class LoadingView extends LinearLayout {

    private FrameLayout host;
    private TextView msg;
    private LinearLayout wrap;

    private LoadingRenderer renderer;
    private LoadingOptions opts = new LoadingOptions();

    public LoadingView(Context context) { super(context); init(context, null); }
    public LoadingView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(context, attrs); }

    private void init(Context c, @Nullable AttributeSet attrs) {
        setOrientation(VERTICAL);
        LayoutInflater.from(c).inflate(R.layout.lk_loading_view, this, true);
        wrap = findViewById(R.id.lkWrap);
        host = findViewById(R.id.lkRendererHost);
        msg  = findViewById(R.id.lkMessage);

        // default brand blue
        opts.accentColor = 0xFF1E88E5;
        opts.messageTextColor = 0xFF1E88E5;

        if (attrs != null) {
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LoadingView);

            // Optional: apply preset first
            if (a.hasValue(R.styleable.LoadingView_lk_preset)) {
                int pv = a.getInt(R.styleable.LoadingView_lk_preset, 0);
                LoadingPreset preset =
                        (pv==1) ? LoadingPreset.MODERN_SPINKIT_FADING_CIRCLE :
                        (pv==2) ? LoadingPreset.MODERN_CUSTOM_RING :
                        (pv==3) ? LoadingPreset.MODERN_CUSTOM_ORBIT :
                        (pv==4) ? LoadingPreset.MODERN_TEXT_DOTS :
                        (pv==5) ? LoadingPreset.MODERN_TEXT_TYPING :
                        (pv==6) ? LoadingPreset.MODERN_LOGO_ROTATE :
                        (pv==7) ? LoadingPreset.MODERN_LOGO_RIPPLE :
                        (pv==8) ? LoadingPreset.MODERN_GIF_DOTS :
                                LoadingPreset.MODERN_SPINKIT_WAVE;
                LoadingPresets.apply(preset, opts);
            }

            // type (can override preset)
            int t = a.getInt(R.styleable.LoadingView_lk_type, 0);
            opts.type = (t==1) ? LoadingType.LOTTIE
                    : (t==2) ? LoadingType.TEXT
                    : (t==3) ? LoadingType.LOGO_PULSE
                    : (t==4) ? LoadingType.GIF
                    : (t==5) ? LoadingType.CUSTOM
                    : LoadingType.SPINKIT;

            String m = a.getString(R.styleable.LoadingView_lk_message);
            if (m != null) opts.message = m;

            // Default OFF: respect your library rule
            if (a.hasValue(R.styleable.LoadingView_lk_showMessage))
                opts.showMessage = a.getBoolean(R.styleable.LoadingView_lk_showMessage, false);
            if (a.hasValue(R.styleable.LoadingView_lk_showLoader))
                opts.showLoader  = a.getBoolean(R.styleable.LoadingView_lk_showLoader, false);

            if (a.hasValue(R.styleable.LoadingView_lk_accentColor)) {
                opts.accentColor = a.getColor(R.styleable.LoadingView_lk_accentColor, 0xFF1E88E5);
                opts.messageTextColor = opts.accentColor;
            }

            if (a.hasValue(R.styleable.LoadingView_lk_sizeDp)) opts.sizeDp = a.getInt(R.styleable.LoadingView_lk_sizeDp, 72);
            if (a.hasValue(R.styleable.LoadingView_lk_contentPaddingDp)) opts.contentPaddingDp = a.getInt(R.styleable.LoadingView_lk_contentPaddingDp, 8);
            if (a.hasValue(R.styleable.LoadingView_lk_gapDp)) opts.gapDp = a.getInt(R.styleable.LoadingView_lk_gapDp, 10);
            if (a.hasValue(R.styleable.LoadingView_lk_messageMaxWidthDp)) opts.messageMaxWidthDp = a.getInt(R.styleable.LoadingView_lk_messageMaxWidthDp, 560);
            if (a.hasValue(R.styleable.LoadingView_lk_messageMaxLines)) opts.messageMaxLines = a.getInt(R.styleable.LoadingView_lk_messageMaxLines, 4);

            if (a.hasValue(R.styleable.LoadingView_lk_lottieAssetName)) opts.lottieAssetName = a.getString(R.styleable.LoadingView_lk_lottieAssetName);
            if (a.hasValue(R.styleable.LoadingView_lk_gifAssetName)) opts.gifAssetName = a.getString(R.styleable.LoadingView_lk_gifAssetName);
            if (a.hasValue(R.styleable.LoadingView_lk_gifLoop)) opts.gifLoop = a.getBoolean(R.styleable.LoadingView_lk_gifLoop, true);

            // custom style
            if (a.hasValue(R.styleable.LoadingView_lk_customStyle)) {
                int cs = a.getInt(R.styleable.LoadingView_lk_customStyle, 0);
                opts.customStyle = (cs==1) ? CustomStyle.BARS_WAVE
                        : (cs==2) ? CustomStyle.DOTS_ORBIT
                        : (cs==3) ? CustomStyle.ARC_SPINNER
                        : CustomStyle.RING_PULSE;
            }

            // logo
            if (a.hasValue(R.styleable.LoadingView_lk_logoRes)) {
                opts.logoResId = a.getResourceId(R.styleable.LoadingView_lk_logoRes, 0);
            }
            if (a.hasValue(R.styleable.LoadingView_lk_logoMode)) {
                int lm = a.getInt(R.styleable.LoadingView_lk_logoMode, 0);
                opts.logoMode = (lm==1) ? LogoMode.ROTATE
                        : (lm==2) ? LogoMode.BOUNCE
                        : (lm==3) ? LogoMode.GLOW
                        : (lm==4) ? LogoMode.RIPPLE
                        : LogoMode.PULSE;
            }

            a.recycle();
        }

        apply();
    }

    private void apply() {
        // layout (padding + size + spacing)
        if (wrap != null) {
            int p = dp(opts.contentPaddingDp);
            wrap.setPadding(p, p, p, p);
        }

        // loader size
        ViewGroup.LayoutParams hlp = host.getLayoutParams();
        if (hlp != null) {
            hlp.width = dp(opts.sizeDp);
            hlp.height = dp(opts.sizeDp);
            host.setLayoutParams(hlp);
        }

        // message spacing + wrap rules
        ViewGroup.LayoutParams mlp0 = msg.getLayoutParams();
        if (mlp0 instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) mlp0).topMargin = dp(opts.gapDp);
            msg.setLayoutParams(mlp0);
        }
        msg.setMaxLines(Math.max(1, opts.messageMaxLines));
        if (opts.messageMaxWidthDp > 0) msg.setMaxWidth(dp(opts.messageMaxWidthDp));

        String m = (opts.message == null || opts.message.trim().isEmpty()) ? "Loading..." : opts.message;

        boolean isTextLoader = opts.showLoader && opts.type == LoadingType.TEXT;
        if (isTextLoader) {
            host.setVisibility(View.GONE);
            host.removeAllViews();
            renderer = null;

            msg.setVisibility(View.VISIBLE);
            msg.setText(m);
            msg.setTextColor(opts.messageTextColor);
            TextAnimator.start(msg, opts);
            return;
        }

        TextAnimator.stop(msg);

        msg.setVisibility(opts.showMessage ? View.VISIBLE : View.GONE);
        msg.setText(m);
        msg.setTextColor(opts.messageTextColor);

        boolean shouldShowLoader = opts.showLoader && opts.type != LoadingType.NONE;
        host.setVisibility(shouldShowLoader ? View.VISIBLE : View.GONE);

        if (shouldShowLoader) {
            renderer = RendererFactory.create(getContext(), opts);
            View rv = renderer.create(getContext(), opts);
            host.removeAllViews();
            host.addView(rv);
        } else {
            host.removeAllViews();
            renderer = null;
        }
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }

    // Public API
    public void setOptions(LoadingOptions options) {
        if (options != null) this.opts = options;
        apply();
    }

    public LoadingOptions getOptions() { return opts; }

    public void start() {
        // TEXT loader starts in apply() (TextAnimator.start)
        if (renderer != null) renderer.start();
    }

    public void stop() {
        TextAnimator.stop(msg);
        if (renderer != null) renderer.stop();
    }
}
