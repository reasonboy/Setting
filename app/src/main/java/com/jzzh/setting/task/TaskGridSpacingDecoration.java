package com.jzzh.setting.task;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskGridSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int mSpanCount;
    private final int mEdgeSpacing;
    private final int mHorizontalSpacing;
    private final int mVerticalSpacing;
    private final int mTopSpacing;

    public TaskGridSpacingDecoration(int spanCount, int edgeSpacing,
            int horizontalSpacing, int verticalSpacing, int topSpacing) {
        mSpanCount = spanCount;
        mEdgeSpacing = edgeSpacing;
        mHorizontalSpacing = horizontalSpacing;
        mVerticalSpacing = verticalSpacing;
        mTopSpacing = topSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
            @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if(position == RecyclerView.NO_POSITION) {
            return;
        }

        int column = position % mSpanCount;
        int row = position / mSpanCount;
        int firstHalfSpacing = mHorizontalSpacing / 2;
        int secondHalfSpacing = mHorizontalSpacing - firstHalfSpacing;

        if(column == 0) {
            outRect.left = mEdgeSpacing;
            outRect.right = firstHalfSpacing;
        } else {
            outRect.left = secondHalfSpacing;
            outRect.right = mEdgeSpacing;
        }

        if(row == 0) {
            outRect.top = mTopSpacing;
        } else {
            outRect.top = mVerticalSpacing;
        }
    }
}
