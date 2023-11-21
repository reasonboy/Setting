package com.jzzh.setting.display;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jzzh.setting.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


@SuppressLint("ValidFragment")
public class UserImageSettingFragment extends Fragment implements View.OnClickListener {

    private File mFile;
    private String[] mSaveLogoPath;
    private UserImageSettingItem mFitCenter, mCenterCrop, mRotate, mRotateReverse;
    private OnSettingCompletedListener mOnSettingCompletedListener;

    @SuppressLint("ValidFragment")
    public UserImageSettingFragment(File selectFile, String[] saveLogoPath) {
        mFile = selectFile;
        mSaveLogoPath = saveLogoPath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("xml_log_fra","UserImageSettingFragment");
        View view = inflater.inflate(R.layout.display_user_image_setting_fragment,null);
        mFitCenter = view.findViewById(R.id.user_image_fit_center);
        mCenterCrop = view.findViewById(R.id.user_image_center_crop);
        mRotate = view.findViewById(R.id.user_image_rotate);
        mRotateReverse = view.findViewById(R.id.user_image_rotate_reverse);
        mFitCenter.setOnClickListener(this);
        mCenterCrop.setOnClickListener(this);
        mRotate.setOnClickListener(this);
        mRotateReverse.setOnClickListener(this);
        updatePreview();
        return view;
    }

    public void setOnClickListener(OnSettingCompletedListener onSettingCompletedListener) {
        mOnSettingCompletedListener = onSettingCompletedListener;
    }

    private void updatePreview() {
        mFitCenter.setPreview(mFile,UserImageSettingItem.MODE_FIT_CENTER);
        mCenterCrop.setPreview(mFile,UserImageSettingItem.MODE_CENTER_CROP);
        mRotate.setPreview(mFile,UserImageSettingItem.MODE_ROTATE);
        mRotateReverse.setPreview(mFile,UserImageSettingItem.MODE_ROTATE_REVERSE);
    }

    @Override
    public void onClick(View view) {
        Bitmap srcBitmap = ((UserImageSettingItem)view).getSrcBitmap();
        Bitmap logo = BitmapManager.adjustBitmap(srcBitmap);
        Bitmap rotateLogo = BitmapManager.rotateBitmap(logo,270);
        for(String logoPath : mSaveLogoPath) {
            BitmapManager.saveBitmap(rotateLogo,logoPath);
        }
        executeCommand("id > /storage/emulated/0/Music/log.txt");
        for(String logoPath : mSaveLogoPath) {
            File file = new File(logoPath);
            String command = "su mv " + logoPath + " /storage/emulated/0/Music/" + file.getName();
            Log.v("xml_log_shell",command);
            executeCommand(command);
        }
        if(mOnSettingCompletedListener != null) {
            mOnSettingCompletedListener.settingCompleted();
        }
    }

    public static void executeCommand(String command) {
        Runtime mRuntime = Runtime.getRuntime();
        try {
            //Process中封装了返回的结果和执行错误的结果
            Process mProcess = mRuntime.exec(command);
            Log.v("xml_log_shell",mProcess.toString());
            BufferedReader mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            StringBuffer mRespBuff = new StringBuffer();
            char[] buff = new char[1024];
            int ch = 0;
            while ((ch = mReader.read(buff)) != -1) {
                mRespBuff.append(buff, 0, ch);
            }
            mReader.close();
            System.out.print(mRespBuff.toString());
            mProcess.waitFor();
            mProcess.destroy();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            Log.v("xml_log_shell",e.toString());
            e.printStackTrace();
        }
    }

    public interface OnSettingCompletedListener {
        void settingCompleted();
    }
}
