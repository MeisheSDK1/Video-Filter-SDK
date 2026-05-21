package com.meishe.makeup.net;


import com.meishe.makeup.makeup.MakeupCategory;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/7 16:12
 * @Description: 素材分类信息 Material classification information
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeupCategoryInfo {
    private int id;
    private String displayName;
    private String displayNameZhCn;
    private int displayState;
    private String validExtension;
    private List<CategoriesBean> categories;

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

    public List<CategoriesBean> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesBean> categories) {
        this.categories = categories;
    }

    public static class CategoriesBean {
        private int id;
        private int type;
        private String displayName;
        private String displayNameZhCn;
        private List<MakeupCategory> kinds;

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

        public List<MakeupCategory> getKinds() {
            return kinds;
        }

        public void setKinds(List<MakeupCategory> kinds) {
            this.kinds = kinds;
        }
    }
}
