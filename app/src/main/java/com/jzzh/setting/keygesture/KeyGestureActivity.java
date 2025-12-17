package com.jzzh.setting.keygesture;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class KeyGestureActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture);
        findViewById(R.id.setting_physical_key).setOnClickListener(this);
        findViewById(R.id.setting_inno_key).setOnClickListener(this);
        findViewById(R.id.setting_keypack).setOnClickListener(this);
        findViewById(R.id.setting_bottom_gesture).setOnClickListener(this);
        findViewById(R.id.setting_side_gesture).setOnClickListener(this);
        findViewById(R.id.setting_light_gesture).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_physical_key:
                startActivity(PhysicalKeySettingActivity.class);
                break;
            case R.id.setting_inno_key:
                callInnoKeySetting();
                break;
            case R.id.setting_keypack:
                callKeyPackSetting();
                break;
            case R.id.setting_bottom_gesture:
                startActivity(BottomGestureSettingActivity.class);
                break;
            case R.id.setting_side_gesture:
                startActivity(SideGestureSettingActivity.class);
                break;
            case R.id.setting_light_gesture:
                startActivity(LightGestureSettingActivity.class);
                break;
        }
    }

    public void callInnoKeySetting() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.inno.spaceservice", "com.inno.spaceservice.InnoKeySettingActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callKeyPackSetting() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.inno.spaceservice",
                    "com.inno.spaceservice.KeyPackSettingActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
