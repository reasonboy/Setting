package com.jzzh.setting.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jzzh.setting.R;

import java.util.List;

public class ListRadioBtnMenuAdapter extends RecyclerView.Adapter<ListRadioBtnMenuAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<String> listMenus;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;
    protected OnSelected mOnSelected;

    public interface OnSelected {
        void onSelected(int position);
    }

    public ListRadioBtnMenuAdapter(Context context, List<String> menus, int selectedPosition, OnSelected onSelected) {
        mInflater = LayoutInflater.from(context);
        listMenus = menus;
        mSelectedPosition = selectedPosition;
        mOnSelected = onSelected;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_list_radio_btn_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(listMenus.get(position));
        holder.itemView.setOnClickListener(v -> {
            if(position != mSelectedPosition && mSelectedRB != null){
                mSelectedRB.setChecked(false);
            }

            mSelectedPosition = position;
            mSelectedRB = (RadioButton)v.findViewById(R.id.radio_button);

            if (mOnSelected != null) {
                mOnSelected.onSelected(position);
            }

            notifyDataSetChanged();
        });

        if(mSelectedPosition != position){
            holder.radioButton.setChecked(false);
        } else {
            holder.radioButton.setChecked(true);
            if(mSelectedRB != null && holder.radioButton != mSelectedRB){
                mSelectedRB = holder.radioButton;
            }
        }
    }

    @Override
    public int getItemCount() {
        return listMenus.size();
    }
    
    public void setMenusAndIdx(List<String> menus, int idx) {
        if (idx != mSelectedPosition || !menus.equals(listMenus)) {
            listMenus = menus;
            mSelectedPosition = idx;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RadioButton radioButton;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_menu_title);
            radioButton = itemView.findViewById(R.id.radio_button);
        }
    }
}
