package com.meishe.sdkdemo.edit.filter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate : 2019/10/29.
 * @Description :视频编辑-编辑-单段滤镜-Activity
 * @Description :VideoEdit-Filter-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ClipFilterActivity extends BaseFilterActivity {
    private static final String TAG = "ClipFilterActivity";
    private static final String FILTER_INTENSITY = "Filter Intensity";
    private ClipInfo mClipInfo;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mClipIndex;
    private RelativeLayout mAddKeyFrameBtn;
    private LinearLayout mFilterPanel;
    private LinearLayout mFilterKeyFramePanel;
    private FilterKeyFrameView mFilterKeyFrameView;
    private ImageView mKeyFrameFinishView;
    private AppCompatTextView mAddKeyFrameHint;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_filter_clip;
    }

    @Override
    protected VideoClipFxInfo initClipFxInfo() {
        VideoClipFxInfo videoClipFxData = mClipInfo.getVideoClipFxInfo();
        if (videoClipFxData == null) {
            videoClipFxData = new VideoClipFxInfo();
        }
        return videoClipFxData;
    }

    @Override
    protected void initSubViews() {
        super.initSubViews();
        mAddKeyFrameBtn = findViewById(R.id.add_keyframe_title_view);
        mAddKeyFrameBtn.setOnClickListener(this);
        mFilterPanel = findViewById(R.id.filter_panel_rv);
        mFilterKeyFramePanel = findViewById(R.id.filter_key_frame_panel_rv);
        mFilterKeyFrameView = findViewById(R.id.filter_key_frame_view);
        mKeyFrameFinishView = findViewById(R.id.filter_key_frame_finish_view);
        mKeyFrameFinishView.setOnClickListener(this);
        mAddKeyFrameHint = findViewById(R.id.inline_add_keyframe_text_hint);
    }

    private void changeAddKeyFrameTitleViewIcon(boolean hasKeyFrame) {
        if (hasKeyFrame) {
            mAddKeyFrameHint.setText(R.string.clip_edit_key_frame_text);
            mAddKeyFrameHint.setTextColor(getResources().getColor(R.color.color_yellow_a5));
        } else {
            mAddKeyFrameHint.setText(R.string.clip_add_key_frame_text);
            mAddKeyFrameHint.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected NvsTimeline initTimeLine() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mClipIndex = BackupData.instance().getClipIndex();
        Logger.e(TAG, "initTimeLine ->  mClipIndex = " + mClipIndex);
        mClipInfo = mClipArrayList.get(mClipIndex);
        NvsTimeline timeline = TimelineUtil.createSingleClipTimeline(mClipInfo, true);
        TimelineUtil.buildSingleClipFilter(timeline, mClipInfo, mClipInfo.getVideoClipFxInfo());
        return timeline;
    }

    @Override
    protected void afterIntentInit() {
        super.afterIntentInit();
        initKeyFrameView(mTimeline);
        if (mSelectedPos == 0) {
            mAddKeyFrameBtn.setVisibility(View.INVISIBLE);
        } else {
            mAddKeyFrameBtn.setVisibility(View.VISIBLE);
        }
        if ((mVideoClipFxInfo != null) && (mVideoClipFxInfo.getKeyFrameInfoMap() != null) && (mVideoClipFxInfo.getKeyFrameInfoMap().size() > 0)) {
            mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
            changeAddKeyFrameTitleViewIcon(true);
        } else {
            if ((null != mVideoClipFxInfo) && mVideoClipFxInfo.getIsAdjusted() == 1) {
                mAddKeyFrameBtn.setVisibility(View.INVISIBLE);
            }
            changeAddKeyFrameTitleViewIcon(false);
        }
    }

    @Override
    protected void playbackTimelinePositionFromParent(NvsTimeline timeline, long stamp) {
        super.playbackTimelinePositionFromParent(timeline, stamp);
        mFilterKeyFrameView.scrollSequenceViewTo(stamp);
    }

    @Override
    protected void streamingEngineStateChangedFromParent(int state) {
        super.streamingEngineStateChangedFromParent(state);
        if (state != NvsStreamingContext.STREAMING_ENGINE_STATE_SEEKING) {
            mFilterKeyFrameView.setSequenceViewIsSeekingStatus(false);
        }
    }

    private void initKeyFrameView(NvsTimeline timeline) {
        mFilterKeyFrameView.initKeyFrameView(mClipInfo.getFilePath(), timeline, mVideoClipFxInfo.getKeyFrameInfoMap());
        mFilterKeyFrameView.setOnKeyFrameViewClickListener(new FilterKeyFrameView.OnKeyFrameViewClickListener() {
            @Override
            public void addFrameClick() {
                mStreamingContext.stop();
                mVideoClipFxInfo.putKeyFrameInfo(mStreamingContext.getTimelineCurrentPosition(mTimeline), getFxIntensityAndApplyKeyFrame());
                mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
            }

            @Override
            public void nextFrameClick() {
                NvsFx fxFromClip = getFxFromClip();
                if (fxFromClip != null) {
                    long nextKeyFrame = fxFromClip.findKeyframeTime(FILTER_INTENSITY, mStreamingContext.getTimelineCurrentPosition(mTimeline),
                            NvsFx.KEY_FRAME_FIND_MODE_INPUT_TIME_AFTER);
                    if (nextKeyFrame == -1) {
                        mFilterKeyFrameView.setNextViewEnable(false);
                    } else {
                        mFilterKeyFrameView.setNextViewEnable(true);
                        mFilterKeyFrameView.setSequenceViewIsSeekingStatus(true);
                        mStreamingContext.seekTimeline(mTimeline, nextKeyFrame, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
                        mFilterKeyFrameView.scrollSequenceViewTo(nextKeyFrame);
                    }
                }
            }

            @Override
            public void beforeFrameClick() {
                NvsFx fxFromClip = getFxFromClip();
                if (fxFromClip != null) {
                    long beforeKeyFrame = fxFromClip.findKeyframeTime(FILTER_INTENSITY, mStreamingContext.getTimelineCurrentPosition(mTimeline),
                            NvsFx.KEY_FRAME_FIND_MODE_INPUT_TIME_BEFORE);
                    if (beforeKeyFrame == -1) {
                        mFilterKeyFrameView.setBeforeViewEnable(false);
                    } else {
                        mFilterKeyFrameView.setBeforeViewEnable(true);
                        mFilterKeyFrameView.setSequenceViewIsSeekingStatus(true);
                        mStreamingContext.seekTimeline(mTimeline, beforeKeyFrame, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
                        mFilterKeyFrameView.scrollSequenceViewTo(beforeKeyFrame);
                    }
                }
            }

            @Override
            public void deleteFrameClick(long deleteStamp) {
                NvsFx fxFromClip = getFxFromClip();
                if (fxFromClip != null) {
                    fxFromClip.removeKeyframeAtTime(FILTER_INTENSITY, deleteStamp);
                    mVideoClipFxInfo.getKeyFrameInfoMap().remove(deleteStamp);
                    mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
                }
            }

            @Override
            public void onProgressChanged(boolean needAddKeyFrame, long currentStamp, double intensity) {
                if (needAddKeyFrame) {
                    mVideoClipFxInfo.putKeyFrameInfo(currentStamp, intensity);
                    mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
                } else {
                    mVideoClipFxInfo.putKeyFrameInfo(currentStamp, intensity);
                    NvsFx fxFromClip = getFxFromClip();
                    fxFromClip.setFloatValAtTime(FILTER_INTENSITY, intensity, currentStamp);
                }
                mVideoFragment.seekTimeline(currentStamp, 0);
            }
        });
        mFilterKeyFrameView.setOnSequenceScrollChangeListener(new FilterKeyFrameView.OnSequenceScrollChangeListener() {
            @Override
            public void onScrollX(long currentTimeStamp) {
                mVideoFragment.seekTimeline(currentTimeStamp, 0);
            }
        });
    }

    private NvsFx getFxFromClip() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }

            int fxCount = videoClip.getRawFxCount();
            for (int j = 0; j < fxCount; j++) {
                NvsVideoFx fx = videoClip.getRawFxByIndex(j);
                if (fx == null) {
                    continue;
                }

                String name = fx.getBuiltinVideoFxName();
                if (name == null) {
                    continue;
                }
                if (name.equals(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST)
                        || name.equals(Constants.ADJUST_TYPE_SHARPEN)
                        || name.equals(Constants.ADJUST_TYPE_VIGETTE)
                        || name.equals(Constants.ADJUST_TYPE_TINT)
                        || name.equals(Constants.ADJUST_TYPE_DENOISE)
                        || name.equals(Constants.FX_TRANSFORM_2D)) {
                    continue;
                }
                return fx;
            }
        }
        return null;
    }

    private float getFxIntensityAndApplyKeyFrame() {
        NvsFx fx = getFxFromClip();
        if (fx != null) {
            float filter_intensity = (float) fx.getFloatValAtTime(FILTER_INTENSITY, mStreamingContext.getTimelineCurrentPosition(mTimeline));
            fx.setFloatValAtTime(FILTER_INTENSITY, filter_intensity, mStreamingContext.getTimelineCurrentPosition(mTimeline));
            return filter_intensity;
        }
        return 1.0f;
    }

    @Override
    protected void onFilterChanged(int position) {
        super.onFilterChanged(position);
        if (position == 0) {
            mAddKeyFrameBtn.setVisibility(View.INVISIBLE);
        } else {
            mAddKeyFrameBtn.setVisibility(View.VISIBLE);
            changeAddKeyFrameTitleViewIcon(false);
            mFilterView.setIntensityLayoutVisible(View.VISIBLE);
            boolean isAdjust = (mVideoClipFxInfo.getIsAdjusted() == 1);
            mAddKeyFrameBtn.setVisibility(isAdjust ? View.INVISIBLE : View.VISIBLE);
            mAdjustSpecialEffectsView.setVisibility(isAdjust ? View.VISIBLE : View.INVISIBLE);
            mFilterView.setIntensityLayoutVisible(isAdjust ? View.INVISIBLE : View.VISIBLE);
        }
        // 切换滤镜 移除之前的关键帧信息
        // Switch filter to remove previous key frame information
        mVideoClipFxInfo.getKeyFrameInfoMap().clear();
        mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
    }

    @Override
    protected void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter) {
        TimelineUtil.buildSingleClipFilter(timeline, mClipInfo, changedClipFilter);
    }

    @Override
    protected boolean isNeedShowSeekBarWhenChangeFilterFromParent() {
        if (mVideoClipFxInfo != null && mVideoClipFxInfo.getKeyFrameInfoMap() != null && mVideoClipFxInfo.getKeyFrameInfoMap().keySet().size() > 0) {
            return false;
        } else {
            return super.isNeedShowSeekBarWhenChangeFilterFromParent();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_keyframe_title_view) {// 处理 关键帧布局的显示和隐藏
            // Processing the display and hiding of the key frame layout
            mFilterKeyFrameView.setCurrentFx(getFxFromClip());
            mFilterPanel.setVisibility(View.GONE);
            mFilterKeyFramePanel.setVisibility(View.VISIBLE);
            mVideoFragment.setPlaySeekVisiable(false);
        } else if (id == R.id.filterAssetFinish) {
            mClipInfo.setVideoClipFxInfo(mVideoClipFxInfo);
            mClipArrayList.set(mClipIndex, mClipInfo);
            BackupData.instance().setClipInfoData(mClipArrayList);
            TimelineData.instance().setClipInfoData(mClipArrayList);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            quitActivity();
        } else if (id == R.id.filter_key_frame_finish_view) {// 处理完关键帧
            // Processed key frames
            mFilterPanel.setVisibility(View.VISIBLE);
            mFilterKeyFramePanel.setVisibility(View.GONE);
            HashMap<Long, Double> keyFrameInfoMap = mVideoClipFxInfo.getKeyFrameInfoMap();
            if (keyFrameInfoMap.size() > 0) {
                //mAdjustSpecialEffectsView.set
                mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                changeAddKeyFrameTitleViewIcon(true);
            } else {
                if (mVideoClipFxInfo.getIsAdjusted() == 1) {
                    mAdjustSpecialEffectsView.setVisibility(View.VISIBLE);
                } else {
                    mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                }
                changeAddKeyFrameTitleViewIcon(false);
            }
            mVideoFragment.setPlaySeekVisiable(true);
        }
    }
}
