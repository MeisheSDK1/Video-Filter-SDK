package com.meishe.engine.bean;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsTimeline;
import com.meishe.engine.local.LMeicamAudioTrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 20:32
 */
public class MeicamAudioTrack extends TrackInfo<NvsAudioTrack> implements Cloneable, Serializable{
    @SerializedName("transitions")
    private List<MeicamTransition> mTransitionInfoList = new ArrayList<>();


    public MeicamAudioTrack(int index) {
        super(CommonData.TRACK_AUDIO, index);
    }

    public List<MeicamTransition> getTransitionInfoList() {
        return mTransitionInfoList;
    }

    public void setTransitionInfoList(List<MeicamTransition> transitionInfoList) {
        mTransitionInfoList = transitionInfoList;
    }


    public NvsAudioTrack bindToTimeline(NvsTimeline timeline) {
        if (timeline == null) {
            return null;
        }
        NvsAudioTrack track = timeline.appendAudioTrack();
        setObject(track);
        return track;
    }

    @Override
    public LMeicamAudioTrack parseToLocalData() {
        LMeicamAudioTrack local = new LMeicamAudioTrack(getIndex());
        setCommondData(local);
        for (MeicamTransition meicamTransition : mTransitionInfoList) {
            local.getTransitionInfoList().add(meicamTransition.parseToLocalData());
        }
        return local;
    }
}
