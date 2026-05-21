package com.meishe.engine.adapter.jsonadapter;

import com.meishe.engine.local.LMeicamStickerCaptionTrack;

/**
 * author：yangtailin on 2020/8/15 16:40
 * Meicam贴纸标题轨道适配器
 * Meicam sticker title track adapter
 */
public class LMeicamStickerCaptionTrackAdapter extends BaseTypeAdapter<LMeicamStickerCaptionTrack> {

    @Override
    public Class<LMeicamStickerCaptionTrack> getClassOfT() {
        return LMeicamStickerCaptionTrack.class;
    }
}
