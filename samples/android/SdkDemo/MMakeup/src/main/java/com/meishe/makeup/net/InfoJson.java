package com.meishe.makeup.net;

import com.meishe.makeup.makeup.MakeupParamContent;

import java.util.List;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2022/5/18.
 */
public class InfoJson {
    private String makeupId;
    private List<RecommendColor> makeupRecommendColors;
    private List<Translation> translation;
    private String className;
    private MakeupParamContent effectContent;

    public List<RecommendColor> getMakeupRecommendColors() {
        return makeupRecommendColors;
    }

    public String getClassName() {
        return className;
    }

    public String getMakeupId() {
        return makeupId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMakeupId(String makeupId) {
        this.makeupId = makeupId;
    }

    public void setMakeupRecommendColors(List<RecommendColor> makeupRecommendColors) {
        this.makeupRecommendColors = makeupRecommendColors;
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    public void setTranslation(List<Translation> translation) {
        this.translation = translation;
    }

    public MakeupParamContent getEffectContent() {
        return effectContent;
    }

    public void setEffectContent(MakeupParamContent effectContent) {
        this.effectContent = effectContent;
    }
}
