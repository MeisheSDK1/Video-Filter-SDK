package com.meishe.sdkdemo.edit.filter;

import com.meishe.base.model.IBaseView;
import com.meishe.sdkdemo.edit.data.FilterItem;

import java.util.ArrayList;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/22 11:01
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface FiltersView extends IBaseView {
    /**
     * 获取滤镜数据
     * Get filter data
     *
     * @param mFilterData data
     */
    void onFilterData(ArrayList<FilterItem> mFilterData);

    /**
     * 修改可调节参数滤镜
     * Modify the adjustable parameter filter
     */
    void onChangeAdjustFilter();

}
