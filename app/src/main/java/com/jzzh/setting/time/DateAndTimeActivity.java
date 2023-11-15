package com.jzzh.setting.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.util.Calendar;


public class DateAndTimeActivity extends BaseActivity implements TimeSettingItem.OnClickListener, TimeSettingItem.OnCheckBoxChangeListener {

    private TimeBroadcast mTimeBroadcast;
    private TimeSettingItem mAutoTime, mSettingsDate, mSettingsTime, mSettingsTimeZone, mHourFormat;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_date_and_time);
        initView();
        mTimeBroadcast = new TimeBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mTimeBroadcast,intentFilter);
    }

    private void initView() {
        mAutoTime = findViewById(R.id.time_auto_time);
        mAutoTime.setOnCheckBoxChangeListener(this);
        mSettingsDate = findViewById(R.id.time_set_date);
        mSettingsDate.setOnClickListener(this);
        mSettingsTime = findViewById(R.id.time_set_time);
        mSettingsTime.setOnClickListener(this);
        mSettingsTimeZone = findViewById(R.id.time_select_timezone);
        mSettingsTimeZone.setOnClickListener(this);
        mHourFormat = findViewById(R.id.time_hour_format);
        mHourFormat.setOnCheckBoxChangeListener(this);
    }

    private void setSetDateEnabled(boolean enabled){
        mSettingsDate.setEnable(!enabled);
    }

    private void setSetTimeEnabled(boolean enabled){
        mSettingsTime.setEnable(!enabled);
    }

    private boolean isAutoTimeEnabled() {
        //api 17以上
        return Settings.Global.getInt(getContentResolver(),Settings.Global.AUTO_TIME, 0) > 0;
    }

    private boolean isAutoTimeZoneEnabled() {
        //api 17以上
        return Settings.Global.getInt(getContentResolver(),Settings.Global.AUTO_TIME_ZONE, 0) > 0;
    }

    private boolean is24HourFormatEnabled() {
        return DateFormat.is24HourFormat(this);
    }

    private void timeUpdated(boolean is24Hour) {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        int timeFormatPreference =
                is24Hour ? 1/*Intent.EXTRA_TIME_PREF_VALUE_USE_24_HOUR*/
                        : 0/*Intent.EXTRA_TIME_PREF_VALUE_USE_12_HOUR*/;
        timeChanged.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", timeFormatPreference);
        sendBroadcast(timeChanged);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.time_set_date:
                if(!isAutoTimeEnabled()) {
                    new SetDateDialog(this, R.style.ZhDialog, new SetDateDialog.DialogCallback() {
                        @Override
                        public void callBackData(String[] data) {
                            updateDate();
                        }
                    }).show();
                }
                break;
            case R.id.time_set_time:
                if(!isAutoTimeEnabled()) {
                    new SetTimeDialog(this, R.style.ZhDialog, new SetTimeDialog.DialogCallback() {
                        @Override
                        public void callBackData(String[] data) {
                            updateTime();
                        }
                    }).show();
                }
                break;
            case R.id.time_select_timezone:
                startActivity(TimeZoneActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mTimeBroadcast);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAutoTime.setCheckBoxEnable(isAutoTimeEnabled());
        updateTime();
        updateDate();
        setSetDateEnabled(isAutoTimeEnabled());
        setSetTimeEnabled(isAutoTimeEnabled());
        //updateFormat();
        updateTimeTone();
        mHourFormat.setCheckBoxEnable(is24HourFormatEnabled());
    }

    private void updateTimeTone() {
        TimeZone timeZone =  TimeZone.getDefault();
        int offset = timeZone.getRawOffset();
        String timeZoneName = timeZone.getDisplayName();
        mSettingsTimeZone.setSummary(ZoneGetter.offsetToGMT(offset)+","+timeZoneName);
    }

    @Override
    public void onCheckBoxChanged(View view, boolean checked) {
        switch (view.getId()){
            case R.id.time_auto_time:
                Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME,checked ? 1 : 0);
                setSetDateEnabled(isAutoTimeEnabled());
                setSetTimeEnabled(isAutoTimeEnabled());
                if(checked){  // 点击自动获取后，及时显示设置好的日期时间
                    mAutoTime.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateDate();
                            updateTime();
                        }
                    },50);
                }
                break;
            case R.id.time_hour_format:
                Settings.System.putString(getContentResolver(),Settings.System.TIME_12_24,checked ? "24" : "12");
                timeUpdated(is24HourFormatEnabled());
                //updateFormat();
                updateTime();
                break;
        }
    }

    private class TimeBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Intent.ACTION_TIME_TICK.equals(action)) {
                updateTime();
            } else if (Intent.ACTION_DATE_CHANGED.equals(action)){
                updateDate();
            }
        }
    }

    private void updateFormat() {
        String timeFormatContent = "13:00";
        if (!is24HourFormatEnabled()) {
            timeFormatContent = "下午1:00";
        }
        mHourFormat.setSummary(timeFormatContent);
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int apm = calendar.get(Calendar.AM_PM);
        String minStr = min < 10 ? "0"+min : ""+min;
        String apmStr = apm == Calendar.AM ? getString(R.string.time_am) : getString(R.string.time_pm);
        String summary = "";

        if(is24HourFormatEnabled()) {
            summary = hour + ":" + minStr;
        } else {
            if(apm == Calendar.PM) {
                if(hour > 12) {
                    hour -= 12;
                }
            }
            summary = hour + ":" + minStr + " " + apmStr;
        }
        mSettingsTime.setSummary(summary);
    }

    private void updateDate() {
        Calendar calendar = Calendar.getInstance();
        mSettingsDate.setSummary(calendar.get(Calendar.YEAR)+"-"+
                (calendar.get(Calendar.MONTH)+1)+"-"+
                calendar.get(Calendar.DAY_OF_MONTH));
    }
}
