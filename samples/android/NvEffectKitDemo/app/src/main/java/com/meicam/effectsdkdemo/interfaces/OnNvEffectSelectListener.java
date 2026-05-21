package com.meicam.effectsdkdemo.interfaces;

import com.meicam.effect.sdk.NvsEffect;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Meng Guijun
 * @CreateDate: 2021/2/2 13:56
 * @Description:
 * @Copyright:2021 www.meishesdk.com Inc. All rights reserved.
 */
public interface OnNvEffectSelectListener {

    void onNvEffectSelected(String uuid, NvsEffect effect, int mode);
}
