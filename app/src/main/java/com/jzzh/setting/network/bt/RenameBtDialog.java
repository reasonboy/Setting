package com.jzzh.setting.network.bt;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jzzh.setting.R;

public class RenameBtDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private DialogCallback mDialogCallback;
    private String mBtName;
    private EditText mBtNameET;


    public RenameBtDialog(Context context, int i, String btName,DialogCallback callback) {
        super(context, i);
        Log.v("xml_log_bt","btName = " + btName);
        mContext = context;
        mDialogCallback = callback;
        mBtName = btName;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.v("xml_log_bt","mBtName = " + mBtName);
        setContentView(R.layout.bluetooth_rename_dialog);
        findViewById(R.id.bt_rename_dialog_cancle).setOnClickListener(this);
        findViewById(R.id.bt_rename_dialog_ok).setOnClickListener(this);
        mBtNameET = findViewById(R.id.bt_rename_dialog_name);
        mBtNameET.setText(mBtName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_rename_dialog_cancle:
                dismiss();
                break;
            case R.id.bt_rename_dialog_ok:
                String newName = mBtNameET.getText().toString();
                if(newName != null && !newName.trim().equals("")) {
                    mDialogCallback.callBackData(new String[]{mBtNameET.getText().toString()});
                    dismiss();
                } else {
                    Toast.makeText(mContext,mContext.getString(R.string.bt_name_cannot_be_empty),Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public interface DialogCallback {
        void callBackData(String[] data);
    }
}
