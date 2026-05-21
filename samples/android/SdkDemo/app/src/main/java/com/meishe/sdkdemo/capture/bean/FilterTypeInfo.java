package com.meishe.sdkdemo.capture.bean;

import java.io.Serializable;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/17 下午5:42
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterTypeInfo implements Serializable {
    private int id;
    private String displayName;
    private String displayNameZhCn;
    private int displayState;
    private String validExtension;
    private CategoryInfo categories;

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

    public CategoryInfo getCategories() {
        return categories;
    }

    public void setCategories(CategoryInfo categories) {
        this.categories = categories;
    }
}
