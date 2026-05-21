package com.meishe.arscene.presenter;

import android.content.Context;

import com.meishe.arscene.BeautyDataManager;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.iview.BeautyFragmentView;
import com.meishe.base.model.Presenter;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/16 17:33
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyFragmentPresenter extends Presenter<BeautyFragmentView> {
    /**
     * 获取相关美颜数据
     *
     * @param mContext        Context
     * @param mIsSupportMatte 是否支持去油光 Whether degreasing is supported
     */
    public void getSkinData(Context mContext, int mBeautyType, List<IFxInfo> mSkinList, boolean mIsSupportMatte) {
        if (mBeautyType == FxParams.BEAUTY_SKIN) {
            mSkinList.addAll(BeautyDataManager.getBeautyList(mContext, mIsSupportMatte));
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_FACE) {
            mSkinList.addAll(BeautyDataManager.getBeautyShapeList(mContext));
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_SMALL) {
            mSkinList.addAll(BeautyDataManager.getMicroPlasticList(mContext));
        }
    }

    /**
     * 获取美肤-磨皮数据
     * Get beauty - dermabrasion data
     *
     * @param mContext Context
     */
    public List<IFxInfo> getBuffingSkinData(Context mContext) {
        return BeautyDataManager.getBuffingSkin(mContext);
    }
}
