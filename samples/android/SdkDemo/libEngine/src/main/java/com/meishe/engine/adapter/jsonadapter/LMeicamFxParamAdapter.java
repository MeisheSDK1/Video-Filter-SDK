package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.local.LMeicamFxParam;

/**
 * author：yangtailin on 2020/8/24 15:52
 * Meicam Fx参数适配器
 * Meicam Fx parameter adapter
 */
public class LMeicamFxParamAdapter extends BaseTypeAdapter<LMeicamFxParam> {
    @Override
    public Class<LMeicamFxParam> getClassOfT() {
        return LMeicamFxParam.class;
    }

    @Override
    protected JsonElement parseReadData(JsonElement jsonObject) {
        JsonObject rootJson = jsonObject.getAsJsonObject();
        JsonElement type = rootJson.get("type");
        if (type != null) {
            if ("string".equals(type.getAsString())) {
                rootJson.remove("type");
                rootJson.addProperty("type", "String");
            }
        }
        return super.parseReadData(jsonObject);
    }

    @Override
    protected LMeicamFxParam parseToObject(JsonElement jsonObject) {
        return LGsonContext.getInstance().getCommonGson().fromJson(jsonObject, getClassOfT());
    }
}
