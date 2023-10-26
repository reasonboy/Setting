package com.jzzh.setting.task;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends BaseActivity implements TasksAdapter.OnChoiceClickListener,
        TasksAdapter.OnOnLongClickListener,View.OnClickListener {

    private static final int MSG_UPDATE_RECYCLERVIEW = 0;
    private static final int MSG_UPDATE_MEMORY = 1;
    private static int SPAN_COUNT = 5;
    private ActivityManager mActivityManager;
    private RecyclerView mRecyclerView;
    private TasksAdapter mAdapter;
    private int mState = TasksAdapter.STATE_NORMAL;

    private TextView mMemory;
    private View mChooseBtn,mCleanBtn;

    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_RECYCLERVIEW:
                    updateRecyclerView();
                    break;
                case MSG_UPDATE_MEMORY:
                    updateMemoryInfo();
                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        List<TaskItem> taskList = getTaskItem();
        mAdapter = new TasksAdapter(this,taskList,mState);
        mAdapter.setChoiceClickListener(this);
        mAdapter.setOnLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mMemory = findViewById(R.id.task_memory);
        mChooseBtn = findViewById(R.id.task_choice);
        mChooseBtn.setOnClickListener(this);
        mCleanBtn = findViewById(R.id.task_clean);
        mCleanBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(MSG_UPDATE_RECYCLERVIEW);
        mHandler.sendEmptyMessage(MSG_UPDATE_MEMORY);
    }

    private void updateMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        Log.v("xml_log_men","memoryInfo.availMem="+memoryInfo.availMem);
        Log.v("xml_log_men","memoryInfo.totalMem="+memoryInfo.totalMem);
        float usageRate = (float)(memoryInfo.totalMem - memoryInfo.availMem) / memoryInfo.totalMem;
        String usagePercent = (int)(usageRate * 100) + "%";
        mMemory.setText(getString(R.string.task_memory_usage, usagePercent));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void updateRecyclerView() {
        List<TaskItem> taskList = getTaskItem();
        mAdapter.setData(taskList,mState);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void removeTask(int taskId) {
        try {
            Class activityManager = Class.forName("android.app.IActivityManager");
            Class activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Method getDefault = activityManagerNative.getDeclaredMethod("getDefault");
            Object objIActMag = getDefault.invoke(activityManagerNative);

            Class[] clzParams = {int.class};
            Method removeTask = activityManager.getDeclaredMethod("removeTask", clzParams);
            removeTask.invoke(objIActMag, taskId);
        } catch (Exception e) {

        }
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_MEMORY,500);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void removeAllTask() {
        Log.e("xml_log_t","removeAllTask");
        List<TaskItem> list = getTaskItem();
        for(TaskItem item: list) {
            removeTask(item.taskId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private List<TaskItem> getTaskItem() {
        List<TaskItem> list = new ArrayList<>();
        List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(Integer.MAX_VALUE, 0x0002);
        for(ActivityManager.RecentTaskInfo info : recentTasks) {
            Log.v("xml_log_t","info.toString()="+info.toString());
            TaskItem item = new TaskItem();
            ComponentName cn = info.topActivity != null ? info.topActivity : (ComponentName) getSubField(info,"realActivity");
            String packageName = cn.getPackageName();
            try {
                item.title = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName,PackageManager.GET_META_DATA));
                item.icon = getPackageManager().getActivityIcon(cn);
                item.componentName = cn;
                item.taskId = info.taskId;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            list.add(item);
        }
        return list;
    }

    public static Object getSubField(Object obj, String fieldName) {
        if(obj == null) throw new NullPointerException("obj == null");
        Object subObj = null;
        try {
            if (obj != null) {
                Field field = obj.getClass().getField(fieldName);	// 获取成员变量对应的Field方法
                field.setAccessible(true);	// 设置为可访问
                subObj = field.get(obj);	// 通过Field方法从Object中提取子变量
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return subObj;
    }

    private void updateChooseState() {
        if(mState == TasksAdapter.STATE_CHOICE) {
            mChooseBtn.setSelected(true);
        } else {
            mChooseBtn.setSelected(false);
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_RECYCLERVIEW);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onChoiceClick(int position) {
        List<TaskItem> list = getTaskItem();
        removeTask(list.get(position).taskId);
        mHandler.sendEmptyMessage(MSG_UPDATE_RECYCLERVIEW);
    }

    @Override
    public void onLongClick(int position) {
        if(mState != TasksAdapter.STATE_CHOICE) {
            mState = TasksAdapter.STATE_CHOICE;
            updateChooseState();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(View view) {
        if(view == mChooseBtn) {
            if(mState == TasksAdapter.STATE_CHOICE) {
                mState = TasksAdapter.STATE_NORMAL;
            } else {
                mState = TasksAdapter.STATE_CHOICE;
            }
            updateChooseState();
        } else if(view == mCleanBtn){
            removeAllTask();
        }
    }
}
