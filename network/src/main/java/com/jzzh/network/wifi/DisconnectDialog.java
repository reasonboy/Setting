package com.jzzh.network.wifi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.jzzh.network.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

public class DisconnectDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private String mWifiName;
    private TextView mWifiNameTx;
    private Button mCancle,mConnect;
    private DialogCallback mCallback;
    private int mSignalLevel;
    private String mSecurity;
    private static final String TAG=DisconnectDialog.class.getSimpleName();

    public DisconnectDialog(Context context, int style, DialogCallback callback, String wifiName,int signalLevel,String security) {
        super(context, style);
        mContext=context;
        mCallback = callback;
        mWifiName = wifiName;
        mSignalLevel = signalLevel;
        mSecurity = security;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_disconnect_dialog);
        mWifiNameTx = findViewById(R.id.wifi_disconnect_dialog_name);
        mWifiNameTx.setText(mWifiName);
        mCancle = findViewById(R.id.wifi_disconnect_dialog_cancle);
        mCancle.setOnClickListener(this);
        mConnect = findViewById(R.id.wifi_disconnect_dialog_disconnect);
        mConnect.setOnClickListener(this);
        showConnectInfo();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wifi_disconnect_dialog_cancle) {
            dismiss();
        } else if (id == R.id.wifi_disconnect_dialog_disconnect) {
            mCallback.callBackData(new String[]{mWifiName});
            dismiss();
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showConnectInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        LinkProperties mLinkProperties = mConnectivityManager.getLinkProperties(mConnectivityManager.getActiveNetwork());
        // Find IPv4 and IPv6 addresses.
        String ipv4Address = "";
        String subnet = "";
        String dnsServers = "";
        // Find IPv4 default gateway.
        String gateway = "";

        if (mLinkProperties != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (LinkAddress addr : mLinkProperties.getLinkAddresses()) {
                    if (addr.getAddress() instanceof Inet4Address) {
                        ipv4Address = addr.getAddress().getHostAddress();
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (RouteInfo routeInfo : mLinkProperties.getRoutes()) {
                    if (routeInfo.hasGateway()) {
                        gateway = routeInfo.getGateway().getHostAddress();
                        break;
                    }
                }
            }

            // Find all (IPv4 and IPv6) DNS addresses.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dnsServers = mLinkProperties.getDnsServers().stream()
                        .map(InetAddress::getHostAddress)
                        .collect(Collectors.joining("\n"));
            }
        }

        String band = "";
        int linkSpeed = 0;
        String macAddress = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            int frequency = mWifiManager.getConnectionInfo().getFrequency();
            macAddress = mWifiManager.getConnectionInfo().getMacAddress();
            linkSpeed = mWifiManager.getConnectionInfo().getLinkSpeed();
            if (frequency >= 2400
                    && frequency < 2500) {
                band = mContext.getResources().getString(R.string.wifi_band_24ghz);
            } else if (frequency >= 4900
                    && frequency < 5900) {
                band = mContext.getResources().getString(R.string.wifi_band_5ghz);
            } else {
                Log.e(TAG, "Unexpected frequency " + frequency);
            }
        }

        Log.e(TAG, "signalLevel:" + mSignalLevel + "ipv4:" + ipv4Address + " subnet:" + subnet + " gateway:" + gateway + " DNS:" + dnsServers + " band:" + band);
        TextView signalStrengthTv = findViewById(R.id.tv_signal_strength);
        String signalStrength = getSignalStrengthByLevel(mSignalLevel);
        signalStrengthTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_signal_strength), signalStrength));
        TextView bandTv = findViewById(R.id.tv_frequency);
        bandTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_frequency), band));
        TextView ipTv = findViewById(R.id.tv_ip_address);
        ipTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_ip_address), ipv4Address));
        TextView gatewayTv = findViewById(R.id.tv_gateway);
        gatewayTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_gateway), gateway));
        TextView subnetMaskTv = findViewById(R.id.tv_subnet_mask);
        subnetMaskTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_subnet_mask), subnet));
        TextView dnsTv = findViewById(R.id.tv_dns);
        dnsTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_dns), dnsServers));
        TextView statusInfoTv = findViewById(R.id.tv_status_info);
        statusInfoTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_status_info), "Connected"));
        TextView connectingSpeed = findViewById(R.id.tv_connecting_speed);
        connectingSpeed.setText(String.format("%s: %s%s", mContext.getString(R.string.wifi_detail_connecting_speed), linkSpeed, "Mbps"));
        TextView macAddressTv = findViewById(R.id.tv_mac_address);
        macAddressTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_mac_address), macAddress));
        TextView securityTv = findViewById(R.id.tv_security);
        securityTv.setText(String.format("%s:%s", mContext.getString(R.string.wifi_detail_security), mSecurity));
    }

    /*
    private static String ipv4PrefixLengthToSubnetMask(int prefixLength) {
        try {
            InetAddress all = InetAddress.getByAddress(
                    new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255});
            return NetworkUtils.getNetworkPart(all, prefixLength).getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }*/

    private String getSignalStrengthByLevel(int level) {
        if (level <= 1) {
            return mContext.getString(R.string.wifi_signal_strength_1);
        } else if (level == 2) {
            return mContext.getString(R.string.wifi_signal_strength_2);
        } else if (level == 3) {
            return mContext.getString(R.string.wifi_signal_strength_3);
        } else {
            return mContext.getString(R.string.wifi_signal_strength_4);
        }
    }

    public interface DialogCallback {
        void callBackData(String[] data);
    }
}
