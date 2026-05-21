package com.meishe.engine.bean;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LTrackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/3 17:11
 */
public class TrackInfo<T> extends NvsObject<T> implements Cloneable, TimelineDataToLocalAdapter<LTrackInfo> {
    private String type = "base";
    private int index;
    /**
     * 是否展示轨道，移动端默认为true
     * Whether to display the track, the default is true on mobile
     */
    private boolean show = true;
    private float volume = 1;
    @SerializedName("clipInfos")
    private List<ClipInfo<?>> mClipInfoList = new ArrayList<>();

    public TrackInfo() {
        super(null);
    }

    public TrackInfo(String type, int index) {
        super(null);
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

    public List<ClipInfo<?>> getClipInfoList() {
        return mClipInfoList;
    }

    public void setClipInfoList(List<? extends ClipInfo<?>> clipInfoList) {
        if (mClipInfoList != null) {
            mClipInfoList.clear();
            mClipInfoList.addAll(clipInfoList);
        }
    }

    public void addClipInfoList(List<? extends ClipInfo<?>> clipInfoList, int index) {
        if (mClipInfoList != null) {
            mClipInfoList.addAll(index, clipInfoList);
        }
    }

    @Override
    public LTrackInfo parseToLocalData() {
        return null;
    }

    protected void setCommondData(LTrackInfo lTrackInfo) {
        for (ClipInfo clipInfo : mClipInfoList) {
            lTrackInfo.getClipInfoList().add(clipInfo.parseToLocalData());
        }
        lTrackInfo.setShow(show);
        lTrackInfo.setVolume(volume);
    }
}
