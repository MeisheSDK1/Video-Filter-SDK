package com.meishe.engine.bean;

/**
 * @author LiFei
 * @version 1.0
 * @title
 * @description 该类主要功能描述
 * @company 美摄
 * @created 2020/11/26 11:24
 * @changeRecord [修改记录] <br/>
 */
public class TransformResponse {
    private String coverUrl;
    private int createdAt;
    private String path;
    private String resolvingPower;
    private String liveUrl;
    private String title;
    private int type;
    private int projectId;
    private int modifyAt;
    private String uuid;
    private int userId;
    private String url;

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResolvingPower() {
        return resolvingPower;
    }

    public void setResolvingPower(String resolvingPower) {
        this.resolvingPower = resolvingPower;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(int modifyAt) {
        this.modifyAt = modifyAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TransformResponse{" +
                "coverUrl='" + coverUrl + '\'' +
                ", createdAt=" + createdAt +
                ", path='" + path + '\'' +
                ", resolvingPower='" + resolvingPower + '\'' +
                ", liveUrl='" + liveUrl + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", projectId=" + projectId +
                ", modifyAt=" + modifyAt +
                ", uuid='" + uuid + '\'' +
                ", userId=" + userId +
                ", url='" + url + '\'' +
                '}';
    }
}
