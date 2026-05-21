package com.meishe.engine.bean;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsAudioFx;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMeicamAudioFx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoZhiChao on 2020/7/4 11:20
 */
public class MeicamAudioFx extends NvsObject<NvsAudioFx> implements Cloneable, TimelineDataToLocalAdapter<LMeicamAudioFx> {
    int index;
    String type;
    String desc;
    @SerializedName("fxParams")
    List<MeicamFxParam> mMeicamFxParam = new ArrayList<>();
    public MeicamAudioFx() {
        super(null);
    }

    public MeicamAudioFx(int index, String type, String desc) {
        super(null);
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

    public List<MeicamFxParam> getMeicamFxParam() {
        return mMeicamFxParam;
    }

    public void setMeicamFxParam(List<MeicamFxParam> meicamFxParam) {
        mMeicamFxParam = meicamFxParam;
    }

    @Override
    public LMeicamAudioFx parseToLocalData() {
        LMeicamAudioFx local = new LMeicamAudioFx();
        local.setType(getType());
        local.setIndex(getIndex());
        local.setDesc(getDesc());
        for (MeicamFxParam meicamFxParam : mMeicamFxParam) {
            local.getMeicamFxParam().add(meicamFxParam.parseToLocalData());
        }
        return local;
    }
}
