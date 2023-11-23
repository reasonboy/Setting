package com.jzzh.setting.display;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

public class HomeScreenUserActivity extends BaseActivity implements View.OnClickListener,ZhCheckBox.OnZhCheckedChangeListener {

    private static final int HOME_SCREEN_DEF_APP_STYLE = 0;
    private static final int HOME_SCREEN_WIDGET_STYLE = 1;
    private static final int HOME_SCREEN_WIDGET_DISABLE = 0;
    private static final int HOME_SCREEN_WIDGET_ENABLE = 1;

    private HomeScreenUserItem mWidgetStyle, mDefaultAppStyle;
    private TextView mWidgetDescribe;
    private ZhCheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_home_screen);
        mWidgetStyle = findViewById(R.id.widget_style);
        mWidgetStyle.setOnClickListener(this);
        mWidgetStyle.setTitle(R.string.home_screen_widget_style);

        mDefaultAppStyle = findViewById(R.id.default_app_style);
        mDefaultAppStyle.setOnClickListener(this);
        mDefaultAppStyle.setTitle(R.string.home_screen_default_style);
        mDefaultAppStyle.setPreview(R.drawable.home_scree_default);

        mWidgetDescribe = findViewById(R.id.widget_setting_describe);
        mCheckBox = findViewById(R.id.widget_setting_switch);
        mCheckBox.setOnZhCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        int homeScreenStyle = getHomeScreenStyle();
        int widgetEnable = getWidgetEnable();
        if(homeScreenStyle == HOME_SCREEN_WIDGET_STYLE) {
            mWidgetStyle.select(true);
            mDefaultAppStyle.select(false);
            mCheckBox.setEnable(true);
            mCheckBox.setCheck(widgetEnable == HOME_SCREEN_WIDGET_ENABLE);
            mWidgetDescribe.setText(R.string.home_screen_widget_setting_describe1);
            if(widgetEnable == HOME_SCREEN_WIDGET_ENABLE) {
                mWidgetStyle.setPreview(R.drawable.home_scree_widget_on);
            } else {
                mWidgetStyle.setPreview(R.drawable.home_scree_widget_off);
            }
        } else {
            mWidgetStyle.select(false);
            mDefaultAppStyle.select(true);
            mCheckBox.setEnable(false);
            mCheckBox.setCheck(false);
            mWidgetDescribe.setText(R.string.home_screen_widget_setting_describe2);
        }
    }


    private void setHomeScreenStyle(int style) {
        Settings.System.putInt(getContentResolver(),"space_widget_style_enable", style);
    }

    private int getHomeScreenStyle() {
        return Settings.System.getInt(getContentResolver(),"space_widget_style_enable", HOME_SCREEN_WIDGET_STYLE);
    }

    private void setWidgetEnable(int enable) {
        Settings.System.putInt(getContentResolver(),"space_widget_enable", enable);
    }

    private int getWidgetEnable() {
        return Settings.System.getInt(getContentResolver(),"space_widget_enable", HOME_SCREEN_WIDGET_ENABLE);
    }

    @Override
    public void onClick(View view) {
        if(view == mWidgetStyle) {
            setHomeScreenStyle(HOME_SCREEN_WIDGET_STYLE);
        } else {
            setHomeScreenStyle(HOME_SCREEN_DEF_APP_STYLE);
        }
        updateView();
    }

    @Override
    public void onCheckedChanged(boolean checked) {
        if(checked) {
            setWidgetEnable(HOME_SCREEN_WIDGET_ENABLE);
        } else {
            setWidgetEnable(HOME_SCREEN_WIDGET_DISABLE);
        }
        updateView();
    }
}
