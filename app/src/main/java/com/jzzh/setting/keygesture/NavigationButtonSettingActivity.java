package com.jzzh.setting.keygesture;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.jzzh.setting.BaseActivityNoNav;
import com.jzzh.setting.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationButtonSettingActivity extends BaseActivityNoNav {

    private static final String NAVIGATION_BAR_ICON = "navigation_bar_icon";
    private static final String DEFAULT_NAVIGATION_BAR_ICONS = "task,back,home,refresh,mini";
    private static final int MAX_ICON_COUNT = 5;
    private static final int[] ICON_VIEW_IDS = {
            R.id.navigation_button_icon_1,
            R.id.navigation_button_icon_2,
            R.id.navigation_button_icon_3,
            R.id.navigation_button_icon_4,
            R.id.navigation_button_icon_5
    };

    private final List<NavigationIcon> mSelectedIcons = new ArrayList<>();
    private View mSelectedRow;
    private View mAvailableRow;
    private View mAvailableSecondRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_button_setting);
        setNavText(getString(R.string.setting_navigation_button_card_title));
        mSelectedRow = findViewById(R.id.navigation_button_selected_row);
        mAvailableRow = findViewById(R.id.navigation_button_available_row);
        mAvailableSecondRow = findViewById(R.id.navigation_button_available_second_row);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIcons();
        updateIconRows();
    }

    private void loadIcons() {
        String value = Settings.System.getString(getContentResolver(), NAVIGATION_BAR_ICON);
        if (TextUtils.isEmpty(value)) {
            value = DEFAULT_NAVIGATION_BAR_ICONS;
        }

        mSelectedIcons.clear();
        for (String name : value.split(",")) {
            NavigationIcon icon = NavigationIcon.fromName(name.trim());
            if (icon != null && !mSelectedIcons.contains(icon)) {
                mSelectedIcons.add(icon);
                if (mSelectedIcons.size() == MAX_ICON_COUNT) {
                    break;
                }
            }
        }
    }

    private void saveIcons() {
        List<String> names = new ArrayList<>();
        for (NavigationIcon icon : mSelectedIcons) {
            names.add(icon.name);
        }
        boolean saved = Settings.System.putString(getContentResolver(), NAVIGATION_BAR_ICON,
                TextUtils.join(",", names));
        if (!saved) {
            loadIcons();
        }
        updateIconRows();
    }

    private void updateIconRows() {
        List<NavigationIcon> availableIcons = new ArrayList<>();
        for (NavigationIcon icon : NavigationIcon.values()) {
            if (!mSelectedIcons.contains(icon)) {
                availableIcons.add(icon);
            }
        }

        showIconRow(mSelectedRow, mSelectedIcons, 0, true);
        showIconRow(mAvailableRow, availableIcons, 0, false);
        if (availableIcons.size() > MAX_ICON_COUNT) {
            mAvailableSecondRow.setVisibility(View.VISIBLE);
            showIconRow(mAvailableSecondRow, availableIcons, MAX_ICON_COUNT, false);
        } else {
            mAvailableSecondRow.setVisibility(View.GONE);
        }
    }

    private void showIconRow(View row, List<NavigationIcon> icons, int start, boolean selected) {
        for (int slot = 0; slot < ICON_VIEW_IDS.length; slot++) {
            ImageView image = row.findViewById(ICON_VIEW_IDS[slot]);
            int index = start + slot;
            if (index >= icons.size()) {
                image.setVisibility(View.INVISIBLE);
                continue;
            }

            NavigationIcon icon = icons.get(index);
            image.setVisibility(View.VISIBLE);
            image.setImageResource(icon.drawableResId);
            image.setContentDescription(getString(icon.titleResId));
            if (selected) {
                image.setOnClickListener(view -> removeIcon(icon));
                image.setOnLongClickListener(view -> ViewCompat.startDragAndDrop(
                        view, null, new View.DragShadowBuilder(view), icon, 0));
                image.setOnDragListener((view, event) -> onIconDrag(event, icon));
            } else {
                image.setOnClickListener(view -> addIcon(icon));
            }
        }
    }

    private void addIcon(NavigationIcon icon) {
        if (mSelectedIcons.size() == MAX_ICON_COUNT) {
            Toast.makeText(this, R.string.navigation_button_limit_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        mSelectedIcons.add(icon);
        saveIcons();
    }

    private void removeIcon(NavigationIcon icon) {
        if (mSelectedIcons.size() == 1) {
            Toast.makeText(this, R.string.navigation_button_minimum_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        mSelectedIcons.remove(icon);
        saveIcons();
    }

    private boolean onIconDrag(DragEvent event, NavigationIcon target) {
        if (!(event.getLocalState() instanceof NavigationIcon)) {
            return false;
        }
        if (event.getAction() == DragEvent.ACTION_DROP) {
            NavigationIcon source = (NavigationIcon) event.getLocalState();
            if (source != target) {
                int targetIndex = mSelectedIcons.indexOf(target);
                mSelectedIcons.remove(source);
                mSelectedIcons.add(targetIndex, source);
                saveIcons();
            }
        }
        return true;
    }

    private enum NavigationIcon {
        TASK("task", R.drawable.zh_navigation_task, R.string.gesture_task_manager),
        BACK("back", R.drawable.zh_navigation_back, R.string.gesture_back),
        HOME("home", R.drawable.zh_navigation_home, R.string.gesture_home),
        REFRESH("refresh", R.drawable.zh_navigation_refresh, R.string.gesture_refresh),
        MINI("mini", R.drawable.zh_navigation_mini, R.string.gesture_quick_mini),
        KEY("key", R.drawable.zh_navigation_key, R.string.gesture_eink_center),
        VOLUME("volume", R.drawable.zh_navigation_volume, R.string.gesture_key_setting),
        SETTING("setting", R.drawable.zh_navigation_setting, R.string.setting);

        final String name;
        final int drawableResId;
        final int titleResId;

        NavigationIcon(String name, int drawableResId, int titleResId) {
            this.name = name;
            this.drawableResId = drawableResId;
            this.titleResId = titleResId;
        }

        static NavigationIcon fromName(String name) {
            for (NavigationIcon icon : values()) {
                if (icon.name.equals(name)) {
                    return icon;
                }
            }
            return null;
        }
    }
}
