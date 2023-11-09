package com.jzzh.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.network.wifi.WifiFragment;

public class MainActivity extends Activity {

    private WifiFragment mWifiFragment;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiFragment = new WifiFragment();
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, mWifiFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);  // 去除动画
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWifiFragment.showTitleLayoutExit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}