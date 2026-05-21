package com.meishe.engine.adapter.jsonadapter;

import com.meishe.engine.local.LMeicamTimelineVideoFxTrack;

/**
 * author：yangtailin on 2020/8/15 16:42
 * Meicam时间线视频Fx轨道适配器
 * Meicam timeline video Fx track adapter
 */
public class LMeicamTimelineVideoFxTrackAdapter extends BaseTypeAdapter<LMeicamTimelineVideoFxTrack> {

    @Override
    public Class<LMeicamTimelineVideoFxTrack> getClassOfT() {
        return LMeicamTimelineVideoFxTrack.class;
    }
}
