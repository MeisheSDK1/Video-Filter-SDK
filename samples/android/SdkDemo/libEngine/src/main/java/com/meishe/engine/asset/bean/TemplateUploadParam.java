package com.meishe.engine.asset.bean;

import java.io.File;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/3/25 17:03
 * @Description :上传模板用参数 Param for template uploading.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TemplateUploadParam {
    /**
     * 素材文件
     * Material file
     */
    public File materialFile;
    /**
     * 封面
     * cover
     */
    public File coverFile;
    /**
     * 预览视频文件
     * Preview video file
     */
    public File previewVideoFile;
    /**
     * 自定义名称
     * Custom name
     */
    public String customDisplayName;
    /**
     * 素材type
     * Material type
     */
    public int materialType = AssetsConstants.AssetsTypeData.TEMPLATE.type;

    /**
     * 素材描述
     * Material description
     */
    public String description;
    /**
     * 素材中文描述
     * Chinese description of material
     */
    public String descriptinZhCn;

    /**
     * 是否通用 0：非通用 1：通用
     * Common or not 0: non-common 1: common
     */
    public int ratioFlag;

    /**
     * 智能标签Id 以空格分隔 拼接而成的字符串
     * Smart label Id Is a string separated by Spaces
     */
    public int intelTags;

    @Override
    public String toString() {
        return "TemplateUploadParam{" +
                "materialFile='" + materialFile + '\'' +
                ", coverFile='" + coverFile + '\'' +
                ", previewVideoFile='" + previewVideoFile + '\'' +
                ", customDisplayName='" + customDisplayName + '\'' +
                ", materialType=" + materialType +
                ", description='" + description + '\'' +
                ", descriptinZhCn='" + descriptinZhCn + '\'' +
                ", ratioFlag=" + ratioFlag +
                ", intelTags=" + intelTags +
                '}';
    }
}
