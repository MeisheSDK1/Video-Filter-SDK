package com.meishe.sdkdemo.edit.clipEdit.adjust;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/12/9 17:31
 * @Description :浮点类型的点 The point of float.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FloatPoint {
    public float x;
    public float y;

    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public FloatPoint() {
    }

    public FloatPoint(FloatPoint floatPoint) {
        if (floatPoint != null) {
            this.x = floatPoint.x;
            this.y = floatPoint.y;
        }
    }

    @Override
    public String toString() {
        return "FloatPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


    @Override
    public FloatPoint clone(){
        FloatPoint clone = new FloatPoint();
        clone.x = x;
        clone.y = y;
        return clone;
    }
}
