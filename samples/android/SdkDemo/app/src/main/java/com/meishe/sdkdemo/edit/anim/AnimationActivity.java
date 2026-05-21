package com.meishe.sdkdemo.edit.anim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
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
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.anim.view.AnimationBottomView;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.utils.ToastUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.AnimationInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : Jml
 * @CreateDate : 2020/8/24.
 * @Description :动画Activity
 * @Description :animationActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */

public class AnimationActivity extends BaseActivity {

    private static final String TAG = "AnimationActivity";
    private static final int ANIMATION_REQUESTLIST = 101;
    private VideoFragment mVideoFragment;
    private CustomTitleBar mTitleBar;
    private RecyclerView mAnimationClipRecyclerView;
    private ImageView mAnimationAssetFinish;
    private RelativeLayout mBottomLayout;
    private AnimationClipAdapter mAnimationClipAdapter;
    private AnimationBottomView mAnimationBottomView;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;

    private NvsVideoTrack mVidoeTrack;

    /**
     * 动画特效的数据结构,保存给videoEdit页面使用 和 第一次初始化时候使用
     * 期间修改动画或者修改动画时间 保存数据
     * The data structure of the animation special effects is saved
     * for use on the videoEdit page and used during the first initialization
     * to modify the animation or modify the animation time. Save the data
     */
    private Map<Integer, AnimationInfo> mAnimationFxMap = new TreeMap<>();
    private int mAssetType = NvAsset.ASSET_ANIMATION_IN;
    private LinearLayout mAnimationInLayout, mAnimationOutLayout, mAnimationCompanyLayout;
    private int mSelectedClipPosition = 0;
    /**
     * 每个片段对应的特效对象 当前页面使用，设置的动画
     * The special effect object corresponding to each
     * segment The animation used and set on the current page
     */
    private Map<Integer, NvsVideoFx> mNvsVideoFxMap;
    /**
     * 每个片段--对应的进度集合
     * Each segment-the corresponding progress set
     * key -- 片段坐标
     * value -- 该片段选择过的所有特效的id -- 和对应的 value
     */
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, Long>> mClipVideoFxAnimationDurationMap;
    private boolean mShowAnimationList = false;
    private static final float DEFAULT_DURATION_IN = 0.5f;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_animation;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mAnimationClipRecyclerView = (RecyclerView) findViewById(R.id.clip_list);
        mAnimationAssetFinish = (ImageView) findViewById(R.id.animationAssetFinish);
        mAnimationOutLayout = findViewById(R.id.ll_animation_out);
        mAnimationCompanyLayout = findViewById(R.id.ll_animation_company);
        mAnimationInLayout = findViewById(R.id.ll_animation_in);
        mAnimationOutLayout = findViewById(R.id.ll_animation_out);
        mAnimationCompanyLayout = findViewById(R.id.ll_animation_company);
        mAnimationBottomView = findViewById(R.id.animation_bottom);
        mBottomLayout = findViewById(R.id.bottom_layout);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.animation);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        init();
        initVideoFragment();
        initAnimationClipList();
        initAnimationViewList();

    }


    @Override
    protected void initListener() {
        mAnimationAssetFinish.setOnClickListener(this);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                mVideoFragment.setDrawRectVisible(View.GONE);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
            }
        });
        mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
            @Override
            public void onAssetDelete() {
            }

            @Override
            public void onAssetSelected(PointF curPoint) {

            }

            @Override
            public void onAssetTranslation() {
            }

            @Override
            public void onAssetScale() {
            }

            @Override
            public void onAssetAlign(int alignVal) {
            }

            @Override
            public void onAssetHorizFlip(boolean isHorizFlip) {
            }
        });
        mVideoFragment.setThemeCaptionSeekListener(new VideoFragment.OnThemeCaptionSeekListener() {
            @Override
            public void onThemeCaptionSeek(long stamp) {
            }
        });
        mAnimationInLayout.setOnClickListener(this);
        mAnimationOutLayout.setOnClickListener(this);
        mAnimationCompanyLayout.setOnClickListener(this);
        mBottomLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_animation_in) {
            if (mSelectedClipPosition == -1) {
                ToastUtil.showToast(AnimationActivity.this, getResources().getString(R.string.add_videoClip_first));
                return;
            }
            mAnimationBottomView.setVisibility(View.VISIBLE);
            mAnimationBottomView.setAssetType(NvAsset.ASSET_ANIMATION_IN);
            setAnimationListViewSelected(NvAsset.ASSET_ANIMATION_IN);
            mAssetType = NvAsset.ASSET_ANIMATION_IN;
            mShowAnimationList = true;
        } else if (id == R.id.ll_animation_out) {
            if (mSelectedClipPosition == -1) {
                ToastUtil.showToast(AnimationActivity.this, getResources().getString(R.string.select_videoClip_first));
                return;
            }
            mAnimationBottomView.setVisibility(View.VISIBLE);
            mAnimationBottomView.setAssetType(NvAsset.ASSET_ANIMATION_OUT);
            setAnimationListViewSelected(NvAsset.ASSET_ANIMATION_OUT);
            mAssetType = NvAsset.ASSET_ANIMATION_OUT;
            mShowAnimationList = true;
        } else if (id == R.id.ll_animation_company) {
            if (mSelectedClipPosition == -1) {
                ToastUtil.showToast(AnimationActivity.this, getResources().getString(R.string.select_videoClip_first));
                return;
            }
            mAnimationBottomView.setVisibility(View.VISIBLE);
            mAnimationBottomView.setAssetType(NvAsset.ASSET_ANIMATION_COMPANY);
            setAnimationListViewSelected(NvAsset.ASSET_ANIMATION_COMPANY);
            mAssetType = NvAsset.ASSET_ANIMATION_COMPANY;
            mShowAnimationList = true;
        } else if (id == R.id.animationAssetFinish) {
            if (mShowAnimationList) {
                return;
            }

            quitActivity();
        } else if (id == R.id.bottom_layout) {//当底部的动画列表显示的时候 不处理取消选中
            //When the animation list at the bottom is displayed, do not process the deselection
            if (mAnimationBottomView.getVisibility() == View.VISIBLE) {
                return;
            }
            //点击空白处播放全部视频
            //Click the blank space to play all videos
            playCurrentClip(-1, 0, mAssetType);
            //不设置选中片段
            //Do not set the selected fragment
            mSelectedClipPosition = -1;
            mAnimationClipAdapter.setSelectPos(mSelectedClipPosition);
            mAnimationBottomView.setSelectedClipPosition(mSelectedClipPosition);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ANIMATION_REQUESTLIST:
                if (null != mAnimationBottomView) {
                    mAnimationBottomView.initAnimationDataList(this);
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        mTimeline = TimelineUtil.createTimeline();
        //去掉专场
        //clearBuildInTransform(mTimeline);
        if (mTimeline == null) {
            return;
        }
        mVidoeTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVidoeTrack == null) {
            return;
        }
        mNvsVideoFxMap = new HashMap<>();
        mClipVideoFxAnimationDurationMap = new ConcurrentHashMap<>();
    }

    /**
     * 设置完成退出Activity
     * Exit Activity after setting
     */
    private void quitActivity() {
        ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        if (clipInfoData != null && mAnimationFxMap != null) {
            for (int i = 0; i < clipInfoData.size(); i++) {
                ClipInfo clipInfo = clipInfoData.get(i);
                if (clipInfo != null) {
                    clipInfo.setAnimationInfo(mAnimationFxMap.get(i));
                }
            }
        }
        removeTimeline();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }


    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mAnimationAssetFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mVideoFragment) {
                            playCurrentClip(mSelectedClipPosition, getClipDuration(mSelectedClipPosition), mAssetType);
                        }
                        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                    }
                }, 100);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setIsAnimationView(true);
        Bundle bundle = new Bundle();
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putBoolean("playBarVisible", true);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);

    }

    /**
     * 视频片段列表
     * Initialize the list of video clips
     */
    private void initAnimationClipList() {

        mAnimationClipAdapter = new AnimationClipAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAnimationClipRecyclerView.setLayoutManager(linearLayoutManager);
        mAnimationClipRecyclerView.setAdapter(mAnimationClipAdapter);
        final ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        //设置蒙层  Set Mask
        buildClipAnimationDuration(clipInfoData);
        mAnimationClipAdapter.setClipInfoList(clipInfoData);
        mAnimationClipAdapter.setTimeLine(mTimeline);
        mAnimationClipAdapter.setOnItemClickListener(new AnimationClipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= 0 && position < clipInfoData.size()) {

                    if (null != mVideoFragment) {
                        AnimationInfo animationInfo = mAnimationFxMap.get(position);
                        //如果当前设置了动画或者处在编辑动画的页面，播放动画长度
                        //If the animation is currently set or on the page of editing animation, play the animation length
                        if (null != animationInfo && mShowAnimationList) {

                            long duration = animationInfo.getmAnimationOut() - animationInfo.getmAnimationIn();
                            int assetType = animationInfo.getmAssetType();
                            playCurrentClip(position, duration, assetType);
                        } else {
                            playCurrentClip(position, getClipDuration(position), mAssetType);
                        }
                    }
                    //如果当前显示的是动画列表 切换到显示动画类型
                    //If the current animation list is displayed, switch to display animation type
                    if (mSelectedClipPosition == position) {
                        return;
                    }
                    mSelectedClipPosition = position;
                    mAnimationBottomView.setSelectedClipPosition(mSelectedClipPosition);
                   /* if(mAnimationBottomView.getVisibility() == View.VISIBLE){
                        mAnimationBottomView.setVisibility(View.GONE);
                    }*/
                    if (mAnimationBottomView.getVisibility() == View.VISIBLE) {
                        if (mAssetType == NvAsset.ASSET_ANIMATION_IN) {
                            mAnimationInLayout.performClick();
                        } else if (mAssetType == NvAsset.ASSET_ANIMATION_OUT) {
                            mAnimationOutLayout.performClick();
                        } else {
                            mAnimationCompanyLayout.performClick();
                        }
                    }
                }
            }


        });
    }

    /**
     * 构建视频片段的蒙层效果 初始化
     * Build the masking effect and initialization of the video clip
     *
     * @param clipInfoData
     */
    private void buildClipAnimationDuration(ArrayList<ClipInfo> clipInfoData) {
        if (null != clipInfoData && clipInfoData.size() > 0) {
            if (null != mAnimationFxMap) {
                for (int i = 0; i < clipInfoData.size(); i++) {
                    ClipInfo clipInfo = clipInfoData.get(i);
                    mAnimationFxMap.put(i, clipInfo.getAnimationInfo());
                }
            }
        }
    }

    /**
     * 动画特效列表
     * Initialize the animation effects list
     */
    private void initAnimationViewList() {
        mAnimationBottomView.setFunctionListener(new AnimationBottomView.OnFunctionListener() {
            @Override
            public void onItemClick(FilterItem filterItem, int position, float duration, int animationType) {
                String mAnimationId = filterItem.getPackageId();
                long value = getAnimationDurationValue(mAnimationId, animationType);
                appendAnimationFxToVideoPackage(mSelectedClipPosition, filterItem, value);
                mAnimationBottomView.setSelectedProgress(value);
                //播放片段，如果选择无，播放完整的片段
                //Play the clip. If you select None, play the complete clip
                if (position == 0) {
                    //removeVideoFx(mSelectedClipPosition,mNvsVideoFxMap.get(mSelectedClipPosition));
                    //播放完整片段
                    //play the complete clip
                    playCurrentClip(mSelectedClipPosition, getClipDuration(mSelectedClipPosition), mAssetType);

                    //把当前选择的片段对应的默认设置的所有动画时长设为默认值，不在保存之前的值
                    //Set all the animation duration corresponding to the default settings of the currently selected clip as the default value, not the value before saving
                    if (mClipVideoFxAnimationDurationMap.containsKey(mSelectedClipPosition)) {
                        mClipVideoFxAnimationDurationMap.remove(mSelectedClipPosition);
                    }
                    //这个赋值为设置蒙层使用
                    //This assignment is used to set the mask
                    value = 0;
                } else {

                    playCurrentClip(mSelectedClipPosition, value, mAssetType);
                    //保存到本地
                    //Save to local
                    saveCurrentClipVideoFxAnimationValue(filterItem, value);
                }

                //设置视频片段列表显示蒙层
                //Set video clip list display mask
                mAnimationClipAdapter.setAnimationDuration(mSelectedClipPosition, value, mAssetType);

            }

            @Override
            public void onConfirm() {
                mAnimationBottomView.setVisibility(View.GONE);
                mShowAnimationList = false;
            }

            @Override
            public void onLoadMore(int categoryId) {
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreAnimation);
                bundle.putInt("assetType", mAssetType);
                bundle.putInt("categoryId", categoryId);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, ANIMATION_REQUESTLIST);
            }

            @Override
            public void onSeekChanged(FilterItem filterItem, long progress) {
                NvsVideoFx animationVideoFx = mNvsVideoFxMap.get(mSelectedClipPosition);
                if (null != animationVideoFx) {
                    double amplitude = (progress * 1f / Constants.NS_TIME_BASE);
                    Log.d(TAG, "amplitude:" + amplitude + " progress:" + progress);
                    animationVideoFx.setExprVar("amplitude", amplitude);
                    if (mAssetType == NvAsset.ASSET_ANIMATION_OUT) {
                        long duration = getClipDuration(mSelectedClipPosition);
                        animationVideoFx.setFloatVal("Package Effect In", duration - progress);
                    } else {
                        animationVideoFx.setFloatVal("Package Effect Out", progress);
                    }
                } else {
                    appendAnimationFxToVideoPackage(mSelectedClipPosition, filterItem, progress);
                }
                if (null != mVideoFragment) {
                    playCurrentClip(mSelectedClipPosition, progress, mAssetType);
                }
                saveCurrentClipVideoFxAnimationValue(filterItem, progress);
                AnimationInfo animationInfo = mAnimationFxMap.get(mSelectedClipPosition);
                if (null != animationInfo) {
                    if (mAssetType == NvAsset.ASSET_ANIMATION_OUT) {
                        animationInfo.setmAnimationIn(getClipDuration(mSelectedClipPosition) - progress);
                    } else {
                        animationInfo.setmAnimationOut(progress);
                    }
                }
                mAnimationClipAdapter.setAnimationDuration(mSelectedClipPosition, progress, mAssetType);
            }
        });
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 设置当前选中的片段 选中的特效 并设置其动画时长
     * Set the currently selected clip, the selected special effect and set its animation duration
     *
     * @param animationType 动画类型不同，默认的动画的时间不同
     */
    private void setAnimationListViewSelected(int animationType) {
        AnimationInfo animationInfo = mAnimationFxMap.get(mSelectedClipPosition);
        String packageId = "";

        long value = 0;
        if (animationType == NvAsset.ASSET_ANIMATION_COMPANY) {
            value = getClipDuration(mSelectedClipPosition);
        } else {
            value = (long) (DEFAULT_DURATION_IN * 1000 * 1000);
        }
        if (null != animationInfo) {
            //如果设置了特效的话，就拿上次设置的时间显示
            //If special effects are set, the last set time will be displayed
            packageId = animationInfo.getmPackageId();
            if (!TextUtils.isEmpty(packageId)) {
                value = animationInfo.getmAnimationOut() - animationInfo.getmAnimationIn();
            }

        } else {
            //获取动画时长
            //Get animation duration
            value = getAnimationDurationValue(packageId, animationType);
        }
        mAnimationBottomView.setSelectedPackageId(packageId);
        mAnimationBottomView.setSelectedProgress(value);
        mAnimationBottomView.setMaxProgress(getClipDuration(mSelectedClipPosition));
    }

    /**
     * 添加动画特效
     * Add animation effects
     *
     * @param clipPosition 给第几个片段添加特效
     * @param filterItem   特效Id
     * @param duration     特效时长
     */
    private void appendAnimationFxToVideoPackage(int clipPosition, FilterItem filterItem, long duration) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(clipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return;
        }
        nvsVideoClip.enablePropertyVideoFx(true);
        NvsVideoFx mPositionerVideoFx;
        if (mNvsVideoFxMap.containsKey(mSelectedClipPosition)) {
            mPositionerVideoFx = mNvsVideoFxMap.get(mSelectedClipPosition);
        } else {
            mPositionerVideoFx = nvsVideoClip.getPropertyVideoFx();
        }
        if (mPositionerVideoFx == null) {
            return;
        }
        //默认设置为入动画对应的时间节点
        //The default setting is the time node corresponding to the animation
        long in = 0;
        long out = duration;
        //如果是空，选择无动画
        //If empty, select No Animation
        if (TextUtils.isEmpty(filterItem.getPackageId())) {
            out = 0;
        }
        //设置动画的起始结束时间点,如动画从0 —— end ,出动画从 duration-start——end  组合动画当前默认和入动画相同
        //Set the start and end time of the animation, such as the animation from 0 to end, and the animation from duration start to end.
        // The current default of the combined animation is the same as that of the incoming animation
        if (mAssetType == NvAsset.ASSET_ANIMATION_OUT) {
            long clipDuration = nvsVideoClip.getOutPoint() - nvsVideoClip.getInPoint();
            in = clipDuration - duration;
            out = clipDuration;
        }
        AnimationInfo animationInfo = new AnimationInfo();
        animationInfo.setmAnimationIn(in);
        animationInfo.setmAnimationOut(out);
        animationInfo.setmPackageId(filterItem.getPackageId());
        animationInfo.setPostPackage(filterItem.isPostPackage());
        animationInfo.setmAssetType(mAssetType);

        mPositionerVideoFx = TimelineUtil.applyAnimation(nvsVideoTrack, nvsVideoClip, animationInfo);
        mAnimationFxMap.put(clipPosition, animationInfo);
        mNvsVideoFxMap.put(mSelectedClipPosition, mPositionerVideoFx);
    }

    /**
     * 获取片段的时长
     * The duration of the videoClip by position
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
     * 获取当前选择的片段的起始位置
     * Get the starting position of the currently selected videoClip by position
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
     * Get the end position of the currently selected videoClip by position
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
     * 保存选择的特效 和 片段 及对应的值
     * Save the selected special effects and clips and their corresponding values
     *
     * @param filterItem
     * @param value
     */
    private void saveCurrentClipVideoFxAnimationValue(FilterItem filterItem, Long value) {
        //判断是否创建过该片段的集合
        ConcurrentHashMap<String, Long> mNvsVideoFxAnimationDurationMap = null;
        if (mClipVideoFxAnimationDurationMap.containsKey(mSelectedClipPosition)) {
            mNvsVideoFxAnimationDurationMap = mClipVideoFxAnimationDurationMap.get(mSelectedClipPosition);
        } else {
            mNvsVideoFxAnimationDurationMap = new ConcurrentHashMap<>();
            mClipVideoFxAnimationDurationMap.put(mSelectedClipPosition, mNvsVideoFxAnimationDurationMap);
        }
        if (!TextUtils.isEmpty(filterItem.getPackageId())) {
            mNvsVideoFxAnimationDurationMap.put(filterItem.getPackageId(), value);
        }
        //遍历集合，该视频所有的动画时长修改为刚才选择的时长
        //Traversing the set, all the animation duration of the video is modified to the duration just selected
        if (mNvsVideoFxAnimationDurationMap.size() > 0) {
            Set<String> keySet = mNvsVideoFxAnimationDurationMap.keySet();
            for (String key : keySet) {
                if (!key.equals(filterItem.getPackageId()) && containAnimationId(key)) {
                    mNvsVideoFxAnimationDurationMap.put(key, value);
                }
            }
        }

    }

    /**
     * 获取动画对应的时长情况
     * 1.先拿到默认的动画时长 不同动画类型市场不懂
     * 2.如果该动画类型之前设置过时长，则拿到这个时长作为动画的时长
     * <p>
     * Get the duration of the animation
     * 1. Get the default animation duration first. Different animation types don’t understand the market
     * 2. If the duration of the animation type is set before, then this duration will be used as the duration of the animation
     *
     * @param mAnimationId
     * @param animationType
     * @return
     */
    private long getAnimationDurationValue(String mAnimationId, int animationType) {
        //获取到特效对应的值,如果手动设置过 ，会在回调中保存，没设置过，则使用默认值
        //Get the value corresponding to the special effect. If it is set manually, it will be saved in the callback. If it is not set, the default value will be used
        //默认值 组合动画为最大时长，其他为0.5s
        //The default value of composite animation is the maximum duration, others are 0.5s
        long value = 0;
        if (animationType == NvAsset.ASSET_ANIMATION_COMPANY) {

            value = getClipDuration(mSelectedClipPosition);
        } else {
            value = (long) (DEFAULT_DURATION_IN * 1000 * 1000);
        }/*else{
            value = (long) (DEFAULT_DURATION_IN*1000*1000);
        }*/

        //mAnimationId 非空代表选择有效动画效果, 拿到这个packageId之前选择的动画时长设置
        //Non-empty means to select the effective animation effect and get the animation duration setting selected before this packageId
        if (!TextUtils.isEmpty(mAnimationId)) {
            if (mClipVideoFxAnimationDurationMap.containsKey(mSelectedClipPosition)) {
                ConcurrentHashMap<String, Long> map = mClipVideoFxAnimationDurationMap.get(mSelectedClipPosition);

                //遍历这个片段设置过的动画列表 ， 如果 当前类型的动画列表中包含这个，直接返回这个动画对应的时长,否则不处理value
                //Traverse the list of animations set for this clip. If the current type of animation list contains this,
                // directly return the corresponding duration of this animation. Otherwise, value will not be processed
                if (null != map && map.size() > 0) {

                    Set<String> keySet = map.keySet();
                    for (String key : keySet) {
                        if (!TextUtils.isEmpty(key) && containAnimationId(key)) {
                            value = map.get(key);
                            break;
                        }
                    }
                }
            }
        }

        return value;
    }

    /**
     * 该动画id 是否在当前的选择的动画类型列表中
     * Whether the animation id is in the currently selected animation type list
     *
     * @param animationId
     * @return
     */
    private boolean containAnimationId(String animationId) {
        //获取该片段之前设置过的动画时长,只修改当前选择的动画类型 出 入 组合
        //Get the animation duration set before the clip, and only modify the current selected animation type in and out combination
        List<FilterItem> packageList = mAnimationBottomView.getCurrentAnimationList();
        if (null == packageList || packageList.size() <= 0) {
            return false;
        }
        if (TextUtils.isEmpty(animationId)) {
            return false;
        }
        for (FilterItem item : packageList) {
            String packageId = item.getPackageId();
            if (!TextUtils.isEmpty(packageId) && packageId.equals(animationId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 播放视频
     * Play video
     *
     * @param animationDuration 动画时长 只播放动画部分
     * @param animationType     动画类型
     */
    private void playCurrentClip(int mSelectedClipPosition, long animationDuration, int animationType) {
        if (mTimeline == null) {
            return;
        }
        int clipCount = mTimeline.getVideoTrackByIndex(0).getClipCount();
        long playStartPoint = 0;
        long playEndPoint = 0;
        if (mSelectedClipPosition >= 0 && mSelectedClipPosition < clipCount) {
            playStartPoint = getClipStartTime(mSelectedClipPosition);
            playEndPoint = getClipEndTime(mSelectedClipPosition);
            if (playEndPoint > playStartPoint) {
                //出动画只播放结尾部分 入动画播放开始部分
                //Only the ending part of the output animation is played.
                // The beginning part of the input animation is played
                if (animationType == NvAsset.ASSET_ANIMATION_OUT) {
                    playStartPoint = playEndPoint - animationDuration;
                } else {
                    playEndPoint = playStartPoint + animationDuration;
                }
                //除了第一条片段，其他片段延0.5s播放时间，从开始向前
                //Except for the first clip, other clips will be played for 0.5s, starting from the beginning
                if (mSelectedClipPosition > 0) {
                    playStartPoint -= 0.5 * 1000 * 1000;
//                    playEndPoint -= 0.5 * 1000 * 1000;
                }
                mVideoFragment.setmPlaySeekBarMaxAndCurrent(playStartPoint, playEndPoint, playStartPoint, mTimeline.getDuration());
                //只播放动画部分
                //Play only the animated part
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
}
