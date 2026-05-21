package com.meishe.makeup.makeup;

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
public class MakeupParamContent implements Serializable {
    private List<MakeupParam> makeup;
    private List<BeautyParam> beauty;
    private List<BaseParam> shape;
    private List<BaseParam> microShape;
    private List<FilterParam> filter;
    private List<BeautyParam> adjust;

    public List<MakeupParam> getMakeupParams() {
        return makeup;
    }

    public boolean onlyOneMakeupParam() {
        return makeup != null && makeup.size() == 1;
    }

    public MakeupParam getMakeupParam(int index) {
        if (makeup == null) {
            return null;
        }
        if (index >= 0 && index < makeup.size()) {
            return makeup.get(index);
        }
        return null;
    }

    public void setMakeupParams(List<MakeupParam> makeupParams) {
        this.makeup = makeupParams;
    }

    public List<BeautyParam> getBeautyParams() {
        return beauty;
    }

    public void setBeautyParams(List<BeautyParam> beauty) {
        this.beauty = beauty;
    }

    public List<BaseParam> getShapeParams() {
        return shape;
    }

    public void setShapeParams(List<BaseParam> shape) {
        this.shape = shape;
    }

    public List<BaseParam> getMicroShapeParams() {
        return microShape;
    }

    public void setMicroShapeParams(List<BaseParam> microShape) {
        this.microShape = microShape;
    }

    public List<FilterParam> getFilterParams() {
        return filter;
    }

    public void setFilterParams(List<FilterParam> filter) {
        this.filter = filter;
    }

    public List<BeautyParam> getAdjustParams() {
        return adjust;
    }

    public void setAdjustParams(List<BeautyParam> adjust) {
        this.adjust = adjust;
    }
}
