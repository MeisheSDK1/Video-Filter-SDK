package com.meishe.libmakeup.bean;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/15 16:38
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class ColorData {
    public ColorData() {
    }

    public ColorData(float colorsProgress, int colorIndex, int color) {
        this.colorsProgress = colorsProgress;
        this.colorIndex = colorIndex;
        this.color = color;
    }

    public float colorsProgress = -1F;
    public int colorIndex = -1;
    public int color = -1;
}
