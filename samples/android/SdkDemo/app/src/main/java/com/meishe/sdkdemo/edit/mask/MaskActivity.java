package com.meishe.sdkdemo.edit.mask;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.constants.NvsConstants;
import com.meishe.engine.bean.CutData;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.adapter.MaskAdapter;
import com.meishe.sdkdemo.edit.anim.AnimationClipAdapter;
import com.meishe.sdkdemo.edit.data.mask.MaskBean;
import com.meishe.sdkdemo.edit.data.mask.MaskInfoData;
import com.meishe.sdkdemo.edit.mask.view.ZoomView;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.InputDialog;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.BackGroundInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/3/4.
 * @Description :蒙版。Mask class
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MaskActivity extends BaseActivity {
    private final String TAG = "MaskActivity";
    private CustomTitleBar mTitleBar;
    private View maskBgView;
    private View maskMenuView;
    private RecyclerView clipRecyclerView;
    private int mSelectedClipPosition = 0;
    private View addMask;
    private View okView;
    /**
     * 反转
     * reversal
     */
    private View maskReverse;
    private RecyclerView menuRecyclerView;
    private View addMaskOk;
    private AnimationClipAdapter mClipAdapter;
    private NvsTimeline mTimeline;
    private VideoFragment mVideoFragment;
    private ArrayList<ClipInfo> clipInfoData;
    private MaskAdapter menuAdapter;
    private Map<Integer, MaskInfoData> maskInfoDataMap = new TreeMap<>();
    private View bottomView;
    private InputDialog inputDialog;

    @Override
    protected int initRootView() {
        return R.layout.activity_mask;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.mask_title_bar);
        maskBgView = findViewById(R.id.mask_def_view);
        maskMenuView = findViewById(R.id.mask_menu_view);
        clipRecyclerView = findViewById(R.id.mask_clips_recycler);

        addMask = findViewById(R.id.iv_mask_add);
        okView = findViewById(R.id.iv_mask_ok);

        maskReverse = findViewById(R.id.mask_reverse);
        menuRecyclerView = findViewById(R.id.mask_recycler_menus);
        addMaskOk = findViewById(R.id.iv_mask_add_ok);

        bottomView = findViewById(R.id.mask_clips_view);
        inputDialog = new InputDialog(this, R.style.dialog, new InputDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean ok) {
                ClipInfo clipInfo = clipInfoData.get(mSelectedClipPosition);
                if (clipInfo == null) {
                    return;
                }
                MaskInfoData maskInfoData = mVideoFragment.getMaskZoomView().getMaskInfoData();
                maskInfoData.setText(inputDialog.getUserInputText());
                mVideoFragment.getMaskZoomView().setMaskTypeAndInfo(maskInfoData.getMaskType(), maskInfoData);
                maskInfoDataMap.put(mSelectedClipPosition, mVideoFragment.getMaskZoomView().getMaskInfoData());
            }
        });
        inputDialog.setNoEmoji(true);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.mask);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initTimeline();
        initMaskMenus();
        initClipsData();
        initVideoFragment();
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 添加蒙版完成后跳转回上一级
                // has added maskView and return to pre activity
                if (clipInfoData != null && maskInfoDataMap != null) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        clipInfoData.get(i).setMaskInfoData(maskInfoDataMap.get(i));
                    }
                }
                TimelineData.instance().setClipInfoData(clipInfoData);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        });
        addMaskOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 单个Clip添加蒙版结束
                // single Clip has added maskView
                showMaskMenu(false);
            }
        });
        addMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 查看是否已经添加蒙版,做具体蒙版信息展示,只判断当未选中状态下
                //Check whether the mask has been added, display the specific mask information,
                // only judge when it is not selected
                if (menuAdapter.getSelectPos() == 0 && clipInfoData != null &&
                        clipInfoData.size() > mSelectedClipPosition && mSelectedClipPosition >= 0) {
                    ClipInfo clipInfo = clipInfoData.get(mSelectedClipPosition);
                    if (clipInfo != null) {
                        MaskInfoData maskInfoData = maskInfoDataMap.get(mSelectedClipPosition);
                        if (maskInfoData != null && maskInfoData.getMaskType() != MaskInfoData.MaskType.NONE) {
                            //有蒙版信息 去添加蒙版信息
                            // There is mask information to add mask information
                            setMaskCenter(clipInfo);
                            mVideoFragment.getMaskZoomView().setMaskTypeAndInfo(maskInfoData.getMaskType(),
                                    maskInfoData, false);
                            menuAdapter.setMaskType(maskInfoData.getMaskType());
                            menuRecyclerView.scrollToPosition(0);
                            if (menuAdapter.getSelectPos() > 3) {
                                menuRecyclerView.scrollToPosition(menuAdapter.getSelectPos());
                            }
                        }
                    }
                }
                // 显示蒙版菜单
                //Show mask menu
                showMaskMenu(true);
            }
        });

    }

    /**
     * 是否展示蒙版详细菜单
     * Whether to show the mask detailed menu
     *
     * @param showEnable
     */
    private void showMaskMenu(boolean showEnable) {
        maskMenuView.setVisibility(showEnable ? View.VISIBLE : View.GONE);
        maskBgView.setVisibility(showEnable ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeTimeline();
        AppManager.getInstance().finishActivity();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private void initVideoFragment() {

        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.setMaskViewDataChangeListener(new ZoomView.OnDataChangeListener() {
                    @Override
                    public void onDataChanged() {
                        ClipInfo clipInfo = clipInfoData.get(mSelectedClipPosition);
                        if (clipInfo == null) {
                            return;
                        }
                        setMaskCenter(clipInfo);
                        applyMaskInfo(clipInfo, mVideoFragment.getMaskZoomView().getMaskInfoData());
                    }

                    @Override
                    public void onMaskTextClick() {
                        if (inputDialog != null && !inputDialog.isShowing()) {
                            inputDialog.setUserInputText(mVideoFragment.getMaskZoomView().getMaskInfoData().getText());
                            inputDialog.show();
                        }
                    }
                });
                addMask.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //播放片段，通过片段时间控制
                        //play the current video by position
                        if (null != mVideoFragment) {
                            playCurrentClip(mSelectedClipPosition);
                        }
                    }
                }, 100);
            }
        });

        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setIsAnimationView(false);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", bottomView.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.mask_video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);


    }

    /**
     * 播放视频
     * Play video
     *
     * @param mSelectedClipPosition 播放某段视频 Play a certain video
     */
    private void playCurrentClip(int mSelectedClipPosition) {
        int clipCount = mTimeline.getVideoTrackByIndex(0).getClipCount();
        long playStartPoint = 0;
        long playEndPoint = 0;
        if (mSelectedClipPosition >= 0 && mSelectedClipPosition < clipCount) {
            playStartPoint = getClipStartTime(mSelectedClipPosition);
            playEndPoint = getClipEndTime(mSelectedClipPosition);
            //播放时针对timeline Play for timeline
            if (playEndPoint > playStartPoint) {
                //出动画只播放结尾部分 入动画播放开始部分
                //Only the ending part of the output animation is played.
                //The beginning part of the input animation is played
                mVideoFragment.setmPlaySeekBarMaxAndCurrent(playStartPoint, playEndPoint, playStartPoint, mTimeline.getDuration());
                //只播放动画部分
                //Only play the animation part
                mVideoFragment.playVideoButtonClick(playStartPoint, playEndPoint);
            }
        }
        //播放全部视频
        //Play timeline
        else if (mSelectedClipPosition == -1) {
            playStartPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            playEndPoint = mTimeline.getDuration();
            if (playStartPoint >= playEndPoint) {
                playStartPoint = 0;
            }
            mVideoFragment.setmPlaySeekBarMaxAndCurrent(0, playEndPoint, playStartPoint, playEndPoint);
            if (playEndPoint > playStartPoint) {
                mVideoFragment.playVideoButtonClick(playStartPoint, playEndPoint);
            }
        }
    }

    /**
     * 获取当前选择的片段的起始位置
     * Get the starting position of the current videoClip
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
     * 获取当前选择的片段的结束位置
     * Get the end position of the current videoClip
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

    /**
     * 获取片段的时长
     * get the duration of the selected videoClip by position
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipDuration(int mSelectedClipPosition) {
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
        long clipOutPoint = nvsVideoClip.getOutPoint();
        return (clipOutPoint - clipInPoint);
    }

    /**
     * 设置clip的数据
     * init the clipData
     */
    private void initClipsData() {
        mClipAdapter = new AnimationClipAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        clipRecyclerView.setLayoutManager(linearLayoutManager);
        clipRecyclerView.setAdapter(mClipAdapter);
        clipInfoData = TimelineData.instance().cloneClipInfoData();
        for (int i = 0; i < clipInfoData.size(); i++) {
            ClipInfo clipInfoDatum = clipInfoData.get(i);
            String filePath = clipInfoDatum.getFilePath();
            NvsAVFileInfo avInfoFromFile = NvsStreamingContext.getAVInfoFromFile(filePath, 0);
            if (avInfoFromFile != null) {
                int videoWidth = avInfoFromFile.getVideoStreamDimension(0).width;
                int videoHeight = avInfoFromFile.getVideoStreamDimension(0).height;
                int videoStreamRotation = avInfoFromFile.getVideoStreamRotation(0);
                int width = (videoStreamRotation % 2 == 1) ? videoHeight : videoWidth;
                int height = (videoStreamRotation % 2 == 1) ? videoWidth : videoHeight;
                float fileRatio = width * 1f / (height * 1f);
                CutData cutData = clipInfoDatum.getCropInfo();
                if (null != cutData) {
                    float value = cutData.getRatioValue();
                    fileRatio = (value != 0) ? value : fileRatio;
                }
                clipInfoDatum.setFileRatio(fileRatio);
                maskInfoDataMap.put(i, clipInfoDatum.getMaskInfoData());
            }
        }
        //设置蒙层 setMask
        mClipAdapter.setClipInfoList(clipInfoData);
        mClipAdapter.setTimeLine(mTimeline);
        mClipAdapter.setOnItemClickListener(new AnimationClipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //选中 select
                if (mSelectedClipPosition == position) {
                    playCurrentClip(mSelectedClipPosition);
                    return;
                }
                mSelectedClipPosition = position;
                if (mSelectedClipPosition < clipInfoData.size()) {
                    ClipInfo clipInfo = clipInfoData.get(mSelectedClipPosition);
                    if (clipInfo == null) {
                        return;
                    }
                    setMaskCenter(clipInfo);
                    MaskInfoData maskInfoData = maskInfoDataMap.get(mSelectedClipPosition);
                    if (maskInfoData != null) {
                        mVideoFragment.getMaskZoomView().setMaskTypeAndInfo(maskInfoData.getMaskType(),
                                maskInfoData);
                        menuAdapter.setMaskType(maskInfoData.getMaskType());
                        //有蒙版信息 去添加蒙版信息
                        //Mask information already exists to add mask information
                    } else {
                        // 清除蒙版信息
                        // clear mask information
                        menuAdapter.setSelectPos(0);
                        mVideoFragment.getMaskZoomView().clear();
                        mVideoFragment.getMaskZoomView().setVisibility(View.GONE);
                    }
                    menuRecyclerView.scrollToPosition(0);
                    if (menuAdapter.getSelectPos() > 3) {
                        menuRecyclerView.scrollToPosition(menuAdapter.getSelectPos());
                    }
                }
                playCurrentClip(mSelectedClipPosition);
                // 如果有蒙版展示蒙版详情没有则展示添加蒙版
                // If there is a mask, show the details of the mask, then show that the mask is added
                showMaskMenu(false);
            }
        });
    }

    /**
     * 设置MaskView的中心点
     * Set the center point of MaskView
     *
     * @param clipInfo
     */
    private void setMaskCenter(ClipInfo clipInfo) {
        if (clipInfo == null) return;
        BackGroundInfo backGroundInfo = clipInfo.getBackGroundInfo();
        float transX = 0, transY = 0, scaleX = 1, rotation = 0;
        NvsVideoResolution videoResolution = TimelineData.instance().getVideoResolution();
        if (backGroundInfo != null && videoResolution != null) {
            transX = backGroundInfo.getTransX() / videoResolution.imageWidth * mVideoFragment.getLiveWindow().getWidth();
            transY = backGroundInfo.getTransY() / videoResolution.imageHeight * mVideoFragment.getLiveWindow().getHeight();
            Log.d(TAG, "=-= setMaskCenter transX:" + backGroundInfo.getTransX() + "transY:" + backGroundInfo.getTransY());
            scaleX = backGroundInfo.getScaleX();
            rotation = backGroundInfo.getRotation();
        }
        mVideoFragment.getMaskZoomView().setBackgroundInfo(transX, transY, rotation, scaleX);
    }

    private void initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null)
            return;
        NvsVideoTrack mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }
        removeTimelineAnimal(mTimeline);
    }

    /**
     * 去除timeline动画
     * removeTimelineAnimal
     *
     * @param timeline
     */
    private void removeTimelineAnimal(NvsTimeline timeline) {
        if (timeline == null)
            return;
        NvsVideoTrack mVideoTrack = timeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }
        int clipCount = mVideoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = mVideoTrack.getClipByIndex(i);
            if (videoClip != null) {
                NvsVideoFx propertyVideoFx = videoClip.getPropertyVideoFx();
                if (propertyVideoFx != null) {
                    propertyVideoFx.setStringVal(NvsConstants.ANIMATION_POST_PACKAGE_ID, "");
                }
            }
            if (i < clipCount - 1) {
                mVideoTrack.setBuiltinTransition(i, "");
                mVideoTrack.setPackagedTransition(i, "");
            }
        }
    }

    /**
     * 初始化蒙版菜单数据
     * init the mask menus
     */
    private void initMaskMenus() {
        menuAdapter = new MaskAdapter(MaskUtils.getMaskMenus(getApplicationContext()), getApplicationContext());
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        menuRecyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(new MaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MaskBean maskBean) {
                if (maskBean != null) {
                    if (mSelectedClipPosition < clipInfoData.size()) {
                        ClipInfo clipInfo = clipInfoData.get(mSelectedClipPosition);
                        if (clipInfo == null) {
                            return;
                        }
                        mVideoFragment.getMaskZoomView().setMaskTypeAndInfo(maskBean.getMaskType(), maskInfoDataMap.get(mSelectedClipPosition));
                        MaskInfoData maskInfoData = mVideoFragment.getMaskZoomView().getMaskInfoData();
                        changeMaskByCrop(mVideoFragment.getLiveWindow(), clipInfo, maskInfoData);
                        maskInfoDataMap.put(mSelectedClipPosition, maskInfoData);
                        if (maskBean.getMaskType() == MaskInfoData.MaskType.TEXT) {
                            // 字幕蒙版弹窗 textMaskView dialog
                            if (inputDialog != null && !inputDialog.isShowing()) {
                                inputDialog.setUserInputText(maskInfoData.getText());
                                inputDialog.show();
                            }
                        }
                    }
                }
                menuAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initListener() {
        maskReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mSelectedClipPosition >= 0 添加是为了增加扩展，避免后期出现不选择现象
                //The addition is to increase the expansion and avoid the phenomenon of non-selection in the later stage
                if (mSelectedClipPosition >= 0 && clipInfoData != null && clipInfoData.size() > mSelectedClipPosition) {
                    ClipInfo clipInfo = clipInfoData.get(mSelectedClipPosition);
                    if (clipInfo != null) {
                        MaskInfoData maskInfoData = mVideoFragment.getMaskZoomView().getMaskInfoData();
                        if (maskInfoData != null) {
                            maskInfoData.setReverse(!maskInfoData.isReverse());
                            applyMaskInfo(clipInfo, maskInfoData);
                        }
                    }
                }
            }
        });
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
//                playCurrentClip(mSelectedClipPosition);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
//                playCurrentClip(mSelectedClipPosition);
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {

            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                    mVideoFragment.getMaskZoomView().setMaskViewVisibility(View.GONE);
                } else {
                    mVideoFragment.getMaskZoomView().setMaskViewVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 应用蒙版
     * applyMaskInfo
     *
     * @param clipInfo info
     */
    private void applyMaskInfo(ClipInfo clipInfo, MaskInfoData maskInfoData) {
        BackGroundInfo backGroundInfo = clipInfo.getBackGroundInfo();
        float transX = 0, transY = 0, scaleX = 1, rotation = 0;
        if (backGroundInfo != null) {
            transX = backGroundInfo.getTransX();
            transY = backGroundInfo.getTransY();
            scaleX = backGroundInfo.getScaleX();
            rotation = backGroundInfo.getRotation();
        }
        NvMaskHelper.buildRealMaskInfoData(maskInfoData, mVideoFragment.getLiveWindow(), rotation,
                transX, transY, scaleX, clipInfo.getFileRatio());
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        NvsVideoClip videoClip = null;
        int clipCount = videoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clipByIndex = videoTrack.getClipByIndex(i);
            if (clipByIndex != null && i == mSelectedClipPosition) {
                videoClip = clipByIndex;
            }
        }
        if (videoClip == null) {
            return;
        }
        TimelineUtil.applyMask(videoClip, maskInfoData);
        mVideoFragment.seekTimeline(videoClip.getInPoint(), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 通过裁剪重新计算蒙版
     * 注：如果裁剪使用crop特技，则需要重新计算。如果是mask generator特技则不需要
     * Recalculate the mask by cropping
     * Note: If crop stunt is used for cropping, recalculation is required. This parameter is not required for mask generator
     *
     * @param liveWindow   livewindow
     * @param clipInfo     clipInfo
     * @param maskInfoData data
     */
    private void changeMaskByCrop(NvsLiveWindow liveWindow, ClipInfo clipInfo, MaskInfoData maskInfoData) {
        if (!NvMaskHelper.calculateMaskByCrop(liveWindow, clipInfo, maskInfoData)) {
            return;
        }
        mVideoFragment.getMaskZoomView().updateMaskSize(maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight());
        applyMaskInfo(clipInfo, maskInfoData);
    }

}
