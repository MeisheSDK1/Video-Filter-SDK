package com.meishe.sdkdemo.activity.view;

import androidx.fragment.app.Fragment;

import com.meishe.base.model.IBaseView;
import com.meishe.sdkdemo.main.bean.AdBeansFormUrl;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/4 16:31
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface MainView extends IBaseView {
    /**
     * 获取轮播图数据
     * Get carousel data
     *
     * @param adInfos adInfos
     */
    void getSpannerViewData(List<AdBeansFormUrl.AdInfo> adInfos);

    /**
     * 组装首页功能视图碎片
     * Assemble the Home Features View fragment
     *
     * @param mFragmentList fragment
     * @param listMap       Radio
     */
    void onPackageFragmentView(List<Fragment> mFragmentList, Map<Integer, List<String>> listMap);

    /**
     * 隐私协议是否同意
     * Whether the Privacy Agreement is agreed
     *
     * @param isAgree isAgree
     */
    void onPrivacyPolicyAgree(boolean isAgree);

    /**
     * 隐私协议跳转
     * Privacy protocol jump
     *
     * @param url url
     */
    void onPrivacyPolicyWeb(String url);

    /**
     * 初始化人脸模型授权相关
     * Initialize face model authorization
     *
     */
    void onInitArSceneEffect();

}
