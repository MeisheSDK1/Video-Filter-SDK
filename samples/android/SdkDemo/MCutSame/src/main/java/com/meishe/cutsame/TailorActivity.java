package com.meishe.cutsame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.meishe.cutsame.bean.TailorClip;
import com.meishe.cutsame.fragment.BaseVideoFragment;
import com.meishe.cutsame.view.MultiThumbnailSequenceView2;
import com.meishe.cutsame.view.TailorView;
import com.meicam.sdk.NvsTimeline;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.MeicamVideoClip;
import com.meishe.engine.bean.MeicamVideoTrack;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.interf.StreamingConstant;
import com.meishe.engine.interf.VideoFragmentListenerWithClick;
import com.meishe.engine.util.TimelineUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : CaoZhiChao
 * @CreateDate : 2020/11/27 16:29
 * @Description : 剪同款的视频裁剪 Cut video clip Activity
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class TailorActivity extends BaseActivity implements VideoFragmentListenerWithClick {
    public static final String VIDEO_PATH = "videoPath";
    public static final String VIDEO_LIMIT = "videoLimit";
    public static final String VIDEO_TYPE = "videoType";
    public static final String START_TRIM = "startTrim";
    private BaseVideoFragment mBaseVideoFragment;
    private String mVideoPath;
    private long mVideoLimit;
    private int mVideoType;
    private long mStartTrim = 0;
    private TailorView mTailorView;
    private TailorClip mTailorClip;
    private int mState = -1;
    private long mNowStartTime = 0;

    @Override
    protected int bindLayout() {
        return R.layout.activity_tailor;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            mVideoPath = extras.getString(VIDEO_PATH);
            mVideoLimit = extras.getLong(VIDEO_LIMIT);
            mVideoType = extras.getInt(VIDEO_TYPE);
            mStartTrim = extras.getLong(START_TRIM);
            mNowStartTime = mStartTrim;
        }
        if (!TextUtils.isEmpty(mVideoPath)) {
            NvsTimeline nvsTimeline = TimelineUtil.newTimeline(TimelineUtil.getVideoEditResolutionByClip(mVideoPath));
            EditorController.getInstance().setNvsTimeline(nvsTimeline);
            EditorController.getInstance().seekTimeline(mNowStartTime);
            List<MeicamVideoTrack> videoTrackList = new ArrayList<>();
            MeicamVideoTrack meicamVideoTrack = new MeicamVideoTrack(0);
            long duration;
            String videoType = CommonData.CLIP_VIDEO;
            if (mVideoType == StreamingConstant.VideoClipType.VIDEO_CLIP_TYPE_AV) {
                duration = EditorController.getInstance().getVideoDuration(mVideoPath);
            } else {
                videoType = CommonData.CLIP_IMAGE;
                duration = mVideoLimit;
            }
            mTailorClip = new TailorClip(mVideoPath, mVideoLimit, 0, duration);
            MeicamVideoClip newClip = new MeicamVideoClip(mVideoPath, videoType, duration);
            newClip.setTrimOut(duration);
            meicamVideoTrack.getClipInfoList().add(newClip);
            videoTrackList.add(meicamVideoTrack);
            TimelineUtil.fillTrack(EditorController.getInstance().getNvsTimeline(), videoTrackList);
        } else {
            LogUtils.e("initData: error! mVideoPath is empty!");
        }
    }

    @Override
    protected void initView() {
        addVideoFragment();
        ImageView activityTailorBack = findViewById(R.id.activity_tailor_back);
        activityTailorBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
        Button activityTailorSure = findViewById(R.id.activity_tailor_sure);
        activityTailorSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(CutSameEditorActivity.INTENT_TRAM, mNowStartTime);
                // 设置返回码和返回携带的数据 Sets the return code and returns the data carried
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        Drawable drawable = CommonUtils.getRadiusDrawable(-1, -1,
                getResources().getDimensionPixelOffset(R.dimen.dp_px_150), getResources().getColor(R.color.activity_tailor_button_background));
        activityTailorSure.setBackground(drawable);
        mTailorView = findViewById(R.id.activity_tailor_view);
        mTailorView.setOnScrollListener(new MultiThumbnailSequenceView2.OnScrollListener() {
            @Override
            public void onScrollChanged(int dx, int oldDx) {
                long nowTime = EditorController.getInstance().lengthToDuration(dx, mTailorView.getPixelPerMicrosecond());
                mNowStartTime = nowTime;
                if (!EditorController.getInstance().isPlaying()) {
                    EditorController.getInstance().seekTimeline(nowTime);
                }
            }

            @Override
            public void onScrollStopped() {
                EditorController.getInstance().playNow(EditorController.getInstance().nowTime() + mVideoLimit);
            }

            @Override
            public void onSeekingTimeline() {
                EditorController.getInstance().stop();
            }
        });
        mTailorView.setTailorClip(mTailorClip);
        mTailorView.setState(TailorView.FROM_USER);
        mTailorView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTailorView.seekNvsMultiThumbnailSequenceView(EditorController.getInstance().durationToLength(mStartTrim, mTailorView.getPixelPerMicrosecond()));
            }
        }, 200);
        TextView activityTailorTextLimit = findViewById(R.id.activity_tailor_text_limit);
        String text = (mVideoLimit / 1000000) + "S";
        activityTailorTextLimit.setText(text);
    }

    private void addVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mBaseVideoFragment = BaseVideoFragment.newInstance(true);
        mBaseVideoFragment.setVideoFragmentListener(this);
        fragmentManager.beginTransaction().add(R.id.activity_tailor_fragment_container, mBaseVideoFragment).commit();
        fragmentManager.beginTransaction().show(mBaseVideoFragment);
    }

    @Override
    public void playBackEOF(NvsTimeline timeline) {
//        if (mBaseVideoFragment != null) {
//            mBaseVideoFragment.changePlayButtonState(true);
//        }
        EditorController.getInstance().playNow(mNowStartTime, mNowStartTime + mVideoLimit);
    }

    @Override
    public void playStopped(NvsTimeline timeline) {
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(true);
        }
    }

    @Override
    public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
        mState = TailorView.FROM_VIDEO;
        mTailorView.seekToPosition(stamp, mState, mNowStartTime);
    }

    @Override
    public void streamingEngineStateChanged(int state) {
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(!EditorController.getInstance().isPlaying());
        }
        if (EditorController.getInstance().isPlaying()) {
            mState = TailorView.FROM_VIDEO;
            mTailorView.setState(mState);
        } else {
            mTailorView.setState(TailorView.FROM_USER);
        }
    }

    @Override
    public void onSeekingTimelinePosition(NvsTimeline timeline, long position) {

    }

    @Override
    public boolean clickPlayButtonByOthers() {
        controllerVideoFragmentClick();
        return true;
    }

    @Override
    public boolean clickLiveWindowByOthers() {
        controllerVideoFragmentClick();
        return true;
    }

    @Override
    public void connectTimelineWithLiveWindow() {
        EditorController.getInstance().playNow(mNowStartTime, mNowStartTime + mVideoLimit);
    }

    private void controllerVideoFragmentClick() {
        if (!EditorController.getInstance().isPlaying()) {
            EditorController.getInstance().playNow(EditorController.getInstance().nowTime(), mNowStartTime + mVideoLimit);
        } else {
            EditorController.getInstance().stop();
        }
    }
}