package com.meishe.sdkdemo.edit.data.mask;

import android.graphics.PointF;

import com.meicam.sdk.NvsMaskRegionInfo;
import com.meishe.sdkdemo.edit.data.BaseInfo;

import androidx.annotation.NonNull;

/**
 * @author :Jml
 * @date :2020/9/18 15:23
 * @des : 蒙版的列表使用的数据源
 * Mask the list of data sources used
 */
public class MaskInfoData extends BaseInfo implements Cloneable {
    /**
     * liveWindow的中心点
     * The center point of liveWindow
     */
    private PointF liveWindowCenter;
    /**
     * 放大缩小
     * Zoom out
     */
    private float scale = 1;
    /**
     * 蒙版宽度
     * Mask width
     */
    private int maskWidth;
    /**
     * 蒙版高度
     * Mask Height
     */
    private int mashHeight;
    /**
     * 旋转角度
     * Angle of rotation
     */
    private float rotation;
    /**
     * 蒙版类型 0--6
     * Mask type 0--6
     */
    private int type;
    /**
     * 区域反转
     * Regional inversion
     */
    private boolean reverse = false;
    /**
     * 羽化值
     * Emergence value
     */
    private float featherWidth;
    /**
     * 圆角值
     * Fillet value
     */
    private float roundCornerWidthRate;
    /**
     * 背景资源id
     * cover
     */
    private int coverId;
    private String name;
    /**
     * 蒙版字体样式
     * Mask font style
     */
    private String text = "";
    /**
     * 蒙版字幕单行最宽
     */
    private float textWidth;
    /**
     * 蒙版字幕高度
     * The mask has the widest single line
     */
    private float textHeight;
    /**
     * 蒙版字幕单行最高
     * Mask the highest single line
     */
    private float singleTextHeight;
    /**
     * 蒙版字幕storyboard
     * Mask the storyboard
     */
    private String textStoryboard;

    private float textSize = 100;
    private int translationX;

    private int translationY;

    private NvsMaskRegionInfo maskRegionInfo;

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getSingleTextHeight() {
        return singleTextHeight;
    }

    public void setSingleTextHeight(float singleTextHeight) {
        this.singleTextHeight = singleTextHeight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextStoryboard() {
        return textStoryboard;
    }

    public void setTextStoryboard(String textStoryboard) {
        this.textStoryboard = textStoryboard;
    }

    public PointF getLiveWindowCenter() {
        return liveWindowCenter;
    }

    public void setLiveWindowCenter(PointF liveWindowCenter) {
        this.liveWindowCenter = liveWindowCenter;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * Gets type.
     * 获取类型
     *
     * @return the type
     */
    public int getMaskType() {
        return type;
    }

    /**
     * Sets type.
     * 设置类型
     *
     * @param mType the m type
     */
    public void setMaskType(int mType) {
        this.type = mType;
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

    /**
     * Gets rotation.
     * 获取旋转
     *
     * @return the rotation
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Sets rotation.
     * 设置旋转
     *
     * @param rotation the m rotation
     */
    public void setRotation(float rotation) {
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
    public int getTranslationX() {
        return translationX;
    }

    /**
     * Gets translation y.
     * 获取平移y
     *
     * @return the translation y
     */
    public int getTranslationY() {
        return translationY;
    }

    /**
     * Sets translation x.
     * 设置平移x
     *
     * @param translationX the translation x
     */
    public void setTranslationX(int translationX) {
        this.translationX = translationX;
    }

    /**
     * Sets translation y.
     * 设置平移y
     *
     * @param translationY the translation y
     */
    public void setTranslationY(int translationY) {
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

    public int getCoverId() {
        return coverId;
    }

    public void setCoverId(int coverId) {
        this.coverId = coverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NvsMaskRegionInfo getMaskRegionInfo() {
        return maskRegionInfo;
    }

    public void setMaskRegionInfo(NvsMaskRegionInfo maskRegionInfo) {
        this.maskRegionInfo = maskRegionInfo;
    }

//    public MaskInfoData clone() {
//        MaskInfoData maskInfoData =new MaskInfoData();
//        maskInfoData.setTextSize(getTextSize());
//        maskInfoData.setText(getText());
//        maskInfoData.setScale(getScale());
//        maskInfoData.setMaskWidth(getMaskWidth());
//        maskInfoData.setMaskHeight(getMaskHeight());
//        maskInfoData.setTextStoryboard(getTextStoryboard());
//        maskInfoData.setLiveWindowCenter(getLiveWindowCenter());
//        maskInfoData.setReverse(isReverse());
//        maskInfoData.setTranslationY(getTranslationY());
//        maskInfoData.setTranslationX(getTranslationX());
//        maskInfoData.setRoundCornerWidthRate(getRoundCornerWidthRate());
//        maskInfoData.setMaskType(getMaskType());
//        maskInfoData.setFeatherWidth(getFeatherWidth());
//        maskInfoData.setCoverId(getCoverId());
//        maskInfoData.setRotation(getRotation());
//        maskInfoData.setMaskRegionInfo(getMaskRegionInfo());
//        maskInfoData.setName(getName());
//        maskInfoData.setSingleTextHeight(getSingleTextHeight());
//        return maskInfoData;
//    }


    @NonNull
    @Override
    public MaskInfoData clone() {
        try {
            return (MaskInfoData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "MaskInfoData{" +
                "liveWindowCenter=X" + liveWindowCenter.x + " liveWindowCenterY=" + liveWindowCenter.y +
                ", scale=" + scale +
                ", maskWidth=" + maskWidth +
                ", mashHeight=" + mashHeight +
                ", rotation=" + rotation +
                ", type=" + type +
                ", reverse=" + reverse +
                ", featherWidth=" + featherWidth +
                ", roundCornerWidthRate=" + roundCornerWidthRate +
                ", coverId=" + coverId +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", textWidth=" + textWidth +
                ", textHeight=" + textHeight +
                ", singleTextHeight=" + singleTextHeight +
                ", textStoryboard='" + textStoryboard + '\'' +
                ", textSize=" + textSize +
                ", translationX=" + translationX +
                ", translationY=" + translationY +
                ", maskRegionInfo=" + maskRegionInfo.toString() +
                '}';
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
        /**
         * The constant Caption
         * 字幕蒙版
         */
        public static final int TEXT = 7;

    }
}
