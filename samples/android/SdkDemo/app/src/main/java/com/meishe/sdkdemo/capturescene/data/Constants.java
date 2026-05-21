package com.meishe.sdkdemo.capturescene.data;

/**
 * Created by CaoZhiChao on 2019/1/3 16:03
 */
public class Constants {
    private static final String BASEPATH = "https://mall.meishesdk.com/api/sdkdemo/materialcenter/appSdkApi/material/listAll?command=listMaterial&acceptAspectRatio=0&category=0&page=0&pageSize=10&lang=zh_CN&type=";
    private static final String BASE_PATH = "https://vsapi.meishesdk.com/materialinfo/index.php?command=listMaterial&acceptAspectRatio=0&category=2&page=0&pageSize=10&lang=zh_CN&type=";
    private static final int TYPE_CAPTURE_SCENE = 8;
    public static final String CAPTURE_SCENE_PATH_VIDEO = BASEPATH + TYPE_CAPTURE_SCENE;
    public static final String CAPTURE_SCENE_PATH_IMAGE = BASE_PATH + TYPE_CAPTURE_SCENE;
    public static final String RESOURCE_NEW_PATH = "https://qasset.meishesdk.com";
    public static final String RESOURCE_OLD_PATH = "https://assets.meishesdk.com";



    /*
     * CaptureScene类型标识
     * CaptureScene type identification
     * */
    public static final int CAPTURE_SCENE_LOCAL = 801;
    public static final int CAPTURE_SCENE_ONLINE = 802;

    public static final int CAPTURE_SCENE_TYPE_IMAGE = 1;
    public static final int CAPTURE_SCENE_TYPE_VIDEO = 2;
}
