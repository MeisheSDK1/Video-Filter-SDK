package com.meishe.cutsame.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.meishe.http.bean.UserInfo;

import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_16v9;
import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_18v9;
import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_1v1;
import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_3v4;
import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_4v3;
import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_9v16;
import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_ASPECT_RATIO_9v18;

/**
 * author : lhz
 * date   : 2020/11/4
 * desc   :模板实体类
 * Template entity class
 * <p>
 */
public class Template implements Parcelable, Comparable<Template> {
    public static final String TYPE_TEMPLATE_STANDER = "stander";
    public static final String TYPE_TEMPLATE_FREE = "free";
    public static final String TYPE_TEMPLATE_AE = "AE";
    public static final int TYPE_TEMPLATE_NETWORK = 0;
    public static final int TYPE_TEMPLATE_LOCAL = 1;
    private float ratio;//比例大小 Size proportion
    private String id;//剪同款模板ID Cut the same template ID
    private String displayName;//模板名称 template name
    private String description;//模板描述 Descript
    private String coverUrl;// 模板封面URL Template cover URL
    private String previewVideoUrl;// 模板封面URL Template cover URL
    private String infoUrl;//模板info.json URL Template info. Json URL
    private String packageInfo;//模板描述 Descript
    private String packageUrl;//模板包URL The template package URL
    private long duration;//时长 duration
    private int shotsNumber;//片段数量 The number of pieces
    private int useNum;//使用次数 number of use
    private int likeNum;//点赞次数 Thumb up number
    private int supportedAspectRatio;//支持的画幅比例 Supported picture scale
    private int defaultAspectRatio;//画幅比例 Draw a percentage
    private UserInfo userInfo;// 创建者信息 Creator information
    private int version;// 版本 versions
    private long createTime;//创建时间 creation time
    /**
     * kind分类
     * kind type
     */
    private int kind;
    /**
     * 模板类别
     * template category
     */
    private String type = TYPE_TEMPLATE_STANDER;

    private int localType = TYPE_TEMPLATE_NETWORK;

    public Template() {
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * Sets ratio.
     * 设置比例
     *
     * @param ratio the ratio 设置比例
     */
    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    /**
     * Gets id.
     * 获取id
     *
     * @return the id id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    public int getUseNum() {
        return useNum;
    }

    public void setUseNum(int useNum) {
        this.useNum = useNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public int getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getShotsNumber() {
        return shotsNumber;
    }

    public void setShotsNumber(int shotsNumber) {
        this.shotsNumber = shotsNumber;
    }

    public void setSupportedAspectRatio(int supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public int getDefaultAspectRatio() {
        return defaultAspectRatio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLocalType() {
        return localType;
    }

    public void setLocalType(int localType) {
        this.localType = localType;
    }

    /**
     * Sets default aspect ratio.
     * 设置默认长宽比
     *
     * @param defaultAspectRatio the default aspect ratio 默认长宽比
     */
    public void setDefaultAspectRatio(int defaultAspectRatio) {
        this.defaultAspectRatio = defaultAspectRatio;
        if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_16v9) {
            ratio = 9 / 16f;
        } else if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_1v1) {
            ratio = 1;
        } else if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_9v16) {
            ratio = 16 / 9f;
        } else if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_4v3) {
            ratio = 3 / 4f;
        } else if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_3v4) {
            ratio = 4 / 3f;
        } else if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_18v9) {
            ratio = 9 / 18f;
        } else if (defaultAspectRatio == ASSET_PACKAGE_ASPECT_RATIO_9v18) {
            ratio = 18 / 9f;
        } else if (defaultAspectRatio == 128) {
            ratio = 1 / 2.39f;
        } else if (defaultAspectRatio == 256) {
            ratio = 1 / 2.55f;
        } else if (defaultAspectRatio == 512) {
            ratio = 9 / 21f;
        } else if (defaultAspectRatio == 1024) {
            ratio = 21 / 9f;
        } else {
            ratio = 1f;
        }
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public float getRatio() {
        if (ratio == 0) {
            setDefaultAspectRatio(getDefaultAspectRatio());
        }
        return ratio;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    protected Template(Parcel in) {
        ratio = in.readFloat();
        id = in.readString();
        displayName = in.readString();
        description = in.readString();
        coverUrl = in.readString();
        previewVideoUrl = in.readString();
        infoUrl = in.readString();
        packageInfo = in.readString();
        packageUrl = in.readString();
        duration = in.readLong();
        shotsNumber = in.readInt();
        useNum = in.readInt();
        likeNum = in.readInt();
        version = in.readInt();
        supportedAspectRatio = in.readInt();
        defaultAspectRatio = in.readInt();
        kind = in.readInt();
        type = in.readString();
        localType = in.readInt();
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
    }

    public static final Creator<Template> CREATOR = new Creator<Template>() {
        @Override
        public Template createFromParcel(Parcel in) {
            return new Template(in);
        }

        @Override
        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(ratio);
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(description);
        dest.writeString(coverUrl);
        dest.writeString(previewVideoUrl);
        dest.writeString(infoUrl);
        dest.writeString(packageInfo);
        dest.writeString(packageUrl);
        dest.writeLong(duration);
        dest.writeInt(shotsNumber);
        dest.writeInt(useNum);
        dest.writeInt(likeNum);
        dest.writeInt(version);
        dest.writeInt(supportedAspectRatio);
        dest.writeInt(defaultAspectRatio);
        dest.writeInt(kind);
        dest.writeString(type);
        dest.writeInt(localType);
        dest.writeParcelable(userInfo, flags);
    }

    @Override
    public int compareTo(Template o) {
        int i = (int) (o.getCreateTime() - this.getCreateTime());
        return i;
    }
}
