package com.meishe.engine.bean;

import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMaskRegionInfoData;

import java.io.Serializable;

/**
 * @author :Jml
 * @date :2020/9/21 16:07
 * @des : 构建NvRegionInfo 使用的数据源
 */
public class MaskRegionInfoData implements Cloneable, Serializable, TimelineDataToLocalAdapter<LMaskRegionInfoData> {
    private float centerX;
    private float centerY;
    /**
     * 蒙版宽度
     * Mask width
     */
    private int maskWidth;
    /**
     * 蒙版高度
     * Mask height
     */
    private int maskHeight;
    /**
     * 旋转角度
     * Angle of rotation
     */
    private int rotation;
    /**
     * 蒙版类型 0--6
     * Mask Type
     */
    private int type;
    private String itemName;
    private int drawableIcon;
    /**
     * 区域反转
     * Regional inversion
     */
    private boolean reverse;
    /**
     * 羽化值
     * Emergence value
     */
    private float featherWidth;

    private float roundCornerRate;
    private float translationX;

    private float translationY;

    /**
     * 水平缩放值
     * Horizontal scaling value
     */
    private float horizontalScale = 1F;

    /**
     * 竖直缩放值
     * Vertical scaling value
     */
    private float verticalScale = 1F;


    public float getHorizontalScale() {
        return horizontalScale;
    }

    public void setHorizontalScale(float horizontalScale) {
        this.horizontalScale = horizontalScale;
    }

    public float getVerticalScale() {
        return verticalScale;
    }

    public void setVerticalScale(float verticalScale) {
        this.verticalScale = verticalScale;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setDrawableIcon(int drawableIcon) {
        this.drawableIcon = drawableIcon;
    }

    public int getDrawableIcon() {
        return drawableIcon;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaskWidth() {
        return maskWidth;
    }

    public void setMaskWidth(int mMaskWidth) {
        this.maskWidth = mMaskWidth;
    }

    public int getMaskHeight() {
        return maskHeight;
    }

    public void setMaskHeight(int mMashHeight) {
        this.maskHeight = mMashHeight;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setFeatherWidth(float featherWidth) {
        this.featherWidth = featherWidth;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public float getFeatherWidth() {
        return featherWidth;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setRoundCornerRate(float roundCornerRate) {
        this.roundCornerRate = roundCornerRate;
    }

    public float getRoundCornerRate() {
        return roundCornerRate;
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

    @Override
    public LMaskRegionInfoData parseToLocalData() {
        LMaskRegionInfoData lMaskRegionInfoData = new LMaskRegionInfoData();
        lMaskRegionInfoData.setDrawableIcon(getDrawableIcon());
        lMaskRegionInfoData.setFeatherWidth(getFeatherWidth());
        lMaskRegionInfoData.setItemName(getItemName());
        lMaskRegionInfoData.setmCenterX(getCenterX());
        lMaskRegionInfoData.setmCenterY(getCenterY());
        lMaskRegionInfoData.setMaskHeight(getMaskHeight());
        lMaskRegionInfoData.setMaskWidth(getMaskWidth());
        lMaskRegionInfoData.setmRotation(getRotation());
        lMaskRegionInfoData.setmType(getType());
        lMaskRegionInfoData.setReverse(isReverse());
        lMaskRegionInfoData.setRoundCornerRate(getRoundCornerRate());
        lMaskRegionInfoData.setTranslationX(getTranslationX());
        lMaskRegionInfoData.setTranslationY(getTranslationY());
        lMaskRegionInfoData.setHorizontalScale(getHorizontalScale());
        lMaskRegionInfoData.setVerticalScale(getVerticalScale());
        return lMaskRegionInfoData;
    }
}
