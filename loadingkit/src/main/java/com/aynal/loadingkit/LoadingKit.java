package com.aynal.loadingkit;

import com.aynal.loadingkit.host.FullscreenHost;
import com.aynal.loadingkit.host.InViewHost;
import com.aynal.loadingkit.host.OverlayHost;
import com.aynal.loadingkit.host.PopupHost;

public final class LoadingKit {

    private static final PopupHost POPUP = new PopupHost();
    private static final OverlayHost OVERLAY = new OverlayHost();
    private static final FullscreenHost FULLSCREEN = new FullscreenHost();
    private static final InViewHost IN_VIEW = new InViewHost();

    private LoadingKit() {}

    public static OverlayHost overlay() { return OVERLAY; }
    public static FullscreenHost fullscreen() { return FULLSCREEN; }
    public static InViewHost inView() { return IN_VIEW; }

    public static PopupHost popup() { return POPUP; }

}
