package com.meishe.engine.local.background;

import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.background.MeicamStoryboardInfo;
import com.meishe.engine.local.LMeicamVideoFx;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author：yangtailin on 2020/6/28 13:43
 */
public class LMeicamStoryboardInfo extends LMeicamVideoFx implements Cloneable, Serializable {
    private static String TAG = "MeicamStoryboardInfo";
    private Map<String, Float> clipTrans = new HashMap<>();
    private String storyDesc;
    private String source;
    private String sourceDir;

    public LMeicamStoryboardInfo() {
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

    @Override
    public MeicamStoryboardInfo parseToTimelineData() {
        MeicamStoryboardInfo timelineData = new MeicamStoryboardInfo();
        setCommonData(timelineData);
        timelineData.setStoryDesc(getStoryDesc());
        timelineData.setSource(getSource());
        timelineData.setSourceDir(getSourceDir());
        Map<String, Float> localClipTrans = new HashMap<>();
        Set<String> keySet = clipTrans.keySet();
        for (String key : keySet) {
            localClipTrans.put(key, clipTrans.get(key));
        }
        timelineData.setClipTrans(localClipTrans);
        return timelineData;
    }

    @Override
    public String toString() {
        return "LMeicamStoryboardInfo{" +
                "clipTrans=" + clipTrans +
                ", storyDesc='" + storyDesc + '\'' +
                ", source='" + source + '\'' +
                ", sourceDir='" + sourceDir + '\'' +
                ", index=" + index +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", classType='" + classType + '\'' +
                ", desc='" + desc + '\'' +
                ", intensity=" + intensity +
                ", mMeicamFxParam=" + mMeicamFxParam +
                '}';
    }
}
