package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @Author: lpf
 * @CreateDate: 2022/10/12 上午10:49
 * @Description:
 */
public class DrawRectParentView extends RelativeLayout {
    private static final String TAG = "DrawRectParentView";
    private static final float MAX_SCALE = 800f;
    private static final float MIN_SCALE = 0.00001f;
    private ScaleGestureDetector mScaleDetector;
    private float mScale = 1.0f;

    public DrawRectParentView(Context context) {
        super(context);
        init(context);
    }

    public DrawRectParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawRectParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    boolean isDoubleFlinger = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            isDoubleFlinger = true;
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            isDoubleFlinger = false;
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isDoubleFlinger = false;
        }
        return isDoubleFlinger;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isDoubleFlinger = false;
        }
        mScaleDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();

            if (mScale > MAX_SCALE) {
                if (mScaleFactor < 1) {
                    mScale *= mScaleFactor;
                }
            } else if (mScale < MIN_SCALE) {
                if (mScaleFactor > 1) {
                    mScale *= mScaleFactor;
                }
            } else {
                mScale *= mScaleFactor;
            }

            if (mScale > MAX_SCALE || mScale < MIN_SCALE) {
                return false;
            }
            if (mOnRectScaleListener!=null){
                mOnRectScaleListener.onScale(DrawRectParentView.this,mScale);
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

    private OnDoubleFlingerScaleListener mOnRectScaleListener;

    public void setOnRectScaleListener(OnDoubleFlingerScaleListener mOnRectScaleListener) {
        this.mOnRectScaleListener = mOnRectScaleListener;
    }

    public interface OnDoubleFlingerScaleListener {
        void onScale(View view, float onScale);
    }

    public void setScale(float mScale) {
        this.mScale = mScale;
    }
}
