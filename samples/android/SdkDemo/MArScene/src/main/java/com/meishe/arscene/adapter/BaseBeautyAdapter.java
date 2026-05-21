package com.meishe.arscene.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.meishe.arscene.R;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;
import com.meishe.third.adpater.util.MultiTypeDelegate;
import com.meishe.utils.DrawableUitls;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/16 20:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public abstract class BaseBeautyAdapter extends BaseQuickAdapter<IFxInfo, BaseViewHolder> {
    public static final int SKIN_MODE_NO = 0x11;
    public static final int SKIN_MODE_WHITE = 0x12;
    public static final int SKIN_MODE_BLACK = 0x13;
    public static final int SLELCT_STYLE_DEFAULT = 0x22;
    public static final int SLELCT_STYLE_SELF = 0x23;
    protected static final int NO_PLACE_HOLDER = 0;
    protected static final int PLACE_HOLDER = 1;
    protected int mSelectPosition = -1;
    protected boolean isEnable = false;
    protected int mSkinMode = SKIN_MODE_WHITE;
    protected int mSelectStyle = SLELCT_STYLE_SELF;
    protected Drawable mSelectBg;

    public BaseBeautyAdapter() {
        super(null);
        setMultiTypeDelegate(new MultiTypeDelegate<IFxInfo>() {
            @Override
            protected int getItemType(IFxInfo entity) {
                if (TextUtils.equals(BaseFxInfo.TYPE_PLACE_HOLDER, entity.getType())) {
                    return PLACE_HOLDER;
                }
                return NO_PLACE_HOLDER;
            }
        });
        getMultiTypeDelegate()
                .registerItemType(PLACE_HOLDER, R.layout.item_beauty_point)
                .registerItemType(NO_PLACE_HOLDER, R.layout.item_beauty);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, IFxInfo item) {
        switch (holder.getItemViewType()) {
            case PLACE_HOLDER:
                holder.setBackgroundDrawable(R.id.view_beauty_point, tintColor(R.drawable.beauty_point, mSkinMode));
                holder.itemView.setAlpha(isEnable ? 1.0f : 0.4f);
                convertPlaceHolder(holder, item);
                break;
            case NO_PLACE_HOLDER:
                mSelectBg = DrawableUitls.getRadiusDrawable(
                        mContext.getResources().getDimensionPixelSize(R.dimen.dp_px_7),
                        mContext.getResources().getColor(R.color.color_63ABFF),
                        mContext.getResources().getDimensionPixelSize(R.dimen.dp_px_26),
                        Color.TRANSPARENT);
                convertHolder(holder, item);
                CheckBox mBeautyPoint = holder.getView(R.id.beauty_point);
                mBeautyPoint.setVisibility((item.getStrength() != 0) ? View.VISIBLE : View.INVISIBLE);
                mBeautyPoint.setBackgroundResource((mSkinMode == SKIN_MODE_WHITE)
                        ? R.drawable.beauty_select_white_point
                        : R.drawable.beauty_select_black_point);
                mBeautyPoint.setChecked(mSelectPosition == holder.getAdapterPosition());
                break;
            default:
                break;
        }
    }

    /**
     * 绑定数据
     * Bind data
     *
     * @param holder holder
     * @param item   item
     */
    protected abstract void convertHolder(BaseViewHolder holder, IFxInfo item);

    /**
     * 绑定占位数据
     * Bind placeholder data
     *
     * @param holder holder
     * @param item   item
     */
    protected abstract void convertPlaceHolder(BaseViewHolder holder, IFxInfo item);

    public void setSkinMode(int skinMode) {
        mSkinMode = skinMode;
    }

    public void setSelectStyle(int selectStyle) {
        mSelectStyle = selectStyle;
    }

    protected Drawable tintColor(int resId, int skinMode) {
        if (resId == 0) {
            return null;
        }
        Drawable drawable = mContext.getResources().getDrawable(resId).mutate();
        if (skinMode != SKIN_MODE_NO) {
            drawable.setColorFilter((skinMode == SKIN_MODE_WHITE) ? Color.WHITE : Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        }
        return drawable;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectPosition(int position) {
        if (position > getData().size()) {
            return;
        }
        mSelectPosition = position;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectPosition(int position, boolean isRefresh) {
        if (position > getData().size()) {
            return;
        }
        mSelectPosition = position;
        if (isRefresh) {
            notifyDataSetChanged();
        }
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEnable(boolean enable) {
        isEnable = enable;
        notifyDataSetChanged();
    }

    public boolean isEnable() {
        return isEnable;
    }


    public void setAllExpanded(boolean isExpanded) {
        List<IFxInfo> mData = getData();
        for (IFxInfo info : mData) {
            if (null == info) {
                continue;
            }
            info.setIsExpanded(isExpanded);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAllExpandedExceptSelect(boolean isExpanded) {
        List<IFxInfo> mData = getData();
        for (int i = 0; i < mData.size(); i++) {
            if (i == mSelectPosition) {
                continue;
            }
            IFxInfo info = mData.get(i);
            if (null == info) {
                continue;
            }
            info.setIsExpanded(isExpanded);
        }
        notifyDataSetChanged();
    }

    public void setExpandedByFxInfo(IFxInfo info) {
        if (null == info) {
            return;
        }
        info.setIsExpanded(!info.isExpanded());
    }
}
