package com.meishe.cutsame.fragment.presenter;

import com.meishe.cutsame.CutSameNetApi;
import com.meishe.cutsame.fragment.iview.CutCompileVpView;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.LogUtils;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;

/**
 * Created by CaoZhiChao on 2020/11/18 16:05
 * 剪同款裁剪页面调用生成
 * Cut the same cut page call compile
 */
public class CutCompilePresenter extends Presenter<CutCompileVpView> {

    @Override
    public void attachView(CutCompileVpView cutCompileVpView) {
        super.attachView(cutCompileVpView);
    }

    /**
     * 用于用户使用模板时，模板的使用数量的增加
     * <p></p>
     * Increase in the number of templates used by the user when using a template
     */
    public void updateTemplateInteraction(final String templateId) {
        CutSameNetApi.uploadUsedTemplate(this, templateId, new RequestCallback<Object>() {
            @Override
            public void onSuccess(BaseResponse<Object> response) {
                //LogUtils.d("response=" + response);
            }

            @Override
            public void onError(BaseResponse<Object> response) {
                LogUtils.e("response=" + response);
            }
        });
    }
}
