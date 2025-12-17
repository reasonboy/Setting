package com.jzzh.setting.keygesture;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jzzh.setting.Const;
import com.jzzh.setting.Const.Gesture;
import com.jzzh.setting.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppSelectionDialog extends Dialog {
    public static final String BOTTOM_LEFT_GESTURE_APPLICATION = "bottom_left_gesture_application";
    public static final String BOTTOM_MIDDLE_GESTURE_APPLICATION = "bottom_middle_gesture_application";
    public static final String BOTTOM_RIGHT_GESTURE_APPLICATION = "bottom_right_gesture_application";
    public static final String LEFT_GESTURE_APPLICATION = "left_gesture_application";
    public static final String RIGHT_GESTURE_APPLICATION = "right_gesture_application";

    private Context mContext;
    private Gesture mGesture;
    private ListView mListView;
    private AppSelectionAdapter mAdapter;
    private ArrayList<Data> mData;

    public AppSelectionDialog(Context context, Gesture gesture) {
        super(context);
        mContext = context;
        mGesture = gesture;
        setContentView(R.layout.dialog_setting_gesture);
        
        initViews();
        initData();
        setupListView();
    }

    private void initViews() {
        mListView = findViewById(R.id.gesture_setting_listview);
        ImageView exitBtn = findViewById(R.id.exit);
        exitBtn.setOnClickListener(v -> dismiss());
    }

    private void initData() {
        mData = new ArrayList<>();
        loadUserApps();
    }

    private void loadUserApps() {
        PackageManager pm = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        
        List<String> blacklist = Arrays.asList(Const.APP_BLACKLIST);
        List<String> whitelist = Arrays.asList(Const.APP_WHITELIST);
        
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;

            if (blacklist.contains(packageName)) {
                continue;
            }

            boolean isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSystemApp && !whitelist.contains(packageName)) {
                continue;
            }
            
            String appName = resolveInfo.loadLabel(pm).toString();
            Drawable appIcon = resolveInfo.loadIcon(pm);
            mData.add(new Data(appName, packageName, appIcon));
        }

        Collections.sort(mData, new Comparator<Data>() {
            @Override
            public int compare(Data d1, Data d2) {
                return d1.title.compareTo(d2.title);
            }
        });
    }

    private void setupListView() {
        String currentPackageName = getGestureApplication();
        int currentSelection = -1;

        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).package_name.equals(currentPackageName)) {
                currentSelection = i;
                break;
            }
        }
        
        mAdapter = new AppSelectionAdapter(mContext, mData);
        mAdapter.setSelectedPosition(currentSelection);
        mListView.setAdapter(mAdapter);
        
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                mAdapter.notifyDataSetChanged();
                setGestureApplication(mData.get(position).package_name);
                dismiss();
            }
        });
    }

    private String getGestureApplication() {
        String key = getSettingsKey();
        String packageName = Settings.System.getString(mContext.getContentResolver(), key);
        return packageName != null ? packageName : "";
    }

    private void setGestureApplication(String packageName) {
        String key = getSettingsKey();
        Settings.System.putString(mContext.getContentResolver(), key, packageName);
    }

    private String getSettingsKey() {
        switch (mGesture) {
            case BOTTOM_LEFT:
                return BOTTOM_LEFT_GESTURE_APPLICATION;
            case BOTTOM_MIDDLE:
                return BOTTOM_MIDDLE_GESTURE_APPLICATION;
            case BOTTOM_RIGHT:
                return BOTTOM_RIGHT_GESTURE_APPLICATION;
            case LEFT:
                return LEFT_GESTURE_APPLICATION;
            case RIGHT:
                return RIGHT_GESTURE_APPLICATION;
            default:
                return BOTTOM_LEFT_GESTURE_APPLICATION;
        }
    }

    public class AppSelectionAdapter extends BaseAdapter {

        private ArrayList<Data> mData;
        private Context mContext;
        private int mSelectedPosition;

        public AppSelectionAdapter(Context context, ArrayList<Data> data) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.select_item_small, viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.title = view.findViewById(R.id.select_title);
                viewHolder.icon = view.findViewById(R.id.select_icon);
                viewHolder.app_icon = view.findViewById(R.id.app_icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Data data = mData.get(i);
            viewHolder.title.setText(data.title);

            if (data.app_icon != null) {
                viewHolder.app_icon.setImageDrawable(data.app_icon);
                viewHolder.app_icon.setVisibility(View.VISIBLE);
            }

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
            ImageView app_icon;
        }
    }

    public class Data {
        public String title;
        public String package_name;
        public Drawable app_icon;

        public Data(String title, String package_name, Drawable app_icon) {
            this.title = title;
            this.package_name = package_name;
            this.app_icon = app_icon;
        }
    }

}
