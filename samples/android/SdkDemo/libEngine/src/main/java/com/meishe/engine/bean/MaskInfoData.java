package com.meishe.engine.bean;

import android.graphics.PointF;

/**
 * @author :Jml
 * @date :2020/9/18 15:23
 * @des : 蒙版的列表使用的数据源
 * Mask the list of data sources used
 */
public class MaskInfoData extends BaseInfo {
    /**
     * 选择的锚点
     * The selected anchor point
     */
    private PointF center;
    /**
     * 蒙版宽度
     * Mask width
     */
    private int maskWidth;
    /**
     * 蒙版高度
     * Mask height
     */
    private int mashHeight;

    /**
     * 初始化蒙版宽度
     * Initialize the mask width
     */
    private int initWidth;
    /**
     * 初始化蒙版高度
     * Initialize the mask height
     */
    private int initHeight;

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

    /**
     * 旋转角度
     * Angle of rotation
     */
    private int rotation;
    /**
     * 蒙版类型 0--6
     * Mask Type
     */
    private int makType;
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
    private float roundCornerWidthRate;

    private float translationX;

    private float translationY;


    /**
     * Gets center.
     * 获取居中
     *
     * @return the center
     */
    public PointF getCenter() {
        return center;
    }

    /**
     * Sets center.
     * 设置居中
     *
     * @param mCenter the m center
     */
    public void setCenter(PointF mCenter) {
        this.center = mCenter;
    }

    /**
     * Gets type.
     * 获取类型
     *
     * @return the type
     */
    public int getMaskType() {
        return makType;
    }

    /**
     * Sets type.
     * 设置类型
     *
     * @param mType the m type
     */
    public void setMaskType(int mType) {
        this.makType = mType;
    }

    /**
     * Gets mask width.
     * 获取面具宽度
     *
     * @return the mask width
     */
    public int getMaskWidth() {
        return maskWidth;
    }

    /**
     * Sets mask width.
     * 设置蒙版宽度
     *
     * @param mMaskWidth the m mask width
     */
    public void setMaskWidth(int mMaskWidth) {
        this.maskWidth = mMaskWidth;
    }

    /**
     * Gets mash height.
     * 获取蒙版高度
     *
     * @return the mash height
     */
    public int getMaskHeight() {
        return mashHeight;
    }

    /**
     * Sets mash height.
     * 设置蒙版高度
     *
     * @param mMashHeight the m mash height
     */
    public void setMaskHeight(int mMashHeight) {
        this.mashHeight = mMashHeight;
    }


    public int getInitWidth() {
        return initWidth;
    }

    public void setInitWidth(int initWidth) {
        this.initWidth = initWidth;
    }

    public int getInitHeight() {
        return initHeight;
    }

    public void setInitHeight(int initHeight) {
        this.initHeight = initHeight;
    }

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

    /**
     * Gets rotation.
     * 获取旋转
     *
     * @return the rotation
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets rotation.
     * 设置旋转
     *
     * @param rotation the m rotation
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * Sets feather width.
     * 设置羽化宽度
     *
     * @param featherWidth the feather width
     */
    public void setFeatherWidth(float featherWidth) {
        this.featherWidth = featherWidth;
    }

    /**
     * Sets reverse.
     * 设置逆向
     *
     * @param reverse the reverse
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Gets feather width.
     * 获取羽化宽度
     *
     * @return the feather width
     */
    public float getFeatherWidth() {
        return featherWidth;
    }

    /**
     * Is reverse boolean.
     * 是反向布尔
     *
     * @return the boolean
     */
    public boolean isReverse() {
        return reverse;
    }

    /**
     * Gets translation x.
     * 获取平移x
     *
     * @return the translation x
     */
    public float getTranslationX() {
        return translationX;
    }

    /**
     * Gets translation y.
     * 获取平移y
     *
     * @return the translation y
     */
    public float getTranslationY() {
        return translationY;
    }

    /**
     * Sets translation x.
     * 设置平移x
     *
     * @param translationX the translation x
     */
    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    /**
     * Sets translation y.
     * 设置平移y
     *
     * @param translationY the translation y
     */
    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }

    /**
     * Sets round corner width rate.
     * 设置圆角宽度率
     *
     * @param roundCornerWidthRate the round corner width rate
     */
    public void setRoundCornerWidthRate(float roundCornerWidthRate) {
        this.roundCornerWidthRate = roundCornerWidthRate;
    }

    /**
     * Gets round corner width rate.
     * 获取圆角宽度率
     *
     * @return the round corner width rate
     */
    public float getRoundCornerWidthRate() {
        return roundCornerWidthRate;
    }

    /**
     * The type Mask type.
     * 类型掩码类型
     */
    public class MaskType {
        /**
         * The constant NONE.
         * 常数没有
         */
        public static final int NONE = 0;
        /**
         * The constant LINE.
         * 不断线
         */
        public static final int LINE = 1;
        /**
         * The constant MIRROR.
         * 不断的镜子
         */
        public static final int MIRROR = 2;
        /**
         * The constant CIRCLE.
         * 不断循环
         */
        public static final int CIRCLE = 3;
        /**
         * The constant RECT.
         * 矩形
         */
        public static final int RECT = 4;
        /**
         * The constant HEART.
         * 心形
         */
        public static final int HEART = 5;
        /**
         * The constant STAR.
         * 星形
         */
        public static final int STAR = 6;

    }


    public MaskInfoData createMaskInfoData() {
        MaskInfoData maskDataNew = new MaskInfoData();
        maskDataNew.setMaskWidth(getMaskWidth());
        maskDataNew.setMaskHeight(getMaskHeight());
        maskDataNew.setTranslationX(getTranslationX());
        maskDataNew.setTranslationY(getTranslationY());
        maskDataNew.setCenter(getCenter());
        maskDataNew.setRotation(getRotation());
        maskDataNew.setReverse(isReverse());
        maskDataNew.setFeatherWidth(getFeatherWidth());
        maskDataNew.setRoundCornerWidthRate(getRoundCornerWidthRate());
        maskDataNew.setMaskType(getMaskType());
        maskDataNew.setHorizontalScale(getHorizontalScale());
        maskDataNew.setVerticalScale(getVerticalScale());
        maskDataNew.setInitWidth(getInitWidth());
        maskDataNew.setInitHeight(getInitHeight());
        return maskDataNew;
    }
}
