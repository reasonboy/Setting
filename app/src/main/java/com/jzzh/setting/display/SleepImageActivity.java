package com.jzzh.setting.display;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.CheckedTextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.StyleChooseView;
import com.jzzh.setting.display.logo.BitmapManager;
import com.jzzh.setting.display.logo.UserImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SleepImageActivity extends BaseActivity {

    private StyleChooseView mStyleChooseView;
    public static final String DEF_LOGO_TAG_PATH = "/data/misc/eink/standby_tag_def.png";
    public static final String USER_LOGO_TAG_PATH = "/data/misc/eink/standby_tag_user.png";
    public static final String STANDBY_LOGO_TAG = "standby_logo_tag";
    public static final int STANDBY_LOGO_TAG_DEF = 0;
    public static final int STANDBY_LOGO_TAG_USER = 1;
    public static final int STANDBY_LOGO_TAG_CUR_SCREEN = 2;
    private static final String SLEEP_SCREEN_MODE = "sleep_screen_mode";
    private static final int SLEEP_SCREEN_MODE_CLOCK = 2;
    private static final String EINK_SLEEP_CLOCK_STYLE = "eink_sleep_clock_style";
    private static final int CLOCK_STYLE_WHITE = 0;
    private static final int CLOCK_STYLE_BLACK = 1;
    private Bitmap mPreviewBitmapDef,mPreviewBitmapUser;
    private List<StyleChooseView.PageData> mPages = new ArrayList<>();
    private StyleChooseView.PageData mClockPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off_image);
        mStyleChooseView = findViewById(R.id.style_choose_view);
        initPages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        int logoTag = getStandbyTag();
        boolean clockApplied = getSleepScreenMode() == SLEEP_SCREEN_MODE_CLOCK;
        //page0设置图片
        File defTagFile = new File(DEF_LOGO_TAG_PATH);
        mPreviewBitmapDef = BitmapFactory.decodeFile("/vendor/media/standby.png");
        if(defTagFile.exists()) {
            mPreviewBitmapDef = BitmapFactory.decodeFile(DEF_LOGO_TAG_PATH);
        }
        StyleChooseView.PageData defaultPage = mPages.get(0);
        defaultPage.setImage(mPreviewBitmapDef);
        //page0设置右按钮
        String rightButtonText = null;
        if(!clockApplied && logoTag == STANDBY_LOGO_TAG_DEF) {
            rightButtonText = getString(R.string.setting_display_applied);
            defaultPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            defaultPage.setRightButtonSelected(false);
        }
        defaultPage.setRightButton(rightButtonText);

        //page1设置图片
        File userTagFile = new File(USER_LOGO_TAG_PATH);
        mPreviewBitmapUser = Bitmap.createBitmap(1264, 1680, Bitmap.Config.ARGB_8888);
        if(userTagFile.exists()) {
            mPreviewBitmapUser = BitmapFactory.decodeFile(USER_LOGO_TAG_PATH);
        }
        StyleChooseView.PageData customPage = mPages.get(1);
        customPage.setImage(mPreviewBitmapUser);
        //page1设置右按钮
        if(!clockApplied && logoTag == STANDBY_LOGO_TAG_USER) {
            rightButtonText = getString(R.string.setting_display_applied);
            customPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            customPage.setRightButtonSelected(false);
        }
        customPage.setRightButton(rightButtonText);

        //page2设置右按钮
        StyleChooseView.PageData curScreenPage = mPages.get(2);
        if(!clockApplied && logoTag == STANDBY_LOGO_TAG_CUR_SCREEN) {
            rightButtonText = getString(R.string.setting_display_applied);
            curScreenPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            curScreenPage.setRightButtonSelected(false);
        }
        curScreenPage.setRightButton(rightButtonText);

        if (clockApplied) {
            mClockPage.setRightButton(getString(R.string.setting_display_applied));
        } else {
            mClockPage.setRightButton(getString(R.string.setting_display_apply));
        }
        mClockPage.setRightButtonSelected(clockApplied);
        updateClockPreview(getClockStyle());
        mStyleChooseView.updatePageDisplay();
    }

    private void initPages() {
        StyleChooseView.PageData defaultPage = new StyleChooseView.PageData();
        defaultPage.setTitle(getString(R.string.setting_display_default_image));
        defaultPage.setLeftButton(getString(R.string.setting_display_select_image), () -> {
            startDefImageActivity();
        });
        defaultPage.setRightButton("", () -> {
            setDefLogo();
        });

        StyleChooseView.PageData customPage = new StyleChooseView.PageData();
        customPage.setTitle(getString(R.string.setting_display_si_user));
        customPage.setLeftButton(getString(R.string.setting_display_select_image), () -> {
            startUserImageActivity();
        });
        customPage.setRightButton("", () -> {
            setUserLogo();
        });

        StyleChooseView.PageData curScreenPage = new StyleChooseView.PageData();
        curScreenPage.setTitle(getString(R.string.setting_display_current_screen));
        curScreenPage.setWarning(getString(R.string.setting_display_current_screen_warning));
        curScreenPage.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.setting_display_sleepimg_currentscreen_length));
        curScreenPage.setRightButton("", () -> {
            setcurScreenLogo();
        });

        mClockPage = new StyleChooseView.PageData();
        mClockPage.setTitle(getString(R.string.setting_display_sleep_clock));
        mClockPage.setLeftButton(getString(R.string.sleep_clock_color_mode), this::showColorModeDialog);
        mClockPage.setRightButton(getString(R.string.setting_display_apply), this::applyClock);

        mPages.add(defaultPage);
        mPages.add(customPage);
        mPages.add(curScreenPage);
        mPages.add(mClockPage);
        mStyleChooseView.setPages(mPages);
    }

    private void showColorModeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_sleep_clock_color_mode);

        CheckedTextView lightMode = dialog.findViewById(R.id.sleep_clock_light_mode);
        CheckedTextView darkMode = dialog.findViewById(R.id.sleep_clock_dark_mode);
        updateColorModeOptions(lightMode, darkMode);

        lightMode.setOnClickListener(view -> selectClockStyle(CLOCK_STYLE_WHITE, lightMode, darkMode));
        darkMode.setOnClickListener(view -> selectClockStyle(CLOCK_STYLE_BLACK, lightMode, darkMode));
        dialog.findViewById(R.id.exit).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void selectClockStyle(int style, CheckedTextView lightMode, CheckedTextView darkMode) {
        if (Settings.System.putInt(getContentResolver(), EINK_SLEEP_CLOCK_STYLE, style)) {
            updateColorModeOptions(lightMode, darkMode);
            updateClockPreview(style);
            mStyleChooseView.updatePageDisplay();
            if (getSleepScreenMode() == SLEEP_SCREEN_MODE_CLOCK) {
                sendBroadcast(new Intent("zhihe.action.UPDATE_STANDBY"));
            }
        }
    }

    private void updateColorModeOptions(CheckedTextView lightMode, CheckedTextView darkMode) {
        if (getClockStyle() == CLOCK_STYLE_BLACK) {
            lightMode.setChecked(false);
            lightMode.setCheckMarkDrawable(R.drawable.check_off);
            darkMode.setChecked(true);
            darkMode.setCheckMarkDrawable(R.drawable.check_on);
        } else {
            lightMode.setChecked(true);
            lightMode.setCheckMarkDrawable(R.drawable.check_on);
            darkMode.setChecked(false);
            darkMode.setCheckMarkDrawable(R.drawable.check_off);
        }
    }

    private int getClockStyle() {
        return Settings.System.getInt(getContentResolver(), EINK_SLEEP_CLOCK_STYLE, CLOCK_STYLE_WHITE);
    }

    private void updateClockPreview(int style) {
        if (style == CLOCK_STYLE_BLACK) {
            mClockPage.imageResId = R.drawable.clock_black;
        } else {
            mClockPage.imageResId = R.drawable.clock_white;
        }
    }

    private void applyClock() {
        setSleepScreenMode(SLEEP_SCREEN_MODE_CLOCK);
        sendBroadcast(new Intent("zhihe.action.UPDATE_STANDBY"));
        updateView();
    }

    private int getStandbyTag() {
        int tag = Settings.System.getInt(getContentResolver(),STANDBY_LOGO_TAG,0);
        return tag;
    }

    private void setStandbyTag(int tag) {
        Settings.System.putInt(getContentResolver(),STANDBY_LOGO_TAG,tag);
    }

    private void setSleepScreenMode(int mode) {
        Settings.System.putInt(getContentResolver(), SLEEP_SCREEN_MODE, mode);
    }

    private int getSleepScreenMode() {
        return Settings.System.getInt(getContentResolver(), SLEEP_SCREEN_MODE, 0);
    }

    private void setcurScreenLogo() {
        setSleepScreenMode(1);
        Intent intent = new Intent("zhihe.action.UPDATE_STANDBY");
        sendBroadcast(intent);
        setStandbyTag(STANDBY_LOGO_TAG_CUR_SCREEN);
        updateView();
    }

    private void setUserLogo() {
        File userTagFile = new File(USER_LOGO_TAG_PATH);
        if(userTagFile.exists()) {
            for(String logoPath : BitmapManager.SLEEP_IMAGE_SAVE_FILES) {
                BitmapManager.saveBitmap(mPreviewBitmapUser,logoPath);
            }
            setSleepScreenMode(0);
            Intent intent = new Intent("zhihe.action.UPDATE_STANDBY");
            sendBroadcast(intent);
            setStandbyTag(STANDBY_LOGO_TAG_USER);
            updateView();
        }
    }

    private void setDefLogo() {
        for(String logoPath : BitmapManager.SLEEP_IMAGE_SAVE_FILES) {
            BitmapManager.saveBitmap(mPreviewBitmapDef,logoPath);
        }
        setSleepScreenMode(0);
        Intent intent = new Intent("zhihe.action.UPDATE_STANDBY");
        sendBroadcast(intent);
        setStandbyTag(STANDBY_LOGO_TAG_DEF);
        updateView();
    }

    private void startUserImageActivity() {
        Intent intent = new Intent(this, UserImageActivity.class);
        intent.putExtra(UserImageActivity.EXTRA_IMAGE_PATH, "/storage/emulated/0/Sleep/");
        intent.putExtra(UserImageActivity.EXTRA_SAVE_LOGO_PATH, BitmapManager.SLEEP_IMAGE_SAVE_FILES);
        intent.putExtra(UserImageActivity.EXTRA_NO_IMAGE_RES_ID, R.string.sleep_user_no_image);
        intent.putExtra(UserImageActivity.EXTRA_TAG_PATH, USER_LOGO_TAG_PATH);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS, STANDBY_LOGO_TAG);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS_VALUE, STANDBY_LOGO_TAG_USER);
        startActivity(intent);
    }

    private void startDefImageActivity() {
        Intent intent = new Intent(this, UserImageActivity.class);
        intent.putExtra(UserImageActivity.EXTRA_IMAGE_PATH, "/vendor/media/def_sleep");
        intent.putExtra(UserImageActivity.EXTRA_SAVE_LOGO_PATH, BitmapManager.SLEEP_IMAGE_SAVE_FILES);
        intent.putExtra(UserImageActivity.EXTRA_NO_IMAGE_RES_ID, R.string.sleep_user_no_image);
        intent.putExtra(UserImageActivity.EXTRA_TAG_PATH, DEF_LOGO_TAG_PATH);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS, STANDBY_LOGO_TAG);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS_VALUE, STANDBY_LOGO_TAG_DEF);
        startActivity(intent);
    }

}
