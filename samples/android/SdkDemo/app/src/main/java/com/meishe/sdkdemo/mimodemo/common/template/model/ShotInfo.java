package com.meishe.sdkdemo.mimodemo.common.template.model;

import java.util.List;

/**
 * 镜头片段数据类
 * Shot segment data class
 */
public class ShotInfo {
    public int getShot() {
        return shot;
    }

    public void setShot(int shot) {
        this.shot = shot;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getMainTrackFilter() {
        return mainTrackFilter;
    }

    public void setMainTrackFilter(String mainTrackFilter) {
        this.mainTrackFilter = mainTrackFilter;
    }

    public List<SubTrackFilterInfo> getSubTrackFilter() {
        return subTrackFilter;
    }

    public void setSubTrackFilter(List<SubTrackFilterInfo> subTrackFilter) {
        this.subTrackFilter = subTrackFilter;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public List<SpeedInfo> getSpeed() {
        return speed;
    }

    public void setSpeed(List<SpeedInfo> speed) {
        this.speed = speed;
    }

    public List<RepeatInfo> getRepeat() {
        return repeat;
    }

    public void setRepeat(List<RepeatInfo> repeat) {
        this.repeat = repeat;
    }

    /**
     * 镜头号
     * Lens number
     */
    private int shot;
    /**
     * 片段滤镜效果，只作用于这一个片段
     * The fragment filter effect applies only to this fragment
     */
    private String filter;
    /**
     * 转场，不写效果默认为硬切
     * Transition, do not write effect default to hard cut
     */
    private String trans;

    /**
     * 分屏不同画面的滤镜，mainTrackFilter为最底层画面，subTrackFilter为在底层画面上增加的画面
     * Split screen filter for different screen, mainTrackFilter is the bottom screen, subTrackFilter is the screen added on the bottom screen
     */
    private String mainTrackFilter;
    /**
     * 子轨道滤镜
     * Subtrack filter
     */
    private List<SubTrackFilterInfo> subTrackFilter;
    /**
     * 片段持续时间,单位是毫秒，变速后的时长，需要计算变速前的视频片段的时长
     * Segment duration, in milliseconds, after the shift. The duration of the video segment before the shift needs to be calculated
     */
    private float duration;
    /**
     * 表示片段视频是否倒放，默认为false
     * Indicates whether the clip is played backwards. The default value is false
     */
    private boolean reverse;
    /**
     * 变速
     * Speed change
     */
    private List<SpeedInfo> speed;
    /**
     * 画面反复
     * Picture repetition
     */
    private List<RepeatInfo> repeat;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isCanPlaced() {
        return mCanPlaced;
    }

    public void setCanPlaced(boolean mCanPlaced) {
        this.mCanPlaced = mCanPlaced;
    }

    public long getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(long fileDuration) {
        this.fileDuration = fileDuration;
    }

    public long getNeedDuration() {
        return needDuration;
    }

    public void setNeedDuration(long needDuration) {
        this.needDuration = needDuration;
    }

    public String getTransLen() {
        return transLen;
    }

    public void setTransLen(String transLen) {
        this.transLen = transLen;
    }

    public String getCompoundCaption() {
        return compoundCaption;
    }

    public void setCompoundCaption(String compoundCaption) {
        this.compoundCaption = compoundCaption;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }

    /**
     * 裁剪入点
     * Clipping entry point
     */
    private long trimIn;
    private String source;
    private boolean mCanPlaced;
    /**
     * 视频文件时长，单位为毫秒
     * Video file duration, expressed in milliseconds
     */
    private long fileDuration;
    /**
     * 实际需要的时长，单位为毫秒
     * The actual time required, in milliseconds
     */
    private long needDuration;
    private String transLen;
    private String compoundCaption;

}
