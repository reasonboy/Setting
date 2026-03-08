package com.jzzh.setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jzzh.tools.ZhCheckBox;

import java.util.ArrayList;
import java.util.List;

public class GestureChooseView extends LinearLayout {

    private TextView mTitleTextView;
    private ImageView mPreviewImageView;
    private ImageView mPrevButton;
    private ImageView mNextButton;
    private NavigationDotView mNavigationDotView;
    private View mBottomEnterView;
    private ZhCheckBox mCheckBox;
    private TextView mCheckBoxText;
    private TextView mBottomText;
    private List<PageData> mPages;
    private int mCurrentPageIndex = 0;

    private static final int SWIPE_THRESHOLD = 80;
    private float mTouchStartX = 0f;
    private boolean mIsSwiping = false;

    public GestureChooseView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public GestureChooseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GestureChooseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.gesture_choose_view, this, true);

        mTitleTextView = findViewById(R.id.def_tv);
        mPreviewImageView = findViewById(R.id.def_iv);
        mPrevButton = findViewById(R.id.btn_prev);
        mNextButton = findViewById(R.id.btn_next);
        mNavigationDotView = findViewById(R.id.dot_navigation);
        mBottomEnterView = findViewById(R.id.bottom_enter);
        mCheckBoxText = findViewById(R.id.checkbox_text);
        mBottomText = findViewById(R.id.bottom_tv);
        mCheckBox = findViewById(R.id.checkbox);

        mPrevButton.setOnClickListener(v -> showPreviousPage());
        mNextButton.setOnClickListener(v -> showNextPage());

        mNavigationDotView.setOnNavigationDotClickListener(this::onNavigationDotClick);

        mPages = new ArrayList<>();
    }

    public void onNavigationDotClick(int srcId) {
        setPageIndex(srcId);
    }

    public void setPages(List<PageData> pages) {
        mPages = pages;
        mCurrentPageIndex = 0;
        mNavigationDotView.setDotCount(pages.size());
        mNavigationDotView.enableDot(0);

        updatePageDisplay();
        updateNavigationButtons();
    }

    private void showPreviousPage() {
        if (mCurrentPageIndex > 0) {
            setPageIndex(mCurrentPageIndex - 1);
        }
    }

    private void showNextPage() {
        if (mCurrentPageIndex < mPages.size() - 1) {
            setPageIndex(mCurrentPageIndex + 1);
        }
    }

    private void setPageIndex(int index) {
        mCurrentPageIndex = index;
        updatePageDisplay();
        updateNavigationButtons();
        mNavigationDotView.enableDot(mCurrentPageIndex);
    }

    public void updatePageDisplay() {
        PageData currentPage = mPages.get(mCurrentPageIndex);

        if (currentPage.title != null) {
            mTitleTextView.setText(currentPage.title);
            mTitleTextView.setVisibility(VISIBLE);
        } else {
            mTitleTextView.setVisibility(GONE);
        }

        if (currentPage.image != null) {
            mPreviewImageView.setImageBitmap(currentPage.image);
        } else if (currentPage.imageResId != 0) {
            mPreviewImageView.setImageResource(currentPage.imageResId);
        }

        mCheckBox.setCheck(currentPage.checkBoxChecked);

        if (currentPage.checkBoxText != null) {
            mCheckBoxText.setText(currentPage.checkBoxText);
        } else {
            mCheckBoxText.setText("");
        }
        
        if (currentPage.checkBoxListener != null) {
            mCheckBox.setOnZhCheckedChangeListener(currentPage.checkBoxListener);
        } else {
            mCheckBox.setOnZhCheckedChangeListener(null);
        }

        if (currentPage.bottomViewText != null) {
            mBottomText.setText(currentPage.bottomViewText);
            mBottomText.setVisibility(VISIBLE);
            mBottomEnterView.setVisibility(VISIBLE);
        } else {
            mBottomText.setText("");
            mBottomText.setVisibility(GONE);
            mBottomEnterView.setVisibility(GONE);
        }
        
        if (currentPage.bottomViewListener != null) {
            mBottomEnterView.setOnClickListener(currentPage.bottomViewListener);
        } else {
            mBottomEnterView.setOnClickListener(null);
        }
    }

    private void updateNavigationButtons() {
        mPrevButton.setEnabled(mCurrentPageIndex > 0);
        mPrevButton.setAlpha(mCurrentPageIndex > 0 ? 1.0f : 0.3f);

        mNextButton.setEnabled(mCurrentPageIndex < mPages.size() - 1);
        mNextButton.setAlpha(mCurrentPageIndex < mPages.size() - 1 ? 1.0f : 0.3f);
    }

    public PageData getCurrentPage() {
        return mPages.get(mCurrentPageIndex);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mIsSwiping = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mTouchStartX) > SWIPE_THRESHOLD) {
                    mIsSwiping = true;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (mIsSwiping) {
                    float deltaX = event.getX() - mTouchStartX;
                    if (deltaX > SWIPE_THRESHOLD) {
                        showPreviousPage();
                    } else if (deltaX < -SWIPE_THRESHOLD) {
                        showNextPage();
                    }
                    mIsSwiping = false;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    public static class PageData {
        public String title;
        public Bitmap image;
        public int imageResId;
        public boolean checkBoxChecked = false;
        public String checkBoxText;
        public String bottomViewText;
        public OnClickListener bottomViewListener;
        public ZhCheckBox.OnZhCheckedChangeListener checkBoxListener;

        public PageData(String title, Bitmap image) {
            this.title = title;
            this.image = image;
        }

        public PageData(String title, int imageResId) {
            this.title = title;
            this.imageResId = imageResId;
        }

        public void setCheckBoxChecked(boolean b){
            this.checkBoxChecked = b;
        }

        public void setCheckBox(String text, ZhCheckBox.OnZhCheckedChangeListener listener){
            this.checkBoxText = text;
            this.checkBoxListener = listener;
        }

        public void setBottomViewListener(OnClickListener listener){
            this.bottomViewListener = listener;
        }

        public void setBottomViewText(String text){
            this.bottomViewText = text;
        }

    }

}
