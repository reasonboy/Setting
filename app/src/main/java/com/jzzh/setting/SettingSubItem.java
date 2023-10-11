package com.jzzh.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingSubItem extends RelativeLayout {
    private String mTitle;
    private ViewGroup mLayout;
    private TextView mTvTitle;

    public SettingSubItem(Context context) {
        super(context);
        initView(context);
    }

    public SettingSubItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingSubItem);
        mTitle = typedArray.getString(R.styleable.SettingSubItem_title);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.setting_sub_item,this, false);
        addView(mLayout);

        mTvTitle = mLayout.findViewById(R.id.title);
        mTvTitle.setText(mTitle);
    }
}
