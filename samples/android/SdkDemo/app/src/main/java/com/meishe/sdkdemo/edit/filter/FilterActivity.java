package com.meishe.sdkdemo.edit.filter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsExpressionParam;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.base.utils.BarUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.bean.AdjustSpecialEffectsInfo;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.view.AdjustSpecialEffectsView;
import com.meishe.sdkdemo.view.FilterView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-滤镜-Activity
 * @Description :VideoEdit-Filter-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterActivity extends BaseMvpActivity<FilterPresenter> implements FiltersView {
    private static final int REQUEST_FILTER_LIST_CODE = 102;
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private AdjustSpecialEffectsView mAdjustView;
    private FilterView mFilterView;
    private ImageView mFinish;

    private VideoFragment mVideoFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;

    private ArrayList<FilterItem> mFilterList = new ArrayList<>();
    private int mSelectedPos = 0;
    private FilterItem mFilterItem;

    @Override
    protected int bindLayout() {
        BarUtils.setStatusBarColor(this, Color.BLACK);
        return R.layout.activity_filter;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        AppManager.getInstance().addActivity(this);
        mStreamingContext = NvsStreamingContext.getInstance();
        initFilterAssets();
    }

    @Override
    protected void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        BarUtils.addMarginTopEqualStatusBarHeight(mTitleBar);
        mTitleBar.setBackImageVisible(View.VISIBLE);
        mTitleBar.setTextCenter(R.string.filter);
        mBottomLayout = findViewById(R.id.filter_bottom_layout);
        mAdjustView = findViewById(R.id.adjustSpecialEffectsView);
        mFilterView = findViewById(R.id.filterView);
        mFilterView.setFilterFxListBackgroud("#00000000");
        mFilterView.setBlackTheme(true);
        mFinish = findViewById(R.id.filterAssetFinish);
        initListener();
    }

    @Override
    protected void requestData() {
        super.requestData();
        mTimeline = mPresenter.createTimeline();
        if (null == mTimeline) {
            AppManager.getInstance().finishActivity(this);
            return;
        }
        initVideoFragment();
        initFilterView();
        mPresenter.getFilterData(this);
    }

    private void initFilterAssets() {
        NvAssetManager.sharedInstance().searchLocalAssets(NvAsset.ASSET_FILTER);
        NvAssetManager.sharedInstance().searchReservedAssets(NvAsset.ASSET_FILTER, "filter");
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
            }

            @Override
            public void streamingEngineStateChanged(int state) {
            }
        });
    }

    private void initFilterView() {
        mFilterView.setFilterArrayList(mFilterList);
        mFilterView.initFilterRecyclerView(this);
    }

    private void initListener() {
        mFilterView.setFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void onItmeClick(View v, int position) {
                int count = mFilterList.size();
                if (mFilterList.isEmpty() || (position >= count)) {
                    return;
                }
                if (mSelectedPos == position) {
                    mVideoFragment.playVideoButtonClick();
                    return;
                }
                mSelectedPos = position;
                if (position == 0) {
                    mAdjustView.setVisibility(View.GONE);
                    mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                    mFilterItem = null;
                    TimelineUtil.applyFilterByTimeline(mTimeline, null);
                    if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                        mVideoFragment.playVideoButtonClick();
                    }
                    return;
                }
                applyFilter(position);
            }

            @Override
            public void onMoreFilter() {
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFilter);
                bundle.putInt("assetType", NvAsset.ASSET_FILTER);
                bundle.putString("from", "edit_filter");
                AppManager.getInstance().jumpActivityForResult(FilterActivity.this
                        , AssetDownloadActivity.class, bundle, REQUEST_FILTER_LIST_CODE);
            }

            @Override
            public void onIntensity(int value) {
                if ((null == mTimeline) || (null == mFilterItem)) {
                    return;
                }
                NvsTimelineVideoFx videoFx = mTimeline.getFirstTimelineVideoFx();
                if (null == videoFx) {
                    return;
                }
                float intensity = value / (float) 100;
                videoFx.setFilterIntensity(intensity);
                mFilterItem.setIntensity(intensity);
                seekTimeline();
            }
        });
        mAdjustView.setOnItemProgressChangeListener(new AdjustSpecialEffectsView.OnItemProgressChangeListener() {
            @Override
            public void onProgressChange(AdjustSpecialEffectsInfo info, int progress, boolean fromUser) {
                if (fromUser) {
                    return;
                }
                mPresenter.changeAdjustIntensity(info, mFilterItem, progress, false);
            }

            @Override
            public void onColorChange(AdjustSpecialEffectsInfo effectsInfo, int color) {
                mPresenter.changeAdjustIntensity(effectsInfo, mFilterItem, color, true);
            }

            @Override
            public void onTouchUp(boolean touchUpFlag) {
                if (touchUpFlag) {
                    mVideoFragment.playVideoButtonClick();
                }
            }
        });
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineData.instance().setFilterFx(mFilterItem);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity(FilterActivity.this);
            }
        });
    }

    /**
     * 应用滤镜
     * Apply Filter
     *
     * @param position position
     */
    private void applyFilter(int position) {
        mFilterItem = mFilterList.get(position);
        if (null == mFilterItem) {
            return;
        }
        TimelineUtil.applyFilterByTimeline(mTimeline, mFilterItem);
        if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mVideoFragment.playVideoButtonClick();
        }
        String packageId = mFilterItem.getPackageId();
        if (TextUtils.isEmpty(packageId)) {
            mAdjustView.setVisibility(View.GONE);
            mFilterView.setIntensityLayoutVisible(View.VISIBLE);
            mFilterView.setIntensitySeekBarVisibility(View.VISIBLE);
            mFilterView.setIntensitySeekBarProgress(100);
            return;
        }
        List<NvsExpressionParam> expValueList = mStreamingContext.getAssetPackageManager().getExpValueList(packageId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);

        Hashtable<String, String> hashtable = mStreamingContext.getAssetPackageManager().getTranslationMap(packageId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        if ((null == expValueList) || (null == hashtable) || expValueList.isEmpty() || hashtable.isEmpty()) {
            mAdjustView.setVisibility(View.GONE);
            mFilterView.setIntensityLayoutVisible(View.VISIBLE);
            mFilterView.setIntensitySeekBarVisibility(View.VISIBLE);
            mFilterView.setIntensitySeekBarProgress(100);
            return;
        }
        mAdjustView.setVisibility(View.VISIBLE);
        mFilterView.setIntensityLayoutVisible(View.INVISIBLE);

        //可调节设置 Adjustable setting
        mAdjustView.setData(mPresenter.setAdjustFilter(packageId, expValueList, hashtable, mFilterItem));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_FILTER_LIST_CODE) {
            mPresenter.getFilterData(this);
        }
    }

    @Override
    public void onFilterData(ArrayList<FilterItem> mFilterData) {
        mFilterList = mFilterData;
        mFilterView.setFilterArrayList(mFilterList);
        mFilterView.notifyDataSetChanged();
        mFilterItem = TimelineData.instance().getFilterFx();
        if (null == mFilterItem) {
            return;
        }
        mSelectedPos = mPresenter.getSelectedFilterPos(mFilterList, mFilterItem);
        mFilterView.setSelectedPos(mSelectedPos);
        mFilterView.setIntensityLayoutVisible(mSelectedPos <= 0 ? View.INVISIBLE : View.VISIBLE);
        mFilterView.setIntensityTextVisible(View.VISIBLE);
        mFilterView.setIntensitySeekBarMaxValue(100);
        float intensity = mFilterItem.getIntensity();
        mFilterView.setIntensitySeekBarProgress((int) (intensity * 100));
    }

    @Override
    public void onChangeAdjustFilter() {
        NvsTimelineVideoFx videoFx = mTimeline.getFirstTimelineVideoFx();
        TimelineUtil.applyExprVarByFilter(videoFx, mFilterItem);
        seekTimeline();
    }

    private void seekTimeline() {
        int streamingEngineState = mStreamingContext.getStreamingEngineState();
        if (streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
        }
    }
}
