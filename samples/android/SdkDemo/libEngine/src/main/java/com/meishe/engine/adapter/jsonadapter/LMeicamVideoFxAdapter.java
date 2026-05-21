package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.local.LMeicamFxParam;
import com.meishe.engine.local.LMeicamVideoFx;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author：yangtailin on 2020/8/24 16:42
 * Meicam视频Fx适配器
 * Meicam Video Fx adapter
 */
public class LMeicamVideoFxAdapter extends BaseTypeAdapter<LMeicamVideoFx> {
    private static final String TAG = "LLMeicamVideoFxAdapter";
    @Override
    public Class<LMeicamVideoFx> getClassOfT() {
        return LMeicamVideoFx.class;
    }

    @Override
    protected JsonElement parseReadData(JsonElement jsonObject) {
       // LogUtils.d( "parseReadData before " + jsonObject);
        JsonObject rootJson = jsonObject.getAsJsonObject();
        JsonElement fxParams = rootJson.get("fxParams");
        if (fxParams != null && !fxParams.isJsonArray()) {
            fxParams = rootJson.remove("fxParams");
            Gson mapGson = new GsonBuilder().enableComplexMapKeySerialization().create();
            Type type = new TypeToken<Map<String, LMeicamFxParam>>() {
            }.getType();
            List<LMeicamFxParam> listData = new ArrayList<>();
            try{
                Map<String, LMeicamFxParam> mapData = mapGson.fromJson(fxParams, type);
                //try的目的是防止IllegalArgumentException: JSON forbids NaN and infinities: NaN
                if (mapData != null && !mapData.isEmpty()) {
                    Set<String> keySet = mapData.keySet();
                    for (String key : keySet) {
                        listData.add(mapData.get(key));
                    }
                }
            }catch (Exception e){
                LogUtils.e(e);
            }
            Type listType = new TypeToken<List<LMeicamFxParam>>() {
            }.getType();
            JsonElement jsonElement = LGsonContext.getInstance().getCommonGson().toJsonTree(listData, listType);
            rootJson.add("fxParams", jsonElement);
        }
        //LogUtils.d( "parseReadData after " + rootJson);
        return rootJson;
    }

    @Override
    protected LMeicamVideoFx parseToObject(JsonElement jsonObject) {
        return LGsonContext.getInstance().getCommonGson().fromJson(jsonObject, getClassOfT());
    }
}
