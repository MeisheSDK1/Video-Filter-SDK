package com.meishe.base.bean;

import com.meishe.third.adpater.entity.SectionEntity;

/**
 * author : lhz
 * date   : 2020/11/5
 * desc   :媒体部分分类实体类
 * The media section classifies the entity classes
 */
public class MediaSection extends SectionEntity<MediaData> {
    /**
     * 如果有什么属性，可以在此类中写，不要写到MediaData中
     * If there are any properties you can write in this class, do not write to MediaData
     */
    public MediaSection(MediaData mediaData) {
        super(mediaData);
    }
}
