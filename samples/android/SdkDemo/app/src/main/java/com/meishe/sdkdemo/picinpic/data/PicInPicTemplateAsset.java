package com.meishe.sdkdemo.picinpic.data;

import java.util.ArrayList;

/**
 * Created by admin on 2018/10/12.
 */

public class PicInPicTemplateAsset {
    private String mTemplateName;
    private String mTemplateCover;
    private ArrayList<String> mTempatePackageID;
    private boolean isBundle = false;

    public boolean isBundle() {
        return isBundle;
    }

    public void setBundle(boolean bundle) {
        isBundle = bundle;
    }

    public String getTemplateName() {
        return mTemplateName;
    }

    public void setTemplateName(String templateName) {
        this.mTemplateName = templateName;
    }

    public ArrayList<String> getTempatePackageID() {
        return mTempatePackageID;
    }

    public void setTempatePackageID(ArrayList<String> tempatePackageID) {
        mTempatePackageID = tempatePackageID;
    }

    public String getTemplateCover() {
        return mTemplateCover;
    }

    public void setTemplateCover(String templateCover) {
        mTemplateCover = templateCover;
    }


    public PicInPicTemplateAsset() {
    }

    public static class PicInPicJsonFileInfo {
        public ArrayList<PicInPicJsonFileInfoList> getPipInfoList() {
            return pipInfoList;
        }

        private ArrayList<PicInPicJsonFileInfoList> pipInfoList;
    }

    public static class PicInPicJsonFileInfoList {
        public String getName() {
            return name;
        }

        private String name;

        public String getName_Zh() {
            return name_Zh;
        }

        private String name_Zh;

        public String getFileDirName() {
            return fileDirName;
        }

        private String fileDirName;

        public String getPipPackageName1() {
            return pipPackageName1;
        }

        private String pipPackageName1;

        public String getPipPackageName2() {
            return pipPackageName2;
        }

        private String pipPackageName2;

        public String getPipPackageName3() {
            return pipPackageName3;
        }

        public void setPipPackageName3(String pipPackageName3) {
            this.pipPackageName3 = pipPackageName3;
        }

        public String getPipPackageName4() {
            return pipPackageName4;
        }

        public void setPipPackageName4(String pipPackageName4) {
            this.pipPackageName4 = pipPackageName4;
        }

        public String getPipPackageName5() {
            return pipPackageName5;
        }

        public void setPipPackageName5(String pipPackageName5) {
            this.pipPackageName5 = pipPackageName5;
        }

        public String getPipPackageName6() {
            return pipPackageName6;
        }

        public void setPipPackageName6(String pipPackageName6) {
            this.pipPackageName6 = pipPackageName6;
        }

        public String getPipPackageName7() {
            return pipPackageName7;
        }

        public void setPipPackageName7(String pipPackageName7) {
            this.pipPackageName7 = pipPackageName7;
        }

        public String getPipPackageName8() {
            return pipPackageName8;
        }

        public void setPipPackageName8(String pipPackageName8) {
            this.pipPackageName8 = pipPackageName8;
        }

        public String getPipPackageName9() {
            return pipPackageName9;
        }

        public void setPipPackageName9(String pipPackageName9) {
            this.pipPackageName9 = pipPackageName9;
        }

        private String pipPackageName3;
        private String pipPackageName4;
        private String pipPackageName5;
        private String pipPackageName6;
        private String pipPackageName7;
        private String pipPackageName8;
        private String pipPackageName9;

        public String getCoverImageName() {
            return coverImageName;
        }

        private String coverImageName;

        public boolean isBundle() {
            return isBundle;
        }

        private boolean isBundle;
    }
}
