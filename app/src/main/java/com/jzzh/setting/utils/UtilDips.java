package com.jzzh.setting.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;

public class UtilDips {
    static Context context;
    private static WindowManager wm;

    public static void init(Context context) {
        UtilDips.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static int spToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static int dpToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(final int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}