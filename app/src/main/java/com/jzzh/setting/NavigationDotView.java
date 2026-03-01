package com.jzzh.setting;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class NavigationDotView extends LinearLayout{

    private Context mContext;
    private LinearLayout mLayout;
    private List<ImageView> mDotViews;
    private OnNavigationDotClickListener mListener;

    public NavigationDotView(Context context) {
        super(context);
        initView(context);
    }

    public NavigationDotView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setGravity(Gravity.CENTER);
        mLayout = new LinearLayout(mContext);
        final MarginLayoutParams lp = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mLayout,lp);
    }

    public void setDotCount(int count) {
        mLayout.removeAllViews();
        mDotViews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int srcId = i;
            final MarginLayoutParams ivLp = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (srcId + 1 == count) { //最后一个点不加空隙，整体看起来居中些
                ivLp.setMargins(0, 0, 0, 0);
            } else {
                ivLp.setMargins(0, 0, getResources().getDimensionPixelOffset(R.dimen.dot_view_space), 0);
            }
            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.dot_white);
            iv.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onNavigationDotClick(srcId);
                }
            });
            mLayout.addView(iv, ivLp);
            mDotViews.add(iv);
        }
    }

    public void enableDot(int index) {
        for (ImageView dot : mDotViews) {
            dot.setImageResource(R.drawable.dot_white);
        }
        mDotViews.get(index).setImageResource(R.drawable.dot_black);
    }

    public interface OnNavigationDotClickListener {
        void onNavigationDotClick(int srcId);
    }

    public void setOnNavigationDotClickListener(OnNavigationDotClickListener l) {
        mListener = l;
    }
}
