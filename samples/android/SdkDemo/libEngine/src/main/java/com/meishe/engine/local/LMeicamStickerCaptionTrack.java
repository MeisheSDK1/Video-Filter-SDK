package com.meishe.engine.local;

import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamStickerCaptionTrack;

/**
 * Created by CaoZhiChao on 2020/7/3 20:39
 */
public class LMeicamStickerCaptionTrack extends LTrackInfo implements Cloneable{
    public LMeicamStickerCaptionTrack(int index) {
        super(CommonData.TRACK_STICKER_CAPTION, index);
    }

    @Override
    public MeicamStickerCaptionTrack parseToTimelineData() {
        MeicamStickerCaptionTrack timelineData = new MeicamStickerCaptionTrack(getIndex());
        setCommondData(timelineData);
        return timelineData;
    }
}
