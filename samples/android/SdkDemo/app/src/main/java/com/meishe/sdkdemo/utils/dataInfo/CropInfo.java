package com.meishe.sdkdemo.utils.dataInfo;

import java.util.Arrays;

import androidx.annotation.NonNull;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 * 裁剪实体类
 * crop bean
 *
 * @Author : zcy
 * @CreateDate : 2021/3/19.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CropInfo implements Cloneable{
    /**
     * 宽高比的索引
     * Index of aspect ratio
     */
    private int ratio;
    /**
     * 宽高比具体值
     * Specific value of aspect ratio
     */
    private float ratioValue;
    /**
     * 设置是否是老数据
     * Set whether the data is old
     */
    private boolean oldDataFlag = false;
    /**
     * liveWindow宽高
     * liveWindow width and height
     */
    private int liveWindowWidth;
    private int liveWindowHeight;
    /**
     * 缩放(liveWindow的缩放值，包含初始缩放，计算transForm2D缩放要除去初始缩放)
     * Scaling (liveWindow scaling, including the initial scaling, excluding the initial scaling when calculating transForm2D scaling)
     */
    private float scaleX = 1;
    private float scaleY = 1;
    /**
     * 初始缩放，此缩放为view初始缩放，不涉及transform2D缩放
     * Initial scaling, which is the initial scaling of the view and does not involve transform2D scaling
     */
    private float realScale = 1f;
    /**
     * 平移
     * translation
     */
    private float transX;
    private float transY;
    /**
     * 旋转
     * rotation
     */
    private float rotationZ;
    /**
     * 蒙版数据
     * Mask data
     */
    private float[] regionData;
    private int cutViewHeight;
    private int cutViewWidth;
    private int timelineWidth;
    private int timelineHeight;

    public float getRealScale() {
        return realScale;
    }

    public void setRealScale(float realScale) {
        if (realScale < 1) realScale = 1;
        this.realScale = realScale;
    }

    public int getCutViewHeight() {
        return cutViewHeight;
    }

    public void setCutViewHeight(int cutViewHeight) {
        this.cutViewHeight = cutViewHeight;
    }

    public int getCutViewWidth() {
        return cutViewWidth;
    }

    public void setCutViewWidth(int cutViewWidth) {
        this.cutViewWidth = cutViewWidth;
    }

    public int getTimelineWidth() {
        return timelineWidth;
    }

    public void setTimelineWidth(int timelineWidth) {
        this.timelineWidth = timelineWidth;
    }

    public int getTimelineHeight() {
        return timelineHeight;
    }

    public void setTimelineHeight(int timelineHeight) {
        this.timelineHeight = timelineHeight;
    }

    public float[] getRegionData() {
        return regionData;
    }

    public void setRegionData(float[] regionData) {
        this.regionData = regionData;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public float getRatioValue() {
        return ratioValue;
    }

    public void setRatioValue(float ratioValue) {
        this.ratioValue = ratioValue;
    }

    public boolean isOldDataFlag() {
        return oldDataFlag;
    }

    public void setOldDataFlag(boolean oldDataFlag) {
        this.oldDataFlag = oldDataFlag;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getTransX() {
        return transX;
    }

    public void setTransX(float transX) {
        this.transX = transX;
    }

    public float getTransY() {
        return transY;
    }

    public void setTransY(float transY) {
        this.transY = transY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public int getLiveWindowWidth() {
        return liveWindowWidth;
    }

    public void setLiveWindowWidth(int liveWindowWidth) {
        this.liveWindowWidth = liveWindowWidth;
    }

    public int getLiveWindowHeight() {
        return liveWindowHeight;
    }

    public void setLiveWindowHeight(int liveWindowHeight) {
        this.liveWindowHeight = liveWindowHeight;
    }

    public void calculationRealScale() {
    }

    @NonNull
    @Override
    public CropInfo clone() {
        try {
            return (CropInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "CropInfo{" +
                "ratio=" + ratio +
                ", ratioValue=" + ratioValue +
                ", oldDataFlag=" + oldDataFlag +
                ", liveWindowWidth=" + liveWindowWidth +
                ", liveWindowHeight=" + liveWindowHeight +
                ", scaleX=" + scaleX +
                ", scaleY=" + scaleY +
                ", transX=" + transX +
                ", transY=" + transY +
                ", rotationZ=" + rotationZ +
                ", regionData=" + Arrays.toString(regionData) +
                ", cutViewHeight=" + cutViewHeight +
                ", cutViewWidth=" + cutViewWidth +
                ", timelineWidth=" + timelineWidth +
                ", timelineHeight=" + timelineHeight +
                '}';
    }


}
