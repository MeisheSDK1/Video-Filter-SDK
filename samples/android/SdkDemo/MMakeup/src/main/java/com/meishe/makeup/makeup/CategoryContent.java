package com.meishe.makeup.makeup;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/25 11:22
 * @Description :美妆分类的内容 The content about makeup category
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CategoryContent {
    /**
     * 所属美妆分类的id
     * The makeup category id
     */
    private int id;
    /**
     * 所属美妆分类的类型
     * The makeup category type
     */
    private String type;
    /**
     * 是否已经从网络更新过
     * Whether it has been updated from the network
     */
    private boolean updatedFromNet;

    /**
     * 远程美妆列表，包含网络、本地下载的美妆
     * the other makeup list,Contains net and local download of beauty makeup
     */
    private List<Makeup> remoteMakeupList;

    /**
     * 本地美妆列表，包含asset目录和sdcard测试目录的美妆
     * the local makeup list,Contains assets directory and sdcard test directory beauty makeup
     */
    private List<Makeup> localMakeupList;

    /**
     * 选中的美妆索引
     * The selected position from makeup list
     */
    private int selectedPosition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUpdatedFromNet() {
        return updatedFromNet;
    }

    public void setUpdatedFromNet(boolean updatedFromNet) {
        this.updatedFromNet = updatedFromNet;
    }

    public List<Makeup> getRemoteMakeupList() {
        return remoteMakeupList;
    }

    public void setRemoteMakeupList(List<Makeup> remoteMakeupList) {
        this.remoteMakeupList = remoteMakeupList;
    }

    public List<Makeup> getLocalMakeupList() {
        return localMakeupList;
    }

    public void setLocalMakeupList(List<Makeup> localMakeupList) {
        this.localMakeupList = localMakeupList;
    }

    public List<Makeup> getAllMakeupList() {
        if (localMakeupList != null) {
            List<Makeup> allMakeupList = new ArrayList<>(localMakeupList);
            if (remoteMakeupList != null) {
                allMakeupList.addAll(remoteMakeupList);
            }
            return allMakeupList;
        }
        return null;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
