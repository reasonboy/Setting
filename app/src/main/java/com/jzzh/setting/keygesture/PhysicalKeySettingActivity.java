package com.jzzh.setting.keygesture;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.util.ArrayList;

public class PhysicalKeySettingActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final int PAGE_KEY = 0;
    public static final int VOLUME_KEY = 1;
    public static final int DPAD_KEY = 2;
    public static final String PHYSICAL_KEY_MODE = "physical_key_mode";
    public static final String REVERSE_PHYSICAL_KEY = "reverse_physical_key";
    private ArrayList<Data> mListData;
    private KeySettingAdapter mAdapter;
    private TextView mReverseKey;
    private KeyGestureItem mShortPressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygesture_physical);
        initData();

        mShortPressItem = findViewById(R.id.short_press);
        mShortPressItem.setOnClickListener(this);
        updateShortPressValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateShortPressValue();
    }

    private void initData() {
        mListData = new ArrayList<>();
        mListData.add(new Data(getString(R.string.setting_physical_key_volume),
                getString(R.string.setting_physical_key_volume_value), VOLUME_KEY));
        mListData.add(new Data(getString(R.string.setting_physical_key_page),
                getString(R.string.setting_physical_key_page_value), PAGE_KEY));
        mListData.add(new Data(getString(R.string.setting_physical_key_dpad),
                getString(R.string.setting_physical_key_dpad_value), DPAD_KEY));
    }

    private void showShortPressDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_volume_key_setting);

        mReverseKey = dialog.findViewById(R.id.zh_reverse_key_switch);
        mReverseKey.setOnClickListener(this);

        ListView listView = dialog.findViewById(R.id.key_setting_listview);
        listView.setOnItemClickListener(this);
        mAdapter = new KeySettingAdapter(this, mListData);
        mAdapter.setSelectedPosition(getSelectedPosition());
        listView.setAdapter(mAdapter);

        dialog.findViewById(R.id.exit).setOnClickListener(view -> dialog.dismiss());
        setReverseKeyState();
        dialog.show();
    }

    private int getSelectedPosition() {
        int keyMode = getKeyMode();
        int position = 0;
        for (int i = 0; i < mListData.size(); i++) {
            if (keyMode == mListData.get(i).mode) {
                position = i;
            }
        }
        return position;
    }

    private void updateDisplay() {
        int position = getSelectedPosition();
        mAdapter.setSelectedPosition(position);
        mAdapter.notifyDataSetChanged();
        updateShortPressValue();
    }

    private void updateShortPressValue() {
        int position = getSelectedPosition();
        mShortPressItem.setValue(mListData.get(position).value);
    }

    private void setReverseKeyState() {
        int reverseKey = getKeyReverse();
        if (reverseKey == 1) {
            mReverseKey.setBackground(getDrawable(R.drawable.zh_button_bg_on));
            mReverseKey.setTextColor(Color.BLACK);
        } else {
            mReverseKey.setBackground(getDrawable(R.drawable.zh_button_bg_off));
            mReverseKey.setTextColor(Color.WHITE);
        }
    }

    private int getKeyMode() {
        return Settings.System.getInt(getContentResolver(), PHYSICAL_KEY_MODE, PAGE_KEY);
    }

    private void setKeyMode(int mode) {
        Settings.System.putInt(getContentResolver(), PHYSICAL_KEY_MODE, mode);
    }

    private int getKeyReverse() {
        return Settings.System.getInt(getContentResolver(), REVERSE_PHYSICAL_KEY, 0);
    }

    private void setKeyReverse(int mode) {
        Settings.System.putInt(getContentResolver(), REVERSE_PHYSICAL_KEY, mode);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        int mode = mListData.get(position).mode;
        setKeyMode(mode);
        updateDisplay();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.short_press) {
            showShortPressDialog();
        } else if (view.getId() == R.id.zh_reverse_key_switch) {
            int reverseKey = getKeyReverse();
            if (reverseKey == 1) {
                setKeyReverse(0);
            } else {
                setKeyReverse(1);
            }
            setReverseKeyState();
        }
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
        public Data getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.volume_key_dialog_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.title = view.findViewById(R.id.select_title);
                viewHolder.icon = view.findViewById(R.id.select_icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText(mData.get(position).title);
            if (position == mSelectedPosition) {
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
        public String value;
        public int mode;

        public Data(String title, String value, int mode) {
            this.title = title;
            this.value = value;
            this.mode = mode;
        }
    }
}
