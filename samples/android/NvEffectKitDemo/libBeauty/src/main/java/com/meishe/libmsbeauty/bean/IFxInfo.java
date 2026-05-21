package com.meishe.libmsbeauty.bean;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 16:52
 * @Description : 特效数据接口 ,注意只有共性数据 fx data interface
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface IFxInfo extends IResource {
    /**
     * 获取类型
     * Get type
     *
     * @return String the type
     */
    String getType();

    /**
     * 设置类型
     * Set type
     *
     * @param type the type
     */
    IFxInfo setType(String type);

    /**
     * 获取特效名称
     * Get Name
     *
     * @return String the fx name
     */
    String getFxName();

    /**
     * 设置特效名称
     * Set fx Name
     *
     * @param fxName the fx name
     */
    IFxInfo setFxName(String fxName);

    /**
     * 获取特效强度
     * Get fx strength
     *
     * @return double the fx strength
     */
    double getStrength();

    /**
     * 设置特效强度
     * Set fx strength
     *
     * @param strength the fx strength
     */
    IFxInfo setStrength(double strength);

    /**
     * 获取特效的默认强度
     * Get fx default strength
     *
     * @return double the fx strength
     */
    double getDefaultStrength();

    /**
     * 设置特效的默认强度
     * Set fx default strength
     *
     * @param strength the fx strength
     */
    IFxInfo setDefaultStrength(double strength);

    /**
     * 获取特效安装包的id
     * Get fx package id
     *
     * @return String the fx package id
     */
    String getPackageId();

    /**
     * 设置特效安装包路径
     * Set fx package id
     *
     * @param packageId the fx package id
     */
    IFxInfo setPackageId(String packageId);

    /**
     * 获取特效安装包路径
     * Get fx package path
     *
     * @return String the fx package path
     */
    String getAssetPackagePath();

    /**
     * 设置特效安装包路径
     * Set fx package path
     *
     * @param packagePath the fx package path
     */
    IFxInfo setAssetPackagePath(String packagePath);

    /**
     * 复制
     * Copy
     *
     * @param fxInfo the data resource
     */
    IFxInfo copy(IFxInfo fxInfo);
}
