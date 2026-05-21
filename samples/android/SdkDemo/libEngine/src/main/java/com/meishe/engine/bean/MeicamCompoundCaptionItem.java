package com.meishe.engine.bean;


import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMeicamCompoundCaptionItem;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/4 13:37
 */
public class MeicamCompoundCaptionItem implements Cloneable, Serializable, TimelineDataToLocalAdapter<LMeicamCompoundCaptionItem> {
    private int index;
    private String text;
    private float[] textColor = {1f, 1f, 1f, 1f};
    private String font = "";

    public MeicamCompoundCaptionItem(int index, String text) {
        this.index = index;
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public float[] getTextColor() {
        return textColor;
    }

    public void setTextColor(float[] textColor) {
        this.textColor = textColor;
    }

    public MeicamCompoundCaptionItem copy() {
        MeicamCompoundCaptionItem captionItem = new MeicamCompoundCaptionItem(getIndex(), getText());
        captionItem.setFont(getFont());
        captionItem.setTextColor(getTextColor());
        return captionItem;
    }

    public MeicamCompoundCaptionItem copy(MeicamCompoundCaptionItem item) {
        setIndex(item.getIndex());
        setText(item.getText());
        setTextColor(item.getTextColor());
        setFont(item.getFont());
        return this;
    }

    @Override
    public LMeicamCompoundCaptionItem parseToLocalData() {
        LMeicamCompoundCaptionItem local = new LMeicamCompoundCaptionItem(index, text);
        local.setFont(getFont());
        float[] textColor = getTextColor();
        float[] localTextColor = new float[4];
        for (int index = 0; index < textColor.length; index++) {
            localTextColor[index] = textColor[index];
        }
        local.setTextColor(localTextColor);
        return local;
    }
}
