package com.jzzh.setting.display;

import android.os.Bundle;
import android.os.FileObserver;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jzzh.setting.display.BitmapManager.LOGO_SAVE_PATH;

public class PowerOffImageUserActivity extends UserImageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagePath = "/storage/emulated/0/PowerOff/";
        //1.正常状态; 2.低电量状态; 3.充电状态
        mSaveLogoPath = new String[]{"/data/misc/keystore/user_0/"+"poweroff.png", "/data/misc/keystore/user_0/"+"poweroff_nopower.png"};
        mNoImageSrcId = R.string.power_user_no_image;
    }
}
