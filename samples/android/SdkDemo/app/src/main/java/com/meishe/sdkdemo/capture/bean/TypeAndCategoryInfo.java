package com.meishe.sdkdemo.capture.bean;

import java.io.Serializable;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/18 下午3:31
 * @Description : 列别信息
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TypeAndCategoryInfo implements Serializable {
    private int id;
    private String displayName;
    private String displayNameZhCn;
    private int displayState;
    private String validExtension;
    private List<CategoryInfo> categories;

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

    public int getDisplayState() {
        return displayState;
    }

    public void setDisplayState(int displayState) {
        this.displayState = displayState;
    }

    public String getValidExtension() {
        return validExtension;
    }

    public void setValidExtension(String validExtension) {
        this.validExtension = validExtension;
    }

    public List<CategoryInfo> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryInfo> categories) {
        this.categories = categories;
    }
}
