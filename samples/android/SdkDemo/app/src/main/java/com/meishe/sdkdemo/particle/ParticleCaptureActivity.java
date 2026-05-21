package com.meishe.sdkdemo.particle;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsAssetPackageParticleDescParser;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meishe.base.utils.Utils;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.interfaces.TipsButtonClickListener;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.permission.PermissionsActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_INPROGRESS;
import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_START_TIMER;
import static com.meishe.sdkdemo.utils.Constants.ASSET_DOWNLOAD_SUCCESS;
import static com.meishe.sdkdemo.utils.Constants.ASSET_LIST_REQUEST_FAILED;
import static com.meishe.sdkdemo.utils.Constants.ASSET_LIST_REQUEST_SUCCESS;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author :  yyj
 * @CreateDate : 2019/6/28.
 * @Description :粒子特效拍摄页面。Particle effects shooting activity.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ParticleCaptureActivity extends BasePermissionActivity implements NvsStreamingContext.CaptureDeviceCallback,
        NvsStreamingContext.CaptureRecordingDurationCallback,
        NvsStreamingContext.CaptureRecordingStartedCallback {
    private static final String TAG = "ParticleCaptureActivity";

    private final int MIN_RECORD_DURATION = 1000000;
    /**
     * 滤镜：一般模式
     * Filter: Normal mode
     */
    private static final int TYPE_CAPTURE_FX_CUSTOM = 0;
    private Context mContext;

    private LiveWindow mLiveWindow;
    private NvsStreamingContext mStreamingContext;
    private Button mCloseButton;
    private LinearLayout mFunctionButtonLayout, mSwitchFacingLayout, mFlashLayout, mParticleLayout, mGraffitiLayout;
    private RelativeLayout mStartLayout;
    private ImageView mFlashButton, mStartRecordingImage, mDelete, mNext, mImageAutoFocusRect;
    private TextView mStartText, mRecordTime;
    /**
     * 录制相关
     * Recording related
     */
    private List<Long> mRecordTimeList = new ArrayList<>();
    private List<String> mRecordFileList = new ArrayList<>();
    private long mEachRecodingVideoTime = 0, mAllRecordingTime = 0;
    private String mCurRecordVideoPath;
    private boolean mIsSwitchingCamera = false;
    private int mCurrentDeviceIndex = 1;
    private NvsStreamingContext.CaptureDeviceCapability mCapability = null;
    private AlphaAnimation mFocusAnimation;
    /**
     * 粒子相关
     * Particle correlation
     */
    private static NvAssetManager mAssetManager;
    private List<FilterItem> mCustomFxEmitter = new ArrayList<>();
    private View mParticleFxView;
    private RecyclerView mParticleRv;
    private ParticleCaptureFxAdapter mParticleFxAdapter;
    private int mCurrentFxType = TYPE_CAPTURE_FX_CUSTOM, mCustomSelectPos, mFaceSelectPos, mHandSelectPos;
    private NvsCaptureVideoFx mCurrentCaptureVideoFx;

    private CountDownTimer m_showTipsTimer = new CountDownTimer(5000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
        }
    };

    private ParticleCaptureHandler mParticleCaptureHandler = new ParticleCaptureHandler(this);
    /**
     * 所有粒子特效
     * All particle effects
     */
    private List<NvAsset> mFilterList = new ArrayList<>();
    private Timer mTimer;
    private TimerTask mTimerTask;
    private ParticleAssetManagerListener mParticleAssetManagerListener;

    static class ParticleCaptureHandler extends Handler {
        WeakReference<ParticleCaptureActivity> mWeakReference;

        public ParticleCaptureHandler(ParticleCaptureActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ParticleCaptureActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case ASSET_LIST_REQUEST_SUCCESS:
                        /*
                         * 更新列表成功
                         * Update list successfully
                         * */
                        activity.updateParticleDataList();
                        break;
                    case ASSET_LIST_REQUEST_FAILED:
                        /*
                         * 更新列表失败
                         * Update list failed
                         * */
                        ToastUtil.showToast(activity, activity.getResources().getString(R.string.check_network));
                        break;
                    case ASSET_DOWNLOAD_START_TIMER:
                        /*
                         * 打开下载更新定时器
                         * Open download update timer
                         * */
                        activity.startProgressTimer();
                        break;
                    case ASSET_DOWNLOAD_INPROGRESS:
                        /*
                         * 更新下载进度
                         * Update download progress
                         * */
                        activity.updateFilterDownloadProgress();
                        break;
                    case ASSET_DOWNLOAD_SUCCESS:
                        /*
                         * 更新下载进度
                         * Update download progress
                         * */
                        activity.updateFilterDownloadProgress();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 下载进度更新
     * Download progress update
     */
    private void updateFilterDownloadProgress() {
        ArrayList<NvAsset> remoteAssets = getRemoteAssetsWithPage();
        for (NvAsset filterItemFromDownload : remoteAssets) {
            for (int i = 0; i < mCustomFxEmitter.size(); ++i) {
                FilterItem asset = mCustomFxEmitter.get(i);
                if (asset == null) {
                    continue;
                }
                if (TextUtils.equals(filterItemFromDownload.uuid, mCustomFxEmitter.get(i).getPackageId())) {
                    asset.downloadProgress = filterItemFromDownload.downloadProgress;
                    asset.downloadStatus = filterItemFromDownload.downloadStatus;
                    asset.setAssetDescription(filterItemFromDownload.assetDescription);
                }
            }
        }
        switch (mCurrentFxType) {
            case TYPE_CAPTURE_FX_CUSTOM:
                mParticleFxAdapter.setFilterDataList(mCustomFxEmitter);
                mParticleFxAdapter.setSelectPos(mCustomSelectPos);
                mParticleFxAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    /**
     * 获取远程信息后更新数据列表
     * Update data list after obtaining remote information
     */
    private void updateParticleDataList() {
        /*
         * getRemoteAssetsWithPage 存储了所有资源的ID
         * getRemoteAssetsWithPage stores the IDs of all resources
         * */
        ArrayList<NvAsset> filterDataList = getRemoteAssetsWithPage();
        if (filterDataList == null || filterDataList.size() == 0) {
            return;
        }
        /*
         * 添加新数据
         * Add new data
         * */
        ArrayList<String> tempAssetUuidArray = new ArrayList<>();
        for (NvAsset nvAsset : mFilterList) {
            tempAssetUuidArray.add(nvAsset.uuid);
        }
        for (NvAsset nvAssetFromNet : filterDataList) {
            if (!tempAssetUuidArray.contains(nvAssetFromNet.uuid) && !TextUtils.isEmpty(nvAssetFromNet.uuid)
                    && !nvAssetFromNet.uuid.contains("CDC1BFA5-6922-4B96-A427-F27F071F2EC3")
                    && !nvAssetFromNet.uuid.contains("765D2987-D9D7-42D7-BD63-72B34AA4B944")) {
                mFilterList.add(nvAssetFromNet);
            }
        }
        tempAssetUuidArray.clear();
        initParticleList();
        initParticleRecyclerView();
    }

    private ArrayList<NvAsset> getRemoteAssetsWithPage() {
        return mAssetManager.getRemoteAssets(NvAsset.ASSET_ARSCENE_PARTICLE, NvAsset.AspectRatio_All, 0);
    }

    @Override
    protected int initRootView() {
        mContext = this;
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_particle_capture;
    }

    @Override
    protected void initViews() {
        mLiveWindow = (LiveWindow) findViewById(R.id.liveWindow);
        mRecordTime = (TextView) findViewById(R.id.recordTime);
        mImageAutoFocusRect = (ImageView) findViewById(R.id.imageAutoFocusRect);
        mDelete = (ImageView) findViewById(R.id.delete);
        mNext = (ImageView) findViewById(R.id.next);
        mStartLayout = (RelativeLayout) findViewById(R.id.startLayout);
        mStartRecordingImage = (ImageView) findViewById(R.id.startRecordingImage);
        mStartText = (TextView) findViewById(R.id.startText);
        mCloseButton = (Button) findViewById(R.id.closeButton);
        mFunctionButtonLayout = (LinearLayout) findViewById(R.id.functionButtonLayout);
        mSwitchFacingLayout = (LinearLayout) findViewById(R.id.switchFacingLayout);
        mFlashLayout = (LinearLayout) findViewById(R.id.flashLayout);
        mFlashLayout.setEnabled(false);
        mFlashButton = (ImageView) findViewById(R.id.flashButton);
        mParticleLayout = (LinearLayout) findViewById(R.id.particleLayout);
        mGraffitiLayout = (LinearLayout) findViewById(R.id.graffitiLayout);
        /*
         * 粒子特效
         * Particle effects
         * */
        mParticleFxView = findViewById(R.id.particle_fx_layout);
        mParticleRv = (RecyclerView) mParticleFxView.findViewById(R.id.recyclerViewFx);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        initCapture();
        searchAssetData();
        initParticleList();
        initParticleRecyclerView();
        requestRemoteAssets();
    }

    class ParticleAssetManagerListener implements NvAssetManager.NvAssetManagerListener {

        @Override
        public void onRemoteAssetsChanged(boolean hasNext) {
            Log.e(TAG, "onRemoteAssetsChanged hasNext: " + hasNext);
            /*
             * 粒子列表信息 获取成功
             * Particle list information obtained successfully
             * */
            mParticleCaptureHandler.sendEmptyMessage(ASSET_LIST_REQUEST_SUCCESS);
        }

        @Override
        public void onRemoteAssetsChanged(List<NvAsset> assetDataList, boolean hasNext) {
            Log.e(TAG, "onRemoteAssetsChanged assetDataList: " + assetDataList.size());

        }

        @Override
        public void onGetRemoteAssetsFailed() {
            Log.e(TAG, "onGetRemoteAssetsFailed: ");
            /*
             * 粒子列表信息 获取失败
             * Particle list information acquisition failed
             * */
            mParticleCaptureHandler.sendEmptyMessage(ASSET_LIST_REQUEST_FAILED);
        }

        @Override
        public void onDownloadAssetProgress(String uuid, int progress) {
            Log.e(TAG, "onDownloadAssetProgress: " + uuid + "  progress: " + progress);
            sendHandleMsg(uuid, ASSET_DOWNLOAD_START_TIMER);
        }

        @Override
        public void onDonwloadAssetFailed(String uuid) {
            Log.e(TAG, "onDonwloadAssetFailed: " + uuid);
            sendHandleMsg(uuid, ASSET_DOWNLOAD_INPROGRESS);
        }

        @Override
        public void onDonwloadAssetSuccess(String uuid) {
            Log.e(TAG, "onDonwloadAssetSuccess: " + uuid);
            sendHandleMsg(uuid, ASSET_DOWNLOAD_SUCCESS);
        }

        @Override
        public void onFinishAssetPackageInstallation(String uuid) {
            Log.e(TAG, "onFinishAssetPackageInstallation: " + uuid);
        }

        @Override
        public void onFinishAssetPackageUpgrading(String uuid) {
            Log.e(TAG, "onFinishAssetPackageUpgrading: " + uuid);
        }
    }

    /**
     * 请求远程资源信息
     * Request remote resource information
     */
    private void requestRemoteAssets() {
        if (mParticleAssetManagerListener == null) {
            mParticleAssetManagerListener = new ParticleAssetManagerListener();
        }
        mAssetManager.setManagerlistener(mParticleAssetManagerListener);
        mAssetManager.downloadRemoteAssetsInfo(NvAsset.ASSET_ARSCENE_PARTICLE, -1, NvAsset.AspectRatio_All, 0, 1, 100);
    }

    @Override
    protected void onStop() {
        /*
         * 存储素材数据线程
         * Store material data thread
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvAssetManager.sharedInstance().setAssetInfoToSharedPreferences(NvAsset.ASSET_ARSCENE_PARTICLE);
            }
        }).start();
        super.onStop();
    }

    @Override
    protected void initListener() {

        mLiveWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeCaptureDialogView();

                float rectHalfWidth = mImageAutoFocusRect.getWidth() / 2;
                if (event.getX() - rectHalfWidth >= 0 && event.getX() + rectHalfWidth <= mLiveWindow.getWidth()
                        && event.getY() - rectHalfWidth >= 0 && event.getY() + rectHalfWidth <= mLiveWindow.getHeight()) {
                    mImageAutoFocusRect.setX(event.getX() - rectHalfWidth);
                    mImageAutoFocusRect.setY(event.getY() - rectHalfWidth);
                    RectF rectFrame = new RectF();
                    rectFrame.set(mImageAutoFocusRect.getX(), mImageAutoFocusRect.getY(),
                            mImageAutoFocusRect.getX() + mImageAutoFocusRect.getWidth(),
                            mImageAutoFocusRect.getY() + mImageAutoFocusRect.getHeight());
                    /*
                     * 启动自动聚焦
                     * Start autofocus
                     * */
                    mImageAutoFocusRect.startAnimation(mFocusAnimation);
                    mStreamingContext.startAutoFocus(new RectF(rectFrame));
                }
                return false;
            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwitchFacingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    return;
                }
                if (mIsSwitchingCamera) {
                    return;
                }
                if (mCurrentDeviceIndex == 0) {
                    mCurrentDeviceIndex = 1;
                    mFlashLayout.setEnabled(false);
                    mFlashButton.setImageResource(R.mipmap.icon_flash_off);
                    mFlashButton.setImageAlpha(128);
                } else {
                    mCurrentDeviceIndex = 0;
                    mFlashLayout.setEnabled(true);
                    mFlashButton.setImageResource(R.mipmap.icon_flash_off);
                    mFlashButton.setImageAlpha(255);
                }
                mIsSwitchingCamera = true;
                startCapturePreview(true);
            }
        });

        mFlashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentDeviceIndex != 0) {
                    return;
                }
                if (mStreamingContext.isFlashOn()) {
                    mStreamingContext.toggleFlash(false);
                    mFlashButton.setImageResource(R.mipmap.icon_flash_off);
                    mFlashButton.setImageAlpha(255);
                } else {
                    mStreamingContext.toggleFlash(true);
                    mFlashButton.setImageResource(R.mipmap.icon_flash_on);
                    mFlashButton.setImageAlpha(255);
                }
            }
        });

        mStartRecordingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 当前在录制状态，可停止视频录制
                 * Currently in recording state, you can stop video recording
                 * */
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    stopRecording();
                } else {
                    mCurRecordVideoPath = PathUtils.getParticleRecordPath();
                    if (mCurRecordVideoPath == null) {
                        return;
                    }
                    mStartRecordingImage.setEnabled(false);

                    mStartRecordingImage.setBackgroundResource(R.mipmap.particle_capture_recording);
                    mEachRecodingVideoTime = 0;
                    /*
                     * 当前未在视频录制状态，则启动视频录制。此处使用带特效的录制方式
                     * If video recording is not currently in progress, start video recording. Use the recording method with special effects here
                     * */
                    if (!mStreamingContext.startRecording(mCurRecordVideoPath)) {
                        return;
                    }
                    isInRecording(false);
                    mRecordFileList.add(mCurRecordVideoPath);
                }
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordTimeList.size() != 0 && mRecordFileList.size() != 0) {
                    mAllRecordingTime -= mRecordTimeList.get(mRecordTimeList.size() - 1);
                    mRecordTimeList.remove(mRecordTimeList.size() - 1);
                    PathUtils.deleteFile(mRecordFileList.get(mRecordFileList.size() - 1));
                    mRecordFileList.remove(mRecordFileList.size() - 1);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));

                    if (mRecordTimeList.size() == 0) {
                        mStartRecordingImage.setBackgroundResource(R.mipmap.particle_default);
                        mStartText.setVisibility(View.GONE);
                        mDelete.setVisibility(View.GONE);
                        mNext.setVisibility(View.GONE);
                        mRecordTime.setVisibility(View.INVISIBLE);
                    } else {
                        mStartText.setText(mRecordTimeList.size() + "");
                        mRecordTime.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 将拍摄的视频传到下一个页面
                 * Transfer the captured video to the next page
                 * */
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

                /*
                 * 数据清空
                 * Data clear
                 * */
                TimelineData.instance().clear();
                NvsSize size = avFileInfo.getVideoStreamDimension(0);
                int rotation = avFileInfo.getVideoStreamRotation(0);
                if (rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90
                        || rotation == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
                    int tmp = size.width;
                    size.width = size.height;
                    size.height = tmp;
                }
                int makeRatio = size.width > size.height ? NvAsset.AspectRatio_16v9 : NvAsset.AspectRatio_9v16;
                TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
                TimelineData.instance().setMakeRatio(makeRatio);
                TimelineData.instance().setClipInfoData(pathList);
                mNext.setClickable(false);

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.START_ACTIVITY_FROM_CAPTURE, true);
                AppManager.getInstance().jumpActivity(ParticleCaptureActivity.this, ParticlePreviewActivity.class, bundle);
            }
        });

        mParticleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureDialogView();
            }
        });

        mGraffitiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_PARTICLE);
                AppManager.getInstance().jumpActivity(ParticleCaptureActivity.this, SelectVideoActivity.class, bundle);
            }
        });
    }

    private void stopRecording() {
        mStreamingContext.stopRecording();
        mStartRecordingImage.setBackgroundResource(R.mipmap.particle_capture_stop);

        mAllRecordingTime += mEachRecodingVideoTime;
        mRecordTimeList.add(mEachRecodingVideoTime);
        mStartText.setText(mRecordTimeList.size() + "");
        isInRecording(true);
    }

    private void sendHandleMsg(String uuid, int what) {
        Message sendMsg = mParticleCaptureHandler.obtainMessage();
        if (sendMsg == null) {
            sendMsg = new Message();
        }
        sendMsg.what = what;
        sendMsg.obj = uuid;
        mParticleCaptureHandler.sendMessage(sendMsg);
    }

    private void startProgressTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mParticleCaptureHandler.sendEmptyMessage(ASSET_DOWNLOAD_INPROGRESS);
                }
            };
            mTimer.schedule(mTimerTask, 0, 300);
        }
    }

    private void searchAssetData() {
        mAssetManager = NvAssetManager.sharedInstance();
//        String bundlePath = "particle/fx";
//        mAssetManager.searchReservedAssets(NvAsset.ASSET_ARSCENE_PARTICLE, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ARSCENE_PARTICLE);
        mFilterList = mAssetManager.getUsableAssets(NvAsset.ASSET_ARSCENE_PARTICLE, NvAsset.AspectRatio_All, 0);
        Log.e(TAG, "searchAssetData: " + mFilterList.size());
    }

    private void initCapture() {
        if (null == mStreamingContext) {
            return;
        }
        mStreamingContext.removeAllCaptureVideoFx();
        mFocusAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFocusAnimation.setDuration(1000);
        mFocusAnimation.setFillAfter(true);
        /*
         *  给Streaming Context设置回调接口
         * Set callback interface for Streaming Context
         * */
        mStreamingContext.setCaptureDeviceCallback(this);
        mStreamingContext.setCaptureRecordingDurationCallback(this);
        mStreamingContext.setCaptureRecordingStartedCallback(this);
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

        /*
         * 采集设备数量判定
         * Judging the number of collection equipment
         * */
        if (mStreamingContext.getCaptureDeviceCount() > 1) {
            mSwitchFacingLayout.setEnabled(true);
            mCurrentDeviceIndex = 1;
        } else {
            mSwitchFacingLayout.setEnabled(false);
            mCurrentDeviceIndex = 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            try {
                startCapturePreview(false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "startCapturePreviewException: initCapture failed,below 6.0 device may has no access to camera");
                /*
                 * 拒绝后，所有按钮禁止点击
                 * After rejection, all buttons are not clickable
                 * */
                noPermissionDialog();
            }
        }
    }

    private boolean startCapturePreview(boolean deviceChanged) {
        mFlashLayout.setEnabled(mCurrentDeviceIndex != 0);
        mFlashButton.setImageResource(R.mipmap.icon_flash_off);
        mFlashButton.setImageAlpha(mCurrentDeviceIndex != 0 ? 128 : 255);
        /*
         * 判断当前引擎状态是否为采集预览状态
         * Determine if the current engine status is the collection preview status
         * */
        int captureResolutionGrade = ParameterSettingValues.instance().getCaptureResolutionGrade();
        if (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            int flags = NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER |
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME
                    | NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE;
            flags = flags | NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_GRAB_CAPTURED_VIDEO_FRAME;

            if (!mStreamingContext.startCapturePreview(mCurrentDeviceIndex, captureResolutionGrade, flags, null)) {
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

    private void updateSettingsWithCapability(int deviceIndex) {
        /*
         * 获取采集设备能力描述对象，设置自动聚焦，曝光补偿，缩放
         * Get acquisition device capability description object, set auto focus, exposure compensation, zoom
         * */
        mCapability = mStreamingContext.getCaptureDeviceCapability(deviceIndex);
        if (null == mCapability) {
            return;
        }

        /*
         * 是否支持闪光灯
         * Whether to support flash
         * */
        if (mCapability.supportFlash) {
            mFlashLayout.setEnabled(true);
        }

        mImageAutoFocusRect.setX((mLiveWindow.getWidth() - mImageAutoFocusRect.getWidth()) / 2);
        mImageAutoFocusRect.setY((mLiveWindow.getHeight() - mImageAutoFocusRect.getHeight()) / 2);
        RectF rectFrame = new RectF();
        rectFrame.set(mImageAutoFocusRect.getX(), mImageAutoFocusRect.getY(),
                mImageAutoFocusRect.getX() + mImageAutoFocusRect.getWidth(),
                mImageAutoFocusRect.getY() + mImageAutoFocusRect.getHeight());
    }

    private void isInRecording(boolean isInRecording) {
        int show;
        if (isInRecording) {
            show = View.VISIBLE;
            mRecordTime.setTextColor(0xffffffff);
        } else {
            mRecordTime.setTextColor(0xffD0021B);
            show = View.INVISIBLE;
        }
        mCloseButton.setVisibility(show);
        mFunctionButtonLayout.setVisibility(show);
        mDelete.setVisibility(show);
        mNext.setVisibility(show);
        mStartText.setVisibility(show);
        if (mRecordTimeList.isEmpty()) {
            mRecordTime.setVisibility(View.INVISIBLE);
        } else {
            mRecordTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCaptureDeviceCapsReady(int captureDeviceIndex) {
        if (captureDeviceIndex != mCurrentDeviceIndex) {
            return;
        }
        updateSettingsWithCapability(captureDeviceIndex);
    }

    @Override
    public void onCaptureDevicePreviewResolutionReady(int i) {

    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {
        mIsSwitchingCamera = false;
    }

    @Override
    public void onCaptureDeviceError(int i, int i1) {
        Log.e(TAG, "onCaptureDeviceError: initCapture failed,under 6.0 device may has no access to camera");
        noPermissionDialog();
    }

    @Override
    public void onCaptureDeviceStopped(int i) {

    }

    @Override
    public void onCaptureDeviceAutoFocusComplete(int i, boolean b) {

    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        /*
         * 保存到媒体库
         * Save to media library
         * */
        if (mRecordFileList != null && !mRecordFileList.isEmpty()) {
            for (String path : mRecordFileList) {
                if (path == null) {
                    continue;
                }
                if (path.endsWith(".mp4")) {
                    MediaScannerUtil.scanFile(path, "video/mp4");
                } else if (path.endsWith(".jpg")) {
                    MediaScannerUtil.scanFile(path, "image/jpg");
                }
            }
        }
    }

    @Override
    public void onCaptureRecordingError(int i) {

    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        if (l >= MIN_RECORD_DURATION) {
            mStartRecordingImage.setEnabled(true);
        }
        mEachRecodingVideoTime = l;
        mRecordTime.setVisibility(View.VISIBLE);
        mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime + mEachRecodingVideoTime));
    }

    @Override
    public void onCaptureRecordingStarted(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case PermissionsActivity.PERMISSIONS_DENIED:
                String[] permissionsTips = getResources().getStringArray(R.array.permissions_tips);
                Util.showDialog(ParticleCaptureActivity.this, permissionsTips[0], permissionsTips[1], new TipsButtonClickListener() {
                    @Override
                    public void onTipsButtoClick(View view) {
                        AppManager.getInstance().finishActivity();
                    }
                });

                break;
            case PermissionsActivity.PERMISSIONS_GRANTED:
                break;
            default:
                break;
        }
    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {
        startCapturePreview(false);
    }

    @Override
    protected void nonePermission() {
        Log.d(TAG, "initCapture failed,above 6.0 device may has no access to camera");
        noPermissionDialog();
    }

    @Override
    protected void noPromptPermission() {
        Log.d(TAG, "initCapture failed,above 6.0 device may has no access to camera");
        noPermissionDialog();
    }

    private void noPermissionDialog() {
        String[] permissionsTips = getResources().getStringArray(R.array.permissions_tips);
        Util.showDialog(ParticleCaptureActivity.this, permissionsTips[0], permissionsTips[1], new TipsButtonClickListener() {
            @Override
            public void onTipsButtoClick(View view) {
                AppManager.getInstance().finishActivity();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureVideoFx();
            mStreamingContext.stop();
        }
//        if (m_actionDetected != null) {
//            m_actionDetected.closeDetected();
//            m_actionDetected = null;
//        }
        destoryTimer();
    }

    /**
     * 取消定时器
     * Cancel timer
     */
    private void destoryTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNext.setClickable(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCapturePreview(false);
            }
        }, 100);
        if (mParticleAssetManagerListener == null) {
            mParticleAssetManagerListener = new ParticleAssetManagerListener();
        }
        mAssetManager.setManagerlistener(mParticleAssetManagerListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
            stopRecording();
        }
    }

    /**
     * 显示窗口
     * Show window
     */
    private void showCaptureDialogView() {
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        translate.setDuration(200);
        translate.setFillAfter(false);
        mStartLayout.startAnimation(translate);

        TranslateAnimation translate2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translate2.setDuration(300);
        translate2.setFillAfter(false);
        mParticleFxView.setAnimation(translate2);

        mStartLayout.setVisibility(View.GONE);
        mParticleFxView.setVisibility(View.VISIBLE);

        isShowCaptureButton(false);
    }

    /**
     * 关闭窗口
     * close the window
     */
    private void closeCaptureDialogView() {
        if (mParticleFxView.getVisibility() != View.VISIBLE) {
            return;
        }
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translate.setDuration(300);
        translate.setFillAfter(false);
        mStartLayout.setAnimation(translate);

        TranslateAnimation translate2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        translate2.setDuration(200);
        translate2.setFillAfter(false);
        mParticleFxView.startAnimation(translate2);

        mStartLayout.setVisibility(View.VISIBLE);
        mParticleFxView.setVisibility(View.GONE);

        isShowCaptureButton(true);
    }

    private void isShowCaptureButton(boolean isShow) {
        int show;
        if (isShow) {
            show = View.VISIBLE;
        } else {
            show = View.INVISIBLE;
        }
        if (show == View.VISIBLE) {
            mCloseButton.requestLayout();
            mFunctionButtonLayout.requestLayout();
            mStartLayout.requestLayout();
            mRecordTime.requestLayout();
        }
        mCloseButton.setVisibility(show);
        mFunctionButtonLayout.setVisibility(show);
        mStartLayout.setVisibility(show);
    }

    private void initParticleList() {
        if (mFilterList == null) {
            return;
        }
        if (!mCustomFxEmitter.isEmpty()) {
            mCustomFxEmitter.clear();
        }
        for (int i = 0; i < mFilterList.size(); ++i) {
            NvAsset oneAsset = mFilterList.get(i);
            if (oneAsset == null || oneAsset.uuid == null) {
                continue;
            }
            FilterItem filterItem = new FilterItem();
            filterItem.setFilterMode(FilterItem.FILTERMODE_PACKAGE);
            filterItem.setFilterName(oneAsset.name);
            // PackageId
            filterItem.setPackageId(oneAsset.uuid);
            filterItem.setAssetDescription(oneAsset.assetDescription);
            filterItem.setImageUrl(oneAsset.coverUrl);
            filterItem.downloadStatus = oneAsset.downloadStatus;
            filterItem.setParticleType(NvsAssetPackageParticleDescParser.PARTICLE_TYPE_NORMAL);
            mCustomFxEmitter.add(filterItem);
        }
    }

    private void initParticleRecyclerView() {
        if (mParticleFxAdapter == null) {
            mParticleFxAdapter = new ParticleCaptureFxAdapter(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mParticleRv.setLayoutManager(linearLayoutManager);
            mParticleRv.setAdapter(mParticleFxAdapter);
            FilterItem filterItem = new FilterItem();
            filterItem.setFilterName(getResources().getString(R.string.NO_FX));
            filterItem.setImageId(R.mipmap.no);
            switch (mCurrentFxType) {
                case TYPE_CAPTURE_FX_CUSTOM:
                    mCustomFxEmitter.add(0, filterItem);
                    mParticleFxAdapter.setFilterDataList(mCustomFxEmitter);
                    mParticleFxAdapter.setSelectPos(mCustomSelectPos);
                    mParticleFxAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            /*
             * 选择特效
             * Select special effects
             * */
            mParticleFxAdapter.setOnItemClickListener(new ParticleCaptureFxAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.e(TAG, "onItemClick: mCustomSelectPos " + mCustomSelectPos
                            + "  position: " + position);
                    if (mCustomSelectPos == position) {
                        return;
                    }
                    mCustomSelectPos = position;
                    if (position == 0) {
                        clearEffect();
                        return;
                    }
                    changeCurrentFxSelected();
                }

                @Override
                public void onSameItemClick() {

                }
            });
            /*
             * 资源下载
             * Download
             * */
            mParticleFxAdapter.setParticleItemDownloadListener(new ParticleCaptureFxAdapter.ParticleItemDownloadListener() {
                @Override
                public void onItemDownload(View view, int position) {
                    int count = 0;
                    /*
                     * 分类下载
                     * Category Download
                     * */
                    switch (mCurrentFxType) {
                        case TYPE_CAPTURE_FX_CUSTOM:
                            /*
                             * 滤镜
                             * Filter
                             * */
                            count = mCustomFxEmitter.size();
                            if (position <= 0 || position >= count) {
                                return;
                            }
                            mAssetManager.downloadAsset(NvAsset.ASSET_ARSCENE_PARTICLE, mCustomFxEmitter.get(position).getPackageId());
                            break;
                        default:
                            break;
                    }
                }
            });
        } else {
            FilterItem filterItem = new FilterItem();
            filterItem.setFilterName(getResources().getString(R.string.NO_FX));
            filterItem.setImageId(R.mipmap.no);
            switch (mCurrentFxType) {
                case TYPE_CAPTURE_FX_CUSTOM:
                    mCustomFxEmitter.add(0, filterItem);
                    mParticleFxAdapter.setFilterDataList(mCustomFxEmitter);
                    mParticleFxAdapter.setSelectPos(mCustomSelectPos);
                    mParticleFxAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 移除所有特效
     * Remove all effects
     */
    private void clearEffect() {
        mStreamingContext.removeAllCaptureVideoFx();
    }

    /**
     * 更新当前选中特效
     * Updates the currently selected videoFx effect
     */
    private void changeCurrentFxSelected() {
        FilterItem cur_fx_item = null;
        if (mCustomSelectPos == 0) {
            clearEffect();
            return;
        }
        if (mCustomSelectPos < mCustomFxEmitter.size()) {
            cur_fx_item = mCustomFxEmitter.get(mCustomSelectPos);
        }
        if (cur_fx_item == null || cur_fx_item.getPackageId() == null || cur_fx_item.getPackageId().isEmpty()) {
            clearEffect();
            return;
        }
        m_showTipsTimer.cancel();
        m_showTipsTimer.start();

        /*
         * 移除所有采集视频特效
         * Remove all captured video effects
         * */
        mStreamingContext.removeAllCaptureVideoFx();

        /*
         * 添加采集粒子特效
         * Add collect particle effects
         * */

        mCurrentCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx("AR Scene");
        if (BuildConfig.FACE_MODEL == 240) {
            mCurrentCaptureVideoFx.setBooleanVal("Use Face Extra Info", true);
        }
        mCurrentCaptureVideoFx.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true);
        Utils.setAIFaceOcclusion(mCurrentCaptureVideoFx);

        Log.e(TAG, "包ID: " + cur_fx_item.getPackageId());
        mCurrentCaptureVideoFx.setStringVal("Scene Id", cur_fx_item.getPackageId());
        showPropsToast(getApplicationContext(), cur_fx_item.getPackageId());
    }

    public static void showPropsToast(Context context, String sceneId) {
        NvsAssetPackageManager manager = NvsStreamingContext.getInstance().getAssetPackageManager();
        if (manager == null) {
            return;
        }
        String packagePrompt = manager.getARSceneAssetPackagePrompt(sceneId);
        if (!TextUtils.isEmpty(packagePrompt)) {
            ToastUtil.showToastCenter(context, packagePrompt);
        }
    }
}
