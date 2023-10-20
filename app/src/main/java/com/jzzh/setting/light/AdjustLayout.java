package com.jzzh.setting.light;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jzzh.setting.R;
import com.jzzh.setting.ZhCheckBox;
import com.jzzh.setting.ZhSeekBar;

public class AdjustLayout extends LinearLayout implements ZhCheckBox.OnZhCheckedChangeListener,
        ZhSeekBar.OnZhSeekBarChangeListener,View.OnClickListener{

    private Context mContext;
    private ViewGroup mLayout;
    private ImageView mReduce,mIncrease;
    private ZhCheckBox mCheckBox;
    private ZhSeekBar mSeekBar;
    private int mValue, mMaxValue, mMinValue;
    private boolean mEnable = true;
    private OnEnableChangeListener mOnEnableChangeListener;
    private OnValueChangeListener mOnValueChangeListener;
    public AdjustLayout(Context context) {
        super(context);
    }

    public AdjustLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdjustLayout);
        int iconId = typedArray.getResourceId(R.styleable.AdjustLayout_icon, 0);
        String title = typedArray.getString(R.styleable.AdjustLayout_title);
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.adjust_layout,this, false);
        addView(mLayout);

        ImageView icon = mLayout.findViewById(R.id.zh_adjust_icon);
        icon.setBackground(context.getDrawable(iconId));
        TextView titleTv = mLayout.findViewById(R.id.zh_adjust_title);
        titleTv.setText(title);

        mReduce = mLayout.findViewById(R.id.zh_adjust_reduce);
        mReduce.setOnClickListener(this);
        mIncrease = mLayout.findViewById(R.id.zh_adjust_increase);
        mIncrease.setOnClickListener(this);
        mCheckBox = mLayout.findViewById(R.id.zh_adjust_switch);
        mCheckBox.setOnZhCheckedChangeListener(this);
        mSeekBar = mLayout.findViewById(R.id.zh_adjust_seekbar);
        mSeekBar.setOnZhSeekBarChangeListener(this);
    }

    @Override
    public void onCheckedChanged(boolean checked) {
        mEnable = checked;
		updateViewState(mEnable);
        if(mOnEnableChangeListener!=null) {
            mOnEnableChangeListener.enable(this,mEnable);
        }
    }
    
    private void updateViewState(boolean enable) {
		if(enable) {
            mSeekBar.enable(true);
            mReduce.setBackground(mContext.getDrawable(R.drawable.soft_bar_on_reduce));
            mIncrease.setBackground(mContext.getDrawable(R.drawable.soft_bar_on_increase));
        } else {
            mSeekBar.enable(false);
            mReduce.setBackground(mContext.getDrawable(R.drawable.soft_bar_off_reduce));
            mIncrease.setBackground(mContext.getDrawable(R.drawable.soft_bar_off_increase));
        }
	}

    @Override
    public void onProgressChanged(int value) {
        mValue = value;
        if(mOnValueChangeListener!=null) {
            mOnValueChangeListener.valueChange(AdjustLayout.this,value);
        }
    }

    @Override
    public void onClick(View view) {
        if(!mEnable) {
            return;
        }
        int recordValue = mValue;
        if(view.getId()==R.id.zh_adjust_increase) {
			if(mValue < mMaxValue) mValue++;
		} else if(view.getId()==R.id.zh_adjust_reduce) {
			if(mValue > mMinValue) mValue--;
		}
		if(mValue != recordValue) {
			mSeekBar.setValue(mValue);
			if(mOnValueChangeListener!=null && mEnable) {
				mOnValueChangeListener.valueChange(AdjustLayout.this,mValue);
			}
		}
    }

    public void setValue(int value) {
        mValue = value;
        mSeekBar.setValue(value);
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        mSeekBar.setMaxValue(maxValue);
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
        mSeekBar.setMinValue(minValue);
    }

    public void enable(boolean enable) {
        mCheckBox.setCheck(enable);
        updateViewState(enable);
    }

    public interface OnEnableChangeListener {
        public void enable(View view, boolean enable);
    }

    public void setOnEnableChangeListener(OnEnableChangeListener l) {
        mOnEnableChangeListener = l;
    }

    public interface OnValueChangeListener {
        public void valueChange(View view,int value);
    }

    public void setOnValueChangeListener(OnValueChangeListener l) {
        mOnValueChangeListener = l;
    }

    public void hideCheckBox() {
        mCheckBox.setVisibility(View.GONE);
    }

}
