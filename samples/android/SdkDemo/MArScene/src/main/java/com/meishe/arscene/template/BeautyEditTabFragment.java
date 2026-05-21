package com.meishe.arscene.template;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.meicam.nvconvertorlib.Logger;
import com.meishe.arscene.R;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.iview.BeautyEditTabView;
import com.meishe.arscene.presenter.BeautyEditTabPresenter;
import com.meishe.arscene.view.SeekBarView;
import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.pop.TipsPop;
import com.meishe.base.view.CustomViewPager;
import com.meishe.base.view.MagicProgress;
import com.meishe.third.tablayout.SlidingTabLayout;
import com.meishe.third.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_MATTE_FILL_RADIUS;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_MATTE_INTENSITY;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 15:11
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyEditTabFragment extends BaseMvpFragment<BeautyEditTabPresenter> implements View.OnClickListener, BeautyEditTabView {
    private Context mContext;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTabTitleList = new ArrayList<>();
    /**
     * 是否支持去油光
     * Whether it supports degreasing
     */
    private final boolean mIsSupportMatte;
    private ConstraintLayout mBeautyProgressLayout;
    private SeekBarView mMainSeekBar;
    private SeekBarView mSubSeekBar;
    private SlidingTabLayout mEditTab;
    private CustomViewPager mEditPager;
    private ImageView mEditBack;
    private TextView mEditReset;
    private TipsPop mTipsPop;
    private OnBeautyEditListener mBeautyEditListener;
    private int mBeautyType = FxParams.BEAUTY_SKIN;
    /**
     * 当前tab选中的页面
     * The page selected by the current tab
     */
    private BeautyEditFragment mBeautyFragment;
    private IFxInfo mFxInfo;

    public BeautyEditTabFragment(boolean isSupportMatte) {
        mIsSupportMatte = isSupportMatte;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    public static BeautyEditTabFragment newInstance(boolean isSupportMatte) {
        return new BeautyEditTabFragment(isSupportMatte);
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_beauty_edit_tab;
    }

    @Override
    protected void onLazyLoad() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mBeautyProgressLayout.setVisibility(View.GONE);
            mEditTab.setCurrentTab(0);
            setBeautyFragment(0);
        }
    }

    @Override
    protected void initView(View rootView) {
        mBeautyProgressLayout = rootView.findViewById(R.id.beauty_edit_progress_layout);
        mMainSeekBar = rootView.findViewById(R.id.beauty_edit_main_seekbar);
        mSubSeekBar = rootView.findViewById(R.id.beauty_edit_sub_seekbar);
        mEditTab = rootView.findViewById(R.id.beauty_edit_tab);
        mEditPager = rootView.findViewById(R.id.beauty_edit_pager);
        mEditBack = rootView.findViewById(R.id.beauty_edit_back);
        mEditReset = rootView.findViewById(R.id.beauty_edit_reset);
        initViewPager();
        initListener();
        mTipsPop = TipsPop.create(mContext, false).setDefaultTipsContent(mContext.getResources().getString(R.string.reset_all_beauty_tips))
                .setOnTipsPopListener(new TipsPop.OnTipsPopListener() {
                    @Override
                    public void onTipsCancel() {

                    }

                    @Override
                    public void onTipsConfirm() {
                        if (mBeautyProgressLayout.getVisibility() == View.VISIBLE) {
                            mBeautyProgressLayout.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < mFragmentList.size(); i++) {
                            Fragment fragment = mFragmentList.get(i);
                            if (fragment instanceof BeautyEditFragment) {
                                ((BeautyEditFragment) fragment).resetFx();
                            }
                        }
                        mTipsPop.dismiss();
                    }
                });
    }

    private void initViewPager() {
        mEditPager.setOffscreenPageLimit(5);
        mEditPager.setNoScroll(true);
        mEditPager.setAdapter(new CommonFragmentAdapter(getChildFragmentManager(), mFragmentList, mTabTitleList));
        mEditTab.setViewPager(mEditPager);
    }

    @Override
    protected void initData() {
        mPresenter.initFragment(mContext, mFragmentList, mTabTitleList, mIsSupportMatte);
        mEditTab.updateTitles(mTabTitleList);
        setBeautyFragment(0);
    }

    public void initChildFragmentData(HashMap<Integer, List<IFxInfo>> mapDatas) {
        if (mFragmentList.isEmpty()) {
            return;
        }
        mPresenter.getBeautyTabTitle(mContext, mTabTitleList);
        List<Integer> needRemove = new ArrayList<>();
        for (int i = 0; i < mFragmentList.size(); i++) {
            Fragment fragment = mFragmentList.get(i);
            if (fragment instanceof BeautyEditFragment) {
                BeautyEditFragment beautyEditFragment = (BeautyEditFragment) fragment;
                int type = FxParams.getBeautyTemplateType()[i];
                List<IFxInfo> fxDatas = mapDatas.get(type);
                if (null == fxDatas) {
                    needRemove.add(i);
                    continue;
                }
                beautyEditFragment.refreshViewData(fxDatas);
            }
        }
        for (int i = needRemove.size() - 1; i >= 0; i--) {
            int index = needRemove.get(i);
            mTabTitleList.remove(index);
        }
        mEditTab.updateTitles(mTabTitleList);
    }

    private void initListener() {
        mEditBack.setOnClickListener(this);
        mEditReset.setOnClickListener(this);
        mEditTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                setBeautyFragment(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mMainSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (fromUser) {
                    return;
                }
                setProgressChange(progress);
            }
        });


        mSubSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (fromUser) {
                    return;
                }
                if (null == mFxInfo) {
                    return;
                }
                double paramStrength = progress * 0.27 + 3;
                if (TextUtils.equals(mFxInfo.getFxName(), ADVANCED_BEAUTY_MATTE_INTENSITY) &&
                        mFxInfo instanceof CompoundFxInfo) {
                    FxParams param = ((CompoundFxInfo) mFxInfo).findParam(ADVANCED_BEAUTY_MATTE_FILL_RADIUS);
                    param.value = paramStrength;
                    if (null != mBeautyFragment) {
                        mBeautyFragment.changeParamValue(ADVANCED_BEAUTY_MATTE_FILL_RADIUS, paramStrength);
                    }
                    applyBeautyFx(mFxInfo);
                }
            }
        });
    }

    private void setBeautyFragment(int position) {
        if (mBeautyProgressLayout.getVisibility() == View.VISIBLE) {
            mBeautyProgressLayout.setVisibility(View.GONE);
        }
        if (mFragmentList.isEmpty() || (position > mFragmentList.size()) || (position < 0)) {
            return;
        }
        Fragment fragment = mFragmentList.get(position);
        if (null == fragment) {
            return;
        }
        if (fragment instanceof BeautyEditFragment) {
            mBeautyFragment = (BeautyEditFragment) fragment;
            mBeautyFragment.setBeautyEditListener(new BeautyEditFragment.OnBeautyEditListener() {
                @Override
                public void onBeautyEdit(int beautyType, IFxInfo info) {
                    mBeautyType = beautyType;
                    if (null == info) {
                        mBeautyProgressLayout.setVisibility(View.GONE);
                        return;
                    }
                    mFxInfo = info;
                    setApplyBeauty(beautyType, info);
                    updateSubSeekBarView(info);
                }

                @Override
                public void onResetFx(int beautyType, IFxInfo info) {
                    String type = info.getType();
                    if (TextUtils.isEmpty(type)) {
                        mBeautyEditListener.onBeautyApply(beautyType, info);
                        return;
                    }
                    switch (type) {
                        case FxParams.SHARPEN:
                            mBeautyEditListener.onBeautySharpen(info);
                            break;
                        case FxParams.DEFINITION:
                            mBeautyEditListener.onBeautyDefinition(info);
                            break;
                        case FxParams.CONTOURING:
                            if (beautyType == FxParams.BEAUTY_CONTOURING) {
                                applyBeautyMakeupFx(info);
                            }
                            break;
                        default:
                            mBeautyEditListener.onBeautyApply(beautyType, info);
                            break;
                    }
                }
            });
        }
    }

    private void setApplyBeauty(int beautyType, IFxInfo info) {
        mBeautyProgressLayout.setVisibility(View.VISIBLE);
        mMainSeekBar.setVisibility(View.VISIBLE);
        mMainSeekBar.setSeekTitle(info.getName());
        switch (beautyType) {
            case FxParams.BEAUTY_SKIN:
                applyBeautySkin(info);
                break;
            case FxParams.BEAUTY_FACE:
                applyBeautyFace(info);
                break;
            case FxParams.BEAUTY_SMALL:
                applyBeautySmall(info);
                break;
            case FxParams.BEAUTY_ADJUST:
                applyBeautyAdjust(info);
                break;
            case FxParams.BEAUTY_CONTOURING:
                applyBeautyMakeup(info);
                break;
            default:
                break;
        }
    }

    /**
     * 应用美肤
     * Apply skin beauty
     *
     * @param info info
     */
    private void applyBeautySkin(IFxInfo info) {
        List<IFxInfo> iFxInfos = info.getFxNodes();
        if ((null != iFxInfos) && !iFxInfos.isEmpty()) {
            return;
        }
        Logger.e("meicam", "apply  skin:" + info.getName() + "   strength:" + info.getStrength());
        setProgressStrength(mBeautyType, (int) (info.getStrength() * 100), info);
        if (TextUtils.equals(info.getType(), FxParams.SKINNING)) {
            if (null != mBeautyEditListener) {
                mBeautyEditListener.resetAllSkinning();
            }
        }
        //去油光 degreasing
        if (TextUtils.equals(info.getName(), getResources().getString(R.string.quyouguang)) && (info instanceof CompoundFxInfo)) {
            FxParams param = ((CompoundFxInfo) info).findParam(ADVANCED_BEAUTY_MATTE_FILL_RADIUS);
            Double defaultLevel = param.getDefaultDoubleValue();
            double level = param.getDoubleValue();
            mSubSeekBar.setMax(100);
            mSubSeekBar.setBreakProgress(0);
            mSubSeekBar.setPointEnable(defaultLevel != 0);
            mSubSeekBar.setPointProgress(defaultLevel.intValue());
            mSubSeekBar.setProgress((int) ((level - 3) / 0.27));
        }

        if (TextUtils.equals(info.getType(), FxParams.SKIN_COLOUR)) {
            if (null != mBeautyEditListener) {
                mBeautyEditListener.removeAllSkinColour();
            }
        }
        applyBeautyFx(info);
    }

    /**
     * 应用美型
     * Applied beauty
     *
     * @param info info
     */
    private void applyBeautyFace(IFxInfo info) {
        Logger.e("meicam", "apply  face:" + info.getName() + "   strength:" + info.getStrength());
        double level;
        double floatVal = info.getStrength();
        if (floatVal >= 0) {
            level = (Math.round(floatVal * 100)) * 0.01;
        } else {
            level = -Math.round((Math.abs(floatVal) * 100)) * 0.01;
        }
        setProgressStrength(mBeautyType, (int) (level * 100 + 100), info);
        applyBeautyFx(info);
    }

    /**
     * 应用微整形
     * Applied microshaping
     *
     * @param info info
     */
    private void applyBeautySmall(IFxInfo info) {
        Logger.e("meicam", "apply  small:" + info.getName() + "   strength:" + info.getStrength());
        double level = info.getStrength();
        double defaultLevel = info.getDefaultStrength();
        if (info instanceof CompoundFxInfo) {
            //美型 shape
            level = (level >= 0)
                    ? (Math.round(level * 100)) * 0.01
                    : -Math.round((Math.abs(level) * 100)) * 0.01;
            defaultLevel = (defaultLevel >= 0)
                    ? (Math.round(level * 100)) * 0.01
                    : -Math.round((Math.abs(level) * 100)) * 0.01;
            setProgressStrength(mBeautyType, (int) (level * 100 + 100), info);
            mMainSeekBar.setPointProgress((int) (defaultLevel * 100 + 100));
        } else {
            //美颜 beauty
            setProgressStrength(mBeautyType, (int) (level * 100), info);
            mMainSeekBar.setPointProgress((int) (defaultLevel * 100));
        }
        applyBeautyFx(info);
    }

    /**
     * 应用调节
     * Apply Adjust
     *
     * @param info info
     */
    private void applyBeautyAdjust(IFxInfo info) {
        Logger.e("meicam", "apply  adjust:" + info.getName() + "   strength:" + info.getStrength());
        setProgressStrength(mBeautyType, (int) (info.getStrength() * 100), info);
        if ((null != mBeautyEditListener) && (info instanceof CompoundFxInfo)) {
            //锐度 sharpness
            if (TextUtils.equals(info.getName(), getResources().getString(R.string.sharpness))) {
                mBeautyEditListener.onBeautySharpen(info);
                return;
            }
            //清晰度 definition
            if (TextUtils.equals(info.getName(), getResources().getString(R.string.definition))) {
                mBeautyEditListener.onBeautyDefinition(info);
                return;
            }
        }
        applyBeautyFx(info);
    }

    /**
     * 应用单妆。美妆已应用，只需调节强度即可
     * Apply a single makeup. The makeup is applied, just adjust the intensity
     *
     * @param info info
     */
    private void applyBeautyMakeup(IFxInfo info) {
        Logger.e("meicam", "apply  makeup:" + info.getName() + "   strength:" + info.getStrength());
        setProgressStrength(mBeautyType, (int) (info.getStrength() * 100), info);
    }

    /**
     * 设置特效强度
     * Set effect intensity
     *
     * @param progress progress
     */
    private void setProgressChange(int progress) {
        if (null == mFxInfo) {
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_SKIN) {
            double strength = progress * 1.0 / 100;
            mFxInfo.setStrength(strength);
            if (TextUtils.equals(mFxInfo.getType(), FxParams.SKINNING)
                    || TextUtils.equals(mFxInfo.getType(), FxParams.SKIN_COLOUR)) {
                mBeautyFragment.changeNodeStrength(strength);
            } else {
                mBeautyFragment.changeStrength(strength);
            }
            applyBeautyFx(mFxInfo);
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_FACE) {
            double strength = (float) (progress - 100) / 100;
            mFxInfo.setStrength(strength);
            mBeautyFragment.changeStrength(strength);
            applyBeautyFx(mFxInfo);
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_SMALL) {
            double strength = (mFxInfo instanceof CompoundFxInfo)
                    ? ((float) (progress - 100) / 100) : (progress * 1.0 / 100);
            mFxInfo.setStrength(strength);
            mBeautyFragment.changeStrength(strength);
            applyBeautyFx(mFxInfo);
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_ADJUST) {
            double strength = progress * 1.0 / 100;
            mFxInfo.setStrength(strength);
            mBeautyFragment.changeStrength(strength);
            //锐度 sharpness
            if (TextUtils.equals(mFxInfo.getName(), getResources().getString(R.string.sharpness))) {
                mBeautyEditListener.onBeautySharpen(mFxInfo);
                return;
            }
            //清晰度 definition
            if (TextUtils.equals(mFxInfo.getName(), getResources().getString(R.string.definition))) {
                mBeautyEditListener.onBeautyDefinition(mFxInfo);
                return;
            }
            applyBeautyFx(mFxInfo);
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_CONTOURING) {
            double strength = progress * 1.0 / 100;
            mFxInfo.setStrength(strength);
            mBeautyFragment.changeStrength(strength);
            applyBeautyMakeupFx(mFxInfo);
        }
    }

    /**
     * 设置强度
     * Set strength
     *
     * @param beautyType type
     * @param strength   strength
     */
    private void setProgressStrength(int beautyType, int strength, IFxInfo info) {
        mPresenter.initSkinProgressBar(beautyType, mMainSeekBar, info);
        mMainSeekBar.setProgress(strength);
    }

    private void updateSubSeekBarView(IFxInfo info) {
        boolean isDegreasing = TextUtils.equals(info.getName(), mContext.getResources().getString(R.string.quyouguang));
        mSubSeekBar.setVisibility(isDegreasing ? View.VISIBLE : View.GONE);
        if (isDegreasing) {
            mSubSeekBar.setSeekTitle(mContext.getResources().getString(R.string.radius));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.beauty_edit_back) {
            if (null != mBeautyEditListener) {
                mBeautyEditListener.onBeautyEditBack();
            }
        } else if (id == R.id.beauty_edit_reset) {
            if (null == mTipsPop) {
                return;
            }
            mTipsPop.show();
        }
    }

    private void applyBeautyFx(IFxInfo info) {
        if (null == mBeautyEditListener) {
            return;
        }
        mBeautyEditListener.onBeautyApply(mBeautyType, info);
    }

    private void applyBeautyMakeupFx(IFxInfo info) {
        if (null == mBeautyEditListener) {
            return;
        }
        mBeautyEditListener.onBeautyMakeupApply(mBeautyType, info);
    }

    public void setBeautyEditListener(OnBeautyEditListener beautyEditListener) {
        mBeautyEditListener = beautyEditListener;
    }

    public interface OnBeautyEditListener {
        /**
         * 返回
         * Back
         */
        void onBeautyEditBack();

        /**
         * 美颜相关应用
         * Beauty related applications
         *
         * @param beautyType type
         * @param info       info
         */
        void onBeautyApply(int beautyType, IFxInfo info);

        /**
         * 锐度应用
         * Sharpness application
         *
         * @param info info
         */
        void onBeautySharpen(IFxInfo info);

        /**
         * 清晰度应用
         * Definition application
         *
         * @param info info
         */
        void onBeautyDefinition(IFxInfo info);

        /**
         * 移除所有的肤色效果
         * Remove all skin color effects
         */
        void removeAllSkinColour();

        /**
         * 重置磨皮效果。普通磨皮和高级磨皮是互斥的，需要上层处理，高级磨皮1，2，3在sdk内部是互斥的
         * Reset the dermabrasion effect. Normal and advanced peels are mutually exclusive and require upper level processing,
         * while advanced peels 1,2,3 are mutually exclusive inside the sdk
         */
        void resetAllSkinning();

        /**
         * 单妆应用
         * Single makeup application
         *
         * @param beautyType type
         * @param info       info
         */
        void onBeautyMakeupApply(int beautyType, IFxInfo info);
    }


}
