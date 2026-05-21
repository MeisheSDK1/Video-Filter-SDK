package com.meishe.sdkdemo.edit.animatesticker;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsAnimatedSticker;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineTimeSpan;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.CustomViewPager;
import com.meishe.sdkdemo.edit.view.dialog.TipsDialog;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.meishe.sdkdemo.utils.dataInfo.StickerInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : unKnow
 * @CreateDate : 2021/4/20.
 * @Description :贴纸Activity
 * @Description :Sticker Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AnimatedStickerActivity extends BaseActivity {
    private static final String TAG = "AnimatedStickerActivity";
    private static final int ANIMATESTICKERREQUESTLIST = 104;
    private static final int VIDEOPLAYTOEOF = 105;
    private static final int ANIMATED_STICKER_REQUEST_STYLE = 106;
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private RelativeLayout mZoomOutButton;
    private RelativeLayout mZoomInButton;
    private TextView mCurrentPlaytime;
    private ImageView mVideoPlay;
    private NvsTimelineEditor mTimelineEditor;
    private ImageView mAddAnimateStickerButton;
    private ImageView mStickerFinish;

    private RelativeLayout mAnimateStickerAssetLayout;
    private ImageView mMoreDownload;
    private TabLayout mAnimateStickerTypeTab;
    private CustomViewPager mViewPager;
    private ImageView mStickerAssetFinish;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private VideoFragment mVideoFragment;
    private NvsMultiThumbnailSequenceView mMultiThumbnailSequenceView;
    private boolean mIsSeekTimeline = true;
    private AnimatedStickerActivity.AnimateStickerHandler m_handler = new AnimatedStickerActivity.AnimateStickerHandler(this);
    private List<AnimatedStickerActivity.AnimateStickerTimeSpanInfo> mTimeSpanInfoList = new ArrayList<>();
    private NvsTimelineAnimatedSticker mCurSelectAnimatedSticker;
    ArrayList<StickerInfo> mStickerDataListClone;
    //
    private ArrayList<String> mStickerAssetTypeList;
    /*
     * 总的贴纸列表
     * List of total stickers
     * */
    private ArrayList<NvAsset> mTotalStickerAssetList;
    /*
     * 自定义贴纸列表
     * Custom sticker list
     * */
    private ArrayList<NvAssetManager.NvCustomStickerInfo> mCustomStickerAssetList;
    private ArrayList<AnimateStickerListFragment> mAssetFragmentsArray = new ArrayList<>();
    private AnimateStickerListFragment mStickerListFragment;
    private AnimateStickerListFragment mCustomStickerListFragment;
    private NvAssetManager mAssetManager;
    private int mAssetType = NvAsset.ASSET_ANIMATED_STICKER;
    private long mInPoint = 0;
    private long mStickerDuration = 0;
    /*
     * 记录当前tab页
     * Record the current tab page
     * */
    private int mCurTabPage = 0;
    /*
     * 记录上次操作的Tab
     * Record the last operation tab
     * */
    private int mPrevTabPage = 0;

    /*
     * 新添加贴纸对象
     * Newly added sticker object
     * */
    private NvsTimelineAnimatedSticker mAddAnimateSticker = null;
    private int mCurSelectedPos = -1;
    private boolean isNewStickerUuidItemClick = false;
    private String mSelectUuid = "";
    private int mCurAnimateStickerZVal = 0;
    private StringBuilder mShowCurrentDuration = new StringBuilder();
    private View stickerAddFrameView;
    private ImageView ivAddFrame;
    private View stickerRemoveAllFrameView;
    private View removeFirst;//辅助删除所有关键帧隐藏用
    private LinearLayout mFrameOperationWrapperLayout;
    private ImageView mKeyFrameFinishView;
    private TextView mBeforeKeyFrameView;
    private TextView mAddDeleteKeyFrameView;
    private TextView mNextKeyFrameView;
    private boolean mCurrentStatusIsKeyFrame = false;
    private View stickerAnimal;
    private TipsDialog mDeleteAllKeyFrameTips;

    static class AnimateStickerHandler extends Handler {
        WeakReference<AnimatedStickerActivity> mWeakReference;

        public AnimateStickerHandler(AnimatedStickerActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final AnimatedStickerActivity activity = mWeakReference.get();
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

    /*
     * 贴纸与timeSpan类，存储添加的贴纸跟TimeSpan
     * Stickers and timeSpan class, store added stickers and TimeSpan
     * */
    private class AnimateStickerTimeSpanInfo {
        public NvsTimelineAnimatedSticker mAnimateSticker;
        public NvsTimelineTimeSpan mTimeSpan;

        public AnimateStickerTimeSpanInfo(NvsTimelineAnimatedSticker sticker, NvsTimelineTimeSpan timeSpan) {
            this.mAnimateSticker = sticker;
            this.mTimeSpan = timeSpan;
        }
    }

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_animate_sticker;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mZoomOutButton = (RelativeLayout) findViewById(R.id.zoomOut);
        mZoomInButton = (RelativeLayout) findViewById(R.id.zoomIn);
        mCurrentPlaytime = (TextView) findViewById(R.id.currentPlaytime);
        mVideoPlay = (ImageView) findViewById(R.id.videoPlay);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.timelineEditor);
        mAddAnimateStickerButton = (ImageView) findViewById(R.id.addAnimateStickerButton);
        mStickerFinish = (ImageView) findViewById(R.id.stickerFinish);
        mMultiThumbnailSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();

        mAnimateStickerAssetLayout = (RelativeLayout) findViewById(R.id.animateStickerAsset_layout);
        mMoreDownload = (ImageView) findViewById(R.id.moreDownload);
        mAnimateStickerTypeTab = (TabLayout) findViewById(R.id.animateStickerTypeTab);
        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mViewPager.setPagingEnabled(false);
        mStickerAssetFinish = (ImageView) findViewById(R.id.stickerAssetFinish);
        // keyFrame 关键帧
        stickerAddFrameView = findViewById(R.id.animateStickerKeyFrameButton);
        ivAddFrame = findViewById(R.id.iv_sticker_add_frame);
        stickerAddFrameView.setOnClickListener(this);
        stickerRemoveAllFrameView = findViewById(R.id.removeAllKeyFrameButton);
        removeFirst = findViewById(R.id.remove_first);
        stickerRemoveAllFrameView.setOnClickListener(this);
        mFrameOperationWrapperLayout = findViewById(R.id.key_frame_operation_wrapper_layout);
        mKeyFrameFinishView = findViewById(R.id.keyFrameFinishView);
        mKeyFrameFinishView.setOnClickListener(this);
        mBeforeKeyFrameView = findViewById(R.id.before_key_frame_view);
        mBeforeKeyFrameView.setOnClickListener(this);
        mAddDeleteKeyFrameView = findViewById(R.id.add_delete_frame_view);
        mAddDeleteKeyFrameView.setTag(0);
        mAddDeleteKeyFrameView.setOnClickListener(this);
        mNextKeyFrameView = findViewById(R.id.next_key_frame_view);
        mNextKeyFrameView.setOnClickListener(this);
        stickerAnimal = findViewById(R.id.sticker_animal);
        stickerAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StickerInfo currentStickerInfo = getCurrentStickerInfo();
                if (currentStickerInfo != null) {
                    // 跳转贴纸边界页面 1.停止播放 2.保存信息3.跳转
                    //Jump to sticker boundary page 1. Stop playing 2. Save information 3. Jump
                    long currentTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    seekTimeline(currentTime);
                    NvsAnimatedSticker curAnimateSticker = mVideoFragment.getCurAnimateSticker();
                    if (curAnimateSticker != null && curAnimateSticker instanceof NvsTimelineAnimatedSticker) {
                        mCurSelectAnimatedSticker = (NvsTimelineAnimatedSticker) curAnimateSticker;
                    } else {
                        mCurSelectAnimatedSticker = null;
                    }
                    if (mCurSelectAnimatedSticker != null) {
                        if (currentTime < mCurSelectAnimatedSticker.getInPoint() || currentTime > mCurSelectAnimatedSticker.getOutPoint()) {
                            currentTime = mCurSelectAnimatedSticker.getInPoint();
                        }
                        BackupData.instance().setStickerZVal((int) mCurSelectAnimatedSticker.getZValue());
                        BackupData.instance().setCurSeekTimelinePos(currentTime);
                        BackupData.instance().setAnimateStickerData(mStickerDataListClone);
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                AnimatedStickerAnimationActivity.class, null, ANIMATED_STICKER_REQUEST_STYLE);
                    }
                }
            }
        });
    }

    //根据boolean切换关键帧展示view的状态
    //Switch key frames according to boolean to show the state of the view
    private void setAddDeleteViewStatus(boolean isAddStatus) {
        if (isAddStatus) {
            mAddDeleteKeyFrameView.setTag(0);
            mAddDeleteKeyFrameView.setText(R.string.key_frame_add_frame_text);
            mAddDeleteKeyFrameView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_add_frame_selector), null, null);
        } else {
            mAddDeleteKeyFrameView.setTag(1);
            mAddDeleteKeyFrameView.setText(R.string.key_frame_delete_frame_text);
            mAddDeleteKeyFrameView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_delete_frame_selector), null, null);
        }
    }


    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.animatedSticker);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    private void changeAddAndFinishViewStatus(int visibility) {
        stickerAddFrameView.setVisibility(visibility);
        mAddAnimateStickerButton.setVisibility(visibility);
        stickerAnimal.setVisibility(visibility);
        mStickerFinish.setVisibility(visibility);
    }

    @Override
    protected void initData() {
        if (!initAssetData()) {
            return;
        }
        setPlaytimeText(0);
        initMultiSequence();
        /*
         * 添加所有贴纸
         * Add all stickers
         * */
        addAllTimeSpan();
        initVideoFragment();

        /*
         * 初始化素材列表
         * Initialize material list
         * */
        initAnimateStickerDataList();
        initCustomAssetsDataList();
        initTabLayout();
        /*
         * gif转caf图需要这个贴纸模板
         * gif to caf chart needs this sticker template
         * */
        gifToCafStickerTemplateinstall();
    }

    @Override
    protected void initListener() {
        mZoomOutButton.setOnClickListener(this);
        mZoomInButton.setOnClickListener(this);
        mVideoPlay.setOnClickListener(this);
        mAddAnimateStickerButton.setOnClickListener(this);
        mStickerFinish.setOnClickListener(this);
        mMoreDownload.setOnClickListener(this);
        mStickerAssetFinish.setOnClickListener(this);
        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                Log.d(TAG, "setOnScrollListener timeStamp:" + timeStamp);
                // 更新所有按钮的状态
                //Update the state of all buttons
                if ((mCurSelectAnimatedSticker != null) && mCurrentStatusIsKeyFrame) {
                    Map<Long, KeyFrameInfo> keyFrameInfoHashMap = getCurrentStickerInfo().getKeyFrameInfoHashMap();
                    updateAllKeyFrameViewStatusWhenScroll(timeStamp, keyFrameInfoHashMap);
                    mVideoFragment.setDrawRectVisible(View.GONE);
                }
                // 如果现在是关键帧模式的话[即:预览模式下 存在关键帧信息]，则不允许进行平移缩放编辑
                // If you are in key frame mode [ie: key frame information exists in preview mode], pan and zoom editing is not allowed
                if ((mCurSelectAnimatedSticker != null) && !mCurrentStatusIsKeyFrame) {
                    StickerInfo currentStickerInfo = getCurrentStickerInfo();
                    if (currentStickerInfo != null) {
                        if (mIsSeekTimeline) {
                            mVideoFragment.setDrawRectVisible(View.VISIBLE);
                        } else {
                            mVideoFragment.setDrawRectVisible(View.GONE);
                        }
                    }
                }
                if (!mCurrentStatusIsKeyFrame) {
                    // 更新所有贴纸的状态
                    // Update the status of all stickers
                    updateAllStickerPos();
                }
                if (!mIsSeekTimeline) {
                    // 播放过程中不进行选中操作
                    //Do not select operation during playback
                    return;
                }
                // seek 过程中起作用
                if (mCurSelectAnimatedSticker != null) {
                    StickerInfo currentStickerInfo = getCurrentStickerInfo();
                    if (timeStamp > mCurSelectAnimatedSticker.getOutPoint() || timeStamp < mCurSelectAnimatedSticker.getInPoint()) {
                        // 当前贴纸时间之外隐藏框 禁用所有关键帧按钮
                        //Hide the frame outside the current sticker time Disable all keyframe buttons
                        mVideoFragment.setDrawRectVisible(View.GONE);
                        mAddDeleteKeyFrameView.setEnabled(false);
                        mBeforeKeyFrameView.setEnabled(false);
                        mNextKeyFrameView.setEnabled(false);
                    } else {
                        if (mCurrentStatusIsKeyFrame) {
                            // seek到关键帧的位置的时候，选中一下当前的关键帧，使以后所有的操作，在当前帧下操作.
                            //When seek to the position of the key frame, select the current key frame to make all subsequent operations operate under the current frame.
                            boolean hasKeyFrame = false;
                            long currentKeyFrameStamp = -1;
                            if (currentStickerInfo != null) {
                                Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
                                Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                                for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                                    if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                                        hasKeyFrame = true;
                                        currentKeyFrameStamp = entry.getKey();
                                        break;
                                    }
                                }
                            }
                            if (hasKeyFrame) {
                                mVideoFragment.setDrawRectVisible(View.VISIBLE);
                            }
                            //setCurrentKeyFrameTime可以满足刷新在两个关键帧之间获取方框的问题，不需要添加关键帧并获取后删除
                            //setCurrentKeyFrameTime can meet the problem of refreshing to obtain the box between two key frames,
                            // there is no need to add key frames and delete them after obtaining
                            mCurSelectAnimatedSticker.setCurrentKeyFrameTime(timeStamp - mCurSelectAnimatedSticker.getInPoint());
                            updateStickerBoundingRect();

                            mAddDeleteKeyFrameView.setEnabled(true);
                            mVideoFragment.setDrawRectVisible(View.VISIBLE);
                        }
                    }
                }
                if (mTimeline != null) {
                    seekTimeline((mAnimateStickerAssetLayout.getVisibility() == View.VISIBLE) ? mInPoint : timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                    setPlaytimeText(timeStamp);
                    selectAnimateStickerAndTimeSpan(true);
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mTimelineEditor.setTimelineScaleForSeek(mTimeline, mTimelineEditor.getDurationWidth());
                } else if (action == MotionEvent.ACTION_UP | action == MotionEvent.ACTION_CANCEL) {
                    seekTimeline(NvsStreamingContext.getInstance().getTimelineCurrentPosition(mTimeline));
                }
                mIsSeekTimeline = true;
                return false;
            }
        });

        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
                if (isNewStickerUuidItemClick) {
                    return;
                }
                updateStickerBoundingRect();
                selectAnimateStickerAndTimeSpan(true);
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                mVideoFragment.setDrawRectVisible(View.GONE);
                if (mAnimateStickerAssetLayout.getVisibility() != View.VISIBLE) {
                    setPlaytimeText(stamp);
                    if (!mCurrentStatusIsKeyFrame) {
                        mTimelineEditor.unSelectAllTimeSpan();
                    }
                    multiThumbnailSequenceViewSmooth(stamp);
                }
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mVideoPlay.setBackgroundResource(R.mipmap.icon_edit_pause);
                    mIsSeekTimeline = false;
                    mAssetFragmentsArray.get(mCurTabPage).setIsStickerInPlay(true);
                } else {
                    mVideoPlay.setBackgroundResource(R.mipmap.icon_edit_play);
                    mIsSeekTimeline = true;
                    int tapCount = mAssetFragmentsArray.size();
                    for (int index = 0; index < tapCount; ++index) {
                        mAssetFragmentsArray.get(index).setIsStickerInPlay(false);
                    }
                    selectAnimateStickerAndTimeSpan(false);
                }
                if (mAnimateStickerAssetLayout.getVisibility() == View.VISIBLE) {
                    int tapCount = mAssetFragmentsArray.size();
                    for (int index = 0; index < tapCount; ++index) {
                        mAssetFragmentsArray.get(index).notifyDataSetChanged();
                    }
                }
            }
        });
        mVideoFragment.setBeforeAnimateStickerEditListener(new VideoFragment.IBeforeAnimateStickerEditListener() {
            @Override
            public boolean beforeTransitionCouldDo() {
                if (mCurSelectAnimatedSticker == null) {
                    return false;
                }
                if (!mCurrentStatusIsKeyFrame) {
                    boolean b = ifCouldEditAnimateSticker();
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
                if (mCurSelectAnimatedSticker == null) {
                    return false;
                }
                if (!mCurrentStatusIsKeyFrame) {
                    boolean b = ifCouldEditAnimateSticker();
                    if (!b) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            }
        });
        mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
            @Override
            public void onAssetDelete() {
                if (mCurrentStatusIsKeyFrame) {
                    ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_when_delete_animate_sticker));
                    return;
                }
                int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                int stickerIndex = getAnimateStickerIndex(zVal);
                if (stickerIndex >= 0) {
                    // 贴纸信息已经移除 包括了关键帧信息
                    // The sticker information has been removed, including key frame information
                    mStickerDataListClone.remove(stickerIndex);
                }
                mAddAnimateSticker = null;
                deleteAnimateSticker();

                /*
                 * 取消所有Tab页贴纸选中的状态
                 * Deselect all tab page stickers
                 * */
                mCurSelectedPos = -1;
                int tabCount = mAssetFragmentsArray.size();
                for (int index = 0; index < tabCount; ++index) {
                    mAssetFragmentsArray.get(index).setSelectedPos(mCurSelectedPos);
                    mAssetFragmentsArray.get(index).notifyDataSetChanged();
                }
                // 如果是在关键帧页面删除了贴纸 则更新所有关键帧按钮的状态
                // If the sticker is deleted on the key frame page, update the state of all key frame buttons
                mAddDeleteKeyFrameView.setEnabled(false);
                mBeforeKeyFrameView.setEnabled(false);
                mNextKeyFrameView.setEnabled(false);
            }

            @Override
            public void onAssetSelected(PointF curPoint) {
                if ((mAnimateStickerAssetLayout.getVisibility() == View.VISIBLE) || mCurrentStatusIsKeyFrame) {
                    return;
                }
                mVideoFragment.selectAnimateStickerByHandClick(curPoint);
                NvsAnimatedSticker curAnimateSticker = mVideoFragment.getCurAnimateSticker();
                if (curAnimateSticker != null && curAnimateSticker instanceof NvsTimelineAnimatedSticker) {
                    mCurSelectAnimatedSticker = (NvsTimelineAnimatedSticker) curAnimateSticker;
                } else {
                    mCurSelectAnimatedSticker = null;
                }
                mVideoFragment.updateAnimateStickerCoordinate(mCurSelectAnimatedSticker);
                updateStickerMuteVisible();
                mVideoFragment.changeStickerRectVisible();
                selectTimeSpan();
            }

            // 贴纸平移 Sticker trans
            @Override
            public void onAssetTranslation() {
                if (mCurSelectAnimatedSticker == null) {
                    return;
                }
                if (mCurrentStatusIsKeyFrame) {
                    updateOrAddKeyFrameInfors();
                }
                int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                int index = getAnimateStickerIndex(zVal);
                if (index >= 0) {
                    mStickerDataListClone.get(index).setTranslation(mCurSelectAnimatedSticker.getTranslation());
                }
            }

            // 贴纸缩放 sticker scale
            @Override
            public void onAssetScale() {
                if (mCurSelectAnimatedSticker == null) {
                    return;
                }
                if (mCurrentStatusIsKeyFrame) {
                    updateOrAddKeyFrameInfors();
                }
                int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                int index = getAnimateStickerIndex(zVal);
                if (index >= 0) {
                    mStickerDataListClone.get(index).setTranslation(mCurSelectAnimatedSticker.getTranslation());
                    mStickerDataListClone.get(index).setScaleFactor(mCurSelectAnimatedSticker.getScale());
                    mStickerDataListClone.get(index).setRotation(mCurSelectAnimatedSticker.getRotationZ());
                }
            }

            @Override
            public void onAssetAlign(int alignVal) {

            }

            @Override
            public void onAssetHorizFlip(boolean isHorizFlip) {
                if (mCurSelectAnimatedSticker == null) {
                    return;
                }
                int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                int index = getAnimateStickerIndex(zVal);
                if (index >= 0) {
                    mStickerDataListClone.get(index).setHorizFlip(mCurSelectAnimatedSticker.getHorizontalFlip());
                }
            }
        });
        mVideoFragment.setLiveWindowClickListener(new VideoFragment.OnLiveWindowClickListener() {
            @Override
            public void onLiveWindowClick() {
                isNewStickerUuidItemClick = false;
            }
        });

        mVideoFragment.setStickerMuteListener(new VideoFragment.OnStickerMuteListener() {
            @Override
            public void onStickerMute() {
                if (mCurSelectAnimatedSticker == null) {
                    return;
                }
                float volumeGain = mCurSelectAnimatedSticker.getVolumeGain().leftVolume;
                int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                int index = getAnimateStickerIndex(zVal);
                if (index >= 0) {
                    mStickerDataListClone.get(index).setVolumeGain(volumeGain);
                }
            }
        });

        mAnimateStickerAssetLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * 刷新某个时间点上所有的贴纸信息
     * Refresh all sticker information at a certain point in time
     */
    private void updateAllStickerPos() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineAnimatedSticker> animateStickerList = mTimeline.getAnimatedStickersByTimelinePosition(curPos);
        for (NvsTimelineAnimatedSticker nvsTimelineAnimatedSticker : animateStickerList) {
            int zVal = (int) nvsTimelineAnimatedSticker.getZValue();
            int index = getAnimateStickerIndex(zVal);
            if (index >= 0) {
                StickerInfo stickerInfo = mStickerDataListClone.get(index);
                Map<Long, KeyFrameInfo> keyFrameInfoHashMap = stickerInfo.getKeyFrameInfoHashMap();
                if (!keyFrameInfoHashMap.isEmpty()) {
                    // 关键帧不为空
                    //Keyframe is not empty
                    nvsTimelineAnimatedSticker.setCurrentKeyFrameTime(curPos - nvsTimelineAnimatedSticker.getInPoint());
                    Set<Long> longs = keyFrameInfoHashMap.keySet();
                    if (longs.contains(curPos)) {
                        // 不移除  Do not remove
                    } else {
                        nvsTimelineAnimatedSticker.removeKeyframeAtTime("Sticker TransX", curPos - nvsTimelineAnimatedSticker.getInPoint());
                        nvsTimelineAnimatedSticker.removeKeyframeAtTime("Sticker TransY", curPos - nvsTimelineAnimatedSticker.getInPoint());
                        nvsTimelineAnimatedSticker.removeKeyframeAtTime("Sticker Scale", curPos - nvsTimelineAnimatedSticker.getInPoint());
                        nvsTimelineAnimatedSticker.removeKeyframeAtTime("Sticker RotZ", curPos - nvsTimelineAnimatedSticker.getInPoint());
                    }
                }
            }
        }
    }

    // 修改当前贴纸关键帧处的缩放、平移、旋转信息 并记录数据 或者是在当前位置添加一个关键帧
    //Modify the zoom, translation, and rotation information at the key frame of the current sticker
    // and record the data or add a key frame at the current position
    private void updateOrAddKeyFrameInfors() {
        StickerInfo currentStickerInfo = getCurrentStickerInfo();
        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
        long currentTimelinePos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (keyFrameInfoHashMap.isEmpty()) {
            // 没有关键帧 添加一个新的关键帧
            // No key frame Add a new key frame
            mAddDeleteKeyFrameView.performClick();
            return;
        }
        boolean hasKeyFrame = false;
        long currentKeyFrameStamp = -1;
        for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
            if (currentTimelinePos > (entry.getKey() - 100000) && currentTimelinePos < (entry.getKey() + 100000)) {
                hasKeyFrame = true;
                currentKeyFrameStamp = entry.getKey();
                break;
            }
        }
        if (hasKeyFrame) {
            // 数据结构记录在时间线上的位置
            // The position of the data structure recorded on the timeline
            currentStickerInfo.putKeyFrameInfo(currentKeyFrameStamp, generateKeyFrameInfo(mCurSelectAnimatedSticker));
        } else {
            // 当前位置没有关键帧 添加一个新的关键帧
            //No key frame Add a new key frame
            mAddDeleteKeyFrameView.performClick();
        }
    }

    /**
     * 拖动过程中更新贴纸边框位置
     * Update sticker border during drag
     *
     * @param timeStamp
     * @param keyFrameInfoHashMap
     */
    private void updateAllKeyFrameViewStatusWhenScroll(long timeStamp, Map<Long, KeyFrameInfo> keyFrameInfoHashMap) {
        boolean isHasKeyFrame = !keyFrameInfoHashMap.isEmpty();
        if (isHasKeyFrame) {
            Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
            Set<Long> keyFrameKeySet = keyFrameInfoHashMap.keySet();
            Object[] objects = keyFrameKeySet.toArray();
            // before
            long beforeKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key < timeStamp) {
                    // 找到距离当前位置 向前最近的一个时间点
                    //Find a time point closest to the current position
                    beforeKeyFrame = key;
                }
            }
            if (beforeKeyFrame == -1 || ((objects != null) && ((long) (objects[0]) == timeStamp))) {
                mBeforeKeyFrameView.setEnabled(false);
            } else {
                mBeforeKeyFrameView.setEnabled(true);
            }

            // next
            long nextKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key > timeStamp) {
                    // 找到距离当前位置 向后最近的一个时间点
                    // Find the nearest point in time backwards from the current location
                    nextKeyFrame = key;
                    break;
                }
            }

            if (nextKeyFrame == -1 || ((objects != null) && ((long) (objects[objects.length - 1]) == timeStamp))) {
                mNextKeyFrameView.setEnabled(false);
            } else {
                mNextKeyFrameView.setEnabled(true);
            }

            // add delete
            boolean hasKeyFrame = false;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                    hasKeyFrame = true;
                    break;
                }
            }
            setAddDeleteViewStatus(!hasKeyFrame);
            // keyFramePoint
            NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
            if (currentTimeSpan != null) {
                currentTimeSpan.setCurrentTimelinePosition(timeStamp);
            }
        } else {
            mBeforeKeyFrameView.setEnabled(false);
            mNextKeyFrameView.setEnabled(false);
            setAddDeleteViewStatus(true);
        }
    }

    private StickerInfo getCurrentStickerInfo() {
        if (mCurSelectAnimatedSticker == null) {
            return null;
        }
        int zVal = (int) mCurSelectAnimatedSticker.getZValue();
        int index = getAnimateStickerIndex(zVal);
        if (index >= 0) {
            return mStickerDataListClone.get(index);
        }
        return null;
    }

    /**
     * 生成一个新的关键帧信息
     * Generate a new key frame information
     *
     * @param sticker
     * @return
     */
    private KeyFrameInfo generateKeyFrameInfo(NvsTimelineAnimatedSticker sticker) {
        return new KeyFrameInfo().setScaleX(sticker.getScale())
                .setScaleY(sticker.getScale())
                .setRotationZ(sticker.getRotationZ())
                .setTranslation(sticker.getTranslation());
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.next_key_frame_view) {
            if (mCurSelectAnimatedSticker != null) {
                mVideoFragment.stopEngine();
                StickerInfo currentStickerInfo = getCurrentStickerInfo();
                Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
                Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                long currentTimelinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                long nextKeyFrame = -1;
                for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                    Long key = entry.getKey();
                    if (key > currentTimelinePosition) {
                        nextKeyFrame = key;
                        break;
                    }
                }
                if (nextKeyFrame == -1) {
                    mNextKeyFrameView.setEnabled(false);
                } else {
                    mNextKeyFrameView.setEnabled(true);
                    seekTimeline(nextKeyFrame);
                    seekMultiThumbnailSequenceView();
                }
            }
        } else if (id == R.id.before_key_frame_view) {
            if (mCurSelectAnimatedSticker != null) {
                mVideoFragment.stopEngine();
                StickerInfo currentStickerInfo = getCurrentStickerInfo();
                Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
                Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                long currentTimelinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                long nextKeyFrame = -1;
                for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                    Long key = entry.getKey();
                    if (key < currentTimelinePosition) {
                        nextKeyFrame = key;
                    }
                }
                if (nextKeyFrame == -1) {
                    mBeforeKeyFrameView.setEnabled(false);
                } else {
                    mBeforeKeyFrameView.setEnabled(true);
                    seekTimeline(nextKeyFrame);
                    seekMultiThumbnailSequenceView();
                }
            }
            // 清除所有的关键帧  Clear all keyframes
        } else if (id == R.id.removeAllKeyFrameButton) {
            if (mDeleteAllKeyFrameTips == null) {
                mDeleteAllKeyFrameTips = new TipsDialog(this);
                mDeleteAllKeyFrameTips.setOnBtnClickListener(new TipsDialog.OnBtnClickListener() {
                    @Override
                    public void OnConfirmBtnClicked() {
                        if (mCurSelectAnimatedSticker != null) {
                            mCurSelectAnimatedSticker.removeAllKeyframe("Sticker TransX");
                            mCurSelectAnimatedSticker.removeAllKeyframe("Sticker TransY");
                            mCurSelectAnimatedSticker.removeAllKeyframe("Sticker Scale");
                            mCurSelectAnimatedSticker.removeAllKeyframe("Sticker RotZ");
                            stickerRemoveAllFrameView.setVisibility(View.GONE);
                            removeFirst.setVisibility(View.GONE);
                            StickerInfo currentStickerInfo = getCurrentStickerInfo();
                            if (currentStickerInfo != null) {
                                // 数据结构记录在时间线上的位置
                                currentStickerInfo.getKeyFrameInfoHashMap().clear();
                            }
                            NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                            if (currentTimeSpan != null) {
                                currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                            }
                            updateStickerKeyFrameButtonBackground();
                            updateStickerBoundingRect();
                            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                            mDeleteAllKeyFrameTips.dismiss();
                        }
                    }

                    @Override
                    public void OnCancelBtnClicked() {
                        mDeleteAllKeyFrameTips.dismiss();
                    }
                });
            }
            mDeleteAllKeyFrameTips.show();
            mDeleteAllKeyFrameTips.setTipsText(R.string.tips_delete_all_key_frame);

            // 添加/删除关键帧 add/delete keyFrame
        } else if (id == R.id.add_delete_frame_view) {
            if ((int) (v.getTag()) == 0) {
                //add
                if (mCurSelectAnimatedSticker != null) {
                    mVideoFragment.stopEngine();
                    // 关键帧的应用时间是相对于当前贴纸的开始时间
                    //The time of the key frame is relative to the start time of the current sticker
                    long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurSelectAnimatedSticker.getInPoint();
                    addKeyFrameOnCurrentTime(duration, mCurSelectAnimatedSticker.getTranslation(),
                            mCurSelectAnimatedSticker.getScale(), mCurSelectAnimatedSticker.getRotationZ());
//                        mCurSelectAnimatedSticker.setCurrentKeyFrameTime(mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurSelectAnimatedSticker.getInPoint());

                    StickerInfo currentStickerInfo = getCurrentStickerInfo();
                    if (currentStickerInfo != null) {
                        // 数据结构记录在时间线上的位置
                        // The position of the data structure recorded on the timeline
                        currentStickerInfo.putKeyFrameInfo(mStreamingContext.getTimelineCurrentPosition(mTimeline), generateKeyFrameInfo(mCurSelectAnimatedSticker));
                    }
                    NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                    if (currentTimeSpan != null) {
                        currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                    }
                    updateStickerBoundingRect();
                }
                // 显示出操作框来
                // Show the action box
                mIsSeekTimeline = true;
                seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                seekMultiThumbnailSequenceView();
                setAddDeleteViewStatus(false);
            } else {
                //delete
                if (mCurSelectAnimatedSticker != null) {
                    mVideoFragment.stopEngine();
                    StickerInfo currentStickerInfo = getCurrentStickerInfo();
                    if (currentStickerInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
                        long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        Set<Long> longs = keyFrameInfoHashMap.keySet();
                        for (Long aLong : longs) {
                            if (timelineCurrentPosition >= aLong - 100000 && timelineCurrentPosition <= aLong + 100000) {
                                keyFrameInfoHashMap.get(aLong);
                                keyFrameInfoHashMap.remove(aLong);
                                boolean sticker_transX = mCurSelectAnimatedSticker.removeKeyframeAtTime("Sticker TransX", aLong - mCurSelectAnimatedSticker.getInPoint());
                                Log.d(TAG, sticker_transX ? "sticker_transX success" : "sticker_transX failed");
                                boolean sticker_transY = mCurSelectAnimatedSticker.removeKeyframeAtTime("Sticker TransY", aLong - mCurSelectAnimatedSticker.getInPoint());
                                Log.d(TAG, sticker_transY ? "sticker_transY success" : "sticker_transY failed");
                                boolean sticker_scale = mCurSelectAnimatedSticker.removeKeyframeAtTime("Sticker Scale", aLong - mCurSelectAnimatedSticker.getInPoint());
                                Log.d(TAG, sticker_scale ? "sticker_scale success" : "sticker_scale failed");
                                boolean sticker_rotZ = mCurSelectAnimatedSticker.removeKeyframeAtTime("Sticker RotZ", aLong - mCurSelectAnimatedSticker.getInPoint());
                                Log.d(TAG, sticker_rotZ ? "sticker_rotZ success" : "sticker_rotZ failed");
                                break;
                            }
                        }
                        NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                        if (currentTimeSpan != null) {
                            currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                        }
                        seekTimeline(timelineCurrentPosition);
                        updateStickerBoundingRect();
                    }
                }
                setAddDeleteViewStatus(true);
            }

            // 关键帧处理完成
            // Key frame processing completed
        } else if (id == R.id.keyFrameFinishView) {
            mStreamingContext.stop();
            changeAddAndFinishViewStatus(View.VISIBLE);
            stickerRemoveAllFrameView.setVisibility(View.VISIBLE);
            mFrameOperationWrapperLayout.setVisibility(View.GONE);
            mCurrentStatusIsKeyFrame = false;
            if (mCurSelectAnimatedSticker != null) {
                mCurSelectAnimatedSticker.setCurrentKeyFrameTime(-1);
            }
            NvsTimelineTimeSpan currentTimeSpanView = getCurrentTimeSpan();
            if (currentTimeSpanView != null) {
                currentTimeSpanView.setKeyFrameInfo(null);
            }
            // 处理添加（编辑）关键帧按钮
            // Process add (edit) key frame button
            if (mCurSelectAnimatedSticker != null) {
                if ((mCurSelectAnimatedSticker.getOutPoint() < mStreamingContext.getTimelineCurrentPosition(mTimeline))
                        || mCurSelectAnimatedSticker.getInPoint() > mStreamingContext.getTimelineCurrentPosition(mTimeline)) {
                    stickerAddFrameView.setVisibility(View.GONE);
                    stickerAnimal.setVisibility(View.GONE);
                    stickerRemoveAllFrameView.setVisibility(View.GONE);
                    removeFirst.setVisibility(View.GONE);
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    mTimelineEditor.unSelectAllTimeSpan();
                } else {
                    stickerAnimal.setVisibility(View.VISIBLE);
                    stickerAddFrameView.setVisibility(View.VISIBLE);
                    updateStickerKeyFrameButtonBackground();
                    ifShowRemoveAllKeyFrameView();
                    mVideoFragment.setDrawRectVisible(View.VISIBLE);
                }
            } else {
                mTimelineEditor.unSelectAllTimeSpan();
                mVideoFragment.setDrawRectVisible(View.GONE);
                stickerAnimal.setVisibility(View.GONE);
                stickerAddFrameView.setVisibility(View.GONE);
                stickerRemoveAllFrameView.setVisibility(View.GONE);
                removeFirst.setVisibility(View.GONE);
            }
            // 跳转关键帧布局
            // Jump key frame layout
        } else if (id == R.id.animateStickerKeyFrameButton) {
            mStreamingContext.stop();
            changeAddAndFinishViewStatus(View.INVISIBLE);
            stickerRemoveAllFrameView.setVisibility(View.INVISIBLE);
            mFrameOperationWrapperLayout.setVisibility(View.VISIBLE);
            mCurrentStatusIsKeyFrame = true;
            NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
            StickerInfo currentStickerInfo = getCurrentStickerInfo();
            if (currentStickerInfo != null) {
                currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                mVideoFragment.setDrawRectVisible(View.VISIBLE);
                mIsSeekTimeline = true;
                // 滚动一帧的时间的1/4
                //1/4 of the time to scroll one frame
                seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline) + 10000);
                multiThumbnailSequenceViewSmooth(mStreamingContext.getTimelineCurrentPosition(mTimeline) + 10000);
            }
        } else if (id == R.id.zoomIn) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomInSequence();
        } else if (id == R.id.zoomOut) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomOutSequence();
        } else if (id == R.id.videoPlay) {
            playVideo();
        } else if (id == R.id.addAnimateStickerButton) {
            mVideoFragment.stopEngine();
            mInPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            mStickerDuration = 4 * Constants.NS_TIME_BASE;
            long duration = mTimeline.getDuration();
            long outPoint = mInPoint + mStickerDuration;
            if (outPoint > duration) {
                mStickerDuration = duration - mInPoint;
                if (mStickerDuration <= Constants.NS_TIME_BASE) {
                    mStickerDuration = Constants.NS_TIME_BASE;
                    mInPoint = duration - mStickerDuration;
                    if (duration <= Constants.NS_TIME_BASE) {
                        mStickerDuration = duration;
                        mInPoint = 0;
                    }
                }
            }
            if (mCurSelectAnimatedSticker != null) {
                mCurAnimateStickerZVal = (int) mCurSelectAnimatedSticker.getZValue();
            }
            mAnimateStickerAssetLayout.setVisibility(View.VISIBLE);
            mVideoFragment.setDrawRectVisible(View.GONE);
        } else if (id == R.id.stickerFinish) {// 关闭贴纸页面 Close sticker page
            TimelineData.instance().setStickerData(mStickerDataListClone);
            mVideoFragment.stopEngine();
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            AppManager.getInstance().finishActivity();
        } else if (id == R.id.moreDownload) {
            mStreamingContext.stop();
            mMoreDownload.setClickable(false);
            Bundle bundle = new Bundle();
            bundle.putInt("titleResId", R.string.moreAnimatedSticker);
            bundle.putInt("assetType", NvAsset.ASSET_ANIMATED_STICKER);
            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, ANIMATESTICKERREQUESTLIST);
        } else if (id == R.id.stickerAssetFinish) {// 关闭素材添加页面 Close the material adding page
            multiThumbnailSequenceViewSmooth(mInPoint);
            mAnimateStickerAssetLayout.setVisibility(View.GONE);
            seekTimeline(mInPoint);
            if (mAddAnimateSticker != null) {
                selectAnimateStickerAndTimeSpan(true);
            } else {
                selectAnimateStickerAndTimeSpanByZVal();
            }
            /*
             * Add a sticker object and leave it blank, otherwise entering the sticker list again will cause deletion by mistake
             *
             * */
            mAddAnimateSticker = null;
            mCurAnimateStickerZVal = 0;
            isNewStickerUuidItemClick = false;
            mSelectUuid = "";
            /*
             * 取消当前Tab页贴纸选中的状态
             * Cancel the status of the current Tab page sticker selection
             * */
            mCurSelectedPos = -1;
            mAssetFragmentsArray.get(mCurTabPage).setSelectedPos(mCurSelectedPos);
            mAssetFragmentsArray.get(mCurTabPage).notifyDataSetChanged();
            mFrameOperationWrapperLayout.setVisibility(View.GONE);
            changeAddAndFinishViewStatus(View.VISIBLE);
        }
    }


    /**
     * 添加一个关键帧
     * Add a keyframe
     *
     * @param currentTime
     * @param transF
     * @param rotationZ
     */
    private void addKeyFrameOnCurrentTime(long currentTime, PointF transF, float scale, float rotationZ) {
        mCurSelectAnimatedSticker.setCurrentKeyFrameTime(currentTime);
        mCurSelectAnimatedSticker.setTranslation(transF);
        mCurSelectAnimatedSticker.setScale(scale);
        mCurSelectAnimatedSticker.setRotationZ(rotationZ);
    }


    private NvsTimelineTimeSpan getCurrentTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mAnimateSticker == mCurSelectAnimatedSticker) {
                return mTimeSpanInfoList.get(i).mTimeSpan;
            }
        }
        return null;
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

    @Override
    public void onBackPressed() {
        mVideoFragment.stopEngine();
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
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
            case ANIMATESTICKERREQUESTLIST:
                initAnimateStickerDataList();
                mAssetFragmentsArray.get(0).setAssetInfolist(mTotalStickerAssetList);
                mCurSelectedPos = getSelectedPos();
                mAssetFragmentsArray.get(0).setSelectedPos(mCurSelectedPos);
                mAssetFragmentsArray.get(0).notifyDataSetChanged();
                updateStickerBoundingRect();
                break;
            case ANIMATED_STICKER_REQUEST_STYLE:
                // 设置完动画回来生效
                // After setting the animation, come back to take effect
                mStickerDataListClone.clear();
                mStickerDataListClone.addAll(BackupData.instance().getStickerInfoList());
                mCurAnimateStickerZVal = BackupData.instance().getStickerZVal();
                TimelineUtil.applyAnimatedStickerAnimation(mCurSelectAnimatedSticker, getCurrentStickerInfo());
                break;
            default:
                break;
        }
    }

    /**
     * 多图平滑滚动到指定时间戳
     * Smooth scrolling of multiple images to the specified timestamp
     *
     * @param stamp
     */
    private void multiThumbnailSequenceViewSmooth(long stamp) {
        if (mMultiThumbnailSequenceView != null) {
            int x = Math.round((stamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
            mMultiThumbnailSequenceView.smoothScrollTo(x, 0);
        }
    }

    /**
     * 选择贴纸和timeSpan依据zValue
     * Choose sticker and timeSpan according to zValue
     */
    private void selectAnimateStickerAndTimeSpanByZVal() {
        selectAnimateStickerByZVal();
        updateStickerBoundingRect();
        if (mCurSelectAnimatedSticker != null) {
            selectTimeSpan();
            stickerAddFrameView.setVisibility(View.VISIBLE);
            stickerAnimal.setVisibility(View.VISIBLE);
            updateStickerKeyFrameButtonBackground();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
            stickerAnimal.setVisibility(View.GONE);
            stickerAddFrameView.setVisibility(View.GONE);
        }
        ifShowRemoveAllKeyFrameView();
    }

    /**
     * 选择贴纸依据zValue
     * Choose sticker according to zValue
     */
    private void selectAnimateStickerByZVal() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineAnimatedSticker> animateStickerList = mTimeline.getAnimatedStickersByTimelinePosition(curPos);
        int stickerCount = animateStickerList.size();
        if (stickerCount > 0) {
            int index = -1;
            for (int i = 0; i < animateStickerList.size(); i++) {
                int tmpZVal = (int) animateStickerList.get(i).getZValue();
                if (tmpZVal == mCurAnimateStickerZVal) {
                    index = i;
                    break;
                }
            }
            mCurSelectAnimatedSticker = index >= 0 ? animateStickerList.get(index) : null;
        } else {
            mCurSelectAnimatedSticker = null;
        }
    }

    /**
     * 更新贴纸边框
     * update the sticker frame
     */
    private void updateStickerBoundingRect() {
        mVideoFragment.setCurAnimateSticker(mCurSelectAnimatedSticker);
        mVideoFragment.updateAnimateStickerCoordinate(mCurSelectAnimatedSticker);
        updateStickerMuteVisible();
        if (mAddAnimateSticker == null && mAnimateStickerAssetLayout.getVisibility() == View.VISIBLE) {
            mVideoFragment.setDrawRectVisible(View.GONE);
        } else {
            mVideoFragment.changeStickerRectVisible();
        }
    }

    private int getSelectedPos() {
        int selectPos = -1;
        if (mSelectUuid.isEmpty()) {
            return selectPos;
        }
        for (int index = 0; index < mTotalStickerAssetList.size(); ++index) {
            if (mTotalStickerAssetList.get(index).uuid.equals(mSelectUuid)) {
                selectPos = index;
                break;
            }
        }
        return selectPos;
    }

    /**
     * 获取当前选中贴纸的索引
     * Get the index of the currently selected sticker
     *
     * @return
     */
    private int getCustomStickerSelectedPos() {
        int selectPos = -1;
        if (mSelectUuid.isEmpty()) {
            return selectPos;
        }
        for (int index = 0; index < mCustomStickerAssetList.size(); ++index) {
            if (mCustomStickerAssetList.get(index).uuid.equals(mSelectUuid)) {
                selectPos = index;
                break;
            }
        }
        return selectPos;
    }

    private boolean initAssetData() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return false;
        }
        mStickerDataListClone = TimelineData.instance().cloneStickerData();
        mStickerAssetTypeList = new ArrayList<>();
        mTotalStickerAssetList = new ArrayList<>();
        mCustomStickerAssetList = new ArrayList<>();
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(mAssetType);
        String bundlePath = "sticker";
        mAssetManager.searchReservedAssets(mAssetType, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_CUSTOM_ANIMATED_STICKER);
        String bundlePath2 = "customsticker";
        mAssetManager.searchReservedAssets(NvAsset.ASSET_CUSTOM_ANIMATED_STICKER, bundlePath2);
        mAssetManager.initCustomStickerInfoFromSharedPreferences();
        return true;
    }

    private void initAnimateStickerDataList() {
        mTotalStickerAssetList.clear();
        ArrayList<NvAsset> userableAsset = getAssetsDataList();
        if (userableAsset != null && userableAsset.size() > 0) {
            for (NvAsset asset : userableAsset) {
                if (asset.isReserved()) {
                    String coverPath = com.meishe.utils.PathUtils.getAssetFileBySuffixPic("sticker/" + asset.uuid);
                    if (coverPath.endsWith("webp")) {
                        coverPath = "asset://android_asset/" + coverPath;
                    } else {
                        coverPath = "file:///android_asset/" + coverPath;
                    }
                    /*
                     * 加载assets/sticker文件夹下的图片
                     * Load images in the assets / sticker folder
                     * */
                    asset.coverUrl = coverPath;
                }
            }
            mTotalStickerAssetList = userableAsset;
        }
    }

    /*
     * 获取下载到手机缓存路径下的素材，包括assets路径下自带的素材
     * Get the material downloaded to the cache path of the mobile phone, including the material that comes with the assets path
     * */
    private ArrayList<NvAsset> getAssetsDataList() {
        return mAssetManager.getUsableAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    /*
     * 获取自定义贴纸列表
     * Get custom sticker list
     * */
    private void initCustomAssetsDataList() {
        mCustomStickerAssetList.clear();
        mCustomStickerAssetList = mAssetManager.getUsableCustomStickerAssets();
    }

    /**
     * 初始化tab的menu数据
     * int tab data
     */
    private void initTabLayout() {
        String[] tabList = getResources().getStringArray(R.array.animatedSticker_type);
        mStickerAssetTypeList.add(tabList[0]);
        mStickerAssetTypeList.add(tabList[1]);
        for (int index = 0; index < mStickerAssetTypeList.size(); index++) {
            mAnimateStickerTypeTab.addTab(mAnimateStickerTypeTab.newTab().setText(mStickerAssetTypeList.get(index)));
        }
        initAnimateStickerFragment();
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mAssetFragmentsArray.get(position);
            }

            @Override
            public int getCount() {
                return mAssetFragmentsArray.size();
            }
        });

        /*
         * 添加tab切换的监听事件
         * Add a tab switch to listen for events
         * */
        mAnimateStickerTypeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*
                 * 当前选中的tab的位置，切换到相应的fragment
                 * Position of the currently selected tab, switch to the corresponding fragment
                 * */
                mCurTabPage = tab.getPosition();
                mViewPager.setCurrentItem(mCurTabPage);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 安装一个贴纸包
     * install a animatedSticker
     */
    private void gifToCafStickerTemplateinstall() {
        String stickerTemplatePath = "assets:/E14FEE65-71A0-4717-9D66-3397B6C11223/E14FEE65-71A0-4717-9D66-3397B6C11223.5.animatedsticker";
        PackageManagerUtil.installAssetPackage(stickerTemplatePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER);
    }

    private void initAnimateStickerFragment() {
        mStickerListFragment = new AnimateStickerListFragment();
        mStickerListFragment.setAnimateStickerClickerListener(new AnimateStickerListFragment.AnimateStickerClickerListener() {
            @Override
            public void onFragmentLoadFinish() {
                mStickerListFragment.setCustomStickerButtonVisible(View.GONE);
                mCustomStickerListFragment.setIsCutomStickerAsset(false);
                mStickerListFragment.setAssetInfolist(mTotalStickerAssetList);
            }

            @Override
            public void onItemClick(View view, int pos) {
                if (pos < 0 || pos >= mTotalStickerAssetList.size()) {
                    return;
                }
                applyAnimateSticker(pos);
            }

            @Override
            public void onAddCustomSticker() {

            }
        });

        mAssetFragmentsArray.add(mStickerListFragment);
        mCustomStickerListFragment = new AnimateStickerListFragment();
        mCustomStickerListFragment.setAnimateStickerClickerListener(new AnimateStickerListFragment.AnimateStickerClickerListener() {
            @Override
            public void onFragmentLoadFinish() {
                mCustomStickerListFragment.setCustomStickerButtonVisible(View.VISIBLE);
                mCustomStickerListFragment.setIsCutomStickerAsset(true);
                mCustomStickerListFragment.setCustomStickerAssetInfolist(mCustomStickerAssetList);
            }

            @Override
            public void onItemClick(View view, int pos) {
                if (pos < 0 || pos >= mCustomStickerAssetList.size()) {
                    return;
                }
                applyCustomAnimateSticker(pos);
            }

            @Override
            public void onAddCustomSticker() {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_CUSTOM_STICKER);
                AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), SingleClickActivity.class, bundle);
            }
        });

        mAssetFragmentsArray.add(mCustomStickerListFragment);
    }

    /**
     * 应用某个贴纸通过position
     * apply the animatedSticker by position
     *
     * @param pos
     */
    private void applyAnimateSticker(int pos) {
        if (mAddAnimateSticker != null
                && mPrevTabPage == mCurTabPage
                && mCurSelectedPos == pos) {
            isNewStickerUuidItemClick = false;
            if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                long endTime = mInPoint + mStickerDuration;
                mVideoFragment.playVideo(mInPoint, endTime);
                mVideoFragment.setDrawRectVisible(View.GONE);
            } else {
                mVideoFragment.stopEngine();
            }
            return;
        }

        removeAddAniamteSticker();

        float zStickerVal = getCurAnimateStickerZVal();
        /*
         * 添加贴纸
         * add stickers
         * */
        mAddAnimateSticker = mTimeline.addAnimatedSticker(mInPoint, mStickerDuration,
                mTotalStickerAssetList.get(pos).uuid);
        if (mAddAnimateSticker == null) {
            return;
        }
        mAddAnimateSticker.setZValue(zStickerVal);

        /*
         * 取消其他页贴纸选中
         * Uncheck other page stickers
         * */
        mCurSelectedPos = pos;
        mSelectUuid = mTotalStickerAssetList.get(pos).uuid;
        addTimeSpanAndPlayVideo(false, "");
    }

    /**
     * 应用自定义贴纸
     * apply the custom animatedSticker
     *
     * @param pos
     */
    private void applyCustomAnimateSticker(final int pos) {
        if (mAddAnimateSticker != null
                && mPrevTabPage == mCurTabPage
                && mCurSelectedPos == pos) {
            isNewStickerUuidItemClick = false;
            if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                long endTime = mInPoint + mStickerDuration;
                mVideoFragment.playVideo(mInPoint, endTime);
                mVideoFragment.setDrawRectVisible(View.GONE);
            } else {
                mVideoFragment.stopEngine();
            }
            return;
        }

        removeAddAniamteSticker();

        /*
         * 添加自定义贴纸
         * Add custom stickers
         * */
        String imageSrcFilePath = mCustomStickerAssetList.get(pos).imagePath;
        int lastPointPos = imageSrcFilePath.lastIndexOf(".");
        String fileSuffixName = imageSrcFilePath.substring(lastPointPos).toLowerCase();
        if (".gif".equals(fileSuffixName)) {//gif
            String targetCafPath = mCustomStickerAssetList.get(pos).targetImagePath;
            File targetCafFile = new File(targetCafPath);
            if (targetCafFile.exists()) {
                /*
                 * 检测目标caf文件是否存在
                 * Detect the existence of the target caf file
                 * */
                addCustomAnimateSticker(pos, targetCafPath);
            }
        } else {//image
            addCustomAnimateSticker(pos, mCustomStickerAssetList.get(pos).imagePath);
        }
    }

    /**
     * 添加一个自定义贴纸
     * add a new custom animatedSticker
     *
     * @param pos
     * @param imageFilePath
     */
    private void addCustomAnimateSticker(int pos, String imageFilePath) {
        float zStickerVal = getCurAnimateStickerZVal();
        mAddAnimateSticker = mTimeline.addCustomAnimatedSticker(mInPoint, mStickerDuration,
                mCustomStickerAssetList.get(pos).templateUuid, imageFilePath);
        if (mAddAnimateSticker == null) {
            return;
        }

        mAddAnimateSticker.setZValue(zStickerVal);
        mCurSelectedPos = pos;
        mSelectUuid = mCustomStickerAssetList.get(pos).uuid;
        addTimeSpanAndPlayVideo(true, imageFilePath);
    }

    private void removeAddAniamteSticker() {
        if (mAddAnimateSticker != null) {
            int zVal = (int) mAddAnimateSticker.getZValue();
            int index = getAnimateStickerIndex(zVal);
            if (index >= 0) {
                mStickerDataListClone.remove(index);
            }
            deleteCurStickerTimeSpan(mAddAnimateSticker);
            mTimeline.removeAnimatedSticker(mAddAnimateSticker);
            mAddAnimateSticker = null;
            mVideoFragment.setCurAnimateSticker(mAddAnimateSticker);
            mVideoFragment.changeStickerRectVisible();
        }
    }

    /**
     * 保存一个stickerInfo 对象
     * save the stickerInfo
     *
     * @return
     */
    private StickerInfo saveStickerInfo() {
        StickerInfo stickerInfo = new StickerInfo();
        stickerInfo.setInPoint(mAddAnimateSticker.getInPoint());
        stickerInfo.setOutPoint(mAddAnimateSticker.getOutPoint());
        stickerInfo.setHorizFlip(mAddAnimateSticker.getHorizontalFlip());
        stickerInfo.setTranslation(mAddAnimateSticker.getTranslation());
        String packagedId = mAddAnimateSticker.getAnimatedStickerPackageId();
        stickerInfo.setId(packagedId);
        int zVal = (int) mAddAnimateSticker.getZValue();
        stickerInfo.setAnimateStickerZVal(zVal);
        return stickerInfo;
    }

    /**
     * 添加一个TimeSpan并且播放视频
     * add a timeSpan and paly the video
     *
     * @param isCustomSticker
     * @param imageFilePath
     */
    private void addTimeSpanAndPlayVideo(boolean isCustomSticker, String imageFilePath) {
        if (mAddAnimateSticker == null) {
            return;
        }
        if (mPrevTabPage != mCurTabPage) {
            mAssetFragmentsArray.get(mPrevTabPage).setSelectedPos(-1);
            mAssetFragmentsArray.get(mPrevTabPage).notifyDataSetChanged();
        }
        isNewStickerUuidItemClick = true;
        mPrevTabPage = mCurTabPage;
        long endTime = mInPoint + mStickerDuration;
        /*
         * 添加timeSpan
         * addTimeSpan
         * */
        NvsTimelineTimeSpan timeSpan = addTimeSpan(mInPoint, endTime);
        if (timeSpan != null) {
            AnimatedStickerActivity.AnimateStickerTimeSpanInfo timeSpanInfo = new AnimatedStickerActivity.AnimateStickerTimeSpanInfo(mAddAnimateSticker, timeSpan);
            mTimeSpanInfoList.add(timeSpanInfo);
        }

        /*
         * 保存数据
         * save data
         * */
        StickerInfo stickerInfo = saveStickerInfo();
        stickerInfo.setCustomSticker(isCustomSticker);
        stickerInfo.setCustomImagePath(imageFilePath);
        mStickerDataListClone.add(stickerInfo);
        mVideoFragment.setDrawRectVisible(View.GONE);
        /*
         * 播放视频
         * Play video
         * */
        mVideoFragment.playVideo(mInPoint, endTime);
    }

    private float getCurAnimateStickerZVal() {
        float zVal = 0.0f;
        NvsTimelineAnimatedSticker animatedSticker = mTimeline.getFirstAnimatedSticker();
        while (animatedSticker != null) {
            float tmpZVal = animatedSticker.getZValue();
            if (tmpZVal > zVal) {
                zVal = tmpZVal;
            }
            animatedSticker = mTimeline.getNextAnimatedSticker(animatedSticker);
        }
        zVal += 1.0;
        return zVal;
    }

    /**
     * 选择贴纸和timeSpan
     * Choose sticker and timeSpan
     */
    private void selectAnimateStickerAndTimeSpan(boolean isFindSticker) {
        if (mCurrentStatusIsKeyFrame) {
            return;
        }
        if (null == mCurSelectAnimatedSticker || isFindSticker) {
            selectAnimateSticker();
        }
        StickerInfo currentStickerInfo = getCurrentStickerInfo();
        if (currentStickerInfo != null) {
            updateStickerBoundingRect();
        } else {
            mVideoFragment.setDrawRectVisible(View.GONE);
        }
        if (mCurSelectAnimatedSticker != null) {
            selectTimeSpan();
            stickerAnimal.setVisibility(View.VISIBLE);
            stickerAddFrameView.setVisibility(View.VISIBLE);
            updateStickerKeyFrameButtonBackground();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
            stickerAnimal.setVisibility(View.GONE);
            stickerAddFrameView.setVisibility(View.GONE);
        }
        ifShowRemoveAllKeyFrameView();
    }

    /**
     * 如果正在播放则移除所有关键帧边框
     * Remove all sticker key frame if it is playing
     */
    private void ifShowRemoveAllKeyFrameView() {
        StickerInfo currentStickerInfo = getCurrentStickerInfo();
        if (currentStickerInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
            if (keyFrameInfoHashMap != null && !keyFrameInfoHashMap.isEmpty()) {
                stickerRemoveAllFrameView.setVisibility(View.VISIBLE);
                removeFirst.setVisibility(View.VISIBLE);
            } else {
                stickerRemoveAllFrameView.setVisibility(View.GONE);
                removeFirst.setVisibility(View.GONE);
            }
        } else {
            stickerRemoveAllFrameView.setVisibility(View.GONE);
            removeFirst.setVisibility(View.GONE);
        }
    }

    /**
     * 更新贴纸关键帧按钮背景
     * Update sticker key frame button background
     */
    private void updateStickerKeyFrameButtonBackground() {
        StickerInfo currentStickerInfo = getCurrentStickerInfo();
        boolean isAddStatus = true;
        if (currentStickerInfo != null) {
            isAddStatus = currentStickerInfo.getKeyFrameInfoHashMap().isEmpty();
        }
        if (isAddStatus) {
            ivAddFrame.setImageResource(R.mipmap.sticker_frame);
        } else {
//            ivAddFrame.setImageResource(R.mipmap.sticker_frame_edit);
        }
    }

    /**
     * 删除当前关键帧
     * delete the current AnimatedSticker
     */
    private void deleteAnimateSticker() {
        deleteCurStickerTimeSpan(mCurSelectAnimatedSticker);
        mTimeline.removeAnimatedSticker(mCurSelectAnimatedSticker);
        mCurSelectAnimatedSticker = null;
        selectAnimateStickerAndTimeSpan(true);
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
    }

    /**
     * 删除当前贴纸的timeSpan
     * delete the timeSpan of current AnimatedSticker
     */
    private void deleteCurStickerTimeSpan(NvsTimelineAnimatedSticker animateSticker) {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (animateSticker != null
                    && mTimeSpanInfoList.get(i).mAnimateSticker == animateSticker) {
                mTimelineEditor.deleteSelectedTimeSpan(mTimeSpanInfoList.get(i).mTimeSpan);
                mTimeSpanInfoList.remove(i);
                break;
            }
        }
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
            Log.e(TAG, "addTimeSpan: " + " 添加TimeSpan失败!");
            return null;
        }
        // Span左边调节
        //Span left adjustment
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimInChangeListener() {
            @Override
            public void onTrimInChange(long timeStamp, boolean isDragEnd) {
                seekTimeline(timeStamp);
                StickerInfo currentStickerInfo = getCurrentStickerInfo();
                if (currentStickerInfo != null) {
                    boolean noInfo = currentStickerInfo.getKeyFrameInfoHashMap().isEmpty();
                    if (!noInfo) {
                        mVideoFragment.changeStickerRectVisible();
                    }
                }
                setPlaytimeText(timeStamp);
                NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                if (currentStickerInfo != null && mCurrentStatusIsKeyFrame) {
                    currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                }
                if (isDragEnd && mCurSelectAnimatedSticker != null) {
                    mCurSelectAnimatedSticker.changeInPoint(timeStamp);
                    if (mCurSelectAnimatedSticker == null) {
                        return;
                    }
                    int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                    int index = getAnimateStickerIndex(zVal);
                    if (index >= 0) {
                        mStickerDataListClone.get(index).setInPoint(mCurSelectAnimatedSticker.getInPoint());
                    }
                    seekMultiThumbnailSequenceView();
                    // 移动左边缘 松手之后 1.更新上层数据结构中记录的关键帧信息  2.更新底层关键帧位置信息
                    // After moving the left edge and letting go
                    // 1. Update the key frame information recorded in the upper data structure
                    // 2. Update the position information of the bottom key frame
                    if (mCurSelectAnimatedSticker != null && currentStickerInfo != null) {
                        // 1.step one
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                        long currentTimeLinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                            if (next.getKey() < currentTimeLinePosition) {
                                iterator.remove();
                            }
                        }
                        if (mCurrentStatusIsKeyFrame) {
                            currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                        }
                        // 2.step two 底层移除之前添加的关键帧信息 根据上层数据结构重新添加关键帧
                        // 2.step two The bottom layer removes the previously added key frame information and re-adds the key frame according to the upper layer data structure
                        boolean removeStickerTransXSuccess = mCurSelectAnimatedSticker.removeAllKeyframe("Sticker TransX");
                        boolean removeStickerTransYSuccess = mCurSelectAnimatedSticker.removeAllKeyframe("Sticker TransY");
                        boolean removeStickerScale = mCurSelectAnimatedSticker.removeAllKeyframe("Sticker Scale");
                        boolean removeStickerRotZ = mCurSelectAnimatedSticker.removeAllKeyframe("Sticker RotZ");
                        if (removeStickerTransXSuccess || removeStickerTransYSuccess || removeStickerScale || removeStickerRotZ) {
                            Log.d(TAG, "timelineTimeSpan.setOnChangeListener onChangeLeft  removeAllKeyframe success");
                        }
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = currentStickerInfo.getKeyFrameInfoHashMap();
                        Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                        for (Map.Entry<Long, KeyFrameInfo> longStickerKeyFrameInfoEntry : entriesAfter) {
                            KeyFrameInfo stickerKeyFrameInfo = longStickerKeyFrameInfoEntry.getValue();
                            mCurSelectAnimatedSticker.setCurrentKeyFrameTime(longStickerKeyFrameInfoEntry.getKey() - mCurSelectAnimatedSticker.getInPoint());
                            mCurSelectAnimatedSticker.setTranslation(stickerKeyFrameInfo.getTranslation());
                            mCurSelectAnimatedSticker.setScale(stickerKeyFrameInfo.getScaleX());
                            mCurSelectAnimatedSticker.setRotationZ(stickerKeyFrameInfo.getRotationZ());
                        }
                    }
                }
            }
        });
        // Span右边调节
        //Span right adjustment
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimOutChangeListener() {
            @Override
            public void onTrimOutChange(long timeStamp, boolean isDragEnd) {
                /*
                 * outPoint是开区间，seekTimeline时，需要往前平移一帧即0.04秒，转换成微秒即40000微秒
                 * outPoint is an open interval. In seekTimeline, you need to pan one frame, that is, 0.04 seconds, and convert it to microseconds, that is, 40,000 microseconds.
                 * */
                seekTimeline(timeStamp - 40000);
                setPlaytimeText(timeStamp);
                StickerInfo curStickerInfo = getCurrentStickerInfo();
                if (curStickerInfo != null) {
                    boolean noInfo = curStickerInfo.getKeyFrameInfoHashMap().isEmpty();
                    if (!noInfo) {
                        mVideoFragment.changeStickerRectVisible();
                    }
                }
                if (isDragEnd && mCurSelectAnimatedSticker != null) {
                    mCurSelectAnimatedSticker.changeOutPoint(timeStamp);
                    if (mCurSelectAnimatedSticker == null) {
                        return;
                    }
                    int zVal = (int) mCurSelectAnimatedSticker.getZValue();
                    int index = getAnimateStickerIndex(zVal);
                    if (index >= 0) {
                        mStickerDataListClone.get(index).setOutPoint(timeStamp);
                    }
                    seekMultiThumbnailSequenceView();
                    // 若覆盖了关键帧 则移除覆盖的关键帧信息
                    // If the key frame is covered, remove the covered key frame information
                    StickerInfo currentStickerInfo = getCurrentStickerInfo();
                    if (currentStickerInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                        Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                            // 这里比较的是timeline上的时间
                            // The comparison here is the time on the timeline
                            if (next.getKey() > mStreamingContext.getTimelineCurrentPosition(mTimeline)) {
                                iterator.remove();
                            }
                        }
                        if (mCurrentStatusIsKeyFrame) {
                            NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                            currentTimeSpan.setKeyFrameInfo(currentStickerInfo.getKeyFrameInfoHashMap());
                        }
                    }
                }
            }
        });

        return timelineTimeSpan;
    }

    /**
     * 更新缩略图
     * seek the multiThumb view
     */
    private void seekMultiThumbnailSequenceView() {
        if (mMultiThumbnailSequenceView != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long duration = mTimeline.getDuration();
            mMultiThumbnailSequenceView.scrollTo(Math.round(((float) curPos) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }

    private void seekTimeline(long timestamp) {
        mVideoFragment.seekTimeline(timestamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
    }

    private void seekTimeline(long timestamp, int flag) {
        mVideoFragment.seekTimeline(timestamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER | flag);
    }

    /**
     * 根据所有贴纸信息 添加贴纸蒙层布局
     * Add sticker mask layout based on all sticker information
     */
    private void addAllTimeSpan() {
        NvsTimelineAnimatedSticker animatedSticker = mTimeline.getFirstAnimatedSticker();
        while (animatedSticker != null) {
            long inPoint = animatedSticker.getInPoint();
            long outPoint = animatedSticker.getOutPoint();
            NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);
            if (timeSpan != null) {
                AnimatedStickerActivity.AnimateStickerTimeSpanInfo timeSpanInfo = new AnimatedStickerActivity.AnimateStickerTimeSpanInfo(animatedSticker, timeSpan);
                mTimeSpanInfoList.add(timeSpanInfo);
            }
            animatedSticker = mTimeline.getNextAnimatedSticker(animatedSticker);
        }
    }

    /**
     * 查找并选中当前的贴纸
     * search and select the current animatedSticker
     */
    private void selectAnimateSticker() {
        if (null == mTimeline) {
            return;
        }
        // 找到最顶部的一个贴纸
        // Find the top sticker
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineAnimatedSticker> animateStickerList = mTimeline.getAnimatedStickersByTimelinePosition(curPos);
        Logger.e(TAG, "animateStickerListCount-->" + animateStickerList.size());
        int stickerCount = animateStickerList.size();
        if (stickerCount > 0) {
            float zVal = animateStickerList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < animateStickerList.size(); i++) {
                float tmpZVal = animateStickerList.get(i).getZValue();
                if (tmpZVal > zVal) {
                    zVal = tmpZVal;
                    index = i;
                }
            }
            mCurSelectAnimatedSticker = animateStickerList.get(index);
        }
    }

    /**
     * 查找并选中当前的timeSpan
     * search and select the current timeSpan
     */
    private void selectTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mAnimateSticker == mCurSelectAnimatedSticker) {
                mTimelineEditor.selectTimeSpan(mTimeSpanInfoList.get(i).mTimeSpan);
                break;
            }
        }
    }

    /**
     * 当前贴纸是否可以编辑
     * Whether the current sticker can be edited
     *
     * @return
     */
    private boolean ifCouldEditAnimateSticker() {
        StickerInfo currentStickerInfo = getCurrentStickerInfo();
        if (currentStickerInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentStickerInfo.getKeyFrameInfoHashMap();
            if (!keyFrameInfoHashMap.isEmpty()) {
                // give tips
                ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_when_move_animate_sticker));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.setCurAnimateSticker(mCurSelectAnimatedSticker);
                mStickerFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                        selectAnimateStickerAndTimeSpan(true);
                    }
                }, 100);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME |
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
        /*
         * 设置贴纸模式
         * Set sticker mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_STICKER);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    /**
     * 更新是否可以显示贴纸边框
     * Update whether the sticker border can be displayed
     */
    private void updateStickerMuteVisible() {
        if (mCurSelectAnimatedSticker != null) {
            boolean hasAudio = mCurSelectAnimatedSticker.hasAudio();
            mVideoFragment.setMuteVisible(hasAudio);
            if (hasAudio) {
                float leftVolume = (int) mCurSelectAnimatedSticker.getVolumeGain().leftVolume;
                mVideoFragment.setStickerMuteIndex(leftVolume > 0 ? 0 : 1);
            }
        }
    }

    /**
     * 初始化缩略图
     * Initialize thumbnail
     */
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
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoPlay.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
    }

    /**
     * 设置当前播放时间
     * set the current play time
     *
     * @param playTime
     */
    private void setPlaytimeText(long playTime) {
        long totalDuaration = mTimeline.getDuration();
        String strTotalDuration = TimeFormatUtil.formatUsToString1(totalDuaration);
        String strCurrentDuration = TimeFormatUtil.formatUsToString1(playTime);
        mShowCurrentDuration.setLength(0);
        mShowCurrentDuration.append(strCurrentDuration);
        mShowCurrentDuration.append("/");
        mShowCurrentDuration.append(strTotalDuration);
        mCurrentPlaytime.setText(mShowCurrentDuration.toString());
    }

    private void resetView() {
        setPlaytimeText(0);
        mVideoPlay.setBackgroundResource(R.mipmap.icon_edit_play);
        mMultiThumbnailSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        seekTimeline(mAnimateStickerAssetLayout.getVisibility() == View.VISIBLE ? mInPoint : 0);
        selectAnimateStickerAndTimeSpan(true);
    }

    /**
     * 获取当前选中的贴纸时间
     *
     * @param curZValue
     * @return
     */
    private int getAnimateStickerIndex(int curZValue) {
        int index = -1;
        if (mStickerDataListClone == null) return -1;
        int count = mStickerDataListClone.size();
        for (int i = 0; i < count; ++i) {
            int zVal = mStickerDataListClone.get(i).getAnimateStickerZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMoreDownload.setClickable(true);
        initCustomAssetsDataList();
        mAssetFragmentsArray.get(1).setCustomStickerAssetInfolist(mCustomStickerAssetList);
        mCurSelectedPos = getCustomStickerSelectedPos();
        mAssetFragmentsArray.get(1).setSelectedPos(mCurSelectedPos);
        mAssetFragmentsArray.get(1).notifyDataSetChanged();
        updateStickerBoundingRect();
    }
}
