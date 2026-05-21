package com.meishe.engine.interf;


/**
 * The interface Base info.
 * 信息基类
 */
public interface IBaseInfo {
    /**
     * 获取资源id
     * Gets the info id
     */
    String getId();

    /**
     * 设置资源id
     * Set the info id
     */
    void setId(String id);

    /**
     * 获取资源名称
     * Gets the info name
     */
    String getName();

    /**
     * 设置资源名称
     * Set the info name
     */
    void setName(String name);

    /**
     * 获取资源类型
     * Gets the info type
     */
    int getType();

    /**
     * 设置资源类型
     * Set the info type
     */
    void setType(int type);

    /**
     * 获取资源封面路径，包含本地和网络
     * Gets the info cover path
     */
    String getCoverPath();

    /**
     * 设置资源封面路径，包含本地和网络
     * Set the info cover path
     */
    void setCoverPath(String coverPath);

    /**
     * 获取资源封面id,仅包含本地
     * Gets the info cover id
     */
    int getCoverId();

    /**
     * 设置资源封面id,仅包含本地
     * Set the info cover id
     */
    void setCoverId(int resId);

    /**
     * 获取资源安装包包/特效包等id
     * Gets the info package id
     */
    String getPackageId();

    /**
     * 设置资源安装包/特效包等id
     * Set the info package id
     */
    void setPackageId(String packageId);

    /**
     * 获取资源路径
     * Gets the info asset path
     */
    String getAssetPath();

    /**
     * 设置资源路径
     * Set the info asset path
     */
    void setAssetPath(String assetPath);

    /**
     * 获取特效id
     * Gets the info effect id
     */
    String getEffectId();

    /**
     * 获取特效id
     * Set the info effect id
     */
    void setEffectId(String effectId);

    /**
     * 设置特效模式
     * Set the info effect mode
     */
    void setEffectMode(int effectMode);

    /**
     * 获取特效模式
     * Gets the info effect mode
     */
    int getEffectMode();

    /**
     * 设置特效强度
     * Set the info effect strength
     */
    void setEffectStrength(float strength);

    /**
     * 设置特效强度
     * Gets the info effect strength
     */
    float getEffectStrength();

    /**
     * 设置公用字符串类型信息
     * Set the info common string
     */
    void setCommonInfo(String info);

    /**
     * 获取公用字符串类型信息
     * Gets the info common string
     */
    String getCommonInfo();

    /**
     * 复制
     * Copy a new info
     */
    IBaseInfo copy();

    /**
     * 更新
     * Update info
     */
    void update(IBaseInfo newInfo);

    /**
     * 设置辅助标记信息
     * Set the secondary tag information
     */
    void setTag(Object obj);

    /**
     * 获取辅助标记信息
     * Gets the secondary tag information
     */
    Object getTag();


    /**
     *
     * Whether is authorized.
     * <P></>
     * 是否授权
     *
     * @return true:yes , false: no
     */
    boolean isAuthorized();

}
