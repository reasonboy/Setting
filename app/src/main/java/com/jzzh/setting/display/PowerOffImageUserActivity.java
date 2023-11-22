package com.jzzh.setting.display;

import android.os.Bundle;

import com.jzzh.setting.R;
import com.jzzh.setting.display.logo.BitmapManager;
import com.jzzh.setting.display.logo.UserImageActivity;

public class PowerOffImageUserActivity extends UserImageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagePath = "/storage/emulated/0/PowerOff/";
        //1.正常状态; 2.低电量状态; 3.充电状态
        mSaveLogoPath = BitmapManager.POWER_OFF_IMAGE_SAVE_FILES;
        mNoImageSrcId = R.string.power_user_no_image;
    }
}
