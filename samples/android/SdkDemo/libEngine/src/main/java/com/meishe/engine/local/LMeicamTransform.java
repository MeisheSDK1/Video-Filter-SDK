package com.meishe.engine.local;

import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamTransform;

import java.io.Serializable;

/**
 * author：yangtailin on 2020/7/6 20:38
 */
public class LMeicamTransform implements Serializable, Cloneable, LocalToTimelineDataAdapter<MeicamTransform> {
    private float transformX;
    private float transformY;
    private float scaleX = 1.0F;
    private float scaleY = 1.0F;
    private float rotationZ;

    public float getTransformX() {
        return transformX;
    }

    public void setTransformX(float transformX) {
        this.transformX = transformX;
    }

    public float getTransformY() {
        return transformY;
    }

    public void setTransformY(float transformY) {
        this.transformY = transformY;
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

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    @Override
    public MeicamTransform parseToTimelineData() {
        MeicamTransform timelineData = new MeicamTransform();
        timelineData.setRotationZ(getRotationZ());
        timelineData.setScaleX(getScaleX());
        timelineData.setScaleY(getScaleY());
        timelineData.setTransformX(getTransformX());
        timelineData.setTransformY(getTransformY());
        return timelineData;
    }
}
