package com.meishe.engine.adapter.jsonadapter;

import com.meishe.engine.local.LMeicamStickerClip;

/**
 * author：yangtailin on 2020/8/15 16:52
 * Meicam贴纸夹适配器
 * Meicam sticker clip adapter
 */
public class LMeicamStickerClipAdapter extends BaseTypeAdapter<LMeicamStickerClip> {

    @Override
    public Class<LMeicamStickerClip> getClassOfT() {
        return LMeicamStickerClip.class;
    }
}
