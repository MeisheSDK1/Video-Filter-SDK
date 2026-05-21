package com.meishe.engine.util;

import android.graphics.Color;

import com.meicam.sdk.NvsColor;

/**
 * author：yangtailin on 2020/6/19 11:48
 * 颜色工具类。主要用于SDK中的NvsColor与UI中的颜色值的变换
 */
public class ColorUtil {

    /**
     * #FFFFFFFF变换为NvsColor
     * Color to nvs color
     *
     * @param colorString the color string
     * @return the nvs color
     */
    public static NvsColor colorToNvsColor(String colorString) {
        if (colorString == null || colorString.isEmpty())
            return null;
        NvsColor color = new NvsColor(1, 1, 1, 1);
        int parseColor = Color.parseColor(colorString);
        color.a = (float) ((parseColor & 0xff000000) >>> 24) / 0xFF;
        color.r = (float) ((parseColor & 0x00ff0000) >> 16) / 0xFF;
        color.g = (float) ((parseColor & 0x0000ff00) >> 8) / 0xFF;
        color.b = (float) ((parseColor) & 0x000000ff) / 0xFF;
        return color;
    }

    /**
     * #FFFFFFFF变换为记录rgba的数组
     * String color to color float [ ].
     *
     * @param colorString the color string
     * @return the float [ ]
     */
    public static float[] stringColorToColor(String colorString) {
        if (colorString == null || colorString.isEmpty())
            return null;
        int parseColor = Color.parseColor(colorString);
        float a = (float) ((parseColor & 0xff000000) >>> 24) / 0xFF;
        float r = (float) ((parseColor & 0x00ff0000) >> 16) / 0xFF;
        float g = (float) ((parseColor & 0x0000ff00) >> 8) / 0xFF;
        float b = (float) ((parseColor) & 0x000000ff) / 0xFF;
        return new float[]{r, g, b, a};
    }

    /**
     * rgba的数组变换为#FFFFFFFF格式
     * Color float to nvs color nvs color.
     *
     * @param colorString the color string
     * @return the nvs color
     */
    public static NvsColor colorFloatToNvsColor(float[] colorString) {
        if (colorString == null || colorString.length != 4) {
            return null;
        }
        return new NvsColor(colorString[0], colorString[1], colorString[2], colorString[3]);
    }


    /**
     * NvsColor变换为记录rgba的数组
     * Get color array float [ ].
     *
     * @param nvsColor the nvs color
     * @return the float [ ].数组中为[0, 1)的颜色值
     */
    public static float[] getColorArray(NvsColor nvsColor) {
        if (nvsColor == null) {
            return null;
        }
        return new float[]{nvsColor.r, nvsColor.g, nvsColor.b, nvsColor.a};
    }
}
