package com.jzzh.network.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.jzzh.network.R;

public class ProxyDialog extends Dialog implements View.OnClickListener {
    private DialogCallback mDialogCallback;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_proxy_dialog);
        findViewById(R.id.proxy_none).setOnClickListener(this);
        findViewById(R.id.proxy_manual).setOnClickListener(this);
        findViewById(R.id.proxy_auto_config).setOnClickListener(this);
    }

    public ProxyDialog(@NonNull Context context, int themeResId, DialogCallback dialogCallback) {
        super(context, themeResId);
        mDialogCallback = dialogCallback;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.proxy_none) {
            mDialogCallback.callBackData(Type.NONE);
        } else if (id == R.id.proxy_manual) {
            mDialogCallback.callBackData(Type.MANUAL);
        } else if (id == R.id.proxy_auto_config) {
            mDialogCallback.callBackData(Type.AUTO_CONFIG);
        }
        dismiss();
    }

    public enum Type {
        NONE,
        MANUAL,
        AUTO_CONFIG
    }

    public interface DialogCallback {
        void callBackData(Type data);
    }
}
