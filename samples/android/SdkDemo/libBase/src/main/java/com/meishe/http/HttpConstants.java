package com.meishe.http;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/17.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class HttpConstants {
//    http://123.57.47.107:8180/materialcenter/appSdkApi/material/listAll?isTestMaterial=&kind=&pageSize=20&sdkVersion=3.11.0&keyword=&pageNum=1
    /**
     * 可使用BuildConfig.DEBUG配置
     * You can configure this using BuildConfig.DEBUG
     */
    public static final boolean ISDEBUG = false;
    public static final String HOST_DEBUG = "testeditor.meishesdk.com:18080";
    public static final String GET_RESOURCE_LIST_API = "materialcenter/appSdkApi/material/listAll";
    public static final String NV_ASSET_FONT_URL = "materialcenter/listFont";
    /**
     * 素材分类
     * Material type
     */
    public static final String GET_RESOURCE_TYPE_CATEGORY_API = "materialcenter/appSdkApi/listTypeAndCategory";
    public static final String CUT_SAME_TEMPLATE_CATEGORY = getServerBaseUrl() + "/materialcenter/appSdkApi/listTypeAndCategory";
    /**
     * 首页轮播图
     * Home carousel
     */
    public static final String AD_SPANNER_URL = getServerBaseUrl() + "/app/listAdvertisement";

    /**
     * 查询素材和音乐
     */
    public static final String GET_RESOURCE_LIST_MEDIAINFO_API = "materialcenter/appSdkApi/listMediaInfo";

    public static String getServerBaseUrl() {
        return ISDEBUG
                ? "http://123.57.47.107:8180"
//                ? "https://" + HttpConstants.HOST_DEBUG
                : "https://mall.meishesdk.com/api/sdkdemo";
    }

}
