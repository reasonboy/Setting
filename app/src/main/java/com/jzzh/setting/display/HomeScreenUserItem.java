package com.jzzh.setting.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jzzh.setting.R;

public class HomeScreenUserItem extends LinearLayout {

    private Context mContext;
    private ViewGroup mLayout;

    private TextView mTitle;
    private ImageView mPreview;
    private ImageView mSwitch;

    public HomeScreenUserItem(Context context) {
        super(context);
        initView(context);
    }

    public HomeScreenUserItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.home_screen_item,this, false);
        addView(mLayout);
        mTitle = mLayout.findViewById(R.id.home_screen_item_title);
        mPreview = mLayout.findViewById(R.id.home_screen_item_preview);
        mSwitch = mLayout.findViewById(R.id.home_screen_item_switch);
    }

    public void setTitle(int src) {
        mTitle.setText(mContext.getString(src));
    }

    public void setPreview(int src) {
        mPreview.setImageResource(src);
    }

    public void select(boolean select) {
        mSwitch.setSelected(select);
    }
}
