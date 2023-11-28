package com.jzzh.setting.display.logo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jzzh.setting.R;
import com.jzzh.setting.SettingApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserImageAdapter extends RecyclerView.Adapter<UserImageAdapter.UserImageHolder> {

    private Context mContext;
    private List<File> mList = new ArrayList<>();
    private OnItemClickListener mClickListener;
    private RequestOptions mRequestOptions;

    public UserImageAdapter(Context context) {
        this.mContext=context;
    }

    public void setData(List list) {
        mList = list;
        mRequestOptions = new RequestOptions().override(SettingApplication.screenWidth,SettingApplication.screenHeight);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }


    @Override
    public UserImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.display_user_image_list_item, parent, false);
        return new UserImageHolder(mContext,view);
    }

    @Override
    public void onBindViewHolder(UserImageHolder holder, final int position) {
        //holder.preview.setImageBitmap(mList.get(position));

        Glide.with(mContext).load(mList.get(position).getPath()).apply(mRequestOptions).into(holder.preview);

        if(mClickListener != null) {
            holder.preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(mList.get(position),position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class UserImageHolder extends RecyclerView.ViewHolder {

        ImageView preview;

        public UserImageHolder(Context context, View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.user_image_preview);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(File selectFile, int position);
    }
}
