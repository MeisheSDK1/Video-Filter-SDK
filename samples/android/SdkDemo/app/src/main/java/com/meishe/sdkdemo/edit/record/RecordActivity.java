package com.meishe.sdkdemo.edit.record;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioFx;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.bean.DOPermission;
import com.meishe.sdkdemo.dialog.PermissionTipsPop;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.VerticalSeekBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.RecordAudioInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.permission.OnPermissionsSettingListener;
import com.meishe.sdkdemo.view.VerticalIndicatorSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :配音界面
 * @Description :Dubbing Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class RecordActivity extends BaseActivity {
    private final String TAG = "RecordActivity";
    private final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 100;
    private final int SEEK_TYPE_NULL = 0;
    private final int SEEK_TYPE_DRAG = 1;
    private final int SEEK_TYPE_PLAY = 2;

    private static float muteVolumeGain = 0f;
    private float videoLeftVolume = muteVolumeGain;
    private float musicLeftVolume = muteVolumeGain;
    private float themeLeftVolume = muteVolumeGain;
    private float stickerLeftVolume = muteVolumeGain;

    private VideoFragment m_videoFragment;
    private CustomTitleBar m_titleBar;
    private RelativeLayout m_bottomLayout;
    private NvsStreamingContext m_streamingContext;
    private NvsTimeline m_timeLine;
    private SqLayout m_sequenceLayout;
    private SqView m_sequenceView;
    private RelativeLayout m_playBtnLayout, m_zoomInBtn, m_zoomOutBtn;
    private Button m_recordFxBtn, m_playBtn, m_recordBeginBtn, m_recordStopBtn, m_recordDelBtn, m_fxOkBtn, m_okBtn;
    private TextView m_playCurTime;
    private RelativeLayout m_recordFxLayout;
    private RecyclerView m_recordFxRv;
    private VerticalIndicatorSeekBar m_recordVolumeSeekBar;
    private int m_seekTimeline = SEEK_TYPE_PLAY;
    private RecordFxAdapter m_recordFxAdapter;
    private List<RecordFxListItem> mDataList = new ArrayList<>();

    /**
     * 录音相关
     * Recording related
     */
    private long m_minRecordDuration = 1000000;
    private boolean m_isRecording = false, m_isSelectFx = false;
    private String mCurrentFxId;
    private long m_curRecordInPoint, m_curPositon;
    private RecordManager m_recordManager;
    private RecordAudioInfo m_recordAudioInfo;
    private Map<Long, RecordAudioInfo> m_audioInfoList = new HashMap<>();
    private NvsAudioTrack m_recordAudioTrack;
    private NotificationManager m_notificationManager;
    private boolean m_haveRecordPermission = false;

    private RadioGroup rg_noise;
    private TextView tvEffect, tvNoise;
    private NvsAudioFx noiseAudioEffect;
    private int mNoiseSuppressionLevel;
    private PermissionTipsPop mPermissionTipsPop;

    @Override
    protected int initRootView() {
        m_streamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_record;
    }

    @Override
    protected void initViews() {
        m_titleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        m_bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        m_sequenceLayout = (SqLayout) findViewById(R.id.sq_view);
        m_playBtnLayout = (RelativeLayout) findViewById(R.id.play_btn_layout);
        m_zoomInBtn = (RelativeLayout) findViewById(R.id.zoom_in_btn);
        m_zoomOutBtn = (RelativeLayout) findViewById(R.id.zoom_out_btn);
        m_recordFxBtn = (Button) findViewById(R.id.recordFxButton);
        m_playBtn = (Button) findViewById(R.id.play_btn);
        m_playCurTime = (TextView) findViewById(R.id.play_cur_time);
        m_recordBeginBtn = (Button) findViewById(R.id.record_begin_btn);
        m_recordStopBtn = (Button) findViewById(R.id.record_stop_btn);
        m_recordDelBtn = (Button) findViewById(R.id.record_del_btn);
        m_recordFxLayout = (RelativeLayout) findViewById(R.id.record_fx_layout);
        m_fxOkBtn = (Button) findViewById(R.id.fx_ok_btn);
        m_recordFxRv = (RecyclerView) findViewById(R.id.record_fx_rv);
        m_okBtn = (Button) findViewById(R.id.ok_btn);
        m_recordVolumeSeekBar = (VerticalIndicatorSeekBar) findViewById(R.id.record_volume_seekBar);
        m_recordVolumeSeekBar.setProgress((int) (1.0f / 3 * 100));
        rg_noise = (RadioGroup) findViewById(R.id.rg_noise);
        tvEffect = findViewById(R.id.audio_effect);
        tvNoise = findViewById(R.id.audio_noise);
    }

    @Override
    protected void initTitle() {
        m_titleBar.setTextCenter(R.string.dub);
        m_titleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initVideoFragment();
        updateSequenceView();
        getRecordFxList();
        m_recordManager = RecordManager.getInstance();
        updatePlaytimeText(0);

        ArrayList<RecordAudioInfo> recordAudioInfos = TimelineData.instance().getRecordAudioData();
        if (recordAudioInfos != null) {
            for (int i = 0; i < recordAudioInfos.size(); ++i) {
                RecordAudioInfo audioInfo = recordAudioInfos.get(i);
                if (audioInfo == null) {
                    continue;
                }
                m_sequenceLayout.addRecordView(audioInfo.getInPoint(), audioInfo.getOutPoint());
                m_audioInfoList.put(audioInfo.getInPoint(), audioInfo);

                /*
                 * 如果首帧就有录音，那么要显示设置录音的选项(因为刚刚进入到页面画面停在首帧)
                 * If there is a recording in the first frame, then the option to set the recording should be displayed
                 * (because the screen has just stopped on the first frame after entering the page)
                 * */
                if (audioInfo.getInPoint() == 0) {
                    haveRecordArea(0);
                }
            }
        }

        /*
         * 静音权限
         * Mute permission
         * */
        silenceRequest();

        // 录音权限 Recording permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                m_haveRecordPermission = true;
            } else {
                requestAudioPermission();
            }
        } else {
            m_haveRecordPermission = true;
        }
    }

    @Override
    protected void initListener() {
        m_titleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                stopRecording();
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

            }
        });

        m_zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_seekTimeline = SEEK_TYPE_NULL;
                m_sequenceView.zoomInSequence();
                m_sequenceLayout.reLayoutAllViews();
            }
        });

        m_zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_seekTimeline = SEEK_TYPE_NULL;
                m_sequenceView.zoomOutSequence();
                m_sequenceLayout.reLayoutAllViews();
            }
        });

        m_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });

        m_videoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (m_isRecording) {
                            m_recordManager.release();
                        }
                        if (m_isSelectFx) {
                            m_sequenceView.scrollTo(m_curPositon);
                        } else {
                            m_sequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                            m_videoFragment.seekTimeline(0, 0);
                            updatePlaytimeText(0);
                        }
                        /*
                         * 如果从0开始就有录音，则选中
                         * If there are recordings starting from 0, select
                         * */
                        for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
                            NvsAudioClip audioClip = m_recordAudioTrack.getClipByIndex(i);
                            if (audioClip == null) {
                                continue;
                            }
                            if (audioClip.getInPoint() == 0) {
                                haveRecordArea(0);
                                break;
                            }
                        }
                    }
                });
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                updatePlaytimeText(stamp);
                if (m_sequenceView != null) {
                    int x = Math.round((stamp / (float) m_timeLine.getDuration() * m_sequenceView.getSequenceWidth()));
                    m_sequenceView.smoothScrollTo(x, 0);
                }
                if (m_isRecording) {
                    m_sequenceLayout.updateRecordView(m_curRecordInPoint, stamp);
                    if (stamp - m_curRecordInPoint >= m_minRecordDuration) {
                        m_recordStopBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                    m_seekTimeline = SEEK_TYPE_PLAY;
                    m_playBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                } else {
                    m_seekTimeline = SEEK_TYPE_NULL;
                    m_playBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                }
            }
        });

        m_sequenceLayout.setHorizontalScrollListener(new SqLayout.HorizontalScrollListener() {
            @Override
            public void horizontalScrollStoped() {

            }

            @Override
            public void horizontalScrollChanged(long inPoint, boolean isDraging, long cur_audio_inpoint) {
                if (isDraging) {
                    m_seekTimeline = SEEK_TYPE_DRAG;
                }
                if (m_seekTimeline == SEEK_TYPE_DRAG || m_seekTimeline == SEEK_TYPE_PLAY) {
                    if (!m_isRecording) {
                        if (cur_audio_inpoint != -1) {
                            haveRecordArea(cur_audio_inpoint);
                        } else {
                            m_recordBeginBtn.setVisibility(View.VISIBLE);
                            m_recordDelBtn.setVisibility(View.GONE);
                            m_recordStopBtn.setVisibility(View.GONE);
                            m_recordFxBtn.setVisibility(View.GONE);
                            m_recordVolumeSeekBar.setVisibility(View.GONE);
                        }
                    }
                }
                if (m_seekTimeline == SEEK_TYPE_DRAG) {
                    m_videoFragment.seekTimeline(inPoint, 0);
                    updatePlaytimeText(inPoint);
                }
            }
        });

        m_recordBeginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!silenceRequest()) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(RecordActivity.this, android.Manifest.permission.RECORD_AUDIO)) {
                        m_haveRecordPermission = true;
                    } else {
                        requestAudioPermission();
                        return;
                    }
                } else {
                    m_haveRecordPermission = true;
                }
                if (m_haveRecordPermission) {
                    getOrginVolumeGain();
                    setVolumeGain(muteVolumeGain, muteVolumeGain, muteVolumeGain, muteVolumeGain);
                    m_recordManager.prepareAudio(PathUtils.getAudioRecordFilePath());
                }
            }
        });

        m_recordStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!silenceRequest()) {
                    return;
                }
                m_recordManager.release();
                setVolumeGain(videoLeftVolume, musicLeftVolume, themeLeftVolume, stickerLeftVolume);
            }
        });

        m_recordDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_recordBeginBtn.setVisibility(View.VISIBLE);
                m_recordStopBtn.setVisibility(View.GONE);
                m_recordDelBtn.setVisibility(View.GONE);
                m_recordFxBtn.setVisibility(View.GONE);
                m_recordVolumeSeekBar.setVisibility(View.GONE);
                deleteAudioClip(m_curRecordInPoint);
            }
        });

        m_recordFxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_curPositon = m_streamingContext.getTimelineCurrentPosition(m_timeLine);
                m_recordFxLayout.setVisibility(View.VISIBLE);
                m_bottomLayout.setVisibility(View.GONE);
                if (m_recordAudioTrack != null) {
                    for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
                        NvsAudioClip audioClip = m_recordAudioTrack.getClipByIndex(i);
                        if (audioClip == null) {
                            continue;
                        }
                        if (audioClip.getInPoint() == m_curRecordInPoint) {
                            NvsAudioFx fx = audioClip.getFxByIndex(0);
                            if (fx == null || fx.getBuiltinAudioFxName() == null || fx.getBuiltinAudioFxName().isEmpty()) {
                                m_recordFxAdapter.setItemSelectedByFxID(Constants.NO_FX);
                            } else {
                                m_recordFxAdapter.setItemSelectedByFxID(fx.getBuiltinAudioFxName());
                            }
                            break;
                        }
                    }
                }
            }
        });

        m_fxOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_isSelectFx = false;
                m_recordFxLayout.setVisibility(View.GONE);
                m_bottomLayout.setVisibility(View.VISIBLE);
                m_sequenceView.scrollTo(m_curPositon);
            }
        });

        m_recordManager.setOnRecordStart(new RecordManager.OnRecordStartListener() {
            @Override
            public void onRecordStart(Long id, String filePath) {
                doOnRecordStart(id, filePath);
            }

            @Override
            public void onRecordEnd() {
                doOnRecordEnd();
            }
        });

        m_okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
                TimelineData.instance().setRecordAudioData(new ArrayList<>(m_audioInfoList.values()));
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        });

        m_recordVolumeSeekBar.setOnSeekBarChangedListener(new VerticalIndicatorSeekBar.OnSeekBarChangedListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                float value = (float) progress / 100 * 3;
                Log.e("===>", "record volume: " + value);
                setAudioClipVolume(m_curRecordInPoint, value);
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

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
        tvEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEffect.setTextColor(getResources().getColor(R.color.ms_blue));
                ;
                tvNoise.setTextColor(getResources().getColor(R.color.white));
                m_recordFxRv.setVisibility(View.VISIBLE);
                rg_noise.setVisibility(View.GONE);

            }
        });
        tvNoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoise.setTextColor(getResources().getColor(R.color.ms_blue));
                ;
                tvEffect.setTextColor(getResources().getColor(R.color.white));
                m_recordFxRv.setVisibility(View.GONE);
                rg_noise.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void requestAudioPermission() {
        mPermissionTipsPop = PermissionTipsPop.Companion.create(this);
        DOPermission doPermission = new DOPermission(getString(R.string.permission_write_audio_head));
        doPermission.setDesc(getString(R.string.permission_desc_audio));
        String[] needRequestPermission = new String[]{Manifest.permission.RECORD_AUDIO};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionTipsPop.show(doPermission);
            requestPermissions(needRequestPermission, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopRecording();
        removeTimeline();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }

    private void getOrginVolumeGain() {
        NvsVideoTrack videoTrack = m_timeLine.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            videoLeftVolume = videoTrack.getVolumeGain().leftVolume;
        }
        /*
         * 音乐轨道
         * Music track
         * */
        NvsAudioTrack musicTrack = m_timeLine.getAudioTrackByIndex(0);
        if (musicTrack != null) {
            musicLeftVolume = musicTrack.getVolumeGain().leftVolume;
        }
        themeLeftVolume = m_timeLine.getThemeMusicVolumeGain().leftVolume;
        NvsTimelineAnimatedSticker sticker = m_timeLine.getFirstAnimatedSticker();
        while (sticker != null) {
            if (sticker.hasAudio()) {
                stickerLeftVolume = sticker.getVolumeGain().leftVolume;
                break;
            }
            sticker = m_timeLine.getNextAnimatedSticker(sticker);
        }
    }

    private void setVolumeGain(float videoLeftVolume,
                               float musicLeftVolume,
                               float themeLeftVolume,
                               float stickerLeftVolume) {
        /*
         * 视频轨道
         * Video track
         * */
        NvsVideoTrack videoTrack = m_timeLine.getVideoTrackByIndex(0);
        if (videoTrack != null) {
            videoTrack.setVolumeGain(videoLeftVolume, videoLeftVolume);
        }
        /*
         * 音乐轨道
         * Music track
         * */
        NvsAudioTrack musicTrack = m_timeLine.getAudioTrackByIndex(0);
        if (musicTrack != null) {
            musicTrack.setVolumeGain(musicLeftVolume, musicLeftVolume);
        }
        m_timeLine.setThemeMusicVolumeGain(themeLeftVolume, themeLeftVolume);
        NvsTimelineAnimatedSticker sticker = m_timeLine.getFirstAnimatedSticker();
        while (sticker != null) {
            if (sticker.hasAudio()) {
                sticker.setVolumeGain(stickerLeftVolume, stickerLeftVolume);
            }
            sticker = m_timeLine.getNextAnimatedSticker(sticker);
        }
    }

    private boolean silenceRequest() {
//        m_notificationManager = (NotificationManager) MSApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if(m_notificationManager != null && !m_notificationManager.isNotificationPolicyAccessGranted()) {
//                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                MSApplication.getContext().startActivity(intent);
//                return false;
//            }
//        }
        return true;
    }

    private void stopRecording() {
        if (m_isRecording) {
            m_recordManager.release();
        }
    }

    private void haveRecordArea(long cur_audio_inpoint) {
        m_curRecordInPoint = cur_audio_inpoint;
        m_recordDelBtn.setVisibility(View.VISIBLE);
        m_recordFxBtn.setVisibility(View.VISIBLE);
        m_recordBeginBtn.setVisibility(View.GONE);
        m_recordStopBtn.setVisibility(View.GONE);
        float volume = getAudioClipVolume(m_curRecordInPoint);
        if (volume != -1) {
            m_recordVolumeSeekBar.setVisibility(View.VISIBLE);
            m_recordVolumeSeekBar.setProgress((int) (volume / 3 * 100));
        } else {
            m_recordVolumeSeekBar.setVisibility(View.GONE);
        }
    }

    private void setRecordState(boolean is_recording) {
        if (is_recording) {
            m_recordBeginBtn.setVisibility(View.GONE);
            m_recordDelBtn.setVisibility(View.GONE);
            m_recordFxBtn.setVisibility(View.GONE);
            m_recordStopBtn.setVisibility(View.VISIBLE);
            m_recordStopBtn.setEnabled(false);
        } else {
            m_recordBeginBtn.setVisibility(View.VISIBLE);
            m_recordStopBtn.setVisibility(View.GONE);
            m_recordDelBtn.setVisibility(View.GONE);
            m_recordFxBtn.setVisibility(View.GONE);
        }
        /*
         * 禁掉界面的其他操作
         * Disable other operations on the interface
         * */
        m_playBtn.setEnabled(!is_recording);
        m_recordFxBtn.setEnabled(!is_recording);
        m_zoomInBtn.setEnabled(!is_recording);
        m_zoomOutBtn.setEnabled(!is_recording);
        m_sequenceLayout.setTouchEnabled(is_recording);
        m_videoFragment.setRecording(is_recording);
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(m_timeLine);
        m_timeLine = null;
    }

    private void playVideo() {
        if (m_videoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            long startTime = m_streamingContext.getTimelineCurrentPosition(m_timeLine);
            long endTime = m_timeLine.getDuration();
            m_videoFragment.playVideo(startTime, endTime);
        } else {
            m_videoFragment.stopEngine();
        }
    }

    /**
     * 获取声音特效列表
     * Get the list of sound effects
     */
    private void getRecordFxList() {
        if (m_recordFxAdapter == null) {
            if (m_streamingContext == null) {
                return;
            }
            RecordFxListItem item_none = new RecordFxListItem();
            item_none.fxID = Constants.NO_FX;
            item_none.fxName = getResources().getString(R.string.NO_FX);
            item_none.image_drawable = ContextCompat.getDrawable(this, R.drawable.record_none_fx);
            mDataList.add(item_none);

            List<RecordFxListItem> listFxAsset = Util.listRecordFxFromJson(this);
            if (listFxAsset != null) {
                mDataList.addAll(listFxAsset);
            }

            /*
             * 国际化
             * globalization
             * */
            String[] effectList = getResources().getStringArray(R.array.effect_list);
            for (int i = 0; i < effectList.length; i++) {
                mDataList.get(i).fxName = effectList[i];
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            m_recordFxAdapter = new RecordFxAdapter(this, mDataList);
            m_recordFxRv.setLayoutManager(linearLayoutManager);
            m_recordFxRv.setAdapter(m_recordFxAdapter);
            m_recordFxAdapter.setItemSelectedByFxID(Constants.NO_FX);

            m_recordFxAdapter.setOnItemClickListener(new RecordFxAdapter.OnItemClickListener() {
                @Override
                public void fxSelected(int pos, RecordFxListItem audioFxListItem) {
                    if (audioFxListItem == null || audioFxListItem.fxID == null || audioFxListItem.fxID.isEmpty()) {
                        return;
                    }
                    RecordAudioInfo cur_recordAudioInfo = m_audioInfoList.get(m_curRecordInPoint);
                    if (cur_recordAudioInfo == null) {
                        return;
                    }
                    /*
                     * 找到AudioClip
                     * Find AudioClip
                     * */
                    NvsAudioClip nvsAudioClip = null;
                    if (m_recordAudioTrack == null) {
                        return;
                    }
                    for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
                        nvsAudioClip = m_recordAudioTrack.getClipByIndex(i);
                        if (nvsAudioClip != null && nvsAudioClip.getInPoint() == cur_recordAudioInfo.getInPoint()) {
                            break;
                        }
                    }
                    if (nvsAudioClip == null) {
                        return;
                    }
                    int audioFxPos = getAudioFxPositionInClip(nvsAudioClip, mCurrentFxId);
                    if (audioFxPos >= 0) {
                        nvsAudioClip.removeFx(audioFxPos);
                    }
                    cur_recordAudioInfo.setFxID(audioFxListItem.fxID);
                    if (!audioFxListItem.fxID.equals(Constants.NO_FX)) {
                        nvsAudioClip.appendFx(audioFxListItem.fxID);
                    }
                    m_isSelectFx = true;
                    m_videoFragment.playVideo(cur_recordAudioInfo.getInPoint(), cur_recordAudioInfo.getOutPoint());
                    mCurrentFxId = audioFxListItem.fxID;
                }
            });
        }
    }

    private void initVideoFragment() {
        m_timeLine = TimelineUtil.createTimeline();
        if (m_timeLine == null)
            return;
        m_recordAudioTrack = m_timeLine.getAudioTrackByIndex(1);
        m_videoFragment = new VideoFragment();
        m_videoFragment.setAutoPlay(false);
        m_videoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                m_videoFragment.seekTimeline(m_streamingContext.getTimelineCurrentPosition(m_timeLine), 0);
            }
        });
        m_videoFragment.setTimeline(m_timeLine);
        Bundle m_bundle = new Bundle();
        m_bundle.putInt("titleHeight", m_titleBar.getLayoutParams().height);
        m_bundle.putInt("bottomHeight", m_bottomLayout.getLayoutParams().height);
        m_bundle.putBoolean("playBarVisible", false);
        m_bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        m_videoFragment.setArguments(m_bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, m_videoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(m_videoFragment);
    }

    /**
     * 更新缩略图
     * Update thumbnail
     */
    private void updateSequenceView() {
        if (m_timeLine == null)
            return;
        m_sequenceView = m_sequenceLayout.getSqView();
        final ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> infoDescArray = new ArrayList<>();
        NvsVideoTrack videoTrack = m_timeLine.getVideoTrackByIndex(0);
        if (videoTrack == null)
            return;
        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
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
        double duration = (double) m_timeLine.getDuration();
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) m_playBtnLayout.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        m_sequenceView.setStartPadding(sequenceLeftPadding);
        m_sequenceView.setEndPadding(halfScreenWidth);
        m_sequenceLayout.initData((long) duration, infoDescArray);
    }

    private void updatePlaytimeText(long playTime) {
        long totalDuaration = m_timeLine.getDuration();
        String totalStr = TimeFormatUtil.formatUsToString1(totalDuaration);
        String playTimeStr = TimeFormatUtil.formatUsToString1(playTime);
        String tmpStr = playTimeStr + "/" + totalStr;
        m_playCurTime.setText(tmpStr);
    }

    public void doOnRecordStart(Long id, String filePath) {
        /*
         * 静音
         * Mute
         * */
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if(audioManager != null){
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//            audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        }

        m_isRecording = true;
        m_curRecordInPoint = m_streamingContext.getTimelineCurrentPosition(m_timeLine);
        m_videoFragment.playVideoButtonClick(m_curRecordInPoint, m_timeLine.getDuration());
        m_sequenceLayout.addRecordView(m_curRecordInPoint, 0);
        setRecordState(true);

        if (m_recordAudioInfo == null) {
            m_recordAudioInfo = new RecordAudioInfo();
        }
        m_recordAudioInfo.clear();
        m_recordAudioInfo.setId(id);
        m_recordAudioInfo.setPath(filePath);
        m_recordAudioInfo.setInPoint(m_curRecordInPoint);
    }

    public void doOnRecordEnd() {
        /*
         * 取消静音
         * Unmute
         * */
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if(audioManager != null){
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        }

        m_videoFragment.stopEngine();
        m_isRecording = false;
        setRecordState(false);

        if (m_recordAudioInfo != null) {
            long cur_point = m_streamingContext.getTimelineCurrentPosition(m_timeLine);
            m_recordAudioInfo.setOutPoint(cur_point);
        }
        addAudioClip(m_recordAudioInfo);
    }

    private void addAudioClip(RecordAudioInfo recordInfo) {
        if (recordInfo == null || m_recordAudioTrack == null) {
            return;
        }
        m_audioInfoList.put(recordInfo.getInPoint(), recordInfo.clone());

        /*
         * 清除掉重合区域的录音
         * Clear out recordings in coincident areas
         * */
        List<NvsAudioClip> remove_list = new ArrayList<>();
        for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
            NvsAudioClip audioClip = m_recordAudioTrack.getClipByIndex(i);
            if (audioClip == null) {
                continue;
            }
            Log.e("===>", "clip in: " + audioClip.getInPoint() + " rr in: " + recordInfo.getInPoint() + " rr out: " + recordInfo.getOutPoint());

            /*
             * 全覆盖则删除，部分覆盖则改trimIn
             * Full coverage is deleted, and partial coverage is changed to trimIn
             * */
            if (audioClip.getInPoint() < recordInfo.getOutPoint() && audioClip.getInPoint() >= recordInfo.getInPoint()
                    && audioClip.getOutPoint() <= recordInfo.getOutPoint()) {
                remove_list.add(audioClip);
                Log.e("===>", "remove: " + audioClip.getFilePath() + " " + audioClip.getInPoint());
            } else if (audioClip.getInPoint() < recordInfo.getOutPoint() && audioClip.getInPoint() >= recordInfo.getInPoint()
                    && audioClip.getOutPoint() > recordInfo.getOutPoint()) {
                Log.e("===>", "change trimIn: " + audioClip.getFilePath() + " " + audioClip.getInPoint());
                /*
                 * 处理数据
                 * Data processing
                 * */
                RecordAudioInfo recordAudioInfo = deleteAudioInfoData(audioClip.getInPoint());
                recordAudioInfo.setInPoint(recordInfo.getOutPoint());
                recordAudioInfo.setTrimIn(recordInfo.getOutPoint() - audioClip.getInPoint());
                m_audioInfoList.put(recordAudioInfo.getInPoint(), recordAudioInfo);

                /*
                 * 选中这个录音
                 * Select this recording
                 * */
                haveRecordArea(recordInfo.getOutPoint());
            }
        }
        /*
         * 删除数据
         * delete data
         * */
        if (remove_list.size() > 0) {
            for (int i = 0; i < remove_list.size(); ++i) {
                deleteAudioClip(remove_list.get(i).getInPoint());
            }
        }
        /*
         * 重新设置UI
         * Reset UI
         * */
        m_sequenceLayout.clearAllAreas();
        Set<Map.Entry<Long, RecordAudioInfo>> set = m_audioInfoList.entrySet();
        Iterator<Map.Entry<Long, RecordAudioInfo>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, RecordAudioInfo> entry = iterator.next();
            RecordAudioInfo audioInfo = entry.getValue();
            if (audioInfo == null) {
                continue;
            }
            m_sequenceLayout.addRecordView(audioInfo.getInPoint(), audioInfo.getOutPoint());
        }

        /*
         * 添加音频到视频上
         * Add audio to video
         * */
        m_recordAudioTrack.addClip(recordInfo.getPath(), recordInfo.getInPoint(), 0, recordInfo.getOutPoint() - recordInfo.getInPoint());
    }

    private void deleteAudioClip(long del_point) {
        if (m_recordAudioTrack == null) {
            return;
        }
        /*
         * UI层删除
         * UI layer delete
         * */
        m_sequenceLayout.deleteRecordView(del_point);
        /*
         * 数据层删除
         * Data layer deletion
         * */
        deleteAudioInfoData(del_point);
        /*
         * 音轨上删除
         * Delete on track
         * */
        for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
            NvsAudioClip audioClip = m_recordAudioTrack.getClipByIndex(i);
            if (audioClip == null) {
                continue;
            }
            if (audioClip.getInPoint() == del_point) {
                m_recordAudioTrack.removeClip(i, true);
                break;
            }
        }
    }

    private RecordAudioInfo deleteAudioInfoData(long del_point) {
        RecordAudioInfo recordAudioInfo = null;
        Set<Map.Entry<Long, RecordAudioInfo>> set = m_audioInfoList.entrySet();
        Iterator<Map.Entry<Long, RecordAudioInfo>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, RecordAudioInfo> entry = iterator.next();
            if (del_point == entry.getKey()) {
                recordAudioInfo = entry.getValue().clone();
                iterator.remove();
                break;
            }
        }
        return recordAudioInfo;
    }

    private void setAudioClipVolume(long inPoint, float volume) {
        if (m_recordAudioTrack != null) {
            RecordAudioInfo cur_recordAudioInfo = m_audioInfoList.get(inPoint);
            for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
                NvsAudioClip audioClip = m_recordAudioTrack.getClipByIndex(i);
                if (audioClip == null) {
                    continue;
                }
                if (audioClip.getInPoint() == inPoint && cur_recordAudioInfo != null) {
                    audioClip.setVolumeGain(volume, volume);
                    cur_recordAudioInfo.setVolume(volume);
                    break;
                }
            }
        }
    }

    private float getAudioClipVolume(long inPoint) {
        if (m_recordAudioTrack != null) {
            for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
                NvsAudioClip audioClip = m_recordAudioTrack.getClipByIndex(i);
                if (audioClip == null) {
                    continue;
                }
                if (audioClip.getInPoint() == inPoint) {
                    return audioClip.getVolumeGain().leftVolume;
                }
            }
        }
        return -1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_CODE) {
            if (null != mPermissionTipsPop && mPermissionTipsPop.isShow()) {
                mPermissionTipsPop.dismiss();
                mPermissionTipsPop = null;
            }
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                m_haveRecordPermission = true;
            } else {
                Util.setPermissionsSetting(RecordActivity.this, new OnPermissionsSettingListener() {
                    @Override
                    public void onSettingCancel() {

                    }

                    @Override
                    public void onSettingConfirm() {

                    }
                });
            }
        }
    }

    /**
     * 添加降噪特技
     * Add noise reduction effects
     *
     * @param level
     */
    private void addNoiseSuppression(int level) {
        if (null == m_timeLine) {
            return;
        }
        RecordAudioInfo cur_recordAudioInfo = m_audioInfoList.get(m_curRecordInPoint);
        if (cur_recordAudioInfo == null) {
            return;
        }
        /*
         * 找到AudioClip
         * Find AudioClip
         * */
        NvsAudioClip nvsAudioClip = null;
        if (m_recordAudioTrack == null) {
            return;
        }
        for (int i = 0; i < m_recordAudioTrack.getClipCount(); ++i) {
            //nvsAudioClip  = m_recordAudioTrack.getClipByTimelinePosition(m_streamingContext.getTimelineCurrentPosition(m_timeLine));
            nvsAudioClip = m_recordAudioTrack.getClipByIndex(i);
            if (nvsAudioClip != null && nvsAudioClip.getInPoint() == cur_recordAudioInfo.getInPoint()) {
                break;
            }
        }
        if (nvsAudioClip == null) {
            return;
        }
        if (null == noiseAudioEffect) {

            noiseAudioEffect = nvsAudioClip.appendFx(Constants.NOISE_SUPPRESSION_KEY);
        }

        if (null == noiseAudioEffect) {
            return;
        }
        mNoiseSuppressionLevel = level;

        if (mNoiseSuppressionLevel == 0) {
            int pos = getAudioFxPositionInClip(nvsAudioClip, Constants.NOISE_SUPPRESSION_KEY);

            nvsAudioClip.removeFx(pos);
            noiseAudioEffect = null;
        } else {

            noiseAudioEffect.setIntVal(Constants.NOISE_SUPPRESSION_VALUE_KEY, mNoiseSuppressionLevel);
        }
        cur_recordAudioInfo.setNoiseSuppressionLevel(mNoiseSuppressionLevel);
        m_videoFragment.playVideo(cur_recordAudioInfo.getInPoint(), cur_recordAudioInfo.getOutPoint());
    }

    /**
     * 获取某个音频特技在片段上的位置
     * Gets the position of an audio stunt on the clip
     *
     * @param nvsAudioClip
     * @param fxName
     * @return
     */
    private int getAudioFxPositionInClip(NvsAudioClip nvsAudioClip, String fxName) {
        int position = -1;
        if (null != nvsAudioClip) {

            if (!TextUtils.isEmpty(fxName)) {

                int count = nvsAudioClip.getFxCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        NvsAudioFx fxByIndex = nvsAudioClip.getFxByIndex(i);
                        if (TextUtils.equals(fxName, fxByIndex.getBuiltinAudioFxName())) {
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
