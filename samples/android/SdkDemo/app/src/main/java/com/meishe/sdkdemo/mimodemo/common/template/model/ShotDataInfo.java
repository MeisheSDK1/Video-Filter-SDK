package com.meishe.sdkdemo.mimodemo.common.template.model;

import java.util.List;

/**
 * 存储镜头里的数据信息，包括主轨道，子轨道数据
 * Store the data information in the lens, including the main track, the subtrack data
 */
public class ShotDataInfo {
    public ShotVideoInfo getMainTrackVideoInfo() {
        return mainTrackVideoInfo;
    }

    public void setMainTrackVideoInfo(ShotVideoInfo mainTrackVideoInfo) {
        this.mainTrackVideoInfo = mainTrackVideoInfo;
    }

    public List<ShotVideoInfo> getSubTrackVideoInfos() {
        return subTrackVideoInfos;
    }

    public void setSubTrackVideoInfos(List<ShotVideoInfo> subTrackVideoInfos) {
        this.subTrackVideoInfos = subTrackVideoInfos;
    }

    /**
     * 主轨视频信息
     * Main track video information
     */
    private ShotVideoInfo mainTrackVideoInfo;
    /**
     * 子轨视频信息，可能包含多个子轨道
     * Subtrack video information, which may contain multiple subtracks
     */
    private List<ShotVideoInfo> subTrackVideoInfos;
}
