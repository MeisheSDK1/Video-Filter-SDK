package com.meishe.cutsame.bean;


import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/10
 * desc   :模板列表实体类
 * Template list entity class
 */
public class TemplateList {
    private List<Template> elements;
    private int total;

    public List<Template> getElements() {
        return elements;
    }

    public void setElements(List<Template> elements) {
        this.elements = elements;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
