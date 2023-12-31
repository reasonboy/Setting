package com.jzzh.setting.display;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.jzzh.setting.R;

public class WidgetTextDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private EditText mChangeWidgetEt;
    private DialogCallback mCallback;

    public WidgetTextDialog(@NonNull Context context, int themeResId, DialogCallback callback) {
        super(context, themeResId);
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.widget_text_dialog);
        findViewById(R.id.change_widget_cancel).setOnClickListener(this);
        findViewById(R.id.change_widget_ok).setOnClickListener(this);
        mChangeWidgetEt = findViewById(R.id.change_widget_et);
        if(getWidgetText() != null) {
            mChangeWidgetEt.setText(getWidgetText());
        }
        mChangeWidgetEt.addTextChangedListener(new TextWatcher() {
            CharSequence beforeText = null;
            private int line;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                StringBuilder sb = new StringBuilder(charSequence);
                beforeText = sb.delete(i, i + i2).toString();
                line = mChangeWidgetEt.getLineCount();
                int length = beforeText.length();
                if (line >= 4 || length >= 75) {  // 限制最多输入3行，75个字符
                    mChangeWidgetEt.setText(beforeText.toString());
                    mChangeWidgetEt.setSelection(length);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.change_widget_cancel) {
            dismiss();
        } else if (id == R.id.change_widget_ok) {
            String widget = mChangeWidgetEt.getText().toString();
            setWidgetText(widget);
            mCallback.callBackData();
            dismiss();
        }
    }

    private void setWidgetText(String text) {
        Settings.System.putString(mContext.getContentResolver(),"space_widget_text", text);
    }

    private String getWidgetText() {
        return Settings.System.getString(mContext.getContentResolver(),"space_widget_text");
    }

    public interface DialogCallback {
        void callBackData();
    }
}
