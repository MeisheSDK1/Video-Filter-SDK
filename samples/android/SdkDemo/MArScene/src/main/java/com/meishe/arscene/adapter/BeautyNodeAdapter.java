package com.meishe.arscene.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishe.arscene.R;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.third.adpater.BaseViewHolder;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/16 20:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyNodeAdapter extends BaseBeautyAdapter {

    @Override
    protected void convertHolder(BaseViewHolder holder, IFxInfo item) {
        int position = holder.getAdapterPosition();
        ImageView mBeautyIcon = holder.getView(R.id.item_beauty_icon);
        boolean isSetSkinMode = TextUtils.equals(item.getType(), FxParams.SKINNING)
                || (TextUtils.equals(item.getType(), FxParams.SKIN_COLOUR)
                || (TextUtils.equals(item.getType(), FxParams.BEAUTY_WHITENING)));
        if ((mSelectPosition == position)) {
            mBeautyIcon.setImageResource(item.getResourceId());
            mBeautyIcon.setSelected(true);
        } else {
            mBeautyIcon.setImageDrawable(tintColor(item.getResourceId(), isSetSkinMode ? SKIN_MODE_NO : mSkinMode));
            mBeautyIcon.setSelected(false);
        }
        TextView mBeautyName = holder.getView(R.id.item_beauty_name);
        mBeautyName.setText(item.getName());
        mBeautyName.setTextColor(mContext.getResources().getColorStateList(
                (mSkinMode == SKIN_MODE_WHITE)
                        ? R.color.beauty_text_white_selector
                        : R.color.beauty_text_black_selector));
        mBeautyName.setSelected(mSelectPosition == position);
    }

    @Override
    protected void convertPlaceHolder(BaseViewHolder holder, IFxInfo item) {
        holder.itemView.setAlpha(1.0f);
    }
}
