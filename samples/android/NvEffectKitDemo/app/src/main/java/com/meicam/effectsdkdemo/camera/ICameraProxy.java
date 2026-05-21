package com.meicam.effectsdkdemo.camera;

import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.View;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/11/4 15:57
 * @des : camera exposes the interface to the outside
 * @des : camera对外暴露接口
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public interface ICameraProxy {

    void openCamera(int cameraId);

    void startPreview(SurfaceTexture surfaceTexture, ICameraCallBack iCameraCallBack);

    void setPreviewSize(int width,int height);

    void stopPreview();

    int getOrientation();

    boolean isFlipHorizontal();

    void autoFocus(View surfaceView, MotionEvent event);

    void toggleFlash(boolean flash);

    boolean isSupportZoom();

    boolean setZoom(int zoomValue);

    int getZoomRange();

    int getCurrentZoom();

    boolean isSupportExpose();

    void setExposureCompensation(int exposureCompensation);

    int getExposureCompensationRange();

    int getCurrentExpose();

    void takePicture(ICameraCallBack iCameraCallBack);

    boolean isCameraOpen();

    void releaseCamera();

    boolean cameraOpenFailed();

}
