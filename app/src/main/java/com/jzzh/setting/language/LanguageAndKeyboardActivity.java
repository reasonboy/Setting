package com.jzzh.setting.language;

import android.os.Bundle;
import android.view.View;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

public class LanguageAndKeyboardActivity extends BaseActivity {

    private KeyboardLayout mKeyboardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_and_keyboard);
        mKeyboardLayout = findViewById(R.id.keyboard);
        mKeyboardLayout.setOnFunctionButtonClickListener(new KeyboardLayout.onFunctionButtonClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(KeyboardManagerActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKeyboardLayout.updateView();
    }
}
