package com.meishe.sdkdemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meishe.arscene.BeautyHelper;
import com.meishe.arscene.BeautyTabFragment;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.arscene.bean.FxParams;
import com.meishe.arscene.inter.IFxInfo;
import com.meishe.arscene.inter.OnBeautyListener;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.base.utils.BarUtils;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.activity.view.BeautyView;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.presenter.BeautyPresenter;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.HashSet;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiFei
 * @CreateDate : 2022-11-15 下午15:20
 * @Description : 编辑-美颜  Editing - Beauty
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyActivity extends BaseMvpActivity<BeautyPresenter> implements BeautyView {
    public static final String BEAUTY_INFO = "beauty_info";
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private VideoFragment mVideoFragment;
    private BeautyTabFragment mBeautyFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private BeautyFxInfo mBeautyParams;
    private BeautyHelper mBeautyHelper;

    @Override
    protected int bindLayout() {
        BarUtils.setStatusBarColor(this, Color.BLACK);
        return R.layout.activity_beauty;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        AppManager.getInstance().addActivity(this);
        mStreamingContext = NvsStreamingContext.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                mBeautyParams = (BeautyFxInfo) bundle.getSerializable(BEAUTY_INFO);
            }
        }
    }

    @Override
    protected void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        BarUtils.addMarginTopEqualStatusBarHeight(mTitleBar);
        mBottomLayout = findViewById(R.id.beauty_bottom_layout);
        mTitleBar.setTextCenter(R.string.beauty);
        mTitleBar.setTextRightVisible(View.GONE);
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

            }
        });
    }

    @Override
    protected void requestData() {
        super.requestData();
        ParameterSettingValues parameterValues = ParameterSettingValues.instance();
        if (null == mBeautyParams) {
            mBeautyParams = new BeautyFxInfo();
        }
        mBeautyParams.setSingleBufferMode(parameterValues.isSingleBufferMode());
        mBeautyHelper = new BeautyHelper(mStreamingContext, BuildConfig.FACE_MODEL == 240, parameterValues.isSingleBufferMode());
        if (initTimeline()) {
            return;
        }
        boolean isSupport = mPresenter.isSupportMatte(mTimeline, mBeautyParams);
        mBeautyParams.setSupportMatte(isSupport);
        initVideoFragment();
        initBeautyFragment();
        postSeekAction();
        initListener();
    }

    private boolean initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        return null == mTimeline;
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

    private void initBeautyFragment() {
        mBeautyFragment = BeautyTabFragment.create(mBeautyParams);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.beauty_bottom_layout, mBeautyFragment)
                .commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().show(mBeautyFragment);
    }

    private void initListener() {
        mBeautyFragment.setOnBeautyListener(new OnBeautyListener() {
            @Override
            public void onBeautyApply(int beautyType, IFxInfo info) {
                switch (beautyType) {
                    case FxParams.BEAUTY_SKIN:
                        mPresenter.setSkinApply(mTimeline, mBeautyHelper, info);
                        mPresenter.setCancelDefaultFxValue(mTimeline, mBeautyHelper, mBeautyParams);
                        break;
                    case FxParams.BEAUTY_FACE:
                        mPresenter.setFaceApply(mTimeline, mBeautyHelper, info);
                        break;
                    case FxParams.BEAUTY_SMALL:
                        mPresenter.setSmallApply(mTimeline, mBeautyHelper, info);
                        mPresenter.setCancelDefaultFxValue(mTimeline, mBeautyHelper, mBeautyParams);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSharpenApply(boolean isOpen) {
                mPresenter.setSharpenApply(mTimeline, mBeautyHelper, isOpen);
            }

            @Override
            public void onBeautyFinish(BeautyFxInfo beautyParams) {
                finishBeauty(beautyParams);
            }
        });
    }

    private void postSeekAction() {
        mBottomLayout.post(() -> mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME));
    }

    @Override
    public void onSeekTimeline() {
        postSeekAction();
    }

    private void finishBeauty(BeautyFxInfo beautyParams) {
        mVideoFragment.stopEngine();
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
        if (null == beautyParams) {
            AppManager.getInstance().finishActivity(this);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(BEAUTY_INFO, beautyParams);
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }
}