package com.meishe.sdkdemo.edit.createPic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.io.File;
import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2021/11/02.
 * @Description :生成图片页面
 * @Description :create picture activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CreatePicActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private RelativeLayout mZoomOutButton;
    private RelativeLayout mZoomInButton;
    private TextView mCurrentPlaytime;
    private ImageView mVideoPlay;
    private NvsTimelineEditor mTimelineEditor;
    private ImageView mAddAnimateStickerButton;
    private ImageView mCreatePicFinish;
    private NvsMultiThumbnailSequenceView mMultiThumbnailSequenceView;
    private VideoFragment mVideoFragment;

    private NvsTimeline mTimeline;
    private StringBuilder mShowCurrentDuration = new StringBuilder();

    private boolean mIsSeekTimeline = true;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_create_pic;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mZoomOutButton = (RelativeLayout) findViewById(R.id.zoomOut);
        mZoomInButton = (RelativeLayout) findViewById(R.id.zoomIn);
        mCurrentPlaytime = (TextView) findViewById(R.id.currentPlaytime);
        mVideoPlay = (ImageView) findViewById(R.id.videoPlay);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.timelineEditor);
        mAddAnimateStickerButton = (ImageView) findViewById(R.id.addAnimateStickerButton);
        mCreatePicFinish = (ImageView) findViewById(R.id.iv_create_pci_finish);
        mMultiThumbnailSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.createPic);
        mTitleBar.setBackImageVisible(View.GONE);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
    }

    @Override
    protected void initData() {
        if (initTimeline()) {
            return;
        }
        setPlaytimeText(0);
        initMultiSequence();
        initVideoFragment();
        mTitleBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                        0);
            }
        }, 0);
    }

    private boolean initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return true;
        }
        return false;
    }

    @Override
    protected void initListener() {
        mZoomOutButton.setOnClickListener(this);
        mZoomInButton.setOnClickListener(this);
        mVideoPlay.setOnClickListener(this);
        mCreatePicFinish.setOnClickListener(this);


        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                Bitmap bitmap = mStreamingContext.grabImageFromTimeline(mTimeline, timelineCurrentPosition, new NvsRational(1, 1));
                saveBitmap(bitmap);
                picFinish();
            }
        });


        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {

            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                setPlaytimeText(stamp);
                multiThumbnailSequenceViewSmooth(stamp);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mVideoPlay.setBackgroundResource(R.mipmap.icon_edit_pause);
                    mIsSeekTimeline = false;
                } else {
                    mVideoPlay.setBackgroundResource(R.mipmap.icon_edit_play);
                    mIsSeekTimeline = true;
                }
            }
        });


        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {

                if (!mIsSeekTimeline) {
                    // 播放过程中不进行选中操作
                    //Do not select operation during playback
                    return;
                }

                if (mTimeline != null) {
                    seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_ALLOW_FAST_SCRUBBING);
                    setPlaytimeText(timeStamp);
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mTimelineEditor.setTimelineScaleForSeek(mTimeline, mTimelineEditor.getDurationWidth());
                } else if (action == MotionEvent.ACTION_UP | action == MotionEvent.ACTION_CANCEL) {
                    seekTimeline(NvsStreamingContext.getInstance().getTimelineCurrentPosition(mTimeline));
                }
                mIsSeekTimeline = true;
                return false;
            }
        });

    }

    private void picFinish() {
        mVideoFragment.stopEngine();
        removeTimeline();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_create_pci_finish) {
            picFinish();
        } else if (id == R.id.zoomIn) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomInSequence();
        } else if (id == R.id.zoomOut) {
            mIsSeekTimeline = false;
            mTimelineEditor.ZoomOutSequence();
        } else if (id == R.id.videoPlay) {
            playVideo();
        }
    }


    @Override
    public void onBackPressed() {
        mVideoFragment.stopEngine();
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }


    /**
     * 初始化缩略图
     * Initialize thumbnail
     */
    private void initMultiSequence() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        int clipCount = videoTrack.getClipCount();
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
        for (int index = 0; index < clipCount; ++index) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
            if (videoClip == null) {
                continue;
            }

            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescsArray.add(sequenceDescs);
        }
        long duration = mTimeline.getDuration();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoPlay.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
    }


    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }


    /**
     * 设置当前播放时间
     * set the current play time
     *
     * @param playTime
     */
    private void setPlaytimeText(long playTime) {
        long totalDuaration = mTimeline.getDuration();
        String strTotalDuration = TimeFormatUtil.formatUsToString1(totalDuaration);
        String strCurrentDuration = TimeFormatUtil.formatUsToString1(playTime);
        mShowCurrentDuration.setLength(0);
        mShowCurrentDuration.append(strCurrentDuration);
        mShowCurrentDuration.append("/");
        mShowCurrentDuration.append(strTotalDuration);
        mCurrentPlaytime.setText(mShowCurrentDuration.toString());
    }

    /**
     * 多图平滑滚动到指定时间戳
     * Smooth scrolling of multiple images to the specified timestamp
     *
     * @param stamp
     */
    private void multiThumbnailSequenceViewSmooth(long stamp) {
        if (mMultiThumbnailSequenceView != null) {
            int x = Math.round((stamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
            mMultiThumbnailSequenceView.smoothScrollTo(x, 0);
        }
    }

    private void playVideo() {
        if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long endTime = mTimeline.getDuration();
            mVideoFragment.playVideo(startTime, endTime);
        } else {
            mVideoFragment.stopEngine();
        }
    }


    private void resetView() {
        setPlaytimeText(0);
        mVideoPlay.setBackgroundResource(R.mipmap.icon_edit_play);
        mMultiThumbnailSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        seekTimeline(0);
    }

    private void seekTimeline(long timestamp, int flag) {
        mVideoFragment.seekTimeline(timestamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER |flag);
    }

    private void seekTimeline(long timestamp) {
        mVideoFragment.seekTimeline(timestamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }


    /**
     * 导出封面图片
     * Export cover image   /storage/emulated/0/DCIM/Camera/meicam_1653026379482.jpg
     */
    private void saveBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            ToastUtil.showToast(this, getResources().getString(R.string.save_failed));
            return;
        }
        String fileName = "meicam_" + System.currentTimeMillis() + ".jpg";
        String filePath = com.meishe.engine.util.PathUtils.getVideoSavePath(fileName);
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        boolean ret = Util.saveBitmapToSD(bitmap, filePath);
        if (ret) {
            MediaScannerUtil.scanFile(filePath, "");
        } else {
            ToastUtil.showToast(this, getResources().getString(R.string.save_failed));
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

}