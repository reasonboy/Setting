package com.jzzh.setting.network.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiUtils {

    private WifiManager mWifiManager;

    public WifiUtils(WifiManager wifiManager) {
        this.mWifiManager = wifiManager;
    }


    /**
     * 连接wifi
     * @param targetSsid wifi的SSID
     * @param targetPsd 密码
     * @param enc 加密类型
     */
    public void connectWifi(String targetSsid, String targetPsd, String enc) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        mWifiManager.addNetwork(conf);
        List<WifiConfiguration> list = null;
        try {
            list = mWifiManager.getConfiguredNetworks();
        } catch (SecurityException e) {

        }
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(i.networkId, true);
                mWifiManager.reconnect();
                break;
            }
        }
    }

    public void removeWifiBySsid(String wifiName) {
        List<WifiConfiguration> list = null;
        try {
            list = mWifiManager.getConfiguredNetworks();
        } catch (SecurityException e) {

        }
        for(int i=0;i<list.size();i++) {
            if(list.get(i).SSID.equals("\"" + wifiName + "\"")) {
                mWifiManager.removeNetwork(list.get(i).networkId);
                mWifiManager.saveConfiguration();
            }
        }
    }
}
