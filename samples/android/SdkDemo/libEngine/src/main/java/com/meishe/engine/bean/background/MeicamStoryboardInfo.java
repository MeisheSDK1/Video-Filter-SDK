package com.meishe.engine.bean.background;

import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamFxParam;
import com.meishe.engine.bean.MeicamVideoFx;
import com.meishe.engine.local.background.LMeicamBackgroundStory;
import com.meishe.engine.local.background.LMeicamStoryboardInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.meishe.engine.bean.MeicamFxParam.TYPE_BOOLEAN;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_STRING;
import static com.meishe.engine.bean.MeicamFxParam.TYPE_STRING_OLD;

/**
 * author：yangtailin on 2020/6/28 13:43
 */
public class MeicamStoryboardInfo extends MeicamVideoFx implements Cloneable, Serializable {
    private static String TAG = "MeicamStoryboardInfo";

    public final static String SUB_TYPE_BACKGROUND = "background";
    public final static String SUB_TYPE_CROPPER = "cropper";
    public final static String SUB_TYPE_CROPPER_TRANSFROM = "cropper_transform";

    private Map<String, Float> clipTrans = new HashMap<>();
    private String storyDesc;
    private String source;
    private String sourceDir;

    public MeicamStoryboardInfo() {
        this.desc = "Storyboard";
        type = CommonData.TYPE_BUILD_IN;
        classType = "Storyboard";
    }

    public String getStoryDesc() {
        return storyDesc;
    }

    public void setStoryDesc(String backgroundStory) {
        this.storyDesc = backgroundStory;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public Map<String, Float> getClipTrans() {
        return clipTrans;
    }

    public void setClipTrans(Map<String, Float> clipTrans) {
        this.clipTrans = clipTrans;
    }

    public NvsVideoFx bindToTimelineByType(NvsVideoClip videoClip, String type) {
        if (videoClip == null) {
            return null;
        }

        NvsVideoFx nvsVideoFx = getStoryboardFx(videoClip, type);
        if (nvsVideoFx == null) {
            nvsVideoFx = videoClip.appendBuiltinFx(getDesc());
        }
        if (nvsVideoFx == null) {
           LogUtils.e( "bindToTimelineByType nvsVideoFx is null!");
            return null;
        }
        Set<String> keySet = mMeicamFxParam.keySet();
        for (String key : keySet) {
            MeicamFxParam meicamFxParam = mMeicamFxParam.get(key);
            if (TYPE_STRING.equals(meicamFxParam.getType()) || TYPE_STRING_OLD.equals(meicamFxParam.getType())) {
                nvsVideoFx.setStringVal(key, (String) meicamFxParam.getValue());
            } else if (TYPE_BOOLEAN.equals(meicamFxParam.getType())) {
                nvsVideoFx.setBooleanVal(key, (Boolean) meicamFxParam.getValue());
            }
        }
        if (nvsVideoFx != null) {
            nvsVideoFx.setAttachment(ATTACHMENT_KEY_SUB_TYPE, type);
            setIndex(nvsVideoFx.getIndex());
            setObject(nvsVideoFx);
        }
        return nvsVideoFx;
    }

    private NvsVideoFx getStoryboardFx(NvsVideoClip videoClip, String type) {
        int fxCount = videoClip.getFxCount();
        for (int index = 0; index < fxCount; index++) {
            NvsVideoFx clipFx = videoClip.getFxByIndex(index);
            Object attachment = clipFx.getAttachment(ATTACHMENT_KEY_SUB_TYPE);
            if (attachment != null && attachment instanceof String) {
                String subType = (String) attachment;
                if (subType.equals(type)) {
                    return clipFx;
                }
            }
        }
        return null;
    }

    @Override
    public LMeicamStoryboardInfo parseToLocalData() {
        LMeicamStoryboardInfo local = new LMeicamBackgroundStory();
        setCommonData(local);
        local.setStoryDesc(getStoryDesc());
        local.setSource(getSource());
        local.setSourceDir(getSourceDir());
        Map<String, Float> localClipTrans = new HashMap<>();
        Set<String> keySet = clipTrans.keySet();
        for (String key : keySet) {
            localClipTrans.put(key, clipTrans.get(key));
        }
        local.setClipTrans(localClipTrans);
        return local;
    }
}
