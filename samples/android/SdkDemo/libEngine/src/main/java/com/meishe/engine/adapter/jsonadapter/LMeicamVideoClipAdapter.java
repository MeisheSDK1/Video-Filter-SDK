package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.local.LMeicamVideoClip;

/**
 * author：yangtailin on 2020/8/15 16:49
 * Meicam视频剪辑适配器
 * Meicam video clip adapter
 */
public class LMeicamVideoClipAdapter extends BaseTypeAdapter<LMeicamVideoClip> {

    @Override
    public Class<LMeicamVideoClip> getClassOfT() {
        return LMeicamVideoClip.class;
    }

    @Override
    protected JsonElement parseReadData(JsonElement jsonObject) {
        JsonObject rootJson = jsonObject.getAsJsonObject();
        JsonElement storyboardInfo = rootJson.get("storyboardInfo");
        if (storyboardInfo != null && !storyboardInfo.isJsonArray()) {
            storyboardInfo = rootJson.remove("storyboardInfo");
            JsonObject asJsonObject = storyboardInfo.getAsJsonObject();
            JsonArray jsonArray = new JsonArray();
            if (asJsonObject != null) {
                JsonElement background = asJsonObject.remove("background");
                if (background != null) {
                    jsonArray.add(background);
                }
                JsonElement cropper = asJsonObject.remove("cropper");
                if (cropper != null) {
                    jsonArray.add(cropper);
                }
                JsonElement cropper_transform = asJsonObject.remove("cropper_transform");
                if (background != null) {
                    jsonArray.add(cropper_transform);
                }
            }
            rootJson.add("storyboardInfo", jsonArray);
        }
        return jsonObject;
    }

    @Override
    protected LMeicamVideoClip parseToObject(JsonElement jsonObject) {
        return LGsonContext.getInstance().getClipGson().fromJson(jsonObject, getClassOfT());
    }
}
