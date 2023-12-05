package com.jzzh.setting.display;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class WidgetTextActivity extends BaseActivity {

    private View mView;
    private TextView mWidgetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_widget_text);
        mView = findViewById(R.id.widget_text_layout);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WidgetTextDialog(WidgetTextActivity.this, com.jzzh.network.R.style.ZhDialog, new WidgetTextDialog.DialogCallback() {
                    @Override
                    public void callBackData() {
                        updateText();
                    }
                }).show();
            }
        });
        mWidgetText = findViewById(R.id.widget_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateText();
    }

    private void updateText() {
        String widgetText = getWidgetText();
        if(widgetText == null || widgetText.trim().equals("")) {
            mWidgetText.setText(R.string.widget_text_default);
        } else {
            mWidgetText.setText(widgetText);
        }
    }

    private void setWidgetText(String text) {
        Settings.System.putString(getContentResolver(),"space_widget_text", text);
    }

    private String getWidgetText() {
        return Settings.System.getString(getContentResolver(),"space_widget_text");
    }
}
