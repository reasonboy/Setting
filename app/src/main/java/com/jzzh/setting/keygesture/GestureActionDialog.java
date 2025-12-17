package com.jzzh.setting.keygesture;

import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jzzh.setting.Const.Gesture;
import com.jzzh.setting.Const.GestureType;
import com.jzzh.setting.R;

import java.util.ArrayList;

public class GestureActionDialog extends Dialog {
    static final String BOTTOM_LEFT_GESTURE_TYPE = "bottom_left_gesture_type";
    static final String BOTTOM_MIDDLE_GESTURE_TYPE = "bottom_middle_gesture_type";
    static final String BOTTOM_RIGHT_GESTURE_TYPE = "bottom_right_gesture_type";
    static final String LEFT_GESTURE_TYPE = "left_gesture_type";
    static final String RIGHT_GESTURE_TYPE = "right_gesture_type";

    private Context mContext;
    private Gesture mGesture;
    private ListView mListView;
    private GestureActionAdapter mAdapter;
    private ArrayList<Data> mData;
    private OnGestureSelectedListener mOnGestureSelectedListener;

    public GestureActionDialog(Context context, Gesture gesture) {
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
        mData.add(new Data(mContext.getString(R.string.gesture_none), GestureType.NONE.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_back), GestureType.BACK.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_home), GestureType.HOME.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_refresh), GestureType.REFRESH.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_task_manager), GestureType.TASK_MANAGER.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_eink_center), GestureType.EINK_CENTER.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_key_setting), GestureType.KEY_SETTINGS.ordinal()));
        mData.add(new Data(mContext.getString(R.string.gesture_user_app), GestureType.USER_APP.ordinal()));
    }

    private void setupListView() {
        int currentSelection = getCurrentGestureType();
        mAdapter = new GestureActionAdapter(mContext, mData);
        mAdapter.setSelectedPosition(currentSelection);
        mListView.setAdapter(mAdapter);
        
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                mAdapter.notifyDataSetChanged();
                
                Data selectedData = mData.get(position);
                GestureType gestureType = GestureType.values()[selectedData.mode];

                if (gestureType == GestureType.USER_APP) {
                    setGestureType(selectedData.mode);
                    AppSelectionDialog appDialog = new AppSelectionDialog(mContext, mGesture);
                    appDialog.setOnDismissListener(dialog -> {
                        if (mOnGestureSelectedListener != null) {
                            mOnGestureSelectedListener.onGestureSelected(gestureType);
                        }
                    });
                    appDialog.show();
                    dismiss();
                } else {
                    setGestureType(selectedData.mode);
                    if (mOnGestureSelectedListener != null) {
                        mOnGestureSelectedListener.onGestureSelected(gestureType);
                    }
                    dismiss();
                }
            }
        });
    }

    private int getCurrentGestureType() {
        String key = getSettingsKey();
        return Settings.System.getInt(mContext.getContentResolver(), key, 0);
    }

    private void setGestureType(int type) {
        String key = getSettingsKey();
        Settings.System.putInt(mContext.getContentResolver(), key, type);
    }

    private String getSettingsKey() {
        switch (mGesture) {
            case BOTTOM_LEFT:
                return BOTTOM_LEFT_GESTURE_TYPE;
            case BOTTOM_MIDDLE:
                return BOTTOM_MIDDLE_GESTURE_TYPE;
            case BOTTOM_RIGHT:
                return BOTTOM_RIGHT_GESTURE_TYPE;
            case LEFT:
                return LEFT_GESTURE_TYPE;
            case RIGHT:
                return RIGHT_GESTURE_TYPE;
            default:
                return BOTTOM_LEFT_GESTURE_TYPE;
        }
    }

    public void setOnGestureSelectedListener(OnGestureSelectedListener listener) {
        mOnGestureSelectedListener = listener;
    }

    public interface OnGestureSelectedListener {
        void onGestureSelected(GestureType gestureType);
    }

    public class GestureActionAdapter extends BaseAdapter {

        private ArrayList<Data> mData;
        private Context mContext;
        private int mSelectedPosition;

        public GestureActionAdapter(Context context, ArrayList<Data> data) {
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
