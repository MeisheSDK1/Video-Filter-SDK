package com.meishe.sdkdemo.utils.dataInfo;

import android.graphics.RectF;

import java.io.Serializable;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2021/8/3.
 */
public class VolumeKeyInfo implements Serializable {
    private long preControlPoint = -1;
    private long nextControlPoint = -1;
    private float volumeValue;
    private long keyTime;
    private float preVolumeValue;
    private float nextVolumeValue;
    /**
     * 用户保存当前关键帧与下一帧之间的自定义贝塞尔曲线数据,仅用于view层展示
     * The user saves the customized Bessel curve data between the current keyframe and the next frame for display at the view layer only
     */
    private RectF customABezierRectF;

    public VolumeKeyInfo(long keyTime,float volumeValue) {
        this.volumeValue = volumeValue;
        this.keyTime = keyTime;
    }

    public long getKeyTime() {
        return keyTime;
    }

    public void setKeyTime(long keyTime) {
        this.keyTime = keyTime;
    }

    public long getPreControlPoint() {
        return preControlPoint;
    }

    public void setPreControlPoint(long preControlPoint) {
        this.preControlPoint = preControlPoint;
    }

    public long getNextControlPoint() {
        return nextControlPoint;
    }

    public void setNextControlPoint(long nextControlPoint) {
        this.nextControlPoint = nextControlPoint;
    }

    public float getVolumeValue() {
        return volumeValue;
    }

    public void setVolumeValue(float volumeValue) {
        this.volumeValue = volumeValue;
    }

    public float getPreVolumeValue() {
        return preVolumeValue;
    }

    public void setPreVolumeValue(float preVolumeValue) {
        this.preVolumeValue = preVolumeValue;
    }

    public float getNextVolumeValue() {
        return nextVolumeValue;
    }

    public void setNextVolumeValue(float nextVolumeValue) {
        this.nextVolumeValue = nextVolumeValue;
    }

    public RectF getCustomABezierRectF() {
        return customABezierRectF;
    }

    public void setCustomABezierRectF(RectF customABezierRectF) {
        this.customABezierRectF = customABezierRectF;
    }
}
