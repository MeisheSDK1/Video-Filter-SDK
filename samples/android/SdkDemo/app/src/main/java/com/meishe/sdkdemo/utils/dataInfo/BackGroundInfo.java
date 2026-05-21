package com.meishe.sdkdemo.utils.dataInfo;

import androidx.annotation.NonNull;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 * 背景数据实体类
 * Background data entity class
 *
 * @Author : zcy
 * @CreateDate : 2021/3/18.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BackGroundInfo implements Cloneable {
    float transX;
    float transY;
    float scaleX = 1;
    float scaleY = 1;
    float rotation;
    float opacity;
    float anchorX;
    float AnchorY;
    /**
     * 画布类型 见BackgroundType
     * See BackgroundType for canvas types
     */
    int type = -1;
    /**
     * 画布模糊程度值
     * Canvas blur value
     */
    float value = 100;
    /**
     * 画布颜色值
     * Canvas color value
     */
    String colorValue = "#000000";

    /**
     * 画布样式相关 文件背景地址
     * Canvas style related file background address
     */
    private String filePath;
    /**
     * 画布样式相关 文件背景资源ID
     * Canvas style associated file background resource ID
     */
    private int iconRcsId;

    public float getTransX() {
        return transX;
    }

    public void setTransX(float transX) {
        this.transX = transX;
    }

    public float getTransY() {
        return transY;
    }

    public void setTransY(float transY) {
        this.transY = transY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getIconRcsId() {
        return iconRcsId;
    }

    public void setIconRcsId(int iconRcsId) {
        this.iconRcsId = iconRcsId;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getAnchorX() {
        return anchorX;
    }

    public void setAnchorX(float anchorX) {
        this.anchorX = anchorX;
    }

    public float getAnchorY() {
        return AnchorY;
    }

    public void setAnchorY(float anchorY) {
        AnchorY = anchorY;
    }

    @NonNull
    @Override
    protected BackGroundInfo clone() {
        try {
            return (BackGroundInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class BackgroundType {
        //画布颜色 Canvas color
        public static final int BACKGROUND_COLOR = 0;
        //画布样式 Canvas style
        public static final int BACKGROUND_TYPE = 1;
        //画布模糊 Canvas blur
        public static final int BACKGROUND_BLUR = 2;
    }
}
