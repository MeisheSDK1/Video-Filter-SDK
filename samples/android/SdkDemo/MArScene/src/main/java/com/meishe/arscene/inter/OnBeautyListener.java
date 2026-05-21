package com.meishe.arscene.inter;

import com.meishe.arscene.bean.BeautyFxInfo;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/22 10:54
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface OnBeautyListener {
    /**
     * 美颜效果应用
     * Beauty effect application
     *
     * @param beautyType 美颜类型：美肤，美型，微整形... Beauty type: Beauty, beauty, micro plastic...
     * @param info       fx
     */
    void onBeautyApply(int beautyType, IFxInfo info);

    /**
     * 是否应用锐度
     * Whether to apply sharpness
     *
     * @param isOpen 是否开启 Whether to enable
     */
    void onSharpenApply(boolean isOpen);

    /**
     * 美颜应用完成
     * Beauty application is complete
     *
     * @param beautyParams params
     */
    void onBeautyFinish(BeautyFxInfo beautyParams);
}
