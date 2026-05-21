package com.meishe.engine.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meishe.engine.bean.ClipInfo;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamAudioClip;
import com.meishe.engine.bean.MeicamAudioTrack;
import com.meishe.engine.bean.MeicamCaptionClip;
import com.meishe.engine.bean.MeicamCompoundCaptionClip;
import com.meishe.engine.bean.MeicamStickerCaptionTrack;
import com.meishe.engine.bean.MeicamStickerClip;
import com.meishe.engine.bean.MeicamTimelineVideoFxClip;
import com.meishe.engine.bean.MeicamTimelineVideoFxTrack;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.bean.TrackInfo;
import com.meishe.engine.bean.background.MeicamBackgroundStory;
import com.meishe.engine.bean.background.MeicamStoryboardInfo;

/**
 * author：yangtailin on 2020/7/8 17:08
 * 使用Gson的解析方法类
 * Use Gson's parsing method class
 */
public class GsonContext {
    private volatile static GsonContext sGsonContext;
    private Gson mGson;
    private GsonContext(){
    }

    public static GsonContext getInstance(){
        if (sGsonContext == null) {
            synchronized (GsonContext.class) {
                if (sGsonContext == null) {
                    sGsonContext = new GsonContext();
                }
            }
        }
        return sGsonContext;
    }

    public Gson getGson() {
        if (mGson == null) {
            RuntimeTypeAdapterFactory<TrackInfo> trackFactory = RuntimeTypeAdapterFactory.of(TrackInfo.class,"base")
                    .registerSubtype(MeicamAudioTrack.class, CommonData.TRACK_AUDIO)
                    .registerSubtype(MeicamVideoTrack.class, CommonData.TRACK_VIDEO)
                    .registerSubtype(MeicamStickerCaptionTrack.class, CommonData.TRACK_STICKER_CAPTION)
                    .registerSubtype(MeicamTimelineVideoFxTrack.class, CommonData.TRACK_TIMELINE_FX);
            RuntimeTypeAdapterFactory<ClipInfo> clipFactory = RuntimeTypeAdapterFactory.of(ClipInfo.class, "base")
                    .registerSubtype(MeicamAudioClip.class, CommonData.CLIP_AUDIO)
                    .registerSubtype(MeicamVideoClip.class, CommonData.CLIP_VIDEO)
                    .registerSubtype(MeicamCaptionClip.class, CommonData.CLIP_CAPTION)
                    .registerSubtype(MeicamCompoundCaptionClip.class, CommonData.CLIP_COMPOUND_CAPTION)
                    .registerSubtype(MeicamStickerClip.class, CommonData.CLIP_STICKER)
                    .registerSubtype(MeicamTimelineVideoFxClip.class, CommonData.CLIP_TIMELINE_FX);
            RuntimeTypeAdapterFactory<MeicamStoryboardInfo> storyboardFactory = RuntimeTypeAdapterFactory.of(MeicamStoryboardInfo.class, "Storyboard")
                    .registerSubtype(MeicamBackgroundStory.class, "BackgroundStory")
                    .registerSubtype(MeicamStoryboardInfo.class, "Storyboard");
            mGson = new GsonBuilder()
                    .registerTypeAdapterFactory(trackFactory)
                    .registerTypeAdapterFactory(clipFactory)
                    .registerTypeAdapterFactory(storyboardFactory)
                    .serializeSpecialFloatingPointValues()
                    .create();
        }
        return mGson;
    }
}
