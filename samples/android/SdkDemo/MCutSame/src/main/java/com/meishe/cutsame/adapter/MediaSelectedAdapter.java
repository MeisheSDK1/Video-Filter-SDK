package com.meishe.cutsame.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.base.bean.MediaData;
import com.meishe.base.constants.AndroidOS;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.ImageLoader;
import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.TemplateClip;
import com.meishe.cutsame.util.ConfigUtil;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lhz
 * @CreateDate : 2020/9/1
 * @Description :The selected video media adapter
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MediaSelectedAdapter extends BaseQuickAdapter<TemplateClip, BaseViewHolder> {
    private int mSelectedPos;
    private ImageLoader.Options mRoundCornerOptions;
    private DecimalFormat mDecimalFormat;

    public MediaSelectedAdapter() {
        super(R.layout.item_media_selected);
        mRoundCornerOptions = new ImageLoader
                .Options()
                .roundedCornersSmall(10, true);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.addOnClickListener(R.id.iv_delete);
        return holder;
    }

    /**
     * 删除对应位置的有效片段
     * Deletes a valid fragment for the corresponding location
     *
     * @param position the position 位置
     */
    public void deleteClip(int position) {
        TemplateClip item = getItem(position);
        if (item != null && !TextUtils.isEmpty(item.getFilePath())) {
            int tempPos = mSelectedPos;
            if (tempPos >= 0) {
                mSelectedPos = -1;
                notifyItemChanged(tempPos);
            }
            item.setFilePath("");
            notifyItemChanged(position);
            mSelectedPos = findSelectedPosition();
            if (position != mSelectedPos && mSelectedPos >= 0) {
                notifyItemChanged(mSelectedPos);
            }
        }

    }

    /**
     * 设置自动选中位置的有效片段
     * Sets a valid fragment of the automatically selected location
     *
     * @param mediaData the media data 媒体数据
     * @param mediaData the target position
     */
    public void setSelected(MediaData mediaData, int position) {
        mSelectedPos = position;
        setSelected(mediaData);
    }

    /**
     * 设置自动选中位置的有效片段
     * Sets a valid fragment of the automatically selected location
     *
     * @param mediaData the media data 媒体数据
     */
    public void setSelected(MediaData mediaData) {
        if (mSelectedPos >= 0) {
            TemplateClip item = getItem(mSelectedPos);
            if (item != null) {
                item.setFilePath(mediaData.getThumbPath());
            }
            notifyItemChanged(mSelectedPos);
            mSelectedPos = findSelectedPosition();
            if (mSelectedPos >= 0) {
                notifyItemChanged(mSelectedPos);
            }
        }
    }

    public void setSelected(int position) {
        mSelectedPos = position;
    }

    /**
     * 找到自动选中的位置(自动选中靠前的没有设置有效片段的item)
     * Find the automatically selected position (automatically select the item at the front that does not have a valid section set)
     */
    private int findSelectedPosition() {
        int position = -1;
        for (TemplateClip clip : getData()) {
            position++;
            if (TextUtils.isEmpty(clip.getFilePath())) {
                return position;
            }
        }
        return -1;
    }

    public int getSelectedPosition() {
        return mSelectedPos;
    }

    /**
     * 列表中是否还有相同的媒体文件
     * Is the same media file still in the list
     *
     * @param filePath the file path 文件路径
     * @return the boolean
     */
    public boolean hasSameMedia(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            for (TemplateClip clip : getData()) {
                if (filePath.equals(clip.getFilePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TemplateClip templateClip) {
        ImageView cover = holder.getView(R.id.iv_cover);
        TextView tvDuration = holder.getView(R.id.tv_duration);
        ImageView ivDelete = holder.getView(R.id.iv_delete);
        int position = holder.getAdapterPosition();
        String path = templateClip.getFilePath();
        if (position == mSelectedPos) {
            cover.setBackgroundResource(R.drawable.bg_rectangle_round_stroke_red365);
        } else {
            cover.setBackgroundResource(0);
        }
        if (TextUtils.isEmpty(path)) {
            tvDuration.setTextColor(mContext.getResources().getColor(R.color.black));
            if (ivDelete.getVisibility() == View.VISIBLE) {
                ivDelete.setVisibility(View.INVISIBLE);
            }
            cover.setImageResource(R.drawable.bg_rectangle_round_white);
        } else {
            String filePath = AndroidOS.USE_SCOPED_STORAGE ? templateClip.getFilePath() : "file://" + templateClip.getFilePath();
            ImageLoader.loadUrl(mContext, filePath, cover, mRoundCornerOptions);
            tvDuration.setTextColor(mContext.getResources().getColor(R.color.white));
            if (ivDelete.getVisibility() != View.VISIBLE) {
                ivDelete.setVisibility(View.VISIBLE);
            }
        }
        tvDuration.setText(formatDuration(templateClip.getTrimDuration()));
        ImageView ivPoint = holder.getView(R.id.image_group_point);
        if (templateClip.isHasGroup()) {
            ivPoint.setVisibility(View.VISIBLE);
            int groupIndex = templateClip.getGroupIndex() % ConfigUtil.colors.length;
            Drawable drawable = CommonUtils.getRadiusDrawable(10, mContext.getResources().getColor(ConfigUtil.colors[groupIndex]));
            ivPoint.setImageDrawable(drawable);
        } else {
            ivPoint.setVisibility(View.GONE);
        }
        holder.setText(R.id.tv_index, (holder.getAdapterPosition() + 1) + "");
    }


    private String formatDuration(long duration) {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat("0.0");
        }
        return mDecimalFormat.format(duration / 1000000f) + "s";
    }
}
