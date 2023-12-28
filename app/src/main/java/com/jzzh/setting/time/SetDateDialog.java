package com.jzzh.setting.time;

import android.annotation.SuppressLint;
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

    private TextView mYearTv, mYearIncreaseTv, mYearReduceTv;
    private TextView mMonthTv, mMonthIncreaseTv, mMonthReduceTv;
    private TextView mDateTv, mDateIncreaseTv, mDateReduceTv;
    private int mYear,mMonth,mDate;
    private Context mContext;
    private DialogCallback mDialogCallback;
    private static final String[] MONTH_ABBR={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public SetDateDialog(Context context, int i, DialogCallback callback) {
        super(context, i);
        mContext = context;
        mDialogCallback = callback;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.time_set_date_dialog);
        mYearIncreaseTv = findViewById(R.id.set_date_dialog_year_increase);
        mYearIncreaseTv.setOnClickListener(this);
        mYearReduceTv = findViewById(R.id.set_date_dialog_year_reduce);
        mYearReduceTv.setOnClickListener(this);
        mMonthIncreaseTv = findViewById(R.id.set_date_dialog_month_increase);
        mMonthIncreaseTv.setOnClickListener(this);
        mMonthReduceTv = findViewById(R.id.set_date_dialog_month_reduce);
        mMonthReduceTv.setOnClickListener(this);
        mDateIncreaseTv = findViewById(R.id.set_date_dialog_date_increase);
        mDateIncreaseTv.setOnClickListener(this);
        mDateReduceTv = findViewById(R.id.set_date_dialog_date_reduce);
        mDateReduceTv.setOnClickListener(this);
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

    @SuppressLint("SetTextI18n")
    private void updateDate() {
        if (mDate > getDaysOfCurrentMonth()) {
            mDate = getDaysOfCurrentMonth();
        }
        mYearTv.setText(mYear + "");
        if (mYear - 1 < 1970) {
            mYearReduceTv.setText(2991 + "");
        } else {
            mYearReduceTv.setText(mYear - 1 + "");
        }
        if (mYear + 1 > 2991) {
            mYearIncreaseTv.setText(1970 + "");
        } else {
            mYearIncreaseTv.setText(mYear + 1 + "");
        }

        mMonthTv.setText(MONTH_ABBR[mMonth - 1]);
        if (mMonth - 1 < 1) {
            mMonthReduceTv.setText(MONTH_ABBR[11]);
        } else {
            mMonthReduceTv.setText(MONTH_ABBR[mMonth - 2]);
        }
        if (mMonth + 1 > 12) {
            mMonthIncreaseTv.setText(MONTH_ABBR[0]);
        } else {
            mMonthIncreaseTv.setText(MONTH_ABBR[mMonth]);
        }

        mDateTv.setText(mDate + "");
        if (mDate - 1 < 1) {
            mDateReduceTv.setText(getDaysOfCurrentMonth() + "");
        } else {
            mDateReduceTv.setText(mDate - 1 + "");
        }
        if (mDate + 1 > getDaysOfCurrentMonth()) {
            mDateIncreaseTv.setText(1 + "");
        } else {
            mDateIncreaseTv.setText(mDate + 1 + "");
        }
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
