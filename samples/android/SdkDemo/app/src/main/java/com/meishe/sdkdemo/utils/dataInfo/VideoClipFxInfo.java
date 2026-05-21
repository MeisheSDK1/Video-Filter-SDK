package com.meishe.sdkdemo.utils.dataInfo;

import android.util.Log;

import com.meicam.sdk.NvsColor;

import java.util.HashMap;

/**
 * filter info
 */
public class VideoClipFxInfo {
    public static int FXMODE_BUILTIN = 0;
    public static int FXMODE_PACKAGE = 1;

    private int m_fxMode;
    private String m_fxId;
    private float m_fxIntensity;

    /**
     * filter keyFrame Time and intensity
     */
    private HashMap<Long, Double> mKeyFrameInfoMap = new HashMap<>();
    /**
     * 用于特殊的滤镜
     * For special filters
     */
    private boolean m_isCartoon = false;
    private boolean m_isStrokenOnly = true;
    private boolean m_isGrayScale = true;

    /**
     * 1可调 0不可调
     * 注：只有查询素材分类分类为滤镜、转场、字幕、贴纸、AR道具、组合字幕才生效
     * 1 is adjustable and 0 is not
     * Note: It only takes effect if the query material is classified as filter, transition, subtitles, stickers, AR props and combined subtitles
     */
    public int isAdjusted;

    private HashMap<String, Float> filterParam = new HashMap<>();
    private HashMap<String, NvsColor> filterColorParam = new HashMap<>();


    public VideoClipFxInfo() {
        m_fxId = null;
        m_fxMode = FXMODE_BUILTIN;
        m_fxIntensity = 1.0f;
        isAdjusted = 0;
    }

    public void putKeyFrameInfo(long keyFrameTime, double intensity) {
        if (mKeyFrameInfoMap != null) {
            mKeyFrameInfoMap.put(keyFrameTime, intensity);
        }
    }

    public HashMap<Long, Double> getKeyFrameInfoMap() {
        return mKeyFrameInfoMap;
    }

    public void setFxMode(int mode) {
        if (mode != FXMODE_BUILTIN && mode != FXMODE_PACKAGE) {
            Log.e("", "invalid mode data");
            return;
        }
        m_fxMode = mode;
    }

    public int getFxMode() {
        return m_fxMode;
    }

    public void setFxId(String fxId) {
        m_fxId = fxId;
    }

    public String getFxId() {
        return m_fxId;
    }

    public void setFxIntensity(float intensity) {
        m_fxIntensity = intensity;
    }

    public float getFxIntensity() {
        return m_fxIntensity;
    }

    public void setStrokenOnly(boolean strokenOnly) {
        m_isStrokenOnly = strokenOnly;
    }

    public boolean getStrokenOnly() {
        return m_isStrokenOnly;
    }

    public void setGrayScale(boolean grayScale) {
        m_isGrayScale = grayScale;
    }

    public boolean getGrayScale() {
        return m_isGrayScale;
    }

    public void setIsCartoon(boolean isCartoon) {
        m_isCartoon = isCartoon;
    }

    public boolean getIsCartoon() {
        return m_isCartoon;
    }


    public void putFilterParam(String key, float value) {
        if (filterParam != null) {
            filterParam.put(key, value);
        }
    }

    public HashMap<String, Float> getFilterParam() {
        return filterParam;
    }

    public void clearFilterParam() {
        if (filterParam != null) {
            filterParam.clear();
        }
    }

    public HashMap<String, NvsColor> getFilterColorParam() {
        return filterColorParam;
    }

    public void putFilterColorParam(String key, NvsColor value) {
        if (filterColorParam != null) {
            filterColorParam.put(key, value);
        }
    }

    public int getIsAdjusted() {
        return isAdjusted;
    }

    public void setIsAdjusted(int isAdjusted) {
        this.isAdjusted = isAdjusted;
    }
}
