package com.jzzh.setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StyleChooseView extends LinearLayout {

    private TextView mTitleTextView;
    private ImageView mPreviewImageView;
    private TextView mWarningTextView;
    private ImageView mPrevButton;
    private ImageView mNextButton;
    private NavigationDotView mNavigationDotView;
    private FrameLayout mLeftButton;
    private TextView mLeftButtonText;
    private FrameLayout mRightButton;
    private TextView mRightButtonText;
    private List<PageData> mPages;
    private int mCurrentPageIndex = 0;

    private static final int SWIPE_THRESHOLD = 80;
    private float mTouchStartX = 0f;
    private boolean mIsSwiping = false;

    public StyleChooseView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public StyleChooseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StyleChooseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.style_choose_view, this, true);

        mTitleTextView = findViewById(R.id.def_tv);
        mPreviewImageView = findViewById(R.id.def_iv);
        mWarningTextView = findViewById(R.id.tv_warning);
        mPrevButton = findViewById(R.id.btn_prev);
        mNextButton = findViewById(R.id.btn_next);
        mNavigationDotView = findViewById(R.id.dot_navigation);
        mLeftButton = findViewById(R.id.btn_left);
        mRightButton = findViewById(R.id.btn_right);
        mLeftButtonText = findViewById(R.id.text_btn_left);
        mRightButtonText = findViewById(R.id.text_btn_right);

        mPrevButton.setOnClickListener(v -> showPreviousPage());
        mNextButton.setOnClickListener(v -> showNextPage());

        mLeftButton.setOnClickListener(v -> {
            PageData currentPage = getCurrentPage();
            currentPage.leftButtonListener.onClick();
        });

        mRightButton.setOnClickListener(v -> {
            PageData currentPage = getCurrentPage();
            if(!currentPage.isRightButtonSelected()) {
                currentPage.rightButtonListener.onClick();
            }
        });

        mNavigationDotView.setOnNavigationDotClickListener(this::onNavigationDotClick);

        mPreviewImageView.setOnTouchListener((v, event) -> {
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
            return false;
        });

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

        if (currentPage.warning != null) {
            mWarningTextView.setText(currentPage.warning);
            mWarningTextView.setVisibility(VISIBLE);
        } else {
            mWarningTextView.setVisibility(GONE);
        }

        if (currentPage.leftButtonSelected) {
            mLeftButton.setBackgroundResource(R.drawable.display_def_apply_frame_selected);
            mLeftButtonText.setTextColor(Color.WHITE);
        } else {
            mLeftButton.setBackgroundResource(R.drawable.display_def_apply_frame);
            mLeftButtonText.setTextColor(Color.BLACK);
        }

        if (currentPage.rightButtonSelected) {
            mRightButton.setBackgroundResource(R.drawable.display_def_apply_frame_selected);
            mRightButtonText.setTextColor(Color.WHITE);
        } else {
            mRightButton.setBackgroundResource(R.drawable.display_def_apply_frame);
            mRightButtonText.setTextColor(Color.BLACK);
        }

        if (currentPage.leftButtonText != null) {
            mLeftButtonText.setText(currentPage.leftButtonText);
            mLeftButton.setVisibility(VISIBLE);
        } else {
            mLeftButton.setVisibility(GONE);
        }
        if (currentPage.rightButtonText != null) {
            mRightButtonText.setText(currentPage.rightButtonText);
            mRightButton.setVisibility(VISIBLE);
        } else {
            mRightButton.setVisibility(GONE);
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


    public static class PageData {
        public String title;
        public Bitmap image;
        public String warning;
        public int imageResId;
        public String leftButtonText;
        public String rightButtonText;
        public boolean leftButtonSelected = false;
        public boolean rightButtonSelected = false;
        public OnPageButtonClickListener leftButtonListener;
        public OnPageButtonClickListener rightButtonListener;

        public PageData() {

        }
        public PageData(String title, Bitmap image) {
            this.title = title;
            this.image = image;
        }

        public PageData(String title, int imageResId) {
            this.title = title;
            this.imageResId = imageResId;
        }

        public void setLeftButton(String text, OnPageButtonClickListener listener) {
            this.leftButtonText = text;
            this.leftButtonListener = listener;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public void setWarning(String warning) {
            this.warning = warning;
        }

        public void setLeftButton(String text) {
            this.leftButtonText = text;
        }

        public void setRightButton(String text, OnPageButtonClickListener listener) {
            this.rightButtonText = text;
            this.rightButtonListener = listener;
        }

        public void setRightButton(String text) {
            this.rightButtonText = text;
        }

        public void setLeftButtonSelected(boolean b){
            this.leftButtonSelected = b;
        }

        public void setRightButtonSelected(boolean b){
            this.rightButtonSelected = b;
        }

        public boolean isRightButtonSelected() {
            return rightButtonSelected;
        }
    }

    public interface OnPageButtonClickListener {
        void onClick();
    }
}
