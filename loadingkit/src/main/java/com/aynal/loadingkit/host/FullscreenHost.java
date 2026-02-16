package com.aynal.loadingkit.host;

import android.app.Activity;
import android.view.ViewGroup;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.ui.FullscreenOverlayView;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class FullscreenHost {

    private final WeakHashMap<Activity, WeakReference<FullscreenOverlayView>> map = new WeakHashMap<>();

    public void show(Activity activity, LoadingOptions opts) {
        if (activity == null || activity.isFinishing()) return;

        hide(activity);

        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        FullscreenOverlayView v = new FullscreenOverlayView(activity);
        v.bind(opts);
        v.setAlpha(0f);
        root.addView(v, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        v.animate().alpha(1f).setDuration(180).start();

        map.put(activity, new WeakReference<>(v));
        v.start();
    }

    public void hide(Activity activity) {
        WeakReference<FullscreenOverlayView> ref = map.get(activity);
        if (ref != null) {
            FullscreenOverlayView v = ref.get();
            if (v != null) {
                v.animate().alpha(0f).setDuration(160).withEndAction(() -> {
                    v.stop();
                    ViewGroup parent = (ViewGroup) v.getParent();
                    if (parent != null) parent.removeView(v);
                }).start();
            }
        }
        map.remove(activity);
    }
}
