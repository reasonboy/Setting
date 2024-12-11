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

public class AutoPowerOffActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final  int POWER_OFF_30_MIN = 30 * 60 * 1000;
    private static final  int POWER_OFF_1_HOUR = 60 * 60 * 1000;
    private static final  int POWER_OFF_2_HOURS = 2 * 60 * 60 * 1000;
    private static final  int POWER_OFF_8_HOURS = 8 * 60 * 60 * 1000;
    private static final  int POWER_OFF_1_DAY = 24 * 60 * 60 * 1000;
    private static final  int POWER_OFF_2_DAYS = 2 * 24 * 60 * 60 * 1000;
    private static final  int POWER_OFF_NONE = -1;

    private ArrayList<POData> mListData;
    private ListView mListView;
    private AutoPowerOffAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_power_off);
        initData();
        mListView = findViewById(R.id.auto_power_off_listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new AutoPowerOffAdapter(this,mListData);
        mAdapter.setSelectedPosition(getSelectedPosition());
        mListView.setAdapter(mAdapter);

    }

    private void initData() {
        mListData = new ArrayList<>();
        mListData.add(new POData(getString(R.string.power_30min), POWER_OFF_30_MIN));
        mListData.add(new POData(getString(R.string.power_1hour), POWER_OFF_1_HOUR));
        mListData.add(new POData(getString(R.string.power_2hour), POWER_OFF_2_HOURS));
        mListData.add(new POData(getString(R.string.power_8hour), POWER_OFF_8_HOURS));
        mListData.add(new POData(getString(R.string.power_1day), POWER_OFF_1_DAY));
        mListData.add(new POData(getString(R.string.power_2day), POWER_OFF_2_DAYS));
        mListData.add(new POData(getString(R.string.power_none),POWER_OFF_NONE));
    }

    public int getSelectedPosition() {
        int sleepTime = getPowerOffTimeout();
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

    private int getPowerOffTimeout() {
        int time = Settings.System.getInt(getContentResolver(),"power_off_timeout",-1);
        return time;
    }

    private void setPowerOffTimeout(int time) {
        Settings.System.putInt(getContentResolver(),"power_off_timeout",time);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int sleepTime = mListData.get(i).time;
        setPowerOffTimeout(sleepTime);
        updateDisplay();
    }

    public class AutoPowerOffAdapter extends BaseAdapter {

        private ArrayList<POData> mData;
        private Context mContext;
        private int mSelectedPosition;

        public AutoPowerOffAdapter(Context context, ArrayList<POData> data) {
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
        public POData getItem(int i) {
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

    public class POData {
        public String title;
        public int time;

        public POData(String title, int time) {
            this.title = title;
            this.time = time;
        }
    }
}
