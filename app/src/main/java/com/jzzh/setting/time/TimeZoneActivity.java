package com.jzzh.setting.time;

import android.app.AlarmManager;
import android.content.Context;
import android.icu.util.TimeZone;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.PageIndication;

import java.util.ArrayList;
import java.util.List;

public class TimeZoneActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private TimeZoneAdapter mAdapter;
    private PageIndication mPageIndication;
    private int mCurPage = 1,mTotalPage,mListViewItemNum = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_zone);
        mPageIndication = findViewById(R.id.page_indication);
        mPageIndication.setOnPageChangeListener(new PageIndication.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page) {
                mCurPage = page;
                updateList();
            }
        });
        mListView = findViewById(R.id.time_zone_list);
        mListView.setOnItemClickListener(this);
        mAdapter = new TimeZoneAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        mAdapter.setData(getSubScanResult(ZoneGetter.readTimezonesToDisplay(this)));
        mAdapter.notifyDataSetChanged();
        mPageIndication.init(mCurPage,mTotalPage);
    }

    private List getSubScanResult(List<String[]> parent) {
        List<String[]> list = new ArrayList<String[]>();
        mTotalPage = parent.size() / mListViewItemNum;
        if(parent.size() % mListViewItemNum != 0) {
            mTotalPage++;
        }
        if(mCurPage > mTotalPage ) {
            mCurPage = mTotalPage ;
        }
        //Log.v("xml_log_app","mTotalPage = " + mTotalPage);
        for(int i = 0;i < parent.size();i++) {
            if(i >= (mCurPage - 1) * mListViewItemNum && i < mCurPage * mListViewItemNum) {
                list.add(parent.get(i));
            }
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String[] data = (String[]) adapterView.getItemAtPosition(i);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone(data[1]);
        finish();
    }

    public class TimeZoneAdapter extends BaseAdapter {

        private List<String[]> mData = new ArrayList<String[]>();;
        private Context mContext;

        public TimeZoneAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<String[]> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String[] getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.time_zone_list_item, viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.city = view.findViewById(R.id.time_zone_city);
                viewHolder.code = view.findViewById(R.id.time_zone_code);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.city.setText(mData.get(i)[0]);
            TimeZone timeZone = TimeZone.getTimeZone(mData.get(i)[1]);
            viewHolder.code.setText(ZoneGetter.offsetToGMT(timeZone.getRawOffset()));
            return view;
        }

        private class ViewHolder {
            TextView city;
            TextView code;
        }
    }
}
