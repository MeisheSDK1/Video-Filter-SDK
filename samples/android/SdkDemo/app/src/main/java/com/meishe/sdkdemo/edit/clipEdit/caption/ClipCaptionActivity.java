package com.meishe.sdkdemo.edit.clipEdit.caption;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsClipCaption;
import com.meicam.sdk.NvsControlPointPair;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsPointD;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.adapter.CurveAdjustViewAdapter;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.CurveAdjustData;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineTimeSpan;
import com.meishe.sdkdemo.edit.view.BezierAdjustView;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.InputDialog;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.OnSeekBarChangeListenerAbs;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.meishe.sdkdemo.utils.Constants.NS_TIME_BASE;
import static com.meishe.sdkdemo.utils.Constants.ROTATION_Z;
import static com.meishe.sdkdemo.utils.Constants.SCALE_X;
import static com.meishe.sdkdemo.utils.Constants.SCALE_Y;
import static com.meishe.sdkdemo.utils.Constants.TRACK_OPACITY;
import static com.meishe.sdkdemo.utils.Constants.TRANS_X;
import static com.meishe.sdkdemo.utils.Constants.TRANS_Y;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-字幕-Activity
 * @Description :VideoEdit-Caption-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ClipCaptionActivity extends BaseActivity {
    private static final String TAG = "ClipCaptionActivity";
    private static final int VIDEOPLAYTOEOF = 105;
    private static final int REQUESTCAPTIONSTYLE = 103;
    private CustomTitleBar mTitleBar;
    private TextView mPlayCurTime;
    private ImageView mIvZoomIn, mIvZoomOut;
    private ImageView iv_confirm_curve, iv_confirm_curve_adjust;
    private RecyclerView rv_curve;
    private TextView mTvKeyFrame;
    private Button mCaptionStyleButton;
    private NvsTimelineEditor mTimelineEditor;
    private Button mPlayBtn;
    private LinearLayout mLlKeyFrame;
    private TextView mTvLastFrame, mTvAddDeleteFrame, mTvNextFrame;
    private LinearLayout mLlAddTraditional;
    private LinearLayout mLlAddPieced;
    private ImageView mOkBtn;
    private VideoFragment mVideoFragment;
    private RelativeLayout mBottomRelativeLayout;
    private RelativeLayout mPlayBtnLayout;
    private RelativeLayout mCurveAdjustLayout, mCurveListLayout;
    private BezierAdjustView mBezierAdjustView;
    private NvsMultiThumbnailSequenceView mMultiSequenceView;

    private NvsTimeline mTimeline;
    private boolean mIsSeekTimeline = true;
    private boolean mIsPlaying = false;
    private NvsClipCaption mCurCaption;
    private NvsStreamingContext mStreamingContext;
    private List<CaptionTimeSpanInfo> mTimeSpanInfoList = new ArrayList<>();
    private ClipCaptionActivity.CaptionHandler m_handler = new ClipCaptionActivity.CaptionHandler(this);
    private List<CaptionInfo> mCaptionDataListClone;
    private boolean mIsInnerDrawRect = false;
    private StringBuilder mShowCurrentDuration = new StringBuilder();
    private boolean isTraditionCaption;
    private boolean mAddKeyFrame;
    private CurveAdjustViewAdapter mAdapter;
    private List<CurveAdjustData> mCurveAdjustDataList;
    private RectF mCustomBezierRectF;

    private Map.Entry<Long, KeyFrameInfo> mBeforeKeyMapInfo;
    private Map.Entry<Long, KeyFrameInfo> mNextKeyMapInfo;
    private SeekBar alphaSeek;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex;
    private ClipInfo mClipInfo;
    private NvsVideoClip curVideoClip;
    private TextView seekBarOpacityWidthValue;
    private View mLlSeekContainer;

    static class CaptionHandler extends Handler {
        WeakReference<ClipCaptionActivity> mWeakReference;

        public CaptionHandler(ClipCaptionActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ClipCaptionActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case VIDEOPLAYTOEOF:
                        activity.resetView();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 播放结束，将视图重置到播放起始点
     * When the playback ends, resets the view to the start of the playback
     */
    private void resetView() {
        long timeStamp = 0;
        if (mCurveListLayout.getVisibility() == View.VISIBLE && mBeforeKeyMapInfo != null) {
            timeStamp = mBeforeKeyMapInfo.getKey();
            int x = Math.round((timeStamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
            mMultiSequenceView.smoothScrollTo(x, 0);
        } else {
            mMultiSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        }
        updatePlaytimeText(timeStamp);
        seekTimeline(timeStamp);
    }

    private void playbackTimeline(long start, long end) {
        mVideoFragment.playVideo(start, end);
    }

    private void seekTimeline(long timeStamp, int flag) {
        mVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER | flag);
    }

    private void seekTimeline(long timeStamp) {
        mVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    /**
     * 选择字幕并渲染相关视图
     * Select the caption and render related view
     */
    private void selectCaptionAndTimeSpan() {
        selectCaption();
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        mVideoFragment.changeCaptionRectVisible();

        if (mCurCaption != null) {
            int alignVal = mCurCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
        }

        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }


    private void handlePlayStop() {
        selectCaption();
        if (mCurCaption != null) {
            int alignVal = mCurCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
        }
        if (mCurveListLayout.getVisibility() != View.VISIBLE) {
            changeRectVisible();
        }
        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }

    @Override
    protected int initRootView() {
        return R.layout.activity_caption;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mPlayCurTime = (TextView) findViewById(R.id.play_cur_time);
        mIvZoomIn = findViewById(R.id.iv_zoom_in);
        mIvZoomOut = findViewById(R.id.iv_zoom_out);
        mTvKeyFrame = findViewById(R.id.tv_key_frame);
        mCaptionStyleButton = (Button) findViewById(R.id.captionStyleButton);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.caption_timeline_editor);
        mPlayBtn = (Button) findViewById(R.id.play_btn);
        mLlKeyFrame = findViewById(R.id.ll_key_frame);
        mTvLastFrame = findViewById(R.id.tv_last_frame);
        mTvAddDeleteFrame = findViewById(R.id.tv_add_or_delete_frame);
        mTvNextFrame = findViewById(R.id.tv_next_frame);
        mLlAddTraditional = findViewById(R.id.ll_traditional_caption);
        mLlAddPieced = findViewById(R.id.ll_pieced_caption);
        mOkBtn = findViewById(R.id.iv_ok);
        mBottomRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mPlayBtnLayout = (RelativeLayout) findViewById(R.id.play_btn_layout);
        mCurveListLayout = findViewById(R.id.curve_list_layout);
        mCurveAdjustLayout = findViewById(R.id.curve_adjust_layout);
        rv_curve = findViewById(R.id.rv_curve_adjust);
        mBezierAdjustView = findViewById(R.id.bezier_adjust_view);
        iv_confirm_curve_adjust = findViewById(R.id.iv_confirm_curve_adjust);
        iv_confirm_curve = findViewById(R.id.iv_confirm_curve);
        alphaSeek = findViewById(R.id.seek_alpha);
        mLlSeekContainer = findViewById(R.id.ll_seek_1);
        seekBarOpacityWidthValue = findViewById(R.id.seekBarOpacityWidthValue);
        alphaSeek.setMax(100);
        mMultiSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();
        initCurveView();
    }

    /**
     * 初始化贝塞尔调节视图
     * Initialize the Bezier adjustment view
     */
    private void initCurveView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        rv_curve.setLayoutManager(gridLayoutManager);
        mAdapter = new CurveAdjustViewAdapter(this);
        rv_curve.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mCurCaption == null || mBeforeKeyMapInfo == null || mNextKeyMapInfo == null) {
                    return;
                }
                CurveAdjustData curveAdjustData = mCurveAdjustDataList.get(pos);
                if (curveAdjustData.isCustom()) {
                    if (mCustomBezierRectF == null) {
                        PointF leftPointF = curveAdjustData.getFrontControlPointF();
                        PointF rightPointF = curveAdjustData.getBackControlPointF();
                        mCustomBezierRectF = new RectF(leftPointF.x, rightPointF.y, rightPointF.x, leftPointF.y);
                        mBezierAdjustView.updateControlPoint(curveAdjustData.getFrontControlPointF(), curveAdjustData.getBackControlPointF());
                    } else {
                        mBezierAdjustView.updateControlPoint(new PointF(checkBezierFloatValue(mCustomBezierRectF.left), checkBezierFloatValue(mCustomBezierRectF.bottom)),
                                new PointF(checkBezierFloatValue(mCustomBezierRectF.right), checkBezierFloatValue(mCustomBezierRectF.top)));
                    }
                    mCurveAdjustLayout.setVisibility(View.VISIBLE);
                } else {
                    long beforeTimeStamp = mBeforeKeyMapInfo.getKey();
                    long nextTimeStamp = mNextKeyMapInfo.getKey();
                    updateCaptionControlPoint(curveAdjustData.getFrontControlPointF(), true, beforeTimeStamp, nextTimeStamp);
                    updateCaptionControlPoint(curveAdjustData.getBackControlPointF(), false, beforeTimeStamp, nextTimeStamp);
                    playbackTimeline(beforeTimeStamp, nextTimeStamp);
                }
                mBeforeKeyMapInfo.getValue().setSelectForwardBezierPos(pos);
                mNextKeyMapInfo.getValue().setSelectBackwardBezierPos(pos);
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
        if (mCurveAdjustDataList == null) {
            return;
        }
        mAdapter.updateData(mCurveAdjustDataList);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.caption);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mStreamingContext = NvsStreamingContext.getInstance();
        mStreamingContext.setDefaultCaptionFade(false);
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size()) {
            return;
        }
        mClipInfo = mClipArrayList.get(mCurClipIndex);
        mTimeline = TimelineUtil.createSingleClipTimeline(mClipInfo, true);
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            curVideoClip = videoTrack.getClipByIndex(0);
        }
        if (curVideoClip == null) {
            return;
        }
        mCaptionDataListClone = mClipInfo.getCaptionInfoList();
        if (mCaptionDataListClone == null) {
            mCaptionDataListClone = new ArrayList<>();
            mClipInfo.setCaptionInfoList(mCaptionDataListClone);
        }
        TimelineUtil.applyClipCaption(curVideoClip, mCaptionDataListClone, 0);
        initVideoFragment();
        updatePlaytimeText(0);
        initMultiSequence();
        addAllTimeSpan();
        selectCaption();
        selectTimeSpan();
    }

    private void showOrHideCaptionAlpha() {
//        if (mCurCaption != null) {
//            alphaSeek.setProgress((int) (alphaSeek.getMax() * mCurCaption.getOpacity()));
//        }
        alphaSeek.setVisibility(mCurCaption == null ? View.GONE : View.VISIBLE);
        mLlSeekContainer.setVisibility(mCurCaption == null ? View.GONE : View.VISIBLE);
        if (alphaSeek.getVisibility() == View.VISIBLE) {
            seekBarOpacityWidthValue.setText(alphaSeek.getProgress() + "");
        }
    }

    @Override
    protected void initListener() {
        alphaSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b) {
                    return;
                }
                if (mCurCaption != null) {
                    seekBarOpacityWidthValue.setText(i + "");
                    float opacity = i * 1f / alphaSeek.getMax();
                    CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                    if (currentCaptionInfo != null) {
                        currentCaptionInfo.setOpacity(opacity);
                    }
                    mCurCaption.setOpacity(opacity);

                    if (mAddKeyFrame) {
                        updateOrAddKeyFrameInfo();
                        long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                        mCurCaption.setFloatValAtTime(TRACK_OPACITY, opacity, duration);
                    }

                    if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    }

                }
            }
        });
        mIvZoomIn.setOnClickListener(this);
        mIvZoomOut.setOnClickListener(this);
        mTvKeyFrame.setOnClickListener(this);
        mCaptionStyleButton.setOnClickListener(this);
        mTvLastFrame.setOnClickListener(this);
        mTvAddDeleteFrame.setOnClickListener(this);
        mTvNextFrame.setOnClickListener(this);
        mLlAddTraditional.setOnClickListener(this);
        mLlAddPieced.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        iv_confirm_curve.setOnClickListener(this);
        iv_confirm_curve_adjust.setOnClickListener(this);
        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                updateCaptionRelativeView(timeStamp);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mVideoFragment.setDrawRectVisible(View.GONE);
                        mTimelineEditor.unSelectAllTimeSpan();
                        selectCaption();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        seekTimeline(NvsStreamingContext.getInstance().getTimelineCurrentPosition(mTimeline));
                        handlePlayStop();
                        if (mLlKeyFrame.getVisibility() == View.VISIBLE) {
                            updateCaptionRelativeView(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                        }
                        return true;
                }
                mIsSeekTimeline = true;
                return false;
            }
        });

        if (mVideoFragment != null) {
            mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
                @Override
                public void playBackEOF(NvsTimeline timeline) {
                    m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
                }

                @Override
                public void playStopped(NvsTimeline timeline) {
                    handlePlayStop();
                }

                @Override
                public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                    updatePlaytimeText(stamp);
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    mTimelineEditor.unSelectAllTimeSpan();
                    selectCaption();
                    if (mMultiSequenceView != null) {
                        int x = Math.round((stamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
                        mMultiSequenceView.smoothScrollTo(x, 0);
                    }
                }

                @Override
                public void streamingEngineStateChanged(int state) {
                    if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                        mIsSeekTimeline = false;
                        mIsPlaying = true;
                        displayEnable(false);
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                    } else {
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                        mIsSeekTimeline = true;
                        mIsPlaying = false;
                        if (mCurCaption != null) {
                            displayEnable(true);
                            CaptionInfo captionInfo = getCurrentCaptionInfo();
                            if (captionInfo != null) {
                                Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                                updateKeyFrameView(mStreamingContext.getTimelineCurrentPosition(mTimeline), keyFrameInfo);
                            }
                        } else {
                            displayEnable(false);
                        }
                    }
                }
            });
            mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
                @Override
                public void onAssetDelete() {
                    if (mAddKeyFrame) {
                        ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_when_delete_caption));
                        return;
                    }
                    deleteCurCaptionTimeSpan();
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.remove(index);
                    }
                    curVideoClip.removeCaption(mCurCaption);
                    mCurCaption = null;
                    selectCaptionAndTimeSpan();//onAssetDelete
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    if (mCurCaption != null) {
                        alphaSeek.setProgress((int) (mCurCaption.getOpacity() * alphaSeek.getMax()));
                    }
                }

                @Override
                public void onAssetSelected(PointF curPoint) {
                    if (mAddKeyFrame) {
                        //关键帧模式 不切换选中的字幕
                        //The keyframe mode does not toggle the selected subtitles
                        return;
                    }
                    /*
                     * 判断若没有选中当前字幕框则选中，选中则不处理
                     * Judge if the current subtitle box is not selected, select it, do not process
                     * */
                    mIsInnerDrawRect = mVideoFragment.curPointIsInnerDrawRect((int) curPoint.x, (int) curPoint.y);
                    if (!mIsInnerDrawRect) {
                        mVideoFragment.selectCaptionByHandClick(curPoint);
                        NvsCaption curCaption = mVideoFragment.getCurCaption();
                        if (curCaption != null && curCaption instanceof NvsClipCaption) {
                            mCurCaption = (NvsClipCaption) curCaption;
                        } else {
                            mCurCaption = null;
                        }
                        selectTimeSpan();//onAssetSelected
                        if (mCurCaption != null) {
                            int alignVal = mCurCaption.getTextAlignment();
                            mVideoFragment.setAlignIndex(alignVal);
                        }
                    }
                }

                @Override
                public void onAssetTranslation() {
                    if (mCurCaption == null) {
                        return;
                    }
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                    }
                    if (mAddKeyFrame) {
                        updateOrAddKeyFrameInfo();
                    }
                }

                @Override
                public void onAssetScale() {
                    if (mCurCaption == null) {
                        return;
                    }
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedScaleRotationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setScaleFactorX(mCurCaption.getScaleX());
                        mCaptionDataListClone.get(index).setScaleFactorY(mCurCaption.getScaleY());
                        mCaptionDataListClone.get(index).setAnchor(mCurCaption.getAnchorPoint());
                        mCaptionDataListClone.get(index).setRotation(mCurCaption.getRotationZ());
//                        mCaptionDataListClone.get(index).setCaptionSize(mCurCaption.getFontSize());
                        PointF pointF = mCurCaption.getCaptionTranslation();
                        //Log.e(TAG,"pointF.x = " + pointF.x + "pointF.y =" + pointF.y);
                        mCaptionDataListClone.get(index).setTranslation(pointF);
                    }
                    if (mAddKeyFrame) {
                        updateOrAddKeyFrameInfo();
                    }
                }

                @Override
                public void onAssetAlign(int alignVal) {
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setAlignVal(alignVal);
                    }
                }

                @Override
                public void onOrientationChange(boolean isHorizontal) {
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOrientationType(isHorizontal ? CaptionInfo.O_HORIZONTAL : CaptionInfo.O_VERTICAL);
                    }
                }

                @Override
                public void onAssetHorizFlip(boolean isHorizFlip) {

                }
            });
            mVideoFragment.setCaptionTextEditListener(new VideoFragment.VideoCaptionTextEditListener() {
                @Override
                public void onCaptionTextEdit() {
                    if (!mIsInnerDrawRect) {
                        return;
                    }
                    InputDialog inputDialog = new InputDialog(ClipCaptionActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean ok) {
                            if (ok) {
                                InputDialog d = (InputDialog) dialog;
                                String userInputText = d.getUserInputText();
                                if (null != mCurCaption) {
                                    mCurCaption.setText(userInputText);
                                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                                    mVideoFragment.updateCaptionCoordinate(mCurCaption);
                                    mVideoFragment.changeCaptionRectVisible();
                                    int zVal = (int) mCurCaption.getZValue();
                                    int index = getCaptionIndex(zVal);
                                    if (index >= 0) {
                                        mCaptionDataListClone.get(index).setText(userInputText);
                                    }
                                }

                            }
                        }
                    });
                    if (mCurCaption != null) {
                        inputDialog.setUserInputText(mCurCaption.getText());
                    }
                    inputDialog.show();
                    mIsInnerDrawRect = false;
                }
            });
        }


        mVideoFragment.setBeforeAnimateStickerEditListener(new VideoFragment.IBeforeAnimateStickerEditListener() {
            @Override
            public boolean beforeTransitionCouldDo() {
                if (mCurCaption == null) {
                    return false;
                }
                if (!mAddKeyFrame) {
                    boolean b = ifCouldEditCaption();
                    if (!b) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            }

            @Override
            public boolean beforeScaleCouldDo() {
                if (mCurCaption == null) {
                    return false;
                }
                if (!mAddKeyFrame) {
                    boolean b = ifCouldEditCaption();
                    if (!b) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            }
        });

        mBezierAdjustView.setTouchPointCallback(new BezierAdjustView.OnTouchPointCallback() {
            @Override
            public void onPointTouched(PointF pointF, boolean isFront) {
                if (mCurCaption == null || mBeforeKeyMapInfo == null || mNextKeyMapInfo == null) {
                    return;
                }
                long beforeTimeStamp = mBeforeKeyMapInfo.getKey();
                long nextTimeStamp = mNextKeyMapInfo.getKey();
                updateCaptionControlPoint(pointF, isFront, beforeTimeStamp, nextTimeStamp);
                playbackTimeline(beforeTimeStamp, nextTimeStamp);
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
     * 将SDK视图数据换算成贝塞尔控制点数据
     * Convert SDK view data to Bezier control point data
     *
     * @param beforeTimeStamp
     * @param nextTimeStamp
     * @param beforeKeyFrameInfo
     * @param nextKeyFrameInfo
     * @return
     */
    private RectF mapSdkData2BezierRectF(long beforeTimeStamp, long nextTimeStamp, KeyFrameInfo beforeKeyFrameInfo, KeyFrameInfo nextKeyFrameInfo) {
        RectF rectF = new RectF();
        long timeStampDim = nextTimeStamp - beforeTimeStamp;
        rectF.left = (float) ((beforeKeyFrameInfo.getForwardControlPointX() - beforeTimeStamp) * 1.0F / timeStampDim);
        rectF.right = (float) ((nextKeyFrameInfo.getBackwardControlPointX() - beforeTimeStamp) * 1.0F / timeStampDim);

        PointF beforePointF = beforeKeyFrameInfo.getTranslation();
        PointF nextPointF = nextKeyFrameInfo.getTranslation();

        if (!beforePointF.equals(nextPointF)) {
            float tranX = nextPointF.x - beforePointF.x;
            float tranY = nextPointF.y - beforePointF.y;
            if (tranX != 0) {
                rectF.bottom = (float) (beforeKeyFrameInfo.getForwardControlPointYForTransX() - beforePointF.x) / tranX;
                rectF.top = (float) (nextKeyFrameInfo.getBackwardControlPointYForTransX() - beforePointF.x) / tranX;
            } else if (tranY != 0) {
                rectF.bottom = (float) (beforeKeyFrameInfo.getForwardControlPointYForTransY() - beforePointF.y) / tranY;
                rectF.top = (float) (beforeKeyFrameInfo.getBackwardControlPointYForTransY() - beforePointF.y) / tranY;
            }
        }
        return rectF;
    }

    /**
     * 获取当前选中贝塞尔曲线调节类别的索引
     * Gets the index of the currently selected Bezier curve adjustment type
     *
     * @return
     */
    private int getSelectedCurveListIndex() {
        mCustomBezierRectF = null;
        if (mCurCaption == null || mBeforeKeyMapInfo == null || mNextKeyMapInfo == null || mCurveAdjustDataList == null) {
            return 0;
        }
        KeyFrameInfo beforeKeyFrameInfo = mBeforeKeyMapInfo.getValue();
        KeyFrameInfo nextKeyFrameInfo = mNextKeyMapInfo.getValue();
        /**
         * 贝塞尔调整数据已变成无效（例如经历添加或删除关键帧）
         * Bezier adjustment data has become invalid (such as experiencing adding or removing keyframes)
         */
        if (beforeKeyFrameInfo.getForwardControlPointX() == -1 || nextKeyFrameInfo.getBackwardControlPointX() == -1) {
            return 0;
        }
        int lastSelectIndex = beforeKeyFrameInfo.getSelectForwardBezierPos();
        if (lastSelectIndex == nextKeyFrameInfo.getSelectBackwardBezierPos()) {
            /**
             * 判断上次选择的贝塞尔调整曲线类型是否是自定义类型
             * Judge whether the Bezier adjustment curve type selected last time is a custom type
             */
            if (lastSelectIndex == mCurveAdjustDataList.size() - 1) {
                long beforeTimeStamp = mBeforeKeyMapInfo.getKey();
                long nextTimeStamp = mNextKeyMapInfo.getKey();
                mCustomBezierRectF = mapSdkData2BezierRectF(beforeTimeStamp, nextTimeStamp, beforeKeyFrameInfo, nextKeyFrameInfo);
            }
            return lastSelectIndex;
        }
        return 0;
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
        PointF beforePointF = mBeforeKeyMapInfo.getValue().getTranslation();
        PointF nextPointF = mNextKeyMapInfo.getValue().getTranslation();
        if (isFront) {
            double forwardControlPointX = (nextTimeStamp - beforeTimeStamp) * pointF.x + beforeTimeStamp;
            double forwardControlPointYForTransX = (nextPointF.x - beforePointF.x) * pointF.y + beforePointF.x;
            double forwardControlPointYForTransY = (nextPointF.y - beforePointF.y) * pointF.y + beforePointF.y;
            mCurCaption.setCurrentKeyFrameTime(beforeTimeStamp - mCurCaption.getInPoint());
            NvsControlPointPair pairX = mCurCaption.getControlPoint(TRANS_X);
            if (pairX == null) {
                pairX = new NvsControlPointPair(new NvsPointD(0, 0), new NvsPointD(0, 0));
            }
            pairX.forwardControlPoint = new NvsPointD(forwardControlPointX - mCurCaption.getInPoint(), forwardControlPointYForTransX);
            mCurCaption.setControlPoint(TRANS_X, pairX);
            NvsControlPointPair pairY = mCurCaption.getControlPoint(TRANS_Y);
            if (pairY == null) {
                pairY = new NvsControlPointPair(new NvsPointD(0, 0), new NvsPointD(0, 0));
            }
            pairY.forwardControlPoint = new NvsPointD(forwardControlPointX - mCurCaption.getInPoint(), forwardControlPointYForTransY);
            mCurCaption.setControlPoint(TRANS_Y, pairY);
            KeyFrameInfo keyFrameInfo = mBeforeKeyMapInfo.getValue();
            keyFrameInfo.setForwardControlPointX(forwardControlPointX);
            keyFrameInfo.setForwardControlPointYForTransX(forwardControlPointYForTransX);
            keyFrameInfo.setForwardControlPointYForTransY(forwardControlPointYForTransY);
        } else {
            double backwardControlPointX = (nextTimeStamp - beforeTimeStamp) * pointF.x + beforeTimeStamp;
            double backwardControlPointYForTransX = (nextPointF.x - beforePointF.x) * pointF.y + beforePointF.x;
            double backwardControlPointYForTransY = (nextPointF.y - beforePointF.y) * pointF.y + beforePointF.y;
            mCurCaption.setCurrentKeyFrameTime(nextTimeStamp - mCurCaption.getInPoint());
            NvsControlPointPair pairX = mCurCaption.getControlPoint(TRANS_X);
            if (pairX == null) {
                pairX = new NvsControlPointPair(new NvsPointD(0, 0), new NvsPointD(0, 0));
            }
            pairX.backwardControlPoint = new NvsPointD(backwardControlPointX - mCurCaption.getInPoint(), backwardControlPointYForTransX);
            mCurCaption.setControlPoint(TRANS_X, pairX);

            NvsControlPointPair pairY = mCurCaption.getControlPoint(TRANS_Y);
            if (pairY == null) {
                pairY = new NvsControlPointPair(new NvsPointD(0, 0), new NvsPointD(0, 0));
            }
            pairY.backwardControlPoint = new NvsPointD(backwardControlPointX - mCurCaption.getInPoint(), backwardControlPointYForTransY);
            mCurCaption.setControlPoint(TRANS_Y, pairY);
            KeyFrameInfo keyFrameInfo = mNextKeyMapInfo.getValue();
            keyFrameInfo.setBackwardControlPointX(backwardControlPointX);
            keyFrameInfo.setBackwardControlPointYForTransX(backwardControlPointYForTransX);
            keyFrameInfo.setBackwardControlPointYForTransY(backwardControlPointYForTransY);
        }
    }

    /**
     * 更新时间线指定位置上的字幕数据和相关视图
     * Updates the caption data and associated views at the specified timestamp on the timeline
     *
     * @param timeStamp
     */
    private void updateCaptionRelativeView(long timeStamp) {
        if (timeStamp < 0) {
            return;
        }
        CaptionInfo captionInfo = null;
        Map<Long, KeyFrameInfo> keyFrameMap = null;
        if (mCurCaption != null) {
            captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
            if (captionInfo != null) {
                keyFrameMap = captionInfo.getKeyFrameInfo();
                if (!mIsPlaying) {
                    updateKeyFrameView(timeStamp, keyFrameMap);
                }
            }
        }

        long currentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        long minTime = getMinTimeFromFrame(keyFrameMap);
        long maxTime = getMaxTimeFromFrame(keyFrameMap);
        if ((mCurCaption != null)
                && minTime >= 0
                && maxTime >= 0
                && (currentPosition - mCurCaption.getInPoint() >= minTime)
                && (currentPosition - mCurCaption.getInPoint() <= maxTime)) {
            double floatValAtTime = mCurCaption.getFloatValAtTime(TRACK_OPACITY, currentPosition - mCurCaption.getInPoint());
            alphaSeek.setProgress((int) ((floatValAtTime) * alphaSeek.getMax()));

        }

        if (!mIsSeekTimeline) {
            return;
        }
        if (mCurCaption != null) {
            if (timeStamp > mCurCaption.getOutPoint() + 1000 || timeStamp < mCurCaption.getInPoint() - 1000) {
                // 当前字幕时间之外隐藏框 禁用所有关键帧按钮
                //Disable all keyframe buttons outside the current subtitle time hidden box
                mVideoFragment.setDrawRectVisible(View.GONE);
                displayEnable(false);
            } else {
                // seek到关键帧的位置的时候，选中一下当前的关键帧，使以后所有的操作，在当前帧下操作.
                //When seeking the location of the keyframe, select the current keyframe so that all future operations will be operated under the current frame.
                boolean hasKeyFrame = false;
                long currentKeyFrameStamp = -1;
                if (keyFrameMap != null) {
                    for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
                        if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                            hasKeyFrame = true;
                            currentKeyFrameStamp = entry.getKey();
                            break;
                        }
                    }
                }
                if (hasKeyFrame) {
                    mCurCaption.setCurrentKeyFrameTime(currentKeyFrameStamp - mCurCaption.getInPoint());
                }
                mTvAddDeleteFrame.setEnabled(true);
            }
        }
        if (mTimeline != null) {
            updatePlaytimeText(timeStamp);
            seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
        }
    }

    private void displayEnable(boolean enable) {
        mTvAddDeleteFrame.setEnabled(enable);
        mTvLastFrame.setEnabled(enable);
        mTvNextFrame.setEnabled(enable);
    }


    private long getMaxTimeFromFrame(Map<Long, KeyFrameInfo> keyFrameMap) {
        long maxTime = 0;
        if (keyFrameMap == null) {
            return -1;
        }
        for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
            Long key = entry.getKey();
            if (maxTime < key) {
                maxTime = key;
            }
        }
        return maxTime;
    }

    private long getMinTimeFromFrame(Map<Long, KeyFrameInfo> keyFrameMap) {
        long minTime = 0;
        if (keyFrameMap == null) {
            return -1;
        }
        for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
            Long key = entry.getKey();
            if (minTime > key) {
                minTime = key;
            }
        }
        return minTime;
    }


    /**
     * 判断字幕是否可编辑
     * Judge the caption is in edit state
     *
     * @return
     */
    private boolean ifCouldEditCaption() {
        CaptionInfo captionInfo = getCurrentCaptionInfo();
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
            if (!keyFrameInfoHashMap.isEmpty()) {
                // give tips
                ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_when_move_caption));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新字幕边框位置
     * Update the caption border position
     */
    private void updateCaptionBoundingRect() {
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        if (mCurCaption == null) {
            mVideoFragment.setDrawRectVisible(View.GONE);
        } else {
            mVideoFragment.changeCaptionRectVisible();
        }
    }

    /**
     * 更新字幕关键帧控制键的状态
     * Update the state of the caption keyframe control button
     *
     * @param timeStamp
     * @param keyFrameMap
     */
    private void updateKeyFrameView(long timeStamp, Map<Long, KeyFrameInfo> keyFrameMap) {
        if (keyFrameMap == null || keyFrameMap.isEmpty()) {
            mTvLastFrame.setEnabled(false);
            mTvNextFrame.setEnabled(false);
            changeAddOrDeleteView(true);
        } else {
            Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameMap.entrySet();
            Set<Long> keyFrameKeySet = keyFrameMap.keySet();
            Object[] objects = keyFrameKeySet.toArray();
            //上一帧
            //Previous frame
            long beforeKeyFrame = -1;
            Map.Entry<Long, KeyFrameInfo> beforeKeyMapInfo = null;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key < timeStamp) {
                    // 找到距离当前位置 向前最近的一个时间点
                    //Find the point in time closest to the current position
                    beforeKeyFrame = key;
                    beforeKeyMapInfo = entry;
                }
            }

            if (beforeKeyFrame == -1 || ((objects != null) && ((long) (objects[0]) == timeStamp))) {
                mTvLastFrame.setEnabled(false);
            } else {
                mTvLastFrame.setEnabled(true);
                if (mCurveListLayout.getVisibility() != View.VISIBLE) {
                    mBeforeKeyMapInfo = beforeKeyMapInfo;
                }
            }

            // 下一帧
            //next frame
            long nextKeyFrame = -1;
            Map.Entry<Long, KeyFrameInfo> nextKeyMapInfo = null;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key > timeStamp) {
                    // 找到距离当前位置 向后最近的一个时间点
                    //Find the point in time closest to the current position
                    nextKeyFrame = key;
                    nextKeyMapInfo = entry;
                    break;
                }
            }
            if (nextKeyFrame == -1 || ((objects != null) && ((long) (objects[objects.length - 1]) == timeStamp))) {
                mTvNextFrame.setEnabled(false);
            } else {
                mTvNextFrame.setEnabled(true);
                if (mCurveListLayout.getVisibility() != View.VISIBLE) {
                    mNextKeyMapInfo = nextKeyMapInfo;
                }
            }

            // 增加或者删除
            // add or delete frame
            boolean hasKeyFrame = false;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                    hasKeyFrame = true;
                    break;
                }
            }
            changeAddOrDeleteView(!hasKeyFrame);
            // keyFramePoint
            CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                if (mAddKeyFrame) {
                    spanInfo.mTimeSpan.setCurrentTimelinePosition(timeStamp, keyFrameMap);
                } else {
                    spanInfo.mTimeSpan.setCurrentTimelinePosition(timeStamp, null);
                }
            }
            if (mLlKeyFrame.getVisibility() == View.VISIBLE) {
                updateCurveAdjustView();
            }
        }
    }

    /**
     * 更新贝塞尔调节入口视图
     * Updated Bezier adjustment entry view
     */
    private void updateCurveAdjustView() {
        boolean isEnable = false;
        if (mTvLastFrame.isEnabled() && mTvNextFrame.isEnabled()
                && mTvAddDeleteFrame.getText().equals(getResources().getString(R.string.key_frame_add_frame_text))) {
            isEnable = true;
        }
        mTvKeyFrame.setText(getResources().getString(R.string.curve_to_adjust));
        mTvKeyFrame.setTextColor(isEnable ? getResources().getColor(R.color.white) : getResources().getColor(R.color.white_20));
        mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(isEnable ? ContextCompat.getDrawable(this, R.mipmap.icon_curve_adjust) : ContextCompat.getDrawable(this, R.mipmap.icon_curve_adjust_disable), null, null, null);
        mTvKeyFrame.setEnabled(isEnable);
    }

    /**
     * 添加或更新关键帧数据信息
     * Adds or updates key frame data information
     */
    private void updateOrAddKeyFrameInfo() {
        if (mCurCaption == null) {
            return;
        }
        int zValue = (int) mCurCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = null;
        if (captionIndex >= 0 && mCaptionDataListClone != null && mCaptionDataListClone.size() > captionIndex) {
            captionInfo = mCaptionDataListClone.get(captionIndex);
        }
        if (captionInfo == null) {
            return;
        }
        Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
        if (keyFrameInfo.isEmpty()) {
            mAddKeyFrame = true;
            addFrame();
            return;
        }
        long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        boolean hasKeyFrame = false;
        long currentKeyFrameStamp = -1;
        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfo.entrySet();
        for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
            if (timelineCurrentPosition > (entry.getKey() - 100000) && timelineCurrentPosition < (entry.getKey() + 100000)) {
                hasKeyFrame = true;
                currentKeyFrameStamp = entry.getKey();
                break;
            }
        }
        if (hasKeyFrame) {
            captionInfo.putKeyFrameInfo(currentKeyFrameStamp, generateKeyFrameInfo(mCurCaption));
        } else {
            mAddKeyFrame = true;
            addFrame();
        }
    }

    /**
     * 根据字幕信息生成一个关键帧对象
     * Generate a KeyFrameInfo object based on NvsClipCaption information
     *
     * @param caption
     * @return
     */
    private KeyFrameInfo generateKeyFrameInfo(NvsClipCaption caption) {
        return new KeyFrameInfo().setScaleX(caption.getScaleX())
                .setScaleY(caption.getScaleY())
                .setRotationZ(caption.getRotationZ())
                .setTranslation(caption.getCaptionTranslation())
                .setOpacity(caption.getOpacity());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_zoom_in) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomOutSequence();
        } else if (id == R.id.iv_zoom_out) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomInSequence();
        } else if (id == R.id.captionStyleButton) {
            mCaptionStyleButton.setClickable(false);
            CaptionInfo currentCaptionInfo1 = getCurrentCaptionInfo();
            if (currentCaptionInfo1 != null) {
                currentCaptionInfo1.setTranslation(mCurCaption.getCaptionTranslation());
                currentCaptionInfo1.setScaleFactorX(mCurCaption.getScaleX());
                currentCaptionInfo1.setScaleFactorY(mCurCaption.getScaleY());
                currentCaptionInfo1.setRotation(mCurCaption.getRotationZ());
            }
            BackupData.instance().setClipCaptionList(mCaptionDataListClone);
            BackupData.instance().setCaptionZVal((int) mCurCaption.getZValue());
            BackupData.instance().setCurSeekTimelinePos(mCurCaption.getInPoint());
            Bundle bundle = new Bundle();
            bundle.putBoolean("tradition_caption", isTraditionCaption);
            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), ClipCaptionStyleActivity.class, bundle, REQUESTCAPTIONSTYLE);
        } else if (id == R.id.ll_traditional_caption) {
            if (mTimeline.getDuration() < NS_TIME_BASE) {
                ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_add_caption_restrictions));
                return;
            }
            new InputDialog(this, R.style.dialog, new InputDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean ok) {
                    if (ok) {
                        InputDialog d = (InputDialog) dialog;
                        String userInputText = d.getUserInputText();
                        addCaption(userInputText, true);
                    }
                }
            }).show();
        } else if (id == R.id.ll_pieced_caption) {
            if (mTimeline.getDuration() < NS_TIME_BASE) {
                ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_add_caption_restrictions));
                return;
            }
            new InputDialog(this, R.style.dialog, new InputDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean ok) {
                    if (ok) {
                        InputDialog d = (InputDialog) dialog;
                        String userInputText = d.getUserInputText();
                        addCaption(userInputText, false);
                    }
                }
            }).show();
        } else if (id == R.id.iv_ok) {
            if (mLlKeyFrame.getVisibility() == View.VISIBLE) {
                //如果是关键帧模式
                //If it is key frame mode
                displayKeyFrameLayout(false);
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                if (currentCaptionInfo == null) {
                    displayKeyFrame(true, false);
                } else {
                    Map<Long, KeyFrameInfo> keyFrameInfo = currentCaptionInfo.getKeyFrameInfo();
                    if (keyFrameInfo.isEmpty()) {
                        displayKeyFrame(true, false);
                    } else {
                        displayKeyFrame(true, true);
                    }
                }
                CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
                if (spanInfo != null && spanInfo.mTimeSpan != null) {
                    spanInfo.mTimeSpan.setKeyFrameInfo(null);
                }
                mAddKeyFrame = false;
                mCaptionStyleButton.setVisibility(View.VISIBLE);
            } else {
                mStreamingContext.stop();
                removeTimeline();
                BackupData.instance().setClipInfoData(mClipArrayList);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        } else if (id == R.id.play_btn) {
            playVideo();
        } else if (id == R.id.tv_key_frame) {
            if (mAddKeyFrame) {
                mAdapter.setSelectedPosition(getSelectedCurveListIndex());
                mVideoFragment.setDrawRectVisible(View.GONE);
                mCurveListLayout.setVisibility(View.VISIBLE);
            } else {
                displayKeyFrameLayout(true);
                displayKeyFrame(true, false);
                mCaptionStyleButton.setVisibility(View.GONE);
                CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                if (spanInfo != null && spanInfo.mTimeSpan != null && currentCaptionInfo != null) {
                    spanInfo.mTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }
            }
        } else if (id == R.id.tv_last_frame) {
            toOneKeyFrame(false);
        } else if (id == R.id.tv_add_or_delete_frame) {
            CharSequence text = mTvAddDeleteFrame.getText();
            if (text == null) {
                return;
            }
            if (text.toString().equals(getString(R.string.key_frame_add_frame_text))) {
                addFrame();
            } else {
                deleteFrame();
            }
        } else if (id == R.id.tv_next_frame) {
            toOneKeyFrame(true);
        } else if (id == R.id.iv_confirm_curve) {
            if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK)
                mStreamingContext.stop();
            mCurveListLayout.setVisibility(View.GONE);
            changeRectVisible();
        } else if (id == R.id.iv_confirm_curve_adjust) {
            mCurveAdjustLayout.setVisibility(View.GONE);
            if (mCustomBezierRectF == null || mBeforeKeyMapInfo == null || mNextKeyMapInfo == null) {
                return;
            }
            long beforeTimeStamp = mBeforeKeyMapInfo.getKey();
            long nextTimeStamp = mNextKeyMapInfo.getKey();
            updateCaptionControlPoint(new PointF(mCustomBezierRectF.left, mCustomBezierRectF.bottom), true, beforeTimeStamp, nextTimeStamp);
            updateCaptionControlPoint(new PointF(mCustomBezierRectF.right, mCustomBezierRectF.top), false, beforeTimeStamp, nextTimeStamp);
            playbackTimeline(beforeTimeStamp, nextTimeStamp);
        }
    }

    private CaptionInfo getCurrentCaptionInfo() {
        if (mCurCaption == null) {
            return null;
        }
        int zValue = (int) mCurCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
        return captionInfo;
    }

    /**
     * 展示关键帧的显示布局
     * Display layout of key frames
     *
     * @param display boolean true显示关键帧布局，进入关键帧编辑模式，false不显示
     */
    private void displayKeyFrameLayout(boolean display) {
        if (display) {
            mLlKeyFrame.setVisibility(View.VISIBLE);
            mLlAddTraditional.setVisibility(View.INVISIBLE);
            mLlAddPieced.setVisibility(View.INVISIBLE);
            mAddKeyFrame = true;
        } else {
            mLlKeyFrame.setVisibility(View.INVISIBLE);
            mLlAddTraditional.setVisibility(View.VISIBLE);
            mLlAddPieced.setVisibility(View.VISIBLE);
            mAddKeyFrame = false;
        }
    }

    /**
     * 使能关键帧入口的显示
     * Enables the display of keyframe entry view
     *
     * @param visible boolean true 显示关键帧文字以及按钮 false 不显示
     * @param isEdit  boolean true 显示编辑 false 不显示
     */
    private void displayKeyFrame(boolean visible, boolean isEdit) {
        if (mAddKeyFrame) {
            updateCurveAdjustView();
        } else {
            if (visible && mTvKeyFrame.getVisibility() != View.VISIBLE) {
                mTvKeyFrame.setVisibility(View.VISIBLE);
            } else if (!visible && mTvKeyFrame.getVisibility() == View.VISIBLE) {
                mTvKeyFrame.setVisibility(View.INVISIBLE);
            }
            mTvKeyFrame.setEnabled(true);
            if (isEdit && !getString(R.string.key_frame_edit_text).equals(mTvKeyFrame.getText().toString())) {
                mTvKeyFrame.setText(getString(R.string.key_frame_edit_text));
                mTvKeyFrame.setTextColor(getResources().getColor(R.color.color_yellow_a5));
                mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.key_frame_edit), null, null, null);
            } else if (!isEdit && !getString(R.string.key_frame_text).equals(mTvKeyFrame.getText().toString())) {
                mTvKeyFrame.setText(getString(R.string.key_frame_text));
                mTvKeyFrame.setTextColor(getResources().getColor(R.color.white));
                mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.key_frame), null, null, null);
            }
        }
    }

    /**
     * 跳转到上一个关键帧或下一个关键帧
     * Seek to the last or next keyframe location
     *
     * @param next boolean true下一帧，false上一帧.
     */
    private void toOneKeyFrame(boolean next) {
        if (mCurCaption == null) {
            return;
        }
        mVideoFragment.stopEngine();
        CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameMap = captionInfo.getKeyFrameInfo();
            long currentTimelinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long nextKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
                Long key = entry.getKey();
                if (next && key > currentTimelinePosition) {
                    nextKeyFrame = key;
                    break;
                } else if (!next && key < currentTimelinePosition) {
                    nextKeyFrame = key;
                }
            }
            if (nextKeyFrame == -1) {
                if (next) {
                    mTvNextFrame.setEnabled(false);
                } else {
                    mTvLastFrame.setEnabled(false);
                }

            } else {
                if (next) {
                    mTvNextFrame.setEnabled(true);
                } else {
                    mTvLastFrame.setEnabled(true);
                }
                seekTimeline(nextKeyFrame);
                seekMultiThumbnailSequenceView();
            }
        }
        updateCaptionBoundingRect();
    }


    /**
     * 添加关键帧
     * Add keyframe
     */
    private void addFrame() {
        if (mCurCaption == null) {
            return;
        }
        mVideoFragment.stopEngine();
        CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
        mVideoFragment.stopEngine();
        float opacity = mCurCaption.getOpacity();
        long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
        addKeyFrameOnCurrentTime(duration, mCurCaption.getCaptionTranslation(), mCurCaption.getScaleX(),
                mCurCaption.getScaleY(), mCurCaption.getRotationZ(), opacity);
        long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (captionInfo != null) {
            KeyFrameInfo keyFrameInfo = new KeyFrameInfo()
                    .setScaleX(mCurCaption.getScaleX())
                    .setScaleY(mCurCaption.getScaleY())
                    .setRotationZ(mCurCaption.getRotationZ())
                    .setTranslation(mCurCaption.getCaptionTranslation())
                    .setOpacity(opacity);
            captionInfo.putKeyFrameInfo(timelineCurrentPosition, keyFrameInfo);
            disableKeyFrameBezierData(captionInfo.getKeyFrameInfo(), timelineCurrentPosition);
        }
        int alignVal = mCurCaption.getTextAlignment();
        mVideoFragment.setAlignIndex(alignVal);
        updateCaptionBoundingRect();
        mIsSeekTimeline = true;
        seekTimeline(timelineCurrentPosition);
        seekMultiThumbnailSequenceView();
        CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
        if (captionInfo != null && spanInfo != null && spanInfo.mTimeSpan != null) {
            spanInfo.mTimeSpan.setKeyFrameInfo(captionInfo.getKeyFrameInfo());
        }
        changeAddOrDeleteView(false);
    }


    /**
     * 删除关键帧
     * Delete keyframe
     */
    private void deleteFrame() {
        if (mCurCaption == null) {
            return;
        }
        mVideoFragment.stopEngine();
        CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameMap = captionInfo.getKeyFrameInfo();
            long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            for (Long aLong : keyFrameMap.keySet()) {
                if (timelineCurrentPosition >= aLong - 100000 && timelineCurrentPosition <= aLong + 100000) {
                    keyFrameMap.get(aLong);
                    keyFrameMap.remove(aLong);
                    removeKeyFrameByCurrentTime(aLong - mCurCaption.getInPoint());
                    break;
                }
            }
            disableKeyFrameBezierData(keyFrameMap, timelineCurrentPosition);
        }
        CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
        if (captionInfo != null && spanInfo != null && spanInfo.mTimeSpan != null) {
            spanInfo.mTimeSpan.setKeyFrameInfo(captionInfo.getKeyFrameInfo());
        }
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        updateCaptionBoundingRect();
        changeAddOrDeleteView(true);
    }

    /**
     * 添加或删除关键帧时，将上一针或下一帧的贝塞尔调节数据置为无效数据
     * When adding or deleting a key frame, set the Bezier adjustment data of the previous stitch or the next frame as invalid
     *
     * @param keyFrameMap
     * @param currentTimelinePosition
     */
    private void disableKeyFrameBezierData(Map<Long, KeyFrameInfo> keyFrameMap, long currentTimelinePosition) {
        long nextKeyFrame = -1;
        long lastKeyFrame = -1;
        for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
            Long key = entry.getKey();
            if (key > currentTimelinePosition) {
                if (nextKeyFrame == -1 || nextKeyFrame < key) {
                    nextKeyFrame = key;
                }
            }
            if (key < currentTimelinePosition) {
                if (lastKeyFrame == -1 || lastKeyFrame > key) {
                    lastKeyFrame = key;
                }
            }
        }
        if (nextKeyFrame > 0) {
            keyFrameMap.get(nextKeyFrame).setBackwardControlPointX(-1);
        }
        if (lastKeyFrame > 0) {
            keyFrameMap.get(lastKeyFrame).setForwardControlPointX(-1);
        }

    }

    /**
     * 改变关键帧模式中添加或者删除关键帧的显示
     * Change the display of added or deleted keyframes in keyframe mode
     */
    private void changeAddOrDeleteView(boolean add) {
        if (add) {
            mTvAddDeleteFrame.setText(R.string.key_frame_add_frame_text);
            mTvAddDeleteFrame.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_add_frame_selector), null, null);
        } else {
            mTvAddDeleteFrame.setText(R.string.key_frame_delete_frame_text);
            mTvAddDeleteFrame.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_delete_frame_selector), null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUESTCAPTIONSTYLE:
                mCaptionDataListClone = BackupData.instance().getClipCaptionList();
                mClipInfo.setCaptionInfoList(mCaptionDataListClone);
                mCurCaption = null;
                TimelineUtil.applyClipCaption(curVideoClip, mCaptionDataListClone, 0);
                mTimelineEditor.deleteAllTimeSpan();
                mTimeSpanInfoList.clear();
                addAllTimeSpan();
                long curSeekPos = BackupData.instance().getCurSeekTimelinePos();
                seekTimeline(curSeekPos);
                seekMultiThumbnailSequenceView();
                boolean isSelectCurCaption = data.getBooleanExtra("isSelectCurCaption", true);
                if (!isSelectCurCaption) {
                    selectCaptionAndTimeSpan();//REQUESTCAPTIONSTYLE
                } else {
                    int curZVal = BackupData.instance().getCaptionZVal();
                    selectCaptionByZVal(curZVal);
                    //这里依然存在null 的可能性 需要添加判断处理
                    //The possibility that there is still null requires the addition of judgment handling
                    if (mCurCaption != null) {
                        int captionIndex = getCaptionIndex((int) mCurCaption.getZValue());
                        reloadKeyFrame(captionIndex);
                        long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                        mCurCaption.setCurrentKeyFrameTime(duration);
                        mVideoFragment.setCurCaption(mCurCaption);
                        mVideoFragment.updateCaptionCoordinate(mCurCaption);
                        mVideoFragment.changeCaptionRectVisible();
                        removeKeyFrameByCurrentTime(duration);
                        int alignVal = mCurCaption.getTextAlignment();
                        mVideoFragment.setAlignIndex(alignVal);
                    }
                    selectTimeSpan();//REQUESTCAPTIONSTYLE
                }
                break;
            default:
                break;
        }
    }

    /**
     * 重新加载指定位置上关键帧的数据
     * Reloads the data for the keyframe at the specified location
     *
     * @param index
     */
    private void reloadKeyFrame(int index) {
        Map<Long, KeyFrameInfo> keyFrameInfoMap = mCaptionDataListClone.get(index).getKeyFrameInfo();
        Set<Long> keySet = keyFrameInfoMap.keySet();
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            long duration = currentTime - mCurCaption.getInPoint();
            removeKeyFrameByCurrentTime(duration);
            addKeyFrameOnCurrentTime(duration, keyFrameInfo.getTranslation(), keyFrameInfo.getScaleX()
                    , keyFrameInfo.getScaleY(), keyFrameInfo.getRotationZ(), keyFrameInfo.getOpacity());
        }
    }

    /**
     * 通过字幕Z值查找字幕控件
     * Find the caption widget by the caption Z value
     *
     * @param curZVal
     */
    private void selectCaptionByZVal(int curZVal) {
        if (mTimeline != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            List<NvsClipCaption> captionList = curVideoClip.getCaptionsByClipTimePosition(curPos);
            int captionCount = captionList.size();
            if (captionCount > 0) {
                for (int i = 0; i < captionCount; i++) {
                    int zVal = (int) captionList.get(i).getZValue();
                    if (curZVal == zVal) {
                        mCurCaption = captionList.get(i);
                        break;
                    }
                }
                if (mCurCaption != null) {
                    mCaptionStyleButton.setVisibility(View.VISIBLE);
                    CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                        displayKeyFrame(true, !keyFrameInfo.isEmpty());
                    }
                }
            } else {
                mCurCaption = null;
                mCaptionStyleButton.setVisibility(View.GONE);
                displayKeyFrame(false, false);
            }
        }
    }

    private void playVideo() {
        if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long endTime = mTimeline.getDuration();
            mVideoFragment.playVideo(startTime, endTime);
        } else {
            mVideoFragment.stopEngine();
        }
    }


    private void updatePlaytimeText(long playTime) {
        if (mTimeline != null) {
            long totalDuaration = mTimeline.getDuration();
            String strTotalDuration = TimeFormatUtil.formatUsToString1(totalDuaration);
            String strCurrentDuration = TimeFormatUtil.formatUsToString1(playTime);
            mShowCurrentDuration.setLength(0);
            mShowCurrentDuration.append(strCurrentDuration);
            mShowCurrentDuration.append("/");
            mShowCurrentDuration.append(strTotalDuration);
            mPlayCurTime.setText(mShowCurrentDuration.toString());
        }
    }

    /**
     * 获取当前字幕Z值
     * Gets the current caption Z value
     */
    private float getCurCaptionZVal() {
        float zVal = 0.0f;
        NvsClipCaption caption = curVideoClip.getFirstCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal) {
                zVal = tmpZVal;
            }
            caption = curVideoClip.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.setCurCaption(mCurCaption);
                mOkBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurCaption != null) {
                            boolean initCaption = true;
                            if (getCurrentCaptionInfo() != null) {
                                Map<Long, KeyFrameInfo> keyFrameInfo = getCurrentCaptionInfo().getKeyFrameInfo();
                                if (keyFrameInfo != null && keyFrameInfo.get(0L) != null) {
                                    initCaption = false;
                                }
                            }
                            long frameDuration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                            mCurCaption.setCurrentKeyFrameTime(frameDuration);
                            updateCaptionBoundingRect();
                            if (initCaption) {
                                removeKeyFrameByCurrentTime(frameDuration);
                            }
                        }
                        mVideoFragment.updateCaptionCoordinate(mCurCaption);
                        mVideoFragment.changeCaptionRectVisible();
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    }
                }, 100);
            }
        });
        /*
         * 设置字幕模式
         * Set caption mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_CAPTION);
        mVideoFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomRelativeLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
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
    }

    /*
     * 添加字幕
     * Add captions
     * */
    private void addCaption(String caption, boolean traditional) {
        long inPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        long captionDuration = 4 * Constants.NS_TIME_BASE;
        long outPoint = inPoint + captionDuration;
        long duration = mTimeline.getDuration();

        if (outPoint > duration) {
            captionDuration = duration - inPoint;
            if (captionDuration <= Constants.NS_TIME_BASE) {
                captionDuration = Constants.NS_TIME_BASE;
                inPoint = duration - captionDuration;
                if (duration <= Constants.NS_TIME_BASE) {
                    captionDuration = duration;
                    inPoint = 0;
                }
            }
            outPoint = duration;
        }
        if (traditional) {
            mCurCaption = curVideoClip.addCaption(caption, inPoint, captionDuration, null);
        } else {
            mCurCaption = curVideoClip.addModularCaption(caption, inPoint, captionDuration);
        }
        if (mCurCaption == null) {
            Log.e(TAG, "addCaption is failed");
            return;
        }
        float zVal = getCurCaptionZVal();
        mCurCaption.setZValue(zVal);
        NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);
        if (timeSpan == null) {
            Log.e(TAG, "addCaption time span is failed");
            return;
        }
        mCaptionStyleButton.setVisibility(View.VISIBLE);
        displayKeyFrame(true, false);
        CaptionTimeSpanInfo info = new CaptionTimeSpanInfo(mCurCaption, timeSpan);
        info.setTraditional(traditional);
        if (!traditional) {
            timeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
        }
        mTimeSpanInfoList.add(info);
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        int alignVal = mCurCaption.getTextAlignment();
        mVideoFragment.setAlignIndex(alignVal);
        mVideoFragment.changeCaptionRectVisible();
        seekTimeline(inPoint);
        /*
         * 选择timeSpan
         * Select timeSpan
         * */
        selectTimeSpan();//addCaption
        CaptionInfo captionInfo = Util.saveCaptionData(mCurCaption);
        if (captionInfo != null) {
            captionInfo.setTraditionCaption(traditional);
            mCaptionDataListClone.add(captionInfo);
        }
        showOrHideCaptionAlpha();
        if (mCurCaption != null) {
            alphaSeek.setProgress((int) (mCurCaption.getOpacity() * alphaSeek.getMax()));
            seekBarOpacityWidthValue.setText((int) (mCurCaption.getOpacity() * alphaSeek.getMax()) + "");
        }
    }

    /**
     * 添加TimeSpan
     * Add TimeSpan
     *
     * @param inPoint
     * @param outPoint
     * @return
     */
    private NvsTimelineTimeSpan addTimeSpan(long inPoint, long outPoint) {
        /*
         * warning: 使用addTimeSpanExt()之前必须设置setTimeSpanType()
         * warning: setTimeSpanType () must be set before using addTimeSpanExt ()
         * */
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        NvsTimelineTimeSpan timelineTimeSpan = mTimelineEditor.addTimeSpan(inPoint, outPoint);
        if (timelineTimeSpan == null) {
            Log.e(TAG, "addTimeSpan: " + " addCaption time span is failed");
            return null;
        }
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimInChangeListener() {
            @Override
            public void onTrimInChange(long timeStamp, boolean isDragEnd) {
                seekTimeline(timeStamp);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();
                NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                if (currentCaptionInfo != null && mAddKeyFrame) {
                    currentTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }
                if (mCurCaption != null) {
                    mCurCaption.changeInPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setInPoint(timeStamp);
                    }
                    if (isDragEnd) {
                        //拖拽结束后滑动播放帧到最后
                        //After dragging, slide the playback frame to the end
                        seekMultiThumbnailSequenceView();
                    } else {
                        mVideoFragment.setDrawRectVisible(View.GONE);
                    }
                    // 若覆盖了关键帧 则移除覆盖的关键帧信息
                    //If the key frame is covered, remove the covered key frame information
                    CaptionInfo captionInfo = getCurrentCaptionInfo();
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = null;
                        if (keyFrameInfoHashMap != null) {
                            entries = keyFrameInfoHashMap.entrySet();
                        }
                        if (entries != null && !entries.isEmpty()) {
                            boolean refresh = false;
                            Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                            long currentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                            while (iterator.hasNext()) {
                                Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                                // 这里比较的是timeline上的时间
                                // The comparison here is the time on the timeline
                                Log.d(TAG, " trimIn:" + timeStamp + " itemKey:" + next.getKey() + " timelinePosition:" + currentPosition);
                                if (next.getKey() < currentPosition) {
                                    removeKeyFrameByCurrentTime(next.getKey());
                                    iterator.remove();
                                    refresh = true;
                                }
                            }
                            if (refresh || isDragEnd) {
                                removeAllKeyFrame();
                                Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = captionInfo.getKeyFrameInfo();
                                Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                                for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
                                    KeyFrameInfo captionKeyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
                                    addKeyFrameOnCurrentTime(longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint(),
                                            captionKeyFrameInfo.getTranslation(), captionKeyFrameInfo.getScaleX(),
                                            captionKeyFrameInfo.getScaleY(), captionKeyFrameInfo.getRotationZ(), captionKeyFrameInfo.getOpacity());
                                }
                                rebuildBezierData(entriesAfter);
                            }
                        }
                    }
                }
                boolean initCaption = true;
                long frameDuration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                if (getCurrentCaptionInfo() != null) {
                    Map<Long, KeyFrameInfo> keyFrameInfo = getCurrentCaptionInfo().getKeyFrameInfo();
                    if (keyFrameInfo != null) {
                        Set<Long> longs = keyFrameInfo.keySet();
                        for (Long aLong : longs) {
                            if (frameDuration < aLong + 100000 && frameDuration > aLong + 100000) {
                                initCaption = false;
                            }
                        }
                    }
                }
                mCurCaption.setCurrentKeyFrameTime(frameDuration);
                updateCaptionBoundingRect();
                if (initCaption) {
                    removeKeyFrameByCurrentTime(frameDuration);
                }
            }
        });
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimOutChangeListener() {
            @Override
            public void onTrimOutChange(long timeStamp, boolean isDragEnd) {
                /*
                 * outPoint是开区间，seekTimeline时，需要往前平移一帧即0.04秒，转换成微秒即40000微秒
                 * outPoint is an open interval. In seekTimeline, you need to pan one frame, that is, 0.04 seconds, and convert it to microseconds, that is, 40,000 microseconds.
                 * */
                seekTimeline(timeStamp - 40000);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();
                if (mCurCaption != null) {
                    mCurCaption.changeOutPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOutPoint(timeStamp);
                    }
                    if (isDragEnd) {
                        //拖拽结束后滑动播放帧到最后
                        //After dragging, slide the playback frame to the end
                        seekMultiThumbnailSequenceView();
                    }
                    // 若覆盖了关键帧 则移除覆盖的关键帧信息
                    // If the key frame is covered, remove the covered key frame information
                    CaptionInfo captionInfo = getCurrentCaptionInfo();
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = null;
                        if (keyFrameInfoHashMap != null) {
                            entries = keyFrameInfoHashMap.entrySet();
                        }
                        if (entries != null && !entries.isEmpty()) {
                            boolean refresh = false;
                            Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                            long currentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                            while (iterator.hasNext()) {
                                Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                                // 这里比较的是timeline上的时间
                                //The comparison here is the time on the timeline
                                Log.d(TAG, " trimOut:" + timeStamp + " itemKey:" + next.getKey() + " timelinePosition:" + currentPosition);
                                if (next.getKey() > currentPosition) {
                                    removeKeyFrameByCurrentTime(next.getKey());
                                    iterator.remove();
                                    refresh = true;
                                }
                            }
                            if (refresh) {
                                removeAllKeyFrame();
                                Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = captionInfo.getKeyFrameInfo();
                                Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                                for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
                                    KeyFrameInfo captionKeyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
                                    addKeyFrameOnCurrentTime(longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint(),
                                            captionKeyFrameInfo.getTranslation(), captionKeyFrameInfo.getScaleX(),
                                            captionKeyFrameInfo.getScaleY(), captionKeyFrameInfo.getRotationZ(), captionKeyFrameInfo.getOpacity());
                                }
                                rebuildBezierData(entriesAfter);
                            }
                        }
                    }
                }
            }
        });
        return timelineTimeSpan;
    }

    /**
     * 拖动字幕timespan后需要重新载入贝塞尔调整的数据
     * Reload the Bezier adjusted data after draging the subtitle TimeSpan
     *
     * @param entriesAfter
     */
    private void rebuildBezierData(Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter) {
        //设置字幕贝塞尔曲线数据
        //set caption bezier adjust function
        for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
            KeyFrameInfo keyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
            double forwardControlPointX = keyFrameInfo.getForwardControlPointX();
            double backwardControlPointX = keyFrameInfo.getBackwardControlPointX();
            if (forwardControlPointX == -1 && backwardControlPointX == -1) {
                continue;
            }
            long duration = longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint();
            mCurCaption.setCurrentKeyFrameTime(duration);
            NvsControlPointPair pairX = mCurCaption.getControlPoint(TRANS_X);
            NvsControlPointPair pairY = mCurCaption.getControlPoint(TRANS_Y);
            if (pairX == null || pairY == null) {
                continue;
            }
            if (backwardControlPointX != -1) {
                pairX.backwardControlPoint.x = backwardControlPointX - mCurCaption.getInPoint();
                pairX.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransX();
                pairY.backwardControlPoint.x = backwardControlPointX - mCurCaption.getInPoint();
                pairY.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransY();
            }
            if (forwardControlPointX != -1) {
                pairX.forwardControlPoint.x = forwardControlPointX - mCurCaption.getInPoint();
                pairX.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransX();
                pairY.forwardControlPoint.x = forwardControlPointX - mCurCaption.getInPoint();
                pairY.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransY();
            }
            mCurCaption.setControlPoint(TRANS_X, pairX);
            mCurCaption.setControlPoint(TRANS_Y, pairY);
        }
    }

    /**
     * 清空关键帧
     * Remove all keyframe
     */
    private boolean removeAllKeyFrame() {
        if (mCurCaption != null) {
            boolean removeStickerTransXSuccess = mCurCaption.removeAllKeyframe(TRANS_X);
            boolean removeStickerTransYSuccess = mCurCaption.removeAllKeyframe(TRANS_Y);
            boolean removeStickerScaleX = mCurCaption.removeAllKeyframe(SCALE_X);
            boolean removeStickerScaleY = mCurCaption.removeAllKeyframe(SCALE_Y);
            boolean removeStickerRotZ = mCurCaption.removeAllKeyframe(ROTATION_Z);
            Log.d(TAG, "removeAllKeyFrame transX:" + removeStickerTransXSuccess + " transY:" + removeStickerTransYSuccess
                    + " scaleX:" + removeStickerScaleX + " scaleY:" + removeStickerScaleY + " rotationZ:" + removeStickerRotZ);
            return removeStickerTransXSuccess && removeStickerTransYSuccess && removeStickerScaleX
                    && removeStickerScaleY && removeStickerRotZ;
        }
        return false;
    }

    /**
     * 添加一个关键帧
     * Add a keyframe
     *
     * @param currentTime
     * @param transF
     * @param scaleX
     * @param scaleY
     * @param rotationZ
     */
    private void addKeyFrameOnCurrentTime(long currentTime, PointF transF, float scaleX, float scaleY, float rotationZ, float opacity) {
        mCurCaption.setCurrentKeyFrameTime(currentTime);
        mCurCaption.setCaptionTranslation(transF);
        mCurCaption.setScaleX(scaleX);
        mCurCaption.setScaleY(scaleY);
        mCurCaption.setRotationZ(rotationZ);
        mCurCaption.setFloatValAtTime(TRACK_OPACITY, opacity, currentTime);
    }

    /**
     * 删除指定时间点的关键帧
     * Deletes a keyframe at the specified point in time
     *
     * @param currentTime
     */
    private void removeKeyFrameByCurrentTime(long currentTime) {
        if (mCurCaption != null) {
            mCurCaption.removeKeyframeAtTime(TRANS_X, currentTime);
            mCurCaption.removeKeyframeAtTime(TRANS_Y, currentTime);
            mCurCaption.removeKeyframeAtTime(SCALE_X, currentTime);
            mCurCaption.removeKeyframeAtTime(SCALE_Y, currentTime);
            mCurCaption.removeKeyframeAtTime(ROTATION_Z, currentTime);
        }
    }

    /**
     * 这个方法修改
     * 1.添加移除关键帧的操作是为了播放时如果字幕移动的话，字幕外边框跟随移动
     * 2.现在播放过程中字幕移动外边框隐藏 不需要显示 取消关键帧操作
     * This method is modified as follows
     * 1. The operation of removing key frame is added so that if the subtitle moves during playback, the outer frame of the subtitle will move along with it
     * 2. It is no longer necessary to cancel the keyframe operation when the subtitle moves the outer border to hide during playback
     */
    private void changeRectVisible() {
        if (mCurCaption != null) {
            boolean addAndDelete = true;
            long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            if (mCurCaption != null) {
                CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                if (captionInfo != null) {
                    Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                    if (keyFrameInfo == null || keyFrameInfo.isEmpty() || keyFrameInfo.containsKey(timelineCurrentPosition)) {
                        addAndDelete = false;
                    }
                }
            }
            if (addAndDelete) {
                long duration = timelineCurrentPosition - mCurCaption.getInPoint();
                mVideoFragment.setCurCaption(mCurCaption);
                mVideoFragment.updateCaptionCoordinate(mCurCaption);
                mVideoFragment.changeCaptionRectVisible();
                mCurCaption.setCurrentKeyFrameTime(duration);
                removeKeyFrameByCurrentTime(duration);
            }
            mVideoFragment.setCurCaption(mCurCaption);
            mVideoFragment.updateCaptionCoordinate(mCurCaption);
            mVideoFragment.changeCaptionRectVisible();
        }
    }

    private NvsTimelineTimeSpan getCurrentTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mCaption == mCurCaption) {
                return mTimeSpanInfoList.get(i).mTimeSpan;
            }
        }
        return null;
    }

    private void seekMultiThumbnailSequenceView() {
        if (mMultiSequenceView != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long duration = mTimeline.getDuration();
            mMultiSequenceView.scrollTo(Math.round(((float) curPos) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }

    /**
     * 添加所有的TimeSpan
     * Add all TimeSpan
     */
    private void addAllTimeSpan() {
        NvsClipCaption caption = curVideoClip.getFirstCaption();
        while (caption != null) {
            int capCategory = caption.getCategory();
            int roleTheme = caption.getRoleInTheme();
            Logger.e(TAG, "capCategoryCp = " + capCategory + "**isModular=" + caption.isModular());
            /*
             * capCategory值为0是默认字幕即未使用字幕样式的字幕，
             * 值为1表示是用户自定义种类即使用字幕样式的字幕，值为2是主题字幕
             * A capCategory value of 0 is the default caption, that is, a caption with no subtitle style.
             * A value of 1 is a user-defined category, that is, a caption with subtitle style.
             * A value of 2 is the theme caption
             * */
            if (capCategory == NvsClipCaption.THEME_CATEGORY
                    && roleTheme != NvsClipCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作编辑处理 Subject captions are not edited
                caption = curVideoClip.getNextCaption(caption);
                continue;
            }
            long inPoint = caption.getInPoint();
            long outPoint = caption.getOutPoint();
            NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);

            if (timeSpan != null) {
                CaptionTimeSpanInfo timeSpanInfo = new CaptionTimeSpanInfo(caption, timeSpan);
                timeSpanInfo.setTraditional(!caption.isModular());
                if (!timeSpanInfo.isTraditional) {
                    timeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
                }
                mTimeSpanInfoList.add(timeSpanInfo);
            }
            caption = curVideoClip.getNextCaption(caption);
        }
    }

    /**
     * 选择字幕
     * Select caption
     */
    private void selectCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsClipCaption> captionList = curVideoClip.getCaptionsByClipTimePosition(curPos);
        int captionCount = captionList.size();
        if (captionCount > 0) {
            float zVal = captionList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < captionCount; i++) {
                float tmpZVal = captionList.get(i).getZValue();
                if (tmpZVal >= zVal) {
                    index = i;
                    break;
                }
            }
            CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                spanInfo.mTimeSpan.setKeyFrameInfo(null);
            }
            if (mAddKeyFrame) {
                if (mCurCaption == null) {
                    mCurCaption = captionList.get(index);
                }
            } else {
                mCurCaption = captionList.get(index);
            }

            if (mAddKeyFrame) {
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                spanInfo = getCurrentTimeSpanInfo();
                if (spanInfo != null && spanInfo.mTimeSpan != null) {
                    spanInfo.mTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }
            }

            if (mCurCaption.getCategory() == NvsClipCaption.THEME_CATEGORY
                    && mCurCaption.getRoleInTheme() != NvsClipCaption.ROLE_IN_THEME_GENERAL) {
                mCurCaption = null;
                mCaptionStyleButton.setVisibility(View.GONE);
                displayKeyFrame(false, false);
            } else {
                boolean isEdit = false;
                if (mLlKeyFrame.getVisibility() != View.VISIBLE) {
                    //如果没有进入编辑关键帧
                    //If you do not enter the edit key frame
                    CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                        if (keyFrameInfo != null && !keyFrameInfo.isEmpty()) {
                            //只要有关键帧就显示编辑关键帧
                            //Show editing keyframes as long as there are keyframes
                            isEdit = true;
                        }
                    }
                }
                if (!mAddKeyFrame) {
                    mCaptionStyleButton.setVisibility(View.VISIBLE);
                    displayKeyFrame(true, isEdit);
                }
            }
        } else {
            CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                spanInfo.mTimeSpan.setKeyFrameInfo(null);
            }
            mCurCaption = null;
            mCaptionStyleButton.setVisibility(View.GONE);
            displayKeyFrame(false, false);
        }
        showOrHideCaptionAlpha();
    }

    private void selectTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            CaptionTimeSpanInfo captionTimeSpanInfo = mTimeSpanInfoList.get(i);
            if (mCurCaption != null && captionTimeSpanInfo != null) {
                if (captionTimeSpanInfo.mCaption == mCurCaption) {
                    NvsTimelineTimeSpan timeSpan = mTimeSpanInfoList.get(i).mTimeSpan;
                    if (timeSpan != null) {
                        mTimelineEditor.selectTimeSpan(timeSpan);
                    }
                    isTraditionCaption = captionTimeSpanInfo.isTraditional;
                    break;
                }

            }
        }
    }

    private CaptionTimeSpanInfo getCurrentTimeSpanInfo() {
        CaptionTimeSpanInfo captionTimeSpanInfo;
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            captionTimeSpanInfo = mTimeSpanInfoList.get(i);
            if (mCurCaption != null && captionTimeSpanInfo != null) {
                if (captionTimeSpanInfo.mCaption == mCurCaption) {
                    return captionTimeSpanInfo;
                }
            }
        }
        return null;
    }

    private void deleteCurCaptionTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mCaption == mCurCaption) {
                mTimelineEditor.deleteSelectedTimeSpan(mTimeSpanInfoList.get(i).mTimeSpan);
                mTimeSpanInfoList.remove(i);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        mStreamingContext.stop();
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    private class CaptionTimeSpanInfo {
        private boolean isTraditional = true;
        public NvsClipCaption mCaption;
        public NvsTimelineTimeSpan mTimeSpan;

        public CaptionTimeSpanInfo(NvsClipCaption caption, NvsTimelineTimeSpan timeSpan) {
            this.mCaption = caption;
            this.mTimeSpan = timeSpan;
        }

        public boolean isTraditionanl() {
            return isTraditional;
        }

        public void setTraditional(boolean traditional) {
            isTraditional = traditional;
        }
    }

    /**
     * 获取字幕索引
     * Get the caption index
     *
     * @param curZValue
     * @return
     */
    private int getCaptionIndex(int curZValue) {
        int index = -1;
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            int zVal = mCaptionDataListClone.get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 通过字幕Z值查找字幕数据信息
     * Find caption data information by caption Z value
     *
     * @param curZValue
     * @return
     */
    private CaptionInfo getCaptionInfo(int curZValue) {
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            CaptionInfo captionInfo = mCaptionDataListClone.get(i);
            if (captionInfo != null && captionInfo.getCaptionZVal() == curZValue) {
                return captionInfo;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptionStyleButton.setClickable(true);
    }
}
