package com.jzzh.setting.display;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.StyleChooseView;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenStyleActivity extends BaseActivity {

    private static final int HOME_SCREEN_DEF_APP_STYLE = 0;
    private static final int HOME_SCREEN_WIDGET_STYLE = 1;

    private StyleChooseView mStyleChooseView;
    private List<StyleChooseView.PageData> mPages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_home_screen_style);
        mStyleChooseView = findViewById(R.id.style_choose_view);
        initPages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void initPages() {
        StyleChooseView.PageData widgetStylePage = new StyleChooseView.PageData(getString(R.string.home_screen_widget_style), R.drawable.home_screen_widget_on);
        widgetStylePage.setLeftButton(getString(R.string.home_background), this::startBackgroundSetting);
        widgetStylePage.setRightButton(getString(R.string.setting_display_apply), () -> {
            setHomeScreenStyle(HOME_SCREEN_WIDGET_STYLE);
            updateView();
        });
        mPages.add(widgetStylePage);

        StyleChooseView.PageData defaultAppStylePage = new StyleChooseView.PageData(getString(R.string.home_screen_default_style), R.drawable.home_screen_app_style);
        defaultAppStylePage.setLeftButton(getString(R.string.home_background), this::startBackgroundSetting);

        defaultAppStylePage.setRightButton(getString(R.string.setting_display_apply), () -> {
            setHomeScreenStyle(HOME_SCREEN_DEF_APP_STYLE);
            updateView();
        });
        mPages.add(defaultAppStylePage);
        mStyleChooseView.setPages(mPages);
    }

    private void updateView() {
        int homeScreenStyle = getHomeScreenStyle();

        for (int i = 0; i < mPages.size();i++) {
            mPages.get(i).setRightButton(getString(R.string.setting_display_apply));
            mPages.get(i).setRightButtonSelected(false);
        }

        if (homeScreenStyle == HOME_SCREEN_WIDGET_STYLE) {
            mPages.get(0).setRightButton(getString(R.string.setting_display_applied));
            mPages.get(0).setRightButtonSelected(true);
        } else {
            mPages.get(1).setRightButton(getString(R.string.setting_display_applied));
            mPages.get(1).setRightButtonSelected(true);
        }
        mStyleChooseView.updatePageDisplay();
    }

    private void setHomeScreenStyle(int style) {
        Settings.System.putInt(getContentResolver(),"space_widget_style_enable", style);
    }

    private int getHomeScreenStyle() {
        return Settings.System.getInt(getContentResolver(),"space_widget_style_enable", HOME_SCREEN_WIDGET_STYLE);
    }

    private void startBackgroundSetting() {
        Intent intent = new Intent();
        intent.setClassName(
                "com.inno.filemanager",
                "com.inno.filemanager.FileListActivity"
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("action_type", "show_dialog");
        intent.putExtra("category", "images");

        startActivity(intent);
    }

}
