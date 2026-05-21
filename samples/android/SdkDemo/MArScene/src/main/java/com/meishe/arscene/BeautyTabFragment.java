package com.meishe.arscene;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.meicam.nvconvertorlib.Logger;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.inter.OnBeautyApplyListener;
import com.meishe.arscene.inter.OnBeautyListener;
import com.meishe.arscene.iview.BeautyTabView;
import com.meishe.arscene.presenter.BeautyTabPresenter;
import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.view.CustomViewPager;
import com.meishe.base.view.MagicProgress;
import com.meishe.third.tablayout.SlidingTabLayout;
import com.meishe.third.tablayout.listener.OnTabSelectListener;
import com.meishe.utils.DrawableUitls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_MATTE_FILL_RADIUS;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_MATTE_INTENSITY;
import static com.meishe.arscene.bean.FxParams.BEAUTY_WHITENING;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/15 18:33
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyTabFragment extends BaseMvpFragment<BeautyTabPresenter> implements View.OnClickListener, BeautyTabView {
    private static final String TAG = "meicam";
    private static final String BEAUTY_PARAMS = "beauty_params";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTabTitleList = new ArrayList<>();
    private String[] mBeautyArray;
    private ImageView mBeautyContrast;
    private LinearLayout mBeautyAbRoot;
    private Button mBeautyA;
    private Button mBeautyB;
    private SlidingTabLayout mBeautyTab;
    private ImageView mBeautyFinish;
    private CustomViewPager mBeautyPager;
    private LinearLayout mBeautyRadiusRoot;
    private MagicProgress mBeautyRadius;
    private LinearLayout mBeautyProgressRoot;
    private MagicProgress mBeautyProgress;
    private Switch mBeautySwitch;
    private Switch mBeautySubSwitch;
    private TextView mBeautyReset;
    private BeautyFxInfo mBeautyParams;
    private String mBeautyAB;
    private OnBeautyListener mOnBeautyListener;
    private int mBeautyType = FxParams.BEAUTY_SKIN;
    private IFxInfo mFxInfo;

    /**
     * 当前tab选中的页面
     * The page selected by the current tab
     */
    private BeautyFragment mBeautyFragment;

    private BeautyTabFragment() {
    }

    public static BeautyTabFragment create(BeautyFxInfo params) {
        BeautyTabFragment fragment = new BeautyTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(BEAUTY_PARAMS, params);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_beauty_tab;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        mBeautyContrast = rootView.findViewById(R.id.beauty_contrast);
        mBeautyAbRoot = rootView.findViewById(R.id.beauty_ab_root);
        mBeautyA = rootView.findViewById(R.id.beauty_a);
        mBeautyB = rootView.findViewById(R.id.beauty_b);
        mBeautyTab = rootView.findViewById(R.id.beauty_tab);
        mBeautyFinish = rootView.findViewById(R.id.beauty_finish);
        mBeautyPager = rootView.findViewById(R.id.beauty_pager);
        mBeautySubSwitch = rootView.findViewById(R.id.beauty_sub_switch);
        mBeautySwitch = rootView.findViewById(R.id.beauty_switch);
        mBeautyRadiusRoot = rootView.findViewById(R.id.beauty_radius_root);
        mBeautyRadius = rootView.findViewById(R.id.beauty_radius);
        mBeautyProgressRoot = rootView.findViewById(R.id.beauty_progress_root);
        mBeautyProgress = rootView.findViewById(R.id.beauty_progress);
        mBeautyReset = rootView.findViewById(R.id.beauty_reset);
        Drawable drawable = DrawableUitls.tintColor(getContext(), R.mipmap.icon_reset, Color.WHITE);
        if (null != drawable) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBeautyReset.setCompoundDrawables(drawable, null, null, null);
        }
        initSkinRadiusProgressBar();
        initViewPager();
        initListener();
    }

    @Override
    protected void initData() {
        if (null != getArguments()) {
            mBeautyParams = (BeautyFxInfo) getArguments().getSerializable(BEAUTY_PARAMS);
        }
        mPresenter.initFragment(mBeautyArray, mFragmentList, mTabTitleList);
        mBeautyTab.updateTitles(mTabTitleList);
        setBeautyFragment(0);
        updateBeautyAbView(FxParams.BEAUTY_B);
        mPresenter.getBeautySwitch(mBeautyType, mBeautyParams);
        mBeautyTab.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null == mBeautyParams) {
                    return;
                }
                if (null != mBeautyFragment) {
                    mBeautyFragment.setIsEnable(mBeautyParams.isOpenSkin());
                }
                if (TextUtils.equals(mBeautyParams.getBeautyType(), FxParams.BEAUTY_A)) {
                    updateBeautyAbView(FxParams.BEAUTY_A);
                }
                mPresenter.initBeautyData(getActivity(), mFragmentList, mBeautyParams);
            }
        }, 300);
    }

    private void initViewPager() {
        mBeautyPager.setOffscreenPageLimit(3);
        mBeautyPager.setNoScroll(true);
        mBeautyPager.setAdapter(new CommonFragmentAdapter(getChildFragmentManager(), mFragmentList, mTabTitleList));
        mBeautyTab.setViewPager(mBeautyPager);
        mBeautyArray = Arrays.copyOf(getResources().getStringArray(R.array.beauty_type), 3);
    }

    private void setBeautyFragment(int position) {
        if (mFragmentList.isEmpty() || (position > mFragmentList.size()) || (position < 0)) {
            return;
        }
        mBeautyFragment = (BeautyFragment) mFragmentList.get(position);
        initBeautyApplyListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mBeautyA.setOnClickListener(this);
        mBeautyB.setOnClickListener(this);
        mBeautyFinish.setOnClickListener(this);
        mBeautyReset.setOnClickListener(this);
        initBeautyApplyListener();
        mBeautyContrast.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mPresenter.contrastBeautyFx(mBeautyType, false, mBeautyParams);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPresenter.contrastBeautyFx(mBeautyType, true, mBeautyParams);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        mBeautyTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position > mBeautyArray.length) {
                    return;
                }
                mBeautySwitch.setText(mBeautyArray[position]);
                mBeautyType = FxParams.getBeautyType()[position];
                resetView();
                setBeautyFragment(position);
                //Tab切换时将adapter取消选中 When Tab is switched, the adapter is deselected
                if (!mFragmentList.isEmpty()) {
                    for (Fragment fragment : mFragmentList) {
                        if (null == fragment) {
                            continue;
                        }
                        if (fragment instanceof BeautyFragment) {
                            ((BeautyFragment) fragment).setSelectPosition(-1, -1);
                        }
                    }
                }
                mPresenter.getBeautySwitch(mBeautyType, mBeautyParams);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mBeautySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                resetView();
                switch (mBeautyType) {
                    case FxParams.BEAUTY_SKIN:
                        mBeautyParams.setOpenSkin(isChecked);
                        break;
                    case FxParams.BEAUTY_FACE:
                        mBeautyParams.setOpenFace(isChecked);
                        break;
                    case FxParams.BEAUTY_SMALL:
                        mBeautyParams.setOpenSmall(isChecked);
                        break;
                    default:
                        break;
                }
                if (null != mBeautyFragment) {
                    mBeautyFragment.setIsEnable(isChecked);
                    mPresenter.clearBeautyFx(mBeautyType, isChecked, mBeautyParams, mBeautyFragment.getBeautyData());
                }
            }
        });
        mBeautySubSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null == mFxInfo) {
                    return;
                }
                if (TextUtils.equals(mFxInfo.getName(), getResources().getString(R.string.correctionColor))) {
                    showProgress(isChecked);
                    if (null != mOnBeautyListener) {
                        IFxInfo info = new BaseFxInfo();
                        info.copy(mFxInfo);
                        info.setStrength(isChecked ? (int) (mFxInfo.getStrength() * 100) : 0);
                        mBeautyParams.setOpenAdjustColor(isChecked);
                        mOnBeautyListener.onBeautyApply(mBeautyType, info);
                    }
                    return;
                }
                if (TextUtils.equals(mFxInfo.getName(), getResources().getString(R.string.sharpness))) {
                    showProgress(false);
                    if (null != mOnBeautyListener) {
                        mBeautyParams.setOpenSharpen(isChecked);
                        mOnBeautyListener.onSharpenApply(isChecked);
                    }
                }
            }
        });
        mBeautyRadius.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
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
                    if (null != mOnBeautyListener) {
                        mOnBeautyListener.onBeautyApply(mBeautyType, mFxInfo);
                        mPresenter.recordBeautyData(getContext(), mBeautyParams, mFxInfo);
                    }
                }
            }
        });
        mBeautyProgress.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (fromUser) {
                    return;
                }
                setProgressChange(progress);
            }
        });
    }

    private void initBeautyApplyListener() {
        if (null == mBeautyFragment) {
            return;
        }
        mBeautyFragment.setBeautyApplyListener(new OnBeautyApplyListener() {
            @Override
            public void onBeautyApply(IFxInfo info) {
                switch (mBeautyType) {
                    case FxParams.BEAUTY_SKIN:
                        applyBeautySkin(info);
                        break;
                    case FxParams.BEAUTY_FACE:
                        applyBeautyFace(info);
                        break;
                    case FxParams.BEAUTY_SMALL:
                        applyBeautySmall(info);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBeautySwitch(boolean isChecked) {
        mBeautySwitch.setChecked(isChecked);
        if (null != mBeautyFragment) {
            mBeautyFragment.setIsEnable(isChecked);
        }
    }

    @Override
    public void onClearBeautyFx(int beautyType, IFxInfo info) {
        if (null == info) {
            return;
        }
        if (null != mOnBeautyListener) {
            mOnBeautyListener.onBeautyApply(beautyType, info);
        }
    }

    @Override
    public void onRecoverClearBeautyFx(int beautyType, IFxInfo info, boolean isApply) {
        if (isApply) {
            onClearBeautyFx(beautyType, info);
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
        resetView();
        if ((null != iFxInfos) && !iFxInfos.isEmpty()) {
            return;
        }
        Logger.e(TAG, "apply  skin:" + info.getName() + "   strength:" + info.getStrength());
        mFxInfo = info;
        mBeautyType = FxParams.BEAUTY_SKIN;
        showProgress(true);
        setProgressStrength(mBeautyType, (int) (info.getStrength() * 100));
        mPresenter.recordBeautyData(getContext(), mBeautyParams, info);
        //美白 whitening
        if (info.getName().startsWith(getResources().getString(R.string.whitening))) {
            showBeautyAb(true);
        }
        //去油光 degreasing
        if (TextUtils.equals(info.getName(), getResources().getString(R.string.quyouguang)) && (info instanceof CompoundFxInfo)) {
            showRadius(true);
            FxParams param = ((CompoundFxInfo) info).findParam(ADVANCED_BEAUTY_MATTE_FILL_RADIUS);
            Double defaultLevel = param.getDefaultDoubleValue();
            double level = param.getDoubleValue();
            mBeautyRadius.setPointEnable(defaultLevel != 0);
            mBeautyRadius.setPointProgress(defaultLevel.intValue());
            mBeautyRadius.setProgress((int) ((level - 3) / 0.27));
        }
        //校色 adjust color
        if (TextUtils.equals(info.getName(), getResources().getString(R.string.correctionColor))) {
            showSubSwitch(true);
            boolean isChecked = mBeautyParams.isOpenAdjustColor();
            showProgress(isChecked);
            mBeautySubSwitch.setChecked(isChecked);
            mBeautySubSwitch.setText(getResources().getString(R.string.correctionColor));
            if (!isChecked) {
                return;
            }
        }
        //锐度 sharpness
        if (TextUtils.equals(info.getName(), getResources().getString(R.string.sharpness))) {
            showProgress(false);
            showSubSwitch(true);
            mBeautySubSwitch.setChecked(mBeautyParams.isOpenSharpen());
            mBeautySubSwitch.setText(getResources().getString(R.string.sharpness));
            return;
        }
        if (null != mOnBeautyListener) {
            mOnBeautyListener.onBeautyApply(mBeautyType, info);
        }
    }

    /**
     * 应用美型
     * Applied beauty
     *
     * @param info info
     */
    private void applyBeautyFace(IFxInfo info) {
        resetView();
        Logger.e(TAG, "apply  face:" + info.getName() + "   strength:" + info.getStrength());
        mFxInfo = info;
        mBeautyType = FxParams.BEAUTY_FACE;
        showProgress(true);
        double level;
        double floatVal = info.getStrength();
        if (floatVal >= 0) {
            level = (Math.round(floatVal * 100)) * 0.01;
        } else {
            level = -Math.round((Math.abs(floatVal) * 100)) * 0.01;
        }
        setProgressStrength(mBeautyType, (int) (level * 100 + 100));
        if (null != mOnBeautyListener) {
            mOnBeautyListener.onBeautyApply(mBeautyType, info);
            mPresenter.recordBeautyData(getContext(), mBeautyParams, info);
        }
    }

    /**
     * 应用微整形
     * Applied microshaping
     *
     * @param info info
     */
    private void applyBeautySmall(IFxInfo info) {
        resetView();
        Logger.e(TAG, "apply  small:" + info.getName() + "   strength:" + info.getStrength());
        mFxInfo = info;
        mBeautyType = FxParams.BEAUTY_SMALL;
        showProgress(true);
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
            setProgressStrength(mBeautyType, (int) (level * 100 + 100));
            mBeautyProgress.setPointProgress((int) (defaultLevel * 100 + 100));
        } else {
            //美颜 beauty
            setProgressStrength(mBeautyType, (int) (level * 100));
            mBeautyProgress.setPointProgress((int) (defaultLevel * 100));
        }
        if (null != mOnBeautyListener) {
            mPresenter.recordBeautyData(getContext(), mBeautyParams, info);
            mOnBeautyListener.onBeautyApply(mBeautyType, info);
        }
    }

    /**
     * 设置特效强度
     * Set effect intensity
     *
     * @param progress progress
     */
    private void setProgressChange(int progress) {
        if (null == mBeautyFragment) {
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_SKIN) {
            double strength = progress * 1.0 / 100;
            mFxInfo.setStrength(strength);
            if (mFxInfo.getName().startsWith(getResources().getString(R.string.strength))) {
                mBeautyFragment.changeNodeStrength(strength);
            } else {
                mBeautyFragment.changeStrength(strength);
            }
            if (null != mOnBeautyListener) {
                mOnBeautyListener.onBeautyApply(mBeautyType, mFxInfo);
                mPresenter.recordBeautyData(getContext(), mBeautyParams, mFxInfo);
            }
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_FACE) {
            double strength = (float) (progress - 100) / 100;
            mFxInfo.setStrength(strength);
            mBeautyFragment.changeStrength(strength);
            if (null != mOnBeautyListener) {
                mOnBeautyListener.onBeautyApply(mBeautyType, mFxInfo);
                mPresenter.recordBeautyData(getContext(), mBeautyParams, mFxInfo);
            }
            return;
        }
        if (mBeautyType == FxParams.BEAUTY_SMALL) {
            double strength = (mFxInfo instanceof CompoundFxInfo)
                    ? ((float) (progress - 100) / 100) : (progress * 1.0 / 100);
            mFxInfo.setStrength(strength);
            mBeautyFragment.changeStrength(strength);
            if (null != mOnBeautyListener) {
                mOnBeautyListener.onBeautyApply(mBeautyType, mFxInfo);
                mPresenter.recordBeautyData(getContext(), mBeautyParams, mFxInfo);
            }
        }
    }

    private void resetView() {
        showRadius(false);
        showProgress(false);
        showBeautyAb(false);
        showSubSwitch(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.beauty_a) {
            updateBeautyAbView(FxParams.BEAUTY_A);
            changeWhiteningMode(true);
        } else if (id == R.id.beauty_b) {
            updateBeautyAbView(FxParams.BEAUTY_B);
            changeWhiteningMode(false);
        } else if (id == R.id.beauty_reset) {
            mPresenter.resetBeautyFx(mBeautyType, mBeautyParams, mBeautyFragment.getBeautyData());
            resetView();
            if (null != mBeautyFragment) {
                mBeautyFragment.setSelectPosition(-1, -1);
            }
        } else if (id == R.id.beauty_finish) {
            if (null != mOnBeautyListener) {
                mOnBeautyListener.onBeautyFinish(mBeautyParams);
            }
        }
    }

    /**
     * 更新美白AB view
     * Renewal Whitening AB view
     *
     * @param type type
     */
    private void updateBeautyAbView(String type) {
        if (TextUtils.equals(type, mBeautyAB)) {
            return;
        }
        mBeautyAB = type;
        if (TextUtils.equals(type, FxParams.BEAUTY_A)) {
            mBeautyA.setSelected(true);
            mBeautyB.setSelected(false);
        } else {
            mBeautyA.setSelected(false);
            mBeautyB.setSelected(true);
        }
        if (null != mBeautyFragment) {
            mBeautyFragment.changeFxName(
                    getResources().getString((TextUtils.equals(type, FxParams.BEAUTY_A))
                            ? R.string.whitening_A : R.string.whitening_B));
        }
    }

    /**
     * 是否显示强度
     * Show strength or not
     *
     * @param isShow isShow
     */
    private void showProgress(boolean isShow) {
        mBeautyProgressRoot.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 初始化强度
     * Initial strength
     *
     * @param beautyType type
     */
    private void initSkinProgressBar(int beautyType) {
        switch (beautyType) {
            case FxParams.BEAUTY_SKIN:
                //美肤范围[0,1] Beauty range[0,1]
                mBeautyProgress.setMax(100);
                mBeautyProgress.setPointEnable(false);
                mBeautyProgress.setBreakProgress(0);
                break;
            case FxParams.BEAUTY_FACE:
                //美型范围[-1,1] Range of beauty[-1,1]
                mBeautyProgress.setMax(200);
                mBeautyProgress.setPointEnable(true);
                mBeautyProgress.setBreakProgress(100);
                mBeautyProgress.setPointProgress(100);
                break;
            case FxParams.BEAUTY_SMALL:
                //微整形范围[-1,1]和[0,1](法令纹，黑眼圈，亮眼，美牙) Micro-shaping range [-1,1] and [0,1](regular lines, dark circles, bright eyes, beautiful teeth)
                mBeautyProgress.setMax(100);
                mBeautyProgress.setPointEnable(false);
                mBeautyProgress.setBreakProgress(0);
                mBeautyProgress.setPointProgress(0);
                if ((null != mFxInfo) && (mFxInfo instanceof CompoundFxInfo)) {
                    mBeautyProgress.setMax(200);
                    mBeautyProgress.setPointEnable(true);
                    mBeautyProgress.setBreakProgress(100);
                    mBeautyProgress.setPointProgress(100);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置强度
     * Set strength
     *
     * @param beautyType type
     * @param strength   strength
     */
    private void setProgressStrength(int beautyType, int strength) {
        initSkinProgressBar(beautyType);
        mBeautyProgress.setProgress(strength);
    }


    /**
     * 是否显示美白AB
     * Whether to show whitening AB
     *
     * @param isShow isShow
     */
    private void showBeautyAb(boolean isShow) {
        mBeautyAbRoot.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 美白AB模式切换
     * Whitening AB mode switch
     *
     * @param showWhiteningA 是否是美白A
     */
    private void changeWhiteningMode(boolean showWhiteningA) {
        if ((null == mBeautyFragment)
                || (null == mFxInfo)
                || !TextUtils.equals(mFxInfo.getFxName(), BEAUTY_WHITENING)) {
            return;
        }
        IFxInfo info;
        if (showWhiteningA) {
            info = BeautyDataManager.getWhiteningA(getContext());
        } else {
            info = BeautyDataManager.getWhiteningB(getContext());
        }
        info.setStrength(mFxInfo.getStrength());
        mBeautyFragment.updateFxInfo(info);
        mBeautyParams.setBeautyType(showWhiteningA ? FxParams.BEAUTY_A : FxParams.BEAUTY_B);
        if (null != mOnBeautyListener) {
            mOnBeautyListener.onBeautyApply(mBeautyType, info);
            mPresenter.recordBeautyData(getContext(), mBeautyParams, info);
        }
    }

    /**
     * 是否显示半径
     * Show radius or not
     *
     * @param isShow isShow
     */
    private void showRadius(boolean isShow) {
        mBeautyRadiusRoot.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 初始化半径
     * Initialization radius
     */
    private void initSkinRadiusProgressBar() {
        mBeautyRadius.setMax(100);
        mBeautyRadius.setPointEnable(true);
        mBeautyRadius.setBreakProgress(0);
    }

    /**
     * 是否显示sub switch  如校色，锐度
     * Whether to display sub switch such as color calibration, sharpness
     *
     * @param isShow isShow
     */
    private void showSubSwitch(boolean isShow) {
        mBeautySubSwitch.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setOnBeautyListener(OnBeautyListener onBeautyListener) {
        mOnBeautyListener = onBeautyListener;
    }
}
