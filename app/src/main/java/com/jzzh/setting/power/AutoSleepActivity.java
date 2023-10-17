package com.jzzh.setting.power;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.util.ArrayList;

public class AutoSleepActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final  int SLEEP_2_MIN = 2 * 60 * 1000;
    private static final  int SLEEP_5_MIN = 5 * 60 * 1000;
    private static final  int SLEEP_10_MIN = 10 * 60 * 1000;
    private static final  int SLEEP_30_MIN = 30 * 60 * 1000;
    private static final  int SLEEP_60_MIN = 60 * 60 * 1000;
    private static final  int SLEEP_NONE = 10 * 24 * 60 * 60 * 1000;

    private ArrayList<ASData> mListData;
    private ListView mListView;
    private AutoSleepAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_sleep);
        initData();
        mListView = findViewById(R.id.auto_sleep_listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new AutoSleepAdapter(this,mListData);
        mAdapter.setSelectedPosition(getSelectedPosition());
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        mListData = new ArrayList<>();
        mListData.add(new ASData(getString(R.string.sleep_2min),SLEEP_2_MIN));
        mListData.add(new ASData(getString(R.string.sleep_5min),SLEEP_5_MIN));
        mListData.add(new ASData(getString(R.string.sleep_10min),SLEEP_10_MIN));
        mListData.add(new ASData(getString(R.string.sleep_30min),SLEEP_30_MIN));
        mListData.add(new ASData(getString(R.string.sleep_60min),SLEEP_60_MIN));
        mListData.add(new ASData(getString(R.string.sleep_none),SLEEP_NONE));
    }

    public int getSelectedPosition() {
        int sleepTime = getScreenOffTimeout();
        int position = 0;
        for (int i=0;i<mListData.size();i++) {
            if(sleepTime == mListData.get(i).time) {
                position = i;
            }
        }
        return position;
    }

    public void updateDisplay() {
        int position = getSelectedPosition();
        mAdapter.setSelectedPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    private int getScreenOffTimeout() {
        int time = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,0);
        return time;
    }

    private void setScreenOffTimeout(int time) {
        Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,time);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int sleepTime = mListData.get(i).time;
        setScreenOffTimeout(sleepTime);
        updateDisplay();
    }

    public class AutoSleepAdapter extends BaseAdapter {

        private ArrayList<ASData> mData;
        private Context mContext;
        private int mSelectedPosition;

        public AutoSleepAdapter(Context context, ArrayList<ASData> data) {
            mContext = context;
            mData = data;
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public ASData getItem(int i) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.select_item, viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.title = view.findViewById(R.id.select_title);
                viewHolder.icon = view.findViewById(R.id.select_icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText(mData.get(i).title);
            if(i == mSelectedPosition) {
                viewHolder.icon.setBackground(getDrawable(R.drawable.check_on));
            } else {
                viewHolder.icon.setBackground(getDrawable(R.drawable.check_off));
            }
            return view;
        }

        private class ViewHolder {
            TextView title;
            ImageView icon;
        }
    }

    public class ASData {
        public String title;
        public int time;

        public ASData(String title, int time) {
            this.title = title;
            this.time = time;
        }
    }
}
