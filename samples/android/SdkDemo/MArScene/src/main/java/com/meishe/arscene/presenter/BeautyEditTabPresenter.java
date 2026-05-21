package com.meishe.arscene.presenter;

import android.content.Context;

import com.meishe.arscene.R;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.iview.BeautyEditTabView;
import com.meishe.arscene.template.BeautyEditFragment;
import com.meishe.arscene.view.SeekBarView;
import com.meishe.base.model.Presenter;

import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 15:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyEditTabPresenter extends Presenter<BeautyEditTabView> {
    /**
     * 初始化页面
     * Initialize page
     *
     * @param context       context
     * @param mFragmentList tabs
     * @param mTabTitleList tab
     */
    public void initFragment(Context context, List<Fragment> mFragmentList, List<String> mTabTitleList, boolean mIsSupportMatte) {
        String[] mBeautyArray = context.getResources().getStringArray(R.array.beauty_type);
        if (mBeautyArray.length == 0) {
            return;
        }
        for (int i = 0; i < mBeautyArray.length; i++) {
            mFragmentList.add(BeautyEditFragment.newInstance(FxParams.getBeautyTemplateType()[i], mIsSupportMatte));
            mTabTitleList.add(mBeautyArray[i]);
        }
    }

    public void getBeautyTabTitle(Context context, List<String> mTabTitleList) {
        if (null == mTabTitleList) {
            return;
        }
        mTabTitleList.clear();
        String[] mBeautyArray = context.getResources().getStringArray(R.array.beauty_type);
        mTabTitleList.addAll(Arrays.asList(mBeautyArray));
    }

    /**
     * 初始化强度
     * Initial strength
     *
     * @param beautyType type
     */
    public void initSkinProgressBar(int beautyType, SeekBarView mMainSeekBar, IFxInfo fxInfo) {
        switch (beautyType) {
            case FxParams.BEAUTY_SKIN:
            case FxParams.BEAUTY_ADJUST:
            case FxParams.BEAUTY_CONTOURING:
                //美肤范围[0,1] Beauty range[0,1]
                mMainSeekBar.setMax(100);
                mMainSeekBar.setPointEnable(false);
                mMainSeekBar.setBreakProgress(0);
                break;
            case FxParams.BEAUTY_FACE:
                //美型范围[-1,1] Range of beauty[-1,1]
                mMainSeekBar.setMax(200);
                mMainSeekBar.setPointEnable(true);
                mMainSeekBar.setBreakProgress(100);
                mMainSeekBar.setPointProgress(100);
                break;
            case FxParams.BEAUTY_SMALL:
                //微整形范围[-1,1]和[0,1](法令纹，黑眼圈，亮眼，美牙) Micro-shaping range [-1,1] and [0,1](regular lines, dark circles, bright eyes, beautiful teeth)
                mMainSeekBar.setMax(100);
                mMainSeekBar.setPointEnable(false);
                mMainSeekBar.setBreakProgress(0);
                mMainSeekBar.setPointProgress(0);
                if ((fxInfo instanceof CompoundFxInfo)) {
                    mMainSeekBar.setMax(200);
                    mMainSeekBar.setPointEnable(true);
                    mMainSeekBar.setBreakProgress(100);
                    mMainSeekBar.setPointProgress(100);
                }
                break;
            default:
                break;
        }
    }
}
