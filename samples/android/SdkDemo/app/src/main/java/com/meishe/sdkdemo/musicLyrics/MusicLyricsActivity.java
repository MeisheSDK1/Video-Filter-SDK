package com.meishe.sdkdemo.musicLyrics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.musiclyrics.MusicCaptionInfo;
import com.meishe.musiclyrics.NvMusicLyricsHelper;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.bean.MusicLyrics;
import com.meishe.sdkdemo.edit.CompileVideoFragment;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.data.AssetInfoDescription;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.BitmapData;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.music.SelectMusicActivity;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.PackageManagerUtil;
import com.meishe.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.meicam.sdk.NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTIONSTYLE;
import static com.meishe.sdkdemo.utils.Constants.SELECT_MUSIC_FROM_MUSICLYRICS;
import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2020/6/28.
 * @Description :音乐唱词 Activity
 * @Description :MusicLyricsActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MusicLyricsActivity extends BaseActivity {
    private static final String TAG = "MusicLyricsActivity";

    private VideoFragment mVideoFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsAudioTrack mMusicTrack;

    private CustomTitleBar mTitleBar;

    private RelativeLayout mBottomLayout;
    private RecyclerView mCaptionRecycleView;
    private RelativeLayout mCompilePage;
    private CompileVideoFragment mCompileVideoFragment;
    private LinearLayout mSelectMusic;
    private LinearLayout mSelectCaption;

    private List<MusicInfo> musicInfosClone = new ArrayList<>();
    private long m_curInPoint;
    private LinearLayout mVolumeUpLayout;
    private LinearLayout mCaptionLayout;
    private SeekBar mVideoVoiceSeekBar;
    private SeekBar mMusicVoiceSeekBar;
    private TextView mVideoVoiceSeekBarValue;
    private TextView mMusicVoiceSeekBarValue;
    private ImageView mSetVoiceFinish;
    private ImageView mSetCaptionFinish;
    private ArrayList<AssetInfoDescription> mArrayCaptionInfo;
    private LrcCaptionRecyclerViewAdapter mCaptionRecycleAdapter;

    int[] CaptionStyleImageId = {
            R.mipmap.no,
            R.mipmap.lyrics_caption,
            R.mipmap.lyrics_caption,
            R.mipmap.lyrics_caption,
            R.mipmap.lyrics_caption,
            R.mipmap.lyrics_caption,
    };

    private ArrayList<MusicCaptionInfo> mCaptionInfos;

    private String mLrcCaptionPackagePath[] = {
            "E895DACC-0AFC-48D0-A397-BBC416A5F8A5",
            "E2A0505C-80DE-4AF3-8F4C-CFE7ECF67AC3",
            "4B4348C0-33D5-459E-8003-A3A1FE2186B1",
            "41CB346F-6111-4633-AA6B-DB6165E6804D,97D562AE-9D7D-4074-B912-B554B3EEE69A",
            "F67275C8-04E7-4717-9EC5-BF5B24A486BB",
            "8AD8B89F-128F-488B-9A73-883680B2C0CF",
    };
    private int mCurrentLrcIndex = -1;
    private String mDefaultCaptionStyle = "E895DACC-0AFC-48D0-A397-BBC416A5F8A5";


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_music_lyrics;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mVolumeUpLayout = (LinearLayout) findViewById(R.id.volumeUpLayout);
        mVideoVoiceSeekBar = (SeekBar) findViewById(R.id.videoVoiceSeekBar);
        mMusicVoiceSeekBar = (SeekBar) findViewById(R.id.musicVoiceSeekBar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        mCompilePage = (RelativeLayout) findViewById(R.id.compilePage);
        mSelectMusic = (LinearLayout) findViewById(R.id.selectMusic);
        mSelectCaption = (LinearLayout) findViewById(R.id.selectCaption);
        mVideoVoiceSeekBarValue = (TextView) findViewById(R.id.videoVoiceSeekBarValue);
        mMusicVoiceSeekBarValue = (TextView) findViewById(R.id.musicVoiceSeekBarValue);
        mSetVoiceFinish = (ImageView) findViewById(R.id.finish);
        mCaptionLayout = (LinearLayout) findViewById(R.id.captionStyleLayout);
        mCaptionRecycleView = (RecyclerView) findViewById(R.id.captionRecycleList);
        mSetCaptionFinish = (ImageView) findViewById(R.id.captionfinish);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.musicCaption);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
    }

    @Override
    protected void initData() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }
        mCaptionInfos = new ArrayList<>();

        initVideoFragment();
        initCompileVideoFragment();
        installCaptionPackage();
        initAssetInfo();
        initAssetRecycleAdapter();
        initVoiceSeekBar();
    }

    private void installCaptionPackage() {
        String[] path_list = null;
        try {
            path_list = this.getAssets().list("musiccaption");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> fullPath_List = new ArrayList<String>();
        ArrayList<StringBuilder> m_captionId_List = new ArrayList<StringBuilder>();
        if (path_list != null) {
//            Map<String, String> captionStyle = new HashMap<>();
            for (int i = 0; i < path_list.length; ++i) {
                String jsonPath = "musiccaption/" + path_list[i] + File.separator + "info.json";
                String jsonText = ParseJsonFile.readAssetJsonFile(this, jsonPath);
                if (TextUtils.isEmpty(jsonText)) {
                    return;
                }
                MusicLyrics musicLyrics = ParseJsonFile.fromJson(jsonText, new TypeToken<MusicLyrics>() {
                }.getType());
                if (musicLyrics == null) {
                    return;
                }
                String packagePath = "assets:/musiccaption/" + path_list[i] + File.separator + musicLyrics.getPackageFileName();
                if (TextUtils.isEmpty(packagePath)){
                    continue;
                }
                fullPath_List.add(packagePath);
                m_captionId_List.add(new StringBuilder());
            }
            for (int i = 0; i < fullPath_List.size(); i++) {
                String packageId = PackageManagerUtil.installAssetPackage(fullPath_List.get(i), null, ASSET_PACKAGE_TYPE_CAPTIONSTYLE);
                m_captionId_List.set(i, new StringBuilder(packageId));
                if (packageId.isEmpty()) {
                    Log.e(TAG, "Failed to install captionStyle package!");
                }
            }
        }
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }

    private void initAssetInfo() {
        mArrayCaptionInfo = new ArrayList<>();
        String[] assetName = {getString(R.string.asset_nothing),
                getString(R.string.setting_sun),
                getString(R.string.smoke),
                getString(R.string.happy),
                getString(R.string.confession),
                getString(R.string.time)};
        for (int i = 0; i < assetName.length; i++) {
            mArrayCaptionInfo.add(new AssetInfoDescription(assetName[i], CaptionStyleImageId[i]));
        }
    }

    private void initAssetRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(MusicLyricsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mCaptionRecycleView.setLayoutManager(layoutManager);
        mCaptionRecycleAdapter = new LrcCaptionRecyclerViewAdapter(MusicLyricsActivity.this);
        mCaptionRecycleAdapter.updateData(mArrayCaptionInfo);
        mCaptionRecycleView.setAdapter(mCaptionRecycleAdapter);
        mCaptionRecycleView.addItemDecoration(new SpaceItemDecoration(18, 12));
        mCaptionRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                mCurrentLrcIndex = pos;
                addCaption();
                mStreamingContext.playbackTimeline(mTimeline, 0, -1, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
            }
        });
    }

    private void addCaption() {
        NvsTimelineCaption first = mTimeline.getFirstCaption();
        if (first != null) {
            while (first != mTimeline.getLastCaption()) {
                NvsTimelineCaption next = mTimeline.getNextCaption(first);
                mTimeline.removeCaption(first);
                first = next;
            }
            mTimeline.removeCaption(first);
        }
        if (mCaptionInfos == null) {
            return;
        }
        for (int i = 0; i < mCaptionInfos.size(); i++) {
            String text = mCaptionInfos.get(i).getCaptionText();
            long start = mCaptionInfos.get(i).getCaptionStartTime();
            long durtion = mCaptionInfos.get(i).getCaptionDurtion();
            if (mCurrentLrcIndex == 3) {
                /*添加字幕 最后一个参数为字幕的packageID，字幕需要先安装*/
                NvsTimelineCaption caption1 = mTimeline.addCaption(text, start, durtion, mLrcCaptionPackagePath[mCurrentLrcIndex].split(",")[0]);
                NvsTimelineCaption caption2 = mTimeline.addCaption(text, start, durtion, mLrcCaptionPackagePath[mCurrentLrcIndex].split(",")[1]);
            } else if (mCurrentLrcIndex != -1) {
                NvsTimelineCaption caption = mTimeline.addCaption(text, start, durtion, mLrcCaptionPackagePath[mCurrentLrcIndex]);
            } else if (mCurrentLrcIndex == -1) {
                NvsTimelineCaption caption = mTimeline.addCaption(text, start, durtion, mLrcCaptionPackagePath[0]);
            }

        }
    }

    private void initVoiceSeekBar() {
        mVideoVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mMusicVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        if (mVideoTrack == null)
            return;
        int volumeVal = (int) Math.floor(mVideoTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        updateVideoVoiceSeekBar(volumeVal);
        updateMusicVoiceSeekBar(volumeVal);
    }

    private void updateVideoVoiceSeekBar(int volumeVal) {
        mVideoVoiceSeekBar.setProgress(volumeVal);
        mVideoVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateMusicVoiceSeekBar(int volumeVal) {
        mMusicVoiceSeekBar.setProgress(volumeVal);
        mMusicVoiceSeekBarValue.setText(String.valueOf(volumeVal));
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
        bundle.putBoolean("voiceButtonVisible", true);
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

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        getFragmentManager().beginTransaction()
                .add(R.id.compilePage, mCompileVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mCompileVideoFragment);
    }

    private void clearData() {
        TimelineData.instance().clear();
        BackupData.instance().clear();
        BitmapData.instance().clear();
    }

    private void removeTimeline() {
        TimelineData.instance().setMusicList(musicInfosClone);
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    @Override
    protected void initListener() {
        mSelectMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle musicBundle = new Bundle();
                musicBundle.putInt(Constants.SELECT_MUSIC_FROM, SELECT_MUSIC_FROM_MUSICLYRICS);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                        SelectMusicActivity.class,
                        musicBundle,
                        Constants.SELECT_MUSIC_FROM_MUSICLYRICS);
            }
        });

        mSelectCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCaptionLayout.setVisibility(View.VISIBLE);
            }
        });

        mSetVoiceFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVolumeUpLayout.setVisibility(View.GONE);
            }
        });

        mSetCaptionFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCaptionLayout.setVisibility(View.GONE);
            }
        });


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
                mCompilePage.setVisibility(View.VISIBLE);
                mCompileVideoFragment.compileVideo();
            }
        });

        mCompilePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
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

        mVideoFragment.setVideoVolumeListener(new VideoFragment.VideoVolumeListener() {
            @Override
            public void onVideoVolume() {
                mVolumeUpLayout.setVisibility(View.VISIBLE);
            }
        });
        mVolumeUpLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
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
    }

    /**
     * 设置视频声音强度
     * Set video sound intensity
     *
     * @param voiceVal
     */
    private void setVideoVoice(int voiceVal) {
        if (mVideoTrack == null)
            return;
        updateVideoVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mVideoTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setOriginVideoVolume(volumeVal);
    }

    /**
     * 设置背景音乐声音强度
     * Set background music sound intensity
     *
     * @param voiceVal
     */
    private void setMusicVoice(int voiceVal) {
        if (mMusicTrack == null)
            return;
        updateMusicVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mMusicTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setMusicVolume(volumeVal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMusicTrack = mTimeline.getAudioTrackByIndex(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeTimeline();
        clearData();
        AppManager.getInstance().finishActivity();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (data == null)
            return;
        switch (requestCode) {
            case SELECT_MUSIC_FROM_MUSICLYRICS:
                // 处理从单段音乐选择的音乐
                //Process music selected from a single piece of music
                MusicInfo musicInfo = (MusicInfo) data.getSerializableExtra("select_music");
                if (mTimeline == null || null == musicInfo) {
                    return;
                }
                String lrcPath = musicInfo.getLrcPath();
                if (lrcPath != null && lrcPath != "") {
                    long startTime = musicInfo.getTrimIn();
                    long endTime = musicInfo.getTrimOut();
                    List<Map<Long, String>> lrcList = NvMusicLyricsHelper.analysisLrcFile(this, lrcPath);
                    if (lrcList == null) {
                        ToastUtil.showToast(MusicLyricsActivity.this, "lyrics is not authorised");
                        Logger.e("Functionality lyrics is not authorised!");
                        return;
                    }
                    mCaptionInfos = NvMusicLyricsHelper.getCaptionInfoList(lrcList, startTime, endTime, 0);
                    if (mCaptionInfos == null) {
                        ToastUtil.showToast(MusicLyricsActivity.this, "lyrics is not authorised");
                        Logger.e("Functionality lyrics is not authorised!");
                        return;
                    }
                }
                addCaption();
                musicInfosClone.clear();
                musicInfosClone.add(musicInfo);
                m_curInPoint = 0;
                TimelineUtil.buildTimelineMusic(mTimeline, musicInfosClone);
                mVideoFragment.playVideo(m_curInPoint, mTimeline.getDuration());
                break;
            default:
                break;
        }
    }
}
