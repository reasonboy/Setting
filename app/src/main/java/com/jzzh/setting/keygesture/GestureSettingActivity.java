package com.jzzh.setting.keygesture;

import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.utils.UtilSpaceUserSettings;
import com.jzzh.tools.ZhCheckBox;

import java.util.Locale;

public class GestureSettingActivity extends BaseActivity {
    private static final String TAG = GestureSettingActivity.class.getSimpleName();
    private ZhCheckBox BtnKeyPackOnOff, BtnBottomGestureOnOff, BtnLightGestureOnOff;
    private ImageView mInfoKeyPack, mInfoLightGesture, mInfoBottomGesture;

    private boolean isUseKeypack = true;
    private boolean isUseLightGesture = true;
    private boolean isUseBottomGesture = true;

    boolean isKoreaLocale = true;

    private UtilSpaceUserSettings mUtilSpaceUserSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_setting);

        mUtilSpaceUserSettings = new UtilSpaceUserSettings(this);

        mInfoKeyPack = findViewById(R.id.btn_key_pack_gesture_info);
        mInfoKeyPack.setOnClickListener(v -> {
            makeInfoDialog(R.string.title_key_pack_gesture_enable, -1,
                    new int[] {isKoreaLocale ? R.drawable.setting_k_g_gesturesetting_keypackinfo
                            : R.drawable.setting_k_g_gesturesetting_keypackinfo_en},
                    false);
        });
        BtnKeyPackOnOff = findViewById(R.id.btn_key_pack_on_off);
        BtnKeyPackOnOff.setOnZhCheckedChangeListener(checked -> {
            Log.d(TAG, "checked:" + checked);
            isUseKeypack = !isUseKeypack;
            BtnKeyPackOnOff.setCheck(isUseKeypack);
            mUtilSpaceUserSettings.setKeyPackGestureEnable(isUseKeypack ? 1 : 0);
        });

        mInfoBottomGesture = findViewById(R.id.btn_bottom_gesture_info);
        mInfoBottomGesture.setOnClickListener(v -> {
            makeInfoDialog(R.string.title_bottom_gesture_enable, -1,
                    new int[] {R.drawable.setting_k_g_gesturesetting_bottomgestureinfo},
                    false);
        });
        BtnBottomGestureOnOff = findViewById(R.id.btn_bottom_gesture_on_off);
        BtnBottomGestureOnOff.setOnZhCheckedChangeListener(checked -> {
            Log.d(TAG, "checked:" + checked);
            isUseBottomGesture = !isUseBottomGesture;
            BtnBottomGestureOnOff.setCheck(isUseBottomGesture);
            mUtilSpaceUserSettings.setBottomGestureEnable(isUseBottomGesture ? 1 : 0);
        });

        mInfoLightGesture = findViewById(R.id.btn_light_gesture_info);
        mInfoLightGesture.setOnClickListener(v -> {
            makeInfoDialog(R.string.title_light_gesture_enable, -1,
                    new int[] {isKoreaLocale ? R.drawable.setting_k_g_gesturesetting_lightgestureinfo
                            : R.drawable.setting_k_g_gesturesetting_lightgestureinfo_en},
                    true);
        });
        BtnLightGestureOnOff = findViewById(R.id.btn_light_gesture_on_off);
        BtnLightGestureOnOff.setOnZhCheckedChangeListener(checked -> {
            Log.d(TAG, "checked:" + checked);
            isUseLightGesture = !isUseLightGesture;
            BtnLightGestureOnOff.setCheck(isUseLightGesture);
            mUtilSpaceUserSettings.setLightGestureEnable(isUseLightGesture ? 1 : 0);
        });

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateImageByLocale();
    }

    private void updateImageByLocale() {
        Locale currentLocale = getResources().getConfiguration().locale;
        isKoreaLocale = Locale.KOREA.toString().equals(currentLocale.toString());
    }

    private void updateUI() {
        isUseKeypack = mUtilSpaceUserSettings.getKeyPackGestureEnable() == 1;
        isUseBottomGesture = mUtilSpaceUserSettings.getBottomGestureEnable() == 1;
        isUseLightGesture = mUtilSpaceUserSettings.getLightGestureEnable() == 1;

        BtnKeyPackOnOff.setCheck(isUseKeypack);
        BtnBottomGestureOnOff.setCheck(isUseBottomGesture);
        BtnLightGestureOnOff.setCheck(isUseLightGesture);
    }
}
