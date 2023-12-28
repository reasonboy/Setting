package com.jzzh.network.bt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jzzh.network.R;

public class BtDisconnectDialog extends Dialog implements View.OnClickListener {
    private BtDialogCallback mDialogCallback;
    private String mAlias;
    private TextView mHint;
    private Context mContext;

    public BtDisconnectDialog(@NonNull Context context, int themeResId, String alias, BtDialogCallback callback) {
        super(context, themeResId);
        mContext = context;
        mDialogCallback = callback;
        mAlias = alias;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bluetooth_disconnect_dialog);
        findViewById(R.id.bt_disconnect_dialog_cancle).setOnClickListener(this);
        findViewById(R.id.bt_disconnect_dialog_confirm).setOnClickListener(this);
        mHint = findViewById(R.id.tv_disconnect_hint);
        String hintText = mContext.getString(R.string.bt_disconnect_hint) + " " + mAlias + ".";
        mHint.setText(hintText);
    }

    @Override
    public void onClick(View view) {
        if (mDialogCallback == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.bt_disconnect_dialog_cancle) {
            mDialogCallback.callBackData(ButtonType.LEFT);
        } else if (id == R.id.bt_disconnect_dialog_confirm) {
            mDialogCallback.callBackData(ButtonType.RIGHT);
        }
        dismiss();
    }

    public enum ButtonType {
        LEFT, RIGHT
    }

    public interface BtDialogCallback {
        void callBackData(ButtonType buttonType);
    }
}
