package com.meishe.engine.local;

/**
 * author：yangtailin on 2020/8/24 14:29
 */
public class LPathInfo {
    private String type;
    private String path;
    private boolean needTranscode = false;

    public LPathInfo(String type, String path, boolean needTranscode) {
        this.type = type;
        this.path = path;
        this.needTranscode = needTranscode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isNeedTranscode() {
        return needTranscode;
    }

    public void setNeedTranscode(boolean needTranscode) {
        this.needTranscode = needTranscode;
    }
}
