package com.meishe.sdkdemo.quicksplicing

import androidx.recyclerview.widget.RecyclerView

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/7 19:47
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
interface IItemTouchHelperAdapterCallback {
    /**
     * 数据交换
     * change data
     * @param source
     * @param target
     */
    fun onItemMove(source: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?)

    /**
     * 删除item
     * delete item
     * @param source
     */
    fun onSwiped(position:Int)

    /**
     * drag或者swipe选中
     * @param source
     */
    fun onItemSelect(source: RecyclerView.ViewHolder?)

    /**
     * 状态清除
     * clear state
     * @param source
     */
    fun onItemClear(source: RecyclerView.ViewHolder?)
}