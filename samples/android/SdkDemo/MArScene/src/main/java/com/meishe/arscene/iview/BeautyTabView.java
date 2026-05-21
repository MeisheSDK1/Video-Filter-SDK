package com.meishe.arscene.iview;

import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.model.IBaseView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/15 19:29
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface BeautyTabView extends IBaseView {
    /**
     * 美型开关
     * Beautiful type switch
     *
     * @param isChecked isChecked
     */
    void onBeautySwitch(boolean isChecked);

    /**
     * 清除美颜特效
     * Clear beauty effects
     *
     * @param beautyType type
     * @param info       info
     */
    void onClearBeautyFx(int beautyType, IFxInfo info);

    /**
     * 恢复清除的美颜特效
     * Restores cleared beauty effects
     *
     * @param beautyType beautyType
     * @param info       info
     * @param isApply    是否需要恢复 Whether to restore
     */
    void onRecoverClearBeautyFx(int beautyType, IFxInfo info, boolean isApply);
}
