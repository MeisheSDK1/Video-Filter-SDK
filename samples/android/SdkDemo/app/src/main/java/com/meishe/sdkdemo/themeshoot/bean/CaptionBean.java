package com.meishe.sdkdemo.themeshoot.bean;

import java.io.Serializable;
/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/5.
 * @Description :字幕bean。CaptionBean
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionBean implements Serializable {
    /**
     * 显示文字
     * Display text
     */
    String text;
    /**
     * 位置标识
     * Location identification
     */
    int countIndex;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(int countIndex) {
        this.countIndex = countIndex;
    }
}
