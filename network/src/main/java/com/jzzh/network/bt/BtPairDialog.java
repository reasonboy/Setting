package com.jzzh.network.bt;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jzzh.network.R;

public class BtPairDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private BtDialogCallback mDialogCallback;
    private Type mType;
    private BluetoothDevice mDevice;
    private TextView mBtNameTv;
    private Button mDefine;

    public BtPairDialog(Context context, int i, Type type, BluetoothDevice device, BtDialogCallback callback) {
        super(context, i);
        mContext = context;
        mDialogCallback = callback;
        mType = type;
        mDevice = device;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bluetooth_pair_dialog);
        mBtNameTv = findViewById(R.id.bt_pair_dialog_name);
        mBtNameTv.setText(mDevice.getName());
        findViewById(R.id.bt_pair_dialog_cancle).setOnClickListener(this);
        mDefine = findViewById(R.id.bt_pair_dialog_define);
        mDefine.setOnClickListener(this);
        if(mType == Type.PAIR) {
            mDefine.setText(mContext.getString(R.string.bt_pair));
        } else {
            mDefine.setText(mContext.getString(R.string.bt_disconnect));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bt_pair_dialog_cancle) {
            dismiss();
        } else if (id == R.id.bt_pair_dialog_define) {
            mDialogCallback.callBackData(mDevice);
            dismiss();
        }
    }

    public enum Type {
        PAIR, DISCONNECT
    }

    public interface BtDialogCallback {
        void callBackData(BluetoothDevice device);
    }
}