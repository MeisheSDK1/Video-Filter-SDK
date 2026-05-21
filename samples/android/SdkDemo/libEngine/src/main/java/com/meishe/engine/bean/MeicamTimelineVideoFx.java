package com.meishe.engine.bean;

import com.meishe.engine.local.LMeicamTimelineVideoFx;

/**
 * author：yangtailin on 2020/8/13 10:52
 */
public class MeicamTimelineVideoFx extends MeicamVideoFx{
    private long inPoint;
    private long outPoint;

    public MeicamTimelineVideoFx() {
        classType = "timelineVideoFx";
    }

    public long getInPoint() {
        return inPoint;
    }

    public void setInPoint(long inPoint) {
        this.inPoint = inPoint;
    }

    public long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.outPoint = outPoint;
    }

    @Override
    public LMeicamTimelineVideoFx parseToLocalData() {
        LMeicamTimelineVideoFx local = new LMeicamTimelineVideoFx();
        setCommonData(local);
        local.setInPoint(getInPoint());
        local.setOutPoint(getOutPoint());
        return local;
    }
}
