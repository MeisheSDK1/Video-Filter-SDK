package com.meishe.engine.local;

import android.graphics.PointF;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meishe.base.utils.CommonUtils;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamCompoundCaptionClip;
import com.meishe.engine.util.ColorUtil;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by CaoZhiChao on 2020/7/4 11:57
 */
public class LMeicamCompoundCaptionClip extends LClipInfo implements Cloneable, Serializable {
    @SerializedName("compoundCaptionItems")
    private List<LMeicamCompoundCaptionItem> mCompoundCaptionItems = new ArrayList<>();
    @SerializedName("styleId")
    private String styleDesc;
    private float scaleX;
    private float scaleY;
    private float zValue;
    private float rotation = 0;
    private float translationX = 0;
    private float translationY = 0;
    private int itemSelectedIndex = 0;
    public LMeicamCompoundCaptionClip(String styleDesc) {
        super(CommonData.CLIP_COMPOUND_CAPTION);
        this.styleDesc = styleDesc;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }

    public List<LMeicamCompoundCaptionItem> getCompoundCaptionItems() {
        return mCompoundCaptionItems;
    }

    public void setCompoundCaptionItems(List<LMeicamCompoundCaptionItem> compoundCaptionItems) {
        this.mCompoundCaptionItems = compoundCaptionItems;
    }

    public String getStyleDesc() {
        return styleDesc;
    }

    public void setStyleDesc(String styleDesc) {
        this.styleDesc = styleDesc;
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


    public int getItemSelectedIndex() {
        return itemSelectedIndex;
    }

    public void setItemSelectedIndex(int itemSelectedIndex) {
        this.itemSelectedIndex = itemSelectedIndex;
    }

    private static void updateCompoundCaptionAttribute(NvsTimelineCompoundCaption newCaption, LMeicamCompoundCaptionClip meicamCompoundCaptionClip) {
        if (newCaption == null || meicamCompoundCaptionClip == null) {
            return;
        }
        List<LMeicamCompoundCaptionItem> captionAttrList = meicamCompoundCaptionClip.getCompoundCaptionItems();
        int captionCount = newCaption.getCaptionCount();
        for (int index = 0; index < captionCount; ++index) {
            LMeicamCompoundCaptionItem compoundCaptionItem = captionAttrList.get(index);
            if (compoundCaptionItem == null) {
                continue;
            }
            NvsColor textColor = ColorUtil.colorFloatToNvsColor(compoundCaptionItem.getTextColor());
            newCaption.setTextColor(index, textColor);

            String fontName = compoundCaptionItem.getFont();
            if (!TextUtils.isEmpty(fontName)) {
                newCaption.setFontFamily(index, fontName);
            }
            String captionText = compoundCaptionItem.getText();
            if (!TextUtils.isEmpty(captionText)) {
                newCaption.setText(index, captionText);
            }
        }

        /*
         * 放缩字幕
         * Shrink captions
         * */
        float scaleFactorX = meicamCompoundCaptionClip.getScaleX();
        float scaleFactorY = meicamCompoundCaptionClip.getScaleY();
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);

        /*
         * 旋转字幕
         * Spin subtitles
         * */
        float rotation = meicamCompoundCaptionClip.getRotation();
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(meicamCompoundCaptionClip.getzValue());
        PointF translation = new PointF(meicamCompoundCaptionClip.getTranslationX(), meicamCompoundCaptionClip.getTranslationY());
        newCaption.setCaptionTranslation(translation);
    }

    @NonNull
    @Override
    public Object clone() {
        return DeepCopyUtil.deepClone(this);
    }

    @Override
    public MeicamCompoundCaptionClip parseToTimelineData() {
        MeicamCompoundCaptionClip timelineData = new MeicamCompoundCaptionClip(styleDesc);
        setCommonData(timelineData);
        timelineData.setStyleDesc(getStyleDesc());
        timelineData.setScaleX(getScaleX());
        timelineData.setScaleY(getScaleY());
        timelineData.setzValue(getzValue());
        timelineData.setRotation(getRotation());
        timelineData.setTranslationX(getTranslationX());
        timelineData.setTranslationY(getTranslationY());
        timelineData.setItemSelectedIndex(getItemSelectedIndex());
        if (!CommonUtils.isEmpty(mCompoundCaptionItems)) {
            for (LMeicamCompoundCaptionItem compoundCaptionItem : mCompoundCaptionItems) {
                timelineData.getCompoundCaptionItems().add(compoundCaptionItem.parseToTimelineData());
            }
        }
        return timelineData;
    }
}
