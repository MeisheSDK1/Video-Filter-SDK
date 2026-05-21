package com.meishe.engine.asset.bean;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author : chu chen guang on 2020/6/17 13:38
 * 网络资源包的信息列表
 * A list of information for network resource bundles
 */

public class AssetList {
    public int type;
    public int total;
    public int pageNum;
    public int pageSize;
    public boolean hasNext;
    public ArrayList<NvAssetInfo> list;
    /**
     * 真实资源列表（网络数据源的转化）
     * List of real resources,Network data source transformation
     */
    public List<AssetInfo> realAssetList;
    public List<NvAssetInfo> elements;

    public class NvAssetInfo {
        public String id;
        public int category;
        public int kind;
        public String name;
        public String desc;
        public String tags;
        public int version;
        public int type;
        public String minAppVersion;
        public String packageUrl;
        public int packageSize;
        public String coverUrl;
        public int supportedAspectRatio;
        public String previewVideoUrl;
        public String packageRelativePath;
        public String infoUrl;
        public long templateTotalDuration;
        public String description;
        public String descriptionZhCn;

        /**
         * display Name if asset is custom
         * <P></>
         * 自定义名称名称
         */
        public String customDisplayName;
        /**
         * English name
         * <P></>
         * 英文名称
         */
        public String displayName;
        /**
         * Chinese name
         * <p></>
         * 中文名称
         */
        public String displayNameZhCn;

        /**
         * Indicates Whether the asset is authorized.
         * <p></>
         * 是否已经授权
         */
        public boolean authed;
        /**
         * ratio flag 0: uncommon 1: common
         */
        public int ratioFlag;

        @Override
        public String toString() {
            return "NvAssetInfo{" +
                    "id='" + id + '\'' +
                    ", category=" + category +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", tags='" + tags + '\'' +
                    ", version=" + version +
                    ", minAppVersion='" + minAppVersion + '\'' +
                    ", packageUrl='" + packageUrl + '\'' +
                    ", packageSize=" + packageSize +
                    ", coverUrl='" + coverUrl + '\'' +
                    ", supportedAspectRatio=" + supportedAspectRatio +
                    ", customDisplayName='" + customDisplayName + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", displayNameZhCn='" + displayNameZhCn + '\'' +
                    ", authed='" + authed + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AssetList{" +
                "type=" + type +
                ", total=" + total +
                ", hasNext=" + hasNext +
                ", list=" + list +
                ", realAssetList=" + realAssetList +
                ", elements=" + elements +
                '}';
    }
}
