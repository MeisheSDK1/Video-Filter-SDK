package com.meishe.sdkdemo.capture.bean;

import java.io.Serializable;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/17 下午5:50
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CategoryInfo implements Serializable {

    private int id;
    private int type;
    private String displayName;
    private String displayNameZhCn;
    private List<KindInfo> kinds;

    /**
     * 用于数据请求，每页数据数量
     * Used for data request, data quantity per page
     */
    private int pageSize;
    /**
     * 第几页
     * page number
     */
    private int pageNumber;

    private int assetType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public List<KindInfo> getKinds() {
        return kinds;
    }

    public void setKinds(List<KindInfo> kinds) {
        this.kinds = kinds;
    }

    public int getAssetType() {
        return assetType;
    }

    public void setAssetType(int assetType) {
        this.assetType = assetType;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
