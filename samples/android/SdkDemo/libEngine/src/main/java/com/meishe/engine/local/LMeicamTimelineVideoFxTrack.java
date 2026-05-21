package com.meishe.engine.local;

import androidx.annotation.NonNull;

import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamTimelineVideoFxTrack;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/3 20:36
 */
public class LMeicamTimelineVideoFxTrack extends LTrackInfo implements Cloneable, Serializable{
    public LMeicamTimelineVideoFxTrack(int index) {
        super(CommonData.TRACK_TIMELINE_FX, index);
    }

    @NonNull
    @Override
    public LMeicamTimelineVideoFxTrack clone() {
        return (LMeicamTimelineVideoFxTrack) DeepCopyUtil.deepClone(this);
    }

    @Override
    public MeicamTimelineVideoFxTrack parseToTimelineData() {
        MeicamTimelineVideoFxTrack timelineData = new MeicamTimelineVideoFxTrack(getIndex());
        setCommondData(timelineData);
        return timelineData;
    }
}
