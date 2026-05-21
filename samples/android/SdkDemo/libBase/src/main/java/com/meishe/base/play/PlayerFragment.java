package com.meishe.base.play;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.R;
import com.meishe.base.model.BaseFragment;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/2/7 10:02
 * @Description :共用的可更改屏幕尺寸的预览播放页面 Common preview playback page that can change screen size
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
@SuppressLint("ValidFragment")
public class PlayerFragment extends BaseFragment {
    private NvsLiveWindowExt mLiveWindow;
    private PlayEventListener mPlayerListener;
    private NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext;

    public PlayerFragment(NvsTimeline timeline, NvsStreamingContext streamingContext, PlayEventListener listener) {
        setPlayListener(listener);
        mTimeline = timeline;
        mStreamingContext = streamingContext;
    }
    /**
     * 设置播放事件监听
     * Set play event listening
     *
     * @param playListener the play listener
     */
    private void setPlayListener(PlayEventListener playListener) {
        this.mPlayerListener = playListener;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_palyer;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initView(View rootView) {
        mLiveWindow = rootView.findViewById(R.id.live_window);
        if (mTimeline != null) {
            setLiveWindowRatio(mTimeline.getVideoRes());
        }
    }

    @Override
    protected void initData() {
        connectTimeline();
    }

    /**
     * 设置播放窗口的比例
     * Set the live window ratio
     */
    public void setLiveWindowRatio(final NvsVideoResolution videoResolution) {
        if (isInitView && videoResolution != null) {
            if (mLiveWindow.getWidth() == 0 && mLiveWindow.getHeight() == 0) {
                mLiveWindow.post(new Runnable() {
                    @Override
                    public void run() {
                        setLiveWindowSize(videoResolution);
                    }
                });
            } else {
                setLiveWindowSize(videoResolution);
            }
        }
    }

    /**
     * 设置播放窗口的大小
     * Set the live window size
     */
    private void setLiveWindowSize(NvsVideoResolution videoResolution) {
        int viewWidth = mLiveWindow.getWidth();
        int viewHeight = mLiveWindow.getHeight();
        float timelineRatio = 1f * videoResolution.imageWidth / videoResolution.imageHeight;
        float viewRatio = viewWidth * 1F / viewHeight;
        ViewGroup.LayoutParams layoutParams = mLiveWindow.getLayoutParams();
        if (timelineRatio > viewRatio) {//宽对齐
            layoutParams.width = viewWidth;
            layoutParams.height = (int) (viewWidth / timelineRatio);
        } else {
            layoutParams.height = viewHeight;
            layoutParams.width = (int) (viewHeight * timelineRatio);
        }
        mLiveWindow.setLayoutParams(layoutParams);
    }

    /**
     * 连接时间线和实时预览图像窗口
     * Connect timeline with live window
     */
    public void connectTimeline() {
        //之所以在connectTimeline的时候设置callback是因为mStreamingContext是单例的，防止监听被替换
        //The reason callback is set during the connectTimeline is because the mStreamingContext is singleton, preventing the listener from being replaced
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
                if (mPlayerListener != null) {
                    mPlayerListener.playStopped(nvsTimeline);
                }
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                if (mPlayerListener != null) {
                    mPlayerListener.playBackEOF(nvsTimeline);
                }
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long currentPosition) {
                if (mPlayerListener != null) {
                    mPlayerListener.playbackTimelinePosition(nvsTimeline, currentPosition);
                }
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int state) {
                if (mPlayerListener != null) {
                    mPlayerListener.streamingEngineStateChanged(state);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });
        mStreamingContext.connectTimelineWithLiveWindowExt(mTimeline, mLiveWindow);
    }

    /**
     * 播放
     * Play
     *
     * @param flag the flag
     */
    public void playVideo(int flag) {
        if (mStreamingContext != null && mTimeline != null) {
            playVideo(mStreamingContext.getTimelineCurrentPosition(mTimeline), mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, flag);
        }
    }

    /**
     * 播放
     * Play
     *
     * @param startTime the start position
     * @param endTime   the end position
     * @param flag      the flag
     */
    public void playVideo(long startTime, long endTime, int flag) {
        playVideo(startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, flag);
    }

    /**
     * 播放
     * Play
     *
     * @param startTime     the start position
     * @param endTime       the end position
     * @param videoSizeMode video preview Mode
     * @param flag          the flag
     */
    public void playVideo(long startTime, long endTime, int videoSizeMode, int flag) {
        // 播放视频 play video
        if (mTimeline != null && mStreamingContext != null) {
            mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, videoSizeMode, true, flag);
        }
    }

    /**
     * 预览时间线的当前的时间点
     * Seek to the current position of timeline
     *
     * @param flag the flag
     */
    public void seekTimeline(int flag) {
        if (mStreamingContext != null)
            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), flag);
    }

    /**
     * 预览时间线的某个时间点
     * Seek to the position of timeline
     *
     * @param timestamp the timestamp
     * @param flag      the flag
     */
    public void seekTimeline(long timestamp, int flag) {
        seekTimeline(timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, flag);
    }

    /**
     * 预览时间线的某个时间点
     * Seek to the position of timeline
     *
     * @param timestamp     the timestamp
     * @param videoSizeMode video preview Mode
     * @param flag          the flag
     */
    public void seekTimeline(long timestamp, int videoSizeMode, int flag) {
        if (mStreamingContext != null && mTimeline != null) {
            mStreamingContext.seekTimeline(mTimeline, timestamp, videoSizeMode, flag);
        }
    }

    /**
     * 停止播放
     * Stop play
     */
    public void stopVideo() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    public interface PlayEventListener {

        void playBackEOF(NvsTimeline timeline);

        void playStopped(NvsTimeline timeline);

        void playbackTimelinePosition(NvsTimeline timeline, long stamp);

        void streamingEngineStateChanged(int state);
    }

}
