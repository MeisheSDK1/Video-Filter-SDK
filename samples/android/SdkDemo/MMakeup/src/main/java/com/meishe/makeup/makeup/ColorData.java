package com.meishe.makeup.makeup;

import java.io.Serializable;

public class ColorData implements Serializable {
    public int color;
    public String name;

    public ColorData() {
    }

    public ColorData(float colorsProgress, int colorIndex, int color) {
        this.colorsProgress = colorsProgress;
        this.colorIndex = colorIndex;
        this.color = color;
    }

    public float colorsProgress = -1F;
    public int colorIndex = -1;
}
