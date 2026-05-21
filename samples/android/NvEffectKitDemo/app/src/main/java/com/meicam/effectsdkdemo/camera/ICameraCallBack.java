package com.meicam.effectsdkdemo.camera;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/11/4 16:16
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public interface ICameraCallBack {

    void onCameraPreviewCallBack(byte[] data);

    void onCameraTakePictureCallBack(byte[] data,int width,int height);
}
