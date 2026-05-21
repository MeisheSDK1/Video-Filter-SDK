package com.meishe.sdkdemo.main.bean;

import java.util.List;

public class AdBeansFormUrl {
    private int code;
    private String enMsg;
    private String msg;
    private List<AdInfo> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AdInfo> getData() {
        return data;
    }

    public void setData(List<AdInfo> data) {
        this.data = data;
    }


    public static class AdInfo {
        private String id;
        private String coverUrl;
        private String coverUrl2;
        private String coverUrl3;
        private String advertisementUrl;
        private String advertisementUrlEn;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getCoverUrl2() {
            return coverUrl2;
        }

        public void setCoverUrl2(String coverUrl) {
            this.coverUrl2 = coverUrl;
        }

        public String getCoverUrl3() {
            return coverUrl3;
        }

        public void setCoverUrl3(String coverUrl) {
            this.coverUrl3 = coverUrl;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setAdvertisementUrl(String advertisementUrl) {
            this.advertisementUrl = advertisementUrl;
        }

        public String getAdvertisementUrl() {
            return advertisementUrl;
        }

        public String getAdvertisementUrlEn() {
            return advertisementUrlEn;
        }

        public void setAdvertisementUrlEn(String advertisementUrlEn) {
            this.advertisementUrlEn = advertisementUrlEn;
        }
    }
}

