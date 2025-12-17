package com.jzzh.setting.keygesture;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

import java.util.ArrayList;

public class PhysicalKeySettingActivity extends BaseActivity implements ZhCheckBox.OnZhCheckedChangeListener,
        View.OnClickListener, AdapterView.OnItemClickListener{

    public static final int PAGE_KEY = 0;
    public static final int VOLUME_KEY = 1;
    public static final int DPAD_KEY = 2;
    public static final String PHYSICAL_KEY_MODE = "physical_key_mode";
    public static final String REVERSE_PHYSICAL_KEY = "reverse_physical_key";
    private ArrayList<Data> mListData;
    private ListView mListView;
    private KeySettingAdapter mAdapter;
    private TextView mReverseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture_physical);
        initData();
        mReverseKey = findViewById(R.id.reverse_key_switch);
        mReverseKey.setOnClickListener(this);
        mListView = findViewById(R.id.key_setting_listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new KeySettingAdapter(this,mListData);
        mAdapter.setSelectedPosition(getSelectedPosition());
        mListView.setAdapter(mAdapter);
        setReverseKeyState();
    }

    private void initData() {
        mListData = new ArrayList<>();
        mListData.add(new Data(getString(R.string.setting_physical_key_page), PAGE_KEY));
        mListData.add(new Data(getString(R.string.setting_physical_key_volume), VOLUME_KEY));
        mListData.add(new Data(getString(R.string.setting_physical_key_dpad), DPAD_KEY));
    }

    private int getSelectedPosition() {
        int keyMode = getKeyMode();
        int position = 0;
        for (int i=0;i<mListData.size();i++) {
            if(keyMode == mListData.get(i).mode) {
                position = i;
            }
        }
        return position;
    }

    private void updateDisplay() {
        int position = getSelectedPosition();
        mAdapter.setSelectedPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    private void setReverseKeyState() {
        int reverseKey = getKeyReverse();
        if(reverseKey == 1) {
            mReverseKey.setBackground(getDrawable(R.drawable.display_def_apply_frame));
            mReverseKey.setTextColor(Color.BLACK);
        } else {
            mReverseKey.setBackground(getDrawable(R.drawable.display_def_apply_frame_selected));
            mReverseKey.setTextColor(Color.WHITE);
        }
    }

    private int getKeyMode() {
        int mode = Settings.System.getInt(getContentResolver(),PHYSICAL_KEY_MODE,PAGE_KEY);
        return mode;
    }

    private void setKeyMode(int mode) {
        Settings.System.putInt(getContentResolver(),PHYSICAL_KEY_MODE,mode);
    }

    private int getKeyReverse() {
        int mode = Settings.System.getInt(getContentResolver(),REVERSE_PHYSICAL_KEY,0);
        return mode;
    }

    private void setKeyReverse(int mode) {
        Settings.System.putInt(getContentResolver(),REVERSE_PHYSICAL_KEY,mode);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int mode = mListData.get(i).mode;
        setKeyMode(mode);
        updateDisplay();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.reverse_key_switch) {
            int reverseKey = getKeyReverse();
            if (reverseKey == 1) {
                setKeyReverse(0);
            } else {
                setKeyReverse(1);
            }
            setReverseKeyState();
        }
    }

    @Override
    public void onCheckedChanged(boolean checked) {
        setKeyReverse(checked ? 1 : 0);
    }

    public class KeySettingAdapter extends BaseAdapter {

        private ArrayList<Data> mData;
        private Context mContext;
        private int mSelectedPosition;

        public KeySettingAdapter(Context context, ArrayList<Data> data) {
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
                viewHolder.icon.setBackground(mContext.getDrawable(R.drawable.check_on));
            } else {
                viewHolder.icon.setBackground(mContext.getDrawable(R.drawable.check_off));
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
        public int mode;

        public Data(String title, int mode) {
            this.title = title;
            this.mode = mode;
        }
    }
}