package com.jzzh.setting.power;

import android.os.Bundle;
import android.provider.Settings;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

public class PowerManageActivity extends BaseActivity {

    private ZhCheckBox mWakeCheckBox, mSleepCheckBox, mMusicCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_manage);
        mWakeCheckBox = findViewById(R.id.setting_hall_wake_cb);
        mWakeCheckBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setHallWakeEnable(checked);
            }
        });
        mSleepCheckBox = findViewById(R.id.setting_hall_sleep_cb);
        mSleepCheckBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setHallSleepEnable(checked);
            }
        });
        mMusicCheckBox = findViewById(R.id.setting_pause_music_cb);
        mMusicCheckBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setPauseMusic(checked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeCheckBox.setCheck(getHallWakeEnable());
        mSleepCheckBox.setCheck(getHallSleepEnable());
        mMusicCheckBox.setCheck(getPauseMusic());
    }

    private void setHallWakeEnable(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"hall_wake_enable", value);
    }

    private boolean getHallWakeEnable() {
        int value = Settings.System.getInt(getContentResolver(),"hall_wake_enable", 1);
        return value == 1;
    }

    private void setHallSleepEnable(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"hall_sleep_enable", value);
    }

    private boolean getHallSleepEnable() {
        int value = Settings.System.getInt(getContentResolver(),"hall_sleep_enable", 1);
        return value == 1;
    }

    private void setPauseMusic(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"pause_music_in_sleep", value);
    }

    private boolean getPauseMusic() {
        int value = Settings.System.getInt(getContentResolver(),"pause_music_in_sleep", 1);
        return value == 1;
    }
}
