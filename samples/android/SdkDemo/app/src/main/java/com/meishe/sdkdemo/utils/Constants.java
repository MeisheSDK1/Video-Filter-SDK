package com.meishe.sdkdemo.utils;

import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.utils.asset.NvAsset;

/**
 * Created by admin on 2018-5-29.
 */

public class Constants {
    /**
     * sp中用到的key
     * key used in sp
     */
    public static final String KEY_PARAMTER = "paramter";
    /**
     * 用来标记层级
     * Used to mark hierarchies
     */
    public static final String KEY_LEVEL = "key_level";

    /**
     * 默认抠像背景capture scene路径
     * By default, capture scene path for the background
     */
    public static final String BG_SEGMENT_CAPTURE_SCENE_PATH = "assets:/capturescene/A2F05F58-87AB-4D1D-9609-6C00EF09E4D1/A2F05F58-87AB-4D1D-9609-6C00EF09E4D1.capturescene";
    public static final int EDIT_MODE_CAPTION = 0;
    public static final int EDIT_MODE_STICKER = 1;
    public static final int EDIT_MODE_WATERMARK = 2;
    public static final int EDIT_MODE_THEMECAPTION = 3;
    public static final int EDIT_MODE_COMPOUND_CAPTION = 4;
    public static final int EDIT_MODE_EFFECT = 5;

    public static final long NS_TIME_BASE = 1000000;
    public static final long US_TIME_BASE = 1000;
    public static final int MEDIA_TYPE_AUDIO = 1;

    public static final int ACTIVITY_START_CODE_MUSIC_SINGLE = 100;
    public static final int ACTIVITY_START_CODE_MUSIC_MULTI = 101;

    public static final String START_ACTIVITY_FROM_CAPTURE = "start_activity_from_capture";
    public static final String CAN_USE_ARFACE_FROM_MAIN = "can_use_arface_from_main";

    /*
     * 从主页面进入视频选择页面
     * Enter video selection page from main page
     * */
    public static final int FROMMAINACTIVITYTOVISIT = 1001;
    /*
     * 从片段编辑页面进入视频选择页面
     * Go to video selection page from clip editing page
     * */
    public static final int FROMCLIPEDITACTIVITYTOVISIT = 1002;
    /*
     * 从画中画面进入视频选择页面
     * Enter the video selection page from the picture-in-picture
     * */
    public static final int FROMPICINPICACTIVITYTOVISIT = 1003;
    /*
     * 从快速拼接进入素材选择页面
     * Enter the video selection page from the quick splicing
     * */
    public static final int FROM_QUICK_SPLICING_ACTIVITY = 1004;
    /*
     * 从快速拼接进入素材选择页面 只选择一个素材
     * Enter the video selection page from the quick splicing,only select one
     * */
    public static final int FROM_QUICK_SPLICING_ONLY_ONE_ACTIVITY = 1005;

    /**
     * 图片运动，区域显示
     * Picture movement, area display
     */
    public static final int EDIT_MODE_PHOTO_AREA_DISPLAY = 2001;
    /*
     * 图片运动，全图显示
     * Picture movement, full picture display
     * */
    public static final int EDIT_MODE_PHOTO_TOTAL_DISPLAY = 2002;

    /**
     * 自定义贴纸类型
     * Custom sticker type
     */
    public static final int CUSTOMSTICKER_EDIT_FREE_MODE = 2003;//Free
    public static final int CUSTOMSTICKER_EDIT_CIRCLE_MODE = 2004;//Circle
    public static final int CUSTOMSTICKER_EDIT_SQUARE_MODE = 2005;//Square

    /**
     * 无特效的ID
     * No effect ID
     */
    public static final String NO_FX = "None";

    /**
     * music
     */
    public static final String MUSIC_EXTRA_AUDIOCLIP = "extra";
    public static final String MUSIC_EXTRA_LAST_AUDIOCLIP = "extra_last";
    public static final long MUSIC_MIN_DURATION = 1000000;

    public static final String SELECT_MUSIC_FROM = "select_music_from";
    public static final String SELECT_MUSIC_HAVE = "is_have_music";
    public static final int SELECT_MUSIC_FROM_DOUVIDEO = 5001;
    public static final int SELECT_MUSIC_FROM_EDIT = 5002;
    public static final int SELECT_MUSIC_FROM_MUSICLYRICS = 5003;

    /**
     * 视音频音量值
     * <p>
     * Video and audio volume value
     */
    public static final float VIDEOVOLUME_DEFAULTVALUE = 1.0f;
    public static final float VIDEOVOLUME_MAXVOLUMEVALUE = 2.0f;
    public static final int VIDEOVOLUME_MAXSEEKBAR_VALUE = 100;

    /**
     * Click duration in microseconds
     */
    //
    public final static int HANDCLICK_DURATION = 200;
    /*
     * touch移动距离，单位像素值
     * touch movement distance, unit pixel value
     * */
    public final static double HANDMOVE_DISTANCE = 10.0;

    public final static String FX_TRANSFORM_2D = "Transform 2D";
    public final static String FX_TRANSFORM_2D_ROTATION = "Rotation";
    public final static String FX_TRANSFORM_2D_SCALE_X = "Scale X";
    public final static String FX_TRANSFORM_2D_SCALE_Y = "Scale Y";
    public final static String FX_TRANSFORM_2D_TRANS_X = "Trans X";
    public final static String FX_TRANSFORM_2D_TRANS_Y = "Trans Y";
    /**
     * Color Property 颜色属性
     * <p>
     * Color Property
     */
    public final static String FX_COLOR_PROPERTY = "Color Property";
    public final static String FX_COLOR_PROPERTY_BRIGHTNESS = "Brightness";
    public final static String FX_COLOR_PROPERTY_CONTRAST = "Contrast";
    public final static String FX_COLOR_PROPERTY_SATURATION = "Saturation";
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

    /**
     * color property 老旧不用了 使用以下的参数实现调节
     */
    public final static String ADJUST_TYPE_BASIC_IMAGE_ADJUST = "BasicImageAdjust";              //亮度，对比度，饱和度，高光，阴影，褪色
    public final static String ADJUST_TYPE_SHARPEN = "Sharpen";                                  //锐度
    public final static String ADJUST_TYPE_VIGETTE = "Vignette";                                 //暗角
    public final static String ADJUST_TYPE_TINT = "Tint";                                        //色温 色调
    public final static String ADJUST_TYPE_DENOISE = "Noise";                                  //噪点

    public final static String ADJUST_BRIGHTNESS = "Brightness";        //亮度 -1 ~ 1  0
    public final static String ADJUST_CONTRAST = "Contrast";            //对比度 -1 ~ 1  0
    public final static String ADJUST_SATURATION = "Saturation";        //饱和度 -1 ~ 1  0
    public final static String ADJUST_HIGHTLIGHT = "Highlight";         //高光 -1 ~ 1  0
    public final static String ADJUST_SHADOW = "Shadow";                //阴影 -1 ~ 1  0
    public final static String ADJUST_TEMPERATURE = "Temperature";      //色温 -1 ~ 1  0
    public final static String ADJUST_TINT = "Tint";                    //色调 -1 ~ 1  0
    public final static String ADJUST_FADE = "Blackpoint";              //褪色 -10 ~ 10 0
    public final static String ADJUST_DEGREE = "Degree";                 //暗角 0 - 1   0
    public final static String ADJUST_AMOUNT = "Amount";                 //锐度 0 ~ 5   1
    public final static String ADJUST_DENOISE = "Intensity";             //噪点程度 0 ~ 1   0.5
    public final static String ADJUST_DENOISE_DENSITY = "Density";       //噪点密度 0 ~ 1   0.5
    public final static String ADJUST_DENOISE_COLOR_MODE = "Grayscale";  //噪点单色彩色 true 单色  false 彩色


    public final static String TRANS_X = "Caption TransX";
    public final static String TRANS_Y = "Caption TransY";
    public final static String SCALE_X = "Caption ScaleX";
    public final static String SCALE_Y = "Caption ScaleY";
    public final static String ROTATION_Z = "Caption RotZ";
    public final static String TRACK_OPACITY = "Track Opacity";

    public static final String NOISE_SUPPRESSION_KEY = "Audio Noise Suppression";
    public static final String NOISE_SUPPRESSION_VALUE_KEY = "Level";

    public final static String[] CaptionColors = {
            "#ffffffff", "#ff000000", "#ffd0021b",
            "#ff4169e1", "#ff05d109", "#ff02c9ff",
            "#ff9013fe", "#ff8b6508", "#ffff0080",
            "#ff02F78E", "#ff00FFFF", "#ffFFD709",
            "#ff4876FF", "#ff19FF2F", "#ffDA70D6",
            "#ffFF6347", "#ff5B45AE", "#ff8B1C62",
            "#ff8B7500", "#ff228B22", "#ffC0FF3E",
            "#ff00BFFF", "#ffABABAB", "#ff6495ED",
            "#ff0000E3", "#ffE066FF", "#ffF08080"
    };

    public final static String[] FilterColors = {
            "#80d0021b", "#804169e1", "#8005d109",
            "#8002c9ff", "#809013fe", "#808b6508",
            "#80ff0080", "#8002F78E", "#8000FFFF",
            "#80FFD709", "#804876FF", "#8019FF2F",
            "#80DA70D6", "#80FF6347", "#805B45AE",
            "#808B1C62", "#808B7500", "#80228B22",
            "#80C0FF3E", "#8000BFFF", "#80ABABAB",
            "#806495ED", "#800000E3", "#80E066FF",
            "#80F08080"
    };

    /**
     * 素材下载状态值
     * Material download status value
     */
    public static final int ASSET_LIST_REQUEST_SUCCESS = 106;
    public static final int ASSET_LIST_REQUEST_FAILED = 107;
    public static final int ASSET_DOWNLOAD_SUCCESS = 108;
    public static final int ASSET_DOWNLOAD_FAILED = 109;
    public static final int ASSET_DOWNLOAD_INPROGRESS = 110;
    public static final int ASSET_DOWNLOAD_START_TIMER = 111;

    /**
     * 拍摄
     * <p>
     * Shoot
     */
    public static final int RECORD_TYPE_NULL = 3000;
    public static final int RECORD_TYPE_PICTURE = 3001;
    public static final int RECORD_TYPE_VIDEO = 3002;

    public static final String SELECT_MEDIA_FROM = "select_media_from"; // key
    /*
     *  从水印入口进入单个图片选择页面
     * Enter the single image selection page from the watermark entrance
     * */
    public static final int SELECT_IMAGE_FROM_WATER_MARK = 4001;
    /*
     * 从制作封面入口进入单个图片选择页面
     * Enter the single picture selection page from the production cover entrance
     * */
    public static final int SELECT_IMAGE_FROM_MAKE_COVER = 4002;
    /*
     * 从自定义贴纸入口进入单个图片选择页面
     * Go to the single picture selection page from the custom sticker entrance
     * */
    public static final int SELECT_IMAGE_FROM_CUSTOM_STICKER = 4003;
    /*
     * 从视频拍摄入口进入单个视频选择页面
     * Enter the single video selection page from the video shooting portal
     * */
    public static final int SELECT_VIDEO_FROM_DOUVIDEOCAPTURE = 4004;

    /*
     *  从翻转字幕页面入口进入视频选择选择视频
     * Select the video from the flip subtitle page entry
     * */
    public static final int SELECT_VIDEO_FROM_FLIP_CAPTION = 4005;
    /*
     * 从音乐歌词入口进入视频选择选择视频
     * Select video from music lyrics entry
     * */
    public static final int SELECT_VIDEO_FROM_MUSIC_LYRICS = 4006;

    /*
     * 从影集入口进入视频选择选择视频
     * Enter video from album entrance
     * */
    public static final int SELECT_VIDEO_FROM_PHOTO_ALBUM = 4007;
    /*
     * 从主页面进到选择视频页面，跳转到音频均衡器页面
     * Enter the video selection page from the main page to audio equalizer page
     * */
    public static final int FROM_MAIN_ACTIVITY_TO_AUDIO_EQUALIZER = 4008;

    /*
     * 从粒子入口进入视频选择页面
     * Enter the video selection page from the particle portal
     * */
    public static final int SELECT_VIDEO_FROM_PARTICLE = 4010;

    /*
     * 从背景页面进入单个图片选择页面
     * Enter the single image selection page from the background page
     * */
    public static final int SELECT_PICTURE_FROM_BACKGROUND = 4011;
    /*
     * 从背景抠像页面进入单个图片选择页面
     * Enter the single image selection page from the capture scene page
     * */
    public static final int SELECT_PICTURE_FROM_CAPTURE_SCENE = 4012;
    /*
     * 从序列嵌套入口进入视频选择选择视频
     * Select video from music lyrics entry
     * */
    public static final int SELECT_VIDEO_FROM_SEQUENCE_NESTING = 4013;

    /*
     * 从序列嵌套入口进入视频选择选择视频
     * Select video from music lyrics entry
     * */
    public static final int SELECT_PICTURE_FROM_BACKGROUND_SEG = 4014;

    public static final int POINT16V9 = NvAsset.AspectRatio_16v9;
    public static final int POINT1V1 = NvAsset.AspectRatio_1v1;
    public static final int POINT9V16 = NvAsset.AspectRatio_9v16;
    public static final int POINT3V4 = NvAsset.AspectRatio_3v4;
    public static final int POINT4V3 = NvAsset.AspectRatio_4v3;
    public static final int POINT21V9 = NvAsset.AspectRatio_21v9;
    public static final int POINT9V21 = NvAsset.AspectRatio_9v21;
    public static final int POINT18V9 = NvAsset.AspectRatio_18v9;
    public static final int POINT9V18 = NvAsset.AspectRatio_9v18;
    public static final int POINT7V6 = NvAsset.AspectRatio_7v6;
    public static final int POINT6V7 = NvAsset.AspectRatio_6v7;

    /**
     * 拍摄-美型
     * Shooting,Beauty
     */
    public static final double NORMAL_VELUE_INTENSITY_FORHEAD = 0.5;
    public static final double NORMAL_VELUE_INTENSITY_CHIN = 0.5;
    public static final double NORMAL_VELUE_INTENSITY_MOUTH = 0.5;
    /**
     * 拍摄,变焦、曝光
     * Shoot, zoom, exposure
     */
    public static final int CAPTURE_TYPE_ZOOM = 2;
    public static final int CAPTURE_TYPE_EXPOSE = 3;
    /**
     * 拍摄-最小录制时长
     * Shooting, minimum recording duration
     */
    public static final int MIN_RECORD_DURATION = 1000000;

    /**
     * 人脸类型
     * <p>
     * Face type
     */
    public static final int HUMAN_AI_TYPE_NONE = 0;//SDK regular version
    public static final int HUMAN_AI_TYPE_MS = 1;//SDK meishe face module
    public static final int HUMAN_AI_TYPE_FU = 2;//FU

    /**
     * 构建类型
     * Build type
     */
    public static final String BUILD_HUMAN_AI_TYPE_NONE = "NONE";//SDK regular version
    public static final String BUILD_HUMAN_AI_TYPE_MS_ST = "MS_ST";//SDK MS_ST
    public static final String BUILD_HUMAN_AI_TYPE_MS = "MS";//SDK MS
    public static final String BUILD_HUMAN_AI_TYPE_FU = "FaceU";//FU
    public static final String BUILD_HUMAN_AI_TYPE_MS_ST_SUPER = "MS_ST_SUPER";//SDK MS_ST 高级版

    public static final int MOSAIC = 1;
    public static final int BLUR = 2;
    public static final String KEY_CLIP_FILE_PATH = "clip_file_path";

    public static class AspectRatio {
        public static final int AspectRatio_NoFitRatio = 0;
        public static final int AspectRatio_16v9 = 1;
        public static final int AspectRatio_1v1 = 2;
        public static final int AspectRatio_9v16 = 4;
        public static final int AspectRatio_4v3 = 8;
        public static final int AspectRatio_3v4 = 16;
        public static final int AspectRatio_18v9 = 32;
        public static final int AspectRatio_9v18 = 64;
        public static final int AspectRatio_2d39v1 = 128;
        public static final int AspectRatio_2d55v1 = 256;
        public static final int AspectRatio_All = AspectRatio_16v9 | AspectRatio_1v1 | AspectRatio_9v16 |
                AspectRatio_3v4 | AspectRatio_4v3 | AspectRatio_18v9 | AspectRatio_9v18 | AspectRatio_2d39v1 |
                AspectRatio_2d55v1;
    }


    /**
     * 隐私对话框首次同意标识
     * Privacy dialog consent sign for the first time
     */
    public static final String KEY_AGREE_PRIVACY = "isAgreePrivacy";
    public static final String SERVICE_AGREEMENT_URL = "https://vsapi.meishesdk.com/app/privacy/service-agreement.html";
    public static final String PRIVACY_POLICY_URL = "https://vsapi.meishesdk.com/app/privacy/privacy-policy.html";
    //    public static final String LICENSE_FILE_URL = "https://vsapi.meishesdk.com" + "/api/authorization/ST/current?"+"appId=com.meishe.videoshow";
    public static final String LICENSE_FILE_URL = "https://vsapi.meishesdk.com" + "/api/authorization/ST/current?appId=" + MSApplication.getAppId();
    /**
     * shared util key
     */
    public static final String KEY_SHARED_START_TIMESTAMP = "start_timestamp";
    public static final String KEY_SHARED_END_TIMESTAMP = "end_timestamp";
    public static final String KEY_SHARED_AUTHOR_FILE_URL = "author_file_url";
    public static final String KEY_SHARED_AUTHOR_FILE_PATH = "author_file_path";
    public static final String HDR_EXPORT_CONFIG_NONE = "none";
    public static final String HDR_EXPORT_CONFIG_2084 = "st2084";
    public static final String HDR_EXPORT_CONFIG_HLG = "hlg";
    public static final String HDR_EXPORT_CONFIG_HDR10PLUS = "hdr10plus";
    public static final String HDR_EXPORT_HEVC = "hevc";

    /**
     * 音频调整
     * Audio adjustment
     */
    public static final String AUDIO_EQ = "Audio EQ";

    public static final String AR_SCENE = "AR Scene";
    public static final String AR_SCENE_ID_KEY = "Scene Id";
    public static final String AR_SCENE_FACE_CAMERA_FOVY_ID_KEY = "Face Camera Fovy";


    public static final String KEY_SEGMENT_TYPE = "Segment Type";

    public static final String KEY_SEGMENT_TEX_FILE_PATH = "Tex File Path";
    public static final String KEY_SEGMENT_BACKGROUND_COLOR = "Background Color";
    public static final String KEY_SEGMENT_STRETCH_MODE = "Stretch Mode";
    /**
     * 全身模型
     * Whole-body model
     */
    public static final String SEGMENT_TYPE_BACKGROUND = "Background";
    /**
     * 半身模型
     * Half model
     */
    public static final String SEGMENT_TYPE_HALF_BODY = "Half Body";
    /**
     * 天空模型
     * Sky model
     */
    public static final String SEGMENT_TYPE_SKY = "Sky";

    public static final String MAX_FACES_RESPECT_MIN = "Max Faces Respect Min";

    /**
     * 美颜
     * beauty
     */
    public static final String FRAGMENT_BEAUTY_TAG = "fragment_beauty_tag";

    /**
     * 滤镜
     * filter
     */
    public static final String FRAGMENT_FILTER_TAG = "fragment_filter_tag";
    /**
     * 道具
     * prop
     */
    public static final String FRAGMENT_PROP_TAG = "fragment_PROP_tag";
    /**
     * 组合字幕
     * Combined captioning
     */
    public static final String FRAGMENT_COMPONENT_CAPTION_TAG = "fragment_component_caption_tag";
    /**
     * 贴纸
     * sticker
     */
    public static final String FRAGMENT_STICKER_TAG = "fragment_sticker_tag";


    public static final int PAGE_SIZE = 20;

    public class SubscribeType {
        public static final String SUB_REMO_ALL_FILTER_TYPE = "removeAllFilterFx";
        public static final String SUB_APPLY_PROP_TYPE = "applyPropEffect";
        public static final String SUB_UN_USE_FILTER_TYPE = "unUseFilter";

        public static final String SUB_UN_USE_PROP_TYPE = "unUseProp";
        public static final String SUB_UN_SELECT_ITEM_TYPE = "un_select_item";
        public static final String SUB_APPLY_COMPONENT_CAPTION_TYPE = "applyCaption";
        public static final String SUB_APPLY_STICKER_TYPE = "applySticker";
        public static final String SUB_APPLY_FILTER_TYPE = "applyFilter";
        public static final String SUB_APPLY_FILTER_INFO_TYPE = "applyFilterInfo";
        public static final String SUB_ADD_CUSTOM_STICKER_TYPE = "onAddCustomSticker";
        public static final String SUB_APPLY_CUSTOM_STICKER_TYPE = "applyCustomSticker";

        public static final String SUB_REFRESH_DATA_TYPE = "refreshData";

        public static final String SUB_BEAUTY_SKIP_ITEM_CLICK_TYPE = "beautySkipItemClick";

        public static final String SUB_CAPTURE_SCENE_ITEM_CLICK_TYPE = "captureSceneItemClick";
        public static final String SUB_CAPTURE_SCENE_PACKAGE = "captureScenePackage";
        public static final String SUB_CHANGE_CROP = "changeCrop";
    }

    public static final String TITLE_SEARCH = "search";

    public static final String BG_SEG_EFFECT_ATTACH_KEY = "BgSegEffect";

    /**
     * Url material
     */
    public static final String URL_MATERIAL_KEY_FOR_RESULT = "url_material_for_result";
    public static final int URL_MATERIAL_VIDEO = 321;
    public static final int URL_MATERIAL_MUSIC = 322;
    public static final String URL_MATERIAL_TYPE = "url_material_type";
    public static final String URL_MATERIAL_MULTI = "url_material_multi";


}
