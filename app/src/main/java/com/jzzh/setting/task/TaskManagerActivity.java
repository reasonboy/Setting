package com.jzzh.setting.task;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.hardware.HardwareBuffer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.NavigationDotView;
import com.jzzh.setting.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskManagerActivity extends BaseActivity implements TasksAdapter.OnChoiceClickListener,
        TasksAdapter.OnOnLongClickListener,View.OnClickListener {

    private static final int MSG_UPDATE_RECYCLERVIEW = 0;
    private static final int MSG_UPDATE_MEMORY = 1;
    private static final int SPAN_COUNT = 3;
    private static final int ROWS_PER_PAGE = 2;
    private static final int ITEMS_PER_PAGE = SPAN_COUNT * ROWS_PER_PAGE;
    
    private ActivityManager mActivityManager;
    private RecyclerView mRecyclerView;
    private TasksAdapter mAdapter;
    private int mState = TasksAdapter.STATE_NORMAL;
    private NavigationDotView mNavigationDotView;
    private int mCurrentPage = 0;
    private List<TaskItem> mAllTaskList = new ArrayList<>();

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
        
        mAllTaskList = getTaskItem();
        List<TaskItem> currentPageList = getCurrentPageTaskList();
        mAdapter = new TasksAdapter(this, currentPageList, mState);
        mAdapter.setChoiceClickListener(this);
        mAdapter.setOnLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mNavigationDotView = findViewById(R.id.navigation_dot_view);
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
        mAllTaskList = getTaskItem();
        
        int totalPages = getTotalPages();
        if (mCurrentPage >= totalPages && totalPages > 0) {
            mCurrentPage = totalPages - 1;
        } else if (totalPages == 0) {
            mCurrentPage = 0;
        }
        
        List<TaskItem> currentPageList = getCurrentPageTaskList();
        mAdapter.setData(currentPageList, mState);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        
        setupNavigationDots();
    }
    
    private int getTotalPages() {
        if (mAllTaskList.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil((double) mAllTaskList.size() / ITEMS_PER_PAGE);
    }
    
    private List<TaskItem> getCurrentPageTaskList() {
        List<TaskItem> currentPageList = new ArrayList<>();
        int startIndex = mCurrentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, mAllTaskList.size());
        
        if (startIndex < mAllTaskList.size()) {
            for (int i = startIndex; i < endIndex; i++) {
                currentPageList.add(mAllTaskList.get(i));
            }
        }
        return currentPageList;
    }
    
    private void setupNavigationDots() {
        int totalPages = getTotalPages();
        mNavigationDotView.setDotCount(totalPages);
        mNavigationDotView.enableDot(mCurrentPage);
        mNavigationDotView.setOnNavigationDotClickListener(new NavigationDotView.OnNavigationDotClickListener() {
            @Override
            public void onNavigationDotClick(int pageIndex) {
                mCurrentPage = pageIndex;
                updateCurrentPageData();
            }
        });
    }
    
    private void updateCurrentPageData() {
        List<TaskItem> currentPageList = getCurrentPageTaskList();
        mAdapter.setData(currentPageList, mState);
        mAdapter.notifyDataSetChanged();
        mNavigationDotView.enableDot(mCurrentPage);
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

    private static final String SPACE_LAUNCHER = "com.inno.spacelauncher";
    private List<String> ignoreList = new ArrayList<>(Arrays.asList(SPACE_LAUNCHER));
    private boolean checkIfIgnore(String pkgName) {
        return ignoreList.contains(pkgName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private List<TaskItem> getTaskItem() {
        List<TaskItem> list = new ArrayList<>();
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager != null ? mActivityManager.getRunningTasks(100) : new ArrayList<>();
        PackageManager pm = getPackageManager();

        for(ActivityManager.RunningTaskInfo info : tasks) {
            Log.v("xml_log_t", "info.toString()=" + info.toString());
            ComponentName cn = info.topActivity != null ? info.topActivity : (ComponentName) getSubField(info, "realActivity");
            String packageName = cn.getPackageName();
            if (pm.getLaunchIntentForPackage(packageName) != null && !(checkIfIgnore(packageName))) {
                TaskItem item = new TaskItem();
                try {
                    item.title = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                    item.icon = pm.getActivityIcon(cn);
                    item.componentName = cn;
                    item.taskId = info.taskId;
                    item.thumbnail = getTaskSnapshotModern(info.taskId);
//                    Log.d("lx_log","componentName:"+item.componentName+",mTaskId:"+item.taskId+",thumbnail:"+item.thumbnail);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                list.add(item);
            }
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
        // 计算在全部任务列表中的实际位置
        int realPosition = mCurrentPage * ITEMS_PER_PAGE + position;
        if (realPosition < mAllTaskList.size()) {
            removeTask(mAllTaskList.get(realPosition).taskId);
            mHandler.sendEmptyMessage(MSG_UPDATE_RECYCLERVIEW);
        }
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

    public Bitmap getTaskSnapshotModern(int taskId) {
        try {
            // Android 9.0+ 版本
            Object atms = getActivityTaskManagerService();
            if (atms == null) {
                return null;
            }

            // 方法可能在 ATMS 中
            Method getTaskSnapshot = null;
            for (Method method : atms.getClass().getDeclaredMethods()) {
                if ("getTaskSnapshot".equals(method.getName())) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length >= 2 && params[0] == int.class) {
                        getTaskSnapshot = method;
                        break;
                    }
                }
            }

            if (getTaskSnapshot != null) {
                getTaskSnapshot.setAccessible(true);
                // 可能的方法签名：getTaskSnapshot(int taskId, boolean reducedResolution, boolean isLowResolution)
                Object taskSnapshot = null;

                try {
                    // 尝试三参数版本
                    taskSnapshot = getTaskSnapshot.invoke(atms, taskId, false, false);
                } catch (Exception e) {
                    try {
                        // 尝试两参数版本
                        taskSnapshot = getTaskSnapshot.invoke(atms, taskId, false);
                    } catch (Exception e2) {
                        // 尝试其他参数组合
                    }
                }

                if (taskSnapshot != null) {
                    Log.d("lx_log", "taskSnapshot != null");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        return taskSnapshotToBitmapUsingGraphicBuffer(taskSnapshot);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TaskSnapshot", "Modern method failed", e);
        }
        return null;
    }

    public Object getActivityTaskManagerService() {
        try {
            // Android 10+ 才有 ATMS
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return null;
            }

            // 方法1：通过 ServiceManager
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            IBinder binder = (IBinder) getService.invoke(null, "activity_task");

            if (binder == null) {
                return null;
            }

            // 获取 IActivityTaskManager
            Class<?> iActivityTaskManagerStub = Class.forName(
                    "android.app.IActivityTaskManager$Stub"
            );
            @SuppressLint("BlockedPrivateApi") Method asInterface = iActivityTaskManagerStub.getDeclaredMethod(
                    "asInterface",
                    IBinder.class
            );
            return asInterface.invoke(null, binder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi", "NewApi"})
    public Bitmap taskSnapshotToBitmapUsingGraphicBuffer(Object taskSnapshot) {
        if (taskSnapshot == null) {
            return null;
        }

        // 查找 TaskSnapshot 类
        Class<?> taskSnapshotClass = null;
        try {
            taskSnapshotClass = Class.forName("android.window.TaskSnapshot");  // 兼容Android 14
        } catch (ClassNotFoundException e) {
            try {
                taskSnapshotClass = Class.forName("android.app.ActivityManager$TaskSnapshot");
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }

        try {
            // 方法1：通过反射获取 GraphicBuffer
            Method getGraphicBufferMethod = taskSnapshotClass.getDeclaredMethod("getSnapshot");
            getGraphicBufferMethod.setAccessible(true);

            // GraphicBuffer 是隐藏类，需要反射调用
            Object graphicBuffer = getGraphicBufferMethod.invoke(taskSnapshot);

            // 方法1：通过反射获取 GraphicBuffer
            Method getColorSpaceMethod = taskSnapshotClass.getDeclaredMethod("getColorSpace");
            getColorSpaceMethod.setAccessible(true);

            // GraphicBuffer 是隐藏类，需要反射调用
            Object colorSpace = getColorSpaceMethod.invoke(taskSnapshot);

            if (graphicBuffer == null) {
                Log.w("TaskSnapshot", "GraphicBuffer is null, trying HardwareBuffer...");
                return null;
            }

            HardwareBuffer hb = createFromGraphicBuffer(graphicBuffer);
            return Bitmap.wrapHardwareBuffer(hb, (ColorSpace) colorSpace);

        } catch (Exception e) {
            Log.e("TaskSnapshot", "Failed to get GraphicBuffer via reflection", e);
            return null;
        }
    }

    /**
     * 通过反射调用 HardwareBuffer.createFromGraphicBuffer()
     * 将 GraphicBuffer 转换为 HardwareBuffer
     */
    public static HardwareBuffer createFromGraphicBuffer(Object graphicBuffer) throws ClassNotFoundException {
        if (graphicBuffer == null) {
            return null;
        }
        // 获取 GraphicBuffer 的 Class
        Class<?> graphicBufferClass = Class.forName("android.graphics.GraphicBuffer");

        try {
            // 方法1：直接调用（如果编译时SDK版本支持）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 反射获取方法
                Class<?> hardwareBufferClass = HardwareBuffer.class;
                Method createFromGraphicBufferMethod = hardwareBufferClass
                        .getDeclaredMethod("createFromGraphicBuffer", graphicBufferClass);

                // 调用静态方法
                return (HardwareBuffer) createFromGraphicBufferMethod.invoke(null, graphicBuffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
