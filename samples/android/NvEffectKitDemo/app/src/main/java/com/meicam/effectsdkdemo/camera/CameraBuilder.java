package com.meicam.effectsdkdemo.camera;

import android.content.Context;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/11/8 10:29
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class CameraBuilder {
    private static final int CAMERA_1 = 0;
    private static final int CAMERA_2 = 1;

    public static ICameraProxy buildCameraByType(Context context,int cameraType){
        if(cameraType == CAMERA_2){
            return  Camera2Proxy.createCamera(context);
        }
        return new CameraProxy();
    }
}
