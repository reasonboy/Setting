package com.jzzh.setting.lock;

import static com.jzzh.setting.lock.LockPasswordSettingDialog.DialogType.DELETE_PASSWORD;
import static com.jzzh.setting.lock.LockPasswordSettingDialog.DialogType.SET_PASSWORD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LockScreenActivity extends BaseActivity {
    private static final String TAG = LockScreenActivity.class.getSimpleName();
    private ZhCheckBox mCheckBox;
    private Object mLockPatternUtils;
    private int mUid;
    private int mQuality;
    private LockPasswordSettingDialog mPasswordSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        mLockPatternUtils = newLockPatternUtils(this);
        mUid = getCurrentUserId();
        mQuality = getActivePasswordQuality(mLockPatternUtils, mUid);
        mCheckBox = findViewById(R.id.zh_lock_screen_switch);
        if (mQuality > 0) {
            mCheckBox.setCheck(true);
        } else {
            mCheckBox.setCheck(false);
        }

        mCheckBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                Log.d(TAG, "checked:" + checked);
                if (checked) {
                    mCheckBox.setCheck(false);
                    mPasswordSettingDialog.show();
                    mPasswordSettingDialog.setDialogType(SET_PASSWORD);
                } else {
                    mCheckBox.setCheck(true);
                    mPasswordSettingDialog.show();
                    mPasswordSettingDialog.setDialogType(DELETE_PASSWORD);
                }
            }
        });
        mPasswordSettingDialog = new LockPasswordSettingDialog(LockScreenActivity.this, R.style.ZhDialog, new LockPasswordSettingDialog.DialogCallback() {
            @Override
            public void callBackData(LockPasswordSettingDialog.DialogType dialogType) {
                boolean check = mCheckBox.getCheck();
                mCheckBox.setCheck(!check);  // 操作成功后，才切换状态
            }
        }, mLockPatternUtils, mUid);
    }

    private Object newLockPatternUtils(Context context) {
        Object lockPatternUtils = null;
        Class clazz = null;
        try {
            clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
            Constructor c = clazz.getConstructor(Context.class);
            lockPatternUtils = c.newInstance(context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return lockPatternUtils;
    }

    public static int getCurrentUserId() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                @SuppressLint("DiscouragedPrivateApi") final Method m = android.os.UserHandle.class.getDeclaredMethod("myUserId");
                m.setAccessible(true);
                final Object o = m.invoke(null);
                if (o instanceof Integer) {
                    return (Integer) o;
                }
            } catch (Exception ignored) {
            }
        }
        return -1;
    }

    public int getActivePasswordQuality(Object lockPatternUtils, int userId) {
        Class clazz = null;
        int id = -1;
        try {
            clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
            Method getActivePasswordQuality = clazz.getMethod("getActivePasswordQuality", int.class);
            id = (int) getActivePasswordQuality.invoke(lockPatternUtils, userId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return id;
    }
}
