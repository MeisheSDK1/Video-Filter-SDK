package com.meishe.sdkdemo.edit;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.meicam.sdk.NvsAnimatedSticker;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsClipAnimatedSticker;
import com.meicam.sdk.NvsClipCaption;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.activity.SettingActivity;
import com.meishe.sdkdemo.edit.background.view.TransformView;
import com.meishe.sdkdemo.edit.mask.view.MaskView;
import com.meishe.sdkdemo.edit.mask.view.ZoomView;
import com.meishe.sdkdemo.edit.view.DrawRect;
import com.meishe.sdkdemo.edit.view.DrawRectParentView;
import com.meishe.sdkdemo.edit.watermark.EffectItemData;
import com.meishe.sdkdemo.makecover.ClipImageView;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.BackGroundInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.StoryboardInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoFx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.meishe.sdkdemo.utils.Constants.BLUR;
import static com.meishe.sdkdemo.utils.Constants.EDIT_MODE_EFFECT;
import static com.meishe.sdkdemo.utils.Constants.EDIT_MODE_WATERMARK;
import static com.meishe.sdkdemo.utils.Constants.MOSAIC;

/**
 * Created by yyj on 2018/5/29 0029.
 * VideoFragment，封装liveWindow,供多个页面使用，避免代码重复
 * VideoFragment, encapsulate liveWindow for multiple pages, avoid code duplication
 */

public class VideoFragment extends Fragment {
    public static final int TYPE_CAPTION = 1;
    private final String TAG = "VideoFragment";
    private static final float DEFAULT_SCALE_VALUE = 0.1f;
    private static final long BASE_VALUE = 100000;

    private static final int RESETPLATBACKSTATE = 100;

    private final static float BACKGROUND_MAX_ROTATION = 360.0f;
    private final static float BACKGROUND_MIM_SCALE = 0.1F;
    private final static float BACKGROUND_MAX_SCALE = 10F;

    private RelativeLayout mPlayerLayout;
    private NvsLiveWindow mLiveWindow;
    private DrawRect mDrawRect;
    private LinearLayout mPlayBarLayout;
    private RelativeLayout mPlayButton;
    private ImageView mPlayImage;
    private TextView mCurrentPlayTime;
    private SeekBar mPlaySeekBar;
    private TextView mTotalDuration;
    private RelativeLayout mVoiceButton;
    private ClipImageView mClipImageView;

    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private NvsTimeline mTimeline;
    private boolean mPlayBarVisibleState = true, mVoiceButtonVisibleState = false, mAutoPlay = false, mRecording = false;
    private OnFragmentLoadFinisedListener mFragmentLoadFinisedListener;
    private VideoFragmentListener mVideoFragmentCallBack;
    private AssetEditListener mAssetEditListener;
    private WaterMarkChangeListener waterMarkChangeListener;
    private VideoVolumeListener mVideoVolumeListener;
    private OnLiveWindowClickListener mLiveWindowClickListener;
    private OnStickerMuteListener mStickerMuteListener;
    private VideoCaptionTextEditListener mCaptionTextEditListener;
    private OnThemeCaptionSeekListener mThemeCaptionSeekListener;
    private NvsCaption mCurCaption;
    private int mEditMode = 0;
    private NvsAnimatedSticker mCurAnimateSticker;
    private int mStickerMuteIndex = 0;
    private NvsTimelineCompoundCaption mCurCompoundCaption;
    private OnCompoundCaptionListener mCompoundCaptionListener;
    /**
     * 播放开始标识
     * Play start identification
     */
    private long mPlayStartFlag = -1;
    private boolean mShowSeekbar = true;
    /**
     * liveWindow 实际view中坐标点
     * coordinate point in the actual view of liveWindow
     */
    private List<PointF> pointFListLiveWindow;

    /**
     * 第一次添加水印时的原始坐标列表，用于计算偏移量
     * A list of the original coordinates when the watermark was first added, used to calculate the offset
     */
    private List<PointF> pointFListToFirstAddWaterMark;
    private IBeforeAnimateStickerEditListener mBeforeAnimateStickerEditListener;
    /**
     * 动画页面调用时传入，其他位置忽略即可
     * Animation page is passed when called, other locations can be ignored
     */
    private long mPlayStartPoint = 0;
    private long mPlayEndPoint = 0;
    private boolean mIsAnimationView = false;
    private boolean mIsBackgroundView = false;
    private int playState;

    /**
     * 水印类型
     * Watermarking type
     */
    private int mWaterType = 0;
    private float mPixTimeLineRatio;
    private TransformView mTransformView;
    private ClipInfo mSelectClipInfo;
    private VideoFx mTransformVideoFx;
    private NvsVideoClip mSelectNvsVideoClip;

    private OnBackgroundChangedListener mOnBackgroundChangedListener;
    private VideoFx mVideoFx;
    private View mFlRootContainer;
    private ZoomView zoomView;
    private MaskView maskView;
    private ImageView mVideoLoading;
    private int mPlayFlag;

    private boolean mIsLimitPlay = false;
    private int mLayoutType;
    private DrawRectParentView mDrawRectContainer;
    private boolean isSupportDoubleFlingerScale;

    public void setIsAnimationView(boolean mIsAnimationView) {
        this.mIsAnimationView = mIsAnimationView;
        mPlayStartPoint = 0;
        mPlayEndPoint = mTimeline.getDuration();
    }


    public void setIsBackgroundView(boolean mIsBackgroundView) {
        this.mIsBackgroundView = mIsBackgroundView;
        mPlayStartPoint = 0;
        mPlayEndPoint = mTimeline.getDuration();
    }

    public void setLayoutType(int typeCaption) {
        mLayoutType = typeCaption;
    }


    /**
     * Fragment加载完成回调
     * Fragment loading completion callback
     */
    public interface OnFragmentLoadFinisedListener {
        void onLoadFinished();
    }

    /**
     * 视频播放相关回调
     * Video playback related callbacks
     */
    public interface VideoFragmentListener {
        //video play
        void playBackEOF(NvsTimeline timeline);

        void playStopped(NvsTimeline timeline);

        void playbackTimelinePosition(NvsTimeline timeline, long stamp);

        void streamingEngineStateChanged(int state);
    }

    /**
     * 贴纸和字幕编辑对应的回调，其他素材不用
     * Callbacks for stickers and subtitle editing, other materials are not used
     */
    public abstract static class AssetEditListener {
        public abstract void onAssetDelete();

        public void onAssetSelected(PointF curPoint) {
        }

        public void onAssetTranslation() {
        }

        public void onAssetScale() {
        }

        /**
         * 字幕专用
         * Subtitle only
         */
        public void onAssetAlign(int alignVal) {
        }

        public void onOrientationChange(boolean isHorizontal) {
        }

        /**
         * 贴纸使用
         * Stickers only
         */
        public void onAssetHorizFlip(boolean isHorizFlip) {
        }
    }

    public interface WaterMarkChangeListener {
        void onDrag(List<PointF> list);

        void onScaleAndRotate(List<PointF> curPoint);
    }

    /**
     * 音量回调
     * Volume callback
     */
    public interface VideoVolumeListener {
        void onVideoVolume();
    }

    /**
     * 字幕文本修改回调
     * Subtitle text modification callback
     */
    public interface VideoCaptionTextEditListener {
        void onCaptionTextEdit();
    }

    /**
     * 组合字幕索引回调
     * Combined subtitle index callback
     */
    public interface OnCompoundCaptionListener {
        void onCaptionIndex(int captionIndex);

        void onCaptionDoubleClick(int captionIndex);
    }

    /**
     * LiveWindowd点击回调
     * LiveWindowd click callback
     */
    public interface OnLiveWindowClickListener {
        void onLiveWindowClick();
    }

    /**
     * 贴纸静音点击回调
     * Sticker mute click callback
     */
    public interface OnStickerMuteListener {
        void onStickerMute();
    }

    public interface OnThemeCaptionSeekListener {
        void onThemeCaptionSeek(long stamp);
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

    public void setFragmentLoadFinisedListener(OnFragmentLoadFinisedListener fragmentLoadFinisedListener) {
        this.mFragmentLoadFinisedListener = fragmentLoadFinisedListener;
    }

    public void setVideoFragmentCallBack(VideoFragmentListener videoFragmentCallBack) {
        this.mVideoFragmentCallBack = videoFragmentCallBack;
    }

    public void setAssetEditListener(AssetEditListener assetEditListener) {
        this.mAssetEditListener = assetEditListener;
    }

    public void setWaterMarkChangeListener(WaterMarkChangeListener waterMarkChangeListener) {
        this.waterMarkChangeListener = waterMarkChangeListener;
    }

    public void setVideoVolumeListener(VideoVolumeListener videoVolumeListener) {
        this.mVideoVolumeListener = videoVolumeListener;
    }

    public void setStickerMuteListener(OnStickerMuteListener stickerMuteListener) {
        this.mStickerMuteListener = stickerMuteListener;
    }

    public void setCompoundCaptionListener(OnCompoundCaptionListener compoundCaptionListener) {
        this.mCompoundCaptionListener = compoundCaptionListener;
    }

    private Handler m_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RESETPLATBACKSTATE:
                    updateCurPlayTime(0);
                    seekTimeline(mPlayStartPoint, 0);
                    /*
                     * 播放进度条显示
                     * Play progress bar display
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                        mPlayBarLayout.setVisibility(View.VISIBLE);
                        startHidePlayBarTimer(true);
                    }
                    break;
            }
            return false;
        }
    });

    private CountDownTimer m_hidePlayBarTimer = new CountDownTimer(4000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            /*
             * 播放进度条显示
             * Play progress bar display
             * */
            if (mPlayBarVisibleState && !mShowSeekbar) {
                mPlayStartFlag = -1;
                mPlayBarLayout.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;
        if (mLayoutType == TYPE_CAPTION) {
            rootView = inflater.inflate(R.layout.fragment_video_caption, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_video, container, false);
        }
        mPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.player_layout);
        mLiveWindow = (NvsLiveWindow) rootView.findViewById(R.id.liveWindow);
        mDrawRect = (DrawRect) rootView.findViewById(R.id.draw_rect);
        mPlayBarLayout = (LinearLayout) rootView.findViewById(R.id.playBarLayout);
        mPlayButton = (RelativeLayout) rootView.findViewById(R.id.playLayout);
        mPlayImage = (ImageView) rootView.findViewById(R.id.playImage);
        mCurrentPlayTime = (TextView) rootView.findViewById(R.id.currentPlaytime);
        mPlaySeekBar = (SeekBar) rootView.findViewById(R.id.play_seekBar);
        mTotalDuration = (TextView) rootView.findViewById(R.id.totalDuration);
        mVoiceButton = (RelativeLayout) rootView.findViewById(R.id.voiceLayout);
        mClipImageView = (ClipImageView) rootView.findViewById(R.id.clip_image_view);
        mTransformView = (TransformView) rootView.findViewById(R.id.transform_view);
        mFlRootContainer = rootView.findViewById(R.id.video_main);
        mDrawRectContainer = rootView.findViewById(R.id.draw_rect_container);
        zoomView = rootView.findViewById(R.id.mask_zoom);
        maskView = rootView.findViewById(R.id.mask_view);
        mVideoLoading = rootView.findViewById(R.id.video_loading);
        Glide.with(this)
                .asGif()
                .load(R.drawable.icon_loading_gif)
                .into(mVideoLoading);
        controllerOperation();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        initData();
        mPlayBarLayout.setVisibility(mPlayBarVisibleState ? View.VISIBLE : View.GONE);
        mVoiceButton.setVisibility(mVoiceButtonVisibleState ? View.VISIBLE : View.GONE);
        if (mFragmentLoadFinisedListener != null) {
            mFragmentLoadFinisedListener.onLoadFinished();
        }
        if (mDrawRectContainer != null) {
            mDrawRectContainer.setOnRectScaleListener(new DrawRectParentView.OnDoubleFlingerScaleListener() {
                @Override
                public void onScale(View view, float onScale) {
                    if (mCurCaption == null) {
                        return;
                    }
                    if (!isSupportDoubleFlingerScale) {
                        return;
                    }
                    if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                        if (mCurCaption != null) {
                            mCurCaption.setScaleX(onScale);
                            mCurCaption.setScaleY(onScale);
                            updateCaptionCoordinate(mCurCaption);
                            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                            if (mOnRectScaleListener != null) {
                                mOnRectScaleListener.onScale(view, onScale);
                            }
                        }
                    }
                }
            });
        }
    }


    private List<PointF> getAssetViewVerticesList(boolean horizontal, List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mLiveWindow.mapCanonicalToView(verticesList.get(i));
            newList.add(pointF);
        }
        if (horizontal) {
            Collections.swap(newList, 0, 3);
            Collections.swap(newList, 1, 2);
        }
        return newList;
    }


    private void initData() {
        initMaskView();
        setLiveWindowRatio();
        updateTotalDurationText();
        updateCurPlayTime(0);
        initDrawRectListener();
    }

    private void initMaskView() {
        zoomView.setMaskView(maskView);
        zoomView.post(new Runnable() {
            @Override
            public void run() {
                zoomView.setVideoFragmentHeight(getHeight(), getLiveWindow().getWidth(), getLiveWindow().getHeight());
            }
        });
    }

    /**
     * 设置蒙版改动监听
     * Set up mask change listener
     *
     * @param dataChangeListener
     */
    public void setMaskViewDataChangeListener(ZoomView.OnDataChangeListener dataChangeListener) {
        zoomView.setOnDataChangeListener(dataChangeListener);
    }

    /**
     * 获取蒙版ZoomView
     * Get the mask ZoomView
     *
     * @return
     */
    public ZoomView getMaskZoomView() {
        return zoomView;
    }

    private void setLiveWindowRatio() {
        Bundle bundle = getArguments();
        int ratio = 0, titleHeight = 0, bottomHeight = 0;
        if (bundle != null) {
            ratio = bundle.getInt("ratio", NvAsset.AspectRatio_16v9);
            titleHeight = bundle.getInt("titleHeight");
            bottomHeight = bundle.getInt("bottomHeight");
            mPlayBarVisibleState = bundle.getBoolean("playBarVisible", true);
            mVoiceButtonVisibleState = bundle.getBoolean("voiceButtonVisible", false);
        }

        if (null == mTimeline) {
            Log.e(TAG, "mTimeline is null!");
            return;
        }
        setLiveWindowRatio(ratio, titleHeight, bottomHeight);
        connectTimelineWithLiveWindow();
    }

    public void updateCurPlayTime(long time) {
        mCurrentPlayTime.setText(formatTimeStrWithUs(time));
        mPlaySeekBar.setProgress((int) (time / BASE_VALUE));
    }

    public void updateTotalDurationText() {
        if (mTimeline != null) {
            mTotalDuration.setText(formatTimeStrWithUs(mTimeline.getDuration()));
            mPlaySeekBar.setMax((int) (mTimeline.getDuration() / BASE_VALUE) - 1);
        }
    }

    public void setTimeline(NvsTimeline timeline) {
        mTimeline = timeline;
    }

    public Point getLiveWindowSize() {
        Logger.e(TAG, "mLiveWindow宽高获取  " + mLiveWindow.getWidth() + "    " + mLiveWindow.getHeight());
        return new Point(mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    /**
     * 设置clipInfo信息(初始化Transform 2D特效)
     * Set clipInfo information (initialize Transform 2D special effects)
     *
     * @param clipInfo
     * @param nvsVideoClip
     */
    public void setVideoClipInfo(ClipInfo clipInfo, NvsVideoClip nvsVideoClip) {
        mSelectClipInfo = clipInfo;
        mSelectNvsVideoClip = nvsVideoClip;

        if (mTimeline != null && mSelectClipInfo != null) {

            float timelineHeight = mTimeline.getVideoRes().imageHeight;
            float liveWindowHeight = mLiveWindow.getHeight();
            mPixTimeLineRatio = timelineHeight / liveWindowHeight;

            mTransformVideoFx = mSelectClipInfo.getVideoFx(Constants.FX_TRANSFORM_2D);
            if (mTransformVideoFx == null) {
                mTransformVideoFx = new VideoFx();
                mTransformVideoFx.setSubType(Constants.FX_TRANSFORM_2D);
                mSelectClipInfo.getVideoFxs().add(mTransformVideoFx);
                mTransformVideoFx.setTransX(0F);
                mTransformVideoFx.setTransY(0F);
                mTransformVideoFx.setScaleX(1.0F);
                mTransformVideoFx.setScaleY(1.0F);
                mTransformVideoFx.setRotation(0F);

                NvsVideoFx videoFx = getTransformFx(mSelectNvsVideoClip, Constants.FX_TRANSFORM_2D);
                if (videoFx == null) {
                    videoFx = mSelectNvsVideoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
                    videoFx.setAttachment(StoryboardInfo.SUB_TYPE_BACKGROUND, Constants.FX_TRANSFORM_2D);
                }
                videoFx.setFloatVal(Constants.FX_TRANSFORM_2D_TRANS_X, 0F);
                videoFx.setFloatVal(Constants.FX_TRANSFORM_2D_TRANS_Y, 0F);
                videoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, 1.0F);
                videoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, 1.0F);
                videoFx.setFloatVal(Constants.FX_TRANSFORM_2D_ROTATION, 0F);
            }
        }
    }

    /**
     * 从videoClip中拿指定type的特效
     * Take the special effect of the specified type from the videoClip
     *
     * @param videoClip
     * @param type
     * @return
     */
    public NvsVideoFx getTransformFx(NvsVideoClip videoClip, String type) {
        int fxCount = videoClip.getFxCount();
        for (int index = 0; index < fxCount; index++) {
            NvsVideoFx clipFx = videoClip.getFxByIndex(index);
            Object attachment = clipFx.getAttachment(StoryboardInfo.SUB_TYPE_BACKGROUND);
            if (attachment != null && attachment instanceof String) {
                String subType = (String) attachment;
                if (subType.equals(type)) {
                    return clipFx;
                }
            }
        }
        return null;
    }

    public void setTransformViewVisible(int visible) {
        if (mTransformView != null) {
            mTransformView.setVisibility(visible);
        }
    }

    private void initDrawRectListener() {
        mDrawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {
                //拖动前停止播放 Stop playing before dragging
                if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mStreamingContext.stop();
                }
                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 * 如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView函数进行转换。
                 *
                 *
                 * Coordinate transformation
                 * The coordinates used by the DK interface are the coordinates in the Canonical coordinate system,
                 * and the coordinates we use in the program are generally the coordinates in the Android View coordinate system,
                 * so when using the interface, you need to use the mapViewToCanonical function provided by the SDK to view the coordinates Convert to Canonical coordinates.
                 * Conversely, if you want to convert Canonical coordinates to View coordinates, you can use the mapCanonicalToView function to convert them.
                 * */
                PointF pre = mLiveWindow.mapViewToCanonical(prePointF);
                PointF p = mLiveWindow.mapViewToCanonical(nowPointF);

                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);

                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    /*
                     * 移动字幕
                     * Moving captions
                     * */
                    if (mCurCaption != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeTransitionCouldDo();
                            if (!b) {
                                return;
                            }
                        }

                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        mCurCaption.translateCaption(timeLinePointF);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    /*
                     * 移动贴纸
                     * Mobile stickers
                     * */
                    if (mCurAnimateSticker != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeTransitionCouldDo();
                            if (!b) {
                                return;
                            }
                        }
                        mCurAnimateSticker.translateAnimatedSticker(timeLinePointF);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == EDIT_MODE_WATERMARK) {
                    if (!checkInLiveWindow(mDrawRect.getDrawRect())) {
                        return;
                    }
                    updateWaterMarkPositionOnDrag(nowPointF.x - prePointF.x, prePointF.y - nowPointF.y, mDrawRect.getDrawRect());

                } else if (mEditMode == EDIT_MODE_EFFECT) {
                    if (!checkInLiveWindow(mDrawRect.getDrawRect())) {
                        return;
                    }
                    updateEffectPositionOnDrag(nowPointF.x - prePointF.x, prePointF.y - nowPointF.y, mDrawRect.getDrawRect());

                    switch (mWaterType) {
                        case MOSAIC:
                        case BLUR:
                            float[] fx = getFxData();
                            nvsTimelineVideoFx.setRegion(fx);
                            refreshLiveWindowFrame();
                            VideoFx videoFx = TimelineData.instance().getVideoFx();
                            if (videoFx != null) {
                                videoFx.setRegion(fx);
                            }
                            break;
                    }

                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.translateCaption(timeLinePointF);
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetTranslation();
                }
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float angle) {
                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 * 如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView函数进行转换。
                 *
                 *
                 * Coordinate transformation
                 * The coordinates used by the DK interface are the coordinates in the Canonical coordinate system,
                 * and the coordinates we use in the program are generally the coordinates in the Android View coordinate system,
                 * so when using the interface, you need to use the mapViewToCanonical function provided by the SDK to view the coordinates Convert to Canonical coordinates.
                 * Conversely, if you want to convert Canonical coordinates to View coordinates, you can use the mapCanonicalToView function to convert them.
                 * */
                PointF assetAnchor = mLiveWindow.mapViewToCanonical(anchor);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurCaption != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeScaleCouldDo();
                            if (!b) {
                                return;
                            }
                        }

                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        /*
                         * 放缩字幕
                         * Shrink captions
                         * */
                        mCurCaption.scaleCaption(scaleFactor, assetAnchor);
                        /*
                         * 旋转字幕
                         * Spin subtitles
                         * */
                        mCurCaption.rotateCaption(angle);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    /*
                     *  放缩贴纸
                     * Scale stickers
                     * */
                    if (mCurAnimateSticker != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeScaleCouldDo();
                            if (!b) {
                                return;
                            }
                        }
                        mCurAnimateSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        /*
                         * 旋转贴纸
                         * Rotate stickers
                         * */
                        mCurAnimateSticker.rotateAnimatedSticker(angle);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == EDIT_MODE_WATERMARK) {
                    updateWaterMarkPositionOnScaleAndRotate(scaleFactor, anchor, angle, mDrawRect.getDrawRect());
                } else if (mEditMode == EDIT_MODE_EFFECT) {
                    updateEffectPositionOnScaleAndRotate(scaleFactor, anchor, angle, mDrawRect.getDrawRect());
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        /*
                         * 旋转字幕
                         * Spin subtitles
                         * */
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
            public void onScaleXandY(float xScaleFactor, float yScaleFactor, PointF anchor) {
                if (mEditMode == EDIT_MODE_EFFECT) {
                    switch (mWaterType) {
                        case MOSAIC:
                        case BLUR:
                            if (mDrawRect != null) {
                                mDrawRect.setPicturePath("");
                            }
                            updateWaterMarkPositionOnXAndY(xScaleFactor, yScaleFactor, anchor, mDrawRect.getDrawRect());
                            float[] fx = getFxData();
                            nvsTimelineVideoFx.setRegion(fx);
                            VideoFx videoFx = TimelineData.instance().getVideoFx();
                            if (videoFx != null) {
                                videoFx.setRegion(fx);
                            }
                            refreshLiveWindowFrame();
                            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                            break;
                    }
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
            public void onAlignClick(boolean isHorizontal) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION
                        && mCurCaption != null) {
                    switch (mCurCaption.getTextAlignment()) {
                        case NvsTimelineCaption.TEXT_ALIGNMENT_LEFT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_CENTER);  //居中对齐 Center alignment
                            setAlignIndex(1);
                            break;
                        case NvsTimelineCaption.TEXT_ALIGNMENT_CENTER:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT);  //居右或者居上对齐 Align to the right or top
                            setAlignIndex(2);
                            break;
                        case NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_LEFT);  //居左或者居下对齐 Align left or bottom
                            setAlignIndex(0);
                            break;
                    }
                    updateCaptionCoordinate(mCurCaption);
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetAlign(mCurCaption.getTextAlignment());
                    }
                }
            }

            @Override
            public void onOrientationChange(boolean isHorizontal) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    //切换横竖字幕 Toggle horizontal and vertical subtitles
                    if (mCurCaption != null) {
                        mCurCaption.setVerticalLayout(!isHorizontal);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onOrientationChange(isHorizontal);
                    }
                }
            }

            @Override
            public void onHorizontalFlipClick() {
                if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    if (mCurAnimateSticker == null)
                        return;
                    /*
                     * 贴纸水平翻转
                     * Sticker flips horizontally
                     * */
                    boolean isHorizontalFlip = !mCurAnimateSticker.getHorizontalFlip();
                    mCurAnimateSticker.setHorizontalFlip(isHorizontalFlip);
                    updateAnimateStickerCoordinate(mCurAnimateSticker);
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetHorizFlip(isHorizontalFlip);
                    }
                }
            }

            @Override
            public void onBeyondDrawRectClick() {
                mPlayButton.callOnClick();
            }

            @Override
            public void onTouchUp(PointF pointF) {

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

            @Override
            public void onDrawRectDoubleClick(int captionIndex) {
                if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCompoundCaptionListener != null) {
                        mCompoundCaptionListener.onCaptionDoubleClick(captionIndex);
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

        mTransformView.setOnPipTouchListener(new TransformView.OnTransformTouchEventListener() {
            @Override
            public void onTouchDown(PointF curPoint) {

            }

            @Override
            public void onScaleAndRotate(float scale, float degree) {
                if ((scale == 1.0f) && (degree == 0.0f)) {
                    return;
                }

                if (mSelectClipInfo == null) {
                    return;
                }

                BackGroundInfo backGroundInfo = getBackGroundInfo();
//                Map<String, Float> transform = getTransform();
//                if (transform == null) {
//                    return;
//                }
                if (backGroundInfo == null) return;

                float oldScale = backGroundInfo.getScaleX();
                float newScale = oldScale * scale;
                if (newScale > BACKGROUND_MAX_SCALE) {
                    newScale = BACKGROUND_MAX_SCALE;
                }
                if (newScale < BACKGROUND_MIM_SCALE) {
                    newScale = BACKGROUND_MIM_SCALE;
                }
//                transform.put(STORYBOARD_KEY_SCALE_X, newScale);
//                transform.put(STORYBOARD_KEY_SCALE_Y, newScale);
//                float oldDegree = transform.get(STORYBOARD_KEY_ROTATION_Z);
                float oldDegree = backGroundInfo.getRotation();
                double newDegree = oldDegree + degree;
//                transform.put(STORYBOARD_KEY_ROTATION_Z, (float) newDegree);
                if (backGroundInfo != null) {
                    backGroundInfo.setScaleX(newScale);
                    backGroundInfo.setScaleY(newScale);
                    backGroundInfo.setRotation((float) newDegree);
                }
                if (mOnBackgroundChangedListener != null) {
                    mOnBackgroundChangedListener.onBackgroundChanged();
                }

            }

            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {
                if (mSelectClipInfo == null) {
                    return;
                }

                float transX = nowPointF.x - prePointF.x;
                float transY = nowPointF.y - prePointF.y;
                if ((transX == 0) && (transY == 0)) {
                    return;
                }
                float actualTransX = mPixTimeLineRatio * transX;
                float actualTransY = mPixTimeLineRatio * transY;
                BackGroundInfo backGroundInfo = getBackGroundInfo();
                if (backGroundInfo == null) return;
//                Map<String, Float> transform = getTransform();
//                if (transform == null) {
//                    return;
//                }
//                float oldTransX = transform.get(STORYBOARD_KEY_TRANS_X);
//                float oldTransY = transform.get(STORYBOARD_KEY_TRANS_Y);
                float oldTransX = backGroundInfo.getTransX();
                float oldTransY = backGroundInfo.getTransY();
                double newTransX = oldTransX + actualTransX;
                double newTransY = oldTransY - actualTransY;
//                transform.put(STORYBOARD_KEY_TRANS_X, (float) newTransX);
//                transform.put(STORYBOARD_KEY_TRANS_Y, (float) newTransY);
                Log.d(TAG, "onDrag x:" + newTransX + " y:" + newTransY);
                if (backGroundInfo != null) {
                    backGroundInfo.setTransX((float) newTransX);
                    backGroundInfo.setTransY((float) newTransY);
                }
                if (mOnBackgroundChangedListener != null) {
                    mOnBackgroundChangedListener.onBackgroundChanged();
                }
            }

            @Override
            public void onTouchUp(PointF curPoint) {

            }
        });
    }

    private BackGroundInfo getBackGroundInfo() {
        return mSelectClipInfo.getBackGroundInfo();
    }


    /**
     * @param xScaleDregree x缩放比例 x scaling
     * @param yScaleDregree y缩放比例 y scaling
     * @param centerPoint   中心点 Central point
     * @param list          方框坐标点 Box coordinate points
     */
    private void updateWaterMarkPositionOnXAndY(float xScaleDregree, float yScaleDregree, PointF centerPoint, List<PointF> list) {
        float width = Math.abs(list.get(0).x - list.get(3).x);
        float height = Math.abs(list.get(0).y - list.get(1).y);
        float x0 = centerPoint.x - width / 2 * xScaleDregree;
        float x1 = centerPoint.x + width / 2 * xScaleDregree;
        float y0 = centerPoint.y - height / 2 * yScaleDregree;
        float y1 = centerPoint.y + height / 2 * yScaleDregree;
        refreshWaterMarkByFourPoint(x0, x1, y0, y1);
    }


    /**
     * 删除模糊,马赛克
     * Remove blur, mosaic
     */
    public void removeWaterToTimeline() {
        if (nvsTimelineVideoFx != null) {
            mTimeline.removeTimelineVideoFx(nvsTimelineVideoFx);
            TimelineData.instance().clearTimelineVideoFx();
        }
        setDrawRectVisible(View.GONE);
        refreshLiveWindowFrame();
    }

    public NvsTimelineVideoFx getNvsTimelineVideoFx() {
        return nvsTimelineVideoFx;
    }

    /**
     * 设置当前编辑模式类型，根据不同当前类型状态操作不同逻辑
     * Set the current editing mode type, and operate different logics according to different current types status
     *
     * @param mode
     */
    public void setEditMode(int mode) {
        mEditMode = mode;
    }

    public int getEditMode() {
        return mEditMode;
    }


    public int getWaterType() {
        return mWaterType;
    }

    private NvsTimelineVideoFx nvsTimelineVideoFx;

    public static final String MOSAICNAME = "Mosaic";
    public static final String BLURNAME = "Gaussian Blur";

    public void setWaterType(int type) {
        this.mWaterType = type;
    }

    /**
     * 根据type设置特效
     * Set special effects according to type
     *
     * @param type
     */
    public void setEffectByPoint(int type) {
        setPointFListLiveWindow(mLiveWindow.getWidth(), mLiveWindow.getHeight());
        setEffectPoint(type);
        //如果存在获取原特效隐藏，不存在走创建
        //If there is to get the original special effect to hide, there is no to create it
        if (mTimeline == null) {
            return;
        }
        if (mDrawRect != null) {
            mDrawRect.setPicturePath("");
        }
        nvsTimelineVideoFx = null;
        NvsTimelineVideoFx timelineVideoFx = mTimeline.getFirstTimelineVideoFx();
        while (timelineVideoFx != null) {
            if (timelineVideoFx.getTimelineVideoFxType() == NvsVideoFx.VIDEOFX_TYPE_BUILTIN) {
                String fxName = timelineVideoFx.getBuiltinTimelineVideoFxName();
                if (TextUtils.equals(MOSAICNAME, fxName) ||
                        TextUtils.equals(BLURNAME, fxName)) {
                    timelineVideoFx.setFilterIntensity(0);
                    if (TextUtils.equals(MOSAICNAME, fxName) && type == EffectItemData.TYPE_MOSAIC) {
                        nvsTimelineVideoFx = timelineVideoFx;
                        timelineVideoFx.setFilterIntensity(1);
                    } else if (TextUtils.equals(BLURNAME, fxName) && type == EffectItemData.TYPE_BLUR) {
                        nvsTimelineVideoFx = timelineVideoFx;
                        timelineVideoFx.setFilterIntensity(1);
                    }
                }
            }
            timelineVideoFx = mTimeline.getNextTimelineVideoFx(timelineVideoFx);
        }
        mWaterType = type;
        if (nvsTimelineVideoFx == null) {
            if (type == EffectItemData.TYPE_MOSAIC) {
                nvsTimelineVideoFx = mTimeline.addBuiltinTimelineVideoFx(0, mTimeline.getDuration(), MOSAICNAME);
                nvsTimelineVideoFx.setRegional(true);
                nvsTimelineVideoFx.setRegionalFeatherWidth(0f);
                nvsTimelineVideoFx.setFloatVal("Unit Size", 0.1f);
                nvsTimelineVideoFx.setFilterIntensity(1);
                float[] fx = getFxData();
                nvsTimelineVideoFx.setRegion(fx);
                mVideoFx = new VideoFx();
                mVideoFx.setRegion(fx);
                mVideoFx.setIntensity(1);
                mVideoFx.setUnitSize(0.1f);
                mVideoFx.setDesc(MOSAICNAME);
                mVideoFx.setType(String.valueOf(type));
            } else if (type == EffectItemData.TYPE_BLUR) {
                nvsTimelineVideoFx = mTimeline.addBuiltinTimelineVideoFx(0, mTimeline.getDuration(), BLURNAME);
                nvsTimelineVideoFx.setRegional(true);
                float[] fx = getFxData();
                nvsTimelineVideoFx.setRegion(fx);
                nvsTimelineVideoFx.setRegionalFeatherWidth(0f);
                nvsTimelineVideoFx.setFloatVal("Radius", 64.0f);
                nvsTimelineVideoFx.setFilterIntensity(1);
                mVideoFx = new VideoFx();
                mVideoFx.setRegion(fx);
                mVideoFx.setIntensity(1);
                mVideoFx.setRadius(64.0f);
                mVideoFx.setDesc(BLURNAME);
                mVideoFx.setType(String.valueOf(type));
                TimelineData.instance().setTimelineVideoFx(mVideoFx);
            }
        } else {
            // 设置mDrawRect位置和尺寸
            // Set mDrawRect position and size
            if (type == EffectItemData.TYPE_MOSAIC) {
                float[] fx = nvsTimelineVideoFx.getRegion();
                mVideoFx = new VideoFx();
                mVideoFx.setRegion(fx);
                mVideoFx.setIntensity(1);
                mVideoFx.setUnitSize((float) nvsTimelineVideoFx.getFloatVal("Unit Size"));
                mVideoFx.setDesc(MOSAICNAME);
                mVideoFx.setType(String.valueOf(type));
            } else if (type == EffectItemData.TYPE_BLUR) {
                float[] fx = nvsTimelineVideoFx.getRegion();
                mVideoFx = new VideoFx();
                mVideoFx.setRegion(fx);
                mVideoFx.setIntensity(1);
                mVideoFx.setRadius((float) nvsTimelineVideoFx.getFloatVal("Radius"));
                mVideoFx.setDesc(BLURNAME);
                mVideoFx.setType(String.valueOf(type));
                TimelineData.instance().setTimelineVideoFx(mVideoFx);
            }
            setDrawRectByEffectData(nvsTimelineVideoFx, type);
        }

        TimelineData.instance().setTimelineVideoFx(mVideoFx);
        //刷新 mRectView位置
        //refresh mRectView position
        refreshLiveWindowFrame();
    }

    /**
     * 根据效果特效位置来更新mDrawRect位置
     * Update the position of mDrawRect according to the position of the effect
     */
    private void setDrawRectByEffectData(NvsTimelineVideoFx videoFx, int type) {
        float[] region = videoFx.getRegion();
        if (region.length >= 8) {
            List<PointF> pointFS = new ArrayList<>();
            pointFS.add(mLiveWindow.mapNormalizedToView(new PointF(region[0], region[1])));
            pointFS.add(mLiveWindow.mapNormalizedToView(new PointF(region[2], region[3])));
            pointFS.add(mLiveWindow.mapNormalizedToView(new PointF(region[4], region[5])));
            pointFS.add(mLiveWindow.mapNormalizedToView(new PointF(region[6], region[7])));
            mDrawRect.setDrawRect(pointFS, type);
        }

    }

    /**
     * 从VideoFx中获取信息并设置
     * Get information from VideoFx and set
     *
     * @param videoFx
     */
    public void setEffectByData(VideoFx videoFx) {
        if (videoFx == null) {
            return;
        }

        int type = Integer.valueOf(videoFx.getType());
        setPointFListLiveWindow(mLiveWindow.getWidth(), mLiveWindow.getHeight());
        setEffectPoint(type);
        mWaterType = type;

        mTimeline.removeTimelineVideoFx(nvsTimelineVideoFx);
        if (type == EffectItemData.TYPE_MOSAIC) {
            nvsTimelineVideoFx = mTimeline.addBuiltinTimelineVideoFx(0, mTimeline.getDuration(), MOSAICNAME);
            nvsTimelineVideoFx.setFilterIntensity(videoFx.getIntensity());
            nvsTimelineVideoFx.setRegional(true);
            nvsTimelineVideoFx.setRegion(videoFx.getRegion());
            nvsTimelineVideoFx.setRegionalFeatherWidth(0f);
            nvsTimelineVideoFx.setFloatVal("Unit Size", videoFx.getUnitSize());

        } else if (type == EffectItemData.TYPE_BLUR) {
            nvsTimelineVideoFx = mTimeline.addBuiltinTimelineVideoFx(0, mTimeline.getDuration(), BLURNAME);
            nvsTimelineVideoFx.setFilterIntensity(videoFx.getIntensity());
            nvsTimelineVideoFx.setRegional(true);
            nvsTimelineVideoFx.setRegion(videoFx.getRegion());
            nvsTimelineVideoFx.setRegionalFeatherWidth(0f);
            nvsTimelineVideoFx.setFloatVal("Radius", videoFx.getRadius());

        }
        refreshLiveWindowFrame();
    }

    public void refreshLiveWindowFrame() {
        if (playState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
        }
    }


    public float[] getFxData() {
        float[] floats = new float[8];
        List<PointF> drawRect = mDrawRect.getDrawRect();
        floats[0] = mLiveWindow.mapViewToNormalized(drawRect.get(0)).x;
        floats[1] = mLiveWindow.mapViewToNormalized(drawRect.get(0)).y;
        floats[2] = mLiveWindow.mapViewToNormalized(drawRect.get(1)).x;
        floats[3] = mLiveWindow.mapViewToNormalized(drawRect.get(1)).y;
        floats[4] = mLiveWindow.mapViewToNormalized(drawRect.get(2)).x;
        floats[5] = mLiveWindow.mapViewToNormalized(drawRect.get(2)).y;
        floats[6] = mLiveWindow.mapViewToNormalized(drawRect.get(3)).x;
        floats[7] = mLiveWindow.mapViewToNormalized(drawRect.get(3)).y;
        return floats;
    }


    public void upDateMosicFxLevel(float level) {
        if (mVideoFx != null) {
            mVideoFx.setIntensity(level);
            nvsTimelineVideoFx.setFilterIntensity(level);
            refreshLiveWindowFrame();
        }
    }

    public void upDateMosicFxNum(float num) {
        if (mVideoFx != null) {
            mVideoFx.setUnitSize(num);
            nvsTimelineVideoFx.setFloatVal("Unit Size", num);
            refreshLiveWindowFrame();
        }
    }


    public void upDateBlurFxLevel(float level) {
        if (mVideoFx != null) {
            mVideoFx.setRadius(level);
            nvsTimelineVideoFx.setFloatVal("Radius", level);
            refreshLiveWindowFrame();
        }
    }

    /**
     * 设置特效类型
     * set effect type
     *
     * @param type
     */
    private void setEffectPoint(int type) {
        int screenWidth = mLiveWindow.getWidth();
        int height = mLiveWindow.getHeight();
        int defaultWidth = (int) getActivity().getResources().getDimension(R.dimen.edit_waterMark_width);
        int defaultHeight = (int) getActivity().getResources().getDimension(R.dimen.edit_waterMark_height);
        setEffectDrawableByPath(screenWidth / 2 - defaultWidth / 2, height / 2 - defaultHeight / 2, type);
    }

    public void setEffectDrawableByPath(int left, int top, int type) {
        mDrawRect.setEffectBitmapByImgPath(left, top, type);
    }


    /**
     * 更新字幕在视图上的坐标
     * Update the coordinates of the subtitle on the view
     */
    public void updateThemeCaptionCoordinate(NvsTimelineCaption caption) {
        if (caption != null) {
            /*
             * 获取字幕的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the caption
             * */
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;

            List<PointF> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                PointF pointF = mLiveWindow.mapCanonicalToView(list.get(i));
                newList.add(pointF);
            }
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_THEMECAPTION);
        }
    }

    public void changeThemeCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_THEMECAPTION) {
            setDrawRectVisible(isSelectedCaption() ? View.VISIBLE : View.GONE);
        }
    }

    public void setAlignIndex(int index) {
        mDrawRect.setAlignIndex(index);
    }

    /**
     * 设置字幕
     * Set subtitles
     */
    public void setCurCaption(NvsCaption caption) {
        mCurCaption = caption;
        if (null != caption) {
            Log.e("mCurCaption", "===" + mCurCaption.getBackgroundRadius());
        }
    }

    public NvsCaption getCurCaption() {
        return mCurCaption;
    }

    /**
     * 更新字幕在视图上的坐标
     * Update the coordinates of the subtitle on the view
     */
    public void updateCaptionCoordinate(NvsCaption caption) {
        if (caption != null) {
            /*
             * 获取字幕的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the caption
             * */
            List<PointF> list = caption.getCaptionBoundingVertices(NvsCaption.BOUNDING_TYPE_FRAME);
            if (list == null || list.size() < 4)
                return;

            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setHorizontal(!caption.getVerticalLayout());
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_CAPTION);
        }
    }

    public void setCurCompoundCaption(NvsTimelineCompoundCaption caption) {
        mCurCompoundCaption = caption;
    }

    public NvsTimelineCompoundCaption getCurrCompoundCaption() {
        return mCurCompoundCaption;
    }

    /**
     * 更新组合字幕在视图上的坐标
     * Update the coordinates of the combined subtitles on the view
     */
    public void updateCompoundCaptionCoordinate(NvsTimelineCompoundCaption caption) {
        if (caption != null) {
            /*
             * 获取字幕的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the caption
             * */
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
            setDrawRectVisible(isSelectedCompoundCaption() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 在liveWindow上手动选择字幕
     * Manually select subtitles on liveWindow
     */
    public void selectCompoundCaptionByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (captionList.size() <= 1)
            return;

        for (int j = 0; j < captionList.size(); j++) {
            NvsTimelineCompoundCaption caption = captionList.get(j);
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_COMPOUND_CAPTION);
                mCurCompoundCaption = caption;
                break;
            }
        }
    }

    public Point getPictureSize(String filePath) {
        Point point = mDrawRect.getPicturePoint(filePath);
        int defaultWidth = (int) getActivity().getResources().getDimension(R.dimen.edit_waterMark_width);
        int defaultHeight = (int) getActivity().getResources().getDimension(R.dimen.edit_waterMark_height);
        if (point != null) {
            defaultHeight = defaultWidth * point.y / point.x;
        }
        return new Point(defaultWidth, defaultHeight);
    }

    /**
     * 第一次添加水印
     * Add watermark for the first time
     *
     * @param w livewindow宽度；livewindow width
     * @param h livewindow高度；livewindow height
     */
    public void firstSetWaterMarkPosition(int w, int h, String filePath) {
        setPointFListLiveWindow(w, h);
        Point point = getPictureSize(filePath);
        int defaultWidth = point.x;
        int defaultHeight = point.y;
        int x0 = w - defaultWidth;
        int x1 = w;
        int y0 = 0;
        List<PointF> newList = setFourPointToList(x0, x1, y0, defaultHeight);
        setPointFListToFirstAddWaterMark(newList);

        /*
         * 初始位置需将操作框按钮都放置在画面内
         * The initial position needs to place the operation box buttons in the screen
         * */
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.delete);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        bitmap.recycle();
        x0 = x0 - bitmapWidth / 2;
        x1 = x1 - bitmapWidth / 2;
        defaultHeight = defaultHeight + bitmapHeight / 2;
        y0 = y0 + bitmapHeight / 2;

        newList = setFourPointToList(x0, x1, y0, defaultHeight);
        mDrawRect.setDrawRect(newList, EDIT_MODE_WATERMARK);
    }

    private List<PointF> getAssetViewVerticesList(List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mLiveWindow.mapCanonicalToView(verticesList.get(i));
            newList.add(pointF);
        }
        return newList;
    }

    /**
     * 根据宽高获取liveWindow的四个角坐标
     * Get the four corner coordinates of livewindow according to the width and height
     */
    private void setPointFListLiveWindow(int w, int h) {
//        int x0 = Math.abs(w - h) / 2;
        int x0 = 0;
        int x1 = w;
        int y0 = 0;
        int y1 = h;
        Logger.e(TAG, "Four angular coordinates of the liveWindow:  " + x0 + "  " + x1 + "  " + y0 + "  " + y1);
        pointFListLiveWindow = setFourPointToList(x0, x1, y0, y1);
    }

    /**
     * 四个点就能确定一个矩形 (x0,y0) (x0,y1) (x1,y1) (x1,y0)
     * Four points can determine a rectangle (x0, y0) (x0, y1) (x1, y1) (x1, y0)
     */
    private void refreshWaterMarkByFourPoint(float x0, float x1, float y0, float y1) {
        List<PointF> newList = setFourPointToList(x0, x1, y0, y1);
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, EDIT_MODE_WATERMARK);
            if (waterMarkChangeListener != null) {
                waterMarkChangeListener.onScaleAndRotate(newList);
            }
        }
    }


    /**
     * 四个点就能确定一个矩形 (x0,y0) (x0,y1) (x1,y1) (x1,y0)
     * Four points can determine a rectangle (x0, y0) (x0, y1) (x1, y1) (x1, y0)
     */
    private void refreshEffectByFourPoint(float x0, float x1, float y0, float y1) {
        List<PointF> newList = setFourPointToList(x0, x1, y0, y1);
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, EDIT_MODE_EFFECT);
//            if (waterMarkChangeListener != null) {
//                waterMarkChangeListener.onScaleAndRotate(newList);
//            }
        }
    }


    public void setDrawRect(List<PointF> newList) {
        mDrawRect.setDrawRect(newList, EDIT_MODE_WATERMARK);
    }

    public List<PointF> getDrawRect() {
        return mDrawRect.getDrawRect();
    }

    /**
     * 四个点坐标转化到list，从左上逆时针
     * Four point coordinates converted to list, counterclockwise from top left
     */
    private List<PointF> setFourPointToList(float x0, float x1, float y0, float y1) {
        List<PointF> newList = new ArrayList<>();
        newList.add(new PointF(x0, y0));
        newList.add(new PointF(x0, y1));
        newList.add(new PointF(x1, y1));
        newList.add(new PointF(x1, y0));
        return newList;
    }

    /**
     * 坐标集合是否在liveWindow内
     * Whether the coordinate collection is in the liveWindow
     *
     * @param newList
     * @return
     */
    private boolean checkInLiveWindow(List<PointF> newList) {
        if (pointFListLiveWindow != null) {
            float minX = pointFListLiveWindow.get(0).x;
            float maxX = pointFListLiveWindow.get(2).x;
            float minY = pointFListLiveWindow.get(0).y;
            float maxY = pointFListLiveWindow.get(2).y;
            for (PointF pointF : newList) {
                if (pointF.x < minX || pointF.x > maxX || pointF.y < minY || pointF.y > maxY) {
                    Logger.e(TAG, "checkInLiveWindow " + minX + "       " + pointF.x + "      " + maxX);
                    Logger.e(TAG, "checkInLiveWindow " + minY + "       " + pointF.y + "      " + maxY);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 更新坐标(边界检测，出了边界后挪回)
     * Update coordinates (boundary detection, move back after the boundary is out)
     *
     * @param newList
     */
    private void updateInLiveWindow(List<PointF> newList) {
        if (checkInLiveWindow(newList)) {
            return;
        }
        if (pointFListLiveWindow != null) {
            float minX = pointFListLiveWindow.get(0).x;
            float maxX = pointFListLiveWindow.get(2).x;
            float minY = pointFListLiveWindow.get(0).y;
            float maxY = pointFListLiveWindow.get(2).y;
            if (newList != null && newList.size() >= 4) {
                float dx = 0, dy = 0;
                //4个点左上，左下，右下，右上
                //Four dots top left, bottom left, bottom right, top right
                if (newList.get(0).x < minX) {
                    dx = newList.get(0).x - minX;
                }
                if (newList.get(3).x > maxX) {
                    dx = newList.get(3).x - maxX;
                }
                if (newList.get(0).y < minY) {
                    dy = newList.get(0).y - minY;
                }
                if (newList.get(1).y > maxY) {
                    dy = newList.get(1).y - maxY;
                }
                for (PointF pointF : newList) {
                    pointF.x -= dx;
                    pointF.y -= dy;
                }
            }

        }
    }

    /**
     * 拖动过程中更新水印DrawRect的位置
     * Update the position of the waterMark DrawRect during the drag process
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
        updateInLiveWindow(newList);
        mDrawRect.setDrawRect(newList, EDIT_MODE_WATERMARK);
        if (waterMarkChangeListener != null) {
            waterMarkChangeListener.onDrag(newList);
        }

    }

    /**
     * 拖动过程中更新效果DrawRect的位置
     * Update the position of the effect DrawRect during the drag process
     *
     * @param x
     * @param y
     * @param list
     */
    private void updateEffectPositionOnDrag(float x, float y, List<PointF> list) {
        List<PointF> newList = new ArrayList<>();
        for (PointF pointF : list) {
            newList.add(new PointF(pointF.x + x, pointF.y - y));
        }
        updateInLiveWindow(newList);
        mDrawRect.setDrawRect(newList, EDIT_MODE_EFFECT);
        if (waterMarkChangeListener != null) {
            waterMarkChangeListener.onDrag(newList);
        }

    }

    /**
     * @param scaleDregree 缩放比例；scaling ratio
     * @param centerPoint  中心坐标；Center coordinates
     * @param angle        旋转角度；Rotation angle
     * @param list         方框坐标点；Box coordinates
     */
    private void updateWaterMarkPositionOnScaleAndRotate(float scaleDregree, PointF centerPoint, float angle, List<PointF> list) {
        float width = Math.abs(list.get(0).x - list.get(3).x);
        float height = Math.abs(list.get(0).y - list.get(1).y);
        float x0 = centerPoint.x - width / 2 * scaleDregree;
        float x1 = centerPoint.x + width / 2 * scaleDregree;
        float y0 = centerPoint.y - height / 2 * scaleDregree;
        float y1 = centerPoint.y + height / 2 * scaleDregree;
        refreshWaterMarkByFourPoint(x0, x1, y0, y1);
    }


    /**
     * @param scaleDregree 缩放比例；scaling ratio
     * @param centerPoint  中心坐标；Center coordinates
     * @param angle        旋转角度；Rotation angle
     * @param list         方框坐标点；Box coordinates
     */
    private void updateEffectPositionOnScaleAndRotate(float scaleDregree, PointF centerPoint, float angle, List<PointF> list) {
        float width = Math.abs(list.get(0).x - list.get(3).x);
        float height = Math.abs(list.get(0).y - list.get(1).y);
        float x0 = centerPoint.x - width / 2 * scaleDregree;
        float x1 = centerPoint.x + width / 2 * scaleDregree;
        float y0 = centerPoint.y - height / 2 * scaleDregree;
        float y1 = centerPoint.y + height / 2 * scaleDregree;
        refreshEffectByFourPoint(x0, x1, y0, y1);
    }


    public void setDrawRectVisible(int visibility) {
        mDrawRect.setVisibility(visibility);
    }

    public void setPicturePath(String path) {
        mDrawRect.setPicturePath(path);
    }

    public void changeCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_CAPTION) {
            setDrawRectVisible(isSelectedCaption() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 在liveWindow上手动选择字幕
     * Manually select subtitles on liveWindow
     */
    public void selectCaptionByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (captionList.size() <= 1)
            return;

        for (int j = 0; j < captionList.size(); j++) {
            NvsTimelineCaption caption = captionList.get(j);
            List<PointF> list = caption.getBoundingRectangleVertices();
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setHorizontal(!caption.getVerticalLayout());
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_CAPTION);
                mCurCaption = caption;
                break;
            }
        }
    }

    public boolean curPointIsInnerDrawRect(int xPos, int yPos) {
        return mDrawRect.curPointIsInnerDrawRect(xPos, yPos);
    }

    public void setAutoPlay(boolean flag) {
        mAutoPlay = flag;
    }

    public void setRecording(boolean record_state) {
        mRecording = record_state;
    }

    public void setCurAnimateSticker(NvsAnimatedSticker animateSticker) {
        mCurAnimateSticker = animateSticker;
    }

    public void setStickerMuteIndex(int index) {
        mStickerMuteIndex = index;
        mDrawRect.setStickerMuteIndex(index);
    }


    public NvsAnimatedSticker getCurAnimateSticker() {
        return mCurAnimateSticker;
    }

    /**
     * 更新贴纸在视图上的坐标
     * Update the coordinates of the sticker on the view
     */
    public void updateAnimateStickerCoordinate(NvsAnimatedSticker animateSticker) {
        if (animateSticker != null) {
            /*
             * 获取贴纸的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the sticker
             * */
            List<PointF> list = animateSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;
            boolean isHorizonFlip = animateSticker.getHorizontalFlip();
            if (isHorizonFlip) {
                /*
                 * 如果已水平翻转，需要对顶点数据进行处理
                 * If flipped horizontally, you need to process the vertex data
                 * */
                Collections.swap(list, 0, 3);
                Collections.swap(list, 1, 2);
            }
            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
        }
    }

    /**
     * 设置贴纸选择框显隐
     * Set the sticker selection box to be hidden
     */
    public void changeStickerRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_STICKER) {
            setDrawRectVisible(isSelectedAnimateSticker() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 在liveWindow上手动选择贴纸
     * Manually select stickers on liveWindow
     */
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

    public void selectClipAnimateStickerByHandClick(NvsVideoClip videoClip, PointF curPoint) {
        if (videoClip == null) {
            return;
        }
        List<NvsClipAnimatedSticker> stickerList = videoClip.getAnimatedStickersByClipTimePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (stickerList.size() <= 1)
            return;

        for (int j = 0; j < stickerList.size(); j++) {
            NvsClipAnimatedSticker sticker = stickerList.get(j);
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

    /**
     * 连接时间线跟liveWindow
     * Connect timeline with liveWindow
     */
    public void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null)
            return;
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
                if (mPlayBarVisibleState) {
                    //m_handler.sendEmptyMessage(RESETPLATBACKSTATE);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playBackEOF(nvsTimeline);
                }
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                if (mPlayBarVisibleState) {
                    updateCurPlayTime(cur_position);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playbackTimelinePosition(nvsTimeline, cur_position);
                }
                /*
                 *  播放进度条消失
                 * The playback progress bar disappears
                 * */
                if (mPlayBarVisibleState) {
                    if (mPlayStartFlag != -1) {
                        if (cur_position - mPlayStartFlag >= 4000000)
                            mPlayBarLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                playState = i;
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayImage.setBackgroundResource(R.mipmap.icon_edit_pause);
                    startHidePlayBarTimer(false);
                } else {
                    mPlayImage.setBackgroundResource(R.mipmap.icon_edit_play);
                    mPlayBarLayout.setVisibility(mPlayBarVisibleState ? View.VISIBLE : View.GONE);
                    startHidePlayBarTimer(true);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.streamingEngineStateChanged(i);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });

        mStreamingContext.setHardwareErrorCallback(new NvsStreamingContext.HardwareErrorCallback() {
            @Override
            public void onHardwareError(int errorType, String stringInfo) {
                Log.e(TAG, "onHardwareError: errorType: " + errorType + " stringInfo: " + stringInfo);
            }
        });
        mStreamingContext.setWebCallback(new NvsStreamingContext.WebCallback() {
            @Override
            public void notifyWebRequestWaitStatusChange(boolean isVideo, boolean isWaiting) {
                mVideoLoading.setVisibility(isWaiting ? View.VISIBLE : View.GONE);
            }
        });

        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
    }

    private boolean isSelectedCompoundCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurCompoundCaption != null
                && curPosition >= mCurCompoundCaption.getInPoint()
                && curPosition <= mCurCompoundCaption.getOutPoint()) {
            return true;
        }
        return false;
    }

    private boolean isSelectedCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurCaption != null) {
            long inPoint = 0, outPoint = 0;
            if (mCurCaption instanceof NvsTimelineCaption) {
                inPoint = ((NvsTimelineCaption) mCurCaption).getInPoint();
                outPoint = ((NvsTimelineCaption) mCurCaption).getOutPoint();
            } else if (mCurCaption instanceof NvsClipCaption) {
                inPoint = ((NvsClipCaption) mCurCaption).getInPoint();
                outPoint = ((NvsClipCaption) mCurCaption).getOutPoint();
            }
            if (curPosition >= inPoint && curPosition <= outPoint) {
                return true;
            }
        }
        return false;
    }

    private boolean isSelectedAnimateSticker() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurAnimateSticker != null) {
            long inPoint = 0, outPoint = 0;
            if (mCurAnimateSticker instanceof NvsClipAnimatedSticker) {
                inPoint = ((NvsClipAnimatedSticker) mCurAnimateSticker).getInPoint();
                outPoint = ((NvsClipAnimatedSticker) mCurAnimateSticker).getOutPoint();
            } else if (mCurAnimateSticker instanceof NvsTimelineAnimatedSticker) {
                inPoint = ((NvsTimelineAnimatedSticker) mCurAnimateSticker).getInPoint();
                outPoint = ((NvsTimelineAnimatedSticker) mCurAnimateSticker).getOutPoint();
            }
            if (mCurAnimateSticker != null
                    && curPosition >= inPoint
                    && curPosition <= outPoint) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        int currEngineState = getCurrentEngineState();
        if (currEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_COMPILE) {
            connectTimelineWithLiveWindow();
            long stamp = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            updateCurPlayTime(stamp);
            Log.e(TAG, "onResume");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAutoPlay && mPlayImage != null) {
                        playVideoButtonClick();
                    }
                }
            }, 100);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDrawRect.cleanUp();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mVideoFragmentCallBack = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    /**
     * 播放视频
     * Play video
     */
    public void playVideo(long startTime, long endTime) {
        int flag = NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_SPEED_COMP_MODE;
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        int compileHeight = settingValues.getCompileVideoRes();
        if (compileHeight == SettingActivity.CompileVideoRes_2160) {
            NvsRational nvsRational = new NvsRational(1, 2);
            if (mPlayFlag == 0) {
                mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, nvsRational, true, flag);
            } else {
                mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, nvsRational, true, mPlayFlag | flag);
            }
        } else {
            if (mPlayFlag == 0) {
                mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, flag);
            } else {
                mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, mPlayFlag | flag);
            }

        }

    }

    /**
     * 预览
     * Seek
     */
    public void seekTimeline(long timestamp, int seekShowMode) {
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        int compileHeight = settingValues.getCompileVideoRes();
        if (compileHeight == SettingActivity.CompileVideoRes_2160) {
            NvsRational nvsRational = new NvsRational(1, 2);
            mStreamingContext.seekTimeline(mTimeline, timestamp,
                    nvsRational,
                    seekShowMode | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);
        } else {
            mStreamingContext.seekTimeline(mTimeline, timestamp,
                    NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
                    seekShowMode | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);

        }

    }

    /**
     * 获取当前引擎状态
     * Get the current engine status
     */
    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    /**
     * 停止引擎
     * Stop the engine
     */
    public void stopEngine() {
        //生成的时候不要调用stop，防止切换到后台被停止
        //Do not call stop while generating to prevent the switch to the background from being stopped
        if (getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_COMPILE) {
            if (mStreamingContext != null) {
                mStreamingContext.stop();
            }
        }
    }

    public void playVideoButtonClick() {
        if (mTimeline == null) {
            return;
        }
        long endTime = mTimeline.getDuration();
        playVideoButtonClick(0, endTime);
    }

    public void playVideoButtonClick(long inPoint, long outPoint) {
        playVideo(inPoint, outPoint);
        /*
         * 更新播放进度条显示标识
         * Update the playback progress bar indicator
         * */
        if (mPlayBarVisibleState) {
            mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            mPlayBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setLiveWindowRatio(int ratio, int titleHeight, int bottomHeight) {
        ViewGroup.LayoutParams layoutParams = mPlayerLayout.getLayoutParams();
        int statusHeight = ScreenUtils.getStatusBarHeight(getActivity());
        int screenWidth = ScreenUtils.getScreenWidth(getActivity());
        int screenHeight = ScreenUtils.getScreenHeight(getActivity());
        int newHeight = 0;
        if (mLayoutType == TYPE_CAPTION) {
            newHeight = screenHeight - titleHeight - bottomHeight - statusHeight;
        } else {
            newHeight = screenHeight - titleHeight - bottomHeight;
        }
        switch (ratio) {
            case NvAsset.AspectRatio_16v9: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
            case NvAsset.AspectRatio_1v1: //1:1
                layoutParams.width = screenWidth;
                layoutParams.height = screenWidth;
                if (newHeight < screenWidth) {
                    layoutParams.width = newHeight;
                    layoutParams.height = newHeight;
                }
                break;
            case NvAsset.AspectRatio_9v16: //9:16
                layoutParams.width = (int) (newHeight * 9.0 / 16);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_3v4: // 3:4
                layoutParams.width = (int) (newHeight * 3.0 / 4);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_4v3: //4:3
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 3.0 / 4);
                break;
            case NvAsset.AspectRatio_21v9: //21:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 21);
                break;
            case NvAsset.AspectRatio_9v21: //9:21
                layoutParams.width = (int) (newHeight * 9.0 / 21);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_18v9: //18:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 18);
                break;
            case NvAsset.AspectRatio_9v18: //9:18
                layoutParams.width = (int) (newHeight * 9.0 / 18);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_7v6: //7:6
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 6.0 / 7);
                break;
            case NvAsset.AspectRatio_6v7: //6:7
                layoutParams.width = (int) (newHeight * 6.0 / 7);
                layoutParams.height = newHeight;
                break;
            default: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
        }
        mPlayerLayout.setLayoutParams(layoutParams);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
    }

    //formate time
    private String formatTimeStrWithUs(long us) {
        int second = (int) (us / 1000000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }

    private void controllerOperation() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    stopEngine();
                    /*
                     * 更新播放进度条显示标识
                     * Update the playback progress bar indicator
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                    }
                } else {
                    if (mTimeline == null) {
                        return;
                    }
                    //播放进度，如果是动画的页面，播放的可能是片段或者是整体，指定了播放的范围
                    //The playback schedule, if it is an animated page, may be a segment or a whole, which specifies the range of playback
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    long endTime = mTimeline.getDuration();
                    if (mIsAnimationView || mIsBackgroundView) {
                        endTime = mPlayEndPoint;
                        if (startTime < mPlayStartPoint) {
                            startTime = mPlayStartPoint;
                        }
                    }
                    playVideo(startTime, endTime);
                    /*
                     * 更新播放进度条显示标识
                     * Update the playback progress bar indicator
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    }
                }
            }
        });

        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long currentTimeStamp = progress * BASE_VALUE;
                   /* if(mIsAnimationView){
                        currentTimeStamp = currentTimeStamp+mPlayStartPoint;
                    }*/
                    seekTimeline(currentTimeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                    updateCurPlayTime(progress * BASE_VALUE);
                    if (mThemeCaptionSeekListener != null) {
                        mThemeCaptionSeekListener.onThemeCaptionSeek(progress * BASE_VALUE);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mShowSeekbar = true;
                startHidePlayBarTimer(false);
                NvsStreamingContext.getInstance().setTimelineScaleForSeek(mTimeline, mTimeline.getDuration() / 1000000D / seekBar.getWidth());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mShowSeekbar = false;
                startHidePlayBarTimer(true);
                seekTimeline(seekBar.getProgress() * BASE_VALUE, 0);

            }
        });
        mVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoVolumeListener != null) {
                    mVideoVolumeListener.onVideoVolume();
                }
            }
        });

        mLiveWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLiveWindowClickListener != null) {
                    mLiveWindowClickListener.onLiveWindowClick();
                }
                /*
                 * 如果正在录音，禁止操作
                 * Prohibit operation if recording
                 * */
                if (mRecording) {
                    return;
                }

                if (mIsLimitPlay) {
                    return;
                }

                /*
                 * 播放进度条显示
                 * Play progress bar display
                 * */
                if (mPlayBarVisibleState) {
                    if (mPlayBarLayout.getVisibility() == View.INVISIBLE) {
                        mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        mPlayBarLayout.setVisibility(View.VISIBLE);
                        startHidePlayBarTimer(true);
                        return;
                    }
                }
                mPlayButton.callOnClick();
            }
        });
    }

    public NvsLiveWindow getLiveWindow() {
        return mLiveWindow;
    }

    public List<PointF> getPointFListToFirstAddWaterMark() {
        if (pointFListToFirstAddWaterMark == null) {
            return new ArrayList<>();
        }
        return pointFListToFirstAddWaterMark;
    }

    public void setPointFListToFirstAddWaterMark(List<PointF> pointFListToFirstAddWaterMark) {
        this.pointFListToFirstAddWaterMark = pointFListToFirstAddWaterMark;
    }

    public void setClipImageViewBitmap(Bitmap bitmap, boolean reset) {
        if (bitmap == null) {
            return;
        }
        if (reset) {
            mClipImageView.setVisibility(View.VISIBLE);
        }
        mClipImageView.changeImageBitmap(bitmap, reset);
    }

    public void resetClipImageView() {
        mClipImageView.resetClipImageView();
    }

    public Bitmap getCoverImageBitmap() {
        return mClipImageView.clip();
    }

    public interface IBeforeAnimateStickerEditListener {
        boolean beforeTransitionCouldDo();

        boolean beforeScaleCouldDo();
    }

    public void setBeforeAnimateStickerEditListener(IBeforeAnimateStickerEditListener beforeAnimateStickerEditListener) {
        this.mBeforeAnimateStickerEditListener = beforeAnimateStickerEditListener;
    }

    public void setPlaySeekVisiable(boolean visiable) {
        if (visiable) {
            mPlayBarLayout.setVisibility(View.VISIBLE);
        } else {
            mPlayBarLayout.setVisibility(View.INVISIBLE);
        }
        mPlayBarVisibleState = visiable;
    }

    public void startHidePlayBarTimer(boolean start) {
        if (mPlayBarVisibleState) {
            m_hidePlayBarTimer.cancel();
            if (start) {
                m_hidePlayBarTimer.start();
            }
        }
    }

    /**
     * 设置播放进度调的最大值和当前的进度 (动画页面 和 背景页面 使用)
     * Set the maximum value of the playback progress and the current progress
     * (used by the animation page and the background page)
     *
     * @param playStartPoint 当前播放的片段的起点,相对timeline来讲 The starting point of the clip that is currently playing, relative to timeline
     * @param currentPoint   当前进度 Current progress
     * @param duration       总长度 Total length
     */
    public void setmPlaySeekBarMaxAndCurrent(long playStartPoint, long mPlayEndPoint, long currentPoint, long duration) {
        this.mPlayStartPoint = playStartPoint;
        this.mPlayEndPoint = mPlayEndPoint;
        mPlaySeekBar.setProgress((int) (currentPoint / BASE_VALUE));
        mPlaySeekBar.setMax((int) (duration / BASE_VALUE) - 1);
        mTotalDuration.setText(formatTimeStrWithUs(duration));
    }

    public void setOnBackgroundChangedListener(OnBackgroundChangedListener listener) {
        this.mOnBackgroundChangedListener = listener;
    }

    public interface OnBackgroundChangedListener {
        void onBackgroundChanged();
    }

    public int getHeight() {
        return mFlRootContainer.getHeight();
    }


    public void setPlayFlag(int playFlag) {
        this.mPlayFlag = playFlag;
    }


    public void setIsLimitPlay(boolean isLimitPlay) {
        this.mIsLimitPlay = isLimitPlay;
    }

    public void setLiveWindowLayout() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayerLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerLayout.setLayoutParams(layoutParams);
    }

    private OnDoubleFlingerScaleListener mOnRectScaleListener;

    public void setOnRectScaleListener(OnDoubleFlingerScaleListener mOnRectScaleListener) {
        isSupportDoubleFlingerScale = true;
        this.mOnRectScaleListener = mOnRectScaleListener;
    }

    public interface OnDoubleFlingerScaleListener {
        void onScale(View view, float onScale);
    }

    public void setScale(float scale) {
        if (mDrawRectContainer != null) {
            mDrawRectContainer.setScale(scale);
        }
    }

}
