package com.meishe.base.view.bean;

/**
 * The type Compile param data.
 *
 * @author :Jml
 * @date :2020/9/22 17:05
 * @des :
 * 编译参数数据类
 * Compile the parameter data class
 */
public class CompileParamData {
    private String showData;
    private boolean recommend;

    /**
     * Sets show data.
     * 设置显示数据
     * @param showData the show data 显示的数据
     */
    public void setShowData(String showData) {
        this.showData = showData;
    }

    /**
     * Gets show data.
     * 获得显示的数据
     * @return the show data显示的数据
     */
    public String getShowData() {
        return showData;
    }

    /**
     * Sets recommend.
     * 设置推荐
     * @param recommend the recommend 推荐
     */
    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public boolean isRecommend() {
        return recommend;
    }
}
