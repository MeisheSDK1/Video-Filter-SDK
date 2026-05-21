package com.meishe.engine.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meishe.engine.adapter.jsonadapter.LMeicamAdjsutDataAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamAudioClipAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamAudioTrackAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamBackgroundAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamCaptionClipAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamCompoundCaptionClipAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamFxParamAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamStickerCaptionTrackAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamStickerClipAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamStoryboardInfoAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamTimelineVideoFxAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamTimelineVideoFxClipAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamTimelineVideoFxTrackAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamVideoClipAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamVideoFxAdapter;
import com.meishe.engine.adapter.jsonadapter.LMeicamVideoTrackAdapter;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.local.LClipInfo;
import com.meishe.engine.local.LMeicamAdjustData;
import com.meishe.engine.local.LMeicamAudioClip;
import com.meishe.engine.local.LMeicamAudioTrack;
import com.meishe.engine.local.LMeicamCaptionClip;
import com.meishe.engine.local.LMeicamCompoundCaptionClip;
import com.meishe.engine.local.LMeicamFxParam;
import com.meishe.engine.local.LMeicamStickerCaptionTrack;
import com.meishe.engine.local.LMeicamStickerClip;
import com.meishe.engine.local.LMeicamTimelineVideoFx;
import com.meishe.engine.local.LMeicamTimelineVideoFxClip;
import com.meishe.engine.local.LMeicamTimelineVideoFxTrack;
import com.meishe.engine.local.LMeicamVideoClip;
import com.meishe.engine.local.LMeicamVideoFx;
import com.meishe.engine.local.LMeicamVideoTrack;
import com.meishe.engine.local.LTrackInfo;
import com.meishe.engine.local.background.LMeicamBackgroundStory;
import com.meishe.engine.local.background.LMeicamStoryboardInfo;

/**
 * author：yangtailin on 2020/7/8 17:08
 * Gson上下文类
 * Gson context class
 */
public class LGsonContext {
    private volatile static LGsonContext sGsonContext;
    private Gson mGson;
    private Gson mCommonGson;
    private Gson mClipGson;

    private LGsonContext() {
    }

    public static LGsonContext getInstance() {
        if (sGsonContext == null) {
            synchronized (LGsonContext.class) {
                if (sGsonContext == null) {
                    sGsonContext = new LGsonContext();
                }
            }
        }
        return sGsonContext;
    }

    /**
     * Gets gson.
     * 获取gson解析数据
     * @return the gson
     */
    public Gson getGson() {
        if (mGson == null) {
            LRuntimeTypeAdapterFactory<LTrackInfo> trackFactory = LRuntimeTypeAdapterFactory.of(LTrackInfo.class, "type")
                    .registerSubType(new LMeicamAudioTrackAdapter(), LMeicamAudioTrack.class, CommonData.TRACK_AUDIO)
                    .registerSubType(new LMeicamVideoTrackAdapter(), LMeicamVideoTrack.class, CommonData.TRACK_VIDEO)
                    .registerSubType(new LMeicamStickerCaptionTrackAdapter(), LMeicamStickerCaptionTrack.class, CommonData.TRACK_STICKER_CAPTION)
                    .registerSubType(new LMeicamTimelineVideoFxTrackAdapter(), LMeicamTimelineVideoFxTrack.class, CommonData.TRACK_TIMELINE_FX);

            LRuntimeTypeAdapterFactory<LClipInfo> clipFactory = LRuntimeTypeAdapterFactory.of(LClipInfo.class, "type")
                    .registerSubType(new LMeicamAudioClipAdapter(), LMeicamAudioClip.class, CommonData.CLIP_AUDIO)
                    .registerSubType(new LMeicamVideoClipAdapter(), LMeicamVideoClip.class, CommonData.CLIP_VIDEO)
                    .registerSubType(new LMeicamCaptionClipAdapter(), LMeicamCaptionClip.class, CommonData.CLIP_CAPTION)
                    .registerSubType(new LMeicamCompoundCaptionClipAdapter(), LMeicamCompoundCaptionClip.class, CommonData.CLIP_COMPOUND_CAPTION)
                    .registerSubType(new LMeicamStickerClipAdapter(), LMeicamStickerClip.class, CommonData.CLIP_STICKER)
                    .registerSubType(new LMeicamTimelineVideoFxClipAdapter(), LMeicamTimelineVideoFxClip.class, CommonData.CLIP_TIMELINE_FX);

            LRuntimeTypeAdapterFactory<LMeicamStoryboardInfo> storyboardFactory = LRuntimeTypeAdapterFactory.of(LMeicamStoryboardInfo.class, "classType")
                    .registerSubType(new LMeicamStoryboardInfoAdapter(), LMeicamStoryboardInfo.class, "Storyboard")
                    .registerSubType(new LMeicamBackgroundAdapter(), LMeicamBackgroundStory.class, "BackgroundStory");

            LRuntimeCommonTypeAdapterFactory<LMeicamAdjustData> adjustDataFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamAdjustData.class)
                            .registerAdapter(new LMeicamAdjsutDataAdapter());
           LRuntimeCommonTypeAdapterFactory<LMeicamFxParam> fxParamFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamFxParam.class)
                            .registerAdapter(new LMeicamFxParamAdapter());
           LRuntimeCommonTypeAdapterFactory<LMeicamTimelineVideoFx> timelineFxFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamTimelineVideoFx.class)
                            .registerAdapter(new LMeicamTimelineVideoFxAdapter());
           LRuntimeCommonTypeAdapterFactory<LMeicamVideoFx> videoFxFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamVideoFx.class)
                            .registerAdapter(new LMeicamVideoFxAdapter());
            mGson = new GsonBuilder()
                    .registerTypeAdapterFactory(trackFactory)
                    .registerTypeAdapterFactory(clipFactory)
                    .registerTypeAdapterFactory(storyboardFactory)
                    .registerTypeAdapterFactory(adjustDataFactory)
                    .registerTypeAdapterFactory(fxParamFactory)
                    .registerTypeAdapterFactory(timelineFxFactory)
                    .registerTypeAdapterFactory(videoFxFactory)
                    .serializeSpecialFloatingPointValues()
                    .create();
        }
        return mGson;
    }

    /**
     * Gets clip gson.
     * 获取裁剪的gson解析数据
     * @return the clip gson
     */
    public Gson getClipGson() {
        if (mClipGson == null) {
            LRuntimeTypeAdapterFactory<LMeicamStoryboardInfo> storyboardFactory = LRuntimeTypeAdapterFactory.of(LMeicamStoryboardInfo.class, "classType")
                    //.registerSubType(new LMeicamVideoFxAdapter(), LMeicamVideoFx.class, "videoFx")
                    //.registerSubType(new LMeicamTimelineVideoFxAdapter(), LMeicamTimelineVideoFx.class, "timelineVideoFx")
                    .registerSubType(new LMeicamStoryboardInfoAdapter(), LMeicamStoryboardInfo.class, "Storyboard")
                    .registerSubType(new LMeicamBackgroundAdapter(), LMeicamBackgroundStory.class, "BackgroundStory");
            LRuntimeCommonTypeAdapterFactory<LMeicamAdjustData> adjustDataFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamAdjustData.class)
                            .registerAdapter(new LMeicamAdjsutDataAdapter());

            LRuntimeCommonTypeAdapterFactory<LMeicamFxParam> fxParamFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamFxParam.class)
                            .registerAdapter(new LMeicamFxParamAdapter());
            LRuntimeCommonTypeAdapterFactory<LMeicamTimelineVideoFx> timelineFxFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamTimelineVideoFx.class)
                            .registerAdapter(new LMeicamTimelineVideoFxAdapter());
            LRuntimeCommonTypeAdapterFactory<LMeicamVideoFx> videoFxFactory =
                    LRuntimeCommonTypeAdapterFactory.of(LMeicamVideoFx.class)
                            .registerAdapter(new LMeicamVideoFxAdapter());
            mClipGson = new GsonBuilder()
                    .registerTypeAdapterFactory(storyboardFactory)
                    .registerTypeAdapterFactory(adjustDataFactory)
                    .registerTypeAdapterFactory(fxParamFactory)
                    .registerTypeAdapterFactory(timelineFxFactory)
                    .registerTypeAdapterFactory(videoFxFactory)
                    .serializeSpecialFloatingPointValues()
                    .create();
        }
        return mClipGson;
    }

    /**
     * Gets common gson.
     * 获取常见gson
     * @return the common gson
     */
    public Gson getCommonGson() {
        if (mCommonGson == null) {
            mCommonGson = new Gson();
        }
        return mCommonGson;
    }
}
