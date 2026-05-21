package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MaskRegionInfoData;
import com.meishe.engine.bean.MeicamFxParam;
import com.meishe.engine.bean.MeicamMaskRegionInfo;
import com.meishe.engine.bean.MeicamVideoFx;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.TYPE_BUILD_IN;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_BOOLEAN;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_FLOAT;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_OBJECT;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_STRING;

/**
 * Created by CaoZhiChao on 2020/7/3 21:42
 */
public class LMeicamVideoFx extends LNvsObject implements Cloneable, Serializable, LocalToTimelineDataAdapter<MeicamVideoFx> {
    protected int index;
    protected String type;
    protected String subType;
    protected String classType = "videoFx";

    protected String desc;
    //强度 intensity
    protected float intensity = 1;
    @SerializedName("fxParams")
    protected List<LMeicamFxParam> mMeicamFxParam = new ArrayList<>();

    @SerializedName("maskRegionInfoData")
    public LMaskRegionInfoData maskRegionInfoData;

    @SerializedName("meicamMaskRegionInfo")
    public LMeicamMaskRegionInfo meicamMaskRegionInfo;

    public LMeicamVideoFx() {
        classType = "videoFx";
    }

    public LMeicamVideoFx(int index, String type, String desc) {
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

    public float getIntensity() {
        return intensity;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public List<LMeicamFxParam> getMeicamFxParam() {
        return mMeicamFxParam;
    }

    public void setMeicamFxParam(List<LMeicamFxParam> meicamFxParam) {
        mMeicamFxParam = meicamFxParam;
    }

    public void setStringVal(String key, String value) {
        LMeicamFxParam<String> param = new LMeicamFxParam<>(TYPE_STRING, key, value);
        mMeicamFxParam.add(param);
    }

    public String getStringVal(String key) {
        return getVal(String.class,TYPE_STRING,key);
    }

    private <T extends Object> T getVal(Class<T> clazz, String sysType, String key){
        T t = null;
        for (LMeicamFxParam meicamFxParam : mMeicamFxParam) {
            if (sysType.equals(meicamFxParam.getType()) && key.equals(meicamFxParam.getKey())){
                t = (T) meicamFxParam.getValue();
            }
        }
        return t;
    }

    public float getFloatVal(String key) {
        return getVal(Float.class,TYPE_FLOAT,key);
    }

    public void setBooleanVal(String key, boolean value) {
        LMeicamFxParam<Boolean> param = new LMeicamFxParam<>(TYPE_BOOLEAN, key, value);
        mMeicamFxParam.add(param);
    }

    public void setFloatVal(String key, float value) {
        LMeicamFxParam<Float> param = new LMeicamFxParam<>(TYPE_FLOAT, key, value);
        mMeicamFxParam.add(param);
    }

    public <T> void setObjectVal(String key, T value) {
        LMeicamFxParam<T> param = new LMeicamFxParam<>(TYPE_OBJECT, key, value);
        mMeicamFxParam.add(param);
    }

    public LMeicamMaskRegionInfo getLMeicamMaskRegionInfo() {
        return meicamMaskRegionInfo;
    }

    public void setLMeicamMaskRegionInfo(LMeicamMaskRegionInfo meicamMaskRegionInfo) {
        this.meicamMaskRegionInfo = meicamMaskRegionInfo;
    }

    public LMaskRegionInfoData getMaskRegionInfoData() {
        return maskRegionInfoData;
    }

    public void setMaskRegionInfoData(LMaskRegionInfoData maskRegionInfoData) {
        this.maskRegionInfoData = maskRegionInfoData;
    }

    @NonNull
    @Override
    public LMeicamVideoFx clone() {
        return (LMeicamVideoFx) DeepCopyUtil.deepClone(this);
    }


    @Override
    public MeicamVideoFx parseToTimelineData() {
        MeicamVideoFx timelineData = new MeicamVideoFx();
        timelineData.setIndex(getIndex());
        timelineData.setType(getType());
        timelineData.setSubType(getSubType());
        timelineData.setDesc(getDesc());
        timelineData.setIntensity(getIntensity());
        LMaskRegionInfoData maskRegionInfoData = getMaskRegionInfoData();
        if(null != maskRegionInfoData){
            timelineData.setMaskRegionInfoData(maskRegionInfoData.parseToLocalData());
        }
        LMeicamMaskRegionInfo lMeicamMaskRegionInfo = getLMeicamMaskRegionInfo();
        if(null != lMeicamMaskRegionInfo){
            timelineData.setMeicamMaskRegionInfo(lMeicamMaskRegionInfo.parseToTimelineData());
        }
        for (LMeicamFxParam fxParam : mMeicamFxParam) {
            MeicamFxParam meicamFxParam = fxParam.parseToTimelineData();
            timelineData.getMeicamFxParam().put(meicamFxParam.getKey(), meicamFxParam);
        }
        return timelineData;
    }

    protected void setCommonData(MeicamVideoFx data) {
        data.setIndex(getIndex());
        String type = getType();
        if ("0".equals(type)) {
            type = TYPE_BUILD_IN;
        } else if ("1".equals(type)){
            type = CommonData.TYPE_PACKAGE;
        }
        data.setType(type);
        data.setSubType(getSubType());
        data.setDesc(getDesc());
        data.setIntensity(getIntensity());
        LMaskRegionInfoData maskRegionInfoData =getMaskRegionInfoData() ;
        if(null != maskRegionInfoData){
            MaskRegionInfoData maskData = maskRegionInfoData.parseToLocalData();
            data.setMaskRegionInfoData(maskData);
        }
        LMeicamMaskRegionInfo maskRegionInfo = getLMeicamMaskRegionInfo();
        if(null != maskRegionInfo){
            MeicamMaskRegionInfo meicamMaskRegionInfo = maskRegionInfo.parseToTimelineData();
            data.setMeicamMaskRegionInfo(meicamMaskRegionInfo);
        }
        for (LMeicamFxParam fxParam : mMeicamFxParam) {
            MeicamFxParam meicamFxParam = fxParam.parseToTimelineData();
            if (meicamFxParam.getKey() != null) {
                data.getMeicamFxParam().put(meicamFxParam.getKey(), meicamFxParam);
            }
        }
    }
}
