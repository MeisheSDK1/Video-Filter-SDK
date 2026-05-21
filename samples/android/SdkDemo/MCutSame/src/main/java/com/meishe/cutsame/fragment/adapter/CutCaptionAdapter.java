package com.meishe.cutsame.fragment.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.engine.bean.template.TemplateCaptionDesc;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

/**
 * Created by CaoZhiChao on 2020/11/4 18:16
 * 切割字幕适配器
 * Cut caption adapter
 */
public class CutCaptionAdapter extends BaseQuickAdapter<TemplateCaptionDesc, BaseViewHolder> {
    private int mSelectIndex = -1;
    private int mBeforeSelectIndex = -1;

    public CutCaptionAdapter() {
        super(R.layout.fragment_cut_editor_item);
    }

    /**
     * Sets select index.
     * 设置选择索引
     * @param selectIndex the select index 选择索引
     */
    public void setSelectIndex(int selectIndex) {
        mSelectIndex = selectIndex;
        notifyItemChanged(mBeforeSelectIndex);
        notifyItemChanged(mSelectIndex);
    }

    /**
     * Refresh text by index.
     * 按索引刷新文本
     * @param text  the text 文本
     * @param index the index 索引
     */
    public void refreshTextByIndex(String text, int index) {
        if (index > mData.size() - 1) {
            LogUtils.d( "refreshTextByIndex: index is to big ! index: " + index + "  mData.size()： " + mData.size());
            return;
        }
        mData.get(index).text = text;
        notifyItemChanged(index);
    }

    /**
     * Convert.
     * 转换
     * @param helper the helper helper
     * @param item   the item 条目
     */
    @Override
    protected void convert(@NonNull BaseViewHolder helper, TemplateCaptionDesc item) {
        helper.setText(R.id.fragment_editor_item_count, String.valueOf(helper.getAdapterPosition() + 1));
        helper.setText(R.id.fragment_editor_item_text, item.text);
        helper.setBackgroundDrawable(R.id.fragment_editor_item_editor_cover, CommonUtils.getRadiusDrawable(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_6),
                mContext.getResources().getColor(R.color.activity_tailor_button_background)));
        if (mSelectIndex == helper.getAdapterPosition()) {
            helper.setVisible(R.id.fragment_editor_item_editor_cover, true);
        } else {
            helper.setVisible(R.id.fragment_editor_item_editor_cover, false);
        }
        helper.setVisible(R.id.fragment_editor_item_editor_clock, false);
        helper.setBackgroundDrawable(R.id.fragment_editor_item_container, CommonUtils.getRadiusDrawable(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_6),
                mContext.getResources().getColor(R.color.fragment_editor_item_container_color)));
        if (item.getBitmap() != null) {
            ImageView imageView = helper.getView(R.id.fragment_editor_item_img);
            imageView.setImageBitmap(item.getBitmap());
        }
    }

    /**
     * Sets before select index.
     * 选择索引前的集合
     * @param beforeSelectIndex the before select index 索引前的集合
     */
    public void setBeforeSelectIndex(int beforeSelectIndex) {
        mBeforeSelectIndex = beforeSelectIndex;
    }

    public int getBeforeSelectIndex() {
        return mBeforeSelectIndex;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }
}
