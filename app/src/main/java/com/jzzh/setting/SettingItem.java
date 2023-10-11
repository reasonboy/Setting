package com.jzzh.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItem extends RelativeLayout {

    private Context mContext;
    private String mTitle;
    private int mIdIcon;
    private ViewGroup mLayout;
    private ImageView mIvIcon;
    private TextView mTvTitle;

    public SettingItem(Context context) {
        super(context);
        initView(context);
    }

    public SettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItem);
        mIdIcon = typedArray.getResourceId(R.styleable.SettingItem_icon, 0);
        mTitle = typedArray.getString(R.styleable.SettingItem_title);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.setting_item,this, false);
        addView(mLayout);

        mIvIcon = mLayout.findViewById(R.id.setting_item_icon);
        mTvTitle = mLayout.findViewById(R.id.setting_item_title);
        mIvIcon.setBackgroundResource(mIdIcon);
        mTvTitle.setText(mTitle);
    }
}
