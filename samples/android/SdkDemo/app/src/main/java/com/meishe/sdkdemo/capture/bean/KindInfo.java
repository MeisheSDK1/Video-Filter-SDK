package com.meishe.sdkdemo.capture.bean;

import java.io.Serializable;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/17 下午5:51
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class KindInfo implements Serializable {
    /**
     * 第三级分类
     * Third level classification
     */
    private int id;
    /**
     * 第一级分类
     * First level classification
     */
    private int materialType;
    /**
     * 第二级分类
     * Second level classification
     */
    private int category;
    private String displayName;
    private String displayNameZhCn;
    private int pageNumber = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNameZhCn() {
        return displayNameZhCn;
    }

    public void setDisplayNameZhCn(String displayNameZhCn) {
        this.displayNameZhCn = displayNameZhCn;
    }

    public int getMaterialType() {
        return materialType;
    }

    public void setMaterialType(int materialType) {
        this.materialType = materialType;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
