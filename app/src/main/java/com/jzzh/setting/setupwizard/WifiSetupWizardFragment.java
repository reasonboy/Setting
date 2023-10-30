package com.jzzh.setting.setupwizard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inno.spacesetupwizardlib.SetupWizardFragment;
import com.jzzh.setting.R;

public class WifiSetupWizardFragment extends SetupWizardFragment {
    public static WifiSetupWizardFragment newInstance() {
        return new WifiSetupWizardFragment();
    }

    private IntentFilter mIntentFilter;
    private WifiManager mWifiManager;
    private Context mContext;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                onConnected(isWifiConnected(mContext));
            }
        }
    };

    public WifiSetupWizardFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mContext = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_setupwizard, container, false);

        /*  add settings's wifi page
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.wizard_wifi_list, new WifiFragment())
                .commit();
        */

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        setTitle(R.string.setup_wifi);
        setButtonMode(ButtonMode.Skip);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        mWifiManager.setWifiEnabled(true);

        getActivity().registerReceiver(broadcastReceiver, mIntentFilter);

        onConnected(isWifiConnected(mContext));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onSkipRequested() {
        // dialog ...etc
        super.onSkipRequested();
    }

    public boolean isWifiConnected(Context context) {
        boolean result = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            result = cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getSsid() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        return info.getSSID();
    }

    private void onConnected(boolean connected) {
        if (connected) {
            Intent intent = new Intent();
            intent.putExtra("ssid", getSsid());
            setResultIntent(intent);
        } else {
            setResultIntent(null);
        }
        setButtonMode(connected ? ButtonMode.Next : ButtonMode.Skip);
    }
}
