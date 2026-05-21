package com.meishe.arscene;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.Utils;
import com.meishe.utils.PackageManagerUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.meishe.arscene.CustomBeautyHelper.FILTER_INTENSITY;
import static com.meishe.arscene.bean.FxParams.FOREHEAD_HEIGHT_DEGREE;
import static com.meishe.arscene.bean.FxParams.HEAD_SIZE_DEGREE;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/18 17:08
 * @Description :美颜帮助类 beauty helper
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyHelper {
    private static final String TAG = "BeautyHelper";
    private static final String MAX_FACES_RESPECT_MIN = "Max Faces Respect Min";
    private static final String SINGLE_BUFFER_MODE = "Single Buffer Mode";
    private static final String USE_FACE_EXTRA_INFO = "Use Face Extra Info";
    private static final String AR_SCENE = "AR Scene";
    private static final String BEAUTY = "Beauty";
    private static final String SHARPEN = "Sharpen";
    private static final String DEFINITION = "Definition";
    public static final String ENABLE_BEAUTY = "Beauty Effect";
    public static final String ENABLE_ADVANCED_BEAUTY = "Advanced Beauty Enable";
    public static final String ENABLE_BEAUTY_SHAPE = "Beauty Shape";
    public static final String ENABLE_BEAUTY_SHAPE_NEW = "Face Mesh Internal Enabled";
    private final NvsStreamingContext mStreamingContext;
    private NvsCaptureVideoFx mSharpenFx;
    private NvsCaptureVideoFx mDefinitionFx;
    private NvsCaptureVideoFx mBeautyFx;
    private NvsCaptureVideoFx mLastFx;
    private String mLastInstallPackageId = "123";
    private final boolean mIs240;
    private final boolean mIsSingleBufferMode;

    public BeautyHelper(@NonNull NvsStreamingContext context, boolean is240, boolean singleBuffer) {
        mStreamingContext = context;
        mIs240 = is240;
        mIsSingleBufferMode = singleBuffer;
    }

    /**
     * 检查创建拍摄的美颜、美型特技
     * Check Build Capture beauty FX
     */
    public NvsCaptureVideoFx checkBuildCaptureBeautyFx() {
        if (mBeautyFx == null) {
            if (NvsStreamingContext.hasARModule() == 1) {
                mBeautyFx = getCaptureVideoFx(AR_SCENE);
                if (mBeautyFx == null) {
                    mBeautyFx = mStreamingContext.appendBuiltinCaptureVideoFx(AR_SCENE);
                }
                if (mBeautyFx != null) {
                    // 支持的人脸个数，是否需要使用最小的设置 The number of faces supported, whether you need to use a minimum setting
                    mBeautyFx.setBooleanVal(MAX_FACES_RESPECT_MIN, true);
                    //这个返回true的情况下 必须使用双buffer 不受设置影响 If this returns true, double buffers must be used regardless of the setting
                    if (mStreamingContext.isAndroidCameraPreferDualBufferAR()) {
                        mBeautyFx.setBooleanVal(SINGLE_BUFFER_MODE, false);
                    } else {
                        // 是否设置单buffer模式 Whether to set the single buffer mode
                        mBeautyFx.setBooleanVal(SINGLE_BUFFER_MODE, mIsSingleBufferMode);
                    }
                }
                Utils.setAIFaceOcclusion(mBeautyFx);
            } else {
                mBeautyFx = getCaptureVideoFx(BEAUTY);
                if (mBeautyFx == null) {
                    mBeautyFx = mStreamingContext.appendBeautyCaptureVideoFx();
                }
            }
        }
        return mBeautyFx;
    }

    /**
     * 获取拍摄视频特效
     * Get capture video fx
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
     * 获取拍摄的美颜特技
     * Get Capture Beauty FX
     */
    public NvsFx getCaptureBeautyFx() {
        return mBeautyFx;
    }

    /**
     * 获取美颜特技
     * Get Beauty FX
     */
    public NvsVideoFx getBeautyFx(NvsVideoClip videoClip) {
        NvsVideoFx videoFx = getVideoFx(videoClip, AR_SCENE);
        if (videoFx == null) {
            videoFx = getVideoFx(videoClip, BEAUTY);
        }
        return videoFx;
    }

    /**
     * 检查创建clip的AR Scene特技
     * Check Build the ar scene fx of video clip
     */
    public NvsVideoFx checkBuildClipArSceneFx(NvsVideoClip videoClip) {
        NvsVideoFx videoFx;
        if (NvsStreamingContext.hasARModule() == 1) {
            // 美摄人脸模块 meishe face module
            videoFx = getVideoFx(videoClip, AR_SCENE);
            if (videoFx == null) {
                // 如果外部已经创建了，这里不会执行 If an external one has already been created, it will not be executed here
                videoFx = videoClip.insertRawBuiltinFx(AR_SCENE, 0);
                if (videoFx != null) {
                    // 支持的人脸个数，是否需要使用最小的设置 The number of faces supported, whether you need to use a minimum setting
                    videoFx.setBooleanVal(MAX_FACES_RESPECT_MIN, true);
                    // 是否设置单buffer模式 Whether to set the single buffer mode
                    videoFx.setBooleanVal(SINGLE_BUFFER_MODE, mIsSingleBufferMode);
                    if (videoFx.getARSceneManipulate() != null) {
                        videoFx.getARSceneManipulate().setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);
                    }
                    Utils.setAIFaceOcclusion(videoFx);
                }
            }
            return videoFx;
        } else {
            LogUtils.e("has no ar module");
            videoFx = getVideoFx(videoClip, BEAUTY);
            if (videoFx == null) {
                videoFx = videoClip.insertBeautyFx(0);
            }
        }
        return videoFx;
    }

    /**
     * 获取视频片段的特效
     * Get the effects of the video footage
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
     * 删除拍摄的美颜特效
     * Delete capture beauty
     **/
    public void deleteCaptureBeauty() {
        if (mBeautyFx != null) {
            mStreamingContext.removeCaptureVideoFx(mBeautyFx.getIndex());
        }
        mBeautyFx = null;
    }

    /**
     * 删除美颜特效
     * Delete beauty fx
     **/
    public void deleteBeauty(NvsVideoClip videoClip) {
        if (videoClip != null) {
            NvsVideoFx videoFx = getVideoFx(videoClip, AR_SCENE);
            if (videoFx != null) {
                videoClip.removeFx(videoFx.getIndex());
            }
        }
    }

    /**
     * 检查创建拍摄的锐度特技
     * Check to create shot sharpness stunts
     */
    public NvsCaptureVideoFx checkBuildCaptureSharpenFx() {
        if (mSharpenFx == null) {
            mSharpenFx = getCaptureVideoFx(SHARPEN);
            if (mSharpenFx == null) {
                mSharpenFx = mStreamingContext.appendBuiltinCaptureVideoFx(SHARPEN);
            }
        }
        return mSharpenFx;
    }

    /**
     * 检查创建clip的锐度特技
     * Check Build the Sharpen fx of video clip
     */
    public NvsVideoFx checkBuildClipSharpenFx(NvsVideoClip videoClip) {
        NvsVideoFx videoFx = getVideoFx(videoClip, SHARPEN);
        if (videoFx == null) {
            videoFx = videoClip.appendBuiltinFx(SHARPEN);
        }
        return videoFx;
    }

    /**
     * 删除锐度特技
     * Delete Sharpen Fx
     */
    public void deleteCaptureSharpen() {
        if (mSharpenFx != null && mSharpenFx.getIndex() >= 0) {
            mStreamingContext.removeCaptureVideoFx(mSharpenFx.getIndex());
        }
        mSharpenFx = null;
    }

    /**
     * 检查创建拍摄的清晰度特技
     * Check to create shot Definition stunts
     */
    public NvsCaptureVideoFx checkBuildCaptureDefinitionFx() {
        if (mDefinitionFx == null) {
            mDefinitionFx = getCaptureVideoFx(DEFINITION);
            if (mDefinitionFx == null) {
                mDefinitionFx = mStreamingContext.appendBuiltinCaptureVideoFx(DEFINITION);
            }
            if (null != mDefinitionFx) {
                mDefinitionFx.setBooleanVal("Fast Mode", true);
            }
        }
        return mDefinitionFx;
    }

    /**
     * 检查创建clip的清晰度特技
     * Check Build the Sharpen fx of video clip
     */
    public NvsVideoFx checkBuildClipDefinitionFx(NvsVideoClip videoClip) {
        NvsVideoFx videoFx = getVideoFx(videoClip, DEFINITION);
        if (videoFx == null) {
            videoFx = videoClip.appendBuiltinFx(DEFINITION);
        }
        if (null != videoFx) {
            videoFx.setBooleanVal("Fast Mode", true);
        }
        return videoFx;
    }

    /**
     * 删除清晰度特技
     * Delete Definition Fx
     */
    public void deleteCaptureDefinition() {
        if (mDefinitionFx != null && mDefinitionFx.getIndex() >= 0) {
            mStreamingContext.removeCaptureVideoFx(mDefinitionFx.getIndex());
        }
        mDefinitionFx = null;
    }

    /**
     * 获取资源包形式的特效
     * Get package fx
     *
     * @param packageId the package id
     **/
    public NvsCaptureVideoFx getCapturePackageFx(String packageId) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx videoFx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (videoFx != null && TextUtils.equals(videoFx.getCaptureVideoFxPackageId(), packageId)) {
                return videoFx;
            }
        }
        return mStreamingContext.appendPackagedCaptureVideoFx(packageId);
    }

    public void removeCapturePackageFx(String packageId) {
        if (TextUtils.isEmpty(packageId)) {
            return;
        }
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx videoFx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (videoFx != null && TextUtils.equals(videoFx.getCaptureVideoFxPackageId(), packageId)) {
                mStreamingContext.removeCaptureVideoFx(i);
            }
        }
    }

    /**
     * 获取视频片段的包特效
     * Get video clip package fx
     *
     * @param packedId     the packageId
     * @param nvsVideoClip the video clip
     */
    private NvsVideoFx getVideoPackageFx(NvsVideoClip nvsVideoClip, String packedId) {
        for (int i = 0; i < nvsVideoClip.getFxCount(); i++) {
            NvsVideoFx videoFx = nvsVideoClip.getFxByIndex(i);
            if (videoFx != null && TextUtils.equals(videoFx.getVideoFxPackageId(), packedId)) {
                return videoFx;
            }
        }
        return null;
    }

    /**
     * 视频片段设置包特效
     *
     * @param nvsVideoClip the video clip
     * @param packedId     the packageId
     * @return NvsVideoFx
     */
    private NvsVideoFx setVideoPackageFx(NvsVideoClip nvsVideoClip, String packedId) {
        if ((null == nvsVideoClip) || TextUtils.isEmpty(packedId)) {
            return null;
        }
        return nvsVideoClip.appendPackagedFx(packedId);
    }


    /**
     * 拍摄的普通美颜开关 enable the capture normal beauty
     *
     * @param enable true enable false not
     */
    public void enableCaptureBeauty(boolean enable) {
        enableBeauty(mBeautyFx, enable);
    }

    /**
     * 普通美颜开关 enable the normal beauty
     *
     * @param enable    true enable false not
     * @param videoClip the video clip
     */
    public void enableBeauty(NvsVideoClip videoClip, boolean enable) {
        enableBeauty(checkBuildClipArSceneFx(videoClip), enable);
    }

    /**
     * 普通美颜开关 enable the normal beauty
     *
     * @param enable    true enable false not
     * @param arSceneFx the ar scene fx
     */
    public void enableBeauty(NvsFx arSceneFx, boolean enable) {
        if (arSceneFx != null) {
            arSceneFx.setBooleanVal(ENABLE_BEAUTY, enable);
        }
    }

    /**
     * 拍摄的高级美颜开关 enable the capture advanced beauty
     *
     * @param enable true enable false not
     */
    public void enableCaptureAdvancedBeauty(boolean enable) {
        enableAdvancedBeauty(mBeautyFx, enable);
    }

    /**
     * 高级美颜开关 enable the advanced beauty
     *
     * @param enable    true enable false not
     * @param videoClip the video clip
     */
    public void enableAdvancedBeauty(NvsVideoClip videoClip, boolean enable) {
        enableAdvancedBeauty(checkBuildClipArSceneFx(videoClip), enable);
    }

    /**
     * 高级美颜开关 enable the advanced beauty
     *
     * @param enable    true enable false not
     * @param arSceneFx the ar scene fx
     */
    public void enableAdvancedBeauty(NvsFx arSceneFx, boolean enable) {
        if (arSceneFx != null) {
            arSceneFx.setBooleanVal(ENABLE_ADVANCED_BEAUTY, enable);
        }
    }

    /**
     * 拍摄的普通美型开关 enable the capture normal beauty shape
     *
     * @param enable true enable false not
     */
    public void enableCaptureShape(boolean enable) {
        enableShape(mBeautyFx, enable);
    }

    /**
     * 普通美型开关 enable the normal beauty shape
     *
     * @param enable    true enable false not
     * @param videoClip the video clip
     */
    public void enableShape(NvsVideoClip videoClip, boolean enable) {
        enableShape(checkBuildClipArSceneFx(videoClip), enable);
    }

    /**
     * 普通美型开关 enable the normal beauty shape
     *
     * @param enable    true enable false not
     * @param arSceneFx the ar scene fx
     */
    public void enableShape(NvsFx arSceneFx, boolean enable) {
        if (arSceneFx != null) {
            arSceneFx.setBooleanVal(ENABLE_BEAUTY_SHAPE, enable);
        }
    }

    /**
     * 拍摄的新美型开关 enable the capture new beauty shape
     *
     * @param enable true enable false not
     */
    public void enableCaptureShapeNew(boolean enable) {
        enableShapeNew(mBeautyFx, enable);
    }

    /**
     * 新美型开关 enable the new beauty shape
     *
     * @param enable    true enable false not
     * @param videoClip the video clip
     */
    public void enableShapeNew(NvsVideoClip videoClip, boolean enable) {
        enableShapeNew(checkBuildClipArSceneFx(videoClip), enable);
    }

    /**
     * 新美型开关 enable the new beauty shape
     *
     * @param arSceneFx the ar scene fx
     * @param enable    true enable false not
     */
    public void enableShapeNew(NvsFx arSceneFx, boolean enable) {
        if (arSceneFx != null) {
            arSceneFx.setBooleanVal(ENABLE_BEAUTY_SHAPE_NEW, enable);
        }
    }

    /**
     * 应用拍摄美颜特技中的相关特技
     * Apply the fx about capture ar scene
     *
     * @param fxInfo the fx info
     */
    public void applyCaptureBeautyFx(IFxInfo fxInfo) {
        applyBeautyFx(null, mBeautyFx, fxInfo);
    }

    /**
     * 应用美颜特技中的相关特技
     * Apply the fx about ar scene
     *
     * @param videoClip the NvsVideoClip
     * @param fxInfo    the fx info
     */
    public void applyBeautyFx(NvsVideoClip videoClip, IFxInfo fxInfo) {
        applyBeautyFx(videoClip, checkBuildClipArSceneFx(videoClip), fxInfo);
    }

    /**
     * 应用美颜特技中的相关特技
     * Apply the fx about ar scene
     *
     * @param videoClip the NvsVideoClip
     * @param arSceneFx the ar scene fx
     * @param fxInfo    the fx info
     */
    public void applyBeautyFx(NvsVideoClip videoClip, NvsFx arSceneFx, IFxInfo fxInfo) {
        if (fxInfo != null && arSceneFx != null) {
            String fxDesc = getFxDesc(arSceneFx);
            String assetPackagePath = fxInfo.getAssetPackagePath();
            int packageType = getPackageType(FileUtils.getFileExtension(assetPackagePath));
            if (!TextUtils.isEmpty(assetPackagePath) && !TextUtils.equals(mLastInstallPackageId, fxInfo.getPackageId())) {
                // 检查是否和上次安装的一样，减少安装次数。 如果有资源包，检查安装。
                // Check whether it is the same as the last installation and reduce the number of installation times. If there are resource bundles, check the installation.
                fxInfo.setPackageId(mLastInstallPackageId = installFxPackage(assetPackagePath, packageType));
            }
            // LogUtils.d(fxInfo);
            if (fxInfo instanceof CompoundFxInfo) {
                // 如果是复合型的特效 If it's a compound effect
                CompoundFxInfo compoundFx = (CompoundFxInfo) fxInfo;
                List<FxParams> paramList = compoundFx.getParamList();
                if (paramList != null) {
                    for (FxParams param : paramList) {
                        if (param != null) {
                            Log.d(TAG, "applyBeautyFx:  key = " + param.key + ", value = " + param.value);
                            if (param.value instanceof Float) {
                                arSceneFx.setFloatVal(param.key, (Float) param.value);
                            } else if (param.value instanceof Double) {
                                arSceneFx.setFloatVal(param.key, (Double) param.value);
                            } else if (param.value instanceof String) {
                                arSceneFx.setStringVal(param.key, (String) param.value);
                            } else if (param.value instanceof Boolean) {
                                arSceneFx.setBooleanVal(param.key, (Boolean) param.value);
                            } else if (param.value instanceof Integer) {
                                arSceneFx.setIntVal(param.key, (Integer) param.value);
                            }
                            CustomBeautyHelper.get().addBuildInParam(fxDesc, new FxParams(param.key, param.value));
                        }
                    }
                }
            }
            if (TextUtils.isEmpty(fxInfo.getFxName())) {
                if (!TextUtils.isEmpty(fxInfo.getPackageId()) && packageType == NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX) {
                    if (videoClip == null) {
                        if (mLastFx == null || !TextUtils.equals(mLastFx.getCaptureVideoFxPackageId(), fxInfo.getPackageId())) {
                            // 缓存一次，减小查找次数 Cache once to reduce lookup times
                            mLastFx = getCapturePackageFx(fxInfo.getPackageId());
                        }
                        if (mLastFx != null) {
                            Log.e("applyParam", "package id= " + fxInfo.getPackageId() + " value =" + fxInfo.getStrength());
                            mLastFx.setFilterIntensity((float) fxInfo.getStrength());

                            CustomBeautyHelper.get().addPackageParam(fxInfo.getPackageId(), new FxParams(FILTER_INTENSITY, fxInfo.getStrength()));
                        }
                    } else {
                        NvsVideoFx videoFx = getVideoPackageFx(videoClip, fxInfo.getPackageId());
                        if (null == videoFx) {
                            videoFx = setVideoPackageFx(videoClip, fxInfo.getPackageId());
                        }
                        if (null != videoFx) {
                            videoFx.setFilterIntensity((float) fxInfo.getStrength());
                        }
                    }
                }
            } else {
                if (fxInfo.getName().equals(HEAD_SIZE_DEGREE)) {
                    arSceneFx.setIntVal("Head Size Warp Strategy", 0x7FFFFFFF);

                    CustomBeautyHelper.get().addBuildInParam(fxDesc, new FxParams("Head Size Warp Strategy", 0x7FFFFFFF));
                } else if (fxInfo.getName().equals(FOREHEAD_HEIGHT_DEGREE)) {
                    arSceneFx.setIntVal("Forehead Height Warp Strategy", 0x7FFFFFFF);

                    CustomBeautyHelper.get().addBuildInParam(fxDesc, new FxParams("Forehead Height Warp Strategy", 0x7FFFFFFF));
                }
                Log.e("applyParam", "fxName = " + fxInfo.getFxName() + ",strength=" + fxInfo.getStrength());
                arSceneFx.setFloatVal(fxInfo.getFxName(), fxInfo.getStrength());

                CustomBeautyHelper.get().addBuildInParam(fxDesc, new FxParams(fxInfo.getFxName(), fxInfo.getStrength()));
            }
        }
    }

    private String getFxDesc(NvsFx videoFx) {
        if (videoFx instanceof NvsCaptureVideoFx) {
            NvsCaptureVideoFx captureVideoFx = (NvsCaptureVideoFx) videoFx;
            return captureVideoFx.getBuiltinCaptureVideoFxName();
        }
        return null;
    }


    /**
     * 应用拍摄锐度特技
     * Apply the Fx sharpness capture
     *
     * @param fxInfo info
     */
    public void applyCaptureSharpenFx(IFxInfo fxInfo) {
        applyBeautyFx(null, checkBuildCaptureSharpenFx(), fxInfo);
    }

    /**
     * 应用锐度特技
     * Apply sharpness effects
     *
     * @param videoClip clip
     * @param fxInfo    info
     */
    public void applySharpenFx(NvsVideoClip videoClip, IFxInfo fxInfo) {
        applyBeautyFx(videoClip, checkBuildClipSharpenFx(videoClip), fxInfo);
    }

    /**
     * 应用拍摄清晰度特技
     * Apply the Fx Definition capture
     *
     * @param fxInfo info
     */
    public void applyCaptureDefinitionFx(IFxInfo fxInfo) {
        applyBeautyFx(null, checkBuildCaptureDefinitionFx(), fxInfo);
    }

    /**
     * 应用清晰度特技
     * Apply Definition effects
     *
     * @param videoClip clip
     * @param fxInfo    info
     */
    public void applyDefinitionFx(NvsVideoClip videoClip, IFxInfo fxInfo) {
        applyBeautyFx(videoClip, checkBuildClipDefinitionFx(videoClip), fxInfo);
    }

    /**
     * 安装特效资源包
     * Install fx package
     *
     * @param packagePath the package path
     * @param packageType the package type
     */
    private String installFxPackage(String packagePath, int packageType) {
        return PackageManagerUtil.installAssetPackage(packagePath, null, packageType);
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
            case "capturescene":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE;
            case "arscene":
                return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE;
            default:
                return -1;
        }
    }

    public void applyBuildInCaptureFx(String key, Map<String, FxParams> paramList) {
        NvsCaptureVideoFx videoFx = getCaptureVideoFx(key);
        if (videoFx == null) {
            videoFx = mStreamingContext.appendBuiltinCaptureVideoFx(key);
        }
        if (null != videoFx) {
            if (DEFINITION.equals(key)) {
                videoFx.setBooleanVal("Fast Mode", true);
            }
            applyFxParam(paramList, videoFx);
        }
    }

    public void applyPackageCaptureFx(String key, Map<String, FxParams> paramList) {
        NvsCaptureVideoFx videoFx = getCapturePackageFx(key);
        if (videoFx == null) {
            return;
        }
        if (DEFINITION.equals(key)) {
            videoFx.setBooleanVal("Fast Mode", true);
        }
        applyFxParam(paramList, videoFx);
    }

    private static void applyFxParam(Map<String, FxParams> paramList, NvsCaptureVideoFx videoFx) {
        Set<Map.Entry<String, FxParams>> entries = paramList.entrySet();
        for (Map.Entry<String, FxParams> entry : entries) {
            FxParams param = entry.getValue();
            if (param != null) {
                Log.d(TAG, "applyBeautyFx:  key = " + param.key + ", value = " + param.value);
                if (FILTER_INTENSITY.equals(param.key) && param.value instanceof Float) {
                    videoFx.setFilterIntensity((Float) param.value);
                } else {
                    if (param.value instanceof Float) {
                        videoFx.setFloatVal(param.key, (Float) param.value);
                    } else if (param.value instanceof Double) {
                        videoFx.setFloatVal(param.key, (Double) param.value);
                    } else if (param.value instanceof String) {
                        videoFx.setStringVal(param.key, (String) param.value);
                    } else if (param.value instanceof Boolean) {
                        videoFx.setBooleanVal(param.key, (Boolean) param.value);
                    } else if (param.value instanceof Integer) {
                        videoFx.setIntVal(param.key, (Integer) param.value);
                    }
                }
            }
        }
    }
}
