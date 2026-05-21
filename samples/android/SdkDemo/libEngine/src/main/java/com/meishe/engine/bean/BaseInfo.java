package com.meishe.engine.bean;

import com.meishe.base.utils.GsonUtils;
import com.meishe.engine.interf.IBaseInfo;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/1/18 10:14
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseInfo implements IBaseInfo {
    /**
     * 内建特效
     * Built-in effects
     */
    public static int EFFECT_MODE_BUILTIN = 0;
    /**
     * Asset中预装
     * Pre-installed in Asset
     */
    public static int EFFECT_MODE_BUNDLE = 1;
    /**
     * 包裹特效
     * Package effects
     */
    public static int EFFECT_MODE_PACKAGE = 2;

    /**
     * 不适配比例
     * Unfit ratio
     * */
    public static final int AspectRatio_NoFitRatio = 0;
    public static final int AspectRatio_16v9 = 1;
    public static final int AspectRatio_1v1 = 2;
    public static final int AspectRatio_9v16 = 4;
    public static final int AspectRatio_4v3 = 8;
    public static final int AspectRatio_3v4 = 16;

    public static final int AspectRatio_18v9 = 32;
    public static final int AspectRatio_9v18 = 64;
    public static final int AspectRatio_21v9 = 512;
    public static final int AspectRatio_9v21 = 1024;
    public static final int AspectRatio_All = AspectRatio_16v9
            | AspectRatio_1v1
            | AspectRatio_9v16
            | AspectRatio_3v4
            | AspectRatio_4v3
            | AspectRatio_18v9
            | AspectRatio_9v18
            | AspectRatio_21v9
            | AspectRatio_9v21;

    private String name;
    private int type;
    private String coverPath;
    private int coverId;
    private String commonInfo;
    private boolean isAuthorized;
    private Object tag;
    private String description;
    private long duration;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getCoverPath() {
        return coverPath;
    }

    @Override
    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    @Override
    public int getCoverId() {
        return coverId;
    }

    @Override
    public void setCoverId(int resId) {
        coverId = resId;
    }

    @Override
    public String getPackageId() {
        return null;
    }

    @Override
    public void setPackageId(String packageId) {

    }

    @Override
    public String getAssetPath() {
        return null;
    }

    @Override
    public void setAssetPath(String assetPath) {

    }

    @Override
    public String getEffectId() {
        return null;
    }

    @Override
    public void setEffectId(String effectId) {

    }

    @Override
    public void setEffectMode(int effectMode) {

    }

    @Override
    public int getEffectMode() {
        return 0;
    }

    @Override
    public void setEffectStrength(float strength) {

    }

    @Override
    public float getEffectStrength() {
        return 0;
    }

    @Override
    public void setCommonInfo(String info) {
        this.commonInfo = info;
    }

    @Override
    public String getCommonInfo() {
        return commonInfo;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public IBaseInfo copy() {

        return GsonUtils.fromJson(GsonUtils.toJson(this), getClass());
    }

    @Override
    public void update(IBaseInfo newInfo) {
        if (newInfo != null) {
            setId(newInfo.getId());
            setName(newInfo.getName());
            setType(newInfo.getType());
            setCoverPath(newInfo.getCoverPath());
            setAssetPath(newInfo.getAssetPath());
            setCoverId(newInfo.getCoverId());
            setCommonInfo(newInfo.getCommonInfo());
            setPackageId(newInfo.getPackageId());
            setEffectId(newInfo.getEffectId());
            setEffectMode(newInfo.getEffectMode());
            setEffectStrength(newInfo.getEffectStrength());
            setAuthorized(newInfo.isAuthorized());
            setTag(getTag());
            setDescription(getDescription());
            setDuration(getDuration());
        }
    }

    @Override
    public void setTag(Object obj) {
        this.tag = obj;
    }

    @Override
    public Object getTag() {
        return tag;
    }
}


