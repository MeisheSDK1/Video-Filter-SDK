package com.meishe.engine.local;

import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.bean.MaskRegionInfoData;

import java.io.Serializable;

/**
 * @author :Jml
 * @date :2020/9/21 16:07
 * @des : 构建NvRegionInfo 使用的数据源
 */
public class LMaskRegionInfoData  implements Cloneable, Serializable, TimelineDataToLocalAdapter<MaskRegionInfoData> {
    private float mCenterX;
    private float mCenterY;
    /**
     * 蒙版宽度
     * Mask width
     */
    private int mMaskWidth;
    /**
     * 蒙版高度
     * Mask height
     */
    private int mMashHeight;
    /**
     * 旋转角度
     * Angle of rotation
     */
    private int mRotation;
    /**
     * 蒙版类型 0--6
     * Mask Type
     */
    private int mType;
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

    public float getmCenterX() {
        return mCenterX;
    }

    public void setmCenterX(float mCenterX) {
        this.mCenterX = mCenterX;
    }

    public float getmCenterY() {
        return mCenterY;
    }

    public void setmCenterY(float mCenterY) {
        this.mCenterY = mCenterY;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getMaskWidth() {
        return mMaskWidth;
    }

    public void setMaskWidth(int mMaskWidth) {
        this.mMaskWidth = mMaskWidth;
    }

    public int getMaskHeight() {
        return mMashHeight;
    }

    public void setMaskHeight(int mMashHeight) {
        this.mMashHeight = mMashHeight;
    }

    public int getmRotation() {
        return mRotation;
    }

    public void setmRotation(int mRotation) {
        this.mRotation = mRotation;
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

    public float getRoundCornerRate() {
        return roundCornerRate;
    }
    @Override
    public MaskRegionInfoData parseToLocalData() {
        MaskRegionInfoData maskRegionInfoData = new MaskRegionInfoData();
        maskRegionInfoData.setDrawableIcon(getDrawableIcon());
        maskRegionInfoData.setFeatherWidth(getFeatherWidth());
        maskRegionInfoData.setItemName(getItemName());
        maskRegionInfoData.setCenterX(getmCenterX());
        maskRegionInfoData.setCenterY(getmCenterY());
        maskRegionInfoData.setMaskHeight(getMaskHeight());
        maskRegionInfoData.setMaskWidth(getMaskWidth());
        maskRegionInfoData.setRotation(getmRotation());
        maskRegionInfoData.setType(getmType());
        maskRegionInfoData.setReverse(isReverse());
        maskRegionInfoData.setRoundCornerRate(getRoundCornerRate());
        maskRegionInfoData.setTranslationX(getTranslationX());
        maskRegionInfoData.setTranslationY(getTranslationY());
        maskRegionInfoData.setHorizontalScale(getHorizontalScale());
        maskRegionInfoData.setVerticalScale(getVerticalScale());
        return maskRegionInfoData;
    }

    public class MaskType{
        public static final int NONE = 0;
        public static final int LINE = 1;
        public static final int MIRROR = 2;
        public static final int CIRCLE = 3;
        public static final int RECT = 4;
        public static final int HEART = 5;
        public static final int STAR = 6;

    }
}
