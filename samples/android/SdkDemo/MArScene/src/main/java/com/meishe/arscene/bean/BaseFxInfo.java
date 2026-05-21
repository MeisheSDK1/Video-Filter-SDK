package com.meishe.arscene.bean;

import com.meishe.arscene.inter.IFxInfo;

import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 16:52
 * @Description : 特效数据类 FX data class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseFxInfo implements IFxInfo {
    /**
     * 占位类型
     * Placeholder type
     */
    public static final String TYPE_PLACE_HOLDER = "placeholder";
    /**
     * 美颜类型
     * Beauty type
     */
    public static final String TYPE_BEAUTY = "beauty";
    /**
     * 美型类型
     * Shape type
     */
    public static final String TYPE_BEAUTY_SHAPE = "shape";


    private String name;
    private int resourceId;
    private String resourceUrl;

    /**
     * 类型扩展字段，目前仅使用了占位类型
     * Type extension field, currently only placeholder types are used
     */
    private String type;
    private String fxName;
    private double strength;
    private double defaultStrength;
    private String packageId;
    private String assetPackagePath;
    private boolean canReplace = true;
    /**
     * 特效子分类相关
     * Special effects subclassification is related
     */
    private List<IFxInfo> fxNodes;
    private boolean isExpanded;
    private boolean isSelected;
    private boolean isDefaultFx;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public BaseFxInfo setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String getFxName() {
        return fxName;
    }

    @Override
    public BaseFxInfo setFxName(String fxName) {
        this.fxName = fxName;
        return this;
    }

    @Override
    public double getStrength() {
        return strength;
    }

    @Override
    public BaseFxInfo setStrength(double strength) {
        this.strength = strength;
        return this;
    }

    @Override
    public double getDefaultStrength() {
        return defaultStrength;
    }

    @Override
    public BaseFxInfo setDefaultStrength(double strength) {
        this.defaultStrength = strength;
        return this;
    }

    @Override
    public String getPackageId() {
        return packageId;
    }

    @Override
    public BaseFxInfo setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    @Override
    public String getAssetPackagePath() {
        return assetPackagePath;
    }

    @Override
    public BaseFxInfo setAssetPackagePath(String packagePath) {
        this.assetPackagePath = packagePath;
        return this;
    }

    @Override
    public BaseFxInfo copy(IFxInfo fxInfo) {
        setType(fxInfo.getType());
        setFxName(fxInfo.getFxName());
        setPackageId(fxInfo.getPackageId());
        setAssetPackagePath(fxInfo.getAssetPackagePath());
        setDefaultStrength(fxInfo.getDefaultStrength());
        setStrength(fxInfo.getStrength());

        setName(fxInfo.getName());
        setResourceUrl(fxInfo.getResourceUrl());
        setResourceId(fxInfo.getResourceId());
        setIsExpanded(false);
        setFxNodes(fxInfo.getFxNodes());
        return this;
    }

    @Override
    public BaseFxInfo setFxNodes(List<IFxInfo> fxNodes) {
        this.fxNodes = fxNodes;
        return this;
    }

    @Override
    public List<IFxInfo> getFxNodes() {
        return fxNodes;
    }

    @Override
    public BaseFxInfo setIsExpanded(boolean state) {
        this.isExpanded = state;
        return null;
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean canReplace() {
        return canReplace;
    }

    @Override
    public IFxInfo setCanReplace(boolean canReplace) {
        this.canReplace = canReplace;
        return this;
    }

    @Override
    public IFxInfo setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public IFxInfo setDefaultFx(boolean isDefaultFx) {
        this.isDefaultFx = isDefaultFx;
        return this;
    }

    @Override
    public boolean isDefaultFx() {
        return isDefaultFx;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BaseFxInfo setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int getResourceId() {
        return resourceId;
    }

    @Override
    public BaseFxInfo setResourceId(int resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    @Override
    public String getResourceUrl() {
        return resourceUrl;
    }

    @Override
    public BaseFxInfo setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    @Override
    public String toString() {
        return "BaseFxInfo{" +
                "name='" + name + '\'' +
                ", resourceId=" + resourceId +
                ", resourceUrl='" + resourceUrl + '\'' +
                ", type='" + type + '\'' +
                ", fxName='" + fxName + '\'' +
                ", strength=" + strength +
                ", defaultStrength=" + defaultStrength +
                ", packageId='" + packageId + '\'' +
                ", assetPackagePath='" + assetPackagePath + '\'' +
                ", fxNodes=" + fxNodes +
                ", isExpanded=" + isExpanded +
                '}';
    }
}
