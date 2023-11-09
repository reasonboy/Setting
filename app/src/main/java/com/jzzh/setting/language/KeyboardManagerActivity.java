package com.jzzh.setting.language;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.tools.ZhCheckBox;

import java.util.ArrayList;
import java.util.List;

public class KeyboardManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final  String DEFAULT_IMI_ID = "com.inno.spaceime/.SoftKeyboard";

    private InputMethodManager mImm;
    private ListView mListView;
    private KeyboardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_manager);
        mImm = getSystemService(InputMethodManager.class);
        mListView = findViewById(R.id.keyboard_manager_listview);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<InputMethodInfo> infoList = sortList(mImm.getInputMethodList());
        mAdapter = new KeyboardAdapter(this,infoList);
        mListView.setAdapter(mAdapter);
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

    private boolean isInputMethodInfoEnable(InputMethodInfo info) {
        List<InputMethodInfo> list = mImm.getEnabledInputMethodList();
        for (InputMethodInfo inputMethodInfo : list) {
            if(inputMethodInfo.getId().equals(info.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_checkbox, viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.title = view.findViewById(R.id.list_item_title);
                viewHolder.checkBox = view.findViewById(R.id.list_item_switch);
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
            viewHolder.checkBox.setCheck(isInputMethodInfoEnable(info));
            viewHolder.checkBox.setOnZhCheckedChangeListener(new ZhCheckBox.OnZhCheckedChangeListener() {
                @Override
                public void onCheckedChanged(boolean checked) {
                    Log.v("xml_log_ime","checked="+checked);
                    updateInputMethodInfoEnable(info,checked);
                    notifyDataSetChanged();
                }
            });
            return view;
        }

        private class ViewHolder {
            TextView title;
            ZhCheckBox checkBox;
        }
    }

    private void updateInputMethodInfoEnable(InputMethodInfo info, boolean checked) {
        if(info.getId().equals(DEFAULT_IMI_ID)) {
            Toast.makeText(this,R.string.keyboard_msg,Toast.LENGTH_LONG).show();
            return;
        }
        String currentInputMethodId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        List<InputMethodInfo> enabledInputMethodList = mImm.getEnabledInputMethodList();
        List<String> enabledInputMethodStringList = new ArrayList<>();
        for (InputMethodInfo inputMethodInfo : enabledInputMethodList) {
            enabledInputMethodStringList.add(inputMethodInfo.getId());
        }
        if(checked) {
            enabledInputMethodStringList.add(info.getId());
        } else {
            enabledInputMethodStringList.remove(info.getId());
            if(info.getId().equals(currentInputMethodId)) {
                Settings.Secure.putString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,DEFAULT_IMI_ID);
            }
        }
        String result = buildInputMethodsString(enabledInputMethodStringList);
        Log.v("xml_log_ime","result="+result);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS,result);
    }

    private static String buildInputMethodsString(List<String> imiList) {
        final StringBuilder builder = new StringBuilder();
        for (final String imi : imiList) {
            if (builder.length() > 0) {
                builder.append(":");
            }
            builder.append(imi);
        }
        return builder.toString();
    }
}
