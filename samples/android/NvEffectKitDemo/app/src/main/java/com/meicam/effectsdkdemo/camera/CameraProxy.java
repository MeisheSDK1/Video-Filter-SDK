package com.meicam.effectsdkdemo.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cdv
 */
public class CameraProxy implements ICameraProxy {

    private static final String TAG = "CameraProxy";
    private boolean isDebug = true;

    private int mCameraId;
    private Camera mCamera;
    private boolean isCameraOpen = false;
    private boolean mCameraOpenFailed = false;
    private CameraInfo mCameraInfo = new CameraInfo();
    private PreviewCallback previewcallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (null != mICameraCallBack) {
                mICameraCallBack.onCameraPreviewCallBack(data);
            }
        }
    };
    private ICameraCallBack mICameraCallBack;

    public Camera getCamera() {
        return mCamera;
    }

    public void openCamera(int cameraId) {
        try {
            releaseCamera();
            mCamera = Camera.open(cameraId);
            mCamera.getParameters();
            mCameraId = cameraId;
            Camera.getCameraInfo(cameraId, mCameraInfo);
            mCamera.setDisplayOrientation(90);
            setDefaultParameters();
            isCameraOpen = true;
            mCameraOpenFailed = false;
        } catch (Exception e) {
            mCameraOpenFailed = true;
            mCamera = null;
            Log.i(TAG, "openCamera fail msg=" + e.getMessage());
        }
    }


    public void startPreview(SurfaceTexture surfaceTexture, ICameraCallBack iCameraCallBack) {
        this.mICameraCallBack = iCameraCallBack;
        try {
            if (mCamera == null) {
                return;
            }
            mCamera.setPreviewTexture(surfaceTexture);
            if (previewcallback != null && mCamera != null) {
                mCamera.setPreviewCallback(previewcallback);
            }
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    @Override
    public int getOrientation() {
        if (mCameraInfo == null) {
            return 0;
        }
        return mCameraInfo.orientation;
    }

    @Override
    public boolean isFlipHorizontal() {
        if (mCameraInfo == null) {
            return false;
        }
        return mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
    public void takePicture(final ICameraCallBack iCameraCallBack) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (null != iCameraCallBack) {
                        iCameraCallBack.onCameraTakePictureCallBack(data, 0, 0);
                    }
                }
            });
        }
    }

    @Override
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void setDefaultParameters() {
        Parameters parameters = mCamera.getParameters();
        Log.e(TAG, "parameters: " + parameters.flatten());
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        if ((flashModes != null) && flashModes.contains(Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }

        Point previewSize = getSuitablePreviewSize();
        //	parameters.setPreviewSize(previewSize.x, previewSize.y);
        parameters.setPreviewSize(640, 480);
        Point pictureSize = getSuitablePictureSize();
        parameters.setPictureSize(pictureSize.x, pictureSize.y);

        mCamera.setParameters(parameters);
    }

    @Override
    public void setPreviewSize(int width, int height) {
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(width, height);
        if (mCamera == null) {
            return;
        }
        mCamera.setParameters(parameters);
    }

    private Point getSuitablePreviewSize() {
        Point defaultsize = new Point(1920, 1080);
        if (mCamera != null) {
            List<Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
            for (Size s : sizes) {
                if ((s.width == defaultsize.x) && (s.height == defaultsize.y)) {
                    return defaultsize;
                }
            }
            return new Point(640, 480);
        }
        return null;
    }

    private Point getSuitablePictureSize() {
        Point defaultsize = new Point(4608, 3456);
        //	Point defaultsize = new Point(3264, 2448);
        if (mCamera != null) {
            Point maxSize = new Point(0, 0);
            List<Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
            for (Size s : sizes) {
                if ((s.width == defaultsize.x) && (s.height == defaultsize.y)) {
                    return defaultsize;
                }
                if (maxSize.x < s.width) {
                    maxSize.x = s.width;
                    maxSize.y = s.height;
                }
            }
            return maxSize;
        }
        return null;
    }


    @Override
    public boolean cameraOpenFailed() {
        return mCameraOpenFailed;
    }

    @Override
    public boolean isCameraOpen() {
        return isCameraOpen;
    }

    @Override
    public void toggleFlash(boolean state) {
        Parameters parameters = mCamera.getParameters();
        if (state) {
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        } else {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }
        if (mCamera == null) {
            return;
        }
        mCamera.setParameters(parameters);
    }

    @Override
    public boolean isSupportZoom() {
        Parameters parameters = mCamera.getParameters();
        return parameters.isZoomSupported();
    }

    @Override
    public int getZoomRange() {
        if (!isSupportZoom()) {
            return 0;
        }
        Parameters parameters = mCamera.getParameters();
        //Too much zoom and the preview goes black
        // 变焦太大 预览画面变黑
        return parameters.getMaxZoom() / 3;
    }

    @Override
    public int getCurrentZoom() {
        if (!isSupportZoom()) {
            return 0;
        }
        Parameters parameters = mCamera.getParameters();
        return parameters.getZoom();
    }

    @Override
    public boolean setZoom(int zoomValue) {
        try {
            Parameters parameters = mCamera.getParameters();
            parameters.setZoom(zoomValue);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        // create Camera.Area
        // 创建Camera.Area
        Parameters parameters = mCamera.getParameters();
        Camera.Area cameraArea = new Camera.Area(focusArea, 1000);
        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        if (parameters.getMaxNumMeteringAreas() > 0) {
            meteringAreas.add(cameraArea);
            focusAreas.add(cameraArea);
        }
        //Set the focus mode
        // 设置对焦模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //Set the focus area
        // 设置对焦区域
        parameters.setFocusAreas(focusAreas);
        // Set the metering area
        // 设置测光区域
        parameters.setMeteringAreas(meteringAreas);
        try {
            // Each time you focus, you need to unfocus
            // 每次对焦前，需要先取消对焦
            mCamera.cancelAutoFocus();
            // Setting camera parameters
            // 设置相机参数
            mCamera.setParameters(parameters);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Log.d(TAG, "onAutoFocus: success");
                    }
                }
            });
        } catch (Exception e) {
            // 开启对焦
            Log.e(TAG, "autoFocus: error");
        }
    }

    @Override
    public boolean isSupportExpose() {
        Camera.Parameters parameters = mCamera.getParameters();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();
        int minExposureCompensation = parameters.getMinExposureCompensation();
        if (minExposureCompensation >= maxExposureCompensation) {
            return false;
        }
        return true;
    }

    @Override
    public void setExposureCompensation(int exposureCompensation) {
        Camera.Parameters parameters = mCamera.getParameters();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();
        int minExposureCompensation = parameters.getMinExposureCompensation();
        if (minExposureCompensation >= maxExposureCompensation) {
            return;
        }
        parameters.setExposureCompensation(exposureCompensation);
        mCamera.setParameters(parameters);
    }

    @Override
    public int getExposureCompensationRange() {
        Camera.Parameters parameters = mCamera.getParameters();
        return parameters.getMaxExposureCompensation() * 2;
    }

    @Override
    public int getCurrentExpose() {
        Camera.Parameters parameters = mCamera.getParameters();
        return parameters.getExposureCompensation();

    }

}
