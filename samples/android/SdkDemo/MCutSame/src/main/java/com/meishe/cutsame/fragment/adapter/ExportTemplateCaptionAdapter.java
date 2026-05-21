package com.meishe.cutsame.fragment.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.ExportTemplateCaption;
import com.meishe.base.utils.ImageLoader;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/24 20:35
 * @Description : 导出模板-字幕适配器
 * export template caption adapter
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateCaptionAdapter extends BaseQuickAdapter<ExportTemplateCaption, BaseViewHolder> {

    public static final int ACTION_STATE_DEFAULT = 0;
    public static final int ACTION_STATE_SELECT_CAPTION = 1;

    private int mState;

    private OnChildViewClickListener mOnChildViewClickListener;

    public ExportTemplateCaptionAdapter() {
        super(R.layout.item_export_template_caption);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, final ExportTemplateCaption item) {
        ImageView imageView = helper.getView(R.id.iv_image);
        ImageLoader.loadUrl(mContext, item.getImagePath(), imageView);
        View lock = helper.getView(R.id.iv_lock);
        if (item.isLock()) {
            lock.setVisibility(View.VISIBLE);
        } else {
            lock.setVisibility(View.GONE);
        }
        ImageView selectCaption = helper.getView(R.id.iv_select_caption);
        ImageView lockRight = helper.getView(R.id.iv_is_lock);

        if (item.isLock()) {
            helper.setBackgroundRes(R.id.iv_select_caption, R.mipmap.ic_cut_same_export_template_group_forbid_selected);
        } else {
            if (item.isCaptionSelect()) {
                helper.setBackgroundRes(R.id.iv_select_caption, R.mipmap.ic_cut_same_export_template_group_selected);
            } else {
                helper.setBackgroundRes(R.id.iv_select_caption, R.mipmap.ic_cut_same_export_template_group_unchecked);
            }
        }

        if (mState == ACTION_STATE_DEFAULT) {
            lockRight.setVisibility(View.VISIBLE);
            selectCaption.setVisibility(View.INVISIBLE);
        } else {
            lockRight.setVisibility(View.INVISIBLE);
            selectCaption.setVisibility(View.VISIBLE);
        }

        helper.setBackgroundRes(R.id.iv_is_lock, item.isLock() ? R.mipmap.ic_cut_same_export_template_locked_red : R.mipmap.ic_cut_same_export_template_group_lock);
        helper.setText(R.id.tv_clip_duration, item.getCaptionDuration());
        helper.setText(R.id.tv_caption_name, item.getCaptionContent());
        lockRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean lock = item.isLock();
                item.setLock(!lock);
                notifyDataSetChanged();
            }
        });

        selectCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isLock()) {
                    return;
                }
                boolean select = item.isCaptionSelect();
                item.setCaptionSelect(!select);
                notifyDataSetChanged();
                if (mOnChildViewClickListener != null) {
                    mOnChildViewClickListener.onSelectCaptionClick();
                }
            }
        });
    }

    public int getState() {
        return mState;
    }

    public void setState(int mState) {
        this.mState = mState;
        notifyDataSetChanged();
    }


    public void setOnChildViewClickListener(OnChildViewClickListener onChildViewClickListener) {
        this.mOnChildViewClickListener = onChildViewClickListener;
    }

    public interface OnChildViewClickListener {
        /**
         * 右侧选中按钮点击
         * right button click
         */
        void onSelectCaptionClick();
    }
}
