package com.meishe.sdkdemo.edit.filter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsExpressionParam;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.bean.AdjustSpecialEffectsInfo;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.dialog.TipsDialog;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.AssetFxUtil;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meishe.sdkdemo.view.AdjustSpecialEffectsView;
import com.meishe.sdkdemo.view.FilterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate : 2019/10/29.
 * @Description :视频编辑-滤镜-BaseActivity
 * @Description :VideoEdit-Filter-BaseActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseFilterActivity extends BaseActivity {
    private static final int REQUEST_FILTER_LIST_CODE = 102;
    protected VideoFragment mVideoFragment;
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private ImageView mFilterAssetFinish;
    private ArrayList<FilterItem> mFilterItemArrayList;
    private int mAssetType = NvAsset.ASSET_FILTER;
    private NvAssetManager mAssetManager;
    protected FilterView mFilterView;
    protected NvsTimeline mTimeline;
    protected VideoClipFxInfo mVideoClipFxInfo;
    protected int mSelectedPos = 0;
    protected NvsStreamingContext mStreamingContext;
    private TipsDialog mTipsDialog;
    protected AdjustSpecialEffectsView mAdjustSpecialEffectsView;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_filter;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.filter_bottom_layout);
        mFilterView = (FilterView) findViewById(R.id.filterView);
        mFilterAssetFinish = (ImageView) findViewById(R.id.filterAssetFinish);
        mAdjustSpecialEffectsView = (AdjustSpecialEffectsView) findViewById(R.id.adjustSpecialEffectsView);
        mTipsDialog = new TipsDialog(this);
        mFilterView.setBlackTheme(true);
        initSubViews();
        if (mAdjustSpecialEffectsView != null) {
            mAdjustSpecialEffectsView.setOnItemProgressChangeListener(new AdjustSpecialEffectsView.OnItemProgressChangeListener() {
                @Override
                public void onProgressChange(AdjustSpecialEffectsInfo info, int progress, boolean fromUser) {
                    if (!fromUser) {
                        float maxVal = info.getMaxVal();
                        float minVal = info.getMinVal();
                        float diff = maxVal - minVal;
                        float strength = diff * progress / 100 + minVal;
                        info.setStrength(strength);
                        if (mVideoClipFxInfo != null) {
                            List<NvsVideoFx> filterFxs = TimelineUtil.getFilterFxs();
                            if (filterFxs != null) {
                                for (NvsVideoFx filterFx : filterFxs) {
                                    filterFx.setExprVar(info.getKey(), info.getStrength());
                                }
                                mVideoClipFxInfo.putFilterParam(info.getKey(), info.getStrength());
                            }
                            int streamingEngineState = mStreamingContext.getStreamingEngineState();
                            if (streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                            }
                        }
                    }
                }

                @Override
                public void onTouchUp(boolean touchUpFlag) {
                    if (touchUpFlag) {
                        mVideoFragment.playVideoButtonClick();
                    }
                }

                @Override
                public void onColorChange(AdjustSpecialEffectsInfo effectsInfo, int color) {
                    float alphaF = (Color.alpha(color) * 1.0f / 255f);
                    float red = (Color.red(color) * 1.0f / 255f);
                    float green = (Color.green(color) * 1.0f / 255f);
                    float blue = (Color.blue(color) * 1.0f / 255f);
                    effectsInfo.setColor(new NvsColor(red, green, blue, alphaF));
                    if (mVideoClipFxInfo != null) {
                        List<NvsVideoFx> filterFxs = TimelineUtil.getFilterFxs();
                        if (filterFxs != null) {
                            for (NvsVideoFx filterFx : filterFxs) {
                                filterFx.setExprObjectVar(effectsInfo.getKey(), effectsInfo.getColor());
                            }
                            mVideoClipFxInfo.putFilterColorParam(effectsInfo.getKey(), effectsInfo.getColor());
                        }
                        int streamingEngineState = mStreamingContext.getStreamingEngineState();
                        if (streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                            mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                        }
                    }
                }
            });

        }

    }

    protected void initSubViews() {

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.filter);
    }

    @Override
    protected void initData() {
        init();
        initFilterDataList();
        initVideoFragment();
        initFilterView();
        afterIntentInit();
    }

    @Override
    protected void initListener() {
        mFilterAssetFinish.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_FILTER_LIST_CODE:
                initFilterDataList();
                mFilterView.setFilterArrayList(mFilterItemArrayList);
                mSelectedPos = AssetFxUtil.getSelectedFilterPos(mFilterItemArrayList, mVideoClipFxInfo);
                mFilterView.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    protected void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    protected void quitActivity() {
        AppManager.getInstance().finishActivity();
    }

    private void init() {
        mTimeline = initTimeLine();
        mVideoClipFxInfo = initClipFxInfo();
        mAssetManager = getNvAssetManager();
        mAssetManager.searchLocalAssets(mAssetType);
        String bundlePath = "filter";
        mAssetManager.searchReservedAssets(mAssetType, bundlePath);
    }

    protected void afterIntentInit() {
    }

    protected abstract VideoClipFxInfo initClipFxInfo();

    protected abstract NvsTimeline initTimeLine();

    private void initFilterDataList() {
        mFilterItemArrayList = AssetFxUtil.getFilterData(this,
                getLocalData(),
                null,
                true,
                false);
    }

    private ArrayList<NvAsset> getBundleData() {
        return mAssetManager.getReservedAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private ArrayList<NvAsset> getLocalData() {
        return mAssetManager.getUsableAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putBoolean("playBarVisible", true);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                if (timeline.getDuration() - mStreamingContext.getTimelineCurrentPosition(mTimeline) <= 40000) {
                    mVideoFragment.updateCurPlayTime(0);
                    mVideoFragment.seekTimeline(0, 0);
                }
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                playbackTimelinePositionFromParent(timeline, stamp);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                streamingEngineStateChangedFromParent(state);
            }
        });
    }

    protected void streamingEngineStateChangedFromParent(int state) {
    }

    protected void playbackTimelinePositionFromParent(NvsTimeline timeline, long stamp) {
    }

    private void initFilterView() {
        mFilterView.setFilterArrayList(mFilterItemArrayList);
        mFilterView.initFilterRecyclerView(this);
        mSelectedPos = AssetFxUtil.getSelectedFilterPos(mFilterItemArrayList, mVideoClipFxInfo);
        // 只更改界面 不触发点击 Only changing the interface does not trigger clicking
        mFilterView.setIntensityLayoutVisible(mSelectedPos <= 0 ? View.INVISIBLE : View.VISIBLE);
        mFilterView.setIntensityTextVisible(View.VISIBLE);
        mFilterView.setIntensitySeekBarMaxValue(100);
        mFilterView.setFilterFxListBackgroud("#00000000");
        mFilterView.setFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void onItmeClick(View v, final int position) {
                final int currentSelectedPos = mFilterView.getSelectedPos();
                int count = mFilterItemArrayList.size();
                if (position < 0 || position >= count) {
                    return;
                }
                if (mSelectedPos == position) {
                    mVideoFragment.playVideoButtonClick();
                    return;
                }
                // 没有关键帧 直接替换  No key frame direct replacement
                if (mVideoClipFxInfo.getKeyFrameInfoMap().isEmpty()) {
                    mSelectedPos = position;
                    mFilterView.setIntensitySeekBarProgress(100);
                    if (position == 0) {
                        if (null != mAdjustSpecialEffectsView) {
                            mAdjustSpecialEffectsView.setVisibility(View.GONE);
                        }
                        mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                        mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                        mVideoClipFxInfo.setFxId(null);
                    } else {
                        FilterItem filterItem = mFilterItemArrayList.get(position);
                        String packageId = filterItem.getPackageId();
                        boolean isAdjust = getAdjustFilter(packageId);
                        if ((null != mAdjustSpecialEffectsView) && isAdjust) {
                            mVideoClipFxInfo.setIsAdjusted(filterItem.getIsAdjusted());
                            mAdjustSpecialEffectsView.setVisibility(View.VISIBLE);
                            mFilterView.setIntensitySeekBarVisibility(View.GONE);
                        } else {
                            if (mAdjustSpecialEffectsView != null) {
                                mAdjustSpecialEffectsView.setVisibility(View.GONE);
                            }
                            mFilterView.setIntensitySeekBarVisibility(View.VISIBLE);
                            if (isNeedShowSeekBarWhenChangeFilterFromParent()) {
                                mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                            }
                            int filterMode = filterItem.getFilterMode();
                            if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                                String filterName = filterItem.getFilterName();
                                mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                                mVideoClipFxInfo.setFxId(filterName);
                                mVideoClipFxInfo.setIsCartoon(filterItem.getIsCartoon());
                                mVideoClipFxInfo.setGrayScale(filterItem.getGrayScale());
                                mVideoClipFxInfo.setStrokenOnly(filterItem.getStrokenOnly());
                            } else {
                                mVideoClipFxInfo.setIsAdjusted(filterItem.getIsAdjusted());
                                mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                                mVideoClipFxInfo.setFxId(packageId);
                            }
                            mVideoClipFxInfo.setFxIntensity(1.0f);
                        }
                    }
                    onFilterChanged(position);
                    onFilterChanged(mTimeline, mVideoClipFxInfo);
                    if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                        mVideoFragment.playVideoButtonClick();
                    }
                    return;
                }
                // 有关键帧 进行dialog提示
                //There is a key frame for dialog prompt
                if ((mTipsDialog != null) && !mTipsDialog.isShowing()) {
                    mTipsDialog.setOnBtnClickListener(new TipsDialog.OnBtnClickListener() {
                        @Override
                        public void OnConfirmBtnClicked() {
                            mSelectedPos = position;
                            mFilterView.setIntensitySeekBarProgress(100);
                            if (position == 0) {
                                mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                                mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                                mVideoClipFxInfo.setFxId(null);
                            } else {
                                if (isNeedShowSeekBarWhenChangeFilterFromParent()) {
                                    mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                                }
                                FilterItem filterItem = mFilterItemArrayList.get(position);
                                int filterMode = filterItem.getFilterMode();
                                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                                    String filterName = filterItem.getFilterName();
                                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                                    mVideoClipFxInfo.setFxId(filterName);
                                    mVideoClipFxInfo.setIsCartoon(filterItem.getIsCartoon());
                                    mVideoClipFxInfo.setGrayScale(filterItem.getGrayScale());
                                    mVideoClipFxInfo.setStrokenOnly(filterItem.getStrokenOnly());
                                } else {
                                    String packageId = filterItem.getPackageId();
                                    mVideoClipFxInfo.setIsAdjusted(filterItem.getIsAdjusted());
                                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                                    mVideoClipFxInfo.setFxId(packageId);
                                    getAdjustFilter(packageId);
                                }
                                mVideoClipFxInfo.setFxIntensity(1.0f);
                            }
                            onFilterChanged(position);
                            onFilterChanged(mTimeline, mVideoClipFxInfo);

                            if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                                mVideoFragment.playVideoButtonClick();
                            }
                            mTipsDialog.dismiss();
                        }

                        @Override
                        public void OnCancelBtnClicked() {
                            mFilterView.setSelectedPos(currentSelectedPos);
                            mTipsDialog.dismiss();
                        }
                    });
                    mTipsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mFilterView.setSelectedPos(currentSelectedPos);
                        }
                    });
                    mTipsDialog.show();
                    // 添加切换滤镜 移除关键帧的文案提示
                    // Add switching filter, remove key frame copywriting tips
                    mTipsDialog.setTipsText(R.string.replace_keyFrame_ffects);
                }
            }

            @Override
            public void onMoreFilter() {
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFilter);
                bundle.putInt("assetType", NvAsset.ASSET_FILTER);
                bundle.putString("from", "edit_filter");
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, REQUEST_FILTER_LIST_CODE);
                mFilterView.setMoreFilterClickable(false);
            }

            @Override
            public void onIntensity(int value) {
                float intensity = value / (float) 100;
                mVideoClipFxInfo.setFxIntensity(intensity);
                updateFxIntensity(intensity);
                if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                }
            }
        });
        HashMap<String, Float> filterParam = mVideoClipFxInfo.getFilterParam();
        HashMap<String, NvsColor> filterColorParam = mVideoClipFxInfo.getFilterColorParam();
        HashMap<String, Object> map = new HashMap<>(filterParam);
        map.putAll(filterColorParam);
        boolean isAdjust = getAdjustFilter(mVideoClipFxInfo.getFxId());
        if (!isAdjust) {
            float intensity = mVideoClipFxInfo.getFxIntensity();
            mFilterView.setIntensitySeekBarProgress((int) (intensity * 100));
        } else {
            mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
            mFilterView.setIntensityTextVisible(View.INVISIBLE);
            mAdjustSpecialEffectsView.setVisibility(View.VISIBLE);
            updateFxAdjustParams(map);
        }
        mFilterView.setSelectedPos(mSelectedPos);
    }

    private void updateFxAdjustParams(HashMap<String, Object> map) {
        if ((null == mVideoClipFxInfo) || (null == mAdjustSpecialEffectsView)) {
            return;
        }
        List<AdjustSpecialEffectsInfo> adjustFxData = mAdjustSpecialEffectsView.getData();
        if ((null == adjustFxData) || adjustFxData.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            for (AdjustSpecialEffectsInfo info : adjustFxData) {
                if (TextUtils.equals(key, info.getKey())) {
                    if (value instanceof NvsColor) {
                        info.setColor((NvsColor) value);
                        break;
                    }
                    info.setStrength((Float) value);
                    break;
                }
            }
        }
    }

    private boolean getAdjustFilter(String packageId) {
        if (TextUtils.isEmpty(packageId)) {
            return false;
        }
        List<NvsExpressionParam> expValueList = mStreamingContext.getAssetPackageManager().getExpValueList(packageId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        Hashtable<String, String> hashtable = mStreamingContext.getAssetPackageManager().getTranslationMap(packageId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        if ((null == expValueList) || expValueList.isEmpty() || (null == hashtable) || hashtable.isEmpty()) {
            return false;
        }
        List<AdjustSpecialEffectsInfo> dataList = new ArrayList<>();
        AdjustSpecialEffectsInfo adjustSpecialEffectsInfo = null;
        String name = "";
        String key = "";
        for (NvsExpressionParam nvsExpressionParam : expValueList) {
            int type = nvsExpressionParam.getType();
            key = nvsExpressionParam.getName();
            name = hashtable.get(key);
            adjustSpecialEffectsInfo = new AdjustSpecialEffectsInfo();
            adjustSpecialEffectsInfo.setPackageId(packageId);
            adjustSpecialEffectsInfo.setAdjustmentCategoryName(name);
            adjustSpecialEffectsInfo.setKey(key);
            adjustSpecialEffectsInfo.setType(type);
            switch (type) {
                case NvsExpressionParam.TYPE_COLOR:
                    NvsColor nvsColor = nvsExpressionParam.getColor();
                    if (null != nvsColor) {
                        adjustSpecialEffectsInfo.setColor(nvsColor);
                        if ((mVideoClipFxInfo != null)) {
                            mVideoClipFxInfo.putFilterColorParam(key, nvsColor);
                        }
                    }
                    break;
                case NvsExpressionParam.TYPE_FLOAT:
                    NvsExpressionParam.FloatParam floatParam = nvsExpressionParam.getFloatParam();
                    if (null != floatParam) {
                        adjustSpecialEffectsInfo.setDefVal(floatParam.getDefVal());
                        adjustSpecialEffectsInfo.setMaxVal(floatParam.getMaxVal());
                        adjustSpecialEffectsInfo.setMinVal(floatParam.getMinVal());
                        adjustSpecialEffectsInfo.setStrength(floatParam.getDefVal());
                        if (mVideoClipFxInfo != null) {
                            mVideoClipFxInfo.putFilterParam(adjustSpecialEffectsInfo.getKey(), adjustSpecialEffectsInfo.getStrength());
                        }
                    }
                    break;
                case NvsExpressionParam.TYPE_INT:
                    NvsExpressionParam.IntParam intParam = nvsExpressionParam.getIntParam();
                    if (null != intParam) {
                        adjustSpecialEffectsInfo.setDefVal(intParam.getDefVal());
                        adjustSpecialEffectsInfo.setMaxVal(intParam.getMaxVal());
                        adjustSpecialEffectsInfo.setMinVal(intParam.getMinVal());
                        adjustSpecialEffectsInfo.setStrength(intParam.getDefVal());
                        if (mVideoClipFxInfo != null) {
                            mVideoClipFxInfo.putFilterParam(adjustSpecialEffectsInfo.getKey(), adjustSpecialEffectsInfo.getStrength());
                        }
                    }
                    break;
                case NvsExpressionParam.TYPE_BOOLEAN:
                    break;
                default:
                    break;
            }
            dataList.add(adjustSpecialEffectsInfo);
        }
        mAdjustSpecialEffectsView.setData(dataList);
        mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
        mVideoClipFxInfo.setFxId(packageId);
        mVideoClipFxInfo.setFxIntensity(1.0f);
        return true;
    }

    protected void onFilterChanged(int position) {
    }

    /**
     * 子类用来判断是不是有关键帧数据 来判断是否显示默认强度 seekbar
     * The subclass is used to determine whether there is key frame data to determine whether to display the default strength seekbar
     *
     * @return boolean
     */
    protected boolean isNeedShowSeekBarWhenChangeFilterFromParent() {
        return true;
    }

    /**
     * 滤镜改变时调用
     * Called when the filter changes
     *
     * @param timeline          timeline
     * @param changedClipFilter 更改后的Filter；Changed Filter
     */
    protected abstract void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter);

    private void updateFxIntensity(float value) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }

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
                fx.setFilterIntensity(value);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFilterView.setMoreFilterClickable(true);
    }
}
