package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamAudioTrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 20:32
 */
public class LMeicamAudioTrack extends LTrackInfo implements Cloneable, Serializable {
    @SerializedName("transitions")
    private List<LMeicamTransition> mTransitionInfoList = new ArrayList<>();


    public LMeicamAudioTrack(int index) {
        super(CommonData.TRACK_AUDIO, index);
    }

    public List<LMeicamTransition> getTransitionInfoList() {
        return mTransitionInfoList;
    }

    public void setTransitionInfoList(List<LMeicamTransition> transitionInfoList) {
        mTransitionInfoList = transitionInfoList;
    }

    @Override
    public MeicamAudioTrack parseToTimelineData() {
        MeicamAudioTrack timeline = new MeicamAudioTrack(getIndex());
        setCommondData(timeline);
        for (LMeicamTransition meicamTransition : mTransitionInfoList) {
            timeline.getTransitionInfoList().add(meicamTransition.parseToTimelineData());
        }
        return timeline;
    }
}
