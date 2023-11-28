package com.jzzh.setting.device;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzzh.setting.R;

public class DeviceIfoItem extends RelativeLayout {

    private Context mContext;
    private ViewGroup mLayout;
    private TextView mTitleTv, mInformationTv;
    private String mTitle;

    public DeviceIfoItem(Context context) {
        super(context);
        intView(context);
    }

    public DeviceIfoItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeviceIfoItem);
        mTitle = typedArray.getString(R.styleable.DeviceIfoItem_title);
        intView(context);
    }

    private void intView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.device_info_item,this, false);
        addView(mLayout);

        mTitleTv = mLayout.findViewById(R.id.device_info_title);
        mInformationTv = mLayout.findViewById(R.id.device_info_information);
        setTitle(mTitle);
    }

    private void setTitle(String title) {
        if(title != null && !title.equals("")) {
            mTitleTv.setText(title);
        }
    }


    public void setInformation(String information) {
        mInformationTv.setText(information);
    }
}
