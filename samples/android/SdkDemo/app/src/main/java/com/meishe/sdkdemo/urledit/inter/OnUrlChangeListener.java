package com.meishe.sdkdemo.urledit.inter;

import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/11 15:59
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public interface OnUrlChangeListener {
    void onUrlChange();

    void onEditVisibility(boolean isShow);

    void onUrlInfo(UrlMaterialInfo info);

}
