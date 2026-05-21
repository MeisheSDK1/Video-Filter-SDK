package com.meishe.sdkdemo.mimodemo.common;

/**
 * Created by admin on 2018-5-29.
 */

public class Constants {
    public static final float DEFAULT_SPEED_VALUE = 1f;
    public static final long BASE_MILLISECOND = 1000;
    /**
     * 默认时长比例值
     * Default duration ratio
     */
    public static final float DEFAULT_DURATION_RATIO = 1f;

    public static final int EDIT_MODE_CAPTION = 0;
    public static final int EDIT_MODE_STICKER = 1;
    public static final int EDIT_MODE_WATERMARK = 2;
    public static final int EDIT_MODE_THEMECAPTION = 3;
    public static final int EDIT_MODE_COMPOUND_CAPTION = 4;

    public static final long NS_TIME_BASE = 1000000;
    public static final long US_TIME_BASE = 1000;


    /**
     * 图片运动-区域显示
     * Picture motion - area display
     */
    public static final int EDIT_MODE_PHOTO_AREA_DISPLAY = 2001;
    /**
     * 图片运动-全图显示
     * Picture motion - full picture display
     */
    public static final int EDIT_MODE_PHOTO_TOTAL_DISPLAY = 2002;


    /**
     * 无特效的ID
     * No special effect ID
     */
    public static final String NO_FX = "None";

    /**
     * music
     */
    public static final String MUSIC_EXTRA_AUDIOCLIP = "extra";
    public static final String MUSIC_EXTRA_LAST_AUDIOCLIP = "extra_last";
    public static final long MUSIC_MIN_DURATION = 1000000;

    public static final String SELECT_MUSIC_FROM = "select_music_from";
    public static final int SELECT_MUSIC_FROM_DOUVIDEO = 5001;
    public static final int SELECT_MUSIC_FROM_EDIT = 5002;
    public static final int SELECT_MUSIC_FROM_MUSICLYRICS = 5003;

    /**
     * 视音频音量值
     * Audio and video volume value
     */
    public static final float VIDEOVOLUME_DEFAULTVALUE = 1.0f;
    public static final float VIDEOVOLUME_MAXVOLUMEVALUE = 2.0f;
    public static final int VIDEOVOLUME_MAXSEEKBAR_VALUE = 100;

    /**
     * 屏幕点击常量定义
     * Screen click constant definition
     */
    public final static int HANDCLICK_DURATION = 200;
    public final static double HANDMOVE_DISTANCE = 10.0;

    public final static String FX_TRANSFORM_2D = "Transform 2D";
    public final static String FX_TRANSFORM_2D_SCALE_X = "Scale X";
    public final static String FX_TRANSFORM_2D_SCALE_Y = "Scale Y";
    /**
     * Color Property 颜色属性
     */
    public final static String FX_COLOR_PROPERTY = "Color Property";
    public final static String FX_COLOR_PROPERTY_BRIGHTNESS = "Brightness";
    public final static String FX_COLOR_PROPERTY_CONTRAST = "Contrast";
    public final static String FX_COLOR_PROPERTY_SATURATION = "Saturation";
    /**
     * Vignette 暗角
     */
    public final static String FX_VIGNETTE = "Vignette";
    public final static String FX_VIGNETTE_DEGREE = "Degree";
    /**
     * Sharpen 锐度
     */
    public final static String FX_SHARPEN = "Sharpen";
    public final static String FX_SHARPEN_AMOUNT = "Amount";

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


    public static final String SELECT_MEDIA_FROM = "select_media_from";

    public static final int POINT16V9 = AspectRatio.AspectRatio_16v9;
    public static final int POINT1V1 = AspectRatio.AspectRatio_1v1;
    public static final int POINT9V16 = AspectRatio.AspectRatio_9v16;
    public static final int POINT3V4 = AspectRatio.AspectRatio_3v4;
    public static final int POINT4V3 = AspectRatio.AspectRatio_4v3;


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

    public static class IntentKey {
        public static final String INTENT_KEY_SHOT_ID = "shot_id";
    }
}
