package com.meishe.sdkdemo.urledit.bean;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2024/12/11 14:39
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class UrlMaterialInfo {
    protected String url;
    protected String coverUrl;
    protected Integer type;
    protected Integer width;
    protected Integer height;
    protected String waveUrl;
    protected String displayName;
    protected boolean isSelected;
    protected long duration;
    protected long trimIn;
    protected long trimOut;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getWaveUrl() {
        return waveUrl;
    }

    public void setWaveUrl(String waveUrl) {
        this.waveUrl = waveUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }

    public long getTrimOut() {
        return trimOut;
    }

    public void setTrimOut(long trimOut) {
        this.trimOut = trimOut;
    }

    @Override
    public String toString() {
        return "UrlMaterialInfo{" +
                "url='" + url + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", type=" + type +
                ", width=" + width +
                ", height=" + height +
                ", waveUrl='" + waveUrl + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isSelected=" + isSelected +
                ", duration=" + duration +
                ", trimIn=" + trimIn +
                ", trimOut=" + trimOut +
                '}';
    }
}
