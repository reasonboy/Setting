package com.jzzh.setting.device;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class DeviceInfoActivity extends BaseActivity implements View.OnClickListener {
    public static final int ADB_SETTING_ON = 1;
    public static final int ADB_SETTING_OFF = 0;
    private final Integer BREAK_THROUGH_TIMES = 6;
    private int breakthrough = BREAK_THROUGH_TIMES;
    private long lastClickTime;

    private DeviceIfoItem mSystemVersion, mAndroidVersion, mModelNumber, mSerialNumber, mBatteryInfo, mStorageInfo, mSdCardInfo, mLicense;
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = (int)(100f
                    * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100));
            boolean pluggedIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
            String state = "";
            if(level == 100) {
                state = getString(R.string.device_info_battery_full);
            } else {
                if(pluggedIn) {
                    state = getString(R.string.device_info_battery_charging);
                } else {
                    state = getString(R.string.device_info_battery_discharging);
                }
            }
            mBatteryInfo.setInformation(state+" / "+level+"%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        mSystemVersion = findViewById(R.id.device_info_system_version);
        mSystemVersion.setOnClickListener(this);
        mAndroidVersion = findViewById(R.id.device_info_android_version);
        mAndroidVersion.setOnClickListener(this);
        mModelNumber = findViewById(R.id.device_info_model_number);
        mModelNumber.setOnClickListener(this);
        mSerialNumber = findViewById(R.id.device_info_serial_number);
        mBatteryInfo = findViewById(R.id.device_info_battery);
        mStorageInfo = findViewById(R.id.device_info_storage);
        mSdCardInfo = findViewById(R.id.device_info_sd);
        mLicense = findViewById(R.id.device_info_license);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver,filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSystemVersion();
        mAndroidVersion.setInformation(Build.VERSION.RELEASE);
        mModelNumber.setInformation(Build.MODEL);
        mSerialNumber.setInformation(getSerialNumber());
        mStorageInfo.setInformation(getStorage());
        mSdCardInfo.setInformation(getSdStorage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatteryReceiver);
    }

    private String getSdStorage() {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        try {
            Class storeManagerClazz = Class.forName("android.os.storage.StorageManager");

            Method getVolumesMethod = storeManagerClazz.getMethod("getVolumes");

            List<?> volumeInfos = (List<?>) getVolumesMethod.invoke(storageManager);

            Class volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getTypeMethod = volumeInfoClazz.getMethod("getType");
            Method isMountedReadableMethod = volumeInfoClazz.getMethod("isMountedReadable");
            Method getPathMethod = volumeInfoClazz.getMethod("getPath");
            Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");

            long freeBytes = 0;
            long totalBytes = 0;
            Log.v("xml_log_sd","volumeInfos.size() = "+volumeInfos.size());
            for(Object volumeInfo : volumeInfos){
                String uuid = (String)getFsUuidMethod.invoke(volumeInfo);
                int type = (int)getTypeMethod.invoke(volumeInfo);
                boolean isMountedReadable = (boolean) isMountedReadableMethod.invoke(volumeInfo);
                Log.v("xml_log_sd","uuid=" + uuid + "\ntype=" + type + "\nisMountedReadable=" + isMountedReadable);
                if(type == 0/*VolumeInfo.TYPE_PUBLIC*/ || type == 5/*VolumeInfo.TYPE_STUB*/) {
                    if(isMountedReadable) {
                        File path = (File) getPathMethod.invoke(volumeInfo);
                        totalBytes = totalBytes + path.getTotalSpace();
                        freeBytes = freeBytes + path.getFreeSpace();
                        Log.v("xml_log_sd",totalBytes+"--"+freeBytes);
                    }
                }
            }
            if(freeBytes != 0 && totalBytes != 0) {
                String totalStr = Formatter.formatFileSize(this, totalBytes);
                String availStr = Formatter.formatFileSize(this, freeBytes);
                return getString(R.string.device_info_available) + availStr + "\n"
                        + getString(R.string.device_info_total) + totalStr;
            }
        } catch (Exception e) {
            Log.v("xml_log_sd","e.toString = "+e.toString());
        }
        return getString(R.string.device_info_no_sd);
    }

    private String getStorage() {
        // TODO Auto-generated method stub
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlacks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();

        long totalSize = blockSize * totalBlacks;
        long availSize = availableBlocks * blockSize;


        String totalStr = Formatter.formatFileSize(this, totalSize);
        String availStr = Formatter.formatFileSize(this, availSize);
        return getString(R.string.device_info_available) + availStr + "\n"
                + getString(R.string.device_info_total) + totalStr;
    }

    public static String getProperties(Context context, String key) {
        String result = "";
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method getString = SystemProperties.getMethod("get", paramTypes);
            //参数
            Object[] params = new Object[1];
            params[0] = new String(key);

            result = (String) getString.invoke(SystemProperties, params);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            //如果key超过32个字符则抛出该异常
            Log.w("xml_log", "key超过32个字符");
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= 28) {//9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "读取设备序列号异常：" + e.toString());
        }
        return serial;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (breakthrough > 0) {
            if (!isFastClickEachOneSec()) {
                breakthrough = BREAK_THROUGH_TIMES;
            }
        }

        switch (id) {
            case R.id.device_info_system_version:
                if (breakthrough == 6 || breakthrough == 3)
                    breakthrough--;
                break;
            case R.id.device_info_android_version:
                if (breakthrough == 1)
                    breakthrough--;
                break;
            case R.id.device_info_model_number:
                if (breakthrough == 5 || breakthrough == 4 || breakthrough == 2)
                    breakthrough--;
                break;
        }
        if (breakthrough == 0) {
            breakthrough = BREAK_THROUGH_TIMES;
            writeAdbSetting(getAdbSetting() != ADB_SETTING_ON);
            updateSystemVersion();
            showToast();
        }
    }

    private void updateSystemVersion(){
        if (getAdbSetting() == ADB_SETTING_ON) {
            mSystemVersion.setInformation(getProperties(this, "ro.build.version.incremental") + ".D");
        } else {
            mSystemVersion.setInformation(getProperties(this, "ro.build.version.incremental"));
        }
    }

    private void showToast() {
        if (getAdbSetting() == ADB_SETTING_ON) {
            Toast.makeText(this, "ADB enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ADB disabled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 防止1秒内连续操作
     *
     * @return true为连续操作，false则不是连续操作
     */
    public boolean isFastClickEachOneSec() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private void writeAdbSetting(boolean enabled) {
        Settings.Global.putInt(getContentResolver(),
                Settings.Global.ADB_ENABLED, enabled ? ADB_SETTING_ON : ADB_SETTING_OFF);
    }

    private int getAdbSetting() {
        return Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, ADB_SETTING_OFF);
    }
}
