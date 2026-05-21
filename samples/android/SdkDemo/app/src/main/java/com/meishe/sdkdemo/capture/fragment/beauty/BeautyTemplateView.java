package com.meishe.sdkdemo.capture.fragment.beauty;

import com.meishe.base.model.IBaseView;
import com.meishe.sdkdemo.capture.bean.BeautyTemplateInfo;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 15:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface BeautyTemplateView extends IBaseView {
    /**
     * * 获取美颜模板数据
     * * Obtain beauty template data
     *
     * @param mData      data
     * @param isMoreData isMoreData
     */
    void onBeautyTemplateData(List<BeautyTemplateInfo> mData, boolean isMoreData);

    /**
     * 下载进度
     * Download progress
     *
     * @param position     position
     * @param templateInfo templateInfo
     * @param progress     progress
     */
    void onDownloadPackageProgress(int position, BeautyTemplateInfo templateInfo, int progress);

    /**
     * 下载异常
     * Download exception
     *
     * @param position     position
     * @param templateInfo templateInfo
     */
    void onDownloadPackageError(int position, BeautyTemplateInfo templateInfo);

    /**
     * 下载成功
     * Download successfully
     *
     * @param position     position
     * @param templateInfo templateInfo
     */
    void onDownloadPackageSuccess(int position, BeautyTemplateInfo templateInfo);
}
