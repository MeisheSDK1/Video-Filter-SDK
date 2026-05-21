package com.meishe.base.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.util.Collection;

/**
 * All rights reserved,Designed by www.meishesdk.com
 * 版权所有www.meishesdk.com
 * @Author : LiHangZhou
 * @CreateDate :2020/12/2 17:41
 * @Description :包含一些公用的但是无明显分类方法的工具类 Contains utility classes that are common but have no obvious classification methods
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CommonUtils {
    /**
     * 判断集合是否为空
     * Whether the collection is empty
     *
     * @param <T> the type parameter
     * @param t   T extends Collection
     * @return boolean true is empty, false not empty
     */
    public static <T extends Collection> boolean isEmpty(T t) {
        return ((t == null) || (t.isEmpty()));
    }

    /**
     * 判断索引在集合中是否非法
     * Is the index illegal in the collection
     *
     * @param <T>   the type parameter
     * @param index int the index in the collection
     * @param t     T extends Collection
     * @return boolean true is  normal  false  is illegal,
     */
    public static <T extends Collection> boolean isIndexAvailable(int index, T t) {
        if (t == null) {
            return false;
        }
        return ((index >= 0) && (index < t.size()));
    }

    /**
     * 获取圆角背景 不用每次都在drawable新建文件  那个不够灵活也很浪费时间
     * Gets radius drawable
     *
     * @param strokeWidth 边框宽度 小于0代表不需要边框，同时不再检查颜色 stroke width
     * @param roundRadius 圆角大小 小于0代表不需要圆角 round radius
     * @param strokeColor 边框颜色 stroke color
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
     * 只有圆角和填充
     * Gets radius drawable
     *
     * @param roundRadius 圆角 round radius
     * @param fillColor   填充颜色 fill color
     */
    public static Drawable getRadiusDrawable(int roundRadius, int fillColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        if (fillColor != -1) {
            gradientDrawable.setColor(fillColor);
        }
        if (roundRadius > 0) {
            gradientDrawable.setCornerRadius(roundRadius);
        }
        return gradientDrawable;
    }
}
