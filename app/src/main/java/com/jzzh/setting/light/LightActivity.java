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
    private static final Uri WARM_LIGHT_ENABLE = Settings.System.getUriFor("warm_light_enable");
    private static final Uri COLD_LIGHT_ENABLE = Settings.System.getUriFor("cold_light_enable");
    private static final Uri TEMP_WARM_BRIGHTNESS = Settings.System.getUriFor("temp_warm_brightness");
    private static final Uri TEMP_COLD_BRIGHTNESS = Settings.System.getUriFor("temp_cold_brightness");
    private AdjustLayout mWarmLight,mColdLight;
    private int mBrightnessGradient = 25;//亮度梯度
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

        mWarmLight = findViewById(R.id.adjust_warm_light);
        mColdLight = findViewById(R.id.adjust_cold_light);

        mWarmLight.setOnEnableChangeListener(this);
        mWarmLight.setOnValueChangeListener(this);
        mWarmLight.setMinValue(0);
        mWarmLight.setMaxValue(mBrightnessGradient);

        mColdLight.setOnEnableChangeListener(this);
        mColdLight.setOnValueChangeListener(this);
        mColdLight.setMinValue(0);
        mColdLight.setMaxValue(mBrightnessGradient);
        updateLightView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLightObserver.stopObserving();
    }

    private void updateLightView() {
        mWarmLight.enable(getWarmLightEnable());
        if(getTempWarmBrightness() == -1) {//第一次开机
            mWarmLight.setValue(brightnessToGradient(getWarmBrightnessSetting()));
        } else {
            mWarmLight.setValue(brightnessToGradient(getTempWarmBrightness()));
        }

        mColdLight.enable(getColdLightEnable());
        if(getTempColdBrightness() == -1) {
            mColdLight.setValue(brightnessToGradient(getColdBrightnessSetting()));
        } else {
            mColdLight.setValue(brightnessToGradient(getTempColdBrightness()));
        }
    }

    @Override
    public void enable(View view, boolean enable) {
        if(view==mWarmLight) {
            setWarmLightEnable(enable);
            if(!enable) {
                setWarmBrightness(0);
            } else {
                setWarmBrightness(getTempWarmBrightness());
            }
        } else if(view==mColdLight) {
            setColdLightEnable(enable);
            if(!enable) {
                setColdBrightness(0);
            } else {
                setColdBrightness(getTempColdBrightness());
            }
        }
    }

    @Override
    public void valueChange(View view, int value) {
        if(view==mWarmLight) {
            int brightness = gradientToBrightness(value);
            setTempWarmBrightness(brightness);
            setWarmBrightness(brightness);
        } else if(view==mColdLight) {
            int brightness = gradientToBrightness(value);
            setTempColdBrightness(brightness);
            setColdBrightness(brightness);
        }
    }

    private void setWarmBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(),"screen_warm_brightness", brightness);
    }

    private void setColdBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(),"screen_cold_brightness", brightness);
    }

    private void setTempWarmBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(),"temp_warm_brightness", brightness);
    }

    private int getTempWarmBrightness() {
        return Settings.System.getInt(getContentResolver(),"temp_warm_brightness", -1);
    }

    private void setTempColdBrightness(int brightness) {
        Settings.System.putInt(getContentResolver(),"temp_cold_brightness", brightness);
    }

    private int getTempColdBrightness() {
        return Settings.System.getInt(getContentResolver(),"temp_cold_brightness", -1);
    }

    private void setWarmLightEnable(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"warm_light_enable", value);
    }

    private boolean getWarmLightEnable() {
        int value = Settings.System.getInt(getContentResolver(),"warm_light_enable", 1);
        boolean enable = value == 1 ? true : false;
        return enable;
    }

    private void setColdLightEnable(boolean enable) {
        int value = enable ? 1 : 0;
        Settings.System.putInt(getContentResolver(),"cold_light_enable", value);
    }

    private boolean getColdLightEnable() {
        int value = Settings.System.getInt(getContentResolver(),"cold_light_enable", 1);
        boolean enable = value == 1 ? true : false;
        return enable;
    }

    private int getColdBrightnessSetting() {
        return Settings.System.getInt(getContentResolver(),"screen_cold_brightness", -1);
    }

    private int getWarmBrightnessSetting() {
        return Settings.System.getInt(getContentResolver(),"screen_warm_brightness", -1);
    }

    private int brightnessToGradient(int brightness) {
        int per = 255 / mBrightnessGradient;
        int gradient = brightness/per;
        return gradient;
    }

    private int gradientToBrightness(int gradient) {
        int per = 255 / mBrightnessGradient;
        int brightness = gradient * per;
        return brightness;
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
            cr.registerContentObserver(TEMP_WARM_BRIGHTNESS, false, this);
            cr.registerContentObserver(TEMP_COLD_BRIGHTNESS, false, this);
            cr.registerContentObserver(WARM_LIGHT_ENABLE, false, this);
            cr.registerContentObserver(COLD_LIGHT_ENABLE, false, this);
        }

        public void stopObserving() {
            final ContentResolver cr = getContentResolver();
            cr.unregisterContentObserver(this);
        }
    }
}
