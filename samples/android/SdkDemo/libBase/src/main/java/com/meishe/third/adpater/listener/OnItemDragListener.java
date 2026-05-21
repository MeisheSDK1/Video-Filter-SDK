package com.meishe.third.adpater.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by luoxw on 2016/6/20.
 * 关于项目拖动监听接口
 * About the project drag listening interface
 */
public interface OnItemDragListener {

    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);

    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);

    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
