package com.jzzh.network.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.jzzh.network.R;


public class MeteredDialog extends Dialog implements View.OnClickListener {
    private DialogCallback mDialogCallback;

    public MeteredDialog(@NonNull Context context) {
        super(context);
    }

    public MeteredDialog(@NonNull Context context, int themeResId, DialogCallback callback) {
        super(context, themeResId);
        mDialogCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_metered_dialog);
        findViewById(R.id.metered_auto).setOnClickListener(this);
        findViewById(R.id.metered_metered).setOnClickListener(this);
        findViewById(R.id.metered_unmetered).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.metered_auto) {
            mDialogCallback.callBackData(Type.AUTO);
        } else if (id == R.id.metered_metered) {
            mDialogCallback.callBackData(Type.METERED);
        } else if (id == R.id.metered_unmetered) {
            mDialogCallback.callBackData(Type.UNMETERED);
        }
        dismiss();
    }

    public enum Type{
        AUTO,
        METERED,
        UNMETERED
    }

    public interface DialogCallback {
        void callBackData(Type data);
    }
}
