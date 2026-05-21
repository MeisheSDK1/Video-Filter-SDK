package com.meishe.engine.bean;

import android.graphics.PointF;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meishe.engine.local.LMeicamTimelineVideoFxClip;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.EFFECT_BUILTIN;

/**
 * Created by CaoZhiChao on 2020/7/4 11:24
 */
public class MeicamTimelineVideoFxClip extends ClipInfo<NvsTimelineVideoFx> implements Serializable {
    /**
     * 取值：0， 内建，1、包
     * The value can be 0, built-in, or 1, package
     */
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

    private transient List<PointF> list;

    @SerializedName("fxParams")
    private List<MeicamFxParam> mMeicamFxParamList = new ArrayList<>();

    public MeicamTimelineVideoFxClip() {
        super(CommonData.CLIP_TIMELINE_FX);
    }

    public MeicamTimelineVideoFxClip(int clipType, String desc) {
        super(CommonData.CLIP_TIMELINE_FX);
        this.clipType = clipType;
        this.desc = desc;
    }

    public List<PointF> getList() {
        return list;
    }

    public void setList(List<PointF> list) {
        this.list = list;
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

    public List<MeicamFxParam> getMeicamFxParamList() {
        return mMeicamFxParamList;
    }

    public void setMeicamFxParamList(List<MeicamFxParam> meicamFxParamList) {
        mMeicamFxParamList = meicamFxParamList;
    }

    public NvsTimelineVideoFx bindToTimeline(NvsTimeline timeline) {
        if (timeline == null) {
            return null;
        }
        NvsTimelineVideoFx nvsTimelineVideoFx;
        if (EFFECT_BUILTIN == clipType) {
            nvsTimelineVideoFx = timeline.addBuiltinTimelineVideoFx(getInPoint(), getOutPoint() - getInPoint(), desc);
        } else {
            nvsTimelineVideoFx = timeline.addPackagedTimelineVideoFx(getInPoint(), getOutPoint() - getInPoint(), desc);
        }

        setObject(nvsTimelineVideoFx);
        return nvsTimelineVideoFx;
    }

    @Override
    public void loadData(NvsTimelineVideoFx effect) {
        if (effect == null) {
            return;
        }
        setObject(effect);
        setInPoint(effect.getInPoint());
        setOutPoint(effect.getOutPoint());
        if (EFFECT_BUILTIN == clipType) {
            setDesc(effect.getBuiltinTimelineVideoFxName());
        } else {
            setDesc(effect.getTimelineVideoFxPackageId());
        }
    }

    @Override
    @NonNull
    public MeicamTimelineVideoFxClip clone() {
        return (MeicamTimelineVideoFxClip) DeepCopyUtil.deepClone(this);
    }

    public void setPointList(NvsLiveWindowExt mLiveWindow) {
        float[] point = new float[8];
        for (int i = 0; i < mMeicamFxParamList.size(); i++) {
            MeicamFxParam meicamFxParam = mMeicamFxParamList.get(i);
            if (meicamFxParam.type.equals("float[]") && meicamFxParam.key.equals("Unit Size")) {
                Object value = meicamFxParam.getValue();
                if (value instanceof ArrayList) {
                    List<Double> valueList = new ArrayList<>();
                    ArrayList data = (ArrayList) value;
                    for (Object object : data) {
                        if (object instanceof Double) {
                            valueList.add((Double) object);
                        }
                    }
                    for (int j = 0; j < valueList.size(); j++) {
                        point[j] = Float.parseFloat(valueList.get(j).toString());
                    }
                } else {
                    point = (float[]) value;
                }
                break;
            }
        }
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        PointF leftTop = new PointF(point[0], point[1]);
        PointF leftBottom = new PointF(point[2], point[3]);
        PointF rightBottom = new PointF(point[4], point[5]);
        PointF rightTop = new PointF(point[6], point[7]);
        list.add(mLiveWindow.mapNormalizedToView(leftTop));
        list.add(mLiveWindow.mapNormalizedToView(leftBottom));
        list.add(mLiveWindow.mapNormalizedToView(rightBottom));
        list.add(mLiveWindow.mapNormalizedToView(rightTop));
    }

    @Override
    public LMeicamTimelineVideoFxClip parseToLocalData() {
        LMeicamTimelineVideoFxClip local = new LMeicamTimelineVideoFxClip();
        setCommonData(local);
        local.setClipType(getClipType());
        local.setDesc(getDesc());
        local.setClipSubType(getClipSubType());
        local.setIntensity(getIntensity());
        local.setRegional(isRegional());
        local.setIgnoreBackground(isIgnoreBackground());
        local.setInverseRegion(isInverseRegion());
        local.setRegionalFeatherWidth(getRegionalFeatherWidth());
        local.setDisplayName(getDisplayName());
        local.setDisplayNamezhCN(getDisplayNamezhCN());
        for (MeicamFxParam meicamFxParam : mMeicamFxParamList) {
            local.getMeicamFxParamList().add(meicamFxParam.parseToLocalData());
        }
        return local;
    }
}
