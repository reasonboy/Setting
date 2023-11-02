package com.jzzh.setting.language;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jzzh.setting.R;
import com.jzzh.setting.network.wifi.WifiActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeyboardLayout extends LinearLayout implements AdapterView.OnItemClickListener{

    private static final  String DEFAULT_IMI_ID = "com.inno.spaceime/.SoftKeyboard";

    private InputMethodManager mImm;
    private ListView mListView;
    private KeyboardAdapter mAdapter;

    private Context mContext;
    private ViewGroup mLayout;
    private onFunctionButtonClickListener mOnFunctionButtonClickListener;

    public KeyboardLayout(Context context) {
        super(context);
    }

    public KeyboardLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mImm = context.getSystemService(InputMethodManager.class);

        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.keyboard_layout,this, false);
        addView(mLayout);

        findViewById(R.id.title_function_1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnFunctionButtonClickListener!=null) {
                    mOnFunctionButtonClickListener.onClick(view);
                }
            }
        });
        mListView = findViewById(R.id.keyboard_listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new KeyboardAdapter(mContext,sortList(mImm.getEnabledInputMethodList()));
        mListView.setAdapter(mAdapter);
    }

    public void setOnFunctionButtonClickListener(onFunctionButtonClickListener l) {
        mOnFunctionButtonClickListener = l;
    }

    public void updateView() {
        List<InputMethodInfo> list = sortList(mImm.getEnabledInputMethodList());
        mAdapter.setDate(list);
        mAdapter.notifyDataSetChanged();
    }

    private List<InputMethodInfo> sortList(List<InputMethodInfo> list){
        List<InputMethodInfo> set = new ArrayList<>();
        for (InputMethodInfo info : list) { // 将DEFAULT_IMI_ID置于首位
            if (info.getId().equals(DEFAULT_IMI_ID))
                set.add(0, info);
            else
                set.add(info);
        }
        return set;
    }


    private String getDefInputMethod() {
        return Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    private void setDefInputMethod(String method) {
        Settings.Secure.putString(mContext.getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,method);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        InputMethodInfo info = mAdapter.getItem(i);
        setDefInputMethod(info.getId());
        mAdapter.notifyDataSetChanged();
    }

    public class KeyboardAdapter extends BaseAdapter {

        private List<InputMethodInfo> mData;
        private Context mContext;

        public KeyboardAdapter(Context context, List<InputMethodInfo> date) {
            mContext = context;
            mData = date;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public InputMethodInfo getItem(int i) {
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
            InputMethodInfo info = mData.get(i);
            CharSequence title = "";
            try {
                title = mContext.getPackageManager().getApplicationLabel(mContext.getPackageManager().getApplicationInfo(
                        info.getPackageName(), PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            viewHolder.title.setText(title);

            String infoId = info.getId();
            Log.v("xml_log_ime","infoId = " + infoId);
            if(infoId.equals(getDefInputMethod())) {
                viewHolder.icon.setBackground(mContext.getDrawable(R.drawable.check_on));
            } else {
                viewHolder.icon.setBackground(mContext.getDrawable(R.drawable.check_off));
            }
            return view;
        }

        public void setDate(List<InputMethodInfo> list) {
            mData = list;
        }

        private class ViewHolder {
            TextView title;
            ImageView icon;
        }
    }

    public interface onFunctionButtonClickListener {
        void onClick(View view);
    }
}
