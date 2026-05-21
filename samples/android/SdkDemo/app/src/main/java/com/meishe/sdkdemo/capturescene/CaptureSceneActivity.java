package com.meishe.sdkdemo.capturescene;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureSceneInfo;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.capturescene.view.CaptureSceneBottomView;
import com.meishe.sdkdemo.selectmedia.view.CustomPopWindow;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.PopWindowUtil;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.permission.OnPermissionsSettingListener;
import com.meishe.sdkdemo.utils.permission.PermissionsActivity;
import com.meishe.sdkdemo.utils.permission.PermissionsChecker;
import com.meishe.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

import static com.meishe.sdkdemo.activity.MainActivity.REQUEST_CAMERA_PERMISSION_CODE;
import static com.meishe.sdkdemo.base.BaseConstants.CAPTURESCENE_DIALOG_SP_KEY;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_PATH;

public class CaptureSceneActivity extends BasePermissionActivity implements NvsAssetPackageManager.AssetPackageManagerCallback,
        CustomPopWindow.OnViewClickListener,
        NvsStreamingContext.CaptureDeviceCallback,
        NvsStreamingContext.CaptureRecordingDurationCallback,
        NvsStreamingContext.CaptureRecordingStartedCallback {
    private String TAG = "CaptureSceneActivity";
    private final String NAME = "Master Keyer";
    private ImageView switchText;
    private ImageView flashText;
    private ImageView backgroundText;
    private NvsLiveWindow mLiveWindow;
    private ImageView closeButton_cs;
    private int mCurrentDeviceIndex = 1;
    private PermissionsChecker mPermissionsChecker;
    private boolean mPermissionGranted;
    private List<String> mAllRequestPermission = new ArrayList<>();

    private RelativeLayout captureSceneRecordLayout;
    private CheckBox captureSceneRecord;
    private ImageView captureSceneRecordDelete;
    private ImageView captureSceneRecordSure;
    private TextView captureSceneRecordTime;
    private RecordData currentRecordData;
    private long currentRecordLength = 0;
    private List<RecordData> listOfRecordData = new ArrayList<>();
    private LinearLayout captureSceneControl;
    private CaptureSceneBottomView csLayoutBackground;
    private boolean needShowDialog = false;
    private String mBgSegPackageId;
    ;

    @Override
    protected int initRootView() {
        return R.layout.activity_capture_scene;
    }

    @Override
    protected void initViews() {
        initView();
        mLiveWindow = (NvsLiveWindow) findViewById(R.id.liveWindow);
        switchText = (ImageView) findViewById(R.id.captureScene_switch);
        flashText = (ImageView) findViewById(R.id.captureScene_flash);
        backgroundText = (ImageView) findViewById(R.id.captureScene_background);
        closeButton_cs = (ImageView) findViewById(R.id.closeButton_cs);
        csLayoutBackground = (CaptureSceneBottomView) findViewById(R.id.cs_layout_background);
    }

    @Override
    protected void initTitle() {
    }


    @Override
    protected void initData() {
        MSBus.getInstance().register(this);
        mAssetManager = NvAssetManager.sharedInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        setClick(switchText, flashText, backgroundText, closeButton_cs, captureSceneRecordDelete, captureSceneRecordSure);
        initCapture();
        captureSceneRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Util.isFastClick()) {
                    return;
                }
                if (isRecording()) {
                    stopRecording();
                } else {
                    captureSceneRecord.setBackground(getResources().getDrawable(R.drawable.capturescene_record_button));
                    String filePath = PathUtils.getCaptureSceneRecordVideoPath();
                    if (filePath == null) {
                        return;
                    }
                    currentRecordData = new RecordData(0, filePath);
                    /*
                     * 当前未在视频录制状态，则启动视频录制。此处使用带特效的录制方式
                     * If video recording is not currently in progress, start video recording. Use the recording method with special effects here
                     * */
                    if (!mStreamingContext.startRecording(currentRecordData.getPath())) {
                        return;
                    }
                    listOfRecordData.add(currentRecordData);
                }
            }
        });
        mLiveWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isContainCaptureVideoFxByName(NAME)) {
                    appendBuiltinCaptureVideoFx();
                }
                if (csLayoutBackground.isShow() == true) {
                    setLayoutVisibility();
                    return false;
                }
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    return false;
                }

                NvsColor sampledColor = getColorFromLiveWindow(event);
                /*
                 * 将吸取下来的背景画面颜色值设置给抠像特技
                 * Set the extracted background color value to the keying effect
                 * */
                NvsCaptureVideoFx keyerFx = mStreamingContext.getCaptureVideoFxByIndex(0);
                if (keyerFx == null) {
                    return false;
                }
                keyerFx.setColorVal("Key Color", sampledColor);
                return true;
            }
        });
    }

    private boolean isContainCaptureVideoFxByName(String name) {
        int count = mStreamingContext.getCaptureVideoFxCount();
        for (int i = 0; i < count; i++) {
            NvsCaptureVideoFx nvsCaptureVideoFx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (nvsCaptureVideoFx.getBuiltinCaptureVideoFxName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从NvsLiveWindow控件的点击位置吸取背景画面的颜色值
     * Extract the color value of the background image from the click position of the NVsLiveWindow control
     */
    private NvsColor getColorFromLiveWindow(MotionEvent event) {
        int sampleWidth = 20;
        int sampleHeight = 20;
        RectF sampleRect = new RectF();
        sampleRect.left = (int) (event.getX() - sampleWidth / 2);
        if (sampleRect.left < 0) {
            sampleRect.left = 0;
        } else if (sampleRect.left + sampleWidth > mLiveWindow.getWidth()) {
            sampleRect.left = mLiveWindow.getWidth() - sampleWidth;
        }

        sampleRect.top = (int) (event.getY() - sampleHeight / 2);
        if (sampleRect.top < 0) {
            sampleRect.top = 0;
        } else if (sampleRect.top + sampleHeight > mLiveWindow.getHeight()) {
            sampleRect.top = mLiveWindow.getHeight() - sampleHeight;
        }
        sampleRect.right = sampleRect.left + sampleWidth;
        sampleRect.bottom = sampleRect.top + sampleHeight;
        return mStreamingContext.sampleColorFromCapturedVideoFrame(sampleRect);
    }

    private boolean isRecording() {
        return getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING;
    }

    private void stopRecording() {
        mStreamingContext.stopRecording();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !needShowDialog && !SpUtil.getInstance(this).getBoolean(CAPTURESCENE_DIALOG_SP_KEY, false)) {
            PopWindowUtil.getInstance().show(CaptureSceneActivity.this, R.layout.pop_tips_capturescene, CaptureSceneActivity.this);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.captureScene_switch) {
            mCurrentDeviceIndex = mCurrentDeviceIndex == 0 ? 1 : 0;
            if (mStreamingContext.isCaptureDeviceBackFacing(mCurrentDeviceIndex)) {
                flashText.setEnabled(true);
                flashText.setAlpha(1f);
            } else {
                flashText.setEnabled(false);
                flashText.setAlpha(0.5f);
                if (mStreamingContext.isFlashOn()) {
                    changeFlash();
                }
            }
            startCapturePreview(true);
        } else if (id == R.id.closeButton_cs) {
            AppManager.getInstance().finishActivity();
        } else if (id == R.id.captureScene_flash) {
            changeFlash();
        } else if (id == R.id.captureScene_background) {
            setLayoutVisibility();
        } else if (id == R.id.captureScene_record_delete) {
            if (Util.isFastClick()) {
                return;
            }
            if (isRecording()) {
                stopRecording();
                return;
            }
            if (!listOfRecordData.isEmpty()) {
                listOfRecordData.remove(listOfRecordData.size() - 1);
            }
            onTotalLengthChange();
        } else if (id == R.id.captureScene_record_sure) {
            jumpToPreview();
        }
    }

    /**
     * 打开或关闭闪光灯
     * Turn on or off the flash
     */
    private void changeFlash() {
        mStreamingContext.toggleFlash(!mStreamingContext.isFlashOn());
        flashText.setBackground(mStreamingContext.isFlashOn() ? ContextCompat.getDrawable(getBaseContext(), R.mipmap.icon_flash_on)
                : ContextCompat.getDrawable(getBaseContext(), R.mipmap.icon_flash_off));
    }

    private void setLayoutVisibility() {
        boolean show = csLayoutBackground.isShow();
        if (show) {
            csLayoutBackground.hide();
        } else {
            csLayoutBackground.show(mStreamingContext);
        }
        captureSceneRecordLayout.setVisibility(!csLayoutBackground.isShow() ? View.VISIBLE : View.GONE);
    }

    /**
     * 跳转到预览页面
     * Jump to the preview page
     */
    private void jumpToPreview() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (int i = 0; i < listOfRecordData.size(); i++) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(listOfRecordData.get(i).getPath());
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
        int makeRatio = size.width > size.height ? NvAsset.AspectRatio_16v9 : NvAsset.AspectRatio_9v16;
        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
        TimelineData.instance().setMakeRatio(makeRatio);
        TimelineData.instance().setClipInfoData(pathList);
        Bundle bundle = new Bundle();
        AppManager.getInstance().jumpActivity(CaptureSceneActivity.this, PreviewActivity.class, bundle);
    }

    private void initCapture() {
        if (null == mStreamingContext) {
            return;
        }
        mStreamingContext.getAssetPackageManager().setCallbackInterface(this);
        mStreamingContext.setCaptureDeviceCallback(this);
        mStreamingContext.setCaptureRecordingDurationCallback(this);
        mStreamingContext.setCaptureRecordingStartedCallback(this);
        if (mStreamingContext.getCaptureDeviceCount() == 0) {
            return;
        }
        if (!mStreamingContext.connectCapturePreviewWithLiveWindow(mLiveWindow)) {
            Logger.e(TAG, "Failed to connect capture preview with livewindow!");
            return;
        }
        switchText.setEnabled(mStreamingContext.getCaptureDeviceCount() > 1);
        mPermissionsChecker = new PermissionsChecker(this);
        mAllRequestPermission.add(Manifest.permission.CAMERA);
        mAllRequestPermission = mPermissionsChecker.checkPermission(mAllRequestPermission);
        if (mAllRequestPermission.isEmpty()) {
            mPermissionGranted = true;
            startCapturePreview(false);
        } else {
            int code = getCodeInPermission(mAllRequestPermission.get(0));
            startPermissionsActivity(code, mAllRequestPermission.get(0));
        }
    }

    private void appendBuiltinCaptureVideoFx() {
        NvsCaptureVideoFx keyerFx = mStreamingContext.appendBuiltinCaptureVideoFx("Master Keyer");
        if (keyerFx != null) {
            /*
             * 开启溢色去除
             * Turn on overflow color removal
             * */
            keyerFx.setBooleanVal("Spill Removal", true);
            /*
             * 将溢色去除强度设置为最低
             * Set the overflow color removal intensity to minimum
             * */
            keyerFx.setFloatVal("Spill Removal Intensity", 0);
            /*
             * 设置收缩边界强度
             * Set shrink border strength
             * */
            keyerFx.setFloatVal("Shrink Intensity", 0.4);
        }
    }

    private void startPermissionsActivity(int code, String... permission) {
        PermissionsActivity.startActivityForResult(this, code, permission);
    }

    /**
     * 获取activity需要的权限列表
     * Get the list of permissions required by the activity
     *
     * @return 权限列表;Permission list
     */
    @Override
    protected List<String> initPermissions() {
        return Util.getWriteReadPermission();
    }

    /**
     * 获取权限后
     * After obtaining permissions
     */
    @Override
    protected void hasPermission() {
        mPermissionGranted = true;
    }

    /**
     * 没有允许权限
     * No permission
     */
    @Override
    protected void nonePermission() {

    }

    /**
     * 用户选择了不再提示
     * The user chose not to prompt again
     */
    @Override
    protected void noPromptPermission() {
        Util.setPermissionsSetting(CaptureSceneActivity.this, new OnPermissionsSettingListener() {
            @Override
            public void onSettingCancel() {
                AppManager.getInstance().finishActivity();
            }

            @Override
            public void onSettingConfirm() {
                AppManager.getInstance().finishActivity();
            }
        });
    }

    private int getCodeInPermission(String permission) {
        int code = 0;
        if (permission.equals(Manifest.permission.CAMERA)) {
            code = REQUEST_CAMERA_PERMISSION_CODE;
        }
        return code;
    }

    private void startCapturePreview(boolean deviceChanged) {
        int captureResolutionGrade = ParameterSettingValues.instance().getCaptureResolutionGrade();
        if (mPermissionGranted && (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW)) {
            if (!mStreamingContext.startCapturePreview(mCurrentDeviceIndex,
                    captureResolutionGrade,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_GRAB_CAPTURED_VIDEO_FRAME |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER, null)) {
                Logger.e(TAG, "Failed to start capture preview!");
            }
        }
    }

    private int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    private void setClick(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onFinishAssetPackageInstallation(String assetPackageId, String s1, int i, int error) {
        Logger.e(TAG, "onFinishAssetPackageInstallation: " + error + "     " + s1);
        if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR) {
            mStreamingContext.applyCaptureScene(assetPackageId);
        }
    }

    @Override
    public void onFinishAssetPackageUpgrading(String s, String s1, int i, int i1) {

    }


    @MSSubscribe(Constants.SubscribeType.SUB_CAPTURE_SCENE_ITEM_CLICK_TYPE)
    public void onImageResourceItemClick(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        NvAsset asset = mAssetManager.installAssetPackage(Constants.BG_SEGMENT_CAPTURE_SCENE_PATH, NvAsset.ASSET_CAPTURE_SCENE, false);
        if (null != asset) {
            mBgSegPackageId = asset.uuid;
        }
        if (TextUtils.isEmpty(mBgSegPackageId)) {
            return;
        }
        NvsCaptureSceneInfo sceneInfo = new NvsCaptureSceneInfo();
        List<NvsCaptureSceneInfo.ClipData> clipDataList = new ArrayList<>();
        NvsCaptureSceneInfo.ClipData clipData = new NvsCaptureSceneInfo.ClipData();
        //视频 Video
        clipData.scan = 1;
        //图片 Image
        clipData.imageFillMode = NvsCaptureSceneInfo.CAPTURESCENE_INFO_IMAGE_FILLMODE_CROP;
        clipData.mediaPath = filePath;
        clipDataList.add(clipData);
        sceneInfo.backgroundClipArray = clipDataList;
        mStreamingContext.applyCaptureScene(mBgSegPackageId, sceneInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 100) {
            String imagePath = data.getStringExtra(SINGLE_PICTURE_PATH);
            if ((null == csLayoutBackground) || TextUtils.isEmpty(imagePath)) {
                return;
            }
            csLayoutBackground.addLocalResource(imagePath);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionsChecker == null) {
            mPermissionsChecker = new PermissionsChecker(this);
        }
        if (mPermissionGranted) {
            startCapturePreview(false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
        flashText.setBackground(ContextCompat.getDrawable(getBaseContext(), R.mipmap.icon_flash_off));
    }


    @Override
    public void onViewClick(CustomPopWindow popWindow, View view) {
        if (view.getId() == R.id.pop_tips_tv_noMore) {
            SpUtil.getInstance(this).putBoolean(CAPTURESCENE_DIALOG_SP_KEY, true);
        } else if (view.getId() == R.id.pop_tips_tv_iKnow) {
            needShowDialog = true;
        }
    }

    private void initView() {
        captureSceneControl = (LinearLayout) findViewById(R.id.captureScene_control);
        captureSceneRecordLayout = (RelativeLayout) findViewById(R.id.captureScene_record_layout);
        captureSceneRecord = (CheckBox) findViewById(R.id.captureScene_record);
        captureSceneRecordDelete = (ImageView) findViewById(R.id.captureScene_record_delete);
        captureSceneRecordSure = (ImageView) findViewById(R.id.captureScene_record_sure);
        captureSceneRecordTime = (TextView) findViewById(R.id.captureScene_record_time);
    }

    @Override
    public void onCaptureDeviceCapsReady(int i) {

    }

    @Override
    public void onCaptureDevicePreviewResolutionReady(int i) {

    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {

    }

    @Override
    public void onCaptureDeviceError(int i, int i1) {

    }

    @Override
    public void onCaptureDeviceStopped(int i) {

    }

    @Override
    public void onCaptureDeviceAutoFocusComplete(int i, boolean b) {

    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        captureSceneControl.setVisibility(View.VISIBLE);
        captureSceneRecord.setChecked(true);
        currentRecordData.setLength(currentRecordLength);
        onTotalLengthChange();
        if (listOfRecordData != null && !listOfRecordData.isEmpty()) {
            for (RecordData recordData : listOfRecordData) {
                if (recordData == null) {
                    continue;
                }
                if (recordData.getPath().endsWith(".mp4")) {
                    MediaScannerUtil.scanFile(recordData.getPath(), "video/mp4");
                } else if (recordData.getPath().endsWith(".jpg")) {
                    MediaScannerUtil.scanFile(recordData.getPath(), "image/jpg");
                }
            }
        }
    }

    @Override
    public void onCaptureRecordingError(int i) {
        captureSceneRecord.setChecked(true);
    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        currentRecordLength = l;
        captureSceneRecord.setEnabled(l / 1000 > 3000);
        captureSceneRecordTime.setText(TimeFormatUtil.formatUsToString2(l + getTotalRecordLength()));
        captureSceneRecordTime.setTextColor(getResources().getColor(R.color.cs_textColor_recording));

    }

    @Override
    public void onCaptureRecordingStarted(int i) {
        captureSceneControl.setVisibility(View.GONE);
        captureSceneRecord.setText("");
    }

    /**
     * 获取拍摄片段总时长
     * Gets the total length of the shot clip
     *
     * @return long
     */
    private long getTotalRecordLength() {
        if (listOfRecordData.isEmpty()) {
            return 0;
        } else {
            long total = 0;
            for (RecordData listOfRecordDatum : listOfRecordData) {
                total += listOfRecordDatum.getLength();
            }
            return total;
        }
    }

    private void onTotalLengthChange() {
        long length = getTotalRecordLength();
        captureSceneRecordDelete.setVisibility(length == 0 ? View.GONE : View.VISIBLE);
        captureSceneRecordSure.setVisibility(length == 0 ? View.GONE : View.VISIBLE);
        captureSceneRecordTime.setText(length == 0 ? "" : TimeFormatUtil.formatUsToString2(getTotalRecordLength()));
        captureSceneRecordTime.setTextColor(getResources().getColor(R.color.white));
        captureSceneRecord.setBackground(length == 0 ? getResources().getDrawable(R.drawable.capturescene_normal) : getResources().getDrawable(R.drawable.capturescene_record_button));
        captureSceneRecord.setText(length == 0 ? "" : String.valueOf(listOfRecordData.size()));
    }

    @Override
    protected void onDestroy() {
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureVideoFx();
            mStreamingContext.removeCurrentCaptureScene();
            mStreamingContext.stop();
            mStreamingContext = null;
        }
        MSBus.getInstance().unregister(this);
        super.onDestroy();
    }


}
