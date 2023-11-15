package com.jzzh.setting.time;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jzzh.setting.R;

import java.util.Calendar;

public class SetDateDialog extends Dialog implements View.OnClickListener{

    private TextView mYearTv,mMonthTv,mDateTv;
    private int mYear,mMonth,mDate;
    private Context mContext;
    private DialogCallback mDialogCallback;

    public SetDateDialog(Context context, int i, DialogCallback callback) {
        super(context, i);
        mContext = context;
        mDialogCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.time_set_date_dialog);
        findViewById(R.id.set_date_dialog_year_increase).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_year_reduce).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_month_increase).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_month_reduce).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_date_increase).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_date_reduce).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_cancel).setOnClickListener(this);
        findViewById(R.id.set_date_dialog_ok).setOnClickListener(this);
        mYearTv = findViewById(R.id.set_date_dialog_year);
        mMonthTv = findViewById(R.id.set_date_dialog_month);
        mDateTv = findViewById(R.id.set_date_dialog_date);
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDate = calendar.get(Calendar.DAY_OF_MONTH);
        updateDate();
        Log.v("xml_log_time",mYear+"-"+mMonth+"-"+mDate);
    }

    private void updateDate() {
        if(mDate > getDaysOfCurrentMonth()) {
            mDate = getDaysOfCurrentMonth();
        }
        mYearTv.setText(mYear+"");
        mMonthTv.setText(mMonth+"");
        mDateTv.setText(mDate+"");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_date_dialog_year_increase:
                if(mYear < 2991) {
                    mYear++;
                }
                updateDate();
                break;
            case R.id.set_date_dialog_year_reduce:
                if(mYear > 1970) {
                    mYear--;
                }
                updateDate();
                break;
            case R.id.set_date_dialog_month_increase:
                if(mMonth < 12) {
                    mMonth++;
                } else {
                    mMonth = 1;
                }
                updateDate();
                break;
            case R.id.set_date_dialog_month_reduce:
                if(mMonth > 1) {
                    mMonth--;
                } else {
                    mMonth = 12;
                }
                updateDate();
                break;
            case R.id.set_date_dialog_date_increase:
                if(mDate < getDaysOfCurrentMonth()) {
                    mDate++;
                } else {
                    mDate = 1;
                }
                updateDate();
                break;
            case R.id.set_date_dialog_date_reduce:
                if(mDate > 1) {
                    mDate--;
                } else {
                    mDate = getDaysOfCurrentMonth();
                }
                updateDate();
                break;
            case R.id.set_date_dialog_cancel:
                dismiss();
                break;
            case R.id.set_date_dialog_ok:
                setDate();
                break;
        }
    }

    private int getDaysOfCurrentMonth() {
        if(mMonth == 1 || mMonth == 3 || mMonth == 5 || mMonth == 7 || mMonth == 8 || mMonth == 10 || mMonth == 12) {
            return 31;
        } else if (mMonth == 4 || mMonth == 6 || mMonth == 9 || mMonth == 11) {
            return 30;
        } else if(mMonth == 2) {
            if((mYear%4==0 && mYear%100!=0)|| mYear%400==0) {
                return 29;
            } else {
                return 28;
            }
        }
        return 0;
    }


    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,mYear);
        calendar.set(Calendar.MONTH,mMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH,mDate);
        long when = calendar.getTimeInMillis();
        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
        dismiss();
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
