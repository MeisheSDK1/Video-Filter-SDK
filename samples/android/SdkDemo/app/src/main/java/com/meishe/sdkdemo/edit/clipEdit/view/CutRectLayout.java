package com.meishe.sdkdemo.edit.clipEdit.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * author：yangtailin on 2020/7/25 14:48
 */
public class CutRectLayout extends RelativeLayout {
    private Context mContext;
    private CutRectViewEx mRectView;
    private boolean mIsTwoFingerEvent = false;
    private OnTransformListener mOnTransformListener;
    private float mOldTouchX;
    private float mOldTouchY;
    private double mTwoFingerStartLength;
    private PointF mTwoFingerOldPoint = new PointF();
    private final static int ONE_FINGER = 1;
    private final static int TWO_FINGER = 2;
    private int mTouchRect;

    public CutRectLayout(Context context) {
        super(context);
    }

    public CutRectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        mRectView = new CutRectViewEx(mContext);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mRectView, layoutParams);
        mRectView.setOnTransformListener(new CutRectViewEx.OnTransformListener() {
            @Override
            public void onTrans(float deltaX, float deltaY) {

            }

            @Override
            public void onScaleAndRotate(float scale, float degree) {

            }

            @Override
            public void onTransEnd(float scale, float[] size) {
                if (mOnTransformListener != null) {
                    mOnTransformListener.onTransEnd(scale, size);
                }
            }

            @Override
            public void onRectMoved(float scale, Point distance,  Point anchor) {
                if (mOnTransformListener != null) {
                    mOnTransformListener.onRectMoved(scale, distance, anchor);
                }
            }

            @Override
            public boolean canMove(Point anchor) {
                if (mOnTransformListener != null) {
                    return mOnTransformListener.canMove(anchor);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int pointerCount = ev.getPointerCount();
        if (pointerCount == TWO_FINGER) {
            return true;
        }
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchRect = getDrawRectView().getTouchRect(ev);
        }
        return (mTouchRect < 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount > TWO_FINGER) {
            return false;
        }

        if (((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) && (pointerCount == ONE_FINGER)) {
            mIsTwoFingerEvent = false;
        }

        if (pointerCount == TWO_FINGER) {
            mIsTwoFingerEvent = true;
            return twoFingerTouch(event);
        } else {
            return oneFingerTouch(event);
        }
    }

    public int getRectWidth() {
        return mRectView.getRectWidth();
    }

    public int getRectHeight() {
        return mRectView.getRectHeight();
    }

    public CutRectViewEx getDrawRectView() {
        return mRectView;
    }

    public int getDrawRectViewLeft() {
        return mRectView.getRectLeft();
        //return  (int) ((getWidth() - mRectView.getRectWidth()) * 1.0F / 2) + mRectView.getPadding();
    }

    public int getDrawRectViewTop() {
        return mRectView.getRectTop();
        //return  (int) ((getHeight() - mRectView.getRectHeight()) * 1.0F / 2) + mRectView.getPadding();
    }

    public void setWidthHeightRatio(float ratio) {
       mRectView.setWidthHeightRatio(ratio);
    }

    public void setDrawRectSize(int width, int height) {
        mRectView.setDrawRectSize(width, height);
    }

    private boolean oneFingerTouch(MotionEvent event) {
        if (mIsTwoFingerEvent) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTwoFingerEvent = false;
                mOldTouchX = 0;
                mOldTouchY = 0;
                if (mOnTransformListener != null) {
                    mOnTransformListener.onTransEnd(-1, null);
                }
            }
            return false;
        }
        int action = event.getAction();
        float touchX = event.getRawX();
        float touchY = event.getRawY();
        if (action == MotionEvent.ACTION_MOVE) {
            if (mOnTransformListener != null) {
                if (mOldTouchX != 0) {
                    mOnTransformListener.onTrans(mOldTouchX - touchX, mOldTouchY - touchY);
                }
            }
            mOldTouchX = touchX;
            mOldTouchY = touchY;
        } else if (action == MotionEvent.ACTION_UP) {
            if (mOnTransformListener != null) {
                if (mOldTouchX != 0) {
                    mOnTransformListener.onTransUp();
                }
            }
            mOldTouchX = 0;
            mOldTouchY = 0;
        }
        return true;
    }

    private boolean twoFingerTouch(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
            float xLen = event.getX(0) - event.getX(1);
            float yLen = event.getY(0) - event.getY(1);
            mTwoFingerStartLength = Math.sqrt((xLen * xLen) + (yLen * yLen));
            mTwoFingerOldPoint.set(xLen, yLen);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            float xLen = event.getX(0) - event.getX(1);
            float yLen = event.getY(0) - event.getY(1);
            float oldDegree = (float) Math.toDegrees(Math.atan2(mTwoFingerOldPoint.x, mTwoFingerOldPoint.y));
            float newDegree = (float) Math.toDegrees(Math.atan2((event.getX(0) - event.getX(1)),
                    (event.getY(0) - event.getY(1))));
            double twoFingerEndLength = Math.sqrt(xLen * xLen + yLen * yLen);

            float scalePercent = (float) (twoFingerEndLength / mTwoFingerStartLength);
            float degree = newDegree - oldDegree;

            if (mOnTransformListener != null) {
                mOnTransformListener.onScaleAndRotate(scalePercent, degree);
            }
            mTwoFingerStartLength = twoFingerEndLength;
            mTwoFingerOldPoint.set(xLen, yLen);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mOnTransformListener != null) {
                mOnTransformListener.onTransEnd(-1, null);
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnTransformListener(OnTransformListener listener) {
        mOnTransformListener = listener;
    }

    public void setLimitRect(Rect clipRect) {
        mRectView.setLimitRect(clipRect);
    }

    public interface OnTransformListener {
        void onTrans(float deltaX, float deltaY);

        void onTransUp();

        void onScaleAndRotate(float scale, float degree);

        void onTransEnd(float scale, float[] size);

        void onRectMoved(float scale, Point distance, Point anchor);

        boolean canMove(Point anchor);
    }
}
