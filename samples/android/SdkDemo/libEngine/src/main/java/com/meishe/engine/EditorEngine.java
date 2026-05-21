package com.meishe.engine;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.text.TextUtils;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFrameRetriever;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.ImageUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.Utils;
import com.meishe.engine.bean.BaseInfo;
import com.meishe.engine.bean.ClipInfo;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamAudioClip;
import com.meishe.engine.bean.MeicamCaptionClip;
import com.meishe.engine.bean.MeicamCompoundCaptionClip;
import com.meishe.engine.bean.MeicamStickerCaptionTrack;
import com.meishe.engine.bean.MeicamStickerClip;
import com.meishe.engine.bean.MeicamTheme;
import com.meishe.engine.bean.MeicamTimelineVideoFx;
import com.meishe.engine.bean.MeicamTimelineVideoFxClip;
import com.meishe.engine.bean.MeicamTimelineVideoFxTrack;
import com.meishe.engine.bean.MeicamTransition;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoFx;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.bean.TimelineDataUtil;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.engine.interf.EditOperater;
import com.meishe.engine.interf.IBaseInfo;
import com.meishe.engine.util.ColorUtil;
import com.meishe.engine.util.CoordinateUtil;
import com.meishe.engine.util.PathUtils;
import com.meishe.engine.util.TimelineUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.meicam.sdk.NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK;
import static com.meishe.engine.bean.CommonData.EFFECT_BUILTIN;
import static com.meishe.engine.bean.CommonData.EFFECT_PACKAGE;
import static com.meishe.engine.bean.CommonData.MAIN_TRACK_INDEX;
import static com.meishe.engine.bean.CommonData.SUB_TYPE_MASK;
import static com.meishe.engine.bean.CommonData.TYPE_BUILD_IN;
import static com.meishe.engine.bean.CommonData.TYPE_CAPTION;
import static com.meishe.engine.bean.CommonData.TYPE_COMPOUND_CAPTION;
import static com.meishe.engine.bean.CommonData.TYPE_EFFECT;
import static com.meishe.engine.bean.CommonData.TYPE_PACKAGE;
import static com.meishe.engine.bean.CommonData.TYPE_STICKER;
import static com.meishe.engine.bean.MeicamCaptionClip.CAPTION_ALIGN_BOTTOM;
import static com.meishe.engine.bean.MeicamCaptionClip.CAPTION_ALIGN_HORIZ_CENTER;
import static com.meishe.engine.bean.MeicamCaptionClip.CAPTION_ALIGN_LEFT;
import static com.meishe.engine.bean.MeicamCaptionClip.CAPTION_ALIGN_RIGHT;
import static com.meishe.engine.bean.MeicamCaptionClip.CAPTION_ALIGN_TOP;
import static com.meishe.engine.bean.MeicamCaptionClip.CAPTION_ALIGN_VERT_CENTER;

/**
 * All rights reserved,Designed by www.meishesdk.com
 * 版权所有:www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2020/12/18 10:20
 * @Description : 封装sdk api. Encapsulation sdk api.详细方法请参考：https://www.meishesdk.com/android/doc_ch/html/content/index.html
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class EditorEngine implements EditOperater {

    /**
     * 动画的默认时长
     * The constant ANIMATION_DEFAULT_DURATION.
     */
    public static int ANIMATION_DEFAULT_DURATION = 600;
    /**
     * 入动画和出动画的默认时长
     * The constant IN_OUT_ANIMATION_DEFAULT_DURATION.
     */
    public static int IN_OUT_ANIMATION_DEFAULT_DURATION = 500;


    /**
     * 返回值枚举
     * Enum of return code enumeration of return values
     */
    public static class ReturnCode {

        /**
         * 正确
         * Result is ok
         */
        public static final int CODE_OK = 1;
        /**
         * 参数错误
         * Param is error
         */
        public static final int CODE_PARAM_ERROR = 2;

        /**
         * 此操作不允许执行
         * Can not do this operation,
         */
        public static final int CODE_CAN_NOT_OPERATE = 4;
        /**
         * 其它错误
         * Other error
         */
        public static final int CODE_OTHER = 8;
    }

    private static final long MAX_IMAGE_LENGTH = 40 * 60 * 1000 * 1000L;
    private NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext;
    private OnTimelineChangeListener mOnTimelineChangeListener;
    private static final EditorEngine INSTANCE = new EditorEngine();
    private long mStickerDuration = 0;

    /**
     * 选中的音乐片段
     * Selected piece of music
     */
    private NvsAudioTrack mNvsAudioTrack = null;
    private MeicamAudioClip mMeicamAudioClip = null;
    private NvsAudioClip mNvsAudioClip;
    private OnTrackChangeListener mOnTrackChangeListener;
    private MeicamTimelineVideoFxTrack mVideoFxTrackClone;
    private int mEditType = -1;
    private boolean hasAudio = false;

    /**
     * use Face 是否使用到了人脸美型功能
     * Have you used the facial beauty function
     */
    private boolean useFaceShape = true;
    private PointF mLiveWindowSize = null;

    private EditorEngine() {
        mStreamingContext = NvsStreamingContext.getInstance();
        if (mStreamingContext == null) {
            initStreamContext();
        }
    }

    public static EditorEngine getInstance() {
        return INSTANCE;
    }

    /**
     * Init stream context nvsstreamingcontext.
     * 初始化NvsStreamingContext
     *
     * @return the nvs streaming context
     */
    public NvsStreamingContext initStreamContext() {
        String licensePath = "assets:/meishesdk.lic";
        mStreamingContext = NvsStreamingContext.init(Utils.getApp(), licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT);
        return mStreamingContext;
    }

    /**
     * Sets on timeline change listener.
     *
     * @param onTimelineChangeListener the on timeline change listener
     */
    public void setOnTimelineChangeListener(OnTimelineChangeListener onTimelineChangeListener) {
        this.mOnTimelineChangeListener = onTimelineChangeListener;
    }

    /**
     * Sets on track change listener.
     *
     * @param mOnTrackChangeListener the m on track change listener
     */
    public void setOnTrackChangeListener(OnTrackChangeListener mOnTrackChangeListener) {
        this.mOnTrackChangeListener = mOnTrackChangeListener;
    }

    /**
     * Create timeline nvs timeline.
     *
     * @return the nvs timeline
     */
    public NvsTimeline createTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        return mTimeline;
    }


    /**
     * Sets base ui clip.
     *
     * @param baseUIClip the base ui clip
     */
    public void setBaseUIClip(MeicamAudioClip baseUIClip) {
        if (baseUIClip == null) {
            return;
        }
        mNvsAudioTrack = mTimeline.getAudioTrackByIndex(baseUIClip.getIndex());
        if (mNvsAudioTrack == null) {
            LogUtils.e("NvsAudioTrack is null");
            return;
        }
        mMeicamAudioClip = baseUIClip;

        for (int i = 0; i < mNvsAudioTrack.getClipCount(); i++) {
            NvsAudioClip nvsAudioClip = mNvsAudioTrack.getClipByIndex(i);
            if (nvsAudioClip.getInPoint() == mMeicamAudioClip.getInPoint()) {
                mNvsAudioClip = nvsAudioClip;
                break;
            }
        }
    }


    @Override
    public NvsTimeline getCurrentTimeline() {
        if (mTimeline == null) {
            createTimeline();
        }
        return mTimeline;
    }

    /**
     * Sets eidt type.
     *
     * @param editType the edit type
     */
    public void setEidtType(int editType) {
        mEditType = editType;
    }

    /**
     * Gets edit type.
     *
     * @return the edit type
     */
    public int getEditType() {
        return mEditType;
    }

    @Override
    public MeicamVideoClip getEditVideoClip(long timeStamp, int trackIndex) {
        List<MeicamVideoTrack> videoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(videoTrackList)) {
            return null;
        }
        if (!CommonUtils.isIndexAvailable(trackIndex, videoTrackList)) {
            return null;
        }
        MeicamVideoTrack meicamVideoTrack = videoTrackList.get(trackIndex);
        List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return null;
        }
        NvsVideoTrack object = meicamVideoTrack.getObject();
        if (object == null) {
            LogUtils.e("NvsVideoTrack is null");
            return null;
        }
        NvsVideoClip videoClip = object.getClipByTimelinePosition(timeStamp);
        if (videoClip == null) {
            return null;
        }
        int index = videoClip.getIndex();
        if (index >= 0 && index < clipInfoList.size()) {
            return (MeicamVideoClip) clipInfoList.get(index);
        }
      /*  for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() <= timeStamp && clipInfo.getOutPoint() > timeStamp) {
                return (MeicamVideoClip) clipInfo;
            }
        }*/
        return null;
    }

    @Override
    public long getCurrentTimelinePosition() {
        return NvsStreamingContext.getInstance().getTimelineCurrentPosition(mTimeline);
    }




    private boolean handleCutClip(MeicamVideoClip clipInfo, int trackIndex, boolean isPip, long timeStamp) {
        NvsVideoClip editVideoClip = clipInfo.getObject();
        if (editVideoClip == null) {
            return true;
        }
        if (isPip) {
            List<ClipInfo<?>> videoClipsInTrackIndex = TimelineDataUtil.getVideoClipsInTrackIndex(trackIndex);
            int index = editVideoClip.getIndex();
            if (videoClipsInTrackIndex == null || index > videoClipsInTrackIndex.size() - 1) {
                return true;
            }

            long inPoint = editVideoClip.getInPoint();//当前Clip的入点 The entry point of the current Clip
            long outPoint = editVideoClip.getOutPoint();//当前Clip的出点 The outgoing point of the current Clip

            long trimOut = timeStamp - inPoint + clipInfo.getTrimIn();
            clipInfo.setTrimOut(trimOut);
            MeicamVideoClip newClip = (MeicamVideoClip) clipInfo.clone();
            newClip.setTrimIn(trimOut);
            long newTrimOut = (outPoint - timeStamp + trimOut);
            newClip.setTrimOut(newTrimOut);
            newClip.setInPoint(timeStamp);
            videoClipsInTrackIndex.add(index + 1, newClip);
        } else {
            List<ClipInfo<?>> clipInfos = TimelineDataUtil.getMainTrackVideoClip();
            int index = editVideoClip.getIndex();
            if (clipInfos == null || index > clipInfos.size() - 1) {
                return true;
            }
            long inPoint = editVideoClip.getInPoint();//当前Clip的入点 The entry point of the current Clip
            long outPoint = editVideoClip.getOutPoint();//当前Clip的出点 The outgoing point of the current Clip

            long trimOut = timeStamp - inPoint + clipInfo.getTrimIn();
            clipInfo.setTrimOut(trimOut);
            clipInfo.setOutPoint(timeStamp);
            MeicamVideoClip newClip = (MeicamVideoClip) clipInfo.clone();
            newClip.setTrimIn(trimOut);
            long newTrimOut = (outPoint - timeStamp + trimOut);
            newClip.setTrimOut(newTrimOut);
            newClip.setInPoint(timeStamp);
            clipInfos.add(index + 1, newClip);
        }
        return false;
    }


    /**
     * 获取某个视频片段对应的 AI caption
     * get AI Caption form video clip
     *
     * @param clipInfo
     */
    public List<ClipInfo<?>> getAICaptionFromVideoClip(ClipInfo clipInfo) {
        if (clipInfo == null) {
            return null;
        }
        List<ClipInfo<?>> captionList = new ArrayList<>();

        long inPoint = clipInfo.getInPoint();
        long outPoint = clipInfo.getOutPoint();
        List<ClipInfo<?>> allAICaption = TimelineDataUtil.getAllAICaption();
        if (CommonUtils.isEmpty(allAICaption)) {
            return null;
        }

        for (int i = 0; i < allAICaption.size(); i++) {
            ClipInfo<?> captionInfo = allAICaption.get(i);
            if (captionInfo == null) {
                continue;
            }
            if (TimelineDataUtil.isAICaption(captionInfo)) {
                long captionInPoint = captionInfo.getInPoint();
                long captionInfoOutPoint = captionInfo.getOutPoint();
                if (captionInPoint >= inPoint && captionInfoOutPoint <= outPoint) {
                    captionList.add(captionInfo);
                }
            }
        }
        return captionList;
    }

    /**
     * 移动字幕上层数据
     *
     * @param from
     * @param to
     */
    public void swapMeicamAICaption(int from, int to) {
        ClipInfo fromVideoClip = TimelineDataUtil.getVideoClip(MAIN_TRACK_INDEX, from);
        ClipInfo toVideoClip = TimelineDataUtil.getVideoClip(MAIN_TRACK_INDEX, to);
        List<ClipInfo<?>> fromAICaptionFromVideoClip = getAICaptionFromVideoClip(fromVideoClip);
        List<ClipInfo<?>> toAICaptionFromVideoClip = getAICaptionFromVideoClip(toVideoClip);


        long fromInPoint = fromVideoClip.getInPoint();
        long fromOutPoint = fromVideoClip.getOutPoint();
        long fromDuration = fromOutPoint - fromInPoint;

        long toInPoint = toVideoClip.getInPoint();
        long toOutPoint = toVideoClip.getOutPoint();
        long toDuration = toOutPoint - toInPoint;
        List<ClipInfo<?>> areaAllAICaption;

        if (from > to) {
            //右边的向左边交换 The one on the right switches to the left
            long middleDuration = fromVideoClip.getInPoint() - toVideoClip.getOutPoint();
            areaAllAICaption = TimelineDataUtil.getAreaAllAICaption(toVideoClip.getOutPoint(), fromVideoClip.getInPoint());
            //from clip 上的字幕需要移动的区间 The range from clip subtitles need to be moved
            long fromOffset = toInPoint - fromInPoint;
            //to clip 上的字幕需要移动的区间 to clip the range that the subtitles need to move
            long toOffset = fromDuration + middleDuration;
            //area clip 上的字幕需要移动的区间 The range that the subtitle on the area clip needs to move
            long areaOffset = fromDuration - toDuration;

            moveAndRemoveNvsCaption(fromAICaptionFromVideoClip, fromOffset);
            moveAndRemoveNvsCaption(toAICaptionFromVideoClip, toOffset);
            moveAndRemoveNvsCaption(areaAllAICaption, areaOffset);


        } else {
            //左边的向右边交换 The left side switches to the right side
            long middleDuration = toVideoClip.getInPoint() - fromVideoClip.getOutPoint();
            areaAllAICaption = TimelineDataUtil.getAreaAllAICaption(fromVideoClip.getOutPoint(), toVideoClip.getInPoint());
            //from clip 上的字幕需要移动的区间 The range from clip subtitles need to be moved
            long fromOffset = toDuration + middleDuration;
            //to clip 上的字幕需要移动的区间 to clip the range that the subtitles need to move
            long toOffset = fromInPoint - toInPoint;
            //area clip 上的字幕需要移动的区间 The range that the subtitle on the area clip needs to move
            long areaOffset = toDuration - fromDuration;
            moveAndRemoveNvsCaption(fromAICaptionFromVideoClip, fromOffset);
            moveAndRemoveNvsCaption(toAICaptionFromVideoClip, toOffset);
            moveAndRemoveNvsCaption(areaAllAICaption, areaOffset);
        }

        addNvsCaption(fromAICaptionFromVideoClip);
        addNvsCaption(toAICaptionFromVideoClip);
        addNvsCaption(areaAllAICaption);

    }

    /**
     * 移动AI字幕
     * Move AI subtitles
     *
     * @param fromAICaptionFromVideoClip
     * @param offset
     */
    public void moveAndRemoveNvsCaption(List<ClipInfo<?>> fromAICaptionFromVideoClip, long offset) {
        if (!CommonUtils.isEmpty(fromAICaptionFromVideoClip)) {
            for (int i = 0; i < fromAICaptionFromVideoClip.size(); i++) {
                ClipInfo<?> clipInfo = fromAICaptionFromVideoClip.get(i);
                if (TimelineDataUtil.isAICaption(clipInfo)) {
                    clipInfo.setInPoint(clipInfo.getInPoint() + offset);
                    clipInfo.setOutPoint(clipInfo.getOutPoint() + offset);
                    Object object = clipInfo.getObject();
                    if (object instanceof NvsTimelineCaption) {
                        mTimeline.removeCaption((NvsTimelineCaption) object);
                        clipInfo.setObject(null);
                    }
                }
            }
        }
    }

    public void addNvsCaption(List<ClipInfo<?>> fromAICaptionFromVideoClip) {
        if (!CommonUtils.isEmpty(fromAICaptionFromVideoClip)) {
            for (int i = 0; i < fromAICaptionFromVideoClip.size(); i++) {
                ClipInfo<?> clipInfo = fromAICaptionFromVideoClip.get(i);
                if (TimelineDataUtil.isAICaption(clipInfo)) {
                    float trackIndex = ((MeicamCaptionClip) clipInfo).getzValue();
                    NvsTimelineCaption caption = ((MeicamCaptionClip) clipInfo).bindToTimeline(mTimeline, false, true);
                    if (caption == null) {
                        LogUtils.e("addCaption: " + " 添加字幕失败！");
                        return;
                    }
                    caption.setZValue(trackIndex);
                    caption.setBold(false);
                    ((MeicamCaptionClip) clipInfo).setObject(caption);
                }
            }
        }
    }

    /**
     * 删除AI 字幕
     * delete AI caption
     *
     * @param meicamCaptionClip
     */
    public void removeAICaption(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        if (meicamCaptionClip.getOperationType() != BaseConstants.TYPE_AI_CAPTION) {
            return;
        }
        TimelineDataUtil.removeCaption(meicamCaptionClip.getInPoint());
        mTimeline.removeCaption(meicamCaptionClip.getObject());
    }

    /**
     * change AI Caption In Point
     *
     * @param clipInfo
     * @param inPoint
     */
    public void changeAICaptionInPoint(ClipInfo<?> clipInfo, long inPoint) {
        if (TimelineDataUtil.isAICaption(clipInfo)) {
            clipInfo.setInPoint(inPoint);
            Object object = clipInfo.getObject();
            if (object instanceof NvsTimelineCaption) {
                ((NvsTimelineCaption) object).changeInPoint(inPoint);
            }
        }
    }

    /**
     * change AI Caption out Point
     *
     * @param clipInfo
     * @param outPoint
     */
    public void changeAICaptionOutPoint(ClipInfo<?> clipInfo, long outPoint) {
        if (TimelineDataUtil.isAICaption(clipInfo)) {
            clipInfo.setOutPoint(outPoint);
            Object object = clipInfo.getObject();
            if (object instanceof NvsTimelineCaption) {
                ((NvsTimelineCaption) object).changeOutPoint(outPoint);
            }
        }
    }


    /**
     * 检测视频分辨率是否存在
     * Check if the video resolution is present
     */
    public void checkVideoResolution() {
        if (TimelineData.getInstance().getVideoResolution() == null) {
            if (mTimeline != null) {
                NvsVideoResolution videoRes = mTimeline.getVideoRes();
                if (videoRes == null) {
                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
                    if (videoTrack != null) {
                        NvsVideoClip videoClip = videoTrack.getClipByIndex(0);
                        if (videoClip != null) {
                            videoRes = TimelineUtil.getVideoEditResolutionByClip(videoClip.getFilePath());
                        }
                    }
                }
                TimelineData.getInstance().setVideoResolution(videoRes);
            }
        }
    }




    private void setAdjustEffect(NvsVideoClip videoClip, float adjustData, String adjustKey, String attachmentKey) {
        NvsVideoFx nvsVideoFx = null;
        int fxCount = videoClip.getFxCount();
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(i);
            if (videoFx != null) {
                Object attachment = videoFx.getAttachment(attachmentKey);
                if (attachment instanceof String) {
                    if (attachmentKey.equals(attachment)) {
                        nvsVideoFx = videoFx;
                        break;
                    }
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
    }

    private void setTimelineAdjustEffect(float adjustData, String adjustKey, String attachmentKey) {
        NvsTimelineVideoFx sharpenVideoFx = null;
        List<NvsTimelineVideoFx> timelineVideoFxByTimelinePosition = mTimeline.getTimelineVideoFxByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (timelineVideoFxByTimelinePosition != null) {
            for (int i = 0; i < timelineVideoFxByTimelinePosition.size(); i++) {
                NvsTimelineVideoFx nvsTimelineVideoFx = timelineVideoFxByTimelinePosition.get(i);
                Object attachment = nvsTimelineVideoFx.getAttachment(attachmentKey);
                if (attachment instanceof String && attachmentKey.equals(attachment)) {
                    sharpenVideoFx = nvsTimelineVideoFx;
                    break;
                }
            }
        }
        if (sharpenVideoFx != null) {
            sharpenVideoFx.setFloatVal(adjustKey, adjustData);
            sharpenVideoFx.setBooleanVal(NvsConstants.KEY_VIDEO_MODE, true);
        } else {
            sharpenVideoFx = mTimeline.addBuiltinTimelineVideoFx(0, mTimeline.getDuration(), attachmentKey);
            sharpenVideoFx.setAttachment(attachmentKey, attachmentKey);
            sharpenVideoFx.setFloatVal(adjustKey, adjustData);
            sharpenVideoFx.setBooleanVal(NvsConstants.KEY_VIDEO_MODE, true);
        }
    }


    /**
     * Add animator sticker.
     *
     * @param uuid       the uuid
     * @param coverPath  the cover path
     * @param viewWidth  the view width
     * @param viewHeight the view height
     */
    public void addAnimatorSticker(String uuid, String coverPath, int viewWidth, int viewHeight) {
        long inPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        /*
         * 对片头主题 进行特殊处理，贴纸不能加在片头主题片段上
         * Special treatment for the opening theme, stickers can not be added to the opening theme segment
         * */
        long titleThemeDuration = TimelineData.getInstance().getTitleThemeDuration();
        if (titleThemeDuration > 0) {
            if (inPoint < titleThemeDuration) {
                inPoint = titleThemeDuration;
            }
        }
        mStickerDuration = 4 * BaseConstants.NS_TIME_BASE;
        long outPoint = inPoint + mStickerDuration;
        MeicamStickerClip stickerClip = new MeicamStickerClip(uuid);
        stickerClip.setInPoint(inPoint);
        stickerClip.setOutPoint(outPoint);
        stickerClip.setIsCustomSticker(false);
        stickerClip.setCoverImagePath(coverPath);
        int trackIndex = getTrackIndex(stickerClip.getInPoint(), stickerClip.getOutPoint());
        LogUtils.d("addAnimatorSticker: " + trackIndex);
        stickerClip.setzValue(trackIndex);
        stickerClip.bindToTimeline(mTimeline);
        translateStickerRandom(stickerClip, viewWidth, viewHeight, trackIndex);
        seekTimeline(inPoint, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onAddStickerCaptionPicFx(stickerClip, TYPE_STICKER);
        }
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    private void translateStickerRandom(MeicamStickerClip stickerClip, int viewWidth, int viewHeight,
                                        int trackIndex) {
        if (stickerClip == null) {
            return;
        }
        NvsTimelineAnimatedSticker animatedSticker = stickerClip.getObject();
        if (animatedSticker == null) {
            return;
        }
        /*
         * 通过随机值平移贴纸位置
         * Shift the sticker position by random value
         */
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getStickerCaption();
        if (clipInfos != null && clipInfos.size() > 0) {
            /*
             * 获取贴纸的原始包围矩形框变换后的顶点位置
             * Gets the vertex position after the transformation of the original bounding rectangle of the sticker
             * */
            List<PointF> list = animatedSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            boolean isHorizonFlip = animatedSticker.getHorizontalFlip();
            if (isHorizonFlip) {
                /*
                 * 如果已水平翻转，需要对顶点数据进行处理
                 * If it has been flipped horizontally, the vertex data needs to be processed
                 * */
                Collections.swap(list, 0, 3);
                Collections.swap(list, 1, 2);
            }

            List<PointF> newList = CoordinateUtil.parseCanonicalToView(list, viewWidth, viewHeight);
            PointF leftTop = newList.get(0);
            float x = leftTop.x;
            float y = leftTop.y;
            Random rand = new Random();
            int randX = rand.nextInt(10);
            int randY = rand.nextInt(10);
            boolean isX = rand.nextBoolean();
            boolean isY = rand.nextBoolean();
            float transtionX = randX / 10.0f * x;
            float transtionY = randY / 10.0f * y;
            transtionX = isX ? transtionX : -transtionX;
            transtionY = isY ? transtionY : -transtionY;
            PointF timeLinePointF = new PointF(transtionX, transtionY);
            animatedSticker.translateAnimatedSticker(timeLinePointF);
            stickerClip.setTranslationX(transtionX);
            stickerClip.setTranslationY(transtionY);
        }
        LogUtils.file("Before addStickerCaption===" + TimelineData.getInstance().getMeicamStickerCaptionTrackList().size());
        TimelineDataUtil.addStickerCaption(trackIndex, stickerClip);
        LogUtils.file("After addStickerCaption===" + TimelineData.getInstance().getMeicamStickerCaptionTrackList().size());
    }


    /**
     * Add custom animator sticker.
     *
     * @param filePath   the file path
     * @param uuid       the uuid
     * @param coverPath  the cover path
     * @param viewWidth  the view width
     * @param viewHeight the view height
     */
    public void addCustomAnimatorSticker(String filePath, String uuid, String coverPath, int viewWidth, int viewHeight) {
        long inPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        /*
         * 对片头主题 进行特殊处理，贴纸不能加在片头主题片段上
         * Special treatment for the opening theme, stickers can not be added to the opening theme segment
         * */
        long titleThemeDuration = TimelineData.getInstance().getTitleThemeDuration();
        if (titleThemeDuration > 0) {
            if (inPoint < titleThemeDuration) {
                inPoint = titleThemeDuration;
            }
        }
        mStickerDuration = 4 * BaseConstants.NS_TIME_BASE;
        long outPoint = inPoint + mStickerDuration;
        MeicamStickerClip stickerClip = new MeicamStickerClip(uuid);
        stickerClip.setInPoint(inPoint);
        stickerClip.setOutPoint(outPoint);
        stickerClip.setIsCustomSticker(true);
        stickerClip.setCustomanimatedStickerImagePath(filePath);
        stickerClip.setCoverImagePath(coverPath);
        int trackIndex = getTrackIndex(stickerClip.getInPoint(), stickerClip.getOutPoint());
        LogUtils.d("addAnimatorSticker: " + trackIndex);
        stickerClip.setzValue(trackIndex);
        stickerClip.bindToTimeline(mTimeline);
        translateStickerRandom(stickerClip, viewWidth, viewHeight, trackIndex);
        seekTimeline(inPoint, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onAddStickerCaptionPicFx(stickerClip, TYPE_STICKER);
        }
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }


    /**
     * On hand action down.
     *
     * @param index      the index
     * @param isLeftHand the is left hand
     */
    public void onHandActionDown(int index, boolean isLeftHand) {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return;
        }

        NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
        if (videoClip == null) {
            return;
        }

        long newDuration = MAX_IMAGE_LENGTH;
        if (videoClip.getVideoType() == NvsVideoClip.VIDEO_CLIP_TYPE_AV) {
            NvsAVFileInfo fileInfo = mStreamingContext.getAVFileInfo(videoClip.getFilePath());
            newDuration = fileInfo.getDuration();
        }

        long trimPoint = newDuration;
        boolean isInPoint = false;
        if (videoClip.getVideoType() == NvsVideoClip.VIDEO_CLIP_TYPE_AV && isLeftHand) {
            isInPoint = true;
            trimPoint = 0;
        }
        changeTrimPointOnTrimAdjust(videoClip, trimPoint, isInPoint);
    }

    /**
     * On hand action up.
     *
     * @param index      the index
     * @param isLeftHand the is left hand
     */
    public void onHandActionUp(int index, boolean isLeftHand) {
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return;
        }
        NvsVideoClip videoClip = videoTrack.getClipByIndex(index);

        long trimPoint = videoClip.getTrimOut();
        boolean isInPoint = false;
        if (videoClip.getVideoType() == NvsVideoClip.VIDEO_CLIP_TYPE_AV && isLeftHand) {
            trimPoint = videoClip.getTrimIn();
            isInPoint = true;
        }
        changeTrimPointOnTrimAdjust(videoClip, trimPoint, isInPoint);
    }

    /**
     * On change trim timeline object.
     *
     * @param index      the index
     * @param isLeftHand the is left hand
     * @param trim       the trim
     */
    public void onChangeTrimTimelineObject(int index, boolean isLeftHand, long trim) {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return;
        }
        NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
        if (videoClip == null) {
            return;
        }
        if (isLeftHand && videoClip.getVideoType() == NvsVideoClip.VIDEO_CLIP_TYPE_AV) {
            setTrimInPoint(videoClip, trim, true);
        } else {
            setTrimOutPoint(videoClip, trim, true);
        }
        List<ClipInfo<?>> mainTrackVideoClip = TimelineDataUtil.getMainTrackVideoClip();
        for (int i = index + 1; i < mainTrackVideoClip.size(); i++) {
            MeicamVideoClip clip = (MeicamVideoClip) mainTrackVideoClip.get(i);
            if ((clip == null) || (clip.getObject() == null)) {
                LogUtils.e("onChangeTrimTimelineObject: clip or clip.getObject is null!");
                continue;
            }
            NvsVideoClip nvsVideoClip = clip.getObject();
            if (nvsVideoClip == null) {
                continue;
            }
            clip.setInPoint(nvsVideoClip.getInPoint());
            clip.setOutPoint(nvsVideoClip.getOutPoint());
        }
    }

    /**
     * On change trim timeline data.
     *
     * @param index      the index
     * @param isLeftHand the is left hand
     * @param trim       the trim
     */
    public void onChangeTrimTimelineData(int index, boolean isLeftHand, long trim) {
        if (mTimeline == null) {
            return;
        }
        List<ClipInfo<?>> mainTrackVideoClip = TimelineDataUtil.getMainTrackVideoClip();
        MeicamVideoClip clipInfo = null;
        if (CommonUtils.isIndexAvailable(index, mainTrackVideoClip)) {
            clipInfo = (MeicamVideoClip) mainTrackVideoClip.get(index);
        }
        if (clipInfo == null) {
            LogUtils.e("clipInfo is null");
            return;
        }
        long clipOldTrimIn = clipInfo.getTrimIn();
        long clipOldTrimOut = clipInfo.getTrimOut();
        if (isLeftHand && clipInfo.getVideoType().equals(CommonData.CLIP_VIDEO)) {
            clipInfo.setTrimIn(trim);
        } else {
            clipInfo.setTrimOut(trim);
        }
        clipInfo.setOutPoint((long) (clipInfo.getInPoint() + (clipInfo.getTrimOut() - clipInfo.getTrimIn()) / clipInfo.getSpeed()));
        if (mainTrackVideoClip.size() > index) {
//            long offset = clipOldDuration - clipInfo.getTrimOut() - clipInfo.getTrimIn();
            long offset;
            if (isLeftHand) {
                offset = (long) ((clipOldTrimIn - clipInfo.getTrimIn()) / clipInfo.getSpeed());
            } else {
                offset = (long) ((clipOldTrimOut - clipInfo.getTrimOut()) / clipInfo.getSpeed());
            }
            for (int i = index + 1; i < mainTrackVideoClip.size(); i++) {
                ClipInfo clip = mainTrackVideoClip.get(i);
                if (clip == null) {
                    continue;
                }
                clip.setInPoint(clip.getInPoint() - offset);
                clip.setOutPoint(clip.getOutPoint() - offset);
            }
        }
    }


    /**
     * 获取文件时长
     * <p>
     * Get file duration.
     *
     * @param index 文件clip在视频轨道中的索引   the index of clip in video track
     * @return 文件时长 the file duration
     */
    public long getAvFileDuration(int index) {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return 0;
        }
        NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
        if (videoClip == null) {
            LogUtils.e("getAvFileDuration: getClipCount: " + videoTrack.getClipCount() + "  index: " + index);
            return 0;
        }
        long newDuration = MAX_IMAGE_LENGTH;
        if (videoClip.getVideoType() == NvsVideoClip.VIDEO_CLIP_TYPE_AV) {
            NvsAVFileInfo fileInfo = mStreamingContext.getAVFileInfo(videoClip.getFilePath());
            newDuration = fileInfo.getDuration();
        }


        return newDuration;
    }

    /**
     * 设置组合字幕的字体
     * Sets compound caption font.
     *
     * @param caption      the caption
     * @param captionIndex the caption index
     * @param fftPath      "font/xxx.ttf"
     * @return the compound caption font
     */
    public String setCompoundCaptionFont(NvsTimelineCompoundCaption caption, int captionIndex, String fftPath) {
        if (!TextUtils.isEmpty(fftPath)) {
            String fontAssetPath = "assets:/" + fftPath;
            String fontName = mStreamingContext.registerFontByFilePath(fontAssetPath);
            caption.setFontFamily(captionIndex, fontName);
            seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            return fontName;
        } else {
            caption.setFontFamily(captionIndex, fftPath);
            seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            return fftPath;
        }
    }


    /**
     * Seek timeline.
     */
    public void seekTimeline() {
        seekTimeline(0);
    }

    /**
     * Seek timeline.
     *
     * @param seekShowMode the seek show mode
     */
    public void seekTimeline(int seekShowMode) {
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), seekShowMode);
    }


    /**
     * Seek timeline.
     *
     * @param timestamp    the timestamp
     * @param seekShowMode the seek show mode
     */
    public void seekTimeline(long timestamp, int seekShowMode) {
        int flag = 0;
        if (useFaceShape) {
            flag = NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME;
        }
        if (seekShowMode > 0) {
            mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, flag | seekShowMode
                    | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER
                    | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
        } else {
            mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, flag
                    | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER
                    | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
        }
    }

    public void playVideo(long startTime, long endTime) {
        if (mTimeline != null) {
            int flag = 0;
            if (useFaceShape) {
                flag = NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME;
            }
            if (BaseConstants.NEED_100_SPEED) {
                flag |= NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_SPEED_COMP_MODE;
            }
            mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, flag);
        }
    }


    /**
     * Close original voice.
     */
    public void closeOriginalVoice() {
        if (mTimeline == null) {
            return;
        }
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (!CommonUtils.isEmpty(meicamVideoTrackList)) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(BaseConstants.TRACK_INDEX_MAIN);
            meicamVideoTrack.setIsMute(true);
        }
    }

    /**
     * Open original voice.
     */
    public void openOriginalVoice() {
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return;
        }
        videoTrack.setVolumeGain(1, 1);
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (!CommonUtils.isEmpty(meicamVideoTrackList)) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(BaseConstants.TRACK_INDEX_MAIN);
            //不静音 Do not quiet
            meicamVideoTrack.setIsMute(false);
        }
    }


    /**
     * Apply theme.
     *
     * @param packageId the package id
     */
    public void applyTheme(String packageId) {
        //主题 theme
        MeicamTheme meicamTheme = new MeicamTheme(packageId);
        TimelineData.getInstance().setMeicamTheme(meicamTheme);
        TimelineUtil.applyTheme(mTimeline);
        playVideo(0, mTimeline.getDuration());
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, false);
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Apply transform.
     *
     * @param baseInfo              the base info
     * @param targetTransitionIndex the target transition index
     */
    public void applyTransform(IBaseInfo baseInfo, int targetTransitionIndex, long duration) {
        String desc;
        MeicamTransition meicamTransitionInfo;
        if (baseInfo.getEffectMode() == BaseInfo.EFFECT_MODE_BUILTIN) {
            desc = baseInfo.getEffectId();
            meicamTransitionInfo = new MeicamTransition(targetTransitionIndex , TYPE_BUILD_IN, desc, baseInfo.getCoverId());
        } else {
            desc = baseInfo.getPackageId();
            meicamTransitionInfo = new MeicamTransition(targetTransitionIndex , TYPE_PACKAGE, desc, baseInfo.getCoverPath());
        }
        meicamTransitionInfo.setDuration(duration);
        NvsVideoTransition videoTransition = meicamTransitionInfo.bindToTimeline(TimelineDataUtil.getMainTrack().getObject());
        if (videoTransition != null) {
            meicamTransitionInfo.loadData(videoTransition);
            TimelineDataUtil.addMainTransition(meicamTransitionInfo);
            playTransition(targetTransitionIndex );
        } else {
            TimelineDataUtil.addMainTransition(meicamTransitionInfo);
        }
    }


    /**
     * Apply filter meicam timeline video fx.
     *
     * @param baseInfo         the base info
     * @param clipInfo         the clip info
     * @param isTimelineFilter the is timeline filter
     * @return the meicam timeline video fx
     */
    public MeicamTimelineVideoFx applyFilter(IBaseInfo baseInfo, MeicamVideoClip clipInfo, boolean isTimelineFilter) {
        MeicamTimelineVideoFx videoClipFxInfo = new MeicamTimelineVideoFx();
        videoClipFxInfo.setOutPoint(mTimeline.getDuration());
        if (baseInfo.getEffectMode() == BaseInfo.EFFECT_MODE_BUILTIN) {
            videoClipFxInfo.setType(TYPE_BUILD_IN);
            videoClipFxInfo.setDesc(baseInfo.getEffectId());
        } else {
            videoClipFxInfo.setType(TYPE_PACKAGE);
            videoClipFxInfo.setDesc(baseInfo.getPackageId());
        }
        videoClipFxInfo.setIntensity(1.0f);
        if (!isTimelineFilter) {
            videoClipFxInfo.setSubType(MeicamVideoFx.SUB_TYPE_TIMELINE_FILTER);
            TimelineData.getInstance().setFilterFx(videoClipFxInfo);
            TimelineUtil.buildTimelineFilter(mTimeline, videoClipFxInfo);
        } else {
            videoClipFxInfo.setSubType(MeicamVideoFx.SUB_TYPE_CLIP_FILTER);
            TimelineUtil.appendFilterFx(clipInfo, videoClipFxInfo);
        }
        seekTimeline(0);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
        }
        return videoClipFxInfo;
    }


    /**
     * Apply effect.
     *
     * @param baseInfo the base info
     */
    public void applyEffect(IBaseInfo baseInfo, MeicamTimelineVideoFxClip meicamTimelineVideoFxClip) {
        String packagePath = baseInfo.getAssetPath();
        if (TextUtils.isEmpty(packagePath)) {
            return;
        }
        MeicamTimelineVideoFxClip effect = new MeicamTimelineVideoFxClip();
        if (baseInfo.getEffectMode() == BaseInfo.EFFECT_MODE_BUILTIN) {
            effect.setDesc(baseInfo.getEffectId());
            effect.setClipType(EFFECT_BUILTIN);
        } else {
            effect.setDesc(baseInfo.getPackageId());
            effect.setClipType(EFFECT_PACKAGE);
        }
        effect.setDisplayName(baseInfo.getName());
        long inPoint = -1;
        long currentTimelinePosition = getCurrentTimelinePosition();
        if (meicamTimelineVideoFxClip != null) {
            inPoint = meicamTimelineVideoFxClip.getInPoint();
            removePreviewEffect(inPoint);
        } else {
            inPoint = removePreviewEffect(currentTimelinePosition);
        }
        if (inPoint < 0) {
            inPoint = currentTimelinePosition;
        }

        /*
         * 对片头主题 进行特殊处理，特效不能加在片头主题片段上
         * Special treatment to the opening theme, special effects can not be added to the opening theme segment
         * */
        long titleThemeDuration = TimelineData.getInstance().getTitleThemeDuration();
        if (titleThemeDuration > 0) {
            if (inPoint < titleThemeDuration) {
                inPoint = titleThemeDuration;
            }
        }

        effect.setInPoint(inPoint);
        long effectDuration = getEffectDuration(inPoint);
        effect.setOutPoint(inPoint + effectDuration);
        effect.setIntensity(1.0f);
        NvsTimelineVideoFx timelineVideoFx = effect.bindToTimeline(mTimeline);
        if (timelineVideoFx != null) {
            List<MeicamTimelineVideoFxTrack> videoFxTrackList = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
            if (CommonUtils.isEmpty(videoFxTrackList)) {
                MeicamTimelineVideoFxTrack videoFxTrack = new MeicamTimelineVideoFxTrack(0);
                videoFxTrackList.add(videoFxTrack);
            }
            MeicamTimelineVideoFxTrack videoFxTrack = videoFxTrackList.get(0);
            videoFxTrack.getClipInfoList().add(effect);
            effect.setIndex(videoFxTrackList.size() - 1);
            if (mOnTimelineChangeListener != null) {
                mOnTimelineChangeListener.onAddStickerCaptionPicFx(effect, TYPE_EFFECT);
                mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
            }
        }
    }

    public void playTransition(int index) {
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        int videoCount = videoTrack.getClipCount();
        if (videoCount < index + 1) {
            return;
        }

        NvsVideoClip clip = videoTrack.getClipByIndex(index);
        if (clip == null) {
            return;
        }

        long inPointBefore = clip.getInPoint();
        long outPointBefore = clip.getOutPoint();

        NvsVideoClip nextClip = videoTrack.getClipByIndex(index + 1);
        if (nextClip == null) {
            return;
        }

        long inPointNext = nextClip.getInPoint();
        long outPointNext = nextClip.getOutPoint();
        outPointBefore -= BaseConstants.TIME_BASE;
        inPointNext += BaseConstants.TIME_BASE;
        if (outPointBefore < inPointBefore) {
            outPointBefore = inPointBefore;
        }
        if (inPointNext > outPointNext) {
            inPointNext = outPointNext;
        }
        playVideo(outPointBefore, inPointNext);
    }



    private void addCaption(String text) {
        long inPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);

        /*
         * 对片头主题 进行特殊处理，字幕不能加在片头主题片段上
         * Special treatment to the opening theme, subtitle can not be added to the opening theme segment
         * */
        long titleThemeDuration = TimelineData.getInstance().getTitleThemeDuration();
        if (titleThemeDuration > 0) {
            if (inPoint < titleThemeDuration) {
                inPoint = titleThemeDuration;
            }
        }
        long captionDuration = 4 * BaseConstants.NS_TIME_BASE;
        long outPoint = inPoint + captionDuration;
        addCaption(text, inPoint, outPoint, true, false);
    }

    public void addCaption(String text, long inPoint, long outPoint, boolean showNow, boolean isIdentify) {
        addCaption(text, inPoint, outPoint, showNow, isIdentify, 0);
    }

    public void addCaption(String text, long inPoint, long outPoint, boolean showNow, boolean isIdentify, int operateType) {
        MeicamCaptionClip meicamCaptionClip = new MeicamCaptionClip(text, null);
        meicamCaptionClip.setOperationType(operateType);
        meicamCaptionClip.setInPoint(inPoint);
        meicamCaptionClip.setOutPoint(outPoint);
        int trackIndex = getTrackIndex(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint());
        meicamCaptionClip.setzValue(trackIndex);
        NvsTimelineCaption caption = meicamCaptionClip.bindToTimeline(mTimeline, false, isIdentify);
        if (caption == null) {
            LogUtils.e("addCaption: " + " add caption failed！");
            return;
        }
        caption.setZValue(trackIndex);
        caption.setBold(false);
        meicamCaptionClip.setObject(caption);

        TimelineDataUtil.addStickerCaption(trackIndex, meicamCaptionClip);
        if (showNow) {
            if (mOnTimelineChangeListener != null) {
                mOnTimelineChangeListener.onAddStickerCaptionPicFx(meicamCaptionClip, TYPE_CAPTION);
            }
            seekTimeline(inPoint, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        }
    }

    private void updateCaption(NvsTimelineCaption caption, String content) {
        if (caption == null) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            boolean success = TimelineDataUtil.removeStickerCaption((int) caption.getZValue(), caption.getInPoint());
            if (success) {
                mTimeline.removeCaption(caption);
                if (mOnTimelineChangeListener != null) {
                    mOnTimelineChangeListener.onAddStickerCaptionPicFx(null, TYPE_CAPTION);
                }
            }
        } else {
            if (TimelineData.getInstance().isAddTitleTheme() && caption.getRoleInTheme() == NvsTimelineCaption.ROLE_IN_THEME_TITLE) {
                TimelineData.getInstance().getMeicamTheme().setThemeTitleText(content);
            }
            caption.setText(content);
            /*
             * 重新赋值
             *  deassign
             * */
            ClipInfo stickerCaptionData = TimelineDataUtil.getStickerCaptionData((int) caption.getZValue(), caption.getInPoint());
            MeicamCaptionClip clipInfo = null;
            if (stickerCaptionData instanceof MeicamCaptionClip) {
                clipInfo = (MeicamCaptionClip) stickerCaptionData;
            }
            if (clipInfo != null) {
                clipInfo.loadData(caption);
            }
            if (mOnTimelineChangeListener != null) {
                mOnTimelineChangeListener.onAddStickerCaptionPicFx(stickerCaptionData, TYPE_CAPTION);
            }
        }
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }




    private int getTrackIndex(long inPoint, long outPoint) {
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        for (MeicamStickerCaptionTrack meicamStickerCaptionTrack : meicamStickerCaptionTrackList) {
            int nowTrackIndex = meicamStickerCaptionTrack.getIndex();
            List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
            if (clipInfoList.isEmpty()) {
                return nowTrackIndex;
            }
            for (int i = 0; i < clipInfoList.size(); i++) {
                ClipInfo nowClipInfo = clipInfoList.get(i);
                long nowInpoint = nowClipInfo.getInPoint();
                long nowOutpoint = nowClipInfo.getOutPoint();
                long beforeOutpoint = 0;
                if (i > 0) {
                    ClipInfo beforeClipInfo = clipInfoList.get(i - 1);
                    beforeOutpoint = beforeClipInfo.getOutPoint();
                }
                if (inPoint >= beforeOutpoint && outPoint <= nowInpoint) {
                    return nowTrackIndex;
                }
                long afterInPoint;
                if (i < clipInfoList.size() - 1) {
                    ClipInfo afterClipInfo = clipInfoList.get(i + 1);
                    afterInPoint = afterClipInfo.getInPoint();
                    if (inPoint >= nowOutpoint && outPoint <= afterInPoint) {
                        return nowTrackIndex;
                    }
                } else {
                    if (inPoint >= nowOutpoint) {
                        return nowTrackIndex;
                    }
                }
            }
        }
        return meicamStickerCaptionTrackList.size();
    }

    /**
     * 复制获取Index
     * Copy to get Index
     *
     * @return 轨道的索引 Index of track
     */
    private int getTrackIndexByCopy(long inPoint, long outPoint, int index) {
        long during = outPoint - inPoint;
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        for (int i = index + 1; i < meicamStickerCaptionTrackList.size(); i++) {
            if (!CommonUtils.isIndexAvailable(i, meicamStickerCaptionTrackList)) {
                return meicamStickerCaptionTrackList.size();
            }
            MeicamStickerCaptionTrack track = meicamStickerCaptionTrackList.get(i);
            List<ClipInfo<?>> clipInfoList = track.getClipInfoList();
            if (clipInfoList.size() == 0) {
                return i;
            }
            for (int j = 0; j < clipInfoList.size(); j++) {
                ClipInfo clipInfo = clipInfoList.get(j);
                if (clipInfo.getInPoint() > inPoint) {
                    /*
                     * 找到了后面的一个片段了
                     * We found the last clip
                     * */
                    if (j > 0) {
                        /*
                         * 前面一个片段
                         * The previous segment
                         * */
                        ClipInfo clipInfoBefore = clipInfoList.get(j - 1);
                        if (clipInfo.getInPoint() - clipInfoBefore.getOutPoint() > during) {
                            return i;
                        }
                    } else {
                        if (clipInfo.getInPoint() > during) {
                            return i;
                        }
                    }
                } else {
                    ClipInfo last = clipInfoList.get(clipInfoList.size() - 1);
                    if (inPoint > last.getOutPoint()) {
                        return i;
                    }
                }
            }
        }
        return meicamStickerCaptionTrackList.size();
    }

    /**
     * Gets pip video clip index.
     *
     * @param inPoint  the in point
     * @param outPoint the out point
     * @return the pip video clip index
     */
    public int getPipVideoClipIndex(long inPoint, long outPoint) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        for (MeicamVideoTrack meicamVideoTrack : meicamVideoTrackList) {
            int nowTrackIndex = meicamVideoTrack.getIndex();
            if (nowTrackIndex == 0) {
                continue;
            }
//            NvsVideoTrack nvsVideoTrack = meicamVideoTrack.getObject();
//            NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByTimelinePosition(inPoint);
//            if (nvsVideoClip != null) {
//                continue;
//            }
//            NvsVideoClip lastClip = nvsVideoTrack.getClipByIndex(nvsVideoTrack.getClipCount() - 1);
//            if (lastClip == null) {
//                return nowTrackIndex;
//            }
//            if (lastClip.getOutPoint() < inPoint) {
//                return nowTrackIndex;
//            }
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            if (clipInfoList.isEmpty()) {
                return nowTrackIndex;
            }
            for (int j = 0; j < clipInfoList.size(); j++) {
                ClipInfo nowClipInfo = clipInfoList.get(j);
                long nowInpoint = nowClipInfo.getInPoint();
                long nowOutpoint = nowClipInfo.getOutPoint();
                long beforeOutpoint = 0;
                if (j > 0) {
                    ClipInfo beforeClipInfo = clipInfoList.get(j - 1);
                    beforeOutpoint = beforeClipInfo.getOutPoint();
                }
                if (inPoint >= beforeOutpoint && outPoint <= nowInpoint) {
                    return nowTrackIndex;
                }
                long afterInPoint;
                if (j < clipInfoList.size() - 1) {
                    ClipInfo afterClipInfo = clipInfoList.get(j + 1);
                    afterInPoint = afterClipInfo.getInPoint();
                    if (inPoint >= nowOutpoint && outPoint <= afterInPoint) {
                        return nowTrackIndex;
                    }
                } else {
                    if (inPoint >= nowOutpoint) {
                        return nowTrackIndex;
                    }
                }
            }
        }

        return meicamVideoTrackList.size();
    }

    /**
     * 移除某一时间段的字幕
     * Remove caption from a certain period of time
     *
     * @param start the start time
     * @param end   the end time
     **/
    public void removeCaption(long start, long end) {
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        NvsTimelineCaption nextCaption;
        while (caption != null) {
            if ((start >= caption.getInPoint() && start <= caption.getOutPoint())
                    || (caption.getInPoint() >= start && caption.getInPoint() < end)) {
                nextCaption = mTimeline.getNextCaption(caption);
                TimelineDataUtil.removeCaption(caption.getInPoint());
                mTimeline.removeCaption(caption);
                caption = nextCaption;
            } else {
                caption = mTimeline.getNextCaption(caption);
            }

        }
    }

    /**
     * 删除所有的AI 字幕
     * delete all AI caption
     */
    public void removeAllAICaption() {
        List<ClipInfo<?>> allAICaption = TimelineDataUtil.getAllAICaption();
        if (CommonUtils.isEmpty(allAICaption)) {
            return;
        }
        for (int i = 0; i < allAICaption.size(); i++) {
            ClipInfo<?> clipInfo = allAICaption.get(i);
            if (clipInfo == null) {
                continue;
            }
            if (TimelineDataUtil.isAICaption(clipInfo)) {
                TimelineDataUtil.removeCaption(clipInfo.getInPoint());
                mTimeline.removeCaption((NvsTimelineCaption) clipInfo.getObject());
            }

        }
    }

    /**
     * Remove caption.
     *
     * @param captionInfo the caption info
     */
    public boolean removeCaption(MeicamCaptionClip captionInfo) {
        if (captionInfo == null) {
            return false;
        }
        LogUtils.d("removeCaption: " + captionInfo.getzValue() + " " + captionInfo.getInPoint());
        NvsTimelineCaption curCaption = captionInfo.getObject();
        if (curCaption == null) {
            return false;
        }
        boolean success = TimelineDataUtil.removeStickerCaption((int) curCaption.getZValue(), curCaption.getInPoint());
        if (!success) {
            LogUtils.e("removeStickerCaption fail");
            return false;
        }
        mTimeline.removeCaption(curCaption);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onAddStickerCaptionPicFx(null, TYPE_CAPTION);
        }
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
            // mOnTimelineChangeListener.onChangeBottomView();
        }
        return true;
    }

    /**
     * 判断贴纸是否有声音
     * Determine if the sticker makes a sound
     */
    public boolean currStickerHasVoice() {
        return hasAudio;
    }

    /**
     * 设置贴纸是否有声音
     * <p>
     * Set sticker has audio or not.
     *
     * @param hasAudio 是否有声音，true: 是; false: 否. Whether has audio or not. true: yes; false: no.
     */
    public void setStickerHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    /**
     * 设置字幕背景角度
     * Set the subtitle background Angle
     *
     * @param meicamCaptionClip Clip data
     * @param radius            背景角度 Radius of background
     */
    public void handleCaptionColorConner(MeicamCaptionClip meicamCaptionClip, int radius) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setBackgroundAngle(radius);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Handle caption background color.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param progress          the progress
     */
    public void handleCaptionBackgroundColor(MeicamCaptionClip meicamCaptionClip, int progress) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        NvsColor textColor = curCaption.getBackgroundColor();
        if (textColor == null) {
            return;
        }
        textColor.a = progress / 100.0f;
        curCaption.setBackgroundColor(textColor);
        meicamCaptionClip.setBackgroundColor(ColorUtil.getColorArray(textColor));
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Handle caption outline width.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param progress          the progress
     */
    public void handleCaptionOutlineWidth(MeicamCaptionClip meicamCaptionClip, int progress) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        curCaption.setDrawOutline(true);
        curCaption.setOutlineWidth(progress / 10F);
        meicamCaptionClip.setOutlineWidth(progress / 10F);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * 设置字幕描边颜色
     * Set caption stroke color.
     *
     * @param meicamCaptionClip clip data
     * @param progress          描边的百分比 Percentage of color alpha.
     */
    public void handleCaptionOutLineColor(MeicamCaptionClip meicamCaptionClip, int progress) {
        if (meicamCaptionClip == null) {
            return;
        }
        float[] outlineColor = meicamCaptionClip.getOutlineColor();
        if (outlineColor == null || outlineColor.length != 4) {
            return;
        }
        outlineColor[3] = progress / 100.0f;
        meicamCaptionClip.setOutlineColor(outlineColor);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Handle caption text color.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param progress          the progress
     */
    public void handleCaptionTextColor(MeicamCaptionClip meicamCaptionClip, int progress) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        NvsColor textColor = curCaption.getTextColor();
        if (textColor == null) {
            return;
        }
        textColor.a = progress / 100.0f;
        curCaption.setTextColor(textColor);
        meicamCaptionClip.setTextColor(ColorUtil.getColorArray(textColor));
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Handle caption font.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param content           the content
     */
    public void handleCaptionFont(MeicamCaptionClip meicamCaptionClip, String content) {
        if (meicamCaptionClip == null) {
            return;
        }

        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption != null) {
            if (!TextUtils.isEmpty(content)) {
                String fontAssetPath = "assets:/" + content;
                String fontName = mStreamingContext.registerFontByFilePath(fontAssetPath);
                curCaption.setFontFamily(fontName);
                meicamCaptionClip.setFont(fontName);
            } else {
                curCaption.setFontFamily(content);
                meicamCaptionClip.setFont(content);
            }
            seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            if (mOnTimelineChangeListener != null) {
                mOnTimelineChangeListener.onSaveOperation();
            }
        }
    }

    /**
     * 处理字幕阴影
     * Handle caption shadow.
     *
     * @param meicamCaptionClip Clip data
     * @param hasShadow         是否有阴影 Whether the caption has shadow.
     */
    public void handleCaptionShadow(MeicamCaptionClip meicamCaptionClip, boolean hasShadow) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setShadow(hasShadow);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * 处理字幕斜体
     * Handle caption italics
     *
     * @param meicamCaptionClip Clip data
     * @param isItalic          是否有斜体 Whether the caption has italics.
     */
    public void handleCaptionItalics(MeicamCaptionClip meicamCaptionClip, boolean isItalic) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setItalic(isItalic);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * 处理字幕加粗
     * Handle caption bold
     *
     * @param meicamCaptionClip Clip data.
     * @param isBold            是否加粗 Whether the caption is bold.
     */
    public void handleCaptionBold(MeicamCaptionClip meicamCaptionClip, boolean isBold) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setBold(isBold);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }


    /**
     * Sets caption letter space.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param space             the space
     */
    public void setCaptionLetterSpace(MeicamCaptionClip meicamCaptionClip, float space) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        meicamCaptionClip.setLetterSpacing(space);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Sets caption line space.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param space             the space
     */
    public void setCaptionLineSpace(MeicamCaptionClip meicamCaptionClip, float space) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        meicamCaptionClip.setLineSpacing(space);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Handle caption position.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param intValue          the int value
     */
    public void handleCaptionPosition(MeicamCaptionClip meicamCaptionClip, int intValue) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }

        List<PointF> list;
        float xOffset;
        float yOffset;
        float y_dis;
        if (intValue == CAPTION_ALIGN_LEFT) {
            list = curCaption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            Collections.sort(list, new PointXComparator());
            xOffset = -(mTimeline.getVideoRes().imageWidth / 2f + list.get(0).x);
            meicamCaptionClip.setTranslationX(meicamCaptionClip.getTranslationX() + xOffset);
        } else if (intValue == CAPTION_ALIGN_HORIZ_CENTER) {
            list = curCaption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            Collections.sort(list, new PointXComparator());
            xOffset = -((list.get(3).x - list.get(0).x) / 2 + list.get(0).x);
            meicamCaptionClip.setTranslationX(meicamCaptionClip.getTranslationX() + xOffset);
        } else if (intValue == CAPTION_ALIGN_RIGHT) {
            list = curCaption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            Collections.sort(list, new PointXComparator());
            xOffset = mTimeline.getVideoRes().imageWidth / 2f - list.get(3).x;
            meicamCaptionClip.setTranslationX(meicamCaptionClip.getTranslationX() + xOffset);
        } else if (intValue == CAPTION_ALIGN_TOP) {
            list = curCaption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            Collections.sort(list, new PointYComparator());
            y_dis = list.get(3).y - list.get(0).y;
            yOffset = mTimeline.getVideoRes().imageHeight / 2f - list.get(0).y - y_dis;
            meicamCaptionClip.setTranslationY(meicamCaptionClip.getTranslationY() + yOffset);
        } else if (intValue == CAPTION_ALIGN_VERT_CENTER) {
            list = curCaption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            Collections.sort(list, new PointYComparator());
            yOffset = -((list.get(3).y - list.get(0).y) / 2 + list.get(0).y);
            meicamCaptionClip.setTranslationY(meicamCaptionClip.getTranslationY() + yOffset);
        } else if (intValue == CAPTION_ALIGN_BOTTOM) {
            list = curCaption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return;
            }
            Collections.sort(list, new PointYComparator());
            y_dis = list.get(3).y - list.get(0).y;
            yOffset = -(mTimeline.getVideoRes().imageHeight / 2f + list.get(3).y - y_dis);
            meicamCaptionClip.setTranslationY(meicamCaptionClip.getTranslationY() + yOffset);
        }
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * 设置起泡字幕
     * Set caption bubble.
     *
     * @param meicamCaptionClip Clip data.
     * @param packagePath       特效包路径 The path of bubble effect package.
     * @param uuid              特效包的uuid The uuid of bubble effect.
     */
    public void setCaptionBubble(MeicamCaptionClip meicamCaptionClip, String packagePath, String uuid) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setBubbleUuid(uuid);
        playVideo(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint());
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }


    /**
     * 设置字幕花字
     * Set caption flower.
     *
     * @param meicamCaptionClip Clip data.
     * @param packagePath       特效包路径 the path of flower effect package.
     * @param uuid              特效包的uuid the uuid of flower effect.
     */
    public void setCaptionFlower(MeicamCaptionClip meicamCaptionClip, String packagePath, String uuid) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setRichWordUuid(uuid);
        playVideo(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint());
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * 组合字幕动画
     * Handle animation of combined caption.
     *
     * @param meicamCaptionClip clip
     * @param packagePath       包路径 path of package.
     * @param uuid              动画的uuid uuid of animation.
     */
    public void handleCaptionAnimationCombination(MeicamCaptionClip meicamCaptionClip, String packagePath, String uuid) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        if (!TextUtils.isEmpty(curCaption.getModularCaptionOutAnimationPackageId())) {
            curCaption.applyModularCaptionOutAnimation("");
            /*
             * 恢复默认值
             * Restore Defaults
             * */
            curCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
        }
        if (!TextUtils.isEmpty(curCaption.getModularCaptionInAnimationPackageId())) {
            curCaption.applyModularCaptionInAnimation("");
            curCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
        }
        if (curCaption.getModularCaptionAnimationPeroid() == 0) {
            /*
             * 如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
             * If the animation duration is set to 0 when switching, then the default, product requirement, is restored
             * */
            curCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
        }
        boolean isSuccess = curCaption.applyModularCaptionAnimation(uuid);
        LogUtils.d(isSuccess ? "handle caption animation combination success" : "handle caption animation combination failed.");

        meicamCaptionClip.setCombinationAnimationUuid(uuid);
        meicamCaptionClip.setCombinationAnimationDuration(curCaption.getModularCaptionAnimationPeroid());
        meicamCaptionClip.setMarchOutAnimationUuid("");
        meicamCaptionClip.setMarchInAnimationUuid("");
        playVideo(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint());
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }


    /**
     * Handle caption animation out.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param packagePath       the package path
     * @param uuid              the uuid
     */
    public void handleCaptionAnimationOut(MeicamCaptionClip meicamCaptionClip, String packagePath, String uuid) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        if (!TextUtils.isEmpty(curCaption.getModularCaptionAnimationPackageId())) {
            curCaption.applyModularCaptionAnimation("");
            curCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
        }
        if (curCaption.getModularCaptionOutAnimationDuration() == 0) {
            /*
             * 如果切换的时候，设置的动画时长是0，则恢复默认，产品需求
             * If the animation duration is set to 0 when switching, then the default, product requirement, is restored
             * */
            curCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
        }
        boolean isSuccess = curCaption.applyModularCaptionOutAnimation(uuid);
        LogUtils.d(isSuccess ? "caption set success" : "caption set failed");

        meicamCaptionClip.setCombinationAnimationUuid("");
        meicamCaptionClip.setCombinationAnimationDuration(0);
        meicamCaptionClip.setMarchOutAnimationUuid(uuid);
        meicamCaptionClip.setMarchOutAnimationDuration(curCaption.getModularCaptionOutAnimationDuration());
        if (TextUtils.isEmpty(uuid)) {
            meicamCaptionClip.setMarchOutAnimationDuration(0);
        }

        playVideo(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint());
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Handle caption animation in.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param packagePath       the package path
     * @param uuid              the uuid
     */
    public void handleCaptionAnimationIn(MeicamCaptionClip meicamCaptionClip, String packagePath, String uuid) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        if (!TextUtils.isEmpty(curCaption.getModularCaptionAnimationPackageId())) {
            curCaption.applyModularCaptionAnimation("");
            curCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
        }
        if (curCaption.getModularCaptionInAnimationDuration() == 0) {
            /*
             * 如果切换的时候，设置的动画时长是0，则恢复默认,产品需求。
             * If the animation duration is set to 0 when switching, then the default, product requirement, is restored
             * */
            curCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
        }

        boolean isSuccess = curCaption.applyModularCaptionInAnimation(uuid);
        LogUtils.d(isSuccess ? "caption set success" : "caption set failed " + "curCaption.isModular()===" + curCaption.isModular());
        meicamCaptionClip.setCombinationAnimationUuid("");
        meicamCaptionClip.setCombinationAnimationDuration(0);
        meicamCaptionClip.setMarchInAnimationUuid(uuid);
        meicamCaptionClip.setMarchInAnimationDuration(curCaption.getModularCaptionInAnimationDuration());
        if (TextUtils.isEmpty(uuid)) {
            meicamCaptionClip.setMarchInAnimationDuration(0);
        }
        playVideo(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint());
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Delete sticker.
     *
     * @param stickerInfo the sticker info
     */
    public boolean deleteSticker(MeicamStickerClip stickerInfo) {
        if (stickerInfo == null) {
            return false;
        }
        NvsTimelineAnimatedSticker curAnimateSticker = stickerInfo.getObject();
        if (curAnimateSticker == null) {
            return false;
        }
        int zVal = (int) curAnimateSticker.getZValue();
        mTimeline.removeAnimatedSticker(curAnimateSticker);
        TimelineDataUtil.removeStickerCaption(zVal, stickerInfo.getInPoint());
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onAddStickerCaptionPicFx(null, TYPE_STICKER);
        }
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
            //mOnTimelineChangeListener.onChangeBottomView();
        }
        return true;
    }

    /**
     * Remove compound caption.
     *
     * @param meicamCompoundCaptionClip the meicam compound caption clip
     */
    public boolean removeCompoundCaption(MeicamCompoundCaptionClip meicamCompoundCaptionClip) {
        if (meicamCompoundCaptionClip == null) {
            return false;
        }
        LogUtils.d("removeCompoundCaption: getzValue: " + meicamCompoundCaptionClip.getzValue() + " getInPoint: " + meicamCompoundCaptionClip.getInPoint());
        NvsTimelineCompoundCaption currCompoundCaption = meicamCompoundCaptionClip.getObject();
        if (currCompoundCaption == null) {
            return false;
        }
        TimelineDataUtil.removeStickerCaption((int) meicamCompoundCaptionClip.getzValue(), meicamCompoundCaptionClip.getInPoint());
        mTimeline.removeCompoundCaption(currCompoundCaption);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onAddStickerCaptionPicFx(null, TYPE_COMPOUND_CAPTION);
        }
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
            // mOnTimelineChangeListener.onChangeBottomView();
        }
        return true;
    }

    /**
     * Have effect in this position boolean.
     *
     * @return the boolean
     */
    public boolean haveEffectInThisPosition() {
        long timelinePosition = getCurrentTimelinePosition();
        if (mVideoFxTrackClone == null) {
            return false;
        }
        List<ClipInfo<?>> clipInfoList = mVideoFxTrackClone.getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return false;
        }
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() <= timelinePosition && clipInfo.getOutPoint() >= timelinePosition) {
                return true;
            }
        }
        return false;
    }

    private long getEffectDuration(long timelinePosition) {
        NvsTimelineVideoFx timelineVideoFx = mTimeline.getFirstTimelineVideoFx();
        long outPoint = timelinePosition + 4 * 1000 * 1000;
        while (timelineVideoFx != null) {
            long inPoint = timelineVideoFx.getInPoint();
            if (timelinePosition < inPoint) {
                if (outPoint > inPoint) {
                    outPoint = inPoint;
                }
                break;
            }
            timelineVideoFx = mTimeline.getNextTimelineVideoFx(timelineVideoFx);
        }
        if (outPoint > mTimeline.getDuration()) {
            outPoint = mTimeline.getDuration();
        }
        return outPoint - timelinePosition;
    }

    private long removePreviewEffect(long position) {
        List<MeicamTimelineVideoFxTrack> videoFxTrackList = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
        if (CommonUtils.isEmpty(videoFxTrackList)) {
            return -1;
        }
        List<ClipInfo<?>> clipInfoList = videoFxTrackList.get(0).getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return -1;
        }
        for (ClipInfo clipInfo : clipInfoList) {
            if (clipInfo.getInPoint() <= position && clipInfo.getOutPoint() > position) {
                mTimeline.removeTimelineVideoFx(((MeicamTimelineVideoFxClip) clipInfo).getObject());
                clipInfoList.remove(clipInfo);
                return clipInfo.getInPoint();
            }
        }
        return -1;
    }

    /**
     * Copy caption meicam caption clip.
     *
     * @param currSelectedCaption the curr selected caption
     * @return the meicam caption clip
     */
    public MeicamCaptionClip copyCaption(MeicamCaptionClip currSelectedCaption) {
        if (currSelectedCaption == null) {
            return null;
        }
        MeicamCaptionClip meicamCaptionClip = (MeicamCaptionClip) currSelectedCaption.clone();

        long inPoint = currSelectedCaption.getInPoint();
        long captionDuration = currSelectedCaption.getOutPoint() - currSelectedCaption.getInPoint();
        long outPoint = inPoint + captionDuration;

        meicamCaptionClip.setInPoint(inPoint);
        meicamCaptionClip.setOutPoint(outPoint);

        int trackIndex = getTrackIndexByCopy(meicamCaptionClip.getInPoint(), meicamCaptionClip.getOutPoint(), (int) meicamCaptionClip.getzValue());
        meicamCaptionClip.setzValue(trackIndex);
        meicamCaptionClip.bindToTimeline(mTimeline);
        TimelineDataUtil.addStickerCaption(trackIndex, meicamCaptionClip);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
        }
        return meicamCaptionClip;
    }

    /**
     * Copy sticker meicam sticker clip.
     *
     * @param currSelectedSticker the curr selected sticker
     * @return the meicam sticker clip
     */
    public MeicamStickerClip copySticker(MeicamStickerClip currSelectedSticker) {
        if (currSelectedSticker == null) {
            LogUtils.e("copySticker currSelectedSticker is null !");
            return null;
        }

        MeicamStickerClip newMeicamSticker = (MeicamStickerClip) currSelectedSticker.clone();

        long inPoint = currSelectedSticker.getInPoint();
        long captionDuration = currSelectedSticker.getOutPoint() - currSelectedSticker.getInPoint();
        long outPoint = inPoint + captionDuration;

        newMeicamSticker.setInPoint(inPoint);
        newMeicamSticker.setOutPoint(outPoint);
        int trackIndex = getTrackIndexByCopy(newMeicamSticker.getInPoint(), newMeicamSticker.getOutPoint(), (int) currSelectedSticker.getzValue());
        newMeicamSticker.setzValue(trackIndex);
        newMeicamSticker.bindToTimeline(mTimeline);

        TimelineDataUtil.addStickerCaption(trackIndex, newMeicamSticker);
        seekTimeline(0);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
        }
        return newMeicamSticker;
    }

    /**
     * Copy com caption meicam compound caption clip.
     *
     * @param currCompoundCaption the curr compound caption
     * @return the meicam compound caption clip
     */
    public MeicamCompoundCaptionClip copyComCaption(MeicamCompoundCaptionClip currCompoundCaption) {
        if (currCompoundCaption == null) {
            LogUtils.e("copyComCaption currCompoundCaption is null !");
            return null;
        }
        MeicamCompoundCaptionClip newMeicamCompoundCaption = (MeicamCompoundCaptionClip) currCompoundCaption.clone();

        long inPoint = currCompoundCaption.getInPoint();
        long captionDuration = currCompoundCaption.getOutPoint() - currCompoundCaption.getInPoint();
        long outPoint = inPoint + captionDuration;
        newMeicamCompoundCaption.setInPoint(inPoint);
        newMeicamCompoundCaption.setOutPoint(outPoint);
        int trackIndex = getTrackIndexByCopy(newMeicamCompoundCaption.getInPoint(), newMeicamCompoundCaption.getOutPoint(), (int) currCompoundCaption.getzValue());
        newMeicamCompoundCaption.setzValue(trackIndex);
        newMeicamCompoundCaption.bindToTimeline(mTimeline);
        TimelineDataUtil.addStickerCaption(trackIndex, newMeicamCompoundCaption);

        seekTimeline(0);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
        }
        return newMeicamCompoundCaption;
    }

    /**
     * Freeze frame clip int.
     *
     * @param meicamVideoClip the meicam video clip
     * @param trackIndex      the track index
     * @param isPip           the is pip
     * @return the int
     */
    public int freezeFrameClip(MeicamVideoClip meicamVideoClip, int trackIndex, boolean isPip) {
        if (mTimeline == null) {
            return ReturnCode.CODE_PARAM_ERROR;
        }

        if (meicamVideoClip == null) {
            return ReturnCode.CODE_PARAM_ERROR;
        }

        NvsVideoClip editVideoClip = meicamVideoClip.getObject();
        boolean isReverseVideo = meicamVideoClip.getVideoReverse();
        long timeStamp = getCurrentTimelinePosition();
        if (timeStamp <= editVideoClip.getInPoint() || timeStamp >= editVideoClip.getOutPoint()) {
            return ReturnCode.CODE_CAN_NOT_OPERATE;
        }

        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(trackIndex);
        if (nvsVideoTrack == null) {
            return ReturnCode.CODE_OTHER;
        }
        NvsVideoClip nvsVideoClip = meicamVideoClip.getObject();
        if (nvsVideoClip == null) {
            return ReturnCode.CODE_OTHER;
        }
        int clipIndex = nvsVideoClip.getIndex();

        /*
         * 获取当前一帧图片
         * Gets the current frame image
         * */
        NvsVideoFrameRetriever videoFrameRetriever = mStreamingContext.createVideoFrameRetriever(
                isReverseVideo ? meicamVideoClip.getReverseFilePath() : meicamVideoClip.getFilePath());
        if (videoFrameRetriever != null) {
            Bitmap frameAtTime = videoFrameRetriever.getFrameAtTime(meicamVideoClip.getTrimIn() + timeStamp - nvsVideoClip.getInPoint(),
                    NvsVideoFrameRetriever.VIDEO_FRAME_HEIGHT_GRADE_720);
            if (frameAtTime == null) {
                return ReturnCode.CODE_OTHER;
            }
            String fileName = System.currentTimeMillis() + ".png";
            String freezeFilePath = PathUtils.getVideoFreezeConvertDir() + File.separator + fileName;
            ImageUtils.save(frameAtTime, freezeFilePath, Bitmap.CompressFormat.PNG);
            long duration = CommonData.DEFAULT_LENGTH;

            if (handleCutClip(meicamVideoClip, trackIndex, isPip, timeStamp)) {
                return ReturnCode.CODE_OTHER;
            }

            List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();

            List<ClipInfo<?>> clipInfoList;
            if (CommonUtils.isIndexAvailable(trackIndex, meicamVideoTrackList)) {
                MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(trackIndex);
                if (meicamVideoTrack != null) {
                    clipInfoList = meicamVideoTrack.getClipInfoList();
                    MeicamVideoClip newMeicamVideoClip = new MeicamVideoClip();
                    newMeicamVideoClip.setFilePath(freezeFilePath);
                    newMeicamVideoClip.setInPoint(getCurrentTimelinePosition());
                    newMeicamVideoClip.setOutPoint(getCurrentTimelinePosition() + duration);
                    newMeicamVideoClip.setTrimIn(0);
                    newMeicamVideoClip.setTrimOut(duration);
                    newMeicamVideoClip.setVideoType(CommonData.CLIP_IMAGE);
                    newMeicamVideoClip.insertToTimeline(nvsVideoTrack, clipIndex);
                    clipInfoList.add(clipIndex + 1, newMeicamVideoClip);

                    TimelineDataUtil.moveMainTrackClipsFromIndex(clipIndex + 2, duration);
                }
            }
            TimelineUtil.rebuildTimeline(mTimeline);
            seekTimeline(0);
            if (mOnTimelineChangeListener != null) {
                if (isPip) {
                    mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
                } else {
                    mOnTimelineChangeListener.refreshEditorTimelineView(OnTimelineChangeListener.TYPE_FREEZE_CLIP);
                    mOnTimelineChangeListener.onSaveOperation();
                }
            }
        }
        return ReturnCode.CODE_OK;
    }

    /**
     * Sets video convert.
     *
     * @param meicamVideoClip the meicam video clip
     */
    public void setVideoConvert(MeicamVideoClip meicamVideoClip) {
        if (mTimeline == null) {
            return;
        }
        if (meicamVideoClip == null) {
            return;
        }
        MeicamVideoTrack meicamVideoTrack = TimelineData.getInstance().getMeicamVideoTrackList().get(MAIN_TRACK_INDEX);
        if (meicamVideoTrack == null) {
            return;
        }
        NvsVideoTrack videoTrack = meicamVideoTrack.getObject();

        if (videoTrack == null) {
            return;
        }

        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(meicamVideoClip.getFilePath());
        long duration = avFileInfo.getDuration();
        long trimOut = meicamVideoClip.getTrimOut();
        long clipDuration = meicamVideoClip.getTrimOut() - meicamVideoClip.getTrimIn();
        meicamVideoClip.setVideoReverse(!meicamVideoClip.getVideoReverse());

        NvsVideoClip nvsVideoClip = meicamVideoClip.getObject();
        int index = nvsVideoClip.getIndex();

        videoTrack.removeClip(index, false);
        meicamVideoClip.setTrimIn(duration - trimOut);
        meicamVideoClip.setTrimOut(meicamVideoClip.getTrimIn() + clipDuration);
        meicamVideoClip.insertToTimeline(videoTrack, index);
        /*
         * 复制，底层会默认添加转场，要先进行转场删除
         * Copy, the underlying layer will add the transition by default, and the transition must be deleted first
         * */
        TimelineUtil.clearAllBuildInTransition(mTimeline);
        TimelineUtil.setTransition(mTimeline, meicamVideoTrack.getTransitionInfoList());

        seekTimeline(0);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onTimelineChanged(mTimeline, true);
            mOnTimelineChangeListener.refreshEditorTimelineView(OnTimelineChangeListener.TYPE_REVERT_CLIP);
        }
    }


    /**
     * 贴纸音效管理
     * Change sticker voice.
     *
     * @param meicamStickerClip Sticker data.
     */
    public void changeStickerVoice(MeicamStickerClip meicamStickerClip) {
        NvsTimelineAnimatedSticker sticker = meicamStickerClip.getObject();
        if (sticker != null) {
            float leftVolume = (int) sticker.getVolumeGain().leftVolume;
            if (leftVolume > 0) {
                sticker.setVolumeGain(0, 0);
                meicamStickerClip.setLeftVolume(0f);
            } else {
                sticker.setVolumeGain(1.0f, 1.0f);
                meicamStickerClip.setLeftVolume(1.0f);
            }
        }
    }

    /**
     * Change sticker mirror.
     *
     * @param meicamStickerClip the meicam sticker clip
     */
    public void changeStickerMirror(MeicamStickerClip meicamStickerClip) {
        if (mTimeline == null) {
            return;
        }
        if (meicamStickerClip == null) {
            return;
        }
        NvsTimelineAnimatedSticker timelineAnimatedSticker = meicamStickerClip.getObject();

        if (timelineAnimatedSticker == null) {
            return;
        }
        /*
         * 贴纸水平翻转
         * Flip the sticker horizontally
         * */
        boolean isHorizFlip = !timelineAnimatedSticker.getHorizontalFlip();
        timelineAnimatedSticker.setHorizontalFlip(isHorizFlip);
        meicamStickerClip.setHorizontalFlip(isHorizFlip);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
    }

    /**
     * 应用字幕位置到全部片段
     * Apply the position of  caption to all captions.
     *
     * @param meicamCaptionClip Caption data.
     */
    public void applyCaptionPositionToAll(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getStickerCaption();
        if (CommonUtils.isEmpty(clipInfos)) {
            return;
        }
        PointF captionTranslation = curCaption.getCaptionTranslation();
        for (ClipInfo<?> clipInfo : clipInfos) {
            if (clipInfo instanceof MeicamCaptionClip) {
                ((MeicamCaptionClip) clipInfo).setTranslation(captionTranslation);
            }
        }
    }


    /**
     * 应用字体到全部片段
     * Apply font of caption to all captions.
     *
     * @param meicamCaptionClip Caption data.
     */
    public void applyCaptionFontToAll(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getStickerCaption();
        if (CommonUtils.isEmpty(clipInfos)) {
            return;
        }
        String fontFamily = curCaption.getFontFamily();
        for (ClipInfo<?> clipInfo : clipInfos) {
            if (clipInfo instanceof MeicamCaptionClip) {
                ((MeicamCaptionClip) clipInfo).setFont(fontFamily);
            }
        }
    }


    /**
     * 应用行列间距到全部片段
     * Apply Letter and line space if caption to all captions.
     *
     * @param meicamCaptionClip Caption data.
     */
    public void applyCaptionLetterSpaceToAll(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getStickerCaption();
        if (CommonUtils.isEmpty(clipInfos)) {
            return;
        }
        float letterSpacing = curCaption.getLetterSpacing();
        for (ClipInfo<?> clipInfo : clipInfos) {
            if (clipInfo instanceof MeicamCaptionClip) {
                ((MeicamCaptionClip) clipInfo).setLetterSpacing(letterSpacing);
            }
        }
    }

    /**
     * 应用字幕描边到全部片段
     * Apply outline effrect of caption to all captions.
     *
     * @param meicamCaptionClip Caption data
     */
    public void applyCaptionOutlineToAll(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        NvsTimelineCaption curCaption = meicamCaptionClip.getObject();
        if (curCaption == null) {
            return;
        }
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getStickerCaption();
        if (CommonUtils.isEmpty(clipInfos)) {
            return;
        }
        float[] outlineColor = meicamCaptionClip.getOutlineColor();
        if (outlineColor == null) {
            return;
        }
        for (ClipInfo<?> clipInfo : clipInfos) {
            if (clipInfo instanceof MeicamCaptionClip) {
                ((MeicamCaptionClip) clipInfo).setOutlineColor(outlineColor);
            }
        }
    }

    /**
     * 应用字幕颜色到全部片段
     * Apply caption color to all caption
     *
     * @param meicamCaptionClip clip
     */
    public void applyCaptionColorToAll(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getStickerCaption();
        if (CommonUtils.isEmpty(clipInfos)) {
            return;
        }

        float[] textColor = meicamCaptionClip.getTextColor();
        if (textColor == null) {
            return;
        }
        for (ClipInfo<?> clipInfo : clipInfos) {
            if (clipInfo instanceof MeicamCaptionClip) {
                ((MeicamCaptionClip) clipInfo).setTextColor(textColor);
            }
        }
    }

    /**
     * 移除字幕描边
     * <p>
     * Remove the caption stroke.
     *
     * @param meicamCaptionClip 字幕clip caption clip
     */
    public void removeCaptionOutlineColor(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        meicamCaptionClip.setOutline(false);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * 移除字幕背景颜色
     * Remove subtitle background color.
     *
     * @param meicamCaptionClip the meicam caption clip
     */
    public void removeCaptionBackgroundColor(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip == null) {
            return;
        }
        float[] backgroundColor = meicamCaptionClip.getBackgroundColor();
        if (backgroundColor != null) {
            backgroundColor[3] = 0;
            meicamCaptionClip.setBackgroundColor(backgroundColor);
            seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        }
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Sets caption background color.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param colorValue        the color value
     */
    public void setCaptionBackgroundColor(MeicamCaptionClip meicamCaptionClip, String colorValue) {
        if (meicamCaptionClip == null || TextUtils.isEmpty(colorValue)) {
            return;
        }
        float[] color = ColorUtil.stringColorToColor(colorValue);
        meicamCaptionClip.setBackgroundColor(color);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Sets caption out line color.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param colorValue        the color value
     */
    public void setCaptionOutLineColor(MeicamCaptionClip meicamCaptionClip, String colorValue) {
        if (meicamCaptionClip == null || TextUtils.isEmpty(colorValue)) {
            return;
        }
        float[] colorArray = ColorUtil.stringColorToColor(colorValue);
        meicamCaptionClip.setOutlineColor(colorArray);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Sets caption text color.
     *
     * @param meicamCaptionClip the meicam caption clip
     * @param colorValue        the color value
     */
    public void setCaptionTextColor(MeicamCaptionClip meicamCaptionClip, String colorValue) {
        if (meicamCaptionClip == null || TextUtils.isEmpty(colorValue)) {
            return;
        }

        float[] colorArray = ColorUtil.stringColorToColor(colorValue);
        meicamCaptionClip.setTextColor(colorArray);
        seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        if (mOnTimelineChangeListener != null) {
            mOnTimelineChangeListener.onSaveOperation();
        }
    }

    /**
     * Update caption translation.
     *
     * @param meicamCaptionClip the meicam caption clip
     */
    public void updateCaptionTranslation(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip != null) {
            NvsTimelineCaption nvsTimelineCaption = meicamCaptionClip.getObject();
            if (nvsTimelineCaption != null) {
                PointF captionTranslation = nvsTimelineCaption.getCaptionTranslation();
                if (captionTranslation != null) {
                    meicamCaptionClip.setTranslationX(captionTranslation.x);
                    meicamCaptionClip.setTranslationY(captionTranslation.y);
                }
            }
        }
    }

    /**
     * Update sticker translation.
     *
     * @param meicamStickerClip the meicam sticker clip
     */
    public void updateStickerTranslation(MeicamStickerClip meicamStickerClip) {
        if (meicamStickerClip != null) {
            NvsTimelineAnimatedSticker timelineAnimatedSticker = meicamStickerClip.getObject();
            if (timelineAnimatedSticker != null) {
                PointF translation = timelineAnimatedSticker.getTranslation();
                if (translation != null) {
                    meicamStickerClip.setTranslationX(translation.x);
                    meicamStickerClip.setTranslationY(translation.y);
                }
            }
        }
    }

    /**
     * Update compound caption translation.
     *
     * @param meicamCompoundCaptionClip the meicam compound caption clip
     */
    public void updateCompoundCaptionTranslation(MeicamCompoundCaptionClip meicamCompoundCaptionClip) {
        if (meicamCompoundCaptionClip != null) {
            NvsTimelineCompoundCaption timelineCompoundCaption = meicamCompoundCaptionClip.getObject();
            if (timelineCompoundCaption != null) {
                PointF translation = timelineCompoundCaption.getCaptionTranslation();
                if (translation != null) {
                    meicamCompoundCaptionClip.setTranslationX(translation.x);
                    meicamCompoundCaptionClip.setTranslationY(translation.y);
                    meicamCompoundCaptionClip.setRotation(timelineCompoundCaption.getRotationZ());
                }
            }
        }
    }

    /**
     * Update caption scale.
     *
     * @param meicamCaptionClip the meicam caption clip
     */
    public void updateCaptionScale(MeicamCaptionClip meicamCaptionClip) {
        if (meicamCaptionClip != null) {
            NvsTimelineCaption timelineCaption = meicamCaptionClip.getObject();
            if (timelineCaption != null) {
                float scaleX = timelineCaption.getScaleX();
                float scaleY = timelineCaption.getScaleY();
                meicamCaptionClip.setScaleX(scaleX);
                meicamCaptionClip.setScaleY(scaleY);
                meicamCaptionClip.setRotation(timelineCaption.getRotationZ());
            }
        }
    }

    /**
     * Update sticker scale.
     *
     * @param meicamStickerClip the meicam sticker clip
     */
    public void updateStickerScale(MeicamStickerClip meicamStickerClip) {
        if (meicamStickerClip != null) {
            NvsTimelineAnimatedSticker timelineAnimatedSticker = meicamStickerClip.getObject();
            if (timelineAnimatedSticker != null) {
                float scale = timelineAnimatedSticker.getScale();
                meicamStickerClip.setRotation(timelineAnimatedSticker.getRotationZ());
                meicamStickerClip.setScale(scale);
            }
        }
    }

    /**
     * Update compound caption scale.
     *
     * @param meicamCompoundClip the meicam compound clip
     */
    public void updateCompoundCaptionScale(MeicamCompoundCaptionClip meicamCompoundClip) {
        if (meicamCompoundClip != null) {
            NvsTimelineCompoundCaption timelineCompoundCaption = meicamCompoundClip.getObject();
            if (timelineCompoundCaption != null) {
                float scaleX = timelineCompoundCaption.getScaleX();
                float scaleY = timelineCompoundCaption.getScaleY();
                meicamCompoundClip.setScaleX(scaleX);
                meicamCompoundClip.setScaleY(scaleY);
            }
        }
    }

    /**
     * 切换比例改变字幕位置
     * Change caption position.
     *
     * @param widthRatio  the width ratio
     * @param heightRatio the height ratio
     */
    public void changeCaptionPosition(float widthRatio, float heightRatio) {
        List<ClipInfo<?>> clipInfos = TimelineDataUtil.getMainTrackVideoClip();
        if (!CommonUtils.isIndexAvailable(0, clipInfos)) {
            return;
        }
        MeicamVideoClip meicamVideoClip = (MeicamVideoClip) clipInfos.get(0);
        boolean flag = meicamVideoClip.getOriginalWidth() * 1.0f / meicamVideoClip.getOriginalHeight() <= 1 ? true : false;
        float vScale = flag ? heightRatio : widthRatio;
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        if (meicamStickerCaptionTrackList != null) {
            for (int i = 0; i < meicamStickerCaptionTrackList.size(); i++) {
                MeicamStickerCaptionTrack meicamStickerCaptionTrack = meicamStickerCaptionTrackList.get(i);
                if (meicamStickerCaptionTrack == null) {
                    continue;
                }
                List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
                if (clipInfoList == null) {
                    continue;
                }
                for (int j = 0; j < clipInfoList.size(); j++) {
                    ClipInfo<?> clipInfo = clipInfoList.get(j);
                    if (clipInfo == null) {
                        continue;
                    }
                    if (clipInfo instanceof MeicamCaptionClip) {
                        MeicamCaptionClip meicamCaptionClip = (MeicamCaptionClip) clipInfo;
                        meicamCaptionClip.setTranslationX(meicamCaptionClip.getTranslationX() * vScale);
                        meicamCaptionClip.setTranslationY(meicamCaptionClip.getTranslationY() * vScale);
                    }
                }
            }
        }
        seekTimeline();
    }

    /**
     * 时间线变化时的回调
     * The interface On timeline change listener.
     */
    public interface OnTimelineChangeListener {

        //删除clip Delete the clip
        int TYPE_DELETE_CLIP = 1;
        //复制clip Copy the clip
        int TYPE_COPY_CLIP = 2;
        //分割clip Split clip
        int TYPE_CUT_CLIP = 3;
        //定格clip Stop motion clip
        int TYPE_FREEZE_CLIP = 4;
        //倒放clip Backwards clip
        int TYPE_REVERT_CLIP = 5;

        /**
         * 当时间线内容变化的回调
         * On timeline changed.
         *
         * @param timeline the timeline
         * @param needSave 是否保存草稿。the need save
         */
        void onTimelineChanged(NvsTimeline timeline, boolean needSave);

        /**
         * 轨道上的片段选中变化时触发
         * On need track select changed.
         *
         * @param trackIndex 对应的轨道index。the track index
         * @param inPoint    片段在轨道上的入点，通过入点获取clip。the in point
         */
        void onNeedTrackSelectChanged(int trackIndex, long inPoint);

        /**
         * 刷新轨道控件
         * Refresh editor timeline view.
         *
         * @param type 触发轨道变化的操作。the type
         */
        void refreshEditorTimelineView(int type);

        /**
         * 保存草稿的操作
         * On save operation.
         */
        void onSaveOperation();

        /**
         * 添加贴纸、字幕、fx(特效、滤镜等)的操作
         * On add sticker caption pic fx.
         *
         * @param object 添加的对象。the object
         * @param type   添加的类型。the type
         */
        void onAddStickerCaptionPicFx(Object object, int type);

    }

    /**
     * 轨道变化的回调
     * The interface On track change listener.
     */
    public interface OnTrackChangeListener {
        /**
         * 音频的切割操作
         * Audio edit cut clip.
         *
         * @param nvsAudioTrack 音频轨道。the nvs audio track
         * @param inPoint       需要切割的时间点。the in point
         */
        void audioEditCutClip(NvsAudioTrack nvsAudioTrack, long inPoint);

        /**
         * 音频的删除操作，删除的是上层报错的当前选中的片段
         * Audio edit delete clip.
         */
        void audioEditDeleteClip();

        /**
         * 复制上层保存的当前的片段
         * Audio edit copy clip.
         *
         * @param inPoint       复制后新片段的入点。the in point
         * @param nvsAudioTrack the nvs audio track
         */
        void audioEditCopyClip(long inPoint, NvsAudioTrack nvsAudioTrack);

        void pipCopy(MeicamVideoClip meicamVideoClip, int trackIndex);
    }

    public NvsStreamingContext getStreamingContext() {
        if (mStreamingContext == null) {
            mStreamingContext = initStreamContext();
        }
        return mStreamingContext;
    }

    private void changeTrimPointOnTrimAdjust(NvsVideoClip videoClip, long trimPoint, boolean isInPoint) {
        if (videoClip == null) {
            return;
        }
        if (isInPoint) {
            videoClip.changeTrimInPoint(trimPoint, true);
        } else {
            videoClip.changeTrimOutPoint(trimPoint, true);
        }
    }

    private long setTrimOutPoint(NvsVideoClip videoClip, long trim, boolean affectSibling) {
        if (trim < 0) {
            trim = 0;
        }
        return videoClip.changeTrimOutPoint(trim, affectSibling);
    }

    private long setTrimInPoint(NvsVideoClip videoClip, long trim, boolean affectSibling) {
        if (trim < 0) {
            trim = 0;
        }
        return videoClip.changeTrimInPoint(trim, affectSibling);
    }

    /**
     * 引擎的停止操作。可以停止播放，生成等。
     * Stop.
     */
    public void stop() {
        mStreamingContext.stop();
    }

    /**
     * 判断当前是否是在播放中。
     * Is playing boolean.
     *
     * @return the boolean
     */
    public boolean isPlaying() {
        return mStreamingContext.getStreamingEngineState() == STREAMING_ENGINE_STATE_PLAYBACK;
    }



    /**
     * 获取蒙版对应的特效对象
     * <p>
     * Get mask object.
     *
     * @param meicamVideoClip 视频Clip. video clip data.
     * @return 蒙版特效对象 mask object
     */
    public MeicamVideoFx getMaskTargetFx(MeicamVideoClip meicamVideoClip) {
        if (meicamVideoClip == null) {
            LogUtils.e("getMaskTargetFx meicamVideoClip==null");
            return null;
        }
        List<MeicamVideoFx> videoFxs = meicamVideoClip.getVideoFxs();

        if (null != videoFxs && videoFxs.size() > 0) {
            for (int i = 0; i < videoFxs.size(); i++) {
                MeicamVideoFx meicamVideoFx = videoFxs.get(i);
                if (TextUtils.isEmpty(meicamVideoFx.getSubType())) {
                    if (meicamVideoFx.getDesc().equals(NvsConstants.KEY_MASK_GENERATOR)) {
                        return meicamVideoFx;
                    }
                } else {
                    if (SUB_TYPE_MASK.equals(meicamVideoFx.getSubType()) && meicamVideoFx.getDesc().equals(NvsConstants.KEY_MASK_GENERATOR)) {
                        return meicamVideoFx;
                    }
                }
            }
        }
        return null;
    }

    public boolean isUseFaceShape() {
        return useFaceShape;
    }

    public static class PointXComparator implements Comparator<PointF> {

        @Override
        public int compare(PointF bean1, PointF bean2) {
            return (int) (bean1.x - bean2.x);
        }
    }

    /**
     * set livewindrow size.
     * 设置liveWindrow大小，切换比例的时候要调用
     *
     * @param liveWindow the live window
     * @return the live windrow size
     */
    public void setLiveWindrowSize(NvsLiveWindowExt liveWindow) {
        mLiveWindowSize = frameForTimeline(liveWindow.getWidth(), liveWindow.getHeight());
    }

    /**
     * Gets livewindrow size.
     * 获取liveWindrow大小，虽然现在不再改变liveWindrow的大小，但是依然沿用以前的liveWindrow的计算方法
     *
     * @param liveWindow the live window
     * @return the live windrow size
     */
    public PointF getLiveWindrowSize(NvsLiveWindowExt liveWindow) {
        if (mLiveWindowSize == null) {
            mLiveWindowSize = frameForTimeline(liveWindow.getWidth(), liveWindow.getHeight());
        }
        return mLiveWindowSize;
    }

//    /**
//     * Frame for timeline point f.
//     * 根据timeline比例计算timeline 展示size
//     *
//     * @return the point f
//     */
//    public static PointF frameForTimeline(int height) {
//        Point videoSize = TimelineUtil.calculateTimelineSize(TimelineData.getInstance().getMakeRatio());
//
//        PointF rect = new PointF();
//        int maxWidth = ScreenUtils.getAppScreenWidth();
//        int maxHeight = height;
//        if (videoSize.x >= videoSize.y) {
//            rect.x = maxWidth * 1.0f;
//            rect.y = maxWidth * videoSize.y * 1.0f / videoSize.x;
//        } else {
//            rect.x = maxHeight * videoSize.x * 1.0f / videoSize.y;
//            rect.y = maxHeight * 1.0f;
//        }
//        return rect;
//    }


    /**
     * Frame for timeline point f.
     * 根据timeline比例计算timeline 展示size
     *
     * @return the point f
     */
    public static PointF frameForTimeline(int width, int height) {
        NvsVideoResolution videoResolution = TimelineData.getInstance().getVideoResolution();
        float timelineRatio = videoResolution.imageWidth * 1.0F / videoResolution.imageHeight;
        float viewRatio = width * 1.0F / height;
        PointF rect = new PointF();
        if (timelineRatio > viewRatio) {
            rect.x = width;
            rect.y = width / timelineRatio;
        } else {
            rect.y = height;
            rect.x = height * timelineRatio;
        }
        return rect;
    }

    /**
     * The type Point y comparator.
     */
    public static class PointYComparator implements Comparator<PointF> {

        @Override
        public int compare(PointF bean1, PointF bean2) {
            return (int) (bean1.y - bean2.y);
        }
    }
}
