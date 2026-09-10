package com.jzzh.network.wifi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jzzh.network.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Drop-down selection dialog for dynamic item lists, such as EAP methods,
 * Phase 2 authentication methods, and certificates.
 */
public class WifiListDialog extends Dialog implements View.OnClickListener {

    private final List<String> mItems = new ArrayList<>();
    private DialogCallback mDialogCallback;

    public WifiListDialog(@NonNull Context context, int themeResId, List<String> items, DialogCallback callback) {
        super(context, themeResId);
        if (items != null) {
            mItems.addAll(items);
        }
        mDialogCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_list_dialog);
        LinearLayout container = findViewById(R.id.wifi_list_dialog_container);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < mItems.size(); i++) {
            if (i > 0) {
                container.addView(createDivider());
            }
            TextView item = (TextView) inflater.inflate(R.layout.wifi_list_dialog_item, container, false);
            item.setText(mItems.get(i));
            item.setTag(i);
            item.setOnClickListener(this);
            container.addView(item);
        }
    }

    private View createDivider() {
        View divider = new View(getContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(getContext().getResources().getColor(R.color.black));
        return divider;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (mDialogCallback != null) {
            mDialogCallback.callBackData(position, mItems.get(position));
        }
        dismiss();
    }

    public interface DialogCallback {
        void callBackData(int position, String item);
    }
}
