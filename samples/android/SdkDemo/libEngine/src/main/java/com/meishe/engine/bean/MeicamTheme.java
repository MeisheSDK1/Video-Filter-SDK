package com.meishe.engine.bean;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMeicamTheme;

/**
 * Created by CaoZhiChao on 2020/7/4 15:20
 */
public class MeicamTheme implements Cloneable, TimelineDataToLocalAdapter<LMeicamTheme> {

    /**
     * 主题
     * Theme
     */
    @SerializedName("themePackageId")
    private String mThemePackageId;
    /**
     * 片头时长
     * Opening length
     */
    @SerializedName("themeTitlesClipDuration")
    private long mThemeTitlesClipDuration;
    /**
     * 片尾时长
     * End length
     */
    @SerializedName("themeTailClipDuration")
    private long mThemeTailClipDuration;
    @SerializedName("themeTitleText")
    private String mThemeTitleText;
    @SerializedName("themeTrailerText")
    private String mThemeTrailerText;

    public MeicamTheme(String themePackageId) {
        mThemePackageId = themePackageId;
    }

    public String getThemePackageId() {
        return mThemePackageId;
    }

    public void setThemePackageId(String themePackageId) {
        mThemePackageId = themePackageId;
    }

    public long getThemeTitlesClipDuration() {
        return mThemeTitlesClipDuration;
    }

    public void setThemeTitlesClipDuration(long themeTitlesClipDuration) {
        mThemeTitlesClipDuration = themeTitlesClipDuration;
    }

    public String getThemeTitleText() {
        return mThemeTitleText;
    }

    public void setThemeTitleText(String themeTitleText) {
        mThemeTitleText = themeTitleText;
    }

    public String getThemeTrailerText() {
        return mThemeTrailerText;
    }

    public void setThemeTrailerText(String themeTrailerText) {
        mThemeTrailerText = themeTrailerText;
    }

    public long getThemeTailClipDuration() {
        return mThemeTailClipDuration;
    }

    public void setThemeTailClipDuration(long themeTailClipDuration) {
        mThemeTailClipDuration = themeTailClipDuration;
    }

    @Override
    public LMeicamTheme parseToLocalData() {
        LMeicamTheme local = new LMeicamTheme(mThemePackageId);
        local.setThemeTailClipDuration(getThemeTailClipDuration());
        local.setThemeTitlesClipDuration(getThemeTitlesClipDuration());
        local.setThemeTitleText(getThemeTitleText());
        local.setThemeTrailerText(getThemeTrailerText());
        return local;
    }
}
