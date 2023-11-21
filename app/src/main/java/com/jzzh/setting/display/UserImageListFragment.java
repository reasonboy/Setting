package com.jzzh.setting.display;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jzzh.setting.R;
import com.jzzh.tools.PageIndication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class UserImageListFragment extends Fragment {

    private String mImagePath;
    private int mNoImageSrcId;

    public static final String DOWNLOAD_PATH = "/storage/emulated/0/Download/INNOS";
    private UserImageObserver mCustomImageObserver, mDownloadImageObserver;

    private TextView mNoImage;
    private RecyclerView mRecyclerView;
    private UserImageAdapter mAdapter;
    private List<File> mList = new ArrayList<>();
    private PageIndication mPageIndication;
    private int mCurPage = 1,mTotalPage,mRecyclerViewItemNum = 9;
    private OnItemClick mOnItemClick;

    @SuppressLint("ValidFragment")
    public UserImageListFragment(String imagePath, int noImageSrcId) {
        Log.v("xml_log_fra","UserImageListFragment imagePath = "+imagePath);
        mImagePath = imagePath;
        mNoImageSrcId = noImageSrcId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("xml_log_fra","UserImageListFragment");
        View view = inflater.inflate(R.layout.display_user_image_list_fragment,null);
        mNoImage = view.findViewById(R.id.user_no_image);
        mRecyclerView = view.findViewById(R.id.user_image_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new UserImageAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new UserImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File selectFile, int position) {
                if(mOnItemClick != null) {
                    mOnItemClick.onItemClick(selectFile,position);
                }
            }
        });
        mPageIndication = view.findViewById(R.id.page_indication);
        mPageIndication.setOnPageChangeListener(new PageIndication.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page) {
                mCurPage = page;
                updateList(mList);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 退出手写，返回笔记本页面时刷新笔记本列表，故此处刷新更合适
        startFileObserver();
        updateView();
    }

    private void updateView() {
        mNoImage.setText(Html.fromHtml(getString(mNoImageSrcId)));
        List<File> bitmaps = loadBitmaps();
        if(bitmaps.size() != 0) {
            mNoImage.setVisibility(View.GONE);
        } else {
            mNoImage.setVisibility(View.VISIBLE);
        }
        updateList(bitmaps);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    private void startFileObserver() {
        File sleepFile = new File(mImagePath);
        File downloadFile = new File(DOWNLOAD_PATH);

        if (sleepFile.exists()) {
            //不要写成匿名
            mCustomImageObserver = new UserImageObserver(mImagePath);
            mCustomImageObserver.startWatching();
        }
        if (downloadFile.exists()) {
            mDownloadImageObserver = new UserImageObserver(DOWNLOAD_PATH);
            mDownloadImageObserver.startWatching();
        }
    }

    private void stopFileObserver() {
        mCustomImageObserver.stopWatching();
        mDownloadImageObserver.stopWatching();
    }

    private List<File> loadBitmaps() {
        List<File> bitmaps = new ArrayList<File>();
        File localFile = new File(mImagePath);
        File[] localFiles = localFile.listFiles();
        for (int i = 0; i < localFiles.length; ++i) {
            if(!BitmapManager.isImage(localFiles[i])) {
                continue;
            }
            bitmaps.add(localFiles[i]);
        }
        File downloadFile = new File(DOWNLOAD_PATH);
        File[] downloadFiles = downloadFile.listFiles();
        for (int i = 0; i < downloadFiles.length; ++i) {
            if(!BitmapManager.isImage(downloadFiles[i])) {
                continue;
            }
            bitmaps.add(downloadFiles[i]);
        }
        return bitmaps;
    }

    public void updateList(List<File> list) {
        mList = list;
        mAdapter.setData(getSubScanResult(mList));
        mAdapter.notifyDataSetChanged();
        mPageIndication.init(mCurPage,mTotalPage);
    }

    private List getSubScanResult(List<File> parent) {
        if(parent.size() == 0) return parent;
        //Log.v("xml_log_app","parent.size() = " + parent.size());
        List<File> list = new ArrayList<File>();
        mTotalPage = parent.size() / mRecyclerViewItemNum;
        if(parent.size() % mRecyclerViewItemNum != 0) {
            mTotalPage++;
        }
        if(mCurPage > mTotalPage ) {
            mCurPage = mTotalPage ;
        }
        //Log.v("xml_log_app","mTotalPage = " + mTotalPage);
        for(int i = 0;i < parent.size();i++) {
            if(i >= (mCurPage - 1) * mRecyclerViewItemNum && i < mCurPage * mRecyclerViewItemNum) {
                list.add(parent.get(i));
            }
        }
        return list;
    }

    class UserImageObserver extends FileObserver {
        //mask:指定要监听的事件类型，默认为FileObserver.ALL_EVENTS
        public UserImageObserver(String path, int mask) {
            super(path, mask);
        }

        public UserImageObserver(String path) {
            super(path);
        }

        @Override
        public void onEvent(int event, String path) {
            final int action = event & FileObserver.ALL_EVENTS;

            switch (action) {
                case FileObserver.CREATE:
                case FileObserver.DELETE:
                case FileObserver.MODIFY:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateView();
                        }
                    });
                    break;
            }
        }
    }

    public interface OnItemClick {
        void onItemClick(File selectFile, int position);
    }
}
