package com.jzzh.setting.keygesture;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

public class BottomSettingActivity extends BaseActivity {

    private static final String NAVIGATION_BAR_ENABLE = "navigation_bar_enbale";
    private static final int BOTTOM_GESTURE = 0;
    private static final int NAVIGATION_BUTTON = 1;

    private ZhCheckBox mBottomGestureCheckBox;
    private ZhCheckBox mNavigationButtonCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_setting);

        mBottomGestureCheckBox = setupSection(R.id.bottom_gesture_section,
                R.string.setting_bottom_gesture, R.drawable.setting_k_g_bottom,
                R.string.setting_bottom_gesture_card_title);
        mNavigationButtonCheckBox = setupSection(R.id.navigation_button_section,
                R.string.setting_navigation_button, R.drawable.setting_k_g_navigation,
                R.string.setting_navigation_button_card_title);

        mBottomGestureCheckBox.setOnZhCheckedChangeListener(
                checked -> setNavigationMode(BOTTOM_GESTURE));
        mNavigationButtonCheckBox.setOnZhCheckedChangeListener(
                checked -> setNavigationMode(NAVIGATION_BUTTON));

        View bottomGestureSection = findViewById(R.id.bottom_gesture_section);
        View bottomGestureCard = bottomGestureSection.findViewById(R.id.bottom_setting_card);
        bottomGestureCard.setOnClickListener(view -> startActivity(BottomGestureSettingActivity.class));

        View navigationButtonSection = findViewById(R.id.navigation_button_section);
        View navigationButtonCard = navigationButtonSection.findViewById(R.id.bottom_setting_card);
        navigationButtonCard.setOnClickListener(
                view -> startActivity(NavigationButtonSettingActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCheckBoxes();
    }

    private ZhCheckBox setupSection(int sectionId, int titleResId, int previewResId,
            int cardTitleResId) {
        View section = findViewById(sectionId);
        TextView title = section.findViewById(R.id.bottom_setting_section_title);
        ImageView preview = section.findViewById(R.id.bottom_setting_preview);
        TextView cardTitle = section.findViewById(R.id.bottom_setting_card_title);
        title.setText(titleResId);
        preview.setImageResource(previewResId);
        cardTitle.setText(cardTitleResId);

        return section.findViewById(R.id.bottom_setting_checkbox);
    }

    private void setNavigationMode(int mode) {
        Settings.System.putInt(getContentResolver(), NAVIGATION_BAR_ENABLE, mode);
        updateCheckBoxes();
    }

    private void updateCheckBoxes() {
        int mode = Settings.System.getInt(getContentResolver(), NAVIGATION_BAR_ENABLE,
                NAVIGATION_BUTTON);
        boolean navigationButtonEnabled = mode == NAVIGATION_BUTTON;
        mBottomGestureCheckBox.setCheck(!navigationButtonEnabled);
        mNavigationButtonCheckBox.setCheck(navigationButtonEnabled);
    }
}
