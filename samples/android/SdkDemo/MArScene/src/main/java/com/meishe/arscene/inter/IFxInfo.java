package com.meishe.arscene.inter;

import java.util.List;

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

    /**
     * 特效的子特效，如美肤-磨皮，包含磨皮1,磨皮2...
     * Special effects sub-effects, such as Beauty - Dermabrasion, including dermabrasion 1, Dermabrasion 2...
     *
     * @param fxNodes nodes
     * @return nodes
     */
    IFxInfo setFxNodes(List<IFxInfo> fxNodes);

    /**
     * 特效的子特效
     * Subeffects of special effects
     *
     * @return nodes
     */
    List<IFxInfo> getFxNodes();

    /**
     * node状态
     *
     * @param state state false:关闭 true:打开
     * @return IFxInfo
     */
    IFxInfo setIsExpanded(boolean state);

    /**
     * 获取node状态
     * node status
     *
     * @return state
     */
    boolean isExpanded();

    /**
     * 是否可替换修改
     * Whether the modification can be replaced
     *
     * @return boolean
     */
    boolean canReplace();

    /**
     * 设置是否可替换修改
     * Sets whether the changes can be replaced
     *
     * @param canReplace canReplace
     * @return IFxInfo
     */
    IFxInfo setCanReplace(boolean canReplace);

    IFxInfo setSelected(boolean isSelected);

    boolean isSelected();

    IFxInfo setDefaultFx(boolean isDefaultFx);

    boolean isDefaultFx();

}
