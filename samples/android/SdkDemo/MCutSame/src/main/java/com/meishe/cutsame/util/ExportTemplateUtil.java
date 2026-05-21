package com.meishe.cutsame.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.meishe.cutsame.bean.ExportTemplateDescInfo;
import com.meishe.cutsame.bean.TemplateInfo;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.GsonUtils;
import com.meishe.engine.bean.ClipInfo;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamAudioClip;
import com.meishe.engine.bean.MeicamAudioTrack;
import com.meishe.engine.bean.MeicamCaptionClip;
import com.meishe.engine.bean.MeicamCompoundCaptionClip;
import com.meishe.engine.bean.MeicamStickerClip;
import com.meishe.engine.bean.MeicamTimelineVideoFxClip;
import com.meishe.engine.bean.MeicamTimelineVideoFxTrack;
import com.meishe.engine.bean.MeicamTransition;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoFx;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.bean.MeicamWaterMark;
import com.meishe.engine.bean.TimelineData;
import com.meishe.engine.bean.TimelineDataUtil;
import com.meishe.engine.constant.NvsConstants;
import com.meishe.engine.util.PathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2021/1/7 18:03
 * @Description : 导出模板相关的工具类
 * export template util
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class ExportTemplateUtil {

    /**
     * 保存转场数据
     * save transition data
     *
     * @param context
     * @param destTransitionFolderPath 转场输出路径
     *                                 transition output path
     */
    public static void scanAndSaveTransitionResource(Context context, String destTransitionFolderPath) {
        List<MeicamVideoTrack> meicamVideoTrackList = TimelineData.getInstance().getMeicamVideoTrackList();
        if (meicamVideoTrackList != null && meicamVideoTrackList.size() > 0) {
            MeicamVideoTrack meicamVideoTrack = meicamVideoTrackList.get(0);
            if (meicamVideoTrack != null) {
                List<MeicamTransition> transitionInfoList = meicamVideoTrack.getTransitionInfoList();
                if (transitionInfoList != null) {
                    for (int i = 0; i < transitionInfoList.size(); i++) {
                        MeicamTransition meicamTransition = transitionInfoList.get(i);
                        if (meicamTransition == null) {
                            continue;
                        }

                        String desc = meicamTransition.getDesc();
                        String fileName = findFileFromAssets(context, desc, "videotransition", "transition_effect");
                        if (!TextUtils.isEmpty(fileName)) {
                            copyAssetsFile2SDCard(context, fileName, "transition_effect/" + fileName, destTransitionFolderPath);
                            continue;
                        }
                        fileName = findFileFromAssets(context, desc, "videotransition", "transition_3d");
                        if (!TextUtils.isEmpty(fileName)) {
                            copyAssetsFile2SDCard(context, fileName, "transition_3d/" + fileName, destTransitionFolderPath);
                            continue;
                        }

                        String transitionFileFolder = PathUtils.getTransitionFileFolder();
                        if (findAndCopyFileToSdCard(desc, transitionFileFolder, destTransitionFolderPath, "videotransition")) {
                            continue;
                        }
                        String transition3DFileFolder = PathUtils.getTransition3DFileFolder();
                        if (findAndCopyFileToSdCard(desc, transition3DFileFolder, destTransitionFolderPath, "videotransition")) {
                            continue;
                        }

                        String transitionEffectFileFolder = PathUtils.getTransitionEffectFileFolder();
                        findAndCopyFileToSdCard(desc, transitionEffectFileFolder, destTransitionFolderPath, "videotransition");
                    }
                }
            }
        }
    }


    /**
     * 保存 字幕需要的资源
     * save caption need resource
     *
     * @param context
     * @param destCaptionCombAnimationFolderPath 组合动画输出路径
     *                                           com animation output path
     * @param destCaptionInAnimationFolderPath   入动画输出路径
     *                                           in animation output path
     * @param destCaptionOutAnimationFolderPath  出动画输出路径
     *                                           out animation output path
     * @param destCaptionBubbleFolderPath        气泡输出路径
     *                                           bubble output path
     * @param destCaptionFlowerFolderPath        花字输出路径
     *                                           flower output path
     */
    public static void scanAndSaveCaptionAnimation(Context context, String destCaptionCombAnimationFolderPath,
                                                   String destCaptionInAnimationFolderPath,
                                                   String destCaptionOutAnimationFolderPath,
                                                   String destCaptionBubbleFolderPath,
                                                   String destCaptionFlowerFolderPath) {
        List<ClipInfo<?>> captionListByType = TimelineDataUtil.getStickerOrCaptionListByType(CommonData.CLIP_CAPTION);
        if (captionListByType == null) {
            return;
        }
        for (int i = 0; i < captionListByType.size(); i++) {
            ClipInfo<?> clipInfo = captionListByType.get(i);
            if (clipInfo instanceof MeicamCaptionClip) {
                //组合动画
                String combinationAnimationUuid = ((MeicamCaptionClip) clipInfo).getCombinationAnimationUuid();
                if (!TextUtils.isEmpty(combinationAnimationUuid)) {
                    String fileName = findFileFromAssets(context, combinationAnimationUuid, "captionanimation", "captionanimation/combination/");
                    if (!TextUtils.isEmpty(fileName)) {
                        copyAssetsFile2SDCard(context, fileName, "captionanimation/combination/" + fileName, destCaptionCombAnimationFolderPath);
                    } else {
                        String combCaptionAnimation = PathUtils.getCombCaptionAnimation();
                        findAndCopyFileToSdCard(combinationAnimationUuid, combCaptionAnimation, destCaptionCombAnimationFolderPath, "captionanimation");
                    }
                }

                //入场动画 Entrance animation
                String inAnimationUuid = ((MeicamCaptionClip) clipInfo).getMarchInAnimationUuid();
                if (!TextUtils.isEmpty(inAnimationUuid)) {
                    String fileName = findFileFromAssets(context, inAnimationUuid, "captioninanimation", "captionanimation/in/");
                    if (!TextUtils.isEmpty(fileName)) {
                        copyAssetsFile2SDCard(context, fileName, "captionanimation/in/" + fileName, destCaptionInAnimationFolderPath);
                    } else {
                        String combCaptionAnimation = PathUtils.getInCaptionAnimation();
                        findAndCopyFileToSdCard(inAnimationUuid, combCaptionAnimation, destCaptionInAnimationFolderPath, "captioninanimation");
                    }
                }
                //出场动画 Appearance animation
                String outAnimationUuid = ((MeicamCaptionClip) clipInfo).getMarchOutAnimationUuid();
                if (!TextUtils.isEmpty(outAnimationUuid)) {
                    String fileName = findFileFromAssets(context, outAnimationUuid, "captionoutanimation", "captionanimation/out/");
                    if (!TextUtils.isEmpty(fileName)) {
                        copyAssetsFile2SDCard(context, fileName, "captionanimation/out/" + fileName, destCaptionOutAnimationFolderPath);
                    } else {
                        String combCaptionAnimation = PathUtils.getOutCaptionAnimation();
                        findAndCopyFileToSdCard(outAnimationUuid, combCaptionAnimation, destCaptionOutAnimationFolderPath, "captionoutanimation");
                    }
                }

                //气泡 bubble
                String bubbleUuid = ((MeicamCaptionClip) clipInfo).getBubbleUuid();
                if (!TextUtils.isEmpty(bubbleUuid)) {
                    String fileName = findFileFromAssets(context, bubbleUuid, "captioncontext", "captionbubble/");
                    if (!TextUtils.isEmpty(fileName)) {
                        copyAssetsFile2SDCard(context, fileName, "captionbubble/" + fileName, destCaptionBubbleFolderPath);
                    } else {
                        String captionBubble = PathUtils.getCaptionBubble();
                        findAndCopyFileToSdCard(bubbleUuid, captionBubble, destCaptionBubbleFolderPath, "captioncontext");
                    }
                }

                //花字 captionrenderer
                String richWordUuid = ((MeicamCaptionClip) clipInfo).getRichWordUuid();
                if (!TextUtils.isEmpty(richWordUuid)) {
                    String fileName = findFileFromAssets(context, richWordUuid, "captionrenderer", "captionrichword/");
                    if (!TextUtils.isEmpty(fileName)) {
                        copyAssetsFile2SDCard(context, fileName, "captionrichword/" + fileName, destCaptionFlowerFolderPath);
                    } else {
                        String captionFlower = PathUtils.getCaptionFlower();
                        findAndCopyFileToSdCard(richWordUuid, captionFlower, destCaptionFlowerFolderPath, "captionrenderer");
                    }
                }


            }
        }
    }

    /**
     * 保存特效
     * save videoFx
     *
     * @param context
     * @param destEffectFolderPath
     */
    public static void scanAndSaveVideoFx(Context context, String destEffectFolderPath) {
        //保存特效 动感 抖特效 梦幻 边框 Save special effects shaking special effects dream border
        scanAndSaveEffect(context, destEffectFolderPath);
        //保存滤镜 Save filter
        scanAndFilterAndAnimation(context, destEffectFolderPath);
    }

    /**
     * 保存特效  滤镜
     * save filter
     *
     * @param context
     * @param destEffectFolderPath
     */
    private static void scanAndFilterAndAnimation(Context context, String destEffectFolderPath) {
        List<MeicamVideoTrack> meicamVideoTracks = TimelineData.getInstance().getMeicamVideoTrackList();
        for (MeicamVideoTrack meicamVideoTrack : meicamVideoTracks) {
            List<ClipInfo<?>> clipInfos = meicamVideoTrack.getClipInfoList();
            for (ClipInfo clipInfo : clipInfos) {
                if (clipInfo instanceof MeicamVideoClip) {
                    MeicamVideoClip meicamVideoClip = (MeicamVideoClip) clipInfo;

                    //保存动画特效 Save animation effects
                    MeicamVideoFx meicamVideoFx = TimelineDataUtil.findPropertyFx(meicamVideoClip);
                    if (meicamVideoFx == null) {
                        continue;
                    }
                    //动画 animation
                    String keyword = meicamVideoFx.getStringVal(NvsConstants.POST_PACKAGE_ID);
                    if (!TextUtils.isEmpty(keyword)) {
                        String fileName = findFileFromAssets(context, keyword, "videofx", "animation/group/");
                        if (!TextUtils.isEmpty(fileName)) {
                            copyAssetsFile2SDCard(context, fileName, "animation/group/" + fileName, destEffectFolderPath);
                        } else {
                            String combCaptionAnimation = PathUtils.getAnimationGroup();
                            findAndCopyFileToSdCard(keyword, combCaptionAnimation, destEffectFolderPath, "videofx");
                        }
                    }

                    if (!TextUtils.isEmpty(keyword)) {
                        String fileName = findFileFromAssets(context, keyword, "videofx", "animation/in/");
                        if (!TextUtils.isEmpty(fileName)) {
                            copyAssetsFile2SDCard(context, fileName, "animation/in/" + fileName, destEffectFolderPath);
                        } else {
                            String combCaptionAnimation = PathUtils.getAnimationIn();
                            findAndCopyFileToSdCard(keyword, combCaptionAnimation, destEffectFolderPath, "videofx");
                        }
                    }

                    if (!TextUtils.isEmpty(keyword)) {
                        String fileName = findFileFromAssets(context, keyword, "videofx", "animation/out/");
                        if (!TextUtils.isEmpty(fileName)) {
                            copyAssetsFile2SDCard(context, fileName, "animation/out/" + fileName, destEffectFolderPath);
                        } else {
                            String combCaptionAnimation = PathUtils.getAnimationOut();
                            findAndCopyFileToSdCard(keyword, combCaptionAnimation, destEffectFolderPath, "videofx");
                        }
                    }

                    //保存滤镜特效 Save the filter effect
                    List<MeicamVideoFx> videoFxes = meicamVideoClip.getVideoFxs();
                    for (MeicamVideoFx videoFx : videoFxes) {
                        if (MeicamVideoFx.SUB_TYPE_CLIP_FILTER.equals(videoFx.getSubType()) ||
                                MeicamVideoFx.SUB_TYPE_TIMELINE_FILTER.equals(videoFx.getSubType())) {
                            //滤镜 filter
                            String fileName = findFileFromAssets(context, videoFx.getDesc(), "videofx", "filter");
                            if (!TextUtils.isEmpty(fileName)) {
                                copyAssetsFile2SDCard(context, fileName, "filter/" + fileName, destEffectFolderPath);
                                continue;
                            }
                            String effectFrameFileFolder = PathUtils.getFilterFolder();
                            if (findAndCopyFileToSdCard(videoFx.getDesc(), effectFrameFileFolder, destEffectFolderPath, "videofx")) {
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 保存特效 动感 抖特效 梦幻 边框
     * save effect shaking lively frame dream
     *
     * @param context
     * @param destEffectFolderPath
     */
    private static void scanAndSaveEffect(Context context, String destEffectFolderPath) {
        List<MeicamTimelineVideoFxTrack> meicamTimelineVideoFxTrackList = TimelineData.getInstance().getMeicamTimelineVideoFxTrackList();
        if (CommonUtils.isEmpty(meicamTimelineVideoFxTrackList)) {
            return;
        }
        MeicamTimelineVideoFxTrack meicamTimelineVideoFxTrack = meicamTimelineVideoFxTrackList.get(0);
        if (meicamTimelineVideoFxTrack == null) {
            return;
        }
        List<ClipInfo<?>> clipInfoList = meicamTimelineVideoFxTrack.getClipInfoList();
        if (CommonUtils.isEmpty(clipInfoList)) {
            return;
        }
        for (int i = 0; i < clipInfoList.size(); i++) {
            ClipInfo<?> clipInfo = clipInfoList.get(i);
            if (clipInfo instanceof MeicamTimelineVideoFxClip) {
                String desc = ((MeicamTimelineVideoFxClip) clipInfo).getDesc();
                if (TextUtils.isEmpty(desc)) {
                    continue;
                }
                //梦幻 dream
                String fileName = findFileFromAssets(context, desc, "videofx", "effect_dream");
                if (!TextUtils.isEmpty(fileName)) {
                    copyAssetsFile2SDCard(context, fileName, "effect_dream/" + fileName, destEffectFolderPath);
                    continue;
                }
                //边框 frame
                fileName = findFileFromAssets(context, desc, "videofx", "effect_frame");
                if (!TextUtils.isEmpty(fileName)) {
                    copyAssetsFile2SDCard(context, fileName, "effect_frame/" + fileName, destEffectFolderPath);
                    continue;
                }

                //动感 lively
                fileName = findFileFromAssets(context, desc, "videofx", "effect_lively");
                if (!TextUtils.isEmpty(fileName)) {
                    copyAssetsFile2SDCard(context, fileName, "effect_lively/" + fileName, destEffectFolderPath);
                    continue;
                }

                //抖特效 shaking
                fileName = findFileFromAssets(context, desc, "videofx", "effect_shaking");
                if (!TextUtils.isEmpty(fileName)) {
                    copyAssetsFile2SDCard(context, fileName, "effect_shaking/" + fileName, destEffectFolderPath);
                    continue;
                }

                String effectFrameFileFolder = PathUtils.getEffectFrameFileFolder();
                if (findAndCopyFileToSdCard(desc, effectFrameFileFolder, destEffectFolderPath, "videofx")) {
                    continue;
                }

                String effectDreamFileFolder = PathUtils.getEffectDreamFileFolder();
                if (findAndCopyFileToSdCard(desc, effectDreamFileFolder, destEffectFolderPath, "videofx")) {
                    continue;
                }

                String effectShakingFileFolder = PathUtils.getEffectShakingFileFolder();
                if (findAndCopyFileToSdCard(desc, effectShakingFileFolder, destEffectFolderPath, "videofx")) {
                    continue;
                }
                String effectLivelyFileFolder = PathUtils.getEffectLivelyFileFolder();
                findAndCopyFileToSdCard(desc, effectLivelyFileFolder, destEffectFolderPath, "videofx");
            }
        }
    }

    /**
     * 保存贴纸资源
     * save sticker resource
     *
     * @param context
     * @param destStickerFolderPath
     */
    public static void scanAndSaveAnimateSticker(Context context, String destStickerFolderPath) {
        List<ClipInfo<?>> stickerListByType = TimelineDataUtil.getStickerOrCaptionListByType(CommonData.CLIP_STICKER);
        if (CommonUtils.isEmpty(stickerListByType)) {
            return;
        }

        for (int i = 0; i < stickerListByType.size(); i++) {
            ClipInfo<?> clipInfo = stickerListByType.get(i);
            if (clipInfo instanceof MeicamStickerClip) {
                String packageId = ((MeicamStickerClip) clipInfo).getPackageId();
                if (TextUtils.isEmpty(packageId)) {
                    continue;
                }

                String fileName = findFileFromAssets(context, packageId, "animatedsticker", "sticker");
                if (!TextUtils.isEmpty(fileName)) {
                    copyAssetsFile2SDCard(context, fileName, "sticker/" + fileName, destStickerFolderPath);
                    continue;
                }

                String effectFrameFileFolder = PathUtils.getStickerFileFolder();
                findAndCopyFileToSdCard(packageId, effectFrameFileFolder, destStickerFolderPath, "animatedsticker");
            }
        }
    }

    /**
     * 保存组合字幕资源
     * Save the composite title resource
     *
     * @param context
     * @param destCompoundCaptionFolderPath
     */
    public static void scanAndSaveCompoundCaption(Context context, String destCompoundCaptionFolderPath) {
        List<ClipInfo<?>> compoundCaptionListByType = TimelineDataUtil.getStickerOrCaptionListByType(CommonData.CLIP_COMPOUND_CAPTION);
        if (CommonUtils.isEmpty(compoundCaptionListByType)) {
            return;
        }
        for (int i = 0; i < compoundCaptionListByType.size(); i++) {
            ClipInfo<?> clipInfo = compoundCaptionListByType.get(i);
            if (clipInfo instanceof MeicamCompoundCaptionClip) {
                String styleDesc = ((MeicamCompoundCaptionClip) clipInfo).getStyleDesc();
                if (TextUtils.isEmpty(styleDesc)) {
                    continue;
                }

                String fileName = findFileFromAssets(context, styleDesc, "compoundcaption", "compoundcaption");
                if (!TextUtils.isEmpty(fileName)) {
                    copyAssetsFile2SDCard(context, fileName, "compoundcaption/" + fileName, destCompoundCaptionFolderPath);
                    continue;
                }

                String effectFrameFileFolder = PathUtils.getStickerFileFolder();
                findAndCopyFileToSdCard(styleDesc, effectFrameFileFolder, destCompoundCaptionFolderPath, "compoundcaption");
            }
        }
    }

    /**
     * 保存音频资源
     * save audio
     *
     * @param context
     * @param destCompoundCaptionFolderPath
     */
    public static void scanAndSaveAudio(Context context, String destCompoundCaptionFolderPath) {
        List<MeicamAudioTrack> meicamAudioTracks = TimelineData.getInstance().getMeicamAudioTrackList();
        if (CommonUtils.isEmpty(meicamAudioTracks)) {
            return;
        }
        for (MeicamAudioTrack meicamAudioTrack : meicamAudioTracks) {
            List<ClipInfo<?>> clipInfos = meicamAudioTrack.getClipInfoList();
            for (ClipInfo clipInfo : clipInfos) {
                if (clipInfo instanceof MeicamAudioClip) {
                    MeicamAudioClip audioClip = (MeicamAudioClip) clipInfo;
                    String filePath = audioClip.getFilePath();
                    if (TextUtils.isEmpty(filePath)) {
                        return;
                    }

                    File file = new File(filePath);

                    if (!file.exists()) {
                        return;
                    }
                    String destFilePath = destCompoundCaptionFolderPath + File.separator + file.getName();
                    FileUtils.createOrExistsFile(destFilePath);
                    copySdcardFile(filePath, destFilePath);
                }
            }

        }
    }

    /**
     * 保存水印资源
     * save water maker
     *
     * @param context
     * @param destCompoundCaptionFolderPath
     */
    public static void scanAndSaveWaterMaker(Context context, String destCompoundCaptionFolderPath) {

        MeicamWaterMark meicamWaterMark = TimelineData.getInstance().getMeicamWaterMark();
        if (meicamWaterMark == null) {
            return;
        }
        String filePath = meicamWaterMark.getWatermarkPath();
        //assets:/water_mark/water_mark_meiying.png
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        String fileName = FileUtils.getFileName(filePath);
        if (isAssetFile(filePath)) {
            copyAssetsFile2SDCard(context, fileName, "water_mark/" + fileName, destCompoundCaptionFolderPath);
        } else {
            String destFilePath = destCompoundCaptionFolderPath + File.separator + fileName;
            FileUtils.createOrExistsFile(destFilePath);
            copySdcardFile(filePath, destFilePath);
        }

    }

    /**
     * 是否是Asset资源文件
     * Whether it is an Asset resource file
     *
     * @param filePath the file path
     * @return true is ,false not
     */
    private static boolean isAssetFile(String filePath) {
        return filePath.startsWith("assets:/") || (filePath.startsWith("file:///android_asset/"));
    }

    /**
     * 是否是Asset资源文件
     * replace Asset File Path
     *
     * @param filePath the file path
     * @return filePath
     */
    private static String replaceAssetFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        if (filePath.startsWith("assets:/")) {
            filePath = filePath.replace("assets:/", "");
        }
        if (filePath.startsWith("file:///android_asset/")) {
            filePath = filePath.replace("file:///android_asset/", "");
        }
        return filePath;
    }

    /**
     * 查找并复制指定文件到指定的路径
     * find and copy file to destFileFolder
     *
     * @param keyword                      查找关键字
     *                                     keyword
     * @param transitionFileFolder         查找的路径
     *                                     file dir
     * @param destTransitionRootFolderPath 复制到指定的路径的根路径
     *                                     dest file dir
     * @param resourceType                 复制的资源类型
     *                                     copy file resource
     * @return 是否查找并复制成功
     * is copy success
     */
    private static boolean findAndCopyFileToSdCard(String keyword, String transitionFileFolder, String destTransitionRootFolderPath, String resourceType) {
        File file = findFileFromSDCard(keyword, transitionFileFolder, resourceType);
        if (file != null) {
            String name = file.getName();
            String destFilePath = destTransitionRootFolderPath + File.separator + name;
            boolean orExistsFile = FileUtils.createOrExistsFile(destFilePath);
            if (orExistsFile) {
                copySdcardFile(file.getAbsolutePath(), destFilePath);
            }
            return true;
        }
        return false;
    }

    /**
     * 根据关键字在 sd卡指定路径查询文件
     * find file by keyword
     *
     * @param keyword      关键字
     *                     keyword
     * @param fromFileDir  查找路径
     *                     find file path
     * @param resourceType 查找资源的类型
     *                     find file type
     * @return
     */
    private static File findFileFromSDCard(String keyword, String fromFileDir, String resourceType) {
        if (TextUtils.isEmpty(keyword)) {
            return null;
        }
        File fileDir = new File(fromFileDir);
        if (!fileDir.exists()) {
            return null;
        }

        File[] files = fileDir.listFiles();
        if (files == null) {
            return null;
        }

        if (files.length == 0) {
            return null;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file == null) {
                continue;
            }

            if (!file.exists()) {
                continue;
            }
            String fileAbsolutePath = file.getAbsolutePath();
            if (fileAbsolutePath.contains(keyword) && fileAbsolutePath.contains(resourceType)) {
                return file;
            }
        }
        return null;
    }


    public static void copySdcardFile(String fromFile, String toFile) {
        InputStream fosfrom = null;
        OutputStream fosto = null;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fosfrom != null) {
                try {
                    fosfrom.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fosto != null) {
                try {
                    fosto.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 从assets 中找资源文件
     * find file from assets
     *
     * @param keyword      关键字  keyword
     * @param resourceType 资源类型  resourceType
     * @param assetDirPath 资源所在的文件夹 resource path
     * @return 文件名称   file name
     */
    private static String findFileFromAssets(Context context, String keyword, String resourceType, String assetDirPath) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] list = assetManager.list(assetDirPath);
            if (list == null) {
                return null;
            }
            for (String resourceName : list) {
                if (!TextUtils.isEmpty(resourceName) && resourceName.contains(keyword) && resourceName.endsWith(resourceType)) {
                    return resourceName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将文件从assets目录，考贝到 destFilePath 目录中
     * Move the file from the assets directory to the destFilePath directory
     *
     * @param fileName     文件名
     *                     file name
     * @param destFilePath 文件夹绝对路径
     *                     file path
     */
    private static void copyAssetsFile2SDCard(Context context, String fileName, String assetPath, String destFilePath) {
        try {
            InputStream inputStream = context.getAssets().open(assetPath);
            FileUtils.createOrExistsFile(destFilePath + File.separator + fileName);
            File file = new File(destFilePath + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            if (file.length() == 0) {
                fos = new FileOutputStream(file);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                inputStream.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对info.json 文件进行设置
     * set file info.json
     *
     * @param uuid
     * @param duration
     * @param footageCount
     * @param templateName
     * @param templateDesc
     * @param coverPath
     * @return
     */
    public static String getInfoDesc(String creator, String uuid, String ratio, long duration, int footageCount, String templateName, String templateDesc, String coverPath) {
        String mRatio = ratio.replace(RatioUtil.COLON_TAG, RatioUtil.V_TAG);

        TemplateInfo templateInfo = new TemplateInfo();
        templateInfo.setCover(coverPath);
        templateInfo.setMinSdkVersion("2.19.0");
        templateInfo.setName(templateName);
        templateInfo.setSupportedAspectRatio(mRatio);
        templateInfo.setDefaultAspectRatio(mRatio);

        TemplateInfo.TranslationBean translationBean = new TemplateInfo.TranslationBean();
        List<TemplateInfo.TranslationBean> beanList = new ArrayList<>();
        beanList.add(translationBean);
        templateInfo.setTranslation(beanList);

        templateInfo.setUuid(uuid);
        templateInfo.setVersion(1);
        templateInfo.setInnerAssetTotalCount(0);
        templateInfo.setFootageCount(footageCount);
        templateInfo.setDuration(duration);
        templateInfo.setCreator(creator);
        templateInfo.setDescription(templateDesc);
        return GsonUtils.toJson(templateInfo);
    }

    /**
     * 保存描述文件
     * save desc file
     *
     * @param uuid
     * @param duration
     * @param footageCount
     * @param templateName
     * @param templateDesc
     * @param coverPath
     * @param templatePath
     * @param templateVideoPath
     * @return
     */
    public static String getSimpleInfoDesc(String uuid, String ratio, long duration, int footageCount, String templateName, String templateDesc,
                                           String coverPath, String templatePath, String templateVideoPath) {
        String mRatio = ratio.replace(RatioUtil.COLON_TAG, RatioUtil.V_TAG);
        ExportTemplateDescInfo descInfo = new ExportTemplateDescInfo();
        descInfo.setCreateTime(System.currentTimeMillis());
        descInfo.setSupportedAspectRatio(mRatio);
        descInfo.setName(templateName);
        descInfo.setUuid(uuid);
        descInfo.setDescription(templateDesc);
        descInfo.setDuration(duration);
        descInfo.setCover(coverPath);
        descInfo.setFootageCount(footageCount);
        descInfo.setTemplatePath(templatePath);
        descInfo.setTemplateVideoPath(templateVideoPath);
        return GsonUtils.toJson(descInfo);
    }

}
