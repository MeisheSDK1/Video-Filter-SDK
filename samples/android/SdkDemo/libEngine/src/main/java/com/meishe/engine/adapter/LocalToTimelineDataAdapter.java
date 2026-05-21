package com.meishe.engine.adapter;

/**
 * author：yangtailin on 2020/8/11 17:08
 * 本地数据转成TimelineData数据适配器，只有读逻辑
 * The local data is converted to the TimelineData data adapter with only read logic
 */
public interface LocalToTimelineDataAdapter<T> {
    T parseToTimelineData();
}
