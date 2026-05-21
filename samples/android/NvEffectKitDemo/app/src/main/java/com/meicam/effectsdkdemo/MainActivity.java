package com.meicam.effectsdkdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.meicam.effectsdkdemo.camera.RenderCameraUtil;
import com.meicam.effectsdkdemo.controller.MainController;
import com.meicam.effectsdkdemo.interfaces.RenderListener;
import com.meicam.effectsdkdemo.ui.SidebarView;
import com.meicam.effectsdkdemo.view.RecordView;
import com.meicam.effectsdkdemo.view.ZoomExposeView;
import com.meishe.libmakeup.view.MakeUpView;
import com.meishe.libmsbeauty.view.MsBeautyView;
import com.meishe.nveffectkit.NveEffectKit;
import com.meishe.nveffectkit.constants.NveDetectionModelType;

import static com.meicam.effectsdkdemo.Constants.ADVANCED_BEAUTY_DAT;
import static com.meicam.effectsdkdemo.Constants.FACE_COMMON_DAT;
import static com.meicam.effectsdkdemo.Constants.FAKE_FACE_DAT;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private GLSurfaceView mGlView;
    private RenderCameraUtil mRenderCameraUtil;
    private NveEffectKit mNvEffectKit;
    private MainController mMainController;
    private RelativeLayout mStartLayout;
    private RecordView mRecordView;
    private AlertDialog mCaptureDialog;
    private SidebarView mMainSidebar;
    private MakeUpView makeUpView;
    private MsBeautyView msBeautyView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        mNvEffectKit = NveEffectKit.getInstance();
        String licPath = "assets:/effectsdkdemo.lic";
        mNvEffectKit.init(this, licPath);
        mMainController = new MainController();
        initView();
        initEffectModel();
        addViewListener();
    }

    private void initView() {
        mGlView = findViewById(R.id.GLView);
        mStartLayout = findViewById(R.id.start_layout);
        mRecordView = findViewById(R.id.record_view);
        mCaptureDialog = new AlertDialog.Builder(this).create();
        mCaptureDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mRecordView.getVisibility() != View.VISIBLE) {
                    mRecordView.setVisibility(View.VISIBLE);
                }
                mMainController.closeBeautyView();
                mMainController.closeCaptureDialogView(mCaptureDialog, mStartLayout);
            }
        });
        mMainSidebar = findViewById(R.id.main_sidebar);
    }

    /**
     * 初始化模型
     */
    private void initEffectModel() {
        String faceModelPath = "assets:/facemodel/ms/" + Constants.FACE_240_MODEL;
        boolean initSuccess = mNvEffectKit.initModel(this, NveDetectionModelType.FACE, faceModelPath);

        String faceCommonPath = "assets:/facemodel/common/" + FACE_COMMON_DAT;
        boolean faceCommonSuccess = mNvEffectKit.initModel(this, NveDetectionModelType.FACE_COMMON, faceCommonPath);

        String advancedBeautyPath = "assets:/facemodel/common/" + ADVANCED_BEAUTY_DAT;
        boolean advancedBeautySuccess = mNvEffectKit.initModel(this, NveDetectionModelType.ADVANCED_BEAUTY, advancedBeautyPath);

        String fakeFaceModelPath = "assets:/facemodel/common/" + FAKE_FACE_DAT;
        boolean fakefaceSuccess = mNvEffectKit.initModel(this, NveDetectionModelType.FAKE_FACE, fakeFaceModelPath);

        String eyeModelPath = "assets:/facemodel/common/" + Constants.EYE_CONTOUR_MODEL;
        boolean eyeballSuccess = mNvEffectKit.initModel(this, NveDetectionModelType.EYEBALL, eyeModelPath);

        String segModelPath = "assets:/facemodel/common/" + Constants.HUMAN_SEG_MODEL;
        boolean segmentationSuccess = mNvEffectKit.initModel(this, NveDetectionModelType.SEGMENTATION, segModelPath);

        String avatarModelPath = "assets:/facemodel/common/" + Constants.AVATAR_MODEL;
        boolean avatarSuccess = mNvEffectKit.initModel(this, NveDetectionModelType.AVATAR, avatarModelPath);

        StringBuilder sb = new StringBuilder();
        sb.append("******************************************").append("\n")
                .append("Face :").append(initSuccess).append("\n")
                .append("FaceCommon:").append(faceCommonSuccess).append("\n")
                .append("AdvancedBeauty:").append(advancedBeautySuccess).append("\n")
                .append("Fake Face:").append(fakefaceSuccess).append("\n")
                .append("Eyeball:").append(eyeballSuccess).append("\n")
                .append("Segment:").append(segmentationSuccess).append("\n")
                .append("Avatar:").append(avatarSuccess).append("\n")
                .append("******************************************");
        Log.e("meicam", sb.toString());
    }

    private void addViewListener() {
        mRenderCameraUtil = new RenderCameraUtil(this, mGlView, new RenderListener() {
            @Override
            public void onSurfaceChanged() {

            }

            @Override
            public void onDrawFrame() {

            }

            @Override
            public void updateFlashView(boolean toggle) {
                mMainSidebar.updateFlashView(toggle);
            }
        });
        mMainSidebar.setOnSidebarListener(new SidebarView.OnSidebarListener() {
            @Override
            public void onHideView() {
                if (mRecordView.getVisibility() == View.VISIBLE) {
                    mRecordView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBarSwitch() {
                if (null != mRenderCameraUtil) {
                    mRenderCameraUtil.switchCamera();
                }
            }

            @Override
            public void onBarFlash() {
                if (null != mRenderCameraUtil) {
                    mRenderCameraUtil.switchFlash();
                }
            }

            @Override
            public void onBarZoom() {
                if (!mRenderCameraUtil.isSupportZoom()) {
                    Toast.makeText(MainActivity.this, getString(R.string.zoom_is_not_supported), Toast.LENGTH_SHORT).show();
                    return;
                }
                mMainController.showCaptureDialogView(mCaptureDialog,
                        mMainController.initZoomExposeView(MainActivity.this, ZoomExposeView.TYPE_ZOOM, mRenderCameraUtil), mStartLayout);
            }

            @Override
            public void onBarExpose() {
                if (!mRenderCameraUtil.isSupportExpose()) {
                    Toast.makeText(MainActivity.this, getString(R.string.exposure_is_not_supported), Toast.LENGTH_SHORT).show();
                    return;
                }
                mMainController.showCaptureDialogView(mCaptureDialog,
                        mMainController.initZoomExposeView(MainActivity.this, ZoomExposeView.TYPE_EXPOSE, mRenderCameraUtil), mStartLayout);
            }

            @Override
            public void onBarBeauty() {
                if (null == msBeautyView) {
                    msBeautyView = mMainController.initBeautyView(MainActivity.this);
                }
                mMainController.showCaptureDialogView(mCaptureDialog, msBeautyView, mStartLayout);
            }

            @Override
            public void onBarProp() {
                mMainController.showCaptureDialogView(mCaptureDialog, mMainController.initPropView(MainActivity.this), mStartLayout);
            }

            @Override
            public void onBarFilter() {
                mMainController.showCaptureDialogView(mCaptureDialog, mMainController.initFilterView(MainActivity.this), mStartLayout);

            }

            @Override
            public void onBarMakeup() {
                if (null == makeUpView) {
                    makeUpView = mMainController.initMakeupView(MainActivity.this);
                    makeUpView.setMakeupListener(new MakeUpView.OnMakeupListener() {
                        @Override
                        public void onMakeup(int tabPosition, int position) {
                            if (tabPosition == 0 && position == 0 && (null != msBeautyView)) {
                                msBeautyView.resetApply();
                            }
                        }
                    });
                }
                mMainController.showCaptureDialogView(mCaptureDialog, makeUpView, mStartLayout);
            }

            @Override
            public void onBarSeg() {
            }

            @Override
            public void onBarAdjust() {

            }
        });
        mRecordView.setOnRecordListener(new RecordView.OnRecordListener() {
            @Override
            public void onStartRecord(int type) {
                if (null == mRenderCameraUtil) {
                    return;
                }
                mRenderCameraUtil.startRecordVideo();
            }

            @Override
            public void onStopRecord(int type) {
                if (null == mRenderCameraUtil) {
                    return;
                }
                mRenderCameraUtil.stopRecordVideo();
            }
        });
    }

    @Override
    protected void onResume() {
        mRenderCameraUtil.onResume();
        mMainController.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRenderCameraUtil.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRenderCameraUtil.onDestroy();
        mNvEffectKit.destroy();
        super.onDestroy();
    }
}
