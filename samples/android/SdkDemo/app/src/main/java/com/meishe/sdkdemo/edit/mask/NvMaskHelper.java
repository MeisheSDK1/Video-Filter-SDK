package com.meishe.sdkdemo.edit.mask;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsMaskRegionInfo;
import com.meicam.sdk.NvsPosition2D;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.engine.bean.CutData;
import com.meishe.sdkdemo.edit.data.mask.MaskInfoData;
import com.meishe.sdkdemo.edit.data.mask.MaskRegionInfoData;
import com.meishe.sdkdemo.utils.StoryboardUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.CropInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :Jml
 * @date :2020/9/22 15:32
 * @des : 绘制蒙版路径辅助工具类
 * maskView Auxiliary tools
 */
public class NvMaskHelper {
    private final static String TAG = "NvMaskHelper";

    /**
     * 线性mask 点集合.默认盖住下半部分画面
     * lineMask points.Cover the bottom half of the screen by default
     *
     * @param maskWidth
     * @param center             mask中心点  mask center point
     * @param centerCircleRadius 中心圆的半径 The radius of the center circle
     * @param angle              旋转角度 Angle of rotation
     * @return 线性mask 点集合  Linear mask point set
     */
    public static Path lineRegionInfoPath(int maskWidth, PointF center, int centerCircleRadius, int angle) {

        PointF leftTopPoint = new PointF(center.x - maskWidth, center.y);
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle);

        PointF rightTopPoint = new PointF(center.x + maskWidth, center.y);
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle);

        PointF rightBottomPoint = new PointF(center.x + maskWidth, center.y);
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle);


        PointF leftBottomPoint = new PointF(center.x - maskWidth, center.y);
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle);

        PointF innerCircleLeft = getPointByAngle(new PointF(center.x - centerCircleRadius, center.y), center, angle);
        PointF innerCircleRight = getPointByAngle(new PointF(center.x + centerCircleRadius, center.y), center, angle);
        Path path = new Path();
        path.moveTo(center.x, center.y);
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        path.moveTo(leftTopPoint.x, leftTopPoint.y);
        path.lineTo(innerCircleLeft.x, innerCircleLeft.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        //跳转到圆的右侧开始  Jump to the right side of the circle to start
        path.moveTo(innerCircleRight.x, innerCircleRight.y);
        path.lineTo(rightTopPoint.x, rightTopPoint.y);
        path.lineTo(rightBottomPoint.x, rightBottomPoint.y);
        path.lineTo(leftBottomPoint.x, leftBottomPoint.y);
        path.lineTo(leftTopPoint.x, leftTopPoint.y);
        return path;
    }

    public static Path lineRegionInfoPathForDrag(int maskWidth, PointF center, int centerCircleRadius, int angle) {

        PointF leftTopPoint = new PointF(center.x - maskWidth, center.y - 20);
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle);

        PointF rightTopPoint = new PointF(center.x + maskWidth, center.y - 20);
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle);

        PointF rightBottomPoint = new PointF(center.x + maskWidth, center.y + 20);
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle);


        PointF leftBottomPoint = new PointF(center.x - maskWidth, center.y + 20);
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle);

        PointF innerCircleLeft = getPointByAngle(new PointF(center.x - centerCircleRadius, center.y), center, angle);
        PointF innerCircleRight = getPointByAngle(new PointF(center.x + centerCircleRadius, center.y), center, angle);
        Path path = new Path();
        path.moveTo(leftTopPoint.x, leftTopPoint.y);
        path.lineTo(innerCircleLeft.x, innerCircleLeft.y);
        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        //跳转到圆的右侧开始  Jump to the right side of the circle to start
        path.moveTo(innerCircleRight.x, innerCircleRight.y);
        path.lineTo(rightTopPoint.x, rightTopPoint.y);
        path.lineTo(rightBottomPoint.x, rightBottomPoint.y);
        path.lineTo(leftBottomPoint.x, leftBottomPoint.y);
        path.lineTo(leftTopPoint.x, leftTopPoint.y);
        return path;
    }

    /**
     * 线性蒙版拖拽位置构建方法
     * Linear mask dragging position construction method
     *
     * @param center             中心点 centerPoint
     * @param centerCircleRadius
     * @param angle
     * @return
     */
    public static Path lineRegionTouchBuild(int maskWidth, PointF center, int centerCircleRadius, int angle) {

        PointF leftTopPoint = new PointF(center.x - maskWidth, center.y - centerCircleRadius);
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle);

        PointF rightTopPoint = new PointF(center.x + maskWidth, center.y - centerCircleRadius);
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle);

        PointF rightBottomPoint = new PointF(center.x + maskWidth, center.y + centerCircleRadius);
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle);

        PointF leftBottomPoint = new PointF(center.x - maskWidth, center.y + centerCircleRadius);
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle);

        PointF innerCircleLeft = getPointByAngle(new PointF(center.x - maskWidth, center.y), center, angle);
        PointF innerCircleRight = getPointByAngle(new PointF(center.x + maskWidth, center.y), center, angle);
        Path path = new Path();
        path.moveTo(leftTopPoint.x, leftTopPoint.y);
        path.lineTo(innerCircleLeft.x, innerCircleLeft.y);
        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        //跳转到圆的右侧开始  Jump to the right side of the circle to start
        path.moveTo(innerCircleRight.x, innerCircleRight.y);
        path.lineTo(rightTopPoint.x, rightTopPoint.y);
        path.lineTo(rightBottomPoint.x, rightBottomPoint.y);
        path.lineTo(leftBottomPoint.x, leftBottomPoint.y);
        path.lineTo(leftTopPoint.x, leftTopPoint.y);
        return path;
    }


    /**
     * 蒙版矩形
     * Mask rectangle
     *
     * @param maskWidth
     * @param maskHeight
     * @param center
     * @param centerCircleRadius
     * @param roundCornerWidthRate
     * @return
     */
    @SuppressLint("NewApi")
    public static Path rectRegionInfoPath(int maskWidth, int maskHeight, PointF center, int centerCircleRadius, float roundCornerWidthRate) {
        Path path = new Path();
        //直接绘制一个圆形矩形 Draw a circular rectangle directly
        int minSize = maskWidth > maskHeight ? maskHeight : maskWidth;
        path.addRoundRect(new RectF(center.x - maskWidth / 2F, center.y - maskHeight / 2F, center.x + maskWidth / 2F, center.y + maskHeight / 2F), minSize / 2F * roundCornerWidthRate, minSize / 2F * roundCornerWidthRate, Path.Direction.CCW);
        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        return path;
    }


    /**
     * 构建镜像蒙版的path
     * The path to build the mirror mask
     * <p>
     * build region path info of mirror mask
     *
     * @param maskWidth          蒙版的宽 mask width
     * @param maskHeight         蒙版的高 mask height
     * @param center             蒙版的中点 center point of mask
     * @param centerCircleRadius 蒙版的圆角半径 center circle radius
     * @param angle              角度 angle
     * @return 镜像蒙版的path The path of mask
     */
    public static Path mirrorRegionInfoPath(int maskWidth, int maskHeight, PointF center, int centerCircleRadius, int angle) {
        PointF leftTopPoint = new PointF(center.x - maskWidth / 2, center.y - maskHeight / 2);
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle);

        PointF rightTopPoint = new PointF(center.x + maskWidth / 2, center.y - maskHeight / 2);
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle);

        PointF rightBottomPoint = new PointF(center.x + maskWidth / 2, center.y + maskHeight / 2);
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle);


        PointF leftBottomPoint = new PointF(center.x - maskWidth / 2, center.y + maskHeight / 2);
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle);

        Path path = new Path();
        path.moveTo(leftTopPoint.x, leftTopPoint.y);
        path.lineTo(rightTopPoint.x, rightTopPoint.y);
        path.lineTo(rightBottomPoint.x, rightBottomPoint.y);
        path.lineTo(leftBottomPoint.x, leftBottomPoint.y);
        path.lineTo(leftTopPoint.x, leftTopPoint.y);
        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        return path;
    }

    /**
     * 构建圆形蒙版的path
     * Construct the path of the circular mask
     * <p>
     * build region path info of circle mask
     *
     * @param maskWidth          蒙版的宽 mask width
     * @param maskHeight         蒙版的高 mask height
     * @param center             蒙版的中点 center point of mask
     * @param centerCircleRadius 蒙版的圆角半径 center circle radius
     * @param angle              角度 angle
     * @return 圆形蒙版的 path The path of mask
     */
    public static Path circleRegionInfoPath(int maskWidth, int maskHeight, PointF center, int centerCircleRadius, int angle) {

        Path path = new Path();
        //path.addCircle(center.x,center.y,maskWidth/2, Path.Direction.CW);
        RectF rectF = new RectF(center.x - maskWidth / 2, center.y - maskHeight / 2, center.x + maskWidth / 2, center.y + maskHeight / 2);
        path.addOval(rectF, Path.Direction.CW);
        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        return path;
    }

    /**
     * 通过贝塞尔曲线构建心形蒙版的path
     * Construct the path of the heart mask through the Bezier curve
     * <p>
     * Build path of heart mask by Bezier curve
     *
     * @param maskWidth          蒙版的宽 mask width
     * @param center             蒙版的中点 center point of mask
     * @param centerCircleRadius 蒙版的圆角半径 center circle radius
     * @param angle              角度 angle
     * @return 蒙版的 path The path of mask
     */
    public static Path heartRegionInfoPath(int maskWidth, PointF center, int centerCircleRadius, int angle) {

        Path path = new Path();
        //通过三阶贝塞尔曲线绘制  It is drawn by the third-order Bessel curve
        PointF intersectionPoint = getPointByAngle(new PointF(center.x, center.y - maskWidth * (2 * 1.0f / 6)), center, angle);
        path.moveTo(intersectionPoint.x, intersectionPoint.y);

        PointF prePoint = getPointByAngle(new PointF(center.x + 5 * 1.0f / 7 * maskWidth, center.y - 0.8f * maskWidth), center, angle);
        PointF curPoint = getPointByAngle(new PointF(center.x, center.y + maskWidth), center, angle);
        PointF nextPoint = getPointByAngle(new PointF(center.x + 16 * 1.0f / 13 * maskWidth, center.y + 0.1f * maskWidth), center, angle);

        path.cubicTo(prePoint.x, prePoint.y, nextPoint.x, nextPoint.y, curPoint.x, curPoint.y);

        prePoint = getPointByAngle(new PointF(center.x - 16 * 1.0f / 13 * maskWidth, center.y + 0.1f * maskWidth), center, angle);
        curPoint = getPointByAngle(new PointF(center.x, center.y - maskWidth * (2 * 1.0f / 6)), center, angle);
        nextPoint = getPointByAngle(new PointF(center.x - 5 * 1.0f / 7 * maskWidth, center.y - 0.8f * maskWidth), center, angle);
        path.cubicTo(prePoint.x, prePoint.y, nextPoint.x, nextPoint.y, curPoint.x, curPoint.y);

        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        return path;
    }

    /**
     * 构建星形蒙版的path
     * Construct the path of the star mask
     *
     * @param center
     * @param centerCircleRadius
     * @param rotation
     * @return
     */
    public static Path starRegionInfoPath(int width, PointF center, int centerCircleRadius, int rotation) {

        Path path = new Path();

        //外圆 Outer circle
        float radius = width / 2.0f;
        float angel = (float) (Math.PI * 2 / 5);
        PointF[] outPoints = new PointF[5];
        //这里是获取五角星的五个定点的坐标点位置
        // Here are the five points of the five-pointed star
        for (int i = 1; i < 6; i++) {
            float x = (float) (center.x - Math.sin(i * angel) * radius);
            float y = (float) (center.y - Math.cos(i * angel) * radius);
            outPoints[i - 1] = getPointByAngle(new PointF(x, y), center, rotation);
        }

        /// 越大越胖 Bigger and fatter
        float radiusRate = 0.5f; //2/5
        //内圆 Outer circle
        float internalRadius = radius * radiusRate;
        float internalAngel = (float) (Math.PI * 2 / 5);
        PointF[] inPoints = new PointF[5];
        //这里是获取五角星的五个定点的坐标点位置
        // Here are the five points of the five-pointed star
        for (int i = 1; i < 6; i++) {
            float x = (float) (center.x - Math.sin(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius);
            float y = (float) (center.y - Math.cos(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius);
            inPoints[i - 1] = getPointByAngle(new PointF(x, y), center, rotation);
        }

        //移动到第一个点 Let's go to the first point
        path.moveTo(outPoints[0].x, outPoints[0].y);
        for (int i = 0; i < 5; i++) {
            PointF out = outPoints[i];
            PointF in = inPoints[i];
            path.lineTo(out.x, out.y);
            path.lineTo(in.x, in.y);
        }
        path.lineTo(outPoints[0].x, outPoints[0].y);

        path.moveTo(center.x, center.y);
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius, Path.Direction.CW);
        return path;
    }

    /**
     * 构建一个触发羽化值调节的区域
     * Construct an area that triggers the adjustment of the feather value
     *
     * @param mType           蒙版类型 Mask type
     * @param center          中心点 Central point
     * @param rotation        角度 rotation
     * @param maskHeight
     * @param mFeatherIconDis
     * @return
     */
    public static Path buildFeatherPath(int mType, PointF center, float rotation, int maskHeight, int screenWidth, int fragmentHeight, int mFeatherIconDis) {
        Path mFeatherPath = new Path();
        PointF leftTop = new PointF();
        PointF rightTop = new PointF();
        PointF rightBottom = new PointF(screenWidth, fragmentHeight);
        PointF leftBottom = new PointF(0, fragmentHeight);
        if (mType == MaskRegionInfoData.MaskType.LINE) {
            leftTop.x = 0;
            leftTop.y = center.y + mFeatherIconDis;
            rightTop.x = screenWidth;
            rightTop.y = leftTop.y;
        } else {
            leftTop.x = 0;
            leftTop.y = center.y + maskHeight / 2 + mFeatherIconDis;
            rightTop.x = screenWidth;
            rightTop.y = leftTop.y;
        }
        leftTop = getPointByAngle(leftTop, center, rotation);
        rightTop = getPointByAngle(rightTop, center, rotation);
        rightBottom = getPointByAngle(rightBottom, center, rotation);
        leftBottom = getPointByAngle(leftBottom, center, rotation);
        mFeatherPath.moveTo(leftTop.x, leftTop.y);
        mFeatherPath.lineTo(rightTop.x, rightTop.y);
        mFeatherPath.lineTo(rightBottom.x, rightBottom.y);
        mFeatherPath.lineTo(leftBottom.x, leftBottom.y);
        mFeatherPath.lineTo(leftTop.x, leftTop.y);
        return mFeatherPath;
    }

    /**
     * 构建一个宽度调节的区域
     * Construct a width-adjustable area
     *
     * @param center
     * @param rotation
     * @param maskHeight
     * @param mWidthDis
     * @return
     */
    public static Path buildMaskWidthPath(PointF center, float rotation, int maskWidth, int maskHeight, int screenWidth, int mWidthDis) {
        Path mFeatherPath = new Path();
        PointF leftTop = new PointF(center.x + maskWidth / 2 + mWidthDis, center.y - maskHeight / 2);
        PointF rightTop = new PointF(screenWidth, center.y - maskHeight / 2);
        PointF rightBottom = new PointF(screenWidth, center.y + maskHeight / 2);
        PointF leftBottom = new PointF(center.x + maskWidth / 2 + mWidthDis, center.y + maskHeight / 2);
        leftTop = getPointByAngle(leftTop, center, rotation);
        rightTop = getPointByAngle(rightTop, center, rotation);
        rightBottom = getPointByAngle(rightBottom, center, rotation);
        leftBottom = getPointByAngle(leftBottom, center, rotation);
        mFeatherPath.moveTo(leftTop.x, leftTop.y);
        mFeatherPath.lineTo(rightTop.x, rightTop.y);
        mFeatherPath.lineTo(rightBottom.x, rightBottom.y);
        mFeatherPath.lineTo(leftBottom.x, leftBottom.y);
        mFeatherPath.lineTo(leftTop.x, leftTop.y);
        return mFeatherPath;
    }

    /**
     * 构建一个高度调节的区域
     * Construct a height-adjustable area
     *
     * @param center
     * @param rotation
     * @param maskHeight
     * @param heightDis
     * @return
     */
    public static Path buildMaskHeightPath(PointF center, float rotation, int maskWidth, int maskHeight, int screenWidth, int heightDis) {
        Path mHeightPath = new Path();
        PointF leftTop = new PointF(center.x - maskWidth / 2, 0);
        PointF rightTop = new PointF(screenWidth, 0);
        PointF rightBottom = new PointF(screenWidth, center.y - heightDis - maskHeight / 2);
        PointF leftBottom = new PointF(center.x - maskWidth / 2, center.y - heightDis - maskHeight / 2);
        leftTop = getPointByAngle(leftTop, center, rotation);
        rightTop = getPointByAngle(rightTop, center, rotation);
        rightBottom = getPointByAngle(rightBottom, center, rotation);
        leftBottom = getPointByAngle(leftBottom, center, rotation);
        mHeightPath.moveTo(leftTop.x, leftTop.y);
        mHeightPath.lineTo(rightTop.x, rightTop.y);
        mHeightPath.lineTo(rightBottom.x, rightBottom.y);
        mHeightPath.lineTo(leftBottom.x, leftBottom.y);
        mHeightPath.lineTo(leftTop.x, leftTop.y);
        return mHeightPath;
    }

    /**
     * 创建一个带圆角蒙版的路径
     * Create a path with a rounded corner mask
     *
     * @param center
     * @param rotation
     * @param maskWidth
     * @param maskHeight
     * @param screenWidth
     * @param roundCornerDis
     * @return
     */
    public static Path buildMaskCornerPath(PointF center, float rotation, int maskWidth, int maskHeight, int screenWidth, int roundCornerDis) {
        Path mHeightPath = new Path();
        PointF leftTop = new PointF(0, 0);
        PointF rightTop = new PointF(center.x - maskWidth / 2, 0);
        PointF rightBottom = new PointF(center.x - maskWidth / 2, center.y - roundCornerDis);
        PointF leftBottom = new PointF(0, center.y - roundCornerDis);
        leftTop = getPointByAngle(leftTop, center, rotation);
        rightTop = getPointByAngle(rightTop, center, rotation);
        rightBottom = getPointByAngle(rightBottom, center, rotation);
        leftBottom = getPointByAngle(leftBottom, center, rotation);
        mHeightPath.moveTo(leftTop.x, leftTop.y);
        mHeightPath.lineTo(rightTop.x, rightTop.y);
        mHeightPath.lineTo(rightBottom.x, rightBottom.y);
        mHeightPath.lineTo(leftBottom.x, leftBottom.y);
        mHeightPath.lineTo(leftTop.x, leftTop.y);
        return mHeightPath;
    }

    /**
     * 计算旋转角度后的坐标
     * Coordinates after calculating the rotation angle
     *
     * @param p       目标点坐标  Target point coordinates
     * @param pCenter 旋转中心点坐标，锚点 Rotate center coordinates, anchor points
     * @param angle   旋转角度 Angle of rotation
     * @return 旋转角度后对应的坐标点 The corresponding coordinate point after the rotation Angle
     */
    public static PointF getPointByAngle(PointF p, PointF pCenter, float angle) {
//        float l = (float) ((angle * Math.PI) / 180);
//
//        //sin/cos value
//        float cosv = (float) Math.cos(l);
//        float sinv = (float) Math.sin(l);
//
//        // calc new point
//        float newX = (float) ((p.x - pCenter.x) * cosv - (p.y - pCenter.y) * sinv + pCenter.x);
//        float newY = (float) ((p.x - pCenter.x) * sinv + (p.y - pCenter.y) * cosv + pCenter.y);
//        //Log.e(TAG,"X = "+newX +"  Y ="+newX +"  angle="+angle);
        return transformData(p, pCenter, 1.0f, angle);
    }

    /**
     * 点对点的平移旋转等映射方法
     * Point-to-point translation and rotation mapping methods
     *
     * @param point
     * @param centerPoint
     * @param scale
     * @param degree
     * @return
     */
    public static PointF transformData(PointF point, PointF centerPoint, float scale, float degree) {
        float[] src = new float[]{point.x, point.y};
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        point.x = Math.round(src[0]);
        point.y = Math.round(src[1]);
        return point;
    }

    /**
     * 线性蒙版使用 为了达到旋转之后的效果
     * Linear mask is used in order to achieve the effect after rotation
     *
     * @param center 锚点坐标 Anchor coordinates
     * @param angle  旋转角度 Angle of rotation
     * @return 新坐标集 New coordinate set
     */
    public static PointF[] buildLineMaskPoint(PointF center, int maskWidth, int maskHeight, float angle) {
        PointF leftTopPoint = new PointF(center.x - maskWidth, center.y - maskHeight);

        PointF rightTopPoint = new PointF(center.x + maskWidth, center.y - maskHeight);

        PointF rightBottomPoint = new PointF(center.x + maskWidth, center.y);

        PointF leftBottomPoint = new PointF(center.x - maskWidth, center.y);

        return new PointF[]{getPointByAngle(leftTopPoint, center, angle), getPointByAngle(rightTopPoint, center, angle),
                getPointByAngle(rightBottomPoint, center, angle), getPointByAngle(leftBottomPoint, center, angle)};
    }

    /**
     * 绘制镜面蒙版的区域
     * Draw the area of the mirror mask
     *
     * @param center
     * @param maskWidth
     * @param maskHeight
     * @param angle
     * @return
     */
    public static PointF[] buildMirrorMaskPoint(int maskWidth, PointF center, int maskHeight, float angle) {
        PointF leftTopPoint = new PointF(center.x - maskWidth / 2, center.y - maskHeight / 2);
        PointF rightTopPoint = new PointF(center.x + maskWidth / 2, center.y - maskHeight / 2);
        PointF rightBottomPoint = new PointF(center.x + maskWidth / 2, center.y + maskHeight / 2);
        PointF leftBottomPoint = new PointF(center.x - maskWidth / 2, center.y + maskHeight / 2);
        return new PointF[]{getPointByAngle(leftTopPoint, center, angle), getPointByAngle(rightTopPoint, center, angle),
                getPointByAngle(rightBottomPoint, center, angle), getPointByAngle(leftBottomPoint, center, angle)};
    }

    /**
     * 构建局部特效区域  椭圆形区域
     * Build a local special effects area, an oval area
     *
     * @param center     中心点坐标 Center point coordinates
     * @param maskWidth  蒙版宽 Mask width
     * @param maskHeight 蒙版高 Mask height
     * @param angle      旋转角度 Angle of rotation
     * @return
     */
    public static NvsMaskRegionInfo buildCircleMaskRegionInfo(PointF center, float maskWidth, float maskHeight,
                                                              float angle, NvsLiveWindow liveWindow,
                                                              PointF size) {
        float widthPercent = 0, heightPercent = 0;
        widthPercent = maskWidth * 1.0f / size.x;
        heightPercent = maskHeight * 1.0f / size.y;
        center = mapViewToNormalized(center, liveWindow, size);
        //局部特效区域信息 Local effects area information
        NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_ELLIPSE2D);
        /*
         * 中心点坐标
         * 长半轴长 对应屏幕宽度的比例 [-1,1]区间
         * 短半轴长 对应屏幕高度度的比例 [-1,1]区间
         * 旋转角度
         * Center point coordinates
         * The length of the long half-axis corresponds to the ratio of the screen width [-1,1]
         * The length of the short half axis corresponds to the ratio of the screen height [-1,1]
         * Angle of rotation
         */
        regionInfo.setEllipse2D(new NvsMaskRegionInfo.Ellipse2D(new NvsPosition2D(center.x, center.y), widthPercent, heightPercent, 0));
        NvsMaskRegionInfo.Transform2D transform2D = new NvsMaskRegionInfo.Transform2D();
        transform2D.setRotation(-angle);
        transform2D.setAnchor(new NvsPosition2D(center.x, center.y));
        regionInfo.setTransform2D(transform2D);
        nvsMaskRegionInfo.addRegionInfo(regionInfo);
        return nvsMaskRegionInfo;
    }

    /**
     * 构建局部特效区域  矩形区域  带圆角所以用这个
     * Build a local special effect area, a rectangular area with rounded corners, so use this
     *
     * @param center           中心点坐标 Center point coordinates
     * @param width            椭圆长半轴 对应的屏幕比例 The screen scale corresponding to the major axis of the ellipse
     * @param height           椭圆短半轴 对应的屏幕比例 The short axis of the ellipse corresponds to the screen scale
     * @param angle            旋转角度 Angle of rotation
     * @param cornerRadiusRate 圆角比例 Fillet ratio
     * @return
     */
    public static NvsMaskRegionInfo buildRectMaskRegionInfo(PointF center, int width, int height,
                                                            float angle, NvsLiveWindow liveWindow,
                                                            float cornerRadiusRate, PointF size) {

//        center = liveWindow.mapViewToNormalized(center);
        //局部特效区域信息 Local effects area information
        NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_CUBIC_CURVE);
        //获取最小边 获取 圆角的半径 Get the minimum edge get the radius of the fillet
        int minSize = width > height ? height : width;
        int arcRadius = (int) (cornerRadiusRate * minSize * 0.5f);
        int controlPointDis = (int) (arcRadius * 0.45);

        //第一个点 First point
        PointF nextPoint1 = new PointF(center.x - width * 0.5f, center.y - height * 0.5f + controlPointDis);
        PointF curPoint1 = new PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius);
        PointF prePoint1 = new PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius);
        Log.d(TAG, "point 1 c1.x:" + curPoint1.x + " y:" + curPoint1.y + " n1.x:" + nextPoint1.x
                + " y:" + nextPoint1.y + " p1.x:" + prePoint1.x + " y:" + prePoint1.y + " aD:" + arcRadius + "cD:" + controlPointDis);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint1, center, angle),
                getPointByAngle(nextPoint1, center, angle), getPointByAngle(prePoint1, center, angle), liveWindow, size);

        //第二点 Second point
        PointF nextPoint2 = new PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius);
        PointF curPoint2 = new PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius);
        PointF prePoint2 = new PointF(center.x - width * 0.5f, center.y + height * 0.5f - controlPointDis);
        Log.d(TAG, "point 2 c2.x:" + curPoint2.x + " y:" + curPoint2.y + " n2.x:" + nextPoint2.x
                + " y:" + nextPoint2.y + " p2.x:" + prePoint2.x + " y:" + prePoint2.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint2, center, angle),
                getPointByAngle(nextPoint2, center, angle), getPointByAngle(prePoint2, center, angle), liveWindow, size);

        //第三点 Third point
        PointF nextPoint3 = new PointF(center.x - width * 0.5f + controlPointDis, center.y + height * 0.5f);
        PointF curPoint3 = new PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f);
        PointF prePoint3 = new PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f);
        Log.d(TAG, "point 3 c3.x:" + curPoint3.x + " y:" + curPoint3.y + " n3.x:" + nextPoint3.x
                + " y:" + nextPoint3.y + " p1.x:" + prePoint3.x + " y:" + prePoint3.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint3, center, angle),
                getPointByAngle(nextPoint3, center, angle), getPointByAngle(prePoint3, center, angle), liveWindow, size);

        //第四点 Fourth point
        PointF nextPoint4 = new PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f);
        PointF curPoint4 = new PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f);
        PointF prePoint4 = new PointF(center.x + width * 0.5f - controlPointDis, center.y + height * 0.5f);
        Log.d(TAG, "point 4 c4.x:" + curPoint4.x + " y:" + curPoint4.y + " n4.x:" + nextPoint4.x
                + " y:" + nextPoint4.y + " p4.x:" + prePoint4.x + " y:" + prePoint4.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint4, center, angle),
                getPointByAngle(nextPoint4, center, angle), getPointByAngle(prePoint4, center, angle), liveWindow, size);

        //第五点 Fifth point
        PointF nextPoint5 = new PointF(center.x + width * 0.5f, center.y + height * 0.5f - controlPointDis);
        PointF curPoint5 = new PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius);
        PointF prePoint5 = new PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius);
        Log.d(TAG, "point 5 c5.x:" + curPoint5.x + " y:" + curPoint5.y + " n5.x:" + nextPoint5.x
                + " y:" + nextPoint5.y + " p5.x:" + prePoint5.x + " y:" + prePoint5.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint5, center, angle),
                getPointByAngle(nextPoint5, center, angle), getPointByAngle(prePoint5, center, angle), liveWindow, size);

        //第六点 sixth point
        PointF nextPoint6 = new PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius);
        PointF curPoint6 = new PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius);
        PointF prePoint6 = new PointF(center.x + width * 0.5f, center.y - height * 0.5f + controlPointDis);
        Log.d(TAG, "point 6 c6.x:" + curPoint6.x + " y:" + curPoint6.y + " n6.x:" + nextPoint6.x
                + " y:" + nextPoint6.y + " p6.x:" + prePoint6.x + " y:" + prePoint6.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint6, center, angle),
                getPointByAngle(nextPoint6, center, angle), getPointByAngle(prePoint6, center, angle), liveWindow, size);

        //第七点 seventh point
        PointF nextPoint7 = new PointF(center.x + width * 0.5f - controlPointDis, center.y - height * 0.5f);
        PointF curPoint7 = new PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f);
        PointF prePoint7 = new PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f);
        Log.d(TAG, "point 7 c7.x:" + curPoint7.x + " y:" + curPoint7.y + " n7.x:" + nextPoint7.x
                + " y:" + nextPoint7.y + " p1.x:" + prePoint7.x + " y:" + prePoint7.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint7, center, angle),
                getPointByAngle(nextPoint7, center, angle), getPointByAngle(prePoint7, center, angle), liveWindow, size);

        //第八点 eight point
        PointF nextPoint8 = new PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f);
        PointF curPoint8 = new PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f);
        PointF prePoint8 = new PointF(center.x - width * 0.5f + controlPointDis, center.y - height * 0.5f);
        Log.d(TAG, "point 8 c8.x:" + curPoint8.x + " y:" + curPoint8.y + " n8.x:" + nextPoint8.x
                + " y:" + nextPoint8.y + " p8.x:" + prePoint8.x + " y:" + prePoint8.y);
        maskRegionInfoAddPoints(regionInfo, getPointByAngle(curPoint8, center, angle),
                getPointByAngle(nextPoint8, center, angle), getPointByAngle(prePoint8, center, angle), liveWindow, size);
        Log.d(TAG, "point ====================================================================================");

        nvsMaskRegionInfo.addRegionInfo(regionInfo);
        return nvsMaskRegionInfo;
    }

    public static List<PointF> buildRectMaskRegionInfo(PointF center, int width, int height, int angle, float cornerRadiusRate) {
        List<PointF> pointFS = new ArrayList<>();
        //设置类型 Setting type
        int minSize = width > height ? height : width;
        int arcRadius = (int) (cornerRadiusRate * minSize * 0.5f);
        int controlPointDis = (int) (arcRadius * 0.45);
        angle = angle * 2;
        //第一个点  First point
        PointF nextPoint1 = new PointF(center.x - width * 0.5f, center.y - height * 0.5f + controlPointDis);
        PointF curPoint1 = new PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius);
        PointF prePoint1 = new PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius);
        Log.d(TAG, "point 1 c1.x:" + curPoint1.x + " y:" + curPoint1.y + " n1.x:" + nextPoint1.x
                + " y:" + nextPoint1.y + " p1.x:" + prePoint1.x + " y:" + prePoint1.y + " aD:" + arcRadius + "cD:" + controlPointDis);
        addPoints(pointFS, getPointByAngle(nextPoint1, center, angle), getPointByAngle(curPoint1, center, angle));

        //第二点 Second point
        PointF nextPoint2 = new PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius);
        PointF curPoint2 = new PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius);
        PointF prePoint2 = new PointF(center.x - width * 0.5f, center.y + height * 0.5f - controlPointDis);
        Log.d(TAG, "point 2 c2.x:" + curPoint2.x + " y:" + curPoint2.y + " n2.x:" + nextPoint2.x
                + " y:" + nextPoint2.y + " p2.x:" + prePoint2.x + " y:" + prePoint2.y);
        addPoints(pointFS, getPointByAngle(curPoint2, center, angle), getPointByAngle(prePoint2, center, angle));

        //第三点 Third point
        PointF nextPoint3 = new PointF(center.x - width * 0.5f + controlPointDis, center.y + height * 0.5f);
        PointF curPoint3 = new PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f);
        PointF prePoint3 = new PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f);
        Log.d(TAG, "point 3 c3.x:" + curPoint3.x + " y:" + curPoint3.y + " n3.x:" + nextPoint3.x
                + " y:" + nextPoint3.y + " p1.x:" + prePoint3.x + " y:" + prePoint3.y);
        addPoints(pointFS, getPointByAngle(nextPoint3, center, angle),
                getPointByAngle(curPoint3, center, angle));

        //第四点 Fourth point
        PointF nextPoint4 = new PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f);
        PointF curPoint4 = new PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f);
        PointF prePoint4 = new PointF(center.x + width * 0.5f - controlPointDis, center.y + height * 0.5f);
        Log.d(TAG, "point 4 c4.x:" + curPoint4.x + " y:" + curPoint4.y + " n4.x:" + nextPoint4.x
                + " y:" + nextPoint4.y + " p4.x:" + prePoint4.x + " y:" + prePoint4.y);
        addPoints(pointFS, getPointByAngle(curPoint4, center, angle), getPointByAngle(prePoint4, center, angle));

        //第五点 Fifth point
        PointF nextPoint5 = new PointF(center.x + width * 0.5f, center.y + height * 0.5f - controlPointDis);
        PointF curPoint5 = new PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius);
        PointF prePoint5 = new PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius);
        Log.d(TAG, "point 5 c5.x:" + curPoint5.x + " y:" + curPoint5.y + " n5.x:" + nextPoint5.x
                + " y:" + nextPoint5.y + " p5.x:" + prePoint5.x + " y:" + prePoint5.y);
        addPoints(pointFS, getPointByAngle(nextPoint5, center, angle), getPointByAngle(curPoint5, center, angle));

        //第六点 sixth point
        PointF nextPoint6 = new PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius);
        PointF curPoint6 = new PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius);
        PointF prePoint6 = new PointF(center.x + width * 0.5f, center.y - height * 0.5f + controlPointDis);
        Log.d(TAG, "point 6 c6.x:" + curPoint6.x + " y:" + curPoint6.y + " n6.x:" + nextPoint6.x
                + " y:" + nextPoint6.y + " p6.x:" + prePoint6.x + " y:" + prePoint6.y);
        addPoints(pointFS, getPointByAngle(curPoint6, center, angle), getPointByAngle(prePoint6, center, angle));

        //第七点 seventh point
        PointF nextPoint7 = new PointF(center.x + width * 0.5f - controlPointDis, center.y - height * 0.5f);
        PointF curPoint7 = new PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f);
        PointF prePoint7 = new PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f);
        Log.d(TAG, "point 7 c7.x:" + curPoint7.x + " y:" + curPoint7.y + " n7.x:" + nextPoint7.x
                + " y:" + nextPoint7.y + " p1.x:" + prePoint7.x + " y:" + prePoint7.y);
        addPoints(pointFS, getPointByAngle(nextPoint7, center, angle), getPointByAngle(curPoint7, center, angle));

        //第八点  eight point
        PointF nextPoint8 = new PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f);
        PointF curPoint8 = new PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f);
        PointF prePoint8 = new PointF(center.x - width * 0.5f + controlPointDis, center.y - height * 0.5f);
        Log.d(TAG, "point 8 c8.x:" + curPoint8.x + " y:" + curPoint8.y + " n8.x:" + nextPoint8.x
                + " y:" + nextPoint8.y + " p8.x:" + prePoint8.x + " y:" + prePoint8.y);
        addPoints(pointFS, getPointByAngle(curPoint8, center, angle), getPointByAngle(prePoint8, center, angle));
        Log.d(TAG, "point ====================================================================================");
        return pointFS;
    }

    private static void addPoints(List<PointF> pointFS, PointF... curPoints) {
        for (PointF curPoint : curPoints) {
            pointFS.add(new PointF(curPoint.x, curPoint.y));
        }
    }

    /**
     * 添加集合点
     * Add mask points to the collection
     *
     * @param info
     * @param prePoint
     * @param curPoint
     * @param nextPoint
     * @param liveWindow
     */
    private static void maskRegionInfoAddPoints(NvsMaskRegionInfo.RegionInfo info, PointF curPoint,
                                                PointF nextPoint, PointF prePoint, NvsLiveWindow liveWindow,
                                                PointF size) {

        PointF[] pointArray = new PointF[3];
        pointArray[0] = curPoint;
        pointArray[1] = nextPoint;
        pointArray[2] = prePoint;
        Log.e(TAG, "point mask c.x:" + curPoint.x + " y:" + curPoint.y + " n.x:" + nextPoint.x
                + " y:" + nextPoint.y + " p.x:" + prePoint.x + " y:" + prePoint.y);

        List<NvsPosition2D> position2DList = buildNvsPositionListFromPointFList(pointArray, liveWindow, size);
        info.getPoints().addAll(position2DList);

    }

    /**
     * 构建局部特效区域  心形区域
     * Build a local special effects area, a heart-shaped area
     *
     * @param center 中心点坐标
     * @param radius
     * @param angle  旋转角度
     * @return
     */
    public static NvsMaskRegionInfo buildHeartMaskRegionInfo(PointF center, int radius, float angle,
                                                             NvsLiveWindow liveWindow, PointF size) {
        //局部特效区域信息 Center point coordinates
        NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        // 贝塞尔曲线MASK_REGION_TYPE_CUBIC_CURVE+
        NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_CUBIC_CURVE);

        PointF topIntersectionPoint = new PointF(center.x, center.y - radius * (2 * 1.0f / 6));
        PointF bottomIntersectionPoint = new PointF(center.x, center.y + radius);

        PointF prePoint = getPointByAngle(new PointF(center.x + 5 * 1.0f / 7 * radius, center.y - 0.8f * radius), center, angle);
        PointF curPoint = getPointByAngle(topIntersectionPoint, center, angle);
        PointF nextPoint = getPointByAngle(new PointF(center.x - 5 * 1.0f / 7 * radius, center.y - 0.8f * radius), center, angle);


        PointF prePoint1 = getPointByAngle(new PointF(center.x - 16 * 1.0f / 13 * radius, center.y + 0.1f * radius), center, angle);
        PointF curPoint1 = getPointByAngle(bottomIntersectionPoint, center, angle);
        PointF nextPoint1 = getPointByAngle(new PointF(center.x + 16 * 1.0f / 13 * radius, center.y + 0.1f * radius), center, angle);

        PointF[] pointFS = new PointF[]{curPoint, nextPoint, prePoint, curPoint1, nextPoint1, prePoint1};
        List<NvsPosition2D> nvsPosition2DS = buildNvsPositionListFromPointFList(pointFS, liveWindow, size);

        regionInfo.setPoints(nvsPosition2DS);
        nvsMaskRegionInfo.addRegionInfo(regionInfo);
        return nvsMaskRegionInfo;
    }

    /**
     * 构建局部特效区域  多边形区域
     * Build local special effects area Polygonal area
     *
     * @param pointFList
     * @param size
     * @return
     */
    public static NvsMaskRegionInfo buildPolygonMaskRegionInfo(PointF[] pointFList, NvsLiveWindow liveWindow, PointF size) {
        List<NvsPosition2D> nvsPosition2DS = buildNvsPositionListFromPointFList(pointFList, liveWindow, size);
        //局部特效区域信息 Local effects area information
        NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON);
        regionInfo.setPoints(nvsPosition2DS);
        nvsMaskRegionInfo.addRegionInfo(regionInfo);
        return nvsMaskRegionInfo;
    }

    /**
     * 构建星型蒙版数据
     * Build star mask data
     *
     * @param center
     * @param width
     * @param rotation
     * @param liveWindow
     * @param size
     * @return
     */
    public static NvsMaskRegionInfo buildStarMaskRegionInfo(PointF center, int width, float rotation, NvsLiveWindow liveWindow, PointF size) {
        //局部特效区域信息 Center point coordinates
        NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        // 贝塞尔曲线MASK_REGION_TYPE_CUBIC_CURVE+
        NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON);

        //外圆 Outer circle
        float radius = width / 2.0f;
        float angel = (float) (Math.PI * 2 / 5);
        PointF[] outPoints = new PointF[5];
        //这里是获取五角星的五个定点的坐标点位置 Here are the five points of the five-pointed star
        for (int i = 1; i < 6; i++) {
            float x = (float) (center.x - Math.sin(i * angel) * radius);
            float y = (float) (center.y - Math.cos(i * angel) * radius);
            outPoints[i - 1] = new PointF(x, y);
        }

        /// 越大越胖 Bigger and fatter
        float radiusRate = 0.5f; //2/5
        //内圆 Inner circle
        float internalRadius = radius * radiusRate;
        float internalAngel = (float) (Math.PI * 2 / 5);
        PointF[] inPoints = new PointF[5];
        //这里是获取五角星的五个定点的坐标点位置 Here are the five points of the five-pointed star
        for (int i = 1; i < 6; i++) {
            float x = (float) (center.x - Math.sin(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius);
            float y = (float) (center.y - Math.cos(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius);
            inPoints[i - 1] = new PointF(x, y);
        }

        //加入到一个集合中 一外一内的顺序 The order in which one is added to a set
        PointF[] allPoints = new PointF[10];
        for (int i = 0; i < 5; i++) {
            PointF out = getPointByAngle(outPoints[i], center, rotation);
            PointF in = getPointByAngle(inPoints[i], center, rotation);
            allPoints[i * 2] = out;
            allPoints[i * 2 + 1] = in;
        }
        List<NvsPosition2D> position2DList = buildNvsPositionListFromPointFList(allPoints, liveWindow, size);

        regionInfo.setPoints(position2DList);
        nvsMaskRegionInfo.addRegionInfo(regionInfo);

        return nvsMaskRegionInfo;
    }

    /**
     * 转换点位集合为sdk需要的参数
     * Convert the set of points to the parameters required by the SDK
     *
     * @return
     */
    private static PointF mapViewToNormalized(PointF pointF, NvsLiveWindow liveWindow, PointF size) {
//        if (Constants.EnableRawFilterMaskRender) {
//            PointF liveWindowCenterPoint = new PointF(liveWindow.getWidth() * 0.5f, liveWindow.getHeight() * 0.5f);
//            float xValue = (pointF.x - liveWindowCenterPoint.x) / (size.x * 0.5f);
//            float yValue = -(pointF.y - liveWindowCenterPoint.y) / (size.y * 0.5f);
//        }
//        PointF pointF1 = liveWindow.mapViewToNormalized(pointF);
        PointF livePoint = new PointF();
        livePoint.x = -(liveWindow.getWidth() / 2f - pointF.x) / size.x * 2f;
        livePoint.y = (liveWindow.getHeight() / 2f - pointF.y) / size.y * 2f;
        return livePoint;
    }


    /**
     * 转换点位集合为sdk需要的参数
     * Convert the set of points to the parameters required by the SDK
     *
     * @return
     */
    private static List<NvsPosition2D> buildNvsPositionListFromPointFList(PointF[] pointFList, NvsLiveWindow liveWindow, PointF size) {
        List<NvsPosition2D> nvsPosition2DS = new ArrayList<>();
        if (null != pointFList && pointFList.length > 0) {
            for (PointF pointF : pointFList) {
                PointF result = mapViewToNormalized(pointF, liveWindow, size);
                nvsPosition2DS.add(new NvsPosition2D(result.x, result.y));
            }
        }
        return nvsPosition2DS;
    }

    /**
     * 构建sdk所需要的蒙版数据
     * Build meicam mask region info meicam mask region info.
     *
     * @param maskData         the mask data 蒙版上层数据
     * @param liveWindowExt    the live window ext  liveWindow
     * @param rotationFx       the rotation fx 属性特效旋转角度
     * @param fxTransformX     the fx transform x  属性特效X平移
     * @param fxTransformY     the fx transform y  属性特效Y平移
     * @param fxScale          the fx scale 属性特效缩放
     * @param assetAspectRatio
     * @return the meicam mask region info
     */
    public static void buildRealMaskInfoData(MaskInfoData maskData, NvsLiveWindow liveWindowExt,
                                             float rotationFx, float fxTransformX,
                                             float fxTransformY, float fxScale, float assetAspectRatio) {
        if (maskData == null || liveWindowExt == null || fxScale == 0) return;
        PointF size = assetSizeInBox(liveWindowExt, assetAspectRatio);
        NvsMaskRegionInfo nvsMaskRegionInfo = null;
        PointF liveWindowCenter = maskData.getLiveWindowCenter();
        PointF mCenter = new PointF(liveWindowCenter.x, liveWindowCenter.y);
        Log.e(TAG, "mCenter x:" + mCenter.x + " y:" + mCenter.y);
        PointF transform = NvMaskHelper.transformData(new PointF(maskData.getTranslationX(), maskData.getTranslationY())
                , new PointF(0, 0), 1.0f / fxScale, rotationFx);
        mCenter.x += transform.x;
        mCenter.y += transform.y;
//        mCenter.x = (mCenter.x + fxTransformX);
//        mCenter.y = (mCenter.y - fxTransformY);
        int maskWidth = maskData.getMaskWidth();
        int maskHeight = maskData.getMaskHeight();
        float rotation = maskData.getRotation();
        Log.e(TAG, "mCenter x:" + mCenter.x + " y:" + mCenter.y + " maskWidth:" + maskWidth + "maskHeight:" + maskHeight);
        float cornerRadiusRate = maskData.getRoundCornerWidthRate();
        Log.e(TAG, "rotation = " + rotation);
        if (maskData.getMaskType() == MaskInfoData.MaskType.NONE) {
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.LINE) {
            nvsMaskRegionInfo = buildPolygonMaskRegionInfo(
                    buildLineMaskPoint(mCenter, maskWidth, maskHeight, rotation),
                    liveWindowExt, size);
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.MIRROR) {
            nvsMaskRegionInfo = buildPolygonMaskRegionInfo(
                    buildMirrorMaskPoint(maskWidth, mCenter, maskHeight, rotation),
                    liveWindowExt, size);
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.CIRCLE) {
            nvsMaskRegionInfo = buildCircleMaskRegionInfo(mCenter, maskWidth, maskHeight, rotation, liveWindowExt, size);
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.RECT) {
            nvsMaskRegionInfo = buildRectMaskRegionInfo(mCenter, maskWidth, maskHeight, rotation, liveWindowExt, cornerRadiusRate, size);
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.HEART) {
            nvsMaskRegionInfo = buildHeartMaskRegionInfo(mCenter, maskWidth, rotation, liveWindowExt, size);
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.STAR) {
            nvsMaskRegionInfo = buildStarMaskRegionInfo(mCenter, maskWidth, rotation, liveWindowExt, size);
        } else if (maskData.getMaskType() == MaskInfoData.MaskType.TEXT) {
            // 设置蒙版数据 !!!此处y偏移量给的负值 旋转也是负值 由于计算storyboard的偏移与liveWindow对应添加上缩放值即 /fxScale
            //Set mask data!! And here the y offset gives us a negative rotation because we're calculating the offset of the storyboard corresponding to the liveWindow and we're adding a scaling value which is /fxScale
            String storyBoard = StoryboardUtil.getMaskTextStoryboard(liveWindowExt.getWidth(),
                    liveWindowExt.getHeight(), (int) maskData.getSingleTextHeight(), maskData.getText(), 100000,
                    maskData.getScale(), maskData.getScale(), maskData.getTranslationX() / fxScale,
                    -maskData.getTranslationY() / fxScale, -maskData.getRotation());
            maskData.setTextStoryboard(storyBoard);
        }
        maskData.setMaskRegionInfo(nvsMaskRegionInfo);
    }

    /**
     * 计算实际显示的宽高
     * Calculate the actual width and height of the display
     *
     * @param liveWindowExt
     * @param assetAspectRatio 原视频宽高比  Aspect ratio of the original video
     * @return
     */
    public static PointF assetSizeInBox(NvsLiveWindow liveWindowExt, float assetAspectRatio) {
        PointF pointF = new PointF();
        float liveWindowWidth = liveWindowExt.getWidth() * 1.0f;
        float liveWindowHeight = liveWindowExt.getHeight() * 1.0f;
        float boxSizeRate = liveWindowWidth * 1.0f / liveWindowHeight;
        if ((int) (boxSizeRate * 100) == (int) (assetAspectRatio * 100)) {
            pointF.x = liveWindowWidth;
            pointF.y = liveWindowHeight;
        } else if (boxSizeRate > assetAspectRatio) {
            pointF.y = liveWindowHeight;
            pointF.x = pointF.y * assetAspectRatio;
        } else {
            pointF.x = liveWindowWidth;
            pointF.y = pointF.x / assetAspectRatio;
        }
        return pointF;
    }

    /**
     * 构建字幕蒙版path
     * build caption text mask path
     *
     * @param maskWidth
     * @param maskHeight
     * @param center
     * @return
     */
    public static Path textRegionInfoPath(int maskWidth, int maskHeight, PointF center) {
        // 重新绘制 redraw
        Path path = new Path();
        //直接绘制一个圆形矩形 Draw a circular rectangle directly
        int minSize = maskWidth > maskHeight ? maskHeight : maskWidth;
        path.addRoundRect(new RectF(center.x - maskWidth / 2F, center.y - maskHeight / 2F,
                        center.x + maskWidth / 2F, center.y + maskHeight / 2F),
                0, 0, Path.Direction.CCW);
        path.moveTo(center.x, center.y);
        return path;
    }

    /**
     * 计算蒙版文字的大小并重新设置
     * Calculate the size of the mask text and reset
     *
     * @param
     * @return
     */
    public static void buildMaskText(MaskInfoData maskInfoData) {
        if (maskInfoData == null || maskInfoData.getMaskType() != MaskInfoData.MaskType.TEXT)
            return;
        TextPaint tp = new TextPaint();
        float textWidth = 0f;
        float textHeight = 0f;
        float singleTextHeight = 0f;
        int numLines;
        String[] split = maskInfoData.getText().split("\n");
        numLines = split.length;
        tp.setTextSize(maskInfoData.getTextSize());
        for (int i = 0; i < split.length; i++) {
            String lineText = split[i];
            Rect rect = new Rect();
            tp.getTextBounds(lineText, 0, lineText.length(), rect);
            float width = rect.right - rect.left;
            float height = rect.bottom - rect.top;
            if (width > textWidth) {
                //取最宽 widest
                textWidth = width;
            }
            if (height > singleTextHeight) {
                singleTextHeight = height;
            }
            textHeight += height;
        }
        maskInfoData.setMaskHeight((int) ((textHeight + 10) * maskInfoData.getScale()));
        maskInfoData.setMaskWidth((int) ((textWidth + 10) * maskInfoData.getScale()));
        Log.d(TAG, "buildMaskText =-= w:" + maskInfoData.getMaskWidth() + " h:" + maskInfoData.getMaskHeight());
        maskInfoData.setSingleTextHeight(singleTextHeight);
    }

    /**
     * 创建裁剪区域的矩形蒙版数据
     * Create a rectangular mask data for the cropped area
     *
     * @param cropInfo
     * @return
     */
    public static NvsMaskRegionInfo buildMaskRegionRect(CropInfo cropInfo) {

        float[] regionData = cropInfo.getRegionData();
        //局部特效区域信息 Local effects area information
        NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
        List<NvsPosition2D> nvsPosition2DS = new ArrayList<>();
        if (null != regionData && regionData.length >= 8) {
            for (int i = 0; i < regionData.length; i++) {
                nvsPosition2DS.add(new NvsPosition2D(regionData[i], regionData[++i]));
            }
        }
        // 多边形 MASK_REGION_TYPE_POLYGON
        NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON);
        regionInfo.setPoints(nvsPosition2DS);
        nvsMaskRegionInfo.addRegionInfo(regionInfo);
        return nvsMaskRegionInfo;
    }

    /**
     * 通过裁剪重新计算蒙版
     * 注：如果裁剪使用crop特技，则需要重新计算。如果是mask generator特技则不需要
     * Recalculate the mask by cropping
     * Note: If crop stunt is used for cropping, recalculation is required. This parameter is not required for mask generator
     *
     * @param liveWindow   livewindow
     * @param clipInfo     clipInfo
     * @param maskInfoData data
     */
    public static boolean calculateMaskByCrop(NvsLiveWindow liveWindow, ClipInfo clipInfo, MaskInfoData maskInfoData) {
        if ((null == clipInfo) || (null == maskInfoData)) {
            return false;
        }
        CutData cutData = clipInfo.getCropInfo();
        if (null == cutData) {
            return false;
        }
        float scale = calculateScaleInBox(liveWindow, clipInfo);
        if (scale <= 0) {
            return false;
        }
        maskInfoData.setMaskWidth((int) (maskInfoData.getMaskWidth() * scale));
        maskInfoData.setMaskHeight((int) (maskInfoData.getMaskHeight() * scale));
        return true;
    }

    /**
     * 计算裁剪后在livewindow上的缩放值
     * Calculate the scaling value on the livewindow after cropping
     *
     * @param liveWindow livewindow
     * @param info       info
     * @return boolean
     */
    private static float calculateScaleInBox(NvsLiveWindow liveWindow, ClipInfo info) {
        if (null == info) {
            return -1;
        }
        float fileRatio = info.getFileRatio();
        String filePath = info.getFilePath();
        NvsAVFileInfo avInfoFromFile = NvsStreamingContext.getAVInfoFromFile(filePath, 0);
        if (null == avInfoFromFile) {
            return -1;
        }
        int videoWidth = avInfoFromFile.getVideoStreamDimension(0).width;
        int videoHeight = avInfoFromFile.getVideoStreamDimension(0).height;
        int videoStreamRotation = avInfoFromFile.getVideoStreamRotation(0);
        int width = (videoStreamRotation % 2 == 1) ? videoHeight : videoWidth;
        int height = (videoStreamRotation % 2 == 1) ? videoWidth : videoHeight;

        float beforeRatio = width * 1.0F / height;
        PointF sizeBefore = NvMaskHelper.assetSizeInBox(liveWindow, beforeRatio);
        PointF sizeAfter = NvMaskHelper.assetSizeInBox(liveWindow, fileRatio);
        float scaleX = Util.getSpecificValue(sizeBefore.x, sizeAfter.x);
        float scaleY = Util.getSpecificValue(sizeBefore.y, sizeAfter.y);
        return Math.min(scaleX, scaleY);
    }
}
