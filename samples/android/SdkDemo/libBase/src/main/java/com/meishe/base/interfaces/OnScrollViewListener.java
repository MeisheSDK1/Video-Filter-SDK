package com.meishe.base.interfaces;

/**
 * Created by CaoZhiChao on 2020/8/10 13:43
 * 滚动视图监听类
 * Scroll view listens class
 */
public interface OnScrollViewListener {
    /**
     * On scroll changed.
     * 滚动变化
     * @param l    the l
     * @param t    the t
     * @param oldl the oldl
     * @param oldt the oldt
     */
    void onScrollChanged(int l, int t, int oldl, int oldt);
}
