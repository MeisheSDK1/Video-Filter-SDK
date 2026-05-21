package com.meishe.sdkdemo.capturescene;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.VideoCompileUtil;

import java.util.Hashtable;

/**
 * Created by CaoZhiChao on 2018/11/14 14:10
 */
public class NvsStreamingContextUtil {
    private String TAG = "NvsStreamingContextUtil";
    private static final NvsStreamingContextUtil ourInstance = new NvsStreamingContextUtil();

    public NvsStreamingContext getmStreamingContext() {
        return mStreamingContext;
    }

    private NvsStreamingContext mStreamingContext;

    public static NvsStreamingContextUtil getInstance() {
        return ourInstance;
    }

    private NvsStreamingContextUtil() {
        mStreamingContext = NvsStreamingContext.getInstance();
        if (mStreamingContext == null) {
            String licensePath = "assets:/meishesdk.lic";
            mStreamingContext= TimelineUtil.initStreamingContext(MSApplication.getContext(), licensePath);
        }
    }

    /*
    * 预览
    * Seek
    * */
    public void seekTimeline(NvsTimeline mTimeline, long timestamp, int seekShowMode) {
        /*
         * param1: 当前时间线;Current timeline
         * param2: 时间戳,取值范围为[0, timeLine.getDuration()) (左闭右开区间);Timestamp, value range is (0, timeLine.getDuration ()) (left-close right-open interval)
         * param3: 图像预览模式;Image preview mode
         * param4: 引擎定位的特殊标志;Special flag for engine positioning
         * */
        assert mStreamingContext != null;
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
    }

    public void startNow(NvsTimeline mTimeline) {
        assert mStreamingContext != null;
        start(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), -1);
    }

    public int getEngineState() {
        assert mStreamingContext != null;
        return mStreamingContext.getStreamingEngineState();
    }

    public void start(NvsTimeline mTimeline, long startTime, long endTime) {
        assert mStreamingContext != null;
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    public void stop() {
        assert mStreamingContext != null;
        mStreamingContext.stop();
    }

    public boolean startRecording(String path) {
        return mStreamingContext.startRecording(path);
    }

    public void stopRecording() {
        mStreamingContext.stopRecording();
    }

    public void destory() {
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureVideoFx();
            mStreamingContext.removeCurrentCaptureScene();
            mStreamingContext.stop();
            mStreamingContext = null;
        }
    }

    public boolean isCaptureDeviceBackFacing(int captureDeviceIndex) {
        return mStreamingContext.isCaptureDeviceBackFacing(captureDeviceIndex);
    }
}
