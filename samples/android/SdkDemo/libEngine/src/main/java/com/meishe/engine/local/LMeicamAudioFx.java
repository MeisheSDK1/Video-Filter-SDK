package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamAudioFx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/4 11:20
 */
public class LMeicamAudioFx extends LNvsObject implements Cloneable, LocalToTimelineDataAdapter<MeicamAudioFx> {
    int index;
    String type;
    String desc;
    @SerializedName("fxParams")
    List<LMeicamFxParam> mMeicamFxParam = new ArrayList<>();
    public LMeicamAudioFx() {
    }

    public LMeicamAudioFx(int index, String type, String desc) {
        this.index = index;
        this.type = type;
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<LMeicamFxParam> getMeicamFxParam() {
        return mMeicamFxParam;
    }

    public void setMeicamFxParam(List<LMeicamFxParam> meicamFxParam) {
        mMeicamFxParam = meicamFxParam;
    }

    @Override
    public MeicamAudioFx parseToTimelineData() {
        MeicamAudioFx timelineData = new MeicamAudioFx();
        timelineData.setType(getType());
        timelineData.setIndex(getIndex());
        timelineData.setDesc(getDesc());
        for (LMeicamFxParam meicamFxParam : mMeicamFxParam) {
            timelineData.getMeicamFxParam().add(meicamFxParam.parseToTimelineData());
        }
        return timelineData;
    }
}
