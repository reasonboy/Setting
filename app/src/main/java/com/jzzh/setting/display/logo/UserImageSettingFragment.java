package com.jzzh.setting.display.logo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jzzh.setting.R;

import java.io.File;


@SuppressLint("ValidFragment")
public class UserImageSettingFragment extends Fragment implements View.OnClickListener {

    private File mFile;
    private String[] mSaveLogoPath;
    private String mTagPath;
    private UserImageSettingItem mFitCenter, mCenterCrop, mRotate, mRotateReverse;
    private OnSettingCompletedListener mOnSettingCompletedListener;

    @SuppressLint("ValidFragment")
    public UserImageSettingFragment(File selectFile, String[] saveLogoPath, String tagPath) {
        mFile = selectFile;
        mSaveLogoPath = saveLogoPath;
        mTagPath = tagPath;
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
        BitmapManager.saveBitmap(logo,mTagPath);
        for(String logoPath : mSaveLogoPath) {
            BitmapManager.saveBitmap(logo,logoPath);
        }
        Intent intent = new Intent("zhihe.action.UPDATE_STANDBY");
        getActivity().sendBroadcast(intent);
        Toast.makeText(getActivity(),R.string.set_image_successfully,Toast.LENGTH_LONG).show();
        if(mOnSettingCompletedListener != null) {
            mOnSettingCompletedListener.settingCompleted();
        }
    }

    public interface OnSettingCompletedListener {
        void settingCompleted();
    }
}
