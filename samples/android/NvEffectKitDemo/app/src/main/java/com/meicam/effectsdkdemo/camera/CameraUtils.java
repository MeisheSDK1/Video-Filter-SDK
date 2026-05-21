package com.meicam.effectsdkdemo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/5/25 15:06
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class CameraUtils {
    /**
     * The data in the calculation preview shows how much Angle is rotated (the target direction is clockwise upward).
     * The app does not enable the rotation of the horizontal and vertical screen of the phone, so it only needs to calculate once
     * 计算 预览的数据展示旋转了多大角度，(目标方向顺时针朝上) app没开启手机横竖屏的旋转所以只计算一次即可
     *
     * @param context  上下文 context
     * @param cameraId 相机id cameraid
     * @return 旋转角度 rotation
     * Calculation specification
     * 1.Front camera rotation Angle 270, (front camera picture is actually mirror effect)
     * 2.Rear camera camera rotation Angle 90,
     * 3.camera Angle + screen rotation Angle calculates how many degrees of rotation to the positive Angle.
     * 计算说明
     * 1.前置摄像头相机旋转角度 270，（前置摄像头画面实际上是镜像效果）
     * 2.后置摄像头相机旋转角度 90,
     * 3.camera角度+屏幕的旋转角度 计算出旋转多大度数到正角度。
     */
    public static int getDisplayOrientation(Context context, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
