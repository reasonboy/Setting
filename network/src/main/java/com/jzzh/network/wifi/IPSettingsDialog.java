package com.jzzh.network.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.jzzh.network.R;


public class IPSettingsDialog extends Dialog implements View.OnClickListener {
    private DialogCallback mDialogCallback;

    public IPSettingsDialog(@NonNull Context context) {
        super(context);
    }

    public IPSettingsDialog(@NonNull Context context, int themeResId, DialogCallback dialogCallback) {
        super(context, themeResId);
        mDialogCallback=dialogCallback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_ip_settings_dialog);
        findViewById(R.id.ip_settings_dhcp).setOnClickListener(this);
        findViewById(R.id.ip_settings_static).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ip_settings_static) {
            mDialogCallback.callBackData(Type.STATIC);
        } else if (id == R.id.ip_settings_dhcp) {
            mDialogCallback.callBackData(Type.DHCP);
        }
        dismiss();
    }

    public enum Type{
        STATIC,
        DHCP
    }

    public interface DialogCallback {
        void callBackData(Type data);
    }
}
