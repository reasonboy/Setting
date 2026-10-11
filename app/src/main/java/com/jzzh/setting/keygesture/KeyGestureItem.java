package com.jzzh.setting.keygesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzzh.setting.R;

public class KeyGestureItem extends RelativeLayout {

    private String mTitle;
    private boolean mShowArrow = true;
    private ViewGroup mLayout;
    private TextView mTitleView;
    private TextView mValueView;
    private ImageView mArrowView;

    public KeyGestureItem(Context context) {
        super(context);
        initView(context);
    }

    public KeyGestureItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyGestureItem);
        mTitle = typedArray.getString(R.styleable.KeyGestureItem_title);
        mShowArrow = typedArray.getBoolean(R.styleable.KeyGestureItem_show_arrow, true);
        typedArray.recycle();
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.key_gesture_item, this, false);
        addView(mLayout);

        mTitleView = mLayout.findViewById(R.id.key_gesture_item_title);
        mValueView = mLayout.findViewById(R.id.key_gesture_item_value);
        mArrowView = mLayout.findViewById(R.id.key_gesture_item_arrow);
        mTitleView.setText(mTitle);

        if (mShowArrow) {
            mArrowView.setVisibility(View.VISIBLE);
            mValueView.setVisibility(View.GONE);
        } else {
            mArrowView.setVisibility(View.GONE);
            mValueView.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setValue(String value) {
        mValueView.setText(value);
    }
}
