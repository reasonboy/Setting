package com.jzzh.setting.display.logo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.setting.BaseActivityNoNav;
import com.jzzh.setting.R;
import com.jzzh.setting.display.PowerOffImageActivity;
import com.jzzh.setting.display.SleepImageActivity;

import java.io.File;

public class UserImageActivity extends BaseActivityNoNav {
    private static final String TAG = UserImageActivity.class.getSimpleName();
    public static final String ACTION_SLEEP_IMAGE_SETTING = "com.inno.action.SLEEP_IMAGE_SETTING";
    public static final String ACTION_POWER_OFF_IMAGE_SETTING = "com.inno.action.POWER_OFF_IMAGE_SETTING";
    public static final String EXTRA_IMAGE_PATH = "extra_image_path";
    public static final String EXTRA_SAVE_LOGO_PATH = "extra_save_logo_path";
    public static final String EXTRA_NO_IMAGE_RES_ID = "extra_no_image_res_id";
    public static final String EXTRA_TAG_PATH = "extra_tag_path";
    public static final String EXTRA_TAG_SETTINGS = "extra_tag_settings";
    public static final String EXTRA_TAG_SETTINGS_VALUE = "extra_tag_settings_value";

    protected String mImagePath;
    protected String[] mSaveLogoPath;
    protected int mNoImageSrcId;
    protected String mTagPath;
    protected String mTagSettings;
    protected int mTagSettingsValue;
    private UserImageListFragment mUserImageListFragment;
    private boolean mIsSetStandby = false;

    private boolean isExternal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_user_image_activity);
        mNavigationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("xml_log_nn","child onClick" + mUserImageListFragment.isVisible());
                if(mUserImageListFragment.isVisible()) {
                    finish();
                } else {
                    onBackPressed();
                }
            }
        });
        setNavText(getString(R.string.setting_display_select_image));

        Intent intent = getIntent();
        Log.v("xml_log_uia","intent.getAction()="+intent.getAction());
        if (intent != null && intent.getAction() == null) {
            if (intent.hasExtra(EXTRA_IMAGE_PATH)) {
                mImagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
            }
            if (intent.hasExtra(EXTRA_SAVE_LOGO_PATH)) {
                mSaveLogoPath = intent.getStringArrayExtra(EXTRA_SAVE_LOGO_PATH);
            }
            if (intent.hasExtra(EXTRA_NO_IMAGE_RES_ID)) {
                mNoImageSrcId = intent.getIntExtra(EXTRA_NO_IMAGE_RES_ID, 0);
            }
            if (intent.hasExtra(EXTRA_TAG_PATH)) {
                mTagPath = intent.getStringExtra(EXTRA_TAG_PATH);
            }
            if (intent.hasExtra(EXTRA_TAG_SETTINGS)) {
                mTagSettings = intent.getStringExtra(EXTRA_TAG_SETTINGS);
            }
            if (intent.hasExtra(EXTRA_TAG_SETTINGS_VALUE)) {
                mTagSettingsValue = intent.getIntExtra(EXTRA_TAG_SETTINGS_VALUE, 0);
            }
            if(mTagSettings.equals(SleepImageActivity.STANDBY_LOGO_TAG)) {
                mIsSetStandby = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("xml_log_fra","UserImageActivity imagePath = "+mImagePath);
        mUserImageListFragment = new UserImageListFragment(mImagePath,mNoImageSrcId,mTagPath,mTagSettingsValue);
        mUserImageListFragment.setOnItemClick(new UserImageListFragment.OnItemClick() {
            @Override
            public void onItemClick(File selectFile, int position) {
                UserImageSettingFragment userImageSettingFragment = new UserImageSettingFragment(selectFile,mSaveLogoPath,mTagPath);
                userImageSettingFragment.setOnClickListener(new UserImageSettingFragment.OnSettingCompletedListener() {
                    @Override
                    public void settingCompleted() {
                        if(mIsSetStandby) {
                            Settings.System.putInt(getContentResolver(),"sleep_screen_mode",0);
                        }
                        Settings.System.putInt(getContentResolver(),mTagSettings,mTagSettingsValue);
                        finish();
                    }
                });
                switchFragment(userImageSettingFragment);
            }
        });
        switchFragment(mUserImageListFragment);

        try {
            Intent intent = getIntent();
            String action = "";
            String path = "";
            if (intent != null) {
                action = intent.getAction();
                path = intent.getStringExtra("path");
                if (ACTION_SLEEP_IMAGE_SETTING.equals(action)) {
                    isExternal = true;
                    mSaveLogoPath = BitmapManager.SLEEP_IMAGE_SAVE_FILES;
                    mTagPath = SleepImageActivity.USER_LOGO_TAG_PATH;
                    mIsSetStandby = true;
                    mTagSettings = SleepImageActivity.STANDBY_LOGO_TAG;
                    mTagSettingsValue = SleepImageActivity.STANDBY_LOGO_TAG_USER;
                }
                if (ACTION_POWER_OFF_IMAGE_SETTING.equals(action)) {
                    isExternal = true;
                    mSaveLogoPath = BitmapManager.POWER_OFF_IMAGE_SAVE_FILES;
                    mTagPath = PowerOffImageActivity.USER_LOGO_TAG_PATH;
                    mIsSetStandby = false;
                    mTagSettings = PowerOffImageActivity.POWER_OFF_LOGO_TAG;
                    mTagSettingsValue = PowerOffImageActivity.POWER_OFF_LOGO_TAG_USER;
                }
            } else Log.e(TAG, "intent is null !!!!!!");
            Log.d(TAG, "action : " + action + ", path : " + path);

            if (path == null || path.isEmpty()) {
                Log.d(TAG, "Image path is empty!");
            } else {
                UserImageSettingFragment userImageSettingFragment = new UserImageSettingFragment(new File(path), mSaveLogoPath,mTagPath);
                userImageSettingFragment.setOnClickListener(() -> {
                    if (isExternal) {
                        if(mIsSetStandby) {
                            Settings.System.putInt(getContentResolver(),"sleep_screen_mode",0);
                        }
                        Settings.System.putInt(getContentResolver(),mTagSettings,mTagSettingsValue);
                        this.finish();
                    } else {
                        switchFragment(mUserImageListFragment);
                    }
                });
                switchFragment(userImageSettingFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchFragment(Fragment fragment) {
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);  // 去除动画
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
