package com.jzzh.setting.display;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jzzh.setting.BaseActivityNoNav;
import com.jzzh.setting.R;
import com.jzzh.setting.SettingSubItem;
import com.jzzh.setting.utils.UtilSpaceUserSettings;
import com.jzzh.tools.ZhSeekBar;

public class HomeBackgroundSettingActivity extends BaseActivityNoNav {

    private SettingSubItem mHomeBackgroundSetting, mHomeBackgroundAlphaSetting, mResetHomeBackground;

    private UtilSpaceUserSettings mUtilSpaceUserSettings;

    private float mBackgroundAlpha = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_home_background_setting);
        setNavText(getString(R.string.home_background));
        mUtilSpaceUserSettings = new UtilSpaceUserSettings(this);

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
