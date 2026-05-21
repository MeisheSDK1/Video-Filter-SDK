package com.meishe.sdkdemo.edit.animatesticker;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : unKnow
 * @CreateDate : 2021/4/20.
 * @Description :贴纸dapter(样式)
 * @Description :Sticker adapter(Style)
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class StickerRecycleViewAdapter extends RecyclerView.Adapter<StickerRecycleViewAdapter.ViewHolder> {
    private ArrayList<NvAsset> mAssetList = new ArrayList<>();
    private ArrayList<NvAssetManager.NvCustomStickerInfo> mCustomStickerAssetList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;

    private int mSelectedPos = -1;
    private boolean mIsCutomStickerAsset = false;
    /*
     * 记录贴纸是否是播放状态
     * Record whether the sticker is playing
     * */
    private boolean mIsStickerInPlay = false;

    public void setIsStickerInPlay(boolean isStickerInPlay) {
        this.mIsStickerInPlay = isStickerInPlay;
    }

    public StickerRecycleViewAdapter(Context context) {
        mContext = context;
    }

    public void setAssetList(ArrayList<NvAsset> assetList) {
        this.mAssetList = assetList;
    }

    public void setCustomStickerAssetList(ArrayList<NvAssetManager.NvCustomStickerInfo> assetList) {
        this.mCustomStickerAssetList = assetList;
    }

    public void setIsCutomStickerAsset(boolean isCutomStickerAsset) {
        mIsCutomStickerAsset = isCutomStickerAsset;
    }

    public void setSelectedPos(int selectedPos) {
        this.mSelectedPos = selectedPos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_asset_animatesticker, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String assetCoverUrl = mIsCutomStickerAsset ? mCustomStickerAssetList.get(position).imagePath : mAssetList.get(position).coverUrl;
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.placeholder(R.mipmap.default_sticker);
        if (!TextUtils.isEmpty(assetCoverUrl) && assetCoverUrl.endsWith("webp")) {
            Uri uri = Uri.parse(assetCoverUrl);
            PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
            AbstractDraweeController build = pipelineDraweeControllerBuilder.setUri(uri).setAutoPlayAnimations(true).build();
            holder.mStickerAssetCover.setController(build);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(assetCoverUrl)
                    .apply(options)
                    .into(holder.mStickerAssetCover);
        }


        if (mSelectedPos == position) {
            holder.mStickerPlayButton.setImageResource(mIsStickerInPlay ? R.mipmap.icon_edit_pause : R.mipmap.icon_edit_play);
        } else {
            holder.mStickerPlayButton.setImageResource(R.mipmap.icon_edit_pause);
        }

        holder.mStickerPlayButton.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);
        holder.mSelecteItem.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(v, position);
                if (mSelectedPos >= 0 && mSelectedPos == position)
                    return;
                notifyItemChanged(mSelectedPos);
                mSelectedPos = position;
                notifyItemChanged(mSelectedPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIsCutomStickerAsset ? mCustomStickerAssetList.size() : mAssetList.size();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mStickerAssetCover;
        ImageView mStickerPlayButton;
        View mSelecteItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mStickerAssetCover = (SimpleDraweeView) itemView.findViewById(R.id.stickerAssetCover);
            mStickerPlayButton = (ImageView) itemView.findViewById(R.id.stickerPlayButton);
            mSelecteItem = itemView.findViewById(R.id.selectedItem);
        }
    }
}
