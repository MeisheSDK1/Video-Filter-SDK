package com.meishe.cutsame;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.manager.AppManager;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.play.PlayerFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.view.PlayControlView;
import com.meishe.cutsame.fragment.CaptionSettingFragment;
import com.meishe.cutsame.fragment.ClipSettingFragment;
import com.meishe.cutsame.fragment.adapter.ExportTemplateCaptionAdapter;
import com.meishe.cutsame.fragment.adapter.ExportTemplateClipAdapter;
import com.meishe.cutsame.util.CustomConstants;
import com.meishe.engine.EditorEngine;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.util.PathUtils;
import com.meishe.third.tablayout.SlidingTabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

/**
 * @author liupanfeng
 * @CreateDate :2020/12/23 15:14
 * @Description : 导出模板设置页面 Export Template page
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ExportTemplateSettingActivity extends BaseActivity implements View.OnClickListener {

    private View mIvBack;
    private View mTvExportView;
    private PlayerFragment mPlayerFragment;
    private LinearLayout mLlTopOperationBarView;
    private PlayControlView mPlayControlView;

    /**
     * 时间线，编辑场景的时间轴实体
     * Timeline. Edit the timeline entity of the scene
     */
    private NvsTimeline mTimeLine;
    private FrameLayout mRlPreviewContainerView;
    private boolean mTouchToChangeProgress;
    private SlidingTabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTabTitleList = new ArrayList<>();
    private TextView mTvGroup;
    private ClipSettingFragment mClipSettingFragment;
    private CaptionSettingFragment mCaptionSettingFragment;
    private NvsStreamingContext mStreamingContext;


    @Override
    protected int bindLayout() {
        return R.layout.activity_export_template_setting;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mStreamingContext = EditorEngine.getInstance().getStreamingContext();
        mTimeLine = EditorEngine.getInstance().getCurrentTimeline();
        EditorController.getInstance().setNvsTimeline(mTimeLine);
        //删除模板封面 Remove template cover
        FileUtils.deleteAllInDir(PathUtils.getTemplateCoverFileFolder());
    }

    @Override

    protected void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mTvExportView = findViewById(R.id.tv_export_template);
        mLlTopOperationBarView = findViewById(R.id.ll_operation_bar_layout);
        mRlPreviewContainerView = findViewById(R.id.rl_preview_view);
        mPlayControlView = findViewById(R.id.export_play_view);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
        mTvGroup = findViewById(R.id.tv_group);
        mTvGroup.setBackground(CommonUtils.getRadiusDrawable((int) getResources().getDimension(R.dimen.dp_px_99), getResources().getColor(R.color.red_fc2b)));
        initPlayData();
        setTitleBarHeight();
        initVideoFragment();
        initTabLayout();
        initListener();
    }

    private void initTabLayout() {
        mFragmentList.clear();
        mTabTitleList.clear();

        mClipSettingFragment = ClipSettingFragment.newInstance();
        mFragmentList.add(mClipSettingFragment);
        mTabTitleList.add(this.getResources().getString(R.string.activity_cut_export_template_clip));

//        mCaptionSettingFragment = CaptionSettingFragment.newInstance();
//        mFragmentList.add(mCaptionSettingFragment);
//        mTabTitleList.add(this.getResources().getString(R.string.activity_cut_export_template_caption));

        mViewPager.setAdapter(new CommonFragmentAdapter(getSupportFragmentManager(), mFragmentList, mTabTitleList));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    mTvGroup.setText(getString(R.string.activity_cut_export_template_lock));
                    mCaptionSettingFragment.setActionState(ExportTemplateCaptionAdapter.ACTION_STATE_DEFAULT);
                } else {
                    mTvGroup.setText(getString(R.string.activity_cut_export_template_groups));
                    mClipSettingFragment.setActionState(ExportTemplateClipAdapter.ACTION_STATE_DEFAULT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPlayData() {
        int duration = ((int) (mTimeLine.getDuration() / 1000f));
        mPlayControlView.setMax(duration);
        String nvStringTime = FormatUtils.microsecond2Time(EditorController.getInstance().getTimelineDuration());
        mPlayControlView.setStartText("00:00");
        mPlayControlView.setCurrentText(nvStringTime);
        mPlayControlView.setListener(new PlayControlView.OnSeekBarListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                EditorController.getInstance().stop();
                mTouchToChangeProgress = true;
                NvsStreamingContext.getInstance().setTimelineScaleForSeek(mTimeLine, mTimeLine.getDuration() / 1000000D / seekBar.getWidth());

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTouchToChangeProgress = false;
                EditorController.getInstance().seekTimeline(seekBar.getProgress() * 1000L);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long nvTime = progress * 1000L;
                String nvStringTime = FormatUtils.microsecond2Time(nvTime);
                mPlayControlView.setStartText(nvStringTime);
                if (fromUser) {
                    EditorController.getInstance().seekTimeline(nvTime, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                }
            }
        });
        mPlayControlView.setOnPlayClickListener(new PlayControlView.OnPlayClickListener() {
            @Override
            public void onPlayClick() {
                if (EditorController.getInstance().isPlaying()) {
                    EditorController.getInstance().stop();
                } else {
                    long currentPosition = (long) (mPlayControlView.getProgress() * 1000f);
                    if (currentPosition == mTimeLine.getDuration()) {
                        currentPosition = 0;
                    }
                    mClipSettingFragment.setSelectPosition(0);
                    EditorController.getInstance().playNow(currentPosition, mTimeLine.getDuration());
                }
            }
        });
    }

    private void setTitleBarHeight() {
        int statusBarViewHeight = ScreenUtils.getStatusBarHeight();
        if (statusBarViewHeight > 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLlTopOperationBarView.getLayoutParams();
            layoutParams.topMargin = (int) (statusBarViewHeight + getResources().getDimension(R.dimen.title_margin_top));
            mLlTopOperationBarView.setLayoutParams(layoutParams);
        }
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mTvExportView.setOnClickListener(this);
        mTvGroup.setOnClickListener(this);
    }

    public void setPlayControlViewProgress(long position) {
        mPlayControlView.setProgress((int) (position / 1000f));
    }

    private void initVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PlayerFragment.PlayEventListener listener = new PlayerFragment.PlayEventListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                if (timeline.getDuration() - mStreamingContext.getTimelineCurrentPosition(mTimeLine) <= 40000) {
                    mPlayControlView.setStartText(FormatUtils.microsecond2Time(0));
                    mPlayControlView.setProgress(0);
                    mPlayerFragment.seekTimeline(0, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER
                            | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                }
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                if (!mTouchToChangeProgress) {
                    mPlayControlView.setProgress((int) (stamp / 1000));
                }
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                mPlayControlView.changPlayState(EditorController.getInstance().isPlaying());
            }
        };
        mPlayerFragment = new PlayerFragment(mTimeLine, mStreamingContext, listener);
        fragmentTransaction.add(R.id.rl_preview_view, mPlayerFragment).commit();
        fragmentTransaction.show(mPlayerFragment);
        mRlPreviewContainerView.post(new Runnable() {
            @Override
            public void run() {
                mPlayerFragment.seekTimeline(NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER
                        | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
            }
        });
    }

    public PlayerFragment getVideoFragment() {
        return mPlayerFragment;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.tv_export_template) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CustomConstants.TEMPLATE_CLIP_DATA, (Serializable) mClipSettingFragment.getData());
            AppManager.getInstance().jumpActivity(ExportTemplateSettingActivity.this, ExportTemplateActivity.class, bundle);
        } else if (view.getId() == R.id.tv_group) {
            String content = mTvGroup.getText().toString();
            if (mViewPager.getCurrentItem() == 0) {
                if (getString(R.string.activity_cut_export_template_groups).equals(content)) {
                    mTvGroup.setText(R.string.activity_cut_export_template_cancel);
                    mClipSettingFragment.setActionState(ExportTemplateClipAdapter.ACTION_STATE_CREATE_GROUP);
                } else if (getString(R.string.activity_cut_export_template_cancel).equals(content)) {
                    mTvGroup.setText(getString(R.string.activity_cut_export_template_groups));
                    mClipSettingFragment.setActionState(ExportTemplateClipAdapter.ACTION_STATE_DEFAULT);
                }
            } else {
                if (getString(R.string.activity_cut_export_template_lock).equals(content)) {
                    //click lick
                    mTvGroup.setText(getString(R.string.activity_cut_export_template_select_all));
                    mCaptionSettingFragment.setActionState(ExportTemplateCaptionAdapter.ACTION_STATE_SELECT_CAPTION);
                } else if (getString(R.string.activity_cut_export_template_select_all).equals(content)) {
                    //click select all
                    mCaptionSettingFragment.selectAllCaption();
                }
            }
        }
    }


}