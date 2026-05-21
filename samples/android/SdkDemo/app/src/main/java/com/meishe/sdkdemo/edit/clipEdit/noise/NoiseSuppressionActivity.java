package com.meishe.sdkdemo.edit.clipEdit.noise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsAudioFx;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.clipEdit.SingleClipFragment;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.meishe.sdkdemo.edit.timelineEditor.NvsTimelineTimeSpan;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : jml
 * @CreateDate : 202/9/9.
 * @Description :视频编辑-编辑-降噪-Activity
 * @Description :VideoEdit-edit-noise-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class NoiseSuppressionActivity extends BaseActivity {
    private static final String TAG = "VolumeActivity";
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private ImageView mVolumeFinish;
    private RadioGroup rg_noise;
    private TextView tvCurTime;
    private RadioButton rb_1,rb_2,rb_3,rb_4;

    private SingleClipFragment mClipFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex = 0;
    private NvsTimelineEditor mTimelineEditor;
    private NvsMultiThumbnailSequenceView mMultiSequenceView;
    private RelativeLayout mPlayBtnLayout;
    private Button mPlayBtn;
    private NvsTimelineTimeSpan timelineTimeSpan;
    private NvsVideoClip videoClip;
    private NvsAudioFx noiseAudioEffect;
    private int mNoiseSuppressionLevel = 0;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.layout_edit_noise_suppression;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        rg_noise = (RadioGroup) findViewById(R.id.rg_noise);
        mVolumeFinish = (ImageView) findViewById(R.id.volumeFinish);
        mTimelineEditor = findViewById(R.id.volume_timeline_editor);
        mPlayBtnLayout = findViewById(R.id.play_btn_layout);
        mPlayBtn = findViewById(R.id.play_btn);
        tvCurTime = findViewById(R.id.play_cur_time);
        mMultiSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();
        rb_1 = findViewById(R.id.noise_level_one);
        rb_2 = findViewById(R.id.noise_level_two);
        rb_3 = findViewById(R.id.noise_level_three);
        rb_4 = findViewById(R.id.noise_level_four);

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.volume);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size())
            return;
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        if (clipInfo == null)
            return;
        mNoiseSuppressionLevel = clipInfo.getNoiseSuppressionLevel();
        setCheckedNoiseSuppression(mNoiseSuppressionLevel);
        initTimeline(clipInfo);
        initVideoFragment();
        initMultiSequence();
        if (timelineTimeSpan != null) {
            timelineTimeSpan.setVisibility(View.GONE);
        }
        tvCurTime.setText(TimeFormatUtil.formatUsToString2(0) + "/" + TimeFormatUtil.formatUsToString2(mTimeline.getDuration()));
    }

    private void setCheckedNoiseSuppression(int mNoiseSuppressionLevel) {
        if(mNoiseSuppressionLevel >0){
            rb_1.setChecked(mNoiseSuppressionLevel == 1);
            rb_2.setChecked(mNoiseSuppressionLevel == 2);
            rb_3.setChecked(mNoiseSuppressionLevel == 3);
            rb_4.setChecked(mNoiseSuppressionLevel == 4);
        }
    }

    private void initTimeline(ClipInfo clipInfo) {
        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo, true);
        if (mTimeline == null)
            return;
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            videoClip = videoTrack.getClipByIndex(0);
        }
        if(null == videoClip){
            return;
        }
        int position = getAudioFxPositionInClip(videoClip,Constants.NOISE_SUPPRESSION_KEY);
        if(position>=0){
            noiseAudioEffect = videoClip.getAudioFxByIndex(position);
        }
    }

    @Override
    protected void initListener() {
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mClipFragment.stopEngine();
                } else {
                    mClipFragment.playVideo(startPlayTime, mTimeline.getDuration());
                }
            }
        });
        findViewById(R.id.iv_zoom_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimelineEditor.ZoomInSequence();
            }
        });
        findViewById(R.id.iv_zoom_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimelineEditor.ZoomOutSequence();
            }
        });
        mVolumeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishNoiseFun();
            }
        });
        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                mClipFragment.updateCurPlayTime(timeStamp);
                startPlayTime = timeStamp;
                if (null != mTimeline) {
                    tvCurTime.setText(TimeFormatUtil.formatUsToString2(timeStamp) + "/" + TimeFormatUtil.formatUsToString2(mTimeline.getDuration()));
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        rg_noise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int level = 0;
                if (checkedId == R.id.none) {
                    level = 0;
                } else if (checkedId == R.id.noise_level_one) {
                    level = 1;
                } else if (checkedId == R.id.noise_level_two) {
                    level = 2;
                } else if (checkedId == R.id.noise_level_three) {
                    level = 3;
                } else if (checkedId == R.id.noise_level_four) {
                    level = 4;
                }
                addNoiseSuppression(level);
            }
        });

    }


    @Override
    public void onClick(View v) {

    }

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
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayBtnLayout.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        timelineTimeSpan = mTimelineEditor.addTimeSpan(0, mTimeline.getDuration());
        timelineTimeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
        mTimelineEditor.selectTimeSpan(timelineTimeSpan);
        mTimelineEditor.unSelectAllTimeSpan();
    }

    private void finishNoiseFun() {
        mClipArrayList.get(mCurClipIndex).setNoiseSuppressionLevel(mNoiseSuppressionLevel);
        BackupData.instance().setClipInfoData(mClipArrayList);
        removeTimeline();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
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


    private void initVideoFragment() {
        mClipFragment = new SingleClipFragment();
        mClipFragment.setFragmentLoadFinisedListener(new SingleClipFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        });
        mClipFragment.setVideoFragmentCallBack(new SingleClipFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                playVideo(startPlayTime, endPlayTime);
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                Log.d(TAG, "playbackTimelinePosition stamp:" + stamp);
                seekMultiThumbnailSequenceView(stamp);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                    mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                } else {
                    mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                }
            }
        });
        mClipFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mClipFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mClipFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mClipFragment);
    }

    private void seekMultiThumbnailSequenceView(long timeStamp) {
        if (mMultiSequenceView != null) {
            long duration = mTimeline.getDuration();
            mMultiSequenceView.scrollTo(Math.round(((float) timeStamp) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }



    long startPlayTime = 0, endPlayTime = -1;

    public void playVideo(long startPlayTime, long endPlayTime) {
        mClipFragment.playVideo(startPlayTime, endPlayTime);
    }

    /**
     * 添加降噪特技
     * Add noise reduction effects
     * @param level
     */
    private void addNoiseSuppression(int level){
        if(null == videoClip){
            return;
        }
        if(null == noiseAudioEffect){

            noiseAudioEffect = videoClip.appendAudioFx(Constants.NOISE_SUPPRESSION_KEY);
        }

        if(null == noiseAudioEffect){
            return;
        }
        mNoiseSuppressionLevel = level;

        if(mNoiseSuppressionLevel == 0){
            int position = getAudioFxPositionInClip(videoClip,Constants.NOISE_SUPPRESSION_KEY);
            videoClip.removeAudioFx(position);
            noiseAudioEffect = null;
        }else{

            noiseAudioEffect.setIntVal(Constants.NOISE_SUPPRESSION_VALUE_KEY,mNoiseSuppressionLevel);
        }
        startPlayTime = 0;
        playVideo(startPlayTime,mTimeline.getDuration());
    }


    /**
     * 获取某个音频特技在片段上的位置
     * Gets the position of an audio stunt on the clip
     * @param nvsVideoClip
     * @param fxName
     * @return
     */
    private int getAudioFxPositionInClip(NvsVideoClip nvsVideoClip, String fxName){
        int position = -1;
        if(null != nvsVideoClip ){

            if(!TextUtils.isEmpty(fxName)){

                int count = nvsVideoClip.getAudioFxCount();
                if(count>0){
                    for (int i = 0; i < count; i++) {
                        NvsAudioFx fxByIndex = nvsVideoClip.getAudioFxByIndex(i);
                        if(TextUtils.equals(fxName,fxByIndex.getBuiltinAudioFxName())){
                            position = i;
                            break;
                        }
                    }
                }
            }
        }
        return position;
    }
}
