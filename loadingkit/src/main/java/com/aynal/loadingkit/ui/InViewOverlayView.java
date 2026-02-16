package com.aynal.loadingkit.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.LoadingType;
import com.aynal.loadingkit.render.LoadingRenderer;
import com.aynal.loadingkit.render.RendererFactory;
import com.aynal.loadingkit.render.LottieRenderer;
import com.aynal.loadingkit.render.TextAnimator;

public class InViewOverlayView extends FrameLayout {

    private final LinearLayout wrap;
    private final FrameLayout host;
    private final TextView msg;
    private final LinearLayout.LayoutParams hostLp;
    private final LinearLayout.LayoutParams msgLp;

    private LoadingRenderer renderer;

    public InViewOverlayView(Context context) {
        super(context);

        wrap = new LinearLayout(context);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setGravity(Gravity.CENTER);

        LayoutParams wlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;

        host = new FrameLayout(context);
        msg = new TextView(context);

        msg.setGravity(Gravity.CENTER);
        msg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        msg.setSingleLine(false);
        msg.setMaxLines(4);
        msg.setEllipsize(TextUtils.TruncateAt.END);
        hostLp = new LinearLayout.LayoutParams(dp(72), dp(72));
        wrap.addView(host, hostLp);

        msgLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        msgLp.topMargin = dp(8);
        wrap.addView(msg, msgLp);

        addView(wrap, wlp);
    }

    public void bind(LoadingOptions opts) {
        setBackgroundColor(0x00000000);
        setClickable(opts.blockTouches);

        // layout customization
        int p = dp(opts.contentPaddingDp);
        wrap.setPadding(p, p, p, p);
        msgLp.topMargin = dp(opts.gapDp);
        msg.setMaxLines(Math.max(1, opts.messageMaxLines));
        if (opts.messageMaxWidthDp > 0) msg.setMaxWidth(dp(opts.messageMaxWidthDp));

        String m = (opts.message == null || opts.message.trim().isEmpty()) ? "Loading..." : opts.message;

        boolean isTextLoader = opts.showLoader && opts.type == LoadingType.TEXT;
        if (isTextLoader) {
            stop();
            host.setVisibility(View.GONE);
            host.removeAllViews();

            msg.setVisibility(View.VISIBLE);
            msg.setText(m);
            msg.setTextColor(opts.messageTextColor);

            TextAnimator.start(msg, opts);
            renderer = null;
            return;
        }

        TextAnimator.stop(msg);

        msg.setVisibility(opts.showMessage ? View.VISIBLE : View.GONE);
        msg.setText(m);
        msg.setTextColor(opts.messageTextColor);

        int px = dp(opts.sizeDp);
        hostLp.width = px;
        hostLp.height = px;
        host.setLayoutParams(hostLp);

        boolean shouldShowLoader = opts.showLoader && opts.type != LoadingType.NONE;
        host.setVisibility(shouldShowLoader ? View.VISIBLE : View.GONE);

        if (shouldShowLoader) {
            renderer = RendererFactory.create(getContext(), opts);
            View rv = renderer.create(getContext(), opts);

            if (opts.type == LoadingType.LOTTIE && renderer instanceof LottieRenderer) {
                if (((LottieRenderer) renderer).isFailed()) {
                    LoadingOptions fb = new LoadingOptions()
                            .loader(opts.fallbackType)
                            .showLoader(true)
                            .showMessage(opts.showMessage)
                            .message(m)
                            .accentColor(opts.accentColor)
                            .messageTextColor(opts.messageTextColor)
                            .layoutFrom(opts)
                            .blockTouches(opts.blockTouches);

                    renderer = RendererFactory.create(getContext(), fb);
                    rv = renderer.create(getContext(), fb);
                }
            }

            host.removeAllViews();
            host.addView(rv);
        } else {
            host.removeAllViews();
            renderer = null;
        }
    }

    public void start() { if (renderer != null) renderer.start(); }

    public void stop()  {
        TextAnimator.stop(msg);
        if (renderer != null) renderer.stop();
    }

    private int dp(int dp) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (dp * d + 0.5f);
    }
}
