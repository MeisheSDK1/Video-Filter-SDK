package com.meishe.sdkdemo.themeshoot.adapter;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/4.
 * @Description :主题拍摄Decoration - theme preview ItemDecoration
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemePreviewItemDecoration extends RecyclerView.ItemDecoration {

    private int paddingDecoration;

    public ThemePreviewItemDecoration(int paddingDecoration) {
        this.paddingDecoration = paddingDecoration;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos != 0) {
            outRect.left = paddingDecoration;
            outRect.right = paddingDecoration;
        } else if (pos == parent.getChildCount() - 1) {
            outRect.left = paddingDecoration;
            outRect.right = paddingDecoration * 3;
        } else {
            outRect.left = paddingDecoration * 3;
            outRect.right = paddingDecoration;
        }

    }
}
