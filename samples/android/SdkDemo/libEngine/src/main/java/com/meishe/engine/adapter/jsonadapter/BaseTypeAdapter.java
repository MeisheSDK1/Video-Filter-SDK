package com.meishe.engine.adapter.jsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.meishe.engine.adapter.LGsonContext;

import java.io.IOException;

/**
 * author：yangtailin on 2020/8/15 17:14
 * 基础适配器类型
 *Base adapter type
 * @param <T> the type parameter
 */
public abstract class BaseTypeAdapter<T> extends TypeAdapter<T> {

    @Override
    public void write(JsonWriter out, T value) throws IOException {
    }

    @Override
    public T read(JsonReader in) {
        JsonElement jsonElement = Streams.parse(in);
        JsonElement jsonObject = jsonElement.getAsJsonObject();
        jsonObject = parseReadData(jsonObject);
        //LogUtils.d( "read: jsonObject = "+jsonObject );
        return parseToObject(jsonObject);
    }

    /**
     * Parse to object t.
     * 解析到对象t
     * @param jsonObject the json object
     * @return the t
     */
    protected T parseToObject(JsonElement jsonObject){
        return LGsonContext.getInstance().getCommonGson().fromJson(jsonObject, getClassOfT());
    }

    /**
     * 对数据进行修改，用于适配旧数据
     * Modification of data to accommodate older data
     * @param jsonObject the json object
     * @return json element
     */
    protected JsonElement parseReadData(JsonElement jsonObject){
        return jsonObject;
    }

    /**
     * Gets class of t.
     * 得到t的类
     * @return the class of t
     */
    public abstract Class<T> getClassOfT();

}
