package com.meishe.engine.util;

import android.os.Environment;
import android.util.Log;

import com.meishe.app.BaseApp;
import com.meishe.base.constants.AndroidOS;
import com.meishe.engine.asset.bean.AssetInfo;

import java.io.File;

/**
 * The type Path utils.
 * 路径工具类
 */
public class PathUtils {

    private static final String TAG = PathUtils.class.getName();
    private static String SDK_FILE_ROOT_DIRECTORY = "NvStreamingSdk" + File.separator;
    private static String AUDIO_RECORD_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "AudioRecord";

    private static String ASSET_DOWNLOAD_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Asset";
    private static String ASSET_FACE_MODEL = ASSET_DOWNLOAD_DIRECTORY + File.separator + "FaceModel";
    private static String ASSET_DOWNLOAD_DIRECTORY_FILTER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Filter";
    private static String ASSET_DOWNLOAD_DIRECTORY_COMPILE = SDK_FILE_ROOT_DIRECTORY + "Compile";
    private static String CRASH_LOG_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "Log";
    private static String ASSET_DOWNLOAD_DIRECTORY_EFFECT_FRAME = ASSET_DOWNLOAD_DIRECTORY + File.separator + "frame";
    private static String ASSET_DOWNLOAD_DIRECTORY_EFFECT_DREAM = ASSET_DOWNLOAD_DIRECTORY + File.separator + "dream";
    private static String ASSET_DOWNLOAD_DIRECTORY_EFFECT_SHAKING = ASSET_DOWNLOAD_DIRECTORY + File.separator + "shaking";
    private static String ASSET_DOWNLOAD_DIRECTORY_EFFECT_LIVELY = ASSET_DOWNLOAD_DIRECTORY + File.separator + "lively";
    private static String ASSET_DOWNLOAD_DIRECTORY_THEME = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Theme";
    private static String ASSET_DOWNLOAD_DIRECTORY_CAPTION = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption";
    private static String ASSET_DOWNLOAD_DIRECTORY_ANIMATEDSTICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "AnimatedSticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_TRANSITION = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Transition";
    private static String ASSET_DOWNLOAD_DIRECTORY_FONT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Font";
    private static String ASSET_DOWNLOAD_DIRECTORY_CAPTURE_SCENE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "CaptureScene";
    private static String ASSET_DOWNLOAD_DIRECTORY_PARTICLE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Particle";
    private static String ASSET_DOWNLOAD_DIRECTORY_FACE_STICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "FaceSticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_CUSTOM_ANIMATED_STICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "CustomAnimatedSticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_FACE1_STICKER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Face1Sticker";
    private static String ASSET_DOWNLOAD_DIRECTORY_SUPER_ZOOM = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Meicam";
    private static String ASSET_DOWNLOAD_DIRECTORY_ARSCENE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "ArScene";
    private static String ASSET_DOWNLOAD_DIRECTORY_GIFCONVERT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "GifConvert";
    private static String ASSET_DOWNLOAD_DIRECTORY_COMPOUND_CAPTION = ASSET_DOWNLOAD_DIRECTORY + File.separator + "CompoundCaption";
    private static String ASSET_DOWNLOAD_DIRECTORY_PHOTO_ALBUM = ASSET_DOWNLOAD_DIRECTORY + File.separator + "PhotoAlbum";
    private static String ASSET_DOWNLOAD_VIDEO_TRANSITION_3D = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Transtion3D";
    private static String ASSET_DOWNLOAD_VIDEO_TRANSITION_EFFECT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "TranstionEffect";
    private static String ASSET_DOWNLOAD_TEMPLATE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Template";
    private static String ASSET_GENERATE_TEMPLATE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "GenerateTemplate";
    private final static String IMAGE_BACKGROUND_FOLDER = "imageBackground";
    private static String WATERMARK_CAF_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "WaterMark";
    private static String VIDEO_CONVERT_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "VideoConvert";
    private static String VIDEO_FREEZR_DIRECTORY = SDK_FILE_ROOT_DIRECTORY + "VideoFreeze";

    private static final String LICENSE_FILE_FOLDER = SDK_FILE_ROOT_DIRECTORY + "License";
    private static final String LICENSE_FILE_TEMPLATE = SDK_FILE_ROOT_DIRECTORY + "template";

    private static final String ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_FLOWER = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption" + File.separator + "flower";
    private static final String ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_BUBBLE = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption" + File.separator + "bubble";
    private static final String ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_IN = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption" + File.separator + "in";
    private static final String ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_OUT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption" + File.separator + "out";
    private static final String ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_GROUP = ASSET_DOWNLOAD_DIRECTORY + File.separator + "Caption" + File.separator + "group";

    private static String ASSET_DOWNLOAD_VIDEO_ANIMATION_IN = ASSET_DOWNLOAD_DIRECTORY + File.separator + "animation" + File.separator + "in";
    private static String ASSET_DOWNLOAD_VIDEO_ANIMATION_OUT = ASSET_DOWNLOAD_DIRECTORY + File.separator + "animation" + File.separator + "out";
    private static String ASSET_DOWNLOAD_VIDEO_ANIMATION_GROUP = ASSET_DOWNLOAD_DIRECTORY + File.separator + "animation" + File.separator + "group";


    private static final String LOCAL_EFFECT_TEMPLATE = SDK_FILE_ROOT_DIRECTORY + "local/template";
    private static final String LOCAL_EFFECT_TEMPLATE_FREE = SDK_FILE_ROOT_DIRECTORY + "local/template_free";
    private static final String LOCAL_EFFECT_TEMPLATE_AE = SDK_FILE_ROOT_DIRECTORY + "local/template_ae";

    /**
     * Gets audio record file path.
     * 获取音频记录文件路径
     *
     * @return the audio record file path 音频记录文件路径
     */
    public static String getAudioRecordFilePath() {
        return getFolderDirPath(AUDIO_RECORD_DIRECTORY);
    }


    /**
     * Gets folder dir path.
     * 获取文件夹目录路径
     *
     * @param dstDirPathToCreate the dst dir path to create 文件夹目录路径
     * @return the folder dir path 文件夹dir路径
     */
    public static String getFolderDirPath(String dstDirPathToCreate) {
        //File dstFileDir = new File(Environment.getExternalStorageDirectory(), dstDirPathToCreate);
        File dstFileDir = new File(Environment.getExternalStorageDirectory(), dstDirPathToCreate);
        if (AndroidOS.USE_SCOPED_STORAGE) {
            dstFileDir = new File(BaseApp.getContext().getExternalFilesDir(""), dstDirPathToCreate);
        }
        if (!dstFileDir.exists() && !dstFileDir.mkdirs()) {
            Log.e(TAG, "Failed to create file dir path--->" + dstDirPathToCreate);
            return null;
        }
        return dstFileDir.getAbsolutePath();
    }


    public static String getAssetDownloadPath(int assetType) {
        String assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY);
        if (assetDownloadDir == null) {
            return null;
        }
        switch (assetType) {
            case AssetInfo.ASSET_THEME:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_THEME);
                break;
            case AssetInfo.ASSET_FILTER:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FILTER);
                break;
            case AssetInfo.ASSET_EFFECT_LIVELY:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_LIVELY);
                break;
            case AssetInfo.ASSET_EFFECT_FRAME:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_FRAME);
                break;
            case AssetInfo.ASSET_EFFECT_SHAKING:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_SHAKING);
                break;
            case AssetInfo.ASSET_EFFECT_DREAM:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_DREAM);
                break;
            case AssetInfo.ASSET_CAPTION_STYLE:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_CAPTION);
                break;
            case AssetInfo.ASSET_ANIMATED_STICKER:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_ANIMATEDSTICKER);
                break;
            case AssetInfo.ASSET_VIDEO_TRANSITION:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_TRANSITION);
                break;
            case AssetInfo.ASSET_FONT:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FONT);
                break;
            case AssetInfo.ASSET_CAPTURE_SCENE:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_CAPTURE_SCENE);
                break;
            case AssetInfo.ASSET_PARTICLE:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_PARTICLE);
                break;
            case AssetInfo.ASSET_FACE_STICKER:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FACE_STICKER);
                break;
            case AssetInfo.ASSET_CUSTOM_STICKER_PACKAGE:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_CUSTOM_ANIMATED_STICKER);
                break;
            case AssetInfo.ASSET_FACE1_STICKER:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_FACE1_STICKER);
                break;
            case AssetInfo.ASSET_SUPER_ZOOM:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_SUPER_ZOOM);
                break;
            case AssetInfo.ASSET_AR_SCENE_FACE:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_ARSCENE);
                break;
            case AssetInfo.ASSET_COMPOUND_CAPTION:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_COMPOUND_CAPTION);
                break;
            case AssetInfo.ASSET_PHOTO_ALBUM:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_DIRECTORY_PHOTO_ALBUM);
                break;
            case AssetInfo.ASSET_VIDEO_TRANSITION_3D:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_TRANSITION_3D);
                break;
            case AssetInfo.ASSET_VIDEO_TRANSITION_EFFECT:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_TRANSITION_EFFECT);
                break;
            case AssetInfo.ASSET_ANIMATION_IN:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_IN);
                break;
            case AssetInfo.ASSET_ANIMATION_OUT:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_OUT);
                break;
            case AssetInfo.ASSET_ANIMATION_GROUP:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_GROUP);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_FLOWER:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_FLOWER);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_BUBBLE:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_BUBBLE);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_ANIMATION_IN:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_IN);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_ANIMATION_OUT:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_OUT);
                break;
            case AssetInfo.ASSET_CUSTOM_CAPTION_ANIMATION_COMBINATION:
                assetDownloadDir = getAssetDownloadDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_GROUP);
                break;
        }
        return assetDownloadDir;
    }


    private static String getAssetDownloadDirPath(String assetDirPathToCreate) {
        return getFolderDirPath(assetDirPathToCreate);
    }

    public static String getAssetDownloadDir() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY);
    }

    /**
     * Gets file name.
     * 得到文件名称
     *
     * @param filename the filename  文件名称
     * @return the file name 文件名称
     */
    public static String getFileName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.indexOf('.');
            int lastSeparator = filename.lastIndexOf('/');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(lastSeparator + 1);
            }
        }
        return filename;
    }

    /**
     * Gets face model dir.
     * 获取脸部模型目录
     *
     * @return the face model dir 脸部模型目录
     */
    public static String getFaceModelDir() {
        return getFolderDirPath(ASSET_FACE_MODEL);
    }

    /**
     * Gets watermark caf directory dir.
     * 获取水印caf目录dir
     *
     * @return the watermark caf directory dir 水印caf目录dir
     */
    public static String getWatermarkCafDirectoryDir() {
        return getFolderDirPath(WATERMARK_CAF_DIRECTORY);
    }

    /**
     * Gets video convert dir.
     * 获取视频转换目录
     *
     * @return the video convert dir 视频转换dir
     */
    public static String getVideoConvertDir() {
        return getFolderDirPath(VIDEO_CONVERT_DIRECTORY);
    }

    /**
     * Gets video freeze convert dir.
     * 获取视频冻结转换dir
     *
     * @return the video freeze convert dir 视频冻结转换dir
     */
    public static String getVideoFreezeConvertDir() {
        return getFolderDirPath(VIDEO_FREEZR_DIRECTORY);
    }


    /**
     * Gets video save path.
     * 获取视频保存路径
     *
     * @param videoName the video name 视频的名字
     * @return the video save path 视频保存路径
     */
    public static String getVideoSavePath(String videoName) {
        File dstFileDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
        File file = new File(dstFileDir, videoName);
        if (file.exists()) {
            file.delete();
        }
        return file.getAbsolutePath();
    }

    /**
     * Gets video save name.
     * 获取视频保存名称
     *
     * @return the video save name 视频保存的名字
     */
    public static String getVideoSaveName() {
        return "MY_" + System.currentTimeMillis() + ".mp4";
    }

    /**
     * Gets video save name.
     * 获取视频保存名称
     *
     * @return the video save name 视频保存的名字
     */
    public static String getTemplateVideoSaveName(String uuid) {
        return uuid + ".mp4";
    }

    public static String getQuickSplicingVideoSavePath(String videoName) {
        File dstFileDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/quick_splicing");
        if (!dstFileDir.exists()) {
            dstFileDir.mkdirs();
        }
        File file = new File(dstFileDir, videoName);
        if (file.exists()) {
            file.delete();
        }
        return file.getAbsolutePath();
    }

    /**
     * Gets log dir.
     * 获取日志dir
     *
     * @return the log dir 日志dir
     */
    public static String getLogDir() {
        return getFolderDirPath(CRASH_LOG_DIRECTORY);
    }


    /**
     * Gets license file folder.
     * 获取license文件文件夹
     *
     * @return the license file folder license文件夹
     */
    public static String getLicenseFileFolder() {
        String dstDirPath = getFolderDirPath(LICENSE_FILE_FOLDER);
        if (dstDirPath == null) {
            return null;
        }
        return dstDirPath;
    }


    /**
     * Gets effect frame  file folder.
     * 获取边框特效文件文件夹
     *
     * @return the effect frame file folder  文件夹
     */
    public static String getStickerFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_ANIMATEDSTICKER);
    }

    /**
     * Gets effect frame  file folder.
     * 获取边框特效文件文件夹
     *
     * @return the effect frame file folder  文件夹
     */
    public static String getEffectFrameFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_FRAME);
    }

    /**
     * Gets filter  file folder.
     * 获取边滤镜文件夹
     *
     * @return the filter file folder  文件夹
     */
    public static String getFilterFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_FILTER);
    }

    /**
     * Gets effect dream  file folder.
     * 获取梦幻特效文件文件夹
     *
     * @return the effect dream file folder  文件夹
     */
    public static String getEffectDreamFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_DREAM);
    }

    /**
     * Gets effect shaking  file folder.
     * 获取抖特效特效文件文件夹
     *
     * @return the effect shaking file folder  文件夹
     */
    public static String getEffectShakingFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_SHAKING);
    }

    /**
     * Gets effect lively  file folder.
     * 获取动感特效文件文件夹
     *
     * @return the effect shaking file folder  文件夹
     */
    public static String getEffectLivelyFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_EFFECT_LIVELY);
    }


    /**
     * Gets template file folder.
     * 获取template文件文件夹
     *
     * @return the template file folder template 文件夹
     */
    public static String getTemplateFileFolder() {
        return getFolderDirPath(LICENSE_FILE_TEMPLATE);
    }

    /**
     * Gets template cover file folder.
     * 获取template 封面图文件文件夹
     *
     * @return the template cover file folder template 文件夹
     */
    public static String getTemplateCoverFileFolder() {
        return getFolderDirPath(LICENSE_FILE_TEMPLATE + File.separator + "cover");
    }

    /**
     * Gets caption flower file folder.
     * 获取字幕 花字 文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getCaptionFlower() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_FLOWER);
    }

    /**
     * Gets caption bubble file folder.
     * 获取字幕组合动画文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getCaptionBubble() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_BUBBLE);
    }


    /**
     * Gets in animation  file folder.
     * 获取字幕入场动画文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getInCaptionAnimation() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_IN);
    }


    /**
     * Gets out animation  file folder.
     * 获取字幕出动画文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getOutCaptionAnimation() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_OUT);
    }

    /**
     * Gets group animation  file folder.
     * 获取字幕组合动画文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getCombCaptionAnimation() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_CAPTION_ANIMATION_GROUP);
    }

    /**
     * Gets transition  file folder.
     * 获取transition文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getTransitionFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_TRANSITION);
    }


    /**
     * Gets transition 3d file folder.
     * 获取transition文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getTransition3DFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_TRANSITION_3D);
    }

    /**
     * Gets transition effect file folder.
     * 获取transition文件文件夹
     *
     * @return the transition file folder  文件夹
     */
    public static String getTransitionEffectFileFolder() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_TRANSITION_EFFECT);
    }

    /**
     * Gets generate template file folder.
     * 获取 生成模板文件文件夹
     *
     * @return file folder  文件夹
     */
    public static String getGenerateTemplateFileFolder(String uuid) {
        return getFolderDirPath(ASSET_GENERATE_TEMPLATE + File.separator + uuid);
    }

    /**
     * Gets generate template file folder.
     * 获取 生成模板文件文件夹
     *
     * @return file folder  文件夹
     */
    public static String getGenerateTemplateFileFolder() {
        return getFolderDirPath(ASSET_GENERATE_TEMPLATE);
    }

    /**
     * Gets template dir.
     * 得到模板dir
     *
     * @return the template dir 模板dir
     */
    public static String getTemplateDir() {
        return getFolderDirPath(ASSET_DOWNLOAD_TEMPLATE);
    }


    /**
     * 获取本地通用模板路径
     *
     * @return String
     */
    public static String getLocalTemplateDir() {
        String dstDirPath = getFolderDirPath(LOCAL_EFFECT_TEMPLATE);
        if (dstDirPath == null) {
            return null;
        }
        return dstDirPath;
    }

    /**
     * 获取本地不限时长模板路径
     *
     * @return String
     */
    public static String getLocalTemplateFreeDir() {
        String dstDirPath = getFolderDirPath(LOCAL_EFFECT_TEMPLATE_FREE);
        if (dstDirPath == null) {
            return null;
        }
        return dstDirPath;
    }

    /**
     * 获取本地AE模板路径
     *
     * @return String
     */
    public static String getLocalTemplateAEDir() {
        String dstDirPath = getFolderDirPath(LOCAL_EFFECT_TEMPLATE_AE);
        if (dstDirPath == null) {
            return null;
        }
        return dstDirPath;
    }

    /**
     * Gets custom sticker dir.
     * 获取自定义贴纸目录
     *
     * @return the custom sticker dir 自定义贴纸目录
     */
    public static String getCustomStickerDir() {
        return getFolderDirPath(ASSET_DOWNLOAD_DIRECTORY_CUSTOM_ANIMATED_STICKER);
    }

    /**
     * get Group Animation
     * 获取组合动画
     *
     * @return Group Animation path
     */
    public static String getAnimationGroup() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_GROUP);
    }

    /**
     * get In Animation
     * 获取入场动画
     *
     * @return In Animation path
     */
    public static String getAnimationIn() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_IN);
    }

    /**
     * get Out Animation
     * 获取出场动画
     *
     * @return Out Animation path
     */
    public static String getAnimationOut() {
        return getFolderDirPath(ASSET_DOWNLOAD_VIDEO_ANIMATION_OUT);
    }
}
