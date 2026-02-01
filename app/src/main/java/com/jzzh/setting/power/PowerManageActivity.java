package com.jzzh.setting.power;

import android.os.Bundle;
import android.provider.Settings;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

public class PowerManageActivity extends BaseActivity {

    private ZhCheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_manage);
        mCheckBox = findViewById(R.id.setting_power_manage_title_cb);
        mCheckBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setHallEnable(checked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCheckBox.setCheck(getHallEnable());
    }

    private void setHallEnable(boolean enable) {
        int value = enable ? 0 : 1;
        Settings.System.putInt(getContentResolver(),"hall_enable", value);
    }

    private boolean getHallEnable() {
        int value = Settings.System.getInt(getContentResolver(),"hall_enable", 1);
        boolean enable = value == 0 ? true : false;
        return enable;
    }
}
