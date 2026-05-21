package com.meishe.engine.adapter;

/**
 * author：yangtailin on 2020/8/11 17:08
 * TimelineData数据转成磁盘数据适配器，只有写逻辑
 * The TimelineData data was converted to a disk data adapter with only write logic
 * @param <T> the type parameter
 */
public interface TimelineDataToLocalAdapter<T> {
    /**
     * Parse to local data t.
     * 解析到本地数据t
     * @return the t
     */
    T parseToLocalData();
}
