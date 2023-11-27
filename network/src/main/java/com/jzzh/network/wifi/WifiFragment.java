package com.jzzh.network.wifi;

import static com.jzzh.network.wifi.WifiUtils.METERED_OVERRIDE_NONE;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jzzh.network.R;
import com.jzzh.network.NetTitleLayout;
import com.jzzh.tools.PageIndication;
import com.jzzh.tools.ZhCheckBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class WifiFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private WifiManager mWifiManager;
    private WifiUtils mWifiUtils;
    private WifiBroadcaster mWifiBroadcaster;
    private List<ScanResult> mResultList = new ArrayList<ScanResult>();
    private NetTitleLayout mNetTitleLayout;
    private ZhCheckBox mWifiSwitch;
    private ImageView mWifiAdd;
    private ListView mWifiListView;
    private View mWifiContent;
    private TextView mWifiTips;
    private WifiAdapter mWifiAdapter;
    private WifiHandler mHandler;
    private PageIndication mPageIndication;
    private int mCurPage = 1,mTotalPage,mListViewItemNum = 10;
    private String mCurConnectSSID;
    private static final int SCAN_INTERVAL = 20000; //每10秒扫描一次
    private static final int START_SCAN = 0;
    private static final int SCAN_COMPLETE = 1;
    private boolean CLOSE_OPEN_FLAG;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_fragment,null);
        mContext = getContext();
        mWifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiUtils = new WifiUtils(mWifiManager);
        mWifiBroadcaster = new WifiBroadcaster();
        mHandler = new WifiHandler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mWifiBroadcaster,intentFilter);
        mNetTitleLayout = view.findViewById(R.id.title_layout);
        ImageView exit = view.findViewById(R.id.title_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        mWifiAdd = view.findViewById(R.id.title_function_2);
        mWifiAdd.setOnClickListener(this);
        mWifiSwitch = view.findViewById(R.id.title_switch);
        mWifiSwitch.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                mWifiManager.setWifiEnabled(checked);
                showWifiContent(false);
                if(checked) {
                    CLOSE_OPEN_FLAG = true;
                    mHandler.sendEmptyMessage(START_SCAN);
                    mWifiTips.setText(getString(R.string.wifi_opening));
                } else {
                    mHandler.removeMessages(START_SCAN);
                    mWifiTips.setText(getString(R.string.wifi_closing));
                }
            }
        });
        ImageView wifiRefresh = view.findViewById(R.id.title_function_1);
        wifiRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiManager.isWifiEnabled()) {
                    mHandler.sendEmptyMessage(START_SCAN);
                }
            }
        });
        mWifiSwitch.setCheck(mWifiManager.isWifiEnabled());
        mWifiContent = view.findViewById(R.id.wifi_content);
        mWifiTips = view.findViewById(R.id.wifi_tips);
        mWifiListView = view.findViewById(R.id.wifi_listview);
        mWifiAdapter = new WifiAdapter(mContext);
        mWifiListView.setAdapter(mWifiAdapter);
        mWifiListView.setOnItemClickListener(mOnItemClickListener);
        mPageIndication = view.findViewById(R.id.page_indication);
        mPageIndication.setOnPageChangeListener(new PageIndication.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page) {
                mCurPage = page;
                updateList();
            }
        });
        showWifiContent(false);
        if(mWifiManager.isWifiEnabled()) {
            mHandler.sendEmptyMessage(START_SCAN);
            mWifiTips.setText(getString(R.string.wifi_scanning));
        } else {
            mHandler.removeMessages(START_SCAN);
            mWifiTips.setText(getString(R.string.wifi_open_tips));
        }
        return view;
    }

    public void showTitleLayoutExit() {
        mNetTitleLayout.showExit();
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final String name = mWifiAdapter.getItem(i).SSID;
            final String address = mWifiAdapter.getItem(i).BSSID;
            String capabilities = mWifiAdapter.getItem(i).capabilities;
            String enc = getEncryption(capabilities);
            int signalLevel = mWifiAdapter.getSignalLevel(i);
            Log.e("lx","name:"+name+" "+address+" is clicked");
            if (!name.equals(mCurConnectSSID)) {
                if (enc.equals("OPEN") && !isWifiSaved(name)) {
                    mWifiUtils.connectWifi(name, "", "OPEN", METERED_OVERRIDE_NONE, "", null);
                } else {
                    if (isWifiSaved(name)) {
//                        Log.e(TAG, "已保存的无线网络");
                        new ConnectDialog(mContext, R.style.ZhDialog, new ConnectDialog.DialogCallback() {
                            @Override
                            public void callBackData(String[] data, String key, int meteredType,String ipAssignment,String[] ipSettingsData) {
                                if ("connect".equals(key)) {
                                    WifiConfiguration wifiConfig = new WifiConfiguration();
                                    wifiConfig.SSID = "\"" + name + "\"";
                                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                    int networkId = mWifiManager.addNetwork(wifiConfig);
                                    mWifiManager.enableNetwork(networkId, true);
                                    mWifiManager.reconnect();
                                } else if ("delete".equals(key)) {
                                    mWifiUtils.removeWifiBySsid(name);
                                    mHandler.sendEmptyMessage(START_SCAN);
                                }
                            }
                        }, name, enc, true, signalLevel).show();
                    } else {
                        new ConnectDialog(mContext, R.style.ZhDialog, new ConnectDialog.DialogCallback() {
                            @Override
                            public void callBackData(String[] data, String key, int meteredType,String ipAssignment,String[] ipSettingsData) {
                                mWifiUtils.connectWifi(data[0], data[1], data[2], meteredType, ipAssignment, ipSettingsData);
                            }
                        }, name, enc, false, signalLevel).show();
                    }
                }
            } else {
                new DisconnectDialog(mContext, R.style.ZhDialog, new DisconnectDialog.DialogCallback() {
                    @Override
                    public void callBackData(String[] data) {
                        mWifiUtils.removeWifiBySsid(data[0]);
                    }
                }, name, signalLevel, enc).show();
            }
        }
    };

    private boolean isWifiSaved(String wifiName) {
        List<WifiConfiguration> list = new ArrayList<>();
        try {
            list = mWifiManager.getConfiguredNetworks();
        } catch (SecurityException e) {

        }
        for (int i = 0; i < list.size(); i++) {
            if (wifiName.equals(list.get(i).SSID.replace("\"", ""))) {
                return true;
            }
        }
        return false;
    }

    private WifiConfiguration getWifiConfigurationByName(String wifiName) {
        List<WifiConfiguration> list = new ArrayList<>();
        WifiConfiguration wifiConfiguration = null;
        try {
            list = mWifiManager.getConfiguredNetworks();
        } catch (SecurityException e) {

        }
        for (int i = 0; i < list.size(); i++) {
            if (wifiName.equals(list.get(i).SSID.replace("\"", ""))) {
                wifiConfiguration = list.get(i);
            }
        }
        return wifiConfiguration;
    }

    private String getEncryption(String capabilities) {
        String result = "";
        if(capabilities.contains("WPA")) {
            result =  "WPA";
        } else if (capabilities.contains("WEP")) {
            result =  "WEP";
        } else {
            result =  "OPEN";
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mWifiManager.isWifiEnabled()) {
            mHandler.sendEmptyMessage(START_SCAN);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(START_SCAN);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_function_2) {
            if (mWifiManager.isWifiEnabled()) {
                new AddDialog(mContext, R.style.ZhDialog, new AddDialog.DialogCallback() {
                    @Override
                    public void callBackData(String[] data) {
                            mWifiUtils.connectWifi(data[0], data[1], data[2], METERED_OVERRIDE_NONE, "", null);
                    }
                }).show();
            }
        }
    }

    private class WifiBroadcaster extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("xml_log_app","action = " + action);
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                boolean result = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,false);//扫描是否成功
                //Log.v("xml_log_app","scan result = " + result);
                if (result) {
                    synchronized (this) {
                        mResultList = mWifiManager.getScanResults();
                        mCurConnectSSID = getCurConnectSSID();
                        optimizationList();
                        mHandler.sendEmptyMessage(SCAN_COMPLETE);
                    }
                }
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);//wifi状态
                Log.v("xml_log_app","wifiState = " + wifiState);
                if(wifiState == WifiManager.WIFI_STATE_DISABLED) {
                    if(mWifiSwitch.getCheck()){ // 若此时Wifi开关按钮为打开状态，将其关闭
                        mWifiSwitch.setCheck(false);
                        // Wifi关闭处理逻辑
                        showWifiContent(false);
                        mHandler.removeMessages(START_SCAN);
                        mWifiTips.setText(getString(R.string.wifi_closing));
                    }
                    mWifiTips.setText(getString(R.string.wifi_open_tips));
                }else if(wifiState== WifiManager.WIFI_STATE_ENABLED){
                    if(!mWifiSwitch.getCheck()){ // 若此时Wifi开关按钮为关闭状态，将其打开
                        mWifiSwitch.setCheck(true);
                        // Wifi打开处理逻辑
                        CLOSE_OPEN_FLAG = true;
                        mHandler.sendEmptyMessage(START_SCAN);
                        mWifiTips.setText(getString(R.string.wifi_opening));
                    }
                }
            } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {//热点连接状态
                SupplicantState linkState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,0);
                int errorReason = 0;//intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR_REASON,WifiManager.ERROR_AUTH_FAILURE_NONE);
                Log.v("xml_log_app","linkState = " + linkState + " ;errorCode = " + errorCode + " ;errorReason = " + errorReason);
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            }
        }
    }

    /**
     * 获取已连接WiFi的SSID
     */
    private String getCurConnectSSID() {
        String target = mWifiManager.getConnectionInfo().getSSID().replace("\"", "");
        String result = "";

        if (target == null) {
            result = "";
        } else {
            result = target;
        }
        return result;
    }

    /**
     * 优化扫描列表，把已经连接的放在头部，SSID为空的去掉
     **/
    private void optimizationList() {
        List<ScanResult> nullResultList = new ArrayList<ScanResult>();
        for(int i=0;i<mResultList.size();i++) {
            if(mResultList.get(i).SSID.toString().trim().equals("") || mResultList.get(i).SSID == null) {
                nullResultList.add(mResultList.get(i));
            }
        }
        for (int i = 0; i < nullResultList.size(); i++) {
            mResultList.remove(nullResultList.get(i));
        }
        mResultList = filterScanResult(mResultList);
    }

    /**
     * 以 SSID 为关键字，过滤掉信号弱的选项，去重（去除相同SSID项）
     *
     * @param list
     * @return
     */
    public List<ScanResult> filterScanResult(List<ScanResult> list) {
        List<ScanResult> set = new ArrayList<>();
        LinkedHashMap<String, ScanResult> linkedMap = new LinkedHashMap<>(list.size());
        for (ScanResult rst : list) {
            if (linkedMap.containsKey(rst.SSID)) {
                if (rst.level > Objects.requireNonNull(linkedMap.get(rst.SSID)).level) {
                    linkedMap.put(rst.SSID, rst);
                }
                continue;
            }
            linkedMap.put(rst.SSID, rst);
        }
        List<ScanResult> newList = new ArrayList<>(linkedMap.values());
        for (ScanResult wifi : newList) { // 将已连接WiFi置于首位
            if (wifi.SSID.equals(mCurConnectSSID))
                set.add(0, wifi);
            else
                set.add(wifi);
        }
        return set;
    }

    private class WifiHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case START_SCAN:
                    Log.v("xml_log_app","START_SCAN");
                    mHandler.removeMessages(START_SCAN);
                    mWifiManager.startScan();
                    mHandler.sendEmptyMessageDelayed(START_SCAN, SCAN_INTERVAL);
                    break;
                case SCAN_COMPLETE:
                    updateList();
                    showWifiContent(true);
                    if (CLOSE_OPEN_FLAG) {
                        CLOSE_OPEN_FLAG = false;
                        mHandler.sendEmptyMessageDelayed(START_SCAN,200);
                    }
                    break;
            }
        }
    }

    /**
     * 刷新ListView，以及页码
     * */
    private void updateList() {
        mWifiAdapter.setDate(getSubScanResult(mResultList));
        mWifiAdapter.notifyDataSetChanged();
        mPageIndication.init(mCurPage,mTotalPage);
    }

    /**
     * 把扫描到的ScanResult以mListViewItemNum的长度截取成一段段传给ListView来显示
     * */
    private List getSubScanResult(List<ScanResult> parent) {
        if(parent.size() == 0) return null;
        //Log.v("xml_log_app","parent.size() = " + parent.size());
        List<ScanResult> list = new ArrayList<ScanResult>();
        mTotalPage = parent.size() / mListViewItemNum;
        if(parent.size() % mListViewItemNum != 0) {
            mTotalPage++;
        }
        if(mCurPage > mTotalPage ) {
            mCurPage = mTotalPage ;
        }
        //Log.v("xml_log_app","mTotalPage = " + mTotalPage);
        for(int i = 0;i < parent.size();i++) {
            if(i >= (mCurPage - 1) * mListViewItemNum && i < mCurPage * mListViewItemNum) {
                list.add(parent.get(i));
            }
        }
        return list;
    }

    private void showWifiContent(boolean show) {
        if(show) {
            mWifiContent.setVisibility(View.VISIBLE);
            mWifiTips.setVisibility(View.GONE);
        } else {
            mWifiContent.setVisibility(View.GONE);
            mWifiTips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mWifiBroadcaster);
    }

    private class WifiAdapter extends BaseAdapter {

        private List<ScanResult> mResultList = new ArrayList<ScanResult>();
        private Context mContext;
        private static final int MIN_RSSI = -100;
        private static final int MAX_RSSI = -55;

        public WifiAdapter(Context context) {
            mContext = context;
        }

        public void setDate(List list) {
            mResultList = list;
        }

        @Override
        public int getCount() {
            return mResultList.size();
        }

        @Override
        public ScanResult getItem(int i) {
            return mResultList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.wifi_item, viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.wifiSignal = view.findViewById(R.id.wifi_signal);
                viewHolder.wifiName = view.findViewById(R.id.wifi_name);
                viewHolder.wifiMsg = view.findViewById(R.id.wifi_msg);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)view.getTag();
            }
            viewHolder.wifiName.setText(mResultList.get(i).SSID);
            String describe = getDescribe(mResultList.get(i));
            Log.d("lx","describe:"+describe);
            boolean lock;
            if(describe.contains("WPA")) {
                lock = true;
            } else {
                lock = false;
            }
            viewHolder.wifiSignal.setImageResource(getImageResourceByLevel(getSignalLevel(i),lock)); //  5个等级01234

            String connectState = getConnectState(mResultList.get(i));
            if(!connectState.equals("")) {
                viewHolder.wifiMsg.setVisibility(View.VISIBLE);
                viewHolder.wifiMsg.setText(connectState);
            } else {
                viewHolder.wifiMsg.setVisibility(View.GONE);
            }

            return view;
        }

        private String getConnectState(ScanResult scanResult) {
            String wifiName = scanResult.SSID;
            String describe_start = "";
            if (wifiName.equals(mCurConnectSSID)) {
                describe_start = mContext.getString(R.string.wifi_connect);
            } else if (isWifiSaved(wifiName)) {
                describe_start = mContext.getString(R.string.wifi_saved);
            }
            return describe_start;
        }

        private String getDescribe(ScanResult scanResult) {
            String wifiName = scanResult.SSID;
            String address = scanResult.BSSID;
            String capabilities = scanResult.capabilities;
            String psk = "";
            String wps = "";
            String describe = "";
            String describe_start = "";
            if(capabilities.contains("WPA-")) {
                psk += "WPA";
            }
            if(capabilities.contains("WPA2-")) {
                if(psk.equals("")) {
                    psk += "WPA2";
                } else {
                    psk += "/WPA2";
                }
            }
            if(capabilities.contains("[WPS]")) {
                wps += "WPA";
            }
            if(!psk.equals("")) {
                if(!wps.equals("")) {
                    describe = mContext.getString(R.string.wifi_capabilities_psk, psk) +
                            "(" + mContext.getString(R.string.wifi_capabilities_wps) + ")";
                } else {
                    describe = mContext.getString(R.string.wifi_capabilities_psk, psk);
                }
            } else {
                if (!wps.equals("")) {
                    describe = mContext.getString(R.string.wifi_capabilities_wps);
                } else {
                    describe = mContext.getString(R.string.wifi_capabilities_open);
                }
            }
            if (!describe_start.equals("")) {
                describe = describe_start + "、" + describe;
            }
            return describe;
        }

        private int calculateSignalLevel(int rssi, int numLevels) {
            if (rssi <= MIN_RSSI) {
                return 0;
            } else if (rssi >= MAX_RSSI) {
                return numLevels - 1;
            } else {
                float inputRange = (MAX_RSSI - MIN_RSSI);
                float outputRange = (numLevels - 1);
                return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
            }
        }

        int getSignalLevel(int i) {
            return calculateSignalLevel(mResultList.get(i).level, 5);
        }

        private int getImageResourceByLevel(int level,boolean lock) {
            int res = 0;
            switch (level) {
                case 0:
                    if(lock) {
                        res = R.drawable.setting_wifi_lock_0;
                    } else {
                        res = R.drawable.setting_wifi_0;
                    }
                    break;
                case 1:
                case 2:
                    if(lock) {
                        res = R.drawable.setting_wifi_lock_s;
                    } else {
                        res = R.drawable.setting_wifi_s;
                    }
                case 3:
                    if(lock) {
                        res = R.drawable.setting_wifi_lock_m;
                    } else {
                        res = R.drawable.setting_wifi_m;
                    }
                    break;
                case 4:
                    if(lock) {
                        res = R.drawable.setting_wifi_lock_full;
                    } else {
                        res = R.drawable.setting_wifi_full;
                    }
                    break;
            }
            return res;
        }
    }

    private class ViewHolder {
        ImageView wifiConnectTag;
        ImageView wifiSignal;
        ImageView wifiLock;
        TextView wifiName;
        TextView wifiMsg;
    }
}
