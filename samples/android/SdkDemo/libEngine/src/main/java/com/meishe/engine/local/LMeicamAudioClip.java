package com.meishe.engine.local;

import com.google.gson.annotations.SerializedName;
import com.meishe.base.utils.CommonUtils;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamAudioClip;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by CaoZhiChao on 2020/7/4 10:31
 */
public class LMeicamAudioClip extends LClipInfo implements Cloneable, Serializable {
    private String resourceId;
    private long id = -1;
    private String filePath;
    private long trimIn = 0L;
    private long trimOut = 0L;
    private float volume = 0.5f;
    private double speed = 1.0D;
    private long fadeInDuration;
    private long fadeOutDuration;
    @SerializedName("audioType")
    private int mAudioType;
    @SerializedName("leftVolumeGain")
    private float mLeftVolumeGain = 1.0F;
    @SerializedName("rightVolumeGain")
    private float mRightVolumeGain = 1.0F;
    @SerializedName("drawText")
    private String mDrawText;
    @SerializedName("audioFxs")
    private List<LMeicamAudioFx> mMeicamAudioFxs = new ArrayList<>();
    @SerializedName("orgDuration")
    private long mOriginalDuring;
    /**
     * 变调
     * Tone variation
     */
    @SerializedName("keepAudioPitch")
    private boolean keepAudioPitch = true;

    public LMeicamAudioClip() {
        super(CommonData.CLIP_AUDIO);
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getFadeInDuration() {
        return fadeInDuration;
    }

    public void setFadeInDuration(long fadeInDuration) {
        this.fadeInDuration = fadeInDuration;
    }

    public long getFadeOutDuration() {
        return fadeOutDuration;
    }

    public void setFadeOutDuration(long fadeOutDuration) {
        this.fadeOutDuration = fadeOutDuration;
    }

    public int getAudioType() {
        return mAudioType;
    }

    public void setAudioType(int mAudioType) {
        this.mAudioType = mAudioType;
    }

    public float getLeftVolumeGain() {
        return mLeftVolumeGain;
    }

    public void setLeftVolumeGain(float mLeftVolumeGain) {
        this.mLeftVolumeGain = mLeftVolumeGain;
    }

    public float getRightVolumeGain() {
        return mRightVolumeGain;
    }

    public void setRightVolumeGain(float mRightVolumeGain) {
        this.mRightVolumeGain = mRightVolumeGain;
    }

    public String getDrawText() {
        return mDrawText;
    }

    public void setDrawText(String mDrawText) {
        this.mDrawText = mDrawText;
    }

    public List<LMeicamAudioFx> getMeicamAudioFxes() {
        return mMeicamAudioFxs;
    }

    public void setMeicaAudioFxes(List<LMeicamAudioFx> mMeicamAudioFxes) {
        this.mMeicamAudioFxs = mMeicamAudioFxes;
    }

    public boolean isKeepAudioPitch() {
        return keepAudioPitch;
    }

    public void setKeepAudioPitch(boolean keepAudioPitch) {
        this.keepAudioPitch = keepAudioPitch;
    }

    @NonNull
    @Override
    public Object clone() {
        return DeepCopyUtil.deepClone(this);
    }

    @Override
    public MeicamAudioClip parseToTimelineData() {
        MeicamAudioClip timelineData = new MeicamAudioClip();
        setCommonData(timelineData);
        timelineData.setId(getId());
        timelineData.setFilePath(getFilePath());
        timelineData.setTrimIn(getTrimIn());
        timelineData.setTrimOut(getTrimOut());
        timelineData.setVolume(getVolume());
        timelineData.setSpeed(getSpeed(), isKeepAudioPitch());
        timelineData.setFadeInDuration(getFadeInDuration());
        timelineData.setFadeOutDuration(getFadeOutDuration());
        timelineData.setOriginalDuring(getOriginalDuring());
        timelineData.setAudioType(getAudioType());
        timelineData.setDrawText(getDrawText());
        timelineData.setKeepAudioPitch(isKeepAudioPitch());
        if (!CommonUtils.isEmpty(mMeicamAudioFxs)) {
            for (LMeicamAudioFx meicamAudioFx : mMeicamAudioFxs) {
                timelineData.getMeicamAudioFxes().add(meicamAudioFx.parseToTimelineData());
            }
        }
        return timelineData;
    }

    public long getOriginalDuring() {
        return mOriginalDuring;
    }

    public void setOriginalDuring(long mOriginalDuring) {
        this.mOriginalDuring = mOriginalDuring;
    }
}
