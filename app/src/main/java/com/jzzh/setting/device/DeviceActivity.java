package com.jzzh.setting.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class DeviceActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        findViewById(R.id.setting_device_info).setOnClickListener(this);
        findViewById(R.id.setting_device_certification_info).setOnClickListener(this);
        findViewById(R.id.setting_device_system_update).setOnClickListener(this);
        findViewById(R.id.setting_device_app_update).setOnClickListener(this);
        findViewById(R.id.setting_device_reset).setOnClickListener(this);
        findViewById(R.id.setting_device_gsf_id).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_device_info:
                startActivity(DeviceInfoActivity.class);
                break;
            case R.id.setting_device_certification_info:
                startActivity(DeviceCertificationInfoActivity.class);
                break;
            case R.id.setting_device_system_update:
                callSystemUpdate();
                break;
            case R.id.setting_device_app_update:
                callAppUpdate();
                break;
            case R.id.setting_device_reset:
                startActivity(FactoryResetActivity.class);
                break;
            case R.id.setting_device_gsf_id:
                startActivity(GSFActivity.class);
                break;

        }
    }

    public void callSystemUpdate() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.inno.spaceota", "com.inno.spaceota.setting.OtaSettingActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callAppUpdate() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.inno.spaceota", "com.inno.spaceota.setting.AppOtaSettingActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
