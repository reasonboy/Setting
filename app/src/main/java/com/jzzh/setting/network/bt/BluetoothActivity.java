package com.jzzh.setting.network.bt;

import android.bluetooth.BluetoothAdapter;
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

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.ZhCheckBox;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends BaseActivity implements View.OnClickListener {

    private ZhCheckBox mBtSwitch;
    private TextView mDeviceName,mDeviceAddress;
    private ListView mPairedListView,mAvailableListView;
    private BluetoothDevicesAdapter mPairedAdapter,mAvailableAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mAvailableDevices = new ArrayList<>();
    private BtBroadcaster mBroadcaster;
    private TextView mInSearch;
    private ImageView mIvRefresh;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bt);
        //get BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(this,getString(R.string.bt_not_supported),Toast.LENGTH_LONG).show();
            finish();
        }
        //init BtSwitch
        mBtSwitch = findViewById(R.id.title_switch);
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
        mDeviceName = findViewById(R.id.bt_device_name);
        mDeviceAddress = findViewById(R.id.bt_device_address);
        Log.v("xml_log_bt",mBluetoothAdapter.getName()+"--"+mBluetoothAdapter.getAddress());
        mDeviceName.setText(
                String.format(getString(R.string.bt_device_name),mBluetoothAdapter.getName()));
        mDeviceAddress.setText(
                String.format(getString(R.string.bt_device_address),mBluetoothAdapter.getAddress()));
        findViewById(R.id.bt_device_rename).setOnClickListener(this);
        //PairedListView
        mPairedListView = findViewById(R.id.bt_paired_device_listview);
        mPairedAdapter = new BluetoothDevicesAdapter(this,getPairedDevices());
        mPairedListView.setAdapter(mPairedAdapter);
        mPairedListView.setOnItemClickListener(mOnItemClickListener);
        mPairedListView.setOnItemLongClickListener(mOnItemLongClickListener);  // 长按取消配对
        //AvailableListView
        mIvRefresh = findViewById(R.id.bt_available_device_refresh);
        mIvRefresh.setOnClickListener(this);
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
        }
        mAvailableListView = findViewById(R.id.bt_available_device_listview);
        mAvailableAdapter = new BluetoothDevicesAdapter(this,mAvailableDevices);
        mAvailableListView.setAdapter(mAvailableAdapter);
        mAvailableListView.setOnItemClickListener(mOnItemClickListener);
        mInSearch = findViewById(R.id.bt_available_insearch);
        //Broadcaster
        mBroadcaster = new BtBroadcaster();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcaster, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mBroadcaster);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()){
                case R.id.bt_paired_device_listview:
                    BluetoothDevice pairedDevice = mPairedAdapter.getItem(i);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        Log.e("TAG","pairedDevice onItemClick");
                        BluetoothActivity.ViewHolder viewHolder=(BluetoothActivity.ViewHolder)view.getTag();
                        viewHolder.btAddress.setVisibility(View.VISIBLE);
                        viewHolder.btAddress.setText(getString(R.string.bt_connecting));
                        pairedDevice.connectGatt(BluetoothActivity.this, false, new BluetoothGattCallback() {
                            @Override
                            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                                super.onConnectionStateChange(gatt, status, newState);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewHolder.btAddress.setVisibility(View.GONE);
                                        if (newState == BluetoothProfile.STATE_CONNECTED){
                                            Log.e("TAG","连接 STATE_CONNECTED");
                                            Toast.makeText(BluetoothActivity.this,R.string.bt_connect_success,Toast.LENGTH_SHORT).show();
                                        }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                                            Log.e("TAG","连接 STATE_DISCONNECTED");
                                            Toast.makeText(BluetoothActivity.this,R.string.bt_connect_fail_or_disconnect,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    break;
                case R.id.bt_available_device_listview:
                    BluetoothDevice availableDevice = mAvailableAdapter.getItem(i);
                    new BtPairDialog(BluetoothActivity.this, R.style.ZhDialog,
                            BtPairDialog.Type.PAIR, availableDevice, new BtPairDialog.BtDialogCallback() {
                        @Override
                        public void callBackData(BluetoothDevice device) {
                            pair(device);
                        }
                    }).show();
                    break;
            }
        }
    };

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.bt_paired_device_listview) {
                BluetoothDevice pairedDevice = mPairedAdapter.getItem(position);
                new BtPairDialog(BluetoothActivity.this, R.style.ZhDialog,
                        BtPairDialog.Type.DISCONNECT, pairedDevice, new BtPairDialog.BtDialogCallback() {
                    @Override
                    public void callBackData(BluetoothDevice device) {
                        cancelpair(device);
                    }
                }).show();
            }
            return true;
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
            Log.v("xml_log_bt","action = " + action);
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
        switch (view.getId()) {
            case R.id.bt_device_rename:
                if(mBluetoothAdapter.isEnabled()) {
                    new RenameBtDialog(this, R.style.ZhDialog, mBluetoothAdapter.getName(), new RenameBtDialog.DialogCallback() {
                        @Override
                        public void callBackData(String[] data) {
                            mDeviceName.setText(String.format(getString(R.string.bt_device_name), data[0]));
                            mBluetoothAdapter.setName(data[0]);
                        }
                    }).show();
                }
                break;
            case R.id.bt_available_device_refresh:
                scanBluetooth();
                break;
        }
    }

    private class BluetoothDevicesAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mDevices;
        private Context mContext;

        public BluetoothDevicesAdapter(Context context,ArrayList<BluetoothDevice> devices) {
            mContext = context;
            mDevices = devices;
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
            BluetoothActivity.ViewHolder viewHolder;
            if(view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_device_item, viewGroup,false);
                viewHolder = new BluetoothActivity.ViewHolder();
                viewHolder.btName = view.findViewById(R.id.bt_name);
                viewHolder.btAddress = view.findViewById(R.id.bt_address);
                view.setTag(viewHolder);
            } else {
                viewHolder = (BluetoothActivity.ViewHolder)view.getTag();
            }
            viewHolder.btName.setText(mDevices.get(i).getName());
            viewHolder.btAddress.setText(mDevices.get(i).getAddress());
            return view;
        }
    }

    private class ViewHolder {
        TextView btName;
        TextView btAddress;
    }
}
