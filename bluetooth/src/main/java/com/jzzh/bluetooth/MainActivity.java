package com.jzzh.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.jzzh.network.bt.BluetoothFragment;

public class MainActivity extends Activity {

    private BluetoothFragment mBluetoothFragment;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothFragment = new BluetoothFragment();
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, mBluetoothFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);  // 去除动画
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothFragment.showTitleLayoutExit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}