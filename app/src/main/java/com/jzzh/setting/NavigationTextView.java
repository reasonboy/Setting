package com.jzzh.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationTextView extends LinearLayout {

    private Context mContext;
    private ViewGroup mLayout;
    private TextView mText;
    private View mUnderLine;

    public NavigationTextView(Context context) {
        super(context);
        intView(context);
    }

    public NavigationTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        intView(context);
    }

    private void intView(Context context) {
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.navigation_textview,this, false);
        addView(mLayout);
        mText = mLayout.findViewById(R.id.text);
        mUnderLine = mLayout.findViewById(R.id.underline);
    }

    public void setText(String text) {
        mText.setText(text);
    }

    public void underLineVisible(boolean visible) {
        if(visible) {
            mUnderLine.setVisibility(View.VISIBLE);
        } else {
            mUnderLine.setVisibility(View.INVISIBLE);
        }
    }

}
