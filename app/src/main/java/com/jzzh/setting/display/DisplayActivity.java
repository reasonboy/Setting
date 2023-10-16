package com.jzzh.setting.display;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class DisplayActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        findViewById(R.id.setting_display_refresh).setOnClickListener(this);
        findViewById(R.id.setting_display_poi).setOnClickListener(this);
        findViewById(R.id.setting_display_si).setOnClickListener(this);
        findViewById(R.id.setting_display_home_screen).setOnClickListener(this);
        findViewById(R.id.setting_display_widget_text).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_display_refresh:
                startActivity(RefreshActivity.class);
                break;
            case R.id.setting_display_poi:
                startActivity(PowerOffImageActivity.class);
                break;
            case R.id.setting_display_si:
                startActivity(SleepImageActivity.class);
                break;
            case R.id.setting_display_home_screen:
                startActivity(HomeScreenUserActivity.class);
                break;
            case R.id.setting_display_widget_text:
                startActivity(WidgetTextActivity.class);
                break;

        }
    }
}
