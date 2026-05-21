package com.meishe.sdkdemo.utils;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsVideoResolution;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by admin on 2018/11/13.
 */

public class VideoCompileUtil {
    public static String getCompileVideoPath(Context context) {
        String compilePath = PathUtils.getVideoCompileDirPath();
        if (compilePath == null)
            return null;
        long currentMilis = System.currentTimeMillis();
        String videoName = "/meicam_" + String.valueOf(currentMilis) + ".mp4";
        compilePath += videoName;
        return compilePath;
    }

    public static String getCompileLivePhotoPath() {
        String compilePath = PathUtils.getVideoCompileDirPath();
        if (compilePath == null)
            return null;
        long currentMilis = System.currentTimeMillis();
        String videoName = "/meicam_live_" + currentMilis + ".jpg";
        compilePath += videoName;
        return compilePath;
    }

    private static final String TAG = "VideoCompileUtil";

    public static void compileVideo(NvsStreamingContext context,
                                    NvsTimeline timeline,
                                    String compileVideoPath,
                                    long startTime,
                                    long endTime, int videoResolutionGrade, int customHeight) {
        if (context == null || timeline == null || compileVideoPath.isEmpty()) {
            return;
        }
        context.stop();
        /*
         * 之前配置清空
         * Clear before configuration
         * */
        context.setCompileConfigurations(null);
        double bitrate = ParameterSettingValues.instance().getCompileBitrate();
        Hashtable<String, Object> compileConfigurations = new Hashtable<>();
        int encoderFlag = 0;
        if (ParameterSettingValues.instance().disableDeviceEncorder()) {
            encoderFlag = NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_DISABLE_HARDWARE_ENCODER;
        }
        if (bitrate != 0) {
            compileConfigurations.put(NvsStreamingContext.COMPILE_BITRATE, bitrate * 1000000);
        }
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        Log.d("ParameterSetting", "compile supportHEVC:" + settingValues.isSupportHEVC());
        if (settingValues.isSupportHEVC()) {
            compileConfigurations.put(NvsStreamingContext.COMPILE_VIDEO_ENCODER_NAME, Constants.HDR_EXPORT_HEVC);
            Log.d("ParameterSetting", "compile name:" + NvsStreamingContext.COMPILE_VIDEO_ENCODER_NAME + " value:" + Constants.HDR_EXPORT_HEVC);
            if (settingValues.getBitDepth() != NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT) {
                compileConfigurations.put(NvsStreamingContext.COMPILE_VIDEO_HDR_COLOR_TRANSFER, settingValues.getExportConfig());
                Log.d("ParameterSetting", "compile name:" + NvsStreamingContext.COMPILE_VIDEO_HDR_COLOR_TRANSFER +
                        " value:" + settingValues.getExportConfig());
            }
        }
        Hashtable<String, Object> compileConfigurations1 = VideoCompileUtil.getCompileConfigurations(compileConfigurations);
        context.setCompileConfigurations(compileConfigurations1);
        if (customHeight != -1) {
            context.setCustomCompileVideoHeight(customHeight);
            context.compileTimeline(timeline, startTime, endTime, compileVideoPath,
                    NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM, NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, encoderFlag
                            | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_HOST_VIDEO_FRAME
                            | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_ORIGIN_VIDEO_FRAME
                            | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_IGNORE_TIMELINE_VIDEO_SIZE);
        } else {
            context.compileTimeline(timeline, startTime, endTime, compileVideoPath,
                    videoResolutionGrade, NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, encoderFlag
                            | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_HOST_VIDEO_FRAME
                            | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_ORIGIN_VIDEO_FRAME
            );
        }
    }

    public static void compileVideo(NvsStreamingContext context,
                                    NvsTimeline timeline,
                                    String compileVideoPath,
                                    long startTime,
                                    long endTime) {
        NvsVideoResolution videoRes = timeline.getVideoRes();
        int resultHeight = Util.getCustomHeight(videoRes.imageWidth, videoRes.imageHeight);
        compileVideo(context, timeline, compileVideoPath, startTime, endTime, NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM, resultHeight);
    }


    /**
     * 得到编译的配置参数，这个里边包含共有的参数配置
     * Gets the compiled configuration parameter, which contains the common parameter configuration
     *
     * @param hashtable 单独的参数配置
     * @return
     */
    public static Hashtable<String, Object> getCompileConfigurations(Hashtable<String, Object> hashtable) {
        Hashtable<String, Object> compileConfigurations = new Hashtable<>();
        if (hashtable != null) {
            compileConfigurations.putAll(hashtable);
        }
        //共有的配置在这里添加
        //Common configurations are added here
        boolean quickPack = ParameterSettingValues.instance().isQuickPack();
        compileConfigurations.put(NvsStreamingContext.COMPILE_USE_OPERATING_RATE, quickPack);
        return compileConfigurations;
    }

    public static NvsTimelineAnimatedSticker addLogoWaterMark(Context context, NvsStreamingContext mStreamingContext, NvsTimeline mTimeline) {
        NvsTimelineAnimatedSticker mLogoSticker = null;
        String logoTemplatePath = "assets:/E14FEE65-71A0-4717-9D66-3397B6C11223/E14FEE65-71A0-4717-9D66-3397B6C11223.5.animatedsticker";
        String imagePath = "assets:/logo.png";
        StringBuilder packageId = new StringBuilder();
        int error = mStreamingContext.getAssetPackageManager().installAssetPackage(logoTemplatePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER, true, packageId);
        if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR
                || error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            mLogoSticker = mTimeline.addCustomAnimatedSticker(0, mTimeline.getDuration(), packageId.toString(), imagePath);
            if (mLogoSticker == null) {
                return null;
            }
            mLogoSticker.setScale(0.3f);
            List<PointF> list = mLogoSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return null;
            }
            Collections.sort(list, new Util.PointXComparator());
            int offset = ScreenUtils.dip2px(context, 10);
            float xPos = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x - offset);

            Collections.sort(list, new Util.PointYComparator());
            float y_dis = list.get(3).y - list.get(0).y;
            float yPos = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - y_dis - offset;

            PointF logoPos = new PointF(xPos, yPos);
            mLogoSticker.translateAnimatedSticker(logoPos);
        }
        return mLogoSticker;
    }

    public static void removeLogoSticker(NvsTimelineAnimatedSticker mLogoSticker, NvsTimeline mTimeline) {
        if (mLogoSticker != null) {
            mTimeline.removeAnimatedSticker(mLogoSticker);
        }
    }
}
