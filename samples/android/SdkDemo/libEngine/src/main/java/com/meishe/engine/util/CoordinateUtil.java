package com.meishe.engine.util;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2020/12/18 11:30
 * @Description :坐标系转换相关工具类 Utils for coordinate parsing.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CoordinateUtil {

    /**
     * 将坐标点由经典坐标系下转换为View坐标系下的点
     * Parse points in canonical coordinate to view coordinate points.
     *
     * @param verticesList 坐标集合 List of point
     * @param width        view的宽 Width of view
     * @param height       view的高 Height of view
     * @return 转换后的点 List of points after parsing.
     */
    public static List<PointF> parseCanonicalToView(List<PointF> verticesList, int width, int height) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mapCanonicalToView(verticesList.get(i), width, height);
            newList.add(pointF);
        }
        return newList;
    }

    /**
     * 将坐标点由经典坐标系下转换为View坐标系下的点
     * Parse point in canonical coordinate to view coordinate point.
     *
     * @param oldPointF 原始点 Point in canonical coordinate.
     * @param width     view的宽 Width of view
     * @param height    view的高 Height of view
     * @return 转换后的点 The point after parsing.
     */
    private static PointF mapCanonicalToView(PointF oldPointF, int width, int height) {
        PointF pointF = new PointF();
        pointF.x = oldPointF.x + width / 2F;
        pointF.y = height / 2F - oldPointF.y;
        return pointF;
    }

    /**
     * 将坐标点由View坐标系下转换为经典坐标系下的点
     * Parse points in view coordinate to  canonical coordinate point.
     *
     * @param oldPointF 原始点 Point in view coordinate.
     * @param width     view的宽 Width of view
     * @param height    view的高 Height of view
     * @return 转换后的点 The point after parsing.
     */
    public static PointF mapViewToCanonical(PointF oldPointF, int width, int height) {
        PointF pointF = new PointF();
        pointF.x = oldPointF.x - width / 2F;
        pointF.y = height / 2F - oldPointF.y;
        return pointF;
    }
}
