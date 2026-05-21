package com.meishe.sdkdemo.urledit.iview;

import android.content.Context;

import com.meishe.base.model.Presenter;
import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.urledit.bean.UrlMaterialInfo;
import com.meishe.sdkdemo.utils.Util;

import java.util.List;

import static com.meishe.sdkdemo.utils.Constants.PAGE_SIZE;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/2 13:22
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlMaterialPresenter extends Presenter<UrlMaterialView> {
    private int mPage = 1;
    private boolean canLoadMore = true;

    public void getUrlMaterialList(Context context, int type, int page) {
        String lang = Util.isZh(context) ? "zh-cn" : "en";
        HttpManager.getMediaInfoList(null, type, page, PAGE_SIZE, lang, new RequestCallback<BaseBean<UrlMaterialInfo>>() {
            @Override
            public void onSuccess(BaseResponse<BaseBean<UrlMaterialInfo>> response) {
                if (null == response || response.getData() == null) {
                    canLoadMore = false;
                    getView().onUrlMaterialListBack(null);
                    return;
                }
                List<UrlMaterialInfo> urlMaterialInfos = response.getData().getElements();
                if (null == urlMaterialInfos || urlMaterialInfos.isEmpty()) {
                    canLoadMore = false;
                    getView().onUrlMaterialListBack(null);
                    return;
                }
                canLoadMore = response.getData().getTotal() > page * PAGE_SIZE;
                if (page == 1) {
                    mPage = 1;
                    getView().onUrlMaterialListBack(urlMaterialInfos);
                } else {
                    mPage++;
                    getView().onMoreUrlMaterialBack(urlMaterialInfos);
                }
            }

            @Override
            public void onError(BaseResponse<BaseBean<UrlMaterialInfo>> response) {
                canLoadMore = false;
                getView().onUrlMaterialListBack(null);
            }
        });
    }

    public boolean getMoreUrlMaterial(Context context, int type) {
        if (canLoadMore) {
            getUrlMaterialList(context, type, mPage++);
        }
        return canLoadMore;
    }

}
