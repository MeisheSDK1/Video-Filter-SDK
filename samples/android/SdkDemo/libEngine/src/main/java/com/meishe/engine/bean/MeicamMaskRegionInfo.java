package com.meishe.engine.bean;

import com.meicam.sdk.NvsMaskRegionInfo;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMeicamMaskRegionInfo;

import java.io.Serializable;

/**
 * @author :Jml
 * @date :2020/9/18 18:15
 * @des :
 */
public class MeicamMaskRegionInfo implements Cloneable, Serializable, TimelineDataToLocalAdapter<LMeicamMaskRegionInfo> {
    private transient NvsMaskRegionInfo nvsMaskRegionInfo;
    private boolean mRevert;
    private float mFeatherWidth;

    public float getFeatherWidth() {
        return mFeatherWidth;
    }

    public boolean isRevert() {
        return mRevert;
    }

    public void setRevert(boolean mRevert) {
        this.mRevert = mRevert;
    }


    public void setMaskRegionInfo(NvsMaskRegionInfo maskRegionInfo) {
        this.nvsMaskRegionInfo = maskRegionInfo;
    }

    public NvsMaskRegionInfo getMaskRegionInfo() {
        return nvsMaskRegionInfo;
    }

    public void setFeatherWidth(float mFeatherWidth) {
        this.mFeatherWidth = mFeatherWidth;
    }


    @Override
    public LMeicamMaskRegionInfo parseToLocalData() {
        LMeicamMaskRegionInfo lMeicamMaskRegionInfo = new LMeicamMaskRegionInfo();
        lMeicamMaskRegionInfo.setMaskRegionInfo(getMaskRegionInfo());
        lMeicamMaskRegionInfo.setmFeatherWidth(getFeatherWidth());
        lMeicamMaskRegionInfo.setmRevert(isRevert());
        return lMeicamMaskRegionInfo;
    }

}
