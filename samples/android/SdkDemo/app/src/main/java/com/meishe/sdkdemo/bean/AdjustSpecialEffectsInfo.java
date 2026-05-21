package com.meishe.sdkdemo.bean;

import com.meicam.sdk.NvsColor;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/8/16 上午11:25
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class AdjustSpecialEffectsInfo {

    /**
     * 添加类别名称
     * Add Category Name
     */
    private String adjustmentCategoryName;

    private String key;

    private int type = 0;

    private int max;

    private String packageId;

    private float defVal;
    private float minVal;
    private float maxVal;

    private float strength;
    private NvsColor color;


    public String getAdjustmentCategoryName() {
        return adjustmentCategoryName;
    }

    public void setAdjustmentCategoryName(String adjustmentCategoryName) {
        this.adjustmentCategoryName = adjustmentCategoryName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }


    public float getDefVal() {
        return defVal;
    }

    public void setDefVal(float defVal) {
        this.defVal = defVal;
    }

    public float getMinVal() {
        return minVal;
    }

    public void setMinVal(float minVal) {
        this.minVal = minVal;
    }

    public float getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(float maxVal) {
        this.maxVal = maxVal;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public NvsColor getColor() {
        return color;
    }

    public void setColor(NvsColor color) {
        this.color = color;
    }
}
