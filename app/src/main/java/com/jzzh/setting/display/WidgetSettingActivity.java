package com.jzzh.setting.display;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.adapter.ListRadioBtnMenuAdapter;
import com.jzzh.setting.utils.UtilSpaceUserSettings;
import com.jzzh.tools.ZhCheckBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WidgetSettingActivity extends BaseActivity {
    private static final String TAG = WidgetSettingActivity.class.getSimpleName();
    private ZhCheckBox mToggleSearchAlarm, mToggleBottomApps, mToggleLeftSlide;
    private ImageView mSearchAlarmInfo, mBottomAppsInfo, mLeftSlideInfo;
    private TextView mTvSearchAlarmNotice;
    private RecyclerView mRecyclerView;
    private ListRadioBtnMenuAdapter mListRadioBtnMenuAdapter;
    private List<String> listMenus = new ArrayList<>();

    public boolean isWidgetStyle = true;
    private boolean isUseSearchAlarm = true;
    private boolean isUseBottomApps = true;
    private boolean isUseLeftSlide = true;

    private UtilSpaceUserSettings mUtilSpaceUserSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_setting);
        mUtilSpaceUserSettings = new UtilSpaceUserSettings(this);

        // Search and alarm
        mSearchAlarmInfo = findViewById(R.id.btn_search_alarm_info);
        mSearchAlarmInfo.setOnClickListener(v -> {
            makeInfoDialog(R.string.title_search_alarm, -1,
                    new int[] {R.drawable.setting_display_widget_message},
                    new int[] {R.string.detail_desc_search_alarm_on});
        });
        mToggleSearchAlarm = findViewById(R.id.btn_toggle_search_alarm);
        mToggleSearchAlarm.setOnZhCheckedChangeListener(checked -> {
            toggleSearchAlarmSetting();
        });
        mTvSearchAlarmNotice = findViewById(R.id.tv_notice_search_alarm);

        // Home bottom apps
        mBottomAppsInfo = findViewById(R.id.btn_bottom_apps_info);
        mBottomAppsInfo.setOnClickListener(v -> {
            makeInfoDialog(R.string.title_bottom_pinned_app, -1,
                    new int[] {R.drawable.setting_display_widget_5app},
                    new int[] {R.string.detail_desc_bottom_pinned_app});
        });
        mToggleBottomApps = findViewById(R.id.btn_toggle_bottom_apps);
        mToggleBottomApps.setOnZhCheckedChangeListener(checked -> {
            toggleBottomAppsSetting();
        });

        // Home left slide function
        mLeftSlideInfo = findViewById(R.id.btn_left_slide_function_info);
        mLeftSlideInfo.setOnClickListener(v -> {
            makeInfoDialog(R.string.title_left_slide, R.string.sub_title_left_slide_app,
                    new int[] {R.drawable.setting_display_widget_leftslide01, R.drawable.setting_display_widget_leftslide02},
                    new int[] {R.string.detail_desc_left_slide_app1, R.string.detail_desc_left_slide_app2});
        });
        mToggleLeftSlide = findViewById(R.id.btn_toggle_left_slide_function);
        mToggleLeftSlide.setOnZhCheckedChangeListener(checked -> {
            toggleLeftSlideSetting();
        });

        // left slide function menu
        mRecyclerView = findViewById(R.id.rv_left_slide_function_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        int currentValue = mUtilSpaceUserSettings.getHomeLeftSlideFunction();
        int selectedPosition = getLSFSelectedPosition(currentValue);
        initLSFMenus();
        mListRadioBtnMenuAdapter = new ListRadioBtnMenuAdapter(this, listMenus, selectedPosition, position -> {
            int value = getLSFSettingValueByPosition(position);
            mUtilSpaceUserSettings.setHomeLeftSlideFunction(value);
        });
        mRecyclerView.setAdapter(mListRadioBtnMenuAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        getSettingValues();
    }

    private void getSettingValues() {
        int settingNameLogoValue = mUtilSpaceUserSettings.getWidgetStyleEnable();
        if (settingNameLogoValue != -1) {
            isWidgetStyle = settingNameLogoValue == 1;
        } else {
            Log.e(TAG ,"getNameLogoEnable Failed!");
        }

        int settingWidgetValue = mUtilSpaceUserSettings.getWidgetEnable();
        if (settingWidgetValue != -1) {
            isUseSearchAlarm = settingWidgetValue == 1;
        } else {
            Log.e(TAG ,"getWidgetEnable Failed!");
        }

        isUseBottomApps = mUtilSpaceUserSettings.getHomeBottomPinnedAppEnable() == 1;
        isUseLeftSlide = mUtilSpaceUserSettings.getHomeLeftSlideEnable() == 1;

        // toggle menu init setup
        searchAlarmViewUpdate();
        mToggleBottomApps.setCheck(isUseBottomApps);
        mToggleLeftSlide.setCheck(isUseLeftSlide);
        mRecyclerView.setVisibility(isUseLeftSlide ? View.VISIBLE : View.GONE);
    }

    private void searchAlarmViewUpdate() {
        mToggleSearchAlarm.setEnabled(isWidgetStyle);
        mToggleSearchAlarm.setCheck(isWidgetStyle && isUseSearchAlarm);
        mTvSearchAlarmNotice.setText(isWidgetStyle ? R.string.desc_search_alarm_on : R.string.desc_search_alarm_off);
    }

    private void toggleSearchAlarmSetting() {
        if (isWidgetStyle) {
            isUseSearchAlarm = !isUseSearchAlarm;
            mUtilSpaceUserSettings.setWidgetEnable(isUseSearchAlarm);
        }
        mToggleSearchAlarm.setCheck(isWidgetStyle && isUseSearchAlarm);
    }

    private void toggleBottomAppsSetting() {
        isUseBottomApps = !isUseBottomApps;

        mToggleBottomApps.setCheck(isUseBottomApps);
        mUtilSpaceUserSettings.setHomeBottomPinnedAppEnable(isUseBottomApps? 1 : 0);
    }

    private void toggleLeftSlideSetting() {
        isUseLeftSlide = !isUseLeftSlide;

        mToggleLeftSlide.setCheck(isUseLeftSlide);
        mUtilSpaceUserSettings.setHomeLeftSlideEnable(isUseLeftSlide? 1 : 0);
        mRecyclerView.setVisibility(isUseLeftSlide ? View.VISIBLE : View.GONE);
    }

    private void initLSFMenus() {
        listMenus = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_left_slide_function)));
    }

    private int getLSFSettingValueByPosition(int position) {
        int[] values = getResources().getIntArray(R.array.array_left_slide_function_values);
        return values[position];
    }

    private int getLSFSelectedPosition(int v) {
        int[] values = getResources().getIntArray(R.array.array_left_slide_function_values);
        for (int i = 0; i < values.length; i++) {
            if (v == values[i]) {
                return i;
            }
        }
        return 0;
    }
}
