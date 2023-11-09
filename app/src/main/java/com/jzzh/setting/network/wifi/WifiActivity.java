package com.jzzh.setting.network.wifi;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.network.wifi.WifiFragment;
import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;


public class WifiActivity extends BaseActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi);

        WifiFragment wifiFragment = new WifiFragment();
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, wifiFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);  // 去除动画
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
