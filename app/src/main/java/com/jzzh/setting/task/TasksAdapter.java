package com.jzzh.setting.task;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jzzh.setting.R;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskHolder> {

    private Context mContext;
    private List<TaskItem> mList;
    private int mState;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_CHOICE = 1;
    private OnChoiceClickListener mChoiceClickListener;
    private OnOnLongClickListener mOnLongClickListener;

    public TasksAdapter(Context context, List<TaskItem> list, int state) {
        this.mContext = context;
        this.mList = list;
        this.mState = state;
    }

    public void setData(List<TaskItem> list, int state) {
        mList = list;
        mState = state;
    }

    public void setChoiceClickListener(OnChoiceClickListener choiceClickListener) {
        mChoiceClickListener = choiceClickListener;
    }

    public void setOnLongClickListener(OnOnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.task_item, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, final int position) {

        if(mState == STATE_NORMAL) {
            holder.choice.setVisibility(View.GONE);
        } else {
            holder.choice.setVisibility(View.VISIBLE);
        }
        holder.title.setText(mList.get(position).title);
        holder.icon.setBackground(mList.get(position).icon);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mState == STATE_NORMAL) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory("android.intent.category.LAUNCHER");
                    intent.setComponent(mList.get(position).componentName);
                    mContext.startActivity(intent);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnLongClickListener != null) {
                    mOnLongClickListener.onLongClick(position);
                }
                return false;
            }
        });
        holder.choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mChoiceClickListener != null) {
                    mChoiceClickListener.onChoiceClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;
        View view;
        ImageView choice;

        public TaskHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.task_item_icon);
            title = itemView.findViewById(R.id.task_item_title);
            view = itemView.findViewById(R.id.task_item_view);
            choice = itemView.findViewById(R.id.task_item_choice);
        }
    }

    public interface OnChoiceClickListener {
        void onChoiceClick(int position);
    }

    public interface OnOnLongClickListener {
        void onLongClick(int position);
    }
}
