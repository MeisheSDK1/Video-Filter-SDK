package com.meishe.sdkdemo.themeshoot.adapter;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/5.
 * @Description :字幕Decoration - caption ItemDecoration
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionItemDecoration extends RecyclerView.ItemDecoration {

    private int paddingDecoration;

    public CaptionItemDecoration(int paddingDecoration) {
        this.paddingDecoration = paddingDecoration;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        outRect.top = paddingDecoration;
        outRect.bottom = paddingDecoration;

    }
}
