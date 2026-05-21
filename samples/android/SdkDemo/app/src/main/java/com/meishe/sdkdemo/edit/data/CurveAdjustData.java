package com.meishe.sdkdemo.edit.data;

import android.graphics.PointF;

public class CurveAdjustData {

    private PointF frontControlPointF;

    private PointF backControlPointF;

    private boolean isCustom;

    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public PointF getFrontControlPointF() {
        return frontControlPointF;
    }

    public void setFrontControlPointF(PointF frontControlPointF) {
        this.frontControlPointF = frontControlPointF;
    }

    public PointF getBackControlPointF() {
        return backControlPointF;
    }

    public void setBackControlPointF(PointF backControlPointF) {
        this.backControlPointF = backControlPointF;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
