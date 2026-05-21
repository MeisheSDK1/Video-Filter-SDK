package com.meishe.sdkdemo.urledit.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.meishe.base.utils.ImageLoader;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.adpater.BaseViewHolder;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/6 15:27
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public abstract class UrlMaterialAdapter extends BaseQuickAdapter<UrlMaterialInfo, BaseViewHolder> {
    protected final int mItemWidth;
    protected final ImageLoader.Options mRoundCornerOptions;
    protected boolean isShowSelected;

    public UrlMaterialAdapter(int itemWidth, int layoutId) {
        super(layoutId);
        this.mItemWidth = itemWidth;
        mRoundCornerOptions = new ImageLoader.Options().centerCrop().roundedCorners(15);
    }

    /**
     * Convert.
     * 转换
     *
     * @param helper the helper  helper
     * @param item   the item 条目
     */
    @Override
    protected void convert(@NonNull BaseViewHolder helper, final UrlMaterialInfo item) {
        convertHolder(helper, item);
    }

    protected abstract void convertHolder(BaseViewHolder helper, final UrlMaterialInfo item);

    /**
     * 更改布局参数
     * 注意：由于瀑布流各个item大小可能不同，所以在convert做处理，如果非瀑布流不要在convert做处理。
     * Change layout parameters
     * Note: Since waterfall streams may vary in item size, do this at Convert, but not at Convert if not waterfall streams.
     */
    protected void setLayoutParams(View view, UrlMaterialInfo item) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int height = (int) (mItemWidth / (item.getWidth() * 1.0F / item.getHeight()));
        if (params != null && params.height != height) {
            params.width = mItemWidth;
            params.height = height;
            view.setLayoutParams(params);
            ConstraintLayout parent = (ConstraintLayout) view.getParent();
            ViewGroup.LayoutParams parentParams = parent.getLayoutParams();
            parentParams.height = (int) (params.height + view.getContext().getResources().getDimension(com.meishe.cutsame.R.dimen.dp_px_30));
            parent.setLayoutParams(parentParams);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setShowSelected(boolean isShow) {
        isShowSelected = isShow;
        notifyDataSetChanged();
    }

    public boolean isShowSelected() {
        return isShowSelected;
    }
}
