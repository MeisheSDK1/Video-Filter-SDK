package com.meishe.sdkdemo.capture;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.meishe.sdkdemo.BR;

import java.io.Serializable;
import java.util.List;

/**
 * @Class: com.meishe.sdkdemo.capture.BeautyShapeDataItem.java
 * @Time: 2019/3/22 0022 14:18
 * @author: mlj
 * @Description: 拍摄页面美颜美型数据对象类;Beauty data class for capturing video
 */
public class BeautyShapeDataItem extends BaseObservable implements Serializable {
    public static final int EFFECT_TYPE_BEAUTY = 0;
    public static final int EFFECT_TYPE_SHAPE = 1;
    public static final int EFFECT_TYPE_MAKEUP = 2;

    private String path;
    public String beautyShapeId;
    public double intensity = 0.0d;
    public double thresh = 0.0d;
    public double radius = 0.0d;

    /**
     * 当前强度，真实的数据
     * streth real data
     */
    public double strength = 0.0d;
    /**
     * 默认强度，用于reset按钮
     *Default strength, used for reset button
     */
    public double defaultStrength = 0.0d;
    public int resId;
    public String name;
    public String type;
    public double defaultValue = 0.0d;
    /**
     * 是否是美型
     * isShape
     */
    public boolean isShape;
    /**
     * 是否是点
     * isPoint
     */
    public boolean isPoint;

    public int effectType = 0;

    private boolean visible;
    private String textDefaultColor = "#ff707070";
    private String textSelectColor = "#ff63ABFF";
    //最新美妆包
    //The latest beauty bag
    private boolean packageShapeFlag = false;
    private String warpPath;
    private String faceMeshPath;
    private String warpId;
    private String faceMeshId;
    private String warpUUID;
    private String faceUUID;
    private String warpDegree;
    private String faceDegree;
    private boolean wrapFlag = false;
    private boolean canReplace = true;

    public boolean isCanReplace() {
        return canReplace;
    }

    public void setCanReplace(boolean canReplace) {
        this.canReplace = canReplace;
    }

    public boolean isPackageShapeFlag() {
        return packageShapeFlag;
    }

    public void setPackageShapeFlag(boolean packageShapeFlag) {
        this.packageShapeFlag = packageShapeFlag;
    }

    public String getWarpPath() {
        return warpPath;
    }

    public void setWarpPath(String warpPath) {
        setPackageShapeFlag(true);
        setWrapFlag(true);
        this.warpPath = warpPath;
    }

    public String getFaceMeshPath() {
        return faceMeshPath;
    }

    public void setFaceMeshPath(String faceMeshPath) {
        setPackageShapeFlag(true);
        setWrapFlag(false);
        this.faceMeshPath = faceMeshPath;
    }

    public String getWarpId() {
        return warpId;
    }

    public void setWarpId(String warpId) {
        this.warpId = warpId;
    }

    public String getFaceMeshId() {
        return faceMeshId;
    }

    public void setFaceMeshId(String faceMeshId) {
        this.faceMeshId = faceMeshId;
    }

    public String getWarpUUID() {
        return warpUUID;
    }

    public void setWarpUUID(String warpUUID) {
        this.warpUUID = warpUUID;
    }

    public String getFaceUUID() {
        return faceUUID;
    }

    public void setFaceUUID(String faceUUID) {
        this.faceUUID = faceUUID;
    }

    public String getWarpDegree() {
        return warpDegree;
    }

    public void setWarpDegree(String warpDegree) {
        this.warpDegree = warpDegree;
    }

    public String getFaceDegree() {
        return faceDegree;
    }

    public void setFaceDegree(String faceDegree) {
        this.faceDegree = faceDegree;
    }

    public boolean isWrapFlag() {
        return wrapFlag;
    }

    public void setWrapFlag(boolean wrapFlag) {
        this.wrapFlag = wrapFlag;
    }

    private List<BeautyShapeDataItem> dataItems;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public List<BeautyShapeDataItem> getDataItems() {
        return dataItems;
    }

    public void setDataItems(List<BeautyShapeDataItem> dataItems) {
        this.dataItems = dataItems;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Bindable
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        notifyPropertyChanged(BR.visible);
    }

    public String getTextDefaultColor() {
        return textDefaultColor;
    }

    public String getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextDefaultColor(String textDefaultColor) {
        this.textDefaultColor = textDefaultColor;
    }

    public void setTextSelectColor(String textSelectColor) {
        this.textSelectColor = textSelectColor;
    }


}
