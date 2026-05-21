package com.meishe.makeup.makeup;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/6 14:45
 * @Description :美颜、美型参数 the beauty and the beauty shape param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyParam extends BaseParam {
    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
