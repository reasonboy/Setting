package com.jzzh.network.wifi;

import android.annotation.SuppressLint;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class WifiUtils {
    /**
     * No metered override.
     * @hide
     */
    public static final int METERED_OVERRIDE_NONE = 0;
    /**
     * Override network to be metered.
     * @hide
     */
    public static final int METERED_OVERRIDE_METERED = 1;
    /**
     * Override network to be unmetered.
     * @hide
     */
    public static final int METERED_OVERRIDE_NOT_METERED = 2;


    private WifiManager mWifiManager;
    private static final String TAG = WifiUtils.class.getSimpleName();

    public WifiUtils(WifiManager wifiManager) {
        this.mWifiManager = wifiManager;
    }


    /**
     * 连接wifi
     * @param targetSsid wifi的SSID
     * @param targetPsd 密码
     * @param enc 加密类型
     */
    public void connectWifi(String targetSsid, String targetPsd, String enc, int meteredType,String ipAssignment,String[] ipSettingsData,String proxySettings,ProxyInfo proxyInfo) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        setMeteredOverride(conf,meteredType); // 测试metered
        if(!ipAssignment.isEmpty()){
            setIpAssignment(conf,ipAssignment);
            if(ipAssignment.equals("STATIC")){
                Object staticIpConfiguration = buildStaticIpConfiguration(ipSettingsData[0], ipSettingsData[1], ipSettingsData[2], ipSettingsData[3]);
                setStaticIpConfiguration(conf,staticIpConfiguration);
            }
        }
        if (!proxySettings.equals("NONE") && proxyInfo != null) {  // set proxy
            Log.e(TAG, "proxySettings:" + proxySettings);
            if(proxySettings.equals("PAC")){
                Log.e(TAG, "PAC uri:" + proxyInfo.getPacFileUrl().toString());
            }else if(proxySettings.equals("STATIC")){
                Log.e(TAG,"host:"+proxyInfo.getHost()+",port:"+proxyInfo.getPort()+",execList:"+proxyInfo.getExclusionList().toString());
            }
            setProxySettings(conf,proxySettings);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                conf.setHttpProxy(proxyInfo);
            }
        }
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

    private void setMeteredOverride(WifiConfiguration configuration, int meteredOverrideValue) {
        try {
            Class clzWifiConfiguration = configuration.getClass();
            java.lang.reflect.Field meteredOverride = clzWifiConfiguration.getField("meteredOverride");
            meteredOverride.setInt(configuration, meteredOverrideValue);
        } catch (Exception e) {

        }
    }

    private void setIpAssignment(WifiConfiguration configuration, String enumValue) {
        try {
            Class clazz = Class.forName("android.net.IpConfiguration$IpAssignment");
            Object param = Enum.valueOf(clazz, enumValue);
            Class clzWifiConfiguration = configuration.getClass();
            java.lang.reflect.Method setIpAssignment = clzWifiConfiguration.getDeclaredMethod("setIpAssignment",clazz);
            setIpAssignment.invoke(configuration, param);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setProxySettings(WifiConfiguration configuration,String enumValue){
        try {
            Class clazz = Class.forName("android.net.IpConfiguration$ProxySettings");
            Object param = Enum.valueOf(clazz, enumValue);
            Class clzWifiConfiguration = configuration.getClass();
            java.lang.reflect.Method setIpAssignment = clzWifiConfiguration.getDeclaredMethod("setProxySettings",clazz);
            setIpAssignment.invoke(configuration, param);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setHttpProxy(WifiConfiguration configuration, ProxyInfo proxyInfo) {
        Class clzWifiConfiguration = configuration.getClass();
        try {
            java.lang.reflect.Method setHttpProxy = clzWifiConfiguration.getDeclaredMethod("setHttpProxy", proxyInfo.getClass());
            setHttpProxy.invoke(configuration, proxyInfo);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setStaticIpConfiguration(WifiConfiguration configuration, Object staticIpConfiguration) {
        Class clzWifiConfiguration = configuration.getClass();
        try {
            java.lang.reflect.Method setStaticIpConfiguration = clzWifiConfiguration.getDeclaredMethod("setStaticIpConfiguration",staticIpConfiguration.getClass());
            setStaticIpConfiguration.invoke(configuration, staticIpConfiguration);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置静态ip配置
     *
     * @param address ip地址
     * @param mask    子网掩码
     * @param gate    门关
     * @param dns     dns
     * @return {@link Object}
     */
    public Object buildStaticIpConfiguration(String address, String mask, String gate, String dns) {
        try {
            Log.d(TAG, "setStaticIpConfiguration: address = " + address + " netmask = " + mask + " gateway = " + gate + " dns = " + dns);
            @SuppressLint("PrivateApi")
            Class<?> staticIpConfigurationClz = Class.forName("android.net.StaticIpConfiguration");
            Object staticIpConfiguration = staticIpConfigurationClz.newInstance();
            Field ipAddress = staticIpConfigurationClz.getField("ipAddress");
            Field netmask = staticIpConfigurationClz.getField("domains");
            Field gateway = staticIpConfigurationClz.getField("gateway");
            Field dnsServers = staticIpConfigurationClz.getField("dnsServers");
            Log.e(TAG, "prefixLength:" + getPrefixLength(mask));
            ipAddress.set(staticIpConfiguration, getLinkAddress(InetAddress.getByName(address), getPrefixLength(mask)));
            netmask.set(staticIpConfiguration, mask);
            gateway.set(staticIpConfiguration, InetAddress.getByName(gate));
            ArrayList<InetAddress> dnsList = (ArrayList<InetAddress>) dnsServers.get(staticIpConfiguration);
            dnsList.add(InetAddress.getByName(dns));
            return staticIpConfiguration;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException |
                InstantiationException | UnknownHostException e) {
            Log.d(TAG, "setStaticIpConfiguration: " + e);
            return null;
        }
    }

    /**
     * 得到链接地址
     *
     * @param address 地址
     * @param length  长度
     * @return {@link LinkAddress}
     */
    public LinkAddress getLinkAddress(InetAddress address, int length) {
        try {
            Class<?> linkAddressClz = Class.forName("android.net.LinkAddress");
            Constructor<?> linkAddressCsr = linkAddressClz.getConstructor(InetAddress.class, int.class);
            return (LinkAddress) linkAddressCsr.newInstance(address, length);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取长度
     */
    private static int getPrefixLength(String mask) {
        String[] strs = mask.split("\\.");
        int count = 0;
        for (String str : strs) {
            if (str.equals("255")) {
                ++count;
            }
        }
        return count * 8;
    }
}
