package com.meishe.sdkdemo.themeshoot.bean;

import android.graphics.Bitmap;

import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/5.
 * @Description :主题拍摄实体bean。ThemePreviewBean
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemePreviewBean {
    public static final int THEME_TYPE_START = 100;
    public static final int THEME_TYPE_NORMAL = 101;
    public static final int THEME_TYPE_END = 102;
    /**
     * 名称
     * name
     */
    String name;
    /**
     * 背景图地址
     * Background image address
     */
    String bgUrl;
    /**
     * 显示图Bitmap
     * Display the diagram Bitmap
     */
    Bitmap bitmap;
    /**
     * 总时长
     * Total duration
     */
    long duration;
    /**
     * 开始时长
     * Starting time
     */
    long startDuration;
    /**
     * 字幕文字
     * Subtitle text
     */
    String comText;
    /**
     * 滤镜id
     * Filter id
     */
    String filterId;
    int filterIndex;
    /**
     * 转场添加位置
     * Transition adds position
     */
    int transCount;
    /**
     * 片头 片尾
     * First and last
     */
    int type = THEME_TYPE_NORMAL;
    /**
     * 字幕
     * subtitle
     */
    NvsTimelineCompoundCaption compoundCaption;
    /**
     * 添加的特效，用于替换时使用
     * Added special effects for use when replacing
     */
    NvsTimelineVideoFx videoFx;

    public NvsTimelineVideoFx getVideoFx() {
        return videoFx;
    }

    public int getType() {
        return type;
    }

    public NvsTimelineCompoundCaption getCompoundCaption() {
        return compoundCaption;
    }

    public void setCompoundCaption(NvsTimelineCompoundCaption compoundCaption) {
        this.compoundCaption = compoundCaption;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTransCount() {
        return transCount;
    }

    public void setTransCount(int transCount) {
        this.transCount = transCount;
    }

    public void setVideoFx(NvsTimelineVideoFx videoFx) {
        this.videoFx = videoFx;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartDuration() {
        return startDuration;
    }

    public void setStartDuration(long startDuration) {
        this.startDuration = startDuration;
    }

    public String getComText() {
        return comText;
    }

    public void setComText(String comText) {
        this.comText = comText;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public int getFilterIndex() {
        return filterIndex;
    }

    public void setFilterIndex(int filterIndex) {
        this.filterIndex = filterIndex;
    }
}
