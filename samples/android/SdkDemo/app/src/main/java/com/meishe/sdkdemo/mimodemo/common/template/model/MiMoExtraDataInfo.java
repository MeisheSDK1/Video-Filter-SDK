package com.meishe.sdkdemo.mimodemo.common.template.model;

/**
 * 记录模板额外数据信息
 * Example Record additional data information about the template
 */
public class MiMoExtraDataInfo {
    public String getPreviewVideoPath() {
        return previewVideoPath;
    }

    public void setPreviewVideoPath(String previewVideoPath) {
        this.previewVideoPath = previewVideoPath;
    }

    public String getCoverFilePath() {
        return coverFilePath;
    }

    public void setCoverFilePath(String coverFilePath) {
        this.coverFilePath = coverFilePath;
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }

    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    /**
     * 模板预览视频路径
     * Template preview video path
     */
    private String previewVideoPath;
    /**
     * 封面文件路径
     * Cover file path
     */
    private String coverFilePath;
    /**
     * 音乐文件路径
     * Music file path
     */
    private String musicFilePath;
    /**
     * 模板目录路径
     * Template directory path
     */
    private String templateDirectory;
}
