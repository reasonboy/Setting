package com.jzzh.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzzh.tools.ZhCheckBox;

public class TitleLayout extends RelativeLayout {

    private Context mContext;
    private ViewGroup mLayout;
    private String mTitle;
    private int mIdIcon1,mIdIcon2;
    private boolean mEnableCheckBox;

    private TextView mTvTitle;
    private ImageView mIvIcon1, mIvIcon2;
    private ZhCheckBox mCheckBox;

    public TitleLayout(Context context) {
        super(context);
        intView(context);
    }

    public TitleLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TitleLayout);
        mTitle = typedArray.getString(R.styleable.TitleLayout_title);
        mIdIcon1 = typedArray.getResourceId(R.styleable.TitleLayout_function_icon_1, 0);
        mIdIcon2 = typedArray.getResourceId(R.styleable.TitleLayout_function_icon_2, 0);
        mEnableCheckBox = typedArray.getBoolean(R.styleable.TitleLayout_enable_checkBox, false);
        intView(context);
    }

    private void intView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.net_title_layout,this, false);
        addView(mLayout);

        mTvTitle = mLayout.findViewById(R.id.title_name);
        mIvIcon1 = mLayout.findViewById(R.id.title_function_1);
        mIvIcon2 = mLayout.findViewById(R.id.title_function_2);
        mCheckBox = mLayout.findViewById(R.id.title_switch);

        mTvTitle.setText(mTitle);
        if(mIdIcon1 != 0) {
            mIvIcon1.setVisibility(View.VISIBLE);
            mIvIcon1.setBackgroundResource(mIdIcon1);
        }
        if(mIdIcon2 != 0) {
            mIvIcon2.setVisibility(View.VISIBLE);
            mIvIcon2.setBackgroundResource(mIdIcon2);
        }
        if(mEnableCheckBox) {
            mCheckBox.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
        mTvTitle.setText(mTitle);
    }

    public String getTitle(String title) {
        return mTitle;
    }
}
