package com.meishe.engine.bean;

import android.graphics.PointF;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meishe.engine.adapter.parser.IResourceParser;
import com.meishe.engine.local.LMeicamStickerClip;


/**
 * Created by CaoZhiChao on 2020/7/4 12:04
 */
public class MeicamStickerClip extends ClipInfo<NvsTimelineAnimatedSticker> implements Cloneable, IResourceParser {
    @SerializedName("subType")
    private String stickerType = "general";
    @SerializedName("id")
    private String mPackageId;
    private float scale = 1.0F;
    private float rotation;
    private float translationX;
    private float translationY;
    private boolean horizontalFlip = false;
    private boolean verticalFlip = false;
    private String displayName;
    private String displayNamezhCN;

    private float leftVolume = 1.0f;
    private boolean hasAudio;
    private String coverImagePath;

    private float zValue;

    @SerializedName("isCustomSticker")
    private boolean mIsCustomSticker;

    @SerializedName("customanimatedStickerImagePath")
    private String mCustomanimatedStickerImagePath;
    private String resourceId;

    public MeicamStickerClip(String desc) {
        super(CommonData.CLIP_STICKER);
        this.mPackageId = desc;
    }

    public String getStickerType() {
        return stickerType;
    }

    public void setStickerType(String stickerType) {
        this.stickerType = stickerType;
    }

    public String getPackageId() {
        return mPackageId;
    }

    public void setPackageId(String packageId) {
        mPackageId = packageId;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }

    public boolean isHorizontalFlip() {
        return horizontalFlip;
    }

    public void setHorizontalFlip(boolean horizontalFlip) {
        this.horizontalFlip = horizontalFlip;
    }

    public boolean isVerticalFlip() {
        return verticalFlip;
    }

    public void setVerticalFlip(boolean verticalFlip) {
        this.verticalFlip = verticalFlip;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNamezhCN() {
        return displayNamezhCN;
    }

    public void setDisplayNamezhCN(String displayNamezhCN) {
        this.displayNamezhCN = displayNamezhCN;
    }

    public void setCustomSticker(boolean customSticker) {
        mIsCustomSticker = customSticker;
    }

    public boolean isCustomSticker() {
        return mIsCustomSticker;
    }

    public void setCustomanimatedStickerImagePath(String customanimatedStickerImagePath) {
        mCustomanimatedStickerImagePath = customanimatedStickerImagePath;
    }

    public String getCustomanimatedStickerImagePath() {
        return mCustomanimatedStickerImagePath;
    }


    public NvsTimelineAnimatedSticker bindToTimeline(NvsTimeline timeline) {
        if (timeline == null) {
            return null;
        }
        long duration = getOutPoint() - getInPoint();
        boolean isCustomSticker = isCustomSticker();
        NvsTimelineAnimatedSticker nvsTimelineAnimatedSticker = isCustomSticker ?
                timeline.addCustomAnimatedSticker(getInPoint(), duration, getPackageId(), getCustomanimatedStickerImagePath())
                : timeline.addAnimatedSticker(getInPoint(), duration, getPackageId());
        if (nvsTimelineAnimatedSticker == null) {
            return null;
        }
        nvsTimelineAnimatedSticker.setHorizontalFlip(isHorizontalFlip());
        nvsTimelineAnimatedSticker.setClipAffinityEnabled(false);
        PointF translation = new PointF(getTranslationX(), getTranslationY());
        float scaleFactor = getScale();
        float rotation = getRotation();
        if (scaleFactor > 0) {
            nvsTimelineAnimatedSticker.setScale(scaleFactor);
        }
        nvsTimelineAnimatedSticker.setZValue(getzValue());
        nvsTimelineAnimatedSticker.setRotationZ(rotation);
        setObject(nvsTimelineAnimatedSticker);
        nvsTimelineAnimatedSticker.setTranslation(translation);
        float volumeGain = getLeftVolume();
        nvsTimelineAnimatedSticker.setVolumeGain(volumeGain, volumeGain);
        nvsTimelineAnimatedSticker.setHorizontalFlip(isHorizontalFlip());
        nvsTimelineAnimatedSticker.setVerticalFlip(isVerticalFlip());
        return nvsTimelineAnimatedSticker;
    }

    @Override
    public void loadData(NvsTimelineAnimatedSticker sticker) {
        if (sticker == null) {
            return;
        }
        setObject(sticker);
        setInPoint(sticker.getInPoint());
        setOutPoint(sticker.getOutPoint());
        mPackageId = sticker.getAnimatedStickerPackageId();
        PointF translation = sticker.getTranslation();
        translationX = translation.x;
        translationY = translation.y;
        scale = sticker.getScale();
        rotation = sticker.getRotationZ();
        horizontalFlip = sticker.getHorizontalFlip();
        verticalFlip = sticker.getVerticalFlip();
        leftVolume = sticker.getVolumeGain().leftVolume;
        zValue = sticker.getZValue();
    }

    public float getLeftVolume() {
        return leftVolume;
    }

    public void setLeftVolume(float leftVolume) {
        this.leftVolume = leftVolume;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public boolean ismIsCustomSticker() {
        return mIsCustomSticker;
    }

    public void setIsCustomSticker(boolean mIsCustomSticker) {
        this.mIsCustomSticker = mIsCustomSticker;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }

    public void removeFromTimeline(NvsTimeline timeline) {
        NvsTimelineAnimatedSticker object = getObject();
        if (object != null) {
            timeline.removeAnimatedSticker(object);
        }
    }


    @NonNull
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LMeicamStickerClip parseToLocalData() {
        parseToResourceId();
        LMeicamStickerClip local = new LMeicamStickerClip(getPackageId());
        setCommonData(local);
        local.setStickerType(getStickerType());
        local.setPackageId(getPackageId());
        local.setScale(getScale());
        local.setRotation(getRotation());
        local.setTranslationX(getTranslationX());
        local.setTranslationY(getTranslationY());
        local.setHorizontalFlip(isHorizontalFlip());
        local.setVerticalFlip(isVerticalFlip());
        local.setDisplayName(getDisplayName());
        local.setDisplayNamezhCN(getDisplayNamezhCN());
        local.setLeftVolume(getLeftVolume());
        local.setHasAudio(isHasAudio());
        local.setCoverImagePath(getCoverImagePath());
        local.setzValue(getzValue());
        local.setIsCustomSticker(isCustomSticker());
        local.setCustomanimatedStickerImagePath(getCustomanimatedStickerImagePath());
        local.setResourceId(resourceId);
        return local;
    }

    @Override
    public void parseToResourceId() {
        if (!TextUtils.isEmpty(mCustomanimatedStickerImagePath)) {
            MeicamResource resource = new MeicamResource();
            resource.addPathInfo(new MeicamResource.PathInfo("path",
                    mCustomanimatedStickerImagePath, false));
            resourceId = TimelineData.getInstance().getPlaceId(resource);
        }
    }
}
