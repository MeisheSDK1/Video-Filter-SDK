package com.meishe.engine.asset.bean;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.meishe.base.utils.Utils;
import com.meishe.engine.bean.BaseInfo;
import com.meishe.engine.interf.IBaseInfo;

/**
 * 资源信息实体类，和网络资源具有对应关系
 * Resource information entity class, and NVAsset has a corresponding relationship
 */
public class AssetInfo extends BaseInfo {
    public static final int ASSET_THEME = 1;
    public static final int ASSET_FILTER = 2;
    public static final int ASSET_CAPTION_STYLE = 3;
    public static final int ASSET_ANIMATED_STICKER = 4;
    public static final int ASSET_VIDEO_TRANSITION = 5;
    public static final int ASSET_FONT = 6;
    public static final int ASSET_CAPTURE_SCENE = 8;
    public static final int ASSET_PARTICLE = 9;
    public static final int ASSET_FACE_STICKER = 10;
    public static final int ASSET_FACE1_STICKER = 11;
    /**
     * 注意和ASSET_CUSTOM_STICKER_PACKAGE作区分，本类型是自定义贴纸所需特效包类型
     * Note that this type is different from ASSET_CUSTOM_STICKER_PACKAGE
     */
    public static final int ASSET_CUSTOM_STICKER_PACKAGE = 12;
    public static final int ASSET_SUPER_ZOOM = 13;
    public static final int ASSET_FACE_BUNDLE_STICKER = 14;
    public static final int ASSET_AR_SCENE_FACE = 15;
    public static final int ASSET_COMPOUND_CAPTION = 16;
    public static final int ASSET_PHOTO_ALBUM = 17;
    public static final int ASSET_EFFECT_FRAME = 18;
    public static final int ASSET_EFFECT_DREAM = 19;
    public static final int ASSET_EFFECT_LIVELY = 20;
    public static final int ASSET_EFFECT_SHAKING = 21;
    /**
     * 注意和ASSET_CUSTOM_STICKER_PACKAGE作区分，本类型是自定义贴纸类型
     * Note that, unlike ASSET_CUSTOM_STICKER_PACKAGE, this type is a custom sticker type
     */
    public static final int ASSET_CUSTOM_STICKER = 22;

    /*
     * 自定义贴纸
     * Custom sticker
     * */
    public static final int ASSET_ANIMATED_STICKER_CUSTOM = 22;
    /*
     * 效果 模糊 马赛克
     * Effect blur Mosaic
     * */
    public static final int ASSET_WATER_EFFECT = 23;
    /*
     * 效果 水印
     * Effect of watermark
     * */
    public static final int ASSET_WATER = 24;

    /*
     * 3D转场
     * 3 d transitions
     * */
    public static final int ASSET_VIDEO_TRANSITION_3D = 25;
    /*
     * 特效转场
     * The special effects transitions
     * */
    public static final int ASSET_VIDEO_TRANSITION_EFFECT = 26;

    public static final int ASSET_ANIMATION_IN = 27;
    public static final int ASSET_ANIMATION_OUT = 28;
    public static final int ASSET_ANIMATION_GROUP = 29;
    public static final int ASSET_CUSTOM_CAPTION_FLOWER = 30;
    public static final int ASSET_CUSTOM_CAPTION_BUBBLE = 31;
    public static final int ASSET_CUSTOM_CAPTION_ANIMATION_IN = 32;
    public static final int ASSET_CUSTOM_CAPTION_ANIMATION_OUT = 33;
    public static final int ASSET_CUSTOM_CAPTION_ANIMATION_COMBINATION = 34;

    /*
     * 曲线变速
     * speed curve
     * */
    public static final int ASSET_CHANGE_SPEED_CURVE = 35;

    /*
     * 其它特效
     * Other effect
     * */
    public static final int ASSET_EFFECT_OTHER = 36;

    public static final int NV_CATEGORY_ID_ALL = 0;
    public static final int NV_CATEGORY_ID_DOUVIDEOFILTER = 7;
    public static final int NV_CATEGORY_ID_CUSTOM = 20000;
    public static final int NV_CATEGORY_ID_PARTICLE_TOUCH_TYPE = 2;
    /**
     * 下载失败的下载进度
     * Download failed download progress
     */
    public static final int DOWNLOAD_FAILED_PROGRESS = 101;
    /**
     * 英文名称
     */
    private String enName;
    private String id;
    private String packageId;
    private String effectId;
    private int effectMode;
    private String assetPath;
    private String downloadUrl;
    private int assetSize;
    private String minAppVersion;
    private int downloadProgress = -1;
    private boolean hadDownloaded;
    /**
     * （最新）资源版本号
     * The asset version (latest)
     */
    private int version;
    /**
     * 本地已经下载的（如果有的话）资源版本号
     * The local version (old)
     */
    private int localVersion;
    /**
     * 资源支持的比例
     * The asset supported ratio
     */
    private int supportedAspectRatio;

    /**
     * 资源支持的比例标记
     * The asset supported ratio flag
     */
    private int ratioFlag;

    /**
     * Url of sample for preview
     * <P></>
     * 预览样例路径
     */
    private String previewSampleUrl;

    /**
     * Url of information
     * <P></>
     * 信息文件路径
     */
    private String infoUrl;

    private boolean isNewData = false;

    @Override
    public String getName() {
        return Utils.isZh() ? super.getName() : getEnName();
    }


    public String getEnName() {
        if (TextUtils.isEmpty(enName)) {
            return super.getName();
        }
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @Override
    public String getPackageId() {
        return packageId;
    }

    @Override
    public void setEffectMode(int effectMode) {
        this.effectMode = effectMode;
    }

    @Override
    public int getEffectMode() {
        return effectMode;
    }

    @Override
    public String getEffectId() {
        return effectId;
    }

    @Override
    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    @Override
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    @Override
    public String getAssetPath() {
        return assetPath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getAssetSize() {
        return assetSize;
    }

    public void setAssetSize(int assetSize) {
        this.assetSize = assetSize;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLocalVersion() {
        return localVersion;
    }

    public void setLocalVersion(int localVersion) {
        this.localVersion = localVersion;
    }

    public boolean needUpdate() {
        return isHadDownloaded() && version > localVersion && (!TextUtils.isEmpty(downloadUrl));

    }

    public int getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(int supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public String getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(String minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public boolean isHadDownloaded() {
        return hadDownloaded;
    }

    public void setHadDownloaded(boolean hadDownloaded) {
        this.hadDownloaded = hadDownloaded;
    }

    public String getPreviewSampleUrl() {
        return previewSampleUrl;
    }

    public void setPreviewSampleUrl(String previewSampleUrl) {
        this.previewSampleUrl = previewSampleUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public boolean isNewData() {
        return isNewData;
    }

    public void setNewData(boolean newData) {
        isNewData = newData;
    }

    public int getRatioFlag() {
        return ratioFlag;
    }

    public void setRatioFlag(int ratioFlag) {
        this.ratioFlag = ratioFlag;
    }

    @Override
    public void update(IBaseInfo newInfo) {
        super.update(newInfo);
        AssetInfo info = (AssetInfo) newInfo;
        setDownloadProgress(info.getDownloadProgress());
        setVersion(info.getVersion());
        setLocalVersion(info.getLocalVersion());
        setMinAppVersion(info.getMinAppVersion());
        setHadDownloaded(info.isHadDownloaded());
        setAssetSize(info.getAssetSize());
        setDownloadUrl(info.getDownloadUrl());
        setSupportedAspectRatio(info.getSupportedAspectRatio());
        setPreviewSampleUrl(info.getPreviewSampleUrl());
        setInfoUrl(info.getInfoUrl());
        setNewData(info.isNewData);
    }

    @NonNull
    @Override
    public String toString() {
        return "AssetInfo{id=" + id + ",name=" + getName() + ",packageId=" + getPackageId() + ",version=" + getVersion()
                + ",localVersion=" + getLocalVersion() + ",type=" + getType() + ",hadDownload=" + hadDownloaded + "}";
    }

    public static AssetInfo create(AssetList.NvAssetInfo info, int type) {
        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setId(info.id);
        assetInfo.setPackageId(info.id);
        assetInfo.setEffectMode(BaseInfo.EFFECT_MODE_PACKAGE);
        assetInfo.setHadDownloaded(false);
        if (!TextUtils.isEmpty(info.name)){
            assetInfo.setName(info.name);
        } else {
            if (Utils.isZh()) {
                assetInfo.setName(info.displayNameZhCn);
            } else {
                assetInfo.setEnName(info.displayName);
            }
        }

        if (Utils.isZh()) {
            assetInfo.setDescription(info.descriptionZhCn);
        } else {
            assetInfo.setDescription(info.description);
        }

        if (TextUtils.isEmpty(assetInfo.getName())) {
            assetInfo.setName(info.customDisplayName);
        }
        assetInfo.setCoverPath(info.coverUrl);
        assetInfo.setVersion(info.version);
        assetInfo.setMinAppVersion(info.minAppVersion);
        assetInfo.setAssetSize(info.packageSize);
        assetInfo.setDownloadUrl(info.packageUrl);

        assetInfo.setType(type);
        assetInfo.setAuthorized(info.authed);
        assetInfo.setPreviewSampleUrl(info.previewVideoUrl);
        if (TextUtils.isEmpty(assetInfo.getDownloadUrl())) {
            assetInfo.setDownloadUrl(info.packageRelativePath);
        }
        assetInfo.setInfoUrl(info.infoUrl);
        assetInfo.setRatioFlag(info.ratioFlag);
        assetInfo.setDuration(info.templateTotalDuration);
        return assetInfo;
    }

}
