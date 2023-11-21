package com.jzzh.setting.display;

import android.os.Bundle;

import com.jzzh.setting.R;

import static com.jzzh.setting.display.BitmapManager.LOGO_SAVE_PATH;

public class SleepImageUserActivity extends UserImageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagePath = "/storage/emulated/0/Sleep/";
        //1.正常状态; 2.低电量状态; 3.充电状态
        mSaveLogoPath = new String[]{LOGO_SAVE_PATH+"standby.png", LOGO_SAVE_PATH+"standby_lowpower.png", LOGO_SAVE_PATH+"standby_charge.png"};
        mNoImageSrcId = R.string.sleep_user_no_image;
    }
}
