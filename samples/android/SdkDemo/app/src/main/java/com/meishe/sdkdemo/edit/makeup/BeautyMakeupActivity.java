package com.meishe.sdkdemo.edit.makeup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.arscene.bean.CompoundFxInfo;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.makeup.MakeupDataManager;
import com.meishe.makeup.MakeupHelper;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.makeup.makeup.MakeupCategory;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.EditMakeUpView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2021/11/08.
 * @Description : 美妆
 * @Description : makeup activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyMakeupActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String MAKEUP_MAP = "makeup_map";
    private CustomTitleBar mTitleBar;
    private LinearLayout mBottomLayout;
    private ImageView mMakeupFinish;
    private VideoFragment mVideoFragment;

    private NvsTimeline mTimeline;
    private EditMakeUpView mMakeUpView;

    private MakeupHelper mMakeupHelper;
    private HashMap<String, Makeup> mSelectedMakeupMap;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        getNvAssetManager();
        return R.layout.activity_beauty_makeup;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mMakeupFinish = (ImageView) findViewById(R.id.iv_makeup_finish);
        mMakeUpView = (EditMakeUpView) findViewById(R.id.make_up_view);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.makeup);
        mTitleBar.setBackImageVisible(View.GONE);
        mTitleBar.setTextRightVisible(View.GONE);
    }

    @Override
    protected void initData() {
        if (initTimeline()) {
            return;
        }
        mMakeupHelper = new MakeupHelper(mStreamingContext, BuildConfig.FACE_MODEL == 240, false);
        initVideoFragment();

        Intent intent = getIntent();
        if (intent != null) {
            Serializable serializableExtra = intent.getSerializableExtra(MAKEUP_MAP);
            if (serializableExtra != null) {
                mSelectedMakeupMap = (HashMap<String, Makeup>) serializableExtra;
            }
        }
        if (mSelectedMakeupMap == null) {
            //使用HashMap保持先后顺序，最后添加的最后生效
            //The HashMap is used to maintain order, with the last addition taking effect
            mSelectedMakeupMap = new HashMap<>(6);
        }
        setMakeupEffect();
        mMakeUpView.setMakeupCategoryList(MakeupDataManager.getMakeupCategoryList(this, true), mSelectedMakeupMap);
        postSeekAction();
    }

    private void setMakeupEffect() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        int clips = videoTrack.getClipCount();
        if (mSelectedMakeupMap != null) {
            for (int i = 0; i < clips; i++) {
                NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
                if (videoClip == null) {
                    continue;
                }
                for (Map.Entry<String, Makeup> entry : mSelectedMakeupMap.entrySet()) {
                    Makeup value = entry.getValue();
                    mMakeupHelper.applyMakeupFx(value, videoClip);
                }
            }
        }
    }


    @Override
    protected void initListener() {
        mMakeupFinish.setOnClickListener(this);

        mMakeUpView.setOnMakeUpEventListener(new EditMakeUpView.MakeUpEventListener() {
            @Override
            public void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup) {
                onMakeupDataChanged(tabPosition, position, isClearMakeup);
                postSeekAction();
            }

            @Override
            public void onMakeupColorChanged(String makeupId, NvsColor color) {
                makeupColorChanged(makeupId, color);
                postSeekAction();
            }

            @Override
            public void onMakeupIntensityChanged(String makeupId, float intensity) {
                makeupIntensityChanged(makeupId, intensity);
                postSeekAction();
            }

            @Override
            public void removeVideoFxByName(String name) {

            }

            @Override
            public void onMakeUpViewDismiss() {
            }
        });

    }

    /**
     * 美妆强度变化
     * Beauty intensity changes
     *
     * @param makeupId
     * @param intensity
     */
    private void makeupIntensityChanged(String makeupId, float intensity) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = videoTrackByIndex.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }
            mMakeupHelper.setMakeupIntensity(videoClip, makeupId, intensity);
        }

    }

    /**
     * 颜色变化
     * Color change
     *
     * @param makeupId
     * @param color
     */
    private void makeupColorChanged(String makeupId, NvsColor color) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = videoTrackByIndex.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }
            mMakeupHelper.setMakeupColor(videoClip, makeupId, color);
        }

    }

    private void onMakeupDataChanged(int tabPosition, int position, boolean isClearMakeup) {
        if (mTimeline == null) {
            return;
        }
        Makeup selectItem = mMakeUpView.getSelectItem();
        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = videoTrackByIndex.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }
            NvsVideoFx nvsVideoFx = mMakeupHelper.checkBuildClipArSceneFx(videoClip);
            if (nvsVideoFx != null) {
                //这里整装和单妆互斥，所以需先移除整妆
                //Full and single makeup are mutually exclusive here, so remove the full makeup first
                nvsVideoFx.setStringVal("Makeup Compound Package Id", null);
            }
            //针对clip上面的特效
            //For the effects on clip
            if (tabPosition == 0) {
                clearShape(i, nvsVideoFx);
                // 妆容清理全部的单妆
                //Clean up all your makeup
                clearAllCustomMakeup(nvsVideoFx);
                // 清除重置妆容的所有效果，目前旧逻辑只清除了下边的滤镜，暂时保留旧的逻辑
                //Clear all effects of make-up reset. Currently, Old logic only clears the lower filter. Keep old logic for now
                mMakeupHelper.resetMakeupFx(mSelectedMakeupMap.get(mMakeUpView.getSelectMakeupId()), videoClip);
                // 清理滤镜
                //Clean filter
                mMakeupHelper.resetMakeupFilter(mSelectedMakeupMap.get(mMakeUpView.getSelectMakeupId()), videoClip);
                if (position > 0) {
                    if (null != selectItem) {
                        String assetsDirectory = selectItem.getAssetsDirectory();
                        File file = new File(assetsDirectory);
                        selectItem = mMakeupHelper.applyMakeupFx(file, videoClip);
                        if (null != selectItem) {
                            selectItem.setAssetsDirectory(assetsDirectory);
                        }
                    }
                }
            } else {
                if (position == 0) {
                    //点击清理之前的单妆
                    //Click to clean up the previous single makeup
                    resetCustomMakeup(nvsVideoFx, mMakeUpView.getSelectMakeupId());
                } else {
                    mMakeupHelper.applyMakeupFx(selectItem, videoClip);
                }
            }
        }
        if (position == 0) {
            if (tabPosition == 0) {
                mSelectedMakeupMap.clear();
                ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
                if (videoClipArray != null) {
                    for (ClipInfo clipInfo : videoClipArray) {
                        clipInfo.setBeautyFxInfo(null);
                    }
                }
            } else {
                mSelectedMakeupMap.remove(mMakeUpView.getSelectMakeupId());
            }
        } else {
            mSelectedMakeupMap.put(mMakeUpView.getSelectMakeupId(), selectItem);
        }
    }

    /**
     * 清除编辑-美颜中添加的美型和微整形效果，以妆容效果为准
     * Clear Edit - Beauty added beauty and micro shape effect, based on the makeup effect
     *
     * @param i          position
     * @param nvsVideoFx videofx
     */
    private void clearShape(int i, NvsVideoFx nvsVideoFx) {
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        if ((null == nvsVideoFx) || (i > clipInfos.size())) {
            return;
        }
        ClipInfo clipInfo = clipInfos.get(i);
        if (null == clipInfo) {
            return;
        }
        BeautyFxInfo beautyFxInfo = clipInfo.getBeautyFxInfo();
        if (null == beautyFxInfo) {
            return;
        }
        HashSet<IFxInfo> infos = beautyFxInfo.getBeautys();
        if ((null == infos) || infos.isEmpty()) {
            return;
        }
        for (IFxInfo info : infos) {
            if (info instanceof CompoundFxInfo && !TextUtils.isEmpty(info.getPackageId())) {
                CompoundFxInfo compoundFxInfo = (CompoundFxInfo) info;
                String fxName = compoundFxInfo.getFxName();
                if (!TextUtils.isEmpty(fxName)) {
                    nvsVideoFx.setFloatVal(fxName, 0);
                }
            }
        }
    }


    /**
     * 删除单妆和妆容
     * Remove makeup
     */
    private void clearAllCustomMakeup(NvsVideoFx videoFx) {
        List<MakeupCategory> makeupCategoryList = mMakeUpView.getMakeupCategoryList();
        for (MakeupCategory categoryInfo : makeupCategoryList) {
            if (categoryInfo.getMaterialType() == 21) {
                continue;
            }
            categoryInfo.getCategoryContent().setSelectedPosition(0);
            resetCustomMakeup(videoFx, Util.upperCaseName(categoryInfo.getDisplayName()));
        }
    }


    public void resetCustomMakeup(NvsVideoFx videoFx, String makeupType) {
        mMakeupHelper.setMakeupIntensity(videoFx, makeupType, EditMakeUpView.DEFAULT_MAKEUP_INTENSITY);
        mMakeupHelper.setMakeupColor(videoFx, makeupType, new NvsColor(0, 0, 0, 0));
        mMakeupHelper.setMakeupPackageId(videoFx, makeupType, "");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_makeup_finish) {//clearAllBeauty();
            makeupFinish();
        }
    }

    private boolean initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return true;
        }
        return false;
    }

    private void postSeekAction() {
        mBottomLayout.post(() -> mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME));
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", true);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    private void makeupFinish() {
        mVideoFragment.stopEngine();
        removeTimeline();
        Intent intent = new Intent();
        intent.putExtra(MAKEUP_MAP, mSelectedMakeupMap);
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }

    private void clearAllBeauty() {
        if (null == mSelectedMakeupMap) {
            return;
        }
        Makeup makeup = mSelectedMakeupMap.get(MakeupDataManager.COMBINED_MAKEUP_TYPE);
        if (null == makeup) {
            return;
        }
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        for (ClipInfo clipInfo : clipInfos) {
            clipInfo.setBeautyFxInfo(null);
        }
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }


}