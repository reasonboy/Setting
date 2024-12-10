package com.jzzh.setting.display;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.SettingSubItem;
import com.jzzh.setting.utils.UtilSpaceUserSettings;
import com.jzzh.tools.ZhSeekBar;

public class HomeScreenStyleActivity extends BaseActivity implements View.OnClickListener {

    private static final int HOME_SCREEN_DEF_APP_STYLE = 0;
    private static final int HOME_SCREEN_WIDGET_STYLE = 1;
    private static final int HOME_SCREEN_WIDGET_DISABLE = 0;
    private static final int HOME_SCREEN_WIDGET_ENABLE = 1;

    private HomeScreenUserItem mWidgetStyle, mDefaultAppStyle;
    private SettingSubItem mHomeBackgroundSetting, mHomeBackgroundAlphaSetting, mResetHomeBackground;

    private UtilSpaceUserSettings mUtilSpaceUserSettings;

    private float mBackgroundAlpha = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_home_screen_style);
        mUtilSpaceUserSettings = new UtilSpaceUserSettings(this);

        mWidgetStyle = findViewById(R.id.widget_style);
        mWidgetStyle.setTitle(R.string.home_screen_widget_style);
        mWidgetStyle.setPreview(R.drawable.home_screen_widget_on);
        mWidgetStyle.setOnClickListener(view -> {
            setHomeScreenStyle(HOME_SCREEN_WIDGET_STYLE);
            updateView();
        });

        mDefaultAppStyle = findViewById(R.id.default_app_style);
        mDefaultAppStyle.setTitle(R.string.home_screen_default_style);
        mDefaultAppStyle.setPreview(R.drawable.home_screen_app_style);
        mDefaultAppStyle.setOnClickListener(view -> {
            setHomeScreenStyle(HOME_SCREEN_DEF_APP_STYLE);
            updateView();
        });

        mHomeBackgroundSetting = findViewById(R.id.goto_home_background_setting);
        mHomeBackgroundSetting.setOnClickListener(v -> {
            // go to browser for background setting
            Intent intent = new Intent("com.inno.action.OPEN_FILE_BROWSER.BACKGROUND_IMAGE");
            sendBroadcast(intent);
        });

        mHomeBackgroundAlphaSetting = findViewById(R.id.home_background_alpha_setting);
        mHomeBackgroundAlphaSetting.setOnClickListener(v -> {
            makeBackgroundAlphaSettingDialog();
        });

        mResetHomeBackground = findViewById(R.id.reset_home_background);
        mResetHomeBackground.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.reset_home_background_setting)
                    .setMessage(R.string.msg_reset_home_background_setting)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                    })
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> resetHomeBackground())
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        int homeScreenStyle = getHomeScreenStyle();
        int widgetEnable = mUtilSpaceUserSettings.getWidgetEnable();
        if(homeScreenStyle == HOME_SCREEN_WIDGET_STYLE) {
            mWidgetStyle.select(true);
            mDefaultAppStyle.select(false);

            if(widgetEnable == HOME_SCREEN_WIDGET_ENABLE) {
                mWidgetStyle.setPreview(R.drawable.home_screen_widget_on);
            } else {
                mWidgetStyle.setPreview(R.drawable.home_screen_widget_off);
            }
        } else {
            mWidgetStyle.select(false);
            mDefaultAppStyle.select(true);
        }
    }

    private void setHomeScreenStyle(int style) {
        Settings.System.putInt(getContentResolver(),"space_widget_style_enable", style);
    }

    private int getHomeScreenStyle() {
        return Settings.System.getInt(getContentResolver(),"space_widget_style_enable", HOME_SCREEN_WIDGET_STYLE);
    }

    @Override
    public void onClick(View view) {

    }

    public void resetHomeBackground() {
        boolean result = mUtilSpaceUserSettings.setHomeBackground(this, BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper));
        if (result) Toast.makeText(this, R.string.reset_home_background_setting, Toast.LENGTH_SHORT).show();
    }

    public void makeBackgroundAlphaSettingDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bg_popup = inflater.inflate(R.layout.dialog_setting_background, null);

        ZhSeekBar seekBar = bg_popup.findViewById(R.id.background_setting_alpha);
        ImageView minus = bg_popup.findViewById(R.id.background_setting_alpha_minus);
        ImageView plus = bg_popup.findViewById(R.id.background_setting_alpha_plus);

        ImageView preview = bg_popup.findViewById(R.id.background_preview);

        preview.setImageDrawable(mUtilSpaceUserSettings.getHomeBackgroundDrawable(this));
        mBackgroundAlpha = mUtilSpaceUserSettings.getHomeBackgroundAlpha();
        seekBar.setMaxValue(100);
        seekBar.setValue(Math.round(mBackgroundAlpha * 100));
        preview.setAlpha(mBackgroundAlpha);

        minus.setOnClickListener(v1 -> {
            int progress = seekBar.getValue();
            if (progress > 0) {
                seekBar.setValue(progress - 10);
                preview.setAlpha((progress - 10) / 100.0f);
                mBackgroundAlpha = (progress - 10) / 100.0f;
            }
        });

        plus.setOnClickListener(v1 -> {
            int progress = seekBar.getValue();
            if (progress < 100) {
                seekBar.setValue(progress + 10);
                preview.setAlpha((progress + 10) / 100.0f);
                mBackgroundAlpha = (progress + 10) / 100.0f;
            }
        });

        seekBar.setOnZhSeekBarChangeListener(new ZhSeekBar.OnZhSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int value) {
                mBackgroundAlpha = value / 100.0f;
                preview.setAlpha(mBackgroundAlpha);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.home_background_alpha_setting);
        builder.setView(bg_popup)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> mUtilSpaceUserSettings.setHomeBackgroundAlpha(mBackgroundAlpha))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialogs = builder.create();
        dialogs.show();
    }
}
