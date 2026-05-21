package com.meishe.sdkdemo.quicksplicing

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/7 19:29
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
class RecyclerViewTouchCallBack(private val mQuickSplicingRecyclerViewAdapter: QuickSplicingRecyclerViewAdapter) :
    ItemTouchHelper.Callback() {

   private var mIsDeleteHappen = false
    /**
     * 拖动逻辑的判断
     * Drag logical judgment
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.DOWN
        val dragFlags =
            ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.DOWN or ItemTouchHelper.UP
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        mQuickSplicingRecyclerViewAdapter.onItemMove(viewHolder, viewHolder1)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * 移动删除的代码逻辑
     * Move the deleted code logic
     * @param viewHolder
     * @param i
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mQuickSplicingRecyclerViewAdapter.onSwiped(viewHolder.adapterPosition);
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            val alpha: Float = 1.0f - Math.abs(dY) / (viewHolder.itemView.height + 50)
                .toFloat()
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationY = dY
            mQuickSplicingRecyclerViewAdapter.showBottomDeleteView(isCurrentlyActive)
            mIsDeleteHappen = false
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (!isItemViewSwipeEnabled() || mQuickSplicingRecyclerViewAdapter.isDeleteFinish) {
                return
            }
            if (dY > 250) {
                mIsDeleteHappen = true
            }
            viewHolder.itemView.translationX = dX
            viewHolder.itemView.translationY = dY
            mQuickSplicingRecyclerViewAdapter.showBottomDeleteView(isCurrentlyActive)
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        //mQuickSplicingRecyclerViewAdapter.onItemClear(viewHolder)
        if (mIsDeleteHappen) {
            mQuickSplicingRecyclerViewAdapter.onDragDelete(viewHolder)
            mIsDeleteHappen = false
        } else {
            mQuickSplicingRecyclerViewAdapter.hideBottomDeleteView()
        }
    }

    /**
     * 是否启用拖拽删除 false 不启用，true启用
     * Whether to enable drag and drop deletion. false Indicates whether to enable drag and drop deletion
     * @return
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        if (mQuickSplicingRecyclerViewAdapter.getData()?.size!! <= 1) {
            return false
        }
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        //长按启用拖拽
        // Long press to enable drag and drop
        return true
    }


}