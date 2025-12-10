package com.jzzh.setting.display.logo;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int horizontalSpacing;
    private int verticalSpacing;
    public GridSpacingItemDecoration(int spanCount, int horizontalSpacing, int verticalSpacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        float density = view.getContext().getResources().getDisplayMetrics().density;
        int horizontalSpacingPx = (int) (horizontalSpacing * density);
        int verticalSpacingPx = (int) (verticalSpacing * density);

        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        outRect.left = column * horizontalSpacingPx / spanCount;
        outRect.right = horizontalSpacingPx - (column + 1) * horizontalSpacingPx / spanCount;
        if (position >= spanCount) {
            outRect.top = verticalSpacingPx; // item top
        }
    }
}
