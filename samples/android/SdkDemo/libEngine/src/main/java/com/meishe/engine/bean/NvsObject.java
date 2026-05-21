package com.meishe.engine.bean;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/3 17:10
 */
public class NvsObject<T> implements Cloneable, Serializable {
    /**
     * SDK的底层对象
     * The underlying object of the SDK
     */
    private transient T mObject;

    public NvsObject() {
    }

    public NvsObject(T object) {
        mObject = object;
    }

    public T getObject() {
        return mObject;
    }

    public void setObject(T object) {
        mObject = object;
    }

    public void loadData(T object){}
}
