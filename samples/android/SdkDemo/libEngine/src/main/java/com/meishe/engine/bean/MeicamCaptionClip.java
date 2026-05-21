package com.meishe.engine.bean;

import android.graphics.PointF;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.engine.local.LMeicamCaptionClip;
import com.meishe.engine.util.ColorUtil;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by CaoZhiChao on 2020/7/4 11:42
 */
public class MeicamCaptionClip extends ClipInfo<NvsTimelineCaption> implements Cloneable, Serializable {
    private static final String TAG = "MeicamCaptionClip";
    /**
     * The constant CAPTION_ALIGN_LEFT.
     * 常数CAPTION_ALIGN_LEFT
     */
    public static final int CAPTION_ALIGN_LEFT = 0;
    /**
     * The constant CAPTION_ALIGN_HORIZ_CENTER.
     * 常数CAPTION_ALIGN_HORIZ_CENTER
     */
    public static final int CAPTION_ALIGN_HORIZ_CENTER = 1;
    /**
     * The constant CAPTION_ALIGN_RIGHT.
     * 常数CAPTION_ALIGN_RIGHT
     */
    public static final int CAPTION_ALIGN_RIGHT = 2;
    /**
     * The constant CAPTION_ALIGN_TOP.
     * 常数CAPTION_ALIGN_TOP
     */
    public static final int CAPTION_ALIGN_TOP = 3;
    /**
     * The constant CAPTION_ALIGN_VERT_CENTER.
     * 常数CAPTION_ALIGN_VERT_CENTER
     */
    public static final int CAPTION_ALIGN_VERT_CENTER = 4;
    /**
     * The constant CAPTION_ALIGN_BOTTOM.
     * 常数CAPTION_ALIGN_BOTTOM
     */
    public static final int CAPTION_ALIGN_BOTTOM = 5;

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
    //背景颜色 Background Color
    private float[] backgroundColor = new float[4];
    //描边默认是5，范围为0-10 Stroke defaults to 5 and ranges from 0 to 10
    private float outlineWidth = 5;
    //背景角度  Background Angle
    private float backgroundAngle = 5;
    private float zValue;
    //100,150,200
    private float letterSpacing = 100;
    //行间距 Line spacing
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

    public MeicamCaptionClip() {
        super(CommonData.CLIP_CAPTION);
    }

    public MeicamCaptionClip(String text, String styleId) {
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
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setCaptionTranslation(new PointF(translationX, getTranslationY()));
        }
        this.translationX = translationX;
    }

    public void setTranslation(PointF captionTranslation) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setCaptionTranslation(captionTranslation);
        }
        this.translationX = captionTranslation.x;
        this.translationY = captionTranslation.y;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setCaptionTranslation(new PointF(getTranslationX(), translationY));
        }
        this.translationY = translationY;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setFontFamily(font);
        }
        this.font = font;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setLineSpacing(lineSpacing);
        }
        this.lineSpacing = lineSpacing;
    }

    public float[] getTextColor() {
        return textColor;
    }

    public void setTextColor(float[] textColor) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            NvsColor nvsColor = ColorUtil.colorFloatToNvsColor(textColor);
            if (nvsColor != null) {
                object.setTextColor(nvsColor);
            }
        }
        this.textColor = textColor;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setBold(bold);
        }
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setItalic(italic);
        }
        this.italic = italic;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setDrawShadow(shadow);
        }
        this.shadow = shadow;
    }

    public boolean isOutline() {
        return outline;
    }

    public void setOutline(boolean outline) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setDrawOutline(outline);
        }
        this.outline = outline;
    }

    public float[] getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(float[] outlineColor) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            NvsColor nvsColor = ColorUtil.colorFloatToNvsColor(outlineColor);
            if (nvsColor != null) {
                object.setDrawOutline(true);
                object.setOutlineColor(nvsColor);
            }
        }
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
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setLetterSpacing(letterSpacing);
        }
        this.letterSpacing = letterSpacing;
    }

    public int getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public float[] getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(float[] backgroundColor) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            NvsColor nvsColor = ColorUtil.colorFloatToNvsColor(backgroundColor);
            if (nvsColor != null) {
                object.setBackgroundColor(nvsColor);
            }
        }
        this.backgroundColor = backgroundColor;
    }

    public float getBackgroundAngle() {
        return backgroundAngle;
    }

    public void setBackgroundAngle(float backgroundAngle) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.setBackgroundRadius(backgroundAngle);
        }
        this.backgroundAngle = backgroundAngle;
    }

    public String getRichWordUuid() {
        return richWordUuid;
    }

    public void setRichWordUuid(String richWordUuid) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.applyModularCaptionRenderer(richWordUuid);
        }
        this.richWordUuid = richWordUuid;
    }

    public String getBubbleUuid() {
        return bubbleUuid;
    }

    public void setBubbleUuid(String bubbleUuid) {
        NvsTimelineCaption object = getObject();
        if (object != null) {
            object.applyModularCaptionContext(bubbleUuid);
        }
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

    public NvsTimelineCaption bindToTimeline(NvsTimeline timeline) {
        return bindToTimeline(timeline, true, false);
    }


    public NvsTimelineCaption bindToTimeline(NvsTimeline timeline, boolean isNeedUpdateAttribute, boolean isIdentify) {
        NvsTimelineCaption caption = timeline.addModularCaption(getText(), getInPoint(),
                getOutPoint() - getInPoint());
        if (caption == null) {
            return null;
        }
        caption.setClipAffinityEnabled(false);
//        caption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
        setObject(caption);
        Log.e(TAG, "bindToTimeline: " + caption.getTextColor().r + "  " + caption.getTextColor().g + "   " + caption.getTextColor().b + "   "
                + caption.getTextColor().a);
        if (isIdentify) {
            NvsVideoResolution videoRes = timeline.getVideoRes();
            if (videoRes != null) {
                if (videoRes.imageHeight > videoRes.imageWidth) {
                    setScaleX(0.4f);
                    setScaleY(0.4f);
                } else {
                    setScaleX(0.6f);
                    setScaleY(0.6f);
                }
            }
            caption.setScaleX(getScaleX());
            caption.setScaleY(getScaleY());
            float y;
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list != null && list.size() >= 4) {
                y = Math.abs(list.get(1).y - list.get(0).y) / 2 - timeline.getVideoRes().imageHeight / 2f + 40;
                setTranslationY(y);
            }
        }

        if (isNeedUpdateAttribute) {
            updateCaptionAttribute(caption, this);
        }
        return caption;
    }

    private static void updateCaptionAttribute(NvsTimelineCaption newCaption, MeicamCaptionClip meicamCaptionClip) {
        if (newCaption == null) {
            return;
        }
        if (meicamCaptionClip == null) {
            return;
        }
        String styleUuid = meicamCaptionClip.getStyleId();
        newCaption.applyCaptionStyle(styleUuid);

        if (!TextUtils.isEmpty(meicamCaptionClip.getCombinationAnimationUuid())) {
            //优先使用组合动画，和入场、出场动画互斥 The combination animation is preferred, and the entry and exit animation are mutually exclusive
            newCaption.applyModularCaptionAnimation(meicamCaptionClip.getCombinationAnimationUuid());
            if (meicamCaptionClip.getCombinationAnimationDuration() >= 0) {
                newCaption.setModularCaptionAnimationPeroid(meicamCaptionClip.getCombinationAnimationDuration());
            }
        } else {
            newCaption.applyModularCaptionInAnimation(meicamCaptionClip.getMarchInAnimationUuid());
            int maxDuration = (int) ((newCaption.getOutPoint() - newCaption.getInPoint()) / 1000);
            if (meicamCaptionClip.getMarchInAnimationDuration() >= 0) {
                if (maxDuration - meicamCaptionClip.getMarchInAnimationDuration() < 500) {
                    //如果设置的入动画时间后，剩余的默认时间小于500毫秒（出入动画默认时长500ms，不论设置不设置出动画）
                    //If the default time remaining is less than 500ms after the animation in time is set (the default time for animation in and out is 500ms, whether the animation is set or not)
                    newCaption.setModularCaptionOutAnimationDuration(maxDuration - meicamCaptionClip.getMarchInAnimationDuration());
                }
                //先后顺序不可乱，因为出入动画默认时长500ms，不论设置不设置出动画
                //The sequence should not be disorderly, because the default duration of animation in and out is 500ms, whether the animation is set or not
                newCaption.setModularCaptionInAnimationDuration(meicamCaptionClip.getMarchInAnimationDuration());
            }
            newCaption.applyModularCaptionOutAnimation(meicamCaptionClip.getMarchOutAnimationUuid());
            if (meicamCaptionClip.getMarchOutAnimationDuration() >= 0) {
                if (maxDuration - meicamCaptionClip.getMarchOutAnimationDuration() < 500) {
                    //如果设置的出动画时间后，剩余的默认时间小于500毫秒（出入动画默认时长500ms，不论设置不设置出动画）
                    //If the default time remaining after the animation time is set is less than 500ms (the default time for entering and leaving the animation is 500ms, whether the animation is set or not)
                    newCaption.setModularCaptionInAnimationDuration(maxDuration - meicamCaptionClip.getMarchOutAnimationDuration());
                }
                //先后顺序不可乱，因为出入动画默认时长500ms，不论设置不设置出动画
                //The sequence should not be disorderly, because the default duration of animation in and out is 500ms, whether the animation is set or not
                newCaption.setModularCaptionOutAnimationDuration(meicamCaptionClip.getMarchOutAnimationDuration());
            }
        }

        int alignVal = meicamCaptionClip.getTextAlign();
        if (alignVal >= 0) {
            newCaption.setTextAlignment(alignVal);
        }
        float[] textColorMeicam = meicamCaptionClip.getTextColor();
        NvsColor textColor = ColorUtil.colorFloatToNvsColor(meicamCaptionClip.getTextColor());
        if (textColor != null) {
            if (textColorMeicam[0] != 1.0 || textColorMeicam[1] != 1.0 || textColorMeicam[2] != 1.0 || textColorMeicam[3] != 1.0) {
                newCaption.setTextColor(textColor);
            }
        }

        /*
         * 放缩字幕
         * Shrink captions
         * */
        float scaleFactorX = meicamCaptionClip.getScaleX();
        float scaleFactorY = meicamCaptionClip.getScaleY();
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);
        float rotation = meicamCaptionClip.getRotation();
        /*
         * 旋转字幕
         * Spin subtitles
         * */
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(meicamCaptionClip.getzValue());
        boolean hasOutline = meicamCaptionClip.isOutline();
        newCaption.setDrawOutline(hasOutline);
        if (hasOutline) {
            NvsColor outlineColor = ColorUtil.colorFloatToNvsColor(meicamCaptionClip.getOutlineColor());
            if (outlineColor != null) {
                newCaption.setOutlineColor(outlineColor);
                newCaption.setOutlineWidth(meicamCaptionClip.getOutlineWidth());
            }
        }
        NvsColor backgroundColor = ColorUtil.colorFloatToNvsColor(meicamCaptionClip.getBackgroundColor());
        if (backgroundColor != null) {
            newCaption.setBackgroundColor(backgroundColor);
        }

        String fontPath = meicamCaptionClip.getFont();
        if (!fontPath.isEmpty()) {
            newCaption.setFontByFilePath(fontPath);
        }
        newCaption.setBold(meicamCaptionClip.isBold());
        newCaption.setItalic(meicamCaptionClip.isItalic());
        newCaption.setDrawShadow(meicamCaptionClip.isShadow());
        newCaption.setBackgroundRadius(meicamCaptionClip.getBackgroundAngle());
        PointF translation = new PointF(meicamCaptionClip.getTranslationX(), meicamCaptionClip.getTranslationY());
        newCaption.setCaptionTranslation(translation);

        /*
         * 应用字符间距
         * Apply character spacing
         * */
        float letterSpacing = meicamCaptionClip.getLetterSpacing();
        newCaption.setLetterSpacing(letterSpacing);
        float lineSpacing = meicamCaptionClip.getLineSpacing();
        newCaption.setLineSpacing(lineSpacing);


        newCaption.setFontFamily(meicamCaptionClip.getFont());

        if (!TextUtils.isEmpty(meicamCaptionClip.getRichWordUuid())) {
            newCaption.applyModularCaptionRenderer(meicamCaptionClip.getRichWordUuid());
        }
        if (!TextUtils.isEmpty(meicamCaptionClip.getBubbleUuid())) {
            newCaption.applyModularCaptionContext(meicamCaptionClip.getBubbleUuid());
        }
    }


    @Override
    public void loadData(NvsTimelineCaption caption) {
        if (caption == null) {
            return;
        }
        setObject(caption);
        setInPoint(caption.getInPoint());
        setOutPoint(caption.getOutPoint());
        text = caption.getText();
        styleId = caption.getCaptionStylePackageId();
        NvsColor color = caption.getTextColor();
        if (textColor != null && color != null) {
            textColor[0] = color.r;
            textColor[1] = color.g;
            textColor[2] = color.b;
            textColor[3] = color.a;
        }
        PointF point = caption.getCaptionTranslation();
        if (point != null) {
            translationX = point.x;
            translationY = point.y;
        }
        scaleX = caption.getScaleX();
        scaleY = caption.getScaleY();
        rotation = caption.getRotationZ();
        letterSpacing = caption.getLetterSpacing();
        lineSpacing = caption.getLineSpacing();
        font = caption.getFontFamily();
        bold = caption.getBold();
        italic = caption.getItalic();
        shadow = caption.getDrawShadow();
        outline = caption.getDrawOutline();
        zValue = (int) caption.getZValue();
        textAlign = caption.getTextAlignment();
        backgroundAngle = caption.getBackgroundRadius();
        NvsColor nvsColor = caption.getOutlineColor();
        if (outlineColor != null && nvsColor != null) {
            outlineColor[3] = nvsColor.a;
            outlineColor[0] = nvsColor.r;
            outlineColor[1] = nvsColor.g;
            outlineColor[2] = nvsColor.b;
        }

        NvsColor nvsBgColor = caption.getBackgroundColor();
        if (backgroundColor != null && nvsBgColor != null) {
            backgroundColor[3] = nvsBgColor.a;
            backgroundColor[0] = nvsBgColor.r;
            backgroundColor[1] = nvsBgColor.g;
            backgroundColor[2] = nvsBgColor.b;
        }

        outlineWidth = caption.getOutlineWidth();
        richWordUuid = caption.getModularCaptionRendererPackageId();
        bubbleUuid = caption.getModularCaptionContextPackageId();
    }

    @NonNull
    @Override
    public Object clone() {
        return DeepCopyUtil.deepClone(this);
    }


    @Override
    public LMeicamCaptionClip parseToLocalData() {
        LMeicamCaptionClip local = new LMeicamCaptionClip();
        setCommonData(local);
        local.setText(getText());
        local.setStyleId(getStyleId());
        local.setScaleX(getScaleX());
        local.setScaleY(getScaleY());
        local.setRotation(getRotation());
        local.setTranslationX(getTranslationX());
        local.setTranslationY(getTranslationY());
        local.setFont(getFont());
        float[] textColor = getTextColor();
        float[] localTextColor = new float[4];
        for (int index = 0; index < textColor.length; index++) {
            localTextColor[index] = textColor[index];
        }
        local.setTextColor(localTextColor);
        local.setBold(isBold());
        local.setItalic(isItalic());
        local.setShadow(isShadow());
        local.setOutline(isOutline());

        float[] outlineColor = getOutlineColor();
        float[] localOutlineColor = new float[4];
        for (int index = 0; index < textColor.length; index++) {
            localOutlineColor[index] = outlineColor[index];
        }
        local.setOutlineColor(localOutlineColor);

        float[] bgColor = getBackgroundColor();
        float[] localBgColor = new float[4];
        for (int index = 0; index < bgColor.length; index++) {
            localBgColor[index] = bgColor[index];
        }
        local.setBackgroundColor(localBgColor);
        local.setBackgroundAngle(getBackgroundAngle());
        local.setOutlineWidth(getOutlineWidth());
        local.setzValue(getzValue());
        local.setLetterSpacing(getLetterSpacing());
        local.setLineSpacing(getLineSpacing());
        local.setTextAlign(getTextAlign());
        local.setRichWordUuid(getRichWordUuid());
        local.setBubbleUuid(getBubbleUuid());
        local.setCombinationAnimationUuid(getCombinationAnimationUuid());
        local.setCombinationAnimationDuration(getCombinationAnimationDuration());
        local.setMarchInAnimationUuid(getMarchInAnimationUuid());
        local.setMarchInAnimationDuration(getMarchInAnimationDuration());
        local.setMarchOutAnimationUuid(getMarchOutAnimationUuid());
        local.setMarchOutAnimationDuration(getMarchOutAnimationDuration());
        local.setOperationType(getOperationType());
        return local;
    }
}
