package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamAdjustData;

import java.io.Serializable;

/**
 * author：yangtailin on 2020/6/17 12:09
 */
public class LMeicamAdjustData implements Cloneable, Serializable, LocalToTimelineDataAdapter<MeicamAdjustData> {
    @SerializedName("Brightness")
    private float mBrightness; //亮度 Brightness
    @SerializedName("Contrast")
    private float mContrast;  //对比度 Contrast
    @SerializedName("Saturation")
    private float mSaturation;  //饱和度 Saturation
    @SerializedName("Highlight")
    private float mHighlight;  //高光 Highlight
    @SerializedName("Shadow")
    private float mShadow;  //阴影 Shadow
    @SerializedName("Blackpoint")
    private float mBlackPoint;  //褐色 Blackpoint
    @SerializedName("Degree")
    private float mDegree;  //暗角 Degree
    @SerializedName("Amount")
    private float mAmount;  //锐度 Amount
    @SerializedName("Temperature")
    private float mTemperature;   //色温 Temperature
    @SerializedName("Tint")
    private float mTint;    //色调 Tint

    public LMeicamAdjustData() {
        reset();
    }

    public float getBrightness() {
        return mBrightness;
    }

    public void setBrightness(float brightness) {
        mBrightness = brightness;
    }

    public float getContrast() {
        return mContrast;
    }

    public void setContrast(float contrast) {
        mContrast = contrast;
    }

    public float getSaturation() {
        return mSaturation;
    }

    public void setSaturation(float saturation) {
        mSaturation = saturation;
    }

    public float getHighlight() {
        return mHighlight;
    }

    public void setHighlight(float highlight) {
        mHighlight = highlight;
    }

    public float getShadow() {
        return mShadow;
    }

    public void setShadow(float shadow) {
        mShadow = shadow;
    }

    public float getBlackPoint() {
        return mBlackPoint;
    }

    public void setBlackPoint(float blackPoint) {
        mBlackPoint = blackPoint;
    }

    public float getDegree() {
        return mDegree;
    }

    public void setDegree(float degree) {
        mDegree = degree;
    }

    public float getAmount() {
        return mAmount;
    }

    public void setAmount(float amount) {
        mAmount = amount;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float temperature) {
        mTemperature = temperature;
    }

    public float getTint() {
        return mTint;
    }

    public void setTint(float tint) {
        mTint = tint;
    }

    public void reset() {
        mBrightness = 0;
        mContrast = 0;
        mSaturation = 0;
        mHighlight = 0;
        mShadow = 0;
        mBlackPoint = 0;
        mDegree = 0;
        mAmount = 0;
        mTemperature = 0;
        mTint = 0;
    }


    public void setAdjustData(LMeicamAdjustData meicamAdjustData) {
        if (meicamAdjustData == null) {
            return;
        }
        mBrightness = meicamAdjustData.getBrightness();
        mContrast = meicamAdjustData.getContrast();
        mSaturation = meicamAdjustData.getSaturation();
        mHighlight = meicamAdjustData.getHighlight();
        mShadow = meicamAdjustData.getShadow();
        mBlackPoint = meicamAdjustData.getBlackPoint();
        mDegree = meicamAdjustData.getDegree();
        mAmount = meicamAdjustData.getAmount();
        mTemperature = meicamAdjustData.getTemperature();
        mTint = meicamAdjustData.getTint();
    }

    @Override
    public MeicamAdjustData parseToTimelineData() {
        MeicamAdjustData timelineData = new MeicamAdjustData();
        timelineData.setBrightness(getBrightness());
        timelineData.setContrast(getContrast());
        timelineData.setSaturation(getSaturation());
        timelineData.setHighlight(getHighlight());
        timelineData.setShadow(getShadow());
        timelineData.setBlackPoint(getBlackPoint());
        timelineData.setDegree(getDegree());
        timelineData.setAmount(getAmount());
        timelineData.setTemperature(getTemperature());
        timelineData.setTint(getTint());
        return timelineData;
    }
}
