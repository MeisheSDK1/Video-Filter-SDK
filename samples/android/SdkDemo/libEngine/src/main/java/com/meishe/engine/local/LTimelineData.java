package com.meishe.engine.local;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.constant.NvsConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 17:09
 */
public class LTimelineData implements LocalToTimelineDataAdapter<TimelineData> {
    private long duration;
    @SerializedName("resources")
    private List<LMeicamResource> mMeicamResourceList;
    @SerializedName("videoTracks")
    private List<LMeicamVideoTrack> mMeicamVideoTrackList;
    @SerializedName("audioTracks")
    private List<LMeicamAudioTrack> mMeicamAudioTrackList;
    @SerializedName("stickerCaptionTracks")
    private List<LMeicamStickerCaptionTrack> mMeicamStickerCaptionTrackList;
    @SerializedName("timelineVideoFxTracks")
    private List<LMeicamTimelineVideoFxTrack> mMeicamTimelineVideoFxTrackList;
    /**
     * 模糊 马赛克 目前只能添加一个
     * Blur Mosaic can only be added one at present
     */
    @SerializedName("timelineVideoFxClips")
    private List<LMeicamTimelineVideoFxClip> mMeicamTimelineVideoFxClipList;
    @SerializedName("waterMark")
    private LMeicamWaterMark mMeicamWaterMark;
    @SerializedName("theme")
    private LMeicamTheme mMeicamTheme;
    /**
     * 调节对应的信息
     * Adjust the corresponding information
     */
    @SerializedName("adjustData")
    private LMeicamAdjustData mMeicamAdjustData = new LMeicamAdjustData();

    @SerializedName("filterFx")
    private LMeicamTimelineVideoFx mFilterFx;
    /**
     * 草稿ID
     * Draft ID
     */
    @SerializedName("projectId")
    private String mProjectId;
    @SerializedName("projectName")
    private String mProjectName;
    @SerializedName("lastModifiedTime")
    private String mLastModifiedTime;
    @SerializedName("projectDuring")
    private String mProjectDuring;
    @SerializedName("aspectRatioMode")
    private  int mMakeRatio = NvsConstants.AspectRatio.AspectRatio_NoFitRatio;
    @SerializedName("coverImagePath")
    private String mCoverImagePath;

    @SerializedName("videoResolution")
    private NvsVideoResolution mVideoResolution;
    @SerializedName("Rational")
    private NvsRational mNvsRational;
    @SerializedName("isAddTitleTheme")
    private boolean isAddTitleTheme;
    @SerializedName("titleThemeDuration")
    private long titleThemeDuration = 0;

    public LTimelineData() {
        mMeicamResourceList = new ArrayList<>();
        mMeicamVideoTrackList = new ArrayList<>();
        mMeicamAudioTrackList = new ArrayList<>();
        mMeicamStickerCaptionTrackList = new ArrayList();
        mMeicamTimelineVideoFxTrackList = new ArrayList<>();
        mMeicamTimelineVideoFxClipList = new ArrayList<>();
        mMeicamWaterMark = new LMeicamWaterMark(null);
    }


    public String toJson() {
        Gson gson = LGsonContext.getInstance().getGson();
        return gson.toJson(this);
    }

    public static LTimelineData fromJson(String jsonData) {
        LTimelineData timelineData = null;
        try {
            Gson gson = LGsonContext.getInstance().getGson();
            timelineData = gson.fromJson(jsonData, LTimelineData.class);
        } catch (Exception e) {
            LogUtils.e("error:" + e.getMessage());
        }
        return timelineData;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<LMeicamResource> getMeicamResourceList() {
        return mMeicamResourceList;
    }

    public void setMeicamResourceList(List<LMeicamResource> meicamResourceList) {
        mMeicamResourceList = meicamResourceList;
    }

    public List<LMeicamVideoTrack> getMeicamVideoTrackList() {
        return mMeicamVideoTrackList;
    }

    public void setMeicamVideoTrackList(List<LMeicamVideoTrack> meicamVideoTrackList) {
        mMeicamVideoTrackList = meicamVideoTrackList;
    }

    public List<LMeicamAudioTrack> getMeicamAudioTrackList() {
        return mMeicamAudioTrackList;
    }

    public void setMeicamAudioTrackList(List<LMeicamAudioTrack> meicamAudioTrackList) {
        mMeicamAudioTrackList = meicamAudioTrackList;
    }

    public List<LMeicamStickerCaptionTrack> getMeicamStickerCaptionTrackList() {
        return mMeicamStickerCaptionTrackList;
    }

    public void setMeicamStickerCaptionTrackList(List<LMeicamStickerCaptionTrack> meicamStickerCaptionTrackList) {
        mMeicamStickerCaptionTrackList = meicamStickerCaptionTrackList;
    }

    public List<LMeicamTimelineVideoFxTrack> getMeicamTimelineVideoFxTrackList() {
        return mMeicamTimelineVideoFxTrackList;
    }

    public void setMeicamTimelineVideoFxTrackList(List<LMeicamTimelineVideoFxTrack> meicamTimelineVideoFxTrackList) {
        mMeicamTimelineVideoFxTrackList = meicamTimelineVideoFxTrackList;
    }

    public List<LMeicamTimelineVideoFxClip> getMeicamTimelineVideoFxClipList() {
        if (mMeicamTimelineVideoFxClipList == null) {
            mMeicamTimelineVideoFxClipList = new ArrayList<>();
        }
        return mMeicamTimelineVideoFxClipList;
    }

    public void setMeicamTimelineVideoFxClipList(List<LMeicamTimelineVideoFxClip> meicamTimelineVideoFxClipList) {
        mMeicamTimelineVideoFxClipList = meicamTimelineVideoFxClipList;
    }

    public LMeicamWaterMark getMeicamWaterMark() {
        if (mMeicamWaterMark == null) {
            mMeicamWaterMark = new LMeicamWaterMark(null);
        }
        return mMeicamWaterMark;
    }

    public void setMeicamWaterMark(LMeicamWaterMark meicamWaterMark) {
        mMeicamWaterMark = meicamWaterMark;
    }

    public LMeicamTheme getMeicamTheme() {
        return mMeicamTheme;
    }

    public void setMeicamTheme(LMeicamTheme meicamTheme) {
        mMeicamTheme = meicamTheme;
    }

    public LMeicamAdjustData getMeicamAdjustData() {
        return mMeicamAdjustData;
    }

    public void setMeicamAdjustData(LMeicamAdjustData meicamAdjustData) {
        mMeicamAdjustData = meicamAdjustData;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String projectId) {
        mProjectId = projectId;
    }

    public String getProjectDuring() {
        return mProjectDuring;
    }

    public void setProjectDuring(String mProjectDuring) {
        this.mProjectDuring = mProjectDuring;
    }

    public String getProjectName() {
        return mProjectName;
    }

    public void setProjectName(String projectName) {
        mProjectName = projectName;
    }

    public String getLastModifiedTime() {
        return mLastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        mLastModifiedTime = lastModifiedTime;
    }

    public int getMakeRatio() {
        return mMakeRatio;
    }

    public void setMakeRatio(int makeRatio) {
        mMakeRatio = makeRatio;
    }

    public String getCoverImagePath() {
        return mCoverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        mCoverImagePath = coverImagePath;
    }

    public NvsVideoResolution getVideoResolution() {
        return mVideoResolution;
    }

    public void setVideoResolution(NvsVideoResolution mVideoResolution) {
        this.mVideoResolution = mVideoResolution;
    }

    public LMeicamVideoFx getFilterFx() {
        return mFilterFx;
    }

    public void setFilterFx(LMeicamTimelineVideoFx filterFx) {
        this.mFilterFx = filterFx;
    }

    public int addPicClipInfoData(LMeicamVideoClip meicamClipInfo) {
        LMeicamVideoTrack meicamVideoTrack = new LMeicamVideoTrack(mMeicamVideoTrackList.size());
        meicamVideoTrack.getClipInfoList().add(meicamClipInfo);
        mMeicamVideoTrackList.add(meicamVideoTrack);
        return meicamVideoTrack.getIndex();
    }

    public NvsRational getNvsRational() {
        return mNvsRational;
    }

    public void setNvsRational(NvsRational nvsRational) {
        mNvsRational = nvsRational;
    }

    public boolean isAddTitleTheme() {
        return isAddTitleTheme;
    }

    public void setAddTitleTheme(boolean addTitleTheme) {
        isAddTitleTheme = addTitleTheme;
    }

    public long getTitleThemeDuration() {
        return titleThemeDuration;
    }

    public void setTitleThemeDuration(long titleThemeDuration) {
        this.titleThemeDuration = titleThemeDuration;
    }

    @Override
    public TimelineData parseToTimelineData() {
        TimelineData.getInstance().clearData();
        TimelineData timelineData = TimelineData.getInstance();
        if (!CommonUtils.isEmpty(mMeicamResourceList)) {
            for (LMeicamResource meicamResource : mMeicamResourceList) {
                if (null != meicamResource) {
                    timelineData.getMeicamResourceList().add(meicamResource.parseToTimelineData());
                }
            }
        }
        for (LMeicamVideoTrack meicamVideoTrack : mMeicamVideoTrackList) {
            timelineData.getMeicamVideoTrackList().add(meicamVideoTrack.parseToTimelineData());
        }

        for (LMeicamAudioTrack meicamAudioTrack : mMeicamAudioTrackList) {
            timelineData.getMeicamAudioTrackList().add(meicamAudioTrack.parseToTimelineData());
        }
        for (LMeicamStickerCaptionTrack stickerCaptionTrack : mMeicamStickerCaptionTrackList) {
            timelineData.getMeicamStickerCaptionTrackList().add(stickerCaptionTrack.parseToTimelineData());
        }
        for (LMeicamTimelineVideoFxTrack videoFxTrack : mMeicamTimelineVideoFxTrackList) {
            timelineData.getMeicamTimelineVideoFxTrackList().add(videoFxTrack.parseToTimelineData());
        }

        for (LMeicamTimelineVideoFxClip meicamTimelineVideoFxClip : mMeicamTimelineVideoFxClipList) {
            timelineData.getMeicamTimelineVideoFxClipList().add(meicamTimelineVideoFxClip.parseToTimelineData());
        }

        if (mMeicamWaterMark != null) {
            timelineData.setMeicamWaterMark(mMeicamWaterMark.parseToTimelineData());
        }

        if (mMeicamTheme != null) {
            timelineData.setMeicamTheme(mMeicamTheme.parseToTimelineData());
        }
        if (mMeicamAdjustData != null) {
            timelineData.setMeicamAdjustData(mMeicamAdjustData.parseToTimelineData());
        }

        if (mFilterFx != null) {
            timelineData.setFilterFx(mFilterFx.parseToTimelineData());
        }

        timelineData.setVideoResolution(mVideoResolution);
        timelineData.setNvsRational(mNvsRational);
        timelineData.setProjectId(mProjectId);
        timelineData.setProjectName(mProjectName);
        timelineData.setLastModifiedTime(mLastModifiedTime);
        timelineData.setProjectDuring(mProjectDuring);
        timelineData.setCoverImagePath(mCoverImagePath);
        timelineData.setDuration(duration);
        timelineData.setAddTitleTheme(isAddTitleTheme);
        timelineData.setTitleThemeDuration(titleThemeDuration);
        timelineData.setMakeRatio(mMakeRatio);
        return timelineData;
    }
}
