package com.meishe.cutsame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.view.NvsTimelineEditor;
import com.meishe.base.view.NvsTimelineTimeSpan;
import com.meishe.cutsame.fragment.BaseVideoFragment;
import com.meishe.cutsame.view.TailorView;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.interf.StreamingConstant;
import com.meishe.engine.interf.VideoFragmentListenerWithClick;
import com.meishe.engine.util.TimelineUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : CaoZhiChao
 * @CreateDate : 2020/11/27 16:29
 * @Description : 剪同款的视频裁剪 Cut video clip Activity
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class TailorFreeActivity extends BaseActivity implements VideoFragmentListenerWithClick {
    public static final String VIDEO_PATH = "videoPath";
    public static final String VIDEO_LIMIT = "videoLimit";
    public static final String VIDEO_TYPE = "videoType";
    public static final String START_TRIM = "startTrim";
    public static final String CLIP_INDEX = "clipIndex";
    public static final String END_TRIM = "endTrim";
    private static final String TAG = TailorFreeActivity.class.getSimpleName();
    private BaseVideoFragment mBaseVideoFragment;
    private String mVideoPath;
    private int mVideoType;
    private long mStartTrim = 0;
    private long mEndTrim = 0;
    private int mState = -1;
    private int clipIndex;
    private long mNowStartTime = 0;
    private NvsTimelineEditor mTimelineEditor;
    private NvsMultiThumbnailSequenceView mMultiThumbnailSequenceView;
    private NvsTimeline mNvsTimeline;
    private ImageView mVideoPlay;
    private TextView mActivityTailorTextLimit;
    private TextView mTvTrimDuration;
    private long mTrimIn;
    private long mTrimOut;
    private NvsStreamingContext mNvsStreamingContext;
    private boolean mIsSeekTimeline;

    @Override
    protected int bindLayout() {
        return R.layout.activity_tailor_free;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            mVideoPath = extras.getString(VIDEO_PATH);
            mVideoType = extras.getInt(VIDEO_TYPE);
            mStartTrim = extras.getLong(START_TRIM);
            mEndTrim = extras.getLong(END_TRIM);
            mNowStartTime = mStartTrim;
            clipIndex = extras.getInt(CLIP_INDEX);
        }
        mNvsStreamingContext = NvsStreamingContext.getInstance();
        if (!TextUtils.isEmpty(mVideoPath)) {
            mNvsTimeline = TimelineUtil.newTimeline(TimelineUtil.getVideoEditResolutionByClip(mVideoPath));
            EditorController.getInstance().setNvsTimeline(mNvsTimeline);
            EditorController.getInstance().seekTimeline(mNowStartTime);
            List<MeicamVideoTrack> videoTrackList = new ArrayList<>();
            MeicamVideoTrack meicamVideoTrack = new MeicamVideoTrack(0);
            long duration = 0;
            String videoType = CommonData.CLIP_VIDEO;

            if (mVideoType == StreamingConstant.VideoClipType.VIDEO_CLIP_TYPE_AV) {
                duration = EditorController.getInstance().getVideoDuration(mVideoPath);
            } else {
                videoType = CommonData.CLIP_IMAGE;
                duration = 4000000;
            }
            MeicamVideoClip newClip = new MeicamVideoClip(mVideoPath, videoType, duration);
            newClip.setTrimOut(duration);
            meicamVideoTrack.getClipInfoList().add(newClip);
            videoTrackList.add(meicamVideoTrack);
            TimelineUtil.fillTrack(EditorController.getInstance().getNvsTimeline(), videoTrackList);
        } else {
            LogUtils.e("initData: error! mVideoPath is empty!");
        }
    }

    @Override
    protected void initView() {
        addVideoFragment();
        ImageView activityTailorBack = findViewById(R.id.activity_tailor_back);
        activityTailorBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

        Button activityTailorSure = findViewById(R.id.activity_tailor_sure);
        activityTailorSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(CutSameEditorActivity.INTENT_TRAM_IN, mTrimIn);
                intent.putExtra(CutSameEditorActivity.INTENT_TRAM_OUT, mTrimOut);
                intent.putExtra(CutSameEditorActivity.INTENT_EDIT_CLIP_INDEX, clipIndex);
                // 设置返回码和返回携带的数据 Sets the return code and returns the data carried
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        Drawable drawable = CommonUtils.getRadiusDrawable(-1, -1,
                getResources().getDimensionPixelOffset(R.dimen.dp_px_150), getResources().getColor(R.color.activity_tailor_button_background));
        activityTailorSure.setBackground(drawable);


        mTrimIn = 0;
        mTrimOut = mNvsTimeline.getDuration();

        mActivityTailorTextLimit = findViewById(R.id.activity_tailor_text_limit);
        mActivityTailorTextLimit.setText("00:00");

        mTvTrimDuration = findViewById(R.id.tv_cut_same_trim_duration);


        mVideoPlay = (ImageView) findViewById(R.id.videoPlay);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.timelineEditor);
        mMultiThumbnailSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();

        mMultiThumbnailSequenceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mIsSeekTimeline = true;
                return false;
            }
        });


        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                if (!EditorController.getInstance().isPlaying()) {
                    EditorController.getInstance().seekTimeline(timeStamp);
                    updateCurrentTime(timeStamp);
                }
            }
        });

        initMultiSequence();
        addTimeSpan(mStartTrim, mEndTrim);
        mTrimIn = mStartTrim;
        mTrimOut = mEndTrim;

        updateTrimDuration(mTrimIn, mTrimOut);

    }

    private void updateTrimDuration(long trimIn, long trimOut) {
        if (trimOut < trimIn) {
            return;
        }
        mTvTrimDuration.setText("裁剪片段时长：" + FormatUtils.microsecond2Time(trimOut - trimIn));
    }

    private void addVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mBaseVideoFragment = BaseVideoFragment.newInstance(true);
        mBaseVideoFragment.setVideoFragmentListener(this);
        fragmentManager.beginTransaction().add(R.id.activity_tailor_fragment_container, mBaseVideoFragment).commit();
        fragmentManager.beginTransaction().show(mBaseVideoFragment);
    }

    /**
     * 初始化缩略图
     * Initialize thumbnail
     */
    private void initMultiSequence() {
        NvsVideoTrack videoTrack = mNvsTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        int clipCount = videoTrack.getClipCount();
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
        for (int index = 0; index < clipCount; ++index) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
            if (videoClip == null) {
                continue;
            }
            /*ThumbnailSequenceDesc 对应的clip的描述*/
            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescsArray.add(sequenceDescs);
        }

        long duration = mNvsTimeline.getDuration();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoPlay.getLayoutParams();
//        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int halfScreenWidth = ScreenUtils.getScreenWidth() / 2;
//        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        int sequenceLeftPadding = halfScreenWidth;

        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);


    }


    /**
     * 添加一个timeSpan
     * add a new timeSpan
     *
     * @param inPoint
     * @param outPoint
     * @return
     */
    private NvsTimelineTimeSpan addTimeSpan(long inPoint, long outPoint) {
        /*
         * warning: 使用addTimeSpanExt之前必须设置setTimeSpanType()
         * warning: setTimeSpanType () must be set before using addTimeSpanExt
         * */
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        final NvsTimelineTimeSpan timelineTimeSpan = mTimelineEditor.addTimeSpan(inPoint, outPoint);
        if (timelineTimeSpan == null) {
            Log.e(TAG, "addTimeSpan: " + " add TimeSpan failed!");
            return null;
        }
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimInChangeListener() {
            @Override
            public void onTrimInChange(long timeStamp, boolean isDragEnd) {
                if (EditorController.getInstance().isPlaying()) {
                    mBaseVideoFragment.onPause();
                }
                mTrimIn = timeStamp;
                updateTrimDuration(mTrimIn, mTrimOut);
            }
        });

        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimOutChangeListener() {
            @Override
            public void onTrimOutChange(long timeStamp, boolean isDragEnd) {
                if (EditorController.getInstance().isPlaying()) {
                    mBaseVideoFragment.onPause();
                }
                mTrimOut = timeStamp;
                updateTrimDuration(mTrimIn, mTrimOut);
            }
        });

        timelineTimeSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EditorController.getInstance().isPlaying()) {
                    mBaseVideoFragment.onPause();
                }
            }
        });

        return timelineTimeSpan;
    }

    /**
     * 多图平滑滚动到指定时间戳
     * Smooth scrolling of multiple images to the specified timestamp
     *
     * @param stamp
     */
    private void multiThumbnailSequenceViewSmooth(long stamp) {
        if (mMultiThumbnailSequenceView != null) {
            int x = Math.round((stamp / (float) mNvsTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
            mMultiThumbnailSequenceView.smoothScrollTo(x, 0);
        }
    }


    /**
     * 更新缩略图
     * seek the multiThumb view
     */
    private void seekMultiThumbnailSequenceView() {
        if (mMultiThumbnailSequenceView != null) {
            long curPos = NvsStreamingContext.getInstance().getTimelineCurrentPosition(mNvsTimeline);
            long duration = mNvsTimeline.getDuration();
            mMultiThumbnailSequenceView.scrollTo(Math.round(((float) curPos) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }


    @Override
    public void playBackEOF(NvsTimeline timeline) {
//        if (mBaseVideoFragment != null) {
//            mBaseVideoFragment.changePlayButtonState(true);
//        }
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(true);
        }
        updateCurrentTime(timeline.getDuration());
    }

    @Override
    public void playStopped(NvsTimeline timeline) {
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(true);
        }
    }

    @Override
    public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
        mState = TailorView.FROM_VIDEO;

        multiThumbnailSequenceViewSmooth(stamp);
        updateCurrentTime(stamp);

    }

    private void updateCurrentTime(long stamp) {
        String nvStringTime = FormatUtils.microsecond2Time(stamp);
        mActivityTailorTextLimit.setText(nvStringTime);
    }

    @Override
    public void streamingEngineStateChanged(int state) {
        boolean playing = EditorController.getInstance().isPlaying();
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(!playing);
//            if (playing) {
//                mBaseVideoFragment.notShowCaptionBox();
//            }
        }
//        if (EditorController.getInstance().isPlaying()) {
//            mState = TailorView.FROM_VIDEO;
//            mTailorView.setState(mState);
//        } else {
//            mTailorView.setState(TailorView.FROM_USER);
//        }

        if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mIsSeekTimeline = false;
        } else {
            mIsSeekTimeline = true;
        }

    }

    @Override
    public void onSeekingTimelinePosition(NvsTimeline timeline, long position) {

    }

    @Override
    public boolean clickPlayButtonByOthers() {
//        controllerVideoFragmentClick();
        return false;
    }

    @Override
    public boolean clickLiveWindowByOthers() {
//        controllerVideoFragmentClick();
        return false;
    }

    @Override
    public void connectTimelineWithLiveWindow() {
        EditorController.getInstance().seekTimeline(mTrimIn);
        EditorController.getInstance().playNow(mTrimIn, mNvsTimeline.getDuration());
    }

    private void controllerVideoFragmentClick() {
        if (!EditorController.getInstance().isPlaying()) {
            EditorController.getInstance().playNow(EditorController.getInstance().nowTime(), mNowStartTime);
        } else {
            EditorController.getInstance().stop();
        }
    }
}