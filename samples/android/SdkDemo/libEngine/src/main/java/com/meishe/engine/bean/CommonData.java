package com.meishe.engine.bean;

import com.meishe.base.utils.LogUtils;
import com.meishe.engine.constant.NvsConstants;

import static com.meishe.base.constants.BaseConstants.TRACK_INDEX_MAIN;

/**
 * Created by CaoZhiChao on 2020/6/16 14:02
 */
public class CommonData {
    /**
     * 主轨道index
     * Main orbital index
     */
    public static final int MAIN_TRACK_INDEX = TRACK_INDEX_MAIN;
    /**
     * 系统长按时长
     * The system is long and the time is long
     */
    public static final int CLICK_LONG = 500;
    /**
     * 点击时长
     * Click duration
     */
    public static final int CLICK_TIME = 200;
    /**
     * 点击位置限制
     * Click position limit
     */
    public static final int CLICK_MOVE = 10;
    public static final long TIMEBASE = 1000000L;
    public static final long ONE_FRAME = 4000L;
    public static final long DEFAULT_LENGTH = 4 * TIMEBASE;

    public static final long DEFAULT_TRIM_IN = TIMEBASE * 60 * 60L;

    /**
     * 最小的展示duration
     * Minimum presentation duration
     */
    public static final long MIN_SHOW_LENGTH_DURATION = (long) (CommonData.TIMEBASE * 0.8);

    /**
     * 特效
     * Effect
     */
    public final static String VIDEO_FX_AR_SCENE = "AR Scene";

    /**
     * 美颜
     * Beauty
     */
    public final static String VIDEO_FX_BEAUTY_EFFECT = "Beauty Effect";
    /**
     * 美型
     * Shape
     */
    public final static String VIDEO_FX_BEAUTY_SHAPE = "Beauty Shape";


    public final static String VIDEO_FX_SINGLE_BUFFER_MODE = "Single Buffer Mode";

    /**
     * 磨皮
     * Skinning
     */
    public final static String VIDEO_FX_BEAUTY_STRENGTH = "Beauty Strength";
    /**
     * 美白
     * Whitening
     */
    public final static String VIDEO_FX_BEAUTY_WHITENING = "Beauty Whitening";
    /**
     * 红润
     * Reddening
     */
    public final static String VIDEO_FX_BEAUTY_REDDENING = "Beauty Reddening";


    public final static String ATTACHMENT_KEY_STICKER_COVER_PATH = "attachment_key_sticker_cover_path";
    public final static String ATTACHMENT_KEY_VIDEO_CLIP_AR_SCENE = "attachment_key_video_clip_ar_scene";

    /**
     * 轨道类型
     * Track Type
     */
    public static final String TRACK_VIDEO = "videoTrack";
    public static final String TRACK_AUDIO = "audioTrack";
    public static final String TRACK_TIMELINE_FX = "timelineVideoFxTrack";
    public static final String TRACK_STICKER_CAPTION = "stickerCaptionTrack";
    /**
     * clip类型video audio timelineVideoFx caption compoundCaption  sticker
     */
    public static final String CLIP_TIMELINE_FX = "timelineVideoFx";
    public static final String CLIP_CAPTION = "caption";
    public static final String CLIP_COMPOUND_CAPTION = "compound_caption";
    public static final String CLIP_STICKER = "sticker";
    public static final String CLIP_VIDEO = "video";
    public static final String CLIP_AUDIO = "audio";
    public static final String CLIP_IMAGE = "image";
    public static final String CLIP_HOLDER = "holder";
    /**
     * Blacks the material by default
     * 默认补黑素材
     */
    public static final String EMPTY_THUMBNAIL_IMAGE = "assets:/black.png";
    /**
     * The constant IMAGE_BLACK_FILE_NAME.
     */
    public static final String IMAGE_BLACK_FILE_NAME = "9d388abb-ab09-4b9f-953e-daba77e9037a.png";

    /**
     * Blacks the material by default.The file name is unique.
     * 默认补黑素材,文件名是唯一的
     */
    public static final String IMAGE_BLACK_HOLDER = "assets:/" + IMAGE_BLACK_FILE_NAME;

    public static final int TYPE_COMMON_CAPTION = 0;
    public static final int TYPE_AI_CAPTION = 1;


    public static final String TRANSITION = "transition";

    /**
     * storyboard类型
     */
    public final static int STORYBOARD_BACKGROUND_TYPE_COLOR = 0;
    public final static int STORYBOARD_BACKGROUND_TYPE_IMAGE = 1;
    public final static int STORYBOARD_BACKGROUND_TYPE_BLUR = 2;

    public static final String TYPE_BUILD_IN = "builtin";//内置特效 Built-in effects
    public static final String TYPE_PACKAGE = "package";//包特效 Package effects
    public static final String TYPE_PROPERTY = "property";//属性特技 property
    public static final String TYPE_RAW_BUILTIN = "rawBuiltin";//原始内置特效 Original built-in effects


    public static final String SUB_TYPE_CROPPER_TRANSFORM = "cropper_transform";//裁剪transform
    public static final String SUB_TYPE_CROPPER = "cropper";//裁剪 crop
    public static final String SUB_TYPE_MASK = "mask";

    public final static int EFFECT_BUILTIN = 0;//內建特效 Built-in effects
    public final static int EFFECT_PACKAGE = 1;//包特效 Package effects

    public static final int MAX_AUDIO_COUNT = 16;
    public static final int TIMELINE_RESOLUTION_VALUE = 720;


    public static final int TYPE_CAPTION = 1;
    public static final int TYPE_COMPOUND_CAPTION = 2;
    public static final int TYPE_STICKER = 3;
    public static final int TYPE_PIC = 4;
    public static final int TYPE_EFFECT = 5;


    public static class UserAssetType {
        public static final int All = 0;
        public static final int PURCHASED = 1;
        public static final int CUSTOM = 2;
    }


    public enum AspectRatio {
        /**
         * 相关比例
         * Correlation ratio
         */
        ASPECT_16V9(NvsConstants.AspectRatio.AspectRatio_16v9, 16.0f / 9, "16v9"),
        ASPECT_1V1(NvsConstants.AspectRatio.AspectRatio_1v1, 1, "1v1"),
        ASPECT_9V16(NvsConstants.AspectRatio.AspectRatio_9v16, 9.0f / 16, "9v16"),
        ASPECT_4V3(NvsConstants.AspectRatio.AspectRatio_4v3, 4.0f / 3, "4v3"),
        ASPECT_3V4(NvsConstants.AspectRatio.AspectRatio_3v4, 3.0f / 4, "3v4");
        private final int aspect;
        private final float ratio;
        private final String stringValue;

        AspectRatio(int aspect, float ratio, String stringValue) {
            this.aspect = aspect;
            this.ratio = ratio;
            this.stringValue = stringValue;
        }

        public float getRatio() {
            return ratio;
        }

        public String getStringValue() {
            return stringValue;
        }

        public int getAspect() {
            return aspect;
        }

        /**
         * Gets  Template aspect.
         * 获取模板近似比例
         *
         * @param ratio the ratio
         * @return the aspect
         */
        public static int getTemplateAspect(float ratio) {
            AspectRatio[] values = AspectRatio.values();
            AspectRatio finalValue = values[0];

            for (int i = 1; i < values.length; i++) {
                float currRatio = values[i].ratio - ratio;
                float beforeRatio = finalValue.ratio - ratio;
                if (Math.abs(currRatio) < Math.abs(beforeRatio)) {
                    finalValue = values[i];
                }
            }

            LogUtils.e("finalValue.aspect===" + finalValue.aspect + "===finalValue.ratio===" + finalValue.aspect);
            return finalValue.aspect;
        }

        /**
         * Gets aspect.
         * 获取近似比例
         *
         * @param ratio the ratio
         * @return the aspect
         */
        public static int getAspect(float ratio) {
            AspectRatio[] values = AspectRatio.values();
            for (AspectRatio value : values) {
                if (Math.abs(value.ratio - ratio) < 0.1f) {
                    return value.aspect;
                }
            }
            return NvsConstants.AspectRatio.AspectRatio_NoFitRatio;
        }

        /**
         * Gets aspect.
         * 获取近似比例对应的串
         *
         * @param ratio the ratio
         * @return the aspect string
         */
        public static AspectRatio getAspectRatio(int ratio) {
            AspectRatio[] values = AspectRatio.values();
            for (AspectRatio value : values) {
                if (value.aspect == ratio) {
                    return value;
                }
            }
            return null;
        }
    }
}
