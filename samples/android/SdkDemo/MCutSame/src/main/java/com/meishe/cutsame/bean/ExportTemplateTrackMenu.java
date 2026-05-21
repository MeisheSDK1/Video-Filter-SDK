package com.meishe.cutsame.bean;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiuPanFeng
 * @CreateDate: 2020/12/24 14:34
 * @Description: 导出模板-轨道菜单
 *               export template track menu
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateTrackMenu {

    /**
     * 轨道名称
     * clip name
     */
    private String trackName;

    /**
     * 轨道index
     * 主轨：0，画中画轨道1......画中画轨道n
     */
    private int trackIndex;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }
}
