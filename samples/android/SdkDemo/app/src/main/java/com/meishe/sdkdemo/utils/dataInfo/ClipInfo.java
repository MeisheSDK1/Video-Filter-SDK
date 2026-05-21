package com.meishe.sdkdemo.utils.dataInfo;

import android.graphics.RectF;
import android.text.TextUtils;

import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.engine.bean.CutData;
import com.meishe.sdkdemo.edit.data.ChangeSpeedCurveInfo;
import com.meishe.sdkdemo.edit.data.LivePhotoInfo;
import com.meishe.sdkdemo.edit.data.mask.MaskInfoData;
import com.meishe.sdkdemo.utils.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_DEFAULTVALUE;

public class ClipInfo {
    public int trackIndex;
    public boolean isRecFile = false;
    public int rotation = NvsVideoStreamInfo.VIDEO_ROTATION_0;
    private String m_filePath;
    private String coverPath;
    private float m_speed;
    private boolean keepAudioPitch;
    private boolean m_mute;
    private long m_trimIn;
    private long m_trimOut;
    private long inPoint;
    private long outPoint;
    private double m_start_speed = 1;
    private double m_end_speed = 1;
    private long duration;
    /**
     * 音量
     * volume
     */
    private float m_volume;
    /**
     * 降噪等级
     * 有四个等级 1 2 3 4
     * 0 代表没有设置降噪
     * Noise reduction grade
     * There are four levels: 1, 2, 3, 4
     * 0 indicates that no noise reduction is set
     */
    private int noiseSuppressionLevel;
    /**
     * 旋转角度
     * Rotation angle
     */
    private int m_rotateAngle;
    /**
     * 校色数据
     * Correction color info
     */
    private CorrectionColorInfo mCorrectionColorInfo;
    private int m_scaleX;
    private int m_scaleY;
    /**
     * 图片展示模式
     * Picture display mode
     */
    private int m_imgDispalyMode = Constants.EDIT_MODE_PHOTO_AREA_DISPLAY;
    /**
     * 是否开启图片运动
     * Whether to enable picture campaign
     */
    private boolean isOpenPhotoMove = false;
    /**
     * 图片起始ROI
     * Picture starting ROI
     */
    private RectF m_normalStartROI;
    /**
     * 图片终止ROI
     * Picture termination ROI
     */
    private RectF m_normalEndROI;

    /**
     * 视频横向裁剪，纵向平移
     * Video cropped horizontally, panned vertically
     */
    private float m_pan;
    private float m_scan;

    /**
     * 片段滤镜
     * Fragment filter
     */
    private VideoClipFxInfo m_videoClipFxInfo;

    private String m_fxStoryBoardFileName;
    private String m_clipStoryBoardFileName;

    /**
     * 最小模糊区域的比例
     * The proportion of the least fuzzy area
     */
    private String m_minBlurRectAspectRatio;
    /**
     * 最大模糊区域的比例
     * The proportion of the maximum fuzzy area
     */
    private String m_maxBlurRectAspectRatio;

    private int m_isBlurInFront;

    private long mAnimationDuration;
    private int mAnimationType;

    private String mBackgroundValue;

    /**
     * clip设置的VideoFx transform 2D 等
     * clip set VideoFx transform 2D and so on
     */
    private final List<VideoFx> videoFxs = new ArrayList<>();

    private ChangeSpeedCurveInfo mCurveSpeed;
    /**
     * 音频贝塞尔曲线数据
     * Audio Bessel curve data
     */
    private Map<Long, VolumeKeyInfo> mVolumeKeyFrameInfoHashMap = new TreeMap<>(
            new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    // 时间升序排列 Time is arranged in ascending order
                    return o1.compareTo(o2);
                }
            }
    );
    /**
     * 蒙版数据
     * Mask data
     */
    private MaskInfoData maskInfoData;
    /**
     * 蒙版用原视频宽高比例
     * Mask with the original video aspect ratio
     */
    private float fileRatio = 1;
    /**
     * 背景信息
     * Background Information
     */
    private BackGroundInfo backGroundInfo;
    /**
     * 调整(裁剪的数据)
     * To adjust (cropped data).
     */
    private CutData cropInfo;
    /**
     * 动画
     * animation
     */
    private AnimationInfo animationInfo;
    /**
     * 贴纸
     * sticker
     */
    private List<StickerInfo> stickerInfoList;
    /**
     * 字幕
     * caption
     */
    private List<CaptionInfo> captionInfoList;

    /**
     * 美颜数据，包括美肤，美型，微整形
     * Beauty data, including beauty skin, beauty type, micro plastic surgery
     */
    private BeautyFxInfo beautyFxInfo;

    private LivePhotoInfo livePhotoInfo;

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public List<CaptionInfo> getCaptionInfoList() {
        return captionInfoList;
    }

    public void setCaptionInfoList(List<CaptionInfo> captionInfoList) {
        this.captionInfoList = captionInfoList;
    }

    public List<StickerInfo> getStickerInfoList() {
        return stickerInfoList;
    }

    public void setStickerInfoList(List<StickerInfo> stickerInfoList) {
        this.stickerInfoList = stickerInfoList;
    }

    public AnimationInfo getAnimationInfo() {
        return animationInfo;
    }

    public void setAnimationInfo(AnimationInfo animationInfo) {
        this.animationInfo = animationInfo;
    }

    public CutData getCropInfo() {
        return cropInfo;
    }

    public void setCropInfo(CutData cropInfo) {
        this.cropInfo = cropInfo;
    }

    public BackGroundInfo getBackGroundInfo() {
        return backGroundInfo;
    }

    public void setBackGroundInfo(BackGroundInfo backGroundInfo) {
        this.backGroundInfo = backGroundInfo;
    }

    public Map<Long, VolumeKeyInfo> getVolumeKeyFrameInfoHashMap() {
        return mVolumeKeyFrameInfoHashMap;
    }

    public void setVolumeKeyFrameInfoHashMap(Map<Long, VolumeKeyInfo> mVolumeKeyFrameInfoHashMap) {
        this.mVolumeKeyFrameInfoHashMap = mVolumeKeyFrameInfoHashMap;
    }

    public MaskInfoData getMaskInfoData() {
        return maskInfoData;
    }

    public void setMaskInfoData(MaskInfoData maskInfoData) {
        this.maskInfoData = maskInfoData;
    }

    public float getFileRatio() {
        return fileRatio;
    }

    public void setFileRatio(float fileRatio) {
        this.fileRatio = fileRatio;
    }

    public List<VideoFx> getVideoFxs() {
        return videoFxs;
    }

    public VideoFx getVideoFx(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        for (VideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getSubType())) {
                return videoFx;
            }
        }
        return null;
    }


    private Map<String, StoryboardInfo> mStoryboardInfos = new TreeMap<>();

    public void addStoryboardInfo(String key, StoryboardInfo storyInfo) {
        mStoryboardInfos.put(key, storyInfo);
    }

    public void setStoryboardInfos(Map<String, StoryboardInfo> storyboardInfos) {
        mStoryboardInfos = storyboardInfos;
    }

    public void setmAnimationDuration(long mAnimationDuration) {
        this.mAnimationDuration = mAnimationDuration;
    }

    public void setmAnimationType(int mAnimationType) {
        this.mAnimationType = mAnimationType;
    }

    public long getmAnimationDuration() {
        return mAnimationDuration;
    }

    public int getmAnimationType() {
        return mAnimationType;
    }

    public float getPan() {
        return m_pan;
    }

    public void setPan(float pan) {
        this.m_pan = pan;
    }

    public float getScan() {
        return m_scan;
    }

    public void setScan(float scan) {
        this.m_scan = scan;
    }

    public RectF getNormalStartROI() {
        return m_normalStartROI;
    }

    public void setNormalStartROI(RectF normalStartROI) {
        this.m_normalStartROI = normalStartROI;
    }

    public RectF getNormalEndROI() {
        return m_normalEndROI;
    }

    public void setNormalEndROI(RectF normalEndROI) {
        this.m_normalEndROI = normalEndROI;
    }

    public boolean isOpenPhotoMove() {
        return isOpenPhotoMove;
    }

    public void setOpenPhotoMove(boolean openPhotoMove) {
        isOpenPhotoMove = openPhotoMove;
    }

    public int getImgDispalyMode() {
        return m_imgDispalyMode;
    }

    public void setImgDispalyMode(int imgDispalyMode) {
        m_imgDispalyMode = imgDispalyMode;
    }

    public int getScaleX() {
        return m_scaleX;
    }

    public void setScaleX(int scaleX) {
        this.m_scaleX = scaleX;
    }

    public int getScaleY() {
        return m_scaleY;
    }

    public void setScaleY(int scaleY) {
        this.m_scaleY = scaleY;
    }

    public int getRotateAngle() {
        return m_rotateAngle;
    }

    public void setRotateAngle(int rotateAngle) {
        this.m_rotateAngle = rotateAngle;
    }

    public float getVolume() {
        return m_volume;
    }

    public void setVolume(float volume) {
        this.m_volume = volume;
    }


    public double getStartSpeed() {
        return m_start_speed;
    }

    public void setStartSpeed(double startSpeed) {
        this.m_start_speed = startSpeed;
    }

    public double getEndSpeed() {
        return m_end_speed;
    }

    public void setEndSpeed(double endSpeed) {
        this.m_end_speed = endSpeed;
    }

    public void setFilePath(String filePath) {
        m_filePath = filePath;
    }

    public String getFilePath() {
        return m_filePath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public void setSpeed(float speed) {
        m_speed = speed;
    }

    public float getSpeed() {
        return m_speed;
    }

    public void setKeepAudioPitch(boolean keepAudioPitch) {
        this.keepAudioPitch = keepAudioPitch;
    }

    public boolean isKeepAudioPitch() {
        return keepAudioPitch;
    }

    public void setmCurveSpeed(ChangeSpeedCurveInfo mCurveSpeed) {
        this.mCurveSpeed = mCurveSpeed;
    }

    public ChangeSpeedCurveInfo getmCurveSpeed() {
        return mCurveSpeed;
    }

    public void setMute(boolean flag) {
        m_mute = flag;
    }

    public boolean getMute() {
        return m_mute;
    }

    public void changeTrimIn(long data) {
        m_trimIn = data;
    }

    public long getTrimIn() {
        return m_trimIn;
    }

    public void changeTrimOut(long data) {
        m_trimOut = data;
    }

    public long getTrimOut() {
        return m_trimOut;
    }

    public long getInPoint() {
        return inPoint;
    }

    public void setInPoint(long inPoint) {
        this.inPoint = inPoint;
    }

    public long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.outPoint = outPoint;
    }

    public VideoClipFxInfo getVideoClipFxInfo() {
        return m_videoClipFxInfo;
    }

    public void setVideoClipFxInfo(VideoClipFxInfo videoClipFxInfo) {
        this.m_videoClipFxInfo = videoClipFxInfo;
    }

    public String getFxStoryBoardFileName() {
        return m_fxStoryBoardFileName;
    }

    public void setFxStoryBoardFileName(String storyBoardFileName) {
        this.m_fxStoryBoardFileName = storyBoardFileName;
    }

    public String getClipStoryBoardFileName() {
        return m_clipStoryBoardFileName;
    }

    public void setClipStoryBoardFileName(String storyBoardFileName) {
        this.m_clipStoryBoardFileName = storyBoardFileName;
    }

    public String getMinBlurRectAspectRatio() {
        return m_minBlurRectAspectRatio;
    }

    public void setMinBlurRectAspectRatio(String blurRectAspectRatio) {
        this.m_minBlurRectAspectRatio = blurRectAspectRatio;
    }

    public String getMaxBlurRectAspectRatio() {
        return m_maxBlurRectAspectRatio;
    }

    public void setMaxBlurRectAspectRatio(String blurRectAspectRatio) {
        this.m_maxBlurRectAspectRatio = blurRectAspectRatio;
    }

    public int getIsBlurInFront() {
        return m_isBlurInFront;
    }

    public void setIsBlurInFront(int isBlurInFront) {
        this.m_isBlurInFront = isBlurInFront;
    }

    public String getBackgroundValue() {
        return mBackgroundValue;
    }

    public void setBackgroundValue(String colorValue) {
        this.mBackgroundValue = colorValue;
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public BeautyFxInfo getBeautyFxInfo() {
        return beautyFxInfo;
    }

    public void setBeautyFxInfo(BeautyFxInfo beautyFxInfo) {
        this.beautyFxInfo = beautyFxInfo;
    }

    public LivePhotoInfo getLivePhotoInfo() {
        return livePhotoInfo;
    }

    public void setLivePhotoInfo(LivePhotoInfo livePhotoInfo) {
        this.livePhotoInfo = livePhotoInfo;
    }

    public ClipInfo() {
        trackIndex = 0;
        m_filePath = null;
        coverPath = null;
        m_speed = 1.0f;
        m_mute = false;
        m_trimIn = -1;
        m_trimOut = -1;
        inPoint = -1;
        outPoint =-1;
        m_volume = VIDEOVOLUME_DEFAULTVALUE;
        m_rotateAngle = 0;
        m_scaleX = -2;
        m_scaleY = -2;
        m_pan = 0.0f;
        m_scan = 0.0f;
        keepAudioPitch = true;
        noiseSuppressionLevel = 0;
    }

    public ClipInfo clone() {
        ClipInfo newClipInfo = new ClipInfo();
        newClipInfo.setTrackIndex(this.getTrackIndex());
        newClipInfo.isRecFile = this.isRecFile;
        newClipInfo.rotation = this.rotation;
        newClipInfo.setFilePath(this.getFilePath());
        newClipInfo.setMute(this.getMute());
        newClipInfo.setSpeed(this.getSpeed());
        newClipInfo.setKeepAudioPitch(this.isKeepAudioPitch());
        newClipInfo.setmCurveSpeed(this.getmCurveSpeed());
        newClipInfo.changeTrimIn(this.getTrimIn());
        newClipInfo.changeTrimOut(this.getTrimOut());
        newClipInfo.setInPoint(this.getInPoint());
        newClipInfo.setOutPoint(this.getOutPoint());
        newClipInfo.setVolumeKeyFrameInfoHashMap(this.getVolumeKeyFrameInfoHashMap());
        newClipInfo.setVolume(this.getVolume());
        newClipInfo.setNoiseSuppressionLevel(this.getNoiseSuppressionLevel());
        newClipInfo.setRotateAngle(this.getRotateAngle());
        newClipInfo.setScaleX(this.getScaleX());
        newClipInfo.setScaleY(this.getScaleY());
        newClipInfo.setVideoClipFxInfo(this.getVideoClipFxInfo());
        newClipInfo.setDuration(this.getDuration());
        newClipInfo.setCoverPath(this.getCoverPath());

        /*
         * 图片数据
         * Picture data
         * */
        newClipInfo.setImgDispalyMode(this.getImgDispalyMode());
        newClipInfo.setOpenPhotoMove(this.isOpenPhotoMove());
        newClipInfo.setNormalStartROI(this.getNormalStartROI());
        newClipInfo.setNormalEndROI(this.getNormalEndROI());

        /*
         * 视频横向裁剪，纵向平移
         * Video cropped horizontally, panned vertically
         * */
        newClipInfo.setPan(this.getPan());
        newClipInfo.setScan(this.getScan());

        /*
         * 视屏片段
         * Video clip
         * */
        newClipInfo.setVideoClipFxInfo(this.getVideoClipFxInfo());

        //动画 animation
        AnimationInfo animationInfo = getAnimationInfo();
        if (animationInfo != null) {
            newClipInfo.setAnimationInfo(animationInfo.clone());
        }
        /*
         * 调整裁剪
         * Adjustment cutting
         */
//        newClipInfo.setStoryboardInfos(this.getStoryboardInfos());
        CutData cropInfo = getCropInfo();
        if (cropInfo != null) {
            newClipInfo.setCropInfo(cropInfo.clone());
        }
        List<StickerInfo> stickerInfoList = getStickerInfoList();
        if (stickerInfoList != null) {
            List<StickerInfo> newStickerInfoList = new ArrayList<>();
            for (int i = 0; i < stickerInfoList.size(); i++) {
                newStickerInfoList.add(stickerInfoList.get(i).clone());
            }
            newClipInfo.setStickerInfoList(newStickerInfoList);
        }
        List<CaptionInfo> captionInfoList = getCaptionInfoList();
        if (captionInfoList != null) {
            List<CaptionInfo> newCaptionList = new ArrayList<>();
            for (int i = 0; i < captionInfoList.size(); i++) {
                newCaptionList.add(captionInfoList.get(i).clone());
            }
            newClipInfo.setCaptionInfoList(newCaptionList);
        }
        //蒙版 mask
        MaskInfoData maskInfoData = getMaskInfoData();
        if (maskInfoData != null) {
            newClipInfo.setMaskInfoData(maskInfoData.clone());
        }
        //背景 BackGround
        BackGroundInfo backGroundInfo = getBackGroundInfo();
        if (backGroundInfo != null) {
            newClipInfo.setBackGroundInfo(backGroundInfo.clone());
        }
        //美颜 Beauty
        BeautyFxInfo beautyFxInfo = getBeautyFxInfo();
        if (null != beautyFxInfo) {
            newClipInfo.setBeautyFxInfo(beautyFxInfo);
        }
        CorrectionColorInfo correctionColorInfo = getCorrectionColorInfo();
        if (correctionColorInfo != null) {
            newClipInfo.setCorrectionColorInfo(correctionColorInfo.clone());
        }
        //livephoto
        LivePhotoInfo photoInfo = getLivePhotoInfo();
        if (null != photoInfo) {
            newClipInfo.setLivePhotoInfo(photoInfo.clone());
        }
        return newClipInfo;
    }

    public void removeBackground(NvsVideoClip videoClip) {
        if (mStoryboardInfos.isEmpty()) {
            return;
        }
        StoryboardInfo backgroundInfo = mStoryboardInfos.get(StoryboardInfo.SUB_TYPE_BACKGROUND);
        if (backgroundInfo == null) {
            return;
        }
        if (videoClip == null) {
            return;
        }

        NvsVideoFx nvsVideoFx = backgroundInfo.getStoryboardFx(videoClip, backgroundInfo.getSubType());
        if (nvsVideoFx == null) {
            return;
        }
        videoClip.removeFx(nvsVideoFx.getIndex());
        mStoryboardInfos.remove(StoryboardInfo.SUB_TYPE_BACKGROUND);
    }

    public BackGroundInfo buildBackgroundInfo(int backGroundType) {
        if (backGroundInfo == null) {
            backGroundInfo = new BackGroundInfo();
        }
        backGroundInfo.setType(backGroundType);
        return getBackGroundInfo();
    }

    public void setNoiseSuppressionLevel(int noiseSuppressionLevel) {
        this.noiseSuppressionLevel = noiseSuppressionLevel;
    }

    public int getNoiseSuppressionLevel() {
        return noiseSuppressionLevel;
    }

    public CorrectionColorInfo getCorrectionColorInfo() {
        return mCorrectionColorInfo;
    }

    public void setCorrectionColorInfo(CorrectionColorInfo mCorrectionColorInfo) {
        this.mCorrectionColorInfo = mCorrectionColorInfo;
    }
}
