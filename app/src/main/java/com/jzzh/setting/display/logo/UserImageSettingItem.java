package com.jzzh.setting.display.logo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jzzh.setting.R;
import com.jzzh.setting.SettingApplication;

import java.io.File;

public class UserImageSettingItem extends LinearLayout {

    public static int MODE_FIT_CENTER = 0;
    public static int MODE_CENTER_CROP = 1;
    public static int MODE_ROTATE = 2;
    public static int MODE_ROTATE_REVERSE = 3;

    private Context mContext;
    private ViewGroup mLayout;
    private ImageView mPreview;
    private String mDescribe;
    private Bitmap mSrcBitmap;
    private RequestOptions mRequestOptions;
    private RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(GlideException e, Object model, Target target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target RequestListener, DataSource dataSource, boolean isFirstResource) {
            mSrcBitmap = ((BitmapDrawable) resource).getBitmap();
            return false;
        }
    };

    public UserImageSettingItem(Context context) {
        super(context);
        initView(context);
    }

    public UserImageSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserImageSettingItem);
        mDescribe = typedArray.getString(R.styleable.UserImageSettingItem_describe);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.display_user_image_setting_item,this, false);
        addView(mLayout);

        mRequestOptions = new RequestOptions();
        Log.v("bbb",SettingApplication.screenWidth+"-SettingApplication-"+SettingApplication.screenHeight);
        mRequestOptions.override(SettingApplication.screenWidth,SettingApplication.screenHeight);

        mPreview = mLayout.findViewById(R.id.setting_image_preview);
        TextView tv = mLayout.findViewById(R.id.setting_image_text);
        tv.setText(mDescribe);
    }

    public void setPreview(File file,int mode) {
        if(mode == MODE_FIT_CENTER) {
            mRequestOptions.fitCenter();
            Glide.with(mContext).load(file.getPath()).apply(mRequestOptions)
                    .listener(mRequestListener)
                    .into(mPreview);
        } else if(mode == MODE_CENTER_CROP) {
            mRequestOptions.centerCrop();
            Glide.with(mContext).load(file.getPath()).apply(mRequestOptions)
                    .listener(mRequestListener)
                    .into(mPreview);
        } else if(mode == MODE_ROTATE) {
            Glide.with(mContext).load(file.getPath()).apply(mRequestOptions)
                    .transform(new Rotate(90))
                    .listener(mRequestListener)
                    .into(mPreview);
        } else if(mode == MODE_ROTATE_REVERSE) {
            Glide.with(mContext).load(file.getPath()).apply(mRequestOptions)
                    .transform(new Rotate(270))
                    .listener(mRequestListener)
                    .into(mPreview);
        }
    }



    public Bitmap getSrcBitmap() {
        return mSrcBitmap;
    }
}
