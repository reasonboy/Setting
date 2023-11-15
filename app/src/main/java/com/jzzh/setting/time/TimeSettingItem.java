package com.jzzh.setting.time;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

public class TimeSettingItem extends RelativeLayout implements ZhCheckBox.OnZhCheckedChangeListener, View.OnClickListener {

    private Context mContext;
    private String mTitle;
    private String mSummary;
    private int mFunction;
    public static final int SETTINGS_ITEM_FUNCTION_SET_TIME = 0;
    public static final int SETTINGS_ITEM_FUNCTION_CHECKBOX = 1;
    public static final int SETTINGS_ITEM_FUNCTION_SET_ZONE = 2;
    private ViewGroup mLayout;
    private TextView mTvTitle;
    private TextView mTvSummary;
    private ZhCheckBox mCheckBox;
    private ImageView mEnter;
    private OnCheckBoxChangeListener mCheckBoxChangeListener;
    private OnClickListener mOnClickListener;
    private boolean mEnable = true;

    public TimeSettingItem(Context context) {
        super(context);
        intView(context);
    }

    public TimeSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeSettingItem);
        mTitle = typedArray.getString(R.styleable.TimeSettingItem_title);
        mSummary = typedArray.getString(R.styleable.TimeSettingItem_summary);
        mFunction = typedArray.getInt(R.styleable.TimeSettingItem_function,0);
        intView(context);
    }

    private void intView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.time_setting_item,this, false);
        addView(mLayout);

        mTvTitle = mLayout.findViewById(R.id.time_setting_item_title);
        mTvSummary = mLayout.findViewById(R.id.time_setting_item_summary);
        mCheckBox = mLayout.findViewById(R.id.time_setting_item_switch);
        mEnter = mLayout.findViewById(R.id.time_setting_item_enter);

        setTitle(mTitle);
        setSummary(mSummary);
        mCheckBox.setOnZhCheckedChangeListener(this);
        mLayout.setOnClickListener(this);
        defineFunction();
    }

    private void defineFunction() {
        switch (mFunction){
            case SETTINGS_ITEM_FUNCTION_SET_TIME:
                break;
            case SETTINGS_ITEM_FUNCTION_CHECKBOX:
                mCheckBox.setVisibility(View.VISIBLE);
                break;
            case SETTINGS_ITEM_FUNCTION_SET_ZONE:
                mEnter.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setTitle(String title) {
        if(title != null && !title.equals("")) {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }
    }

    public void setSummary(String summary) {
        if(summary != null && !summary.equals("")) {
            mTvSummary.setVisibility(View.VISIBLE);
            mTvSummary.setText(summary);
        }
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
        if(enable) {
            mTvTitle.setTextColor(Color.BLACK);
            mTvSummary.setTextColor(Color.BLACK);
        } else {
            mTvTitle.setTextColor(Color.GRAY);
            mTvSummary.setTextColor(Color.GRAY);
        }
    }

    public boolean getEnable() {
        return mEnable;
    }

    public void setCheckBoxEnable(boolean enable) {
        mCheckBox.setCheck(enable);
    }

    public void setOnCheckBoxChangeListener(OnCheckBoxChangeListener l) {
        mCheckBoxChangeListener = l;
    }

    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void onCheckedChanged(boolean checked) {
        if(mCheckBoxChangeListener != null) {
            mCheckBoxChangeListener.onCheckBoxChanged(TimeSettingItem.this,checked);
        }
    }

    @Override
    public void onClick(View view) {
        if(mOnClickListener != null && mEnable) {
            mOnClickListener.onClick(TimeSettingItem.this);
        }
    }

    public interface OnCheckBoxChangeListener {
        void onCheckBoxChanged(View v, boolean checked);
    }

    public interface OnClickListener {
        void onClick(View v);
    }
}
