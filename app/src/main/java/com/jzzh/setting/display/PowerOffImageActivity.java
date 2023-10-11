package com.jzzh.setting.display;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class PowerOffImageActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_poi);
        findViewById(R.id.setting_display_poi_default).setOnClickListener(this);
        findViewById(R.id.setting_display_poi_user).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_display_poi_default:
                startActivity(PowerOffImageDefaultActivity.class);
                break;
            case R.id.setting_display_poi_user:
                startActivity(PowerOffImageUserActivity.class);
                break;
        }
    }
}
