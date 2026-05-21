package com.meishe.engine.constant;


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

        public static final int AspectRatio_18v9 = 32;
        public static final int AspectRatio_9v18 = 64;
        public static final int AspectRatio_21v9 = 512;
        public static final int AspectRatio_9v21 = 1024;
        public static final int AspectRatio_6v7 = 2048;
        public static final int AspectRatio_7v6 = 4096;
        public static final int AspectRatio_All = AspectRatio_16v9 | AspectRatio_1v1 | AspectRatio_9v16
                | AspectRatio_3v4 | AspectRatio_4v3 | AspectRatio_21v9 | AspectRatio_9v21 |
                AspectRatio_18v9 | AspectRatio_9v18 |AspectRatio_7v6 | AspectRatio_6v7 ;

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

    /*
     * Video Mode Key
     * Video Mode
     * */
    public final static String KEY_VIDEO_MODE = "Video Mode";


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
     * 背景模式，图片属性
     * Background mode, fuzzy attribute
     * */
    public final static String VALUE_IMAGE_BACKGROUND_MODE = "Image File";
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
    /**
     * 图片路径 key
     * Set the image path key
     */
    public final static String KEY_BACKGROUND_IMAGE_PATH = "Background Image";

    public final static String KEY_BACKGROUND_ROTATION = "Enable Background Rotation";  //背景旋转 Rotation
    public final static String KEY_BACKGROUND_MUTLISAMPLE = "Enable MutliSample"; //背景，锯齿 MutliSample
    public final static String PROPERTY_OPACITY = "Opacity"; // 透明度 Opacity

    public final static String POST_PACKAGE_ID = "Package Id";
    //适配 动画ID key  Adapt the animation ID
    public final static String POST_PACKAGE_ID_OLD = "Post Package Id";
    public final static String IS_POST_STORY_BOARD_3D = "Is Post Storyboard 3D";
    public final static String PACKAGE_EFFECT_IN = "Package Effect In";
    public final static String PACKAGE_EFFECT_OUT = "Package Effect Out";
    public final static String AMPLITUDE = "amplitude";
    //蒙版 Mask
    public final static String KEY_MASK_GENERATOR = "Mask Generator";
    //设置忽略背景 Setting Ignore background
    public final static String KEY_MASK_KEEP_RGB = "Keep RGB";
    //设置区域反转 Set zone inversion
    public final static String KEY_MASK_INVERSE_REGION = "Inverse Region";
    //设置蒙版区域 Set the mask area
    public final static String KEY_MASK_REGION_INFO = "Region Info";
    //设置羽化值 Set the feather value
    public final static String KEY_MASK_FEATHER_WIDTH = "Feather Width";

    public final static String KEY_MASK_TYPE = "maskType";
    public final static String KEY_MASK_HORIZONTAL_SCALE = "horizontalScale";
    public final static String KEY_MASK_VERTICAL_SCALE = "verticalScale";
    public final static String KEY_MASK_CORNER_RADIUS_RATE = "cornerRadiusRate";
    public final static String KEY_MASK_TRANS_X = "Trans X";
    public final static String KEY_MASK_TRANS_Y = "Trans Y";
    public final static String KEY_MASK_SCALE_X = "Scale X";
    public final static String KEY_MASK_SCALE_Y = "Scale Y";
    public final static String KEY_MASK_ROTATION = "Rotation";
    public final static String KEY_MASK_OPACITY = "Opacity";
    public final static String KEY_MASK_ANCHOR_X = "Anchor X";
    public final static String KEY_MASK_ANCHOR_Y = "Anchor Y";

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

    public final static String VALUE_CROPPER_FREE = "free";

}
