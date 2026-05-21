package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.JsonElement;
import com.meishe.engine.local.LMeicamAudioClip;

/**
 * author：yangtailin on 2020/8/15 16:47
 * Meicam音频剪辑适配器
 * Meicam audio clip adapter
 */
public class LMeicamAudioClipAdapter extends BaseTypeAdapter<LMeicamAudioClip> {

    @Override
    protected JsonElement parseReadData(JsonElement jsonObject) {
        return super.parseReadData(jsonObject);
    }

    @Override
    public Class<LMeicamAudioClip> getClassOfT() {
        return LMeicamAudioClip.class;
    }
}
