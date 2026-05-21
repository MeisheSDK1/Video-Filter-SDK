package com.meishe.base.bean;

/**
 * author : lhz
 * date   : 2020/9/2
 * desc   :标识
 * 媒体标识类
 * Media identification class
 */
public class MediaTag {
    private int type;
    /**
     * 在集合中的索引
     * The index in the collection
     */
    private int index = -1;

    /**
     * Gets type.
     * 获取类型
     *
     * @return the type
     */
    public int getType() {
        return type;
    }


    public MediaTag setType(int type) {
        this.type = type;
        return this;
    }


    public int getIndex() {
        return index;
    }


    public MediaTag setIndex(int index) {
        this.index = index;
        return this;
    }
}
