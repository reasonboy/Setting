package com.jzzh.setting.task;

import android.app.ActivityManager;
import android.content.Context;

public class TaskUtils {

    public static void moveTaskToFront(Context context, int taskId) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.moveTaskToFront(taskId, 0);
    }
}
