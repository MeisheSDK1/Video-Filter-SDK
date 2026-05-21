package com.meishe.cutsame.fragment.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.meishe.cutsame.R;
import com.meishe.cutsame.bean.Template;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsVideoClip;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.ImageLoader;
import com.meishe.engine.bean.template.MeicamNvsTemplateFootageCorrespondingClipInfo;
import com.meishe.engine.editor.EditorController;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;

import java.text.DecimalFormat;

/**
 * Created by CaoZhiChao on 2020/11/4 18:16
 * 切割视频适配器
 * Cutting video adapter
 */
public class CutVideoAdapter extends BaseQuickAdapter<MeicamNvsTemplateFootageCorrespondingClipInfo, BaseViewHolder> {
    private ImageLoader.Options mRoundCornerOptions;
    private int mSelectIndex = -1;
    private int mBeforeSelectIndex = -1;

    private String mTemplateType= Template.TYPE_TEMPLATE_STANDER;

    public CutVideoAdapter() {
        super(R.layout.fragment_cut_editor_item);
        mRoundCornerOptions = new ImageLoader
                .Options();
    }

    /**
     * Sets select index.
     * 设置选择索引
     *
     * @param selectIndex the select index 选择索引
     */
    public void setSelectIndex(int selectIndex) {
        mSelectIndex = selectIndex;
        notifyItemChanged(mBeforeSelectIndex);
        notifyItemChanged(mSelectIndex);
    }

    public void setTemplateType(String templateType) {
        this.mTemplateType = templateType;
    }

    private void setBackGroundImage(ImageView imageView, MeicamNvsTemplateFootageCorrespondingClipInfo item) {
        NvsVideoClip nvsVideoClip = EditorController.getInstance().getVideoClipByTemplateFootageCorrespondingClipInfo(item);
        if (nvsVideoClip != null) {
            if (TextUtils.isEmpty(nvsVideoClip.getFilePath())) {
                imageView.setImageResource(0);
            } else {
                ImageLoader.loadUrl(mContext, "file://" + nvsVideoClip.getFilePath(), imageView, mRoundCornerOptions);
            }
        } else {
            imageView.setImageResource(0);
        }
    }

    /**
     * Convert.
     * 转换
     *
     * @param helper the helper helper
     * @param item   the item 条目
     */
    @Override
    protected void convert(
            @NonNull BaseViewHolder helper,MeicamNvsTemplateFootageCorrespondingClipInfo item) {
        helper.setText(R.id.fragment_editor_item_count, String.valueOf(helper.getAdapterPosition() + 1));
        ImageView imageView = helper.getView(R.id.fragment_editor_item_img);
        ImageView mEditor = helper.getView(R.id.editor_shadow);
        helper.setVisible(R.id.fragment_editor_item_editor_edit_icon, false);
        helper.setVisible(R.id.fragment_editor_item_editor_edit_text, false);
        if (item.canReplace) {
            helper.setVisible(R.id.fragment_editor_item_editor_clock, false);
            if (mSelectIndex == helper.getAdapterPosition()) {
                mEditor.setImageDrawable(CommonUtils.getRadiusDrawable(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_6),
                        mContext.getResources().getColor(R.color.activity_tailor_button_background)));
                helper.setVisible(R.id.fragment_editor_item_editor_edit_icon, true);
                helper.setVisible(R.id.fragment_editor_item_editor_edit_text, true);
                mEditor.setVisibility(View.VISIBLE);
            } else {
                setBackGroundImage(imageView, item);
                mEditor.setVisibility(View.INVISIBLE);
                helper.setVisible(R.id.fragment_editor_item_editor_edit_icon, false);
                helper.setVisible(R.id.fragment_editor_item_editor_edit_text, false);
            }

            if ((item.trimOut - item.trimIn)!=0){
                helper.setText(R.id.fragment_editor_item_text, formatDuration(item.trimOut - item.trimIn));
            }
        } else {
            if (mSelectIndex == helper.getAdapterPosition()) {
                mEditor.setImageDrawable(CommonUtils.getRadiusDrawable(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_6),
                        mContext.getResources().getColor(R.color.activity_tailor_button_background)));
            } else {
                mEditor.setImageDrawable(CommonUtils.getRadiusDrawable(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_6),
                        mContext.getResources().getColor(R.color.color_ff646464)));
            }
            mEditor.setVisibility(View.VISIBLE);
            helper.setVisible(R.id.fragment_editor_item_editor_clock, true);
            helper.setText(R.id.fragment_editor_item_text, "");
        }
      /*  helper.setBackgroundDrawable(R.id.fragment_editor_item_container, CommonUtils.getRadiusDrawable(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_px_6),
                mContext.getResources().getColor(R.color.fragment_editor_item_container_color)));*/

    }

    public void setBeforeSelectIndex(int beforeSelectIndex) {
        mBeforeSelectIndex = beforeSelectIndex;
    }

    public int getBeforeSelectIndex() {
        return mBeforeSelectIndex;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

    private DecimalFormat mDecimalFormat;

    private String formatDuration(long duration) {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat("0.0");
        }
        return mDecimalFormat.format(duration / 1000000f) + "s";
    }
}
