package com.jzzh.setting.display;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.StyleChooseView;
import com.jzzh.setting.display.logo.BitmapManager;
import com.jzzh.setting.display.logo.UserImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PowerOffImageActivity extends BaseActivity {

    private StyleChooseView mStyleChooseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off_image);
        mStyleChooseView = findViewById(R.id.style_choose_view);
        initPages();
    }

    private void initPages() {
        List<StyleChooseView.PageData> pages = new ArrayList<>();

        Bitmap defPowerOffImage = BitmapFactory.decodeFile("/vendor/media/poweroff.png");
        if (defPowerOffImage != null) {
            StyleChooseView.PageData defaultPage = new StyleChooseView.PageData(getString(R.string.setting_display_default_image), defPowerOffImage);
            defaultPage.setLeftButton(getString(R.string.setting_display_select_image), () -> {
                startUserImageActivity();
            });
            defaultPage.setRightButton(getString(R.string.setting_display_apply), () -> {
            });
            
            pages.add(defaultPage);
        }


        if (new File(BitmapManager.POWER_OFF_IMAGE_SAVE_FILES[0]).exists() && 
            new File(BitmapManager.POWER_OFF_IMAGE_SAVE_FILES[1]).exists()) {
            
            Bitmap userImage = BitmapFactory.decodeFile(BitmapManager.POWER_OFF_IMAGE_SAVE_FILES[0]);
            if (userImage != null) {
                StyleChooseView.PageData customPage = new StyleChooseView.PageData(getString(R.string.setting_display_default_image), userImage);
                customPage.setLeftButton(getString(R.string.setting_display_select_image), () -> {
                    startUserImageActivity();
                });
                
                customPage.setRightButton(getString(R.string.setting_display_apply), () -> {
                });
                
                pages.add(customPage);
            }
        }

        mStyleChooseView.setPages(pages);
    }

    private void startUserImageActivity() {
        Intent intent = new Intent(this, UserImageActivity.class);
        intent.putExtra(UserImageActivity.EXTRA_IMAGE_PATH, "/storage/emulated/0/PowerOff/");
        intent.putExtra(UserImageActivity.EXTRA_SAVE_LOGO_PATH, BitmapManager.POWER_OFF_IMAGE_SAVE_FILES);
        intent.putExtra(UserImageActivity.EXTRA_NO_IMAGE_RES_ID, R.string.power_user_no_image);
        startActivity(intent);
    }

}
