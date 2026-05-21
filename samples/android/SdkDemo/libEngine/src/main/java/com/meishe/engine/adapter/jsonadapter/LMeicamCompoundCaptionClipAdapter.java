package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.meishe.engine.local.LMeicamCompoundCaptionClip;

/**
 * author：yangtailin on 2020/8/15 16:51
 * Meicam复合标题剪辑适配器
 * Meicam composite title clip adapter
 */
public class LMeicamCompoundCaptionClipAdapter extends BaseTypeAdapter<LMeicamCompoundCaptionClip> {
    @Override
    public Class<LMeicamCompoundCaptionClip> getClassOfT() {
        return LMeicamCompoundCaptionClip.class;
    }

    @Override
    protected JsonElement parseReadData(JsonElement jsonObject) {
        JsonObject rootJson = jsonObject.getAsJsonObject();
        JsonElement compoundCaptionItems = rootJson.remove("mCompoundCaptionItems");
        if (compoundCaptionItems != null) {
            rootJson.add("compoundCaptionItems", compoundCaptionItems.getAsJsonArray());
        }
        return rootJson;
    }
}
