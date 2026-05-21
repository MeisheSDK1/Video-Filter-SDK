package com.meishe.sdkdemo.capture.fragment.beauty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.meicam.sdk.NvsFx;
import com.meishe.arscene.BeautyHelper;
import com.meishe.arscene.CustomBeautyHelper;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.template.BeautyEditTabFragment;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FragmentUtils;
import com.meishe.makeup.MakeupHelper;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.bean.BeautyTemplateInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/2/9 14:42
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyCaptureFragment extends BaseMvpFragment<BeautyCapturePresenter> implements BeautyCaptureView {
    private final BeautyHelper mBeautyHelper;
    private final MakeupHelper mMakeupHelper;
    private final List<BeautyTemplateInfo> assetsBeautyTemplates = new ArrayList<>();
    private Context mContext;
    private ImageView mContrast;
    private BeautyTemplateFragment templateFragment;
    private BeautyEditTabFragment editTabFragment;
    private final List<Fragment> mFragments = new ArrayList<>();
    /**
     * 是否支持去油光
     * Whether it supports degreasing
     */
    private final boolean mIsSupportMatte;
    private HashMap<Integer, List<IFxInfo>> mMapBeautyDatas;
    private HashMap<Integer, List<IFxInfo>> mCustomBeautyDatas;


    private MakeupTemplateChangedListener mMakeupTemplateChangedListener;
    private boolean mIsCustomTemplateNow;

    public void setMakeupTemplateChangedListener(MakeupTemplateChangedListener listener) {
        this.mMakeupTemplateChangedListener = listener;
    }

    public static BeautyCaptureFragment newInstance(boolean isSupportMatte, MakeupHelper makeupHelper, BeautyHelper beautyHelper) {
        CustomBeautyHelper.get().clearData();
        return new BeautyCaptureFragment(isSupportMatte, makeupHelper, beautyHelper);
    }

    public BeautyCaptureFragment(boolean isSupportMatte, MakeupHelper makeupHelper, BeautyHelper beautyHelper) {
        mIsSupportMatte = isSupportMatte;
        mMakeupHelper = makeupHelper;
        mBeautyHelper = beautyHelper;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override

    protected int bindLayout() {
        return R.layout.fragment_capture_beauty;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CustomBeautyHelper.get().clearData();
    }

    @Override
    protected void initView(View rootView) {
        mContrast = rootView.findViewById(R.id.capture_contrast);
        templateFragment = BeautyTemplateFragment.newInstance();
        templateFragment.setAssetsData(assetsBeautyTemplates);
        editTabFragment = BeautyEditTabFragment.newInstance(mIsSupportMatte);
        mFragments.add(templateFragment);
        mFragments.add(editTabFragment);
        FragmentUtils.add(
                getChildFragmentManager(),
                mFragments,
                R.id.beauty_fragment_container,
                new String[]{"BeautyTemplate", "BeautyEdit"}
                , 0);
        initListener();
    }

    public void initAssetsData(Context context, boolean isDefaultArScene) {
        initPresenter();
        assetsBeautyTemplates.clear();
        mPresenter.getBeautyTemplateFromAssets(context, assetsBeautyTemplates);
        if (assetsBeautyTemplates.isEmpty()) {
            return;
        }
        if (!isDefaultArScene) {
            return;
        }
        BeautyTemplateInfo info = assetsBeautyTemplates.get(0);
        Makeup makeupAssets = info.getMakeup();
        if (null == makeupAssets) {
            return;
        }
        mPresenter.applyBeautyTemplate(context, mMakeupHelper, mBeautyHelper, info);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mContrast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                HashMap<Integer, List<IFxInfo>> beautyDatas = mIsCustomTemplateNow ? mCustomBeautyDatas : mMapBeautyDatas;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mPresenter.resetBeautyTemplate(mContext, beautyDatas, mMakeupHelper, mBeautyHelper, true);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPresenter.resetBeautyTemplate(mContext, beautyDatas, mMakeupHelper, mBeautyHelper, false);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        templateFragment.setTemplateListener(new BeautyTemplateFragment.OnBeautyTemplateListener() {
            @Override
            public void onEditTemplate() {
                FragmentUtils.showHide(1, mFragments);
                editTabFragment.initChildFragmentData(mCustomBeautyDatas != null ? mCustomBeautyDatas : mMapBeautyDatas);
            }

            @Override
            public void onApplyTemplate(BeautyTemplateInfo templateInfo) {
                mPresenter.resetBeautyTemplate(mContext, mMapBeautyDatas, mMakeupHelper, mBeautyHelper, true);
                if (null == templateInfo) {
                    if (mMakeupTemplateChangedListener != null) {
                        mMakeupTemplateChangedListener.onMakeupTemplateDeleted(mMapBeautyDatas);
                    }
                    if (null != mMapBeautyDatas) {
                        mMapBeautyDatas.clear();
                    }
                    return;
                }
                boolean isCustomTemplate = templateInfo.getTemplateType() == BeautyTemplateInfo.BeautyTemplateType.BeautyTemplate_Custom;
                if (isCustomTemplate) {
                    mPresenter.applyBeautyTemplate(mBeautyHelper);
                }
                mPresenter.applyBeautyTemplate(mContext, mMakeupHelper, mBeautyHelper, templateInfo);
            }
        });
        editTabFragment.setBeautyEditListener(new BeautyEditTabFragment.OnBeautyEditListener() {
            @Override
            public void onBeautyEditBack() {
                CustomBeautyHelper.get().setCustomMode(false);
                if ((null != templateFragment)
                        && (templateFragment.isApplyCustomTemplate())
                        && (null != mCustomBeautyDatas)
                        && (!mCustomBeautyDatas.isEmpty())) {
                    templateFragment.updateCustomTemplatePointView(mPresenter.getUserChangeTemplateStrength(mCustomBeautyDatas));
                }
                FragmentUtils.showHide(0, mFragments);
            }

            @Override
            public void onBeautyApply(int beautyType, IFxInfo info) {
                if (null != mBeautyHelper) {
                    mBeautyHelper.applyCaptureBeautyFx(info);
                }
            }

            @Override
            public void onBeautySharpen(IFxInfo info) {
                if (null != mBeautyHelper) {
                    mBeautyHelper.applyCaptureSharpenFx(info);
                }
            }

            @Override
            public void onBeautyDefinition(IFxInfo info) {
                if (null != mBeautyHelper) {
                    mBeautyHelper.applyCaptureDefinitionFx(info);
                }
            }

            @Override
            public void removeAllSkinColour() {
                mPresenter.removaAllSkinColour(mContext, mBeautyHelper);
            }

            @Override
            public void resetAllSkinning() {
                if (null != mBeautyHelper) {
                    NvsFx nvsFx = mBeautyHelper.getCaptureBeautyFx();
                    if (null == nvsFx) {
                        return;
                    }
                    nvsFx.setFloatVal(FxParams.BEAUTY_STRENGTH, 0);
                    nvsFx.setFloatVal(FxParams.ADVANCED_BEAUTY_INTENSITY, 0);
                }
            }

            @Override
            public void onBeautyMakeupApply(int beautyType, IFxInfo info) {
                if (null != mMakeupHelper) {
                    mMakeupHelper.setCaptureMakeupIntensity(info.getFxName(), (float) info.getStrength());
                }
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    public void getBeautyData(HashMap<Integer, List<IFxInfo>> mapDatas, boolean isCustomTemplate) {
        mIsCustomTemplateNow = isCustomTemplate;
        if (isCustomTemplate && mCustomBeautyDatas == null) {
            //这里只进行一次初始化 Only one initialization is performed here.
            mCustomBeautyDatas = mapDatas;
        } else {
            mMapBeautyDatas = mapDatas;
        }
    }

    public void checkBeautyTemplate() {
        if (null == templateFragment) {
            return;
        }
        if (templateFragment.checkIsApplyTemplate()) {
            return;
        }
        if (null == mCustomBeautyDatas || mCustomBeautyDatas.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, List<IFxInfo>> entry : mCustomBeautyDatas.entrySet()) {
            List<IFxInfo> value = entry.getValue();
            if (CommonUtils.isEmpty(value)) {
                continue;
            }
            for (IFxInfo fxInfo : value) {
                List<IFxInfo> nodeIFxInfo = fxInfo.getFxNodes();
                if (!CommonUtils.isEmpty(nodeIFxInfo)) {
                    for (IFxInfo nodeFxInfo : nodeIFxInfo) {
                        if (nodeFxInfo.getStrength() == 0) {
                            continue;
                        }
                        mBeautyHelper.applyCaptureBeautyFx(nodeFxInfo);
                    }
                    continue;
                }
                if (fxInfo.getStrength() == 0) {
                    continue;
                }
                mBeautyHelper.applyCaptureBeautyFx(fxInfo);
            }
        }
    }

    public interface MakeupTemplateChangedListener {
        /**
         * On makeup template changed listener.
         * 美颜模板数据变化回调
         *
         * @param makeupMap the makeup map
         */
        void onMakeupTemplateDeleted(HashMap<Integer, List<IFxInfo>> makeupMap);

    }
}
