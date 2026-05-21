package com.meishe.cutsame.bean;

import java.io.Serializable;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2020/12/24 14:34
 * @Description : 导出模板-片段编辑
 * export template exit clip
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateClip implements Serializable {

    public static final String TYPE_FOOTAGE_IMAGE = "image";

    public static final String TYPE_FOOTAGE_VIDEO = "video";

    public static final String TYPE_FOOTAGE_IMAGE_AND_VIDEO = "videoImage";


    /**
     * 图片路径
     * image path
     */
    private String imagePath;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 是否锁定
     * is or not locked
     */
    private boolean isLock;

    /**
     * 片段名称
     * clip name
     */
    private String clipName;

    /**
     * 片段时长
     * clip duration
     */
    private String clipDuration;

    /**
     * 镜头类型："image":图片  "video":视频  "videoImage":图片或者视频（无限制） "audio":音频
     * footage type "image":picture "video":video  "videoImage":video or picture "audio":music
     */
    private String footageType = "videoImage";

    /**
     * 镜头群组id
     * footage groups id
     */
    private int footageGroupsId;

    /**
     * 镜头id
     * footage id
     */
    private int footageId;

    /**
     * 片段入点
     * clip inPoint
     */
    private long inPoint;

    /**
     * 片段出点
     * clip outPoint
     */
    private long outPoint;

    private long trimIn;

    private long trimOut;

    private int trackIndex;

    private boolean isSelectFootageGroups;

    private String coverPath;
    /**
     * 获取封面图 taskID
     * Get cover image taskID
     */
    private long taskId;

    private boolean isVideoReverse;
    private String reversePath;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getClipName() {
        return clipName;
    }

    public void setClipName(String clipName) {
        this.clipName = clipName;
    }

    public String getClipDuration() {
        return clipDuration;
    }

    public void setClipDuration(String clipDuration) {
        this.clipDuration = clipDuration;
    }

    public String getFootageType() {
        return footageType;
    }

    public void setFootageType(String footageType) {
        this.footageType = footageType;
    }

    public int getFootageGroupsId() {
        return footageGroupsId;
    }

    public void setFootageGroupsId(int footageGroupsId) {
        this.footageGroupsId = footageGroupsId;
    }


    public boolean isSelectFootageGroups() {
        return isSelectFootageGroups;
    }

    public void setSelectFootageGroups(boolean selectFootageGroups) {
        isSelectFootageGroups = selectFootageGroups;
    }

    public int getFootageId() {
        return footageId;
    }

    public void setFootageId(int footageId) {
        this.footageId = footageId;
    }

    public long getInPoint() {
        return inPoint;
    }

    public void setInPoint(long inPoint) {
        this.inPoint = inPoint;
    }

    public long getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.outPoint = outPoint;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }

    public long getTrimOut() {
        return trimOut;
    }

    public void setTrimOut(long trimOut) {
        this.trimOut = trimOut;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackInde) {
        this.trackIndex = trackInde;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public boolean isVideoReverse() {
        return isVideoReverse;
    }

    public void setVideoReverse(boolean videoReverse) {
        isVideoReverse = videoReverse;
    }

    public String getReversePath() {
        return reversePath;
    }

    public void setReversePath(String reversePath) {
        this.reversePath = reversePath;
    }
}
