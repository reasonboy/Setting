package com.jzzh.setting.network.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzzh.setting.R;

public class ConnectDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String mWifiName,mEncryption;
    private TextView mWifiNameTx;
    private EditText mPasswordEt;
    private ImageView mShowPassword;
    private Button mCancle,mConnect;
    private DialogCallback mCallback;
    private boolean mPasswordVisible = false;
    private boolean mIsSavedNet;
    private int mSignalLevel;

    private static final String WPA = "WPA";

    public ConnectDialog(Context context, int style, DialogCallback callback, String wifiName, String enc, boolean isSavedNet, int signalLevel) {
        super(context, style);
        mContext = context;
        mCallback = callback;
        mWifiName = wifiName;
        mEncryption = enc;
        mIsSavedNet = isSavedNet;
        mSignalLevel = signalLevel;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_connect_dialog);
        mWifiNameTx = findViewById(R.id.wifi_connect_dialog_name);
        mWifiNameTx.setText(mWifiName);
        mPasswordEt = findViewById(R.id.wifi_connect_connect_password);
        mShowPassword = findViewById(R.id.wifi_connect_dialog_show_password);
        mShowPassword.setOnClickListener(this);
        mCancle = findViewById(R.id.wifi_connect_dialog_cancle);
        mCancle.setOnClickListener(this);
        mConnect = findViewById(R.id.wifi_connect_dialog_connect);
        mConnect.setOnClickListener(this);
        setPasswordVisible(mPasswordVisible);
        if (mIsSavedNet) {
            mPasswordEt.setVisibility(View.GONE);
            LinearLayout ll = findViewById(R.id.ll_enable_show_password);
            ll.setVisibility(View.GONE);
            TextView passwordTv = findViewById(R.id.tv_password);
            passwordTv.setVisibility(View.GONE);
            TextView deleteThisInternet = findViewById(R.id.wifi_delete_this_internet);
            deleteThisInternet.setVisibility(View.VISIBLE);
            deleteThisInternet.setOnClickListener(this);
        } else {  // 未保存的wifi，输入密码交互
            mConnect.setEnabled(false);
            mConnect.setTextAppearance(mContext, R.style.NegativeDialogButtonStyle);
            mConnect.setBackgroundResource(R.drawable.rectangle_negative);
            mPasswordEt.addTextChangedListener(textWatcher);
        }
        TextView signalStrengthTv = findViewById(R.id.tv_signal_strength);
        String signalStrength = getSignalStrengthByLevel(mSignalLevel);
        signalStrengthTv.setText(String.format("%s: %s", mContext.getString(R.string.wifi_detail_signal_strength), signalStrength));
    }

    private void setPasswordVisible(boolean visible) {
        if(visible) {
            mShowPassword.setImageResource(R.drawable.check_on);
            mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mPasswordEt.setSelection(mPasswordEt.getText().toString().length());
        } else {
            mShowPassword.setImageResource(R.drawable.check_off);
            mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPasswordEt.setSelection(mPasswordEt.getText().toString().length());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.wifi_connect_dialog_show_password:
                mPasswordVisible = !mPasswordVisible;
                setPasswordVisible(mPasswordVisible);
                //closeKeybord();
                break;
            case R.id.wifi_connect_dialog_cancle:
                dismiss();
                break;
            case R.id.wifi_connect_dialog_connect:
                mCallback.callBackData(new String[]{mWifiName, mPasswordEt.getText().toString(), mEncryption}, "connect");
                dismiss();
                break;
            case R.id.wifi_delete_this_internet:
                mCallback.callBackData(new String[]{mWifiName}, "delete");
                dismiss();
                break;
        }
    }

    private void closeKeybord() {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getWindow().peekDecorView();
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private String getSignalStrengthByLevel(int level) {
        if (level <= 1) {
            return mContext.getString(R.string.wifi_signal_strength_1);
        } else if (level == 2) {
            return mContext.getString(R.string.wifi_signal_strength_2);
        } else if (level == 3) {
            return mContext.getString(R.string.wifi_signal_strength_3);
        } else {
            return mContext.getString(R.string.wifi_signal_strength_4);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() >= 8) {
                mConnect.setEnabled(true);
                mConnect.setTextAppearance(mContext, R.style.DialogButtonStyle);
                mConnect.setBackgroundResource(R.drawable.rectangle);
            } else {
                mConnect.setEnabled(false);
                mConnect.setTextAppearance(mContext, R.style.NegativeDialogButtonStyle);
                mConnect.setBackgroundResource(R.drawable.rectangle_negative);
            }
        }
    };

    public interface DialogCallback {
        void callBackData(String[] data, String key);
    }
}
