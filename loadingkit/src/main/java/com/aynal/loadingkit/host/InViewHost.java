package com.aynal.loadingkit.host;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.ui.InViewOverlayView;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class InViewHost {

    private final WeakHashMap<View, WeakReference<InViewOverlayView>> map = new WeakHashMap<>();

    // Preferred API
    public void attach(ViewGroup container, LoadingOptions opts) {
        if (container == null) return;

        detach(container);

        InViewOverlayView v = new InViewOverlayView(container.getContext());
        v.bind(opts);
        v.setAlpha(0f);

        container.addView(v, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        v.animate().alpha(1f).setDuration(180).start();

        map.put(container, new WeakReference<>(v));
        v.start();
    }

    public void detach(ViewGroup container) {
        if (container == null) return;
        WeakReference<InViewOverlayView> ref = map.get(container);
        if (ref != null) {
            InViewOverlayView v = ref.get();
            if (v != null) {
                v.animate().alpha(0f).setDuration(160).withEndAction(() -> {
                    v.stop();
                    ViewGroup parent = (ViewGroup) v.getParent();
                    if (parent != null) parent.removeView(v);
                }).start();
            }
        }
        map.remove(container);
    }

    public void show(ViewGroup container, LoadingOptions opts) { attach(container, opts); }
    public void hide(ViewGroup container) { detach(container); }

    public void show(Activity activity, LoadingOptions opts) {
        if (activity == null) return;
        ViewGroup root = activity.findViewById(android.R.id.content);
        attach(root, opts);
    }

    public void hide(Activity activity) {
        if (activity == null) return;
        ViewGroup root = activity.findViewById(android.R.id.content);
        detach(root);
    }
}
