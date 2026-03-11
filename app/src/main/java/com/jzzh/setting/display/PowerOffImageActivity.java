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

public class PowerOffImageActivity extends BaseActivity {

    private StyleChooseView mStyleChooseView;
    public static final String DEF_LOGO_TAG_PATH = "/data/misc/eink/poweroff_tag_def.png";
    public static final String USER_LOGO_TAG_PATH = "/data/misc/eink/poweroff_tag_user.png";
    public static final String POWER_OFF_LOGO_TAG = "power_off_logo_tag";
    public static final int POWER_OFF_LOGO_TAG_DEF = 0;
    public static final int POWER_OFF_LOGO_TAG_USER = 1;
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
        int logoTag = getPowerOffTag();
        File defTagFile = new File(DEF_LOGO_TAG_PATH);
        mPreviewBitmapDef = BitmapFactory.decodeFile("/vendor/media/poweroff.png");
        if(defTagFile.exists()) {
            mPreviewBitmapDef = BitmapFactory.decodeFile(DEF_LOGO_TAG_PATH);
        }
        StyleChooseView.PageData defaultPage = mPages.get(0);
        defaultPage.setTitle(getString(R.string.setting_display_default_image));
        defaultPage.setImage(mPreviewBitmapDef);
        String rightButtonText = null;
        if(logoTag == POWER_OFF_LOGO_TAG_DEF) {
            rightButtonText = getString(R.string.setting_display_applied);
            defaultPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            defaultPage.setRightButtonSelected(false);
        }
        defaultPage.setRightButton(rightButtonText);

        File userTagFile = new File(USER_LOGO_TAG_PATH);
        mPreviewBitmapUser = Bitmap.createBitmap(1264, 1680, Bitmap.Config.ARGB_8888);
        if(userTagFile.exists()) {
            mPreviewBitmapUser = BitmapFactory.decodeFile(USER_LOGO_TAG_PATH);
        }
        StyleChooseView.PageData customPage = mPages.get(1);
        customPage.setTitle(getString(R.string.setting_display_si_user));
        customPage.setImage(mPreviewBitmapUser);
        if(logoTag == POWER_OFF_LOGO_TAG_USER) {
            rightButtonText = getString(R.string.setting_display_applied);
            customPage.setRightButtonSelected(true);
        } else {
            rightButtonText = getString(R.string.setting_display_apply);
            customPage.setRightButtonSelected(false);
        }
        customPage.setRightButton(rightButtonText);
        mStyleChooseView.updatePageDisplay();
}

    private void initPages() {
        StyleChooseView.PageData defaultPage = new StyleChooseView.PageData();
        defaultPage.setLeftButton(getString(R.string.setting_display_select_image), () -> {
            startDefImageActivity();
        });
        defaultPage.setRightButton("", () -> {
            setDefLogo();
        });
        StyleChooseView.PageData customPage = new StyleChooseView.PageData();
        customPage.setLeftButton(getString(R.string.setting_display_select_image), () -> {
            startUserImageActivity();
        });
        customPage.setRightButton("", () -> {
            setUserLogo();
        });
        mPages.add(defaultPage);
        mPages.add(customPage);
        mStyleChooseView.setPages(mPages);
    }

    private int getPowerOffTag() {
        int tag = Settings.System.getInt(getContentResolver(),POWER_OFF_LOGO_TAG,0);
        return tag;
    }

    private void setPowerOffTag(int tag) {
        Settings.System.putInt(getContentResolver(),POWER_OFF_LOGO_TAG,tag);
    }

    private void setUserLogo() {
        File userTagFile = new File(USER_LOGO_TAG_PATH);
        if(userTagFile.exists()) {
            for(String logoPath : BitmapManager.POWER_OFF_IMAGE_SAVE_FILES) {
                BitmapManager.saveBitmap(mPreviewBitmapUser,logoPath);
            }
            Intent intent = new Intent("zhihe.action.UPDATE_STANDBY");
            sendBroadcast(intent);
            setPowerOffTag(POWER_OFF_LOGO_TAG_USER);
            updateView();
        }
    }

    private void setDefLogo() {
        for(String logoPath : BitmapManager.POWER_OFF_IMAGE_SAVE_FILES) {
            BitmapManager.saveBitmap(mPreviewBitmapDef,logoPath);
        }
        Intent intent = new Intent("zhihe.action.UPDATE_STANDBY");
        sendBroadcast(intent);
        setPowerOffTag(POWER_OFF_LOGO_TAG_DEF);
        updateView();
    }

    private void startUserImageActivity() {
        Intent intent = new Intent(this, UserImageActivity.class);
        intent.putExtra(UserImageActivity.EXTRA_IMAGE_PATH, "/storage/emulated/0/PowerOff/");
        intent.putExtra(UserImageActivity.EXTRA_SAVE_LOGO_PATH, BitmapManager.POWER_OFF_IMAGE_SAVE_FILES);
        intent.putExtra(UserImageActivity.EXTRA_NO_IMAGE_RES_ID, R.string.power_user_no_image);
        intent.putExtra(UserImageActivity.EXTRA_TAG_PATH, USER_LOGO_TAG_PATH);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS, POWER_OFF_LOGO_TAG);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS_VALUE, POWER_OFF_LOGO_TAG_USER);
        startActivity(intent);
    }

    private void startDefImageActivity() {
        Intent intent = new Intent(this, UserImageActivity.class);
        intent.putExtra(UserImageActivity.EXTRA_IMAGE_PATH, "/vendor/media/def_poweroff");
        intent.putExtra(UserImageActivity.EXTRA_SAVE_LOGO_PATH, BitmapManager.POWER_OFF_IMAGE_SAVE_FILES);
        intent.putExtra(UserImageActivity.EXTRA_NO_IMAGE_RES_ID, R.string.power_user_no_image);
        intent.putExtra(UserImageActivity.EXTRA_TAG_PATH, DEF_LOGO_TAG_PATH);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS, POWER_OFF_LOGO_TAG);
        intent.putExtra(UserImageActivity.EXTRA_TAG_SETTINGS_VALUE, POWER_OFF_LOGO_TAG_DEF);
        startActivity(intent);
    }
}
