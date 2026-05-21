package com.meishe.base.constants;

import com.meicam.sdk.NvsVideoClip;

/**
 * The type Nvs constants.
 * Nvs常量类
 *
 * @author LiFei
 * @version 1.0
 * @title
 * @description 该类主要功能描述
 * @company 美摄
 * @created 2020 /11/24 19:44
 * @changeRecord [修改记录] <br/>
 */
public class NvsConstants {
    public static final int SIZE_4K_WIDTH = 3840;
    public static final int SIZE_4K_HEIGHT = 2160;

    /**
     * 人脸类型
     * Face type
     * SDK普通版
     * SDK Normal version
     */
    public static final int HUMAN_AI_TYPE_NONE = 0;
    /*
     * SDK meishe人脸模块
     * SDK meishe The face of a module
     * */
    public static final int HUMAN_AI_TYPE_MS = 1;

    public static final int HUMAN_AI_TYPE_FU = 2;//FU

    public static final int POINT16V9 = AspectRatio.AspectRatio_16v9;

    public static final int POINT1V1 = AspectRatio.AspectRatio_1v1;

    public static final int POINT9V16 = AspectRatio.AspectRatio_9v16;

    public static final int POINT3V4 = AspectRatio.AspectRatio_3v4;

    public static final int POINT4V3 = AspectRatio.AspectRatio_4v3;


    /**
     * The type Aspect ratio.
     * 纵横比类
     */
    public static class AspectRatio {
        /*
         * 不适配比例
         * Not in proportion
         *
         * */
        public static final int AspectRatio_NoFitRatio = 0;

        public static final int AspectRatio_16v9 = 1;

        public static final int AspectRatio_1v1 = 2;

        public static final int AspectRatio_9v16 = 4;

        public static final int AspectRatio_4v3 = 8;
        public static final int AspectRatio_3v4 = 16;
        public static final int AspectRatio_All = AspectRatio_16v9 | AspectRatio_1v1 | AspectRatio_9v16 | AspectRatio_3v4 | AspectRatio_4v3;
    }

    /**
     * The type Video clip type.
     * 视频类型
     */
    public static class VideoClipType {

        public static final int VIDEO_CLIP_TYPE_AV = NvsVideoClip.VIDEO_CLIP_TYPE_AV;

        public static final int VIDEO_CLIP_TYPE_IMAGE = NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE;
    }

    /**
     * 马赛克
     * mosaic
     */
    public static final String MOSAICNAME = "Mosaic";
    /**
     * 高斯模糊
     * Gaussian Blur
     */
    public static final String BLURNAME = "Gaussian Blur";

    /**
     * Transform 2D
     * 二维
     */
    public final static String FX_TRANSFORM_2D = "Transform 2D";

    public final static String FX_TRANSFORM_2D_ROTATION = "Rotation";

    public final static String FX_TRANSFORM_2D_SCALE_X = "Scale X";

    public final static String FX_TRANSFORM_2D_SCALE_Y = "Scale Y";

    public final static String FX_TRANSFORM_2D_TRANS_X = "Trans X";

    public final static String FX_TRANSFORM_2D_TRANS_Y = "Trans Y";

    /**
     * Color Property
     * 颜色属性
     */
    public final static String FX_COLOR_PROPERTY = "Color Property";

    public final static String FX_COLOR_PROPERTY_BRIGHTNESS = "Brightness";

    public final static String FX_COLOR_PROPERTY_CONTRAST = "Contrast";

    public final static String FX_COLOR_PROPERTY_SATURATION = "Saturation";

    /**
     * Vignette
     * 暗角
     */
    public final static String FX_VIGNETTE = "Vignette";

    public final static String FX_VIGNETTE_DEGREE = "Degree";

    /**
     * Sharpen
     * 锐度
     */
    public final static String FX_SHARPEN = "Sharpen";

    public final static String FX_SHARPEN_AMOUNT = "Amount";


    /**
     * 调节
     * adjust
     * 亮度，对比度，饱和度，高光，阴影，褐色
     * Brightness, contrast, saturation, highlight, shadow, brown
     */
    public final static String ADJUST_TYPE_BASIC_IMAGE_ADJUST = "BasicImageAdjust";
    /*
     * 亮度
     *  brightness
     * */
    public final static String ADJUST_BRIGHTNESS = "Brightness";
    /*
     * 对比度
     * contrast
     * */
    public final static String ADJUST_CONTRAST = "Contrast";
    /*
     * 饱和度
     * saturability
     * */
    public final static String ADJUST_SATURATION = "Saturation";
    /*
     * 高光
     * highlight
     * */
    public final static String ADJUST_HIGHTLIGHT = "Highlight";
    /*
     * 阴影
     * shadow
     * */
    public final static String ADJUST_SHADOW = "Shadow";
    /*
     * 褐色
     * brownness
     * */
    public final static String ADJUST_BLACKPOINT = "Blackpoint";
    /*
     * 色调
     * tinge
     * */
    public final static String ADJUST_TINT = "Tint";
    /*
     * 色温
     * color temperature
     * */
    public final static String ADJUST_TEMPERATURE = "Temperature";
    /*
     * 锐度
     * acutance
     * */
    public final static String ADJUST_AMOUNT = "Amount";
    /*
     * 暗角
     * Vignetting
     * */
    public final static String ADJUST_DEGREE = "Degree";


    /**
     * 背景属性特效
     * Background attribute effects
     * 属性
     * property
     */
    public final static String PROPERTY_FX = "property";
    /*
     * 背景模式
     * background mode
     * */
    public final static String KEY_BACKGROUND_MODE = "Background Mode";
    /*
     * 背景模式，颜色属性
     * Background mode, color properties
     * */
    public final static String VALUE_COLOR_BACKGROUND_MODE = "Color Solid";
    /*
     * 背景模式，模糊属性
     * Background mode, fuzzy attribute
     * */
    public final static String VALUE_BLUR_BACKGROUND_MODE = "Blur";
    /*
     * 设置颜色 key
     * Set the color key
     * */

    public final static String KEY_BACKGROUND_COLOR = "Background Color";
    /*
     * 设置模糊程度 key
     * Set the blur level key
     * */
    public final static String KEY_BACKGROUND_BLUR_RADIUS = "Background Blur Radius";

    public final static String KEY_BACKGROUND_ROTATION = "Enable Background Rotation";
    public final static String KEY_BACKGROUND_MUTLISAMPLE = "Enable MutliSample";
    public final static String PROPERTY_OPACITY = "Opacity";

    public final static String POST_PACKAGE_ID = "Post Package Id";
    public final static String IS_POST_STORY_BOARD_3D = "Is Post Storyboard 3D";
    public final static String PACKAGE_EFFECT_IN = "Package Effect In";
    public final static String PACKAGE_EFFECT_OUT = "Package Effect Out";

    //动画相关 Animation correlation
    public final static String ANIMATION_POST_PACKAGE_ID = "Post Package Id";
    public final static String ANIMATION_PACKAGE_ID = "Package Id";
    public final static String ANIMATION_IS_POST_STORYBOARD_3D = "Is Storyboard 3D";
    //背景，锯齿 Background, sawtooth
    public final static String ANIMATION_ENABLE_MUTLISAMPLE = "Enable MutliSample";
    public final static String ANIMATION_PACKAGE_EFFECT_IN = "Package Effect In";
    public final static String ANIMATION_PACKAGE_EFFECT_OUT = "Package Effect Out";
    public final static String ANIMATION_AMPLITUDE = "amplitude";
    //蒙版相关 Mask
    public final static String KEY_MASK_GENERATOR = "Mask Generator";
    public final static String KEY_MASK_GENERATOR_TYPE = "Mask_TYPE";
    public final static String KEY_MASK_GENERATOR_SIGN_MASK = "MASK";

    public final static String KEY_MASK_LUT = "Lut";
    //设置忽略背景 Setting Ignore background
    public final static String KEY_MASK_KEEP_RGB = "Keep RGB";
    public final static String KEY_MASK_STORYBOARD_DESC = "Text Mask Description String";
    //设置区域反转 Set zone inversion
    public final static String KEY_MASK_INVERSE_REGION = "Inverse Region";
    //设置蒙版区域 Set the mask area
    public final static String KEY_MASK_REGION_INFO = "Region Info";
    //设置羽化值 Set the feather value
    public final static String KEY_MASK_FEATHER_WIDTH = "Feather Width";


    //属性特技相关 Property effects
    public final static String PROPERTY_KEY_SCALE_X = "Scale X";
    public final static String PROPERTY_KEY_SCALE_Y = "Scale Y";
    public final static String PROPERTY_KEY_ROTATION = "Rotation";
    public final static String PROPERTY_KEY_TRANS_X = "Trans X";
    public final static String PROPERTY_KEY_TRANS_Y = "Trans Y";
    public final static String PROPERTY_KEY_BACKGROUND_MODE = "Background Mode";
    public final static String PROPERTY_KEY_BACKGROUND_COLOR = "Background Color";
    public final static String PROPERTY_KEY_BACKGROUND_IMAGE = "Background Image";
    public final static String PROPERTY_KEY_BACKGROUND_BLUR_RADIUS = "Background Blur Radius";
    public final static String PROPERTY_VALUE_BACKGROUND_COLOR_SOLID = "Color Solid";
    public final static String PROPERTY_VALUE_BACKGROUND_IMAGE_FILE = "Image File";
    public final static String PROPERTY_VALUE_BACKGROUND_BLUR = "Blur";
    public final static String PROPERTY_VALUE_BACKGROUND_BLUR_NEW_MODE_ENABLE = "Background Blur New Mode Enable";

    //裁剪相关 crop
    public final static String CUT_KEY_IS_NORMALIZED_COORD = "Is Normalized Coord";
    public final static String CUT_KEY_MASK_GENERATOR = "Mask Generator";
    public final static String CUT_KEY_MASK_GENERATOR_TRANSFORM_2D = "Transform 2D";
    public final static String CUT_KEY_MASK_GENERATOR_SCALE_X = "Scale X";
    public final static String CUT_KEY_MASK_GENERATOR_SCALE_Y = "Scale Y";
    public final static String CUT_KEY_MASK_GENERATOR_ROTATION_Z = "Rotation";
    public final static String CUT_KEY_MASK_GENERATOR_TRANS_X = "Trans X";
    public final static String CUT_KEY_MASK_GENERATOR_TRANS_Y = "Trans Y";
    public final static String CUT_KEY_MASK_GENERATOR_KEEP_RGB = "Keep RGB";
    public final static String CUT_KEY_MASK_GENERATOR_INVERSE_REGION = "Inverse Region";
    public final static String CUT_KEY_MASK_GENERATOR_REGION_INFO = "Region Info";
    public final static String CUT_KEY_MASK_GENERATOR_FEATHER_WIDTH = "Feather Width";
    public final static String CUT_FX_TYPE_FOR_EXTRA_TRANSFORM = "CutForExtraTransform";
    public final static String CUT_FX_TYPE_FOR_TRANSFORM = "CutForTransform";

    /**
     * Cropper extra
     * 新的裁剪特效
     */
    public static final String SUB_TYPE_CROPPER_EXT = "cropper_ext";
    public final static String KEY_MASK_GENERATOR_SIGN_CROP = "CROP";
    public final static String KEY_CROP_FULL_TRANSFORM = "Transform";
    public final static String VALUE_CROP_FULL_TRANSFORM = "Full Transform";

    public static class Crop {
        public static final String NAME = "Crop";
        public static final String BOUNDING_LEFT = "Bounding Left";
        public static final String BOUNDING_RIGHT = "Bounding Right";
        public static final String BOUNDING_TOP = "Bounding Top";
        public static final String BOUNDING_BOTTOM = "Bounding Bottom";
    }

    public final static String KEY_CROPPER_TRANS_X = "Trans X";
    public final static String KEY_CROPPER_TRANS_Y = "Trans Y";
    public final static String KEY_CROPPER_SCALE_X = "Scale X";
    public final static String KEY_CROPPER_SCALE_Y = "Scale Y";
    public final static String KEY_CROPPER_ROTATION = "Rotation";
    public final static String KEY_CROPPER_ASSET_ASPECT_RATIO = "cropperAssetAspectRatio";
    public final static String KEY_CROPPER_RATIO = "cropperRatio";
    public final static String KEY_CROPPER_KEEP_RGB = "Keep RGB";
    public final static String KEY_CROPPER_REGION_INFO = "Region Info";
    public final static String KEY_CROPPER_IS_NORMALIZED_COORD = "Is Normalized Coord";

    //音频关键帧特效 Audio keyframe effects
    public final static String AUDIO_CLIP_KEY_AUDIO_VOLUME = "Audio Volume";
    public final static String AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN = "Left Gain";
    public final static String AUDIO_CLIP_KEY_AUDIO_VOLUME_RIGHT_GAIN = "Right Gain";

}
