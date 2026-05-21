package com.meishe.arscene.presenter;

import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.meishe.arscene.BeautyDataManager;
import com.meishe.arscene.BeautyFragment;
import com.meishe.arscene.R;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.iview.BeautyTabView;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/15 19:30
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BeautyTabPresenter extends Presenter<BeautyTabView> {
    /**
     * 初始化页面
     * Initialize page
     *
     * @param mBeautyArray  array
     * @param mFragmentList tabs
     * @param mTabTitleList tab
     */
    public void initFragment(String[] mBeautyArray, List<Fragment> mFragmentList, List<String> mTabTitleList) {
        if (mBeautyArray.length == 0) {
            return;
        }
        for (int i = 0; i < mBeautyArray.length; i++) {
            mFragmentList.add(BeautyFragment.create(FxParams.getBeautyType()[i]));
            mTabTitleList.add(mBeautyArray[i]);
        }
    }

    /**
     * 初始化美颜数据
     * Initialize the beauty data
     *
     * @param context       context
     * @param mFragmentList fragments
     * @param mBeautyParams Params
     */
    public void initBeautyData(Context context, List<Fragment> mFragmentList, BeautyFxInfo mBeautyParams) {
        if ((null == context) || (null == mFragmentList) || (null == mBeautyParams)) {
            return;
        }
        HashSet<IFxInfo> mData = mBeautyParams.getBeautys();
        if ((null == mData) || mData.isEmpty()) {
            return;
        }
        for (int i = 0; i < mFragmentList.size(); i++) {
            Fragment fragment = mFragmentList.get(i);
            if (null == fragment) {
                continue;
            }
            if (fragment instanceof BeautyFragment) {
                BeautyFragment beautyFragment = (BeautyFragment) fragment;
                List<IFxInfo> infoData = beautyFragment.getBeautyData();
                if (null == infoData) {
                    continue;
                }
                if ((i == 0) && TextUtils.equals(mBeautyParams.getBeautyType(), FxParams.BEAUTY_A)) {
                    int index = -1;
                    for (int j = 0; j < infoData.size(); j++) {
                        String name = infoData.get(j).getName();
                        if (name.startsWith(context.getString(R.string.whitening))) {
                            index = j;
                            break;
                        }
                    }
                    IFxInfo infoA = BeautyDataManager.getWhiteningA(context);
                    beautyFragment.updateFxInfo(index, infoA);
                }
                setDefaultBeautyData(infoData, mData);
            }
        }
    }

    private void setDefaultBeautyData(List<IFxInfo> infoData, HashSet<IFxInfo> mData) {
        for (IFxInfo info : infoData) {
            String name = info.getName();
            for (IFxInfo next : mData) {
                if (null == next) {
                    continue;
                }
                if (TextUtils.equals(name, next.getName())) {
                    info.setStrength(next.getStrength());
                    info.setCanReplace(next.canReplace());
                    info.setDefaultStrength(next.getStrength());
                    break;
                }
            }
            List<IFxInfo> nodes = info.getFxNodes();
            if ((null != nodes) && !nodes.isEmpty()) {
                setDefaultBeautyData(nodes, mData);
            }
        }
    }

    /**
     * 获取美型是否开启
     * Gets whether beauty type is enabled
     *
     * @param mBeautyType   type
     * @param mBeautyParams params
     */
    public void getBeautySwitch(int mBeautyType, BeautyFxInfo mBeautyParams) {
        if (null == mBeautyParams) {
            return;
        }
        boolean isChecked = false;
        switch (mBeautyType) {
            case FxParams.BEAUTY_SKIN:
                isChecked = mBeautyParams.isOpenSkin();
                break;
            case FxParams.BEAUTY_FACE:
                isChecked = mBeautyParams.isOpenFace();
                break;
            case FxParams.BEAUTY_SMALL:
                isChecked = mBeautyParams.isOpenSmall();
                break;
            default:
                break;
        }
        getView().onBeautySwitch(isChecked);
    }

    /**
     * 清除美颜特效
     * Clear beauty effects
     *
     * @param beautyType    类型 type
     * @param isChecked     是否清除 Clear or not
     * @param mBeautyParams 选中的特效集合 The selected effects set
     * @param iFxInfos      对应的美颜特效列表数据 Corresponding beauty effects list data
     */
    public void clearBeautyFx(int beautyType, boolean isChecked, BeautyFxInfo mBeautyParams, List<IFxInfo> iFxInfos) {
        if ((null == iFxInfos) || iFxInfos.isEmpty()) {
            return;
        }
        if (isChecked) {
            HashSet<IFxInfo> mDatas = mBeautyParams.getBeautys();
            if ((null == mDatas) || mDatas.isEmpty()) {
                return;
            }
            for (IFxInfo info : mDatas) {
                if (null == info) {
                    continue;
                }
                getView().onRecoverClearBeautyFx(beautyType, info, isExistByDatas(info, iFxInfos));
            }
            return;
        }
        List<IFxInfo> tempData = new ArrayList<>();
        for (IFxInfo info : iFxInfos) {
            if (null == info) {
                continue;
            }
            List<IFxInfo> nodeInfos = info.getFxNodes();
            if ((null != nodeInfos) && !nodeInfos.isEmpty()) {
                for (IFxInfo nodeInfo : nodeInfos) {
                    if (null == nodeInfo) {
                        continue;
                    }
                    tempData.add(copyFx(nodeInfo));
                }
                continue;
            }
            tempData.add(copyFx(info));
        }
        for (IFxInfo info : tempData) {
            if (null == info) {
                continue;
            }
            info.setStrength(0);
            getView().onClearBeautyFx(beautyType, info);
        }
    }

    /**
     * 对比美颜特效
     * Compare the beauty effects
     *
     * @param beautyType    类型 type
     * @param isChecked     是否清除 Clear or not
     * @param mBeautyParams 选中的特效集合 The selected effects set
     */
    public void contrastBeautyFx(int beautyType, boolean isChecked, BeautyFxInfo mBeautyParams) {
        if (null == mBeautyParams) {
            return;
        }
        HashSet<IFxInfo> mDatas = mBeautyParams.getBeautys();
        if ((null == mDatas) || mDatas.isEmpty()) {
            return;
        }
        if (isChecked) {
            for (IFxInfo info : mDatas) {
                if (null == info) {
                    continue;
                }
                getView().onRecoverClearBeautyFx(beautyType, info, true);
            }
            return;
        }
        List<IFxInfo> tempData = new ArrayList<>();
        for (IFxInfo info : mDatas) {
            if (null == info) {
                continue;
            }
            tempData.add(copyFx(info));
        }
        for (IFxInfo info : tempData) {
            if (null == info) {
                continue;
            }
            info.setStrength(0);
            getView().onClearBeautyFx(beautyType, info);
        }
    }

    private IFxInfo copyFx(IFxInfo info) {
        IFxInfo newInfo = new BaseFxInfo();
        if (info instanceof CompoundFxInfo) {
            newInfo = new CompoundFxInfo();
        }
        newInfo.copy(info);
        return newInfo;
    }

    /**
     * 重置美颜特效
     * Reset the beauty effects
     *
     * @param beautyType    type
     * @param mBeautyParams 当前应用的所有美颜 All the beauty currently applied
     * @param iFxInfos      美颜列表数据 Beauty list data
     */
    public void resetBeautyFx(int beautyType, BeautyFxInfo mBeautyParams, List<IFxInfo> iFxInfos) {
        HashSet<IFxInfo> mDatas = mBeautyParams.getBeautys();
        if ((null == mDatas) || mDatas.isEmpty()) {
            return;
        }
        for (IFxInfo info : mDatas) {
            if (null == info) {
                continue;
            }
            for (IFxInfo iFxInfo : iFxInfos) {
                if (null == iFxInfo) {
                    continue;
                }
                List<IFxInfo> nodeInfos = iFxInfo.getFxNodes();
                if ((null != nodeInfos) && !nodeInfos.isEmpty()) {
                    for (IFxInfo nodeInfo : nodeInfos) {
                        if (null == nodeInfo) {
                            continue;
                        }
                        resetFx(beautyType, info, nodeInfo);
                    }
                    continue;
                }
                resetFx(beautyType, info, iFxInfo);
            }
        }
    }

    public void resetFx(int beautyType, IFxInfo info, IFxInfo iFxInfo) {
        if ((null == info) || (null == iFxInfo)) {
            return;
        }
        if (TextUtils.equals(info.getName(), iFxInfo.getName())) {
            iFxInfo.setStrength(0);
            info.setStrength(0);
            getView().onClearBeautyFx(beautyType, info);
        }
    }

    /**
     * 记录选择的美型数据
     * Record selected beauty data
     *
     * @param mBeautyParams params
     * @param iFxInfo       info
     */
    public void recordBeautyData(Context context, BeautyFxInfo mBeautyParams, IFxInfo iFxInfo) {
        if ((null == mBeautyParams) || (null == iFxInfo)) {
            return;
        }
        HashSet<IFxInfo> mData = mBeautyParams.getBeautys();
        if (null == mData) {
            mData = new HashSet<>();
            mBeautyParams.setBeautys(mData);
        }
        boolean isExist = false;
        IFxInfo isReplaceTempFx = null;
        for (IFxInfo info : mData) {
            if (null == info) {
                continue;
            }
            if (TextUtils.equals(iFxInfo.getName(), info.getName())) {
                isExist = true;
                info.setStrength(iFxInfo.getStrength());
                break;
            }
            if (hasSpecialFxInfo(context, info, iFxInfo)) {
                isReplaceTempFx = info;
                break;
            }
        }
        if (null != isReplaceTempFx) {
            //移除已存在的 Remove existing ones
            mData.remove(isReplaceTempFx);
            //记录新的 Record new
            mData.add(iFxInfo);
            isReplaceTempFx = null;
            return;
        }
        if (!isExist) {
            mData.add(iFxInfo);
        }
    }

    /**
     * 是否包含子特效
     * 如磨皮包含磨皮1，2，3，4，只能选择记录一个。美白AB也是
     * Whether subeffects are included
     * If the dermabrasion contains dermabrasion 1,2,3,4, only one can be selected. Whitening AB also
     *
     * @param context    context
     * @param listInfo   显示列表中的fx Displays fx in the list
     * @param recordInfo 要记录的fx fx to record
     * @return boolean
     */
    private boolean hasSpecialFxInfo(Context context, IFxInfo listInfo, IFxInfo recordInfo) {
        if ((null == context) || (null == listInfo) || (null == recordInfo)) {
            return false;
        }
        //磨皮 skinning
        String mBuffingkin = context.getResources().getString(R.string.strength);
        if (listInfo.getName().startsWith(mBuffingkin) && recordInfo.getName().startsWith(mBuffingkin)) {
            return true;
        }
        //美白AB Whitening AB
        String mWhitening = context.getResources().getString(R.string.whitening);
        if (listInfo.getName().startsWith(mWhitening) && recordInfo.getName().startsWith(mWhitening)) {
            return true;
        }
        return false;
    }

    /**
     * 判断目标特效是否在列表数据中
     * Determines if the target effect is in the list data
     *
     * @param targetFx 目标特效 Target effect
     * @param iFxInfos 列表数据 List data
     * @return boolean
     */
    private boolean isExistByDatas(IFxInfo targetFx, List<IFxInfo> iFxInfos) {
        boolean isExist = false;
        if ((null == targetFx) || (null == iFxInfos) || (iFxInfos.isEmpty())) {
            return false;
        }
        for (IFxInfo info : iFxInfos) {
            if (null == info) {
                continue;
            }
            List<IFxInfo> nodeInfos = info.getFxNodes();
            if (!CommonUtils.isEmpty(nodeInfos)) {
                for (IFxInfo nodeInfo : nodeInfos) {
                    if (null == nodeInfo) {
                        continue;
                    }
                    if (TextUtils.equals(targetFx.getType(), nodeInfo.getType())) {
                        isExist = true;
                        break;
                    }
                }
                continue;
            }
            if (TextUtils.equals(targetFx.getName(), info.getName())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }
}
