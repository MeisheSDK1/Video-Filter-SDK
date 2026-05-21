package com.meishe.base.bean;

import android.graphics.PointF;

/**
 * author : lhz
 * date   : 2020/8/3
 * desc   :关键帧信息 Keyframe information
 */
public class KeyFrameInfo {
    private float scaleX;
    private float scaleY;
    private float rotationZ;
    private PointF translation;
    private float opacity;

    /**
     * 前置控制点或控制点值为-1认为是无效值
     * A leading control point or a control point value of -1 is considered as an invalid value
     */
    private double backwardControlPointX = -1;
    private double backwardControlPointYForTransX;
    private double backwardControlPointYForTransY;
    private double forwardControlPointX = -1;
    private double forwardControlPointYForTransX;
    private double forwardControlPointYForTransY;
    private int selectForwardBezierPos = 0;
    private int selectBackwardBezierPos = 0;

    public float getScaleX() {
        return scaleX;
    }

    public KeyFrameInfo setScaleX(float scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public float getScaleY() {
        return scaleY;
    }

    public KeyFrameInfo setScaleY(float scaleY) {
        this.scaleY = scaleY;
        return this;
    }


    public float getOpacity() {
        return opacity;
    }

    public KeyFrameInfo setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public KeyFrameInfo setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
        return this;
    }

    public PointF getTranslation() {
        return translation;
    }

    public KeyFrameInfo setTranslation(PointF translation) {
        this.translation = translation;
        return this;
    }

    public double getBackwardControlPointX() {
        return backwardControlPointX;
    }

    public KeyFrameInfo setBackwardControlPointX(double backwardControlPointX) {
        this.backwardControlPointX = backwardControlPointX;
        return this;
    }

    public double getBackwardControlPointYForTransX() {
        return backwardControlPointYForTransX;
    }

    public void setBackwardControlPointYForTransX(double backwardControlPointYForTransX) {
        this.backwardControlPointYForTransX = backwardControlPointYForTransX;
    }

    public double getBackwardControlPointYForTransY() {
        return backwardControlPointYForTransY;
    }

    public void setBackwardControlPointYForTransY(double backwardControlPointYForTransY) {
        this.backwardControlPointYForTransY = backwardControlPointYForTransY;
    }

    public double getForwardControlPointX() {
        return forwardControlPointX;
    }

    public KeyFrameInfo setForwardControlPointX(double forwardControlPointX) {
        this.forwardControlPointX = forwardControlPointX;
        return this;
    }

    public double getForwardControlPointYForTransX() {
        return forwardControlPointYForTransX;
    }

    public void setForwardControlPointYForTransX(double forwardControlPointYForTransX) {
        this.forwardControlPointYForTransX = forwardControlPointYForTransX;
    }

    public double getForwardControlPointYForTransY() {
        return forwardControlPointYForTransY;
    }

    public void setForwardControlPointYForTransY(double forwardControlPointYForTransY) {
        this.forwardControlPointYForTransY = forwardControlPointYForTransY;
    }

    public int getSelectForwardBezierPos() {
        return selectForwardBezierPos;
    }

    public void setSelectForwardBezierPos(int selectForwardBezierPos) {
        this.selectForwardBezierPos = selectForwardBezierPos;
    }

    public int getSelectBackwardBezierPos() {
        return selectBackwardBezierPos;
    }

    public void setSelectBackwardBezierPos(int selectBackwardBezierPos) {
        this.selectBackwardBezierPos = selectBackwardBezierPos;
    }


}
