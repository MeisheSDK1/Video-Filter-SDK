package com.meishe.engine.local;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamStickerClip;


/**
 * Created by CaoZhiChao on 2020/7/4 12:04
 */
public class LMeicamStickerClip extends LClipInfo implements Cloneable{
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

    private float leftVolume;
    private boolean hasAudio;
    private String coverImagePath;

    private float zValue;

    @SerializedName("isCustomSticker")
    private boolean mIsCustomSticker;

    @SerializedName("customanimatedStickerImagePath")
    private String mCustomanimatedStickerImagePath;

    private String resourceId;

    public LMeicamStickerClip(String desc) {
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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
    public MeicamStickerClip parseToTimelineData() {
        MeicamStickerClip timelineData = new MeicamStickerClip(getPackageId());
        setCommonData(timelineData);
        timelineData.setStickerType(getStickerType());
        timelineData.setPackageId(getPackageId());
        timelineData.setScale(getScale());
        timelineData.setRotation(getRotation());
        timelineData.setTranslationX(getTranslationX());
        timelineData.setTranslationY(getTranslationY());
        timelineData.setHorizontalFlip(isHorizontalFlip());
        timelineData.setVerticalFlip(isVerticalFlip());
        timelineData.setDisplayName(getDisplayName());
        timelineData.setDisplayNamezhCN(getDisplayNamezhCN());
        timelineData.setLeftVolume(getLeftVolume());
        timelineData.setHasAudio(isHasAudio());
        timelineData.setCoverImagePath(getCoverImagePath());
        timelineData.setzValue(getzValue());
        timelineData.setIsCustomSticker(isCustomSticker());
        timelineData.setCustomanimatedStickerImagePath(getCustomanimatedStickerImagePath());
        return timelineData;
    }
}
