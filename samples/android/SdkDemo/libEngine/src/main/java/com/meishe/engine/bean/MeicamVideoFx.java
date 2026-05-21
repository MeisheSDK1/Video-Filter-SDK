package com.meishe.engine.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsArbitraryData;
import com.meicam.sdk.NvsMaskRegionInfo;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.base.constants.BaseConstants;
import com.meishe.base.utils.GsonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.engine.local.LMaskRegionInfoData;
import com.meishe.engine.local.LMeicamMaskRegionInfo;
import com.meishe.engine.local.LMeicamVideoFx;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.TYPE_BUILD_IN;
import static com.meishe.engine.bean.CommonData.TYPE_PACKAGE;
import static com.meishe.engine.bean.CommonData.TYPE_RAW_BUILTIN;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_BOOLEAN;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_FLOAT;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_OBJECT;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_STRING;

/**
 * Created by CaoZhiChao on 2020/7/3 21:42
 */
public class MeicamVideoFx extends NvsObject<NvsVideoFx> implements Cloneable, Serializable,
        TimelineDataToLocalAdapter<LMeicamVideoFx> {

    public final static String SUB_TYPE_CLIP_FILTER = "clipFilter";
    public final static String SUB_TYPE_TIMELINE_FILTER = "timelineFilter";

    public static final String ATTACHMENT_KEY_SUB_TYPE = "subType";
    protected String classType = "videoFx";
    protected int index;
    protected String type;
    protected String subType;

    protected String desc;
    //强度 intensity
    protected float intensity = 1;
    @SerializedName("fxParams")
    protected Map<String, MeicamFxParam<?>> mMeicamFxParam = new TreeMap<>();

    @SerializedName("maskRegionInfoData")
    public MaskRegionInfoData maskRegionInfoData;
    @SerializedName("meicamMaskRegionInfo")
    private MeicamMaskRegionInfo meicamMaskRegionInfo;

    public MeicamVideoFx() {
        super(null);
    }

    public MeicamVideoFx(int index, String type, String desc) {
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

    public float getIntensity() {
        return intensity;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    @Deprecated
    public void setMeicamMaskRegionInfo(MeicamMaskRegionInfo meicamMaskRegionInfo) {
        this.meicamMaskRegionInfo = meicamMaskRegionInfo;
    }

    @Deprecated
    public MeicamMaskRegionInfo getMeicamMaskRegionInfo() {
        return meicamMaskRegionInfo;
    }

    @Deprecated
    public MaskRegionInfoData getMaskRegionInfoData() {
        return maskRegionInfoData;
    }

    @Deprecated
    public void setMaskRegionInfoData(MaskRegionInfoData maskRegionInfoData) {
        this.maskRegionInfoData = maskRegionInfoData;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        NvsVideoFx object = getObject();
        if (object != null) {
            object.setFilterIntensity(intensity);
        }
    }

    public Map<String, MeicamFxParam<?>> getMeicamFxParam() {
        return mMeicamFxParam;
    }

    public void setMeicamFxParam(Map<String, MeicamFxParam<?>> meicamFxParam) {
        mMeicamFxParam = meicamFxParam;
    }

    public void setStringVal(String key, String value) {
        if (mMeicamFxParam.containsKey(key)) {
            mMeicamFxParam.remove(key);
        }
        MeicamFxParam<String> param = new MeicamFxParam<>(TYPE_STRING, key, value);
        mMeicamFxParam.put(param.getKey(), param);
        NvsVideoFx object = getObject();
        if (object != null) {
            object.setStringVal(key, value);
        }
    }

    public String getStringVal(String key) {
        MeicamFxParam meicamFxParam = mMeicamFxParam.get(key);
        if (meicamFxParam == null) {
            return null;
        }
        if (TYPE_STRING.equals(meicamFxParam.getType())) {
            return (String) meicamFxParam.getValue();
        }
        return null;
    }

    public float getFloatVal(String key) {
        MeicamFxParam meicamFxParam = mMeicamFxParam.get(key);
        if (meicamFxParam == null) {
            return -1;
        }
        if (TYPE_FLOAT.equals(meicamFxParam.getType())) {
            Object value = meicamFxParam.getValue();
            if (value instanceof Float) {
                return (float) value;
            } else if (value instanceof Double) {
                double resultD = (double) value;
                return (float) resultD;
            }
        }
        return -1;
    }


    public boolean getBooleanVal(String key) {
        MeicamFxParam meicamFxParam = mMeicamFxParam.get(key);
        if (meicamFxParam == null) {
            return false;
        }
        if (TYPE_BOOLEAN.equals(meicamFxParam.getType())) {
            Object value = meicamFxParam.getValue();
            if (value instanceof Boolean) {
                return (boolean) value;
            }
        }
        return false;
    }

    public void setBooleanVal(String key, boolean value) {
        if (mMeicamFxParam.containsKey(key)) {
            mMeicamFxParam.remove(key);
        }
        MeicamFxParam<Boolean> param = new MeicamFxParam<>(TYPE_BOOLEAN, key, value);
        mMeicamFxParam.put(param.getKey(), param);
        NvsVideoFx object = getObject();
        if (object != null) {
            object.setBooleanVal(key, value);
        }
    }

    public void setFloatVal(String key, float value) {
        if (Float.isNaN(value)) {
            //防止gson解析报错,IllegalArgumentException: JSON forbids NaN and infinities: NaN
            value = 0f;
        }
        if (mMeicamFxParam.containsKey(key)) {
            mMeicamFxParam.remove(key);
        }
        MeicamFxParam<Float> param = new MeicamFxParam<>(TYPE_FLOAT, key, value);
        mMeicamFxParam.put(param.getKey(), param);
        NvsVideoFx object = getObject();
        if (object != null) {
            object.setFloatVal(key, value);
        }
    }

    public <T> void setObjectVal(String key, T value) {
        if (mMeicamFxParam.containsKey(key)) {
            mMeicamFxParam.remove(key);
        }
        MeicamFxParam<T> param = new MeicamFxParam<>(TYPE_OBJECT, key, value);
        mMeicamFxParam.put(param.getKey(), param);
        NvsVideoFx object = getObject();
        if (object != null) {
            if (value instanceof NvsArbitraryData) {
                object.setArbDataVal(key, (NvsArbitraryData) value);
            }
        }
    }


    public NvsVideoFx bindToTimeline(NvsVideoClip videoClip) {
        if (videoClip == null) {
            return null;
        }
        NvsVideoFx videoFx = null;
        if (TYPE_BUILD_IN.equals(type)) {
            if (NvsConstants.KEY_MASK_GENERATOR.equals(desc) && BaseConstants.EnableRawFilterMaskRender) {
                videoFx = videoClip.appendRawBuiltinFx(desc);
            } else {
                videoFx = videoClip.appendBuiltinFx(desc);
            }
        } else if (TYPE_PACKAGE.equals(type)) {
            videoFx = videoClip.appendPackagedFx(desc);
        } else if (TYPE_RAW_BUILTIN.equals(type)) {
            videoFx = videoClip.appendRawBuiltinFx(desc);
        }
        if (videoFx != null) {
            setValue(videoFx);
            setObject(videoFx);
            videoFx.setFilterIntensity(getIntensity());
            videoFx.setAttachment(ATTACHMENT_KEY_SUB_TYPE, getSubType());
            if (null != meicamMaskRegionInfo) {
                videoFx.setBooleanVal(NvsConstants.KEY_MASK_KEEP_RGB, true);
                videoFx.setBooleanVal(NvsConstants.KEY_MASK_INVERSE_REGION, meicamMaskRegionInfo.isRevert());
                videoFx.setArbDataVal(NvsConstants.KEY_MASK_REGION_INFO, meicamMaskRegionInfo.getMaskRegionInfo());
                videoFx.setFloatVal(NvsConstants.KEY_MASK_FEATHER_WIDTH, meicamMaskRegionInfo.getFeatherWidth());
                setDesc(videoFx.getBuiltinVideoFxName());
            }
        }
        return videoFx;
    }


    private void setValue(NvsVideoFx videoFx) {
        Set<String> keySet = mMeicamFxParam.keySet();
        for (String key : keySet) {
            MeicamFxParam meicamFxParam = mMeicamFxParam.get(key);
            if (TYPE_STRING.equals(meicamFxParam.getType())) {
                videoFx.setStringVal(key, (String) meicamFxParam.getValue());
            } else if (TYPE_BOOLEAN.equals(meicamFxParam.getType())) {
                videoFx.setBooleanVal(key, (Boolean) meicamFxParam.getValue());
            } else if (TYPE_FLOAT.equals(meicamFxParam.getType())) {
                Object value = meicamFxParam.getValue();
                if (value instanceof Float) {
                    float floatValue = (float) value;
                    videoFx.setFloatVal(key, floatValue);
                } else if (value instanceof Double) {
                    videoFx.setFloatVal(key, (Double) value);
                }
            } else if (TYPE_OBJECT.equals(meicamFxParam.getType())) {
                Object value = meicamFxParam.getValue();
                if (value instanceof NvsArbitraryData) {
                    videoFx.setArbDataVal(key, (NvsArbitraryData) value);
                } else {
                    try {
                        Gson gson = GsonUtils.getGson();
                        String json = gson.toJson(value);
                        Type type = new TypeToken<NvsMaskRegionInfo>() {
                        }.getType();
                        NvsMaskRegionInfo result = gson.fromJson(json, type);
                        if (result != null) {
                            videoFx.setArbDataVal(key, result);
                        }
                    } catch (Exception e) {
                        LogUtils.e("error:" + e.getMessage());
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    public MeicamVideoFx clone() {
        return (MeicamVideoFx) DeepCopyUtil.deepClone(this);
    }


    @Override
    public LMeicamVideoFx parseToLocalData() {
        LMeicamVideoFx local = new LMeicamVideoFx();
        setCommonData(local);
        return local;
    }

    protected void setCommonData(LMeicamVideoFx local) {
        local.setIndex(getIndex());
        local.setType(getType());
        local.setSubType(getSubType());
        local.setDesc(getDesc());
        local.setIntensity(getIntensity());
        MaskRegionInfoData maskRegionInfoData = getMaskRegionInfoData();
        if (null != maskRegionInfoData) {
            LMaskRegionInfoData localData = maskRegionInfoData.parseToLocalData();
            local.setMaskRegionInfoData(localData);
        }
        MeicamMaskRegionInfo meicamMaskRegionInfo = getMeicamMaskRegionInfo();
        if (null != meicamMaskRegionInfo) {
            LMeicamMaskRegionInfo lMeicamMaskRegionInfo = meicamMaskRegionInfo.parseToLocalData();
            local.setLMeicamMaskRegionInfo(lMeicamMaskRegionInfo);
        }
        Set<String> keySet = mMeicamFxParam.keySet();
        for (String key : keySet) {
            local.getMeicamFxParam().add(mMeicamFxParam.get(key).parseToLocalData());
        }
    }

}
