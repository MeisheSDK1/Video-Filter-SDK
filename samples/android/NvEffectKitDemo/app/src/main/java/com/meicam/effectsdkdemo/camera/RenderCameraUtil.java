package com.meicam.effectsdkdemo.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.opengl.EGLContext;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.draw.GLDrawer;
import com.meicam.effectsdkdemo.interfaces.RenderListener;
import com.meicam.effectsdkdemo.media.MediaVideoEncoder;
import com.meicam.effectsdkdemo.utils.EGLHelper;
import com.meicam.effectsdkdemo.utils.MediaScannerUtil;
import com.meishe.libbase.util.ToastUtil;
import com.meishe.nveffectkit.NveEffectKit;
import com.meishe.nveffectkit.render.NveImageBuffer;
import com.meishe.nveffectkit.render.NveRenderConfig;
import com.meishe.nveffectkit.render.NveRenderInput;
import com.meishe.nveffectkit.render.NveRenderOutput;
import com.meishe.nveffectkit.render.NveSize;
import com.meishe.nveffectkit.render.NveTexture;
import com.meishe.nveffectkit.render.enumeration.NveFormatType;
import com.meishe.nveffectkit.render.enumeration.NveRenderError;
import com.meishe.nveffectkit.render.enumeration.NveTextureType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glGenTextures;

public class RenderCameraUtil implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "RenderCameraUtil";
    private static final int PREVIEW_HEIGHT = 720;
    private static final int PREVIEW_WIDTH = 1280;
    private final Object mGLThreadSyncObject = new Object();
    private final Activity mContext;
    private final NveEffectKit mNvEffectKit;
    private final GLSurfaceView mGlView;
    private final OrientationEventListener mOrientationEventListener;
    private final int[] mCameraPreviewTextures = new int[1];
    private final float[] mVideoTextureTransform = new float[16];
    private final float[] TEXTURE_COORDS = {
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private NvsVideoResolution mCurrentVideoResolution;
    private ICameraProxy mCameraProxy;
    private RenderListener mRenderListener;
    private SurfaceTexture mCameraPreviewTexture;
    private GLDrawer mGlDrawer;
    private HandlerThread mSurfaceAvailableThread;
    private Handler mSurfaceAvailableHandler;
    private EGLContext mEglContext;
    private MediaRecord mMediaRecord;
    private MediaVideoEncoder mVideoEncoder;
    private int mDeviceOrientation = 0;
    private int mOrientation;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mDisplayWidth, mDisplayHeight;
    private boolean mNeedResetEglContext = false;
    private boolean mFlashToggle = false;
    private boolean mFrameAvailable = false;
    private boolean mIsRecording = false;
    private boolean mIsPreviewing = false;
    private NveRenderInput mRenderInput;
    private NveRenderConfig mRenderConfig;
    private NveTexture mNveTexture;
    private NveImageBuffer mImageBuffer;
    private NveRenderOutput renderOutput;
    private byte[] mPreviewData;

    public RenderCameraUtil(Activity activity, GLSurfaceView glSurfaceView, RenderListener renderListener) {
        mContext = activity;
        mGlView = glSurfaceView;
        mRenderListener = renderListener;
        mMediaRecord = new MediaRecord();
        mNvEffectKit = NveEffectKit.getInstance();
        initSurfaceView();
        mCameraProxy = CameraBuilder.buildCameraByType(activity, 0);
        mOrientationEventListener = new OrientationEventListener(activity, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int angle) {
                mDeviceOrientation = angle;
            }
        };
    }

    private void initSurfaceView() {
        //设置opengl版本
        mGlView.setEGLContextClientVersion(3);
        mGlView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mEglContext = mGlDrawer.createEglContext();
                }
                mGlDrawer.setupGraphics(mContext);
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                mDisplayWidth = width;
                mDisplayHeight = height;
                mGlDrawer.setupFrameInfo(mDisplayWidth, mDisplayHeight);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    drawFrameToGlView();
                }
            }
        });
        Log.e(TAG, "initSurfaceView: setRenderer");
        //RenderMode 有两种，RENDERMODE_WHEN_DIRTY 和 RENDERMODE_CONTINUOUSLY，前者是懒惰渲染，需要手动调用
        // glSurfaceView.requestRender() 才会进行更新，而后者则是不停渲染。
        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void drawFrameToGlView() {
        if (!mIsPreviewing) {
            return;
        }
        synchronized (this) {
            if (mFrameAvailable) {
                mCameraPreviewTexture.updateTexImage();
                mCameraPreviewTexture.getTransformMatrix(mVideoTextureTransform);
                mFrameAvailable = false;
            }
        }
        if (mGlDrawer == null) {
            mGlDrawer = new GLDrawer();
        }
        mGlDrawer.setTransformMatrix(mVideoTextureTransform);

        boolean isInPreview = false;
        synchronized (mGLThreadSyncObject) {
            isInPreview = mIsPreviewing;
        }

        int displayTex = mCameraPreviewTextures[0];
        int texWidth = mCurrentVideoResolution.imageWidth;
        int texHeight = mCurrentVideoResolution.imageHeight;
        if (isInPreview) {
            if (null == mRenderInput) {
                mRenderInput = new NveRenderInput();
                mRenderInput.setPixelFormat(NveFormatType.VIDEO_FRAME_PIXEL_FROMAT_NV21.getType());
            }
            if (null == mRenderConfig) {
                mRenderConfig = new NveRenderConfig().setRenderMode(NveRenderConfig.NveRenderMode.TEXTURE_TEXTURE).build();
                mRenderInput.setConfig(mRenderConfig);
            }
            mRenderConfig.setCameraOrientation(mCameraProxy.getOrientation());
            //设置纹理
            mNveTexture = new NveTexture();
            mNveTexture.setTextureType(NveTextureType.NV_OES_TEXTURE);
            mNveTexture.setOrientation(mCameraProxy.getOrientation());
            mNveTexture.setSize(new NveSize(texWidth, texHeight));
            mNveTexture.setMirror(mCameraProxy.isFlipHorizontal());
            mNveTexture.setVerticalMirror(true);
            mRenderInput.setTexture(mNveTexture);
            mNveTexture.setTextureId(displayTex);

            if (null != mPreviewData) {
                //设置buffer
                mImageBuffer = new NveImageBuffer();
                mImageBuffer.setSize(new NveSize(texHeight, texWidth));
                mImageBuffer.setDisplayRotation(mOrientation);
                mImageBuffer.setMirror(mCameraProxy.isFlipHorizontal());
                mRenderInput.setImageBuffer(mImageBuffer);
                mImageBuffer.setData(mPreviewData);
            }
            renderOutput = mNvEffectKit.renderEffect(mRenderInput);
            if ((null != renderOutput)
                    && (renderOutput.getErrorCode().getCode() == NveRenderError.NO_ERROR.getCode())) {
                displayTex = renderOutput.getTexture().getTextureId();
            }
            if (null != mGlDrawer) {
                mGlDrawer.setBufferMode(false);
            }
        }

        if (mGlDrawer != null) {
            mGlDrawer.setupFrameInfo(mDisplayWidth, mDisplayHeight);
            if (displayTex == mCameraPreviewTextures[0]) {
                mGlDrawer.drawTextureOES(mCameraPreviewTextures[0], texWidth, texHeight, mDisplayWidth, mDisplayHeight, true);
                if (mIsRecording) {
                    displayTex = mGlDrawer.drawTextureToTexture(mCameraPreviewTextures[0], texWidth, texHeight, mDisplayWidth, mDisplayHeight);
                }
            } else {
                mGlDrawer.drawTexture(displayTex, texWidth, texHeight, mDisplayWidth, mDisplayHeight);
            }
        }
        if (mIsRecording) {
            long timestamp = mCameraPreviewTexture.getTimestamp();
            GLES20.glFinish();
            synchronized (this) {
                if (mVideoEncoder != null) {
                    if (mNeedResetEglContext) {
                        mEglContext = mGlDrawer.createEglContext();
                        mVideoEncoder.setEglContext(mEglContext);
                        mNeedResetEglContext = false;
                    }
                    mVideoEncoder.frameAvailableSoon(displayTex, timestamp);
                }
            }
        }
        if (null != renderOutput) {
            mNvEffectKit.recycleOutput(renderOutput);
        }
    }

    public void onResume() {
        if (null == mGlDrawer) {
            mGlDrawer = new GLDrawer();
        }
        mGlView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mGlDrawer.setupVertexBuffer();
                setupTexture();
                // most of sdk function need call in ui thread
                //SDK中绝大部分函数都应在UI线程调用
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsPreviewing && mCameraPreviewTexture != null) {
                            startCapturePreview();
                            mNvEffectKit.initialize();
                        }
                    }
                });

            }
        });
    }


    private void startCapturePreview() {
        if (mIsPreviewing || mCameraPreviewTexture == null) {
            return;
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
        setupCamera();
        mCameraProxy.startPreview(mCameraPreviewTexture, mICameraCallBack);
        mCurrentVideoResolution = new NvsVideoResolution();
        if (mOrientation == 90 || mOrientation == 270) {
            mCurrentVideoResolution.imageWidth = PREVIEW_HEIGHT;
            mCurrentVideoResolution.imageHeight = PREVIEW_WIDTH;
        } else {
            mCurrentVideoResolution.imageWidth = PREVIEW_WIDTH;
            mCurrentVideoResolution.imageHeight = PREVIEW_HEIGHT;
        }
        mCurrentVideoResolution.imagePAR = new NvsRational(1, 1);
        //camera2 The previous frame is reversed when switching the camera. Delay drawing processing.
        //camera2在切换摄像头会出现前一帧画面颠倒的问题。延时绘制处理。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (mGLThreadSyncObject) {
                    mIsPreviewing = true;
                }
            }
        }, 500);
    }

    private void setupTexture() {
        ByteBuffer texturebb = ByteBuffer.allocateDirect(TEXTURE_COORDS.length * 4);
        texturebb.order(ByteOrder.nativeOrder());

        FloatBuffer textureBuffer = texturebb.asFloatBuffer();
        textureBuffer.put(TEXTURE_COORDS);
        textureBuffer.position(0);
        mGlDrawer.setTextureBuffer(textureBuffer);
        // Generate the actual texture
        // 创建纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        glGenTextures(1, mCameraPreviewTextures, 0);
        EGLHelper.checkGlError("Texture generate");

        if (mSurfaceAvailableThread == null) {
            mSurfaceAvailableThread = new HandlerThread("ProcessImageThread");
            mSurfaceAvailableThread.start();
            mSurfaceAvailableHandler = new Handler(mSurfaceAvailableThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                }
            };
        }

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraPreviewTextures[0]);
        // create preview texture for camera
        //创建摄像机需要的Preview Texture
        mCameraPreviewTexture = new SurfaceTexture(mCameraPreviewTextures[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraPreviewTexture.setOnFrameAvailableListener(this, mSurfaceAvailableHandler);
        } else {
            mCameraPreviewTexture.setOnFrameAvailableListener(this);
        }
        mCameraPreviewTexture.setOnFrameAvailableListener(this);
    }


    private void setupCamera() {
        mCameraProxy.stopPreview();
        mCameraProxy.openCamera(mCameraId);
        mCameraProxy.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        mOrientation = CameraUtils.getDisplayOrientation(mContext, mCameraId);
    }


    private void stopRenderThread() {
        try {
            if (mSurfaceAvailableHandler != null) {
                mSurfaceAvailableHandler.removeCallbacksAndMessages(null);
                mSurfaceAvailableHandler = null;
            }
            if (mSurfaceAvailableThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mSurfaceAvailableThread.quitSafely();
                }
                mSurfaceAvailableThread.join();
                mSurfaceAvailableThread = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ICameraCallBack mICameraCallBack = new ICameraCallBack() {
        @Override
        public void onCameraPreviewCallBack(byte[] data) {
            mPreviewData = data;
            //request updata glSurfaceTexture when data comes
            //强制刷新glSurfaceView
            if (mIsPreviewing) {
                mGlView.requestRender();
            }
        }

        @Override
        public void onCameraTakePictureCallBack(byte[] data, int width, int height) {
        }
    };

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            mFrameAvailable = true;
        }
    }

    /**
     * 开始录制
     */
    public void startRecordVideo() {
        if (null == mMediaRecord) {
            return;
        }
        mNeedResetEglContext = true;
        mMediaRecord.startRecord(mCurrentVideoResolution.imageWidth, mCurrentVideoResolution.imageHeight, new MediaRecord.OnMediaRecordListener() {
            @Override
            public void onPrepared(MediaVideoEncoder encoder) {
                setVideoEncoder(encoder);
            }

            @Override
            public void onStopped(MediaVideoEncoder encoder) {
                setVideoEncoder(encoder);
            }
        });
        mIsRecording = true;
    }

    /**
     * 停止录制
     */
    public void stopRecordVideo() {
        mIsRecording = false;
        if (null == mMediaRecord) {
            return;
        }
        String recordPath = mMediaRecord.stopRecord();
        if (!TextUtils.isEmpty(recordPath)) {
            ToastUtil.showToast(mContext, "File Path: " + recordPath);
            MediaScannerUtil.scan(recordPath);
        }
    }

    private void setVideoEncoder(final MediaVideoEncoder encoder) {
        mGlView.queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if ((encoder != null)) {
                        encoder.setEglContext(mEglContext);
                    }
                    mVideoEncoder = encoder;
                }
            }
        });
    }


    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mCameraProxy.cameraOpenFailed()) {
            return;
        }
        synchronized (mGLThreadSyncObject) {
            mIsPreviewing = false;
        }
        mCameraId = 1 - mCameraId;
        startCapturePreview();
        if (null != mRenderListener && (mCameraId == 1)) {
            mRenderListener.updateFlashView(false);
        }
    }

    /**
     * 切换闪光灯
     */
    public void switchFlash() {
        if (mCameraProxy.cameraOpenFailed()) {
            return;
        }
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Toast.makeText(mContext, mContext.getString(R.string.open_flash_after_open_rear_camera), Toast.LENGTH_SHORT).show();
            return;
        }
        mFlashToggle = !mFlashToggle;
        mCameraProxy.toggleFlash(mFlashToggle);
        if (null != mRenderListener) {
            mRenderListener.updateFlashView(mFlashToggle);
        }
    }

    /**
     * 是否支持变焦
     *
     * @return boolean
     */
    public boolean isSupportZoom() {
        return mCameraProxy.isSupportZoom();
    }

    /**
     * 设置变焦
     *
     * @param zoomValue zoomValue
     */
    public void setZoom(int zoomValue) {
        if (!isSupportZoom()) {
            return;
        }
        mCameraProxy.setZoom(zoomValue);
    }

    /**
     * 获取变焦等级
     *
     * @return int
     */
    public int getZoomRange() {
        return mCameraProxy.getZoomRange();
    }

    public int getCurrentZoom() {
        return mCameraProxy.getCurrentZoom();
    }

    /**
     * 是否支持曝光
     *
     * @return boolean
     */
    public boolean isSupportExpose() {
        return mCameraProxy.isSupportExpose();
    }

    /**
     * 设置曝光系数
     *
     * @param exposureCompensation exposureCompensation
     */
    public void setExposureCompensation(int exposureCompensation) {
        if (!isSupportExpose()) {
            return;
        }
        mCameraProxy.setExposureCompensation(exposureCompensation);
    }

    /**
     * 获取曝光系数等级
     *
     * @return int
     */
    public int getExposureCompensationRange() {
        return mCameraProxy.getExposureCompensationRange();
    }

    /**
     * 获取当前曝光系数
     *
     * @return int
     */
    public int getCurrentExpose() {
        return mCameraProxy.getCurrentExpose();
    }

    public void onPause() {
        mCameraProxy.stopPreview();
        mCameraProxy.releaseCamera();
        if (mIsRecording) {
            stopRecordVideo();
        }
        synchronized (mGLThreadSyncObject) {
            mIsPreviewing = false;
        }
        final CountDownLatch count = new CountDownLatch(1);
        mGlView.queueEvent(new Runnable() {
            @Override
            public void run() {
                destroyGlResource();
                mCameraPreviewTexture.release();
                mCameraPreviewTexture = null;
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void destroyGlResource() {
        if (null != mGlDrawer) {
            mGlDrawer.release();
        }
        mNvEffectKit.cleanUp();
    }

    public void onDestroy() {
        stopRenderThread();
        mCameraProxy = null;
    }

}
