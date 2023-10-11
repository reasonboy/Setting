package com.jzzh.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseActivity extends Activity implements TitleLayout.OnTitleClickListener {

    private TitleLayout mTitleLayout;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(R.layout.activity_base);
        FrameLayout frameContentView = (FrameLayout) findViewById(R.id.content_base);
        View.inflate(this, layoutResID, frameContentView);
        mTitleLayout = findViewById(R.id.title_base);
        mTitleLayout.setOnTitleClickListener(this);
        mIndex = sequentialSearch(Const.ACTIVITIES,getClass());
        int[] titleIds = Const.TITLES[mIndex];
        setTitle(titleIds);
    }

    public void setTitle(int[] titleIds) {
        mTitleLayout.setTitle(titleIds);
    }

    @Override
    public void onTitleClick(int srcId) {
        int index = sequentialSearch(Const.TITLES,srcId);
        startActivity(Const.ACTIVITIES[index]);
    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }

    public static int sequentialSearch(Class<?>[] arr,Class value){
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] == value){
                return i;
            }
        }
        return -1;
    }

    public static int sequentialSearch(int[][] arr,int[] value){
        for (int i = 0; i < arr.length; i++) {
            if(Arrays.equals(arr[i],value)){
                return i;
            }
        }
        return -1;
    }

    public static int sequentialSearch(int[][] arr,int value){
        for (int i = 0; i < arr.length; i++) {
            int[] title = arr[i];
            if(title[title.length-1] == value) {
                return i;
            }
        }
        return -1;
    }

    public static class ActivityCollector {
        public static List<Activity> activities = new ArrayList<>();

        public static void addActivity(Activity activity) {
            activities.add(activity);
        }

        public static void removeActivity(Activity activity) {
            activities.remove(activity);
        }

        public static void finishAll() {
            for (Activity activity : activities) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
            activities.clear();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        int superIndex = getSuperIndex();
        if(superIndex == -1) {
            ActivityCollector.finishAll();
        } else {
            startActivity(Const.ACTIVITIES[superIndex]);
        }
    }

    private int getSuperIndex() {
        int[] titleIds = Const.TITLES[mIndex];
        int length = titleIds.length;
        if(length == 1) {//当前为Setting
            return -1;
        } else {
            int[] newArray = new int[length - 1];
            System.arraycopy(titleIds, 0, newArray, 0, length - 1);
            return sequentialSearch(Const.TITLES,newArray);
        }
    }
}
