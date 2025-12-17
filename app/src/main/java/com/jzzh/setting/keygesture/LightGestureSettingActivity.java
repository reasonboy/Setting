package com.jzzh.setting.keygesture;

import android.os.Bundle;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.utils.UtilSpaceUserSettings;
import com.jzzh.tools.ZhCheckBox;

import java.util.Locale;

public class LightGestureSettingActivity extends BaseActivity {
    private static final String TAG = LightGestureSettingActivity.class.getSimpleName();
    private ZhCheckBox mCheckBox;
    private boolean isUseLightGesture = true;
    boolean isKoreaLocale = true;
    private UtilSpaceUserSettings mUtilSpaceUserSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture_light);
        updateImageByLocale();
        mUtilSpaceUserSettings = new UtilSpaceUserSettings(this);
        mCheckBox = findViewById(R.id.zh_light_gesture_switch);
        mCheckBox.setOnZhCheckedChangeListener(checked -> {
            isUseLightGesture = !isUseLightGesture;
            mCheckBox.setCheck(isUseLightGesture);
            mUtilSpaceUserSettings.setLightGestureEnable(isUseLightGesture ? 1 : 0);
        });

        findViewById(R.id.def_iv).setBackgroundResource(
                isKoreaLocale ? R.drawable.setting_k_g_gesturesetting_lightgestureinfo
                : R.drawable.setting_k_g_gesturesetting_lightgestureinfo_en
        );

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateImageByLocale();
    }

    private void updateImageByLocale() {
        Locale currentLocale = getResources().getConfiguration().locale;
        isKoreaLocale = Locale.KOREAN.toString().equals(currentLocale.toString());
    }

    private void updateUI() {
        isUseLightGesture = mUtilSpaceUserSettings.getLightGestureEnable() == 1;
        mCheckBox.setCheck(isUseLightGesture);
    }
}
