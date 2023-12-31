package com.jzzh.setting.display.logo;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.io.File;

public class UserImageActivity extends BaseActivity {

    protected String mImagePath;
    protected String[] mSaveLogoPath;
    protected int mNoImageSrcId;
    private UserImageListFragment mUserImageListFragment;

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
    }

    private void switchFragment(Fragment fragment) {
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);  // 去除动画
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
