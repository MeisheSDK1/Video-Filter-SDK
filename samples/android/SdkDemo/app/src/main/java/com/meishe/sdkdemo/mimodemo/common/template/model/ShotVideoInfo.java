package com.meishe.sdkdemo.mimodemo.common.template.model;

import android.text.TextUtils;


import com.meishe.sdkdemo.mimodemo.common.template.utils.MiMoFileUtils;

import java.util.List;

/**
 * 存储镜头视频数据信息
 * Store shot video data information
 */
public class ShotVideoInfo {
    public void resetShotVideoInfo() {
        fileDuration = 0;
        /**
         * 如果视频源素材是时间线默认扫换源，数据不重置
         * If the video source material is the timeline, the data is not reset
         */
        if (!TextUtils.isEmpty(videoClipPath) && !videoClipPath.contains(MiMoFileUtils.PATH_ASSETS)) {
            videoClipPath = null;
        }
        converClipPath = null;
        updateClipTrimIn(0);
    }

    public void updateClipTrimIn(long trimIn) {
        if (trackClipInfos == null || trackClipInfos.isEmpty()) {
            return;
        }
        this.trimIn = trimIn;
        long newTrimIn = trimIn;
        int clipCount = trackClipInfos.size();
        for (int clipIndex = 0; clipIndex < clipCount; clipIndex++) {
            TrackClipInfo trackClipInfo = trackClipInfos.get(clipIndex);
            if (trackClipInfo == null) {
                continue;
            }
            boolean isReverse = trackClipInfo.isReverse();
            if (isReverse) {
                //需要倒放操作的片段，跳过更新裁剪入点
                //The fragment that needs to be inverted, skipping the update cropping entry point
                continue;
            }
            int reapeatFlag = trackClipInfo.getRepeatFlag();
            if (reapeatFlag > 0 && reapeatFlag % 3 == 0) {
                //反复里面第二个正放片段特殊处理
                //Repeat inside the second playing segment special treatment
                long curRealNeedDuration = trackClipInfo.getRealNeedDuration();
                trackClipInfo.setTrimIn(newTrimIn - curRealNeedDuration);
                continue;
            }
            trackClipInfo.setTrimIn(newTrimIn);
            long realNeedDuration = trackClipInfo.getRealNeedDuration();
            newTrimIn += realNeedDuration;
        }
    }

    public ShotVideoInfo(List<TrackClipInfo> trackClipInfos,
                         long realNeedDuration,
                         long durationBySpeed,
                         int shot,
                         int trackIndex,
                         boolean isConvertFlag,
                         String filter,
                         long duration) {
        this.trackClipInfos = trackClipInfos;
        this.realNeedDuration = realNeedDuration;
        this.durationBySpeed = durationBySpeed;
        this.shot = shot;
        this.trackIndex = trackIndex;
        this.isConvertFlag = isConvertFlag;
        this.filter = filter;
        this.duration = duration;
    }

    public int getShot() {
        return shot;
    }

    public void setShot(int shot) {
        this.shot = shot;
    }

    public String getVideoClipPath() {
        return videoClipPath;
    }

    public void setVideoClipPath(String videoClipPath) {
        this.videoClipPath = videoClipPath;
    }

    public long getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(long fileDuration) {
        this.fileDuration = fileDuration;
    }

    public long getRealNeedDuration() {
        return realNeedDuration;
    }

    public void setRealNeedDuration(long realNeedDuration) {
        this.realNeedDuration = realNeedDuration;
    }

    public long getDurationBySpeed() {
        return durationBySpeed;
    }

    public void setDurationBySpeed(long durationBySpeed) {
        this.durationBySpeed = durationBySpeed;
    }

    public String getConverClipPath() {
        return converClipPath;
    }

    public void setConverClipPath(String converClipPath) {
        this.converClipPath = converClipPath;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public List<TrackClipInfo> getTrackClipInfos() {
        return trackClipInfos;
    }

    public void setTrackClipInfos(List<TrackClipInfo> trackClipInfos) {
        this.trackClipInfos = trackClipInfos;
    }

    public boolean isConvertFlag() {
        return isConvertFlag;
    }

    public void setConvertFlag(boolean convertFlag) {
        isConvertFlag = convertFlag;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private int shot;
    /**
     * 源视频文件路径
     * Source video file path
     */
    private String videoClipPath;
    /**
     * 源视频文件时长
     * Duration of the source video file
     */
    private long fileDuration;
    /**
     * 片段滤镜
     * Fragment filter
     */
    private String filter;
    /**
     * 片段滤镜时长
     * Segment filter duration
     */
    private long duration;
    /**
     * 实际需要的时长
     * The actual time required
     */
    private long realNeedDuration;
    /**
     * 变速后的时长
     * How long it takes to change gears
     */
    private long durationBySpeed;
    /**
     * 转码后的视频文件路径
     * Video file path after transcoding
     */
    private String converClipPath;
    /**
     * 轨道索引
     * Track index
     */
    private int trackIndex;
    /**
     * 根据变速列表拆分的视频片段列表，存储视频片段信息
     * 当前片段需要
     * The video clip list is split according to the variable speed list to store video clip information
     * Current fragment needs
     */
    private List<TrackClipInfo> trackClipInfos;
    /**
     * 是否转码标识
     * Transcoding ID or not
     */
    private boolean isConvertFlag;
    /**
     * 对源视频的裁剪入点值
     * The clip-in point value for the source video
     */
    private long trimIn;
}
