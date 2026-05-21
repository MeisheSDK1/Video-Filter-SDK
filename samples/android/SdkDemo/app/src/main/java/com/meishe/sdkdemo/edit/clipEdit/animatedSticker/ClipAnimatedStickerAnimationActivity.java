package com.meishe.sdkdemo.edit.clipEdit.animatedSticker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.meicam.sdk.NvsClipAnimatedSticker;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.animatesticker.customsticker.StickerAnimationFragment;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.HorizontalSeekBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.StickerInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/8/26.
 * @Description :片段贴纸动画(样式)
 * @Description :StickerAnimal(Style)
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ClipAnimatedStickerAnimationActivity extends BaseActivity {
    private final String TAG = "ClipStickerAnimal";
    private final int RESULT_CODE_ANIMATION_IN = 1001;
    private final int RESULT_CODE_ANIMATION_OUT = 1002;
    private final int RESULT_CODE_ANIMATION = 1003;

    private int IN_OUT_ANIMATION_DEFAULT_DURATION = 500;
    private int ANIMATION_DEFAULT_DURATION = 600;
    private int CATEGORY_IN_ANIMATION = 7;
    private int CATEGORY_OUT_ANIMATION = 8;
    private int CATEGORY_ANIMATION = 9;
    private CustomTitleBar mTitleBar;
    private HorizontalSeekBar seekBar;
    private LinearLayout bottomLayout;
    private ImageView ivFinish;
    private NvsTimeline mTimeline;
    private int mCurStickerZVal;
    private List<StickerInfo> mStickerInfoListClone;
    private NvsClipAnimatedSticker curAnimatedSticker;//当前选中的贴纸 The currently selected sticker
    private long mMaxDuration;
    private VideoFragment mVideoFragment;
    private FrameLayout animalFrame;
    private StickerAnimationFragment stickerAnimationFragment;
    private boolean loadAssetFlag = false;
    private long mCurrentTimelinePosition;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex;
    private ClipInfo mClipInfo;
    private NvsVideoClip curVideoClip;

    @Override
    protected int initRootView() {
        return R.layout.activity_sticker_animal;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.sticker_style_title_bar);
        seekBar = findViewById(R.id.sticker_style_seek_bar);
        seekBar.setTransformText(1000, 1);
        bottomLayout = findViewById(R.id.sticker_style_bottom_layout);
        animalFrame = findViewById(R.id.sticker_animal_frame);
        ivFinish = findViewById(R.id.sticker_style_finish);
        ivFinish.setOnClickListener(this);

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.animatedStickerStyle);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initAssetData();
        initFragments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAssetFlag = false;
    }

    @Override
    protected void initListener() {
        seekBar.setOnRangeListener(new HorizontalSeekBar.onRangeListener() {
            @Override
            public void onRange(float left, float right) {
                if (curAnimatedSticker == null) {
                    return;
                }
                int index = getStickerIndex(mCurStickerZVal);
                StickerInfo stickerInfo = mStickerInfoListClone.get(index);
                int leftValue = (int) (Float.parseFloat(String.format(getString(R.string.format_1f), left)) * 1000);
                int rightValue = (int) (Float.parseFloat(String.format(getString(R.string.format_1f), right)) * 1000);
                Log.d(TAG, "seekBar onRange left:" + left + " right:" + right + " leftValue:" + leftValue + " rightValue:" + rightValue);
                //组合动画与出入动画互斥(出入动画不互斥)。前者默认时长0.5s后者0.6s
                //Combined animation is mutually exclusive with in-out animation (in-out animation is not mutually exclusive). The default duration of the former is 0.5s. The default duration of the latter is 0.6s
                if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerPeriodAnimationPackageId())) {
                    if (leftValue <= 100) {
                        //组合动画最小值可设置成100ms The minimum value of combined animation can be set to 100ms
                        leftValue = 100;
                        seekBar.setLeftProgress(leftValue);
                    }
                    curAnimatedSticker.setAnimatedStickerAnimationPeriod(leftValue);
                    if (stickerInfo != null) {
                        stickerInfo.setPeriodAnimationDuration(leftValue);
                    }
                } else {
                    if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerInAnimationPackageId())) {
                        if (seekBar.getMaxProgress() - leftValue < IN_OUT_ANIMATION_DEFAULT_DURATION && TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerOutAnimationPackageId())) {
                            //如果设置入动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置出动画），且此时没有出动画，则把出动画时长设置成0
                            //If the remaining time after animation is set is less than 500ms by default (the default time for animation is 500ms, whether animation is set or not), and no animation is set at this time, the animation duration is set to 0
                            curAnimatedSticker.setAnimatedStickerOutAnimationDuration(0);
                        }
                        if (stickerInfo != null) {
                            stickerInfo.setInAnimationDuration(leftValue);
                        }
                        curAnimatedSticker.setAnimatedStickerInAnimationDuration(leftValue);
                    }
                    if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerOutAnimationPackageId())) {
                        //如果设置出动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置入动画），且此时没有入动画，则把入动画时长设置成0
                        //If the remaining time after animation is set is less than 500ms by default (the default time for entering and exiting animation is 500ms, whether animation is set or not), and no animation is entered at this time, the animation duration is set to 0
                        if (seekBar.getMaxProgress() - rightValue < IN_OUT_ANIMATION_DEFAULT_DURATION && TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerInAnimationPackageId())) {
                            curAnimatedSticker.setAnimatedStickerInAnimationDuration(0);
                        }
                        if (stickerInfo != null) {
                            stickerInfo.setOutAnimationDuration(rightValue);
                        }
                        curAnimatedSticker.setAnimatedStickerOutAnimationDuration(rightValue);
                    }
                }
                if (mVideoFragment != null) {
                    mVideoFragment.stopEngine();
                }
            }

            @Override
            public void onTouchUpLeft(boolean leftFlag) {
                Log.d(TAG, "seekBar onTouchUpLeft leftFlag:" + leftFlag);
                if (curAnimatedSticker == null) return;
                long playTimeStart = curAnimatedSticker.getInPoint();
                if (leftFlag) {
                    //组合动画或者入动画 combAnimation or inAnimation
                    playTimeStart = curAnimatedSticker.getInPoint();
                } else {
                    //出动画 outAnimation
                    if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerOutAnimationPackageId())) {
                        playTimeStart = curAnimatedSticker.getOutPoint() - curAnimatedSticker.getAnimatedStickerOutAnimationDuration() * 1000;
                    }
                }
                playVideo(playTimeStart, curAnimatedSticker.getOutPoint());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sticker_style_finish) {
            BackupData.instance().setAnimateStickerData(mStickerInfoListClone);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            AppManager.getInstance().finishActivity();
        }
    }

    /**
     * 初始化贴纸动画
     * Initialize sticker animation
     *
     * @return
     */
    private StickerAnimationFragment initAnimationFragment() {
        StickerAnimationFragment mAnimationFragment = new StickerAnimationFragment();
        Bundle bundle = new Bundle();
        if (curAnimatedSticker != null) {
            bundle.putString("periodAnimationId", curAnimatedSticker.getAnimatedStickerPeriodAnimationPackageId());
            bundle.putString("outAnimationId", curAnimatedSticker.getAnimatedStickerOutAnimationPackageId());
            bundle.putString("inAnimationId", curAnimatedSticker.getAnimatedStickerInAnimationPackageId());
        }
        mAnimationFragment.setArguments(bundle);
        mAnimationFragment.setStickerAnimationListener(new StickerAnimationFragment.OnStickerAnimationStateListener() {
            @Override
            public void onFragmentLoadFinished() {

            }

            @Override
            public void onLoadMore(int typ) {
                if (loadAssetFlag)
                    return;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreCaptionStyle);
                bundle.putInt("assetType", typ);
                int requestCode;
                if (typ == NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION) {
                    bundle.putInt("categoryId", CATEGORY_IN_ANIMATION);
                    requestCode = RESULT_CODE_ANIMATION_IN;
                } else if (typ == NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION) {
                    bundle.putInt("categoryId", CATEGORY_OUT_ANIMATION);
                    requestCode = RESULT_CODE_ANIMATION_OUT;
                } else {
                    bundle.putInt("categoryId", CATEGORY_ANIMATION);
                    requestCode = RESULT_CODE_ANIMATION;
                }
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, requestCode);
                loadAssetFlag = true;
            }

            @Override
            public void onItemClick(int pos, int type, AssetItem selectedItem) {
                if (pos < 0 || selectedItem == null)
                    return;
                if (curAnimatedSticker != null) {
                    applyStickerAnimation(type, selectedItem);
                    displayAnimationProgress(pos > 0 && selectedItem != null, type);
                    long time = curAnimatedSticker.getInPoint();
                    if (type == NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION) {
                        time = curAnimatedSticker.getOutPoint() - curAnimatedSticker.getAnimatedStickerOutAnimationDuration() * 1000;
                    }
                    seekTimeline(time);
                    playVideo(mStreamingContext.getTimelineCurrentPosition(mTimeline), curAnimatedSticker.getOutPoint());
                }
            }
        });
        return mAnimationFragment;
    }

    private void playVideo(long startTime) {
        playVideo(startTime, -1);
    }

    private void playVideo(long startTime, long endTime) {
        Log.d(TAG, "playVideo startTime:" + startTime + " endTime:" + endTime);
        if (mVideoFragment != null && curAnimatedSticker != null) {
            if (startTime < 0) {
                startTime = 0;
            }
            mCurrentTimelinePosition = startTime;
            mVideoFragment.playVideo(startTime, endTime);
        }
    }

    /**
     * 贴纸动画生效
     * Sticker animation takes effect
     *
     * @param type
     * @param selectedItem
     */
    private void applyStickerAnimation(int type, AssetItem selectedItem) {
        if (curAnimatedSticker == null) return;
        int index = getStickerIndex(mCurStickerZVal);
        StickerInfo stickerInfo = mStickerInfoListClone.get(index);
        boolean applyStickerAnimation = false;
        if (type == NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION) {
            if (stickerInfo != null) {
                stickerInfo.setPeriodAnimationId("");
                stickerInfo.setInAnimationId(selectedItem.getAsset().uuid);
                stickerInfo.setInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
            }
            curAnimatedSticker.applyAnimatedStickerPeriodAnimation("");
            applyStickerAnimation = curAnimatedSticker.applyAnimatedStickerInAnimation(selectedItem.getAsset().uuid);
            curAnimatedSticker.setAnimatedStickerInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
        } else if (type == NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION) {
            curAnimatedSticker.applyAnimatedStickerPeriodAnimation("");
            applyStickerAnimation = curAnimatedSticker.applyAnimatedStickerOutAnimation(selectedItem.getAsset().uuid);
            if (stickerInfo != null) {
                stickerInfo.setPeriodAnimationId("");
                stickerInfo.setOutAnimationId(selectedItem.getAsset().uuid);
                if (stickerInfo.getOutAnimationDuration() == 0) {
                    stickerInfo.setOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                    curAnimatedSticker.setAnimatedStickerOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
            }
        } else if (type == NvAsset.ASSET_ANIMATED_STICKER_ANIMATION) {
            curAnimatedSticker.applyAnimatedStickerOutAnimation("");
            curAnimatedSticker.applyAnimatedStickerInAnimation("");
            applyStickerAnimation = curAnimatedSticker.applyAnimatedStickerPeriodAnimation(selectedItem.getAsset().uuid);
            if (stickerInfo != null) {
                stickerInfo.setPeriodAnimationId(selectedItem.getAsset().uuid);
                stickerInfo.setInAnimationId("");
                stickerInfo.setOutAnimationId("");
                stickerInfo.setInAnimationDuration(0);
                stickerInfo.setOutAnimationDuration(0);
                if (stickerInfo.getPeriodAnimationDuration() == 0) {
                    stickerInfo.setPeriodAnimationDuration(ANIMATION_DEFAULT_DURATION);
                    curAnimatedSticker.setAnimatedStickerAnimationPeriod(ANIMATION_DEFAULT_DURATION);
                }
            }
        }
        Log.d(TAG, "applyStickerAnimation type:" + type + " result:" + applyStickerAnimation);
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_CODE_ANIMATION_IN:
                //入场动画 Entrance animation
                if (stickerAnimationFragment != null) {
                    stickerAnimationFragment.refreshAnimationIn();
                }
                break;
            case RESULT_CODE_ANIMATION_OUT:
                if (stickerAnimationFragment != null) {
                    stickerAnimationFragment.refreshAnimationOut();
                }
                break;
            case RESULT_CODE_ANIMATION:
                if (stickerAnimationFragment != null) {
                    stickerAnimationFragment.refreshAnimation();
                }
                break;

            default:
                break;
        }
    }

    private void initAssetData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        mCurStickerZVal = BackupData.instance().getStickerZVal();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size()) {
            return;
        }
        mClipInfo = mClipArrayList.get(mCurClipIndex);
        mTimeline = TimelineUtil.createSingleClipTimeline(mClipInfo, false);
        if (mTimeline == null || mClipInfo == null) {
            return;
        }
        mStickerInfoListClone = BackupData.instance().cloneStickerInfoList();
        if (mStickerInfoListClone == null) mStickerInfoListClone = new ArrayList<>();
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            curVideoClip = videoTrack.getClipByIndex(0);
        }
        if (curVideoClip == null) {
            return;
        }
        TimelineUtil.applyClipAnimatedSticker(curVideoClip, mStickerInfoListClone);
        selectSticker();
    }

    private void initFragments() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                seekTimeline(BackupData.instance().getCurSeekTimelinePos());
                if (curAnimatedSticker == null) {
                    selectSticker();
                }
                ivFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (curAnimatedSticker != null) {
                            mVideoFragment.setCurAnimateSticker(curAnimatedSticker);
                            mVideoFragment.updateAnimateStickerCoordinate(curAnimatedSticker);
                            mVideoFragment.changeStickerRectVisible();
                        }
                    }
                }, 100);
            }
        });
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                if (curAnimatedSticker != null && mCurrentTimelinePosition >= curAnimatedSticker.getInPoint()) {
                    seekTimeline(mCurrentTimelinePosition);
                }
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {

            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mVideoFragment.setDrawRectVisible(View.GONE);
                } else {
                    mVideoFragment.changeStickerRectVisible();
                }
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        /*
         * 设置字幕模式
         * Set caption mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_STICKER);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", bottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.sticker_style_spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);

        stickerAnimationFragment = initAnimationFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.sticker_animal_frame, stickerAnimationFragment)
                .commit();
        getSupportFragmentManager().beginTransaction().show(stickerAnimationFragment);
    }

    private int getStickerIndex(int zValue) {
        int index = -1;
        if (mStickerInfoListClone != null) {
            int count = mStickerInfoListClone.size();
            for (int i = 0; i < count; ++i) {
                int zVal = mStickerInfoListClone.get(i).getAnimateStickerZVal();
                if (zValue == zVal) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private void seekTimeline(long timestamp) {
        if (mVideoFragment != null) {
            mVideoFragment.seekTimeline(timestamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
        }

    }

    /**
     * 设置选中的贴纸
     * Set the selected sticker
     */
    private void selectSticker() {
        long curPos = BackupData.instance().getCurSeekTimelinePos();
        List<NvsClipAnimatedSticker> animatedStickers = curVideoClip.getAnimatedStickersByClipTimePosition(curPos);
        int captionCount = animatedStickers.size();
        for (int index = 0; index < captionCount; index++) {
            int tmpZVal = (int) animatedStickers.get(index).getZValue();
            if (mCurStickerZVal == tmpZVal) {
                curAnimatedSticker = animatedStickers.get(index);
                checkInit();
                break;
            }
        }
    }

    /**
     * 控制是否展示seekbar
     * displaySeekBar
     *
     * @param visible
     */
    private void displaySeekBar(boolean visible) {
        if (visible) {
            if (seekBar.getVisibility() != View.VISIBLE) {
                seekBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (seekBar.getVisibility() == View.VISIBLE) {
                seekBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 展示动画的进度--
     * 注意:出入动画和组合动画互斥
     * Show the progress of the animation-
     * note: the in and out animation and the combined animation are mutually exclusive
     */
    private void displayAnimationProgress(boolean visible, int type) {
        displaySeekBar(visible);
        if (!visible || curAnimatedSticker == null) return;
        if (type == NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION) {
            if (TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerInAnimationPackageId())) {
                seekBar.setLeftMoveIcon(0);
            } else {
                //1.判断是否有出动画 无则重置
                if (TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerOutAnimationPackageId())) {
                    seekBar.reset();
                    seekBar.setRightMoveIcon(0);
                    seekBar.setMoveIconSize(20, 35);
                }
                int duration = curAnimatedSticker.getAnimatedStickerInAnimationDuration();
                seekBar.setMoveIconLowPadding(10);
                seekBar.setLeftMoveIcon(R.mipmap.bar_left);
                seekBar.setLeftProgress(duration);
            }
        } else if (type == NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION) {
            if (TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerOutAnimationPackageId())) {
                seekBar.setRightMoveIcon(0);
            } else {
                //1.判断是否有入动画 无则重置
                //Determine whether there is an incoming animation, reset if not
                if (TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerInAnimationPackageId())) {
                    seekBar.reset();
                    seekBar.setMoveIconSize(20, 35);
                    seekBar.setLeftMoveIcon(0);
                }
                int duration = curAnimatedSticker.getAnimatedStickerOutAnimationDuration();
                seekBar.setMoveIconLowPadding(10);
                seekBar.setRightMoveIcon(R.mipmap.bar_right);
                seekBar.setRightProgress(duration);
            }
        } else {
            if (TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerPeriodAnimationPackageId())) {
                seekBar.reset();
                seekBar.setVisibility(View.GONE);
                return;
            }
            int duration = curAnimatedSticker.getAnimatedStickerAnimationPeriod();
            seekBar.reset();
            seekBar.setMoveIconSize(20, 20);
            seekBar.setLeftMoveIcon(R.mipmap.round_white);
            seekBar.setLeftProgress(duration);
        }
    }

    /**
     * 检查并初始化显示贴纸动画信息
     * Check and initialize the display of sticker animation information
     */
    private void checkInit() {
        if (curAnimatedSticker != null) {
            long duration = curAnimatedSticker.getOutPoint() - curAnimatedSticker.getInPoint();
            seekBar.setMaxProgress((int) (duration / 1000));
            mMaxDuration = duration;
            if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerPeriodAnimationPackageId())) {
                displayAnimationProgress(true, NvAsset.ASSET_ANIMATED_STICKER_ANIMATION);
            } else {
                if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerOutAnimationPackageId())) {
                    displayAnimationProgress(true, NvAsset.ASSET_ANIMATED_STICKER_OUT_ANIMATION);
                }
                if (!TextUtils.isEmpty(curAnimatedSticker.getAnimatedStickerInAnimationPackageId())) {
                    displayAnimationProgress(true, NvAsset.ASSET_ANIMATED_STICKER_IN_ANIMATION);
                }
            }
        } else {
            Log.e(TAG, "curAnimatedSticker is null,mCurStickerZVal=" + mCurStickerZVal);
        }
    }
}
