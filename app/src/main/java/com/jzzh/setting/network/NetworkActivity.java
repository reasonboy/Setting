package com.jzzh.setting.network;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.network.bt.BluetoothActivity;
import com.jzzh.setting.network.wifi.WifiActivity;

public class NetworkActivity extends BaseActivity  implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        findViewById(R.id.setting_network_wifi).setOnClickListener(this);
        findViewById(R.id.setting_network_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_network_wifi:
                startActivity(WifiActivity.class);
                break;
            case R.id.setting_network_bt:
                startActivity(BluetoothActivity.class);
                break;
        }
    }
}
