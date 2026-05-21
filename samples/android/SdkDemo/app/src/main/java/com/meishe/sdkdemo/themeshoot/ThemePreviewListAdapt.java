package com.meishe.sdkdemo.themeshoot;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.themeshoot.model.ThemeModel;

import java.util.ArrayList;
import java.util.List;
/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : Guijun
 * @CreateDate : 2020/8/5.
 * @Description :ThemePreviewListAdapt。ThemePreviewListAdapt
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemePreviewListAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ThemeModel> mAssetDataList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mClickListener;

    public ThemePreviewListAdapt(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_theme_shoot, viewGroup, false);
        return new ThemeCoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dealThemeCaptureItem(holder, position);
    }

    public void setAssetDatalist(List<ThemeModel> assetDataList) {
        this.mAssetDataList = assetDataList;
        Log.e("Datalist", "DataCount = " + mAssetDataList.size());
    }


    private void dealThemeCaptureItem(RecyclerView.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        if (mAssetDataList == null) {
            return 0;
        }
        return mAssetDataList.size();
    }

    public class ThemeCoverViewHolder extends RecyclerView.ViewHolder {

        View mItemLayout;
        ImageView mIvCover;
        TextView mTvName;
        TextView mTvNum;
        TextView mTvDuration;
        View itemDown;

        public ThemeCoverViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemLayout = itemView.findViewById(R.id.theme_item_layout);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvNum = itemView.findViewById(R.id.tv_clip_num);
            mTvDuration = itemView.findViewById(R.id.tv_theme_duration);
            itemDown = itemView.findViewById(R.id.item_net_down);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
