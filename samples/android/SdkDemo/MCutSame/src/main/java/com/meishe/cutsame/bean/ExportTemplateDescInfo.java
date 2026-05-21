package com.meishe.cutsame.bean;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2021/1/6 17:46
 * @Description : 生成模板描述文件,主要用于“我的”模板列表页面
 * export template desc file
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateDescInfo {

    private String supportedAspectRatio;

    private String defaultAspectRatio;

    private String name;

    private String uuid;

    private String description;

    private long duration;

    private String cover;

    private int footageCount;

    private String templatePath;

    private String templateVideoPath;

    private long createTime;


    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getFootageCount() {
        return footageCount;
    }

    public void setFootageCount(int footageCount) {
        this.footageCount = footageCount;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplateVideoPath() {
        return templateVideoPath;
    }

    public void setTemplateVideoPath(String templateVideoPath) {
        this.templateVideoPath = templateVideoPath;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDefaultAspectRatio() {
        return defaultAspectRatio;
    }

    public void setDefaultAspectRatio(String defaultAspectRatio) {
        this.defaultAspectRatio = defaultAspectRatio;
    }
}
