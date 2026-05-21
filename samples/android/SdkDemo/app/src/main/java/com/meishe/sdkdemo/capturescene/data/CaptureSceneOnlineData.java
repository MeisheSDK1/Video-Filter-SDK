package com.meishe.sdkdemo.capturescene.data;

import java.util.List;

import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_LOCAL;

import android.text.TextUtils;

/**
 * Created by CaoZhiChao on 2019/1/3 15:21
 */
public class CaptureSceneOnlineData {
    private int code;
    private String enMsg;
    private String msg;
    private CaptureSceneData data;

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

    public CaptureSceneData getData() {
        return data;
    }

    public void setData(CaptureSceneData data) {
        this.data = data;
    }

    public static class CaptureSceneData {
        private int total;
        private int pageNum;
        private int pageSize;
        private List<CaptureSceneDetails> elements;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<CaptureSceneDetails> getElements() {
            return elements;
        }

        public void setElements(List<CaptureSceneDetails> elements) {
            this.elements = elements;
        }
    }

    public static class CaptureSceneDetails {
        private String id;
        private int category;
        private String name;
        private String desc;
        private String tags;
        private int version;
        private String minAppVersion;
        private String packageUrl;
        private int packageSize;
        private String coverUrl;
        private int supportedAspectRatio;
        private String zipUrl;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private int type = CAPTURE_SCENE_LOCAL;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getMinAppVersion() {
            return minAppVersion;
        }

        public void setMinAppVersion(String minAppVersion) {
            this.minAppVersion = minAppVersion;
        }

        public String getPackageUrl() {
            return zipUrl;
        }

        public void setPackageUrl(String packageUrl) {
            this.zipUrl = packageUrl;
        }

        public int getPackageSize() {
            return packageSize;
        }

        public void setPackageSize(int packageSize) {
            this.packageSize = packageSize;
        }

        public String getCoverUrl() {
            if(TextUtils.isEmpty(coverUrl)){
                return "";
            }
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public int getSupportedAspectRatio() {
            return supportedAspectRatio;
        }

        public void setSupportedAspectRatio(int supportedAspectRatio) {
            this.supportedAspectRatio = supportedAspectRatio;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CaptureSceneDetails) {
                CaptureSceneDetails captureSceneDetails = (CaptureSceneDetails) obj;
                return this.id.equals(captureSceneDetails.getId());
            }
            return super.equals(obj);
        }
    }
}
