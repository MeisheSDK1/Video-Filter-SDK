package com.meishe.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/21.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BaseBean<T> implements Serializable {

    private int total;
    private int pageNum;
    private int pageSize;
    private List<T> elements;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }
}
