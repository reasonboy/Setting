package com.jzzh.setting.display.logo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.jzzh.setting.SettingApplication;

import java.io.File;
import java.io.FileOutputStream;

public class BitmapManager {

    public static final String LOGO_SAVE_PATH = "/data/misc/eink/";
    public static final String[] SLEEP_IMAGE_SAVE_FILES = new String[]{
            LOGO_SAVE_PATH+"standby.png",
            LOGO_SAVE_PATH+"standby_lowpower.png",
            LOGO_SAVE_PATH+"standby_charge.png"};
    public static final String[] POWER_OFF_IMAGE_SAVE_FILES = new String[]{
            LOGO_SAVE_PATH+"poweroff.png",
            LOGO_SAVE_PATH+"poweroff_nopower.png"};

    public static Bitmap rotateBitmap(Bitmap srcBitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap resultBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        return resultBitmap;
    }

    public static Bitmap loadBitmap(Context context, String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //设置为true,代表加载器不加载图片,而是把图片的宽高读出来
        opts.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, opts);
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;
        Log.v("xml_log_bm",imageWidth+"--"+imageHeight);

        //得到屏幕的宽高
        WindowManager windowManager = context.getSystemService(WindowManager.class);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics=new DisplayMetrics();
        display.getMetrics(displayMetrics);
        //获得像素大小
        int screenWidth=displayMetrics.widthPixels;
        int screenHeight=displayMetrics.heightPixels;


        int widthScale=imageWidth/screenWidth;
        int heightScale=imageHeight/screenHeight;
        int scale = widthScale > heightScale ? widthScale : heightScale;
        opts.inJustDecodeBounds=false;
        opts.inSampleSize=scale/10;

        Bitmap bitmap = BitmapFactory.decodeFile(path,opts);
        return bitmap;
    }

    /**
     * 将Bitmap保存到targetPath
     * */
    public static void saveBitmap(Bitmap bitmap, String targetPath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetPath);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            }
        } catch (Exception e) {
            Log.v("xml_log_err",""+e.toString());
            e.printStackTrace();
        }
    }

    public static void deleteBitmap(String targetPath) {
        File file = new File(targetPath);
        if(file.exists()) {
            file.delete();
        }
    }

    public static Bitmap adjustBitmap(Bitmap src) {
        int bgWidth = SettingApplication.screenWidth;
        int bgHeight = SettingApplication.screenHeight;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float scaleX = 1;
        float scaleY = 1;
        if(srcWidth > bgWidth) {
            scaleX = (float)bgWidth / srcWidth;
        }
        if(srcHeight > bgHeight) {
            scaleY = (float)bgHeight / srcHeight;
        }
        float scale = Math.min(scaleX,scaleY);
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        Bitmap resizeBitmap = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true);
        int resizeWidth = resizeBitmap.getWidth();
        int resizeHeight = resizeBitmap.getHeight();
        Log.v("xml_log_bb",resizeWidth+":adjustBitmap:"+resizeHeight);

        Bitmap newBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);

        // 绘制白色矩形
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, bgWidth, bgHeight, paint);
        canvas.drawBitmap(resizeBitmap, (bgWidth - resizeWidth)/2, (bgHeight - resizeHeight)/2, null);

        return newBitmap;
    }

    public static boolean isImage(File file) {
        boolean result = false;
        String fileName = file.getName();
        String suffix = "";
        int i = fileName.lastIndexOf('.');
        if(i > 0) {
            suffix = fileName.substring(i+1);
        }
        if(suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("bmp")
                || suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg")) {
            result = true;
        }
        return result;
    }
}
