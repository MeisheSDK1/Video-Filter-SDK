package com.meishe.sdkdemo.themeshoot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureCompoundCaption;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meishe.base.utils.FileUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.edit.view.dialog.ThemeDialog;
import com.meishe.sdkdemo.mimodemo.common.template.utils.MiMoFileUtils;
import com.meishe.sdkdemo.themeshoot.model.ThemeModel;
import com.meishe.sdkdemo.themeshoot.utlils.ThemeShootUtil;
import com.meishe.sdkdemo.themeshoot.view.CaptureProgressView;
import com.meishe.sdkdemo.themeshoot.view.ClipLineView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.utils.PackageManagerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/7/31.
 * @Description :主题拍摄页。Theme theme shooting page
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ThemeCaptureActivity extends BasePermissionActivity implements NvsStreamingContext.CaptureRecordingDurationCallback {
    private static final String TAG = ThemeCaptureActivity.class.getSimpleName();

    private static final int STOP_RECORDING = 101;
    private static final int STOP_RECORDING_FAILED = 104;
    private static final int STOP_MUSIC_PLAYER = 103;
    private static final int STOP_MUSIC_PLAYER_FAILED = 105;
    private static final int UPDATE_RECORDING_TIME = 102;

    private ClipLineView mClipLinesView;
    private CaptureProgressView mCaptureProgressView;
    private NvsLiveWindow mLiveWindow;
    private ImageView mBackView;
    private View mDeleteView, mPreviewView;

    private ThemeModel mThemeModel;
    private List<ThemeModel.ShotInfo> mShotInfos;
    private int mCurrentShotIndex = 0;
    private ArrayList<String> mRecordFileList = new ArrayList<>();
    private String mCurRecordVideoPath;
    private boolean isInRecording = false;
    //单位us Unit us
    private long mCurrentClipDuration;
    private long delayCaptureTime = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_RECORDING:
                    mHandler.removeMessages(STOP_RECORDING);
                    mHandler.removeMessages(STOP_RECORDING_FAILED);
                    stopRecording(true);
                    break;
                case STOP_RECORDING_FAILED:
                    mHandler.removeMessages(STOP_RECORDING);
                    mHandler.removeMessages(STOP_RECORDING_FAILED);
                    stopRecording(false);
                    break;
                case STOP_MUSIC_PLAYER:
                    mHandler.removeMessages(STOP_MUSIC_PLAYER_FAILED);
                    mHandler.removeMessages(STOP_MUSIC_PLAYER);
                    AudioPlayer.getInstance(ThemeCaptureActivity.this).pause();
                    playPosition = AudioPlayer.getInstance(ThemeCaptureActivity.this).getNowPlayPosition();
                    break;
                case STOP_MUSIC_PLAYER_FAILED:
                    mHandler.removeMessages(STOP_MUSIC_PLAYER_FAILED);
                    mHandler.removeMessages(STOP_MUSIC_PLAYER);
                    AudioPlayer.getInstance(ThemeCaptureActivity.this).stopPlay();
                    break;
                case UPDATE_RECORDING_TIME:
                    long time = msg.getData().getLong("time");
                    int progress = 100 - (int) (time * 100 / mCurrentClipDuration);
                    String timeTip = ThemeShootUtil.formatUsToStr(time);
                    if (isInRecording) {
                        mCaptureProgressView.setTextAndProgress(timeTip, progress);
                    }
                    Log.d(TAG, "UPDATE_RECORDING_TIME|isInRecording：" + isInRecording + " |time:" + time + "|timeTip:" + timeTip);
                    break;
                default:
                    break;
            }
        }
    };
    private int ratioType;
    //闪光灯 Flash lamp
    private ImageView ivFlash;
    private NvsStreamingContext.CaptureDeviceCapability mCapability;
    private TextView tvInfo;
    private ImageView ivInfo;
    private long sumDuration;
    private ThemeDialog mConfirmDialog;
    private View iVSwitch;
    private int playPosition = 0;

    @Override
    protected int initRootView() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ratioType = bundle.getInt("ratioType");
                if (ratioType == NvAsset.AspectRatio_9v16 || ratioType == NvAsset.AspectRatio_9v21) {
                    // 设置为竖屏模式
                    //Set to portrait mode
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return R.layout.activity_theme_capture;
                } else {
                    // 设置为横屏模式
                    // Set to landscape mode
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    /**
                     * OPPO reno 4相机横屏拍摄时需首次做延时采集处理
                     * For the first time, the Oppo Reno 4 camera needs to do time-lapse acquisition processing when shooting in landscape
                     */
                    if (isDelayStartCapture()) {
                        delayCaptureTime = 500;
                    }
                    return R.layout.activity_theme_capture_landscape;
                }
            }
        }
        return R.layout.activity_theme_capture;
    }

    /**
     * 部分手机在横屏拍摄时需首次做延时采集处理。比如OPPO reno 4，OPPO Find X3等。
     * SDK的setColorGainForSDRToHDR接口有影响
     * Some mobile phones need to do delay acquisition processing for the first time when shooting in the horizontal screen
     *
     * @return boolean
     */
    private boolean isDelayStartCapture() {
        if (Build.MODEL.equals("PDPM00")
                || Build.MODEL.equals("PEDM00")
                || Build.MODEL.equals("ABR-AL00")
                || Build.MODEL.equals("V2230A")
                || Build.MODEL.equals("PGBM10")
                || Build.MODEL.equals("SM-A5460")) {
            return true;
        }
        return false;
    }

    @Override
    protected void initViews() {

        iVSwitch = findViewById(R.id.iv_switch);
        ivFlash = findViewById(R.id.iv_theme_flash);
        mLiveWindow = (NvsLiveWindow) findViewById(R.id.liveWindow);
        mBackView = findViewById(R.id.iv_back);
        mDeleteView = findViewById(R.id.delete_layout);
        mPreviewView = findViewById(R.id.preview_layout);
        mPreviewView.setEnabled(false);
        mClipLinesView = findViewById(R.id.clip_lines);
        mCaptureProgressView = findViewById(R.id.startRecordingImage);
        mCaptureProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInRecording || getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    return;
                }
                if (mRecordFileList.size() >= mClipLinesView.getmRatiosSize()) {
                    //已经录制满了
                    //Already recorded full
                    return;
                }
                mCurRecordVideoPath = ThemeShootUtil.getVlogCacheFilePath(ThemeCaptureActivity.this, mRecordFileList.size());
                Log.d(TAG, "onClick: path ===============" + mCurRecordVideoPath);
                if (mCurRecordVideoPath == null || mShotInfos == null || mCurrentShotIndex >= mShotInfos.size()) {
                    return;
                }

                mCaptureProgressView.setEnabled(false);
                if (!mStreamingContext.startRecording(mCurRecordVideoPath, NvsStreamingContext.STREAMING_ENGINE_RECORDING_FLAG_IGNORE_VIDEO_ROTATION)) {
                    return;
                }
                mCurrentClipDuration = mShotInfos.get(mCurrentShotIndex).getNeedDuration() * Constants.US_TIME_BASE;
                Log.d(TAG, "mCurrentClipDuration=" + mCurrentClipDuration);
                tvInfo.setVisibility(View.GONE);
                ivInfo.setVisibility(View.GONE);
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
                if (shotInfo.canPlaced()) {
                    updateFilterVideoFx(shotInfo.getFilter());
                }
                //加字幕 add caption
                if (!TextUtils.isEmpty(shotInfo.getCompoundCaption())) {
                    updateCompoundCaption(shotInfo.getCompoundCaption(), 10 * 60 * 60 * Constants.US_TIME_BASE * Constants.US_TIME_BASE);
                }
                AudioPlayer.getInstance(ThemeCaptureActivity.this).startPlay(playPosition);
                isInRecording = true;
                updateRecordingViewState(true);
            }
        });
        tvInfo = findViewById(R.id.theme_tv_info);
        ivInfo = findViewById(R.id.theme_iv_info);
    }

    /**
     * 如果是当前片段需要的前后置信息和当前的不匹配
     * 1.如果正在预览，切换摄像头
     * 2。第一次启动 直接修改Id
     * <p>
     * If the current fragment requires a mismatch between the pre and post information and the current one
     * 1. If you are previewing, switch cameras
     * 2. Change the Id at the first startup
     */
    private void setCameraId(boolean isPreview) {
        //判断用前置还是后置 Decide whether to use front or back
        /*ThemeModel.ShotInfo nextShotInfo = mShotInfos.get(mCurrentShotIndex);
        boolean useFrontCamera = TextUtils.equals(nextShotInfo.getFrontCamera(),"true");
        if((useFrontCamera && cameraIndex != Camera.CameraInfo.CAMERA_FACING_FRONT) ||
                (!useFrontCamera && cameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)){
            if(isPreview){

                switchCamera();
            }else{
                cameraIndex = 1- cameraIndex;
            }
        }*/
    }

    /**
     * 下一个视频片段信息
     * Next video clip information
     *
     * @return
     */
    private void getNextCurrentShotIndex() {
        if (mShotInfos != null) {
            while (mCurrentShotIndex < mShotInfos.size() - 1) {
                mCurrentShotIndex++;
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
                //如果不是空镜头返回
                //If it is not an empty shot, return
                if (shotInfo.canPlaced()) {
                    sumDuration += mShotInfos.get(mCurrentShotIndex).getDuration();
                    return;
                }

            }
        }
    }

    /**
     * 上一个视频片段信息
     * pre video clip information
     *
     * @return
     */
    private void getPreCurrentShotIndex() {
        if (mShotInfos != null) {
            while (mCurrentShotIndex > 0) {
                mCurrentShotIndex--;
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
                //如果不是空镜头返回 If it is not an empty shot, return
                if (shotInfo.canPlaced()) {
                    sumDuration -= mShotInfos.get(mCurrentShotIndex).getDuration();
                    setInfoView(shotInfo);
                    return;
                }
            }
        }
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mThemeModel = (ThemeModel) bundle.get("ThemeModel");
                initThemeModelDada();
            }
        }
        initCaptureEnv();
    }

    private void showConfirmDialog(boolean exit) {
        if (mConfirmDialog == null) {
            mConfirmDialog = new ThemeDialog(this);
        }
        if (mConfirmDialog.isShowing()) {
            return;
        }
        if (exit) {
            mConfirmDialog.setOnBtnClickListener(new ThemeDialog.OnBtnClickListener() {
                @Override
                public void OnConfirmClick(View view) {
                    finish();
                }
            });
        } else {
            mConfirmDialog.setOnBtnClickListener(new ThemeDialog.OnBtnClickListener() {
                @Override
                public void OnConfirmClick(View view) {
                    deleteClip();
                }
            });
        }
        mConfirmDialog.show();
        if (exit) {
            mConfirmDialog.setTittleText(getResources().getText(R.string.exit_capture_confirm) + "");
        } else {
            mConfirmDialog.setTittleText(getResources().getText(R.string.delete_this_video_confirm) + "");
        }
    }

    @Override
    protected void initListener() {
        mBackView.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);
        mPreviewView.setOnClickListener(this);
        ivFlash.setOnClickListener(this);
        iVSwitch.setOnClickListener(this);
    }

    private int flasyType = 0;
    private static final int FLASH_TYPE_ON = 100;
    private static final int FLASH_TYPE_OFF = 101;
    private int cameraIndex = 0;

    @Override
    public void onBackPressed() {
        showConfirmDialog(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            showConfirmDialog(true);
        } else if (id == R.id.delete_layout) {
            if (mRecordFileList != null && mRecordFileList.size() > 0) {
                showConfirmDialog(false);
            }
        } else if (id == R.id.preview_layout) {
            jumpToPreview();
        } else if (id == R.id.iv_theme_flash) {
            changeFlash();
        } else if (id == R.id.iv_switch) {
            switchCamera();
        }
    }

    /**
     * 切换摄像头
     * Switch camera
     */
    private void switchCamera() {
        if (isInRecording) {
            return;
        }
        if (cameraIndex == 0) {
            cameraIndex = 1;
        } else {
            cameraIndex = 0;
        }
        startCapturePreview(true);
        mCapability = mStreamingContext.getCaptureDeviceCapability(cameraIndex);
    }

    /**
     * 删除一段视频
     * delete one video
     */
    private void deleteClip() {
        if (mRecordFileList != null && mRecordFileList.size() > 0) {
            mRecordFileList.remove(mRecordFileList.size() - 1);
            getPreCurrentShotIndex();
        }
        updateRecordingViewState(false);

    }

    private void changeFlash() {
        if (cameraIndex == 1) {
            return;
        }
        if (flasyType == FLASH_TYPE_ON) {
            flasyType = FLASH_TYPE_OFF;
            ivFlash.setImageResource(R.mipmap.theme_flash_close);
            if (mStreamingContext != null) {
                mStreamingContext.toggleFlash(false);
            }
        } else {
            flasyType = FLASH_TYPE_ON;
            ivFlash.setImageResource(R.mipmap.theme_flash_open);
            if (mStreamingContext != null) {
                mStreamingContext.toggleFlash(true);
            }
        }
//        } else {
//            ToastUtil.showToast(getApplicationContext(), "不支持闪光灯切换");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasAllPermission()) {
            startCapturePrevDelay();
        } else {
            checkPermissions();
        }
    }

    private void initThemeModelDada() {
        if (mThemeModel == null || TextUtils.isEmpty(mThemeModel.getFolderPath())) {
            return;
        }
        List<String> packagePaths = mThemeModel.getPackagePaths();
        if (packagePaths != null) {
            for (int i = 0; i < packagePaths.size(); i++) {
                String packagePath = packagePaths.get(i);
                if (!TextUtils.isEmpty(packagePath)) {
                    String b = installAssetPackage(packagePath);
                    Log.d(TAG, "installAssetPackage result:" + b);
                }
            }
        }
        String assetFilePath = mThemeModel.getFolderPath();
        NvAssetManager nvAssetManager = getNvAssetManager();
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_FILTER, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_COMPOUND_CAPTION, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_VIDEO_TRANSITION, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_FONT, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_ANIMATED_STICKER, assetFilePath);
        addMusicInfoData();
        mShotInfos = mThemeModel.getShotInfos();
        List<Double> ratios = new ArrayList<>();
        for (int i = 0; i < mShotInfos.size(); i++) {
            ThemeModel.ShotInfo shotInfo = mShotInfos.get(i);
            Log.d(TAG, "initData -> shotInfo getSource= " + shotInfo.getSource());
            Log.d(TAG, "initData -> shotInfo canPlaced= " + shotInfo.canPlaced());
            if (!shotInfo.canPlaced()) {
                if (ratioType == NvAsset.AspectRatio_9v16) {
                    mShotInfos.get(i).setSource(ThemeShootUtil.get9V16PathByPath(mShotInfos.get(i).getSource()));
                }
                continue;
            }
            double ratio = ((double) mShotInfos.get(i).getNeedDuration());
            ratios.add(ratio);
        }
        if (ratios.size() <= 0) {
            return;
        }
        mClipLinesView.setRatios(ratios);

        if (mShotInfos.size() > 0) {
            for (int i = 0; i < mShotInfos.size(); i++) {
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(i);
                if (shotInfo != null && shotInfo.canPlaced()) {
                    setInfoView(shotInfo);
                    break;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (isInRecording) {
            mHandler.sendEmptyMessage(STOP_MUSIC_PLAYER_FAILED);
            mHandler.sendEmptyMessage(STOP_RECORDING_FAILED);
        }
        super.onPause();
    }


    /**
     * 设置提示信息及图片
     * Set reminder information and pictures
     *
     * @param shotInfo
     */
    private void setInfoView(ThemeModel.ShotInfo shotInfo) {
        if (shotInfo != null) {
            //提示信息
            //info
            List<ThemeModel.ShotInfo.AlertInfo> alertInfos = shotInfo.getAlertInfo();
            tvInfo.setVisibility(View.GONE);
            if (alertInfos != null && alertInfos.size() > 0) {
                ThemeModel.ShotInfo.AlertInfo alertInfo = alertInfos.get(0);
                if (alertInfo != null) {
                    String targetLanguage = alertInfo.getTargetLanguage();
                    String info = alertInfo.getOriginalText();
                    if (Util.isZh(mContext)) {
                        info = alertInfo.getTargetText();
                    } else {
                        info = alertInfo.getOriginalText();
                    }
                    if (!TextUtils.isEmpty(info)) {
                        tvInfo.setText(info + "");
                        tvInfo.setVisibility(View.VISIBLE);
                    }
                }
            }
            //提示图片
            //img info
            String infoImgUrl = mThemeModel.getFolderPath() + File.separator + shotInfo.getAlertImage();
            if (TextUtils.isEmpty(infoImgUrl)) {
                ivInfo.setVisibility(View.GONE);
            } else {
                Glide.with(this).asBitmap().load(infoImgUrl).into(ivInfo);
                ivInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initCaptureEnv() {
        if (null == mStreamingContext) {
            return;
        }
        /*
         *给Streaming Context设置回调接口
         *Set callback interface for Streaming Context
         * */
        mStreamingContext.setCaptureRecordingDurationCallback(this);
        if (mStreamingContext.getCaptureDeviceCount() == 0) {
            return;
        }

        /*
         * 将采集预览输出连接到LiveWindow控件
         * Connect the capture preview output to the LiveWindow control
         * */
        if (!mStreamingContext.connectCapturePreviewWithLiveWindow(mLiveWindow)) {
            Log.e(TAG, "Failed to connect capture preview with livewindow!");
            return;
        }

        try {
            startCapturePrevDelay();
            mCapability = mStreamingContext.getCaptureDeviceCapability(cameraIndex);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startCapturePreviewException: initCapture failed,under 6.0 device may has no access to camera");
        }
    }

    private void startCapturePrevDelay() {
        if (delayCaptureTime == 0) {
            startCapturePreview(false);
        } else {
            mLiveWindow.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startCapturePreview(false);
                    delayCaptureTime = 0;
                }
            }, delayCaptureTime);
        }
    }

    private boolean startCapturePreview(boolean deviceChanged) {
        setCameraId(false);
        NvsRational nvsRational = null;
        if (ratioType == NvAsset.AspectRatio_9v16) {
            nvsRational = new NvsRational(9, 16);
        } else if (ratioType == NvAsset.AspectRatio_9v21) {
            nvsRational = new NvsRational(9, 21);
        } else if (ratioType == NvAsset.AspectRatio_16v9) {
            nvsRational = new NvsRational(16, 9);
        } else if (ratioType == NvAsset.AspectRatio_21v9) {
            nvsRational = new NvsRational(21, 9);
        }
        flasyType = FLASH_TYPE_OFF;
        ivFlash.setImageResource(R.mipmap.theme_flash_close);
        /*
         * 判断当前引擎状态是否为采集预览状态
         * Determine if the current engine status is the collection preview status
         */
        int captureResolutionGrade = NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_SUPER_HIGH;
        if (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            if (!mStreamingContext.startCapturePreview(cameraIndex, captureResolutionGrade,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE, nvsRational)) {
                Log.e(TAG, "Failed to start capture preview!");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前引擎状态
     * Get the current engine status
     */
    private int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }


    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        Log.d(TAG, "onCaptureRecordingDuration l:" + l + "||mCurrentClipDuration:" + mCurrentClipDuration);
        Message msg = Message.obtain();
        msg.what = UPDATE_RECORDING_TIME;
        Bundle bundle = new Bundle();
        long time = mCurrentClipDuration - l;
        if (time < 0) {
            time = 0;
        }
        bundle.putLong("time", time);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        if (l >= mCurrentClipDuration) {
            mHandler.sendEmptyMessage(STOP_MUSIC_PLAYER);
            mHandler.sendEmptyMessage(STOP_RECORDING);
            return;
        }
    }

    /**
     * 停止录制
     * stop record
     *
     * @param addRecord
     */
    private void stopRecording(boolean addRecord) {
        isInRecording = false;
        Log.d(TAG, "0=================onStop stopRecording=" + isInRecording + "|addRecord=" + addRecord);
        AudioPlayer.getInstance(ThemeCaptureActivity.this).pause();
        mStreamingContext.stopRecording();
        mStreamingContext.removeAllCaptureVideoFx();
        if (addRecord) {
            mRecordFileList.add(mCurRecordVideoPath);
            ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
            if (shotInfo != null) {
                shotInfo.setSource(mCurRecordVideoPath);
                shotInfo.setFileDuration(shotInfo.getNeedDuration());
            }
            getNextCurrentShotIndex();
            if (mCurrentShotIndex < mShotInfos.size() && mCurrentShotIndex >= 0 && mRecordFileList.size() < mClipLinesView.getmRatiosSize()) {
                setInfoView(mShotInfos.get(mCurrentShotIndex));
                setCameraId(true);
            }
        }
        updateRecordingViewState(false);

    }

    /**
     * 更新录制状态 根据录制状态
     * Update recording status by isRecording
     *
     * @param isRecording
     */
    private void updateRecordingViewState(boolean isRecording) {
        int show = View.VISIBLE;
        mPreviewView.setVisibility(View.INVISIBLE);
        if (isRecording) {
            mCaptureProgressView.setBackgroundResource(R.drawable.theme_white_ball_bg);
            show = View.INVISIBLE;
            mClipLinesView.setVisibility(View.GONE);
        } else {
            mCaptureProgressView.setTextAndProgress("", 0);
            mCaptureProgressView.setBackgroundResource(R.drawable.theme_capture_button);
            mCaptureProgressView.setEnabled(true);
            int recordFileListSize = mRecordFileList.size();
            if (recordFileListSize >= mClipLinesView.getmRatiosSize()) {
                mPreviewView.setEnabled(true);
                mPreviewView.setVisibility(View.VISIBLE);
            }
//            else {
//                mCurrentClipDuration = mShotInfos.get(mCurrentShotIndex).getNeedDuration() * Constants.US_TIME_BASE;
//            }
            mClipLinesView.setClipIndex(recordFileListSize);
            mClipLinesView.setVisibility(View.VISIBLE);
        }
//        mClipLinesView.setVisibility(show);
        mDeleteView.setVisibility(show);
//        mPreviewView.setVisibility(show);
        iVSwitch.setVisibility(show);
        mBackView.setVisibility(show);
        ivFlash.setVisibility(show);
        Log.d(TAG, "updateRecordingViewState VISIBLE=0 isRecording:" + isRecording + " show:" + show);
    }

    /**
     * 更新滤镜by 滤镜ID
     * update filter videoFx by packageId
     *
     * @param filterPackageId
     */
    private void updateFilterVideoFx(String filterPackageId) {
        if (!TextUtils.isEmpty(filterPackageId)) {
            NvsCaptureVideoFx curCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(filterPackageId);
            if (curCaptureVideoFx != null) {
                curCaptureVideoFx.setFilterIntensity(1.0f);
            }
        }
    }

    private void updateCompoundCaption(String captionId, long captionDuration) {
        if (!TextUtils.isEmpty(captionId)) {
            NvsCaptureCompoundCaption nvsCaptureCompoundCaption = mStreamingContext.appendCaptureCompoundCaption(0, captionDuration, captionId);
            Log.e("meicam", "  === " + nvsCaptureCompoundCaption.getDuration());
        }
    }

    /**
     * 跳转预览
     * jump To Preview
     */
    private void jumpToPreview() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (int i = 0; i < mRecordFileList.size(); i++) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(mRecordFileList.get(i));
            pathList.add(clipInfo);
        }
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(pathList.get(0).getFilePath());
        if (avFileInfo == null) {
            return;
        }
        TimelineData.instance().clear();
        NvsSize size = avFileInfo.getVideoStreamDimension(0);
        int rotation = avFileInfo.getVideoStreamRotation(0);
        if (rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90
                || rotation == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;
        }
        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(ratioType));
        TimelineData.instance().setMakeRatio(ratioType);
        TimelineData.instance().setClipInfoData(pathList);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ThemeModel", mThemeModel);
        AppManager.getInstance().jumpActivity(ThemeCaptureActivity.this, ThemePreviewActivity.class, bundle);
    }

    /**
     * 添加音乐信息
     * add music data
     */
    private void addMusicInfoData() {
        ArrayList<MusicInfo> musicInfos = new ArrayList<>();
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setFilePath(
                MiMoFileUtils.getTemplateAssetsFilePath(mThemeModel.getFolderPath(), mThemeModel.getMusic(), mThemeModel.isBuildInTemp()));
        musicInfo.setIsAsset(mThemeModel.isBuildInTemp());
        musicInfo.setAssetPath(MiMoFileUtils.getTemplateAssetsFilePath(mThemeModel.getFolderPath(), mThemeModel.getMusic(), !mThemeModel.isBuildInTemp()));
        musicInfo.setTrimIn(0);
        musicInfo.setInPoint(0);
        if (mThemeModel.getNeedControlMusicFading() == 1) {
            musicInfo.setFadeDuration(mThemeModel.getMusicFadingTime() * Constants.US_TIME_BASE);
        }
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(
                MiMoFileUtils.getTemplateAssetsFilePath(mThemeModel.getFolderPath(),
                        mThemeModel.getMusic(),
                        mThemeModel.isBuildInTemp()));
        if (avFileInfo != null) {
            musicInfo.setTrimOut(mThemeModel.getMusicDuration() * Constants.US_TIME_BASE);
        }
        AudioPlayer.getInstance(ThemeCaptureActivity.this).setCurrentMusic(musicInfo, false);
        musicInfos.add(musicInfo);
        TimelineData.instance().setMusicList(musicInfos);
    }

    /**
     * 安装滤镜
     * install filter by filterPackageFilePath
     *
     * @param filterPackageFilePath
     * @return
     */
    private String installAssetPackage(String filterPackageFilePath) {
        int type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        if (filterPackageFilePath.endsWith(".videofx")) {
            type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        }
        if (filterPackageFilePath.endsWith(".compoundcaption")) {
            type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION;
        }
        if (filterPackageFilePath.endsWith(".videotransition")) {
            type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION;
        }
        return PackageManagerUtil.installAssetPackage(filterPackageFilePath, null, type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File folder = new File(mContext.getCacheDir(), "mimo");
        if (folder.exists()) {
            FileUtils.deleteFilesInDir(folder);
        }
        if (mRecordFileList != null) {
            mRecordFileList.clear();
        }
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureCaption();
            mStreamingContext.removeAllCaptureCompoundCaption();
            mStreamingContext.removeAllCaptureAnimatedSticker();
            mStreamingContext.removeAllCaptureVideoFx();
        }
    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }
}
