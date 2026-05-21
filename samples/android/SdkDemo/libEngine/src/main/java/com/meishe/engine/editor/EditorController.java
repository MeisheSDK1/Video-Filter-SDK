package com.meishe.engine.editor;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsClipCaption;
import com.meicam.sdk.NvsClipCompoundCaption;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsObject;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTrackCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.EditorEngine;
import com.meishe.engine.bean.BaseInfo;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.template.MeicamNvsTemplateFootageCorrespondingClipInfo;
import com.meishe.engine.bean.template.MeicamNvsTemplateFootageDesc;
import com.meishe.engine.bean.template.TemplateCaptionDesc;
import com.meishe.engine.interf.VideoFragmentListener;
import com.meishe.utils.PackageManagerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.meicam.sdk.NvsAssetPackageManager.TEIMPLATE_FOOTAGE_TYPE_AUDIO;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : CaoZhiChao
 * @CreateDate :2020/11/3 16:50
 * @Description :剪同款中SDK方法的管理类。详细方法请参考：https://www.meishesdk.com/android/doc_ch/html/content/index.html
 * @Description :cutsameModel to manager function of meicam sdk。Please refer to the detailed method：https://www.meishesdk.com/android/doc_ch/html/content/index.html
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class EditorController {
    public static final int ASSET_PACKAGE_TYPE_VIDEOFX = 0;
    public static final int ASSET_PACKAGE_TYPE_VIDEOTRANSITION = 1;
    public static final int ASSET_PACKAGE_TYPE_CAPTIONSTYLE = 2;
    public static final int ASSET_PACKAGE_TYPE_ANIMATEDSTICKER = 3;
    public static final int ASSET_PACKAGE_TYPE_THEME = 4;
    public static final int ASSET_PACKAGE_TYPE_CAPTURESCENE = 5;
    public static final int ASSET_PACKAGE_TYPE_ARSCENE = 6;
    public static final int ASSET_PACKAGE_TYPE_COMPOUND_CAPTION = 7;
    public static final int ASSET_PACKAGE_TYPE_CAPTION_CONTEXT = 8;
    public static final int ASSET_PACKAGE_TYPE_CAPTION_RENDERER = 9;
    public static final int ASSET_PACKAGE_TYPE_CAPTION_ANIMATION = 10;
    public static final int ASSET_PACKAGE_TYPE_CAPTION_IN_ANIMATION = 11;
    public static final int ASSET_PACKAGE_TYPE_CAPTION_OUT_ANIMATION = 12;
    public static final int ASSET_PACKAGE_TYPE_TEMPLATE = 13;
    public static final int ASSET_PACKAGE_STATUS_NOTINSTALLED = 0;
    public static final int ASSET_PACKAGE_STATUS_INSTALLING = 1;
    public static final int ASSET_PACKAGE_STATUS_READY = 2;
    public static final int ASSET_PACKAGE_STATUS_UPGRADING = 3;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR = 0;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_NAME = 1;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED = 2;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_WORKING_INPROGRESS = 3;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_NOT_INSTALLED = 4;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_IMPROPER_STATUS = 5;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_DECOMPRESSION = 6;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_INVALID_PACKAGE = 7;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_ASSET_TYPE = 8;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_PERMISSION = 9;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_META_CONTENT = 10;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_SDK_VERSION = 11;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_UPGRADE_VERSION = 12;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_IO = 13;
    public static final int ASSET_PACKAGE_MANAGER_ERROR_RESOURCE = 14;
    private static final String TAG = "EditorController";
    private NvsStreamingContext mStreamingContext;
    private NvsStreamingContext mAuxiliaryStreamingContext;
    private NvsTimeline mNvsTimeline;
    private NvsTimeline mAuxiliaryTimeline;
    private int mRatio;

    private static final EditorController ourInstance = new EditorController();

    public static EditorController getInstance() {
        return ourInstance;
    }

    private EditorController() {
        mStreamingContext = getStreamingContext();
    }

    /**
     * 获取当前的时间线对象
     * get now timeline object
     *
     * @return 时间线对象 timeline object
     */
    public NvsTimeline getNvsTimeline() {
        return mNvsTimeline;
    }

    /**
     * 设置当前的时间线对象
     * get now timeline object
     *
     * @param nvsTimeline 时间线对象 timeline object
     */
    public void setNvsTimeline(NvsTimeline nvsTimeline) {
        mNvsTimeline = nvsTimeline;
    }

    /**
     * 设置当前的辅助对象
     * get now timeline object
     *
     * @param nvsTimeline 时间线对象 timeline object
     */
    public void setAuxiliaryTimeline(NvsTimeline nvsTimeline) {
        mAuxiliaryTimeline = nvsTimeline;
    }

    /**
     * get now auxiliary timeline
     *
     * @return
     */
    public NvsTimeline getAuxiliaryTimeline() {
        return mAuxiliaryTimeline;
    }

    /**
     * Create auxiliary StreamingContext
     * <P></>
     * 创建辅助上下文
     *
     * @return NvsStreamingContext
     */
    public NvsStreamingContext createAuxiliaryStreamingContext() {
        if (mAuxiliaryStreamingContext == null) {
            mAuxiliaryStreamingContext = mStreamingContext.createAuxiliaryStreamingContext(0);
        }
        return mAuxiliaryStreamingContext;
    }

    /**
     * Destroy auxiliary StreamingContext
     * <P></>
     * 销毁辅助上下文
     */
    public void destroyAuxiliaryStreamingContext() {
        if (mAuxiliaryStreamingContext != null) {
            if (mAuxiliaryTimeline != null) {
                mAuxiliaryStreamingContext.removeTimeline(mAuxiliaryTimeline);
            }
            mStreamingContext.destoryAuxiliaryStreamingContext(mAuxiliaryStreamingContext);
            mAuxiliaryStreamingContext = null;
        }
    }

    /**
     * 停止context引擎
     * stop context engine
     */
    public void stop() {
        if (mStreamingContext == null) {
            Log.e(TAG, "stop: mStreamingContext is null!");
            return;
        }
        mStreamingContext.stop();
    }

    /**
     * 从当前时间线位置开始播放
     * start play from now time in timeline
     */
    public void playNow() {
        if ((mStreamingContext == null) || (mNvsTimeline == null)) {
            Log.e(TAG, "playNow: mStreamingContext or mNvsTimeline is null!");
            return;
        }
        playNow(mStreamingContext.getTimelineCurrentPosition(mNvsTimeline), -1);
    }

    /**
     * 从输入的参数时间开始播放时间线
     * start play from input param
     *
     * @param start 开始时间 start time
     */
    public void playNow2(long start) {
        if ((mStreamingContext == null) || (mNvsTimeline == null)) {
            Log.e(TAG, "playNow: mStreamingContext or mNvsTimeline is null!");
            return;
        }
        playNow(start, -1);
    }

    /**
     * 从时间线的当前时间播放到输入的结束时间
     * start play from now timeline time to the end of input param
     *
     * @param end 结束时间 end time
     */
    public void playNow(long end) {
        if ((mStreamingContext == null) || (mNvsTimeline == null)) {
            Log.e(TAG, "playNow: mStreamingContext or mNvsTimeline is null!");
            return;
        }
        playNow(mStreamingContext.getTimelineCurrentPosition(mNvsTimeline), end);
    }

    /**
     * Now time long.
     *
     * @return 时间线的当前时间 now time of timeline
     */
    public long nowTime() {
        if ((mStreamingContext == null) || (mNvsTimeline == null)) {
            Log.e(TAG, "nowTime: mStreamingContext or mNvsTimeline is null!");
            return -1;
        }
        return mStreamingContext.getTimelineCurrentPosition(mNvsTimeline);
    }

    /**
     * 从输入的起点播放到输入的终点
     * start play from start to end
     *
     * @param start 起始时间 start time
     * @param end   结束时间 end time
     */
    public void playNow(long start, long end) {
        if ((mStreamingContext == null) || (mNvsTimeline == null)) {
            Log.e(TAG, "playNow: mStreamingContext or mNvsTimeline is null!");
            return;
        }
        if (end == -1) {
            end = mNvsTimeline.getDuration();
        }
        if ((end - start) <= 100000) {
            start = 0;
        }
        int flag = 0;
        if (EditorEngine.getInstance().isUseFaceShape()) {
            flag = NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME;
        }
        mStreamingContext.playbackTimeline(mNvsTimeline, start, end, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, flag);
    }

    /**
     * 时间线当前位置移动到输入位置，同时刷新预览画面
     * seek timeline time to timestamp and refresh liveWindow frame
     *
     * @param timestamp    新的时间线位置  new timeline position
     * @param seekShowMode 界面刷新的flag,默认是0  the flag to refresh liveWindow frame，The default is 0。
     */
    public void seekTimeline(long timestamp, int seekShowMode) {
        int flag = 0;
        if (EditorEngine.getInstance().isUseFaceShape()) {
            flag = NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME;
        }
        if (mNvsTimeline != null && mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mStreamingContext.stop(NvsStreamingContext.STREAMING_ENGINE_RECORDING_FLAG_SOFTWARE_VIDEO_INTRA_FRAME_ONLY_FAST_STOP);
        }
        mStreamingContext.seekTimeline(mNvsTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode | flag);
    }

    /**
     * 时间线当前位置移动到输入位置，同时刷新预览画面
     * seek timeline time to timestamp and refresh liveWindow frame
     *
     * @param timestamp 新的时间线位置  new timeline position
     */
    public void seekTimeline(long timestamp) {
        seekTimeline(timestamp, 0);
    }

    /**
     * 刷新当前预览画面
     * refresh liveWindow frame of now position
     */
    public void seekTimeline() {
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mNvsTimeline), 0);
    }

    /**
     * Gets current engine state.
     *
     * @return 当前引擎状态 now context engine state
     */
    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    /**
     * Gets streaming context.
     *
     * @return 获取context对象 get Streaming Context
     */
    public NvsStreamingContext getStreamingContext() {
        if (mStreamingContext == null) {
            synchronized (NvsStreamingContext.class) {
                if (mStreamingContext == null) {
                    mStreamingContext = NvsStreamingContext.getInstance();
                }
            }
        }
        return mStreamingContext;
    }

    /**
     * 链接时间线到liveWindow上，同时设置相应的接口
     * Connect timeline with live window.Set the interface at the same time
     *
     * @param mLiveWindow            the live window
     * @param mVideoFragmentCallBack video fragment的相关回调。the video fragment call back
     */
    public void connectTimelineWithLiveWindow(Object mLiveWindow, final VideoFragmentListener mVideoFragmentCallBack) {
        if (mStreamingContext == null || mNvsTimeline == null || mLiveWindow == null) {
            return;
        }
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playStopped(nvsTimeline);
                }
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playBackEOF(nvsTimeline);
                }
            }
        });
        mStreamingContext.setSeekingCallback(new NvsStreamingContext.SeekingCallback() {
            @Override
            public void onSeekingTimelinePosition(NvsTimeline nvsTimeline, long l) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.onSeekingTimelinePosition(nvsTimeline, l);
                }
            }
        });
        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playbackTimelinePosition(nvsTimeline, cur_position);
                }
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.streamingEngineStateChanged(i);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });
        if (mLiveWindow instanceof NvsLiveWindow) {
            NvsLiveWindow liveWindow = (NvsLiveWindow) mLiveWindow;
            mStreamingContext.connectTimelineWithLiveWindow(mNvsTimeline, liveWindow);
        } else if (mLiveWindow instanceof NvsLiveWindowExt) {
            NvsLiveWindowExt liveWindow = (NvsLiveWindowExt) mLiveWindow;
            mStreamingContext.connectTimelineWithLiveWindowExt(mNvsTimeline, liveWindow);
        }
    }

    /**
     * 根据引擎状态判断当前是否是处于播放状态。
     * Determine whether the current state is playing based on the engine state.
     *
     * @return the boolean
     */
    public boolean isPlaying() {
        if (mStreamingContext == null) {
            Log.e(TAG, "isPlaying: mStreamingContext is null!");
            return false;
        }
        int state = mStreamingContext.getStreamingEngineState();
        return state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK;
    }

    /**
     * 获取时间线的长度
     * Get timeline duration.
     *
     * @return the timeline duration
     */
    public long getTimelineDuration() {
        if (mNvsTimeline == null) {
            Log.e(TAG, "getTimelineDuration: mNvsTimeline is null!");
            return 0;
        }
        return mNvsTimeline.getDuration();
    }

    /**
     * 根据视频路径获取视频的长度
     * Gets video duration by filePath
     *
     * @param videoPath the video path
     * @return the video duration
     */
    public long getVideoDuration(String videoPath) {
        if (mStreamingContext == null) {
            Log.e(TAG, "getVideoDuration: mStreamingContext is null!");
            return -1;
        }
        NvsAVFileInfo fileInfo = mStreamingContext.getAVFileInfo(videoPath);
        return fileInfo.getDuration();
    }

    /**
     * 缩略图控件中，时间线时间转化为对应的长度
     * In thumbnail view, the timeline time convert to the view length
     *
     * @param duration             the duration
     * @param mPixelPerMicrosecond 比例尺。the m pixel per microsecond
     * @return the int
     */
    public int durationToLength(long duration, double mPixelPerMicrosecond) {
        return (int) Math.floor(duration * mPixelPerMicrosecond + 0.5D);
    }

    /**
     * 缩略图控件中，view中的长度转化为时间线时间
     * In thumbnail view, the view length convert to the timeline time
     *
     * @param dx                   the dx
     * @param mPixelPerMicrosecond 比例尺。the m pixel per microsecond
     * @return the long
     */
    public long lengthToDuration(int dx, double mPixelPerMicrosecond) {
        return (long) Math.floor(dx / mPixelPerMicrosecond + 0.5D);
    }

    /**
     * 根据输入时间捕捉当前画面并转化为bitmap,默认图片比例为1:1
     * Grab image from timeline async.Default image scale 1:1
     *
     * @param timestamp 输入的时间线时间。the timeline position
     * @param callback  the callback
     */
    public void grabImageFromTimelineAsync(long timestamp, NvsStreamingContext.ImageGrabberCallback callback) {
        mStreamingContext.setImageGrabberCallback(callback);
        mStreamingContext.grabImageFromTimelineAsync(mNvsTimeline, timestamp, new NvsRational(1, 1), 0);
    }

    /**
     * 根据输入时间捕捉当前画面并转化为bitmap，默认图片比例为1:1
     * Grab image from timeline async.Default image scale 1:1
     *
     * @param timestamp 输入的时间线时间。the timeline position
     */
    public void grabImageFromTimelineAsync(long timestamp) {
        mStreamingContext.grabImageFromTimelineAsync(mNvsTimeline, timestamp, new NvsRational(1, 1), 0);
    }

    /**
     * 根据输入时间捕捉当前画面并转化为bitmap
     * Grab image from timeline async.
     *
     * @param timestamp  输入的时间线时间。the timeline position
     * @param proxyScale 需要的图片比例。the proxy scale
     * @param flags      自定义的flag。 the flags
     * @param callback   the callback
     */
    public void grabImageFromTimelineAsync(long timestamp, NvsRational proxyScale, int flags, NvsStreamingContext.ImageGrabberCallback callback) {
        mStreamingContext.setImageGrabberCallback(callback);
        mStreamingContext.grabImageFromTimelineAsync(mNvsTimeline, timestamp, proxyScale, flags);
    }

    /**
     * 设置捕捉当前画面的回调
     * Sets image grabber callback.
     *
     * @param callback the callback
     */
    public void setImageGrabberCallback(NvsStreamingContext.ImageGrabberCallback callback) {
        mStreamingContext.setImageGrabberCallback(callback);
    }

    /**
     * 同步的捕捉当前时间线位置的画面
     * Grab bitmap from timeline bitmap.
     *
     * @param timestamp 输入的时间线时间。the timeline position
     * @return the bitmap
     */
    public Bitmap grabBitmapFromTimeline(long timestamp) {
        return grabBitmapFromTimeline(timestamp, new NvsRational(1, 1));
    }

    /**
     * 异步的捕捉当前时间线位置的画面
     * Grab bitmap from auxiliary timeline bitmap Async.
     *
     * @param timestamp 输入的时间线时间。the timeline position
     * @param callback  the callback
     */
    public void grabBitmapFromAuxiliaryTimelineAsync(long timestamp, NvsStreamingContext.ImageGrabberCallback callback) {
        if (mAuxiliaryStreamingContext == null || mAuxiliaryTimeline == null) {
            return;
        }
        int flag = 0;
        if (EditorEngine.getInstance().isUseFaceShape()) {
            flag |= NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME;
        }
        mAuxiliaryStreamingContext.setImageGrabberCallback(callback);

        boolean b = mAuxiliaryStreamingContext.grabImageFromTimelineAsync(mAuxiliaryTimeline, timestamp, new NvsRational(1, 1), flag);
        Log.e("grabImageFrom", "grabImageFrom = " + b + " duration=" + mAuxiliaryTimeline.getDuration() + "  time =" + timestamp);
    }

    /**
     * 同步的捕捉当前时间线位置的画面
     * Grab bitmap from timeline bitmap.
     *
     * @param timestamp  输入的时间线时间。the timeline position
     * @param proxyScale 需要的图片比例。the proxy scale
     * @return the bitmap
     */
    public Bitmap grabBitmapFromTimeline(long timestamp, NvsRational proxyScale) {
        int flag = 0;
        if (EditorEngine.getInstance().isUseFaceShape()) {
            flag = NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME;
        }
        return mStreamingContext.grabImageFromTimeline(mNvsTimeline, timestamp, proxyScale, flag);
    }

    /**
     * 自定义生成时间线。
     * Compile time line custom.
     *
     * @param path             生成路径。the path
     * @param videoHeight      自定义的视频高度。the video height
     * @param mParamsTable     自定义参数。the params table
     * @param compileCallback  the compile callback
     * @param compileCallback2 the compile callback 2
     */
    public void compileTimeLineCustom(String path, int videoHeight, Hashtable<String, Object> mParamsTable,
                                      NvsStreamingContext.CompileCallback compileCallback, NvsStreamingContext.CompileCallback2 compileCallback2) {
        mStreamingContext.setCompileConfigurations(null);
        mStreamingContext.setCompileCallback(compileCallback);
        mStreamingContext.setCompileCallback2(compileCallback2);
        mStreamingContext.setCompileConfigurations(mParamsTable);
//        if (mRatio == 0) {
        mStreamingContext.setCustomCompileVideoHeight(videoHeight);
//        }

        NvsVideoResolution videoRes = mNvsTimeline.getVideoRes();
        int imageWidth = videoRes.imageWidth;
        int imageHeight = videoRes.imageHeight;
        Log.d(TAG, "imageWidth->" + imageWidth + " imageHeight->" + imageHeight);

        if (mStreamingContext.compileTimeline(mNvsTimeline, 0, mNvsTimeline.getDuration(),
                path, NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM,
                NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_DISABLE_ALIGN_VIDEO_SIZE
                        | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_IGNORE_TIMELINE_VIDEO_SIZE
                        | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_ORIGIN_VIDEO_FRAME)) {
            Log.d(TAG, "compileTimeLine: begin!");
        } else {
            compileCallback.onCompileFailed(mNvsTimeline);
            Log.e(TAG, "compileTimeLine: failed!");
        }
    }

    /**
     * 生成时间线
     * Compile time line.
     *
     * @param path             the path
     * @param videoResolution  生成文件输出的视频分辨率级别。the video resolution
     * @param mParamsTable     the m params table
     * @param compileCallback  the compile callback
     * @param compileCallback2 the compile callback 2
     */
    public void compileTimeLine(String path, int videoResolution, Hashtable<String, Object> mParamsTable,
                                NvsStreamingContext.CompileCallback compileCallback, NvsStreamingContext.CompileCallback2 compileCallback2) {
        mStreamingContext.setCompileCallback(compileCallback);
        mStreamingContext.setCompileCallback2(compileCallback2);
        mStreamingContext.setCompileConfigurations(mParamsTable);
        if (mStreamingContext.compileTimeline(mNvsTimeline, 0, mNvsTimeline.getDuration(),
                path, videoResolution,
                NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_HOST_VIDEO_FRAME
                        | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_ORIGIN_VIDEO_FRAME)) {
            Log.d(TAG, "compileTimeLine: begin!");
        } else {
            compileCallback.onCompileFailed(mNvsTimeline);
            Log.e(TAG, "compileTimeLine: failed!");
        }
    }

    /**
     * 清理生成时的自定义配置
     * Clear compile configurations.
     */
    public void clearCompileConfigurations() {
        mStreamingContext.setCompileConfigurations(null);
    }

    /**
     * 异步安装包的方法
     * Install asset packaged no synchronous.
     *
     * @param path          the path
     * @param type          the type
     * @param stringBuilder the string builder
     * @param callback      the callback
     */
    public void installAssetPackagedNoSynchronous(String path, int type, StringBuilder stringBuilder, NvsAssetPackageManager.AssetPackageManagerCallback callback) {
        if (mStreamingContext == null) {
            Log.e(TAG, "installAssetPackaged: mStreamingContext is null!");
            return;
        }
        PackageManagerUtil.installAssetPackageAsynchronous(path, null, type, stringBuilder, callback);

//        int state = mStreamingContext.getAssetPackageManager().getAssetPackageStatus()
//        int state = mStreamingContext.getAssetPackageManager().installAssetPackage(path, null, type, false, stringBuilder);
        Log.e(TAG, "installAssetPackagedNoSynchronous: " + path, new Exception(""));
//        if ((state == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR || state == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED)
//                && (callback != null)) {
//            callback.onFinishAssetPackageInstallation(stringBuilder.toString(), path, type, state);
//        }
    }

    /**
     * 卸载包
     * Uninstall asset package.
     *
     * @param packageId 包id。the package id
     * @param type      the type
     */
    public void uninstallAssetPackage(String packageId, int type) {
        if (mStreamingContext == null) {
            Log.e(TAG, "uninstallAssetPackage: mStreamingContext is null!");
            return;
        }
        int state = mStreamingContext.getAssetPackageManager().uninstallAssetPackage(packageId, type);
        Log.d(TAG, "uninstallAssetPackage: " + state);
    }

    /**
     * 获取模板中全部的footage信息
     * Gets template footage desc.
     *
     * @param uuidString 模板的uuid。the uuid string
     * @return the template footage desc
     */
    public List<NvsAssetPackageManager.NvsTemplateFootageDesc> getTemplateFootageDesc(String uuidString) {
        List<NvsAssetPackageManager.NvsTemplateFootageDesc> nvsTemplateFootageDescs = new ArrayList<>();
        if (mStreamingContext == null) {
            Log.e(TAG, "getTemplateFootageDesc: mStreamingContext is null!");
            return nvsTemplateFootageDescs;
        }
        nvsTemplateFootageDescs = mStreamingContext.getAssetPackageManager().getTemplateFootages(uuidString);
        return nvsTemplateFootageDescs;
    }

    /**
     * 获取模板中视频的footage信息
     * Gets template footage desc video.
     *
     * @param uuidString 模板的uuid 。the uuid string
     * @return the template footage desc video
     */
    public List<MeicamNvsTemplateFootageDesc> getTemplateFootageDescVideo(String uuidString) {
        List<MeicamNvsTemplateFootageDesc> nvsTemplateFootageDescs = new ArrayList<>();
        if (mStreamingContext == null) {
            Log.e(TAG, "getTemplateFootageDesc: mStreamingContext is null!");
            return nvsTemplateFootageDescs;
        }

        List<NvsAssetPackageManager.NvsTemplateFootageDesc> nvsTemplateFootageDescList = mStreamingContext.getAssetPackageManager().getTemplateFootages(uuidString);
        List<MeicamNvsTemplateFootageDesc> meicamNvsTemplateFootageDescList = new ArrayList<>();
        for (NvsAssetPackageManager.NvsTemplateFootageDesc desc : nvsTemplateFootageDescList) {
            MeicamNvsTemplateFootageDesc meicamNvsTemplateFootageDesc = new MeicamNvsTemplateFootageDesc();
            meicamNvsTemplateFootageDesc.setNvsTemplateFootageDesc(desc);
            meicamNvsTemplateFootageDescList.add(meicamNvsTemplateFootageDesc);
        }

        for (MeicamNvsTemplateFootageDesc nvsTemplateFootageDesc : meicamNvsTemplateFootageDescList) {
            if (needDealFootageDesc(nvsTemplateFootageDesc)) {
                getAllNvsTemplateFootageDescs(nvsTemplateFootageDescs, nvsTemplateFootageDesc);
            }
        }
        return nvsTemplateFootageDescs;
    }

    /**
     * 递归获取所有的NvsTemplateFootage
     * Gets all nvs template footage descs.
     *
     * @param nvsTemplateFootageDescs the nvs template footage
     * @param nvsTemplateFootageDesc  the nvs template footage desc
     */
    private void getAllNvsTemplateFootageDescs(List<MeicamNvsTemplateFootageDesc> nvsTemplateFootageDescs,
                                               MeicamNvsTemplateFootageDesc nvsTemplateFootageDesc) {
        if (nvsTemplateFootageDesc.timelineClipFootages != null && nvsTemplateFootageDesc.timelineClipFootages.size() != 0) {
            //保存中间的trackIndex和clipIndex
            nvsTemplateFootageDesc.addClipTrackIndexInTimeline(nvsTemplateFootageDesc.correspondingClipInfos.get(0).trackIndex);
            nvsTemplateFootageDesc.addClipIndexInTimeline(nvsTemplateFootageDesc.correspondingClipInfos.get(0).clipIndex);

            List<MeicamNvsTemplateFootageDesc> meicamNvsTemplateFootageDescList = new ArrayList<>();
            for (NvsAssetPackageManager.NvsTemplateFootageDesc desc : nvsTemplateFootageDesc.timelineClipFootages) {
                MeicamNvsTemplateFootageDesc meicamNvsTemplateFootageDesc = new MeicamNvsTemplateFootageDesc();
                meicamNvsTemplateFootageDesc.setNvsTemplateFootageDesc(desc);
                meicamNvsTemplateFootageDescList.add(meicamNvsTemplateFootageDesc);
            }
            for (int i = 0; i < meicamNvsTemplateFootageDescList.size(); i++) {
                MeicamNvsTemplateFootageDesc timelineTemplateFootageDesc = meicamNvsTemplateFootageDescList.get(i);
                if (needDealFootageDesc(timelineTemplateFootageDesc)) {
                    timelineTemplateFootageDesc.setClipIndexInTimelineList(nvsTemplateFootageDesc.getClipIndexInTimelineList());
                    timelineTemplateFootageDesc.setClipTrackIndexInTimelineList(nvsTemplateFootageDesc.getClipTrackIndexInTimelineList());
                    getAllNvsTemplateFootageDescs(nvsTemplateFootageDescs, timelineTemplateFootageDesc);
                }
            }
        } else {
            if (needDealFootageDesc(nvsTemplateFootageDesc)) {
                nvsTemplateFootageDescs.add(nvsTemplateFootageDesc);
            }
        }
    }

    /**
     * 获取所有视频片段
     * Gets template video clip.
     *
     * @param uuidString the uuid string
     * @return the template video clip
     */
    public List<MeicamNvsTemplateFootageCorrespondingClipInfo> getTemplateVideoClip(String uuidString) {
        List<MeicamNvsTemplateFootageCorrespondingClipInfo> mClipList = new ArrayList<>();
        List<MeicamNvsTemplateFootageDesc> templateList = getTemplateFootageDescVideo(uuidString);
        if (templateList != null) {
            for (MeicamNvsTemplateFootageDesc footage : templateList) {
                if (footage.type == TEIMPLATE_FOOTAGE_TYPE_AUDIO) {
                    //audio类型无用。
                    continue;
                }
                List<MeicamNvsTemplateFootageCorrespondingClipInfo> correspondingClipInfos = new ArrayList<>();
                for (NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo clipInfo : footage.correspondingClipInfos) {
                    MeicamNvsTemplateFootageCorrespondingClipInfo correspondingClipInfo = new MeicamNvsTemplateFootageCorrespondingClipInfo();
                    correspondingClipInfo.setNvsTemplateFootageCorrespondingClipInfo(clipInfo);
                    correspondingClipInfos.add(correspondingClipInfo);
                }
                for (MeicamNvsTemplateFootageCorrespondingClipInfo info : correspondingClipInfos) {
                    info.setClipTrackIndexInTimelineList(footage.getClipTrackIndexInTimelineList());
                    info.setClipIndexInTimelineList(footage.getClipIndexInTimelineList());
                    if (!EditorController.getInstance().isPlaceHolder(info.trackIndex, info.clipIndex)) {
                        info.canReplace = footage.canReplace;
                        info.setFootageID(footage.id);
                        mClipList.add(info);
                    }
                }
            }

            Collections.sort(mClipList, new Comparator<MeicamNvsTemplateFootageCorrespondingClipInfo>() {
                @Override
                public int compare(MeicamNvsTemplateFootageCorrespondingClipInfo o1, MeicamNvsTemplateFootageCorrespondingClipInfo o2) {
                    int i = (int) (o1.inpoint - o2.inpoint);
                    if (i == 0) {
                        return o1.trackIndex - o2.trackIndex;
                    }
                    return i;
                }
            });
            int groupIndex = -1;
            //适配AE模板，再计算一下编组信息
            Map<String, List<MeicamNvsTemplateFootageCorrespondingClipInfo>> map = new HashMap<>();
            String key = "";
            List<MeicamNvsTemplateFootageCorrespondingClipInfo> tempList = null;
            for (MeicamNvsTemplateFootageCorrespondingClipInfo templateClip : mClipList) {
                key = templateClip.getFootageID();
                tempList = map.get(key);
                if (null == tempList) {
                    tempList = new ArrayList<>();
                    map.put(key, tempList);
                }
                tempList.add(templateClip);
            }

            for (List<MeicamNvsTemplateFootageCorrespondingClipInfo> templateClips : map.values()) {
                if (templateClips.size() > 1) {
                    //有同一编组
                    groupIndex += 1;
                    for (MeicamNvsTemplateFootageCorrespondingClipInfo templateClip : templateClips) {
                        templateClip.setGroupIndex(groupIndex);
                    }
                }
            }
            return mClipList;
        }
        return mClipList;
    }

    /**
     * 是否是占位clip
     * Whether is the clip placeholder.
     *
     * @param trackIndex the track index
     * @param clipIndex  the clip index
     * @return Whether is the clip placeholder.true:yes:false:no.
     */
    public boolean isPlaceHolder(int trackIndex, int clipIndex) {
        if (mNvsTimeline == null) {
            LogUtils.e("timeline is null!");
            return false;
        }
        if (trackIndex != 0) {
            return false;
        }
        NvsVideoTrack videoTrack = mNvsTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }
        int clipCount = videoTrack.getClipCount();
        if (clipIndex != clipCount - 1) {
            return false;
        }
        NvsVideoClip videoClip = videoTrack.getClipByIndex(clipIndex);
        return videoClip != null && videoClip.getFilePath() != null && videoClip.getFilePath().endsWith(CommonData.IMAGE_BLACK_FILE_NAME);
    }

    /**
     * 是否需要处理该FootageDesc
     * Need deal footage desc boolean.
     *
     * @param nvsTemplateFootageDesc the nvs template footage desc
     * @return the boolean
     */
    private boolean needDealFootageDesc(NvsAssetPackageManager.NvsTemplateFootageDesc nvsTemplateFootageDesc) {
        return nvsTemplateFootageDesc.type != NvsAssetPackageManager.TEIMPLATE_FOOTAGE_TYPE_AUDIO;
    }

    /**
     * 获取模板中的字幕信息。
     * Gets template captions.
     *
     * @param uuidString 模板的uuid。the uuid string
     * @return the template captions
     */
    public List<TemplateCaptionDesc> getTemplateCaptionsForStandard(String uuidString) {
        List<TemplateCaptionDesc> templateCaptionDescList = new ArrayList<>();
        List<NvsAssetPackageManager.NvsTemplateCaptionDesc> templateCaptionDescs = new ArrayList<>();
        if (mStreamingContext == null) {
            Log.e(TAG, "getTemplateFootageDesc: mStreamingContext is null!");
            return templateCaptionDescList;
        }
        templateCaptionDescs = mStreamingContext.getAssetPackageManager().getTemplateCaptions(uuidString);
        if (null != templateCaptionDescs && templateCaptionDescs.size() > 0) {
            for (NvsAssetPackageManager.NvsTemplateCaptionDesc desc : templateCaptionDescs) {
                TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
                templateCaptionDesc.setOriginalText(desc.text);
                templateCaptionDesc.setText(desc.text);
                templateCaptionDesc.replaceId = desc.replaceId;
                templateCaptionDesc.trackIndex = desc.trackIndex;
                templateCaptionDesc.clipIndex = desc.clipIndex;
                templateCaptionDescList.add(templateCaptionDesc);
            }
        }
        return templateCaptionDescList;
    }


    public List<TemplateCaptionDesc> getTemplateAllCaptionDescs(String templateId) {
        /*
         *普通字幕数据
         * Common caption data.
         */
        List<TemplateCaptionDesc> templateCaptionDescList = getTemplateCaptions(templateId);

        /*
         *组合字幕数据
         * Compound caption data.
         */
        List<TemplateCaptionDesc> nvsTemplateCompoundCaptionDescs = getTemplateCompoundCaptions(templateId);
        if (nvsTemplateCompoundCaptionDescs != null && nvsTemplateCompoundCaptionDescs.size() != 0) {
            templateCaptionDescList.addAll(nvsTemplateCompoundCaptionDescs);
        }
        Collections.sort(templateCaptionDescList);
        return templateCaptionDescList;
    }


    /**
     * 获取模板中的字幕信息。
     * Gets template captions.
     *
     * @param uuidString 模板的uuid。the uuid string
     * @return the template captions
     */
    public List<TemplateCaptionDesc> getTemplateCaptions(String uuidString) {
        List<TemplateCaptionDesc> templateCaptionDescs = new ArrayList<>();
        if (mStreamingContext == null) {
            Log.e(TAG, "getTemplateFootageDesc: mStreamingContext is null!");
            return templateCaptionDescs;
        }
        List<TemplateCaptionDesc> templateCaptionDescArrayList = new ArrayList<>();
        //模板中字幕描述信息  Description of the subtitle in the template
        List<NvsAssetPackageManager.NvsTemplateCaptionDesc> nvsTemplateCaptionDescs = mStreamingContext.getAssetPackageManager().getTemplateCaptions(uuidString);
        for (NvsAssetPackageManager.NvsTemplateCaptionDesc nvsTemplateCaptionDesc : nvsTemplateCaptionDescs) {
            templateCaptionDescArrayList.add(TemplateCaptionDesc.create(nvsTemplateCaptionDesc));
        }

        for (TemplateCaptionDesc templateCaptionDesc : templateCaptionDescArrayList) {
            getAllNvsTemplateCaptionDescs(templateCaptionDescs, templateCaptionDesc);
        }
        return templateCaptionDescs;
    }

    private void getAllNvsTemplateCaptionDescs(List<TemplateCaptionDesc> templateCaptionDescs,
                                               TemplateCaptionDesc nvsTemplateCaptionDesc) {

        if (nvsTemplateCaptionDesc != null && nvsTemplateCaptionDesc.subCaptions.size() != 0) {
            //保存中间的trackIndex和clipIndex  Save trackIndex and clipIndex in the middle
            nvsTemplateCaptionDesc.addClipTrackIndexInTimeline(nvsTemplateCaptionDesc.trackIndex);
            nvsTemplateCaptionDesc.addClipIndexInTimeline(nvsTemplateCaptionDesc.clipIndex);
            for (TemplateCaptionDesc timelineTemplateFootageDesc : nvsTemplateCaptionDesc.subCaptions) {
                timelineTemplateFootageDesc.setClipIndexInTimelineList(nvsTemplateCaptionDesc.getClipIndexInTimelineList());
                timelineTemplateFootageDesc.setClipTrackIndexInTimelineList(nvsTemplateCaptionDesc.getClipTrackIndexInTimelineList());
                getAllNvsTemplateCaptionDescs(templateCaptionDescs, timelineTemplateFootageDesc);
            }
        } else {
            if (nvsTemplateCaptionDesc != null && !TextUtils.isEmpty(nvsTemplateCaptionDesc.replaceId)) {
                templateCaptionDescs.add(nvsTemplateCaptionDesc);
            }
        }
    }

    /**
     * 获取模板中的复合字幕信息。
     * Gets template captions.
     *
     * @param uuidString 模板的uuid。the uuid string
     * @return the template compound captions
     */
    public List<TemplateCaptionDesc> getTemplateCompoundCaptions(String uuidString) {
        List<TemplateCaptionDesc> templateCompoundCaptionDescs = new ArrayList<>();
        if (mStreamingContext == null) {
            Log.e(TAG, "getTemplateCompoundCaptions: mStreamingContext is null!");
            return templateCompoundCaptionDescs;
        }
        List<TemplateCaptionDesc> templateCompoundCaptionDescArrayList = new ArrayList<>();
        List<NvsAssetPackageManager.NvsTemplateCompoundCaptionDesc> nvsTemplateCaptionDescs =
                mStreamingContext.getAssetPackageManager().getTemplateCompoundCaptions(uuidString);
        for (NvsAssetPackageManager.NvsTemplateCompoundCaptionDesc nvsTemplateCaptionDesc : nvsTemplateCaptionDescs) {
            templateCompoundCaptionDescArrayList.add(TemplateCaptionDesc.create(nvsTemplateCaptionDesc));
        }

        for (TemplateCaptionDesc templateCaptionDesc : templateCompoundCaptionDescArrayList) {
            getAllNvsTemplateCaptionDescs(templateCompoundCaptionDescs, templateCaptionDesc);
        }
        return templateCompoundCaptionDescs;
    }

    /**
     * 获取模板中的字幕信息。
     * Gets template captions.
     * 字幕顺序 -->时间线字幕-时间线复合字幕-片段字幕-片段复合字幕
     *
     * @param nvsTimeline 创建的timeline
     * @return the template captions
     */
    public List<TemplateCaptionDesc> getTemplateCaptions(NvsTimeline nvsTimeline) {
        List<TemplateCaptionDesc> templateCaptionDescs = new ArrayList<>();
        if (mStreamingContext == null) {
            Log.e(TAG, "getTemplateFootageDesc: mStreamingContext is null!");
            return templateCaptionDescs;
        }
        //获取所有时间线字幕  Get all timeline captions
        NvsTimelineCaption firstCaption = nvsTimeline.getFirstCaption();
        while (null != firstCaption) {
            TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
            templateCaptionDesc.text = firstCaption.getText();
            templateCaptionDesc.trackIndex = 0;
            templateCaptionDesc.setInPoint(firstCaption.getInPoint());
            templateCaptionDesc.setNvsCaption(firstCaption);
            templateCaptionDesc.setCaptionType(TemplateCaptionDesc.TemplateCaptionType.TIMELINE_CAPTION);
            templateCaptionDescs.add(templateCaptionDesc);
            firstCaption = nvsTimeline.getNextCaption(firstCaption);
        }
        NvsTimelineCompoundCaption firstCompoundCaption = nvsTimeline.getFirstCompoundCaption();
        while (null != firstCompoundCaption) {
            Log.e("caption", "NvsTimelineCompoundCaption");
            //循环取出所有组合字幕的子字幕的内容，并告知自字幕轨道index  Loop retrieves the contents of all subsubtitles that combine subtitles and tells the subtitle track index
            int captionCount = firstCompoundCaption.getCaptionCount();
            for (int i = 0; i < captionCount; i++) {
                TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
                templateCaptionDesc.trackIndex = 0;
                templateCaptionDesc.text = firstCompoundCaption.getText(i);
                templateCaptionDesc.setInPoint(firstCompoundCaption.getInPoint());
                templateCaptionDesc.setNvsCompoundCaption(firstCompoundCaption);
                templateCaptionDesc.setCaptionIndex(i);
                templateCaptionDesc.setCaptionType(TemplateCaptionDesc.TemplateCaptionType.TIMELINE_COMPOUND_CAPTION);
                templateCaptionDescs.add(templateCaptionDesc);
            }
            firstCompoundCaption = nvsTimeline.getNextCaption(firstCompoundCaption);
        }

        NvsVideoTrack videoTrackByIndex = nvsTimeline.getVideoTrackByIndex(0);
        if (null != videoTrackByIndex) {

            int clipCount = videoTrackByIndex.getClipCount();
            //这个值用来累计当前的字幕对应的clip之前的时长 This value is used to accumulate the time before the clip corresponding to the current title
            long captionInClipInPoint = 0;
            for (int i = 0; i < clipCount; i++) {
                NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(i);
                if (null != clipByIndex) {
                    //获取所有clip上的字幕  Get subtitles on all clips
                    NvsClipCaption clipCaption = clipByIndex.getFirstCaption();
                    while (null != clipCaption) {
                        TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
                        templateCaptionDesc.text = clipCaption.getText();
                        templateCaptionDesc.trackIndex = 0;
                        templateCaptionDesc.clipIndex = i;
                        templateCaptionDesc.setInPoint(captionInClipInPoint + clipCaption.getInPoint());
                        templateCaptionDesc.setNvsCaption(clipCaption);
                        templateCaptionDesc.setCaptionType(TemplateCaptionDesc.TemplateCaptionType.CLIP_CAPTION);
                        templateCaptionDescs.add(templateCaptionDesc);
                        clipCaption = clipByIndex.getNextCaption(clipCaption);
                    }
                    NvsClipCompoundCaption clipCompoundCaption = clipByIndex.getFirstCompoundCaption();
                    while (null != clipCompoundCaption) {
                        int captionCount = clipCompoundCaption.getCaptionCount();
                        for (int j = 0; j < captionCount; j++) {
                            TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
                            templateCaptionDesc.trackIndex = 0;
                            templateCaptionDesc.clipIndex = i;
                            templateCaptionDesc.text = clipCompoundCaption.getText(j);
                            templateCaptionDesc.setCaptionIndex(j);
                            templateCaptionDesc.setInPoint(captionInClipInPoint + clipCompoundCaption.getInPoint());
                            templateCaptionDesc.setNvsCompoundCaption(clipCompoundCaption);
                            templateCaptionDesc.setCaptionType(TemplateCaptionDesc.TemplateCaptionType.CLIP_COMPOUND_CAPTION);
                            templateCaptionDescs.add(templateCaptionDesc);
                        }

                        clipCompoundCaption = clipByIndex.getNextCaption(clipCompoundCaption);
                    }
                    captionInClipInPoint += clipByIndex.getTrimOut() - clipByIndex.getTrimIn();
                }
            }

        }

        return templateCaptionDescs;
    }

    /**
     * 判断当前的timeline上是否有字幕
     * Determines whether there are subtitles on the current timeline
     *
     * @param nvsTimeline 创建的timeline
     * @return 所有字幕集合 All subtitle set
     */
    public boolean hasTemplateCaptions(NvsTimeline nvsTimeline) {
        if (nvsTimeline == null) {
            Log.e(TAG, "getTemplateFootageDesc: mStreamingContext is null!");
            return false;
        }
        NvsTimelineCaption firstCaption = nvsTimeline.getFirstCaption();
        if (null != firstCaption) {
            return true;
        }
        NvsTimelineCompoundCaption firstCompoundCaption = nvsTimeline.getFirstCompoundCaption();
        if (null != firstCompoundCaption) {
            return true;
        }
        NvsVideoTrack videoTrackByIndex = nvsTimeline.getVideoTrackByIndex(0);
        if (null == videoTrackByIndex) {
            return false;
        }
        boolean hasClipCaption = false;
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(i);
            if (null != clipByIndex) {
                NvsClipCaption clipCaption = clipByIndex.getFirstCaption();
                if (null != clipCaption) {
                    hasClipCaption = true;
                    break;
                }
                NvsClipCompoundCaption clipCompoundCaption = clipByIndex.getFirstCompoundCaption();
                if (null != clipCompoundCaption) {
                    hasClipCaption = true;
                    break;
                }

            }
        }
        return hasClipCaption;
    }


    /**
     * 根据模板uuid创建时间线。
     * Create timeline nvs timeline.
     *
     * @param templateId       安装返回的值
     * @param templateFootAges key:NvsTemplateFootageDesc里记录的id；value:添加clip的文件路径
     * @return nvs timeline
     */
    public NvsTimeline createTimeline(String templateId, List<NvsStreamingContext.templateFootageInfo> templateFootAges) {
        if (mStreamingContext == null) {
            Log.e(TAG, "createTimeline: mStreamingContext is null!");
            return null;
        }
        return mStreamingContext.createTimeline(templateId, templateFootAges);
    }

    /**
     * 根据模板uuid创建时间线。
     * Create timeline nvs timeline.
     *
     * @param context          NvsStreaming 上下文
     * @param templateId       安装返回的值
     * @param templateFootAges key:NvsTemplateFootageDesc里记录的id；value:添加clip的文件路径
     * @return nvs timeline
     */
    public NvsTimeline createTimeline(NvsStreamingContext context, String templateId, List<NvsStreamingContext.templateFootageInfo> templateFootAges) {
        if (context == null) {
            Log.e(TAG, "createTimeline: mStreamingContext is null!");
            return null;
        }
        return context.createTimeline(templateId, templateFootAges);
    }


    /**
     * 根据轨道和片段index获取对应的对象
     * Gets video clip by index.
     *
     * @param trackIndex the track index
     * @param clipIndex  the clip index
     * @return the video clip by index
     */
    public NvsVideoClip getVideoClipByIndex(int trackIndex, int clipIndex) {
        if (mNvsTimeline == null) {
            Log.e(TAG, "getVideoClipByIndex: mNvsTimeline is null!");
            return null;
        }
        int trackCount = mNvsTimeline.videoTrackCount();
        if (trackIndex >= trackCount) {
            Log.e(TAG, "getVideoClipByIndex: trackIndex is to BIG! trackIndex: " + trackIndex + "  trackCount: " + trackCount);
            return null;
        }
        NvsVideoTrack nvsVideoTrack = mNvsTimeline.getVideoTrackByIndex(trackIndex);
        int clipCount = nvsVideoTrack.getClipCount();
        if (clipIndex >= clipCount) {
            Log.e(TAG, "getVideoClipByIndex: clipIndex is to BIG! clipIndex: " + clipIndex + "  clipCount: " + clipCount);
            return null;
        }
        return nvsVideoTrack.getClipByIndex(clipIndex);
    }

    public NvsVideoClip getVideoClipByTemplateFootageCorrespondingClipInfo(MeicamNvsTemplateFootageCorrespondingClipInfo item) {
        List<Integer> clipList = item.getClipIndexInTimelineList();
        List<Integer> trackList = item.getClipTrackIndexInTimelineList();
        long clipInPoint = 0;
        NvsVideoClip meicamVideoClip;
        NvsTimeline meicamTimeline = mNvsTimeline;
        if (meicamTimeline == null) {
            meicamTimeline = getNvsTimeline();
        }
        for (int i = 0; i < trackList.size(); i++) {
            int trackIndexTimeline = trackList.get(i);
            int clipIndexTimeline = clipList.get(i);
            NvsVideoTrack meicamVideoTrack = meicamTimeline.getVideoTrackByIndex(trackIndexTimeline);
            if (meicamVideoTrack != null) {
                meicamVideoClip = meicamVideoTrack.getClipByIndex(clipIndexTimeline);
                if (meicamVideoClip != null) {
                    clipInPoint += meicamVideoClip.getInPoint();
                    meicamTimeline = meicamVideoClip.getInternalTimeline();
                }
            }
        }
        if (meicamTimeline != null) {
            NvsVideoTrack meicamVideoTrack = meicamTimeline.getVideoTrackByIndex(item.trackIndex);
            if (meicamVideoTrack != null) {
                NvsVideoClip videoClip = meicamVideoTrack.getClipByIndex(item.clipIndex);
                if (videoClip != null) {
                    if (videoClip.getInternalTimeline() == null) {
                        String bestSeekTime = videoClip.getTemplateAttachment(NvsObject.TEMPLATE_KEY_BEST_SEEK_TIME);
                        if (TextUtils.isEmpty(bestSeekTime)) {
                            bestSeekTime = "0";
                        }
                        if (Long.parseLong(bestSeekTime) < 0) {
                            item.setRealInpoint(clipInPoint + videoClip.getInPoint());
                        } else {
                            item.setRealInpoint(Long.parseLong(bestSeekTime));
                        }

                        return videoClip;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 模板中使用：通过attachment属性获取对应的字幕。
     * Gets caption by attachment.
     *
     * @param replaceId the replace id
     * @return the caption by attachment
     */
    public NvsTimelineCaption getCaptionByAttachment(String replaceId) {
        if (mNvsTimeline == null) {
            Log.e(TAG, "getCaptionByAttachment: mNvsTimeline is null!");
            return null;
        }
        NvsTimelineCaption nextCaption = mNvsTimeline.getFirstCaption();
        return loadTimelineCaptionToTemplate(mNvsTimeline, nextCaption, replaceId);
    }

    private static NvsTimelineCaption loadTimelineCaptionToTemplate(NvsTimeline timeline, NvsTimelineCaption caption, String replaceId) {
        if (caption != null) {
            String id = caption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_REPLACE_ID);
            if (TextUtils.equals(id, replaceId)) {
                return caption;
            }
            NvsTimelineCaption nextCaption = timeline.getNextCaption(caption);
            return loadTimelineCaptionToTemplate(timeline, nextCaption, replaceId);
        }
        return null;
    }


    public NvsVideoClip getVideoClipByTemplateFootageCorrespondingClipInfo
            (NvsTimeline meicamTimeline, List<Integer> clipList, List<Integer> trackList, int trackIndex, int clipIndex) {
        NvsVideoClip meicamVideoClip;
        if (meicamTimeline == null) {
            meicamTimeline = getNvsTimeline();
        }
        for (int i = 0; i < trackList.size(); i++) {
            int trackIndexTimeline = trackList.get(i);
            int clipIndexTimeline = clipList.get(i);
            NvsVideoTrack meicamVideoTrack = meicamTimeline.getVideoTrackByIndex(trackIndexTimeline);
            if (meicamVideoTrack != null) {
                meicamVideoClip = meicamVideoTrack.getClipByIndex(clipIndexTimeline);
                if (meicamVideoClip != null) {
                    meicamTimeline = meicamVideoClip.getInternalTimeline();
                }
            }
        }
        if (meicamTimeline != null) {
            NvsVideoTrack meicamVideoTrack = meicamTimeline.getVideoTrackByIndex(trackIndex);
            if (meicamVideoTrack != null) {
                NvsVideoClip videoClip = meicamVideoTrack.getClipByIndex(clipIndex);
                if ((videoClip != null)) {
                    return videoClip;
                }
            }
        }
        return null;
    }

    /**
     * 更改时间线的画幅
     * Change video size.
     *
     * @param width  宽是4的倍数。the width
     * @param height 高是2的倍数。the height
     */
    public void changeVideoSize(int width, int height) {
        if (mNvsTimeline == null) {
            Log.e(TAG, "changeVideoSize: mNvsTimeline is null!");
            return;
        }
        mNvsTimeline.changeVideoSize(width, height);
    }

    /**
     * 获取时间线的宽高
     * Gets timeline width and height.
     *
     * @return the timeline width and height
     */
    public Point getTimelineWidthAndHeight() {
        if (mNvsTimeline == null) {
            Log.e(TAG, "getTimelineWidthAndHeight: mNvsTimeline is null!");
            return new Point(0, 0);
        }
        NvsVideoResolution nvsVideoResolution = mNvsTimeline.getVideoRes();
        return new Point(nvsVideoResolution.imageWidth, nvsVideoResolution.imageHeight);
    }

    public void changeTemplateAspectRatio(String templateId, int ratio) {
        mRatio = ratio;
        LogUtils.d("mRatio===" + mRatio);
        if (mStreamingContext == null) {
            Log.e(TAG, "changeTemplateAspectRatio: mStreamingContext is null!");
            return;
        }
        mStreamingContext.getAssetPackageManager().changeTemplateAspectRatio(templateId, ratio);
    }

    public int getAssetPackageSupportedAspectRatio(String assetPackageId, int type) {
        if (mStreamingContext == null) {
            Log.e(TAG, "getAssetPackageSupportedAspectRatio: mStreamingContext is null!");
            return -1;
        }
        return mStreamingContext.getAssetPackageManager().getAssetPackageSupportedAspectRatio(assetPackageId, type);
    }

    public NvsAVFileInfo getFileInfo(String path) {
        if (mStreamingContext == null) {
            Log.e(TAG, "getFileInfo: mStreamingContext is null!");
            return null;
        }
        NvsAVFileInfo info = mStreamingContext.getAVFileInfo(path);
        return info;
    }

    public int getRatio() {
        return mRatio;
    }

    /**
     * 获取导出自定义高度
     * Gets the exported custom height
     *
     * @param resolution 分辨率
     * @return
     */
    public int getCustomHeight(int resolution) {
        int height = 0;
        if (mRatio == 0) {
            NvsVideoResolution videoResolution = getNvsTimeline().getVideoRes();
            int widthImage = videoResolution.imageWidth;
            int heightImage = videoResolution.imageHeight;
            height = (widthImage > heightImage ? resolution : (resolution / widthImage * heightImage));
            return height;
        }
        if (mRatio == BaseInfo.AspectRatio_16v9) { // 16:9
            height = resolution;
        } else if (mRatio == BaseInfo.AspectRatio_1v1) { //1:1
            height = resolution;
        } else if (mRatio == BaseInfo.AspectRatio_9v16) { //9:16
            height = (int) (resolution / 9.0 * 16);
        } else if (mRatio == BaseInfo.AspectRatio_3v4) { // 3:4
            height = (int) (resolution / 3.0 * 4.0);
        } else if (mRatio == BaseInfo.AspectRatio_4v3) { //4:3ExportTemplateSettingActivity
            height = resolution;
        }
        return height;
    }

    /**
     * 根据选择的画幅比例 返回宽高信息
     * Returns width and height information based on the selected scale
     *
     * @param ratio 画幅比
     * @return
     */
    public NvsVideoResolution getVideoResolutionByRatio(int ratio) {
        NvsVideoResolution nvsVideoResolution = new NvsVideoResolution();
        int width = 1920;
        int height = 1080;
        if (ratio == BaseInfo.AspectRatio_16v9 || ratio == BaseInfo.AspectRatio_NoFitRatio) { // 16:9
            width = 1920;
            height = 1080;
        } else if (ratio == BaseInfo.AspectRatio_1v1) { //1:1
            width = 1080;
        } else if (ratio == BaseInfo.AspectRatio_9v16) { //9:16
            width = 1080;
            height = 1920;
        } else if (ratio == BaseInfo.AspectRatio_3v4) { // 3:4
            width = 1080;
            height = (int) (width / 3.0 * 4.0);
        } else if (ratio == BaseInfo.AspectRatio_4v3) { //4:3ExportTemplateSettingActivity
            width = (int) (height / 3.0 * 4.0);
        }
        nvsVideoResolution.imageWidth = width;
        nvsVideoResolution.imageHeight = height;
        return nvsVideoResolution;
    }

    /**
     * 获取所有的字幕信息
     * Get all the subtitle information
     *
     * @param templateCaptionDescList
     * @return
     */
    public void getAllCaption(List<TemplateCaptionDesc> templateCaptionDescList) {
        for (TemplateCaptionDesc templateCaptionDesc : templateCaptionDescList) {
            if (templateCaptionDesc.isCaption()) {
                getCaptionByTemplateCaptionDesc(templateCaptionDesc);
            }
        }
    }

    public NvsCaption getCaptionByTemplateCaptionDesc(TemplateCaptionDesc templateCaptionDesc) {
        NvsVideoClip meicamVideoClip = null;
        NvsTimeline meicamTimeline = getNvsTimeline();
        long clipInPoint = 0;
        List<NvsTimeline> allTimeline = new ArrayList<>();
        for (int i = 0; i < templateCaptionDesc.getClipTrackIndexInTimelineList().size(); i++) {
            int trackIndexTimeline = templateCaptionDesc.getClipTrackIndexInTimelineList().get(i);
            int clipIndexTimeline = templateCaptionDesc.getClipIndexInTimelineList().get(i);
            NvsVideoTrack meicamVideoTrack = meicamTimeline.getVideoTrackByIndex(trackIndexTimeline);
            meicamVideoClip = meicamVideoTrack.getClipByIndex(clipIndexTimeline);
            clipInPoint += meicamVideoClip.getInPoint();

            meicamTimeline = meicamVideoClip.getInternalTimeline();

            allTimeline.add(meicamTimeline);
        }
        if (templateCaptionDesc.trackIndex < 0 && templateCaptionDesc.clipIndex < 0) {
            NvsTimelineCaption firstCaption = meicamTimeline.getFirstCaption();
            NvsTimelineCaption meicamCaptionClip = loadTimelineCaptionToTemplate(meicamTimeline, firstCaption, templateCaptionDesc.replaceId);
            if (meicamCaptionClip != null) {
                long bestSeekTime = Long.parseLong(meicamCaptionClip.getTemplateAttachment(NvsObject.TEMPLATE_KEY_BEST_SEEK_TIME));
                if (bestSeekTime < 0) {
                    templateCaptionDesc.setInPoint(clipInPoint + meicamCaptionClip.getInPoint() +
                            (meicamCaptionClip.getOutPoint() - meicamCaptionClip.getInPoint()) / 2);
                } else {
                    templateCaptionDesc.setInPoint(bestSeekTime);
                }
                templateCaptionDesc.setNvsCaption(meicamCaptionClip);
            } else {
                LogUtils.e("字幕获取失败");
            }
            if (meicamCaptionClip != null) {
                String groupID = meicamCaptionClip.getTemplateAttachment(NvsObject.TEMPLATE_KEY_FX_GROUP);
                if (!TextUtils.isEmpty(groupID)) {
                    templateCaptionDesc.setGroupID(groupID);
                }
            }
            return meicamCaptionClip;

        } else if (templateCaptionDesc.trackIndex >= 0 && templateCaptionDesc.clipIndex < 0) {
            NvsVideoTrack meicamVideoTrack = meicamTimeline.getVideoTrackByIndex(templateCaptionDesc.trackIndex);
            NvsTrackCaption firstCaption = meicamVideoTrack.getFirstCaption();
            while (null != firstCaption) {
                String id = firstCaption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_REPLACE_ID);
                if (TextUtils.equals(id, templateCaptionDesc.replaceId)) {
                    long bestSeekTime = Long.parseLong(firstCaption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_BEST_SEEK_TIME));
                    if (bestSeekTime < 0) {
                        templateCaptionDesc.setInPoint(clipInPoint + firstCaption.getInPoint() +
                                (firstCaption.getOutPoint() - firstCaption.getInPoint()) / 2);
                    } else {
                        templateCaptionDesc.setInPoint(bestSeekTime);
                    }
                    templateCaptionDesc.setNvsCaption(firstCaption);
                    String groupID = firstCaption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_FX_GROUP);
                    if (!TextUtils.isEmpty(groupID)) {
                        templateCaptionDesc.setGroupID(groupID);
                    }
                    break;
                } else {
                    firstCaption = meicamVideoTrack.getNextCaption(firstCaption);
                }
            }

        } else if (templateCaptionDesc.trackIndex >= 0) {
            NvsVideoTrack meicamVideoTrackCaption = meicamTimeline.
                    getVideoTrackByIndex(templateCaptionDesc.trackIndex);
            NvsVideoClip meicamVideoClipCaption = meicamVideoTrackCaption.
                    getClipByIndex(templateCaptionDesc.clipIndex);
            NvsClipCaption firstCaption = meicamVideoClipCaption.getFirstCaption();
            while (null != firstCaption) {
                String id = firstCaption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_REPLACE_ID);
                if (TextUtils.equals(id, templateCaptionDesc.replaceId)) {
                    long bestSeekTime = Long.parseLong(firstCaption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_BEST_SEEK_TIME));
                    if (bestSeekTime < 0) {
                        templateCaptionDesc.setInPoint(clipInPoint + firstCaption.getInPoint() +
                                (firstCaption.getOutPoint() - firstCaption.getInPoint()) / 2);
                    } else {
                        templateCaptionDesc.setInPoint(bestSeekTime);
                    }
                    templateCaptionDesc.setNvsCaption(firstCaption);
                    String groupID = firstCaption.getTemplateAttachment(NvsObject.TEMPLATE_KEY_FX_GROUP);
                    if (!TextUtils.isEmpty(groupID)) {
                        templateCaptionDesc.setGroupID(groupID);
                    }
                    break;
                } else {
                    firstCaption = meicamVideoClipCaption.getNextCaption(firstCaption);
                }
            }
        }
        return null;
    }
}
