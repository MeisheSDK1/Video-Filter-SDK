package com.meishe.makeup.makeup.original;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/6 14:45
 * @Description :美颜、美型参数 the beauty and the beauty shape param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyOldParam extends BaseOldParam {
    public static final String ADVANCED_BEAUTY_ENABLE = "Advanced Beauty Enable";
    public static final String ADVANCED_BEAUTY_TYPE = "Advanced Beauty Type";
    public static final String ADVANCED_BEAUTY_INTENSITY = "Advanced Beauty Intensity";
    public static final String WHITENING_LUT_ENABLE = "Whitening Lut Enabled";
    /**
     * 美白
     * Beauty Whitening
     */
    public static final String BEAUTY_WHITENING = "Beauty Whitening";
    @SerializedName(ADVANCED_BEAUTY_ENABLE)
    private int advancedBeautyEnable;
    @SerializedName(ADVANCED_BEAUTY_TYPE)
    private int advancedBeautyType = -1;
    @SerializedName(WHITENING_LUT_ENABLE)
    private int whiteningLutEnabled = -1;

    public void setAdvancedBeautyEnable(int advancedBeautyEnable) {
        this.advancedBeautyEnable = advancedBeautyEnable;
    }

    public void setAdvancedBeautyType(int advancedBeautyType) {
        this.advancedBeautyType = advancedBeautyType;
    }

    public void setWhiteningLutEnabled(int whiteningLutEnabled) {
        this.whiteningLutEnabled = whiteningLutEnabled;
    }

    public boolean paramKeyEndWithEnable() {
        return !TextUtils.isEmpty(getParamKey()) && getParamKey().endsWith("Enabled");
    }

    public boolean advancedBeautyEnable() {
        return advancedBeautyEnable > 0;
    }

    public int getAdvancedBeautyType() {
        return advancedBeautyType;
    }

    public boolean paramKeyIsWhitening() {
        return BEAUTY_WHITENING.equals(getParamKey());
    }

    public boolean whiteningLutEnabled() {
        return whiteningLutEnabled > 0;
    }

    @Override
    public String toString() {
        return "BeautyParam{" +
                "type='" + type + '\'' +
                ", advancedBeautyEnable=" + advancedBeautyEnable() +
                ", advancedBeautyType=" + getAdvancedBeautyType() +
                ", whiteningLutEnabled=" + whiteningLutEnabled() +
                ", paramKey='" + getParamKey() + '\'' +
                ", packageId='" + getPackageId() + '\'' +
                ", value=" + getValue() +
                '}';
    }
}
