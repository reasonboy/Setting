package com.jzzh.setting.light;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class LightActivity extends BaseActivity implements AdjustLayout.OnValueChangeListener,AdjustLayout.OnEnableChangeListener{

    private static final int MSG_UPDATE_BRIGHTNESS_STATE = 0;
    private static final Uri TEMPERATURE_ENABLE = Settings.System.getUriFor("temperature_enable");
    private static final Uri BRIGHTNESS_ENABLE = Settings.System.getUriFor("brightness_enable");
    private static final Uri TEMP_TEMPERATURE_LEVEL = Settings.System.getUriFor("temp_temperature_level");
    private static final Uri TEMP_BRIGHTNESS_LEVEL = Settings.System.getUriFor("temp_brightness_level");
    private int mBrightnessGradient = 32;//亮度梯度
    private int mTemperatureGradient = 32;//色温梯度
    private int mBrightnessLevel = 0;//亮度等级
    private int mTemperatureLevel = 0;//色温等级
    private int mWarmBrightness = 0;//暖光亮度
    private int mColdBrightness = 0;//冷光亮度
    private AdjustLayout mTemperature,mBrightness;
    private LightObserver mLightObserver;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_BRIGHTNESS_STATE:
                    updateLightView();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        mLightObserver = new LightObserver(new Handler());
        mLightObserver.startObserving();

        mTemperature = findViewById(R.id.adjust_temperature);
        mTemperature.setOnEnableChangeListener(this);
        mTemperature.setOnValueChangeListener(this);
        mTemperature.setMinValue(0);
        mTemperature.setMaxValue(mBrightnessGradient);

        mBrightness = findViewById(R.id.adjust_brightness);
        mBrightness.setOnEnableChangeListener(this);
        mBrightness.setOnValueChangeListener(this);
        mBrightness.setMinValue(0);
        mBrightness.setMaxValue(mBrightnessGradient);
        updateLightView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLightObserver.stopObserving();
    }

    private void updateLightView() {
        mBrightness.enable(getBrightnessEnable());
        mBrightnessLevel = getBrightnessLevel();
        mBrightness.setValue(mBrightnessLevel);

        mTemperature.enable(getBrightnessEnable());
        mTemperatureLevel = getTemperatureLevel();
        mTemperature.setValue(mTemperatureLevel);
    }

    @Override
    public void enable(View view, boolean enable) {
        if(view==mTemperature) {
            setTemperatureEnable(enable);
            if(!enable) {
                setTemperatureLevel(0);
            } else {
                setTemperatureLevel(getTempTemperatureLevel());
            }
        } else if(view==mBrightness) {
            setBrightnessEnable(enable);
            if(!enable) {
                setBrightnessLevel(0);
            } else {
                setBrightnessLevel(getTempBrightnessLevel());
            }
        }
        gradientToBrightness(mBrightnessLevel,mTemperatureLevel);
    }

    @Override
    public void valueChange(View view, int value) {
        if(view==mTemperature) {
            setTempTemperatureLevel(value);
            setTemperatureLevel(value);
        } else if(view==mBrightness) {
            setTempBrightnessLevel(value);
            setBrightnessLevel(value);
        }
        gradientToBrightness(mBrightnessLevel,mTemperatureLevel);
    }

    private void setWarmBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(),"screen_warm_brightness", brightness);
    }

    private void setColdBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(),"screen_cold_brightness", brightness);
    }

    private void setBrightnessLevel(int level) {
        mBrightnessLevel = level;
        Settings.System.putInt(getContentResolver(),"brightness_level", level);
    }

    private void setTemperatureLevel(int level) {
        mTemperatureLevel = level;
        Settings.System.putInt(getContentResolver(),"temperature_level", level);
    }

    private int getBrightnessLevel() {
        return Settings.System.getInt(getContentResolver(),"brightness_level", 0);
    }

    private int getTemperatureLevel() {
        return Settings.System.getInt(getContentResolver(),"temperature_level", 0);
    }

    private void setTempTemperatureLevel(int level) {
        Settings.System.putInt(getContentResolver(),"temp_temperature_level", level);
    }

    private int getTempTemperatureLevel() {
        return Settings.System.getInt(getContentResolver(),"temp_temperature_level", 0);
    }

    private void setTempBrightnessLevel(int level) {
        Settings.System.putInt(getContentResolver(),"temp_brightness_level", level);
    }

    private int getTempBrightnessLevel() {
        return Settings.System.getInt(getContentResolver(),"temp_brightness_level", 0);
    }

    private void setTemperatureEnable(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"temperature_enable", value);
    }

    private boolean getTemperatureEnable() {
        int value = Settings.System.getInt(getContentResolver(),"temperature_enable", 1);
        boolean enable = value == 1 ? true : false;
        return enable;
    }

    private void setBrightnessEnable(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"brightness_enable", value);
    }

    private boolean getBrightnessEnable() {
        int value = Settings.System.getInt(getContentResolver(),"brightness_enable", 1);
        boolean enable = value == 1 ? true : false;
        return enable;
    }

    //将亮度等级和色温等级转换为暖光亮度和冷光亮度
    private void gradientToBrightness(int brightness,int temperature) {
        mWarmBrightness = 256*brightness*temperature / (mBrightnessGradient*mTemperatureGradient);
        mColdBrightness = 256*brightness*(mTemperatureGradient - temperature) / (mBrightnessGradient*mTemperatureGradient);
        if(mWarmBrightness < 0) mWarmBrightness = 0;
        if(mWarmBrightness > 256) mWarmBrightness = 255;
        if(mColdBrightness < 0) mColdBrightness = 0;
        if(mColdBrightness > 256) mColdBrightness = 255;
        changeLight();
    }

    private void changeLight() {
        setWarmBrightness(mWarmBrightness);
        setColdBrightness(mColdBrightness);
    }

    private class LightObserver extends ContentObserver {

        public LightObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (selfChange) return;
            Message msg = mHandler.obtainMessage(MSG_UPDATE_BRIGHTNESS_STATE);
            mHandler.sendMessage(msg);
        }

        public void startObserving() {
            final ContentResolver cr = getContentResolver();
            cr.unregisterContentObserver(this);
            cr.registerContentObserver(TEMP_TEMPERATURE_LEVEL, false, this);
            cr.registerContentObserver(TEMP_BRIGHTNESS_LEVEL, false, this);
            cr.registerContentObserver(TEMPERATURE_ENABLE, false, this);
            cr.registerContentObserver(BRIGHTNESS_ENABLE, false, this);
        }

        public void stopObserving() {
            final ContentResolver cr = getContentResolver();
            cr.unregisterContentObserver(this);
        }
    }
}
