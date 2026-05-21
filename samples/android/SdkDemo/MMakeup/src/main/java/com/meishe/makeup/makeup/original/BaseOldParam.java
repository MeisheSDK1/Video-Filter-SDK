package com.meishe.makeup.makeup.original;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/6 10:15
 * @Description :美妆基础参数 makeup base param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseOldParam implements Serializable {
    public String type;
    private int canReplace;

    @SerializedName("className")
    private String paramKey;

    @SerializedName("uuid")
    private String packageId;

    private float value = Float.MIN_VALUE;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean canReplace() {
        return canReplace > 0;
    }

    public void setCanReplace(int canReplace) {
        this.canReplace = canReplace;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean valueEnable() {
        return value > 0;
    }

    @Override
    public String toString() {
        return "BaseParam{" +
                "type='" + type + '\'' +
                ", canReplace=" + canReplace +
                ", paramKey='" + paramKey + '\'' +
                ", packageId='" + packageId + '\'' +
                ", value=" + value +
                '}';
    }
}
