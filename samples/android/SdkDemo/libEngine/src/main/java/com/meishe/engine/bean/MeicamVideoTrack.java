package com.meishe.engine.bean;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.engine.local.LMeicamVideoTrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 20:20
 */
public class MeicamVideoTrack extends TrackInfo<NvsVideoTrack> implements Cloneable, Serializable {
    @SerializedName("transitions")
    private List<MeicamTransition> mTransitionInfoList = new ArrayList<>();
    /**
     * 是否静音
     * Mute or not
     */
    @SerializedName("isMute")
    private boolean mIsMute = false;

    public MeicamVideoTrack(int index) {
        super(CommonData.TRACK_VIDEO, index);
    }

    public List<MeicamTransition> getTransitionInfoList() {
        return mTransitionInfoList;
    }

    public void setTransitionInfoList(List<MeicamTransition> transitionInfoList) {
        mTransitionInfoList = transitionInfoList;
    }

    public boolean isMute() {
        return mIsMute;
    }

    public void setIsMute(boolean isMute) {
        NvsVideoTrack object = getObject();
        if (object != null) {
            if (isMute) {
                object.setVolumeGain(0, 0);
            } else {
                object.setVolumeGain(1, 1);
            }
        }
        this.mIsMute = isMute;
    }

    public NvsVideoTrack bindToTimeline(NvsTimeline nvsTimeline) {
        if (nvsTimeline == null) {
            return null;
        }
        NvsVideoTrack nvsVideoTrack = nvsTimeline.appendVideoTrack();
        if (isMute()) {
            nvsVideoTrack.setVolumeGain(0, 0);
        } else {
            nvsVideoTrack.setVolumeGain(1, 1);
        }
        setObject(nvsVideoTrack);
        return nvsVideoTrack;
    }

    @Override
    public LMeicamVideoTrack parseToLocalData() {
        LMeicamVideoTrack local = new LMeicamVideoTrack(getIndex());
        setCommondData(local);
        local.setIsMute(isMute());
        for (MeicamTransition meicamTransition : mTransitionInfoList) {
            local.getTransitionInfoList().add(meicamTransition.parseToLocalData());
        }
        return local;
    }
}
