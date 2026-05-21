package com.meishe.sdkdemo.capture.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.meishe.third.adpater.BaseSelectAdapter;
import com.meishe.third.adpater.BaseViewHolder;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/17 下午8:43
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CommonLoadMoreAndRefreshAdapter<T> extends BaseSelectAdapter<T> {

    private OnItemClickListener<T> mOnItemClickListener;
    private int variableId;
    private int layoutItemId;

    public CommonLoadMoreAndRefreshAdapter(int layoutItemId, int variableId) {
        super(layoutItemId);
        this.variableId = variableId;
        this.layoutItemId = layoutItemId;
    }

    @Override
    protected CommonViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                layoutItemId, parent, false);
        return new CommonViewHolder(viewDataBinding);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, T item) {
        int _position = helper.getAdapterPosition();
        if (helper instanceof CommonViewHolder) {
            CommonViewHolder holder = (CommonViewHolder) helper;
            holder.getViewDataBinding().setVariable(variableId, item);
            holder.getViewDataBinding().executePendingBindings();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClick(view, _position, item);
                    }
                }
            });
        }
    }

    public static class CommonViewHolder extends BaseViewHolder {

        ViewDataBinding viewDataBinding;

        public CommonViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.viewDataBinding = binding;
        }

        public ViewDataBinding getViewDataBinding() {
            return viewDataBinding;
        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {

        void onItemClick(View view, int posotion, T t);
    }

}
