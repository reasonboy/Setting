package com.jzzh.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jzzh.setting.device.DeviceActivity;
import com.jzzh.setting.display.DisplayActivity;
import com.jzzh.setting.language.LanguageAndKeyboardActivity;
import com.jzzh.setting.light.LightActivity;
import com.jzzh.setting.lock.LockScreenActivity;
import com.jzzh.setting.network.NetworkActivity;
import com.jzzh.setting.power.PowerActivity;
import com.jzzh.setting.task.TaskManagerActivity;
import com.jzzh.setting.time.DateAndTimeActivity;

public class Setting extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.setting_network).setOnClickListener(this);
        findViewById(R.id.setting_power).setOnClickListener(this);
        findViewById(R.id.setting_display).setOnClickListener(this);
        findViewById(R.id.setting_light).setOnClickListener(this);
        findViewById(R.id.setting_language_and_keyboard).setOnClickListener(this);
        findViewById(R.id.setting_date_time).setOnClickListener(this);
        findViewById(R.id.setting_lock_screen).setOnClickListener(this);
        findViewById(R.id.setting_task_manager).setOnClickListener(this);
        findViewById(R.id.setting_device).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_network:
                startActivity(NetworkActivity.class);
                break;
            case R.id.setting_power:
                startActivity(PowerActivity.class);
                break;
            case R.id.setting_display:
                startActivity(DisplayActivity.class);
                break;
            case R.id.setting_light:
                startActivity(LightActivity.class);
                break;
            case R.id.setting_language_and_keyboard:
                startActivity(LanguageAndKeyboardActivity.class);
                break;
            case R.id.setting_date_time:
                startActivity(DateAndTimeActivity.class);
                break;
            case R.id.setting_lock_screen:
                startActivity(LockScreenActivity.class);
                break;
            case R.id.setting_task_manager:
                startActivity(TaskManagerActivity.class);
                break;
            case R.id.setting_device:
                startActivity(DeviceActivity.class);
                break;
        }
    }
}