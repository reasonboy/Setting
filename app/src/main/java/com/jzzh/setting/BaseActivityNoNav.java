package com.jzzh.setting;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivityNoNav extends Activity {

    private TextView mNavTextView;

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(R.layout.activity_base_no_nav);
        FrameLayout frameContentView = (FrameLayout) findViewById(R.id.content_base);
        mNavTextView = findViewById(R.id.navigation_tv);
        ImageView iv = findViewById(R.id.navigation_back);
        iv.setOnClickListener(view -> onBackPressed());
        View.inflate(this, layoutResID, frameContentView);

    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }

    public void setNavText(String text) {
        mNavTextView.setText(text);
    }
}
