package com.aynal.loadingkit.host;

import android.app.Activity;

import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.ui.LoadingPopupDialog;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class PopupHost {

    private final WeakHashMap<Activity, WeakReference<LoadingPopupDialog>> map = new WeakHashMap<>();

    public void show(Activity activity, LoadingOptions opts) {
        if (activity == null || activity.isFinishing()) return;

        hide(activity);

        LoadingPopupDialog dialog = new LoadingPopupDialog(activity, opts);
        map.put(activity, new WeakReference<>(dialog));
        dialog.show();
    }

    public void hide(Activity activity) {
        WeakReference<LoadingPopupDialog> ref = map.get(activity);
        if (ref != null) {
            LoadingPopupDialog d = ref.get();
            if (d != null && d.isShowing()) d.dismiss();
        }
        map.remove(activity);
    }
}
