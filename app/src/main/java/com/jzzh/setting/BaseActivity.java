package com.jzzh.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jzzh.setting.network.bt.BluetoothActivity;
import com.jzzh.setting.network.wifi.WifiActivity;
import com.jzzh.setting.task.TaskManagerActivity;
import com.jzzh.setting.utils.UtilDips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements NavigationLayout.OnNavigationClickListener {

    private NavigationLayout mNavigationLayout;
    private int mIndex;
    private StartActivityReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new StartActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("zhihe.action.START_ACTIVITY_SETTING");
        intentFilter.addAction("zhihe.action.START_ACTIVITY_WIFI");
        intentFilter.addAction("zhihe.action.START_ACTIVITY_BT");
        intentFilter.addAction("zhihe.action.START_ACTIVITY_TASK");
        registerReceiver(mReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(R.layout.activity_base);
        FrameLayout frameContentView = (FrameLayout) findViewById(R.id.content_base);
        View.inflate(this, layoutResID, frameContentView);
        mNavigationLayout = findViewById(R.id.navigation_base);
        mNavigationLayout.setOnNavigationClickListener(this);
        mIndex = sequentialSearch(Const.ACTIVITIES,getClass());
        int[] navigationIds = Const.NAVIGATIONS[mIndex];
        setNavigation(navigationIds);
    }

    public void setNavigation(int[] navigationIds) {
        mNavigationLayout.setNavigation(navigationIds);
    }

    @Override
    public void onNavigationClick(int srcId) {
        int index = sequentialSearch(Const.NAVIGATIONS,srcId);
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
            int[] navi = arr[i];
            if(navi[navi.length-1] == value) {
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
        int[] navigationIds = Const.NAVIGATIONS[mIndex];
        int length = navigationIds.length;
        if(length == 1) {//当前为Setting
            return -1;
        } else {
            int[] newArray = new int[length - 1];
            System.arraycopy(navigationIds, 0, newArray, 0, length - 1);
            return sequentialSearch(Const.NAVIGATIONS,newArray);
        }
    }

    private class StartActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("xml_log_sr","action = " + action);
            if("zhihe.action.START_ACTIVITY_SETTING".equals(action)) {
                startActivity(Setting.class);
            } else if("zhihe.action.START_ACTIVITY_WIFI".equals(action)) {
                startActivity(WifiActivity.class);
            } else if("zhihe.action.START_ACTIVITY_BT".equals(action)) {
                startActivity(BluetoothActivity.class);
            } else if("zhihe.action.START_ACTIVITY_TASK".equals(action)) {
                startActivity(TaskManagerActivity.class);
            }
        }
    }

    /**
     *   Common Info Dialog
     */
    protected Dialog infoDialog;
    protected int dialogCurrentIdx = 0;
    protected void makeInfoDialog(int titleResId, int subTitleResId, int[] imageResIds, int[] descResIds) {
        makeInfoDialog(titleResId, subTitleResId, imageResIds, true, descResIds);
    }

    protected void makeInfoDialog(int titleResId, int subTitleResId, int[] imageResIds, boolean useImgBorder) {
        makeInfoDialog(titleResId, subTitleResId, imageResIds, useImgBorder, new int[] {-1});
    }

    protected void makeInfoDialog(int titleResId, int subTitleResId, int[] imageResIds, boolean useImgBorder, int[] descResIds) {
        if (infoDialog != null) infoDialog.dismiss();

        infoDialog = new Dialog(this);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.setContentView(R.layout.dialog_display_info_widget_setting);
        infoDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(infoDialog.getWindow().getAttributes());
        lp.width = UtilDips.dpToPx(492);
        infoDialog.getWindow().setAttributes(lp);

        TextView tvTitle = infoDialog.findViewById(R.id.tv_title);
        ImageView exit = infoDialog.findViewById(R.id.iv_exit);
        TextView tvSubTitle = infoDialog.findViewById(R.id.tv_sub_title);
        ImageView centerImage = infoDialog.findViewById(R.id.iv_center_image);
        TextView desc = infoDialog.findViewById(R.id.tv_desc);
        ImageView prev = infoDialog.findViewById(R.id.iv_prev);
        ImageView next = infoDialog.findViewById(R.id.iv_next);

        dialogCurrentIdx = 0;

        exit.setOnClickListener(v -> infoDialog.dismiss());

        if (titleResId != -1) tvTitle.setText(titleResId);
        if (subTitleResId != -1) tvSubTitle.setText(subTitleResId);
        else tvSubTitle.setVisibility(View.GONE);

        if (imageResIds[dialogCurrentIdx] != -1) centerImage.setImageResource(imageResIds[dialogCurrentIdx]);
        else centerImage.setVisibility(View.GONE);

        if (descResIds[dialogCurrentIdx] != -1) desc.setText(descResIds[dialogCurrentIdx]);
        else desc.setVisibility(View.GONE);

        if (!useImgBorder) centerImage.setBackground(null);

        boolean isMorePage = imageResIds.length > dialogCurrentIdx + 1 || descResIds.length > dialogCurrentIdx + 1;
        next.setVisibility(isMorePage ? View.VISIBLE : View.INVISIBLE);
        next.setOnClickListener(v -> {
            dialogCurrentIdx++;
            centerImage.setImageResource(imageResIds.length > dialogCurrentIdx ? imageResIds[dialogCurrentIdx] : imageResIds[imageResIds.length - 1]);
            desc.setText(descResIds.length > dialogCurrentIdx ? descResIds[dialogCurrentIdx] : descResIds[descResIds.length - 1]);

            next.setVisibility(imageResIds.length > dialogCurrentIdx + 1 || descResIds.length > dialogCurrentIdx + 1 ? View.VISIBLE : View.INVISIBLE);
            prev.setVisibility(dialogCurrentIdx != 0 ? View.VISIBLE : View.INVISIBLE);
        });

        prev.setVisibility(dialogCurrentIdx != 0 ? View.VISIBLE : View.INVISIBLE);
        prev.setOnClickListener(v -> {
            if (dialogCurrentIdx > 0) {
                dialogCurrentIdx--;
                centerImage.setImageResource(imageResIds[dialogCurrentIdx]);
                desc.setText(descResIds[dialogCurrentIdx]);
            }

            next.setVisibility(imageResIds.length > dialogCurrentIdx + 1 || descResIds.length > dialogCurrentIdx + 1 ? View.VISIBLE : View.INVISIBLE);
            prev.setVisibility(dialogCurrentIdx != 0 ? View.VISIBLE : View.INVISIBLE);
        });
    }
}
