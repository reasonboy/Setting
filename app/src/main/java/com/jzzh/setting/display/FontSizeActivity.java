package com.jzzh.setting.display;

import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhSeekBar;

public class FontSizeActivity extends BaseActivity {

    private static final float[] FONT_SCALES = {0.85f, 1.0f, 1.15f, 1.30f};
    private static final int DEFAULT_FONT_SIZE = 1;

    private TextView mPreview;
    private ZhSeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_font_size);

        mPreview = findViewById(R.id.font_size_preview);
        mSeekBar = findViewById(R.id.font_size_seekbar);
        mSeekBar.setOnZhSeekBarChangeListener(new ZhSeekBar.OnZhSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int value) {
                updatePreview(value);
            }

            @Override
            public void onStopTrackingTouch(int value) {
                setFontSize(value);
            }
        });

        findViewById(R.id.font_size_reset).setOnClickListener(view -> setFontSize(DEFAULT_FONT_SIZE));
        findViewById(R.id.font_size_decrease).setOnClickListener(view -> adjustFontSize(-1));
        findViewById(R.id.font_size_increase).setOnClickListener(view -> adjustFontSize(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        int position = getFontSizePosition();
        mSeekBar.setValue(position);
        updatePreview(position);
    }

    private int getFontSizePosition() {
        float scale = Settings.System.getFloat(getContentResolver(), Settings.System.FONT_SCALE,
                FONT_SCALES[DEFAULT_FONT_SIZE]);
        int position = 0;
        for (int i = 1; i < FONT_SCALES.length; i++) {
            if (Math.abs(FONT_SCALES[i] - scale) < Math.abs(FONT_SCALES[position] - scale)) {
                position = i;
            }
        }
        return position;
    }

    private void adjustFontSize(int step) {
        int position = mSeekBar.getValue() + step;
        if (position >= 0 && position < FONT_SCALES.length) {
            setFontSize(position);
        }
    }

    private void setFontSize(int position) {
        Settings.System.putFloat(getContentResolver(), Settings.System.FONT_SCALE,
                FONT_SCALES[position]);
        mSeekBar.setValue(position);
        updatePreview(position);
    }

    private void updatePreview(int position) {
        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.fontScale = FONT_SCALES[position];
        float textSize = createConfigurationContext(configuration).getResources()
                .getDimension(R.dimen.textSize20);
        mPreview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
