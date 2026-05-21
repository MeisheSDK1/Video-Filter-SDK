package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamVideoTrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 20:20
 */
public class LMeicamVideoTrack extends LTrackInfo implements Cloneable, Serializable{
    @SerializedName("transitions")
    private List<LMeicamTransition> mTransitionInfoList = new ArrayList<>();
    /**
     * 是否静音
     * Mute or not
     */
    @SerializedName("isMute")
    private boolean mIsMute = false;

    public LMeicamVideoTrack(int index) {
        super(CommonData.TRACK_VIDEO, index);
    }

    public List<LMeicamTransition> getTransitionInfoList() {
        return mTransitionInfoList;
    }

    public void setTransitionInfoList(List<LMeicamTransition> transitionInfoList) {
        mTransitionInfoList = transitionInfoList;
    }

    public boolean isMute() {
        return mIsMute;
    }

    public void setIsMute(boolean isMute) {
        this.mIsMute = isMute;
    }

    @Override
    public MeicamVideoTrack parseToTimelineData() {
        MeicamVideoTrack timelineData = new MeicamVideoTrack(getIndex());
        setCommondData(timelineData);
        timelineData.setIsMute(isMute());
        for (LMeicamTransition meicamTransition : mTransitionInfoList) {
            timelineData.getTransitionInfoList().add(meicamTransition.parseToTimelineData());
        }
        return timelineData;
    }
}
