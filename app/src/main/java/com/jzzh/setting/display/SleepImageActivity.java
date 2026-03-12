package com.jzzh.setting.display;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;

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
    private Bitmap mPreviewBitmapDef,mPreviewBitmapUser;
    private List<StyleChooseView.PageData> mPages = new ArrayList<>();

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
        if(logoTag == STANDBY_LOGO_TAG_DEF) {
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
        if(logoTag == STANDBY_LOGO_TAG_USER) {
            rightButtonText = getString(R.string.setting_display_applied);
            customPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            customPage.setRightButtonSelected(false);
        }
        customPage.setRightButton(rightButtonText);

        //page2设置右按钮
        StyleChooseView.PageData curScreenPage = mPages.get(2);
        if(logoTag == STANDBY_LOGO_TAG_CUR_SCREEN) {
            rightButtonText = getString(R.string.setting_display_applied);
            curScreenPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            curScreenPage.setRightButtonSelected(false);
        }
        curScreenPage.setRightButton(rightButtonText);
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
        mPages.add(defaultPage);
        mPages.add(customPage);
        mPages.add(curScreenPage);
        mStyleChooseView.setPages(mPages);
    }

    private int getStandbyTag() {
        int tag = Settings.System.getInt(getContentResolver(),STANDBY_LOGO_TAG,0);
        return tag;
    }

    private void setStandbyTag(int tag) {
        Settings.System.putInt(getContentResolver(),STANDBY_LOGO_TAG,tag);
    }

    private void setSleepScreenMode(int mode) {
        Settings.System.putInt(getContentResolver(),"sleep_screen_mode",mode);
    }

    private void getSleepScreenMode() {
        Settings.System.putInt(getContentResolver(),"sleep_screen_mode",0);
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
