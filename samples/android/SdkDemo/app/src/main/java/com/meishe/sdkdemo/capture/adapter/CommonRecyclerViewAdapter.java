package com.meishe.sdkdemo.capture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/17 下午8:43
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewAdapter<T>.CommonViewHolder> {

    private List<T> mList=new ArrayList<>();
    private OnItemClickListener<T> mOnItemClickListener;
    private int variableId;
    private int layoutItemId;
    private int mCurrentPosition;

    public void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
        notifyDataSetChanged();
    }

    public CommonRecyclerViewAdapter(int layoutItemId, int variableId) {
        this.variableId = variableId;
        this.layoutItemId = layoutItemId;
    }

    public void refreshAllData(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setData(List<T> list) {
        mList=list;
        notifyDataSetChanged();
    }

    public void clear(){
        mList.clear();
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        int _position = position;
        holder.getViewDataBinding().setVariable(variableId, mList.get(position));
        holder.getViewDataBinding().executePendingBindings();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnItemClickListener) {
                    mCurrentPosition=_position;
                    mOnItemClickListener.onItemClick(view, _position, mList.get(_position));
                }
            }
        });
    }


    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                layoutItemId, parent, false);
        return new CommonViewHolder(viewDataBinding);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class CommonViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding viewDataBinding;

        public CommonViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.viewDataBinding = binding;
        }

        public ViewDataBinding getViewDataBinding() {
            return viewDataBinding;
        }
    }

    public int getCurrentSelectPosition() {
        return mCurrentPosition;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {

        void onItemClick(View view, int posotion, T t);
    }

}
