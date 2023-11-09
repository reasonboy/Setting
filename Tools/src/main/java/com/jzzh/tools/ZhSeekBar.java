package com.jzzh.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ZhSeekBar extends View {

    private Paint mPaint;
    private Bitmap mBackground;//滑动条
    private int mEnableColor,mDisableColor,mFrontColor,mBackgroundColor;
    private float mThumbSize,mWidthBg,mHeightBg;//小球直径，滑动条长度，滑动条高度
    private int mTouchableHeight;//触控区域(也就是控件的高度)
    private int mMaxValue,mMinValue,mCurValue;
    private float mSlideDistance;
    private float mBgX,mBgY;//滑动条X,Y左上角坐标
    private float mRadius;//小球半径
    private boolean mEnable = true;
    private OnZhSeekBarChangeListener mOnZhSeekBarChangeListener;

    public ZhSeekBar(Context context) {
        super(context);
    }

    public ZhSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZhSeekBar);
        mEnableColor = typedArray.getColor(R.styleable.ZhSeekBar_enable_color,Color.BLACK);
        mDisableColor = typedArray.getColor(R.styleable.ZhSeekBar_disable_color,Color.GRAY);
        mBackgroundColor = typedArray.getColor(R.styleable.ZhSeekBar_bg_color,0xFFDDDDDD);
        mThumbSize = typedArray.getDimensionPixelOffset(R.styleable.ZhSeekBar_thumb_size, 0);
        mWidthBg = typedArray.getDimensionPixelOffset(R.styleable.ZhSeekBar_bg_width, 0);
        mHeightBg = typedArray.getDimensionPixelOffset(R.styleable.ZhSeekBar_bg_height, 0);
        mTouchableHeight = typedArray.getDimensionPixelOffset(R.styleable.ZhSeekBar_touchable_height, 0);
        if(mTouchableHeight < mThumbSize) {
            mTouchableHeight = (int)mThumbSize;
        }
        mMaxValue = typedArray.getInteger(R.styleable.ZhSeekBar_max_value,0);
        mMinValue = typedArray.getInteger(R.styleable.ZhSeekBar_min_value,0);
        mCurValue = typedArray.getInteger(R.styleable.ZhSeekBar_default_value,-1);
        mBackground = Bitmap.createBitmap((int)mWidthBg,(int)mHeightBg, Bitmap.Config.ARGB_8888);
        mFrontColor = mEnableColor;
        mPaint = new Paint();
        mPaint.setColor(mFrontColor);
        mPaint.setAntiAlias(true);
        viewOrganize();
        updateView();
    }

    private void viewOrganize() {
        mRadius = mThumbSize / 2;
        mBgX = mRadius;
        mBgY = (mTouchableHeight - mHeightBg) / 2;
    }

    private void updateView() {
        float percentage = (float)(mCurValue - mMinValue) / (float)(mMaxValue - mMinValue);
        float slideDistance = percentage * mWidthBg;
        mSlideDistance = slideDistance;
        for(int i = 0;i < mWidthBg;i++) {
            for(int j = 0;j < mHeightBg;j++) {
                if(i < slideDistance) {
                    mBackground.setPixel(i,j, mFrontColor);
                } else {
                    mBackground.setPixel(i,j, mBackgroundColor);
                }
            }
        }
        invalidate();
    }

    public void enable(boolean enable) {
        mEnable = enable;
        if(enable) {
            mFrontColor = mEnableColor;
        } else {
            mFrontColor = mDisableColor;
        }
        mPaint.setColor(mFrontColor);
        updateView();
    }

    public boolean isEnable() {
        return mEnable;
    }

    public void setValue(int curValue) {
        if(mCurValue != curValue) {
            mCurValue = curValue;
            updateView();
        }
    }

    public int getValue() {
        return mCurValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        updateView();
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        updateView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawBitmap(mBackground, mBgX, mBgY, null);
        canvas.drawCircle(mRadius + mSlideDistance,mTouchableHeight/2,mRadius,mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mEnable) {
            return true;
        }
        int recordValue = mCurValue;
        float x = event.getX();
        float slideDistance = x - mBgX;
        if(slideDistance < 0) {
            slideDistance = 0;
        }else if(slideDistance > mWidthBg) {
            slideDistance = mWidthBg;
        }
        float curValue = slideDistance/mWidthBg * (mMaxValue - mMinValue) + mMinValue;
        mCurValue = Math.round(curValue);//四舍五入
        if(recordValue != mCurValue) {//当前值发生变化才更新
            if(mOnZhSeekBarChangeListener != null) {
                mOnZhSeekBarChangeListener.onProgressChanged(mCurValue);
            }
            updateView();
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        this.setMeasuredDimension((int)(mThumbSize+mWidthBg), mTouchableHeight);
    }

    public interface OnZhSeekBarChangeListener {
        public void onProgressChanged(int value);
    }

    public void setOnZhSeekBarChangeListener(OnZhSeekBarChangeListener l) {
        mOnZhSeekBarChangeListener = l;
    }
}
