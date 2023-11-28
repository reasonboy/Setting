package com.jzzh.setting.device;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.jzzh.setting.R;

public class FactoryResetDialog extends Dialog implements View.OnClickListener{

    private Context mContext;

    public FactoryResetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.factory_reaset_dialog);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.ok) {
            Intent intent = new Intent("android.intent.action.FACTORY_RESET");
            intent.setPackage("android");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
            intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
            intent.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", true);
            mContext.sendBroadcast(intent);
            dismiss();
        }
    }
}
