package com.meicam.effectsdkdemo.controller;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.camera.RenderCameraUtil;
import com.meicam.effectsdkdemo.view.BeautyFilterView;
import com.meicam.effectsdkdemo.view.MsPropView;
import com.meicam.effectsdkdemo.view.ZoomExposeView;
import com.meishe.libmakeup.view.MakeUpView;
import com.meishe.libmsbeauty.view.MsBeautyView;

import androidx.core.content.ContextCompat;

public class MainController {
    private ZoomExposeView mZoomExposeView;
    private MsBeautyView mBeautyView;
    private MakeUpView mMakeUpView;
    private MsPropView mMsPropView;
    private BeautyFilterView mFilterView;

    public MainController() {
    }

    public ZoomExposeView initZoomExposeView(Context context, int type, RenderCameraUtil renderCameraUtil) {
        if (null == mZoomExposeView) {
            mZoomExposeView = new ZoomExposeView(context);
        }
        mZoomExposeView.setOnIntensityListener(progress -> {
            if (null == renderCameraUtil) {
                return;
            }
            if (type == ZoomExposeView.TYPE_ZOOM) {
                renderCameraUtil.setZoom(progress);
            } else {
                renderCameraUtil.setExposureCompensation(progress - renderCameraUtil.getExposureCompensationRange() / 2);
            }
        });
        if (null != renderCameraUtil) {
            if (type == ZoomExposeView.TYPE_ZOOM) {
                int zoomRange = renderCameraUtil.getZoomRange();
                mZoomExposeView.setMax(zoomRange);
                mZoomExposeView.setProgress(renderCameraUtil.getCurrentZoom());
            } else {
                int exposureCompensationRange = renderCameraUtil.getExposureCompensationRange();
                int current = renderCameraUtil.getCurrentExpose();
                mZoomExposeView.setMax(exposureCompensationRange);
                mZoomExposeView.setProgress(current + exposureCompensationRange / 2);
            }
        }
        return mZoomExposeView;
    }

    public MsBeautyView initBeautyView(Context context) {
        if (null == mBeautyView) {
            mBeautyView = new MsBeautyView(context);
        }
        return mBeautyView;
    }

    public void closeBeautyView() {
        if ((null != mBeautyView) && (mBeautyView.getVisibility() == View.VISIBLE)) {
            mBeautyView.close();
        }
    }


    public MakeUpView initMakeupView(Context context) {
        if (null == mMakeUpView) {
            mMakeUpView = new MakeUpView(context);
        }
        return mMakeUpView;
    }

    public MsPropView initPropView(Context context) {
        if (null == mMsPropView) {
            mMsPropView = new MsPropView(context);
        }
        return mMsPropView;
    }

    public BeautyFilterView initFilterView(Context context) {
        if (null == mFilterView) {
            mFilterView = new BeautyFilterView(context);
        }
        return mFilterView;
    }

    public void onResume() {
        if (null != mBeautyView) {
            mBeautyView.onResume();
        }
        if (null != mMsPropView) {
            mMsPropView.onResume();
        }
        if (null != mFilterView) {
            mFilterView.onResume();
        }
        if (null != mMakeUpView) {
            mMakeUpView.onResume();
        }
    }

    public void showCaptureDialogView(AlertDialog dialog, View view, View mStartLayout) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mStartLayout, "translationY", mStartLayout.getHeight());
        objectAnimator.setDuration(500);
        objectAnimator.start();
        dialog.show();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(dialog.getContext(), R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
    }

    public void closeCaptureDialogView(AlertDialog dialog, View mStartLayout) {
        dialog.dismiss();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mStartLayout, "translationY", 0);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }
}
