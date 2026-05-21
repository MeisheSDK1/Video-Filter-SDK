package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamTheme;

/**
 * Created by CaoZhiChao on 2020/7/4 15:20
 */
public class LMeicamTheme implements Cloneable, LocalToTimelineDataAdapter<MeicamTheme> {

    @SerializedName("themePackageId")
    private String mThemePackageId;
    @SerializedName("themeTitlesClipDuration")
    private long mThemeTitlesClipDuration;
    @SerializedName("themeTailClipDuration")
    private long mThemeTailClipDuration;
    @SerializedName("themeTitleText")
    private String mThemeTitleText;
    @SerializedName("themeTrailerText")
    private String mThemeTrailerText;

    public LMeicamTheme(String themePackageId) {
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
    public MeicamTheme parseToTimelineData() {
        MeicamTheme timelineData = new MeicamTheme(mThemePackageId);
        timelineData.setThemeTailClipDuration(getThemeTailClipDuration());
        timelineData.setThemeTitlesClipDuration(getThemeTitlesClipDuration());
        timelineData.setThemeTitleText(getThemeTitleText());
        timelineData.setThemeTrailerText(getThemeTrailerText());
        return timelineData;
    }
}
