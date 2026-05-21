package com.meicam.effectsdkdemo;


/**
 * Created by admin on 2018-5-29.
 */

public class Constants {
    /**
     * used model name
     * 模型相关
     */
    public static final String FACE_106_MODEL = "ms_face106_v4.0.1.next.model";
    public static final String FACE_240_MODEL = "ms_face240_v4.0.1.next.model";
    public static final String HUMAN_SEG_MODEL = "ms_humansegment_medium_v2.0.0.next.model";
    public static final String EYE_CONTOUR_MODEL = "ms_eyecontour_v2.0.0.next.model";
    public static final String AVATAR_MODEL = "ms_avatar_v2.0.0.next.model";
    public static final String FAKE_FACE_DAT = "fakeface.dat";
    public static final String FACE_COMMON_DAT = "facecommon_v1.0.1.dat";
    public static final String ADVANCED_BEAUTY_DAT = "advancedbeauty_v1.0.1.dat";
    // caption
    //普通字幕
    public static final int EDIT_MODE_CAPTION = 0;
    // sticker
    //贴纸
    public static final int EDIT_MODE_STICKER = 1;
    // water maker
    //水印
    public static final int EDIT_MODE_WATERMARK = 2;
    // theme caption
    //主题字幕
    public static final int EDIT_MODE_THEMECAPTION = 3;
    // compoundCaption
    //组合字幕
    public static final int EDIT_MODE_COMPOUND_CAPTION = 4;
    // transition
    //转场
    public static final int EDIT_MODE_TRANSITION = 5;
    // arScene effect
    //人脸特技
    public static final int EDIT_MODE_EFFECT_AR_SCENE = 6;
    // filter
    //滤镜
    public static final int EDIT_MODE_EFFECT_FILTER = 7;
    // background segmentation
    //背景分割
    public static final int EDIT_MODE_EFFECT_SEGMENT = 8;

    /**
     * Click duration in microseconds
     * 单击持续时间(微秒)
     */
    public final static int HANDCLICK_DURATION = 200;
    /*
     * touch移动距离，单位像素值
     * touch movement distance, unit pixel value
     * */
    public final static double HANDMOVE_DISTANCE = 10.0;

    // adjust property
    //调节属性待添加
    public final static String FX_COLOR_PROPERTY_DENOISE = "Denoise";
    public final static String FX_COLOR_PROPERTY_DEFINITION = "Definition";
    //Brightness, contrast, saturation, highlight, shadow, fade
    //亮度，对比度，饱和度，高光，阴影，褪色
    public final static String FX_COLOR_PROPERTY_BASIC = "BasicImageAdjust";
    //Degree of noise 0 ~ 1   0.5
    //噪点程度 0 ~ 1   0.5
    public final static String FX_ADJUST_KEY_INTENSITY = "Intensity";
    public final static String FX_ADJUST_KEY_EXPOSURE = "Exposure";
    //Highlight -1 ~ 1  0
    //高光 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_HIGHLIGHT = "Highlight";
    //Shadow -1 ~ 1  0
    //阴影 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_SHADOW = "Shadow";
    //Brightness -1 ~ 1  0
    //亮度 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_BRIGHTNESS = "Brightness";
    //Contrast -1 ~ 1  0
    //对比度 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_CONTRAST = "Contrast";
    //Blackpoint -10 ~ 10 0
    //褪色 -10 ~ 10 0
    public final static String FX_ADJUST_KEY_BLACKPOINT = "Blackpoint";
    //Saturation -1 ~ 1  0
    //饱和度 -1 ~ 1  0
    public final static String FX_ADJUST_KEY_SATURATION = "Saturation";
    public final static String FX_ADJUST_KEY_VIBRANCE = "Vibrance";
    public final static String FX_ADJUST_KEY_TINT = "Tint";
    public final static String FX_ADJUST_KEY_TEMPERATURE = "Temperature";
    /**
     * 暗角
     * Vignette
     */
    public final static String FX_VIGNETTE = "Vignette";
    public final static String FX_VIGNETTE_DEGREE = "Degree";
    /**
     * 锐度
     * Sharpen
     */
    public final static String FX_SHARPEN = "Sharpen";
    public final static String FX_SHARPEN_AMOUNT = "Amount";

    public static final String FX_AR_SCENE = "AR Scene";

    public static final int PREVIEW_HEIGHT = 720;
    public static final int PREVIEW_WIDTH = 1280;
}
