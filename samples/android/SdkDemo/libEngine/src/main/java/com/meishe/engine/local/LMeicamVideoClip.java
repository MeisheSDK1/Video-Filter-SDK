package com.meishe.engine.local;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.bean.background.MeicamStoryboardInfo;
import com.meishe.engine.local.background.LMeicamStoryboardInfo;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_REDDENING;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_STRENGTH;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_WHITENING;

/**
 * Created by CaoZhiChao on 2020/7/3 20:41
 */
public class LMeicamVideoClip extends LClipInfo implements Cloneable, Serializable {
    /**
     * 对应资源id 和resources对应
     * Corresponding resource id corresponds to resources
     */
    private String id;
    private String filePath;
    /**
     * 倒放路径
     * Inverted path
     */
    private String reverseFilePath;
    /**
     * 区分图片还是视频
     * Distinguish between pictures and videos
     */
    private String videoType;
    private long trimIn;
    private long trimOut;
    private long orgDuration;
    private float volume = 1.0f;
    private double speed = 1.0f;
    private String curveSpeed="";
    private String curveSpeedName = "";
    /**
     * 是否倒放
     * Inverted or not
     */
    private boolean isVideoReverse = false;
    /**
     * 视频默认未转码成功
     * By default, the video is not transcoded successfully
     */
    private boolean isConvertSuccess = false;

    /**
     * 图片展示模式
     * Picture display mode
     * */
    @SerializedName("imageDisplayMode")
    private int mImageDisplayMode = 0;

    /**
     * 视频横向裁剪，纵向平移
     * Video cropped horizontally, panned vertically
     * */
    @SerializedName("span")
    private float mSpan = 0;
    @SerializedName("scan")
    private float mScan = 0;


    /**
     * 透明度
     * opacity
     */
    private float opacity = 1f;

    private int extraRotation;
    private boolean reverse;
    /**
     * clip设置的VideoFx 如滤镜 transform 2D 等
     * clip set VideoFx such as filter transform 2D, etc
     */
    private List<LMeicamVideoFx> videoFxs = new ArrayList<>();

    @SerializedName("adjustData")
    private LMeicamAdjustData mAdjustData = new LMeicamAdjustData();
    /**
     * 在主题中的成分。片头或者片尾
     * Components in the subject. The beginning or end of the title
     */
    @SerializedName("roleInTheme")
    private int mRoleInTheme;

    /**
     * 美肤和美型
     * Beautiful skin and shape
     */
    @SerializedName("faceEffectParameter")
    private Map<String, Float> mFaceEffectParameter = new HashMap<>();

    @SerializedName("storyboardInfo")
    private List<LMeicamStoryboardInfo> mStoryboardInfos = new ArrayList<>();

    /**
     * 变调
     * Tone variation
     */
    @SerializedName("keepAudioPitch")
    private boolean keepAudioPitch = true;

    @SerializedName("originalWidth")
    private long originalWidth;
    @SerializedName("originalHeight")
    private long originalHeight;

    public LMeicamVideoClip() {
        super(CommonData.CLIP_VIDEO);
    }

    private String resourceId;

    public LMeicamVideoClip(String filePath, String videoType, long orgDuration) {
        super(CommonData.CLIP_VIDEO);
        this.filePath = filePath;
        this.videoType = videoType;
        this.orgDuration = orgDuration;
    }

    public Map<String, Float> getFaceEffectParameter() {
        return mFaceEffectParameter;
    }

    public void setFaceEffectParameter(Map<String, Float> faceEffectParameter) {
        mFaceEffectParameter = faceEffectParameter;
    }

    /**
     * 仅仅设置美肤数据
     * Just set the beauty data
     *
     * @param faceEffectParameter Parameter
     */
    public void setFaceBeautyEffectParameter(Map<String, Float> faceEffectParameter) {
        if (faceEffectParameter == null) {
            return;
        }
        mFaceEffectParameter.put(VIDEO_FX_BEAUTY_STRENGTH, faceEffectParameter.get(VIDEO_FX_BEAUTY_STRENGTH));
        mFaceEffectParameter.put(VIDEO_FX_BEAUTY_WHITENING, faceEffectParameter.get(VIDEO_FX_BEAUTY_WHITENING));
        mFaceEffectParameter.put(VIDEO_FX_BEAUTY_REDDENING, faceEffectParameter.get(VIDEO_FX_BEAUTY_REDDENING));
    }

    public void setStoryboardInfos(List<LMeicamStoryboardInfo> storyboardInfos) {
        mStoryboardInfos = storyboardInfos;
    }

    public List<LMeicamStoryboardInfo> getStoryboardInfos() {
        return mStoryboardInfos;
    }

    public LMeicamAdjustData getMeicamAdjustData() {
        return mAdjustData;
    }

    public void setMeicamAdjustData(LMeicamAdjustData meicamAdjustData) {
        mAdjustData = meicamAdjustData;
    }

    public int getRoleInTheme() {
        return mRoleInTheme;
    }

    public void setRoleInTheme(int roleInTheme) {
        mRoleInTheme = roleInTheme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getReverseFilePath() {
        return reverseFilePath;
    }

    public void setReverseFilePath(String reverseFilePath) {
        this.reverseFilePath = reverseFilePath;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }

    public long getTrimOut() {
        return trimOut;
    }

    public void setTrimOut(long trimOut) {
        this.trimOut = trimOut;
    }

    public long getOrgDuration() {
        return orgDuration;
    }

    public void setOrgDuration(long orgDuration) {
        this.orgDuration = orgDuration;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getExtraRotation() {
        return extraRotation;
    }

    public void setExtraRotation(int extraRotation) {
        this.extraRotation = extraRotation;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public List<LMeicamVideoFx> getVideoFxs() {
        return videoFxs;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public boolean getVideoReverse() {
        return isVideoReverse;
    }

    public void setVideoReverse(boolean videoReverse) {
        isVideoReverse = videoReverse;
    }

    public boolean isConvertSuccess() {
        return isConvertSuccess;
    }

    public void setConvertSuccess(boolean convertSuccess) {
        isConvertSuccess = convertSuccess;
    }


    public int getImageDisplayMode() {
        return mImageDisplayMode;
    }

    public void setmImageDisplayMode(int imageDisplayMode) {
        this.mImageDisplayMode = imageDisplayMode;
    }

    public String getCurveSpeed() {
        return curveSpeed;
    }

    public void setCurveSpeed(String curveSpeed) {
        this.curveSpeed = curveSpeed;
    }

    public String getCurveSpeedName() {
        return curveSpeedName;
    }

    public void setCurveSpeedName(String curveSpeedName) {
        this.curveSpeedName = curveSpeedName;
    }

    public boolean isKeepAudioPitch() {
        return keepAudioPitch;
    }

    public void setKeepAudioPitch(boolean keepAudioPitch) {
        this.keepAudioPitch = keepAudioPitch;
    }

    public void setScan(float scan) {
        this.mScan = scan;
    }

    public void setSpan(float span) {
        this.mSpan = span;
    }

    public float getScan() {
        return mScan;
    }

    public float getSpan() {
        return mSpan;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public long getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(long originalWidth) {
        this.originalWidth = originalWidth;
    }

    public long getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(long originalHeight) {
        this.originalHeight = originalHeight;
    }

    @NonNull
    @Override
    public Object clone() {
        return DeepCopyUtil.deepClone(this);
    }

    public void setFilterIntensity(float intensity, String type) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        LMeicamVideoFx filterFx = null;
        for (LMeicamVideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getSubType())) {
                filterFx = videoFx;
                break;
            }
        }
        if (filterFx == null) {
            return;
        }
        filterFx.setIntensity(intensity);
    }

    public float getFilterIntensity(String type) {
        if (TextUtils.isEmpty(type)) {
            return 0;
        }
        LMeicamVideoFx filterFx = null;
        for (LMeicamVideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getSubType())) {
                filterFx = videoFx;
                break;
            }
        }
        if (filterFx == null) {
            return 0;
        }
        return filterFx.intensity;
    }

    public LMeicamVideoFx getVideoFx(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        for (LMeicamVideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getSubType())) {
                return videoFx;
            }
        }
        return null;
    }

    private void setAdjustEffect(NvsVideoClip videoClip, float adjustData, String adjustKey, String attachmentKey) {
        NvsVideoFx nvsVideoFx = null;
        int fxCount = videoClip.getFxCount();
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(i);
            if (videoFx != null) {
                Object attachment = videoFx.getAttachment(attachmentKey);
                if (attachment != null && attachmentKey.equals(attachment)) {
                    nvsVideoFx = videoFx;
                    break;
                }
            }
        }
        if (nvsVideoFx != null) {
            nvsVideoFx.setFloatVal(adjustKey, adjustData);
        } else {
            nvsVideoFx = videoClip.appendBuiltinFx(attachmentKey);
            nvsVideoFx.setAttachment(attachmentKey, attachmentKey);
            nvsVideoFx.setFloatVal(adjustKey, adjustData);
        }
        setRegionData(nvsVideoFx);
    }

    private void setRegionData(NvsVideoFx arSceneFx) {
        if (arSceneFx == null) {
            return;
        }
        String filePath = getFilePath();
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(filePath);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int videoStreamRotation = avFileInfo.getVideoStreamRotation(0);
        int width = dimension.width;
        int height = dimension.height;
        if (videoStreamRotation == 1 || videoStreamRotation == 3) {
            width = dimension.height;
            height = dimension.width;
        }
        NvsVideoResolution resolution = TimelineData.getInstance().getVideoResolution();
        int timelineHeight = resolution.imageHeight;
        int timelineWidth = resolution.imageWidth;
        float timelineRatio = timelineWidth * 1.0F / timelineHeight;
        float fileRatio = width * 1.0F / height;
        float X = 1.0F;
        float Y = 1.0F;
        if (fileRatio > timelineRatio) { //宽对齐  Wide alignment
            X = 1.0F;
            float ratio = timelineWidth * 1.0F / width;
            float imageHeightInTimeline = height * ratio;
            Y = imageHeightInTimeline / timelineHeight * 0.99F;
        } else {//高对齐  High alignment
            Y = 1.0F;
            float ratio = timelineHeight * 1.0F / height;
            float imageWidthInTimeline = width * ratio;
            X = imageWidthInTimeline / timelineWidth * 0.99F;
        }
        float[] region = new float[8];
        region[0] = -X;
        region[1] = Y;
        region[2] = X;
        region[3] = Y;

        region[4] = X;
        region[5] = -Y;
        region[6] = -X;
        region[7] = -Y;
        arSceneFx.setRegion(region);
        arSceneFx.setRegional(true);
    }

    @Override
    public MeicamVideoClip parseToTimelineData() {
        MeicamVideoClip local = new MeicamVideoClip();
        setCommonData(local);
        local.setId(getId());
        local.setFilePath(getFilePath());
        local.setReverseFilePath(getReverseFilePath());
        local.setVideoType(getVideoType());
        local.setTrimIn(getTrimIn());
        local.setTrimOut(getTrimOut());
        local.setOrgDuration(getOrgDuration());
        local.setVolume(getVolume());
        local.setSpeed(getSpeed());
        local.setCurveSpeed(getCurveSpeed());
        local.setCurveSpeedName(getCurveSpeedName());
        local.setVideoReverse(getVideoReverse());
        local.setConvertSuccess(isConvertSuccess());
        local.setmImageDisplayMode(getImageDisplayMode());
        local.setSpan(getSpan());
        local.setScan(getScan());
        local.setOpacity(getOpacity());
        local.setExtraRotation(getExtraRotation());
        local.setReverse(isReverse());
        local.setKeepAudioPitch(isKeepAudioPitch());
        local.setOriginalWidth(getOriginalWidth());
        local.setOriginalHeight(getOriginalHeight());
        for (LMeicamVideoFx videoFx : videoFxs) {
            local.getVideoFxs().add(videoFx.parseToTimelineData());
        }
        LMeicamAdjustData meicamAdjustData = getMeicamAdjustData();
        if (meicamAdjustData != null) {
            local.setMeicamAdjustData(meicamAdjustData.parseToTimelineData());
        }
        local.setRoleInTheme(getRoleInTheme());
        Map<String, Float> localEffectParameter = new HashMap<>();
        Set<String> keySet = mFaceEffectParameter.keySet();
        for (String key : keySet) {
            localEffectParameter.put(key, mFaceEffectParameter.get(key));
        }
        local.setFaceEffectParameter(localEffectParameter);
        for (LMeicamStoryboardInfo storyboardInfo : mStoryboardInfos) {
            if (storyboardInfo != null) {
                MeicamStoryboardInfo info = storyboardInfo.parseToTimelineData();
                local.getStoryboardInfos().put(info.getSubType(), info);
            }
        }
        return local;
    }
}
