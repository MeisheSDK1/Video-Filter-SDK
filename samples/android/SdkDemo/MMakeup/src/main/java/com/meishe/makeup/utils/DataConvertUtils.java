package com.meishe.makeup.utils;

import android.content.Context;
import android.text.TextUtils;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.app.BaseApp;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.BeautyParam;
import com.meishe.makeup.makeup.FilterParam;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupParam;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.makeup.makeup.original.BaseOldParam;
import com.meishe.makeup.makeup.original.BeautyOldParam;
import com.meishe.makeup.makeup.original.FilterOldParam;
import com.meishe.makeup.makeup.original.MakeupOldParam;
import com.meishe.makeup.makeup.original.MakeupOldParamContent;
import com.meishe.makeup.makeup.original.OriginalMakeup;
import com.meishe.makeup.makeup.original.ShapeOldParam;
import com.meishe.utils.PackageManagerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/12/8 15:18
 * @Description: 旧美妆数据转换新美妆数据工具类
 * Old beauty data conversion tool class for new beauty data
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class DataConvertUtils {
    private static final String MAEKUP_ID = "Makeup %s Package Id";
    private static final String MAEKUP_INTENSITY = "Makeup %s Intensity";
    private static final String SHAPE_ID = "Face Mesh %s Custom Package Id";
    private static final String SHAPE_DEGREE = "Face Mesh %s Degree";
    private static final String WHITEB_NAME = "WhiteB.mslut";
    public static final String ADJUST_COLOR_ROOT_PATH = "assets:/beauty/65521195-92A4-41CA-9DB5-6AB19C9321B5/";
    public static final String ADJUST_COLOR_PATH = ADJUST_COLOR_ROOT_PATH + "65521195-92A4-41CA-9DB5-6AB19C9321B5.1.videofx";
    public static final String ADJUST_COLOR_LIC_PATH = ADJUST_COLOR_ROOT_PATH + "65521195-92A4-41CA-9DB5-6AB19C9321B5.lic";

    /**
     * 转换美妆
     * convert makeup
     *
     * @param jsonInfo  info
     * @param directory directory
     * @return Makeup
     */
    public static Makeup convertMakeup(String jsonInfo, String directory) {
        OriginalMakeup originalMakeup = ParseJsonFile.fromJson(jsonInfo, OriginalMakeup.class);
        if (null == originalMakeup) {
            return null;
        }
        MakeupOldParamContent oldParamContent = originalMakeup.getEffectContent();
        if (null == oldParamContent) {
            return null;
        }
        Makeup makeup = new Makeup();
        MakeupParamContent paramContent = new MakeupParamContent();
        //公共 common
        makeup.setName(originalMakeup.getName());
        makeup.setUuid(originalMakeup.getUuid());
        makeup.setCover(originalMakeup.getCover());
        makeup.setVersion(originalMakeup.getVersion());
        makeup.setMinSdkVersion(originalMakeup.getMinSdkVersion());
        makeup.setSupportedAspectRatio(originalMakeup.getSupportedAspectRatio());
        makeup.setTranslation(originalMakeup.getTranslation());
        //美妆 makeup
        packageMakeup(oldParamContent, paramContent);
        //美颜 beauty
        packageBeauty(oldParamContent, paramContent, directory);
        //美型 shape
        packageShape(oldParamContent, paramContent);
        //微整形 micro shape
        packageMicroShape(oldParamContent, paramContent);
        //滤镜 filter
        packageFilter(oldParamContent, paramContent);
        makeup.setEffectContent(paramContent);
        return makeup;
    }

    /**
     * 组装美妆数据
     * Assemble makeup data
     *
     * @param oldParamContent old content
     * @param paramContent    content
     */
    private static void packageMakeup(MakeupOldParamContent oldParamContent, MakeupParamContent paramContent) {
        List<MakeupOldParam> makeupOldParams = oldParamContent.getMakeupParams();
        if ((null == makeupOldParams) || makeupOldParams.isEmpty()) {
            return;
        }
        List<MakeupParam> makeupParams = new ArrayList<>();
        for (MakeupOldParam oldParam : makeupOldParams) {
            if (null == oldParam) {
                continue;
            }
            MakeupParam makeupParam = new MakeupParam();
            makeupParam.setType(oldParam.getType());
            makeupParam.setCanReplace(oldParam.canReplace() ? 1 : 0);
            List<BaseParam.Param> paramList = packageCommonParams(MAEKUP_ID, MAEKUP_INTENSITY, makeupParam.getType(), oldParam);
            makeupParam.setParams(paramList);
            makeupParams.add(makeupParam);
        }
        paramContent.setMakeupParams(makeupParams);
    }

    /**
     * 组装美颜数据
     * Assemble beauty data
     *
     * @param oldParamContent old content
     * @param paramContent    content
     */
    private static void packageBeauty(MakeupOldParamContent oldParamContent, MakeupParamContent paramContent, String directory) {
        List<BeautyOldParam> beautyOldParams = oldParamContent.getBeautyParams();
        if ((null == beautyOldParams) || beautyOldParams.isEmpty()) {
            return;
        }
        List<BeautyParam> beautyParams = new ArrayList<>();
        //add enable
        beautyParams.add(enableBeauty());
        for (BeautyOldParam oldParam : beautyOldParams) {
            if (null == oldParam) {
                continue;
            }
            BeautyParam beautyParam = new BeautyParam();
            beautyParam.setCanReplace(oldParam.canReplace() ? 1 : 0);
            //磨皮 skinning
            boolean advancedBeautyEnable = oldParam.advancedBeautyEnable();
            if (advancedBeautyEnable) {
                enableAdvancedBeauty(beautyParam, oldParam.getAdvancedBeautyType(), oldParam.getValue());
                beautyParams.add(beautyParam);
                continue;
            }
            //美白 Whitening
            boolean whiteningLutEnabled = oldParam.whiteningLutEnabled();
            if (whiteningLutEnabled) {
                //是否需要拷贝美白B
                if (!TextUtils.isEmpty(directory)) {
                    String whitePath = directory + File.separator + WHITEB_NAME;
                    File file = new File(whitePath);
                    if (!file.exists()) {
                        copyAssetFile(BaseApp.getContext(), WHITEB_NAME, "capture", directory);
                    }
                }
                enableWhiteningLut(beautyParam, oldParam.getValue());
                beautyParams.add(beautyParam);
                continue;
            }
            String paramKey = oldParam.getParamKey();
            //红润 Reddening
            if (TextUtils.equals(paramKey, "Beauty Reddening")) {
                beautyParam.setParams(packageCommonParams(paramKey, oldParam.getValue()));
                beautyParams.add(beautyParam);
                continue;
            }
            //校色&锐度 Adjust&Sharpen
            if (oldParam.paramKeyEndWithEnable()) {
                //安装校色包
                if (TextUtils.equals(paramKey, "Default Beauty Enabled")) {
                    PackageManagerUtil.installAssetPackage(ADJUST_COLOR_PATH, ADJUST_COLOR_LIC_PATH, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
                }
                List<BaseParam.Param> enables = new ArrayList<>();
                enables.add(enableParam(paramKey, oldParam.valueEnable()));
                beautyParam.setParams(enables);
                beautyParams.add(beautyParam);
            }
        }
        paramContent.setBeautyParams(beautyParams);
    }

    /**
     * 组装美型数据
     * Assemble shape data
     *
     * @param oldParamContent old content
     * @param paramContent    content
     */
    private static void packageShape(MakeupOldParamContent oldParamContent, MakeupParamContent paramContent) {
        List<ShapeOldParam> shapeOldParams = oldParamContent.getShapeParams();
        if ((null == shapeOldParams) || shapeOldParams.isEmpty()) {
            return;
        }
        List<BaseParam> shapeParams = new ArrayList<>();
        //add enable
        shapeParams.add(enableShape());

        for (ShapeOldParam oldParam : shapeOldParams) {
            if (null == oldParam) {
                continue;
            }
            BaseParam shapeParam = new BaseParam();
            shapeParam.setType(oldParam.getType());
            shapeParam.setCanReplace(oldParam.canReplace() ? 1 : 0);
            List<BaseParam.Param> paramList = packageCommonParams(SHAPE_ID, SHAPE_DEGREE, shapeParam.getType(), oldParam);
            shapeParam.setParams(paramList);
            shapeParams.add(shapeParam);
        }
        paramContent.setShapeParams(shapeParams);
    }

    /**
     * 组装微整形数据
     * Assemble Micro shape data
     *
     * @param oldParamContent old content
     * @param paramContent    content
     */
    private static void packageMicroShape(MakeupOldParamContent oldParamContent, MakeupParamContent paramContent) {
        List<ShapeOldParam> shapeOldParams = oldParamContent.getMicroShapeParams();
        if ((null == shapeOldParams) || shapeOldParams.isEmpty()) {
            return;
        }
        List<BaseParam> microShapeParams = new ArrayList<>();
        //add enable
        enableMicroShape(microShapeParams);
        for (ShapeOldParam oldParam : shapeOldParams) {
            if (null == oldParam) {
                continue;
            }
            BaseParam microShapeParam = new BaseParam();
            microShapeParam.setCanReplace(oldParam.canReplace() ? 1 : 0);
            if (!TextUtils.isEmpty(oldParam.getType())) {
                microShapeParam.setType(oldParam.getType());
                List<BaseParam.Param> paramList = packageCommonParams(SHAPE_ID, SHAPE_DEGREE, microShapeParam.getType(), oldParam);
                microShapeParam.setParams(paramList);
                microShapeParams.add(microShapeParam);
                continue;
            }
            List<BaseParam.Param> paramList = new ArrayList<>();
            BaseParam.Param paramIntensity = new BaseParam.Param();
            paramIntensity.setKey(oldParam.getParamKey());
            paramIntensity.setValue(oldParam.getValue());
            paramIntensity.setType("float");
            paramList.add(paramIntensity);
            microShapeParam.setParams(paramList);
            microShapeParams.add(microShapeParam);
        }
        paramContent.setMicroShapeParams(microShapeParams);
    }

    /**
     * 组装滤镜数据
     * Assemble filter data
     *
     * @param oldParamContent old content
     * @param paramContent    content
     */
    private static void packageFilter(MakeupOldParamContent oldParamContent, MakeupParamContent paramContent) {
        List<FilterOldParam> filterOldParams = oldParamContent.getFilterParams();
        if ((null == filterOldParams) || filterOldParams.isEmpty()) {
            return;
        }
        List<FilterParam> fliterParams = new ArrayList<>();
        for (FilterOldParam oldParam : filterOldParams) {
            if (null == oldParam) {
                continue;
            }
            FilterParam filterParam = new FilterParam();
            filterParam.setIsBuiltIn(oldParam.isBuiltIn() ? 1 : 0);
            filterParam.setCanReplace(oldParam.canReplace() ? 1 : 0);
            filterParam.setPackageId(oldParam.getPackageId());
            filterParam.setValue(oldParam.getValue());
            fliterParams.add(filterParam);
        }
        paramContent.setFilterParams(fliterParams);
    }

    /**
     * 开启美颜
     * open beauty
     *
     * @return BeautyParam
     */
    private static BeautyParam enableBeauty() {
        BeautyParam baseParam = new BeautyParam();
        List<BaseParam.Param> enables = new ArrayList<>();
        enables.add(enableParam("Beauty Effect", true));
        baseParam.setParams(enables);
        return baseParam;
    }

    /**
     * 开启高级美颜
     * open  Advanced Beauty
     *
     * @param type      type
     * @param intensity intensity
     */
    private static void enableAdvancedBeauty(BeautyParam beautyParam, int type, float intensity) {
        List<BaseParam.Param> enables = new ArrayList<>();
        enables.add(enableParam("Advanced Beauty Enable", true));

        BaseParam.Param paramType = new BaseParam.Param();
        paramType.setKey("Advanced Beauty Type");
        paramType.setValue(type);
        paramType.setType("int");
        enables.add(paramType);

        BaseParam.Param paramIntensity = new BaseParam.Param();
        paramIntensity.setKey("Advanced Beauty Intensity");
        paramIntensity.setValue(intensity);
        paramIntensity.setType("float");
        enables.add(paramIntensity);
        beautyParam.setParams(enables);
    }

    /**
     * 开启美白
     * open  Whitening
     *
     * @param beautyParam Param
     * @param intensity   intensity
     */
    private static void enableWhiteningLut(BeautyParam beautyParam, float intensity) {
        List<BaseParam.Param> enables = new ArrayList<>();
        enables.add(enableParam("Whitening Lut Enabled", true));

        BaseParam.Param paramType = new BaseParam.Param();
        paramType.setKey("Whitening Lut File");
        paramType.setValue(WHITEB_NAME);
        paramType.setType("path");
        enables.add(paramType);

        BaseParam.Param paramIntensity = new BaseParam.Param();
        paramIntensity.setKey("Beauty Whitening");
        paramIntensity.setValue(intensity);
        paramIntensity.setType("float");
        enables.add(paramIntensity);
        beautyParam.setParams(enables);
    }

    /**
     * 开启美型
     * open shape
     *
     * @return BaseParam
     */
    private static BaseParam enableShape() {
        BaseParam baseParam = new BaseParam();
        List<BaseParam.Param> enables = new ArrayList<>();
        enables.add(enableParam("Face Mesh Internal Enabled", true));
        baseParam.setParams(enables);
        return baseParam;
    }

    /**
     * 开启微整形
     * open micro shape
     *
     * @param shapeParams shapeParams
     */
    private static void enableMicroShape(List<BaseParam> shapeParams) {
        BaseParam beautyParam = new BaseParam();
        List<BaseParam.Param> enables = new ArrayList<>();
        enables.add(enableParam("Advanced Beauty Enable", true));
        beautyParam.setParams(enables);
        shapeParams.add(beautyParam);

        BaseParam shapeParam = new BaseParam();
        List<BaseParam.Param> shapeEnables = new ArrayList<>();
        shapeEnables.add(enableParam("Beauty Shape", true));
        shapeEnables.add(enableParam("Face Mesh Internal Enabled", true));
        shapeParam.setParams(shapeEnables);
        shapeParams.add(shapeParam);
    }

    /**
     * 开启参数设置
     * Enable parameter setting
     *
     * @param key key
     * @return Param
     */
    private static BaseParam.Param enableParam(String key, boolean value) {
        BaseParam.Param paramEnable = new BaseParam.Param();
        paramEnable.setKey(key);
        paramEnable.setValue(value);
        paramEnable.setType("boolean");
        return paramEnable;
    }

    /**
     * 组装共同参数
     * Assemble common data
     *
     * @param keyId    keyId
     * @param valueId  valueId
     * @param type     type
     * @param oldParam oldParam
     * @return List<BaseParam.Param>
     */
    private static List<BaseParam.Param> packageCommonParams(String keyId, String valueId, String type, BaseOldParam oldParam) {
        List<BaseParam.Param> paramList = new ArrayList<>();
        if (!TextUtils.isEmpty(keyId)) {
            BaseParam.Param paramId = new BaseParam.Param();
            paramId.setKey(String.format(keyId, type));
            paramId.setValue(oldParam.getPackageId());
            paramId.setType("string");
            paramList.add(paramId);
        }
        if (!TextUtils.isEmpty(valueId)) {
            BaseParam.Param paramIntensity = new BaseParam.Param();
            paramIntensity.setKey(String.format(valueId, type));
            paramIntensity.setValue(oldParam.getValue());
            paramIntensity.setType("float");
            paramList.add(paramIntensity);
        }
        return paramList;
    }

    /**
     * 组装共同参数
     * Assemble common data
     *
     * @param key   key
     * @param value value
     * @return List<BaseParam.Param>
     */
    private static List<BaseParam.Param> packageCommonParams(String key, float value) {
        List<BaseParam.Param> paramList = new ArrayList<>();
        if (!TextUtils.isEmpty(key)) {
            BaseParam.Param paramIntensity = new BaseParam.Param();
            paramIntensity.setKey(key);
            paramIntensity.setValue(value);
            paramIntensity.setType("float");
            paramList.add(paramIntensity);
        }
        return paramList;
    }

    /**
     * 拷贝asset目录下文件到sd中
     * Copy the files in the asset directory to sd
     */
    public static void copyAssetFile(Context context, String fileName, String className, String destFileDirPath) {
        try {
            File folder = new File(destFileDirPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String destFilePath = destFileDirPath + File.separator + fileName;
            File file = new File(destFilePath);
            if (file.exists()) {
                return;
            }
            InputStream in = context.getAssets().open(className + File.separator + fileName);
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
