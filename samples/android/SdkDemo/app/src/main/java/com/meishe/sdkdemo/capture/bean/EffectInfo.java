package com.meishe.sdkdemo.capture.bean;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.meishe.http.bean.CategoryInfo;
import com.meishe.http.bean.KindInfo;
import com.meishe.http.bean.TypeInfo;
import com.meishe.sdkdemo.BR;

import java.io.Serializable;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/17 下午6:41
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class EffectInfo extends BaseObservable implements Serializable {
    private String id;
    private int version;
    private String minAppVersion;
    @Bindable
    private String displayName;
    @Bindable
    private String displayNamezhCN;
    private String description;
    private String descriptionZhCn;
    @Bindable
    private String coverUrl;
    private String previewVideoUrl;
    private int packageSize;
    private int type;
    private int kind;
    private TypeInfo typeInfo;
    private int category;
    private com.meishe.http.bean.CategoryInfo categoryInfo;
    private com.meishe.http.bean.KindInfo kindInfo;
    private int ratioFlag;
    private int supportedAspectRatio;
    private boolean authed;
    private int rate;
    private int costQuota;
    private int isPostPackage;
    private String zipUrl;
    private String infoUrl;
    private int sizeLevel;
    private int defaultAspectRatio;
    private int shotsNumber;
    private long duration;
    /**
     * 展示的名称
     * show name
     */
    private String name;


    /**
     * 是否选中
     * is select
     */
    @Bindable
    private boolean select;
    /**
     * 是否已经下载
     * is download
     */
    @Bindable
    private boolean download;
    /**
     * 下载进度
     * down progress
     */
    @Bindable
    private int progress;

    private int assetType;

    private String textDefaultColor = "#ff707070";
    private String textSelectColor = "#ff63ABFF";

    /**
     * 1可调 0不可调
     * 注：只有查询素材分类分类为滤镜、转场、字幕、贴纸、AR道具、组合字幕才生效
     *
     * 1 adjustable 0 not adjustable
     *Note: Only the query material is classified as filter, transition, subtitle, sticker, AR prop and combined subtitle
     */
    private int isAdjusted;

    private float strength;
    private float defaultStrength;


    public int getIsPostPackage() {
        return isPostPackage;
    }

    public void setIsPostPackage(int isPostPackage) {
        this.isPostPackage = isPostPackage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(String minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        notifyPropertyChanged(BR.displayName);
    }

    public String getDisplayNameZhCn() {
        return displayNamezhCN;
    }

    public void setDisplayNameZhCn(String displayNameZhCn) {
        this.displayNamezhCN = displayNameZhCn;
        notifyPropertyChanged(BR.displayNamezhCN);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        if(TextUtils.isEmpty(coverUrl)){
            return "";
        }
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        notifyPropertyChanged(BR.coverUrl);
    }

    public String getPreviewVideoUrl() {
        return previewVideoUrl;
    }

    public void setPreviewVideoUrl(String previewVideoUrl) {
        this.previewVideoUrl = previewVideoUrl;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public com.meishe.http.bean.CategoryInfo getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(CategoryInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    public com.meishe.http.bean.KindInfo getKindInfo() {
        return kindInfo;
    }

    public void setKindInfo(KindInfo kindInfo) {
        this.kindInfo = kindInfo;
    }

    public int getRatioFlag() {
        return ratioFlag;
    }

    public void setRatioFlag(int ratioFlag) {
        this.ratioFlag = ratioFlag;
    }

    public int getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(int supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed(boolean authed) {
        this.authed = authed;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getCostQuota() {
        return costQuota;
    }

    public void setCostQuota(int costQuota) {
        this.costQuota = costQuota;
    }

    public String getPackageUrl() {
        return zipUrl;
    }

    public String getZipUrl() {
        return zipUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public int getSizeLevel() {
        return sizeLevel;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;
    }


    public int getDefaultAspectRatio() {
        return defaultAspectRatio;
    }

    public void setDefaultAspectRatio(int defaultAspectRatio) {
        this.defaultAspectRatio = defaultAspectRatio;
    }

    public int getShotsNumber() {
        return shotsNumber;
    }

    public void setShotsNumber(int shotsNumber) {
        this.shotsNumber = shotsNumber;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDescriptionZhCn() {
        return descriptionZhCn;
    }

    public void setDescriptionZhCn(String descriptionZhCn) {
        this.descriptionZhCn = descriptionZhCn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
        notifyPropertyChanged(BR.select);
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
        notifyPropertyChanged(BR.download);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyPropertyChanged(BR.progress);
    }

    public int getAssetType() {
        return assetType;
    }

    public void setAssetType(int assetType) {
        this.assetType = assetType;
    }

    public String getTextDefaultColor() {
        return textDefaultColor;
    }

    public void setTextDefaultColor(String textDefaultColor) {
        this.textDefaultColor = textDefaultColor;
    }

    public String getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextSelectColor(String textSelectColor) {
        this.textSelectColor = textSelectColor;
    }

    public int getIsAdjusted() {
        return isAdjusted;
    }

    public void setIsAdjusted(int isAdjusted) {
        this.isAdjusted = isAdjusted;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public float getDefaultStrength() {
        return defaultStrength;
    }

    public void setDefaultStrength(float defaultStrength) {
        this.defaultStrength = defaultStrength;
    }
}
