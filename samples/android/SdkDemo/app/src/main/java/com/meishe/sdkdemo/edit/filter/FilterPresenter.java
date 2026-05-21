package com.meishe.sdkdemo.edit.filter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsExpressionParam;
import com.meicam.sdk.NvsTimeline;
import com.meishe.base.model.Presenter;
import com.meishe.sdkdemo.bean.AdjustSpecialEffectsInfo;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.utils.AssetFxUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/22 11:01
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class FilterPresenter extends Presenter<FiltersView> {
    /**
     * 创建timeline
     * Create Timeline
     *
     * @return NvsTimeline
     */
    public NvsTimeline createTimeline() {
        NvsTimeline timeline = TimelineUtil.createTimeline();
        if (timeline == null) {
            return null;
        }
        TimelineUtil.applyTheme(timeline, null);
        /*
         * 移除主题，则需要删除字幕，然后重新添加，防止带片头主题删掉字幕
         * To remove a topic, you need to delete the subtitle and then add it again to prevent the title from deleting the subtitle
         * */
        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        TimelineUtil.setCaption(timeline, captionArray);
        return timeline;
    }

    /**
     * 加载滤镜数据
     * Load the filter data
     *
     * @param context context
     */
    public void getFilterData(Context context) {
        ArrayList<FilterItem> mFilterData = AssetFxUtil.getFilterData(context,
                getLocalData(),
                null,
                true,
                false);
        getView().onFilterData(mFilterData);

    }

    /**
     * 获取本地滤镜数据
     * Get the local filter data
     *
     * @return data
     */
    private ArrayList<NvAsset> getLocalData() {
        return NvAssetManager.sharedInstance().getUsableAssets(NvAsset.ASSET_FILTER, NvAsset.AspectRatio_All, 0);
    }

    /**
     * 获取滤镜当前选择位置
     * Get the current selected position of the filter
     */
    public int getSelectedFilterPos(ArrayList<FilterItem> filterDataArrayList, FilterItem filterItem) {
        if ((null == filterDataArrayList) || filterDataArrayList.isEmpty()) {
            return -1;
        }
        if (null == filterItem) {
            return 0;
        }
        String fxName = filterItem.getPackageId();
        if (TextUtils.isEmpty(fxName)) {
            fxName = filterItem.getFilterName();
        }
        if (TextUtils.isEmpty(fxName)) {
            return 0;
        }
        for (int i = 0; i < filterDataArrayList.size(); i++) {
            FilterItem newFilterItem = filterDataArrayList.get(i);
            if (newFilterItem == null) {
                continue;
            }

            int filterMode = newFilterItem.getFilterMode();
            String filterName;
            if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                filterName = newFilterItem.getFilterName();
            } else {
                filterName = newFilterItem.getPackageId();
            }

            if (fxName.equals(filterName)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 可调节滤镜设置
     * Adjustable filter Settings
     *
     * @param packageId    packageId
     * @param expValueList expValueList
     * @param hashtable    hashtable
     * @param mFilterItem  mFilterItem
     * @return List
     */
    public List<AdjustSpecialEffectsInfo> setAdjustFilter(String packageId
            , List<NvsExpressionParam> expValueList
            , Hashtable<String, String> hashtable
            , FilterItem mFilterItem) {
        List<AdjustSpecialEffectsInfo> adjustData = new ArrayList<>();
        AdjustSpecialEffectsInfo adjustSpecialEffectsInfo = null;
        String name;
        String key;
        for (NvsExpressionParam nvsExpressionParam : expValueList) {
            int type = nvsExpressionParam.getType();
            key = nvsExpressionParam.getName();
            name = hashtable.get(key);
            adjustSpecialEffectsInfo = new AdjustSpecialEffectsInfo();
            adjustSpecialEffectsInfo.setPackageId(packageId);
            adjustSpecialEffectsInfo.setAdjustmentCategoryName(name);
            adjustSpecialEffectsInfo.setKey(key);
            adjustSpecialEffectsInfo.setType(type);
            switch (type) {
                case NvsExpressionParam.TYPE_COLOR:
                    NvsColor nvsColor = nvsExpressionParam.getColor();
                    if (null != nvsColor) {
                        adjustSpecialEffectsInfo.setColor(nvsColor);
                        mFilterItem.setFilterColorParam(key, nvsColor);
                    }
                    break;
                case NvsExpressionParam.TYPE_FLOAT:
                    NvsExpressionParam.FloatParam floatParam = nvsExpressionParam.getFloatParam();
                    if (null != floatParam) {
                        adjustSpecialEffectsInfo.setDefVal(floatParam.getDefVal());
                        adjustSpecialEffectsInfo.setMaxVal(floatParam.getMaxVal());
                        adjustSpecialEffectsInfo.setMinVal(floatParam.getMinVal());
                        adjustSpecialEffectsInfo.setStrength(floatParam.getDefVal());
                        mFilterItem.setFilterParam(adjustSpecialEffectsInfo.getKey(), adjustSpecialEffectsInfo.getStrength());
                    }
                    break;
                case NvsExpressionParam.TYPE_INT:
                    NvsExpressionParam.IntParam intParam = nvsExpressionParam.getIntParam();
                    if (null != intParam) {
                        adjustSpecialEffectsInfo.setDefVal(intParam.getDefVal());
                        adjustSpecialEffectsInfo.setMaxVal(intParam.getMaxVal());
                        adjustSpecialEffectsInfo.setMinVal(intParam.getMinVal());
                        adjustSpecialEffectsInfo.setStrength(intParam.getDefVal());
                        mFilterItem.setFilterParam(adjustSpecialEffectsInfo.getKey(), adjustSpecialEffectsInfo.getStrength());
                    }
                    break;
                case NvsExpressionParam.TYPE_BOOLEAN:
                    break;
                default:
                    break;
            }
            adjustData.add(adjustSpecialEffectsInfo);
        }
        return adjustData;
    }

    public void changeAdjustIntensity(AdjustSpecialEffectsInfo info, FilterItem mFilterItem, int progressOrColor, boolean isChangeColor) {
        if ((null == info) || (null == mFilterItem)) {
            return;
        }
        if (isChangeColor) {
            float alphaF = (Color.alpha(progressOrColor) * 1.0f / 255f);
            float red = (Color.red(progressOrColor) * 1.0f / 255f);
            float green = (Color.green(progressOrColor) * 1.0f / 255f);
            float blue = (Color.blue(progressOrColor) * 1.0f / 255f);
            info.setColor(new NvsColor(red, green, blue, alphaF));
            mFilterItem.setFilterColorParam(info.getKey(), info.getColor());
            getView().onChangeAdjustFilter();
            return;
        }
        float maxVal = info.getMaxVal();
        float minVal = info.getMinVal();
        float diff = maxVal - minVal;
        float strength = diff * progressOrColor / 100 + minVal;
        info.setStrength(strength);
        mFilterItem.setFilterParam(info.getKey(), info.getStrength());
        getView().onChangeAdjustFilter();
    }
}
