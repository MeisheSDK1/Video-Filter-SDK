package com.meishe.sdkdemo.glitter;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : malijia
 * @CreateDate : 2019/10/17.
 * @Description :闪光特效实体类。GlitterEffectBean
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class GlitterEffectBean {

    private String mEffectFilePath;
    private String mEffectIcon;
    private String mEffectName;

    public GlitterEffectBean(String effectFilePath, String effectIcon, String effectName) {
        this.mEffectFilePath = effectFilePath;
        this.mEffectIcon = effectIcon;
        this.mEffectName = effectName;
    }

    public String getEffectName() {
        return mEffectName;
    }

    public void setEffectName(String effectName) {
        this.mEffectName = effectName;
    }

    public String getEffectIcon() {
        return mEffectIcon;
    }

    public void setEffectIcon(String effectIcon) {
        this.mEffectIcon = effectIcon;
    }

    public String getEffectFilePath() {
        return mEffectFilePath;
    }

    public void setEffectFilePath(String effectFilePath) {
        this.mEffectFilePath = effectFilePath;
    }
}
