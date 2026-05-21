package com.meishe.makeup;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.Utils;
import com.meishe.makeup.makeup.BaseParam;
import com.meishe.makeup.makeup.BeautyParam;
import com.meishe.makeup.makeup.FilterParam;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupParam;
import com.meishe.makeup.makeup.MakeupParamContent;
import com.meishe.makeup.utils.DataConvertUtils;
import com.meishe.makeup.utils.ParseJsonFile;
import com.meishe.utils.PackageManagerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/23 16:29
 * @Description :美妆帮助类 Beauty makeup helper
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupHelper {
    private static final String MAX_FACES_RESPECT_MIN = "Max Faces Respect Min";
    private static final String SINGLE_BUFFER_MODE = "Single Buffer Mode";
    private static final String USE_FACE_EXTRA_INFO = "Use Face Extra Info";
    private static final String AR_SCENE = "AR Scene";
    private static final String MAKEUP_CUSTOM_ENABLE_FLAG = "Makeup Custom Enabled Flag";
    private final String MAKEUP_S = "Makeup ";
    private static final String S_COLOR = " Color";
    private static final String S_PACKAGE_ID = " Package Id";
    private static final String S_INTENSITY = " Intensity";
    public static final String ENABLE_BEAUTY_SHAPE = "Beauty Shape";
    public static final String ENABLE_BEAUTY_SHAPE_NEW = "Face Mesh Internal Enabled";

    /* ****************** 注意美妆里面有美颜的配置,它们有一定的关联性 Note that there are beauty configurations in beauty makeup, and they are related to a certain extent**************** */

    private final NvsStreamingContext mStreamingContext;
    /**
     * 拍摄的AR Scene特技
     * Capture AR Scene
     */
    private NvsCaptureVideoFx mArSceneFx;
    /**
     * 白名单特效（包含滤镜），产品需求：滤镜特效只能有一个，所以在应用美妆中滤镜的时候需要删除其他滤镜，白名单中的特效会保留。
     * Whitelist effects (including filters), product requirements: There can only be one filter effect,
     * so when applying the beauty filters, other filters need to be deleted, and the effects in the whitelist will be retained
     */
    private final List<String> mWhiteListFx;
    private final boolean mIs240;
    private final boolean mIsSingleBufferMode;

    public MakeupHelper(NvsStreamingContext streamingContext, boolean is240, boolean singleBuffer) {
        mStreamingContext = streamingContext;
        mWhiteListFx = new ArrayList<>();
        mIs240 = is240;
        mIsSingleBufferMode = singleBuffer;
        addWhiteListFx("Beauty");
        addWhiteListFx("Face Effect");
        addWhiteListFx("Sharpen");
        addWhiteListFx("Definition");
        addWhiteListFx(AR_SCENE);
    }

    /**
     * 添加特效白名单,产品需求：滤镜特效只能有一个，所以在应用美妆中滤镜的时候需要删除其他滤镜，白名单中的特效会保留
     * Add whitelist effects
     *
     * @param fxNameOrPackageId the effect name or packageId
     */
    public void addWhiteListFx(String fxNameOrPackageId) {
        if (!TextUtils.isEmpty(fxNameOrPackageId)) {
            mWhiteListFx.add(fxNameOrPackageId);
        }
    }

    /**
     * 检查创建拍摄的AR Scene特技
     * Check Build Capture ar scene fx
     */
    public NvsCaptureVideoFx checkBuildCaptureArSceneFx() {
        if (mStreamingContext != null && NvsStreamingContext.hasARModule() == 1) {
            // 美摄人脸模块 meishe face module
            if (mArSceneFx == null) {
                mArSceneFx = getCaptureVideoFx(AR_SCENE);
                if (mArSceneFx == null) {
                    mArSceneFx = mStreamingContext.appendBuiltinCaptureVideoFx(AR_SCENE);
                }
                if (mArSceneFx != null) {
                    // 支持的人脸个数，是否需要使用最小的设置 The number of supported faces, do you need to use the minimum setting
                    mArSceneFx.setBooleanVal(MAX_FACES_RESPECT_MIN, true);
                    // 是否设置单buffer模式 Whether to set single buffer mode
                    mArSceneFx.setBooleanVal(SINGLE_BUFFER_MODE, mIsSingleBufferMode);
                    enableCaptureMakeupFlag(NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                    Utils.setAIFaceOcclusion(mArSceneFx);
                }
            }
        }
        return mArSceneFx;
    }

    public NvsCaptureVideoFx getCaptureVideoFx() {
        return mArSceneFx;
    }

    /**
     * 检查创建clip的AR Scene特技
     * Check Build the ar scene fx of video clip
     */
    public NvsVideoFx checkBuildClipArSceneFx(NvsVideoClip videoClip) {
        if (NvsStreamingContext.hasARModule() == 1) {
            // 美摄人脸模块 meishe face module
            NvsVideoFx videoFx = getVideoFx(videoClip, AR_SCENE);
            if (videoFx == null) {
                // 如果外部已经创建了，这里不会执行 If the external has been created, it will not be executed here
                videoFx = videoClip.insertRawBuiltinFx(AR_SCENE, 0);
                if (videoFx != null) {
                    // 支持的人脸个数，是否需要使用最小的设置 The number of supported faces, do you need to use the minimum setting
                    videoFx.setBooleanVal(MAX_FACES_RESPECT_MIN, true);
                    // 美型开关打开 Beauty switch open
                    //  videoFx.setBooleanVal(ENABLE_BEAUTY_SHAPE, true);
                    //  videoFx.setBooleanVal(ENABLE_BEAUTY_SHAPE_NEW, true);
                    // 是否设置单buffer模式 Whether to set single buffer mode
                    videoFx.setBooleanVal(SINGLE_BUFFER_MODE, mIsSingleBufferMode);

                    if (videoFx.getARSceneManipulate() != null) {
                        videoFx.getARSceneManipulate().setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);
                    }
                    enableMakeupFlag(videoFx, NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                    Utils.setAIFaceOcclusion(videoFx);
                }
            }
            return videoFx;
        }
        LogUtils.e("has no ar module");
        return null;
    }

    /**
     * 获取视频片段的特效
     * Get  effects for a video clip
     *
     * @param buildInName  the build name
     * @param nvsVideoClip the video clip
     */
    private NvsVideoFx getVideoFx(NvsVideoClip nvsVideoClip, String buildInName) {
        for (int i = 0; i < nvsVideoClip.getRawFxCount(); i++) {
            NvsVideoFx videoFx = nvsVideoClip.getRawFxByIndex(i);
            if (videoFx != null && TextUtils.equals(videoFx.getBuiltinVideoFxName(), buildInName)) {
                return videoFx;
            }
        }
        return null;
    }

    /**
     * 获取拍摄视频特效
     * Get  effects for capture video
     *
     * @param buildInName the build name
     */
    private NvsCaptureVideoFx getCaptureVideoFx(String buildInName) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx videoFx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (videoFx != null && TextUtils.equals(videoFx.getBuiltinCaptureVideoFxName(), buildInName)) {
                return videoFx;
            }
        }
        return null;
    }

    /**
     * 设置拍摄的自定义美妆标志
     * Set the custom makeup enable flag
     *
     * @param flag the makeup enable flag
     */
    public void enableCaptureMakeupFlag(int flag) {
        enableMakeupFlag(mArSceneFx, flag);
    }

    /**
     * 设置自定义美妆标志
     * Set the custom makeup enable flag
     *
     * @param videoClip the video clip
     * @param flag      the makeup enable flag
     */
    public void enableMakeupFlag(NvsVideoClip videoClip, int flag) {
        enableMakeupFlag(checkBuildClipArSceneFx(videoClip), flag);
    }

    /**
     * 设置自定义美妆标志
     * Set the custom makeup enable flag
     *
     * @param flag      the makeup enable flag
     * @param arSceneFx the ar scene fx
     */
    public void enableMakeupFlag(NvsFx arSceneFx, int flag) {
        if (arSceneFx != null) {
            arSceneFx.setIntVal(MAKEUP_CUSTOM_ENABLE_FLAG, flag);
        }
    }

    /**
     * 设置拍摄自定义美妆的颜色
     * Set the capture custom makeup color
     *
     * @param makeupTypeId the makeup type id
     * @param nvsColor     the color
     */
    public void setCaptureMakeupColor(String makeupTypeId, NvsColor nvsColor) {
        setMakeupColor(mArSceneFx, makeupTypeId, nvsColor);
    }

    /**
     * 设置自定义美妆（单妆）的颜色
     * Set the custom makeup color
     *
     * @param videoClip    the video clip
     * @param makeupTypeId the makeup type id
     * @param nvsColor     the color
     */
    public void setMakeupColor(NvsVideoClip videoClip, String makeupTypeId, NvsColor nvsColor) {
        setMakeupColor(checkBuildClipArSceneFx(videoClip), makeupTypeId, nvsColor);
    }

    /**
     * 设置自定义美妆（单妆）的颜色
     * Set the custom makeup color
     *
     * @param arSceneFx    the ar scene fx
     * @param makeupTypeId the makeup type id
     * @param nvsColor     the color
     */
    public void setMakeupColor(NvsFx arSceneFx, String makeupTypeId, NvsColor nvsColor) {
        if (arSceneFx != null && !TextUtils.isEmpty(makeupTypeId)) {
            arSceneFx.setColorVal(getMakeupColorKey(makeupTypeId), nvsColor);
        }
    }

    /**
     * 设置拍摄美妆（单妆）的强度
     * Set the custom makeup intensity
     *
     * @param makeupTypeId the makeup type id
     * @param intensity    the intensity
     */
    public void setCaptureMakeupIntensity(String makeupTypeId, float intensity) {
        setMakeupIntensity(mArSceneFx, makeupTypeId, intensity);
    }

    /**
     * 设置拍摄美妆-滤镜的强度
     * Set the intensity of the shot Beauty - Filter
     *
     * @param videoFxName video fx name
     * @param intensity   intensity
     */
    public void setCaptureMakeupFilterIntensity(String videoFxName, float intensity) {
        setMakeupFilterIntensity(null, videoFxName, intensity);
    }

    /**
     * 设置美妆（单妆）的强度
     * Set the custom makeup intensity
     *
     * @param videoClip    the video clip
     * @param makeupTypeId the makeup type id
     * @param intensity    the intensity
     */
    public void setMakeupIntensity(NvsVideoClip videoClip, String makeupTypeId, float intensity) {
        setMakeupIntensity(checkBuildClipArSceneFx(videoClip), makeupTypeId, intensity);
    }

    /**
     * 设置美妆（单妆）的强度
     * Set the custom makeup intensity
     *
     * @param arSceneFx    the ar scene fx
     * @param makeupTypeId the makeup type id
     * @param intensity    the intensity
     */
    public void setMakeupIntensity(NvsFx arSceneFx, String makeupTypeId, float intensity) {
        if (arSceneFx != null && !TextUtils.isEmpty(makeupTypeId)) {
            arSceneFx.setFloatVal(getMakeupIntensityKey(makeupTypeId), intensity);
        }
    }

    /**
     * 设置美妆-滤镜的强度
     * Set the strength of the makeup - filter
     *
     * @param videoClip   video clip
     * @param videoFxName video fx name
     * @param intensity   intensity
     */
    public void setMakeupFilterIntensity(NvsVideoClip videoClip, String videoFxName, float intensity) {
        if (TextUtils.isEmpty(videoFxName)) {
            return;
        }
        if (null == videoClip) {
            int count = mStreamingContext.getCaptureVideoFxCount();
            for (int i = count - 1; i >= 0; i--) {
                NvsCaptureVideoFx captureVideoFx = mStreamingContext.getCaptureVideoFxByIndex(i);
                if (TextUtils.equals(captureVideoFx.getCaptureVideoFxPackageId(), videoFxName)) {
                    captureVideoFx.setFilterIntensity(intensity);
                }
            }
            return;
        }
        int count = videoClip.getFxCount();
        for (int i = count - 1; i >= 0; i--) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(i);
            if (TextUtils.equals(videoFx.getVideoFxPackageId(), videoFxName)) {
                videoFx.setFilterIntensity(intensity);
            }
        }
    }

    /**
     * 设置拍摄美妆（单妆）资源包
     * Set the custom makeup package id
     *
     * @param makeupTypeId the makeup type id
     * @param packageId    the package id
     */
    public void setCaptureMakeupPackageId(String makeupTypeId, String packageId) {
        setMakeupPackageId(mArSceneFx, makeupTypeId, packageId);
    }

    /**
     * 设置美妆（单妆）的资源包
     * Set the custom makeup intensity
     *
     * @param videoClip    the video clip
     * @param makeupTypeId the makeup type id
     * @param packageId    the package id
     */
    public void setMakeupPackageId(NvsVideoClip videoClip, String makeupTypeId, String packageId) {
        setMakeupPackageId(checkBuildClipArSceneFx(videoClip), makeupTypeId, packageId);
    }

    /**
     * 设置美妆（单妆）的资源包
     * Set the custom makeup intensity
     *
     * @param arSceneFx    the ar scene fx
     * @param makeupTypeId the makeup type id
     * @param packageId    the package id
     */
    public void setMakeupPackageId(NvsFx arSceneFx, String makeupTypeId, String packageId) {
        if (arSceneFx != null && !TextUtils.isEmpty(makeupTypeId)) {
            arSceneFx.setStringVal(getMakeupPackageKey(makeupTypeId), packageId);
        }
    }

    /**
     * 应用美妆(妆容),此方法用于本地妆容包文件夹
     * Apply the beauty makeup
     *
     * @param makeupFileDir the makeup dir
     */
    public Makeup applyCaptureMakeupFx(File makeupFileDir) {
        return applyMakeupFx(makeupFileDir, null, mArSceneFx);
    }

    /**
     * 应用美妆(妆容),此方法用于本地妆容包文件夹
     * Apply the beauty makeup
     *
     * @param makeupFileDir the makeup dir
     * @param videoClip     the video clip
     */
    public Makeup applyMakeupFx(File makeupFileDir, NvsVideoClip videoClip) {
        return applyMakeupFx(makeupFileDir, videoClip, checkBuildClipArSceneFx(videoClip));
    }

    /**
     * 应用美妆(妆容),此方法用于本地妆容包文件夹
     * Apply the beauty makeup
     *
     * @param makeupFileDir the makeup dir
     * @param videoClip     the video clip
     * @param arSceneFx     the ar scene fx
     */
    private Makeup applyMakeupFx(File makeupFileDir, NvsVideoClip videoClip, NvsFx arSceneFx) {
        Makeup makeupInfo = null;
        if ((null == makeupFileDir) || !makeupFileDir.exists()) {
            return null;
        }
        File[] fileList = makeupFileDir.listFiles();
        if ((null == fileList) || (fileList.length == 0)) {
            return null;
        }

        /*
         * 1.Json文件路径，新版美妆包中会存在info.json和info_new.json两个文件.
         * 2.优先获取info_new.json，如果不存在，则使用info.json，此时表示该美妆包为旧版本.
         * 3.使用旧版本时，需要进行数据结构转换成新版本使用的数据结构.
         * >1.Json file path, there will be two files info.json and info new.json in the new beauty package.
         * >2.Get info new.json first, if it does not exist, use info.json, which means the beauty package is an old version.
         * >3.When using the old version, it is necessary to convert the data structure to the data structure used by the new version.
         */
        String jsonPath = "";
        for (File itemFile : fileList) {
            if (isInvalidFolder(itemFile.getName())) {
                continue;
            }
            //带授权的多一层文件夹
            if (itemFile.isDirectory()) {
                return applyMakeupFx(itemFile, videoClip, arSceneFx);
            }
        }
        for (File itemFile : fileList) {
            if (isInvalidFolder(itemFile.getName())) {
                continue;
            }
            String name = itemFile.getName();
            if (TextUtils.equals(MakeupDataManager.INFO_JSON, name)) {
                if (!TextUtils.isEmpty(jsonPath) && jsonPath.endsWith(MakeupDataManager.INFO_NEW_JSON)) {
                    continue;
                }
                jsonPath = itemFile.getAbsolutePath();
                continue;
            }
            if (TextUtils.equals(MakeupDataManager.INFO_NEW_JSON, name)) {
                jsonPath = itemFile.getAbsolutePath();
                continue;
            }
            String fileName = FileUtils.getFileNameNoExtension(itemFile);
            String[] split = fileName.split("\\.");
            String licPath = makeupFileDir.getAbsolutePath() + File.separator + split[0] + ".lic";
            String packagePath = itemFile.getAbsolutePath();
            int packageType = getPackageType(FileUtils.getFileExtension(itemFile));
            if (packageType >= 0) {
                installFxPackage(packagePath, licPath, packageType);
            }
        }
        if (TextUtils.isEmpty(jsonPath)) {
            return null;
        }
        String infoJsonStr = ParseJsonFile.readSdCardJsonFile(jsonPath);
        if (jsonPath.endsWith(MakeupDataManager.INFO_JSON)) {
            makeupInfo = DataConvertUtils.convertMakeup(infoJsonStr, makeupFileDir.getAbsolutePath());
        } else {
            makeupInfo = ParseJsonFile.fromJson(infoJsonStr, Makeup.class);
        }
        if (null != makeupInfo) {
            makeupInfo.setAssetsDirectory(makeupFileDir.getAbsolutePath());
            applyMakeupFx(makeupInfo, videoClip, arSceneFx);
        }
        return makeupInfo;
    }

    /**
     * 应用美妆
     * Apply the beauty makeup about ar scene
     *
     * @param makeup the makeup info
     */
    public void applyCaptureMakeupFx(Makeup makeup) {
        applyMakeupFx(makeup, null, mArSceneFx);
    }

    /**
     * 应用美妆
     * Apply the beauty makeup
     *
     * @param makeup    the makeup info
     * @param videoClip the video clip
     */
    public void applyMakeupFx(Makeup makeup, NvsVideoClip videoClip) {
        applyMakeupFx(makeup, videoClip, checkBuildClipArSceneFx(videoClip));
    }

    /**
     * 重置拍摄的美妆,实际就是取反或者置空
     * Reset the capture beauty makeup
     *
     * @param makeup the makeup info
     */
    public void resetCaptureMakeupFx(Makeup makeup) {
        applyMakeupFx(makeup, null, mArSceneFx, true);
    }

    /**
     * 重置美妆,实际就是取反或者置空、删除
     * Reset the beauty makeup
     *
     * @param makeup    the makeup info
     * @param videoClip the video clip
     */
    public void resetMakeupFx(Makeup makeup, NvsVideoClip videoClip) {
        applyMakeupFx(makeup, videoClip, checkBuildClipArSceneFx(videoClip), true);
    }

    /**
     * 重置美妆中的滤镜
     * Reset the filter in beauty makeup
     *
     * @param makeup    the makeup info
     * @param videoClip the video clip
     */
    public void resetMakeupFilter(Makeup makeup, NvsVideoClip videoClip) {
        if (makeup != null) {
            MakeupParamContent effectContent = makeup.getEffectContent();
            if (effectContent != null) {
                List<FilterParam> filterParams = effectContent.getFilterParams();
                if ((null == filterParams) || filterParams.isEmpty()) {
                    return;
                }
                applyFilter(videoClip, new ArrayList<BaseParam>(filterParams), true);
            }
        }
    }

    /**
     * 应用美妆
     * Apply the beauty makeup
     *
     * @param makeup    the makeup info
     * @param videoClip the video clip
     * @param arSceneFx the ar scene fx
     */
    private void applyMakeupFx(Makeup makeup, NvsVideoClip videoClip, NvsFx arSceneFx) {
        applyMakeupFx(makeup, videoClip, arSceneFx, false);
    }

    /**
     * 应用美妆
     * Apply the beauty makeup
     *
     * @param makeup    the makeup info 美妆参数信息
     * @param videoClip the video clip 视频片段
     * @param arSceneFx the ar scene fx ar scene 特效
     * @param reset     reset fx param  是否重置，重置是取反或者置空对应的美妆参数信息
     */
    private void applyMakeupFx(Makeup makeup, NvsVideoClip videoClip, NvsFx arSceneFx, boolean reset) {
        if (makeup == null || arSceneFx == null) {
            return;
        }
        LogUtils.d("makeupId=" + makeup.getUuid() + ",name=" + makeup.getName());
        MakeupParamContent effectContent = makeup.getEffectContent();
        if (effectContent == null) {
            return;
        }
        // 美颜 beauty
        applyBeautyFx(arSceneFx, effectContent.getBeautyParams(), reset, makeup.getAssetsDirectory());
        // 美型 beauty
        applyBeautyFx(arSceneFx, effectContent.getShapeParams(), reset, makeup.getAssetsDirectory());
        // 微整形 micro plastic
        applyBeautyFx(arSceneFx, effectContent.getMicroShapeParams(), reset, makeup.getAssetsDirectory());
        // 滤镜 filter
        List<BaseParam> filterParams = new ArrayList<>();
        if (null != effectContent.getFilterParams() && effectContent.getFilterParams().size() > 0) {
            filterParams.addAll(effectContent.getFilterParams());
        }
        if (null != effectContent.getBeautyParams() && effectContent.getBeautyParams().size() > 0) {
            for (BeautyParam beautyParam : effectContent.getBeautyParams()) {

                if (null != beautyParam && TextUtils.equals("ColorCorrect", beautyParam.getType())) {
                    filterParams.add(beautyParam);
                }
            }
        }
        applyFilter(videoClip, filterParams, reset);
        // 添加美妆 add makeup
        List<MakeupParam> makeupParamList = effectContent.getMakeupParams();
        applyMakeupContentFx(makeupParamList, arSceneFx, reset, makeup.getAssetsDirectory());
    }

    private void applyMakeupContentFx(List<MakeupParam> makeupParamList, NvsFx arSceneFx, boolean reset, String parentPath) {
        if (makeupParamList != null) {
            for (MakeupParam param : makeupParamList) {
                if (param == null) {
                    continue;
                }
                applyParam(arSceneFx, param.getParams(), parentPath, reset);
                LogUtils.d(param);
                String packageId = reset ? "" : param.getPackageId();
                if (param.getColorData() != null) {
                    int color = param.getColorData().color;
                    float alpha = (Color.alpha(color) * 1.0f / 255f);
                    float red = (Color.red(color) * 1.0f / 255f);
                    float green = (Color.green(color) * 1.0f / 255f);
                    float blue = (Color.blue(color) * 1.0f / 255f);
                    NvsColor nvsColor = reset ? null : new NvsColor(red, green, blue, alpha);
                    setMakeupColor(arSceneFx, param.getType(), nvsColor);
                } else {
                    setMakeupColor(arSceneFx, param.getType(), new NvsColor(0, 0, 0, 0));
                }
            }
        }
    }

    /**
     * 应用美颜
     * Apply beauty fx
     *
     * @param arSceneFx       the ar scene fx
     * @param paramList       the beauty fx list
     * @param reset           reset fx param  是否重置，重置是取反或者置空对应的美妆参数信息
     * @param assetsDirectory
     */
    private void applyBeautyFx(NvsFx arSceneFx, List<? extends BaseParam> paramList, boolean reset, String assetsDirectory) {
        if (paramList != null && arSceneFx != null) {
            for (BaseParam baseParam : paramList) {
                if (null == baseParam) {
                    continue;
                }
                LogUtils.d(baseParam);
                if (baseParam instanceof BeautyParam) {
                    if (TextUtils.isEmpty(baseParam.getType())) {
                        applyParam(arSceneFx, baseParam.getParams(), assetsDirectory, reset);
                    } else if (TextUtils.equals(baseParam.getType(), "ColorCorrect")) {
                        // 校色 在外层与滤镜一起处理了 Color correction is processed in the outer layer together with the filter
                    }
                } else {
                    applyParam(arSceneFx, baseParam.getParams(), assetsDirectory, reset);
                }
            }
        }
    }


    private void applyParam(NvsFx arSceneFx, List<BaseParam.Param> params, String assetsDirectory, boolean reset) {
        if (arSceneFx == null || params == null) {
            return;
        }
        for (BaseParam.Param param : params) {
            if (null == param) {
                continue;
            }
            String paramsType = param.getType();
            if (TextUtils.equals(paramsType, "path") && param.getValue() instanceof String) {
                Log.e("applyParam ", "key = " + param.getKey() + " value =" + assetsDirectory + File.separator + param.getValue());
                arSceneFx.setStringVal(param.getKey(), reset ? "" : ((TextUtils.isEmpty(assetsDirectory) ? "" : assetsDirectory) + File.separator + param.getValue()));
            } else if (TextUtils.equals(paramsType, "float")) {
                float value = Float.parseFloat(param.getValue().toString());
                Log.e("applyParam ", "key = " + param.getKey() + " value =" + value);
                arSceneFx.setFloatVal(param.getKey(), reset ? 0 : value);
            } else if (TextUtils.equals(paramsType, "double")) {
                Log.e("applyParam ", "key = " + param.getKey() + " value =" + (double) param.getValue());
                arSceneFx.setFloatVal(param.getKey(), reset ? 0 : (double) param.getValue());
            } else if (TextUtils.equals(paramsType, "int")) {
                int value = (int) Math.round(Double.parseDouble(String.valueOf(param.getValue())));
                Log.e("applyParam ", "key = " + param.getKey() + " value =" + value);
                arSceneFx.setIntVal(param.getKey(), reset ? 0 : value);
            } else if (TextUtils.equals(paramsType, "boolean")) {
                Log.e("applyParam ", "key = " + param.getKey() + " value =" + param.getValue());
                arSceneFx.setBooleanVal(param.getKey(), (boolean) param.getValue());
            } else if (TextUtils.equals(paramsType, "string")) {
                Log.e("applyParam ", "key = " + param.getKey() + " value =" + (String) param.getValue());
                arSceneFx.setStringVal(param.getKey(), reset ? "" : (String) param.getValue());
            }
        }
    }

    /**
     * 应用滤镜
     * Apply filter
     *
     * @param videoClip       the video clip
     * @param filterParamList the filter fx list
     * @param reset           reset fx param  是否重置，重置是取反或者置空对应的美妆参数信息
     */
    private void applyFilter(NvsVideoClip videoClip, List<BaseParam> filterParamList, boolean reset) {
        if (filterParamList != null) {
            if (!mWhiteListFx.isEmpty() && !filterParamList.isEmpty()) {
                if (videoClip == null) {
                    // 删除非白名单特效 Delete non-whitelist special effects
                    int count = mStreamingContext.getCaptureVideoFxCount();
                    for (int i = count - 1; i >= 0; i--) {
                        NvsCaptureVideoFx captureVideoFx = mStreamingContext.getCaptureVideoFxByIndex(i);
                        boolean delete = true;
                        for (String videoFxName : mWhiteListFx) {
                            if (TextUtils.equals(captureVideoFx.getCaptureVideoFxPackageId(), videoFxName)
                                    || TextUtils.equals(captureVideoFx.getBuiltinCaptureVideoFxName(), videoFxName)) {
                                delete = false;
                                break;
                            }
                        }
                        if (delete) {
                            mStreamingContext.removeCaptureVideoFx(i);
                        }
                    }
                    if (reset) {
                        return;
                    }
                } else {
                    LogUtils.d("not delete other filter of clip");
                }
            }

            if (reset) {
                for (BaseParam filterParam : filterParamList) {
                    int fxCount = videoClip.getFxCount();
                    for (int i = fxCount - 1; i >= 0; i--) {
                        NvsVideoFx videoFx = videoClip.getFxByIndex(i);
                        if (TextUtils.equals(filterParam.getPackageId(), videoFx.getBuiltinVideoFxName()) ||
                                TextUtils.equals(filterParam.getPackageId(), videoFx.getVideoFxPackageId())) {
                            videoClip.removeFx(i);
                            break;
                        }
                    }
                }
            } else {
                for (BaseParam filterParam : filterParamList) {
                    String packageId = filterParam.getPackageId();
                    NvsFx nvsFx;
                    if (filterParam instanceof FilterParam && ((FilterParam) filterParam).isBuiltIn()) {
                        if (videoClip == null) {
                            nvsFx = mStreamingContext.appendBuiltinCaptureVideoFx(packageId);
                        } else {
                            nvsFx = videoClip.appendBuiltinFx(packageId);
                        }
                    } else {
                        if (videoClip == null) {
                            nvsFx = mStreamingContext.appendPackagedCaptureVideoFx(packageId);
                        } else {
                            nvsFx = videoClip.appendPackagedFx(packageId);
                        }
                    }
                    if (nvsFx != null) {
                        float value = 1;
                        if (filterParam instanceof FilterParam) {
                            value = ((FilterParam) filterParam).getValue();
                        } else if (filterParam instanceof BeautyParam) {
                            value = ((BeautyParam) filterParam).getValue();
                        }
                        Log.e("applyParam ", "filter id= " + packageId + " value =" + value);
                        nvsFx.setFilterIntensity(value);
                    }
                }
            }

        }
    }


    /**
     * 获取某一类型美妆的颜色key值
     * Get the color key about the given makeup
     **/
    private String getMakeupColorKey(String type) {
        return MAKEUP_S + type + S_COLOR;
    }

    /**
     * 获取某一类型美妆的强度key值
     * Get the intensity key about the given makeup
     **/
    public String getMakeupIntensityKey(String type) {
        return MAKEUP_S + type + S_INTENSITY;
    }

    /**
     * 获取某一类型美妆的资源包key
     * Get the package key about the given makeup
     **/
    public String getMakeupPackageKey(String type) {
        return MAKEUP_S + type + S_PACKAGE_ID;
    }

    public void installFxPackage(File makeupFileDir) {
        if ((null == makeupFileDir) || !makeupFileDir.exists()) {
            return;
        }
        File[] fileList = makeupFileDir.listFiles();
        if ((null == fileList) || (fileList.length == 0)) {
            return;
        }
        for (File itemFile : fileList) {
            String fileName = FileUtils.getFileNameNoExtension(itemFile);
            String[] split = fileName.split("\\.");
            String licPath = makeupFileDir.getAbsolutePath() + File.separator + split[0] + ".lic";
            String packagePath = itemFile.getAbsolutePath();
            int packageType = getPackageType(FileUtils.getFileExtension(itemFile));
            if (packageType >= 0) {
                installFxPackage(packagePath, licPath, packageType);
            }
        }
    }

    /**
     * 安装特效资源包
     * Install fx package
     *
     * @param packagePath the package path
     */
    private String installFxPackage(String packagePath, String licPath, int packageType) {
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

    private boolean isInvalidFolder(String folderName) {
        return "__MACOSX".equals(folderName);
    }
}
