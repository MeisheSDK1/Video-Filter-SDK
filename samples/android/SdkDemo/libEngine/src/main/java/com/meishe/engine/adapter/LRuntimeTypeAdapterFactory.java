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
import com.meishe.engine.adapter.jsonadapter.BaseTypeAdapter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author：yangtailin on 2020/7/8 17:08
 * 运行时类型适配器工厂
 * The runtime type adapter factory
 * @param <T> the type parameter
 */
public final class LRuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, TypeAdapter<?>> labelToTypeAdapter = new LinkedHashMap<>();
    private final Map<Class<?>,TypeAdapter<?>> subtypeToTypeAdapter = new LinkedHashMap<>();
    private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();

    private LRuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        if (typeFieldName == null || baseType == null) {
            throw new NullPointerException();
        }
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    /**
     * Of l runtime type adapter factory.
     * 运行时类型适配器工厂
     * @param <T>           the type parameter 类型参数
     * @param baseType      the base type 基类型
     * @param typeFieldName the type field name 类型字段名
     * @return the l runtime type adapter factory 运行时类型适配器工厂
     */
    public static <T> LRuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new LRuntimeTypeAdapterFactory<>(baseType, typeFieldName);
    }

    /**
     * Self l runtime type adapter factory.
     * 运行时类型适配器工厂
     * @param typeAdapter the type adapter 类型的适配器
     * @param type        the type 类型
     * @param label       the label 标签
     * @return the l runtime type adapter factory 运行时类型适配器工厂
     */
    public LRuntimeTypeAdapterFactory<T> self (BaseTypeAdapter<? extends T> typeAdapter, Class<? extends T> type, String label) {
        if (typeAdapter == null || label == null || type == null) {
            throw new NullPointerException();
        }
        if (labelToTypeAdapter.containsKey(label) || subtypeToTypeAdapter.containsKey(type)) {
            throw new IllegalArgumentException("types and labels must be unique");
        }
        labelToTypeAdapter.put(label, typeAdapter);
        subtypeToTypeAdapter.put(type, typeAdapter);
        labelToSubtype.put(label, type);
        return this;
    }


    /**
     * Register sub type l runtime type adapter factory.
     * 注册子类型l运行时类型适配器工厂
     * @param typeAdapter the type adapter 类型的适配器
     * @param type        the type 类型
     * @param label       the label 标签
     * @return the l runtime type adapter factory 运行时类型适配器工厂
     */
    public LRuntimeTypeAdapterFactory<T> registerSubType(BaseTypeAdapter<? extends T> typeAdapter, Class<? extends T> type, String label) {
        if (typeAdapter == null || label == null || type == null) {
            throw new NullPointerException();
        }
        if (labelToTypeAdapter.containsKey(label) || subtypeToTypeAdapter.containsKey(type)) {
            throw new IllegalArgumentException("types and labels must be unique");
        }
        labelToTypeAdapter.put(label, typeAdapter);
        subtypeToTypeAdapter.put(type, typeAdapter);
        labelToSubtype.put(label, type);
        return this;
    }

    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (type.getRawType() != baseType) {
            return null;
        }
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate
                = new LinkedHashMap<>();
        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        return new TypeAdapter<R>() {
            @Override
            public R read(JsonReader in) {
                JsonElement jsonElement = Streams.parse(in);
                JsonElement labelJsonElement = jsonElement.getAsJsonObject().get(typeFieldName);
                if (labelJsonElement == null) {
                    throw new JsonParseException("cannot deserialize " + baseType
                            + " because it does not define a field named " + typeFieldName);
                }
                String label = labelJsonElement.getAsString();
                @SuppressWarnings("unchecked") // registration requires that subtype extends T
                TypeAdapter<R> delegate = (BaseTypeAdapter<R>) labelToTypeAdapter.get(label);
                if (delegate == null) {
                    throw new JsonParseException("cannot deserialize " + baseType + " subtype named "
                            + label + "; did you forget to register a subtype?");
                }
                //return delegate.read(in);
                return delegate.fromJsonTree(jsonElement);
            }

            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
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