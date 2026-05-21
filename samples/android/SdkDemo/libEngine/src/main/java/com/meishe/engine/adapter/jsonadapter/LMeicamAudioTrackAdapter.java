package com.meishe.engine.adapter.jsonadapter;

import com.meishe.engine.local.LMeicamAudioTrack;

/**
 * author：yangtailin on 2020/8/15 16:35
 * Meicam音频轨道适配器
 * Meicam audio track adapter
 */
public class LMeicamAudioTrackAdapter extends BaseTypeAdapter<LMeicamAudioTrack> {
    @Override
    public Class<LMeicamAudioTrack> getClassOfT() {
        return LMeicamAudioTrack.class;
    }
}
