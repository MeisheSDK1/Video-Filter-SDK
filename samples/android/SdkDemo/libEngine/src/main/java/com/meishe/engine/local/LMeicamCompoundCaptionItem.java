package com.meishe.engine.local;

import com.meishe.engine.adapter.LocalToTimelineDataAdapter;
import com.meishe.engine.bean.MeicamCompoundCaptionItem;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2020/7/4 13:37
 */
public class LMeicamCompoundCaptionItem implements Cloneable, Serializable, LocalToTimelineDataAdapter<MeicamCompoundCaptionItem> {
    private int index;
    private String text;
    private float[] textColor = {1f, 1f, 1f, 1f};
    private String font = "";

    public LMeicamCompoundCaptionItem(int index, String text) {
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

    @Override
    public MeicamCompoundCaptionItem parseToTimelineData() {
        MeicamCompoundCaptionItem timelineData = new MeicamCompoundCaptionItem(index, text);
        timelineData.setFont(getFont());
        float[] textColor = getTextColor();
        float[] localTextColor = new float[4];
        for (int index = 0; index < textColor.length; index++) {
            localTextColor[index] = textColor[index];
        }
        timelineData.setTextColor(localTextColor);
        return timelineData;
    }
}
