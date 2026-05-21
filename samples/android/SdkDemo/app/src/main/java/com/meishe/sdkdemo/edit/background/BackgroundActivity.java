package com.meishe.sdkdemo.edit.background;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.anim.AnimationClipAdapter;
import com.meishe.sdkdemo.edit.anim.view.AnimationBottomView;
import com.meishe.sdkdemo.edit.background.view.BackgroundBottomView;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.BackGroundInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_PATH;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2020/10/26.
 * @Description :视频编辑-背景页面(分别包含 背景颜色 背景样式  背景模糊)-Activity
 * @Description :VideoEdit-Background page (respectively include background color, background style,
 * background blur)-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BackgroundActivity extends BaseActivity {
    private static final String TAG = "BackgroundActivity";

    public final static int TYPE_BACKGROUND_COLOR = 1;
    public final static int TYPE_BACKGROUND_STYLE = 2;
    public final static int TYPE_BACKGROUND_BLUR = 3;

    private static int REQUEST_CODE_BACKGROUND = 100;
    public final static String STORYBOARD_KEY_SCALE_X = "scaleX";
    public final static String STORYBOARD_KEY_SCALE_Y = "scaleY";
    public final static String STORYBOARD_KEY_ROTATION_Z = "rotationZ";
    public final static String STORYBOARD_KEY_TRANS_X = "transX";
    public final static String STORYBOARD_KEY_TRANS_Y = "transY";
    private int mBackgroundType;
    private CustomTitleBar mTitleBar;
    private NvsTimeline mTimeline;
    private VideoFragment mVideoFragment;
    private RecyclerView mBackgroundClipRecyclerView;
    private ImageView mBackgroundFinish;
    private BackgroundBottomView mBackgroundBottomView;
    private AnimationClipAdapter mAnimationClipAdapter;
    private AnimationBottomView mAnimationBottomView;
    private NvsStreamingContext mStreamingContext;
    private LinearLayout mBackgroundColor, mBackgroundStyle, mBackgroundBlur;
    private NvsVideoTrack mVideoTrack;
    private NvsVideoClip mCurrentNvsVideoClip;
    private ClipInfo mCurrentClipInfo;

    private int mSelectedClipPosition = 0;
    private RelativeLayout mBottomLayout;


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_background;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);

        mBackgroundClipRecyclerView = (RecyclerView) findViewById(R.id.clip_list);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mBackgroundFinish = (ImageView) findViewById(R.id.background_finish);
        mBackgroundStyle = findViewById(R.id.ll_background_style);
        mBackgroundBlur = findViewById(R.id.ll_background_blur);
        mBackgroundColor = findViewById(R.id.ll_background_color);
        mAnimationBottomView = findViewById(R.id.animation_bottom);
        mBackgroundBottomView = findViewById(R.id.background_bottom_view);
        mBackgroundBottomView.setVisibility(View.GONE);

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.background);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initTimeline();
        initVideoFragment();
        initAnimationClipList();
        initCurrentClip();
        initBackgroundData();
    }


    private void initCurrentClip() {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return;
        }
        mCurrentNvsVideoClip = nvsVideoClip;

        ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        if (clipInfoData != null && clipInfoData.size() > 0) {
            mCurrentClipInfo = clipInfoData.get(mSelectedClipPosition);
        }
    }

    private void initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
    }


    private void initBackgroundData() {
        ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        if (null == clipInfoData || null == mTimeline) {
            return;
        }

        int size = clipInfoData.size();
        for (int i = 0; i < size; i++) {
            ClipInfo clipInfo = clipInfoData.get(i);
            BackGroundInfo backGroundInfo = clipInfo.getBackGroundInfo();
            if (backGroundInfo == null) {
                clipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_COLOR);
            }
            updateBackground(clipInfo, i, mTimeline);
        }
    }

    /**
     * 将clipInfo中的信息应用到timeline上
     * Apply the information in clipInfo to the timeline
     *
     * @param clipInfo
     * @param position
     * @param timeline
     */
    public void updateBackground(ClipInfo clipInfo, int position, NvsTimeline timeline) {
        if (clipInfo == null) {
            return;
        }
        NvsVideoTrack videoTrackByIndex = timeline.getVideoTrackByIndex(0);
        NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(position);

        TimelineUtil.applyBackground(clipByIndex, clipInfo.getBackGroundInfo());
    }


    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setIsBackgroundView(true);
        Bundle bundle = new Bundle();
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putBoolean("playBarVisible", true);
        mVideoFragment.setArguments(bundle);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                mVideoFragment.seekTimeline(0, 0);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {

            }

            @Override
            public void streamingEngineStateChanged(int state) {

            }
        });
        getFragmentManager().beginTransaction().add(R.id.video_layout, mVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);

    }


    private void initAnimationClipList() {
        mAnimationClipAdapter = new AnimationClipAdapter(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBackgroundClipRecyclerView.setLayoutManager(layoutManager);
        final ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        mAnimationClipAdapter.setClipInfoList(clipInfoData);
        mAnimationClipAdapter.setTimeLine(mTimeline);
        mBackgroundClipRecyclerView.setAdapter(mAnimationClipAdapter);

        mAnimationClipAdapter.setOnItemClickListener(new AnimationClipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 单端播放
                if (position >= 0 && position < clipInfoData.size()) {
                    playCurrentClip(position);

                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                    if (videoTrack != null) {
                        mCurrentNvsVideoClip = videoTrack.getClipByIndex(position);
                    }
                    mCurrentClipInfo = clipInfoData.get(position);
                    if (mBackgroundBottomView != null && mBackgroundBottomView.getVisibility() == View.VISIBLE) {
                        mBackgroundBottomView.setSelectColor(mCurrentClipInfo);
                    }
                    if (mCurrentClipInfo == null) {
                        return;
                    }

                    mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
                    mVideoFragment.setTransformViewVisible(View.VISIBLE);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSelectedClipPosition = 0;
    }

    @Override
    protected void initListener() {
        mBackgroundStyle.setOnClickListener(this);
        mBackgroundBlur.setOnClickListener(this);
        mBackgroundColor.setOnClickListener(this);

        mBackgroundFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeTimeline();
                quitActivity();
            }
        });

        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mBackgroundFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //播放片段，通过片段时间控制
                        //Play clips, control by clip time
                        if (null != mVideoFragment) {
                            playCurrentClip(mSelectedClipPosition);
                        }
                        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                    }
                }, 100);
            }
        });

        mBackgroundBottomView.setOnBackgroundBottomItemClickListener(new BackgroundBottomView.OnBackgroundBottomItemClickListener() {
            @Override
            public void onColorItemClick(View view, MultiColorInfo colorInfo) {
                //颜色背景 BackGroundColor
                if (colorInfo == null) {
                    return;
                }
                if (mCurrentClipInfo == null) {
                    return;
                }
                BackGroundInfo backGroundInfo = mCurrentClipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_COLOR);
                String colorValue = colorInfo.getColorValue();
                backGroundInfo.setColorValue(colorValue);
                mCurrentClipInfo.setBackgroundValue(colorValue);
                updateBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
            }

            @Override
            public void onStyleItemClick(View view, int position, BackgroundStyleInfo backgroundStyleInfo) {
                if (position == 0) {
                    //素材选择 selectBackgroundResource
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_PICTURE_FROM_BACKGROUND);
                    AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), SingleClickActivity.class, bundle, REQUEST_CODE_BACKGROUND);
                    return;
                }

                if (position == 1) {
                    NvsVideoResolution videoRes = mTimeline.getVideoRes();
                    if (videoRes == null) {
                        return;
                    }
                    setDefaultBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
                    updateBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
                    return;
                }
                String filePath = "assets:/background/image/" + backgroundStyleInfo.getFilePath();
                if (!backgroundStyleInfo.isAssets()) {
                    filePath = backgroundStyleInfo.getFilePath();
                }
                BackGroundInfo backGroundInfo = mCurrentClipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_TYPE);
                backGroundInfo.setFilePath(filePath);
                mCurrentClipInfo.setBackgroundValue(filePath);
                updateBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
            }

            @Override
            public void onBlurItemClick(View view, float strength) {
                BackGroundInfo backGroundInfo = mCurrentClipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_BLUR);
                backGroundInfo.setValue(strength);
                mCurrentClipInfo.setBackgroundValue(strength + "");
                updateBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
            }

            @Override
            public void onConfirmClick() {
                mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
                mVideoFragment.setTransformViewVisible(View.GONE);
            }

            @Override
            public void onStyleApplyAll(String filePath) {
                if (TextUtils.isEmpty(filePath)) {
                    return;
                }
                if (mTimeline == null) {
                    return;
                }

                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                if (videoTrack == null) {
                    return;
                }

                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                if (clipInfoData != null && clipInfoData.size() > 0) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        ClipInfo clipInfo = clipInfoData.get(i);
                        if (clipInfo == mCurrentClipInfo) {
                            continue;
                        }
                        BackGroundInfo backGroundInfo = mCurrentClipInfo.getBackGroundInfo();
                        if (backGroundInfo != null) {
                            clipInfo.buildBackgroundInfo(backGroundInfo.getType());
                            clipInfo.getBackGroundInfo().setFilePath(backGroundInfo.getFilePath());
                            updateBackground(clipInfo, videoTrack.getClipByIndex(i));
                        }
                    }
                }
            }

            @Override
            public void onColorApplyAll(String colorValue) {
                if (TextUtils.isEmpty(colorValue)) {
                    return;
                }

                if (mTimeline == null) {
                    return;
                }

                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                if (videoTrack == null) {
                    return;
                }

                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                if (clipInfoData != null && clipInfoData.size() > 0) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        ClipInfo clipInfo = clipInfoData.get(i);
                        if (clipInfo == mCurrentClipInfo) {
                            continue;
                        }
                        BackGroundInfo backGroundInfo = mCurrentClipInfo.getBackGroundInfo();
                        if (backGroundInfo != null) {
                            clipInfo.buildBackgroundInfo(backGroundInfo.getType());
                            clipInfo.getBackGroundInfo().setColorValue(backGroundInfo.getColorValue());
                            updateBackground(clipInfo, videoTrack.getClipByIndex(i));
                        }
                    }
                }

            }

            @Override
            public void onBlurApplyAll() {

                if (mTimeline == null) {
                    return;
                }

                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                if (videoTrack == null) {
                    return;
                }

                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                if (clipInfoData != null && clipInfoData.size() > 0) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        ClipInfo clipInfo = clipInfoData.get(i);
                        if (clipInfo == mCurrentClipInfo) {
                            continue;
                        }
                        BackGroundInfo backGroundInfo = mCurrentClipInfo.getBackGroundInfo();
                        if (backGroundInfo != null) {
                            clipInfo.buildBackgroundInfo(backGroundInfo.getType());
                            clipInfo.getBackGroundInfo().setValue(backGroundInfo.getValue());
                            updateBackground(clipInfo, videoTrack.getClipByIndex(i));
                        }
                    }
                }
            }
        });


        mVideoFragment.setOnBackgroundChangedListener(new VideoFragment.OnBackgroundChangedListener() {
            @Override
            public void onBackgroundChanged() {
                updateBackground();
            }
        });

    }

    /**
     * 应用完成后离开当前activity
     * Leave the current activity after the background set is complete
     */
    private void quitActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }


    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }


    @Override
    public void onClick(View view) {
        mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
        mVideoFragment.setTransformViewVisible(View.VISIBLE);

        int id = view.getId();
        if (id == R.id.ll_background_color) {
            mBackgroundType = TYPE_BACKGROUND_COLOR;
            mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_COLOR);
            mBackgroundBottomView.setSelectColor(mCurrentClipInfo);
        } else if (id == R.id.ll_background_blur) {
            mBackgroundType = TYPE_BACKGROUND_BLUR;
            mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_BLUR);
            mBackgroundBottomView.setSelectBlur(mCurrentClipInfo);
        } else if (id == R.id.ll_background_style) {
            mBackgroundType = TYPE_BACKGROUND_STYLE;
            mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_STYLE);
            mBackgroundBottomView.setSelectStyle(mCurrentClipInfo);
        }
    }

    private void setDefaultBackgroundIfNull() {
        if (mCurrentClipInfo == null) {
            return;
        }
        BackGroundInfo backGroundInfo = mCurrentClipInfo.getBackGroundInfo();
        if (backGroundInfo == null) {
            mCurrentClipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_COLOR);
        }
    }


    /**
     * 播放视频
     * Play video
     */
    private void playCurrentClip(int mSelectedClipPosition) {
        int clipCount = mTimeline.getVideoTrackByIndex(0).getClipCount();
        long playStartPoint = 0;
        long playEndPoint = 0;
        if (mSelectedClipPosition >= 0 && mSelectedClipPosition < clipCount) {
            playStartPoint = getClipStartTime(mSelectedClipPosition);
            playEndPoint = getClipEndTime(mSelectedClipPosition);
            if (playEndPoint > playStartPoint) {
                if (mSelectedClipPosition > 0) {
                    playStartPoint -= 0.5 * 1000 * 1000;
                    playEndPoint -= 0.5 * 1000 * 1000;
                }
                mVideoFragment.setmPlaySeekBarMaxAndCurrent(playStartPoint, playEndPoint, playStartPoint, mTimeline.getDuration());
                mVideoFragment.playVideoButtonClick(playStartPoint, playEndPoint);
            }
        }
        else if (mSelectedClipPosition == -1) {
            playStartPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            playEndPoint = mTimeline.getDuration();
            mVideoFragment.setmPlaySeekBarMaxAndCurrent(0, playEndPoint, playStartPoint, playEndPoint);
            if (playEndPoint > playStartPoint) {
                mVideoFragment.playVideoButtonClick(playStartPoint, playEndPoint);
            }
        }
    }


    /**
     * 获取当前选择的片段的起始位置
     * Get the starting position of the currently selected segment
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipStartTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipInPoint = nvsVideoClip.getInPoint();
        return clipInPoint;
    }

    /**
     * 获取当前选择的片段的起始位置
     * Get the starting position of the currently selected segment
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipEndTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipOutPoint = nvsVideoClip.getOutPoint();
        return clipOutPoint;
    }


    public void updateBackground() {
        if (mCurrentClipInfo == null) {
            return;
        }
        TimelineUtil.applyBackground(mCurrentNvsVideoClip, mCurrentClipInfo.getBackGroundInfo());
        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }


    /**
     * 设置默认的背景色
     * Set the default background color
     *
     * @param clipInfo
     * @param videoClip
     */
    public void setDefaultBackground(ClipInfo clipInfo, NvsVideoClip videoClip) {

        if (clipInfo == null || videoClip == null) {
            return;
        }
        BackGroundInfo backGroundInfo = clipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_COLOR);
        backGroundInfo.setColorValue("#000000");
        TimelineUtil.applyBackground(videoClip, clipInfo.getBackGroundInfo());
        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }


    private void updateBackground(ClipInfo clipInfo, NvsVideoClip nvsVideoClip) {
        if (clipInfo == null || nvsVideoClip == null || clipInfo.getBackGroundInfo() == null) {
            return;
        }
        TimelineUtil.applyBackground(nvsVideoClip, clipInfo.getBackGroundInfo());
        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BACKGROUND) {
            if (data != null) {
                String filePath = data.getStringExtra(SINGLE_PICTURE_PATH);
                if (!TextUtils.isEmpty(filePath)) {
                    String fileName = filePath;
                    File file = new File(fileName);
                    if (!file.exists()) {
                        return;
                    }
                    BackGroundInfo backGroundInfo = mCurrentClipInfo.buildBackgroundInfo(BackGroundInfo.BackgroundType.BACKGROUND_TYPE);
                    backGroundInfo.setFilePath(filePath);
                    mCurrentClipInfo.setBackgroundValue(filePath);
                    updateBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
                    mBackgroundBottomView.addBackgroundStyle(filePath);
                }
            }
        }
    }
}