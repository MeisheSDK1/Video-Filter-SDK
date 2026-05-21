package com.meishe.cutsame.bean;

import java.io.Serializable;
import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/10
 * desc   :模板分类实体类
 * Templates classify entity classes
 */
public class TemplateCategory {

    /**
     * The type Category.
     * 类别类型
     */
    public static class Category extends BaseCategory implements Serializable {

        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    /**
     * The type Category.
     * 类别类型
     */
    public static class CategoryList extends BaseCategory implements Serializable{
        public List<Category> categories;
    }

    public static class BaseCategory implements Serializable {
        private int id;
        private String displayName;
        private String displayNameZhCn;

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
    }
}
