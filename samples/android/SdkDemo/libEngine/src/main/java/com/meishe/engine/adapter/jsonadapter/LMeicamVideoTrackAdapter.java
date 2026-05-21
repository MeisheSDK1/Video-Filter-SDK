package com.meishe.engine.adapter.jsonadapter;

import com.meishe.engine.local.LMeicamVideoTrack;

/**
 * author：yangtailin on 2020/8/15 16:35
 * Meicam视频轨道适配器
 * Meicam video track adapter
 */
public class LMeicamVideoTrackAdapter extends BaseTypeAdapter<LMeicamVideoTrack> {
    @Override
    public Class<LMeicamVideoTrack> getClassOfT() {
        return LMeicamVideoTrack.class;
    }
}
