package com.meishe.libmsbeauty.bean;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 17:01
 * @Description :UI资源接口
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface IResource {
    /**
     * 获取资源名称
     * Set Name
     *
     * @return String the resource name
     */
    String getName();

    /**
     * 设置资源名称
     * Set Name
     *
     * @param name the source name
     */
    IResource setName(String name);

    /**
     * 设置图片资源id
     * Set icon resource id
     *
     * @return int the icon resource id
     */
    int getResourceId();

    /**
     * 获取图片资源id
     * Set icon resource id
     *
     * @param resourceId the icon id
     */
    IResource setResourceId(int resourceId);

    /**
     * 获取图片资源url
     * Get icon resource url
     *
     * @return String the icon resource url
     */
    String getResourceUrl();

    /**
     * 设置图片资源url
     * Set icon resource url
     *
     * @param resourceUrl the icon url
     */
    IResource setResourceUrl(String resourceUrl);
}
