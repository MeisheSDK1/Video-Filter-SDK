package com.meishe.engine.bean;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.utils.CommonUtils;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.engine.local.LTimelineData;
import com.meishe.engine.util.TimelineUtil;
import com.meishe.engine.util.gson.GsonContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 17:09
 */
public class TimelineData implements TimelineDataToLocalAdapter<LTimelineData> {
    private static TimelineData sTimelineData;
    private long duration;
    @SerializedName("resources")
    private List<MeicamResource> mMeicamResourceList;
    @SerializedName("videoTracks")
    private List<MeicamVideoTrack> mMeicamVideoTrackList;
    @SerializedName("audioTracks")
    private List<MeicamAudioTrack> mMeicamAudioTrackList;
    @SerializedName("stickerCaptionTracks")
    private List<MeicamStickerCaptionTrack> mMeicamStickerCaptionTrackList;
    @SerializedName("timelineVideoFxTracks")
    private List<MeicamTimelineVideoFxTrack> mMeicamTimelineVideoFxTrackList;
    /**
     * 模糊 马赛克 目前只能添加一个
     * Blur Mosaic can only be added one at present
     */
    @SerializedName("timelineVideoFxClips")
    private List<MeicamTimelineVideoFxClip> mMeicamTimelineVideoFxClipList;
    @SerializedName("waterMark")
    private MeicamWaterMark mMeicamWaterMark;
    @SerializedName("theme")
    private MeicamTheme mMeicamTheme;
    /**
     * 调节对应的信息
     * Adjust the corresponding information
     */
    @SerializedName("adjustData")
    private MeicamAdjustData mMeicamAdjustData = new MeicamAdjustData();

    @SerializedName("filterFx")
    private MeicamTimelineVideoFx mFilterFx;
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
    private int mMakeRatio = NvsConstants.AspectRatio.AspectRatio_NoFitRatio;
    @SerializedName("coverImagePath")
    private String mCoverImagePath;

    @SerializedName("videoResolution")
    private NvsVideoResolution mVideoResolution;
    @SerializedName("rational")
    private NvsRational mNvsRational;

    transient private MeicamVideoClip mEditMeicamClipInfo;

    transient private String mDraftDir;
    @SerializedName("isAddTitleTheme")
    private boolean isAddTitleTheme;
    @SerializedName("titleThemeDuration")
    private long titleThemeDuration = 0;

    private TimelineData() {
        mMeicamResourceList = new ArrayList<>();
        mMeicamVideoTrackList = new ArrayList<>();
        mMeicamAudioTrackList = new ArrayList<>();
        mMeicamStickerCaptionTrackList = new ArrayList<>();
        mMeicamTimelineVideoFxTrackList = new ArrayList<>();
        mMeicamTimelineVideoFxClipList = new ArrayList<>();
        mMeicamWaterMark = new MeicamWaterMark(null, null);
    }

    public static TimelineData getInstance() {
        if (sTimelineData == null) {
            synchronized (TimelineData.class) {
                if (sTimelineData == null) {
                    sTimelineData = new TimelineData();
                }
            }
        }
        return sTimelineData;
    }

    /**
     * 用于转换业务TimelineData数据
     *
     * @return
     */
    public String toJson() {
        Gson gson = GsonContext.getInstance().getGson();
        return gson.toJson(this);
    }

    /**
     * 从业务数据恢复TimelineData，不需要转换
     * Used to transform business TimelineData data
     * @param jsonData
     * @return
     */
    public Object fromJson(String jsonData) {
        Gson gson = GsonContext.getInstance().getGson();
        sTimelineData = gson.fromJson(jsonData, TimelineData.class);
        return sTimelineData;
    }


    /**
     * 用于转换草稿数据
     * Used to convert draft data
     * @return
     */
    public String toDraftJson() {
        LTimelineData lTimelineData = parseToLocalData();
        return LGsonContext.getInstance().getGson().toJson(lTimelineData);
    }

    /**
     * 从草稿恢复至TimelineData，需要转换
     * Restoring from draft to TimelineData requires a transformation
     * @param jsonData
     * @return
     */
    public Object fromDraftJson(String jsonData) {
        LTimelineData lTimelineData = LTimelineData.fromJson(jsonData);
        if (lTimelineData != null) {
            sTimelineData = lTimelineData.parseToTimelineData();
        }
        return sTimelineData;
    }


    /**
     * 清除数据
     * Clear data
     */
    public void clearData() {
        sTimelineData = null;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<MeicamResource> getMeicamResourceList() {
        return mMeicamResourceList;
    }

    public void setMeicamResourceList(List<MeicamResource> meicamResourceList) {
        mMeicamResourceList = meicamResourceList;
    }

    public List<MeicamVideoTrack> getMeicamVideoTrackList() {
        return mMeicamVideoTrackList;
    }

    public void setMeicamVideoTrackList(List<MeicamVideoTrack> meicamVideoTrackList) {
        mMeicamVideoTrackList = meicamVideoTrackList;
    }

    public List<MeicamAudioTrack> getMeicamAudioTrackList() {
        return mMeicamAudioTrackList;
    }

    public void setMeicamAudioTrackList(List<MeicamAudioTrack> meicamAudioTrackList) {
        mMeicamAudioTrackList = meicamAudioTrackList;
    }

    public List<MeicamStickerCaptionTrack> getMeicamStickerCaptionTrackList() {
        return mMeicamStickerCaptionTrackList;
    }

    public void setMeicamStickerCaptionTrackList(List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList) {
        mMeicamStickerCaptionTrackList = meicamStickerCaptionTrackList;
    }

    public List<MeicamTimelineVideoFxTrack> getMeicamTimelineVideoFxTrackList() {
        return mMeicamTimelineVideoFxTrackList;
    }

    public void setMeicamTimelineVideoFxTrackList(List<MeicamTimelineVideoFxTrack> meicamTimelineVideoFxTrackList) {
        mMeicamTimelineVideoFxTrackList = meicamTimelineVideoFxTrackList;
    }

    public List<MeicamTimelineVideoFxClip> getMeicamTimelineVideoFxClipList() {
        if (mMeicamTimelineVideoFxClipList == null) {
            mMeicamTimelineVideoFxClipList = new ArrayList<>();
        }
        return mMeicamTimelineVideoFxClipList;
    }

    public void setMeicamTimelineVideoFxClipList(List<MeicamTimelineVideoFxClip> meicamTimelineVideoFxClipList) {
        //置空不处理的话，parseToLocalData 会报错  If null is not processed, parseToLocalData will report an error
        if (null == meicamTimelineVideoFxClipList) {
            mMeicamTimelineVideoFxClipList.clear();
        } else {
            mMeicamTimelineVideoFxClipList = meicamTimelineVideoFxClipList;
        }
    }

    public MeicamWaterMark getMeicamWaterMark() {
        if (mMeicamWaterMark == null) {
            mMeicamWaterMark = new MeicamWaterMark(null, null);
        }
        return mMeicamWaterMark;
    }

    public void setMeicamWaterMark(MeicamWaterMark meicamWaterMark) {
        mMeicamWaterMark = meicamWaterMark;
    }

    public MeicamTheme getMeicamTheme() {
        return mMeicamTheme;
    }

    public void setMeicamTheme(MeicamTheme meicamTheme) {
        mMeicamTheme = meicamTheme;
    }

    public MeicamAdjustData getMeicamAdjustData() {
        return mMeicamAdjustData;
    }

    public void setMeicamAdjustData(MeicamAdjustData meicamAdjustData) {
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
        TimelineUtil.alignedResolution(mVideoResolution);
        this.mVideoResolution = mVideoResolution;
    }

    public MeicamTimelineVideoFx getFilterFx() {
        return mFilterFx;
    }

    public void setFilterFx(MeicamTimelineVideoFx filterFx) {
        this.mFilterFx = filterFx;
    }

    public MeicamVideoClip getSelectedMeicamClipInfo() {
        return mEditMeicamClipInfo;
    }

    public void setSelectedMeicamClipInfo(MeicamVideoClip selectedMeicamClipInfo) {
        this.mEditMeicamClipInfo = selectedMeicamClipInfo;
    }

    public String getDraftDir() {
        return mDraftDir;
    }

    public void setDraftDir(String draftDir) {
        this.mDraftDir = draftDir;
    }

    public int addPicClipInfoData(MeicamVideoClip meicamClipInfo) {
        MeicamVideoTrack meicamVideoTrack = new MeicamVideoTrack(mMeicamVideoTrackList.size());
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

    @Override
    public LTimelineData parseToLocalData() {
        LTimelineData localData = new LTimelineData();
        mMeicamResourceList.clear();
        for (MeicamVideoTrack meicamVideoTrack : mMeicamVideoTrackList) {
            localData.getMeicamVideoTrackList().add(meicamVideoTrack.parseToLocalData());
        }

        for (MeicamAudioTrack meicamAudioTrack : mMeicamAudioTrackList) {
            localData.getMeicamAudioTrackList().add(meicamAudioTrack.parseToLocalData());
        }
        for (MeicamStickerCaptionTrack stickerCaptionTrack : mMeicamStickerCaptionTrackList) {
            localData.getMeicamStickerCaptionTrackList().add(stickerCaptionTrack.parseToLocalData());
        }
        for (MeicamTimelineVideoFxTrack videoFxTrack : mMeicamTimelineVideoFxTrackList) {
            localData.getMeicamTimelineVideoFxTrackList().add(videoFxTrack.parseToLocalData());
        }

        for (MeicamTimelineVideoFxClip meicamTimelineVideoFxClip : mMeicamTimelineVideoFxClipList) {
            localData.getMeicamTimelineVideoFxClipList().add(meicamTimelineVideoFxClip.parseToLocalData());
        }

        if (mMeicamWaterMark != null) {
            localData.setMeicamWaterMark(mMeicamWaterMark.parseToLocalData());
        }

        if (mMeicamTheme != null) {
            localData.setMeicamTheme(mMeicamTheme.parseToLocalData());
        }
        if (mMeicamAdjustData != null) {
            localData.setMeicamAdjustData(mMeicamAdjustData.parseToLocalData());
        }

        if (mFilterFx != null) {
            localData.setFilterFx(mFilterFx.parseToLocalData());
        }

        localData.setVideoResolution(mVideoResolution);
        localData.setNvsRational(mNvsRational);
        localData.setProjectId(mProjectId);
        localData.setProjectName(mProjectName);
        localData.setLastModifiedTime(mLastModifiedTime);
        localData.setProjectDuring(mProjectDuring);
        localData.setCoverImagePath(mCoverImagePath);
        localData.setDuration(duration);
        localData.setAddTitleTheme(isAddTitleTheme);
        localData.setTitleThemeDuration(titleThemeDuration);
        localData.setMakeRatio(mMakeRatio);
        if (!CommonUtils.isEmpty(mMeicamResourceList)) {
            for (MeicamResource meicamResource : mMeicamResourceList) {
                localData.getMeicamResourceList().add(meicamResource.parseToLocalData());
            }
        }
        return localData;
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

    public void setTitleThemeDuration(long titleThemeLength) {
        this.titleThemeDuration = titleThemeLength;
    }

    public String getPlaceId(MeicamResource resource) {
        if (mMeicamResourceList.contains(resource)) {
            return String.valueOf(mMeicamResourceList.indexOf(resource));
        } else {
            resource.setId(String.valueOf(mMeicamResourceList.size()));
            mMeicamResourceList.add(resource);
            return resource.getId();
        }
    }
}
