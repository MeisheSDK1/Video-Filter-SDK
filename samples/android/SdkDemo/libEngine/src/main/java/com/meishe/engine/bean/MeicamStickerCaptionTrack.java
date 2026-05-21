package com.meishe.engine.bean;

import com.meishe.engine.local.LMeicamStickerCaptionTrack;

/**
 * Created by CaoZhiChao on 2020/7/3 20:39
 */
public class MeicamStickerCaptionTrack extends TrackInfo<Object> implements Cloneable{
    public MeicamStickerCaptionTrack(int index) {
        super(CommonData.TRACK_STICKER_CAPTION, index);
    }

    @Override
    public LMeicamStickerCaptionTrack parseToLocalData() {
        LMeicamStickerCaptionTrack local = new LMeicamStickerCaptionTrack(getIndex());
        setCommondData(local);
        return local;
    }
}
