package com.meishe.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/28 15:14
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class DrawableUitls {
    /**
     * 获取圆角背景 不用每次都在drawable新建文件
     * Gets radius drawable
     *
     * @param strokeWidth 边框宽度 小于0代表不需要边框，同时不再检查颜色 stroke width
     * @param strokeColor 边框颜色 stroke color
     * @param roundRadius 圆角大小 小于0代表不需要圆角 round radius
     * @param fillColor   背景填充颜色  -1代表不用 fill color
     */
    public static Drawable getRadiusDrawable(int strokeWidth, int strokeColor, int roundRadius, int fillColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        if (fillColor != -1) {
            gradientDrawable.setColor(fillColor);
        }
        if (roundRadius > 0) {
            gradientDrawable.setCornerRadius(roundRadius);
        }
        if (strokeWidth > 0) {
            gradientDrawable.setStroke(strokeWidth, strokeColor);
        }
        return gradientDrawable;
    }

    /**
     * 角落的顺序是左上，右上，右下，左下
     * The corners are ordered top-left, top-right, bottom-left, bottom-right
     *
     * @param topLeftRadius     top-left
     * @param topRightRadius    top-right
     * @param bottomRightRadius bottom-left
     * @param bottomLeftRadius  bottom-right
     * @param fillColor         fillColor
     * @return Drawable
     */
    public static Drawable getRadiiDrawable(float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius, int fillColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        if (fillColor != -1) {
            gradientDrawable.setColor(fillColor);
        }
        float[] mRadiusArr = new float[8];
        mRadiusArr[0] = topLeftRadius;
        mRadiusArr[1] = topLeftRadius;
        mRadiusArr[2] = topRightRadius;
        mRadiusArr[3] = topRightRadius;
        mRadiusArr[4] = bottomRightRadius;
        mRadiusArr[5] = bottomRightRadius;
        mRadiusArr[6] = bottomLeftRadius;
        mRadiusArr[7] = bottomLeftRadius;
        gradientDrawable.setCornerRadii(mRadiusArr);
        return gradientDrawable;
    }

    /**
     * 只有圆角和填充
     * Gets radius drawable
     *
     * @param roundRadius 圆角 round radius
     * @param fillColor   填充颜色 fill color
     */
    public static Drawable getRadiusDrawable(int roundRadius, int fillColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(fillColor);
        if (roundRadius > 0) {
            gradientDrawable.setCornerRadius(roundRadius);
        }
        return gradientDrawable;
    }


    public static Drawable tintColor(Context mContext, int resId, int color) {
        if (resId == 0) {
            return null;
        }
        Drawable drawable = mContext.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }
}
