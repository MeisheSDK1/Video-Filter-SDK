package com.meishe.engine.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioFx;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsVolume;
import com.meishe.base.utils.CommonUtils;
import com.meishe.engine.adapter.parser.IResourceParser;
import com.meishe.engine.local.LMeicamAudioClip;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static com.meishe.engine.bean.CommonData.TYPE_BUILD_IN;


/**
 * Created by CaoZhiChao on 2020/7/4 10:31
 */
public class MeicamAudioClip extends ClipInfo<NvsAudioClip> implements Cloneable, Serializable, IResourceParser {
    private long id = -1;
    private String filePath;
    private long trimIn = 0L;
    private long trimOut = 0L;
    private float volume = 0.5f;
    private double speed = 1.0D;
    private long fadeInDuration;
    private long fadeOutDuration;
    /**
     * 音频原始时长
     * Original audio duration
     */
    @SerializedName("orgDuration")
    private long mOriginalDuring;
    /**
     * 是否为录音片段
     * Whether it is a recording clip
     */
    @SerializedName("audioType")
    private int mAudioType;
    @SerializedName("drawText")
    private String mDrawText;
    @SerializedName("audioFxs")
    private List<MeicamAudioFx> mMeicamAudioFxs = new ArrayList<>();

    private String resourceId;
    /**
     * 变调
     * Tone variation
     */
    @SerializedName("keepAudioPitch")
    private boolean keepAudioPitch = true;

    public MeicamAudioClip() {
        super(CommonData.CLIP_AUDIO);
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

    public boolean isKeepAudioPitch() {
        return keepAudioPitch;
    }

    public void setKeepAudioPitch(boolean keepAudioPitch) {
        this.keepAudioPitch = keepAudioPitch;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        NvsAudioClip nvsAudioClip = getObject();
        if (nvsAudioClip == null) {
            return;
        }
        nvsAudioClip.setVolumeGain(volume, volume);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed, boolean isChangeVoice) {
        this.speed = speed;
        NvsAudioClip nvsAudioClip = getObject();
        if (nvsAudioClip == null) {
            return;
        }
        //速度改变了，修改上层数据 The speed has changed. Modify the upper level data
        nvsAudioClip.changeSpeed(speed, isChangeVoice);
        this.setKeepAudioPitch(isChangeVoice);
        this.setInPoint(nvsAudioClip.getInPoint());
        this.setOutPoint(nvsAudioClip.getOutPoint());
    }

    public long getFadeInDuration() {
        return fadeInDuration;
    }

    public void setFadeInDuration(long fadeInDuration) {
        this.fadeInDuration = fadeInDuration;
        NvsAudioClip nvsAudioClip = getObject();
        if (nvsAudioClip == null) {
            return;
        }
        nvsAudioClip.setFadeInDuration(fadeInDuration);
    }

    public long getFadeOutDuration() {
        return fadeOutDuration;
    }

    public void setFadeOutDuration(long fadeOutDuration) {
        this.fadeOutDuration = fadeOutDuration;
        NvsAudioClip nvsAudioClip = getObject();
        if (nvsAudioClip == null) {
            return;
        }
        nvsAudioClip.setFadeOutDuration(fadeOutDuration);
    }

    public int getAudioType() {
        return mAudioType;
    }

    public void setAudioType(int mAudioType) {
        this.mAudioType = mAudioType;
    }

    public String getDrawText() {
        return mDrawText;
    }

    public void setDrawText(String mDrawText) {
        this.mDrawText = mDrawText;
    }

    public long getOriginalDuring() {
        return mOriginalDuring;
    }

    public void setOriginalDuring(long mOriginalDuring) {
        this.mOriginalDuring = mOriginalDuring;
    }

    public List<MeicamAudioFx> getMeicamAudioFxes() {
        return mMeicamAudioFxs;
    }

    public void setMeicaAudioFxes(List<MeicamAudioFx> mMeicamAudioFxes) {
        this.mMeicamAudioFxs = mMeicamAudioFxes;
        NvsAudioClip nvsAudioClip = getObject();
        if (nvsAudioClip == null) {
            return;
        }
        if (mMeicamAudioFxes == null) {
            nvsAudioClip.removeFx(0);
            return;
        }
        for (MeicamAudioFx meicamAudioFx : mMeicamAudioFxes) {
            String type = meicamAudioFx.getType();
            if (TYPE_BUILD_IN.equals(type)) {
                nvsAudioClip.removeFx(0);
                nvsAudioClip.appendFx(meicamAudioFx.getDesc());
            }
        }
    }

    @Override
    public void loadData(NvsAudioClip audioClip) {
        if (audioClip==null){
            return;
        }

        setObject(audioClip);
        setFilePath(audioClip.getFilePath());
        setInPoint(audioClip.getInPoint());
        setOutPoint(audioClip.getOutPoint());
        trimIn = audioClip.getTrimIn();
        trimOut = audioClip.getTrimOut();
        speed = audioClip.getSpeed();
        fadeInDuration = audioClip.getFadeInDuration();
        fadeOutDuration = audioClip.getFadeOutDuration();
        int count = audioClip.getFxCount();
        if (mMeicamAudioFxs != null) {
            mMeicamAudioFxs.clear();
            if (count > 0) {
                NvsAudioFx audioFx = audioClip.getFxByIndex(0);
                MeicamAudioFx meicamAudioFx = new MeicamAudioFx(0, TYPE_BUILD_IN, audioFx.getBuiltinAudioFxName());
                mMeicamAudioFxs.add(meicamAudioFx);
            }
        }

        NvsVolume volumeGain = audioClip.getVolumeGain();
        if (volumeGain != null) {
            setVolume(volumeGain.leftVolume);
        }
    }

    public NvsAudioClip bindToTimeline(NvsAudioTrack audioTrack) {
        if (audioTrack == null) {
            return null;
        }
        NvsAudioClip nvsAudioClip = null;
        if (getTrimOut() <= 0) {
            nvsAudioClip = audioTrack.addClip(getFilePath(), getInPoint());
        } else {
            nvsAudioClip = audioTrack.addClip(getFilePath(), getInPoint(), getTrimIn(), getTrimOut());
        }
        setIndex(audioTrack.getIndex());
        setOtherAttribute(nvsAudioClip);
        return nvsAudioClip;
    }

    public void setOtherAttribute(NvsAudioClip nvsAudioClip) {
        if (nvsAudioClip == null) {
            return;
        }
        setObject(nvsAudioClip);
        nvsAudioClip.setFadeInDuration(getFadeInDuration());
        nvsAudioClip.setFadeOutDuration(getFadeOutDuration());
        nvsAudioClip.setVolumeGain(volume, volume);
        nvsAudioClip.changeSpeed(getSpeed(),isKeepAudioPitch());
        if (mMeicamAudioFxs != null) {
            for (MeicamAudioFx meicamAudioFx : mMeicamAudioFxs) {
                String type = meicamAudioFx.getType();
                if (TYPE_BUILD_IN.equals(type)) {
                    nvsAudioClip.appendFx(meicamAudioFx.getDesc());
                }
            }
        }

    }

    @NonNull
    @Override
    public Object clone() {
        return DeepCopyUtil.deepClone(this);
    }

    @Override
    public LMeicamAudioClip parseToLocalData() {
        parseToResourceId();
        LMeicamAudioClip local = new LMeicamAudioClip();
        setCommonData(local);
        local.setId(getId());
        local.setFilePath(getFilePath());
        local.setTrimIn(getTrimIn());
        local.setTrimOut(getTrimOut());
        local.setVolume(getVolume());
        local.setSpeed(getSpeed());
        local.setFadeInDuration(getFadeInDuration());
        local.setFadeOutDuration(getFadeOutDuration());
        local.setOriginalDuring(getOriginalDuring());
        local.setAudioType(getAudioType());
        local.setDrawText(getDrawText());
        local.setResourceId(resourceId);
        local.setKeepAudioPitch(isKeepAudioPitch());
        if (!CommonUtils.isEmpty(mMeicamAudioFxs)) {
            for (MeicamAudioFx meicamAudioFx : mMeicamAudioFxs) {
                local.getMeicamAudioFxes().add(meicamAudioFx.parseToLocalData());
            }
        }
        return local;
    }

    @Override
    public void parseToResourceId() {
        if (!TextUtils.isEmpty(filePath)) {
            MeicamResource resource = new MeicamResource();
            MeicamResource.PathInfo path = new MeicamResource.PathInfo("path", filePath, true);
            resource.addPathInfo(path);
            resourceId = TimelineData.getInstance().getPlaceId(resource);
        }
    }
}
