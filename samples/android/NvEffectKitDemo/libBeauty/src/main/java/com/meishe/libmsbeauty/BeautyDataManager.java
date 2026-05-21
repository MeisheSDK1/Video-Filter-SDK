package com.meishe.libmsbeauty;


import android.content.Context;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meishe.libmsbeauty.bean.BaseFxInfo;
import com.meishe.libmsbeauty.bean.IFxInfo;
import com.meishe.nveffectkit.beauty.NveBeautyBlurTypeEnum;
import com.meishe.nveffectkit.beauty.NveBeautyParams;
import com.meishe.nveffectkit.beauty.NveBeautyWhiteningTypeEnum;
import com.meishe.nveffectkit.microShape.NveMicroShapeTypeEnum;
import com.meishe.nveffectkit.shape.NveShapeTypeEnum;

import java.util.ArrayList;
import java.util.List;

import static com.meishe.libmsbeauty.bean.BaseFxInfo.TYPE_BEAUTY_SHAPE;
import static com.meishe.libmsbeauty.bean.BaseFxInfo.TYPE_PLACE_HOLDER;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2022/8/17 16:49
 * @Description :美颜数据管理者 Beauty data manager
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyDataManager {
    /**
     * 获取美颜信息列表
     * Get beauty info list
     *
     * @param context the context
     */
    public static List<IFxInfo> getBeautyList(Context context, boolean supportQuyouguang) {
        List<IFxInfo> beautyList = new ArrayList<>();
        /*
         * 磨皮
         * Buffing kin
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.strength))
                .setResourceId(R.mipmap.ic_buffing)
                .setFxName(NveBeautyParams.BEAUTY_STRENGTH));
        /*
         * 美白
         * whitening
         * */
        beautyList.add(getWhiteningA(context));

        /*
         * 去油光
         * reddening
         * */
        if (supportQuyouguang) {
            beautyList.add(new BaseFxInfo()
                    .setName(context.getResources().getString(R.string.quyouguang))
                    .setDefaultStrength(0.44)
                    .setResourceId(R.drawable.beauty_quyouguang_selector)
                    .setFxName(NveBeautyParams.ADVANCED_BEAUTY_MATTE_INTENSITY));
        }
        /*
         * 红润
         * reddening
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.ruddy))
                .setResourceId(R.drawable.beauty_reddening_selector)
                .setFxName(NveBeautyParams.WHITENING_REDDENING));
        /*
         * 清晰度
         * sharpness
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.definition))
                .setResourceId(R.drawable.beauty_sharpen_selector)
                .setFxName(NveBeautyParams.FX_DEFINITION));
        /*
         * 锐度
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.sharpness))
                .setResourceId(R.drawable.beauty_adjust_selector)
                .setFxName(NveBeautyParams.SHARPEN));
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
         * Buffing kin
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.strength_1))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(NveBeautyBlurTypeEnum.BuffingSkin.toString()));
        /*
         * 高级磨皮1
         * Advanced Buffing kin 1
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.advanced_strength_1))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setDefaultStrength(0.8f)
                .setFxName(NveBeautyBlurTypeEnum.AdvancedBuffingSkin1.toString()));
        /*
         * 高级磨皮2
         * Advanced Buffing kin 2
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.advanced_strength_2))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(NveBeautyBlurTypeEnum.AdvancedBuffingSkin2.toString()));
        /*
         *高级磨皮3
         * Advanced Buffing kin 3
         * */
        beautyList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.advanced_strength_3))
                .setResourceId(R.drawable.beauty_strength_selector)
                .setFxName(NveBeautyBlurTypeEnum.AdvancedBuffingSkin3.toString()));
        /*
         *高级磨皮4
         * Advanced Buffing kin 4
         * */
        int cpuLevel = NvsEffectSdkContext.getInstance().getDeviceCpuLevel();
        if (cpuLevel == NvsEffectSdkContext.DEVICE_CPU_POWER_LEVEL_HIGH) {
            beautyList.add(new BaseFxInfo()
                    .setName(context.getResources().getString(R.string.advanced_strength_4))
                    .setResourceId(R.drawable.beauty_strength_selector)
                    .setFxName(NveBeautyBlurTypeEnum.AdvancedBuffingSkin4.toString()));
        }

        /*
         * 点
         * */
        beautyList.add(getPoint(context));
        return beautyList;
    }

    /**
     * 获取美白A的信息
     * Get whitening a info
     *
     * @param context the context
     */
    public static IFxInfo getWhiteningA(Context context) {
        return new BaseFxInfo()
                .setName(context.getResources().getString(R.string.whitening_A))
                .setResourceId(R.drawable.beauty_white_selector)
                .setFxName(NveBeautyWhiteningTypeEnum.WhiteningA.toString());
    }

    /**
     * 获取美白B的信息
     * Get whitening a info
     *
     * @param context the context
     */
    public static IFxInfo getWhiteningB(Context context) {
        return new BaseFxInfo()
                .setName(context.getResources().getString(R.string.whitening_B))
                .setResourceId(R.drawable.beauty_white_selector)
                .setFxName(NveBeautyWhiteningTypeEnum.WhiteningB.toString());
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
        /*
         * 窄脸
         * face width
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/96550C89-A5B8-42F0-9865-E07263D0B20C.3.facemesh")
                .setPackageId("96550C89-A5B8-42F0-9865-E07263D0B20C")
                .setName(context.getResources().getString(R.string.face_thin))
                .setResourceId(R.drawable.beauty_narrow_face_selector)
                .setFxName(NveShapeTypeEnum.FACE_WIDTH.toString()));

        /*
         * 小脸
         * face length
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/B85D1520-C60F-4B24-A7B7-6FEB0E737F15.3.facemesh")
                .setPackageId("B85D1520-C60F-4B24-A7B7-6FEB0E737F15")
                .setName(context.getResources().getString(R.string.face_small))
                .setResourceId(R.drawable.beauty_little_face_selector)
                .setStrength(0.4f)
                .setDefaultStrength(0.4f)
                .setFxName(NveShapeTypeEnum.FACE_LENGTH.toString()));

        /*
         * 瘦脸
         * Thin face
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/63BD3F32-D01B-4755-92D5-0DE361E4045A.3.facemesh")
                .setPackageId("63BD3F32-D01B-4755-92D5-0DE361E4045A")
                .setName(context.getResources().getString(R.string.cheek_thinning))
                .setResourceId(R.drawable.beauty_thin_face_selector)
                .setStrength(0.9f)
                .setDefaultStrength(0.9f)
                .setFxName(NveShapeTypeEnum.FACE_SIZE.toString()));

        shapeList.add(getPoint(context));

        /*
         * 额头
         * forehead
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/A351D77A-740D-4A39-B0EA-393643159D99.4.facemesh")
                .setPackageId("A351D77A-740D-4A39-B0EA-393643159D99")
                .setName(context.getResources().getString(R.string.intensity_forehead))
                .setResourceId(R.drawable.beauty_forehead_selector)
                .setFxName(NveShapeTypeEnum.FORE_HEAD.toString()));
        /*
         * 下巴
         * Chin
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/FF2D36C5-6C91-4750-9648-BD119967FE66.3.facemesh")
                .setPackageId("FF2D36C5-6C91-4750-9648-BD119967FE66")
                .setName(context.getResources().getString(R.string.intensity_chin))
                .setResourceId(R.drawable.beauty_chin_selector)
                .setFxName(NveShapeTypeEnum.CHIN.toString()));

        shapeList.add(getPoint(context));

        /*
         * 大眼
         * Eye enlarging
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/71C4CF51-09D7-4CB0-9C24-5DE9375220AE.3.facemesh")
                .setPackageId("71C4CF51-09D7-4CB0-9C24-5DE9375220AE")
                .setName(context.getResources().getString(R.string.eye_enlarging))
                .setResourceId(R.drawable.beauty_big_eye_selector)
                .setStrength(0.6f)
                .setDefaultStrength(0.6f)
                .setFxName(NveShapeTypeEnum.EYE_SIZE.toString()));

        /*
         * 眼角
         * Eye corner
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/B0B7A240-48B9-4983-B2C8-690FFA7211EB.2.facemesh")
                .setPackageId("B0B7A240-48B9-4983-B2C8-690FFA7211EB")
                .setName(context.getResources().getString(R.string.eye_corner))
                .setResourceId(R.drawable.beauty_eye_corner_selector)
                .setFxName(NveShapeTypeEnum.EYE_ANGLE.toString()));

        shapeList.add(getPoint(context));

        /*
         * 瘦鼻
         * Nose width
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/8D676A5F-73BD-472B-9312-B6E1EF313A4C.3.facemesh")
                .setPackageId("8D676A5F-73BD-472B-9312-B6E1EF313A4C")
                .setName(context.getResources().getString(R.string.intensity_nose))
                .setResourceId(R.drawable.beauty_thin_nose_selector)
                .setStrength(0.5f)
                .setDefaultStrength(0.5f)
                .setFxName(NveShapeTypeEnum.NOSE_WIDTH.toString()));

        /*
         * 长鼻
         * Nose LENGTH
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/3632E2FF-8760-4D90-A2B6-FFF09C117F5D.3.facemesh")
                .setPackageId("3632E2FF-8760-4D90-A2B6-FFF09C117F5D")
                .setName(context.getResources().getString(R.string.nose_long))
                .setResourceId(R.drawable.beauty_long_nose_selector)
                .setFxName(NveShapeTypeEnum.NOSE_LENGTH.toString()));

        shapeList.add(getPoint(context));
        /*
         * 嘴形
         * Mouth size
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/A80CC861-A773-4B8F-9CFA-EE63DB23EEC2.3.facemesh")
                .setPackageId("A80CC861-A773-4B8F-9CFA-EE63DB23EEC2")
                .setName(context.getResources().getString(R.string.intensity_mouth))
                .setResourceId(R.drawable.beauty_mouth_selector)
                .setFxName(NveShapeTypeEnum.MOUTH_SIZE.toString()));

        /*
         * 嘴角
         * Mouth corner
         * */
        shapeList.add(new BaseFxInfo()
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/CD69D158-9023-4042-AEAD-F8E9602FADE9.3.facemesh")
                .setPackageId("CD69D158-9023-4042-AEAD-F8E9602FADE9")
                .setName(context.getResources().getString(R.string.mouse_corner))
                .setResourceId(R.drawable.beauty_mouth_corner_selector)
                .setFxName(NveShapeTypeEnum.MOUTH_CORNER.toString()));

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

        /*
         * 缩头 head size
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/316E3641-98BA-4E07-958E-9ED7D7F75E97.1.warp")
                .setPackageId("316E3641-98BA-4E07-958E-9ED7D7F75E97")
                .setName(context.getResources().getString(R.string.head_size))
                .setResourceId(R.drawable.beauty_shape_head_width_selector)
                .setFxName(NveMicroShapeTypeEnum.HEAD_SIZE.toString()));

        /*
         * 颧骨宽 cheekbone width
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/C1C83B8B-8086-49AC-8462-209E429C9B7A.3.facemesh")
                .setPackageId("C1C83B8B-8086-49AC-8462-209E429C9B7A")
                .setName(context.getResources().getString(R.string.malar_size))
                .setResourceId(R.drawable.beauty_shape_malar_selector)
                .setFxName(NveMicroShapeTypeEnum.CHEEKBONE_WIDTH.toString()));

        /*
         * 下颌宽 Jaw Width
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/E903C455-8E23-4539-9195-816009AFE06A.3.facemesh")
                .setPackageId("E903C455-8E23-4539-9195-816009AFE06A")
                .setName(context.getResources().getString(R.string.jaw_size))
                .setResourceId(R.drawable.beauty_shape_jaw_width_selector)
                .setFxName(NveMicroShapeTypeEnum.JAW_WIDTH.toString()));

        /*
         * 太阳穴宽 temple width
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/E4790833-BB9D-4EFC-86DF-D943BDC48FA4.3.facemesh")
                .setPackageId("E4790833-BB9D-4EFC-86DF-D943BDC48FA4")
                .setName(context.getResources().getString(R.string.temple_width))
                .setResourceId(R.drawable.beauty_shape_temple_width_selector)
                .setFxName(NveMicroShapeTypeEnum.TEMPLE_WIDTH.toString()));

        /*
         * 法令纹  nasolabial folds
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_nasolabial))
                .setResourceId(R.drawable.beauty_nasolabial_selector)
                .setFxName(NveMicroShapeTypeEnum.NASOLABIAL_FOLDS.toString()));
        /*
         * 黑眼圈 black eye
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_dark_circles))
                .setResourceId(R.drawable.beauty_dark_circles_selector)
                .setFxName(NveMicroShapeTypeEnum.DARK_CIRCLES.toString()));
        /*
         * 亮眼 Brighten Eyes
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_brighten_eye))
                .setResourceId(R.drawable.beauty_bright_eye_selector)
                .setFxName(NveMicroShapeTypeEnum.BRIGHTEN_EYES.toString()));
        /*
         * 美牙 Whiten Teeth
         * */
        shapeList.add(new BaseFxInfo()
                .setName(context.getResources().getString(R.string.beauty_tooth))
                .setResourceId(R.drawable.beauty_tooth_selector)
                .setFxName(NveMicroShapeTypeEnum.WHITEN_TEETH.toString()));

        /*
         * 眼角距离 From the corner of my eye
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/54B2B9B4-5A7A-484C-B602-39A4730115A0.4.facemesh")
                .setPackageId("69D5BADE-A363-4CE0-B269-F146A851932B")
                .setName(context.getResources().getString(R.string.eye_angel))
                .setResourceId(R.drawable.eye_angel_selector)
                .setFxName(NveMicroShapeTypeEnum.EYE_ANGLE_DEGREE.toString()));

        /*
         * 眼离 eye distance
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/80329F14-8BDB-48D1-B30B-89A33438C481.4.facemesh")
                .setPackageId("80329F14-8BDB-48D1-B30B-89A33438C481")
                .setName(context.getResources().getString(R.string.eye_distance))
                .setResourceId(R.drawable.eye_distance_selector)
                .setFxName(NveMicroShapeTypeEnum.EYE_DISTANCE.toString()));

        /*
         * 眼高度 Eye Height
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/46B1D78F-DF5D-455A-9F97-C01B6405718F.4.facemesh")
                .setPackageId("46B1D78F-DF5D-455A-9F97-C01B6405718F")
                .setName(context.getResources().getString(R.string.eye_height))
                .setResourceId(R.drawable.beauty_eye_height_selector)
                .setFxName(NveMicroShapeTypeEnum.EYE_HEIGHT.toString()));

        /*
         * 眼宽度 Eye Width
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/0605A846-200E-443F-B2FF-FE8339C9E571.facemesh")
                .setPackageId("0605A846-200E-443F-B2FF-FE8339C9E571")
                .setName(context.getResources().getString(R.string.eye_width))
                .setResourceId(R.drawable.beauty_eye_width_selector)
                .setFxName(NveMicroShapeTypeEnum.EYE_WIDTH.toString()));

        /*
         * 眼弧度 Eye Arc
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/BF71EA3E-E39E-4EFD-A30E-161C3D9E454D.4.facemesh")
                .setPackageId("BF71EA3E-E39E-4EFD-A30E-161C3D9E454D")
                .setName(context.getResources().getString(R.string.eye_arc))
                .setResourceId(R.drawable.beauty_eye_arc_selector)
                .setFxName(NveMicroShapeTypeEnum.EYE_ARC.toString()));

        /*
         * 眼上下 Eye Y Offset
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/57C0BDDF-E08B-48F0-95FF-7F5171A9E6DF.4.facemesh")
                .setPackageId("57C0BDDF-E08B-48F0-95FF-7F5171A9E6DF")
                .setName(context.getResources().getString(R.string.eye_y_offset))
                .setResourceId(R.drawable.beauty_eye_y_offset_selector)
                .setFxName(NveMicroShapeTypeEnum.EYE_Y_OFFSET.toString()));

        shapeList.add(getPoint(context));

        /*
         * 人中 philtrum
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/37552044-E743-4A60-AC6E-7AADBA1E5B3B.3.facemesh")
                .setPackageId("37552044-E743-4A60-AC6E-7AADBA1E5B3B")
                .setName(context.getResources().getString(R.string.philtrum_length))
                .setResourceId(R.drawable.philtrum_selector)
                .setFxName(NveMicroShapeTypeEnum.PHILTRUM.toString()));

        /*
         * 宽鼻梁 Nose Bridge Width
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/23A40970-CE6F-4684-AF57-F78A0CBB53D1.3.facemesh")
                .setPackageId("23A40970-CE6F-4684-AF57-F78A0CBB53D1")
                .setName(context.getResources().getString(R.string.nose_bridge))
                .setResourceId(R.drawable.nose_distance_selector)
                .setFxName(NveMicroShapeTypeEnum.NOSE_BRIDGE_WIDTH.toString()));

        /*
         * 鼻头 Nose Head
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/44E11F37-A4E5-44B5-8915-CA42B84F9F09.2.facemesh")
                .setPackageId("44E11F37-A4E5-44B5-8915-CA42B84F9F09")
                .setName(context.getResources().getString(R.string.nose_head))
                .setResourceId(R.drawable.beauty_nose_head_selector)
                .setFxName(NveMicroShapeTypeEnum.NOSE_HEAD.toString()));

        shapeList.add(getPoint(context));

        /*
         * 眉毛粗细 Eyebrow Thickness
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/C2045DCA-D8C5-4C50-B942-69F749E32E93.2.facemesh")
                .setPackageId("C2045DCA-D8C5-4C50-B942-69F749E32E93")
                .setName(context.getResources().getString(R.string.eyebrow_thickness))
                .setResourceId(R.drawable.beauty_eyebrow_thickness_selector)
                .setFxName(NveMicroShapeTypeEnum.EYEBROW_THICKNESS.toString()));

        /*
         * 眉角度 Eyebrow Angle
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/CC86C182-62D7-4F1D-AE9D-F5E4E99977A5.2.facemesh")
                .setPackageId("CC86C182-62D7-4F1D-AE9D-F5E4E99977A5")
                .setName(context.getResources().getString(R.string.eyebrow_angle))
                .setResourceId(R.drawable.beauty_eyebrow_angle_selector)
                .setFxName(NveMicroShapeTypeEnum.EYEBROW_ANGLE.toString()));

        /*
         * 眉上下 Eyebrow Y Offset
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/90C09073-225B-461D-8645-73CE7825BB33.2.facemesh")
                .setPackageId("90C09073-225B-461D-8645-73CE7825BB33")
                .setName(context.getResources().getString(R.string.eyebrow_y_offset))
                .setResourceId(R.drawable.beauty_eyebrow_y_offset_selector)
                .setFxName(NveMicroShapeTypeEnum.EYEBROW_Y_OFFSET.toString()));

        /*
         * 眉间距 Eyebrow X Offset
         * */
        shapeList.add(new BaseFxInfo()
                .setType(TYPE_BEAUTY_SHAPE)
                .setAssetPackagePath("assets:/beauty/shape/shapePackage/F77B5F0E-AF43-45DB-96BB-62419B9CECA8.2.facemesh")
                .setPackageId("F77B5F0E-AF43-45DB-96BB-62419B9CECA8")
                .setName(context.getResources().getString(R.string.eyebrow_x_offset))
                .setResourceId(R.drawable.beauty_eyebrow_x_offset_selector)
                .setFxName(NveMicroShapeTypeEnum.EYEBROW_X_OFFSET.toString()));
        return shapeList;
    }

    public static NveMicroShapeTypeEnum getNveMicroShapeTypeEnumByName(String name) {
        return NveMicroShapeTypeEnum.valueOf(name);
    }
}
