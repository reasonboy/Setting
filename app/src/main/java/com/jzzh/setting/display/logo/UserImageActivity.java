package com.jzzh.setting.display.logo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.io.File;

public class UserImageActivity extends BaseActivity {
    private static final String TAG = UserImageActivity.class.getSimpleName();
    public static final String ACTION_SLEEP_IMAGE_SETTING = "com.inno.action.SLEEP_IMAGE_SETTING";
    public static final String ACTION_POWER_OFF_IMAGE_SETTING = "com.inno.action.POWER_OFF_IMAGE_SETTING";

    protected String mImagePath;
    protected String[] mSaveLogoPath;
    protected int mNoImageSrcId;
    private UserImageListFragment mUserImageListFragment;

    private boolean isExternal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_user_image_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("xml_log_fra","UserImageActivity imagePath = "+mImagePath);
        mUserImageListFragment = new UserImageListFragment(mImagePath,mNoImageSrcId);
        mUserImageListFragment.setOnItemClick(new UserImageListFragment.OnItemClick() {
            @Override
            public void onItemClick(File selectFile, int position) {
                UserImageSettingFragment userImageSettingFragment = new UserImageSettingFragment(selectFile,mSaveLogoPath);
                userImageSettingFragment.setOnClickListener(new UserImageSettingFragment.OnSettingCompletedListener() {
                    @Override
                    public void settingCompleted() {
                        switchFragment(mUserImageListFragment);
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

                if (ACTION_SLEEP_IMAGE_SETTING.equals(action)
                        || ACTION_POWER_OFF_IMAGE_SETTING.equals(action)) {
                    isExternal = true;
                }
            } else Log.e(TAG, "intent is null !!!!!!");
            Log.d(TAG, "action : " + action + ", path : " + path);

            if (path == null || path.isEmpty()) {
                Log.d(TAG, "Image path is empty!");
            } else {
                UserImageSettingFragment userImageSettingFragment = new UserImageSettingFragment(new File(path), mSaveLogoPath);
                userImageSettingFragment.setOnClickListener(() -> {
                    if (isExternal) {
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
