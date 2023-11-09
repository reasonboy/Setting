package com.jzzh.network.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jzzh.network.R;

import java.util.ArrayList;

public class AddDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private EditText mSSID,mPassword;
    private ImageView mNone,mWep,mWpa;
    private ArrayList<ImageView> mImageList = new ArrayList<>();
    private Button mCancle,mConnect;
    private DialogCallback mCallback;
    private String mCapabilities;

    private static final String NONE = "OPEN";
    private static final String WEP = "WEP";
    private static final String WPA = "WPA";


    public AddDialog(Context context, int style, DialogCallback callback) {
        super(context, style);
        Log.v("xml_log_dia","AddDialog" );
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_add_dialog);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //getWindow().clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mSSID = findViewById(R.id.wifi_add_dialog_name);
        mPassword = findViewById(R.id.wifi_add_dialog_password);
        mNone = findViewById(R.id.wifi_add_dialog_none);
        mWep = findViewById(R.id.wifi_add_dialog_wep);
        mWpa = findViewById(R.id.wifi_add_dialog_wpa);
        mCancle = findViewById(R.id.wifi_add_dialog_cancle);
        mConnect = findViewById(R.id.wifi_add_dialog_connect);
        mNone.setOnClickListener(this);
        mWep.setOnClickListener(this);
        mWpa.setOnClickListener(this);
        mCancle.setOnClickListener(this);
        mConnect.setOnClickListener(this);
        mImageList.add(mNone);
        mImageList.add(mWep);
        mImageList.add(mWpa);
        setCheck(WPA);
    }

    private void setCheck(String capabilities) {
        Log.v("xml_log_dia","capabilities = " + capabilities);
        //closeKeybord();
        mCapabilities = capabilities;
        for(int i = 0;i<mImageList.size();i++) {
            mImageList.get(i).setImageResource(R.drawable.check_off);
        }
        if(capabilities.equals(NONE)) {
            mNone.setImageResource(R.drawable.check_on);
            mPassword.setVisibility(View.GONE);
        } else if(capabilities.equals(WEP)){
            mWep.setImageResource(R.drawable.check_on);
            mPassword.setVisibility(View.VISIBLE);
        } else if(capabilities.equals(WPA)){
            mWpa.setImageResource(R.drawable.check_on);
            mPassword.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.wifi_add_dialog_none) {
            setCheck(NONE);
        } else if (id == R.id.wifi_add_dialog_wep) {
            setCheck(WEP);
        } else if (id == R.id.wifi_add_dialog_wpa) {
            setCheck(WPA);
        } else if (id == R.id.wifi_add_dialog_cancle) {
            dismiss();
        } else if (id == R.id.wifi_add_dialog_connect) {
            mCallback.callBackData(new String[]{mSSID.getText().toString(), mPassword.getText().toString(), mCapabilities});
            dismiss();
        }
    }

    private void closeKeybord() {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getWindow().peekDecorView();
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public interface DialogCallback {
        void callBackData(String[] data);
    }
}
