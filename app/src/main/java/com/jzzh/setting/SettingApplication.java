package com.jzzh.setting;

import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class SettingApplication extends Application {

    public static int screenWidth;
    public static int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();

        WindowManager windowManager = getApplicationContext().getSystemService(WindowManager.class);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics=new DisplayMetrics();
        display.getMetrics(displayMetrics);
        //获得像素大小
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }
}
