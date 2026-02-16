package com.aynal.customloadingkit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aynal.loadingkit.LoadingKit;
import com.aynal.loadingkit.LoadingOptions;
import com.aynal.loadingkit.LoadingPreset;
import com.aynal.loadingkit.LoadingPresets;

public class MainActivity extends AppCompatActivity {

    private Spinner spPreset, spHost;
    private EditText etMessage;
    private Switch swShowLoader, swShowMessage;
    private SeekBar sbSize, sbPadding, sbGap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spPreset = findViewById(R.id.spPreset);
        spHost = findViewById(R.id.spHost);
        etMessage = findViewById(R.id.etMessage);
        swShowLoader = findViewById(R.id.swShowLoader);
        swShowMessage = findViewById(R.id.swShowMessage);
        sbSize = findViewById(R.id.sbSize);
        sbPadding = findViewById(R.id.sbPadding);
        sbGap = findViewById(R.id.sbGap);

        ArrayAdapter<CharSequence> presetAdapter = ArrayAdapter.createFromResource(
                this, R.array.lk_presets, android.R.layout.simple_spinner_item);
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPreset.setAdapter(presetAdapter);

        ArrayAdapter<CharSequence> hostAdapter = ArrayAdapter.createFromResource(
                this, R.array.lk_hosts, android.R.layout.simple_spinner_item);
        hostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHost.setAdapter(hostAdapter);

        Button btnShow = findViewById(R.id.btnShow);
        Button btnHide = findViewById(R.id.btnHide);

        btnShow.setOnClickListener(v -> showSelected());
        btnHide.setOnClickListener(v -> hideAll());
    }

    private void showSelected() {
        LoadingPreset preset = presetFromIndex(spPreset.getSelectedItemPosition());

        LoadingOptions opts = LoadingPresets.apply(preset);

        opts.logoRes(R.mipmap.ic_launcher);
        opts.message(etMessage.getText() != null ? etMessage.getText().toString() : "Loading...");

        opts.showLoader(swShowLoader.isChecked());
        opts.showMessage(swShowMessage.isChecked());

        int size = Math.max(24, sbSize.getProgress());
        int pad = Math.max(0, sbPadding.getProgress());
        int gap = Math.max(0, sbGap.getProgress());

        opts.sizeDp(size);
        opts.contentPaddingDp(pad);
        opts.gapDp(gap);
        opts.cancelable(false);

        int host = spHost.getSelectedItemPosition();
        if (host == 0) {
            LoadingKit.popup().show(this, opts);
        } else if (host == 1) {
            LoadingKit.overlay().show(this, opts);
        } else if (host == 2) {
            LoadingKit.fullscreen().show(this, opts);
        } else {
            LoadingKit.inView().show(this, opts);
        }
    }

    private void hideAll() {
        LoadingKit.popup().hide(this);
        LoadingKit.overlay().hide(this);
        LoadingKit.fullscreen().hide(this);
        LoadingKit.inView().hide(this);
    }

    private LoadingPreset presetFromIndex(int i) {
        switch (i) {
            case 1:
                return LoadingPreset.MODERN_SPINKIT_FADING_CIRCLE;
            case 2:
                return LoadingPreset.MODERN_CUSTOM_RING;
            case 3:
                return LoadingPreset.MODERN_CUSTOM_ORBIT;
            case 4:
                return LoadingPreset.MODERN_TEXT_DOTS;
            case 5:
                return LoadingPreset.MODERN_TEXT_TYPING;
            case 6:
                return LoadingPreset.MODERN_LOGO_ROTATE;
            case 7:
                return LoadingPreset.MODERN_LOGO_RIPPLE;
            case 8:
                return LoadingPreset.MODERN_GIF_DOTS;
            case 0:
            default:
                return LoadingPreset.MODERN_SPINKIT_WAVE;
        }
    }
}
