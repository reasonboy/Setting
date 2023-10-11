package com.jzzh.setting.device;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.display.HomeScreenUserActivity;
import com.jzzh.setting.display.PowerOffImageActivity;
import com.jzzh.setting.display.RefreshActivity;
import com.jzzh.setting.display.SleepImageActivity;
import com.jzzh.setting.display.WidgetTextActivity;

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
                startActivity(SystemUpdateActivity.class);
                break;
            case R.id.setting_device_app_update:
                startActivity(AppUpdateActivity.class);
                break;
            case R.id.setting_device_reset:
                startActivity(FactoryResetActivity.class);
                break;
            case R.id.setting_device_gsf_id:
                startActivity(GSFActivity.class);
                break;

        }
    }
}
