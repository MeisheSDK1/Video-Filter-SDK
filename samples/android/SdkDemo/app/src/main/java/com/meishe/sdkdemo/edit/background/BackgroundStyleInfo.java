package com.meishe.sdkdemo.edit.background;

/**
 * @author lpf
 */
public class BackgroundStyleInfo {

    private String mFilePath;
    private int mIconRcsId;
    /**
     * 是否是内置素材
     * Is it built-in material
     */
    private boolean isAssets = true;

    public int getIconRcsId() {
        return mIconRcsId;
    }

    public void setIconRcsId(int iconRcsId) {
        this.mIconRcsId = iconRcsId;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public boolean isAssets() {
        return isAssets;
    }

    public void setAssets(boolean assets) {
        isAssets = assets;
    }
}
