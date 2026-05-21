package com.meishe.sdkdemo.sequenceNesting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishe.cutsame.CutSameEditorActivity;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.activity.CutSameActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.ImageUtils;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.VideoCompileUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.File;
import java.util.ArrayList;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/24.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class SequenceNestingActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private VideoFragment mVideoFragment;
    private RelativeLayout mBottomLayout;
    private NvsMultiThumbnailSequenceView thumbnailView;
    private ImageView ivHead;
    private TextView tvHead;
    private ImageView ivTail;
    private TextView tvTail;
    private boolean headFlag = false;
    private boolean hasHead = false, hasTail = false;
    private View iv_head_delete;
    private View iv_tail_delete;
    private View compilePage;
    private String videoPath;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_sequence_nesting;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        findViewById(R.id.iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasHead) return;
                headFlag = true;
                Bundle bundle = new Bundle();
                bundle.putString(CutSameEditorActivity.BUNDLE_KEY, CutSameEditorActivity.BUNDLE_VALUE_HEAD);
                AppManager.getInstance().jumpActivity(SequenceNestingActivity.this, CutSameActivity.class, bundle);
            }
        });
        findViewById(R.id.iv_tail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasTail) return;
                headFlag = false;
                Bundle bundle = new Bundle();
                bundle.putString(CutSameEditorActivity.BUNDLE_KEY, CutSameEditorActivity.BUNDLE_VALUE_TAIL);
                AppManager.getInstance().jumpActivity(SequenceNestingActivity.this, CutSameActivity.class, bundle);
            }
        });
        mBottomLayout = findViewById(R.id.bottomLayout);
        thumbnailView = findViewById(R.id.nesting_imageThumbView);
        ivHead = findViewById(R.id.iv_head);
        tvHead = findViewById(R.id.tv_head);
        iv_head_delete = findViewById(R.id.iv_head_delete);
        ivTail = findViewById(R.id.iv_tail);
        tvTail = findViewById(R.id.tv_tail);
        iv_tail_delete = findViewById(R.id.iv_tail_delete);
        compilePage = findViewById(R.id.compilePage);
        compilePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        iv_head_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimeline != null) {
                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                    if (videoTrack != null) {
                        if (videoTrack.removeClip(0, false)) {
                            refreshVideoProgress();
                            hasHead = false;
                        }
                    }
                }
                tvHead.setVisibility(View.VISIBLE);
                ivHead.setImageBitmap(null);
                iv_head_delete.setVisibility(View.GONE);
            }
        });
        iv_tail_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimeline != null) {
                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                    if (videoTrack != null) {
                        if (videoTrack.removeClip(videoTrack.getClipCount() - 1, false)) {
                            refreshVideoProgress();
                            hasTail = false;
                        }
                    }
                }
                tvTail.setVisibility(View.VISIBLE);
                iv_tail_delete.setVisibility(View.GONE);
                ivTail.setImageBitmap(null);
            }
        });
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.packaging_template);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {

            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                compilePage.setVisibility(View.VISIBLE);
                videoPath = PathUtils.getVideoCompileDirPath() + File.separator + System.currentTimeMillis() + "Nesting.mp4";
                VideoCompileUtil.compileVideo(mStreamingContext, mTimeline, videoPath, 0, mTimeline.getDuration());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NvsTimeline currentTimeline = CutSameEditorActivity.cutTimeline;
        if (currentTimeline != null) {
            addCutTimeline(currentTimeline);
            refreshVideoProgress();
        }
    }

    /**
     * 更新视频总时长展示以及重新播放
     * Updated video total time display and playback
     */
    private void refreshVideoProgress() {
        if (mVideoFragment != null) {
            mVideoFragment.setmPlaySeekBarMaxAndCurrent(0, -1, 0, mTimeline.getDuration());
            mVideoFragment.playVideo(0, -1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setCallbackEnable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCallbackEnable(false);
    }

    private void setCallbackEnable(boolean b) {
        mStreamingContext.setCompileCallback(b ? null : new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {

            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                compilePage.setVisibility(View.GONE);
                ImageUtils.refreshAlbum(getApplicationContext(), videoPath);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.completedTips), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                compilePage.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void initData() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null)
            return;
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null)
            return;
        initVideoFragment();
        updateSequenceView();
    }

    /**
     * 更新缩略图预览窗口
     * update sequence view
     */
    private void updateSequenceView() {
        if (mVideoTrack == null) return;
        final ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> infoDescArray = new ArrayList<>();
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            NvsVideoClip clip = mVideoTrack.getClipByIndex(i);
            if (clip == null)
                continue;

            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc infoDesc = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            infoDesc.mediaFilePath = clip.getFilePath();
            infoDesc.trimIn = clip.getTrimIn();
            infoDesc.trimOut = clip.getTrimOut();
            infoDesc.inPoint = clip.getInPoint();
            infoDesc.outPoint = clip.getOutPoint();
            infoDesc.stillImageHint = false;
            infoDescArray.add(infoDesc);
        }

        int screenWidth = ScreenUtils.getScreenWidth(this) - ScreenUtils.dip2px(this, 144);
        double duration = (double) mTimeline.getDuration();
        double pixelPerMicrosecond = screenWidth / duration;
        thumbnailView.setPixelPerMicrosecond(pixelPerMicrosecond);
        thumbnailView.setThumbnailSequenceDescArray(infoDescArray);
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
        mVideoFragment.setAutoPlay(true);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", NvAsset.AspectRatio_9v16);
        bundle.putBoolean("playBarVisible", true);
        bundle.putBoolean("voiceButtonVisible", false);
        mVideoFragment.setArguments(bundle);
        mVideoFragment.setVideoVolumeListener(new VideoFragment.VideoVolumeListener() {
            @Override
            public void onVideoVolume() {

            }
        });
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 添加序列模板
     * Add sequence template
     */
    private void addCutTimeline(NvsTimeline timeline) {
        if (timeline == null) return;
        Log.e("variablecompose", "addCutTimeline duration:" + timeline.getDuration());

        Bitmap bitmap = mStreamingContext.grabImageFromTimeline(timeline, 0, new NvsRational(1, 2));
        NvsVideoTrack videoTrack = null;
        if (mTimeline != null) {
            videoTrack = mTimeline.getVideoTrackByIndex(0);
        }
        if (headFlag) {
            if (videoTrack != null) {
                NvsVideoClip videoClip = videoTrack.insertTimelineClip(timeline, 0);
                if (videoClip != null) {
                    hasHead = true;
                }
            }
            if (hasHead) {
                tvHead.setVisibility(View.GONE);
                if (bitmap != null) {
                    ivHead.setImageBitmap(bitmap);
                }
                iv_head_delete.setVisibility(View.VISIBLE);
            }
        } else {
            if (videoTrack != null) {
                NvsVideoClip videoClip = videoTrack.appendTimelineClip(timeline);
                if (videoClip != null) {
                    hasTail = true;
                }
            }
            if (hasTail) {
                tvTail.setVisibility(View.GONE);
                if (bitmap != null) {
                    ivTail.setImageBitmap(bitmap);
                }
                iv_tail_delete.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mStreamingContext != null &&
                mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_COMPILE) {
            return;
        }
        super.onBackPressed();
    }
}
