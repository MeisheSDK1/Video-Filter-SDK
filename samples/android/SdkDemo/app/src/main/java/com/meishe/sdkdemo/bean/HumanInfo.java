package com.meishe.sdkdemo.bean;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

public class HumanInfo {

    private RectF faceRect;

    private List<PointF> pointFList;

    private float pitch;
    private float yaw;
    private float roll;

    public HumanInfo(RectF faceRect, List<PointF> pointList, float pitch, float yaw, float roll) {
        this.faceRect = faceRect;
        this.pointFList = pointList;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public RectF getFaceRect() {
        return faceRect;
    }

    public void setFaceRectF(RectF faceRect) {
        this.faceRect = faceRect;
    }

    public List<PointF> getPointFList() {
        return pointFList;
    }

    public void setPointFList(List<PointF> pointFList) {
        this.pointFList = pointFList;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
