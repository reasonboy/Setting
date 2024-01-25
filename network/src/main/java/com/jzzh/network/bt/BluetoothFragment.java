package com.jzzh.network.bt;

import static com.jzzh.network.bt.BtUtils.getAlias;

import android.app.Fragment;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAssignedNumbers;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jzzh.network.R;
import com.jzzh.network.NetTitleLayout;
import com.jzzh.tools.ZhCheckBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private ZhCheckBox mBtSwitch;
    private TextView mDeviceName,mDeviceAddress;
    private ListView mPairedListView,mAvailableListView;
    private BluetoothDevicesAdapter mPairedAdapter,mAvailableAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mAvailableDevices = new ArrayList<>();
    private BtBroadcaster mBroadcaster;
    private TextView mInSearch;
    private ImageView mIvRefresh;
    private NetTitleLayout mNetTitleLayout;
    private static final int BT_LISTVIEW_AVAILABLE = 0;
    private static final int BT_LISTVIEW_PAIRED = 1;
    private BluetoothA2dp mA2dp;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buletooth_fragment, null);
        //get BluetoothAdapter
        mContext = getContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(mContext,mContext.getString(R.string.bt_not_supported),Toast.LENGTH_LONG).show();
        }
        getA2dpAdapter();
        mNetTitleLayout = view.findViewById(R.id.title_layout);
        ImageView exit = view.findViewById(R.id.title_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        //init BtSwitch
        mBtSwitch = view.findViewById(R.id.title_switch);
        mBtSwitch.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                if(checked){
                    mBluetoothAdapter.enable();
                } else {
                    mBluetoothAdapter.disable();
                }
            }
        });
        mBtSwitch.setCheck(isBluetoothEnable());
        //init Self Info
        mDeviceName = view.findViewById(R.id.bt_device_name);
        mDeviceAddress = view.findViewById(R.id.bt_device_address);
        Log.v("xml_log_bt",mBluetoothAdapter.getName()+"--"+mBluetoothAdapter.getAddress());
        mDeviceName.setText(
                String.format(getString(R.string.bt_device_name),mBluetoothAdapter.getName()));
        mDeviceAddress.setText(
                String.format(getString(R.string.bt_device_address),mBluetoothAdapter.getAddress()));
        view.findViewById(R.id.bt_device_rename).setOnClickListener(this);
        //PairedListView
        mPairedListView = view.findViewById(R.id.bt_paired_device_listview);
        mPairedAdapter = new BluetoothDevicesAdapter(mContext, getPairedDevices(), BT_LISTVIEW_PAIRED);
        mPairedAdapter.setOnElementClickListener(new OnElementClickListener() {
            @Override
            public void onBtSetting(int position) {
                BluetoothDevice pairedDevice = mPairedAdapter.getItem(position);
                new BtPairDialog(mContext, R.style.ZhDialog,
                        BtPairDialog.Type.DISCONNECT, pairedDevice, new BtPairDialog.BtDialogCallback() {
                    @Override
                    public void callBackData(BluetoothDevice device, BtPairDialog.ButtonType buttonType) {
                        if (buttonType == BtPairDialog.ButtonType.LEFT) {
                            cancelpair(device);
                        } else {
                            updatePairedListView();
                        }
                    }
                }).show();
            }
        });
        mPairedListView.setAdapter(mPairedAdapter);
        mPairedListView.setOnItemClickListener(mOnItemClickListener);
        //AvailableListView
        mIvRefresh = view.findViewById(R.id.bt_available_device_refresh);
        mIvRefresh.setOnClickListener(this);
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
        }
        mAvailableListView = view.findViewById(R.id.bt_available_device_listview);
        mAvailableAdapter = new BluetoothDevicesAdapter(mContext, mAvailableDevices, BT_LISTVIEW_AVAILABLE);
        mAvailableListView.setAdapter(mAvailableAdapter);
        mAvailableListView.setOnItemClickListener(mOnItemClickListener);
        mInSearch = view.findViewById(R.id.bt_available_insearch);
        //Broadcaster
        mBroadcaster = new BtBroadcaster();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        mContext.registerReceiver(mBroadcaster, filter);
        return view;
    }

    public void showTitleLayoutExit() {
        mNetTitleLayout.showExit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
        mContext.unregisterReceiver(mBroadcaster);
        closeA2dpAdapter();
    }

    public void getA2dpAdapter() {
        if (mBluetoothAdapter.isEnabled()) { //判断蓝牙是否开启
            //获取A2DP代理对象
            if (mA2dp == null) { //不能重复连接服务，否则会报错
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mBluetoothAdapter.getProfileProxy(getContext(), mBluetoothProfileListener, BluetoothProfile.A2DP);
                }
            }
        } else {
            Log.e("lx","getA2dpAdapter error. bluetooth is not Enabled");
        }
    }

    public void closeA2dpAdapter() {
        if (mBluetoothAdapter.isEnabled()) { //判断蓝牙是否开启
            //获取A2DP代理对象
            if (mA2dp != null) { //不能重复连接服务，否则会报错
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mA2dp);
                    mA2dp = null;
                }
            }
        }
    }

    //getProfileProxy并不会直接返回A2DP代理对象，而是通过mListener中回调获取。
    private BluetoothProfile.ServiceListener mBluetoothProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                Log.d("lx","A2dp onServiceDisconnected ");
                mA2dp = null;
            }
        }

        // getProfileProxy 到 连接成功一般需要几十毫秒！
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                Log.d("lx","A2dp onServiceConnected ");
                mA2dp = (BluetoothA2dp) proxy; //转换 ，这个就是  BluetoothA2dp 对象
            }
        }
    };

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            int id = adapterView.getId();
            if (id == R.id.bt_paired_device_listview) {
                BluetoothDevice pairedDevice = mPairedAdapter.getItem(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Log.e("TAG", "pairedDevice onItemClick");
                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    BluetoothClass bluetoothClass = pairedDevice.getBluetoothClass();
                    if (bluetoothClass.hasService(BluetoothClass.Service.AUDIO) && mA2dp.getConnectionState(pairedDevice) == BluetoothA2dp.STATE_CONNECTED) {  // 如果蓝牙设备连接使用中，则弹出断开连接对话框
                        new BtDisconnectDialog(mContext, R.style.ZhDialog, getAlias(pairedDevice), new BtDisconnectDialog.BtDialogCallback() {
                            @Override
                            public void callBackData(BtDisconnectDialog.ButtonType buttonType) {
                                if (buttonType == BtDisconnectDialog.ButtonType.RIGHT) {
                                    viewHolder.btStatus.setVisibility(View.VISIBLE);
                                    viewHolder.btStatus.setText(R.string.bt_disconnecting);
                                    a2dpDisconnect(mA2dp, pairedDevice);
                                }
                            }
                        }).show();
                        return;
                    }
                    if (bluetoothClass.hasService(BluetoothClass.Service.AUDIO) && mA2dp.getConnectionState(pairedDevice) != BluetoothA2dp.STATE_CONNECTED) {
                        viewHolder.btStatus.setVisibility(View.VISIBLE);
                        viewHolder.btStatus.setText(R.string.bt_connecting);
                        boolean success = a2dpConnect(mA2dp, pairedDevice);
                        Log.d("lx", "a2dpConnect flag:" + success);
                        return;
                    }
                    pairedDevice.connectGatt(mContext, false, new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            super.onConnectionStateChange(gatt, status, newState);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.btStatus.setVisibility(View.GONE);
                                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                                        Log.e("TAG", "连接 STATE_CONNECTED");
                                        Toast.makeText(mContext, R.string.bt_connect_success, Toast.LENGTH_SHORT).show();
                                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                        Log.e("TAG", "连接 STATE_DISCONNECTED");
                                        Toast.makeText(mContext, R.string.bt_connect_fail_or_disconnect, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            } else if (id == R.id.bt_available_device_listview) {
                BluetoothDevice availableDevice = mAvailableAdapter.getItem(i);
                new BtPairDialog(mContext, R.style.ZhDialog,
                        BtPairDialog.Type.PAIR, availableDevice, new BtPairDialog.BtDialogCallback() {
                    @Override
                    public void callBackData(BluetoothDevice device, BtPairDialog.ButtonType buttonType) {
                        pair(device);
                    }
                }).show();
            }
        }
    };

    /**
     * 配对（配对成功与失败通过广播返回）
     * @param device
     */
    public void pair(BluetoothDevice device){
        if (device == null){
            Log.e("xml_log_bt", "bond device null");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()){
            Log.e("xml_log_bt", "Bluetooth not enable!");
            return;
        }
        //配对之前把扫描关闭
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        //判断设备是否配对，没有配对在配，配对了就不需要配了
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            Log.d("xml_log_bt", "attemp to bond:" + device.getName());
            try {
                Method createBondMethod = device.getClass().getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("xml_log_bt", "attemp to bond fail!");
            }
        }
    }

    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     * @param device
     */
    public void cancelpair(BluetoothDevice device){
        if (device == null){
            Log.d("xml_log_bt", "cancel bond device null");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()){
            Log.e("xml_log_bt", "Bluetooth not enable!");
            return;
        }
        //判断设备是否配对，没有配对就不用取消了
        if (device.getBondState() != BluetoothDevice.BOND_NONE) {
            Log.d("xml_log_bt", "attemp to cancel bond:" + device.getName());
            try {
                Method removeBondMethod = device.getClass().getMethod("removeBond");
                Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("xml_log_bt", "attemp to cancel bond fail!");
            }
        }
    }

    private class BtBroadcaster extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.v("xml_log_bt","action = " + action);
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null &&!TextUtils.isEmpty(device.getName()) && !TextUtils.isEmpty(device.getAddress()) && device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.v("xml_log_bond",device.getName()+":"+device.getBondState());
                    if(mAvailableDevices.contains(device))
                        return;
                    mAvailableDevices.add(device);
                    updateAvailableListView();
                }
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                mAvailableDevices.clear();
                updateAvailableListView();
                mInSearch.setVisibility(View.VISIBLE);
                mIvRefresh.setBackgroundResource(R.drawable.settings_searching);
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                mInSearch.setVisibility(View.GONE);
                mIvRefresh.setBackgroundResource(R.drawable.settings_refresh);
            } else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = device.getBondState();
                if(bondState == BluetoothDevice.BOND_NONE) {
                    if(mAvailableDevices.contains(device))
                        return;
                    mAvailableDevices.add(device);
                } else if(bondState == BluetoothDevice.BOND_BONDED) {
                    mAvailableDevices.remove(device);
                }
                updatePairedListView();
                updateAvailableListView();
            } else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                updatePairedListView();
                int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                int oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE,0);
                Log.v("xml_log_bt","btState = " + newState + " ;btState1 = " +oldState);
                if(newState == BluetoothAdapter.STATE_OFF) {
                    mBtSwitch.setCheck(false); // 更新switch bar view
                    mAvailableDevices.clear();
                    updateAvailableListView();
                } else if(newState == BluetoothAdapter.STATE_ON) {
                    mBtSwitch.setCheck(true);
                    mBluetoothAdapter.startDiscovery();
                    getA2dpAdapter();
                }
            } else if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
//                Log.d("lx", "connect state=" + state);
                if (state == BluetoothA2dp.STATE_CONNECTED || state == BluetoothA2dp.STATE_DISCONNECTED) {  // 已连接或断开时，刷新连接状态
                    updatePairedListView();
                }
            }
        }
    }

    private void updateAvailableListView() {
        mAvailableAdapter.setDevices(mAvailableDevices);
        mAvailableAdapter.notifyDataSetChanged();
    }

    private void updatePairedListView() {
        mPairedAdapter.setDevices(getPairedDevices());
        mPairedAdapter.notifyDataSetChanged();
    }

    /**
     * 扫描的方法 返回true 扫描成功
     * 通过接收广播获取扫描到的设备
     * @return
     */
    public boolean scanBluetooth(){
        if (!isBluetoothEnable()){
            return false;
        }
        //当前是否在扫描，如果是就取消当前的扫描，重新扫描
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        //此方法是个异步操作，一般搜索12秒
        return mBluetoothAdapter.startDiscovery();
    }

    private ArrayList<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
        pairedDevices.clear();
        if(devices.size()>0) {
            for(BluetoothDevice device : devices) {
                pairedDevices.add(device);
            }
        }
        return pairedDevices;
    }

    private void initSelfInfo() {
        Log.v("xml_log_bt",mBluetoothAdapter.getName()+"--"+mBluetoothAdapter.getAddress());
        mDeviceName.setText(
                String.format(getString(R.string.bt_device_name),mBluetoothAdapter.getName()));
        mDeviceAddress.setText(
                String.format(getString(R.string.bt_device_address),mBluetoothAdapter.getAddress()));
    }

    /**
     * 蓝牙是否打开   true为打开
     * @return
     */
    private boolean isBluetoothEnable(){
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bt_device_rename) {
            if (mBluetoothAdapter.isEnabled()) {
                new RenameBtDialog(mContext, R.style.ZhDialog, mBluetoothAdapter.getName(), new RenameBtDialog.DialogCallback() {
                    @Override
                    public void callBackData(String[] data) {
                        mDeviceName.setText(String.format(getString(R.string.bt_device_name), data[0]));
                        mBluetoothAdapter.setName(data[0]);
                    }
                }).show();
            }
        } else if (id == R.id.bt_available_device_refresh) {
            scanBluetooth();
        }
    }

    private class BluetoothDevicesAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mDevices;
        private Context mContext;
        private int mBtListViewType;
        private OnElementClickListener mOnElementClickListener;

        public BluetoothDevicesAdapter(Context context, ArrayList<BluetoothDevice> devices, int btListViewType) {
            mContext = context;
            mDevices = devices;
            mBtListViewType = btListViewType;
        }

        public void setOnElementClickListener(OnElementClickListener onElementClickListener) {
            mOnElementClickListener = onElementClickListener;
        }

        public void setDevices(ArrayList list) {
            mDevices = list;
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public BluetoothDevice getItem(int i) {
            return mDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_device_item, viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.btName = view.findViewById(R.id.bt_name);
                viewHolder.btAddress = view.findViewById(R.id.bt_address);
                viewHolder.btSetting = view.findViewById(R.id.bt_setting);
                viewHolder.btStatus = view.findViewById(R.id.bt_status);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)view.getTag();
            }
            BluetoothDevice device = mDevices.get(i);
            String alias = getAlias(device);
            viewHolder.btName.setText(alias);
            viewHolder.btStatus.setVisibility(View.GONE);
            if (mBtListViewType == BT_LISTVIEW_PAIRED) {
                viewHolder.btSetting.setVisibility(View.VISIBLE);
                if (mA2dp != null) {  // 判断是否为使用中的蓝牙设备
                    if(device.getAddress().equals(getActiveDeviceAddress(mA2dp))){  // 改为通过地址判断设备是否Active
                        viewHolder.btStatus.setVisibility(View.VISIBLE);
                        viewHolder.btStatus.setText(R.string.bt_active);
                    }else {
                        viewHolder.btStatus.setVisibility(View.GONE);
                    }
                }
            }
            viewHolder.btSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnElementClickListener != null) {
                        mOnElementClickListener.onBtSetting(i);
                    }
                }
            });
            return view;
        }
    }

    private String getActiveDeviceAddress(BluetoothA2dp a2dp) {
        String address = null;
        try {
            Class<?> bluetoothA2dp = Class.forName("android.bluetooth.BluetoothA2dp");
            Method getActiveDevice = bluetoothA2dp.getMethod("getActiveDevice");
            BluetoothDevice device =
                    (BluetoothDevice) getActiveDevice.invoke(a2dp);
            if (device != null) {
                address = device.getAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    private boolean a2dpConnect(BluetoothA2dp bluetoothA2dp, BluetoothDevice bluetoothDevice) {
        Class<?> bluetoothA2dpClass = bluetoothA2dp.getClass();
        Method connect = null;
        boolean success = false;
        try {
            connect = bluetoothA2dpClass.getMethod("connect", BluetoothDevice.class);
            success = (boolean) connect.invoke(bluetoothA2dp, bluetoothDevice);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return success;
    }

    private boolean a2dpDisconnect(BluetoothA2dp bluetoothA2dp, BluetoothDevice device) {
        Class<?> bluetoothA2dpClass = bluetoothA2dp.getClass();
        Method disconnect = null;
        boolean success = false;
        try {
            disconnect = bluetoothA2dpClass.getMethod("disconnect", BluetoothDevice.class);
            success = (boolean) disconnect.invoke(bluetoothA2dp, device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return success;
    }

    private class ViewHolder {
        TextView btName;
        TextView btAddress;
        ImageView btSetting;
        TextView btStatus;
    }

    public interface OnElementClickListener{
        public void onBtSetting(int position);
    }
}
