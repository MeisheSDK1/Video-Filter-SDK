package com.meishe.makeup.makeup.original;


import java.io.Serializable;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/9 18:41
 * @Description :美妆特效内容 The makeup effect content
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupOldParamContent implements Serializable {
    private List<MakeupOldParam> makeupArgs;
    private List<BeautyOldParam> beauty;
    private List<ShapeOldParam> shape;
    private List<ShapeOldParam> microShape;
    private List<FilterOldParam> filter;

    public List<MakeupOldParam> getMakeupParams() {
        return makeupArgs;
    }

    public void setMakeupParams(List<MakeupOldParam> makeupParams) {
        this.makeupArgs = makeupParams;
    }

    public List<BeautyOldParam> getBeautyParams() {
        return beauty;
    }

    public void setBeautyParams(List<BeautyOldParam> beauty) {
        this.beauty = beauty;
    }

    public List<ShapeOldParam> getShapeParams() {
        return shape;
    }

    public void setShapeParams(List<ShapeOldParam> shape) {
        this.shape = shape;
    }

    public List<ShapeOldParam> getMicroShapeParams() {
        return microShape;
    }

    public void setMicroShapeParams(List<ShapeOldParam> microShape) {
        this.microShape = microShape;
    }

    public List<FilterOldParam> getFilterParams() {
        return filter;
    }

    public void setFilterParams(List<FilterOldParam> filter) {
        this.filter = filter;
    }
}
