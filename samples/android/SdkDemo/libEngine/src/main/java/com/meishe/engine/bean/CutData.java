package com.meishe.engine.bean;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_ROTATION_Z;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_Y;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_TRANS_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_TRANS_Y;


/**
 * author：yangtailin on 2020/7/27 16:00
 */
public class CutData implements Cloneable {

    private Map<String, Float> mTransformData = new HashMap<>();
    private int mRatio;
    private float mRatioValue;
    /**
     * 设置是否是旧数据
     * Set whether the data is old
     */
    private boolean mIsOldData = false;
    private float[] mRegionData;
    public CutData() {
        mTransformData.put(STORYBOARD_KEY_SCALE_X, 1.0F);
        mTransformData.put(STORYBOARD_KEY_SCALE_Y, 1.0F);
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, 0F);
    }

    public Map<String, Float> getTransformData() {
        return mTransformData;
    }

    public void setTransformData(Map<String, Float> transformData) {
        this.mTransformData = transformData;
    }

    public void putTransformData(String key, float value) {
        mTransformData.put(key, value);
    }

    public float getTransformData(String key) {
        return mTransformData.get(key);
    }

    public int getRatio() {
        return mRatio;
    }

    public void setRatio(int ratio) {
        this.mRatio = ratio;
    }

    public float getRatioValue() {
        return mRatioValue;
    }

    public void setRatioValue(float ratioValue) {
        this.mRatioValue = ratioValue;
    }

    public void setIsOldData(boolean mIsOldData) {
        this.mIsOldData = mIsOldData;
    }

    public boolean isOldData() {
        return mIsOldData;
    }

    public float[] getmRegionData() {
        return mRegionData;
    }

    public void setmRegionData(float[] mRegionData) {
        this.mRegionData = mRegionData;
    }

    @NonNull
    @Override
    public CutData clone() {
        try {
            return (CutData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String toString() {
        return "CutData{" +
                "mTransformData=" + mTransformData +
                ", mRatio=" + mRatio +
                ", mRatioValue=" + mRatioValue +
                ", mIsOldData=" + mIsOldData +
                '}';
    }
}
