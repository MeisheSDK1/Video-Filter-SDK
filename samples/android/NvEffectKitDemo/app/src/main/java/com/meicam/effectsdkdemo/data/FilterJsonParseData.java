package com.meicam.effectsdkdemo.data;

import java.util.ArrayList;

public class FilterJsonParseData {

    public ArrayList<FilterJsonFileInfo> getInfoList() {
        return infoList;
    }

    private ArrayList<FilterJsonFileInfo> infoList;
    public static class FilterJsonFileInfo {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFxPackageId() {
            return fxPackageId;
        }

        public void setFxPackageId(String fxPackageId) {
            this.fxPackageId = fxPackageId;
        }

        public String getFxFileName() {
            return fxFileName;
        }

        public void setFxFileName(String fxFileName) {
            this.fxFileName = fxFileName;
        }

        public String getFxLicFileName() {
            return fxLicFileName;
        }

        public void setFxLicFileName(String fxLicFileName) {
            this.fxLicFileName = fxLicFileName;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public int getFitRatio() {
            return fitRatio;
        }

        public void setFitRatio(int fitRatio) {
            this.fitRatio = fitRatio;
        }

        private String name;
        private String fxPackageId;
        private String fxFileName;
        private String fxLicFileName;
        private String imageName;
        private int fitRatio;
    }
}
