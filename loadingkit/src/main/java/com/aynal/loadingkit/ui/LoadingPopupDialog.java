package com.aynal.loadingkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.LoadingType;
import com.aynal.loadingkit.R;
import com.aynal.loadingkit.render.LoadingRenderer;
import com.aynal.loadingkit.render.RendererFactory;
import com.aynal.loadingkit.render.LottieRenderer;
import com.aynal.loadingkit.render.TextAnimator;

public class LoadingPopupDialog extends Dialog {

    private final LoadingOptions opts;
    private LoadingRenderer renderer;

    public LoadingPopupDialog(@NonNull Context context, LoadingOptions opts) {
        super(context, R.style.LK_PopupDialog);
        this.opts = opts;
        setCancelable(opts.cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lk_popup_dialog);

        LinearLayout content = findViewById(R.id.lkContent);
        FrameLayout host = findViewById(R.id.lkRendererHost);
        TextView msg = findViewById(R.id.lkMessage);

        // ✅ Apply layout customization (size/padding/margins)
        if (content != null) {
            int p = dp(opts.contentPaddingDp);
            content.setPadding(p, p, p, p);
        }

        // loader size (TEXT loader will hide host, but keep size ready for other modes)
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

        // ✅ When type is TEXT and showLoader=true, use lkMessage as the animated loader text.
        boolean isTextLoader = opts.showLoader && opts.type == LoadingType.TEXT;
        if (isTextLoader) {
            // no separate renderer text; only lkMessage
            host.setVisibility(View.GONE);
            host.removeAllViews();

            msg.setVisibility(View.VISIBLE);
            msg.setText(m);
            msg.setTextColor(opts.messageTextColor);

            TextAnimator.start(msg, opts);
            renderer = null;
            return;
        }

        // non-text loaders: stop any text animation
        TextAnimator.stop(msg);

        // ✅ message: visibility only depends on showMessage
        msg.setVisibility(opts.showMessage ? View.VISIBLE : View.GONE);
        msg.setText(m);
        msg.setTextColor(opts.messageTextColor);

        // ✅ loader: visibility depends on showLoader + type != NONE
        boolean shouldShowLoader = opts.showLoader && opts.type != LoadingType.NONE;
        host.setVisibility(shouldShowLoader ? View.VISIBLE : View.GONE);

        if (shouldShowLoader) {
            renderer = RendererFactory.create(getContext(), opts);
            View rv = renderer.create(getContext(), opts);

            // Lottie fail fallback
            if (opts.type == LoadingType.LOTTIE && renderer instanceof LottieRenderer) {
                if (((LottieRenderer) renderer).isFailed()) {
                    LoadingOptions fb = new LoadingOptions()
                            .loader(opts.fallbackType)
                            .showLoader(true)
                            .showMessage(opts.showMessage)
                            .message(m)
                            .accentColor(opts.accentColor)
                            .messageTextColor(opts.messageTextColor)
                            .layoutFrom(opts);

                    renderer = RendererFactory.create(getContext(), fb);
                    rv = renderer.create(getContext(), fb);
                }
            }

            host.removeAllViews();
            host.addView(rv);
            renderer.start();
        } else {
            host.removeAllViews();
            renderer = null;
        }
    }

    @Override
    public void dismiss() {
        TextView msg = findViewById(R.id.lkMessage);
        if (msg != null) TextAnimator.stop(msg);
        if (renderer != null) renderer.stop();
        super.dismiss();
    }

    private int dp(int v) {
        float d = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
