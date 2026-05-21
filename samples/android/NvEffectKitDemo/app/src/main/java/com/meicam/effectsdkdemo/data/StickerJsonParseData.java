package com.meicam.effectsdkdemo.data;

import java.util.ArrayList;

public class StickerJsonParseData {

    public ArrayList<StickerJsonFileInfo> getInfoList() {
        return infoList;
    }

    private ArrayList<StickerJsonFileInfo> infoList;

    public static class StickerJsonFileInfo {
        private int coverImageId = 0;
        private String name;
        private String defaultCoverName;
        private String resourceDir;
        private String stringFilePath;
        private String effectId;

        public String getEffectId() {
            return effectId;
        }

        public void setEffectId(String effectId) {
            this.effectId = effectId;
        }

        public int getCoverImageId() {
            return coverImageId;
        }

        public void setCoverImageId(int coverImageId) {
            this.coverImageId = coverImageId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefaultCoverName() {
            return defaultCoverName;
        }

        public void setDefaultCoverName(String defaultCoverName) {
            this.defaultCoverName = defaultCoverName;
        }

        public String getResourceDir() {
            return resourceDir;
        }

        public void setResourceDir(String resourceDir) {
            this.resourceDir = resourceDir;
        }

        public String getStringFilePath() {
            return stringFilePath;
        }

        public void setStringFilePath(String stringFilePath) {
            this.stringFilePath = stringFilePath;
        }
    }
}
