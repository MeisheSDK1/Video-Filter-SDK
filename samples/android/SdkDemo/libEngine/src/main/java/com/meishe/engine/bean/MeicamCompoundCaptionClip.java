package com.meishe.engine.bean;

import android.graphics.PointF;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.local.LMeicamCompoundCaptionClip;
import com.meishe.engine.util.ColorUtil;
import com.meishe.engine.util.DeepCopyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by CaoZhiChao on 2020/7/4 11:57
 */
public class MeicamCompoundCaptionClip extends ClipInfo<NvsTimelineCompoundCaption> implements Cloneable, Serializable {
    @SerializedName("compoundCaptionItems")
    private List<MeicamCompoundCaptionItem> mCompoundCaptionItems = new ArrayList<>();
    @SerializedName("styleId")
    private String styleDesc;
    private float scaleX;
    private float scaleY;
    private float zValue;
    private float rotation = 0;
    private float translationX = 0;
    private float translationY = 0;
    /**
     * 用于更新子轨的文字
     * Text used to update the subtrack
     */
    private int itemSelectedIndex = 0;

    public MeicamCompoundCaptionClip(String styleDesc) {
        super(CommonData.CLIP_COMPOUND_CAPTION);
        this.styleDesc = styleDesc;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }

    public List<MeicamCompoundCaptionItem> getCompoundCaptionItems() {
        return mCompoundCaptionItems;
    }

    public void setCompoundCaptionItems(List<MeicamCompoundCaptionItem> compoundCaptionItems) {
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

    public void setItemSelectedIndex(int index) {
        this.itemSelectedIndex = index;
    }

    public void loadData(NvsTimelineCompoundCaption comCaption) {
        if (comCaption == null) {
            return;
        }
        setObject(comCaption);
        setInPoint(comCaption.getInPoint());
        setOutPoint(comCaption.getOutPoint());
        int captionCount = comCaption.getCaptionCount();
        mCompoundCaptionItems.clear();
        for (int index = 0; index < captionCount; index++) {
            MeicamCompoundCaptionItem meicamCompoundCaptionItem = new MeicamCompoundCaptionItem(index, comCaption.getText(index));
            NvsColor color = comCaption.getTextColor(index);
            float[] textColor = new float[4];
            textColor[0] = color.r;
            textColor[1] = color.g;
            textColor[2] = color.b;
            textColor[3] = color.a;
            meicamCompoundCaptionItem.setTextColor(textColor);
            meicamCompoundCaptionItem.setFont(comCaption.getFontFamily(index));
            mCompoundCaptionItems.add(meicamCompoundCaptionItem);
        }
        PointF point = comCaption.getCaptionTranslation();
        if (point != null) {
            translationX = point.x;
            translationY = point.y;
        }
        scaleX = comCaption.getScaleX();
        scaleY = comCaption.getScaleY();
        rotation = comCaption.getRotationZ();
        zValue = comCaption.getZValue();
    }


    public NvsTimelineCompoundCaption bindToTimeline(NvsTimeline timeline) {
        if (timeline == null) {
            LogUtils.e("NvsTimelineCompoundCaption bindToTimeline timeline==null");
            return null;
        }
        NvsTimelineCompoundCaption mNvsTimelineCompoundCaption = timeline.addCompoundCaption(getInPoint(),
                getOutPoint() - getInPoint(), getStyleDesc());
        if (mNvsTimelineCompoundCaption == null) {
            LogUtils.e("NvsTimelineCompoundCaption bindToTimeline mNvsTimelineCompoundCaption==null");
            return null;
        }
        mNvsTimelineCompoundCaption.setClipAffinityEnabled(false);
        setObject(mNvsTimelineCompoundCaption);
        updateCompoundCaptionAttribute(mNvsTimelineCompoundCaption, this);
        return mNvsTimelineCompoundCaption;
    }

    public NvsTimelineCompoundCaption addCompoundCaptionFirst(NvsTimeline timeline, int zValue) {
        NvsTimelineCompoundCaption mNvsTimelineCompoundCaption = timeline.addCompoundCaption(getInPoint(),
                getOutPoint() - getInPoint(), getStyleDesc());
        if (mNvsTimelineCompoundCaption != null) {
            mNvsTimelineCompoundCaption.setZValue(zValue);
        } else {
            LogUtils.e("addCompoundCaptionFirst", "NvsTimelineCompoundCaption is null! inpoint: "+ getInPoint()
            +"  OutPoint: "+ getOutPoint()+"  StyleDesc: "+ getStyleDesc());
        }
        return mNvsTimelineCompoundCaption;
    }


    private static void updateCompoundCaptionAttribute(NvsTimelineCompoundCaption newCaption, MeicamCompoundCaptionClip meicamCompoundCaptionClip) {
        if (newCaption == null || meicamCompoundCaptionClip == null) {
            return;
        }
        List<MeicamCompoundCaptionItem> captionAttrList = meicamCompoundCaptionClip.getCompoundCaptionItems();
        int captionCount = newCaption.getCaptionCount();
        for (int index = 0; index < captionCount; ++index) {
            if (index >= captionAttrList.size()) {
                break;
            }
            MeicamCompoundCaptionItem compoundCaptionItem = captionAttrList.get(index);
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
    public LMeicamCompoundCaptionClip parseToLocalData() {
        LMeicamCompoundCaptionClip local = new LMeicamCompoundCaptionClip(styleDesc);
        setCommonData(local);
        local.setStyleDesc(getStyleDesc());
        local.setScaleX(getScaleX());
        local.setScaleY(getScaleY());
        local.setzValue(getzValue());
        local.setRotation(getRotation());
        local.setTranslationX(getTranslationX());
        local.setTranslationY(getTranslationY());
        local.setItemSelectedIndex(getItemSelectedIndex());
        for (MeicamCompoundCaptionItem compoundCaptionItem : mCompoundCaptionItems) {
            local.getCompoundCaptionItems().add(compoundCaptionItem.parseToLocalData());
        }
        return local;
    }
}
