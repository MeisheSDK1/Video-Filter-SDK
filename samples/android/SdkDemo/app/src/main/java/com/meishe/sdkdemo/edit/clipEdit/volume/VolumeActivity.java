package com.meishe.sdkdemo.edit.clipEdit.volume;

import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.adapter.CurveAdjustViewAdapter;
import com.meishe.sdkdemo.edit.clipEdit.SingleClipFragment;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.CurveAdjustData;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineTimeSpan;
import com.meishe.sdkdemo.edit.view.BezierAdjustView;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VolumeKeyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-编辑-音量-Activity
 * @Description :VideoEdit-edit-volume-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class VolumeActivity extends BaseActivity {
    private static final String TAG = "VolumeActivity";
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private SeekBar mVolumeSeekBar;
    private TextView mVolumeSeekBarValue;
    private ImageView mVolumeFinish;
    private SingleClipFragment mClipFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex = 0;
    //关键帧模式标记 Keyframe mode flag
    private String keyFrameState = KEY_FRAME_STATE_NOR;
    //非关键帧模式 即正常模式 The non-keyframe mode is the normal mode
    private static final String KEY_FRAME_STATE_NOR = "nor";
    //关键帧模式——编辑关键帧 Keyframe mode -- Edit keyframes
    private static final String KEY_FRAME_STATE_EDITING = "editing";
    //关键帧模式--可编辑曲线变速状态 Keyframe mode -- editable curve shift mode
    private static final String KEY_FRAME_STATE_CURVE = "curve";
    private TextView mTvKeyFrame;
    private View llKeyFrame;
    private NvsTimelineEditor mTimelineEditor;
    private NvsMultiThumbnailSequenceView mMultiSequenceView;
    private View curveLayout;
    private TextView tvCurveTip;
    private RecyclerView rvCurveAdjust;
    private CurveAdjustViewAdapter mAdapter;
    private View ivConfirmCurve;
    private List<CurveAdjustData> mCurveAdjustDataList;
    private TextView addOrDeleteFrame;
    private TextView lastFrame;
    private TextView nextFrame;
    private Map<Long, VolumeKeyInfo> volumeKeyInfoMap;
    private RelativeLayout mPlayBtnLayout;
    private Button mPlayBtn;
    private boolean mCanSeekTimeline;
    private long currentKeyFrameTime = -1;
    private int TimeBase = 100000;//时间戳允许差值 The timestamp allows the difference
    private TextView tvCurTime;
    private RectF mCustomBezierRectF;
    private View curveAdjustLayout;
    private BezierAdjustView mBezierAdjustView;
    private ImageView ivConfirmCurveAdjust;
    private VolumeKeyInfo mBeforeKeyFrameInfo;
    private VolumeKeyInfo mNextKeyFrameInfo;
    private NvsTimelineTimeSpan timelineTimeSpan;
    private Map<Long, KeyFrameInfo> keyMap = new TreeMap<>();
    private NvsVideoClip videoClip;


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_volume;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        mVolumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);
        mVolumeSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mVolumeSeekBar.setVisibility(View.GONE);
        mVolumeSeekBarValue = (TextView) findViewById(R.id.volumeSeekBarValue);
        mVolumeFinish = (ImageView) findViewById(R.id.volumeFinish);
        mTvKeyFrame = findViewById(R.id.tv_key_frame);
        llKeyFrame = findViewById(R.id.ll_key_frame);
        curveLayout = findViewById(R.id.curve_list_layout);
        tvCurveTip = findViewById(R.id.tv_curve_tip);
        rvCurveAdjust = findViewById(R.id.rv_curve_adjust);
        ivConfirmCurve = findViewById(R.id.iv_confirm_curve);
        mTimelineEditor = findViewById(R.id.volume_timeline_editor);
        mPlayBtnLayout = findViewById(R.id.play_btn_layout);
        mPlayBtn = findViewById(R.id.play_btn);
        mBezierAdjustView = findViewById(R.id.bezier_adjust_view);
        ivConfirmCurveAdjust = findViewById(R.id.iv_confirm_curve_adjust);
        curveAdjustLayout = findViewById(R.id.curve_adjust_layout);

        addOrDeleteFrame = findViewById(R.id.tv_add_or_delete_frame);
        lastFrame = findViewById(R.id.tv_last_frame);
        nextFrame = findViewById(R.id.tv_next_frame);
        tvCurTime = findViewById(R.id.play_cur_time);
        mMultiSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.volume);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size())
            return;
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        if (clipInfo == null)
            return;
        initTimeline(clipInfo);
        initVideoFragment();
        int value = (int) Math.floor(clipInfo.getVolume() * VIDEOVOLUME_MAXSEEKBAR_VALUE / VIDEOVOLUME_MAXVOLUMEVALUE + 0.5D);
        updateVolumeSeekBarValue(value);
        initCurveView();
        initMultiSequence();
        if (timelineTimeSpan != null) {
            timelineTimeSpan.setVisibility(View.GONE);
        }
    }

    private void initTimeline(ClipInfo clipInfo) {
        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo, true);
        if (mTimeline == null)
            return;
        volumeKeyInfoMap = clipInfo.getVolumeKeyFrameInfoHashMap();
        if (volumeKeyInfoMap == null) {
            volumeKeyInfoMap = new TreeMap<>();
            clipInfo.setVolumeKeyFrameInfoHashMap(volumeKeyInfoMap);
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            videoClip = videoTrack.getClipByIndex(0);
            if (videoClip != null) {
                TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
            }
        }
    }

    @Override
    protected void initListener() {
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mClipFragment.stopEngine();
                } else {
                    mClipFragment.playVideo(mStreamingContext.getTimelineCurrentPosition(mTimeline), -1);
                }
            }
        });
        findViewById(R.id.iv_zoom_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimelineEditor.ZoomInSequence();
            }
        });
        findViewById(R.id.iv_zoom_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimelineEditor.ZoomOutSequence();
            }
        });
        addOrDeleteFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                if (TextUtils.equals(keyFrameState, KEY_FRAME_STATE_EDITING)) {
                    long keyFrameByTimeStamp = hasKeyFrameByTimeStamp(currentPosition);
                    if (keyFrameByTimeStamp >= 0) {
                        //删除关键帧 Delete key frame
                        deleteKeyFrameByTime(keyFrameByTimeStamp);
                    } else {
                        float volume = VIDEOVOLUME_MAXVOLUMEVALUE * mVolumeSeekBar.getProgress() / VIDEOVOLUME_MAXSEEKBAR_VALUE;
                        addNewKeyFrame(currentPosition, volume);
                        TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
                    }
                }
                updateVolumeRelativeView(currentPosition);
            }
        });
        lastFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转上一帧 Jump to previous frame
                jumpPreOrNextKeyFrame(true);
            }
        });
        nextFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转下一帧 Jump to the next frame
                jumpPreOrNextKeyFrame(false);
            }
        });
        mTvKeyFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(keyFrameState, KEY_FRAME_STATE_NOR)) {
                    //展示编辑关键帧 Show the edit keyframe
                    keyFrameState = KEY_FRAME_STATE_EDITING;
                    updateKeyFrameState();
                    updateVolumeRelativeView(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                } else if (TextUtils.equals(keyFrameState, KEY_FRAME_STATE_EDITING)) {
                    //曲线变速调节 Curve variable speed regulation
                    keyFrameState = KEY_FRAME_STATE_CURVE;
                    updateKeyFrameState();
                    curveLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        mVolumeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishVolumeFun();
            }
        });
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (TextUtils.equals(keyFrameState, KEY_FRAME_STATE_EDITING)) {
                        float volumeGain = VIDEOVOLUME_MAXVOLUMEVALUE * progress / VIDEOVOLUME_MAXSEEKBAR_VALUE;
                        if (currentKeyFrameTime >= 0) {
                            VolumeKeyInfo volumeKeyInfo = volumeKeyInfoMap.get(currentKeyFrameTime);
                            if (volumeKeyInfo != null) {
                                volumeKeyInfo.setVolumeValue(volumeGain);
                                TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
                            }
                        } else {
                            long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                            addNewKeyFrame(timelineCurrentPosition, volumeGain);
                            updateVolumeRelativeView(timelineCurrentPosition);
                        }
                    } else {
                        updateClipVolume(progress);
                    }
                    updateVolumeSeekBarValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                updateVolumeRelativeView(timeStamp);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTimelineEditor.setTimelineScaleForSeek(mTimeline, mTimelineEditor.getDurationWidth());
                        mTimelineEditor.unSelectAllTimeSpan();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mTimeline != null) {
                            mClipFragment.seekTimeline(NvsStreamingContext.getInstance().getTimelineCurrentPosition(mTimeline), 0);
                        }
                        if (llKeyFrame.getVisibility() == View.VISIBLE) {
                            updateVolumeRelativeView(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                        }
                        return true;
                }
                mCanSeekTimeline = true;
                return false;
            }
        });

        mBezierAdjustView.setTouchPointCallback(new BezierAdjustView.OnTouchPointCallback() {
            @Override
            public void onPointTouched(PointF pointF, boolean isFront) {
                Log.w(TAG, "onPointTouched pointF.x" + pointF.x + " pointF.y" + pointF.y + " isFront:" + isFront);
                if (currentKeyFrameTime >= 0 || mBeforeKeyFrameInfo == null || mNextKeyFrameInfo == null) {
                    return;
                }
                long beforeTimeStamp = mBeforeKeyFrameInfo.getKeyTime();
                long nextTimeStamp = mNextKeyFrameInfo.getKeyTime();
                updateCaptionControlPoint(pointF, isFront, beforeTimeStamp, nextTimeStamp);
                if (mCustomBezierRectF != null) {
                    if (isFront) {
                        mCustomBezierRectF.left = pointF.x;
                        mCustomBezierRectF.bottom = pointF.y;
                    } else {
                        mCustomBezierRectF.right = pointF.x;
                        mCustomBezierRectF.top = pointF.y;
                    }
                }
            }
        });
    }

    /**
     * 跳转上一帧或者下一帧
     * Jump to the previous frame or the next frame
     *
     * @param preFlag 上一帧标记 The previous frame tag
     */
    private void jumpPreOrNextKeyFrame(boolean preFlag) {
        if (volumeKeyInfoMap == null || volumeKeyInfoMap.size() == 0) return;
        long preTime = -TimeBase, nextTime = -TimeBase;
        Set<Map.Entry<Long, VolumeKeyInfo>> entries = volumeKeyInfoMap.entrySet();
        long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        for (Map.Entry<Long, VolumeKeyInfo> entry : entries) {
            Long key = entry.getKey();
            if (timelineCurrentPosition > (key + TimeBase)) {
                preTime = key;
            }
            if (nextTime < 0) {
                if (timelineCurrentPosition < (key - TimeBase)) {
                    nextTime = key;
                }
            }
        }
        if (preFlag) {
            currentKeyFrameTime = preTime;
        } else {
            currentKeyFrameTime = nextTime;
        }
        if (currentKeyFrameTime >= 0) {
            updateVolumeRelativeView(currentKeyFrameTime);
            seekMultiThumbnailSequenceView(currentKeyFrameTime);
        }
        changeAddOrDeleteView(false);
    }

    @Override
    public void onClick(View v) {

    }

    private void initMultiSequence() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
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
            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescsArray.add(sequenceDescs);
        }

        long duration = mTimeline.getDuration();
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayBtnLayout.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        timelineTimeSpan = mTimelineEditor.addTimeSpan(0, mTimeline.getDuration());
        timelineTimeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
        mTimelineEditor.selectTimeSpan(timelineTimeSpan);
        mTimelineEditor.unSelectAllTimeSpan();
    }

    /**
     * 初始化贝塞尔调节视图
     * Initialize the Bezier adjustment view
     */
    private void initCurveView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        rvCurveAdjust.setLayoutManager(gridLayoutManager);
        mAdapter = new CurveAdjustViewAdapter(this);
        rvCurveAdjust.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                //当前选中关键帧不能存在,否则不可以设置曲线
                //The selected key frame cannot exist,otherwise, the curve cannot be set
                if (currentKeyFrameTime >= 0) {
                    return;
                }
                CurveAdjustData curveAdjustData = mCurveAdjustDataList.get(pos);
                Log.d(TAG, "beforeTime:" + (mBeforeKeyFrameInfo != null ? (mBeforeKeyFrameInfo.getKeyTime() + " value:" + mBeforeKeyFrameInfo.getVolumeValue()) : "")
                        + " afterTime:" + (mNextKeyFrameInfo != null ? (mNextKeyFrameInfo.getKeyTime() + " value:" + mNextKeyFrameInfo.getVolumeValue()) : ""));
                Log.d(TAG, "onItemClick custom?" + curveAdjustData.isCustom() + " prePointX:" + curveAdjustData.getFrontControlPointF().x
                        + " y:" + curveAdjustData.getFrontControlPointF().y + " nextPointX:" + curveAdjustData.getBackControlPointF().x
                        + " y:" + curveAdjustData.getBackControlPointF().y);
                if (curveAdjustData.isCustom()) {
                    if (mBeforeKeyFrameInfo != null) {
                        mCustomBezierRectF = mBeforeKeyFrameInfo.getCustomABezierRectF();
                    }
                    if (mCustomBezierRectF == null) {
                        PointF leftPointF = curveAdjustData.getFrontControlPointF();
                        PointF rightPointF = curveAdjustData.getBackControlPointF();
                        mCustomBezierRectF = new RectF(leftPointF.x, rightPointF.y, rightPointF.x, leftPointF.y);
                        mBezierAdjustView.updateControlPoint(curveAdjustData.getFrontControlPointF(), curveAdjustData.getBackControlPointF());
                        mBeforeKeyFrameInfo.setCustomABezierRectF(mCustomBezierRectF);
                    } else {
                        mBezierAdjustView.updateControlPoint(new PointF(checkBezierFloatValue(mCustomBezierRectF.left), checkBezierFloatValue(mCustomBezierRectF.bottom)),
                                new PointF(checkBezierFloatValue(mCustomBezierRectF.right), checkBezierFloatValue(mCustomBezierRectF.top)));
                    }
                    curveAdjustLayout.setVisibility(View.VISIBLE);
                } else {
                    long beforeTimeStamp = mBeforeKeyFrameInfo.getKeyTime();
                    long nextTimeStamp = mNextKeyFrameInfo.getKeyTime();
                    updateCaptionControlPoint(curveAdjustData.getFrontControlPointF(), true, beforeTimeStamp, nextTimeStamp);
                    updateCaptionControlPoint(curveAdjustData.getBackControlPointF(), false, beforeTimeStamp, nextTimeStamp);
                    playVideo(beforeTimeStamp, nextTimeStamp);
                }
//                mBeforeKeyMapInfo.getValue().setSelectForwardBezierPos(pos);
//                mNextKeyMapInfo.getValue().setSelectBackwardBezierPos(pos);
                mAdapter.setSelectedPosition(pos);
            }
        });

        String jsonPath = "curve_speed/curve_adjust.json";
        String jsonText = ParseJsonFile.readAssetJsonFile(this, jsonPath);
        if (TextUtils.isEmpty(jsonText)) {
            return;
        }
        mCurveAdjustDataList = ParseJsonFile.fromJson(jsonText, new TypeToken<List<CurveAdjustData>>() {
        }.getType());
        if (mCurveAdjustDataList != null) {
            mAdapter.updateData(mCurveAdjustDataList);
        }
    }

    /**
     * 使贝塞尔控制点数据取值在[0, 1]
     * Make the value of Bezier control point data at [0, 1]
     *
     * @param value
     * @return
     */
    private float checkBezierFloatValue(float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }

    /**
     * 调整字幕贝塞尔控制点数据
     * Adjust caption Bezier control point data
     *
     * @param pointF
     * @param isFront
     * @param beforeTimeStamp
     * @param nextTimeStamp
     */
    private void updateCaptionControlPoint(PointF pointF, boolean isFront, long beforeTimeStamp, long nextTimeStamp) {
        PointF beforePointF = new PointF(mBeforeKeyFrameInfo.getKeyTime(), mBeforeKeyFrameInfo.getVolumeValue());
        PointF nextPointF = new PointF(mNextKeyFrameInfo.getKeyTime(), mNextKeyFrameInfo.getVolumeValue());
        if (isFront) {
            long forwardControlPointX = (long) ((nextTimeStamp - beforeTimeStamp) * pointF.x + beforeTimeStamp);
            float forwardControlVolume = (nextPointF.y - beforePointF.y) * pointF.y + beforePointF.y;
            Log.d(TAG, "updateCaptionControlPoint pre ControlX:" + forwardControlPointX + " y:" + forwardControlVolume);
            mBeforeKeyFrameInfo.setNextControlPoint(forwardControlPointX);
            mBeforeKeyFrameInfo.setNextVolumeValue(forwardControlVolume);
        } else {
            long backwardControlPointX = (long) ((nextTimeStamp - beforeTimeStamp) * pointF.x + beforeTimeStamp);
            float backwardControlVolume = (nextPointF.y - beforePointF.y) * pointF.y + beforePointF.y;
            Log.d(TAG, "updateCaptionControlPoint next ControlX:" + backwardControlPointX + " y:" + backwardControlVolume);
            mNextKeyFrameInfo.setPreControlPoint(backwardControlPointX);
            mNextKeyFrameInfo.setPreVolumeValue(backwardControlVolume);
        }
        TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
    }

    /**
     * 更新时间线指定位置上的字幕数据和相关视图
     * Updates the caption data and associated views at the specified timestamp on the timeline
     *
     * @param timeStamp
     */
    private void updateVolumeRelativeView(long timeStamp) {
        if (timeStamp < 0) {
            return;
        }
        int volumeProgress = 0;
        if (keyMap.size() != volumeKeyInfoMap.size()) {
            keyMap.clear();
            Set<Map.Entry<Long, VolumeKeyInfo>> entries = volumeKeyInfoMap.entrySet();
            for (Map.Entry<Long, VolumeKeyInfo> entry : entries) {
                keyMap.put(entry.getKey(), new KeyFrameInfo());
            }
        }
        timelineTimeSpan.setCurrentTimelinePosition(timeStamp, keyMap);
        //如果是关键帧编辑状态 是否当前位置有关键帧
        //If it is a keyframe edit state whether the current position has a keyframe
        if (!TextUtils.equals(keyFrameState, KEY_FRAME_STATE_NOR)) {
            currentKeyFrameTime = hasKeyFrameByTimeStamp(timeStamp);
            boolean hasPre = hasPreOrNextKeyFrameByTimeStamp(timeStamp, true);
            boolean hasNext = hasPreOrNextKeyFrameByTimeStamp(timeStamp, false);
//            if (hasNext && hasPre) {
            double volume = TimelineUtil.getVolumeByKeyTime(videoClip, timeStamp);
            volumeProgress = (int) (volume * VIDEOVOLUME_MAXSEEKBAR_VALUE / VIDEOVOLUME_MAXVOLUMEVALUE);
//            } else {
//                                volumeProgress = (int) (mClipArrayList.get(mCurClipIndex).getVolume() * VIDEOVOLUME_MAXSEEKBAR_VALUE / VIDEOVOLUME_MAXVOLUMEVALUE);
//                if (currentKeyFrameTime >= 0) {
//                    VolumeKeyInfo volumeKeyInfo = volumeKeyInfoMap.get(currentKeyFrameTime);
//                    if (volumeKeyInfo != null) {
//                        volumeProgress = (int) (volumeKeyInfo.getVolumeValue() * VIDEOVOLUME_MAXSEEKBAR_VALUE / VIDEOVOLUME_MAXVOLUMEVALUE);
//                    }
//                }
//            }
            Log.d(TAG, "updateCaptionRelativeView volumeProgress:" + volumeProgress + " pre:" + hasPre + " next:" + hasNext);
            lastFrame.setEnabled(hasPre);
            nextFrame.setEnabled(hasNext);
            if (currentKeyFrameTime >= 0) {
                //有关键帧 keyframe
                addOrDeleteFrame.setText(getResources().getString(R.string.key_frame_delete_frame_text));
                mTvKeyFrame.setEnabled(false);
                mTvKeyFrame.setTextColor(getResources().getColor(R.color.white_20));
                mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.icon_curve_adjust_disable), null, null, null);
            } else {
                //无关键帧 keyframeless
                if (hasNext && hasPre) {
                    mTvKeyFrame.setEnabled(true);
                    mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.icon_curve_adjust), null, null, null);
                    mTvKeyFrame.setTextColor(getResources().getColor(R.color.white));
                    long preTime = searchPreOrNexKeyFrame(timeStamp, true);
                    long nextTime = searchPreOrNexKeyFrame(timeStamp, false);
                    mBeforeKeyFrameInfo = volumeKeyInfoMap.get(preTime);
                    mNextKeyFrameInfo = volumeKeyInfoMap.get(nextTime);
                } else {
                    mTvKeyFrame.setEnabled(false);
                    mTvKeyFrame.setTextColor(getResources().getColor(R.color.white_20));
                    mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.icon_curve_adjust_disable), null, null, null);
                }
                addOrDeleteFrame.setText(getResources().getString(R.string.key_frame_add_frame_text));
            }

        } else {
            double volume = TimelineUtil.getVolumeByKeyTime(videoClip, timeStamp);
            volumeProgress = (int) (volume * VIDEOVOLUME_MAXSEEKBAR_VALUE / VIDEOVOLUME_MAXVOLUMEVALUE);
            mTvKeyFrame.setEnabled(true);
            llKeyFrame.setVisibility(View.GONE);
        }

        changeAddOrDeleteView((currentKeyFrameTime == -1));
        updateVolumeSeekBarValue(volumeProgress);
        mClipFragment.updateCurPlayTime(timeStamp);
        tvCurTime.setText(TimeFormatUtil.formatUsToString2(timeStamp) + "/" + TimeFormatUtil.formatUsToString2(mTimeline.getDuration()));
        if (!mCanSeekTimeline) {
            return;
        }
        if (mTimeline != null) {
            mClipFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
        }
    }

    /**
     * 查找上一帧或者下一帧的时间戳
     * Finds the timestamp of the previous frame or the next frame
     *
     * @param timeStamp
     * @param preFlag
     * @return
     */
    private long searchPreOrNexKeyFrame(long timeStamp, boolean preFlag) {
        if (volumeKeyInfoMap == null || volumeKeyInfoMap.size() == 0) return -1;
        long preTime = -TimeBase, nextTime = -TimeBase;
        Set<Map.Entry<Long, VolumeKeyInfo>> entries = volumeKeyInfoMap.entrySet();
        for (Map.Entry<Long, VolumeKeyInfo> entry : entries) {
            Long key = entry.getKey();
            if (timeStamp > (key + TimeBase)) {
                preTime = key;
            }
            if (nextTime < 0) {
                if (timeStamp < (key - TimeBase)) {
                    nextTime = key;
                }
            }
        }
        return preFlag ? preTime : nextTime;
    }

    /**
     * 是否有上一帧下一帧
     * Whether there is a frame before the frame after the frame
     *
     * @param timeStamp
     * @param preFlag   返回是否有上一帧 false 返回是否有下一帧 false Returns whether there is a previous frame. False Returns whether there is a next frame
     * @return
     */
    private boolean hasPreOrNextKeyFrameByTimeStamp(long timeStamp, boolean preFlag) {
        boolean hasPre = searchPreOrNexKeyFrame(timeStamp, true) >= 0;
        boolean hasNext = searchPreOrNexKeyFrame(timeStamp, false) > 0;
        return preFlag ? hasPre : hasNext;
    }

    /**
     * 指定时间戳位置是否有关键帧 有则返回关键帧时长 无则返回-1
     * If there are keyframes in the specified timestamp location, the keyframe duration is returned. If yes, the keyframe duration is returned. If no, -1 is returned
     *
     * @param timeStamp
     * @return
     */
    private long hasKeyFrameByTimeStamp(long timeStamp) {
        if (volumeKeyInfoMap != null && timeStamp >= 0) {
            Set<Map.Entry<Long, VolumeKeyInfo>> entries = volumeKeyInfoMap.entrySet();
            for (Map.Entry<Long, VolumeKeyInfo> entry : entries) {
                Long key = entry.getKey();
                if (Math.abs(key - timeStamp) < TimeBase) {
                    return key;
                }
            }
        }
        return -1;
    }

    private void finishVolumeFun() {
        if (TextUtils.equals(keyFrameState, KEY_FRAME_STATE_CURVE)) {
            if (curveLayout.getVisibility() == View.VISIBLE) {
                if (curveAdjustLayout.getVisibility() == View.VISIBLE) {
                    if (mCustomBezierRectF == null || mBeforeKeyFrameInfo == null || mNextKeyFrameInfo == null) {
                        return;
                    }
                    long beforeTimeStamp = mBeforeKeyFrameInfo.getKeyTime();
                    long nextTimeStamp = mNextKeyFrameInfo.getKeyTime();
                    updateCaptionControlPoint(new PointF(mCustomBezierRectF.left, mCustomBezierRectF.bottom), true, beforeTimeStamp, nextTimeStamp);
                    updateCaptionControlPoint(new PointF(mCustomBezierRectF.right, mCustomBezierRectF.top), false, beforeTimeStamp, nextTimeStamp);
                    playVideo(beforeTimeStamp, nextTimeStamp);
                    curveAdjustLayout.setVisibility(View.GONE);
                } else {
                    keyFrameState = KEY_FRAME_STATE_EDITING;
                    curveLayout.setVisibility(View.GONE);
                    TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
                }
            }
        } else if (TextUtils.equals(keyFrameState, KEY_FRAME_STATE_EDITING)) {
            keyFrameState = KEY_FRAME_STATE_NOR;
        } else {
            BackupData.instance().setClipInfoData(mClipArrayList);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            AppManager.getInstance().finishActivity();
        }
        updateKeyFrameState();
    }

    private void updateVolumeSeekBarValue(int volumeValue) {
        mVolumeSeekBar.setProgress(volumeValue);
        mVolumeSeekBarValue.setText(String.valueOf(volumeValue));
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private void updateClipVolume(int volume) {
        float volumeGain = VIDEOVOLUME_MAXVOLUMEVALUE * volume / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mClipArrayList.get(mCurClipIndex).setVolume(volumeGain);
        if (videoClip == null) return;
        if (currentKeyFrameTime < 0) {
            videoClip.setVolumeGain(volumeGain, volumeGain);
            /*
             * 存储数据
             * Storing data
             * */
            mClipArrayList.get(mCurClipIndex).setVolume(volumeGain);
        } else {
            VolumeKeyInfo volumeKeyInfo = volumeKeyInfoMap.get(currentKeyFrameTime);
            if (volumeKeyInfo != null) {
                volumeKeyInfo.setVolumeValue(volume);
                TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
            }
        }
        Log.d(TAG, "updateClipVolume volumeGain" + volumeGain);
    }

    private void initVideoFragment() {
        mClipFragment = new SingleClipFragment();
        mClipFragment.setFragmentLoadFinisedListener(new SingleClipFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        });
        mClipFragment.setVideoFragmentCallBack(new SingleClipFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                resetView();
//                playVideo(startPlayTime, endPlayTime);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                Log.d(TAG, "playbackTimelinePosition stamp:" + stamp);
                updateVolumeRelativeView(stamp);
                seekMultiThumbnailSequenceView(stamp);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                    mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                    mCanSeekTimeline = false;
                } else {
                    mCanSeekTimeline = true;
                    mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                }
            }
        });
        mClipFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mClipFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mClipFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mClipFragment);
    }

    private void seekMultiThumbnailSequenceView(long timeStamp) {
        if (mMultiSequenceView != null) {
            long duration = mTimeline.getDuration();
            mMultiSequenceView.scrollTo(Math.round(((float) timeStamp) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }

    /**
     * 播放结束，将视图重置到播放起始点
     * When the playback ends, resets the view to the start of the playback
     */
    private void resetView() {
        long timeStamp = 0;
        if (curveLayout.getVisibility() == View.VISIBLE && mBeforeKeyFrameInfo != null) {
            timeStamp = mBeforeKeyFrameInfo.getKeyTime();
            int x = Math.round((timeStamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
            mMultiSequenceView.smoothScrollTo(x, 0);
        } else {
            mMultiSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        }
        int x = Math.round((timeStamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
        mMultiSequenceView.smoothScrollTo(x, 0);
        mClipFragment.seekTimeline(timeStamp, 0);
    }

    /**
     * 添加关键帧
     * Add keyframe
     * @param keyTime
     * @param volume
     * @return
     */
    private boolean addNewKeyFrame(long keyTime, float volume) {
        if (volumeKeyInfoMap == null) {
            Log.e(TAG, "volumeKeyInfoMap ==null!!!");
            return false;
        }
        Set<Map.Entry<Long, VolumeKeyInfo>> entries = volumeKeyInfoMap.entrySet();
        for (Map.Entry<Long, VolumeKeyInfo> entry : entries) {
            long entryTime = entry.getKey();
            if (Math.abs(entryTime - keyTime) < TimeBase) {
                return false;
            }
        }
        volumeKeyInfoMap.put(keyTime, new VolumeKeyInfo(keyTime, volume));
        currentKeyFrameTime = keyTime;
        TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
        mClipFragment.seekTimeline(keyTime, 0);
        changeAddOrDeleteView(false);
        return true;
    }

    /**
     * 根据时间点删除一个关键帧
     * Deletes a keyframe based on a point in time
     * @param keyTime
     * @return
     */
    private boolean deleteKeyFrameByTime(long keyTime) {
        if (volumeKeyInfoMap == null) {
            Log.e(TAG, "volumeKeyInfoMap ==null!!!");
            return false;
        }
        if (volumeKeyInfoMap.containsKey(keyTime)) {
            long preTime = searchPreOrNexKeyFrame(keyTime, true);
            long nextTime = searchPreOrNexKeyFrame(keyTime, false);
            //删除关键帧 删除相关的贝塞尔曲线数据
            //Delete keyframes Delete the associated Bessel curve data
            if (preTime > 0) {
                VolumeKeyInfo volumeKeyInfo = volumeKeyInfoMap.get(preTime);
                if (volumeKeyInfo != null) {
                    volumeKeyInfo.setPreControlPoint(-1);
                    volumeKeyInfo.setNextControlPoint(-1);
                }
            }
            if (nextTime > 0) {
                VolumeKeyInfo volumeKeyInfo = volumeKeyInfoMap.get(nextTime);
                if (volumeKeyInfo != null) {
                    volumeKeyInfo.setPreControlPoint(-1);
                    volumeKeyInfo.setNextControlPoint(-1);
                }
            }
            volumeKeyInfoMap.remove(keyTime);
            currentKeyFrameTime = -1;
            TimelineUtil.applyVolumeKeyInfo(videoClip, volumeKeyInfoMap);
            changeAddOrDeleteView(true);
            return true;
        }
        return false;
    }

    long startPlayTime = 0, endPlayTime = -1;

    public void playVideo(long startPlayTime, long endPlayTime) {
        this.startPlayTime = startPlayTime;
        this.endPlayTime = endPlayTime;
        mClipFragment.playVideo(startPlayTime, endPlayTime);
    }

    /**
     * 根据状态码更新UI
     * Update the UI according to the status code
     */
    private void updateKeyFrameState() {
        switch (keyFrameState) {
            case KEY_FRAME_STATE_NOR:
                llKeyFrame.setVisibility(View.GONE);
                mVolumeSeekBar.setVisibility(View.GONE);
                mTvKeyFrame.setTextColor(getResources().getColor(R.color.white));
                mTvKeyFrame.setEnabled(true);
                mTvKeyFrame.setText(getResources().getString(R.string.key_frame_text));
                mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.key_frame), null, null, null);
                if (timelineTimeSpan != null) {
                    timelineTimeSpan.setVisibility(View.GONE);
                }
                break;
            case KEY_FRAME_STATE_EDITING:
                mVolumeSeekBar.setVisibility(View.VISIBLE);
                timelineTimeSpan.setVisibility(View.VISIBLE);
                llKeyFrame.setVisibility(View.VISIBLE);
                mTvKeyFrame.setText(getResources().getString(R.string.curve_to_adjust));
                if (timelineTimeSpan != null) {
                    timelineTimeSpan.setVisibility(View.VISIBLE);
                }
                break;
            case KEY_FRAME_STATE_CURVE:
                mVolumeSeekBar.setVisibility(View.GONE);
                if (timelineTimeSpan != null) {
                    timelineTimeSpan.setVisibility(View.VISIBLE);
                }
                mTvKeyFrame.setEnabled(true);
                mTvKeyFrame.setTextColor(getResources().getColor(R.color.white));
                mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.icon_curve_adjust), null, null, null);
                break;
            default:
        }
    }

    /**
     * 改变关键帧模式中添加或者删除关键帧的显示
     * Change the display of added or deleted keyframes in keyframe mode
     */
    private void changeAddOrDeleteView(boolean add) {
        if (add) {
            addOrDeleteFrame.setText(R.string.key_frame_add_frame_text);
            addOrDeleteFrame.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_add_frame_selector), null, null);
        } else {
            addOrDeleteFrame.setText(R.string.key_frame_delete_frame_text);
            addOrDeleteFrame.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_delete_frame_selector), null, null);
        }
    }

}
