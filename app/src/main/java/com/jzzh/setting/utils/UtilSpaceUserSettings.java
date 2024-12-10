package com.jzzh.setting.utils;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class UtilSpaceUserSettings {
    private static final String TAG = "UtilSpaceUserSettings";

    public static final String SPACE_WIDGET_ENABLE = "space_widget_enable";
    public static final String SPACE_WIDGET_STYLE_ENABLE = "space_widget_style_enable";
    public static final String SPACE_WIDGET_TEXT = "space_widget_text";

    private static final String HOME_BOTTOM_PINNED_APP_ENABLE = "home_bottom_pinned_app_enable";
    private static final String HOME_LEFT_SLIDE_ENABLE = "home_left_slide_enable";
    private static final String HOME_LEFT_SLIDE_FUNCTION = "home_left_slide_function";
    private int defHLSFvalue = 0;

    private static final String HOME_BACKGROUND_ALPHA = "home_background_alpha";

    private static final String KEY_PACK_GESTURE_ENABLE = "key_pack_gesture_enable";
    private static final String LIGHT_GESTURE_ENABLE = "light_gesture_enable";
    private static final String BOTTOM_GESTURE_ENABLE = "bottom_gesture_enable";

    public static final String GOOGLE_PLAY_ENABLE = "google_play_enable";
    private static final Uri sUri = Uri.parse("content://com.google.android.gsf.gservices");

    private ContentResolver mContentResolver;

    public UtilSpaceUserSettings(Context context) {
        mContentResolver = context.getContentResolver();
    }

    public int getWidgetEnable() {
        int value = -1;
        try {
            value = Settings.System.getInt(mContentResolver, SPACE_WIDGET_ENABLE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SPACE_WIDGET_ENABLE get error");
        }
        return value;
    }

    public void setWidgetEnable(boolean isEnabled) {
        int setValue = isEnabled? 1 : 0;
        try {
            Settings.System.putInt(mContentResolver, SPACE_WIDGET_ENABLE, setValue);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SPACE_WIDGET_ENABLE put error : " + setValue);
        }
    }

    public int getWidgetStyleEnable() {
        int value = -1;
        try {
            value = Settings.System.getInt(mContentResolver, SPACE_WIDGET_STYLE_ENABLE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SPACE_WIDGET_STYLE_ENABLE get error");
        }
        return value;
    }

    public void setWidgetStyleEnable(boolean isEnabled) {
        int setValue = isEnabled? 1 : 0;
        try {
            Settings.System.putInt(mContentResolver, SPACE_WIDGET_STYLE_ENABLE, setValue);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SPACE_WIDGET_STYLE_ENABLE put error : " + setValue);
        }
    }

    public String getWidgetText() {
        String value = null;
        try {
            value = Settings.System.getString(mContentResolver, SPACE_WIDGET_TEXT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SPACE_WIDGET_TEXT get error");
        }
        return value;
    }

    public void setWidgetText(String name) {
        try {
            Settings.System.putString(mContentResolver, SPACE_WIDGET_TEXT, name);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SPACE_WIDGET_TEXT put error : " + name);
        }
    }

    // Home Bottom Pinned App
    public int getHomeBottomPinnedAppEnable() {
        int value = 1;
        try {
            value = Settings.System.getInt(mContentResolver, HOME_BOTTOM_PINNED_APP_ENABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_BOTTOM_PINNED_APP_ENABLE get error");
        }
        return value;
    }
    public void setHomeBottomPinnedAppEnable(int value) {
        try {
            Settings.System.putInt(mContentResolver, HOME_BOTTOM_PINNED_APP_ENABLE, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_BOTTOM_PINNED_APP_ENABLE put error : " + value);
        }
    }

    // Left Slide Enable
    public int getHomeLeftSlideEnable() {
        int value = 1;
        try {
            value = Settings.System.getInt(mContentResolver, HOME_LEFT_SLIDE_ENABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_LEFT_SLIDE_FUNCTION get error");
        }
        return value;
    }
    public void setHomeLeftSlideEnable(int value) {
        try {
            Settings.System.putInt(mContentResolver, HOME_LEFT_SLIDE_ENABLE, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_LEFT_SLIDE_FUNCTION put error : " + value);
        }
    }

    // Left Slide Function API
    public int getHomeLeftSlideFunction() {
        int value = defHLSFvalue;
        try {
            value = Settings.System.getInt(mContentResolver, HOME_LEFT_SLIDE_FUNCTION, defHLSFvalue);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_LEFT_SLIDE_FUNCTION get error");
        }
        return value;
    }
    public void setHomeLeftSlideFunction(int value) {
        try {
            Settings.System.putInt(mContentResolver, HOME_LEFT_SLIDE_FUNCTION, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_LEFT_SLIDE_FUNCTION put error : " + value);
        }
    }

    // Home background alpha
    public float getHomeBackgroundAlpha() {
        float value = 1.0f;
        try {
            value = Settings.System.getFloat(mContentResolver, HOME_BACKGROUND_ALPHA, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_BACKGROUND_ALPHA get error");
        }
        return value;
    }
    public void setHomeBackgroundAlpha(float value) {
        try {
            Settings.System.putFloat(mContentResolver, HOME_BACKGROUND_ALPHA, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "HOME_BACKGROUND_ALPHA put error : " + value);
        }
    }

    // Home background reset
    public Drawable getHomeBackgroundDrawable(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            return wallpaperManager.getDrawable();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean setHomeBackground(Context context, Bitmap bitmapWithAlpha) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            wallpaperManager.setBitmap(bitmapWithAlpha);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // KeyPack gesture enable/disable API
    public int getKeyPackGestureEnable() {
        int value = 1;
        try {
            value = Settings.System.getInt(mContentResolver, KEY_PACK_GESTURE_ENABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "KEY_PACK_GESTURE_ENABLE get error");
        }
        return value;
    }
    public void setKeyPackGestureEnable(int value) {
        try {
            Settings.System.putInt(mContentResolver, KEY_PACK_GESTURE_ENABLE, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "KEY_PACK_GESTURE_ENABLE put error : " + value);
        }
    }

    // light gesture enable/disable API
    public int getLightGestureEnable() {
        int value = 1;
        try {
            value = Settings.System.getInt(mContentResolver, LIGHT_GESTURE_ENABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "LIGHT_GESTURE_ENABLE get error");
        }
        return value;
    }
    public void setLightGestureEnable(int value) {
        try {
            Settings.System.putInt(mContentResolver, LIGHT_GESTURE_ENABLE, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "LIGHT_GESTURE_ENABLE put error : " + value);
        }
    }

    // bottom gesture enable/disable API
    public int getBottomGestureEnable() {
        int value = 1;
        try {
            value = Settings.System.getInt(mContentResolver, BOTTOM_GESTURE_ENABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "BOTTOM_GESTURE_ENABLE get error");
        }
        return value;
    }
    public void setBottomGestureEnable(int value) {
        try {
            Settings.System.putInt(mContentResolver, BOTTOM_GESTURE_ENABLE, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "BOTTOM_GESTURE_ENABLE put error : " + value);
        }
    }
}
