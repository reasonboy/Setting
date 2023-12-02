package com.jzzh.setting.device;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class FactoryResetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory_reset);
        findViewById(R.id.factory_reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FactoryResetDialog(FactoryResetActivity.this, R.style.ZhDialog).show();
            }
        });
    }
}
