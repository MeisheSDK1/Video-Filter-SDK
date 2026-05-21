package com.meishe.sdkdemo.edit.data;

import android.util.Log;

import com.meicam.sdk.NvsColor;

import java.io.Serializable;
import java.util.HashMap;

public class FilterItem implements Serializable {
    /**
     * 内建特效
     * Built-in effects
     */
    public static int FILTERMODE_BUILTIN = 0;
    /**
     * Asset中预装
     * Pre-installed in Asset
     */
    public static int FILTERMODE_BUNDLE = 1;
    /**
     * 包裹特效
     * Package effects
     */
    public static int FILTERMODE_PACKAGE = 2;

    private String m_filterName;
    /**
     * m_filterName对应的资源key，用来获取资源value值
     */
    private int m_assetFilterNameId;
    private int m_filterMode;
    private String m_filterId;
    private int m_imageId;
    private String m_imageUrl;
    private String m_packageId;
    private String m_assetDescription;
    private boolean m_isSpecialFilter = false;

    /**
     * 为了兼容旧版本，该字段主要用于转场国际化使用
     * For compatibility with older versions, this field is mainly used for transition internationalization
     */
    private String m_filterDesc;
    private int categoryId = -1;

    /**
     * 粒子类型
     * Particle type
     */
    private int particleType = 0;

    /**
     * 用于特殊漫画的字段
     * Fields for special comics
     */
    private boolean isCartoon = false;
    private boolean isStrokenOnly = true;
    private boolean isGrayScale = true;

    private int backgroundColor;
    private boolean isPostPackage;
    /**
     * 1可调 0不可调
     * 注：只有查询素材分类分类为滤镜、转场、字幕、贴纸、AR道具、组合字幕才生效
     * 1 is adjustable and 0 is not
     * Note: It only takes effect if the query material is classified as filter, transition, subtitles, stickers, AR props and combined subtitles
     */
    public int isAdjusted;
    private float intensity;

    /**
     * 下载
     * download
     */
    public int downloadProgress = 0;
    public int downloadStatus = 0;

    /**
     * 滤镜可调节数据
     * Filter adjusts data
     */
    private HashMap<String, Float> filterParam = new HashMap<>();
    private HashMap<String, NvsColor> filterColorParam = new HashMap<>();

    public FilterItem() {
        m_filterId = null;
        m_filterName = null;
        m_filterMode = FILTERMODE_BUILTIN;
        m_imageId = 0;
        m_imageUrl = null;
        m_packageId = null;
        m_assetDescription = null;
        isAdjusted = 0;
        intensity = 1.0F;
        clearAdjustData();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setFilterName(String name) {
        m_filterName = name;
    }

    public String getFilterName() {
        return m_filterName;
    }

    public void setFilterMode(int mode) {
        if (mode != FILTERMODE_BUILTIN && mode != FILTERMODE_BUNDLE && mode != FILTERMODE_PACKAGE) {
            Log.e("", "invalid mode data");
            return;
        }
        m_filterMode = mode;
    }

    public int getFilterMode() {
        return m_filterMode;
    }

    public void setFilterId(String fxId) {
        m_filterId = fxId;
    }

    public String getFilterId() {
        return m_filterId;
    }

    public void setImageId(int imageId) {
        m_imageId = imageId;
    }

    public int getImageId() {
        return m_imageId;
    }

    public void setImageUrl(String imageUrl) {
        m_imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return m_imageUrl;
    }

    public void setPackageId(String packageId) {
        m_packageId = packageId;
    }

    public String getPackageId() {
        return m_packageId;
    }

    public String getAssetDescription() {
        return m_assetDescription;
    }

    public void setAssetDescription(String m_assetDescription) {
        this.m_assetDescription = m_assetDescription;
    }

    public boolean isPostPackage() {
        return isPostPackage;
    }

    public void setPostPackage(boolean postPackage) {
        isPostPackage = postPackage;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFilterDesc() {
        return m_filterDesc;
    }

    public void setFilterDesc(String m_filterDesc) {
        this.m_filterDesc = m_filterDesc;
    }

    public void setStrokenOnly(boolean strokenOnly) {
        isStrokenOnly = strokenOnly;
    }

    public boolean getStrokenOnly() {
        return isStrokenOnly;
    }

    public void setGrayScale(boolean grayScale) {
        isGrayScale = grayScale;
    }

    public boolean getGrayScale() {
        return isGrayScale;
    }

    public void setIsCartoon(boolean isCartoon) {
        this.isCartoon = isCartoon;
    }

    public boolean getIsCartoon() {
        return isCartoon;
    }

    public int getParticleType() {
        return particleType;
    }

    public void setParticleType(int particleType) {
        this.particleType = particleType;
    }

    public boolean isSpecialFilter() {
        return m_isSpecialFilter;
    }

    public void setIsSpecialFilter(boolean m_isSpecialFilter) {
        this.m_isSpecialFilter = m_isSpecialFilter;
    }

    public int getIsAdjusted() {
        return isAdjusted;
    }

    public void setIsAdjusted(int isAdjusted) {
        this.isAdjusted = isAdjusted;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public HashMap<String, Float> getFilterParam() {
        return filterParam;
    }

    public void setFilterParam(String key, float value) {
        if (filterParam != null) {
            filterParam.put(key, value);
        }
    }

    public HashMap<String, NvsColor> getFilterColorParam() {
        return filterColorParam;
    }


    public void setFilterColorParam(String key, NvsColor value) {
        if (filterColorParam != null) {
            filterColorParam.put(key, value);
        }
    }

    public void clearAdjustData() {
        if (null != filterParam) {
            filterParam.clear();
        }
        if (null != filterColorParam) {
            filterColorParam.clear();
        }
    }


    public int getAssetFilterNameId() {
        return m_assetFilterNameId;
    }

    public void setAssetFilterNameId(int m_assetFilterNameId) {
        this.m_assetFilterNameId = m_assetFilterNameId;
    }
}
