package com.jzzh.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class PageIndication extends LinearLayout implements View.OnClickListener {

    private TextView mContent;
    private int mCurPage,mTotalPage;
    private OnPageChangeListener mChangeListener;

    public PageIndication(Context context) {
        super(context);
    }

    public PageIndication(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        mContent = findViewById(R.id.page_content);
        findViewById(R.id.page_prev).setOnClickListener(this);
        findViewById(R.id.page_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int oldPage = mCurPage;
        int id = view.getId();
        if (id == R.id.page_prev) {
            if (mCurPage > 1) {
                mCurPage--;
            }
        } else if (id == R.id.page_next) {
            if (mCurPage < mTotalPage) {
                mCurPage++;
            }
        }
        if(oldPage != mCurPage && mChangeListener != null) {
            mContent.setText(mCurPage + "/" + mTotalPage);
            mChangeListener.onPageChanged(mCurPage);
        }
    }

    public void init(int curPage,int totalPage) {
        mCurPage = curPage;
        mTotalPage = totalPage;
        mContent.setText(mCurPage + "/" + mTotalPage);
        if(totalPage > 1 ) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    public interface OnPageChangeListener {
        public void onPageChanged(int page);
    }

    public void setOnPageChangeListener(OnPageChangeListener l) {
        mChangeListener = l;
    }
}
