package com.meishe.base.utils;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/04/05
 *     desc  : utils about gson
 * </pre>
 * Gson工具类
 */
public final class GsonUtils {

    private static final String KEY_DEFAULT   = "defaultGson";
    private static final String KEY_DELEGATE  = "delegateGson";
    private static final String KEY_LOG_UTILS = "logUtilsGson";

    private static final Map<String, Gson> GSONS = new ConcurrentHashMap<>();

    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    public static void setGsonDelegate(Gson delegate) {
        if (delegate == null) {
            return;
        }
        GSONS.put(KEY_DELEGATE, delegate);
    }


    public static void setGson(final String key, final Gson gson) {
        if (TextUtils.isEmpty(key) || gson == null) {
            return;
        }
        GSONS.put(key, gson);
    }


    public static Gson getGson(final String key) {
        return GSONS.get(key);
    }

    /**
     * Gets gson.
     * 获取gson
     * @return the gson
     */
    public static Gson getGson() {
        Gson gsonDelegate = GSONS.get(KEY_DELEGATE);
        if (gsonDelegate != null) {
            return gsonDelegate;
        }
        Gson gsonDefault = GSONS.get(KEY_DEFAULT);
        if (gsonDefault == null) {
            gsonDefault = createGson();
            GSONS.put(KEY_DEFAULT, gsonDefault);
        }
        return gsonDefault;
    }

    /**
     * Serializes an object into json.
     * 将对象序列化为json
     * @param object The object to serialize. 要序列化的对象
     * @return object serialized into json. 对象序列化为json
     */
    public static String toJson(final Object object) {
        return toJson(getGson(), object);
    }


    public static String toJson(final Object src, @NonNull final Type typeOfSrc) {
        return toJson(getGson(), src, typeOfSrc);
    }


    public static String toJson(@NonNull final Gson gson, final Object object) {
        return gson.toJson(object);
    }

    public static String toJson(@NonNull final Gson gson, final Object src, @NonNull final Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }


    public static <T> T fromJson(final String json, @NonNull final Class<T> type) {
        return fromJson(getGson(), json, type);
    }


    public static <T> T fromJson(final String json, @NonNull final Type type) {
        return fromJson(getGson(), json, type);
    }


    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Class<T> type) {
        return fromJson(getGson(), reader, type);
    }


    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Type type) {
        return fromJson(getGson(), reader, type);
    }


    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Class<T> type) {
        return gson.fromJson(json, type);
    }


    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Type type) {
        return gson.fromJson(json, type);
    }


    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Class<T> type) {
        return gson.fromJson(reader, type);
    }


    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Type type) {
        return gson.fromJson(reader, type);
    }


    public static Type getListType(@NonNull final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }


    public static Type getSetType(@NonNull final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }


    public static Type getMapType(@NonNull final Type keyType, @NonNull final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }


    public static Type getArrayType(@NonNull final Type type) {
        return TypeToken.getArray(type).getType();
    }


    public static Type getType(@NonNull final Type rawType, @NonNull final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    /**
     * Gets gson 4 log utils.
     * 得到gson 4 log utils
     * @return the gson 4 log utils
     */
    static Gson getGson4LogUtils() {
        Gson gson4LogUtils = GSONS.get(KEY_LOG_UTILS);
        if (gson4LogUtils == null) {
            gson4LogUtils = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            GSONS.put(KEY_LOG_UTILS, gson4LogUtils);
        }
        return gson4LogUtils;
    }

    private static Gson createGson() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    }
}

