package com.meishe.cutsame.bean;

/**
 * Created by CaoZhiChao on 2020/11/4 11:09
 * 裁剪剪辑类
 * Clipping clip class
 */
public class TailorClip {
    private String filePath;
    private long limitLength;
    private long trimIn;
    private long trimOut;

    public TailorClip() {
    }

    public TailorClip(String filePath, long limitLength, long trimIn, long trimOut) {
        this.filePath = filePath;
        this.limitLength = limitLength;
        this.trimIn = trimIn;
        this.trimOut = trimOut;
    }

    /**
     * Gets file path.
     * 文件路径
     * @return the file path 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets file path.
     * 文件路径
     * @param filePath the file path 文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getLimitLength() {
        return limitLength;
    }
    public void setLimitLength(long limitLength) {
        this.limitLength = limitLength;
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
}
