package com.jzzh.setting;

import com.jzzh.setting.device.AppUpdateActivity;
import com.jzzh.setting.device.DeviceActivity;
import com.jzzh.setting.device.DeviceCertificationInfoActivity;
import com.jzzh.setting.device.DeviceInfoActivity;
import com.jzzh.setting.device.FactoryResetActivity;
import com.jzzh.setting.device.GSFActivity;
import com.jzzh.setting.device.LicenseInfo;
import com.jzzh.setting.device.SystemUpdateActivity;
import com.jzzh.setting.display.DisplayActivity;
import com.jzzh.setting.display.HomeScreenStyleActivity;
import com.jzzh.setting.display.PowerOffImageActivity;
import com.jzzh.setting.display.PowerOffImageDefaultActivity;
import com.jzzh.setting.display.PowerOffImageUserActivity;
import com.jzzh.setting.display.RefreshActivity;
import com.jzzh.setting.display.SleepImageActivity;
import com.jzzh.setting.display.SleepImageDefaultActivity;
import com.jzzh.setting.display.SleepImageUserActivity;
import com.jzzh.setting.display.WidgetSettingActivity;
import com.jzzh.setting.display.WidgetTextActivity;
import com.jzzh.setting.keygesture.GestureSettingActivity;
import com.jzzh.setting.keygesture.KeyGestureActivity;
import com.jzzh.setting.language.KeyboardManagerActivity;
import com.jzzh.setting.language.LanguageAndKeyboardActivity;
import com.jzzh.setting.light.LightActivity;
import com.jzzh.setting.lock.LockScreenActivity;
import com.jzzh.setting.network.bt.BluetoothActivity;
import com.jzzh.setting.network.NetworkActivity;
import com.jzzh.setting.network.wifi.WifiActivity;
import com.jzzh.setting.power.AutoPowerOffActivity;
import com.jzzh.setting.power.AutoSleepActivity;
import com.jzzh.setting.power.PowerActivity;
import com.jzzh.setting.task.TaskManagerActivity;
import com.jzzh.setting.time.DateAndTimeActivity;
import com.jzzh.setting.time.TimeZoneActivity;

public class Const {

    public	static final String PACKAGE_NAME = "com.jzzh.setting";

    public static final Class<?>[] ACTIVITIES = new Class[] {
            Setting.class,

            NetworkActivity.class,
            WifiActivity.class,
            BluetoothActivity.class,

            PowerActivity.class,
            AutoSleepActivity.class,
            AutoPowerOffActivity.class,

            DisplayActivity.class,
            RefreshActivity.class,
            PowerOffImageActivity.class,
            PowerOffImageDefaultActivity.class,
            PowerOffImageUserActivity.class,
            SleepImageActivity.class,
            SleepImageDefaultActivity.class,
            SleepImageUserActivity.class,
            HomeScreenStyleActivity.class,
            WidgetTextActivity.class,
            WidgetSettingActivity.class,

            LightActivity.class,

            LanguageAndKeyboardActivity.class,
            KeyboardManagerActivity.class,

            DateAndTimeActivity.class,
            TimeZoneActivity.class,

            LockScreenActivity.class,

            TaskManagerActivity.class,

            DeviceActivity.class,
            DeviceInfoActivity.class,
            LicenseInfo.class,
            DeviceCertificationInfoActivity.class,
            SystemUpdateActivity.class,
            AppUpdateActivity.class,
            FactoryResetActivity.class,
            GSFActivity.class,

            KeyGestureActivity.class,
            GestureSettingActivity.class
    };

    public 	static final int[][] NAVIGATIONS = new int[][]{
            {R.string.setting},

            {R.string.setting,R.string.setting_network},
            {R.string.setting,R.string.setting_network,R.string.setting_network_wifi},
            {R.string.setting,R.string.setting_network,R.string.setting_network_bt},

            {R.string.setting,R.string.setting_power},
            {R.string.setting,R.string.setting_power,R.string.setting_power_auto_sleep},
            {R.string.setting,R.string.setting_power,R.string.setting_power_auto_power_off},

            {R.string.setting,R.string.setting_display},
            {R.string.setting,R.string.setting_display,R.string.setting_display_refresh},
            {R.string.setting,R.string.setting_display,R.string.setting_display_poi},
            {R.string.setting,R.string.setting_display,R.string.setting_display_poi,R.string.setting_display_poi_default},
            {R.string.setting,R.string.setting_display,R.string.setting_display_poi,R.string.setting_display_poi_user},
            {R.string.setting,R.string.setting_display,R.string.setting_display_si},
            {R.string.setting,R.string.setting_display,R.string.setting_display_si,R.string.setting_display_si_default},
            {R.string.setting,R.string.setting_display,R.string.setting_display_si,R.string.setting_display_si_user},
            {R.string.setting,R.string.setting_display,R.string.setting_display_home_screen_style},
            {R.string.setting,R.string.setting_display,R.string.setting_display_widget_text},
            {R.string.setting,R.string.setting_display,R.string.setting_display_widget_setting},

            {R.string.setting,R.string.setting_light},

            {R.string.setting,R.string.setting_language_and_keyboard},
            {R.string.setting,R.string.setting_language_and_keyboard,R.string.setting_language_and_keyboard_km},

            {R.string.setting,R.string.setting_date_and_time},
            {R.string.setting,R.string.setting_date_and_time,R.string.setting_date_and_time_tz},

            {R.string.setting,R.string.setting_lock_screen},

            {R.string.setting,R.string.setting_task_manager},

            {R.string.setting,R.string.setting_device},
            {R.string.setting,R.string.setting_device,R.string.setting_device_info},
            {R.string.setting,R.string.setting_device,R.string.setting_device_info,R.string.setting_device_license},
            {R.string.setting,R.string.setting_device,R.string.setting_device_certification_info},
            {R.string.setting,R.string.setting_device,R.string.setting_device_system_update},
            {R.string.setting,R.string.setting_device,R.string.setting_device_app_update},
            {R.string.setting,R.string.setting_device,R.string.setting_device_reset},
            {R.string.setting,R.string.setting_device,R.string.setting_device_gsf_id},

            {R.string.setting,R.string.setting_key_and_gesture},
            {R.string.setting,R.string.setting_key_and_gesture, R.string.setting_gesture_setting},
    };
}
