package com.meishe.engine.local;

import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamCaptionClip;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Created by CaoZhiChao on 2020/7/4 11:42
 */
public class LMeicamCaptionClip extends LClipInfo implements Cloneable, Serializable {
    private static final String TAG = "MeicamCaptionClip";
    private String text;
    private String styleId;
    private float scaleX = 1;
    private float scaleY = 1;
    private float rotation = 0;
    private float translationX = 0;
    private float translationY = 0;
    private String font = "";
    private float[] textColor = {1f, 1f, 1f, 1f};
    //加粗 bold
    private boolean bold = false;
    // 斜体 italic
    private boolean italic = false;
    // 阴影 shadow
    private boolean shadow = false;
    private boolean outline = false;
    private float[] outlineColor = new float[4];
    //背景颜色 background Color
    private float[] backgroundColor = new float[4];
    //描边默认是5，范围为0-10 描边默认是5，范围为0-10 Stroke defaults to 5 and ranges from 0 to 10
    private float outlineWidth = 5;
    //背景角度 Background Angle
    private float backgroundAngle = 5;
    private float zValue;
    //100,150,200
    private float letterSpacing = 100;
    //行间距  Line spacing
    private float lineSpacing;
    /**
     * TEXT_ALIGNMENT_LEFT = 0
     * 居左对齐 默认值
     * TEXT_ALIGNMENT_CENTER = 1
     * 居中对齐
     * TEXT_ALIGNMENT_RIGHT = 2
     * 居右对齐
     */
    private int textAlign;
    private String subType = "general";
    /**
     * 花字Uuid
     * word
     */
    private String richWordUuid;
    /**
     * 气泡Uuid
     * bubble
     */
    private String bubbleUuid;
    /**
     * 组合动画Uuid
     * Composite animation
     */
    private String combinationAnimationUuid;
    /**
     * 入场动画Uuid
     * Entrance animation
     */
    private String marchInAnimationUuid;
    /**
     * 出场动画Uuid
     * Appearance animation
     */
    private String marchOutAnimationUuid;
    /**
     * 组合动画时长
     * Combined animation duration
     */
    private int combinationAnimationDuration;
    /**
     * 入场动画时长
     * Entry animation duration
     */
    private int marchInAnimationDuration;
    /**
     * 出场动画时长
     * Appearance animation duration
     */
    private int marchOutAnimationDuration;

    /**
     * 字幕类型 0：普通字幕 1 AI字幕
     * Subtitle type 0: normal subtitle 1 AI subtitle
     */
    private int operationType;

    public LMeicamCaptionClip() {
        super(CommonData.CLIP_CAPTION);
    }

    public LMeicamCaptionClip(String text, String styleId) {
        super(CommonData.CLIP_CAPTION);
        this.text = text;
        this.styleId = styleId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public float getBackgroundAngle() {
        return backgroundAngle;
    }

    public void setBackgroundAngle(float backgroundAngle) {
        this.backgroundAngle = backgroundAngle;
    }

    public float[] getTextColor() {
        return textColor;
    }

    public void setTextColor(float[] textColor) {
        this.textColor = textColor;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public boolean isOutline() {
        return outline;
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    public float[] getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(float[] outlineColor) {
        this.outlineColor = outlineColor;
    }

    public float getOutlineWidth() {
        return outlineWidth;
    }

    public void setOutlineWidth(float outlineWidth) {
        this.outlineWidth = outlineWidth;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
    }

    public int getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float[] getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(float[] backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getRichWordUuid() {
        return richWordUuid;
    }

    public void setRichWordUuid(String richWordUuid) {
        this.richWordUuid = richWordUuid;
    }

    public String getBubbleUuid() {
        return bubbleUuid;
    }

    public void setBubbleUuid(String bubbleUuid) {
        this.bubbleUuid = bubbleUuid;
    }

    public String getCombinationAnimationUuid() {
        return combinationAnimationUuid;
    }

    public void setCombinationAnimationUuid(String combinationAnimationUuid) {
        this.combinationAnimationUuid = combinationAnimationUuid;
    }

    public String getMarchInAnimationUuid() {
        return marchInAnimationUuid;
    }

    public void setMarchInAnimationUuid(String marchInAnimationUuid) {
        this.marchInAnimationUuid = marchInAnimationUuid;
    }

    public String getMarchOutAnimationUuid() {
        return marchOutAnimationUuid;
    }

    public void setMarchOutAnimationUuid(String marchOutAnimationUuid) {
        this.marchOutAnimationUuid = marchOutAnimationUuid;
    }

    public int getCombinationAnimationDuration() {
        return combinationAnimationDuration;
    }

    public void setCombinationAnimationDuration(int combinationAnimationDuration) {
        this.combinationAnimationDuration = combinationAnimationDuration;
    }

    public int getMarchInAnimationDuration() {
        return marchInAnimationDuration;
    }

    public void setMarchInAnimationDuration(int marchInAnimationDuration) {
        this.marchInAnimationDuration = marchInAnimationDuration;
    }

    public int getMarchOutAnimationDuration() {
        return marchOutAnimationDuration;
    }

    public void setMarchOutAnimationDuration(int marchOutAnimationDuration) {
        this.marchOutAnimationDuration = marchOutAnimationDuration;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    @NonNull
    @Override
    public Object clone() {
        return DeepCopyUtil.deepClone(this);
    }

    @Override
    public MeicamCaptionClip parseToTimelineData() {
        MeicamCaptionClip timelineData = new MeicamCaptionClip();
        setCommonData(timelineData);
        timelineData.setText(getText());
        timelineData.setStyleId(getStyleId());
        timelineData.setScaleX(getScaleX());
        timelineData.setScaleY(getScaleY());
        timelineData.setRotation(getRotation());
        timelineData.setTranslationX(getTranslationX());
        timelineData.setTranslationY(getTranslationY());
        timelineData.setFont(getFont());
        float[] textColor = getTextColor();
        float[] localTextColor = new float[4];
        for (int index = 0; index < textColor.length; index++) {
            localTextColor[index] = textColor[index];
        }
        timelineData.setTextColor(localTextColor);
        timelineData.setBold(isBold());
        timelineData.setItalic(isItalic());
        timelineData.setShadow(isShadow());
        timelineData.setOutline(isOutline());

        float[] outlineColor = getOutlineColor();
        float[] localOutlineColor = new float[4];
        for (int index = 0; index < textColor.length; index++) {
            localOutlineColor[index] = outlineColor[index];
        }
        timelineData.setOutlineColor(localOutlineColor);

        float[] bgColor = getBackgroundColor();
        float[] localBgColor = new float[4];
        for (int index = 0; index < bgColor.length; index++) {
            localBgColor[index] = bgColor[index];
        }
        timelineData.setBackgroundColor(localBgColor);

        timelineData.setOutlineWidth(getOutlineWidth());
        timelineData.setBackgroundAngle(getBackgroundAngle());
        timelineData.setzValue(getzValue());
        timelineData.setLetterSpacing(getLetterSpacing());
        timelineData.setLineSpacing(getLineSpacing());
        timelineData.setTextAlign(getTextAlign());
        timelineData.setRichWordUuid(getRichWordUuid());
        timelineData.setBubbleUuid(getBubbleUuid());
        timelineData.setCombinationAnimationUuid(getCombinationAnimationUuid());
        timelineData.setCombinationAnimationDuration(getCombinationAnimationDuration());
        timelineData.setMarchInAnimationUuid(getMarchInAnimationUuid());
        timelineData.setMarchInAnimationDuration(getMarchInAnimationDuration());
        timelineData.setMarchOutAnimationUuid(getMarchOutAnimationUuid());
        timelineData.setMarchOutAnimationDuration(getMarchOutAnimationDuration());
        timelineData.setOperationType(getOperationType());
        return timelineData;
    }
}
