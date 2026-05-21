package com.meishe.sdkdemo.base.http;

import android.text.TextUtils;

import com.meishe.http.AssetType;
import com.meishe.http.HttpConstants;
import com.meishe.net.NvsServerClient;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.temp.TempStringCallBack;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/18.
 * @Description :网络操作类
 * @Description :application net manager
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class HttpManager {

    /**
     * 获取素材资源列表
     * Gets a list of effects material resources
     *
     * @param tag       Object 请求标识
     * @param assetType AssetType  资源类型
     * @param ratio     int 资源比例
     * @param pageNum   int 资源页数
     * @param pageSize  int 资源页数大小
     * @param callback  RequestCallback 请求回调
     */
    public static void getMaterialList(Object tag, AssetType assetType, int ratioFlag, int ratio,
                                       String keyword, String sdkVersion, int pageNum, int pageSize,
                                       RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(16);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("type", assetType.getType());
        if (!TextUtils.isEmpty(assetType.getCategory())) {
            params.put("category", assetType.getCategory());
        } else {
            params.put("categories", assetType.getCategories());
        }
        params.put("kind", assetType.getKind());
//        if (ratioFlag >= 0) {
//            params.put("ratioFlag", String.valueOf(ratioFlag));
//            params.put("ratio", String.valueOf(ratio));
//        }
        params.put("keyword", keyword);
        params.put("sdkVersion", sdkVersion);
        params.put("isTestMaterial", getTestMaterial());
        params.put("lang", Util.getLocaleLanguage());
        params.put("sorted", String.valueOf(assetType.getSorted()));
        String apiName = HttpConstants.GET_RESOURCE_LIST_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    /**
     * 获取素材资源列表
     * Gets a list of effects material resources
     *
     * @param tag       Object 请求标识
     * @param assetType AssetType  资源类型
     * @param kind      kind  素材三级分类
     * @param ratio     int 资源比例
     * @param pageNum   int 资源页数
     * @param pageSize  int 资源页数大小
     * @param callback  RequestCallback 请求回调
     */
    public static void getMaterialList(Object tag, AssetType assetType, String kind, int ratioFlag, int ratio,
                                       String keyword, String sdkVersion, int pageNum, int pageSize,
                                       RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(16);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("type", assetType.getType());
        params.put("category", assetType.getCategory());
        params.put("kind", kind);
//        if (ratioFlag >= 0) {
//            params.put("ratioFlag", String.valueOf(ratioFlag));
//            params.put("ratio", String.valueOf(ratio));
//        }
        params.put("keyword", keyword);
        params.put("sdkVersion", sdkVersion);
        params.put("isTestMaterial", getTestMaterial());
        params.put("lang", Util.getLocaleLanguage());
        params.put("sorted", String.valueOf(assetType.getSorted()));
        String apiName = HttpConstants.GET_RESOURCE_LIST_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }


    public static void getMaterialList(Object tag, String type, String category, String kind,
                                       String keyword, String sdkVersion, int pageNum, int pageSize,
                                       RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(8);
        params.put("type", type);
        params.put("category", category);
        params.put("kind", kind);
        params.put("keyword", keyword);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("sdkVersion", sdkVersion);
        params.put("isTestMaterial", getTestMaterial());
        params.put("lang", Util.getLocaleLanguage());
        String apiName = HttpConstants.GET_RESOURCE_LIST_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    public static void getMaterialList(Object tag, String type,
                                       String keyword, String sdkVersion, int pageNum, int pageSize,
                                       RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(4);
        params.put("type", type);
        params.put("keyword", keyword);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("sdkVersion", sdkVersion);
        params.put("isTestMaterial", getTestMaterial());
        params.put("lang", Util.getLocaleLanguage());
        String apiName = HttpConstants.GET_RESOURCE_LIST_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    public static void getFontDataList(Object tag, String sdkVersion, int pageNum, int pageSize,
                                       RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(4);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("sdkVersion", sdkVersion);
        String apiName = HttpConstants.NV_ASSET_FONT_URL;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    /**
     * 获取素材分类
     * Gets a list of material classification
     *
     * @param tag       Object 请求标识
     * @param assetType AssetType  资源类型
     * @param callback  RequestCallback 请求回调
     */
    public static void getMaterialTypeAndCategory(Object tag, AssetType assetType, String sdkVersion,
                                                  RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(4);
        params.put("types", assetType.getType());
        params.put("categories", assetType.getCategory());
        params.put("sdkVersion", sdkVersion);
        String apiName = HttpConstants.GET_RESOURCE_TYPE_CATEGORY_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    /**
     * 获取素材分类
     * Gets a list of material classification
     *
     * @param tag      Object 请求标识
     * @param callback RequestCallback 请求回调
     */
    public static void getMaterialTypeAndCategory(Object tag, String type, String categories, String sdkVersion,
                                                  RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(4);
        params.put("types", type);
        params.put("categories", categories);
        params.put("sdkVersion", sdkVersion);
        String apiName = HttpConstants.GET_RESOURCE_TYPE_CATEGORY_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    public static void getMediaInfoList(Object tag, int type, int pageNum, int pageSize, String lang,
                                        RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(5);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        //1-video   2-music
        params.put("type", String.valueOf(type));
        params.put("lang", lang);
        String apiName = HttpConstants.GET_RESOURCE_LIST_MEDIAINFO_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }


    /**
     * 获取老接口的 get数据
     * Get the get data of the old interface
     *
     * @param url
     * @param callBack
     */
    public static void getOldObjectGet(String url, TempStringCallBack callBack) {
        NvsServerClient.get().requestObjectGet(url, callBack);
    }

    /**
     * 下载实现方法
     * Download implementation method
     *
     * @param tag      下载标识  Download tag
     * @param url      下载地址 Download address
     * @param filePath 文件路径 File path
     * @param fileName 文件名 File name
     * @param listener 下载监听器 Download listener
     */
    public static void download(String tag, String url, String filePath, String fileName, SimpleDownListener listener) {
        NvsServerClient.get().download(tag, url, filePath, fileName, listener);
    }

    /**
     * 取消所有请求
     * Cancel all requests
     */
    public static void cancelAll() {
        NvsServerClient.get().cancelAll();
    }

    /**
     * 取消某个请求
     * Cancel a request
     *
     * @param tag 唯一标识
     */
    public static void cancelRequest(Object tag) {
        NvsServerClient.get().cancelRequest(tag);
    }

    /**
     * 是否需要返回测试素材，只在测试环境有效
     * 0-测试素材  1-非测试素材  不传-全部素材
     * Whether to return the test material, valid only in the test environment
     * 0-Test material  1-No test material  - All material
     *
     * @return String
     */
    public static String getTestMaterial() {
        if (!HttpConstants.ISDEBUG) {
            return "";
        }
        boolean isTestMaterial = ParameterSettingValues.getParameterValues().isTestMaterial();
        return isTestMaterial ? "0" : "1";
    }


}
