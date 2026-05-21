package com.meishe.engine.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.app.BaseApp;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.adapter.LGsonContext;
import com.meishe.engine.adapter.parser.IResourceParser;
import com.meishe.engine.bean.background.MeicamStoryboardInfo;
import com.meishe.engine.constant.ColorsConstants;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.engine.local.LMeicamVideoClip;
import com.meishe.engine.util.ColorUtil;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_AR_SCENE;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_EFFECT;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_REDDENING;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_SHAPE;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_STRENGTH;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_BEAUTY_WHITENING;
import static com.meishe.engine.bean.CommonData.VIDEO_FX_SINGLE_BUFFER_MODE;

/**
 * Created by CaoZhiChao on 2020/7/3 20:41
 */
public class MeicamVideoClip extends ClipInfo<NvsVideoClip> implements Cloneable, Serializable, IResourceParser {
    private final static String[] sFxName = new String[]{"Beauty Strength", "Beauty Whitening",
            "Beauty Reddening", "Face Size Warp Degree",
            "Eye Size Warp Degree", "Chin Length Warp Degree",
            "Forehead Height Warp Degree", "Nose Width Warp Degree",
            "Mouth Size Warp Degree"};

    public final static String ATTACHMENT_KEY_FX_TYPE = "videoFxType";
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
    private String curveSpeed = "";
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
     */
    @SerializedName("imageDisplayMode")
    private int mImageDisplayMode = 0;

    /**
     * 视频横向裁剪，纵向平移
     * Video cropped horizontally, panned vertically
     */
    @SerializedName("span")
    private float mSpan = 0;
    @SerializedName("scan")
    private float mScan = 0;


    /**
     * 透明度
     * opacity
     */
    private float opacity = 1f;
    /**
     * to-left-90  to-right-90  horizontal vertical
     */
    private int extraRotation;
    private boolean reverse;
    /**
     * clip设置的VideoFx 如滤镜 transform 2D 等
     * clip set VideoFx such as filter transform 2D, etc
     */
    private List<MeicamVideoFx> videoFxs = new ArrayList<>();

    @SerializedName("adjustData")
    private MeicamAdjustData mMeicamAdjustData = new MeicamAdjustData();
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
    private Map<String, MeicamStoryboardInfo> mStoryboardInfos = new TreeMap<>();
    private String resourceId;

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
    /**
     * Use to save the mask data, not the draft
     * 用于保存蒙版数据，不保存草稿
     */
    private transient Map<Integer, MaskInfoData> maskInfoDataMap = new HashMap<>();

    public MeicamVideoClip() {
        super(CommonData.CLIP_VIDEO);
    }

    public MeicamVideoClip(String filePath, String videoType, long orgDuration) {
        super(CommonData.CLIP_VIDEO);
        this.filePath = filePath;
        this.videoType = videoType;
        this.orgDuration = orgDuration;
    }

    /**
     * 注意此方法，只刷新了局部数据，某些数据从底层无法恢复到上层，谨慎使用
     * Note that only partial data is refreshed in this method. Some data cannot be recovered from the bottom layer to the top layer. Therefore, exercise caution when using this method
     *
     * @param videoClip
     */
    @Override
    @Deprecated
    public void loadData(NvsVideoClip videoClip) {
        if (videoClip == null) {
            return;
        }
        setObject(videoClip);
        setInPoint(videoClip.getInPoint());
        setOutPoint(videoClip.getOutPoint());
        isVideoReverse = videoClip.getPlayInReverse();
        if (isVideoReverse) {
            reverseFilePath = videoClip.getFilePath();
        } else {
            filePath = videoClip.getFilePath();
        }
        trimIn = videoClip.getTrimIn();
        trimOut = videoClip.getTrimOut();
        speed = videoClip.getSpeed();
        extraRotation = videoClip.getExtraVideoRotation();
        mRoleInTheme = videoClip.getRoleInTheme();
        opacity = videoClip.getOpacity();

    }

    public Map<String, Float> getFaceEffectParameter() {
        return mFaceEffectParameter;
    }

    public void setFaceEffectParameter(Map<String, Float> faceEffectParameter) {
        if (null != faceEffectParameter && faceEffectParameter.size() > 0) {

            mFaceEffectParameter.putAll(faceEffectParameter);
        }
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


    /**
     * 仅仅设置美型数据
     * Set only beauty data
     *
     * @param faceEffectParameter Parameter
     */
    public void setFaceBeautyShapeEffectParameter(Map<String, Float> faceEffectParameter) {
        if (faceEffectParameter == null) {
            return;
        }
        Set<String> keyFxs = faceEffectParameter.keySet();
        for (String key : keyFxs) {
            if (!isBeautyEffectKey(key)) {
                mFaceEffectParameter.put(key, faceEffectParameter.get(key));
            }
        }
    }


    public MeicamStoryboardInfo getBackgroundInfo() {
        return mStoryboardInfos.get(MeicamStoryboardInfo.SUB_TYPE_BACKGROUND);
    }

    public void setStoryboardInfos(Map<String, MeicamStoryboardInfo> storyboardInfos) {
        if (null != storyboardInfos && storyboardInfos.size() > 0) {

            mStoryboardInfos.putAll(storyboardInfos);
        }
    }

    public Map<String, MeicamStoryboardInfo> getStoryboardInfos() {
        return mStoryboardInfos;
    }


    public void addStoryboardInfo(String key, MeicamStoryboardInfo storyInfo) {
        mStoryboardInfos.put(key, storyInfo);
    }

    public MeicamAdjustData getMeicamAdjustData() {
        return mMeicamAdjustData;
    }

    public void setMeicamAdjustData(MeicamAdjustData meicamAdjustData) {
        mMeicamAdjustData = meicamAdjustData;
    }

    public Map<Integer, MaskInfoData> getMaskInfoDataMap() {
        if (maskInfoDataMap == null) {
            maskInfoDataMap = new HashMap<>();
        }
        return maskInfoDataMap;
    }

    public void setMaskInfoDataMap(Map<Integer, MaskInfoData> maskInfoDataMap) {
        this.maskInfoDataMap = maskInfoDataMap;
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
        if (getObject() != null) {
            getObject().setVolumeGain(volume, volume);
        }
        this.volume = volume;
    }

    public String getCurveSpeedName() {
        return curveSpeedName;
    }

    public void setCurveSpeedName(String curveSpeedName) {
        this.curveSpeedName = curveSpeedName;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isKeepAudioPitch() {
        return keepAudioPitch;
    }

    public void setKeepAudioPitch(boolean keepAudioPitch) {
        this.keepAudioPitch = keepAudioPitch;
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

    public List<MeicamVideoFx> getVideoFxs() {
        return videoFxs;
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

    /**
     * 只在分割的时候使用到了复制片段
     * Duplicate fragments are used only for segmentation
     *
     * @param videoFxs
     */
    public void setVideoFxs(List<MeicamVideoFx> videoFxs) {
        if (null != videoFxs && videoFxs.size() > 0) {
            this.videoFxs.clear();
            this.videoFxs.addAll(videoFxs);
        }
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
//        if (getObject() != null) {
//            getObject().setPlayInReverse(isVideoReverse);
//        }
    }

    public boolean isConvertSuccess() {
        return isConvertSuccess;
    }

    public void setConvertSuccess(boolean convertSuccess) {
        isConvertSuccess = convertSuccess;
    }


    public String getCurveSpeed() {
        return curveSpeed;
    }

    public void setCurveSpeed(String curveSpeed) {
        this.curveSpeed = curveSpeed;
    }

    public int getImageDisplayMode() {
        return mImageDisplayMode;
    }

    public void setmImageDisplayMode(int imageDisplayMode) {
        this.mImageDisplayMode = imageDisplayMode;
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

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void removeBackground() {
        if (mStoryboardInfos.isEmpty()) {
            return;
        }
        MeicamStoryboardInfo backgroundInfo = mStoryboardInfos.get(MeicamStoryboardInfo.SUB_TYPE_BACKGROUND);
        if (backgroundInfo == null) {
            return;
        }
        NvsVideoFx nvsVideoFx = backgroundInfo.getObject();
        if (nvsVideoFx == null) {
            return;
        }
        NvsVideoClip object = getObject();
        if (object != null) {
            object.removeFx(nvsVideoFx.getIndex());
            mStoryboardInfos.remove(MeicamStoryboardInfo.SUB_TYPE_BACKGROUND);
        }
    }

    public NvsVideoClip appendToTimeline(NvsVideoTrack videoTrack) {
        if (videoTrack == null) {
            return null;
        }
        String videoFilePath;
        if (isVideoReverse) {
            videoFilePath = reverseFilePath;
        } else {
            videoFilePath = filePath;
        }
        NvsVideoClip videoClip = videoTrack.appendClip(videoFilePath);
        if (videoClip == null) {
            LogUtils.e("failed to append clip");
            return null;
        }
        setObject(videoClip);
        setData(videoClip, videoTrack.getIndex());

        return videoClip;
    }

    public NvsVideoClip addToTimeline(NvsVideoTrack videoTrack) {
        if (videoTrack == null) {
            return null;
        }
        String videoFilePath = null;
        if (isVideoReverse) {
            videoFilePath = reverseFilePath;
        } else {
            videoFilePath = filePath;
        }
        /**
         * Adapts the previous version to use "assets:/black_timeline.png" material blackout
         * 适配以前版本使用"assets:/black_timeline.png"素材补黑
         */
        if ("assets:/black_timeline.png".equals(videoFilePath)) {
            videoFilePath = CommonData.EMPTY_THUMBNAIL_IMAGE;
            filePath = videoFilePath;
        }
        videoFilePath = FileUtils.getAbsPathByContentUri(BaseApp.getContext()
                , BaseApp.CONTENT_FLAG, videoFilePath);

        NvsVideoClip videoClip = videoTrack.addClip(videoFilePath, getInPoint(), getTrimIn(), getTrimOut());
        if (videoClip == null) {
            LogUtils.e("failed to append clip");
            return null;
        }
        setObject(videoClip);
        setData(videoClip, videoTrack.getIndex());
        return videoClip;
    }

    public NvsVideoClip insertToTimeline(NvsVideoTrack videoTrack, int clipIndex) {
        if (videoTrack == null) {
            return null;
        }
        String videoFilePath;
        if (isVideoReverse) {
            videoFilePath = reverseFilePath;
        } else {
            videoFilePath = filePath;
        }
        NvsVideoClip videoClip = videoTrack.insertClip(videoFilePath, clipIndex);
        if (videoClip == null) {
            LogUtils.e("failed to append clip");
            return null;
        }
        setObject(videoClip);
        setData(videoClip, videoTrack.getIndex());
        return videoClip;
    }

    public void setData(NvsVideoClip videoClip, int trackIndex) {
        setIndex(videoClip.getIndex());
//        videoClip.setPlayInReverse(getVideoReverse());
        //添加美肤和美型  Add beauty and shape
        applyFaceEffect(trackIndex);
        //设置调节参数  Set adjustment parameters
        setAdjustEffects(videoClip);

        for (MeicamVideoFx videoFx : videoFxs) {
            removeNvsVideoFx(videoFx.getSubType());
        }

        //添加各种特效  Add various effects
        for (int i = 0; i < videoFxs.size(); i++) {
            MeicamVideoFx meicamVideoFx = videoFxs.get(i);
            meicamVideoFx.bindToTimeline(videoClip);
        }

        int videoType = videoClip.getVideoType();
        //当前片段是图片 The current fragment is a picture
        if (videoType == NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE) {
            long trimIn = videoClip.getTrimIn();
            long trimOut = getTrimOut();
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
            videoClip.setImageMotionMode(getImageDisplayMode());
            videoClip.setImageMotionAnimationEnabled(false);
        } else {
            //当前是视频  Currently video
            //设置左右声道值 0-8  Set the left and right channels to 0 to 8
            videoClip.setVolumeGain(volume, volume);
            //设置变速  Set speed change
            if (TextUtils.isEmpty(videoClip.getClipVariableSpeedCurvesString())) {
                if (speed > 0) {
                    //设置两遍的原因是，变速，分割的时候，如果只设置videoClip.changeSpeed(speed, isKeepAudioPitch())，达不到预期效果，会只显示最初的音频声音
                    // The reason for setting/twice is that if only videoClip.changeSpeed(speed, isKeepAudioPitch()) is set when shifting gears and splitting, it will not achieve the desired effect and only the original audio will be displayed
                    videoClip.changeSpeed(speed);
                    videoClip.changeSpeed(speed, isKeepAudioPitch());
                }
            }
            videoClip.setPanAndScan(getSpan(), getScan());
        }
        //设置旋转角度  Set rotation Angle
        videoClip.setExtraVideoRotation(extraRotation);
        /*
         * 如果是补黑素材设置透明度为0，让其全黑
         * If it is a black fill, set the opacity to 0 and make it all black
         */
        if (CommonData.CLIP_HOLDER.equals(getVideoType())) {
            videoClip.getPropertyVideoFx().setFloatVal(NvsConstants.PROPERTY_OPACITY, 0);
        } else {
            videoClip.getPropertyVideoFx().setFloatVal(NvsConstants.PROPERTY_OPACITY, getOpacity());
        }
        if (trimIn > 0) {
            videoClip.changeTrimInPoint(trimIn, true);
        }
        if (trimOut > 0 && trimOut > trimIn) {
            videoClip.changeTrimOutPoint(trimOut, true);
        }
        addStoryboards(trackIndex);

    }

    private void removeNvsVideoFx(String type) {
        MeicamVideoFx filterFx = getVideoFx(type);
        try {
            if (filterFx != null) {
                NvsVideoFx nvsVideoFx = filterFx.getObject();
                if (nvsVideoFx != null) {
                    getObject().removeFx(nvsVideoFx.getIndex());
                }
            }
        } catch (Exception e) {
            LogUtils.e("removeVideoFx:error:" + e.fillInStackTrace());
        }
    }

    private void addStoryboards(int trackIndex) {
        MeicamVideoFx transformNvsVideoFx = getVideoFx(NvsConstants.FX_TRANSFORM_2D);
        removeVideoFx(NvsConstants.FX_TRANSFORM_2D);//需要最后添加transform2D特效  You need to add the transform2D effect at the end

        Map<String, MeicamStoryboardInfo> storyboardInfos = getStoryboardInfos();
        MeicamStoryboardInfo cropperTransform = storyboardInfos.get(MeicamStoryboardInfo.SUB_TYPE_CROPPER_TRANSFROM);
        if (cropperTransform != null) {
            cropperTransform.bindToTimelineByType(getObject(), cropperTransform.getSubType());
        }
        MeicamStoryboardInfo cropper = storyboardInfos.get(MeicamStoryboardInfo.SUB_TYPE_CROPPER);
        if (cropper != null) {
            cropper.bindToTimelineByType(getObject(), cropper.getSubType());
        }

        if (transformNvsVideoFx != null) {//最后添加transform2D特效  You need to add the transform2D effect at the end
            transformNvsVideoFx.bindToTimeline(getObject());
            getVideoFxs().add(transformNvsVideoFx);
        }
        if (TimelineDataUtil.findPropertyFx(this) == null) {
            setDefaultBackground();
        } else {
            setBackground(this);
        }
        MeicamStoryboardInfo backgroundInfo = getBackgroundInfo();
        if (trackIndex == BaseConstants.TRACK_INDEX_MAIN && backgroundInfo != null) {
            backgroundInfo.bindToTimelineByType(getObject(), backgroundInfo.getSubType());
        }
    }

    private void setAdjustEffects(NvsVideoClip videoClip) {
        MeicamAdjustData meicamAdjustData = getMeicamAdjustData();
        if (meicamAdjustData == null) {
            return;
        }
        setAdjustEffect(videoClip, meicamAdjustData.getAmount(), NvsConstants.ADJUST_AMOUNT, NvsConstants.FX_SHARPEN);
        setAdjustEffect(videoClip, meicamAdjustData.getDegree(), NvsConstants.ADJUST_DEGREE, NvsConstants.FX_VIGNETTE);
        setAdjustEffect(videoClip, meicamAdjustData.getBlackPoint(), NvsConstants.ADJUST_BLACKPOINT, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getTint(), NvsConstants.ADJUST_TINT, NvsConstants.ADJUST_TINT);
        setAdjustEffect(videoClip, meicamAdjustData.getTemperature(), NvsConstants.ADJUST_TEMPERATURE, NvsConstants.ADJUST_TINT);
        setAdjustEffect(videoClip, meicamAdjustData.getShadow(), NvsConstants.ADJUST_SHADOW, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getHighlight(), NvsConstants.ADJUST_HIGHTLIGHT, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getSaturation(), NvsConstants.ADJUST_SATURATION, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getContrast(), NvsConstants.ADJUST_CONTRAST, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getBrightness(), NvsConstants.ADJUST_BRIGHTNESS, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
    }

    public void setAdjustEffects() {
        NvsVideoClip videoClip = getObject();
        if (videoClip == null) {
            return;
        }
        MeicamAdjustData meicamAdjustData = getMeicamAdjustData();
        if (meicamAdjustData == null) {
            return;
        }
        setAdjustEffect(videoClip, meicamAdjustData.getAmount(), NvsConstants.ADJUST_AMOUNT, NvsConstants.FX_SHARPEN);
        setAdjustEffect(videoClip, meicamAdjustData.getDegree(), NvsConstants.ADJUST_DEGREE, NvsConstants.FX_VIGNETTE);
        setAdjustEffect(videoClip, meicamAdjustData.getBlackPoint(), NvsConstants.ADJUST_BLACKPOINT, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getTint(), NvsConstants.ADJUST_TINT, NvsConstants.ADJUST_TINT);
        setAdjustEffect(videoClip, meicamAdjustData.getTemperature(), NvsConstants.ADJUST_TEMPERATURE, NvsConstants.ADJUST_TINT);
        setAdjustEffect(videoClip, meicamAdjustData.getShadow(), NvsConstants.ADJUST_SHADOW, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getHighlight(), NvsConstants.ADJUST_HIGHTLIGHT, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getSaturation(), NvsConstants.ADJUST_SATURATION, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getContrast(), NvsConstants.ADJUST_CONTRAST, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setAdjustEffect(videoClip, meicamAdjustData.getBrightness(), NvsConstants.ADJUST_BRIGHTNESS, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
    }

    @NonNull
    @Override
    public Object clone() {
        MeicamVideoClip clone = (MeicamVideoClip) DeepCopyUtil.deepClone(this);
        if (clone == null) {
            String json = LGsonContext.getInstance().getGson().toJson(this);
            clone = LGsonContext.getInstance().getGson().fromJson(json, MeicamVideoClip.class);
//            clone = new MeicamVideoClip();
//            clone.setId(getId());
//            clone.setFilePath(getFilePath());
//            clone.setReverseFilePath(getReverseFilePath());
//            clone.setVideoType(getVideoType());
//            clone.setTrimIn(getTrimIn());
//            clone.setTrimOut(getTrimOut());
//            clone.setInPoint(getInPoint());
//            clone.setOutPoint(getOutPoint());
//            clone.setOrgDuration(getOrgDuration());
//            clone.setVolume(getVolume());
//            clone.setOriginalWidth(getOriginalWidth());
//            clone.setOriginalHeight(getOriginalHeight());
//            clone.setSpeed(getSpeed());
//            clone.setCurveSpeed(getCurveSpeed());
//            clone.setCurveSpeedName(getCurveSpeedName());
//            clone.setVideoReverse(getVideoReverse());
//            clone.setConvertSuccess(isConvertSuccess());
//            clone.setmImageDisplayMode(getImageDisplayMode());
//            clone.setSpan(getSpan());
//            clone.setScan(getScan());
//            clone.setOpacity(getOpacity());
//            clone.setExtraRotation(getExtraRotation());
//            clone.setReverse(isReverse());
//            clone.setMeicamAdjustData(getMeicamAdjustData());
//            clone.setRoleInTheme(getRoleInTheme());
//            clone.setFaceEffectParameter(getFaceEffectParameter());
//            clone.setStoryboardInfos(getStoryboardInfos());
//            clone.setResourceId(getResourceId());
//            clone.setKeepAudioPitch(isKeepAudioPitch());
            //The effect information of the mask does not work in clone mode, so it needs to be set separately again
//            clone.setVideoFxs(getVideoFxs());
        }
        return clone;
    }

    public void setDefaultColorBlurBackground() {
        NvsVideoClip nvsVideoClip = getObject();
        if (nvsVideoClip != null) {
            nvsVideoClip.enablePropertyVideoFx(true);
            NvsVideoFx nvsVideoFx = nvsVideoClip.getPropertyVideoFx();
            nvsVideoFx.setMenuVal(NvsConstants.KEY_BACKGROUND_MODE, NvsConstants.VALUE_COLOR_BACKGROUND_MODE);
            nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_ROTATION, false);
//        nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_MUTLISAMPLE, true);
            nvsVideoFx.setColorVal(NvsConstants.KEY_BACKGROUND_COLOR, ColorUtil.colorToNvsColor(ColorsConstants.BACKGROUND_DEFAULT_COLOR));
            nvsVideoFx.setFloatVal(NvsConstants.KEY_BACKGROUND_BLUR_RADIUS, 0);
            MeicamVideoFx meicamVideoFx = TimelineDataUtil.findPropertyFx(this);
            meicamVideoFx.setStringVal(NvsConstants.KEY_BACKGROUND_COLOR, ColorsConstants.BACKGROUND_DEFAULT_COLOR);
            meicamVideoFx.setFloatVal(NvsConstants.KEY_BACKGROUND_BLUR_RADIUS, 0);
        }
    }

    public void setDefaultBackground() {
        NvsVideoClip nvsVideoClip = getObject();
        nvsVideoClip.enablePropertyVideoFx(true);
        MeicamVideoFx meicamVideoFx = TimelineDataUtil.findPropertyFx(this);
        if (meicamVideoFx == null) {
            meicamVideoFx = new MeicamVideoFx();
            meicamVideoFx.setDesc(NvsConstants.PROPERTY_FX);
            meicamVideoFx.setType(CommonData.TYPE_PROPERTY);
            getVideoFxs().add(meicamVideoFx);
        }
        NvsVideoFx nvsVideoFx = nvsVideoClip.getPropertyVideoFx();
        if (nvsVideoFx != null) {
            nvsVideoFx.setMenuVal(NvsConstants.KEY_BACKGROUND_MODE, NvsConstants.VALUE_COLOR_BACKGROUND_MODE);
            nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_ROTATION, false);
//        nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_MUTLISAMPLE, true);
            nvsVideoFx.setColorVal(NvsConstants.KEY_BACKGROUND_COLOR, ColorUtil.colorToNvsColor(ColorsConstants.BACKGROUND_DEFAULT_COLOR));
            nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_SCALE_X, 1.0);
            nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_SCALE_Y, 1.0);
            nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_TRANS_X, 0);
            nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_TRANS_Y, 0);
            nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_ROTATION, 0);
            nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_MUTLISAMPLE, true);
        }
        meicamVideoFx.setStringVal(NvsConstants.KEY_BACKGROUND_MODE, NvsConstants.VALUE_COLOR_BACKGROUND_MODE);
        meicamVideoFx.setStringVal(NvsConstants.KEY_BACKGROUND_COLOR, ColorsConstants.BACKGROUND_DEFAULT_COLOR);
        meicamVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_SCALE_X, 1.0F);
        meicamVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_SCALE_Y, 1.0F);
        meicamVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_TRANS_X, 0F);
        meicamVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_TRANS_Y, 0F);
        meicamVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_ROTATION, 0F);
        meicamVideoFx.setFloatVal(NvsConstants.KEY_BACKGROUND_BLUR_RADIUS, -1);
        meicamVideoFx.setStringVal(NvsConstants.KEY_BACKGROUND_IMAGE_PATH, "");

        meicamVideoFx.setStringVal(NvsConstants.POST_PACKAGE_ID, "");
        meicamVideoFx.setBooleanVal(NvsConstants.IS_POST_STORY_BOARD_3D, false);
        meicamVideoFx.setFloatVal(NvsConstants.PACKAGE_EFFECT_IN, 0);
        meicamVideoFx.setFloatVal(NvsConstants.PACKAGE_EFFECT_OUT, 0);

    }

    private void setBackground(MeicamVideoClip videoClip) {
        NvsVideoClip nvsVideoClip = videoClip.getObject();
        nvsVideoClip.enablePropertyVideoFx(true);
        MeicamVideoFx meicamVideoFx = TimelineDataUtil.findPropertyFx(videoClip);
        if (meicamVideoFx == null) {
            return;
        }
        NvsVideoFx nvsVideoFx = nvsVideoClip.getPropertyVideoFx();
        nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_ROTATION, false);
//        nvsVideoFx.setBooleanVal(NvsConstants.KEY_BACKGROUND_MUTLISAMPLE, true);
        Map<String, MeicamFxParam<?>> map = meicamVideoFx.getMeicamFxParam();
        MeicamFxParam meicamFxParam = map.get(NvsConstants.KEY_BACKGROUND_MODE);
        if (meicamFxParam != null && meicamFxParam.getValue().equals(NvsConstants.VALUE_COLOR_BACKGROUND_MODE)) {
            nvsVideoFx.setMenuVal(NvsConstants.KEY_BACKGROUND_MODE, NvsConstants.VALUE_COLOR_BACKGROUND_MODE);
            nvsVideoFx.setColorVal(NvsConstants.KEY_BACKGROUND_COLOR, ColorUtil.colorToNvsColor((String) map.get(NvsConstants.KEY_BACKGROUND_COLOR).getValue()));
        } else if (meicamFxParam != null && meicamFxParam.getValue().equals(NvsConstants.VALUE_IMAGE_BACKGROUND_MODE)) {
            nvsVideoFx.setMenuVal(NvsConstants.KEY_BACKGROUND_MODE, NvsConstants.VALUE_IMAGE_BACKGROUND_MODE);
            nvsVideoFx.setStringVal(NvsConstants.KEY_BACKGROUND_IMAGE_PATH, (String) map.get(NvsConstants.KEY_BACKGROUND_IMAGE_PATH).getValue());
        } else {
            nvsVideoFx.setMenuVal(NvsConstants.KEY_BACKGROUND_MODE, NvsConstants.VALUE_BLUR_BACKGROUND_MODE);
            float radius = 0;
            Object valueRadius = map.get(NvsConstants.KEY_BACKGROUND_BLUR_RADIUS).getValue();
            if (valueRadius != null) {
                if (valueRadius instanceof Float) {
                    radius = (float) valueRadius;
                } else {
                    radius = Float.parseFloat(valueRadius.toString());
                }
            }
            nvsVideoFx.setFloatVal(NvsConstants.KEY_BACKGROUND_BLUR_RADIUS, radius);
        }

        float sx, sy, tx, ty, ro, effectIn, effectOut;
        Object valueSX = map.get(NvsConstants.FX_TRANSFORM_2D_SCALE_X).getValue();
        Object valueSY = map.get(NvsConstants.FX_TRANSFORM_2D_SCALE_Y).getValue();
        Object valueTX = map.get(NvsConstants.FX_TRANSFORM_2D_TRANS_X).getValue();
        Object valueTY = map.get(NvsConstants.FX_TRANSFORM_2D_TRANS_Y).getValue();
        Object valueRO = map.get(NvsConstants.FX_TRANSFORM_2D_ROTATION).getValue();
        if (map.get(NvsConstants.POST_PACKAGE_ID) == null) {
            MeicamFxParam paramNine = new MeicamFxParam("String", NvsConstants.POST_PACKAGE_ID, "");
            MeicamFxParam paramTen = new MeicamFxParam("boolean", NvsConstants.IS_POST_STORY_BOARD_3D, false);
            MeicamFxParam paramEle = new MeicamFxParam("float", NvsConstants.PACKAGE_EFFECT_IN, 0);
            MeicamFxParam paramTwe = new MeicamFxParam("float", NvsConstants.PACKAGE_EFFECT_OUT, 0);
            map.put(NvsConstants.POST_PACKAGE_ID, paramNine);
            map.put(NvsConstants.IS_POST_STORY_BOARD_3D, paramTen);
            map.put(NvsConstants.PACKAGE_EFFECT_IN, paramEle);
            map.put(NvsConstants.PACKAGE_EFFECT_OUT, paramTwe);
        }

        /*
         * Change the key value from POST Package ID to Package ID
         * 做适配，key值发生变化，从Post Package Id 换成Package Id
         */
        String valuePackageId = "";
        MeicamFxParam param = map.get(NvsConstants.POST_PACKAGE_ID_OLD);
        if (param != null) {
            valuePackageId = (String) map.get(NvsConstants.POST_PACKAGE_ID_OLD).getValue();
            map.remove(NvsConstants.POST_PACKAGE_ID_OLD);
            meicamVideoFx.setStringVal(NvsConstants.POST_PACKAGE_ID, valuePackageId);
        } else {
            valuePackageId = (String) map.get(NvsConstants.POST_PACKAGE_ID).getValue();
        }
        boolean valueStoryBoard3D = false;
        Object value = map.get(NvsConstants.IS_POST_STORY_BOARD_3D).getValue();
        if (value instanceof Boolean) {
            valueStoryBoard3D = (boolean) value;
        }
        Object valueEffectIn = map.get(NvsConstants.PACKAGE_EFFECT_IN).getValue();
        Object valueEffectOut = map.get(NvsConstants.PACKAGE_EFFECT_OUT).getValue();
        if (valueSX instanceof Float) {
            sx = (float) valueSX;
        } else {
            sx = Float.parseFloat(valueSX.toString());
        }
        if (valueSY instanceof Float) {
            sy = (float) valueSY;
        } else {
            sy = Float.parseFloat(valueSY.toString());
        }
        if (valueTX instanceof Float) {
            tx = (float) valueTX;
        } else {
            tx = Float.parseFloat(valueTX.toString());
        }
        if (valueTY instanceof Float) {
            ty = (float) valueTY;
        } else {
            ty = Float.parseFloat(valueTY.toString());
        }
        if (valueRO instanceof Float) {
            ro = (float) valueRO;
        } else {
            ro = Float.parseFloat(valueRO.toString());
        }

        if (valueEffectIn instanceof Float) {
            effectIn = (float) valueEffectIn;
        } else {
            effectIn = Float.parseFloat(valueEffectIn.toString());
        }

        if (valueEffectOut instanceof Float) {
            effectOut = (float) valueEffectOut;
        } else {
            effectOut = Float.parseFloat(valueEffectOut.toString());
        }
        nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_SCALE_X, sx);
        nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_SCALE_Y, sy);
        nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_TRANS_X, tx);
        nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_TRANS_Y, ty);
        nvsVideoFx.setFloatVal(NvsConstants.FX_TRANSFORM_2D_ROTATION, ro);

        nvsVideoFx.setStringVal(NvsConstants.POST_PACKAGE_ID, valuePackageId);
        nvsVideoFx.setBooleanVal(NvsConstants.IS_POST_STORY_BOARD_3D, valueStoryBoard3D);
        nvsVideoFx.setFloatVal(NvsConstants.PACKAGE_EFFECT_IN, effectIn);
        nvsVideoFx.setFloatVal(NvsConstants.PACKAGE_EFFECT_OUT, effectOut);
    }

    public void setFilterIntensity(float intensity, String type) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        MeicamVideoFx filterFx = null;
        for (MeicamVideoFx videoFx : videoFxs) {
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
        MeicamVideoFx filterFx = null;
        for (MeicamVideoFx videoFx : videoFxs) {
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

    public MeicamVideoFx getVideoFx(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        for (MeicamVideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getSubType())) {
                return videoFx;
            }
        }
        return null;
    }

    public MeicamVideoFx getVideoFxByType(String type, String subType) {
        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(subType)) {
            return null;
        }
        for (MeicamVideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getType()) && subType.equals(videoFx.getSubType())) {
                return videoFx;
            }
        }
        return null;
    }

    public void removeVideoFx(String type) {
        MeicamVideoFx filterFx = getVideoFx(type);
        try {
            if (filterFx != null) {
                if (NvsConstants.KEY_MASK_GENERATOR.equals(type) && BaseConstants.EnableRawFilterMaskRender) {
                    getObject().removeRawFx(filterFx.getObject().getIndex());
                } else {
                    getObject().removeFx(filterFx.getObject().getIndex());
                }
                videoFxs.remove(filterFx);
            }
        } catch (Exception e) {
            LogUtils.e("removeVideoFx:error:" + e.fillInStackTrace());
        }
    }

    /**
     * 删除特效 remove VideoFx
     *
     * @param type    type of videoFx
     * @param subType subType of videoFx
     * @return videoFx removed
     */
    public MeicamVideoFx removeVideoFx(String type, String subType) {
        MeicamVideoFx filterFx = getVideoFxByType(type, subType);
        try {
            if (filterFx != null) {
                if (CommonData.TYPE_RAW_BUILTIN.equals(type)) {
                    getObject().removeRawFx(filterFx.getObject().getIndex());
                } else {
                    getObject().removeFx(filterFx.getObject().getIndex());
                }
                videoFxs.remove(filterFx);
            }
        } catch (Exception e) {
            LogUtils.e("removeVideoFx:error:" + e.fillInStackTrace());
        }
        return filterFx;
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
            nvsVideoFx.setBooleanVal(NvsConstants.KEY_VIDEO_MODE, true);
        } else {
            nvsVideoFx = videoClip.appendBuiltinFx(attachmentKey);
            nvsVideoFx.setAttachment(attachmentKey, attachmentKey);
            nvsVideoFx.setFloatVal(adjustKey, adjustData);
            nvsVideoFx.setBooleanVal(NvsConstants.KEY_VIDEO_MODE, true);
        }
        setRegionData(nvsVideoFx);
    }

    public void applyFaceEffect(int trackIndex) {

        NvsVideoClip videoClip = getObject();
        if (videoClip == null) {
            return;
        }

        int fxCount = videoClip.getFxCount();
        boolean isContainBeautyFx = false;
        NvsVideoFx arSceneFx = null;
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(i);
            Object attachment = videoFx.getAttachment(ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE);
            if (attachment != null) {
                isContainBeautyFx = true;
                arSceneFx = videoFx;
                break;
            }
        }
        if (!isContainBeautyFx) {
            arSceneFx = videoClip.appendBuiltinFx(VIDEO_FX_AR_SCENE);
            if (arSceneFx == null) {
                LogUtils.e("appendBuiltinFx arSceneFx is null");
                return;
            }
            arSceneFx.setAttachment(ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE, VIDEO_FX_AR_SCENE);
        }

        Set<String> keySet = mFaceEffectParameter.keySet();
        openBeautyFx(arSceneFx);
        if (trackIndex > 0) {
            setRegionData(arSceneFx);
        }
        for (String key : keySet) {
            arSceneFx.setFloatVal(key, mFaceEffectParameter.get(key));
        }
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
        if (resolution == null) {
            return;
        }
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

    /**
     * 仅仅设置美肤
     * Just set the beauty
     */
    public void applyBeautyFaceEffect() {

        NvsVideoClip videoClip = getObject();
        if (videoClip == null) {
            return;
        }

        int fxCount = videoClip.getFxCount();
        boolean isContainBeautyFx = false;
        NvsVideoFx arSceneFx = null;
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(i);
            Object attachment = videoFx.getAttachment(ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE);
            if (attachment != null) {
                isContainBeautyFx = true;
                arSceneFx = videoFx;
                break;
            }
        }
        if (!isContainBeautyFx) {
            arSceneFx = videoClip.appendBuiltinFx(VIDEO_FX_AR_SCENE);
            if (arSceneFx == null) {
                LogUtils.e("appendBuiltinFx arSceneFx is null");
                return;
            }
            arSceneFx.setAttachment(ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE, VIDEO_FX_AR_SCENE);
        }

        openBeautyFx(arSceneFx);
        if (mFaceEffectParameter == null) {
            return;
        }
        Float strength = mFaceEffectParameter.get(VIDEO_FX_BEAUTY_STRENGTH);
        Float whitening = mFaceEffectParameter.get(VIDEO_FX_BEAUTY_WHITENING);
        Float ruddy = mFaceEffectParameter.get(VIDEO_FX_BEAUTY_REDDENING);
        arSceneFx.setFloatVal(VIDEO_FX_BEAUTY_STRENGTH, strength == null ? 50 / 100f : strength);
        arSceneFx.setFloatVal(VIDEO_FX_BEAUTY_WHITENING, whitening == null ? 0 : whitening);
        arSceneFx.setFloatVal(VIDEO_FX_BEAUTY_REDDENING, ruddy == null ? 0 : ruddy);

    }


    /**
     * 仅仅设置美型
     * Just set the beauty type
     */
    public void applyBeautyShapeFaceEffect() {

        NvsVideoClip videoClip = getObject();
        if (videoClip == null) {
            return;
        }

        int fxCount = videoClip.getFxCount();
        boolean isContainBeautyFx = false;
        NvsVideoFx arSceneFx = null;
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(i);
            Object attachment = videoFx.getAttachment(ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE);
            if (attachment != null) {
                isContainBeautyFx = true;
                arSceneFx = videoFx;
                break;
            }
        }
        if (!isContainBeautyFx) {
            arSceneFx = videoClip.appendBuiltinFx(VIDEO_FX_AR_SCENE);
            if (arSceneFx == null) {
                LogUtils.e("appendBuiltinFx arSceneFx is null");
                return;
            }
            arSceneFx.setAttachment(ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE, VIDEO_FX_AR_SCENE);
        }

        Set<String> keySet = mFaceEffectParameter.keySet();
        openBeautyFx(arSceneFx);
        for (String key : keySet) {
            if (!isBeautyEffectKey(key)) {
                arSceneFx.setFloatVal(key, mFaceEffectParameter.get(key));
            }
        }

    }

    /**
     * 是否是 美肤
     * Whether it is beauty or not
     * @param key
     * @return
     */
    public boolean isBeautyEffectKey(String key) {
        if (VIDEO_FX_BEAUTY_STRENGTH.equals(key) || VIDEO_FX_BEAUTY_WHITENING.equals(key) || VIDEO_FX_BEAUTY_REDDENING.equals(key)) {
            return true;
        }
        return false;
    }

    /**
     * 重置 美型
     * reset shape
     */
    public void resetBeautyShape(NvsVideoFx nvsVideoFx) {
        Set<String> keySet = mFaceEffectParameter.keySet();
        openBeautyFx(nvsVideoFx);
        for (String key : keySet) {
            if (!isBeautyEffectKey(key)) {
                nvsVideoFx.setFloatVal(key, 0);
            }
        }
    }


    /**
     * 重置 美肤
     * Resetting skin
     */
    public void resetBeauty(NvsVideoFx nvsVideoFx) {
        if (nvsVideoFx == null) {
            return;
        }
        openBeautyFx(nvsVideoFx);
        nvsVideoFx.setFloatVal(VIDEO_FX_BEAUTY_STRENGTH, 50 / 100f);
        nvsVideoFx.setFloatVal(VIDEO_FX_BEAUTY_WHITENING, 0f);
        nvsVideoFx.setFloatVal(VIDEO_FX_BEAUTY_REDDENING, 0f);

        mFaceEffectParameter.put(VIDEO_FX_BEAUTY_STRENGTH, 50 / 100f);
        mFaceEffectParameter.put(VIDEO_FX_BEAUTY_WHITENING, 0f);
        mFaceEffectParameter.put(VIDEO_FX_BEAUTY_REDDENING, 0f);
    }


    public void openBeautyFx(NvsVideoFx videoFx) {
        videoFx.setBooleanVal(VIDEO_FX_BEAUTY_EFFECT, true);
        videoFx.setBooleanVal(VIDEO_FX_BEAUTY_SHAPE, true);
        videoFx.setBooleanVal(VIDEO_FX_SINGLE_BUFFER_MODE, false);
        NvsARSceneManipulate arSceneManipulate = videoFx.getARSceneManipulate();
        if (arSceneManipulate != null) {
            arSceneManipulate.setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
        }
    }

    @Override
    public LMeicamVideoClip parseToLocalData() {
        parseToResourceId();
        LMeicamVideoClip local = new LMeicamVideoClip();
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
        local.setResourceId(resourceId);
        local.setKeepAudioPitch(isKeepAudioPitch());
        local.setOriginalWidth(getOriginalWidth());
        local.setOriginalHeight(getOriginalHeight());
        for (MeicamVideoFx videoFx : videoFxs) {
            local.getVideoFxs().add(videoFx.parseToLocalData());
        }
        if (mMeicamAdjustData != null) {
            local.setMeicamAdjustData(mMeicamAdjustData.parseToLocalData());
        }
        local.setRoleInTheme(getRoleInTheme());
        Map<String, Float> localEffectParameter = new HashMap<>();
        Set<String> keySet = mFaceEffectParameter.keySet();
        for (String key : keySet) {
            localEffectParameter.put(key, mFaceEffectParameter.get(key));
        }
        local.setFaceEffectParameter(localEffectParameter);
        Set<String> storyKeySet = mStoryboardInfos.keySet();
        for (String key : storyKeySet) {
            local.getStoryboardInfos().add(mStoryboardInfos.get(key).parseToLocalData());
        }
        return local;
    }

    @Override
    public void parseToResourceId() {
        if (!TextUtils.isEmpty(reverseFilePath) || !TextUtils.isEmpty(filePath)) {
            MeicamResource resource = new MeicamResource();
            if (!TextUtils.isEmpty(reverseFilePath)) {
                resource.addPathInfo(new MeicamResource.PathInfo("reversePath", reverseFilePath, true));
            }
            if (!TextUtils.isEmpty(filePath)) {
                resource.addPathInfo(new MeicamResource.PathInfo("path", filePath, true));
            }
            resourceId = TimelineData.getInstance().getPlaceId(resource);
        }
    }
}
