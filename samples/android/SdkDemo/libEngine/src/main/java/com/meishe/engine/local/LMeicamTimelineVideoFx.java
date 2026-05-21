package com.meishe.engine.local;

import com.meishe.engine.bean.MeicamTimelineVideoFx;

/**
 * author：yangtailin on 2020/8/13 10:53
 */
public class LMeicamTimelineVideoFx extends LMeicamVideoFx {
    private long inPoint;
    private long outPoint;

    public LMeicamTimelineVideoFx() {
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
    public MeicamTimelineVideoFx parseToTimelineData() {
        MeicamTimelineVideoFx timelineData = new MeicamTimelineVideoFx();
        setCommonData(timelineData);
        timelineData.setInPoint(getInPoint());
        timelineData.setOutPoint(getOutPoint());
        return timelineData;
    }
}
