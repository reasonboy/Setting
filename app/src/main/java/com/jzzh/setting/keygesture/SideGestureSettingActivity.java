package com.jzzh.setting.keygesture;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.Const.Gesture;
import com.jzzh.setting.Const.GestureType;
import com.jzzh.setting.GestureChooseView;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SideGestureSettingActivity extends BaseActivity {
    public static final String KEY_PACK_GESTURE_ENABLE = "key_pack_gesture_enable";
    public static final String LEFT_GESTURE_ENABLE = "left_gesture_enable";
    public static final String RIGHT_GESTURE_ENABLE = "right_gesture_enable";
    public static final String LEFT_GESTURE_TYPE = "left_gesture_type";
    public static final String RIGHT_GESTURE_TYPE = "right_gesture_type";

    private GestureChooseView mGestureChooseView;
    private List<GestureChooseView.PageData> mPages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture_side);
        mGestureChooseView = findViewById(R.id.gesture_choose_view);
        initPages();
        updateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void initPages() {
        GestureChooseView.PageData leftSideGesturePage = new GestureChooseView.PageData(getString(R.string.setting_left_side_gesture), R.drawable.setting_k_g_gesturesetting_leftsidegesture);
        leftSideGesturePage.setCheckBox(getString(R.string.setting_side_gesture), new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setLeftGestureEnable(checked);
                mPages.get(0).setCheckBoxChecked(checked);
            }
        });
        leftSideGesturePage.setBottomViewText(getGestureTypeName(getLeftGestureType()));
        leftSideGesturePage.setBottomViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGestureDialog(Gesture.LEFT, 0);
            }
        });
        mPages.add(leftSideGesturePage);

        GestureChooseView.PageData rightSideGesturePage = new GestureChooseView.PageData(getString(R.string.setting_right_side_gesture), R.drawable.setting_k_g_gesturesetting_rightsidegesture);
        rightSideGesturePage.setCheckBox(getString(R.string.setting_side_gesture),  new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setRightGestureEnable(checked);
                mPages.get(1).setCheckBoxChecked(checked);
            }
        });
        rightSideGesturePage.setBottomViewText(getGestureTypeName(getRightGestureType()));
        rightSideGesturePage.setBottomViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGestureDialog(Gesture.RIGHT, 1);
            }
        });
        mPages.add(rightSideGesturePage);

        GestureChooseView.PageData keyPackGesturePage = new GestureChooseView.PageData(getString(R.string.setting_keypack_gesture), R.drawable.setting_k_g_gesturesetting_keypackgesture);
        keyPackGesturePage.setCheckBox(getString(R.string.setting_keypack_gesture),  new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                setKeyPackGestureEnable(checked);
                mPages.get(2).setCheckBoxChecked(checked);
            }
        });
        keyPackGesturePage.setBottomViewText(getString(R.string.setting_keypack_information));
        keyPackGesturePage.setBottomViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformationDialog();
            }
        });

        mPages.add(keyPackGesturePage);

        mGestureChooseView.setPages(mPages);
    }

    private void updateView() {
        for (int i = 0; i < mPages.size();i++) {
            mPages.get(i).setCheckBoxChecked(false);
        }

        mPages.get(0).setCheckBoxChecked(getLeftGestureEnable());
        mPages.get(1).setCheckBoxChecked(getRightGestureEnable());
        mPages.get(2).setCheckBoxChecked(getKeyPackGestureEnable());

        mGestureChooseView.updatePageDisplay();
    }

    private boolean getLeftGestureEnable() {
        return Settings.System.getInt(getContentResolver(), LEFT_GESTURE_ENABLE, -1) != 0;
    }

    private boolean getRightGestureEnable() {
        return Settings.System.getInt(getContentResolver(), RIGHT_GESTURE_ENABLE, -1) != 0;
    }

    private boolean getKeyPackGestureEnable() {
        return Settings.System.getInt(getContentResolver(), KEY_PACK_GESTURE_ENABLE, -1) != 0;
    }

    private void setLeftGestureEnable(boolean b) {
        if (b) {
            Settings.System.putInt(getContentResolver(), LEFT_GESTURE_ENABLE, 1);
        } else {
            Settings.System.putInt(getContentResolver(), LEFT_GESTURE_ENABLE, 0);
        }
    }

    private void setRightGestureEnable(boolean b) {
        if (b) {
            Settings.System.putInt(getContentResolver(), RIGHT_GESTURE_ENABLE, 1);
        } else {
            Settings.System.putInt(getContentResolver(), RIGHT_GESTURE_ENABLE, 0);
        }
    }

    private void setKeyPackGestureEnable(boolean b) {
        if (b) {
            Settings.System.putInt(getContentResolver(), KEY_PACK_GESTURE_ENABLE, 1);
        } else {
            Settings.System.putInt(getContentResolver(), KEY_PACK_GESTURE_ENABLE, 0);
        }
    }

    private GestureType getLeftGestureType() {
        int value = Settings.System.getInt(getContentResolver(), LEFT_GESTURE_TYPE, 0);
        if (value >= 0 && value < GestureType.values().length) {
            return GestureType.values()[value];
        }
        return GestureType.NONE;
    }

    private GestureType getRightGestureType() {
        int value = Settings.System.getInt(getContentResolver(), RIGHT_GESTURE_TYPE, 0);
        if (value >= 0 && value < GestureType.values().length) {
            return GestureType.values()[value];
        }
        return GestureType.NONE;
    }

    private String getGestureTypeName(GestureType type) {
        switch (type) {
            case NONE:
                return getString(R.string.gesture_none);
            case BACK:
                return getString(R.string.gesture_back);
            case HOME:
                return getString(R.string.gesture_home);
            case REFRESH:
                return getString(R.string.gesture_refresh);
            case TASK_MANAGER:
                return getString(R.string.gesture_task_manager);
            case EINK_CENTER:
                return getString(R.string.gesture_eink_center);
            case KEY_SETTINGS:
                return getString(R.string.gesture_key_setting);
            case USER_APP:
                return getString(R.string.gesture_user_app);
            default:
                return getString(R.string.gesture_none);
        }
    }

    private void showGestureDialog(Gesture gesture, int pageIndex) {
        GestureActionDialog dialog = new GestureActionDialog(this, gesture);
        dialog.setOnGestureSelectedListener(new GestureActionDialog.OnGestureSelectedListener() {
            @Override
            public void onGestureSelected(GestureType gestureType) {
                mPages.get(pageIndex).setBottomViewText(getGestureTypeName(gestureType));
                mGestureChooseView.updatePageDisplay();
            }
        });
        dialog.show();
    }

    private void showInformationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_keypack_information);
        
        Locale currentLocale = getResources().getConfiguration().locale;
        boolean isKoreaLocale = Locale.KOREAN.toString().equals(currentLocale.toString());
        
        ImageView ivMain = dialog.findViewById(R.id.iv_main);
        ivMain.setImageResource(
                isKoreaLocale ? R.drawable.setting_k_g_gesturesetting_keypackinfo
                        : R.drawable.setting_k_g_gesturesetting_keypackinfo_en
        );
        
        ImageView ivExit = dialog.findViewById(R.id.iv_exit);
        ivExit.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

}
