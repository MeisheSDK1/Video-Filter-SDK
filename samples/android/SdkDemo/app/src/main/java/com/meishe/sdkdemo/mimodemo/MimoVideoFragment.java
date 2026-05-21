package com.meishe.sdkdemo.mimodemo;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.mimodemo.common.Constants;
import com.meishe.sdkdemo.mimodemo.common.dataInfo.TimelineData;
import com.meishe.sdkdemo.mimodemo.common.utils.ScreenUtils;
import com.meishe.sdkdemo.mimodemo.common.utils.TimeUtils;
import com.meishe.sdkdemo.mimodemo.common.view.DrawRect;
import com.meishe.sdkdemo.utils.TimelineUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MimoVideoFragment extends Fragment {
    private static final float DEFAULT_SCALE_VALUE = 1.0f;
    private static final long BASE_VALUE = 100000;
    private static final String PARAM_MAX_DURATION = "max_duration";
    private static final int MESSAGE_RESET_PLATBACK_STATE = 100;
    private NvsLiveWindow mLiveWindow;
    private NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private RelativeLayout mPlayerLayout;
    private long mStartTime;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RESET_PLATBACK_STATE:
                    playVideo(mStartTime, mStartTime + getDuration());
                    break;
            }
            return false;
        }
    });
    private long mMaxDuration;
    private View mPlayBar;
    private View mPlayButton;
    private TextView mTotalDurationView;
    private TextView mCurrentPlaytimeView;
    private SeekBar mSeekBar;
    private ImageView mPlayButtonImage;
    private OnPlayProgressChangeListener mOnPlayProgessChangeListener;
    private DrawRect mDrawRect;
    private NvsTimelineCaption mCurCaption;
    private int mEditMode = 0;
    private List<PointF> pointFListLiveWindow;
    private NvsTimelineCompoundCaption mCurCompoundCaption;
    private NvsTimelineAnimatedSticker mCurAnimateSticker;
    private int mStickerMuteIndex;
    private OnThemeCaptionSeekListener mThemeCaptionSeekListener;
    private OnLiveWindowClickListener mLiveWindowClickListener;
    private VideoCaptionTextEditListener mCaptionTextEditListener;
    private OnCompoundCaptionListener mCompoundCaptionListener;
    private OnStickerMuteListener mStickerMuteListener;
    private AssetEditListener mAssetEditListener;
    private OnFragmentLoadFinishedListener mFragmentLoadFinisedListener;

    public void setOnPlayProgressChangeListener(OnPlayProgressChangeListener onPlayProgessChangeListener) {
        this.mOnPlayProgessChangeListener = onPlayProgessChangeListener;
    }

    public MimoVideoFragment() {
    }

    public static MimoVideoFragment newInstance(long maxDuration) {
        MimoVideoFragment fragment = new MimoVideoFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_MAX_DURATION, maxDuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mMaxDuration = getArguments().getLong(PARAM_MAX_DURATION);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mimo_fragment_video, container, false);
        mLiveWindow = (NvsLiveWindow) rootView.findViewById(R.id.liveWindow);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
        mPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.playerLayout);
        mDrawRect = (DrawRect) rootView.findViewById(R.id.draw_rect);
        mPlayBar = rootView.findViewById(R.id.playBarLayout);
        mPlayButton = rootView.findViewById(R.id.playLayout);
        mCurrentPlaytimeView = (TextView) rootView.findViewById(R.id.currentPlaytime);
        mTotalDurationView = (TextView) rootView.findViewById(R.id.totalDuration);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.playSeekBar);
        mPlayButtonImage = (ImageView) rootView.findViewById(R.id.playImage);
        initListener();
        return rootView;
    }

    private void initListener() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    stopEngine();
                } else {
                    if (mTimeline == null) {
                        return;
                    }
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    //已经在指定片段上播放的时长
                    //The amount of time that has been played on the specified segment
                    long alreadyPlayDuration = startTime - mStartTime;
                    long endTime = startTime + getDuration() - (alreadyPlayDuration);
                    playVideo(startTime, endTime);
                    if (mOnPlayProgessChangeListener != null) {
                        mOnPlayProgessChangeListener.onPlayStateChanged(true);
                    }
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private long currentTime = 0L;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime = getDuration() * progress / 100 + mStartTime;
                    seekTimeline(currentTime, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                    updateCurPlayTime(currentTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                NvsStreamingContext.getInstance().setTimelineScaleForSeek(mTimeline, mTimeline.getDuration() / 1000000D / seekBar.getWidth());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTimeline(currentTime, 0);
                playVideo(currentTime, currentTime + getDuration());
            }
        });
    }

    private void updateCurPlayTime(long currentTime) {
        mCurrentPlaytimeView.setText(TimeUtils.formatTimeStrWithUs(currentTime - mStartTime));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        if (mFragmentLoadFinisedListener != null) {
            mFragmentLoadFinisedListener.onLoadFinished();
        }
    }

    public void initData() {
        setLiveWindowRatio();
        initPlayBar();
        initDrawRectListener();
    }

    public void setMaxDuration(long maxDuration) {
        this.mMaxDuration = maxDuration;
    }

    public void setEditMode(int mode) {
        mEditMode = mode;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public long getDuration() {
        if (mMaxDuration > 0L) {
            return mMaxDuration;
        }
        if (mTimeline == null) {
            return 0L;
        }
        return mTimeline.getDuration();
    }

    private void initPlayBar() {
        mCurrentPlaytimeView.setText(TimeUtils.formatTimeStrWithUs(0));
        mTotalDurationView.setText(TimeUtils.formatTimeStrWithUs(getDuration()));
    }

    private void setLiveWindowRatio() {
        if (null == mTimeline) {
            return;
        }
        int makeRatio = TimelineData.instance().getMakeRatio();
        setLiveWindowRatio(makeRatio);
        connectTimelineWithLiveWindow();
    }

    private void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null)
            return;
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                mHandler.sendEmptyMessage(MESSAGE_RESET_PLATBACK_STATE);
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updatePlayProgress(cur_position);
                if (mOnPlayProgessChangeListener != null) {
                    mOnPlayProgessChangeListener.onPlayProgressChanged(cur_position);
                }
            }
        });
        mStreamingContext.setSeekingCallback(new NvsStreamingContext.SeekingCallback() {
            @Override
            public void onSeekingTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updatePlayProgress(cur_position);
                if (mOnPlayProgessChangeListener != null) {
                    mOnPlayProgessChangeListener.onPlayProgressChanged(cur_position);
                }
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayButtonImage.setBackgroundResource(R.mipmap.icon_pause);
                } else {
                    mPlayButtonImage.setBackgroundResource(R.mipmap.icon_play);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });

        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
        //playVideo(mStartTime, mStartTime + getDuration());
    }

    public boolean isPlaying() {
        return getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK;
    }

    private void updatePlayProgress(long curPosition) {
        float progress = ((float) (curPosition - mStartTime) / (float) getDuration());
        mSeekBar.setProgress((int) (progress * 100));
        updateCurPlayTime(curPosition);
    }

    private void setLiveWindowRatio(int ratio) {
        ViewGroup.LayoutParams layoutParams = mPlayerLayout.getLayoutParams();
        int titleHeight = ScreenUtils.dip2px(MSApplication.getContext(), 44);
        int palyBarHeight = ScreenUtils.dip2px(MSApplication.getContext(), 48);
        int bottomHeight = ScreenUtils.dip2px(MSApplication.getContext(), 145);
        int statusHeight = ScreenUtils.getStatusBarHeight(MSApplication.getContext());
        int screenWidth = ScreenUtils.getScreenWidth(MSApplication.getContext());
        int screenHeight = ScreenUtils.getScreenHeight(MSApplication.getContext());
        int newHeight = screenHeight - titleHeight - bottomHeight - statusHeight;
        switch (ratio) {
            case Constants.AspectRatio.AspectRatio_16v9: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
            case Constants.AspectRatio.AspectRatio_1v1: //1:1
                layoutParams.width = screenWidth;
                layoutParams.height = screenWidth;
                if (newHeight < screenWidth) {
                    layoutParams.width = newHeight;
                    layoutParams.height = newHeight;
                }
                break;
            case Constants.AspectRatio.AspectRatio_9v16: //9:16
                layoutParams.width = (int) (newHeight * 9.0 / 16);
                layoutParams.height = newHeight;
                break;
            case Constants.AspectRatio.AspectRatio_3v4: // 3:4
                layoutParams.width = (int) (newHeight * 3.0 / 4);
                layoutParams.height = newHeight;
                break;
            case Constants.AspectRatio.AspectRatio_4v3: //4:3
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 3.0 / 4);
                break;
            default: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
        }
        mPlayerLayout.setLayoutParams(layoutParams);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setTimeLine(NvsTimeline timeline) {
        mTimeline = timeline;
    }

    public void playVideoButtonCilck() {
        if (mTimeline == null) {
            return;
        }
        long endTime = getDuration();
        playVideoButtonClick(0, endTime);
    }

    public void playVideoButtonClick(long inPoint, long outPoint) {
        playVideo(inPoint, outPoint);
    }

    public void playVideo(long startTime, long endTime) {
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    public void playVideoFromStartPosition() {
        mStreamingContext.playbackTimeline(mTimeline, mStartTime, mStartTime + getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    public void seekTimeline(long timestamp, int seekShowMode) {
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
    }

    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    public void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    public void removeTimeLine() {
        if (mStreamingContext != null && mTimeline != null) {
            TimelineUtil.removeTimeline(mTimeline);
            mTimeline = null;
        }
    }

    private void initDrawRectListener() {
        mDrawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {

                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 * 如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView
                 * 函数进行转换。
                 * */
                PointF pre = mLiveWindow.mapViewToCanonical(prePointF);
                PointF p = mLiveWindow.mapViewToCanonical(nowPointF);
                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    // 移动字幕 Mobile subtitles
                    if (mCurCaption != null) {
                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        mCurCaption.translateCaption(timeLinePointF);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) { // 贴纸编辑 Sticker editing
                    // 移动贴纸 Moving sticker
                    if (mCurAnimateSticker != null) {
                        mCurAnimateSticker.translateAnimatedSticker(timeLinePointF);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_WATERMARK) {
                    updateWaterMarkPositionOnDrag(timeLinePointF.x, timeLinePointF.y, mDrawRect.getDrawRect());
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.translateCaption(timeLinePointF);
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetTranstion();
                }
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float angle) {
                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 *如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView
                 * 函数进行转换。
                 * */
                PointF assetAnchor = mLiveWindow.mapViewToCanonical(anchor);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurCaption != null) {
                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        // 缩放字幕 Zoom subtitles
                        mCurCaption.scaleCaption(scaleFactor, assetAnchor);
                        // 旋转字幕 Rotating subtitles
                        mCurCaption.rotateCaption(angle);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) { // 贴纸编辑 Sticker editing
                    if (mCurAnimateSticker != null) {
                        //缩放贴纸 Scale sticker
                        mCurAnimateSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        //旋转贴纸 Rotating sticker
                        mCurAnimateSticker.rotateAnimatedSticker(angle);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_WATERMARK) {
                    //updateWaterMarkPositionOnScaleAndRotate(scaleFactor, anchor, angle, mDrawRect.getDrawRect());
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        // 旋转字幕 Rotating subtitles
                        mCurCompoundCaption.rotateCaption(angle, assetAnchor);
                        float scaleX = mCurCompoundCaption.getScaleX();
                        float scaleY = mCurCompoundCaption.getScaleY();
                        if (scaleX <= DEFAULT_SCALE_VALUE && scaleY <= DEFAULT_SCALE_VALUE) {
                            mCurCompoundCaption.setScaleX(DEFAULT_SCALE_VALUE);
                            mCurCompoundCaption.setScaleY(DEFAULT_SCALE_VALUE);
                        }
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetScale();
                }
            }

            @Override
            public void onDel() {
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetDelete();
                }
            }

            @Override
            public void onTouchDown(PointF curPoint) {
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetSelected(curPoint);
                }
            }

            @Override
            public void onAlignClick() {
                if (mEditMode == Constants.EDIT_MODE_CAPTION
                        && mCurCaption != null) {
                    switch (mCurCaption.getTextAlignment()) {
                        case NvsTimelineCaption.TEXT_ALIGNMENT_LEFT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_CENTER);
                            setAlignIndex(1);
                            break;
                        case NvsTimelineCaption.TEXT_ALIGNMENT_CENTER:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT);
                            setAlignIndex(2);
                            break;

                        case NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_LEFT);
                            setAlignIndex(0);
                            break;
                    }
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetAlign(mCurCaption.getTextAlignment());
                    }
                }
            }

            @Override
            public void onHorizFlipClick() {
                if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    if (mCurAnimateSticker == null)
                        return;
                    // 贴纸水平翻转 Sticker horizontal flip
                    boolean isHorizFlip = !mCurAnimateSticker.getHorizontalFlip();
                    mCurAnimateSticker.setHorizontalFlip(isHorizFlip);
                    updateAnimateStickerCoordinate(mCurAnimateSticker);
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetHorizFlip(isHorizFlip);
                    }
                }
            }

            @Override
            public void onBeyondDrawRectClick() {
                mPlayButton.callOnClick();
            }
        });

        mDrawRect.setDrawRectClickListener(new DrawRect.onDrawRectClickListener() {
            @Override
            public void onDrawRectClick(int captionIndex) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCaptionTextEditListener != null) {
                        mCaptionTextEditListener.onCaptionTextEdit();
                    }
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCompoundCaptionListener != null) {
                        mCompoundCaptionListener.onCaptionIndex(captionIndex);
                    }
                }
            }
        });

        mDrawRect.setStickerMuteListenser(new DrawRect.onStickerMuteListenser() {
            @Override
            public void onStickerMute() {
                if (mCurAnimateSticker == null)
                    return;
                mStickerMuteIndex = mStickerMuteIndex == 0 ? 1 : 0;
                float volumeGain = mStickerMuteIndex == 0 ? 1.0f : 0.0f;
                mCurAnimateSticker.setVolumeGain(volumeGain, volumeGain);
                setStickerMuteIndex(mStickerMuteIndex);
                if (mStickerMuteListener != null)
                    mStickerMuteListener.onStickerMute();
            }
        });
    }

    /**
     * 拖动过程中更新水印位置
     * Update the watermark position during dragging
     *
     * @param x
     * @param y
     * @param list
     */
    private void updateWaterMarkPositionOnDrag(float x, float y, List<PointF> list) {
        List<PointF> newList = new ArrayList<>();
        for (PointF pointF : list) {
            newList.add(new PointF(pointF.x + x, pointF.y - y));
        }
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
        }
    }

    // 更新字幕在视图上的坐标
    //Update the coordinates of the subtitles on the view
    public void updateCaptionCoordinate(NvsTimelineCaption caption) {
        if (caption != null) {
            // 获取字幕的原始包围矩形框变换后的顶点位置
            //Gets the transformed vertex position of the subtitle's original enclosing rectangle
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;

            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_CAPTION);
        }
    }

    public void setCurAnimateSticker(NvsTimelineAnimatedSticker animateSticker) {
        mCurAnimateSticker = animateSticker;
    }

    public void setStickerMuteIndex(int index) {
        mStickerMuteIndex = index;
        mDrawRect.setStickerMuteIndex(index);
    }

    public NvsTimelineAnimatedSticker getCurAnimateSticker() {
        return mCurAnimateSticker;
    }

    // 更新贴纸在视图上的坐标
    // Update the coordinates of the sticker on the view
    public void updateAnimateStickerCoordinate(NvsTimelineAnimatedSticker animateSticker) {
        if (animateSticker != null) {
            // 获取贴纸的原始包围矩形框变换后的顶点位置
            //Gets the transformed vertex position of the sticker's original enclosing rectangle
            List<PointF> list = animateSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;
            boolean isHorizonFlip = animateSticker.getHorizontalFlip();
            //如果已水平翻转，需要对顶点数据进行处理
            // If it has been flipped horizontally, the vertex data needs to be processed
            if (isHorizonFlip) {
                Collections.swap(list, 0, 3);
                Collections.swap(list, 1, 2);
            }
            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
        }
    }

    //设置贴纸选择框显隐
    //Set the sticker selection box to be hidden
    public void changeStickerRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_STICKER) {
            setDrawRectVisible(isSelectedAnimateSticker() ? View.VISIBLE : View.GONE);
        }
    }

    //在liveWindow上手动选择贴纸
    //Manually select stickers on liveWindow
    public void selectAnimateStickerByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineAnimatedSticker> stickerList = mTimeline.getAnimatedStickersByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (stickerList.size() <= 1)
            return;

        for (int j = 0; j < stickerList.size(); j++) {
            NvsTimelineAnimatedSticker sticker = stickerList.get(j);
            List<PointF> list = sticker.getBoundingRectangleVertices();
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
                mCurAnimateSticker = sticker;
                break;
            }
        }
    }

    public void setMuteVisible(boolean hasAudio) {
        mDrawRect.setMuteVisible(hasAudio);
    }

    private boolean isSelectedAnimateSticker() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurAnimateSticker != null
                && curPosition >= mCurAnimateSticker.getInPoint()
                && curPosition <= mCurAnimateSticker.getOutPoint()) {
            return true;
        }
        return false;
    }

    public void setAlignIndex(int index) {
        mDrawRect.setAlignIndex(index);
    }

    public void setCurCaption(NvsTimelineCaption caption) {
        mCurCaption = caption;
    }

    public NvsTimelineCaption getCurCaption() {
        return mCurCaption;
    }

    public void setCurCompoundCaption(NvsTimelineCompoundCaption caption) {
        mCurCompoundCaption = caption;
    }

    public NvsTimelineCompoundCaption getCurrCompoundCaption() {
        return mCurCompoundCaption;
    }

    // 更新组合字幕在视图上的坐标
    //Update the coordinates of the combined subtitles on the view
    public void updateCompoundCaptionCoordinate(NvsTimelineCompoundCaption caption) {
        if (caption != null) {
            // 获取字幕的原始包围矩形框变换后的顶点位置
            //Gets the transformed vertex position of the subtitle's original enclosing rectangle
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            if (list == null || list.size() < 4) {
                return;
            }
            List<PointF> newList = getAssetViewVerticesList(list);
            List<List<PointF>> newSubCaptionList = new ArrayList<>();
            int subCaptionCount = caption.getCaptionCount();
            for (int index = 0; index < subCaptionCount; index++) {
                List<PointF> subList = caption.getCaptionBoundingVertices(index, NvsTimelineCompoundCaption.BOUNDING_TYPE_TEXT);
                if (subList == null || subList.size() < 4) {
                    continue;
                }
                List<PointF> newSubList = getAssetViewVerticesList(subList);
                newSubCaptionList.add(newSubList);
            }
            mDrawRect.setCompoundDrawRect(newList, newSubCaptionList, Constants.EDIT_MODE_COMPOUND_CAPTION);
        }
    }

    public void changeCompoundCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            setDrawRectVisible(false);
        }
    }

    public boolean curPointIsInnerDrawRect(int xPos, int yPos) {
        return mDrawRect.curPointIsInnerDrawRect(xPos, yPos);
    }

    public boolean selectCompoundCaptionByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return false;
        }
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (captionList.size() < 1)
            return false;

        boolean result = false;
        for (int j = 0; j < captionList.size(); j++) {
            NvsTimelineCompoundCaption caption = captionList.get(j);
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRectVisible(true);
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_COMPOUND_CAPTION);
                mCurCompoundCaption = caption;
                result = true;
                break;
            } else {
                result = false;
                mCurCompoundCaption = null;
            }
        }
        return result;
    }

    private List<PointF> getAssetViewVerticesList(List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mLiveWindow.mapCanonicalToView(verticesList.get(i));
            newList.add(pointF);
        }
        return newList;
    }


    public void setDrawRect(List<PointF> newList) {
        mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
    }

    public List<PointF> getDrawRect() {
        return mDrawRect.getDrawRect();
    }

    public void setDrawRectVisible(int visibility) {
        mDrawRect.setVisibility(visibility);
    }

    public void setDrawRectVisible(boolean show) {
        mDrawRect.bringToFront();
        mDrawRect.setDrawRectVisible(show);
    }

    private boolean checkInLiveWindow(List<PointF> newList) {
        if (pointFListLiveWindow != null) {
            float minX = pointFListLiveWindow.get(0).x;
            float maxX = pointFListLiveWindow.get(2).x;
            float minY = pointFListLiveWindow.get(0).y;
            float maxY = pointFListLiveWindow.get(2).y;
            for (PointF pointF : newList) {
                if (pointF.x < minX || pointF.x > maxX || pointF.y < minY || pointF.y > maxY) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isDrawRectVisible() {
        return mDrawRect.isVisible();
    }

    public NvsTimeline getTimeLine() {
        return mTimeline;
    }

    //贴纸和字幕编辑对应的回调，其他素材不用
    //Callback corresponding to sticker and subtitle editing, other materials are not used
    public interface AssetEditListener {
        void onAssetDelete();

        void onAssetSelected(PointF curPoint);

        void onAssetTranstion();

        void onAssetScale();

        void onAssetAlign(int alignVal);

        void onAssetHorizFlip(boolean isHorizFlip);
    }

    //字幕文本修改回调
    //Subtitle text modification callback
    public interface VideoCaptionTextEditListener {
        void onCaptionTextEdit();
    }

    //字幕文本修改回调
    //Subtitle text modification callback
    public interface OnCompoundCaptionListener {
        void onCaptionIndex(int captionIndex);
    }

    //LiveWindow点击回调
    //liveWindow click callback
    public interface OnLiveWindowClickListener {
        void onLiveWindowClick();
    }

    //LiveWindowd点击回调
    //liveWindow click callback
    public interface OnStickerMuteListener {
        void onStickerMute();
    }

    public interface OnThemeCaptionSeekListener {
        void onThemeCaptionSeek(long stamp);
    }

    //Fragment加载完成回调
    //Fragment load complete callback
    public interface OnFragmentLoadFinishedListener {
        void onLoadFinished();
    }

    public void setThemeCaptionSeekListener(OnThemeCaptionSeekListener themeCaptionSeekListener) {
        mThemeCaptionSeekListener = themeCaptionSeekListener;
    }

    public void setLiveWindowClickListener(OnLiveWindowClickListener liveWindowClickListener) {
        this.mLiveWindowClickListener = liveWindowClickListener;
    }

    public void setCaptionTextEditListener(VideoCaptionTextEditListener captionTextEditListener) {
        this.mCaptionTextEditListener = captionTextEditListener;
    }

    public void setStickerMuteListener(OnStickerMuteListener stickerMuteListener) {
        this.mStickerMuteListener = stickerMuteListener;
    }

    public void setCompoundCaptionListener(OnCompoundCaptionListener compoundCaptionListener) {
        this.mCompoundCaptionListener = compoundCaptionListener;
    }

    public void setAssetEditListener(AssetEditListener assetEditListener) {
        this.mAssetEditListener = assetEditListener;
    }

    public void setFragmentLoadFinisedListener(OnFragmentLoadFinishedListener fragmentLoadFinisedListener) {
        this.mFragmentLoadFinisedListener = fragmentLoadFinisedListener;
    }

    public interface OnPlayProgressChangeListener {
        void onPlayProgressChanged(long curTime);

        void onPlayStateChanged(boolean isPlaying);
    }
}
