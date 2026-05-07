package com.jzzh.setting;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavigationLayout extends LinearLayout{

    private Context mContext;
    private OnNavigationClickListener mListener;
    private LinearLayout mLayout;

    public NavigationLayout(Context context) {
        super(context);
        initView(context);
    }

    public NavigationLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mLayout = new LinearLayout(mContext);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(getResources().getDimensionPixelOffset(R.dimen.navi_layout_marginLeft), 0, 0, 0);
        lp.gravity = Gravity.CENTER_VERTICAL;
        addView(mLayout, lp);
    }

    public void setNavigation(int[] navigationIds) {
        final LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLp.gravity = Gravity.CENTER_VERTICAL;
        ivLp.gravity = Gravity.CENTER_VERTICAL;
        ivLp.setMargins(getResources().getDimensionPixelOffset(R.dimen.navi_layout_view_space), 0,
                getResources().getDimensionPixelOffset(R.dimen.navi_layout_view_space), 0);
        for (int i = 0;i < navigationIds.length;i++) {
            int srcId = navigationIds[i];
            String subNavi = mContext.getString(srcId);
            NavigationTextView tv = new NavigationTextView(mContext);
            tv.setText(subNavi);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null) {
                        mListener.onNavigationClick(srcId);
                    }
                }
            });

            ImageView iv = new ImageView(mContext);
            iv.setImageResource(R.drawable.setting_title_next);

            if(i == navigationIds.length - 1) {
                tv.underLineVisible(true);
                mLayout.addView(tv,tvLp);
            } else {
                mLayout.addView(tv,tvLp);
                mLayout.addView(iv,ivLp);
            }
        }
    }

    public interface OnNavigationClickListener {
        void onNavigationClick(int srcId);
    }

    public void setOnNavigationClickListener(OnNavigationClickListener l) {
        mListener = l;
    }
}
