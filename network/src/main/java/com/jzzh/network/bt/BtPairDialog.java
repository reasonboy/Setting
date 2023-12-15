package com.jzzh.network.bt;

import static com.jzzh.network.bt.BtUtils.getAlias;
import static com.jzzh.network.bt.BtUtils.setAlias;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzzh.network.R;

public class BtPairDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private BtDialogCallback mDialogCallback;
    private Type mType;
    private BluetoothDevice mDevice;
    private TextView mBtNameTv;
    private Button mCancel;
    private Button mDefine;
    private EditText mBtAliasEt;
    private LinearLayout mBtAliasLayout;
    private View mBottomLine;

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
        if (mType == Type.DISCONNECT) {
            mBtNameTv.setText(R.string.bt_paired_devices);
        } else {
            mBtNameTv.setText(mDevice.getName());
        }
        mBtAliasEt = findViewById(R.id.bt_alias_et);
        mBtAliasEt.setText(getAlias(mDevice));
        mBtAliasLayout = findViewById(R.id.ll_alias_et);
        mCancel=findViewById(R.id.bt_pair_dialog_cancle);
        mCancel.setOnClickListener(this);
        mDefine = findViewById(R.id.bt_pair_dialog_define);
        mDefine.setOnClickListener(this);
        mBottomLine=findViewById(R.id.bottom_line);
        if(mType == Type.PAIR) {
            mDefine.setText(mContext.getString(R.string.bt_pair));
        } else {
            mBtAliasLayout.setVisibility(View.VISIBLE);
            mBottomLine.setVisibility(View.VISIBLE);
            mCancel.setText(R.string.bt_forget);
            mDefine.setText(R.string.bt_confirm);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bt_pair_dialog_cancle) {
            if (mType == Type.DISCONNECT) {
                mDialogCallback.callBackData(mDevice, ButtonType.LEFT);
            }
            dismiss();
        } else if (id == R.id.bt_pair_dialog_define) {
            if (mType == Type.DISCONNECT) { // rename
                setAlias(mDevice, mBtAliasEt.getText().toString());
            }
            mDialogCallback.callBackData(mDevice, ButtonType.RIGHT);
            dismiss();
        }
    }

    public enum Type {
        PAIR, DISCONNECT
    }

    public enum ButtonType {
        LEFT, RIGHT
    }

    public interface BtDialogCallback {
        void callBackData(BluetoothDevice device,ButtonType buttonType);
    }
}
