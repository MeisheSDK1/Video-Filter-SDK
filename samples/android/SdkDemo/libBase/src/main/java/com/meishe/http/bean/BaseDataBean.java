package com.meishe.http.bean;


import com.meishe.base.constants.BaseConstants;

import java.io.Serializable;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/18.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseDataBean<T> implements Serializable {
    private String id;
    private String uuid;
    private int version;
    private String minAppVersion;
    private String displayName;
    private String displayNameZhCn;
    private String description;
    private String descriptionZhCn;
    private String coverUrl;
    private String previewVideoUrl;
    private int packageSize;
    private int type;
    private int kind;
    private TypeInfo typeInfo;
    private int category;
    private CategoryInfo categoryInfo;
    private KindInfo kindInfo;
    private int ratioFlag;
    private int supportedAspectRatio;
    private boolean authed;
    private int rate;
    private int costQuota;
    private T infoJson;
    /**
     * 动画是否需要添加post
     * Whether the animation needs to add a post
     */
    private int isPostPackage;
    private UserInfo userInfo;
    private String packageUrl;
    private String infoUrl;
    private String displayNamezhCN;
    private int sizeLevel;
    private QueryInteractiveResultDto queryInteractiveResultDto;
    private int defaultAspectRatio;
    private int shotsNumber;
    private long duration;
    /**
     * 1可调 0不可调
     * 注：只有查询素材分类分类为滤镜、转场、字幕、贴纸、AR道具、组合字幕才生效
     * 1 is adjustable and 0 is not
     * Note: It only takes effect if the query material is classified as filter, transition, subtitles, stickers, AR props and combined subtitles
     */
    public int isAdjusted;

    public int downloadStatus = BaseConstants.RES_STATUS_DOWNLOAD_NO;
    public int downloadProgress;
    private String zipUrl;

    public String getZipUrl() {
        return zipUrl;
    }

    public void setZipUrl(String zipUrl) {
        this.zipUrl = zipUrl;
    }

    public int getIsPostPackage() {
        return isPostPackage;
    }

    public void setIsPostPackage(int isPostPackage) {
        this.isPostPackage = isPostPackage;
    }

    public T getInfoJson() {
        return infoJson;
    }

    public void setInfoJson(T infoJson) {
        this.infoJson = infoJson;
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
    }

    public String getDisplayNameZhCn() {
        return displayNameZhCn;
    }

    public void setDisplayNameZhCn(String displayNameZhCn) {
        this.displayNameZhCn = displayNameZhCn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public CategoryInfo getCategoryInfo() {
        return categoryInfo;
    }

    public void setCategoryInfo(CategoryInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    public KindInfo getKindInfo() {
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getPackageUrl() {
        return zipUrl;
    }
    public String getRemotePackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getDisplayNamezhCN() {
        return displayNamezhCN;
    }

    public void setDisplayNamezhCN(String displayNamezhCN) {
        this.displayNamezhCN = displayNamezhCN;
    }

    public int getSizeLevel() {
        return sizeLevel;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;
    }

    public QueryInteractiveResultDto getQueryInteractiveResultDto() {
        return queryInteractiveResultDto;
    }

    public void setQueryInteractiveResultDto(QueryInteractiveResultDto queryInteractiveResultDto) {
        this.queryInteractiveResultDto = queryInteractiveResultDto;
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

    public int getIsAdjusted() {
        return isAdjusted;
    }

    public void setIsAdjusted(int isAdjusted) {
        this.isAdjusted = isAdjusted;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
