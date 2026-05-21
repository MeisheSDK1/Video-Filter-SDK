package com.meishe.sdkdemo.bean;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/30.
 * @Description :中文 用来记录资源层级的Bean
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AssetLevelBean {
    private String tag;
    private List<PointF> data;

    public AssetLevelBean(List<PointF> data) {
        tag = "TAG" + System.currentTimeMillis();
        this.data = data;
    }

    public String getTag() {
        return tag;
    }

    public List<PointF> getData() {
        return data;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setData(List data) {
        this.data = data;
    }
}
