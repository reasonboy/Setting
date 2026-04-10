package com.jzzh.setting.language;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jzzh.setting.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

public class LanguageLayout extends LinearLayout implements AdapterView.OnItemClickListener{

    private static final  String LANGUAGE_KOREAN = "ko";
    private static final  String LANGUAGE_ENGLISH = "en";

    private ArrayList<LanguageData> mListData;
    private ListView mListView;
    private LanguageAdapter mAdapter;

    private Context mContext;
    private ViewGroup mLayout;

    public LanguageLayout(Context context) {
        super(context);
    }

    public LanguageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        mLayout = (ViewGroup)factory.inflate(R.layout.language_layout,this, false);
        addView(mLayout);

        initData();
        mListView = findViewById(R.id.language_listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new LanguageAdapter(mContext,mListData);
        mAdapter.setSelectedPosition(getSelectedPosition());
        mListView.setAdapter(mAdapter);
    }



    private void initData() {
        mListData = new ArrayList<>();
        mListData.add(new LanguageData(mContext.getString(R.string.language_ko),LANGUAGE_KOREAN));
        mListData.add(new LanguageData(mContext.getString(R.string.language_en),LANGUAGE_ENGLISH));
    }

    public int getSelectedPosition() {
        String language = getLanguage();
        int position = 0;
        for (int i=0;i<mListData.size();i++) {
            if(language.equals(mListData.get(i).language)) {
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

    private void setLanguage(Locale locale) {
        try {
            Locale.setDefault(locale);

            Class<?> amClz = Class.forName("android.app.ActivityManager");
            Object iAm = amClz.getMethod("getService").invoke(null);

            Configuration config;
            try {
                Method getConfiguration = iAm.getClass().getMethod("getConfiguration");
                config = (Configuration) getConfiguration.invoke(iAm);
            } catch (Throwable ignore) {
                config = new Configuration();
            }

            if (config == null) config = new Configuration();

            config.setLocales(new android.os.LocaleList(locale));

            try {
                java.lang.reflect.Field userSetLocale = Configuration.class.getField("userSetLocale");
                userSetLocale.setBoolean(config, true);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // AOSP 12~14
            try {
                Method m = iAm.getClass().getMethod(
                        "updatePersistentConfigurationWithAttribution",
                        Configuration.class, String.class, String.class
                );
                m.invoke(iAm, config, getContext().getPackageName(), null);
            } catch (NoSuchMethodException e) {
                // AOSP 11 fallback
                Method m = iAm.getClass().getMethod(
                        "updatePersistentConfiguration",
                        Configuration.class
                );
                m.invoke(iAm, config);
            }

            BackupManager.dataChanged("com.android.providers.settings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLanguage() {
        return Locale.getDefault().getLanguage().toLowerCase();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String language = mListData.get(i).language;
        setLanguage(getLocaleByLanguage(language));
        updateDisplay();
    }

    private Locale getLocaleByLanguage(String language) {
        Locale locale = Locale.ENGLISH;
        if(language.equals(LANGUAGE_KOREAN)) {
            return Locale.KOREAN;
        } else if(language.equals(LANGUAGE_ENGLISH)) {
            return Locale.ENGLISH;
        }
        return locale;
    }

    public class LanguageAdapter extends BaseAdapter {

        private ArrayList<LanguageData> mData;
        private Context mContext;
        private int mSelectedPosition;

        public LanguageAdapter(Context context, ArrayList<LanguageData> data) {
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
        public LanguageData getItem(int i) {
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

    public class LanguageData {
        public String title;
        public String language;

        public LanguageData(String title, String language) {
            this.title = title;
            this.language = language;
        }
    }
}
