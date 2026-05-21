package com.meishe.http.bean;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/18.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class QueryInteractiveResultDto {
    private String materialId;
    private int likeNum;
    private int useNum;

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getUseNum() {
        return useNum;
    }

    public void setUseNum(int useNum) {
        this.useNum = useNum;
    }
}
