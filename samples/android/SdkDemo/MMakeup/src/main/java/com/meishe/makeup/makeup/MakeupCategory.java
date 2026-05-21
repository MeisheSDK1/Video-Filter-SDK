package com.meishe.makeup.makeup;

import java.io.Serializable;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/25 11:13
 * @Description :美妆分类 Makeup category
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupCategory implements Serializable {
    private int id;
    private int materialType;
    private int category;
    private String type;
    private int cover;
    private String displayName;
    private String displayNameZhCn;
    /**
     * 美妆分类的内容
     * Makeup category content
     **/
    private CategoryContent categoryContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
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

    public CategoryContent getCategoryContent() {
        return categoryContent;
    }

    public void setCategoryContent(CategoryContent categoryContent) {
        this.categoryContent = categoryContent;
    }
}
