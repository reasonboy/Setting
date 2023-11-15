package com.jzzh.setting.time;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jzzh.setting.R;

import java.util.Calendar;

public class SetTimeDialog extends Dialog implements View.OnClickListener {

    private TextView mHourTv,mMinuteTv,mApmTv;
    private int mHour,mMinute,mApm;
    private Context mContext;
    private DialogCallback mDialogCallback;

    public SetTimeDialog(Context context, int i, DialogCallback callback) {
        super(context, i);
        mContext = context;
        mDialogCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.time_set_time_dialog);
        findViewById(R.id.set_time_dialog_hour_increase).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_hour_reduce).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_minute_increase).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_minute_reduce).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_apm_increase).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_apm_reduce).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_cancel).setOnClickListener(this);
        findViewById(R.id.set_time_dialog_ok).setOnClickListener(this);
        mHourTv = findViewById(R.id.set_time_dialog_hour);
        mMinuteTv = findViewById(R.id.set_time_dialog_minute);
        mApmTv = findViewById(R.id.set_time_dialog_apm);
        if(is24HourFormatEnabled()) {
            findViewById(R.id.set_time_dialog_apm_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initDate();
        updateTime();
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        mApm = calendar.get(Calendar.AM_PM);
        if(!is24HourFormatEnabled() && mApm == Calendar.PM && mHour > 12) {
            mHour = mHour - 12;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void updateTime() {
        mHourTv.setText(mHour+"");
        if(mMinute < 10) {
            mMinuteTv.setText("0" + mMinute);
        } else {
            mMinuteTv.setText(mMinute+"");
        }
        if(mApm == Calendar.AM) {
            mApmTv.setText(R.string.time_am);
        } else {
            mApmTv.setText(R.string.time_pm);
        }
    }

    private boolean is24HourFormatEnabled() {
        return DateFormat.is24HourFormat(mContext);
    }

    private void setTime() {
        if(!is24HourFormatEnabled() && mApm == Calendar.PM && mHour != 12) {
            mHour = mHour + 12;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,mHour);
        calendar.set(Calendar.MINUTE,mMinute);
        long when = calendar.getTimeInMillis();
        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_time_dialog_hour_increase:
                hourOperation(true);
                break;
            case R.id.set_time_dialog_hour_reduce:
                hourOperation(false);
                break;
            case R.id.set_time_dialog_minute_increase:
                minOperation(true);
                break;
            case R.id.set_time_dialog_minute_reduce:
                minOperation(false);
                break;
            case R.id.set_time_dialog_apm_increase:
            case R.id.set_time_dialog_apm_reduce:
                apmOperation();
                break;
            case R.id.set_time_dialog_cancel:
                dismiss();
                break;
            case R.id.set_time_dialog_ok:
                setTime();
                break;
        }
    }

    //algorithm为ture为加法，false为减法
    private void hourOperation(boolean algorithm) {
        int hourMax;
        if(is24HourFormatEnabled()) {
            hourMax = 23;
        } else {
            hourMax = 12;
        }
        if(algorithm) {
            mHour = mHour < hourMax ? mHour+1 : 0;
        } else {
            mHour = mHour > 0 ? mHour-1 : hourMax;
        }
        if(mHour == 12) {
            mApm = Calendar.PM;
        }
        if(mHour == 0) {
            mApm = Calendar.AM;
        }
        updateTime();
    }

    private void minOperation(boolean algorithm) {
        if(algorithm) {
            mMinute = mMinute < 59 ? mMinute+1 : 0;
        } else {
            mMinute = mMinute > 0 ? mMinute-1 : 59;
        }
        updateTime();
    }

    private void apmOperation() {
        mApm = mApm == Calendar.AM ? Calendar.PM : Calendar.AM;
        if(!is24HourFormatEnabled() && mApm == Calendar.AM && mHour == 12) {
            mHour = 0;
        }
        if(!is24HourFormatEnabled() && mApm == Calendar.PM && mHour == 0) {
            mHour = 12;
        }
        updateTime();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        mDialogCallback.callBackData(null);
    }

    public interface DialogCallback {
        void callBackData(String[] data);
    }
}
