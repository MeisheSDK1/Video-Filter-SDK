package com.meishe.sdkdemo.mimodemo.bean;

import com.meishe.http.bean.BaseDataBean;

import java.io.Serializable;
import java.util.List;
/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/3.
 * @Description :mimo网络数据。mimo online Data
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MimoOnlineData<T> implements Serializable {
    private int total;
    private int pageNum;
    private int pageSize;
    private List<BaseDataBean<T>> elements;

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

    public List<BaseDataBean<T>> getElements() {
        return elements;
    }

    public void setElements(List<BaseDataBean<T>> elements) {
        this.elements = elements;
    }
}
