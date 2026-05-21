package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.TrackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 17:11
 */
public class LTrackInfo extends LNvsObject implements Cloneable, LocalToTimelineDataAdapter<TrackInfo> {
    private String type = "base";
    private int index;
    private boolean show = true;
    private float volume = 1;
    @SerializedName("clipInfos")
    private List<LClipInfo> mClipInfoList = new ArrayList<>();

    public LTrackInfo() {
    }

    public LTrackInfo(String type, int index) {
        this.type = type;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public List<LClipInfo> getClipInfoList() {
        return mClipInfoList;
    }

    public void setClipInfoList(List<? extends LClipInfo> clipInfoList) {
        if (mClipInfoList != null) {
            mClipInfoList.clear();
            mClipInfoList.addAll(clipInfoList);
        }
    }

    public void addClipInfoList(List<? extends LClipInfo> clipInfoList, int index) {
        if (mClipInfoList != null) {
            mClipInfoList.addAll(index, clipInfoList);
        }
    }

    @Override
    public TrackInfo parseToTimelineData() {
        return null;
    }

    protected void setCommondData(TrackInfo trackInfo) {
        List<LClipInfo> clipInfoList = getClipInfoList();
        for (LClipInfo clipInfo : clipInfoList) {
            if (clipInfo != null) {
                trackInfo.getClipInfoList().add(clipInfo.parseToTimelineData());
            }
        }
        trackInfo.setShow(show);
        trackInfo.setVolume(volume);
    }
}
