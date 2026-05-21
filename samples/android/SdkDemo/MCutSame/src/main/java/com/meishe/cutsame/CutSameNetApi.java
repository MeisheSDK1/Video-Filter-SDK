package com.meishe.cutsame;

import com.meishe.base.utils.Utils;
import com.meishe.http.AssetType;
import com.meishe.http.HttpConstants;
import com.meishe.net.NvsServerClient;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.meishe.http.HttpConstants.CUT_SAME_TEMPLATE_CATEGORY;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2020/12/8 13:42
 * @Description :剪同款模块的网络请求接口api
 * Cut the same module network request interface API
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CutSameNetApi {
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
        Map<String, String> params = new HashMap<>(2);
        params.put("pageNum", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("type", assetType.getType());
        params.put("category", assetType.getCategory());
        params.put("kind", assetType.getKind());
//        params.put("ratioFlag", String.valueOf(ratioFlag));
//        params.put("ratio", String.valueOf(ratio));
        params.put("keyword", keyword);
        params.put("sdkVersion", sdkVersion);
        params.put("needInteractive", "true");
        //0查4k以下 1查4k以上
        //0 Check below 4k. 1 Check above 4k
        params.put("isAbove4k", "0");
        params.put("isTestMaterial", getTestMaterial());
        params.put("sorted", String.valueOf(assetType.getSorted()));
        String apiName = HttpConstants.GET_RESOURCE_LIST_API;
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    /**
     * 获取模板分类列表
     * Gets a list of template categories
     *
     * @param tag      Object 请求标识
     * @param callback RequestCallback 请求回调
     */
    public static void getTemplateCategory(Object tag, RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(2);
        params.put("type", "19");
        params.put("lang", Utils.isZh() ? "zh_CN" : "en");
        NvsServerClient.get().requestGet(tag, CUT_SAME_TEMPLATE_CATEGORY, "", params, callback);
    }

    /**
     * 获取模板对应分类的列表
     * Gets a list of the corresponding categories for the template
     *
     * @param tag        Object 请求标识
     * @param page       int 请求页数
     * @param pageSize   int 页数的大小
     * @param categoryId String 模板分类id
     * @param callback   RequestCallback 请求回调
     */
    public static void getTemplateList(Object tag, int page, int pageSize, String categoryId, RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(6);
        params.put("type", "19");
        params.put("category", categoryId);
        params.put("keyword", "");
        params.put("pageNum", String.valueOf(page));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("lang", Utils.isZh() ? "zh_CN" : "en");
        String apiName = "api/my/template/listTemplatesFinal";
        NvsServerClient.get().requestGet(tag, apiName, params, callback);
    }

    /**
     * 通知服务器被使用的模板
     * Notifies the server of the template being used
     *
     * @param tag        Object 请求标识
     * @param templateId String 模板id
     * @param callback   RequestCallback 请求回调
     */
    public static void uploadUsedTemplate(Object tag, String templateId, RequestCallback<?> callback) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("id", templateId);
        params.put("action", 1);
        //String apiName = "api/my/template/templateInteraction";
        String apiName = "materialcenter/myvideo/templateInteraction";
        NvsServerClient.get().requestPost(tag, apiName, params, callback);
    }

    /**
     * 下载实现方法
     * Download implementation method
     *
     * @param tag      下载标识
     * @param url      下载地址
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param listener 下载监听器
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
        try {
            Class<?> clazz = Class.forName("com.meishe.sdkdemo.base.http.HttpManager");
            Method methodId = clazz.getMethod("getTestMaterial");
            return (String) methodId.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
