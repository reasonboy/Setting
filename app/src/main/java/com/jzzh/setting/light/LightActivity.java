package com.jzzh.setting.light;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.TitleLayout;

public class LightActivity extends BaseActivity implements AdjustLayout.OnValueChangeListener,TitleLayout.OnEnableChangeListener {

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
    private TitleLayout mTitle;
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
        mTemperature.setOnValueChangeListener(this);
        mTemperature.setMinValue(0);
        mTemperature.setMaxValue(mBrightnessGradient);

        mBrightness = findViewById(R.id.adjust_brightness);
        mBrightness.setOnValueChangeListener(this);
        mBrightness.setMinValue(0);
        mBrightness.setMaxValue(mBrightnessGradient);

        mTitle = findViewById(R.id.title);
        mTitle.setOnEnableChangeListener(this);

        updateLightView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLightObserver.stopObserving();
    }

    private void updateLightView() {
        mTitle.enable(getBrightnessEnable());
        mBrightness.enable(getBrightnessEnable());
        mBrightnessLevel = getBrightnessLevel();
        mBrightness.setValue(getTempBrightnessLevel());

        mTemperature.enable(getBrightnessEnable());
        mTemperatureLevel = getTemperatureLevel();
        mTemperature.setValue(getTempTemperatureLevel());
    }

    @Override
    public void enable(View view, boolean enable) {
        if(view == mTitle) {
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
    private void gradientToBrightness1(int brightness,int temperature) {
        mWarmBrightness = 256*brightness*temperature / (mBrightnessGradient*mTemperatureGradient);
        mColdBrightness = 256*brightness*(mTemperatureGradient - temperature) / (mBrightnessGradient*mTemperatureGradient);
        if(mWarmBrightness < 0) mWarmBrightness = 0;
        if(mWarmBrightness > 256) mWarmBrightness = 255;
        if(mColdBrightness < 0) mColdBrightness = 0;
        if(mColdBrightness > 256) mColdBrightness = 255;
        changeLight();
    }

    private void gradientToBrightness(int brightness,int temperature) {

        if (brightness == 0)
        {
            mWarmBrightness = 0;
            mColdBrightness = 0;
        } else {
            //mWarmBrightness = 256*brightness*temperature / (mBrightnessGradient*mTemperatureGradient);
            //mColdBrightness = 256*brightness*(mTemperatureGradient - temperature) / (mBrightnessGradient*mTemperatureGradient);
            double ww = temperature / 32.0f;//暖光权重
            double wc = 1.0f - ww;//冷光权重

            // 计算总亮度值，范围0-255
            double total = (brightness / 32.0f) * 255.0f;

            //权重归一化(防止中间区域亮度下降)
            double sum = Math.sqrt(wc * wc + ww * ww);
            double nc = wc / sum;
            double nw = ww / sum;

            double cold = nc * total;
            double warm = nw * total;

            //可选Gamma校正(提高人眼均匀度)
            double gamma = 2.2f;
            if (total > 0) {
                cold = Math.pow(cold / total, 1.0f / gamma) * total;
                warm = Math.pow(warm / total, 1.0f / gamma) * total;
            }
            mColdBrightness = (int)Math.round(cold);
            mWarmBrightness = (int)Math.round(warm);
            if(mWarmBrightness < 4) mWarmBrightness = 4;
            if(mWarmBrightness > 255) mWarmBrightness = 255;
            if(mColdBrightness < 4) mColdBrightness = 4;
            if(mColdBrightness > 255) mColdBrightness = 255;
        }

        Log.d("ZhLightDialog","gradientToBrightness: brightness="+brightness+",temperature="+temperature
                +",mWarmBrightness="+mWarmBrightness+",mColdBrightness="+mColdBrightness);
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
