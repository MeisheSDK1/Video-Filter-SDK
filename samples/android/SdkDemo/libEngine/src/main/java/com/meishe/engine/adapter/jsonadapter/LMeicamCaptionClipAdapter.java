package com.meishe.engine.adapter.jsonadapter;

import com.meishe.engine.local.LMeicamCaptionClip;

/**
 * author：yangtailin on 2020/8/15 16:50
 * Meicam标题剪辑适配器
 * Meicam Title clip adapter
 */
public class LMeicamCaptionClipAdapter extends BaseTypeAdapter<LMeicamCaptionClip> {
    @Override
    public Class<LMeicamCaptionClip> getClassOfT() {
        return LMeicamCaptionClip.class;
    }
}
