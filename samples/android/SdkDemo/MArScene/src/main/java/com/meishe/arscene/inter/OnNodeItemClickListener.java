package com.meishe.arscene.inter;

import com.meishe.third.adpater.BaseQuickAdapter;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/21 16:16
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface OnNodeItemClickListener {
    /**
     * Node click listaner
     *
     * @param adapter        BaseQuickAdapter
     * @param parentPosition parent position
     * @param position       child position
     */
    void onItemClick(BaseQuickAdapter adapter, int parentPosition, int position);
}
