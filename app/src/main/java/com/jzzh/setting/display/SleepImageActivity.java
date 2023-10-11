package com.jzzh.setting.display;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class SleepImageActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_si);
        findViewById(R.id.setting_display_si_default).setOnClickListener(this);
        findViewById(R.id.setting_display_si_user).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_display_si_default:
                startActivity(SleepImageDefaultActivity.class);
                break;
            case R.id.setting_display_si_user:
                startActivity(SleepImageUserActivity.class);
                break;
        }
    }
}
