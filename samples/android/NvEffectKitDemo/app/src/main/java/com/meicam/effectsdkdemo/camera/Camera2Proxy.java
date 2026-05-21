package com.meicam.effectsdkdemo.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.meicam.effectsdkdemo.Constants;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;

public class Camera2Proxy implements ICameraProxy {

    private static final String TAG = "CameraHelper";

    private Context mContext;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;
    private String mCameraId = null;
    private Surface mSurface;
    private CaptureRequest.Builder mPreviewBuilder;
    private CaptureRequest.Builder mPictureBuilder;
    private Object mSyn = new Object();

    private HandlerThread mBackgroundThread;
    private Handler mMainHandler;
    private Handler mChildHanlder;

    private ImageReader mPictureImageReader;
    private ImageReader mPreviewImageReader;
    private ICameraCallBack mICameraCallBack;

    private boolean isCameraOpen = false;
    private boolean mCameraOpenFailed = false;
    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     * ImageReader的毁掉接口，onImageAvailable 在图片准备好之后会被调用
     */
    private final ImageReader.OnImageAvailableListener mOnPicImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            if (image != null) {
                byte[] picData = getNv21Data(image);
                int width = image.getWidth();
                int height = image.getHeight();
                image.close();
                if (mICameraCallBack != null) {
                    mICameraCallBack.onCameraTakePictureCallBack(picData, width, height);
                }
            }
        }

    };

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * preview image is ready.
     * ImageReader的毁掉接口，onImageAvailable 在预览图像准备好之后会被调用
     */
    private final ImageReader.OnImageAvailableListener mOnPreviewImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            if (image != null) {
                byte[] captureData = getNv21Data(image);
                int width = image.getWidth();
                int height = image.getHeight();
                image.close();

                if (mICameraCallBack != null) {
                    mICameraCallBack.onCameraPreviewCallBack(captureData);
                    getBitmapImageFromYUV(captureData, width, height);
                }
            }
        }

    };
    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }
    public static Camera2Proxy createCamera(Context context) {
        Camera2Proxy cameraHelper = new Camera2Proxy(context);
        // new Thread(cameraHelper).start();
        return cameraHelper;
    }

    private Camera2Proxy(Context context) {
        mContext = context;
        startBackgroundThread();

    }

    public void switchCamera(String cameraId) {
        mCameraId = cameraId;

        stop();
        initCamera();
    }

    public void stop() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }

        stopBackgroundThread();
    }

    /**
     * Starts a background thread and its {@link Handler}.
     * 通过ThreadHandler启动后台线程
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mMainHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     * 停止ThreadHandler后台线程
     */
    private void stopBackgroundThread() {
        if (mBackgroundThread == null)
            return;
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mMainHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void initCamera() {

        if (mCameraId.equalsIgnoreCase("-1"))
            return;

        startBackgroundThread();

        //得到CameraManager
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {

            CameraCharacteristics characteristics
                    = mCameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                return;
            }


            // For still image captures, we use the largest available size.
            // 使用最大可用的尺寸去保存拍照图片
            Size[] outputs = map.getOutputSizes(ImageFormat.YUV_420_888);
            Size largest = Collections.max(
                    Arrays.asList(outputs),
                    new CompareSizesByArea());
            mPictureImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.YUV_420_888, /*maxImages*/3);
            mPictureImageReader.setOnImageAvailableListener(mOnPicImageAvailableListener, mMainHandler);

            mPreviewImageReader = ImageReader.newInstance(Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT,
                    ImageFormat.YUV_420_888, /*maxImages*/6);
            mPreviewImageReader.setOnImageAvailableListener(mOnPreviewImageAvailableListener, mMainHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openCamera(int cameraId) {
        mCameraId = String.valueOf(cameraId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startPreview(SurfaceTexture surfaceTexture, ICameraCallBack iCameraCallBack) {
        try {
            stop();
            this.mICameraCallBack = iCameraCallBack;
            surfaceTexture.setDefaultBufferSize(Constants.PREVIEW_WIDTH, Constants.PREVIEW_HEIGHT);
            mSurface = new Surface(surfaceTexture);
            initCamera();
            if (mCameraId != null) {
                //open the camera
                //打开摄像头
                mCameraManager.openCamera(mCameraId, mStateCallback, mMainHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            mCameraOpenFailed = true;

        }
    }

    @Override
    public void setPreviewSize(int width, int height) {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public int getOrientation() {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isFlipHorizontal() {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            int face = characteristics.get(CameraCharacteristics.LENS_FACING);
            return face == CameraCharacteristics.LENS_FACING_FRONT;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void autoFocus(View surfaceView, MotionEvent event) {
        // Gets the X coordinate of the mapped region
        // 获取映射区域的X坐标
        int x = (int) (event.getX() / surfaceView.getWidth() * 2000) - 1000;
        //Gets the Y coordinate of the mapped region
        // 获取映射区域的Y坐标
        int y = (int) (event.getY() / surfaceView.getWidth() * 2000) - 1000;
        //Create the Rect area
        // 创建Rect区域
        Rect focusArea = new Rect();
        //Take the maximum or minimum value to avoid scope overflow of screen coordinates
        // 取最大或最小值，避免范围溢出屏幕坐标
        focusArea.left = Math.max(x - 100, -1000);
        focusArea.top = Math.max(y - 100, -1000);
        focusArea.right = Math.min(x + 100, 1000);
        focusArea.bottom = Math.min(y + 100, 1000);
        MeteringRectangle[] rectangle = new MeteringRectangle[]{new MeteringRectangle(focusArea, 1)};
        //The focus mode must be set to AUT
        // 对焦模式必须设置为AUTO
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        //AE set AE rect area
        //AE 设置AE的区域
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, rectangle);
        //AF and AE use the same rect here, and the actual rectangular area of AE is slightly larger than AF, so the metering effect is better
        //AF 此处AF和AE用的同一个rect, 实际AE矩形面积比AF稍大, 这样测光效果更好
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, rectangle);
        try {
            //AE/AF The locale continuously sends requests through setRepeatingRequest
            // AE/AF区域设置通过setRepeatingRequest不断发请求
            mSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //Trigger focusing
        //触发对焦
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            //Triggering focus sends a request through capture because the user only needs to trigger focus once after clicking on the screen
            //触发对焦通过capture发送请求, 因为用户点击屏幕后只需触发一次对焦
            mSession.capture(mPreviewBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleFlash(boolean flash) {
        mPreviewBuilder.set(CaptureRequest.FLASH_MODE, flash ? CaptureRequest.FLASH_MODE_TORCH : CaptureRequest.FLASH_MODE_OFF);
        CaptureRequest build = mPreviewBuilder.build();
        try {
            mSession.setRepeatingRequest(build, null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSupportZoom() {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            float[] floats = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
            //A value > 1.0 indicates support
            //值>1.0代表支持
            return floats[0] > 1.0f;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setZoom(int zoomValue) {
        return false;
    }

    @Override
    public int getZoomRange() {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            Float aFloat = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            return aFloat.intValue();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getCurrentZoom() {
        return 0;
    }

    @Override
    public boolean isSupportExpose() {
        return true;
    }

    @Override
    public void setExposureCompensation(int exposureCompensation) {
    }

    @Override
    public int getExposureCompensationRange() {

        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            Range<Integer> integerRange = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
            return integerRange.getUpper() - integerRange.getLower();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getCurrentExpose() {
        return 0;
    }

    @Override
    public void takePicture(ICameraCallBack iCameraCallBack) {
        List<Surface> surfaces = new ArrayList<>();
        Surface surface = mPictureImageReader.getSurface();
        surfaces.add(surface);
        try {
            //Set up a CaptureRequest.Builder with an output Surface
            //设置一个具有输出Surface的CaptureRequest.Builder
            mPictureBuilder = mCameraDevice.
                    createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mPictureBuilder.addTarget(surface);

            //Preview the camera
            //进行相机预览
            mCameraDevice.createCaptureSession(surfaces, mPictureStateCallbackSession, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCameraOpen() {
        return isCameraOpen;
    }

    @Override
    public void releaseCamera() {

    }


    @Override
    public boolean cameraOpenFailed() {
        return mCameraOpenFailed;
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {

            mCameraDevice = camera;
            isCameraOpen = true;
            mCameraOpenFailed = false;
            //Open preview
            //开启预览
            createPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

            mCameraDevice.close();
            mCameraDevice = null;
            isCameraOpen = false;
            mCameraOpenFailed = true;
        }

        @Override
        public void onError(CameraDevice camera, int error) {

            mCameraDevice.close();
            mCameraDevice = null;
            isCameraOpen = false;
            mCameraOpenFailed = true;
        }
    };

    private void createPreview() {

        List<Surface> surfaces = new ArrayList<>();
        surfaces.add(mSurface);
        Surface surface = mPreviewImageReader.getSurface();
        surfaces.add(surface);
        try {
            //Set up a CaptureRequest.Builder with an output Surface
            //设置一个具有输出Surface的CaptureRequest.Builder
            mPreviewBuilder = mCameraDevice.
                    createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(mSurface);
            mPreviewBuilder.addTarget(surface);
            //Preview the camera
            //进行相机预览
            mCameraDevice.createCaptureSession(surfaces, mPreviewStateCallbackSession, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mPreviewStateCallbackSession = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            mSession = session;
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            try {
                //send request
                //发送请求
                session.setRepeatingRequest(mPreviewBuilder.build(),
                        null, null);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.d(TAG, "onConfigureFailed: ");
        }
    };

    private CameraCaptureSession.StateCallback mPictureStateCallbackSession = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {

            mPictureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //mPictureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90);
            //mPictureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,CaptureRequest.NOISE_REDUCTION_MODE_FAST);
            try {
                //发送请求
                session.capture(mPictureBuilder.build(),
                        mCaptureCallback, null);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.d(TAG, "onConfigureFailed: ");
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull
                CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull
                CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.i(TAG, "capture complete");
            createPreview();
        }
    };

    /**
     * Compares two {@code Size}s based on their areas.
     */
    class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            //我们在这里强制转换以确保乘法不会溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

//
//    @Override
//    public void run() {
//        initCamera();
//    }

    private byte[] getNv21Data(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        int stride = planes[0].getRowStride();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uvBuffer = planes[1].getBuffer();
        ByteBuffer vuBuffer = planes[2].getBuffer();

        int uvRemaining = uvBuffer.remaining();
        int vuRemaining = vuBuffer.remaining();

        int ySize = width * height;
        int vuSize = width * height / 2;
        byte[] yData = new byte[ySize];
        byte[] vuData = new byte[vuSize];
        if (width < stride) {
            for (int i = 0; i < height; i++) {
                yBuffer.position(i * stride);
                yBuffer.get(yData, i * width, width);

                if (i < height / 2) {
                    vuBuffer.position(i * stride);
                    if (i == (height / 2) - 1) {
                        vuBuffer.get(vuData, i * width, width - 1);
                        uvBuffer.position(uvRemaining - 1);
                        uvBuffer.get(vuData, vuData.length - 1, 1);
                    } else {
                        vuBuffer.get(vuData, i * width, width);
                    }
                }
            }
        } else {
            yBuffer.get(yData);
            vuBuffer.get(vuData, 0, vuRemaining);
            uvBuffer.position(uvRemaining - 1);
            uvBuffer.get(vuData, vuData.length - 1, 1);
        }

        /*
         * swap uv to become NV21.
         * depends on the data format get from ImageReader
         */
            /*byte temp;
            for(int index = 0; index < vuData.length; index+=2) {
                temp = vuData[index];
                vuData[index] = vuData[index + 1];
                vuData[index + 1] = temp;
            }*/

        byte[] dataNv21 = new byte[ySize + vuSize];
        System.arraycopy(yData, 0, dataNv21, 0, ySize);
        System.arraycopy(vuData, 0, dataNv21, ySize, vuSize);

        return dataNv21;
    }

}