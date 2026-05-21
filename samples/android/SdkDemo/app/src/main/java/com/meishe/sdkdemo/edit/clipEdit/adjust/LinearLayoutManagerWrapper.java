package com.meishe.sdkdemo.edit.clipEdit.adjust;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.base.utils.LogUtils;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: ChuChenGuang
 * @CreateDate: 2021/8/24 11:11
 * @Description: 为了解决系统底层，数组越界问题
 * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position 13(offset:13).
 * state:14 androidx.recyclerview.widget.RecyclerView{2f96ff2c IFED.... ........ 70,25-480,80 #7f09024d app:id/rv_bar_list0}
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class LinearLayoutManagerWrapper extends LinearLayoutManager {

    public LinearLayoutManagerWrapper(Context context) {
        super(context);
    }

    public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            LogUtils.e(e);
        }

    }
}
