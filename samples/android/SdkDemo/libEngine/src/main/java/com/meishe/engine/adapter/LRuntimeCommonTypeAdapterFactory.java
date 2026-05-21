package com.meishe.engine.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

/**
 * author：yangtailin on 2020/7/8 17:08
 * 运行时公共类型适配器工厂类
 * The runtime common type adapter factory class
 * @param <T> the type parameter
 */
public final class LRuntimeCommonTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private TypeAdapter<?> adapter;

    private LRuntimeCommonTypeAdapterFactory(Class<?> baseType) {
        if (baseType == null) {
            throw new NullPointerException();
        }
        this.baseType = baseType;
    }

    /**
     * Of l runtime common type adapter factory.
     * 运行时公共类型适配器工厂
     * @param <T>      the type parameter 类型参数
     * @param baseType the base type 基类型
     * @return the l runtime common type adapter factory 运行时公共类型适配器工厂
     */
    public static <T> LRuntimeCommonTypeAdapterFactory<T> of(Class<T> baseType) {
        return new LRuntimeCommonTypeAdapterFactory<T>(baseType);
    }

    /**
     * Register adapter l runtime common type adapter factory.
     * 注册适配器l运行时通用类型适配器工厂
     * @param typeAdapter the type adapter 类型的适配器
     * @return the l runtime common type adapter factory 运行时公共类型适配器工厂
     */
    public LRuntimeCommonTypeAdapterFactory<T> registerAdapter(TypeAdapter<? extends T> typeAdapter) {
        if (typeAdapter == null) {
            throw new NullPointerException();
        }
        adapter = typeAdapter;
        return this;
    }


    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (type.getRawType() != baseType) {
            return null;
        }

        final TypeAdapter<R> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<R>() {
            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement jsonElement = Streams.parse(in);
                if (adapter == null) {
                    throw new JsonParseException("cannot deserialize " + baseType + " subtype named "
                            + "; did you forget to register a subtype?");
                }
                TypeAdapter<R> delegate = (TypeAdapter<R>) adapter;
                return delegate.fromJsonTree(jsonElement);
            }

            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                if (delegate == null) {
                    throw new JsonParseException("cannot serialize " + srcType.getName()
                            + "; did you forget to register a subtype?");
                }
                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                JsonObject clone = new JsonObject();
                for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                    clone.add(e.getKey(), e.getValue());
                }
                Streams.write(clone, out);

            }
        }.nullSafe();
    }
}