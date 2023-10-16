package com.jzzh.setting.power;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class PowerActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        findViewById(R.id.setting_power_auto_sleep).setOnClickListener(this);
        findViewById(R.id.setting_power_auto_power_off).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_power_auto_sleep:
                startActivity(AutoSleepActivity.class);
                break;
            case R.id.setting_power_auto_power_off:
                startActivity(AutoPowerOffActivity.class);
                break;
        }
    }
}
