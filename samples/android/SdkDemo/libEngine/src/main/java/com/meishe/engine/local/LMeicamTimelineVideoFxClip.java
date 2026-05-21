package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamTimelineVideoFxClip;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.EFFECT_BUILTIN;
import static com.meishe.engine.bean.CommonData.EFFECT_PACKAGE;

/**
 * Created by CaoZhiChao on 2020/7/4 11:24
 */
public class LMeicamTimelineVideoFxClip extends LClipInfo implements Cloneable, Serializable {
    private int clipType;
    private String desc;
    private int clipSubType;
    private float intensity;
    private boolean isRegional = false;
    private boolean isIgnoreBackground = false;
    private boolean isInverseRegion = false;
    private int regionalFeatherWidth = 0;

    private String displayName;
    private String displayNamezhCN;

    @SerializedName("fxParams")
    private List<LMeicamFxParam> mMeicamFxParamList = new ArrayList<>();

    public LMeicamTimelineVideoFxClip() {
        super(CommonData.CLIP_TIMELINE_FX);
    }

    public LMeicamTimelineVideoFxClip(int clipType, String desc) {
        super(CommonData.CLIP_TIMELINE_FX);
        this.clipType = clipType;
        this.desc = desc;
    }

    public int getClipType() {
        return clipType;
    }

    public void setClipType(int clipType) {
        this.clipType = clipType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getClipSubType() {
        return clipSubType;
    }

    public void setClipSubType(int clipSubType) {
        this.clipSubType = clipSubType;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public boolean isRegional() {
        return isRegional;
    }

    public void setRegional(boolean regional) {
        isRegional = regional;
    }

    public boolean isIgnoreBackground() {
        return isIgnoreBackground;
    }

    public void setIgnoreBackground(boolean ignoreBackground) {
        isIgnoreBackground = ignoreBackground;
    }

    public boolean isInverseRegion() {
        return isInverseRegion;
    }

    public void setInverseRegion(boolean inverseRegion) {
        isInverseRegion = inverseRegion;
    }

    public int getRegionalFeatherWidth() {
        return regionalFeatherWidth;
    }

    public void setRegionalFeatherWidth(int regionalFeatherWidth) {
        this.regionalFeatherWidth = regionalFeatherWidth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNamezhCN() {
        return displayNamezhCN;
    }

    public void setDisplayNamezhCN(String displayNamezhCN) {
        this.displayNamezhCN = displayNamezhCN;
    }

    public List<LMeicamFxParam> getMeicamFxParamList() {
        return mMeicamFxParamList;
    }

    public void setMeicamFxParamList(List<LMeicamFxParam> meicamFxParamList) {
        mMeicamFxParamList = meicamFxParamList;
    }

    @NonNull
    @Override
    public LMeicamTimelineVideoFxClip clone() {
        return (LMeicamTimelineVideoFxClip) DeepCopyUtil.deepClone(this);
    }

    @Override
    public MeicamTimelineVideoFxClip parseToTimelineData() {
        MeicamTimelineVideoFxClip timelineData = new MeicamTimelineVideoFxClip();
        setCommonData(timelineData);
        int clipType = getClipType();
        if (0 == clipType) {
            clipType = EFFECT_BUILTIN;
        } else if (1 == clipType) {
            clipType = EFFECT_PACKAGE;
        }
        timelineData.setClipType(clipType);
        timelineData.setDesc(getDesc());
        timelineData.setClipSubType(getClipSubType());
        timelineData.setIntensity(getIntensity());
        timelineData.setRegional(isRegional());
        timelineData.setIgnoreBackground(isIgnoreBackground());
        timelineData.setInverseRegion(isInverseRegion());
        timelineData.setRegionalFeatherWidth(getRegionalFeatherWidth());
        timelineData.setDisplayName(getDisplayName());
        timelineData.setDisplayNamezhCN(getDisplayNamezhCN());
        for (LMeicamFxParam meicamFxParam : mMeicamFxParamList) {
            timelineData.getMeicamFxParamList().add(meicamFxParam.parseToTimelineData());
        }
        return timelineData;
    }
}
