package com.meishe.libmakeup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.libbase.util.FileUtils;
import com.meishe.libbase.util.StringUtils;
import com.meishe.libmakeup.bean.MakeupCategory;
import com.meishe.libmakeup.bean.MakeupListData;
import com.meishe.nveffectkit.entity.Beauty;
import com.meishe.nveffectkit.entity.Makeup;
import com.meishe.nveffectkit.entity.MicroShape;
import com.meishe.nveffectkit.entity.Params;
import com.meishe.nveffectkit.entity.Shape;
import com.meishe.nveffectkit.makeup.NveComposeMakeup;
import com.meishe.nveffectkit.makeup.NveMakeup;
import com.meishe.nveffectkit.makeup.NveMakeupTypeEnum;
import com.meishe.nveffectkit.makeup.NveSingleMakeUp;
import com.meishe.nveffectkit.utils.NveAssetPackageManagerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/25 10:59
 * @Description :美妆数据管理者 Makeup data manager
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupDataManager {
    private static final String TAG = "MakeupDataManager";
    private static final String INFO_JSON = "info.json";
    private static final String INFO_NEW_JSON = "info_new.json";
    private static final String ASSETS_MAKEUP_PATH = "beauty/makeup";
    private static final String ASSETS_VARIABLE_COMPOSE_PATH = "beauty/makeup/newCompose";
    /**
     * single makeup path
     * 单妆路径
     */
    private static final String ASSETS_MAKEUP_CUSTOM_PATH = "beauty/makeup/customcompose";

    /**
     * Local beauty import path
     * 本地美妆导入路径
     */
    private static final String LOCAL_MAKEUP_PATH = "NvStreamingSdk" + File.separator + "Asset" + File.separator + "LocalMakeup";
    private static final String LOCAL_MAKEUP_PATH_COMPOSE = LOCAL_MAKEUP_PATH + File.separator + "Combined";

    public static HashMap<String, List<MakeupListData>> getMakeupCategoryList(Context context, boolean isEdit) {
        String jsonContent = ParseJsonFile.readAssetJsonFile(context, ASSETS_MAKEUP_CUSTOM_PATH + File.separator + INFO_JSON);
        if (TextUtils.isEmpty(jsonContent)) {
            return null;
        }
        HashMap<String, List<MakeupListData>> stringListHashMap = new HashMap<>();
        List<MakeupCategory> categoryInfoList = ParseJsonFile.fromJson(jsonContent, new TypeToken<List<MakeupCategory>>() {
        }.getType());

        String clearCoverPath = ASSETS_MAKEUP_PATH + File.separator + "newCompose" + File.separator + (isEdit ? "icon_edit_custom_none.png" : "icon_compose_none.png");
        //获取单妆数据
        for (MakeupCategory categoryInfo : categoryInfoList) {
            List<MakeupListData> assetsNveComposeMakeupList = getAssetsMakeupList(context,
                    ASSETS_MAKEUP_CUSTOM_PATH + File.separator + categoryInfo.getDisplayName(),
                    true);
            List<MakeupListData> sdcardNveComposeMakeupList = getLocalMakeupList(context,
                    LOCAL_MAKEUP_PATH + File.separator + categoryInfo.getDisplayName(), true);
            assetsNveComposeMakeupList.add(0, getClearMakeup(context, clearCoverPath));
            assetsNveComposeMakeupList.addAll(sdcardNveComposeMakeupList);
            stringListHashMap.put(categoryInfo.getDisplayNameZhCn(), assetsNveComposeMakeupList);
        }

        //获取妆容数据
        List<MakeupListData> assetsNveComposeMakeupList = getAssetsMakeupList(context, ASSETS_VARIABLE_COMPOSE_PATH, false);
        List<MakeupListData> localNveComposeMakeupList = getLocalMakeupList(context, LOCAL_MAKEUP_PATH_COMPOSE, false);
        assetsNveComposeMakeupList.add(0, getClearMakeup(context, clearCoverPath));
        assetsNveComposeMakeupList.addAll(localNveComposeMakeupList);
        stringListHashMap.put(context.getString(R.string.makeup_), assetsNveComposeMakeupList);
        return stringListHashMap;
    }

    private static MakeupListData getClearMakeup(Context context, String coverPath) {
        MakeupListData nveComposeMakeup = new MakeupListData();
        nveComposeMakeup.setName(context.getString(R.string.no));
        nveComposeMakeup.setCover(coverPath);
        return nveComposeMakeup;
    }

    /**
     * 获取assets中的美妆列表
     * Get the makeup list in assets
     *
     * @param context    the context
     * @param assetsPath the assets path
     **/
    public static List<MakeupListData> getAssetsMakeupList(Context context, String assetsPath, boolean singleMakeup) {
        List<MakeupListData> assetsList = new ArrayList<>();
        if (context == null || TextUtils.isEmpty(assetsPath)) {
            return assetsList;
        }
        try {
            String[] list = context.getAssets().list(assetsPath);
            if (list != null) {
                for (String assetFilePath : list) {
                    String[] items = context.getAssets().list(assetsPath + File.separator + assetFilePath);
                    Log.e(TAG, "getAssetsMakeupList items: " + items.length);
                    if (items.length > 0) {
                        if (singleMakeup) {
                            getSingleMakeUp(context, assetsList, assetsPath + File.separator + assetFilePath, items);
                        } else {
                            getComposeMakeUp(context, assetsList, assetsPath + File.separator + assetFilePath, items);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return assetsList;
        }
        return assetsList;
    }

    private static void getComposeMakeUp(Context context, List<MakeupListData> makeupListDataList, String assetsPath, String[] items) {
        MakeupListData makeupListData = new MakeupListData();
        NveMakeup NveMakeup = new NveMakeup();
        makeupListData.setNvMakeup(NveMakeup);
        NveComposeMakeup composeMakeup = getMakeup(context, assetsPath, true);
        if (composeMakeup != null) {
            NveMakeup.setComposeMakeup(composeMakeup);
            makeupListDataList.add(makeupListData);
            //must put a png image with the same name at the same level as the package name
            //固定在和包名同级别下放入同名的png图片
            makeupListData.setCover(assetsPath + File.separator + composeMakeup.getUuid() + ".png");
            composeMakeup.setCover(assetsPath + File.separator + composeMakeup.getUuid() + ".png");
            makeupListData.setName(composeMakeup.getTranslation().get(0).getTargetText());
            makeupListData.setType(NveMakeupTypeEnum.ComposeMakeup);
        }
        if (items != null && items.length > 0) {
            for (String fxItem : items) {
                int packageType = getPackageType(FileUtils.getFileExtension(fxItem));
                if (packageType >= 0) {
                    installFxPackage("assets:/" + assetsPath + File.separator + fxItem, "", packageType);
                }
            }
        }
    }

    private static void getSingleMakeUp(Context context, List<MakeupListData> makeupListDataList, String assetsPath, String[] items) {
        MakeupListData makeupListData = new MakeupListData();
        NveMakeup nvemakeup = new NveMakeup();
        makeupListData.setNvMakeup(nvemakeup);
        NveSingleMakeUp singleMakeupBean;
        String jsonPath = "";
        String coverPath = "";
        String makeupPath = "";
        HashMap<NveMakeupTypeEnum, NveSingleMakeUp> singleMakeUpHashMap = new HashMap<>();
        for (String item : items) {
            if (TextUtils.isEmpty(item)) {
                continue;
            }
            String filePath = assetsPath + File.separator + item;
            if (item.endsWith(".json")) {
                jsonPath = filePath;
            } else if (item.endsWith(".png") || item.endsWith(".jpg")) {
                coverPath = filePath;
            } else if (item.endsWith("makeup")) {
                makeupPath = filePath;
            }
        }
        if (!TextUtils.isEmpty(jsonPath) && !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(makeupPath)) {
            String readInfo = ParseJsonFile.readAssetJsonFile(context, jsonPath);
            singleMakeupBean = ParseJsonFile.fromJson(readInfo, NveSingleMakeUp.class);
            if (!TextUtils.isEmpty(readInfo)) {
                if (StringUtils.isZh(context)) {
                    makeupListData.setName(singleMakeupBean.getTranslation().get(0).getTargetText());
                } else {
                    makeupListData.setName(singleMakeupBean.getTranslation().get(0).getOriginalText());
                }
                singleMakeUpHashMap.put(NveMakeupTypeEnum.valueOf(singleMakeupBean.getMakeupId()), singleMakeupBean);
                nvemakeup.setSingleMakeUpHashMap(singleMakeUpHashMap);
            }
            makeupListData.setCover(coverPath);
            makeupListData.setType(NveMakeupTypeEnum.valueOf(singleMakeupBean.getMakeupId()));

            String packageId = installFxPackage("assets:/" + makeupPath, "", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP);
            singleMakeupBean.setPackageID(packageId);
        }
        makeupListDataList.add(makeupListData);
    }

    /**
     * 获取本地sdcard中的美妆列表
     * Get the makeup list in sdcard
     *
     * @param context   the context
     * @param localPath the local makeup path
     **/
    public static List<MakeupListData> getLocalMakeupList(Context context, String localPath, boolean singleMakeup) {
        List<MakeupListData> assetsList = new ArrayList<>();
        if (context == null || TextUtils.isEmpty(localPath)) {
            return assetsList;
        }
        try {
            File file = new File(localPath);
            if (file.exists()) {
                String[] list = file.list();
                if (list != null) {
                    if (singleMakeup) {
                        getSingleMakeUp(context, assetsList, file.getCanonicalPath(), list);
                    } else {
                        getComposeMakeUp(context, assetsList, file.getCanonicalPath(), list);
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return assetsList;
    }

    /**
     * 获取美妆数据
     * Get the makeup data
     *
     * @param context the context
     * @param fileDir the file dir path
     **/
    public static NveComposeMakeup getMakeup(Context context, String fileDir, boolean isAsset) {
        String jsonPath = fileDir + File.separator + INFO_NEW_JSON;
        String readInfo = !isAsset ? ParseJsonFile.readSdCardJsonFile(jsonPath) : ParseJsonFile.readAssetJsonFile(context, jsonPath);
        if (!TextUtils.isEmpty(readInfo)) {
            NveComposeMakeup composeMakeup = ParseJsonFile.fromJson(readInfo, NveComposeMakeup.class);
            setComposeMakeUpdataPathValue(composeMakeup, fileDir, isAsset);
            if (StringUtils.isZh(context)) {
                composeMakeup.setName(composeMakeup.getTranslation().get(0).getTargetText());
            } else {
                composeMakeup.setName(composeMakeup.getTranslation().get(0).getOriginalText());
            }
            if (isAsset) {
                composeMakeup.setAssetsDirectory("assets:/" + fileDir);
            } else {
                composeMakeup.setAssetsDirectory(fileDir);
            }
            if (TextUtils.isEmpty(composeMakeup.getUuid())) {
                String[] split = fileDir.split("/");
                composeMakeup.setUuid(split[split.length - 1]);
            }
            return composeMakeup;
        }
        return null;
    }

    private static void setComposeMakeUpdataPathValue(NveComposeMakeup composeMakeup, String path, boolean isAsset) {
        if (isAsset) {
            path = "assets:/" + path;
        }
        List<Makeup> makeups = composeMakeup.getEffectContent().getMakeup();
        if (null != makeups) {
            for (Makeup makeup : makeups) {
                setComposeMakeUpdataPathParam(makeup.getParams(), path);
            }
        }
        List<Beauty> beauties = composeMakeup.getEffectContent().getBeauty();
        if (null != beauties) {
            for (Beauty makeup : beauties) {
                setComposeMakeUpdataPathParam(makeup.getParams(), path);
            }
        }
        List<Shape> shapes = composeMakeup.getEffectContent().getShape();
        if (null != shapes) {
            for (Shape makeup : shapes) {
                setComposeMakeUpdataPathParam(makeup.getParams(), path);
            }
        }
        List<MicroShape> microShapes = composeMakeup.getEffectContent().getMicroShape();
        if (null != microShapes) {
            for (MicroShape makeup : microShapes) {
                setComposeMakeUpdataPathParam(makeup.getParams(), path);
            }
        }
    }

    private static void setComposeMakeUpdataPathParam(List<Params> paramsList, String path) {
        if (paramsList == null || paramsList.isEmpty()) {
            return;
        }
        for (Params param : paramsList) {
            if (param.getType().equals("path")) {
                Log.e(TAG, "setComposeMakeUpdataPathParam: " + param.getValue() + "  " + path
                        + " \n " + (path + File.separator + param.getValue()));
                param.setValue(path + File.separator + param.getValue());
            }
        }
    }

    /**
     * 安装特效资源包
     * Install fx package
     *
     * @param packagePath the package path
     */
    private static String installFxPackage(String packagePath, String licPath, int packageType) {
        return NveAssetPackageManagerUtil.installFxPackage(packagePath, licPath, packageType);
    }

    /**
     * 获取SDK中的素材类型表示方式
     * Get the representation of the material type in the SDK
     *
     * @param suffixName the suffix name 后缀名
     * @return the package type
     */
    public static int getPackageType(String suffixName) {
        if (TextUtils.isEmpty(suffixName)) {
            return -1;
        }
        switch (suffixName) {
            case "videofx":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
            case "facemesh":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_FACE_MESH;
            case "warp":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_WARP;
            case "makeup":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP;
            default:
                return -1;
        }
    }
}
