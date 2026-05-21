package com.meishe.sdkdemo.edit.livephoto;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.FileUtils;
import com.meishe.cutsame.view.MultiThumbnailSequenceView2;
import com.meishe.engine.editor.EditorController;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.CompileVideoFragment;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.VideoCompileUtil;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.view.CustomThumbnailCutView;

import java.util.ArrayList;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2025/11/12 17:45
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class LivePhotoActivity extends BaseActivity {
    private CustomTitleBar titleBar;
    private LinearLayout mBottomLayout;
    private RelativeLayout mCompilePage;
    private CustomThumbnailCutView mThumbnailCutView;
    private VideoFragment mVideoFragment;
    private CompileVideoFragment mCompileVideoFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private long cutVideoLimit = 3 * Constants.NS_TIME_BASE;
    private long mNowStartTime = 0;

    @Override
    protected int bindLayout() {
        AppManager.getInstance().addActivity(this);
        return R.layout.activity_live_photo;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mStreamingContext = NvsStreamingContext.getInstance();
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            onBackPressed();
            return;
        }
        if (cutVideoLimit > mTimeline.getDuration()) {
            cutVideoLimit = mTimeline.getDuration();
        }
    }

    @Override
    protected void initView() {
        titleBar = findViewById(R.id.live_photo_title_bar);
        titleBar.setTextCenter(getResources().getString(R.string.live_photo));
        titleBar.setTextRightVisible(View.VISIBLE);
        titleBar.setTextRight(R.string.compile);
        mThumbnailCutView = findViewById(R.id.live_photo_tailor_view);
        mBottomLayout = findViewById(R.id.live_photo_bottom);
        TextView mCutTimeView = findViewById(R.id.live_photo_cut_time);
        mCompilePage = findViewById(R.id.live_photo_compile_page);
        initVideoFragment();
        initCompileVideoFragment();
        initCutView();
        initListener();

        mCutTimeView.setText(String.format(getResources().getString(R.string.select_time), cutVideoLimit / Constants.NS_TIME_BASE));
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.seekTimeline(0, 0);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME |
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", titleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.live_photo_live_window, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                mVideoFragment.playVideo(mNowStartTime, mNowStartTime + cutVideoLimit);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                mThumbnailCutView.seekToPosition(stamp, CustomThumbnailCutView.FROM_USER, mNowStartTime);
            }

            @Override
            public void streamingEngineStateChanged(int state) {

            }
        });
    }

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        getFragmentManager().beginTransaction().add(R.id.live_photo_compile_page, mCompileVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mCompileVideoFragment);
    }

    private void initCutView() {
        ArrayList<ClipInfo> videoClipArray = new ArrayList<>();
        if (null == mTimeline) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(videoClip.getFilePath());
            clipInfo.changeTrimIn(videoClip.getTrimIn());
            clipInfo.changeTrimOut(videoClip.getTrimOut());
            clipInfo.setInPoint(videoClip.getInPoint());
            clipInfo.setOutPoint(videoClip.getOutPoint());
            videoClipArray.add(clipInfo);
        }

        mThumbnailCutView.setOnScrollListener(new MultiThumbnailSequenceView2.OnScrollListener() {
            @Override
            public void onScrollChanged(int dx, int oldDx) {
                long nowTime = EditorController.getInstance().lengthToDuration(dx, mThumbnailCutView.getPixelPerMicrosecond());
                mNowStartTime = nowTime;
                mVideoFragment.seekTimeline(nowTime, 0);
            }

            @Override
            public void onScrollStopped() {
                mVideoFragment.playVideo(mNowStartTime, mNowStartTime + cutVideoLimit);
            }

            @Override
            public void onSeekingTimeline() {
                mStreamingContext.stop();
            }
        });
        mThumbnailCutView.setClipInfo(cutVideoLimit, videoClipArray);
        mThumbnailCutView.setState(CustomThumbnailCutView.FROM_USER);
        mThumbnailCutView.postDelayed(new Runnable() {
            @Override
            public void run() {
                long mStartTrim = 0;
                mThumbnailCutView.seekNvsMultiThumbnailSequenceView(EditorController.getInstance().durationToLength(mStartTrim, mThumbnailCutView.getPixelPerMicrosecond()));
            }
        }, 200);
    }

    private void initListener() {
        titleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                mCompilePage.setVisibility(View.VISIBLE);
                mCompileVideoFragment.compileVideo(mNowStartTime, mNowStartTime + cutVideoLimit);
            }
        });
        mCompilePage.setOnTouchListener((v, event) -> true);
        if (mCompileVideoFragment != null) {
            mCompileVideoFragment.setCompileVideoListener(new CompileVideoFragment.OnCompileVideoListener() {
                @Override
                public void compileProgress(NvsTimeline timeline, int progress) {

                }

                @Override
                public void compileFinished(NvsTimeline timeline) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileFailed(NvsTimeline timeline) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                    mCompilePage.setVisibility(View.GONE);
                    mCompilePage.post(new Runnable() {
                        @Override
                        public void run() {
                            compileLivePhoto(mCompileVideoFragment.getCompileVideoPath());
                        }
                    });
                }

                @Override
                public void compileVideoCancel() {
                    mCompilePage.setVisibility(View.GONE);
                }
            });
        }
    }

    private void compileLivePhoto(String compilePath) {
        String livePhotoPath = VideoCompileUtil.getCompileLivePhotoPath();
        boolean success = mStreamingContext.makeLivePhotoFromVideoFile(compilePath, mNowStartTime, livePhotoPath);
        FileUtils.delete(compilePath);
        if (!success) {
            ToastUtil.showToast(this, getResources().getString(R.string.activity_cut_compile_tip3));
            return;
        }
        MediaScannerUtil.scan(livePhotoPath);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mVideoFragment) {
            mVideoFragment.stopEngine();
        }
    }

    @Override
    public void onBackPressed() {
        if (null != mVideoFragment) {
            mVideoFragment.stopEngine();
        }
        removeTimeline();
        AppManager.getInstance().finishActivity();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

}
