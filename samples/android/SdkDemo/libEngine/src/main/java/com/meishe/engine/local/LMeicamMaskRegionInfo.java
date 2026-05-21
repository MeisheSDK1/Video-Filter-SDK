package com.meishe.engine.local;

import com.meicam.sdk.NvsMaskRegionInfo;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamMaskRegionInfo;

/**
 * @author :Jml
 * @date :2020/9/18 18:17
 * @des :
 */
public class LMeicamMaskRegionInfo implements Cloneable, LocalToTimelineDataAdapter<MeicamMaskRegionInfo> {
    private NvsMaskRegionInfo nvsMaskRegionInfo;
    private boolean mRevert;
    private float mFeatherWidth;

    public float getmFeatherWidth() {
        return mFeatherWidth;
    }

    public boolean ismRevert() {
        return mRevert;
    }

    public void setmRevert(boolean mRevert) {
        this.mRevert = mRevert;
    }


    public void setMaskRegionInfo(NvsMaskRegionInfo maskRegionInfo) {
        this.nvsMaskRegionInfo = maskRegionInfo;
    }

    public NvsMaskRegionInfo getMaskRegionInfo() {
        return nvsMaskRegionInfo;
    }

    public void setmFeatherWidth(float mFeatherWidth) {
        this.mFeatherWidth = mFeatherWidth;
    }


    @Override
    public MeicamMaskRegionInfo parseToTimelineData() {
        MeicamMaskRegionInfo meicamMaskRegionInfo = new MeicamMaskRegionInfo();
        meicamMaskRegionInfo.setMaskRegionInfo(getMaskRegionInfo());
        meicamMaskRegionInfo.setFeatherWidth(getmFeatherWidth());
        meicamMaskRegionInfo.setRevert(ismRevert());
        return meicamMaskRegionInfo;
    }
}
