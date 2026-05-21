package com.meishe.sdkdemo.mimodemo.common.template.model;

import android.text.TextUtils;


import com.meishe.sdkdemo.mimodemo.common.template.utils.MiMoFileUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MiMoInfo implements Serializable {
    /**
     * 重置模板选择的视频数据信息
     * Reset the video data selected by the template
     */
    public void resetTemplateVideoInfo() {
        if (shotDataInfos == null || shotDataInfos.isEmpty()) {
            return;
        }
        int shotCount = shotDataInfos.size();
        for (int shotIndex = 0; shotIndex < shotCount; shotIndex++) {
            ShotDataInfo shotDataInfo = shotDataInfos.get(shotIndex);
            if (shotDataInfo == null) {
                continue;
            }
            ShotVideoInfo mainTrackVideoInfo = shotDataInfo.getMainTrackVideoInfo();
            if (mainTrackVideoInfo != null) {
                mainTrackVideoInfo.resetShotVideoInfo();
            }
            List<ShotVideoInfo> subTrackVideoInfo = shotDataInfo.getSubTrackVideoInfos();
            if (subTrackVideoInfo != null && !subTrackVideoInfo.isEmpty()) {
                int videoCount = subTrackVideoInfo.size();
                for (int videoIndex = 0; videoIndex < videoCount; videoIndex++) {
                    ShotVideoInfo shotVideoInfo = subTrackVideoInfo.get(videoIndex);
                    if (shotVideoInfo == null) {
                        continue;
                    }
                    shotVideoInfo.resetShotVideoInfo();
                }
            }
        }
    }

    /**
     * 获取模板里面所有的镜头视频信息列表
     * Gets a list of all shots and videos in the template
     *
     * @return
     */
    public List<ShotVideoInfo> getTotalShotVideoInfos() {
        return totalShotVideoInfos;
    }

    private void clearTotalShotVideoInfos() {
        totalShotVideoInfos.clear();
    }

    /**
     * 更新模板里面所有的镜头视频信息列表
     * Update the list of all shots and videos in the template
     */
    public void updateTotalShotVideoInfos() {
        clearTotalShotVideoInfos();
        if (shotDataInfos == null || shotDataInfos.isEmpty()) {
            return;
        }
        int shotCount = shotDataInfos.size();
        for (int shotIndex = 0; shotIndex < shotCount; shotIndex++) {
            ShotDataInfo shotDataInfo = shotDataInfos.get(shotIndex);
            if (shotDataInfo == null) {
                continue;
            }
            ShotVideoInfo mainTrackVideoInfo = shotDataInfo.getMainTrackVideoInfo();
            if (mainTrackVideoInfo != null) {
                totalShotVideoInfos.add(mainTrackVideoInfo);
            }
            List<ShotVideoInfo> subTrackVideoInfo = shotDataInfo.getSubTrackVideoInfos();
            if (subTrackVideoInfo != null && !subTrackVideoInfo.isEmpty()) {
                boolean isTimelineTransSource = false;
                String clipPath = subTrackVideoInfo.get(0).getVideoClipPath();
                if (!TextUtils.isEmpty(clipPath) && clipPath.contains(MiMoFileUtils.PATH_ASSETS)) {
                    isTimelineTransSource = true;
                }
                //跳过时间线扫换默认素材源
                //Skip the timeline to scan the default material source
                if (isTimelineTransSource) {
                    continue;
                }
                totalShotVideoInfos.addAll(subTrackVideoInfo);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public int getShotsNumber() {
        return shotsNumber;
    }

    public void setShotsNumber(int shotsNumber) {
        this.shotsNumber = shotsNumber;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public float getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(float musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getTitleFilter() {
        return titleFilter;
    }

    public void setTitleFilter(String titleFilter) {
        this.titleFilter = titleFilter;
    }

    public float getTitleFilterDuration() {
        return titleFilterDuration;
    }

    public void setTitleFilterDuration(float titleFilterDuration) {
        this.titleFilterDuration = titleFilterDuration;
    }

    public String getTitleCaption() {
        return titleCaption;
    }

    public void setTitleCaption(String titleCaption) {
        this.titleCaption = titleCaption;
    }

    public float getTitleCaptionDuration() {
        return titleCaptionDuration;
    }

    public void setTitleCaptionDuration(float titleCaptionDuration) {
        this.titleCaptionDuration = titleCaptionDuration;
    }

    public String getEndingFilter() {
        return endingFilter;
    }

    public void setEndingFilter(String endingFilter) {
        this.endingFilter = endingFilter;
    }

    public float getEndingFilterLen() {
        return endingFilterLen;
    }

    public void setEndingFilterLen(float endingFilterLen) {
        this.endingFilterLen = endingFilterLen;
    }

    public String getTimelineFilter() {
        return timelineFilter;
    }

    public void setTimelineFilter(String timelineFilter) {
        this.timelineFilter = timelineFilter;
    }

    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    public void setTranslation(List<Translation> translation) {
        this.translation = translation;
    }

    public List<ShotInfo> getShotInfos() {
        return shotInfos;
    }

    public void setShotInfos(List<ShotInfo> shotInfos) {
        this.shotInfos = shotInfos;
    }

    public boolean isTimelineTrans() {
        return isTimelineTrans;
    }

    public void setTimelineTrans(boolean timelineTrans) {
        isTimelineTrans = timelineTrans;
    }

    public String getTimeLineTransSource() {
        return timeLineTransSource;
    }

    public void setTimeLineTransSource(String timeLineTransSource) {
        this.timeLineTransSource = timeLineTransSource;
    }

    /**
     * 模板名称
     * Template name
     */
    private String name;
    /**
     * 模板封面图
     * Template cover
     */
    private String cover;
    /**
     * 模板预览视频
     * Template preview video
     */
    private String preview;
    /**
     * 模板片段数量
     * Number of template fragments
     */
    private int shotsNumber;
    /**
     * 音乐名称
     * Music name
     */
    private String music;
    /**
     * 音乐持续时间，脚本里所有时间都按照毫秒写的
     * The duration of the music. All The Times in the script are in milliseconds
     */
    private float musicDuration;
    /**
     * 片头水印贴纸Id
     * Title watermark sticker Id
     */
    private String titleFilter;
    /**
     * 片头水印贴纸持续时间
     * Opening watermark sticker duration
     */
    private float titleFilterDuration;
    /**
     * 片头字幕
     * Opening credits
     */
    private String titleCaption;
    /**
     * 片头字幕持续时间
     * Opening credits duration
     */
    private float titleCaptionDuration;
    /**
     * 片尾压黑滤镜
     * End press black filter
     */
    private String endingFilter;
    /**
     * 片尾压黑滤镜持续时间
     * End press black filter duration
     */
    private float endingFilterLen;
    /**
     * 全轨滤镜，始终存在不受片段影响
     * Full-track filter, always present is not affected by the fragment
     */
    private String timelineFilter;
    /**
     * 模板支持的画幅
     * Template supported frame
     */
    private String supportedAspectRatio;

    /**
     * 是否是时间线扫换模板
     * Whether to scan the timeline template
     */
    private boolean isTimelineTrans;
    /**
     * 时间线扫换，镜头单轨是，添加多轨默认素材源名称
     * Timeline sweep, lens Monorail Yes, add multitrack default source name
     */
    private String timeLineTransSource;
    /**
     * 场景名称
     * Scene name
     */
    private List<Translation> translation;
    /**
     * 镜头片段信息
     * Shot segment information
     */
    private List<ShotInfo> shotInfos;

    /**
     * 额外添加的数据，非模板里面的字段
     * Additional data that is not a field in the template
     *
     * @return
     */
    public MiMoExtraDataInfo getExtraDataInfo() {
        return extraDataInfo;
    }

    public void setExtraDataInfo(MiMoExtraDataInfo extraDataInfo) {
        this.extraDataInfo = extraDataInfo;
    }

    public List<ShotDataInfo> getShotDataInfos() {
        return shotDataInfos;
    }

    public void setShotDataInfos(List<ShotDataInfo> shotDataInfos) {
        this.shotDataInfos = shotDataInfos;
    }

    public String getTransSourcePath() {
        return transSourcePath;
    }

    public void setTransSourcePath(String transSourcePath) {
        this.transSourcePath = transSourcePath;
    }

    MiMoExtraDataInfo extraDataInfo;
    /**
     * 镜头视频数据,可能包含主轨道，子轨道数据
     * Lens video data, may contain primary track, subtrack data
     */
    private List<ShotDataInfo> shotDataInfos;

    /**
     * 当前镜头所有的视频数据
     * All video data for the current shot
     */
    List<ShotVideoInfo> totalShotVideoInfos = new ArrayList<>();
    /**
     * 扫换源素材路径
     * Scan the source material path
     */
    private String transSourcePath;
}
