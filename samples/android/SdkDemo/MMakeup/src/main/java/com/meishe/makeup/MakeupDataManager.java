package com.meishe.makeup;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.CategoryContent;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupCategory;
import com.meishe.makeup.makeup.MakeupParam;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.makeup.utils.DataConvertUtils;
import com.meishe.makeup.utils.ParseJsonFile;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.meishe.utils.PathUtils.COMBINED_MAKEUP;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/25 10:59
 * @Description :美妆数据管理者 Makeup data manager
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupDataManager {
    public static final String INFO_JSON = "info.json";
    public static final String INFO_NEW_JSON = "info_new.json";
    public static final String ASSETS_MAKEUP_PATH = "beauty/makeup";
    public static final String ASSETS_VARIABLE_COMPOSE_PATH = "beauty/variablecompose";
    public static final String COMBINED_MAKEUP_TYPE = "Makeup(New)";
    /**
     * 单妆路径
     * customcompose path
     */
    private static final String ASSETS_MAKEUP_CUSTOM_PATH = "beauty/customcompose";

    public static List<MakeupCategory> getMakeupCategoryList(Context context, boolean isEdit) {
        String jsonContent = ParseJsonFile.readAssetJsonFile(context, ASSETS_MAKEUP_CUSTOM_PATH + File.separator + INFO_JSON);
        if (TextUtils.isEmpty(jsonContent)) {
            return null;
        }
        List<MakeupCategory> categoryInfoList = ParseJsonFile.fromJson(jsonContent, new TypeToken<List<MakeupCategory>>() {
        }.getType());
        String clearCoverPath = ASSETS_MAKEUP_PATH + File.separator + (isEdit ? "icon_edit_custom_none.png" : "icon_custom_none.png");
        for (MakeupCategory categoryInfo : categoryInfoList) {
            CategoryContent categoryContent = new CategoryContent();
            categoryInfo.setCategoryContent(categoryContent);
            categoryContent.setId(categoryInfo.getId());
            categoryContent.setType(categoryInfo.getDisplayName());
            List<Makeup> assetsMakeupList = getAssetsMakeupList(context, ASSETS_MAKEUP_CUSTOM_PATH + File.separator + categoryInfo.getDisplayName());
            List<Makeup> sdcardMakeupList = getLocalMakeupList(context, PathUtils.getLocalMakeupPath(categoryInfo.getDisplayName()), true);
            /*
             * 先本地测试目录，再assets内置目录。
             * First test the directory locally, and then the built-in directory of assets
             */
            sdcardMakeupList.add(0, getClearMakeup(context, clearCoverPath));
            sdcardMakeupList.addAll(assetsMakeupList);
            categoryContent.setLocalMakeupList(sdcardMakeupList);
        }
        MakeupCategory makeupCategory = new MakeupCategory();
        makeupCategory.setId(0);
        makeupCategory.setMaterialType(21);
//        makeupCategory.setDisplayName(COMBINED_MAKEUP_TYPE);
        makeupCategory.setDisplayName(context.getString(R.string.makeup_));
        makeupCategory.setDisplayNameZhCn(context.getString(R.string.makeup_));

        CategoryContent categoryContent = new CategoryContent();
        categoryContent.setId(makeupCategory.getId());
        categoryContent.setType(makeupCategory.getDisplayName());

        List<Makeup> assetsMakeupList = getAssetsMakeupList(context, ASSETS_VARIABLE_COMPOSE_PATH);
        List<Makeup> localMakeupList = getLocalMakeupList(context, PathUtils.getLocalMakeupPath(COMBINED_MAKEUP));
        /*
         * 先本地测试目录，再assets内置目录。
         * First test the directory locally, and then the built-in directory of assets
         */
        localMakeupList.add(0, getClearMakeup(context, clearCoverPath));
        localMakeupList.addAll(assetsMakeupList);
        categoryContent.setLocalMakeupList(localMakeupList);

        List<Makeup> downloadMakeupList = getLocalMakeupList(context, PathUtils.getAssetDownloadPath(31));
        categoryContent.setRemoteMakeupList(downloadMakeupList);

        makeupCategory.setCategoryContent(categoryContent);
        categoryInfoList.add(0, makeupCategory);
        return categoryInfoList;
    }

    public static Makeup getClearMakeup(Context context, String coverPath) {
        Makeup makeup = new Makeup();
        makeup.setName(context.getString(R.string.no));
        makeup.setCover(coverPath);
        return makeup;
    }

    /**
     * 获取assets中的美妆列表
     * Get the makeup list in assets
     *
     * @param context    the context
     * @param assetsPath the assets path
     **/
    public static List<Makeup> getAssetsMakeupList(Context context, String assetsPath) {
        List<Makeup> assetsList = new ArrayList<>();
        if (context == null || TextUtils.isEmpty(assetsPath)) {
            return assetsList;
        }

        try {
            String[] list = context.getAssets().list(assetsPath);
            if (list != null) {
                for (String assetFilePath : list) {
                    String jsonPath = assetsPath + File.separator + assetFilePath;
                    Makeup makeup = getMakeup(context, jsonPath, true);
                    if (makeup != null) {
                        assetsList.add(makeup);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return assetsList;
        }
        return assetsList;
    }

    /**
     * 获取本地sdcard中的美妆列表
     * Get the makeup list in sdcard
     *
     * @param context   the context
     * @param localPath the local makeup path
     **/
    public static List<Makeup> getLocalMakeupList(Context context, String localPath) {
        return getLocalMakeupList(context, localPath, false);
    }

    /**
     * 获取本地sdcard中的美妆列表
     * Get the makeup list in sdcard
     *
     * @param context   the context
     * @param localPath the local makeup path
     **/
    public static List<Makeup> getLocalMakeupList(Context context, String localPath, boolean singleMakeup) {
        List<Makeup> assetsList = new ArrayList<>();
        if (context == null || TextUtils.isEmpty(localPath)) {
            return assetsList;
        }
        File file = new File(localPath);
        if (file.exists()) {
            File[] list = file.listFiles();
            if (list != null) {
                if (singleMakeup) {
                    for (File fileItem : list) {
                        if (fileItem.isFile() && fileItem.getName().endsWith("makeup")) {
                            String packageId = installFxPackage(fileItem.getAbsolutePath(), "", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP);
                            Makeup makeup = new Makeup();
                            makeup.setAssetsDirectory(fileItem.getParent());
                            makeup.setUuid(packageId);
                            MakeupParamContent makeupParamContent = new MakeupParamContent();
                            makeup.setEffectContent(makeupParamContent);

                            ArrayList<MakeupParam> makeupParams = new ArrayList<>();
                            makeupParamContent.setMakeupParams(makeupParams);
                            MakeupParam makeupParam = new MakeupParam();
                            makeupParams.add(makeupParam);
                            File parentFile = fileItem.getParentFile();
                            if (parentFile == null) {
                                LogUtils.e("parent file is null,check it !!!");
                            } else {
                                makeupParam.setType(parentFile.getName());
                                List<BaseParam.Param> paramList = new ArrayList<>();
                                /*
                                 *设置美妆单妆包的参数
                                 * Set the parameters of the beauty makeup package
                                 */
                                BaseParam.Param param = new BaseParam.Param();
                                param.setKey("Makeup " + makeupParam.getType() + " Package Id");
                                param.setValue(packageId);
                                param.setType("string");
                                paramList.add(param);
                                /*
                                 * 设置美妆单妆强度的参数
                                 * Parameters for setting the strength of a single beauty makeup
                                 */
                                BaseParam.Param paramValue = new BaseParam.Param();
                                paramValue.setKey("Makeup " + makeupParam.getType() + " Intensity");
                                paramValue.setValue(0.6f);
                                paramValue.setType("float");
                                paramList.add(paramValue);

                                makeupParam.setParams(paramList);
                            }
                            makeupParam.setPackageId(packageId);
                            assetsList.add(makeup);
                        }
                    }
                } else {
                    for (File fileItem : list) {
                        if (fileItem.isDirectory()) {
                            Makeup makeup = getMakeup(context, fileItem.getAbsolutePath(), false);
                            if (makeup != null) {
                                assetsList.add(makeup);
                            }
                            File[] files = fileItem.listFiles();
                            if (files != null) {
                                for (File fxItem : files) {
                                    int packageType = getPackageType(FileUtils.getFileExtension(fileItem));
                                    if (packageType >= 0) {
                                        installFxPackage(fxItem.getAbsolutePath(), "", packageType);
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
    private static Makeup getMakeup(Context context, String fileDir, boolean isAsset) {
        String jsonNewPath = fileDir + File.separator + INFO_NEW_JSON;
        File newFile = new File(jsonNewPath);
        if (!newFile.exists()) {
            //兼容旧版美妆数据 Compatible with old beauty data
            String jsonPath = fileDir + File.separator + INFO_JSON;
            String readInfo = !isAsset ? ParseJsonFile.readSdCardJsonFile(jsonPath) : ParseJsonFile.readAssetJsonFile(context, jsonPath);
            if (!TextUtils.isEmpty(readInfo)) {
                Makeup makeup = DataConvertUtils.convertMakeup(readInfo, fileDir);
                packageMakeup(context, makeup, fileDir);
                return makeup;
            }
            return null;
        }

        String readInfo = !isAsset ? ParseJsonFile.readSdCardJsonFile(jsonNewPath) : ParseJsonFile.readAssetJsonFile(context, jsonNewPath);
        if (!TextUtils.isEmpty(readInfo)) {
            Makeup makeup = ParseJsonFile.fromJson(readInfo, Makeup.class);
            packageMakeup(context, makeup, fileDir);
            return makeup;
        }
        return null;
    }

    private static void packageMakeup(Context context, Makeup makeup, String fileDir) {
        if ((null == makeup) || (TextUtils.isEmpty(fileDir))) {
            return;
        }
        if (isZh(context)) {
            makeup.setName(makeup.getTranslation().get(0).getTargetText());
        } else {
            makeup.setName(makeup.getTranslation().get(0).getOriginalText());
        }
        makeup.setAssetsDirectory(fileDir);
        if (TextUtils.isEmpty(makeup.getUuid())) {
            String[] split = fileDir.split("/");
            makeup.setUuid(split[split.length - 1]);
        }
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    /**
     * 安装特效资源包
     * Install fx package
     *
     * @param packagePath the package path
     */
    private static String installFxPackage(String packagePath, String licPath, int packageType) {
        return PackageManagerUtil.installAssetPackage(packagePath, licPath, packageType);
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

    private static final int[] MAKEUP_ICON = {
            R.mipmap.icon_lip,
            R.mipmap.icon_eyeshadow,
            R.mipmap.icon_eyebrow,
            R.mipmap.icon_eyelash,
            R.mipmap.icon_eyeliner,
            R.mipmap.icon_blusher,
            R.mipmap.icon_brighten,
            R.mipmap.icon_shadow,
            R.mipmap.icon_eyeball};

    public static List<MakeupCategory> getMakeupCategory(Context context) {
        String jsonContent = ParseJsonFile.readAssetJsonFile(context, ASSETS_MAKEUP_CUSTOM_PATH + File.separator + INFO_JSON);
        if (TextUtils.isEmpty(jsonContent)) {
            return null;
        }
        List<MakeupCategory> categoryInfoList = ParseJsonFile.fromJson(jsonContent, new TypeToken<List<MakeupCategory>>() {
        }.getType());
        if ((null == categoryInfoList) || categoryInfoList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < categoryInfoList.size(); i++) {
            MakeupCategory category = categoryInfoList.get(i);
            category.setType(category.getDisplayName());
            if (i > MAKEUP_ICON.length) {
                continue;
            }
            category.setCover(MAKEUP_ICON[i]);
        }
        return categoryInfoList;
    }
}
