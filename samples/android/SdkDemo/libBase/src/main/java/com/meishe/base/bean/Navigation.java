package com.meishe.base.bean;

import androidx.annotation.IntDef;

import com.meishe.third.adpater.entity.MultiItemEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * author : lhz
 * date   : 2020/10/21
 * desc   :导航栏某一项的实体类 The entity class of an item in the navigation bar
 */
public class Navigation {
    public final static int LEVEL_0 = 0;//第一层。 first floor
    public final static int TYPE_DEFAULT = 0;
    public final static int TYPE_RATIO = 1;
    public final static int TYPE_CUSTOM = 2;
    private int level;//层级 >= 0 Level > = 0
    private int name;//层级名称 Hierarchieebenenname
    private int preName;//上一层级名称 The name of the upper level
    private List<Item> items;//该层级的列表 A list of the levels
    private Item selectedItem;//被选中的某项 Something that is selected

    public Navigation() {
    }

    public Navigation(int name, int preName, int level) {
        this.name = name;
        this.preName = preName;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Navigation setLevel(int level) {
        this.level = level;
        return this;
    }

    public int getName() {
        return name;
    }

    public Navigation setName(int name) {
        this.name = name;
        return this;
    }

    public int getPreName() {
        return preName;
    }

    public Navigation setPreName(int preName) {
        this.preName = preName;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public Navigation addItem(Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (items.size() > 0 && item.getTitleId() == items.get(items.size() - 1).getTitleId()) {
            return this;
        }
        items.add(item);
        return this;
    }

    public Navigation addItem(int position, Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (position == items.size()) {
            items.add(item);
        } else {
            items.add(position, item);
        }
        return this;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    /***
     * 注意：这里没有直接items.remove(remove);因为可能不是同一个对象
     * */
    public Navigation removeItem(Item remove) {
        if (items == null) {
            return this;
        }
        for (Item item : items) {
            if (remove.getTitleId() == item.getTitleId()) {
                items.remove(item);
                break;
            }
        }

        return this;
    }

    public void removeItem(int position) {
        if (items == null) {
            return;
        }
        if (position >= 0 && position < items.size()) {
            items.remove(position);
        }
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public static class Item implements MultiItemEntity {
        private int titleId;//本地标题资源id Local title resource ID
        private String title;//本地标题 Local title
        private int iconId;//本地图片资源id Local image resource ID
        //private String icon;//本地图片资源 Local image resources
        private int nextName;//下一层级名称 The name of the next level
        private boolean enable = true;//是否可用，保留字段，可以据此设置透明度等。 Whether it is available, keep the field, and set the transparency accordingly
        private int type = TYPE_DEFAULT;//导航栏类型,不同类型显示不同Ui样式 Navigation bar type. Different types display different Ui styles
        private int width;//宽度 width
        private int height;//高度 height
        private Object tag;//某些特殊tag

        public Item() {

        }

        public Item(String title) {
            this.title = title;
        }

        public Item(int titleId) {
            this.titleId = titleId;
        }

        public Item(String title, int iconId) {
            this.title = title;
            this.iconId = iconId;
        }

        public Item(int titleId, int iconId) {
            this.titleId = titleId;
            this.iconId = iconId;
        }

        public String getTitle() {
            return title;
        }

        public Item setTitle(String title) {
            this.title = title;
            return this;
        }

        public int getTitleId() {
            return titleId;
        }

        public Item setTitleId(int titleId) {
            this.titleId = titleId;
            return this;
        }

        public int getIconId() {
            return iconId;
        }

        public Item setIconId(int iconId) {
            this.iconId = iconId;
            return this;
        }

        public int getNextName() {
            return nextName;
        }

        public Item setNextName(int nextName) {
            this.nextName = nextName;
            return this;
        }

        public boolean isEnable() {
            return enable;
        }

        public Item setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public Item setWidth(int width) {
            this.width = width;
            return this;
        }

        public int getHeight() {
            return height;
        }

        public Item setHeight(int height) {
            this.height = height;
            return this;
        }

        public Object getTag() {
            return tag;
        }

        public Item setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Item setType(@NavigationType int type) {
            this.type = type;
            return this;
        }

        @Override
        public int getItemType() {
            return type;
        }


    }

    @IntDef({TYPE_DEFAULT, TYPE_RATIO, TYPE_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationType {
    }
}
