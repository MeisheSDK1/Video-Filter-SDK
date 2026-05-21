package com.cdv.customvideofx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsUtils;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.meicam.sdk.NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT;

public class MainActivity extends AppCompatActivity implements
        NvsStreamingContext.PlaybackCallback,
        NvsStreamingContext.PlaybackCallback2,
        NvsStreamingContext.PlaybackExceptionCallback,
        NvsStreamingContext.StreamingEngineCallback,
        NvsStreamingContext.HardwareErrorCallback,
        NvsStreamingContext.SeekingCallback,
        NvsStreamingContext.CompileCallback,
        NvsStreamingContext.CompileCallback2,
        NvsStreamingContext.CompileCallback3 {
    private static final String TAG = "Meicam";
    private String mCompilePath;
    private NvsLiveWindow mLiveWindow;
    private ImageView mPlay;
    private ImageView mCompile;
    private SeekBar mPlaySeekBar;
    private TextView mStartTime;
    private TextView mEndTime;
    private RelativeLayout mCompileLayout;
    private TextView mCompileValue;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private List<String> mSelectPath = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String licensePath = "assets:/meishesdk.lic";
        NvsStreamingContext.setMaxReaderCount(10);
        NvsStreamingContext.init(this, licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_8K_EDIT
                | NvsStreamingContext.STREAMING_CONTEXT_FLAG_ENABLE_HDR_DISPLAY_WHEN_SUPPORTED
                | NvsStreamingContext.STREAMING_CONTEXT_FLAG_INTERRUPT_STOP_FOR_INTERNAL_STOP
                | NvsStreamingContext.STREAMING_CONTEXT_FLAG_NEED_GIF_MOTION);
        setContentView(R.layout.activity_main);
        //流媒体初始化
        mStreamingContext = NvsStreamingContext.getInstance();
        mStreamingContext.setDefaultCaptionFade(false);
        initView();
        initListener();
        initData();
    }

    protected void initView() {
        mLiveWindow = findViewById(R.id.live_window);
        mLiveWindow.setFillMode(FILLMODE_PRESERVEASPECTFIT);
        mCompile = findViewById(R.id.compile);
        mPlay = findViewById(R.id.play);
        mPlaySeekBar = findViewById(R.id.progressBar);
        mStartTime = findViewById(R.id.startTime);
        mEndTime = findViewById(R.id.endTime);
        mCompileLayout = findViewById(R.id.compile_layout);
        mCompileValue = findViewById(R.id.compile_value);
        if (!initTimeline()) {
            return;
        }
        mPlay.setEnabled(false);
        mCompile.setEnabled(false);
    }

    /**
     * 初始化时间线
     */
    private boolean initTimeline() {
        if (null == mStreamingContext) {
            return false;
        }
        NvsUtils.setCheckEnable(false);
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        videoEditRes.imageWidth = 720;
        videoEditRes.imageHeight = 1280;

        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeline = mStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes,
                NvsStreamingContext.CREATE_TIMELINE_FLAG_ORIGINAL_SIZE_FOR_RAW_FILTER );
        //将timeline连接到LiveWindow控件
        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
        mStreamingContext.setPlaybackCallback(this);
        mStreamingContext.setPlaybackCallback2(this);
        mStreamingContext.setCompileCallback(this);
        mStreamingContext.setCompileCallback2(this);
        mStreamingContext.setCompileCallback3(this);
        mStreamingContext.setPlaybackExceptionCallback(this);
        mStreamingContext.setStreamingEngineCallback(this);
        mStreamingContext.setHardwareErrorCallback(this);
        mStreamingContext.setSeekingCallback(this);
        //添加视频轨道，如果不做画中画，添加一条视频轨道即可
        mVideoTrack = mTimeline.appendVideoTrack();
        return true;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                获取引擎的状态
                 */
                int state = mStreamingContext.getStreamingEngineState();
                if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_STOPPED || state == NvsStreamingContext.STREAMING_ENGINE_STATE_SEEKING) {
                    NvsRational rational = new NvsRational(1, 3);
                    //mStreamingContext.clearCachedResources(false);
                    mPlay.setImageResource(R.mipmap.icon_media_pause);
                    mStreamingContext.playbackTimeline(mTimeline
                            , mStreamingContext.getTimelineCurrentPosition(mTimeline), -1
                            , NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE
                            // , rational
                            , true
                            , NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);
                } else if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlay.setImageResource(R.mipmap.icon_media_play);
                    mStreamingContext.stop();
                }
            }
        });
        //导出
        mCompile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前的引擎状态
                int state = mStreamingContext.getStreamingEngineState();
                if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_STOPPED || state == NvsStreamingContext.STREAMING_ENGINE_STATE_SEEKING) {
                    compileVideo(false);
                } else if (state == NvsStreamingContext.STREAMING_ENGINE_STATE_COMPILE) {
                    mCompileLayout.setVisibility(View.GONE);
                    mStreamingContext.stop();
                }
            }
        });

        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                //定位预览视频图像
                if (mStreamingContext != null && mTimeline != null) {
                    seek(progress);
                    mStartTime.setText(EditUtil.formatTimeStrWithUs(progress));
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

    private void initData() {
        String videoPath = getIntent().getStringExtra("video_path");
        if (TextUtils.isEmpty(videoPath)) {
            finish();
            return;
        }
        ArrayList<String> data = new ArrayList<>();
        data.add(videoPath);
        showResult(data);
    }

    private void compileVideo(boolean isDisableHardware) {
        createCompilePath();
        int flag = 0;
        if (isDisableHardware) {
            flag = NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_DISABLE_HARDWARE_ENCODER;
        }
        mCompileLayout.setVisibility(View.VISIBLE);
        mStreamingContext.setCustomCompileVideoHeight(mTimeline.getVideoRes().imageHeight);
        mStreamingContext.compileTimeline(mTimeline, 0, mTimeline.getDuration()
                , mCompilePath
                , NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM
                , NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH
                , flag | NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_ORIGIN_VIDEO_FRAME

        );
    }

    private void showResult(ArrayList<String> pathList) {
        mSelectPath.clear();
        mSelectPath.addAll(pathList);
        clearData();
        for (int i = 0; i < pathList.size(); i++) {
            String mediaPath = pathList.get(i);
            NvsVideoClip clip = mVideoTrack.appendClip(mediaPath);
            if (clip == null) {
                Toast.makeText(this, "素材有错误" + mediaPath, Toast.LENGTH_LONG).show();
                return;
            }
            if (i == 0) {
                NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(mediaPath);
                if (null != avFileInfo) {
                    NvsSize size = avFileInfo.getVideoStreamDimension(0);
                    int width = size.width;
                    int height = size.height;
                    if (width > 0 && height > 0) {
                        mTimeline.changeVideoSize(EditUtil.alignedData(width, 4), EditUtil.alignedData(height, 2));
                    }
                }
            }
            EditCustomVideoFx customVideoFx = new EditCustomVideoFx();
            clip.appendRawCustomFx(customVideoFx);
        }
        mPlay.setEnabled(true);
        mCompile.setEnabled(true);
        updatePlaySeekBar();
    }


    /**
     * 创建生成视频的路径
     *
     * @return boolean
     */
    private boolean createCompilePath() {
        File compileDir = new File(Environment.getExternalStorageDirectory(), "NvStreamingSdk" + File.separator + "Compile");
        if (!compileDir.exists() && !compileDir.mkdirs()) {
            Log.d(TAG, "Failed to make Compile directory");
            return false;
        }
        File file = new File(compileDir, "video_" + System.currentTimeMillis() + ".mp4");
        mCompilePath = file.getAbsolutePath();
        return true;
    }

    private void updatePlaySeekBar() {
        //定位预览视频图像
        seek(0);
        mPlaySeekBar.setProgress(0);
        mPlaySeekBar.setMax((int) mTimeline.getDuration());
        mStartTime.setText("00:00");
        mEndTime.setText(EditUtil.formatTimeStrWithUs(mTimeline.getDuration()));
    }

    private void seek(long duration) {
        mStreamingContext.seekTimeline(mTimeline, duration, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE
                , NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME);
    }


    /**
     * <!----------------------------compile callback1------------------------------------->
     */
    @Override
    public void onCompileProgress(NvsTimeline timeline, int progress) {
        mCompileValue.setText(progress + "%");
    }

    @Override
    public void onCompileFinished(NvsTimeline timeline) {
        mCompileLayout.setVisibility(View.GONE);
        Log.e("meicam", "----callback1 onCompileFinished");
        Toast.makeText(this, "生成文件：" + mCompilePath, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCompileFailed(NvsTimeline timeline) {
        mCompileLayout.setVisibility(View.GONE);
        Log.e("meicam", "----callback1 onCompileFailed");
        Toast.makeText(this, "生成文件失败：", Toast.LENGTH_LONG).show();
    }

    /**
     * <!----------------------------compile callback2------------------------------------->
     */
    @Override
    public void onCompileCompleted(NvsTimeline nvsTimeline, boolean b) {
        mCompileLayout.setVisibility(View.GONE);
        Log.e("meicam", "----callback2 onCompileCompleted");
    }

    /**
     * <!----------------------------compile callback3------------------------------------->
     */
    @Override
    public void onCompileCompleted(NvsTimeline nvsTimeline, boolean b, int errorType, String stringInfo, int i1) {
        Log.e("meicam", "----callback3 onCompileCompleted errorType:" + errorType + "   stringInfo:" + stringInfo + "   b:" + b);
        mCompileLayout.setVisibility(View.GONE);
    }

    @Override
    public void onHardwareError(int errorType, String stringInfo) {
        Log.e("meicam", "----onHardwareError errorType:" + errorType + "   stringInfo:" + stringInfo);
    }

    @Override
    public void onPlaybackPreloadingCompletion(NvsTimeline var1) {
    }

    @Override
    public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long time) {
        mStartTime.setText(EditUtil.formatTimeStrWithUs(time));
        mPlaySeekBar.setProgress((int) time);
    }

    @Override
    public void onPlaybackStopped(NvsTimeline var1) {
    }

    @Override
    public void onPlaybackEOF(NvsTimeline var1) {
    }

    @Override
    public void onPlaybackException(NvsTimeline nvsTimeline, int i, String s) {
        final String desc = "异常类型：" + i + "    异常信息：" + s;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, desc, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStreamingEngineStateChanged(int i) {
        if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mPlay.setImageResource(R.mipmap.icon_media_pause);
        } else {
            mPlay.setImageResource(R.mipmap.icon_media_play);
        }
    }

    @Override
    public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

    }

    @Override
    public void onSeekingTimelinePosition(NvsTimeline nvsTimeline, long l) {

    }

    private void clearData() {
        //移除视频片段
        mVideoTrack.removeAllClips();
        mPlay.setEnabled(false);
        mCompile.setEnabled(false);
        //清空视频帧
        mLiveWindow.clearVideoFrame();
    }

    @Override
    protected void onDestroy() {
        mStreamingContext.clearCachedResources(true);
        mStreamingContext = null;
        super.onDestroy();
    }
}


