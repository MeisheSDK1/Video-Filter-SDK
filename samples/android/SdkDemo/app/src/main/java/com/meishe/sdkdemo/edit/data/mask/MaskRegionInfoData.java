package com.meishe.sdkdemo.edit.data.mask;

/**
 * @author :Jml
 * @date :2020/9/21 16:07
 * @des : 构建NvRegionInfo 使用的数据源
 * Build the data source used by NvRegionInfo
 */
public class MaskRegionInfoData {
    private float centerX;
    private float centerY;
    /**
     * 蒙版宽度
     * Mask Width
     */
    private int maskWidth;
    /**
     * 蒙版高度
     * Mask Height
     */
    private int maskHeight;
    /**
     * 旋转角度
     * Angle of rotation
     */
    private int rotation;
    /**
     * 蒙版类型 0--6
     * Mask type 0--6
     */
    private int type;
    /**
     * item 名字
     * item name
     */
    private String itemName;
    /**
     * 图标
     * icon
     */
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

    public class MaskType {
        public static final int NONE = 0;
        public static final int LINE = 1;
        public static final int MIRROR = 2;
        public static final int CIRCLE = 3;
        public static final int RECT = 4;
        public static final int HEART = 5;
        public static final int STAR = 6;

    }
}
