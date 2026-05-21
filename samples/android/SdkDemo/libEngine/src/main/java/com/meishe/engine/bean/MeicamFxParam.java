package com.meishe.engine.bean;

import android.annotation.SuppressLint;

import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMeicamFxParam;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/3 21:46
 */
public class MeicamFxParam<T> implements Cloneable , Serializable, TimelineDataToLocalAdapter<LMeicamFxParam> {
    private final static String TAG = "MeicamFxParam";
    public final static String TYPE_STRING = "string";
    public final static String TYPE_STRING_OLD = "String";
    public final static String TYPE_BOOLEAN = "boolean";
    public final static String TYPE_FLOAT = "float";
    public final static String TYPE_OBJECT = "Object";
    /**
     * object 或其他
     * object or other
     */
    String type;
    String key;
    /**
     * 这个可能是float 或者 boolean 或者是一组数字（region）
     * This could be a float or a boolean or a set of numbers (region)
     */
    T value;

    @SuppressLint("NewApi")
    public MeicamFxParam(String type, String key, T value) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public LMeicamFxParam parseToLocalData() {
        return new LMeicamFxParam<>(getType(), getKey(),getValue());
    }
}
