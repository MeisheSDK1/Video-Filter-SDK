package com.meishe.engine.interf;

import com.meicam.sdk.NvsTimeline;

/**
 * Created by CaoZhiChao on 2020/11/3 17:08
 * 时间线的播放回调PlaybackCallback和PlaybackCallback2。
 * 详见：https://www.meishesdk.com/android/doc_ch/html/content/classcom_1_1meicam_1_1sdk_1_1NvsStreamingContext.html
 */
public interface VideoFragmentListener {

    void playBackEOF(NvsTimeline timeline);

    void playStopped(NvsTimeline timeline);

    void playbackTimelinePosition(NvsTimeline timeline, long stamp);

    void streamingEngineStateChanged(int state);

    void onSeekingTimelinePosition(NvsTimeline timeline, long position);
}
