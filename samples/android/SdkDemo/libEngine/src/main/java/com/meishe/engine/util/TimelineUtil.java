package com.meishe.engine.util;

import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.EditorEngine;
import com.meishe.engine.bean.ClipInfo;
import com.meishe.engine.bean.MeicamAdjustData;
import com.meishe.engine.bean.MeicamAudioClip;
import com.meishe.engine.bean.MeicamAudioTrack;
import com.meishe.engine.bean.MeicamCaptionClip;
import com.meishe.engine.bean.MeicamCompoundCaptionClip;
import com.meishe.engine.bean.MeicamFxParam;
import com.meishe.engine.bean.MeicamStickerCaptionTrack;
import com.meishe.engine.bean.MeicamStickerClip;
import com.meishe.engine.bean.MeicamTheme;
import com.meishe.engine.bean.MeicamTimelineVideoFxClip;
import com.meishe.engine.bean.MeicamTimelineVideoFxTrack;
import com.meishe.engine.bean.MeicamTransition;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoFx;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.bean.MeicamWaterMark;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.bean.TimelineDataUtil;
import com.meishe.engine.constant.NvsConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.meishe.engine.bean.CommonData.TIMELINE_RESOLUTION_VALUE;

/**
 * The type Timeline util.
 * 、时间轴工具类
 */
public class TimelineUtil {

    private static String TAG = TimelineUtil.class.getSimpleName();

    /**
     * Create timeline nvs timeline.
     * 创建时间线nvs时间线
     *
     * @return the nvs timeline
     */
    public static NvsTimeline createTimeline() {
        NvsTimeline timeline = newTimeline(TimelineData.getInstance().getVideoResolution());
        if (timeline == null) {
            LogUtils.e("failed to create timeline");
            return null;
        }

        if (!buildVideoTrack(timeline)) {
            return timeline;
        }

        setTimelineData(timeline);

        return timeline;
    }

    /**
     * 铺设timeline上的信息，除视频轨道以外。
     * Sets timeline data.
     *
     * @param timeline the timeline
     */
    public static void setTimelineData(NvsTimeline timeline) {
        if (timeline == null) {
            return;
        }
        /*
         * 时间轴数据
         * Timeline data
         * */
        applyTheme(timeline);
        /*
         * 添加timeline特效
         * Add Timeline effects
         * */
        removeAllTimelineEffect(timeline);
        addTimeLineEffect(timeline);

        /*
         * 添加音乐
         * Add Music
         * */
        buildTimelineMusic(timeline);

        /*
         * 添加timeline滤镜
         * Add the Timeline filter
         * */
        MeicamVideoFx filterFx = TimelineData.getInstance().getFilterFx();
        if (filterFx != null) {
            buildTimelineFilter(timeline, filterFx);
        }


        setAdjustEffects(timeline);

        /*
         * 添加clip滤镜
         * Add a clip filter
         * */
        buildClipFilter(timeline);


        removeAllSticker(timeline);
        removeAllCaption(timeline);
        removeAllCompoundCaption(timeline);
        setSitckerCaptionObject(timeline);
        /**
         * 创建音频片段
         * Creating audio clips
         */
        buildTimelineRecordAudio(timeline);

        /**
         * 创建水印
         * Create a watermark
         */
        buildTimelineWaterMark(timeline);
        /**
         * 创建马赛克、模糊特效
         * Create Mosaic and blur effects
         */
        buildTimelineMasicEffect(timeline);
    }


    /**
     * 设置调节功能的参数
     * Sets adjust effects.
     *
     * @param timeline the timeline
     */
    public static void setAdjustEffects(NvsTimeline timeline) {
        if (timeline == null) {
            return;
        }
        MeicamAdjustData meicamAdjustData = TimelineData.getInstance().getMeicamAdjustData();
        if (meicamAdjustData == null) {
            return;
        }
        setTimelineAdjustEffect(timeline, meicamAdjustData.getAmount(), NvsConstants.ADJUST_AMOUNT, NvsConstants.FX_SHARPEN);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getDegree(), NvsConstants.ADJUST_DEGREE, NvsConstants.FX_VIGNETTE);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getBlackPoint(), NvsConstants.ADJUST_BLACKPOINT, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getTint(), NvsConstants.ADJUST_TINT, NvsConstants.ADJUST_TINT);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getTemperature(), NvsConstants.ADJUST_TEMPERATURE, NvsConstants.ADJUST_TINT);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getShadow(), NvsConstants.ADJUST_SHADOW, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getHighlight(), NvsConstants.ADJUST_HIGHTLIGHT, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getSaturation(), NvsConstants.ADJUST_SATURATION, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getContrast(), NvsConstants.ADJUST_CONTRAST, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        setTimelineAdjustEffect(timeline, meicamAdjustData.getBrightness(), NvsConstants.ADJUST_BRIGHTNESS, NvsConstants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
    }


    private static void setTimelineAdjustEffect(NvsTimeline timeline, float adjustData, String adjustKey, String attachmentKey) {
        NvsTimelineVideoFx timenlineSharpenVideoFx = null;
        NvsTimelineVideoFx nvsTimelineVideoFx = timeline.getFirstTimelineVideoFx();

        while (nvsTimelineVideoFx != null) {
            Object attachment = nvsTimelineVideoFx.getAttachment(attachmentKey);
            if (attachment != null && attachmentKey.equals(attachment)) {
                timenlineSharpenVideoFx = nvsTimelineVideoFx;
                break;
            }
            nvsTimelineVideoFx = timeline.getNextTimelineVideoFx(nvsTimelineVideoFx);
        }
        if (timenlineSharpenVideoFx != null) {
            timenlineSharpenVideoFx.setFloatVal(adjustKey, adjustData);
            timenlineSharpenVideoFx.setBooleanVal(NvsConstants.KEY_VIDEO_MODE, true);
        } else {
            timenlineSharpenVideoFx = timeline.addBuiltinTimelineVideoFx(0, timeline.getDuration(), attachmentKey);
            if (timenlineSharpenVideoFx != null) {
                timenlineSharpenVideoFx.setAttachment(attachmentKey, attachmentKey);
                timenlineSharpenVideoFx.setFloatVal(adjustKey, adjustData);
                timenlineSharpenVideoFx.setBooleanVal(NvsConstants.KEY_VIDEO_MODE, true);
            }
        }
    }


    /**
     * 内建特效马赛克模糊
     * buildIn fx mosaic
     */
    public static final String MOSAICNAME = "Mosaic";
    /**
     * 内建特效高斯模糊
     * buildIn fx Gaussian Blur
     */
    public static final String BLURNAME = "Gaussian Blur";

    private static void buildTimelineMasicEffect(NvsTimeline timeline) {
        List<MeicamTimelineVideoFxClip> meicamTimelineVideoFxClips = TimelineData.getInstance().getMeicamTimelineVideoFxClipList();
        if (meicamTimelineVideoFxClips.size() > 0) {
            MeicamTimelineVideoFxClip meicamTimelineVideoFxClip = meicamTimelineVideoFxClips.get(0);
            NvsTimelineVideoFx nvsTimelineVideoFx = meicamTimelineVideoFxClip.bindToTimeline(timeline);
            if (nvsTimelineVideoFx == null) {
                return;
            }
            nvsTimelineVideoFx.setRegional(true);
            nvsTimelineVideoFx.setRegionalFeatherWidth(0f);
            if (meicamTimelineVideoFxClip.getDesc().equals(MOSAICNAME)) {
                nvsTimelineVideoFx.setFilterIntensity(meicamTimelineVideoFxClip.getIntensity());
                List<MeicamFxParam> meicamFxParams = meicamTimelineVideoFxClip.getMeicamFxParamList();
                MeicamFxParam meicamFxParamFirst = null, meicamFxParamSecond = null;
                for (MeicamFxParam param : meicamFxParams) {
                    if ("float".equals(param.getType())) {
                        meicamFxParamFirst = param;
                    } else if ("float[]".equals(param.getType())) {
                        meicamFxParamSecond = param;
                    }
                }
                nvsTimelineVideoFx.setFloatVal(meicamFxParamFirst.getKey(), Double.parseDouble(meicamFxParamFirst.getValue().toString()));
                float[] point = new float[8];
                Object value = meicamFxParamSecond.getValue();
                if (value instanceof ArrayList) {
                    ArrayList<Double> valueList = (ArrayList<Double>) value;
                    for (int i = 0; i < valueList.size(); i++) {
                        point[i] = Float.parseFloat(valueList.get(i).toString());
                    }
                    nvsTimelineVideoFx.setRegion(point);
                } else {
                    nvsTimelineVideoFx.setRegion((float[]) value);
                }

            } else if (meicamTimelineVideoFxClip.getDesc().equals(BLURNAME)) {
                List<MeicamFxParam> meicamFxParams = meicamTimelineVideoFxClip.getMeicamFxParamList();
                if (meicamFxParams == null || meicamFxParams.size() == 0) {
                    return;
                }
                MeicamFxParam meicamFxParam = null, meicamFxParamSecond = null;
                for (MeicamFxParam param : meicamFxParams) {
                    if ("float".equals(param.getType())) {
                        meicamFxParamSecond = param;
                    } else if ("float[]".equals(param.getType())) {
                        meicamFxParam = param;
                    }
                }
                nvsTimelineVideoFx.setFilterIntensity(1);
                if (meicamFxParamSecond != null) {
                    nvsTimelineVideoFx.setFloatVal(meicamFxParamSecond.getKey(), Double.parseDouble(meicamFxParamSecond.getValue().toString()));
                }
                if (meicamFxParam != null) {
                    float[] point = new float[8];
                    Object value = meicamFxParam.getValue();
                    if (value instanceof ArrayList) {
                        ArrayList<Double> valueList = (ArrayList<Double>) value;
                        if (valueList == null || valueList.size() == 0) {
                            LogUtils.e("setRegion  valueList.size() == 0");
                            return;
                        }
                        for (int i = 0; i < valueList.size(); i++) {
                            point[i] = Float.parseFloat(valueList.get(i).toString());
                        }
                        nvsTimelineVideoFx.setRegion(point);
                    } else {
                        nvsTimelineVideoFx.setRegion((float[]) value);
                    }
                }

            }
        }
    }

    /**
     * 创建水印
     * Create a watermark
     *
     * @param timeline
     */
    private static void buildTimelineWaterMark(NvsTimeline timeline) {
        MeicamWaterMark waterMark = TimelineData.getInstance().getMeicamWaterMark();
        if (waterMark == null) {
            return;
        }
        timeline.deleteWatermark();
        if (TextUtils.isEmpty(waterMark.getWatermarkPath())) {
            return;
        }
        timeline.addWatermark(waterMark.getWatermarkPath(), waterMark.getWatermarkW(), waterMark.getWatermarkH(), 1
                , NvsTimeline.NvsTimelineWatermarkPosition_TopLeft, waterMark.getWatermarkX(), waterMark.getWatermarkY());
    }

    private static void addTimeLineEffect(NvsTimeline timeline) {
        List<MeicamTimelineVideoFxTrack> videoFxTrackList = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
        if (CommonUtils.isEmpty(videoFxTrackList)) {
            return;
        }
        List<ClipInfo<?>> clipInfoList = videoFxTrackList.get(0).getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return;
        }
        for (ClipInfo clipInfo : clipInfoList) {
            ((MeicamTimelineVideoFxClip) clipInfo).bindToTimeline(timeline);
        }
    }

    /**
     * 添加时间线上的音频信息
     * Build timeline music boolean.
     *
     * @param timeline the timeline
     * @return the boolean。如果音频数据为空或者添加音频轨道失败则返回false
     */
    public static boolean buildTimelineMusic(NvsTimeline timeline) {

        int count = timeline.audioTrackCount();
        for (int index = 0; index < count; index++) {
            NvsAudioTrack audioTrackByIndex = timeline.getAudioTrackByIndex(index);
            audioTrackByIndex.removeAllClips();
        }
        /*
         * 去掉音乐之后，要把已经应用的主题中的音乐还原
         * After removing the music, you need to restore the music in the theme you have applied
         * */
        MeicamTheme meicamTheme = TimelineData.getInstance().getMeicamTheme();
        if (meicamTheme != null) {
            String themePackageId = meicamTheme.getThemePackageId();
            if (!TextUtils.isEmpty(themePackageId)) {
                timeline.setThemeMusicVolumeGain(1.0f, 1.0f);
            }
        }

        List<MeicamAudioTrack> meicamAudioTrackList = TimelineData.getInstance().getMeicamAudioTrackList();
        if (CommonUtils.isEmpty(meicamAudioTrackList)) {
            return false;
        }
        for (MeicamAudioTrack meicamAudioTrack : meicamAudioTrackList) {
            NvsAudioTrack audioTrack = meicamAudioTrack.bindToTimeline(timeline);
            if (audioTrack == null) {
                LogUtils.e("buildTimelineMusic: fail to create audio track");
                return false;
            }
            List<ClipInfo<?>> meicamAudioClipList = meicamAudioTrack.getClipInfoList();

            for (int j = 0; j < meicamAudioClipList.size(); j++) {
                MeicamAudioClip meicamAudioClip = (MeicamAudioClip) meicamAudioClipList.get(j);
                meicamAudioClip.bindToTimeline(audioTrack);
            }
        }
        return true;
    }

    /**
     * 设置时间线上的贴纸和字幕
     * Sets sitcker caption object.
     *
     * @param timeline the timeline
     */
    public static void setSitckerCaptionObject(NvsTimeline timeline) {
        List<MeicamStickerCaptionTrack> meicamStickerCaptionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        for (MeicamStickerCaptionTrack meicamStickerCaptionTrack : meicamStickerCaptionTrackList) {
            List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
            for (ClipInfo clipInfo : clipInfoList) {
                if (clipInfo instanceof MeicamStickerClip) {
                    MeicamStickerClip meicamStickerClip = (MeicamStickerClip) clipInfo;
                    meicamStickerClip.bindToTimeline(timeline);
                } else if (clipInfo instanceof MeicamCaptionClip) {
                    MeicamCaptionClip meicamCaptionClip = (MeicamCaptionClip) clipInfo;
                    meicamCaptionClip.bindToTimeline(timeline);
                } else if (clipInfo instanceof MeicamCompoundCaptionClip) {
                    MeicamCompoundCaptionClip meicamCompoundCaptionClip = (MeicamCompoundCaptionClip) clipInfo;
                    meicamCompoundCaptionClip.bindToTimeline(timeline);
                }
            }
        }
    }

    private static void removeAllSticker(NvsTimeline timeline) {
        if (timeline == null) {
            Log.e(TAG, "removeAllSticker: timeline is null");
            return;
        }
        NvsTimelineAnimatedSticker sticker = timeline.getFirstAnimatedSticker();
        while (sticker != null) {
            sticker = timeline.removeAnimatedSticker(sticker);
        }
    }

    private static void removeAllTimelineEffect(NvsTimeline timeline) {
        if (timeline == null) {
            Log.e(TAG, "removeAllSticker: timeline is null");
            return;
        }
        NvsTimelineVideoFx videoFx = timeline.getFirstTimelineVideoFx();
        while (videoFx != null) {
            videoFx = timeline.removeTimelineVideoFx(videoFx);
        }
    }

    private static void removeAllCaption(NvsTimeline timeline) {
        if (timeline == null) {
            Log.e(TAG, "removeAllSticker: timeline is null");
            return;
        }
        NvsTimelineCaption deleteCaption = timeline.getFirstCaption();
        while (deleteCaption != null) {
            int capCategory = deleteCaption.getCategory();
            LogUtils.e("capCategory = " + capCategory);
            int roleTheme = deleteCaption.getRoleInTheme();
            if (capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不做删除 Subject subtitles will not be deleted
                deleteCaption = timeline.getNextCaption(deleteCaption);
            } else {
                deleteCaption = timeline.removeCaption(deleteCaption);
            }
        }
    }

    private static void removeAllCompoundCaption(NvsTimeline timeline) {
        if (timeline == null) {
            Log.e(TAG, "removeAllSticker: timeline is null");
            return;
        }
        NvsTimelineCompoundCaption compoundCaption = timeline.getFirstCompoundCaption();
        while (compoundCaption != null) {
            compoundCaption = timeline.removeCompoundCaption(compoundCaption);
        }
    }

    /**
     * 设置时间线上录音的信息
     * Build timeline record audio.
     *
     * @param timeline the timeline
     */
    public static void buildTimelineRecordAudio(NvsTimeline timeline) {
        if (timeline == null) {
            return;
        }
        List<MeicamAudioTrack> meicamAudioTrackList = TimelineData.getInstance().getMeicamAudioTrackList();

        NvsAudioTrack audioTrack = null;
        for (int i = 0; i < meicamAudioTrackList.size(); i++) {
            MeicamAudioTrack meicamAudioTrack = meicamAudioTrackList.get(i);
            List<ClipInfo<?>> meicamAudioClipList = meicamAudioTrack.getClipInfoList();
            audioTrack = timeline.getAudioTrackByIndex(meicamAudioTrack.getIndex());
            if (audioTrack == null) {
                audioTrack = timeline.appendAudioTrack();
            }
            for (int j = 0; j < meicamAudioClipList.size(); j++) {
                MeicamAudioClip meicamAudioClip = (MeicamAudioClip) meicamAudioClipList.get(j);
                meicamAudioClip.bindToTimeline(audioTrack);
            }
        }
    }

    /**
     * 铺设视频轨道
     * build video track
     *
     * @param timeline
     * @return 如果时间线为空或者视频轨道数据为空则返回false.
     * if timeline is null or video track list is empty will return false.
     */
    private static boolean buildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }
        List<MeicamVideoTrack> videoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(videoTrackList)) {
            LogUtils.e("no track data!!!");
            return false;
        }
        return fillTrack(timeline, videoTrackList);
    }

    /**
     * 创建时间线
     * New timeline nvs timeline.
     *
     * @param firstVideoPath the first video path
     * @return the nvs timeline
     */
    public static NvsTimeline newTimeline(String firstVideoPath) {
        return newTimeline(getVideoEditResolutionByClip(firstVideoPath));
    }

    /**
     * 根据设置的宽高信息创建时间线
     * New timeline nvs timeline.
     *
     * @param videoResolution 视频分辨率   audioResolution:音频解析度
     * @return nvs timeline
     */
    public static NvsTimeline newTimeline(NvsVideoResolution videoResolution) {
        if (videoResolution == null) {
            LogUtils.e("videoResolution is null");
            List<ClipInfo<?>> clipInfos = TimelineDataUtil.getMainTrackVideoClip();
            if (clipInfos != null && clipInfos.size() > 0) {
                MeicamVideoClip meicamVideoClip = (MeicamVideoClip) clipInfos.get(0);
                videoResolution = getVideoEditResolutionByClip(meicamVideoClip.getFilePath());
            }
            if (videoResolution == null) {
                LogUtils.e("videoResolution is null");
            }
        }
        NvsStreamingContext context = NvsStreamingContext.getInstance();

        if (context == null) {
            context = EditorEngine.getInstance().initStreamContext();
            if (context == null) {
                LogUtils.e("failed to get streamingContext");
                throw new NullPointerException();
            }
        }

        NvsVideoResolution nvsVideoResolution = videoResolution;

        nvsVideoResolution.imagePAR = new NvsRational(1, 1);
        TimelineData.getInstance().setNvsRational(new NvsRational(25, 1));
        NvsAudioResolution nvsAudioResolution = new NvsAudioResolution();
        nvsAudioResolution.sampleRate = 44100;
        nvsAudioResolution.channelCount = 2;
//        Log.e(TAG, "newTimeline: "+ videoResolution.imageHeight+" "+ videoResolution.imageHeight );
        NvsTimeline timeline = context.createTimeline(nvsVideoResolution, TimelineData.getInstance().getNvsRational(), nvsAudioResolution);
        return timeline;
    }

    /**
     * SDK要求时间线的宽高需要满足：宽为4的倍数，高为2的倍数。
     * SDK requires that the width and height of the timeline should be: a multiple of 4 and a multiple of 2.
     *
     * @param resolution the resolution
     */
    public static void alignedResolution(NvsVideoResolution resolution) {
        if (resolution == null) {
            LogUtils.e("alignedResolution==null");
            return;
        }
        resolution.imageWidth = alignedData(resolution.imageWidth, 4);
        resolution.imageHeight = alignedData(resolution.imageHeight, 2);
    }

    /**
     * 整数对齐
     * Integer alignment
     *
     * @param data，源数据
     * @param num      对齐的数据
     * @return
     */
    private static int alignedData(int data, int num) {
        return data - data % num;
    }


    /**
     * 重新构建时间线
     * rebuild timeline
     *
     * @param timeline the timeline
     */
    public static void rebuildTimeline(NvsTimeline timeline) {
        if (timeline == null) {
            return;
        }
        NvsVideoResolution videoResolution = TimelineData.getInstance().getVideoResolution();
        if (videoResolution != null) {
            timeline.changeVideoSize(videoResolution.imageWidth, videoResolution.imageHeight);
        }

        List<MeicamVideoTrack> videoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(videoTrackList)) {
            LogUtils.e("no track data!!!");
            return;
        }
        int count = timeline.videoTrackCount();
        for (int index = count - 1; index >= 0; index--) {
            timeline.removeVideoTrack(index);
        }

        if (!fillTrack(timeline, videoTrackList)) {
            return;
        }
        setTimelineData(timeline);
    }

    /**
     * 根据视频轨道数据填充到时间线
     * Fill track boolean.
     *
     * @param timeline       the timeline
     * @param videoTrackList the video track list
     * @return the boolean
     */
    public static boolean fillTrack(NvsTimeline timeline, List<MeicamVideoTrack> videoTrackList) {
        for (MeicamVideoTrack meicamVideoTrack : videoTrackList) {
            NvsVideoTrack track = meicamVideoTrack.bindToTimeline(timeline);
            if (track == null) {
                LogUtils.e("failed to append video track");
                return false;
            }
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            if (CommonUtils.isEmpty(clipInfoList)) {
                LogUtils.e("no clip data!!!");
                continue;
            }
            for (int index = 0; index < clipInfoList.size(); index++) {
                MeicamVideoClip clipInfo = (MeicamVideoClip) clipInfoList.get(index);
                clipInfo.addToTimeline(track);
            }

            setTransition(timeline, meicamVideoTrack.getTransitionInfoList());
        }
        return true;
    }

    /**
     * 添加全部转场特效
     * <p>
     * Add all transition effects
     *
     * @param timeline          the timeline
     * @param meicamTransitions the meicam transitions
     * @return transition
     */
    public static boolean setTransition(NvsTimeline timeline, List<MeicamTransition> meicamTransitions) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return false;
        }

        if (meicamTransitions == null) {
            return false;
        }

        int videoClipCount = videoTrack.getClipCount();
        if (videoClipCount <= 1) {
            return false;
        }
        /*
         * 添加全部转场特效
         * Add all transition effects
         * */
        for (MeicamTransition transitionInfo : meicamTransitions) {
            if (transitionInfo == null) {
                continue;
            }
            transitionInfo.bindToTimeline(videoTrack);
        }

        return true;
    }

    /**
     * 清理所有的内建特效
     * <p>
     * Clear all build in transition boolean.
     *
     * @param timeline the timeline
     * @return the boolean
     */
    public static boolean clearAllBuildInTransition(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(BaseConstants.TRACK_INDEX_MAIN);
        if (videoTrack == null) {
            return false;
        }

        int videoClipCount = videoTrack.getClipCount();
        if (videoClipCount <= 1) {
            return false;
        }

        for (int i = 0; i < videoClipCount - 1; i++) {
            videoTrack.setBuiltinTransition(i, "");
        }
        return true;

    }

    /**
     * 时间线添加主题
     * <p>
     * Apply theme boolean.
     *
     * @param timeline the timeline
     * @return the boolean
     */
    public static boolean applyTheme(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }
        /*
         * 添加主题
         * Add Theme
         * */
        MeicamTheme meicamTheme = TimelineData.getInstance().getMeicamTheme();
        if (meicamTheme == null) {
            return false;
        }
        String themeId = meicamTheme.getThemePackageId();
        timeline.removeCurrentTheme();
        if (TextUtils.isEmpty(themeId)) {
            return false;
        }

        if (!timeline.applyTheme(themeId)) {
            Log.e(TAG, "failed to apply theme");
            return false;
        }
//        //如果有音频存在就屏蔽主题的背景音乐
//        if (TimelineData.getInstance().getMeicamAudioTrackList().size() == 0) {
//            timeline.setThemeMusicVolumeGain(1.0f, 1.0f);
//        } else {
//            timeline.setThemeMusicVolumeGain(0f, 0f);
//        }

        timeline.setThemeMusicVolumeGain(1.0f, 1.0f);

        /*
         * 新需求 添加主题删除所有增加的音频轨道
         * New requirements add theme remove all added audio tracks
         * */
        List<MeicamAudioTrack> meicamAudioTrackList = TimelineData.getInstance().getMeicamAudioTrackList();
        Iterator<MeicamAudioTrack> iterator = meicamAudioTrackList.iterator();
        while (iterator.hasNext()) {
            MeicamAudioTrack meicamAudioTrack = iterator.next();
            if (meicamAudioTrack == null) {
                continue;
            }
            NvsAudioTrack nvsAudioTrack = meicamAudioTrack.getObject();
            if (nvsAudioTrack == null) {
                continue;
            }
            timeline.removeAudioTrack(nvsAudioTrack.getIndex());
            iterator.remove();
        }
        return true;

    }

    /**
     * 时间线添加滤镜
     * <p></>
     * Build timeline filter boolean.
     *
     * @param timeline        the timeline
     * @param videoClipFxData the video clip fx data
     * @return the boolean
     */
    public static boolean buildTimelineFilter(NvsTimeline timeline, MeicamVideoFx videoClipFxData) {
        if (timeline == null) {
            return false;
        }
        MeicamVideoTrack meicamVideoTrack = TimelineData.getInstance().getMeicamVideoTrackList().get(BaseConstants.TRACK_INDEX_MAIN);
        if (meicamVideoTrack == null) {
            return false;
        }
        List<ClipInfo<?>> clipInfos = meicamVideoTrack.getClipInfoList();
        for (int index = 0; index < clipInfos.size(); index++) {
            MeicamVideoClip clipInfo = (MeicamVideoClip) clipInfos.get(index);
            MeicamVideoFx cloneVideoFx = videoClipFxData.clone();
            appendFilterFx(clipInfo, cloneVideoFx);
        }
        return true;
    }

    /**
     * 时间线添加滤镜
     * <p></p>
     * Build clip filter boolean.
     *
     * @param timeline the timeline
     * @return the boolean。时间线为空或没有滤镜数据则返回空
     * Returns false if timeline is empty or no filter data
     */
    public static boolean buildClipFilter(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (CommonUtils.isEmpty(meicamVideoTrackList)) {
            return false;
        }
        for (MeicamVideoTrack meicamVideoTrack : meicamVideoTrackList) {
            List<ClipInfo<?>> clipInfoList = meicamVideoTrack.getClipInfoList();
            for (ClipInfo clipInfo : clipInfoList) {
                appendFilterFx((MeicamVideoClip) clipInfo, ((MeicamVideoClip) clipInfo).getVideoFx(MeicamVideoFx.SUB_TYPE_CLIP_FILTER));
            }
        }
        return true;
    }


    /**
     * 片段添加滤镜
     * <p></p>
     * Append filter fx.
     *
     * @param clip            the clip
     * @param videoClipFxData the video clip fx data
     */
    public static void appendFilterFx(MeicamVideoClip clip, MeicamVideoFx videoClipFxData) {
        if (videoClipFxData == null) {
            return;
        }
        clip.removeVideoFx(videoClipFxData.getSubType());
        NvsVideoFx videoFx = videoClipFxData.bindToTimeline(clip.getObject());
        if (videoFx != null) {
            videoFx.setFilterMask(true);
        }
        clip.getVideoFxs().add(videoClipFxData);
    }



    /**
     * 通过视频片段路径获取需要的时间线分辨率信息
     * <P></P>
     * Gets video edit resolution by clip.
     *
     * @param path the path
     * @return the video edit resolution by clip
     */
    public static NvsVideoResolution getVideoEditResolutionByClip(String path) {

        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        int resolution = TIMELINE_RESOLUTION_VALUE;
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(path);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int streamRotation = avFileInfo.getVideoStreamRotation(0);
        int imageWidth = dimension.width;
        int imageHeight = dimension.height;

        if (streamRotation == 1 || streamRotation == 3) {
            imageWidth = dimension.height;
            imageHeight = dimension.width;
        }
        //按照视频的比例生成timeline的宽高 标准是720P （720*1280） 或者是1080P（1080*1920）
        float timelineRation = imageWidth * 1.0F / imageHeight;
        Point size = new Point();
        if (timelineRation > 1) {//宽视频 Wide video
            size.y = resolution;
            size.x = (int) (resolution * timelineRation);
        } else {//高视频 High video
            size.x = resolution;
            size.y = (int) (resolution * 1.0F / timelineRation);
        }

        videoEditRes.imageWidth = alignedData(size.x, 4);
        videoEditRes.imageHeight = alignedData(size.y, 2);

        LogUtils.d("getVideoEditResolution   ", videoEditRes.imageWidth + "     " + videoEditRes.imageHeight);
        return videoEditRes;

    }

    /**
     * 从时间线获取字幕和贴纸数据
     * <p></p>
     * Get the caption and sticker data from the timeline
     *
     * @param timeline the timeline
     */
    public static void loadTimelineToTemplate(NvsTimeline timeline) {
        if (timeline == null) {
            LogUtils.e("timeline == null");
            return;
        }
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        meicamVideoTrackList.clear();
        int trackCount = timeline.videoTrackCount();
        for (int i = 0; i < trackCount; i++) {
            NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(i);
            MeicamVideoTrack meicamVideoTrack = new MeicamVideoTrack(i);
            meicamVideoTrack.loadData(videoTrack);
            meicamVideoTrackList.add(meicamVideoTrack);
        }
        List<MeicamStickerCaptionTrack> captionTrackList = TimelineData.getInstance().getMeicamStickerCaptionTrackList();
        captionTrackList.clear();
        MeicamStickerCaptionTrack meicamStickerCaptionTrack = new MeicamStickerCaptionTrack(0);
        List<ClipInfo<?>> clipInfoList = meicamStickerCaptionTrack.getClipInfoList();
        loadTimelineCaptionToTemplate(timeline, timeline.getFirstCaption(), clipInfoList);
        TimelineData.getInstance().getMeicamStickerCaptionTrackList().add(meicamStickerCaptionTrack);
    }

    private static void loadTimelineCaptionToTemplate(NvsTimeline timeline, NvsTimelineCaption caption, List<ClipInfo<?>> clipInfoList) {
        if (caption != null) {
            MeicamCaptionClip meicamCaptionClip = new MeicamCaptionClip();
            meicamCaptionClip.loadData(caption);
            clipInfoList.add(meicamCaptionClip);
            NvsTimelineCaption nextCaption = timeline.getNextCaption(caption);
            loadTimelineCaptionToTemplate(timeline, nextCaption, clipInfoList);
        }
    }



}
