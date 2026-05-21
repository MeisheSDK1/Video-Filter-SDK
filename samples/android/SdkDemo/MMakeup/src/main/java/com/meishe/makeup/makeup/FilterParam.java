package com.meishe.makeup.makeup;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/7 13:16
 * @Description :滤镜参数 Filter param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterParam extends BaseParam {
    private int isBuiltIn;
    private float value;

    public boolean isBuiltIn() {
        return isBuiltIn >= 1;
    }

    public void setIsBuiltIn(int isBuiltIn) {
        this.isBuiltIn = isBuiltIn;
    }

    public int getIsBuiltIn() {
        return isBuiltIn;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
