package com.meishe.arscene;

import android.content.Context;
import android.text.TextUtils;

import com.meicam.sdk.NvsStreamingContext;
import com.meishe.arscene.bean.BaseFxInfo;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.bean.LocalFxInfo;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.base.utils.FileIOUtils;
import com.meishe.base.utils.GsonUtils;
import com.meishe.utils.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.meishe.arscene.bean.BaseFxInfo.TYPE_BEAUTY_SHAPE;
import static com.meishe.arscene.bean.BaseFxInfo.TYPE_PLACE_HOLDER;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_ENABLE;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_INTENSITY;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_MATTE_FILL_RADIUS;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_MATTE_INTENSITY;
import static com.meishe.arscene.bean.FxParams.ADVANCED_BEAUTY_TYPE;
import static com.meishe.arscene.bean.FxParams.BEAUTY_STRENGTH;
import static com.meishe.arscene.bean.FxParams.BEAUTY_WHITENING;
import static com.meishe.arscene.bean.FxParams.BRIGHTEN_EYES;
import static com.meishe.arscene.bean.FxParams.CHEEKBONE_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.CHEEKBONE_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.DARK_CIRCLES;
import static com.meishe.arscene.bean.FxParams.EYEBROW_ANGLE_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYEBROW_ANGLE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYEBROW_THICKNESS_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYEBROW_THICKNESS_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYEBROW_X_OFFSET_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYEBROW_X_OFFSET_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYEBROW_Y_OFFSET_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYEBROW_Y_OFFSET_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_ANGLE_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_ANGLE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_ARC_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_ARC_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_CORNER_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_CORNER_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_DISTANCE_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_DISTANCE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_HEIGHT_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_HEIGHT_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_SIZE_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_SIZE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.EYE_Y_OFFSET_DEGREE;
import static com.meishe.arscene.bean.FxParams.EYE_Y_OFFSET_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.FACE_CHIN_LENGTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.FACE_CHIN_LENGTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.FACE_LENGTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.FACE_LENGTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.FACE_SIZE_DEGREE;
import static com.meishe.arscene.bean.FxParams.FACE_SIZE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.FACE_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.FACE_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.FOREHEAD_HEIGHT_DEGREE;
import static com.meishe.arscene.bean.FxParams.FOREHEAD_HEIGHT_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.HEAD_SIZE_DEGREE;
import static com.meishe.arscene.bean.FxParams.HEAD_SIZE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.JAW_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.JAW_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.MOUTH_CORNER_DEGREE;
import static com.meishe.arscene.bean.FxParams.MOUTH_CORNER_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.MOUTH_SIZE_DEGREE;
import static com.meishe.arscene.bean.FxParams.MOUTH_SIZE_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.NASOLABIAL_FOLDS;
import static com.meishe.arscene.bean.FxParams.NOSE_BRIDGE_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.NOSE_BRIDGE_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.NOSE_HEAD_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.NOSE_HEAD_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.NOSE_LENGTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.NOSE_LENGTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.NOSE_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.NOSE_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.PHILTRUM_LENGTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.PHILTRUM_LENGTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.TEMPLE_WIDTH_DEGREE;
import static com.meishe.arscene.bean.FxParams.TEMPLE_WIDTH_PACKAGE_ID;
import static com.meishe.arscene.bean.FxParams.WHITENING_LUT_ENABLE;
import static com.meishe.arscene.bean.FxParams.WHITENING_LUT_FILE;
import static com.meishe.arscene.bean.FxParams.WHITENING_REDDENING;
import static com.meishe.arscene.bean.FxParams.WHITEN_TEETH;
import static com.meishe.makeup.utils.DataConvertUtils.ADJUST_COLOR_PATH;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 16:49
 * @Description :美颜数据管理者 Beauty data manager
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyDataManager {
    private static final boolean IS_LOAD_LOCAL = true;

    /**
     * 获取美颜信息列表
     * Get beauty info list
     *
     * @param context the context
     */
    public static List<IFxInfo> getBeautyList(Context context, boolean isSupportMatte) {
        List<IFxInfo> beautyList = new ArrayList<>();
        /*
         * 磨皮
         * Buffing kin
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.strength))
                .setResourceId(R.mipmap.ic_strength));
        /*
         * 美白
         * whitening
         * */
        beautyList.add(getWhiteningB(context));

        if (isSupportMatte) {
            /*
             * 去油光
             * reddening
             * */
            beautyList.add(CompoundFxInfo.create()
                    .addParam(new FxParams(ADVANCED_BEAUTY_MATTE_FILL_RADIUS, 44.44 * 0.27 + 3)
                            .setDefaultValue(44.44))
                    .setName(context.getResources().getString(R.string.quyouguang))
                    .setResourceId(R.drawable.beauty_quyouguang_selector)
                    .setFxName(ADVANCED_BEAUTY_MATTE_INTENSITY)
                    .setDefaultStrength(0)
            );

        }

        /*
         * 红润
         * reddening
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.ruddy))
                .setResourceId(R.drawable.beauty_reddening_selector)
                .setFxName(WHITENING_REDDENING));
        /*
         * 校色
         * School color
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.correctionColor))
                .setResourceId(R.drawable.beauty_adjust_selector)
                .setAssetPackagePath(ADJUST_COLOR_PATH)
                .setPackageId("65521195-92A4-41CA-9DB5-6AB19C9321B5"));
        /*
         * 锐度
         * sharpness
         * */
        beautyList.add(CompoundFxInfo.create()
                //.addParam(new FxParams(DEFAULT_SHARPEN_ENABLE, true))
                .setName(context.getResources().getString(R.string.sharpness))
                .setResourceId(R.drawable.beauty_sharpen_selector));
        return beautyList;
    }

    /**
     * 获取磨皮信息列表
     * Get buffing skin info list
     *
     * @param context the context
     */
    public static List<IFxInfo> getBuffingSkin(Context context) {
        List<IFxInfo> beautyList = new ArrayList<>();
        /*
         * 磨皮
         * Buffing kinf
         * */
        beautyList.add(CompoundFxInfo.create()
                .addParam(new FxParams(ADVANCED_BEAUTY_INTENSITY, 0.0f))
                .addParam(new FxParams(ADVANCED_BEAUTY_TYPE, 0))
                .addParam(new FxParams(ADVANCED_BEAUTY_ENABLE, true))
                .setType(FxParams.SKINNING)
                .setName(context.getResources().getString(R.string.strength_1))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(BEAUTY_STRENGTH));
        /*
         * 高级磨皮1
         * Advanced Buffing kin 1
         * */
        beautyList.add(CompoundFxInfo.create()
                .addParam(new FxParams(BEAUTY_STRENGTH, 0.0f))
                .addParam(new FxParams(ADVANCED_BEAUTY_TYPE, 0))
                .addParam(new FxParams(ADVANCED_BEAUTY_ENABLE, true))
                .setType(FxParams.SKINNING)
                .setName(context.getResources().getString(R.string.advanced_strength_1))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(ADVANCED_BEAUTY_INTENSITY));
        /*
         * 高级磨皮2
         * Advanced Buffing kin 2
         * */
        beautyList.add(CompoundFxInfo.create()
                .addParam(new FxParams(BEAUTY_STRENGTH, 0.0f))
                .addParam(new FxParams(ADVANCED_BEAUTY_TYPE, 1))
                .addParam(new FxParams(ADVANCED_BEAUTY_ENABLE, true))
                .setType(FxParams.SKINNING)
                .setName(context.getResources().getString(R.string.advanced_strength_2))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(ADVANCED_BEAUTY_INTENSITY));
        /*
         *高级磨皮3
         * Advanced Buffing kin 3
         * */
        beautyList.add(CompoundFxInfo.create()
                .addParam(new FxParams(BEAUTY_STRENGTH, 0.0f))
                .addParam(new FxParams(ADVANCED_BEAUTY_TYPE, 2))
                .addParam(new FxParams(ADVANCED_BEAUTY_ENABLE, true))
                .setType(FxParams.SKINNING)
                .setName(context.getResources().getString(R.string.advanced_strength_3))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(ADVANCED_BEAUTY_INTENSITY));

        if (null != NvsStreamingContext.getInstance()) {
            int level = NvsStreamingContext.getInstance().getDeviceCpuLevel();
            if (level == NvsStreamingContext.DEVICE_POWER_LEVEL_HIGH) {
                beautyList.add(CompoundFxInfo.create()
                        .addParam(new FxParams(BEAUTY_STRENGTH, 0.0f))
                        .addParam(new FxParams(ADVANCED_BEAUTY_TYPE, 3))
                        .addParam(new FxParams(ADVANCED_BEAUTY_ENABLE, true))
                        .setType(FxParams.SKINNING)
                        .setName(context.getResources().getString(R.string.advanced_strength_4))
                        .setResourceId(R.drawable.beauty_strength_selector)
                        .setFxName(ADVANCED_BEAUTY_INTENSITY));
            }
        }
        /*
         * 点
         * */
        beautyList.add(getPoint(context));
        return beautyList;
    }

    /**
     * 获取美白信息列表
     * Get whitening info list
     *
     * @param context the context
     */
    public static List<IFxInfo> getWhiteningSkin(Context context) {
        List<IFxInfo> beautyList = new ArrayList<>();
        beautyList.add(getWhiteningA(context, R.drawable.beauty_strength_selector));
        beautyList.add(getWhiteningB(context, R.drawable.beauty_strength_selector));
        return beautyList;
    }

    /**
     * 获取肤色信息列表
     *
     * @param context context
     * @return List
     */
    public static List<IFxInfo> getSkin(Context context) {
        List<IFxInfo> skinList = new ArrayList<>();
        /*
         * 冷白
         * Cold white
         */
        skinList.add(new BaseFxInfo()
                .setType(FxParams.SKIN_COLOUR)
                .setName(context.getResources().getString(R.string.skin_cold_white))
                .setResourceId(R.drawable.beauty_skin_cold_selector)
                .setAssetPackagePath("assets:/beauty/skin/5B35D8D4-3E55-41D5-A65E-37A846EC5692/5B35D8D4-3E55-41D5-A65E-37A846EC5692.6.videofx")
                .setPackageId("5B35D8D4-3E55-41D5-A65E-37A846EC5692"));
        /*
         * 粉白
         * Pink and white
         */
        skinList.add(new BaseFxInfo()
                .setType(FxParams.SKIN_COLOUR)
                .setName(context.getResources().getString(R.string.skin_pink_white))
                .setResourceId(R.drawable.beauty_skin_pink_selector)
                .setAssetPackagePath("assets:/beauty/skin/B8FEAED8-4512-46F6-A791-1EDCB4211D6B/B8FEAED8-4512-46F6-A791-1EDCB4211D6B.6.videofx")
                .setPackageId("B8FEAED8-4512-46F6-A791-1EDCB4211D6B"));
        /*
         * 暖白
         * Warm white
         */
        skinList.add(new BaseFxInfo()
                .setType(FxParams.SKIN_COLOUR)
                .setName(context.getResources().getString(R.string.skin_warm_white))
                .setResourceId(R.drawable.beauty_skin_warm_selector)
                .setAssetPackagePath("assets:/beauty/skin/7D98116E-61D2-4101-AB64-3098DF88B2C5/7D98116E-61D2-4101-AB64-3098DF88B2C5.6.videofx")
                .setPackageId("7D98116E-61D2-4101-AB64-3098DF88B2C5"));
        /*
         * 美黑
         * Tanning
         */
        skinList.add(new BaseFxInfo()
                .setType(FxParams.SKIN_COLOUR)
                .setName(context.getResources().getString(R.string.skin_tanning))
                .setResourceId(R.drawable.beauty_skin_tanning_selector)
                .setAssetPackagePath("assets:/beauty/skin/3C83CF18-FF4A-4541-9FA4-F715B2C2D79C/3C83CF18-FF4A-4541-9FA4-F715B2C2D79C.4.videofx")
                .setPackageId("3C83CF18-FF4A-4541-9FA4-F715B2C2D79C"));

        return skinList;
    }

    /**
     * 获取美白A的信息
     * Get whitening a info
     *
     * @param context the context
     */
    public static IFxInfo getWhiteningA(Context context) {
        return getWhiteningA(context, R.drawable.beauty_white_selector);
    }

    /**
     * 获取美白A的信息
     * Get whitening a info
     *
     * @param context    the context
     * @param resourceId the resourceId
     */
    public static IFxInfo getWhiteningA(Context context, int resourceId) {
        return CompoundFxInfo.create()
                //.addParam(new FxParams(DEFAULT_BEAUTY_LUT_FILE, ""))
                .addParam(new FxParams(WHITENING_LUT_FILE, ""))
                .addParam(new FxParams(WHITENING_LUT_ENABLE, false))
                .setName(context.getResources().getString(R.string.whitening_A))
                .setType(BEAUTY_WHITENING)
                .setResourceId(resourceId)
                .setFxName(BEAUTY_WHITENING);
    }

    /**
     * 获取美白B的信息
     * Get whitening a info
     *
     * @param context the context
     */
    public static IFxInfo getWhiteningB(Context context) {
        return getWhiteningB(context, R.drawable.beauty_white_selector);
    }

    /**
     * 获取美白B的信息
     * Get whitening a info
     *
     * @param context    the context
     * @param resourceId the resourceId
     */
    public static IFxInfo getWhiteningB(Context context, int resourceId) {
        return CompoundFxInfo.create()
                .addParam(new FxParams(WHITENING_LUT_FILE, "assets:/capture/WhiteB.mslut"))
                //.addParam(new FxParams(WHITENING_LUT_FILE, "assets:/capture/filter.png"))
                .addParam(new FxParams(WHITENING_LUT_ENABLE, true))
                .setName(context.getResources().getString(R.string.whitening_B))
                .setType(FxParams.BEAUTY_WHITENING)
                .setResourceId(resourceId)
                .setFxName(BEAUTY_WHITENING);
    }

    /**
     * 获取占位点
     * Get point
     *
     * @param context the context
     */
    public static IFxInfo getPoint(Context context) {
        return new BaseFxInfo()
                .setType(TYPE_PLACE_HOLDER)
                .setName(context.getResources().getString(R.string.blackPoint));
    }

    /**
     * 获取美型信息列表
     * Get beauty shape info list
     *
     * @param context the context
     */
    public static List<IFxInfo> getBeautyShapeList(Context context) {
        List<IFxInfo> shapeList = new ArrayList<>();
        FxParams packageIdParam;

        //获取本地素材 Get local material
        getLocalRes(context, PathUtils.LOCAL_EFFECT_SHAPE, shapeList);

        /*
         * 窄脸
         * face width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(FACE_WIDTH_PACKAGE_ID, "96550C89-A5B8-42F0-9865-E07263D0B20C"))
                .setAssetPackagePath("assets:/beauty/shapePackage/96550C89-A5B8-42F0-9865-E07263D0B20C/96550C89-A5B8-42F0-9865-E07263D0B20C.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.face_thin))
                .setResourceId(R.drawable.beauty_narrow_face_selector)
                .setFxName(FACE_WIDTH_DEGREE));

        /*
         * 小脸
         * face length
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(FACE_LENGTH_PACKAGE_ID, "B85D1520-C60F-4B24-A7B7-6FEB0E737F15"))
                .setAssetPackagePath("assets:/beauty/shapePackage/B85D1520-C60F-4B24-A7B7-6FEB0E737F15/B85D1520-C60F-4B24-A7B7-6FEB0E737F15.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.face_small))
                .setResourceId(R.drawable.beauty_little_face_selector)
                .setFxName(FACE_LENGTH_DEGREE));

        /*
         * 瘦脸
         * Thin face
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(FACE_SIZE_PACKAGE_ID, "63BD3F32-D01B-4755-92D5-0DE361E4045A"))
                .setAssetPackagePath("assets:/beauty/shapePackage/63BD3F32-D01B-4755-92D5-0DE361E4045A/63BD3F32-D01B-4755-92D5-0DE361E4045A.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.cheek_thinning))
                .setResourceId(R.drawable.beauty_thin_face_selector)
                .setFxName(FACE_SIZE_DEGREE));

        shapeList.add(getPoint(context));

        /*
         * 额头
         * forehead
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(FOREHEAD_HEIGHT_PACKAGE_ID, "A351D77A-740D-4A39-B0EA-393643159D99"))
                .setAssetPackagePath("assets:/beauty/shapePackage/A351D77A-740D-4A39-B0EA-393643159D99/A351D77A-740D-4A39-B0EA-393643159D99.4.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.intensity_forehead))
                .setResourceId(R.drawable.beauty_forehead_selector)
                .setFxName(FOREHEAD_HEIGHT_DEGREE));
        /*
         * 下巴
         * Chin
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(FACE_CHIN_LENGTH_PACKAGE_ID, "FF2D36C5-6C91-4750-9648-BD119967FE66"))
                .setAssetPackagePath("assets:/beauty/shapePackage/FF2D36C5-6C91-4750-9648-BD119967FE66/FF2D36C5-6C91-4750-9648-BD119967FE66.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.intensity_chin))
                .setResourceId(R.drawable.beauty_chin_selector)
                .setFxName(FACE_CHIN_LENGTH_DEGREE));

        shapeList.add(getPoint(context));

        /*
         * 大眼
         * Eye enlarging
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_SIZE_PACKAGE_ID, "71C4CF51-09D7-4CB0-9C24-5DE9375220AE"))
                .setAssetPackagePath("assets:/beauty/shapePackage/71C4CF51-09D7-4CB0-9C24-5DE9375220AE/71C4CF51-09D7-4CB0-9C24-5DE9375220AE.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_enlarging))
                .setResourceId(R.drawable.beauty_big_eye_selector)
                .setFxName(EYE_SIZE_DEGREE));

        /*
         * 眼角
         * Eye corner
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_CORNER_PACKAGE_ID, "B0B7A240-48B9-4983-B2C8-690FFA7211EB"))
                .setAssetPackagePath("assets:/beauty/shapePackage/B0B7A240-48B9-4983-B2C8-690FFA7211EB/B0B7A240-48B9-4983-B2C8-690FFA7211EB.2.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_corner))
                .setResourceId(R.drawable.beauty_eye_corner_selector)
                .setFxName(EYE_CORNER_DEGREE));

        shapeList.add(getPoint(context));

        /*
         * 瘦鼻
         * Nose width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(NOSE_WIDTH_PACKAGE_ID, "8D676A5F-73BD-472B-9312-B6E1EF313A4C"))
                .setAssetPackagePath("assets:/beauty/shapePackage/8D676A5F-73BD-472B-9312-B6E1EF313A4C/8D676A5F-73BD-472B-9312-B6E1EF313A4C.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.intensity_nose))
                .setResourceId(R.drawable.beauty_thin_nose_selector)
                .setFxName(NOSE_WIDTH_DEGREE));

        /*
         * 长鼻
         * Nose LENGTH
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(NOSE_LENGTH_PACKAGE_ID, "3632E2FF-8760-4D90-A2B6-FFF09C117F5D"))
                .setAssetPackagePath("assets:/beauty/shapePackage/3632E2FF-8760-4D90-A2B6-FFF09C117F5D/3632E2FF-8760-4D90-A2B6-FFF09C117F5D.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.nose_long))
                .setResourceId(R.drawable.beauty_long_nose_selector)
                .setFxName(NOSE_LENGTH_DEGREE));

        shapeList.add(getPoint(context));
        /*
         * 嘴形
         * Mouth size
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(MOUTH_SIZE_PACKAGE_ID, "A80CC861-A773-4B8F-9CFA-EE63DB23EEC2"))
                .setAssetPackagePath("assets:/beauty/shapePackage/A80CC861-A773-4B8F-9CFA-EE63DB23EEC2/A80CC861-A773-4B8F-9CFA-EE63DB23EEC2.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.intensity_mouth))
                .setResourceId(R.drawable.beauty_mouth_selector)
                .setFxName(MOUTH_SIZE_DEGREE));

        /*
         * 嘴角
         * Mouth corner
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(MOUTH_CORNER_PACKAGE_ID, "CD69D158-9023-4042-AEAD-F8E9602FADE9"))
                .setAssetPackagePath("assets:/beauty/shapePackage/CD69D158-9023-4042-AEAD-F8E9602FADE9/CD69D158-9023-4042-AEAD-F8E9602FADE9.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.mouse_corner))
                .setResourceId(R.drawable.beauty_mouth_corner_selector)
                .setFxName(MOUTH_CORNER_DEGREE));

        return shapeList;
    }

    /**
     * 获取微整形信息列表
     * Get micro-plastic shape info list
     *
     * @param context the context
     */
    public static List<IFxInfo> getMicroPlasticList(Context context) {
        List<IFxInfo> shapeList = new ArrayList<>();
        FxParams packageIdParam;

        //获取本地素材 Get local material
        getLocalRes(context, PathUtils.LOCAL_EFFECT_MICRO_SHAPE, shapeList);

        /*
         * 缩头 head size
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(HEAD_SIZE_PACKAGE_ID, "316E3641-98BA-4E07-958E-9ED7D7F75E97"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/316E3641-98BA-4E07-958E-9ED7D7F75E97/316E3641-98BA-4E07-958E-9ED7D7F75E97.1.warp")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.head_size))
                .setResourceId(R.drawable.beauty_shape_head_width_selector)
                .setFxName(HEAD_SIZE_DEGREE));

        /*
         * 颧骨宽 cheekbone width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(CHEEKBONE_WIDTH_PACKAGE_ID, "C1C83B8B-8086-49AC-8462-209E429C9B7A"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/C1C83B8B-8086-49AC-8462-209E429C9B7A/C1C83B8B-8086-49AC-8462-209E429C9B7A.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.malar_size))
                .setResourceId(R.drawable.beauty_shape_malar_selector)
                .setFxName(CHEEKBONE_WIDTH_DEGREE));

        /*
         * 下颌宽 Jaw Width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(JAW_WIDTH_PACKAGE_ID, "E903C455-8E23-4539-9195-816009AFE06A"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/E903C455-8E23-4539-9195-816009AFE06A/E903C455-8E23-4539-9195-816009AFE06A.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.jaw_size))
                .setResourceId(R.drawable.beauty_shape_jaw_width_selector)
                .setFxName(JAW_WIDTH_DEGREE));

        /*
         * 太阳穴宽 temple width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(TEMPLE_WIDTH_PACKAGE_ID, "E4790833-BB9D-4EFC-86DF-D943BDC48FA4"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/E4790833-BB9D-4EFC-86DF-D943BDC48FA4/E4790833-BB9D-4EFC-86DF-D943BDC48FA4.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.temple_width))
                .setResourceId(R.drawable.beauty_shape_temple_width_selector)
                .setFxName(TEMPLE_WIDTH_DEGREE));

        /*
         * 法令纹  nasolabial folds
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_nasolabial))
                .setResourceId(R.drawable.beauty_nasolabial_selector)
                .setFxName(NASOLABIAL_FOLDS));
        /*
         * 黑眼圈 black eye
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_dark_circles))
                .setResourceId(R.drawable.beauty_dark_circles_selector)
                .setFxName(DARK_CIRCLES));
        /*
         * 亮眼 Brighten Eyes
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_brighten_eye))
                .setResourceId(R.drawable.beauty_bright_eye_selector)
                .setFxName(BRIGHTEN_EYES));
        /*
         * 美牙 Whiten Teeth
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_tooth))
                .setResourceId(R.drawable.beauty_tooth_selector)
                .setFxName(WHITEN_TEETH));

        shapeList.add(getPoint(context));

        /*
         * 眼角度 Eye Angle
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_ANGLE_PACKAGE_ID, "54B2B9B4-5A7A-484C-B602-39A4730115A0"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/54B2B9B4-5A7A-484C-B602-39A4730115A0/54B2B9B4-5A7A-484C-B602-39A4730115A0.4.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_angel))
                .setResourceId(R.drawable.eye_angel_selector)
                .setFxName(EYE_ANGLE_DEGREE));

        /*
         * 眼距 eye distance
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_DISTANCE_PACKAGE_ID, "80329F14-8BDB-48D1-B30B-89A33438C481"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/80329F14-8BDB-48D1-B30B-89A33438C481/80329F14-8BDB-48D1-B30B-89A33438C481.4.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_distance))
                .setResourceId(R.drawable.eye_distance_selector)
                .setFxName(EYE_DISTANCE_DEGREE));

        /*
         * 眼高度 Eye Height
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_HEIGHT_PACKAGE_ID, "46B1D78F-DF5D-455A-9F97-C01B6405718F"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/46B1D78F-DF5D-455A-9F97-C01B6405718F/46B1D78F-DF5D-455A-9F97-C01B6405718F.4.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_height))
                .setResourceId(R.drawable.beauty_eye_height_selector)
                .setFxName(EYE_HEIGHT_DEGREE));

        /*
         * 眼宽度 Eye Width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_WIDTH_PACKAGE_ID, "0605A846-200E-443F-B2FF-FE8339C9E571"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/0605A846-200E-443F-B2FF-FE8339C9E571/0605A846-200E-443F-B2FF-FE8339C9E571.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_width))
                .setResourceId(R.drawable.beauty_eye_width_selector)
                .setFxName(EYE_WIDTH_DEGREE));

        /*
         * 眼弧度 Eye Arc
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_ARC_PACKAGE_ID, "BF71EA3E-E39E-4EFD-A30E-161C3D9E454D"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/BF71EA3E-E39E-4EFD-A30E-161C3D9E454D/BF71EA3E-E39E-4EFD-A30E-161C3D9E454D.4.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_arc))
                .setResourceId(R.drawable.beauty_eye_arc_selector)
                .setFxName(EYE_ARC_DEGREE));

        /*
         * 眼上下 Eye Y Offset
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYE_Y_OFFSET_PACKAGE_ID, "57C0BDDF-E08B-48F0-95FF-7F5171A9E6DF"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/57C0BDDF-E08B-48F0-95FF-7F5171A9E6DF/57C0BDDF-E08B-48F0-95FF-7F5171A9E6DF.4.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eye_y_offset))
                .setResourceId(R.drawable.beauty_eye_y_offset_selector)
                .setFxName(EYE_Y_OFFSET_DEGREE));

        shapeList.add(getPoint(context));

        /*
         * 人中 philtrum
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(PHILTRUM_LENGTH_PACKAGE_ID, "37552044-E743-4A60-AC6E-7AADBA1E5B3B"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/37552044-E743-4A60-AC6E-7AADBA1E5B3B/37552044-E743-4A60-AC6E-7AADBA1E5B3B.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.philtrum_length))
                .setResourceId(R.drawable.philtrum_selector)
                .setFxName(PHILTRUM_LENGTH_DEGREE));

        /*
         * 宽鼻梁 Nose Bridge Width
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(NOSE_BRIDGE_WIDTH_PACKAGE_ID, "23A40970-CE6F-4684-AF57-F78A0CBB53D1"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/23A40970-CE6F-4684-AF57-F78A0CBB53D1/23A40970-CE6F-4684-AF57-F78A0CBB53D1.3.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.nose_bridge))
                .setResourceId(R.drawable.nose_distance_selector)
                .setFxName(NOSE_BRIDGE_WIDTH_DEGREE));

        /*
         * 鼻头 Nose Head
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(NOSE_HEAD_WIDTH_PACKAGE_ID, "44E11F37-A4E5-44B5-8915-CA42B84F9F09"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/44E11F37-A4E5-44B5-8915-CA42B84F9F09/44E11F37-A4E5-44B5-8915-CA42B84F9F09.2.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.nose_head))
                .setResourceId(R.drawable.beauty_nose_head_selector)
                .setFxName(NOSE_HEAD_WIDTH_DEGREE));

        shapeList.add(getPoint(context));

        /*
         * 眉毛粗细 Eyebrow Thickness
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYEBROW_THICKNESS_PACKAGE_ID, "C2045DCA-D8C5-4C50-B942-69F749E32E93"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/C2045DCA-D8C5-4C50-B942-69F749E32E93/C2045DCA-D8C5-4C50-B942-69F749E32E93.2.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eyebrow_thickness))
                .setResourceId(R.drawable.beauty_eyebrow_thickness_selector)
                .setFxName(EYEBROW_THICKNESS_DEGREE));

        /*
         * 眉角度 Eyebrow Angle
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYEBROW_ANGLE_PACKAGE_ID, "CC86C182-62D7-4F1D-AE9D-F5E4E99977A5"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/CC86C182-62D7-4F1D-AE9D-F5E4E99977A5/CC86C182-62D7-4F1D-AE9D-F5E4E99977A5.2.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eyebrow_angle))
                .setResourceId(R.drawable.beauty_eyebrow_angle_selector)
                .setFxName(EYEBROW_ANGLE_DEGREE));

        /*
         * 眉上下 Eyebrow Y Offset
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYEBROW_Y_OFFSET_PACKAGE_ID, "90C09073-225B-461D-8645-73CE7825BB33"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/90C09073-225B-461D-8645-73CE7825BB33/90C09073-225B-461D-8645-73CE7825BB33.2.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eyebrow_y_offset))
                .setResourceId(R.drawable.beauty_eyebrow_y_offset_selector)
                .setFxName(EYEBROW_Y_OFFSET_DEGREE));

        /*
         * 眉间距 Eyebrow X Offset
         * */
        shapeList.add(CompoundFxInfo.create()
                .addParam(packageIdParam = new FxParams(EYEBROW_X_OFFSET_PACKAGE_ID, "F77B5F0E-AF43-45DB-96BB-62419B9CECA8"))
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shapePackage/F77B5F0E-AF43-45DB-96BB-62419B9CECA8/F77B5F0E-AF43-45DB-96BB-62419B9CECA8.2.facemesh")
                .setPackageId(packageIdParam.getStringValue())
                .setName(context.getResources().getString(R.string.eyebrow_x_offset))
                .setResourceId(R.drawable.beauty_eyebrow_x_offset_selector)
                .setFxName(EYEBROW_X_OFFSET_DEGREE));

        return shapeList;
    }

    /**
     * 获取调整信息列表
     *
     * @param context context
     * @return list
     */
    public static List<IFxInfo> getAdjustList(Context context) {
        List<IFxInfo> adjustList = new ArrayList<>();
        /*
         * 校色
         * School color
         * */
        adjustList.add(new BaseFxInfo()
                .setType(FxParams.COLOR_CORRECT)
                .setName(context.getResources().getString(R.string.correctionColor))
                .setResourceId(R.drawable.beauty_adjust_selector)
                .setAssetPackagePath(ADJUST_COLOR_PATH)
                .setPackageId("65521195-92A4-41CA-9DB5-6AB19C9321B5"));
        /*
         * 锐度
         * sharpness
         * */
        adjustList.add(CompoundFxInfo.create()
                .setType(FxParams.SHARPEN)
                .setName(context.getResources().getString(R.string.sharpness))
                .setResourceId(R.drawable.beauty_sharpen_selector)
                .setFxName(FxParams.SHARPEN_AMOUNT));
        /*
         * 清晰度
         * definition
         */
        adjustList.add(CompoundFxInfo.create()
                .setType(FxParams.DEFINITION)
                .setName(context.getResources().getString(R.string.definition))
                .setResourceId(R.drawable.beauty_definition_selector)
                .setFxName(FxParams.DEFINITION_INTENSITY));
        return adjustList;
    }

    /**
     * 获取本地配置资源
     * get local configuration resources
     *
     * @param context   context
     * @param path      path
     * @param shapeList list
     */
    private static void getLocalRes(Context context, String path, List<IFxInfo> shapeList) {
        if (!IS_LOAD_LOCAL) {
            return;
        }
        String rootDirPath = PathUtils.getFolderDirPath(path);
        if (TextUtils.isEmpty(rootDirPath)) {
            return;
        }
        File rootFile = new File(rootDirPath);
        if (!rootFile.exists()) {
            return;
        }
        File[] files = rootFile.listFiles();
        if ((null == files) || (files.length == 0)) {
            return;
        }
        boolean isAddPoint = false;
        for (File childFile : files) {
            String packagePath = "";
            String jsonPath = "";
            File[] childFiles = childFile.listFiles();
            if ((null == childFiles) || (childFiles.length == 0)) {
                continue;
            }
            for (File file : childFiles) {
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(".facemesh") || filePath.endsWith(".warp")) {
                    packagePath = filePath;
                    continue;
                }
                if (filePath.endsWith(".json")) {
                    jsonPath = filePath;
                }
            }
            if (TextUtils.isEmpty(packagePath) || TextUtils.isEmpty(jsonPath)) {
                continue;
            }
            File jsonFile = new File(jsonPath);
            if (!jsonFile.exists()) {
                continue;
            }
            String jsonText = FileIOUtils.readFile2String(jsonPath);
            if (TextUtils.isEmpty(jsonText)) {
                continue;
            }
            LocalFxInfo infos = GsonUtils.fromJson(jsonText, LocalFxInfo.class);
            FxParams packageIdParam;
            shapeList.add(CompoundFxInfo.create()
                    .addParam(packageIdParam = new FxParams(infos.getFxId(), infos.getPackageId()))
                    .setAssetPackagePath(packagePath)
                    .setPackageId(packageIdParam.getStringValue())
                    .setName(infos.getName())
                    .setResourceId(R.drawable.beauty_adjust_selector)
                    .setFxName(infos.getFxName()));
            isAddPoint = true;
        }
        if (isAddPoint) {
            shapeList.add(getPoint(context));
        }
    }
}
