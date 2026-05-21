package com.meishe.base.constants;

/**
 * Created by admin on 2018-5-29.
 * 常量类
 * Constant class
 */

public class BaseConstants {
    public static final long TIME_BASE = 1000000L;
    public final static int TRACK_INDEX_MAIN = 0;
    public static final int MEDIA_TYPE_AUDIO = 1;

    /**
     * 资源下载状态
     * Resource download status
     */
    public static final int RES_STATUS_DOWNLOAD_NO = 0;
    public static final int RES_STATUS_DOWNLOADING = 1;
    public static final int RES_STATUS_DOWNLOAD_ALREADY = 2;
    public static final int RES_STATUS_DOWNLOAD_ERROR = 3;

    /**
     * 是否使用“速度补偿模式”
     * Whether to use "Speed Compensation Mode"
     */
    public static final boolean NEED_100_SPEED = false;

    /**
     * 是否使用美摄人脸
     * Whether to use "Ms Face"
     */
    public static final boolean USE_MS_FACE = true;

    public static final int EDIT_MODE_NONE = -1;
    public static final int EDIT_MODE_CAPTION = 0;
    public static final int EDIT_MODE_STICKER = 1;
    public static final int EDIT_MODE_WATERMARK = 2;
    public static final int EDIT_MODE_WATERMARK_EFFECT = 3;
    public static final int EDIT_MODE_THEME_CAPTION = 4;
    public static final int EDIT_MODE_COMPOUND_CAPTION = 5;


    /**
     * 水印类型
     * Watermark type
     */
    public static final int WATER = 0;
    public static final int MOSAIC = 1;
    public static final int BLUR = 2;
    public static final int NONE = 3;
    public static final int MOSAIC_NUM = 4;
    public static final int MOSAIC_DEGREE = 5;

    /**
     * 自定义贴纸
     * Custom sticker
     */
    public static final int CUSTOMSTICKER_EDIT_FREE_MODE = 2003;
    public static final int CUSTOMSTICKER_EDIT_CIRCLE_MODE = 2004;
    public static final int CUSTOMSTICKER_EDIT_SQUARE_MODE = 2005;

    /**
     * music
     * 音乐
     */
    public static final String MUSIC_EXTRA_AUDIOCLIP = "extra";
    public static final String MUSIC_EXTRA_LAST_AUDIOCLIP = "extra_last";
    public static final long MUSIC_MIN_DURATION = 1000000;

    public static final String SELECT_MUSIC_FROM = "select_music_from";
    public static final int SELECT_MUSIC_FROM_AUDIO = 5001;
    public static final int SELECT_MUSIC_FROM_EDIT = 5002;
    public static final int SELECT_MUSIC_FROM_MUSICLYRICS = 5003;

    /**
     * 屏幕点击常量定义
     * Click constant definition on the screen
     * 点击时长，单位微秒
     * Click duration, in microseconds
     */

    public final static int HAND_CLICK_DURATION = 200;
    /*
     * touch移动距离，单位像素值
     * touch Movement distance, unit pixel value
     * */

    public final static double HAND_MOVE_DISTANCE = 10.0;

    public static final long NS_TIME_BASE = 1000000;


    public static final String SELECT_MEDIA_FROM = "select_media_from";
    /*
     * 从主页面进入视频选择页面
     * Go to the video selection page from the home page
     * */
    public static final int FROMMAINACTIVITYTOVISIT = 1001;
    /*
     * 从水印入口进入单个图片选择页面
     * Enter the single image selection page from the watermark entry
     * */
    public static final int SELECT_IMAGE_FROM_WATER_MARK = 4001;
    /*
     * 从制作封面入口进入单个图片选择页面
     * Enter the single image selection page from the make cover entry
     * */
    public static final int SELECT_IMAGE_FROM_MAKE_COVER = 4002;
    /*
     * 从自定义贴纸入口进入单个图片选择页面
     * Enter the single image selection page from the custom sticker entry
     * */
    public static final int SELECT_IMAGE_FROM_CUSTOM_STICKER = 4003;
    /*
     * 从音频入口进入单个视频选择页面
     * Enter the single video selection page from the audio entry
     * */
    public static final int SELECT_VIDEO_FROM_AUDIO = 4004;

    /*
     * 从翻转字幕页面入口进入视频选择选择视频
     * Enter video from the flip subtitle page entry and select Video
     * */
    public static final int SELECT_VIDEO_FROM_FLIP_CAPTION = 4005;
    /*
     * 从音乐歌词入口进入视频选择选择视频
     * From the music lyrics entry to the video select Select Video
     * */
    public static final int SELECT_VIDEO_FROM_MUSIC_LYRICS = 4006;
    /*
     * 从画中画入口进入素材选择页
     * Enter the Material selection page from the picture-in-picture entry
     * */
    public static final int SELECT_MEDIA_FROM_PICTURE_IN_PICTURE = 4007;
    /*
     * 从图片背景入口进入素材选择页
     * Enter the Material selection page from the image background entry
     * */
    public static final int SELECT_IMAGE_FROM_IMAGE_BACKGROUND = 4008;


    public static final String MIDDLE_OPERATION_HEIGHT_KEY = "middleOperationHeight";
    public static final String RATIO_KEY = "ratio";
    public static final String PLAY_BAR_VISIBLE_KEY = "playBarVisible";

    public static final float PIP_MAX_ROTATION = 360.0f;
    public static final float ADSORB_DEGREE = 10.0f;
    public static final float RECT_NEARBY_DISTANCE = 30.0f;

    public static final int SLOPX = 30;
    public static final int START_ADSORB = 30;
    public static final int END_ADSORB = -30;


    public static final int maxVolumeProgress = 200;
    public static final float maxNvVolume = 8f;

    public static final boolean EnableRawFilterMaskRender = true;
    /*Common Caption*/
    public static final int TYPE_COMMON_CAPTION = 0;
    /*AI Caption*/
    public static final int TYPE_AI_CAPTION = 1;

}
