package com.jzzh.setting.keygesture;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.Const.Gesture;
import com.jzzh.setting.Const.GestureType;
import com.jzzh.setting.R;

public class BottomGestureSettingActivity extends BaseActivity  implements View.OnClickListener{
    public static final String BOTTOM_LEFT_GESTURE_TYPE = "bottom_left_gesture_type";
    public static final String BOTTOM_MIDDLE_GESTURE_TYPE = "bottom_middle_gesture_type";
    public static final String BOTTOM_RIGHT_GESTURE_TYPE = "bottom_right_gesture_type";
    private TextView mTvLeft, mTvMiddle, mTvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture_bottom);
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.middle).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        mTvLeft = findViewById(R.id.tv_left);
        mTvMiddle = findViewById(R.id.tv_middle);
        mTvRight = findViewById(R.id.tv_right);

        setStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                showGestureDialog(Gesture.BOTTOM_LEFT);
                break;
            case R.id.middle:
                showGestureDialog(Gesture.BOTTOM_MIDDLE);
                break;
            case R.id.right:
                showGestureDialog(Gesture.BOTTOM_RIGHT);
                break;
        }
    }

    private void showGestureDialog(Gesture gesture) {
        GestureActionDialog dialog = new GestureActionDialog(this, gesture);
        dialog.setOnDismissListener(dialogInterface -> setStatus());
        dialog.show();
    }

    private void setStatus() {
        GestureType leftGesture = getBottomLeftGesture();
        GestureType middleGesture = getBottomMiddleGesture();
        GestureType rightGesture = getBottomRightGesture();

        mTvLeft.setText(getGestureTypeName(leftGesture));
        mTvMiddle.setText(getGestureTypeName(middleGesture));
        mTvRight.setText(getGestureTypeName(rightGesture));
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

    private GestureType getBottomLeftGesture() {
        int value = Settings.System.getInt(getContentResolver(), BOTTOM_LEFT_GESTURE_TYPE, 0);
        if (value >= 0 && value < GestureType.values().length) {
            return GestureType.values()[value];
        }
        return GestureType.NONE;
    }

    private GestureType getBottomMiddleGesture() {
        int value = Settings.System.getInt(getContentResolver(), BOTTOM_MIDDLE_GESTURE_TYPE, 0);
        if (value >= 0 && value < GestureType.values().length) {
            return GestureType.values()[value];
        }
        return GestureType.NONE;
    }

    private GestureType getBottomRightGesture() {
        int value = Settings.System.getInt(getContentResolver(), BOTTOM_RIGHT_GESTURE_TYPE, 0);
        if (value >= 0 && value < GestureType.values().length) {
            return GestureType.values()[value];
        }
        return GestureType.NONE;
    }

}
