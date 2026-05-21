package com.meishe.sdkdemo.edit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.mask.MaskBean;

import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/3/5.
 * @Description :蒙版实体类。Mask Bean class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MaskAdapter extends RecyclerView.Adapter<MaskAdapter.MaskViewHolder> {
    private List<MaskBean> data;
    private Context context;
    private OnItemClickListener itemListener;
    private int selectPos = 0;

    public void setOnItemClickListener(OnItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public MaskAdapter(List<MaskBean> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mask, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MaskViewHolder holder, int position) {
        final int pos = position;
        MaskBean maskBean = data.get(position);
        holder.ivMask.setImageResource(maskBean.getCoverId());
        holder.tvMask.setText(maskBean.getName());
        holder.bgSelected.setVisibility(selectPos == position ? View.VISIBLE : View.GONE);
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectPos == pos) {
                    return;
                }
                selectPos = pos;
                if (itemListener != null) {
                    itemListener.onItemClick(data.get(pos));
                }
            }
        });
        holder.tvMask.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
        notifyDataSetChanged();
    }

    public void setMaskType(int maskType) {
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                MaskBean maskBean = data.get(i);
                if (maskBean != null) {
                    if (maskType == maskBean.getMaskType()) {
                        setSelectPos(i);
                        break;
                    }
                }
            }
        }
    }

    public int getMaskTypeSelected() {
        if (selectPos > 0) {
            if (data != null && data.size() > selectPos) {
                return data.get(selectPos).getMaskType();
            }
        }
        return 0;
    }

    public int getSelectPos() {
        if (selectPos > 0) {
            return selectPos;
        }
        return 0;
    }

    public class MaskViewHolder extends RecyclerView.ViewHolder {
        View bgSelected;
        TextView tvMask;
        ImageView ivMask;
        View mainView;

        public MaskViewHolder(@NonNull View itemView) {
            super(itemView);
            bgSelected = itemView.findViewById(R.id.item_bg_mask_selected);
            tvMask = itemView.findViewById(R.id.item_tv_mask);
            ivMask = itemView.findViewById(R.id.item_bg_mask);
            mainView = itemView.findViewById(R.id.item_main);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MaskBean maskBean);
    }
}
