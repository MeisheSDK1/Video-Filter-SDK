package com.meishe.cutsame.fragment.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.base.bean.MediaData;
import com.meishe.base.bean.MediaSection;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.ImageLoader;
import com.meishe.third.adpater.BaseSectionQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;


/**
 * author : lhz
 * date   : 2020/9/1
 * desc   :选择视频媒体适配器
 * Select the video media adapter
 */
public class MediaSelectAdapter extends BaseSectionQuickAdapter<MediaSection, BaseViewHolder> {
    private int mItemSize;
    private boolean isNeedHideTag;

    public void setNeedHideTag(boolean needHideTag) {
        isNeedHideTag = needHideTag;
    }

    public MediaSelectAdapter(int itemSize) {
        super(R.layout.item_media_select, R.layout.item_media_select_head, null);
        this.mItemSize = itemSize;
    }

    /**
     * On create view holder base view holder.
     * 在创建视图holder基础视图holder上
     *
     * @param parent   the parent 父视图
     * @param viewType the view type 视图类型
     * @return the base view holder 基础视图持有
     */
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder holder = super.onCreateViewHolder(parent, viewType);
        /*
         * BaseSectionQuickAdapter这里是0,其他的不是
         *  BaseSectionQuickAdapter here is 0, the other one is not
         * */
        if (viewType == 0 && holder.itemView.getHeight() != mItemSize) {
            /*
             * 已经设置过就不再设置了
             *  It's already set, it's not set
             * */
            setLayoutParams(holder.itemView);/*
            根布局
             The root layout
            */
        }
        return holder;
    }


    private void setLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        // layoutParams.width = mItemSize;
        layoutParams.height = mItemSize;
        view.setLayoutParams(layoutParams);
    }

    /**
     * Convert.
     * 转换
     *
     * @param holder  the holder 持有
     * @param section the section 截面
     */
    @Override
    protected void convert(BaseViewHolder holder, MediaSection section) {
        MediaData mediaData = section.t;
        ImageView view = holder.getView(R.id.iv_cover);
        ImageLoader.loadUrl(mContext, "file://" + mediaData.getThumbPath(), view);
        TextView tvDuration = holder.getView(R.id.tv_duration);
        if (mediaData.getType() == MediaData.TYPE_VIDEO) {
            if (tvDuration.getVisibility() != View.VISIBLE) {
                tvDuration.setVisibility(View.VISIBLE);
            }
            tvDuration.setText(FormatUtils.sec2Time((int) (mediaData.getDuration() / 1000)));
        } else if (mediaData.getType() == MediaData.TYPE_PHOTO) {
            if (tvDuration.getVisibility() == View.VISIBLE) {
                tvDuration.setVisibility(View.INVISIBLE);
            }
        }
        if (isNeedHideTag) {
            holder.setVisible(R.id.tv_selected, false);
        } else {
            holder.setVisible(R.id.tv_selected, mediaData.isState());
        }

    }

    /**
     * Convert head.
     * 转换头
     *
     * @param helper  the helper 持有
     * @param section the section 截面
     */
    @Override
    protected void convertHead(BaseViewHolder helper, MediaSection section) {
        helper.setText(R.id.tv_date, section.header);
    }
}
