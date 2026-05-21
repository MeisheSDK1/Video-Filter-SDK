package com.meishe.third.adpater;

import com.meishe.base.utils.CommonUtils;


/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2020/12/29 13:50
 * @Description :调节菜单适配器 Adapter for adjust menu.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseSelectAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    private int mSelectPosition = -1;

    public BaseSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setSelectPosition(int selection) {
        if (CommonUtils.isIndexAvailable(mSelectPosition, getData()) && mSelectPosition != selection) {
            notifyItemChanged(mSelectPosition);
        }
        mSelectPosition = selection;
        notifyItemChanged(mSelectPosition);
    }
}
