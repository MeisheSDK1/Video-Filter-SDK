package com.meishe.engine.bean;

import androidx.annotation.NonNull;

import com.meishe.engine.local.LMeicamTimelineVideoFxTrack;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/3 20:36
 */
public class MeicamTimelineVideoFxTrack extends TrackInfo<Object> implements Cloneable, Serializable{
    public MeicamTimelineVideoFxTrack(int index) {
        super(CommonData.TRACK_TIMELINE_FX, index);
    }

    @NonNull
    @Override
    public MeicamTimelineVideoFxTrack clone() {
        return (MeicamTimelineVideoFxTrack) DeepCopyUtil.deepClone(this);
    }

    @Override
    public LMeicamTimelineVideoFxTrack parseToLocalData() {
        LMeicamTimelineVideoFxTrack local = new LMeicamTimelineVideoFxTrack(getIndex());
        setCommondData(local);
        return local;
    }
}
