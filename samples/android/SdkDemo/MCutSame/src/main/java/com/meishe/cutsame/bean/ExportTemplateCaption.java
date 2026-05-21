package com.meishe.cutsame.bean;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiuPanFeng
 * @CreateDate: 2020/12/24 14:34
 * @Description: 导出模板-字幕编辑
 *               export template exit caption
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateCaption {

    /**
     * 图片路径
     * image path
     */
    private String imagePath;

    /**
     * 是否锁定
     * is or not locked
     */
    private boolean isLock;

    /**
     * 字幕名称
     * clip name
     */
    private String captionName;

    /**
     * 字幕时长
     * clip duration
     */
    private String captionDuration;

    /**
     * 字幕内容
     * caption content
     */
    private String captionContent;

    /**
     * 字幕选中
     * caption select
     */
    private boolean isCaptionSelect;


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getCaptionName() {
        return captionName;
    }

    public void setCaptionName(String captionName) {
        this.captionName = captionName;
    }

    public String getCaptionDuration() {
        return captionDuration;
    }

    public void setCaptionDuration(String captionDuration) {
        this.captionDuration = captionDuration;
    }

    public String getCaptionContent() {
        return captionContent;
    }

    public void setCaptionContent(String captionContent) {
        this.captionContent = captionContent;
    }

    public boolean isCaptionSelect() {
        return isCaptionSelect;
    }

    public void setCaptionSelect(boolean captionSelect) {
        isCaptionSelect = captionSelect;
    }


}
