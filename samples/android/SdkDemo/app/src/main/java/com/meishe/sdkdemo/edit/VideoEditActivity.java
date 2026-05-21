package com.meishe.sdkdemo.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.arscene.BeautyHelper;
import com.meishe.arscene.bean.BeautyFxInfo;
import com.meishe.makeup.MakeupDataManager;
import com.meishe.makeup.MakeupHelper;
import com.meishe.makeup.makeup.Makeup;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.activity.BeautyActivity;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.Caption.CaptionActivity;
import com.meishe.sdkdemo.edit.adapter.AssetRecyclerViewAdapter;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.anim.AnimationActivity;
import com.meishe.sdkdemo.edit.animatesticker.AnimatedStickerActivity;
import com.meishe.sdkdemo.edit.background.BackgroundActivity;
import com.meishe.sdkdemo.edit.clipEdit.EditActivity;
import com.meishe.sdkdemo.edit.compoundcaption.CompoundCaptionActivity;
import com.meishe.sdkdemo.edit.createPic.CreatePicActivity;
import com.meishe.sdkdemo.edit.data.AssetInfoDescription;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.BitmapData;
import com.meishe.sdkdemo.edit.filter.FilterActivity;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.livephoto.LivePhotoActivity;
import com.meishe.sdkdemo.edit.makeup.BeautyMakeupActivity;
import com.meishe.sdkdemo.edit.mask.MaskActivity;
import com.meishe.sdkdemo.edit.music.MusicActivity;
import com.meishe.sdkdemo.edit.record.RecordActivity;
import com.meishe.sdkdemo.edit.theme.ThemeActivity;
import com.meishe.sdkdemo.edit.transition.TransitionActivity;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.PopCompileAndMakePicBottomView;
import com.meishe.sdkdemo.edit.watermark.WaterMarkActivity;
import com.meishe.sdkdemo.edit.watermark.WaterMarkUtil;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.DataConvertUtils;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.RecordAudioInfo;
import com.meishe.sdkdemo.utils.dataInfo.StickerInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.meishe.sdkdemo.utils.dataInfo.VideoFx;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.meishe.sdkdemo.edit.makeup.BeautyMakeupActivity.MAKEUP_MAP;
import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑Activity
 * @Description :VideoEditActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class VideoEditActivity extends BaseActivity {
    private static final String TAG = "VideoEditActivity";
    public static final int REQUESTRESULT_THEME = 1001;
    public static final int REQUESTRESULT_EDIT = 1002;
    public static final int REQUESTRESULT_FILTER = 1003;
    public static final int REQUESTRESULT_STICKER = 1004;
    public static final int REQUESTRESULT_CAPTION = 1005;
    public static final int REQUESTRESULT_TRANSITION = 1006;
    public static final int REQUESTRESULT_MUSIC = 1007;
    public static final int REQUESTRESULT_RECORD = 1008;
    public static final int REQUESTRESULT_WATERMARK = 1009;
    public static final int REQUESTRESULT_COMPOUND_CAPTION = 1010;
    public static final int REQUESTRESULT_ANIMATION = 1011;
    public static final int REQUESTRESULT_MASK = 1013;
    public static final int REQUESTRESULT_BACKGROUND = 1012;
    public static final int REQUESTRESULT_CREATE_PIC = 1013;
    public static final int REQUESTRESULT_MAKEUP = 1014;
    public static final int REQUESTRESULT_BEAUTY = 1015;
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private RecyclerView mAssetRecycleView;
    private ArrayList<AssetInfoDescription> mArrayAssetInfo;
    private LinearLayout mVolumeUpLayout;
    private SeekBar mVideoVoiceSeekBar;
    private SeekBar mMusicVoiceSeekBar;
    private SeekBar mDubbingSeekBarSeekBar;
    private TextView mVideoVoiceSeekBarValue;
    private TextView mMusicVoiceSeekBarValue;
    private TextView mDubbingSeekBarSeekBarValue;
    private ImageView mSetVoiceFinish;
    private RelativeLayout mCompilePage;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsAudioTrack mMusicTrack;
    private NvsAudioTrack mRecordAudioTrack;
    private VideoFragment mVideoFragment;
    private CompileVideoFragment mCompileVideoFragment;
    private boolean m_waitFlag = false;
    private long mThemeClipDuration;
    private PopCompileAndMakePicBottomView mBasePopupView;
    private MakeupHelper mMakeupHelper;
    private HashMap<String, Makeup> mSelectedMakeupMap;
    private BeautyHelper mBeautyHelper;
    private BeautyFxInfo mBeautyFxInfo;
    private final int[] videoEditImageId = {
//            R.mipmap.icon_edit_theme,
            R.mipmap.icon_edit_edit,
            R.mipmap.icon_edit_filter,
            R.mipmap.icon_edit_sticker,
            R.mipmap.icon_edit_animation,
            R.mipmap.icon_edit_mask,
            R.mipmap.icon_edit_caption,
            R.mipmap.icon_compound_caption,
            R.mipmap.icon_edit_background,
            R.mipmap.icon_watermark,
            R.mipmap.icon_edit_transition,
            R.mipmap.icon_edit_music,
            R.mipmap.icon_edit_voice,
            R.mipmap.ic_beauty_edit,
            R.mipmap.icon_edit_makeup
    };

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_video_edit;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mAssetRecycleView = (RecyclerView) findViewById(R.id.assetRecycleList);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        mVolumeUpLayout = (LinearLayout) findViewById(R.id.volumeUpLayout);
        mVideoVoiceSeekBar = (SeekBar) findViewById(R.id.videoVoiceSeekBar);
        mMusicVoiceSeekBar = (SeekBar) findViewById(R.id.musicVoiceSeekBar);
        mDubbingSeekBarSeekBar = (SeekBar) findViewById(R.id.dubbingSeekBar);
        mVideoVoiceSeekBarValue = (TextView) findViewById(R.id.videoVoiceSeekBarValue);
        mMusicVoiceSeekBarValue = (TextView) findViewById(R.id.musicVoiceSeekBarValue);
        mDubbingSeekBarSeekBarValue = (TextView) findViewById(R.id.dubbingSeekBarValue);
        mSetVoiceFinish = (ImageView) findViewById(R.id.finish);
        mCompilePage = (RelativeLayout) findViewById(R.id.compilePage);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.videoEdit);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
    }

    @Override
    protected void initData() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }

        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        int width = videoRes.imageWidth;
        int height = videoRes.imageHeight;
        Log.d(TAG, "width=" + width + " height=" + height);
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }
        initVideoFragment();
        initCompileVideoFragment();
        initAssetInfo();
        initAssetRecycleAdapter();
        initVoiceSeekBar();
        loadVideoClipFailTips();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
        if (mTimeline != null) {
            mMusicTrack = mTimeline.getAudioTrackByIndex(0);
            mRecordAudioTrack = mTimeline.getAudioTrackByIndex(1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeTimeline();
        clearData();
        AppManager.getInstance().finishActivity();
    }

    /**
     * 导入视频无效，提示
     * The imported video is invalid
     */
    private void loadVideoClipFailTips() {
        if (mTimeline == null || (mTimeline.getDuration() <= 0)) {
            String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
            Util.showDialog(VideoEditActivity.this, versionName[0], versionName[1], view -> {
                removeTimeline();
                AppManager.getInstance().finishActivity();
            });
        }
    }

    /**
     * 初始化声音调节view
     * Initialize the sound adjustment view
     */
    private void initVoiceSeekBar() {
        mVideoVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mMusicVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mDubbingSeekBarSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        if (mVideoTrack == null) {
            return;
        }
        int volumeVal = (int) Math.floor(mVideoTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        updateVideoVoiceSeekBar(volumeVal);
        updateMusicVoiceSeekBar(volumeVal);
        updateDubbingVoiceSeekBar(volumeVal);
    }

    private void updateVideoVoiceSeekBar(int volumeVal) {
        mVideoVoiceSeekBar.setProgress(volumeVal);
        mVideoVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateMusicVoiceSeekBar(int volumeVal) {
        mMusicVoiceSeekBar.setProgress(volumeVal);
        mMusicVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateDubbingVoiceSeekBar(int volumeVal) {
        mDubbingSeekBarSeekBar.setProgress(volumeVal);
        mDubbingSeekBarSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void setVideoVoice(int voiceVal) {
        if (mVideoTrack == null) {
            return;
        }
        updateVideoVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mVideoTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setOriginVideoVolume(volumeVal);
    }

    private void setMusicVoice(int voiceVal) {
        if (mMusicTrack == null) {
            return;
        }
        updateMusicVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mMusicTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setMusicVolume(volumeVal);
    }

    /**
     * 设置配音音量
     * setDubbingVoice
     *
     * @param voiceVal val
     */
    private void setDubbingVoice(int voiceVal) {
        if (mRecordAudioTrack == null) {
            return;
        }
        updateDubbingVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mRecordAudioTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setRecordVolume(volumeVal);
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(() ->
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0));
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME |
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
        mVideoFragment.setAutoPlay(true);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", true);
        bundle.putBoolean("voiceButtonVisible", true);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.video_layout, mVideoFragment).commit();
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

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        getFragmentManager().beginTransaction().add(R.id.compilePage, mCompileVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mCompileVideoFragment);
    }

    private void initAssetInfo() {
        mArrayAssetInfo = new ArrayList<>();
        String[] assetName = getResources().getStringArray(R.array.videoEditArray);
        for (int i = 0; i < assetName.length; i++) {
            mArrayAssetInfo.add(new AssetInfoDescription(assetName[i], videoEditImageId[i]));
        }
    }

    private void initAssetRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(VideoEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mAssetRecycleView.setLayoutManager(layoutManager);
        AssetRecyclerViewAdapter mAssetRecycleAdapter = new AssetRecyclerViewAdapter(VideoEditActivity.this);
        mAssetRecycleAdapter.updateData(mArrayAssetInfo);
        mAssetRecycleView.setAdapter(mAssetRecycleAdapter);
        mAssetRecycleView.addItemDecoration(new SpaceItemDecoration(8, 8));
        mAssetRecycleAdapter.setOnItemClickListener((view, pos) -> {
            if (m_waitFlag) {
                return;
            }
            mStreamingContext.stop();
            String tag = (String) view.getTag();
            if (tag.equals(getStringResourse(R.string.theme))) {
                onItemClickToActivity(ThemeActivity.class, VideoEditActivity.REQUESTRESULT_THEME);
            } else if (tag.equals(getStringResourse(R.string.edit))) {
                onItemClickToActivity(EditActivity.class, VideoEditActivity.REQUESTRESULT_EDIT);
            } else if (tag.equals(getStringResourse(R.string.filter))) {
                onItemClickToActivity(FilterActivity.class, VideoEditActivity.REQUESTRESULT_FILTER);
            } else if (tag.equals(getStringResourse(R.string.animatedSticker))) {
                onItemClickToActivity(AnimatedStickerActivity.class, VideoEditActivity.REQUESTRESULT_STICKER);
            } else if (tag.equals(getStringResourse(R.string.animation))) {
                onItemClickToActivity(AnimationActivity.class, VideoEditActivity.REQUESTRESULT_ANIMATION);
            } else if (tag.equals(getStringResourse(R.string.mask))) {
                onItemClickToActivity(MaskActivity.class, VideoEditActivity.REQUESTRESULT_MASK);
            } else if (tag.equals(getStringResourse(R.string.caption))) {
                onItemClickToActivity(CaptionActivity.class, VideoEditActivity.REQUESTRESULT_CAPTION);
            } else if (tag.equals(getStringResourse(R.string.compoundcaption))) {
                onItemClickToActivity(CompoundCaptionActivity.class, VideoEditActivity.REQUESTRESULT_COMPOUND_CAPTION);
            } else if (tag.equals(getStringResourse(R.string.background))) {
                onItemClickToActivity(BackgroundActivity.class, VideoEditActivity.REQUESTRESULT_BACKGROUND);
            } else if (tag.equals(getStringResourse(R.string.watermark))) {
                onItemClickToActivity(WaterMarkActivity.class, VideoEditActivity.REQUESTRESULT_WATERMARK);
            } else if (tag.equals(getStringResourse(R.string.createPic))) {
                onItemClickToActivity(CreatePicActivity.class, VideoEditActivity.REQUESTRESULT_CREATE_PIC);
            } else if (tag.equals(getStringResourse(R.string.music))) {
                onItemClickToActivity(MusicActivity.class, VideoEditActivity.REQUESTRESULT_MUSIC);
            } else if (tag.equals(getStringResourse(R.string.dub))) {
                onItemClickToActivity(RecordActivity.class, VideoEditActivity.REQUESTRESULT_RECORD);
            } else if (tag.equals(getStringResourse(R.string.transition))) {
                if (mTimeline != null) {
                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                    if (videoTrack != null) {
                        int clipCount = videoTrack.getClipCount();
                        if (clipCount <= 1) {
                            String[] transitionTipsInfo = getResources().getStringArray(R.array.transition_tips);
                            Util.showDialog(VideoEditActivity.this, transitionTipsInfo[0], transitionTipsInfo[1]);
                            return;
                        }
                    }
                }
                onItemClickToActivity(TransitionActivity.class, VideoEditActivity.REQUESTRESULT_TRANSITION);
            } else if (tag.equals(getStringResourse(R.string.beauty))) {
                if (null != mSelectedMakeupMap) {
                    Makeup makeup = mSelectedMakeupMap.get(MakeupDataManager.COMBINED_MAKEUP_TYPE);
                    mBeautyFxInfo = DataConvertUtils.makeupToBeauty(this, makeup, mBeautyFxInfo);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(BeautyActivity.BEAUTY_INFO, mBeautyFxInfo);
                onItemClickToActivity(BeautyActivity.class, bundle, VideoEditActivity.REQUESTRESULT_BEAUTY);
            } else if (tag.equals(getStringResourse(R.string.makeup))) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(MAKEUP_MAP, mSelectedMakeupMap);
                onItemClickToActivity(BeautyMakeupActivity.class, bundle, VideoEditActivity.REQUESTRESULT_MAKEUP);
            } else {
                String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                Util.showDialog(VideoEditActivity.this, tipsInfo[0], tipsInfo[1], tipsInfo[2]);
            }
        });
    }

    private void onItemClickToActivity(Class<? extends Activity> cls, int requstcode) {
        onItemClickToActivity(cls, null, requstcode);
    }

    private void onItemClickToActivity(Class<? extends Activity> cls, Bundle bundle, int requstcode) {
        m_waitFlag = true;
        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), cls, bundle, requstcode);
    }

    private String getStringResourse(int id) {
        return getApplicationContext().getResources().getString(id);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeTimeline();
                clearData();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                if (mBasePopupView == null) {
                    mBasePopupView = PopCompileAndMakePicBottomView.create(mContext, mTitleBar.getForwardLayout(), new PopCompileAndMakePicBottomView.OnBottomViewClickListener() {
                        @Override
                        public void onCreateVideo() {
                            mBasePopupView.dismiss();
                            mCompilePage.setVisibility(View.VISIBLE);
                            mCompileVideoFragment.compileVideo();
                        }

                        @Override
                        public void onCreatePicture() {
                            mBasePopupView.dismiss();
                            long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                            Bitmap bitmap = mStreamingContext.grabImageFromTimeline(mTimeline, timelineCurrentPosition,
                                    new NvsRational(1, 1), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);
                            String fileName = "meicam_" + System.currentTimeMillis() + ".jpg";
                            String filePath = com.meishe.engine.util.PathUtils.getVideoSavePath(fileName);
                            if (filePath.isEmpty()) {
                                return;
                            }
                            Observable.just(filePath).map(new Function<String, Boolean>() {
                                @Override
                                public Boolean apply(String filePath) throws Exception {
                                    return saveBitmap(bitmap, filePath);
                                }
                            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean ret) throws Exception {
                                    if (ret) {
                                        ToastUtil.showToastCenter(mContext, String.format(getResources().getString(R.string.compile_save_path), filePath));
                                        MediaScannerUtil.scanFile(filePath, "");
                                    } else {
                                        ToastUtil.showToast(mContext, getResources().getString(R.string.compile_save_failed));
                                        File file = new File(filePath);
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                    }
                                }
                            }).subscribe();
                        }

                        @Override
                        public void onCreateLivePhoto() {
                            mBasePopupView.dismiss();
                            AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), LivePhotoActivity.class);
                        }
                    });
                }
                if (mBasePopupView.isDismiss()) {
                    mBasePopupView.show();
                }
            }
        });
        mVideoVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setVideoVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMusicVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setMusicVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mDubbingSeekBarSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setDubbingVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSetVoiceFinish.setOnClickListener(this);
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
                }

                @Override
                public void compileVideoCancel() {
                    mCompilePage.setVisibility(View.GONE);
                }
            });
        }

        if (mVideoFragment != null) {
            mVideoFragment.setVideoVolumeListener(new VideoFragment.VideoVolumeListener() {
                @Override
                public void onVideoVolume() {
                    mVolumeUpLayout.setVisibility(View.VISIBLE);
                }
            });
        }
        mVolumeUpLayout.setOnTouchListener((v, event) -> true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish) {
            mVolumeUpLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUESTRESULT_THEME:
                String themeId = TimelineData.instance().getThemeData();
                NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
                int clipCount = videoTrackByIndex.getClipCount();
                TimelineUtil.applyTheme(mTimeline, themeId);
                int afclipCount = videoTrackByIndex.getClipCount();
                mThemeClipDuration = 0;
                if (afclipCount > clipCount) {
                    //存在片头主题 There is an opening title theme
                    NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(0);
                    mThemeClipDuration = clipByIndex.getOutPoint() - clipByIndex.getInPoint();
                }
                /*
                 * 重新添加字幕，防止某些主题会删除字幕
                 * Add subtitles again to prevent some topics from deleting them
                 * */
                updateCaption(mThemeClipDuration);
                mVideoFragment.playVideoButtonClick();
                break;
            case REQUESTRESULT_EDIT:
                TimelineUtil.reBuildVideoTrack(mTimeline);
                //TimelineUtil.buildColorAdjustInfo(mTimeline, TimelineData.instance().cloneClipInfoData());
                //TimelineUtil.buildTimelineBackground(mTimeline, TimelineData.instance().getClipInfoData());
                //TimelineUtil.buildTimelineMaskClipInfo(mTimeline, TimelineData.instance().getClipInfoData());
                //TimelineUtil.buildTimelineAnimation(mTimeline, TimelineData.instance().getClipInfoData());
                //TimelineUtil.buildAdjustCutInfo(mTimeline, TimelineData.instance().cloneClipInfoData());
                mVideoFragment.refreshLiveWindowFrame();
                break;
            case REQUESTRESULT_FILTER:
                TimelineUtil.buildFilterByTimeline(mTimeline);
                break;
            case REQUESTRESULT_STICKER:
                ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
                TimelineUtil.setSticker(mTimeline, stickerArray);
                break;
            case REQUESTRESULT_CAPTION:
                updateCaption(mThemeClipDuration);
                break;
            case REQUESTRESULT_COMPOUND_CAPTION:
                updateCompoundCaption();
                break;
            case REQUESTRESULT_TRANSITION:
                ArrayList<TransitionInfo> transitionInfoArray = TimelineData.instance().getTransitionInfoArray();
                if ((transitionInfoArray != null) && !transitionInfoArray.isEmpty()) {
                    TimelineUtil.setTransition(mTimeline, transitionInfoArray);
                }
                break;
            case REQUESTRESULT_MUSIC:
                List<MusicInfo> musicInfos = TimelineData.instance().getMusicData();
                TimelineUtil.buildTimelineMusic(mTimeline, musicInfos);
                break;
            case REQUESTRESULT_RECORD:
                ArrayList<RecordAudioInfo> audioInfos = TimelineData.instance().getRecordAudioData();
                TimelineUtil.buildTimelineRecordAudio(mTimeline, audioInfos);
                break;
            case REQUESTRESULT_WATERMARK:
                TimelineUtil.checkAndDeleteExitFX(mTimeline);
                boolean cleanWaterMark = data.getBooleanExtra(WaterMarkActivity.WATER_CLEAN, true);
                if (cleanWaterMark) {
                    mTimeline.deleteWatermark();
                } else {
                    WaterMarkUtil.setWaterMark(mTimeline, TimelineData.instance().getWaterMarkData());
                }
                //添加临时解决水印中效果不能去除问题，导致问题的原因大概率为每次操作的都不是同一个timeline
                //The effect of adding a temporary solution to the watermark cannot remove the problem, and the cause of the problem is probably that each operation is not the same timeline
                boolean hasEffect = data.getBooleanExtra(WaterMarkActivity.EFFECT_CLEAN, true);
                if (!hasEffect) {
                    NvsTimelineVideoFx lastFx = mTimeline.getLastTimelineVideoFx();
                    while (lastFx != null) {
                        String fxName = lastFx.getBuiltinTimelineVideoFxName();
                        if (TextUtils.equals(fxName, "Mosaic")
                                || TextUtils.equals(fxName, "Gaussian Blur")) {
                            mTimeline.removeTimelineVideoFx(lastFx);
                            break;
                        }
                        lastFx = mTimeline.getPrevTimelineVideoFx(lastFx);
                    }
                }
                VideoFx videoFx = TimelineData.instance().getVideoFx();
                mVideoFragment.setEffectByData(videoFx);
                mVideoFragment.refreshLiveWindowFrame();

                break;
            case REQUESTRESULT_ANIMATION:
                TimelineUtil.buildTimelineAnimation(mTimeline, TimelineData.instance().getClipInfoData());
                break;
            case REQUESTRESULT_MASK:
                TimelineUtil.buildTimelineMaskClipInfo(mTimeline, TimelineData.instance().getClipInfoData());
                break;
            case REQUESTRESULT_BACKGROUND:
                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                TimelineUtil.buildTimelineBackground(mTimeline, clipInfoData);
                break;
            case REQUESTRESULT_BEAUTY:
                buildApplyBeauty(data);
                break;
            case REQUESTRESULT_MAKEUP:
                buildApplyMakeup(data);
                break;
            default:
                break;
        }
        mVideoFragment.updateTotalDurationText();
    }

    /**
     * 应用美颜数据
     * Apply beauty data
     *
     * @param data data
     */
    private void buildApplyBeauty(Intent data) {
        if (null == mBeautyHelper) {
            mBeautyHelper = new BeautyHelper(mStreamingContext, BuildConfig.FACE_MODEL == 240, ParameterSettingValues.instance().isSingleBufferMode());
        }
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        mBeautyFxInfo = (BeautyFxInfo) data.getSerializableExtra(BeautyActivity.BEAUTY_INFO);
        if (null != mBeautyFxInfo) {
            for (ClipInfo clipInfo : clipInfos) {
                clipInfo.setBeautyFxInfo(mBeautyFxInfo);
            }
            TimelineUtil.buildTimelineBeauty(mTimeline, clipInfos, mBeautyHelper);
            mVideoFragment.refreshLiveWindowFrame();
        }
    }

    /**
     * 应用美妆数据
     * Apply makeup data
     *
     * @param data data
     */
    private void buildApplyMakeup(Intent data) {
        Serializable serializableExtra = data.getSerializableExtra(MAKEUP_MAP);
        if (serializableExtra == null) {
            mSelectedMakeupMap = null;
        } else {
            mSelectedMakeupMap = (HashMap<String, Makeup>) serializableExtra;
        }
        if (mMakeupHelper == null) {
            mMakeupHelper = new MakeupHelper(mStreamingContext, BuildConfig.FACE_MODEL == 240, false);
        }
        TimelineData.instance().setSelectedMakeupMap(mSelectedMakeupMap);
        TimelineUtil.reBuildVideoTrack(mTimeline);
        //TimelineUtil.buildTimelineMakeup(mTimeline, mMakeupHelper);
        mVideoFragment.refreshLiveWindowFrame();
    }


    private void updateCaption(long themeDuration) {
        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        TimelineUtil.setCaption(mTimeline, captionArray, themeDuration);
    }

    private void updateCompoundCaption() {
        ArrayList<CompoundCaptionInfo> captionArray = TimelineData.instance().getCompoundCaptionArray();
        TimelineUtil.setCompoundCaption(mTimeline, captionArray);
    }

    /**
     * 导出封面图片
     * Export cover image
     */
    private boolean saveBitmap(Bitmap bitmap, String filePath) {
        if (bitmap == null) {
            ToastUtil.showToast(this, "保存失败！");
            return false;
        }
        return Util.saveBitmapToSD(bitmap, filePath);
    }

    /**
     * 清空数据
     * Clear data
     */
    private void clearData() {
        TimelineData.instance().clear();
        BackupData.instance().clear();
        BitmapData.instance().clear();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }
}


