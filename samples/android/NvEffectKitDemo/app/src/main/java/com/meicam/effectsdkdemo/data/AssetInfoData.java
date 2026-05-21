package com.meicam.effectsdkdemo.data;

public class AssetInfoData {
    public String getDefaultCoverPath() {
        return defaultCoverPath;
    }

    public void setDefaultCoverPath(String defaultCoverPath) {
        this.defaultCoverPath = defaultCoverPath;
    }

    public String getSelCoverPath() {
        return selCoverPath;
    }

    public void setSelCoverPath(String selCoverPath) {
        this.selCoverPath = selCoverPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public int getCoverImageId() {
        return coverImageId;
    }

    public void setCoverImageId(int coverImageId) {
        this.coverImageId = coverImageId;
    }

    public void setFaceMeshId(String faceMeshId) {
        this.faceMeshId = faceMeshId;
    }

    public void setFaceMeshPath(String faceMeshPath) {
        this.faceMeshPath = faceMeshPath;
    }

    public String getFaceMeshId() {
        return faceMeshId;
    }

    public String getFaceMeshPath() {
        return faceMeshPath;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
    //Cover resource Id
    //封面资源Id
    private int coverImageId = 0;
    //Default cover path
    //默认封面路径
    private String defaultCoverPath;
    //The cover path when selected
    //选中时的封面路径
    private String selCoverPath;
    //name of asset
    //名称
    private String name;
    // effect Id Used to adjust the value of a parameter
    //特效Id,用于调节参数的值
    private String effectId;
    //The package path of the application
    //应用的package路径
    private String faceMeshPath;
    //the key used by the package setting
    //package设置使用的key
    private String faceMeshId;
    private String uuid;


    ////////////////////////额外添加属性///////////////////////
    public double getDefaultValById() {
        return defaultValById;
    }

    public void setDefaultValById(double defaultValById) {
        this.defaultValById = defaultValById;
    }
    //The default value of the effect Id
    //特效Id的默认值
    private double defaultValById = 0f;
}
