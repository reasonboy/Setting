package com.jzzh.setting.lock;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jzzh.setting.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LockPasswordSettingDialog extends Dialog implements View.OnClickListener {
    private DialogCallback mDialogCallback;
    private EditText mPassword;
    private EditText mConfirm;
    private TextView mConfirmTitle;
    private ImageView mShowPassword;
    private DialogType mDialogType;
    private Object mLockPatternUtils;
    private Object mNewCredential;
    private int mUid;
    private boolean mPasswordVisible = false;

    public LockPasswordSettingDialog(@NonNull Context context, int themeResId, DialogCallback dialogCallback, Object lockPatternUtils, int uid) {
        super(context, themeResId);
        mDialogCallback = dialogCallback;
        mLockPatternUtils = lockPatternUtils;
        mUid = uid;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.password_setting_dialog);
        findViewById(R.id.password_setting_dialog_cancle).setOnClickListener(this);
        findViewById(R.id.password_setting_dialog_confirm).setOnClickListener(this);
        mPassword = findViewById(R.id.password_setting_password);
        mPassword.requestFocus();
        mConfirmTitle = findViewById(R.id.confirm_title);
        mConfirm = findViewById(R.id.password_setting_confirm_password);
        mShowPassword = findViewById(R.id.password_setting_dialog_show_password);
        mShowPassword.setOnClickListener(this);
        setPasswordVisible(mPasswordVisible);
        if (mDialogType == DialogType.DELETE_PASSWORD) {
            setConfirmVisible(false);
        }
    }

    @Override
    public void onClick(View view) {
        CharSequence passwordText = mPassword.getText();
        CharSequence confirmText = mConfirm.getText();

        switch (view.getId()) {
            case R.id.password_setting_dialog_show_password:
                mPasswordVisible = !mPasswordVisible;
                setPasswordVisible(mPasswordVisible);
                break;
            case R.id.password_setting_dialog_cancle:
                dismiss();
                ((Editable) passwordText).clear();
                ((Editable) confirmText).clear();
                setPasswordVisible(false);
                break;
            case R.id.password_setting_dialog_confirm:
                if (mDialogType == DialogType.DELETE_PASSWORD) {
                    if (passwordText.toString().length() < 6) {
                        Toast.makeText(getContext(), R.string.msg_pwd_6_digit, Toast.LENGTH_SHORT).show();
                    } else {
                        if (disableLockScreen(mLockPatternUtils, passwordText, mUid)) {
                            dismiss();
                            ((Editable) passwordText).clear();
                            setPasswordVisible(false);
                            if (mDialogCallback != null) {
                                mDialogCallback.callBackData(DialogType.DELETE_PASSWORD);
                            }
                        } else {
                            ((Editable) passwordText).clear();
                            Toast.makeText(getContext(), R.string.msg_pwd_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (passwordText.toString().length() < 6 || confirmText.toString().length() < 6) {
                        Toast.makeText(getContext(), R.string.msg_pwd_6_digit, Toast.LENGTH_SHORT).show();
                    } else if (!passwordText.toString().equals(confirmText.toString())) {
                        Toast.makeText(getContext(), R.string.msg_pwd_different, Toast.LENGTH_SHORT).show();
                    } else {
                        mNewCredential = createPin(passwordText);
                        Object saveCredential = createNone();
                        if (setLockCredential(mLockPatternUtils, mNewCredential, saveCredential, mUid)) {
                            dismiss();
                            ((Editable) passwordText).clear();
                            ((Editable) confirmText).clear();
                            setPasswordVisible(false);
                            if (mDialogCallback != null) {
                                mDialogCallback.callBackData(DialogType.SET_PASSWORD);
                            }
                        } else {
                            ((Editable) passwordText).clear();
                            ((Editable) confirmText).clear();
                            Toast.makeText(getContext(), R.string.msg_pwd_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    public void setDialogType(DialogType dialogType) {
        mDialogType = dialogType;
        setConfirmVisible(mDialogType != DialogType.DELETE_PASSWORD);
        mPassword.requestFocus();  // 弹出键盘
    }

    public interface DialogCallback {
        void callBackData(DialogType dialogType);
    }

    public enum DialogType {
        SET_PASSWORD,
        DELETE_PASSWORD
    }

    private void setConfirmVisible(boolean visible) {
        if (visible) {
            mConfirmTitle.setVisibility(View.VISIBLE);
            mConfirm.setVisibility(View.VISIBLE);
        } else {
            mConfirmTitle.setVisibility(View.GONE);
            mConfirm.setVisibility(View.GONE);
        }
    }

    private void setPasswordVisible(boolean visible) {
        if(visible) {
            mShowPassword.setImageResource(com.jzzh.network.R.drawable.check_on);
            mPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
            mPassword.setSelection(mPassword.getText().toString().length());
            mConfirm.setInputType(InputType.TYPE_CLASS_NUMBER);
            mConfirm.setSelection(mConfirm.getText().toString().length());
        } else {
            mShowPassword.setImageResource(com.jzzh.network.R.drawable.check_off);
            mPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mPassword.setSelection(mPassword.getText().toString().length());
            mConfirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            mConfirm.setSelection(mConfirm.getText().toString().length());
        }
    }

    private boolean disableLockScreen(Object lockPatternUtils, CharSequence oldPasswordSeq, int uid) {
        boolean success = false;
        Object nonePassword = createNone();
        Object oldPassword = createPin(oldPasswordSeq);
        success = setLockCredential(lockPatternUtils, nonePassword, oldPassword, uid);
        setLockScreenDisabled(lockPatternUtils, true, uid);
        return success;
    }

    private Object createNone() {
        Class clazz = null;
        Object lockscreenCredential = null;

        try {
            clazz = Class.forName("com.android.internal.widget.LockscreenCredential");
            Method createNone = clazz.getMethod("createNone");
            lockscreenCredential = createNone.invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return lockscreenCredential;
    }

    private Object createPin(@NonNull CharSequence pin) {
        Class clazz = null;
        Object lockscreenCredential = null;
        try {
            clazz = Class.forName("com.android.internal.widget.LockscreenCredential");
            Method createPin = clazz.getMethod("createPin", CharSequence.class);
            lockscreenCredential = createPin.invoke(null, pin);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return lockscreenCredential;
    }

    public boolean setLockCredential(Object lockPatternUtils, @NonNull Object newCredential,
                                     @NonNull Object savedCredential, int userHandle) {
        Class clazz = null;
        Class lockscreenCredentialClazz = null;
        Boolean success = false;
        try {
            clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
            lockscreenCredentialClazz = Class.forName("com.android.internal.widget.LockscreenCredential");
            Method setLockCredential = clazz.getMethod("setLockCredential", lockscreenCredentialClazz, lockscreenCredentialClazz, int.class);
            success = (Boolean) setLockCredential.invoke(lockPatternUtils, newCredential, savedCredential, userHandle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return success;
    }

    public void setLockScreenDisabled(Object lockPatternUtils, boolean disable, int userId) {
        Class clazz = null;

        try {
            clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
            Method setLockScreenDisabled = clazz.getMethod("setLockScreenDisabled", boolean.class, int.class);
            setLockScreenDisabled.invoke(lockPatternUtils, disable, userId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
