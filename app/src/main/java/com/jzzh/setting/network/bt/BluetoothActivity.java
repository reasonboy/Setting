package com.jzzh.setting.network.bt;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.network.wifi.WifiFragment;

public class BluetoothActivity extends BaseActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bt);

        BluetoothFragment bluetoothFragment = new BluetoothFragment();
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, bluetoothFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);  // 去除动画
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
