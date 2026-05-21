package com.meishe.engine.local;

import android.graphics.PointF;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamWaterMark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/4 14:56
 */
public class LMeicamWaterMark implements Cloneable, Serializable, LocalToTimelineDataAdapter<MeicamWaterMark> {
    @SerializedName("watermarkPath")
    private String mWatermarkPath;
    @SerializedName("watermarkX")
    private int mWatermarkX;
    @SerializedName("watermarkY")
    private int mWatermarkY;
    @SerializedName("watermarkW")
    private int mWatermarkW;
    @SerializedName("watermarkH")
    private int mWatermarkH;
    private float opacity = 1;
    private String resourceId;

    public LMeicamWaterMark(String watermarkPath) {
        this.mWatermarkPath = watermarkPath;
    }

    public String getWatermarkPath() {
        return mWatermarkPath;
    }

    public void setWatermarkPath(String mWatermarkPath) {
        this.mWatermarkPath = mWatermarkPath;
    }

    public int getWatermarkX() {
        return mWatermarkX;
    }

    public void setWatermarkX(int mWatermarkX) {
        this.mWatermarkX = mWatermarkX;
    }

    public int getWatermarkY() {
        return mWatermarkY;
    }

    public void setWatermarkY(int mWatermarkY) {
        this.mWatermarkY = mWatermarkY;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getWatermarkW() {
        return mWatermarkW;
    }

    public void setWatermarkW(int mWatermarkW) {
        this.mWatermarkW = mWatermarkW;
    }

    public int getWatermarkH() {
        return mWatermarkH;
    }

    public void setWatermarkH(int mWatermarkH) {
        this.mWatermarkH = mWatermarkH;
    }

    @Override
    public MeicamWaterMark parseToTimelineData() {
        List<PointF> list = new ArrayList<>();
        MeicamWaterMark timelineData = new MeicamWaterMark(getWatermarkPath(), list);
        timelineData.setWatermarkH(getWatermarkH());
        timelineData.setWatermarkW(getWatermarkW());
        timelineData.setWatermarkX(getWatermarkX());
        timelineData.setWatermarkY(getWatermarkY());
        return timelineData;
    }
}
