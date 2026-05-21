package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.local.LMeicamAdjustData;

/**
 * author：yangtailin on 2020/8/18 14:59
 * Meicam调整数据适配器
 * Meicam adjusts the data adapter
 */
public class LMeicamAdjsutDataAdapter extends BaseTypeAdapter<LMeicamAdjustData> {
    private static final String TAG = "LMeicamAdjsutDataAdapter";

    public final static String[] OLD_KEY = new String[]{"brightness", "contrast", "saturation", "highlight", "shadow", "blackPoint", "degree", "amount", "temperature", "tint"};
    public final static String[] NEW_KEY = new String[]{"Brightness", "Contrast", "Saturation", "Highlight", "Shadow", "Blackpoint", "degree", "Amount", "Temperature", "Tint"};

    @Override
    public Class<LMeicamAdjustData> getClassOfT() {
        return LMeicamAdjustData.class;
    }

    @Override
    protected JsonElement parseReadData(JsonElement jsonObject) {
        JsonObject asJsonObject = jsonObject.getAsJsonObject();
       // LogUtils.d( "parseReadData before : " + asJsonObject);
        for (int index = 0; index < OLD_KEY.length; index++) {
            String oldKey = OLD_KEY[index];
            String newKey = NEW_KEY[index];
            JsonElement oldElement = asJsonObject.remove(oldKey);
            if (oldElement != null) {
                asJsonObject.addProperty(newKey, oldElement.getAsString());
            }
        }
        //LogUtils.d("parseReadData after : " + asJsonObject);
        return asJsonObject;
    }

    @Override
    protected LMeicamAdjustData parseToObject(JsonElement jsonObject) {
        return LGsonContext.getInstance().getCommonGson().fromJson(jsonObject, getClassOfT());
    }
}
