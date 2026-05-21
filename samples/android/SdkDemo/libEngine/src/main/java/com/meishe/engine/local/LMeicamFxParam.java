package com.meishe.engine.local;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamFxParam;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/3 21:46
 */
public class LMeicamFxParam<T> implements Cloneable , Serializable, LocalToTimelineDataAdapter<MeicamFxParam<T>> {
    String type;
    String key;
    /**
     * 这个可能是float 或者 boolean 或者是一组数字（region）
     * This could be a float or a boolean or a set of numbers (region)
     */
    T value;


    public LMeicamFxParam(String type, String key, T value) {
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
    public MeicamFxParam<T> parseToTimelineData() {
        MeicamFxParam local = new MeicamFxParam(getType(), getKey(),getValue());
        return local;
    }
}
