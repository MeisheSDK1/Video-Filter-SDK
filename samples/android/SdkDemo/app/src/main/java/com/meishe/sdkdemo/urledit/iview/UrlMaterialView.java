package com.meishe.sdkdemo.urledit.iview;

import com.meishe.base.model.IBaseView;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/4 16:31
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface UrlMaterialView extends IBaseView {
    void onUrlMaterialListBack(List<UrlMaterialInfo> urlMaterialList);

    void onMoreUrlMaterialBack(List<UrlMaterialInfo> urlMaterialList);
}
