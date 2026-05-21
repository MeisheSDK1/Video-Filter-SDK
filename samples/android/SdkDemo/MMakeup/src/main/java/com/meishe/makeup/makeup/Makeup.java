package com.meishe.makeup.makeup;

import com.meishe.makeup.net.Translation;

import java.io.Serializable;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/9 18:32
 * @Description :美妆数据类 makeup data class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class Makeup implements Serializable {
    private String name;
    private String uuid;
    private String cover;
    private String version;
    private String minSdkVersion;
    private String supportedAspectRatio;
    private List<Translation> translation;
    private MakeupParamContent effectContent;
    private String assetsDirectory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
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

    public String getAssetsDirectory() {
        return assetsDirectory;
    }

    public void setAssetsDirectory(String assetsDirectory) {
        this.assetsDirectory = assetsDirectory;
    }

    public boolean isSingleMakeup() {
        return getEffectContent() != null && getEffectContent().onlyOneMakeupParam();
    }

}
