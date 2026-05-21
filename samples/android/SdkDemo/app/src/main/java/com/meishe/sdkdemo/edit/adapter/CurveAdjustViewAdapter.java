package com.meishe.sdkdemo.edit.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.CurveAdjustData;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by admin on 2018/5/25.
 */

public class CurveAdjustViewAdapter extends RecyclerView.Adapter<CurveAdjustViewAdapter.ViewHolder>  {
    private List<CurveAdjustData> m_assetInfolist;
    private Context m_mContext;
    private OnItemClickListener m_onItemClickListener = null;
    private int selectedPosition = 0;

    public CurveAdjustViewAdapter(Context context) {
        m_mContext = context;
    }

    public void updateData(List<CurveAdjustData> assetInfoList) {
        m_assetInfolist = assetInfoList;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPos) {
        this.selectedPosition = selectedPos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(m_mContext).inflate(R.layout.item_curve_adjust, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final int position = pos;
        CurveAdjustData curveAdjustData = m_assetInfolist.get(position);
        if (curveAdjustData == null)
            return;
        Glide.with(m_mContext).load(curveAdjustData.getCover()).into(holder.mImageAsset);
        if (curveAdjustData.isCustom()) {
            holder.mAssetName.setText("+ " + m_mContext.getResources().getString(R.string.tv_custom));
        } else {
            holder.mAssetName.setText("");
        }
        holder.rl_selected.setBackground(ContextCompat.getDrawable(m_mContext, selectedPosition == position ? R.drawable.shape_rect_blue_border_selected : R.drawable.fx_item_radius_shape_unselect));
        if(m_onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    m_onItemClickListener.onItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return m_assetInfolist.size();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.m_onItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageAsset;
        TextView mAssetName;
        FrameLayout rl_selected;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageAsset = itemView.findViewById(R.id.imageAsset);
            mAssetName = itemView.findViewById(R.id.assetName);
            rl_selected = itemView.findViewById(R.id.assetItem);
        }
    }
}
