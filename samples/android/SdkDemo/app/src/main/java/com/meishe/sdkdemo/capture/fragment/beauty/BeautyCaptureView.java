package com.meishe.sdkdemo.capture.fragment.beauty;

import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.model.IBaseView;

import java.util.HashMap;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/14 20:52
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface BeautyCaptureView extends IBaseView {
    /**
     * 获取美颜模板编辑数据
     *
     * @param mapDatas data
     */
    void getBeautyData(HashMap<Integer, List<IFxInfo>> mapDatas, boolean isCustomTemplate);
}
