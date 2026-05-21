package com.meishe.makeup.makeup.original;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/9/7 13:16
 * @Description :滤镜参数 Filter param
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterOldParam extends BaseOldParam {
    private int isBuiltIn;

    public boolean isBuiltIn() {
        return isBuiltIn >= 1;
    }

    public void setIsBuiltIn(int isBuiltIn) {
        this.isBuiltIn = isBuiltIn;
    }
}
