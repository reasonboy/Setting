package com.jzzh.setting.keygesture;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class KeyGestureActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture);
        findViewById(R.id.setting_keygesture_gesture).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_keygesture_gesture:
                startActivity(GestureSettingActivity.class);
                break;
        }
    }
}
