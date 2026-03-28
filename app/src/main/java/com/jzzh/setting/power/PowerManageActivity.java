package com.jzzh.setting.power;

import android.os.Bundle;
import android.provider.Settings;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

public class PowerManageActivity extends BaseActivity {

    private ZhCheckBox mCheckBox, mMusicCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_manage);
        mCheckBox = findViewById(R.id.setting_power_manage_title_cb);
        mCheckBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setHallEnable(checked);
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
        mCheckBox.setCheck(getHallEnable());
        mMusicCheckBox.setCheck(getPauseMusic());
    }

    private void setHallEnable(boolean enable) {
        int value = enable ? 0 : 1;
        Settings.System.putInt(getContentResolver(),"hall_enable", value);
    }

    private boolean getHallEnable() {
        int value = Settings.System.getInt(getContentResolver(),"hall_enable", 1);
        return value == 0;
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
