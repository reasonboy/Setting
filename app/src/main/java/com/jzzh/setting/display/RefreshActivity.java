package com.jzzh.setting.display;

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

public class RefreshActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    public static final String REFRESH_FREQUENCY_KEY = "persist_vendor_fullmode_cnt";
    private ArrayList<Data> mListData;
    private ListView mListView;
    private RefreshAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_refresh);
        initData();
        mListView = findViewById(R.id.refresh_listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new RefreshAdapter(this,mListData);
        mAdapter.setSelectedPosition(getSelectedPosition());
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        mListData = new ArrayList<>();
        mListData.add(new Data(getString(R.string.refresh_none),500));
        mListData.add(new Data(getString(R.string.refresh_1view),1));
        mListData.add(new Data(getString(R.string.refresh_5view),5));
        mListData.add(new Data(getString(R.string.refresh_10view),10));
        mListData.add(new Data(getString(R.string.refresh_15view),15));
        mListData.add(new Data(getString(R.string.refresh_20view),20));
    }

    public int getSelectedPosition() {
        int sleepTime = getRefreshFrequency();
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

    private int getRefreshFrequency() {
        int time = Settings.System.getInt(getContentResolver(),REFRESH_FREQUENCY_KEY,500);
        return time;
    }

    private void setRefreshFrequency(int time) {
        Settings.System.putInt(getContentResolver(),REFRESH_FREQUENCY_KEY,time);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int sleepTime = mListData.get(i).time;
        setRefreshFrequency(sleepTime);
        updateDisplay();
    }

    public class RefreshAdapter extends BaseAdapter {

        private ArrayList<Data> mData;
        private Context mContext;
        private int mSelectedPosition;

        public RefreshAdapter(Context context, ArrayList<Data> data) {
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
        public Data getItem(int i) {
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

    public class Data {
        public String title;
        public int time;

        public Data(String title, int time) {
            this.title = title;
            this.time = time;
        }
    }
}
