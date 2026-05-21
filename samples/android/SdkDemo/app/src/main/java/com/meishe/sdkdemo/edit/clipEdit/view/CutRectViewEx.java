package com.meishe.sdkdemo.edit.clipEdit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * CutRectViewEx class
 *
 * @author yangtailin
 * @date 2020/7/1 17:15
 */
public class CutRectViewEx extends View {
    private final static int RECT_L_T = 1;
    private final static int RECT_L_B = 2;
    private final static int RECT_R_T = 3;
    private final static int RECT_R_B = 4;
    private Rect mDrawRect = new Rect();
    private int mTouchRect = -1;
    private Paint mPaint;
    private Paint mCornerPaint;
    private final static int ANGEL_LENGTH = 30;
    private final static int STROKE_WIDTH = 4;
    private final static int PADDING = 0;
    private final static int MIN_SIZE = 1;
    private int mPadding = PADDING;
    private OnTransformListener mOnTransformListener;
    private float mOldTouchX = 0;
    private float mOldTouchY = 0;
    private int mTempLeft = 0;
    private int mTempRight = 0;
    private int mTempTop = 0;
    private int mTempBottom = 0;

    private final static int ONE_FINGER = 1;
    private final static int TWO_FINGER = 2;
    private boolean mIsTwoFingerEvent = false;
    private double mTwoFingerStartLength;
    private PointF mTwoFingerOldPoint = new PointF();
    private Point mTouchPoint = new Point();
    /**
     * 宽高比，如果是-1，代表自由宽高比
     * Aspect ratio, if it's negative 1, it's the free aspect ratio
     */
    private float mWidthHeightRatio = -1;
    /**
     * 触摸区域的范围
     * The range of the touch area
     */
    private final static int TOUCH_RECT_SIZE = 100;

    private int mRectWidth;
    private int mRectHeight;
    private Path mPath = new Path();
    private Rect mLimitRect;

    public CutRectViewEx(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        mCornerPaint = new Paint();
        mCornerPaint.setColor(Color.WHITE);
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(6);
        initView();
    }

    public CutRectViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        mCornerPaint = new Paint();
        mCornerPaint.setColor(Color.WHITE);
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(6);
        initView();
    }


    private void initView() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        //绘制边框 Draw border
        float halfStroke = mPaint.getStrokeWidth() / 2F;
        mPath.moveTo(mDrawRect.left + halfStroke, mDrawRect.top + halfStroke);
        mPath.lineTo(mDrawRect.right - halfStroke, mDrawRect.top + halfStroke);
        mPath.lineTo(mDrawRect.right - halfStroke, mDrawRect.bottom - halfStroke);
        mPath.lineTo(mDrawRect.left + halfStroke, mDrawRect.bottom - halfStroke);
        mPath.lineTo(mDrawRect.left + halfStroke, mDrawRect.top + halfStroke);
        canvas.drawPath(mPath, mPaint);

        //绘制中线 Draw center line
        int width = mDrawRect.right - mDrawRect.left;
        int height = mDrawRect.bottom - mDrawRect.top;
        //竖线 Vertical line
        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3, mDrawRect.top);
        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3, mDrawRect.bottom);
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.top);
        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.bottom);
        canvas.drawPath(mPath, mPaint);

        //横线 Horizontal line
        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3 * 2);
        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3 * 2);
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3);
        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3);
        canvas.drawPath(mPath, mPaint);

        int angelLength = ANGEL_LENGTH;
        if (angelLength > width) {
            angelLength = width;
        }
        if (angelLength > height) {
            angelLength = height;
        }
        int strokeWidth = STROKE_WIDTH;
        //绘制左上角 Draw top left corner
        mPath.reset();
        mPath.moveTo(mDrawRect.left + angelLength + strokeWidth / 2, mDrawRect.top + strokeWidth / 2F);
        mPath.lineTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.top + strokeWidth / 2F);
        mPath.lineTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.top + angelLength + +strokeWidth / 2F);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制右上角 Draw top right corner
        mPath.moveTo(mDrawRect.right - angelLength - strokeWidth / 2F, mDrawRect.top + strokeWidth / 2F);
        mPath.lineTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.top + strokeWidth / 2F);
        mPath.lineTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.top + angelLength + strokeWidth / 2F);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制右下角 Draw bottom right
        mPath.moveTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2 - angelLength);
        mPath.lineTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2F);
        mPath.lineTo(mDrawRect.right - strokeWidth / 2 - angelLength, mDrawRect.bottom - strokeWidth / 2F);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制左下角 Draw bottom left corner
        mPath.moveTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2F - angelLength);
        mPath.lineTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2F);
        mPath.lineTo(mDrawRect.left + strokeWidth / 2F + angelLength, mDrawRect.bottom - strokeWidth / 2F);
        canvas.drawPath(mPath, mCornerPaint);
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

        getParent().requestDisallowInterceptTouchEvent(true);
        if (pointerCount == TWO_FINGER) {
//            oneFingerActionUp();
            return true;
        } else {
            return oneFingerTouch(event);
        }
    }

    public void setWidthHeightRatio(float ratio) {
        this.mWidthHeightRatio = ratio;
    }

    private boolean oneFingerTouch(MotionEvent event) {
        if (mIsTwoFingerEvent) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTwoFingerEvent = false;
                mOldTouchX = 0;
                mOldTouchY = 0;
            }
            return false;
        }
        int action = event.getAction();
        float touchX = event.getRawX();
        float touchY = event.getRawY();
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchRect = getTouchRect(event);
            mOldTouchX = event.getRawX();
            mOldTouchY = event.getRawY();
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            int eventX = (int) event.getX();
            int eventY = (int) event.getY();
            boolean canMove = true;
            if (mTouchRect > 0) {
                mTouchPoint.x = Math.abs(mDrawRect.right - mDrawRect.left);
                mTouchPoint.y = Math.abs(mDrawRect.bottom - mDrawRect.top);
                canMove = canMove(mTouchPoint);
            }

            if (mTouchRect == RECT_L_T) {
                mDrawRect.right = getWidth() - (getWidth() - getRectWidth()) / 2 - mPadding;
                mDrawRect.bottom = getHeight() - (getHeight() - getRectHeight()) / 2 - mPadding;
                if (mWidthHeightRatio > 0) {
                    mDrawRect.left = mDrawRect.right - (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                    mDrawRect.top = eventY;
                } else {
                    mDrawRect.left = eventX;
                }
                mDrawRect.top = eventY;
                correctRect();
            } else if (mTouchRect == RECT_L_B) {
                mDrawRect.top = (getHeight() - getRectHeight()) / 2 + mPadding;
                mDrawRect.right = getWidth() - (getWidth() - getRectWidth()) / 2 - mPadding;

                if (mWidthHeightRatio > 0) {
                    mDrawRect.left = mDrawRect.right - (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                } else {
                    mDrawRect.left = eventX;
                }
                mDrawRect.bottom = eventY;
                correctRect();
            } else if (mTouchRect == RECT_R_T) {
                mDrawRect.left = (getWidth() - getRectWidth()) / 2 + mPadding;
                mDrawRect.bottom = getHeight() - (getHeight() - getRectHeight()) / 2 - mPadding;

                if (mWidthHeightRatio > 0) {
                    mDrawRect.right = mDrawRect.left + (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                } else {
                    mDrawRect.right = eventX;
                }
                mDrawRect.top = eventY;

                correctRect();
            } else if (mTouchRect == RECT_R_B) {

                mDrawRect.left = (getWidth() - getRectWidth()) / 2 + mPadding;
                mDrawRect.top = (getHeight() - getRectHeight()) / 2 + mPadding;

                if (mWidthHeightRatio > 0) {
                    mDrawRect.right = mDrawRect.left + (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                } else {
                    mDrawRect.right = eventX;
                }
                mDrawRect.bottom = eventY;
                correctRect();
            }
            if (mTouchRect > 0) {
                mTouchPoint.x = Math.abs(mDrawRect.right - mDrawRect.left);
                mTouchPoint.y = Math.abs(mDrawRect.bottom - mDrawRect.top);
                if (canMove && canMove(mTouchPoint)) {
                    //记录临时Rect 数值 Record the temporary rect value.
                    mTempLeft = mDrawRect.left;
                    mTempRight = mDrawRect.right;
                    mTempTop = mDrawRect.top;
                    mTempBottom = mDrawRect.bottom;
                    invalidate();
                }
            } else {
                if (mOnTransformListener != null) {
                    if (mOldTouchX != 0) {
                        mOnTransformListener.onTrans(mOldTouchX - touchX, mOldTouchY - touchY);
                    }
                }
                mOldTouchX = touchX;
                mOldTouchY = touchY;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (mTempTop != 0) {
                mDrawRect.top = mTempTop;
                mTempTop = 0;
            }
            if (mTempBottom != 0) {
                mDrawRect.bottom = mTempBottom;
                mTempBottom = 0;
            }
            if (mTempLeft != 0) {
                mDrawRect.left = mTempLeft;
                mTempLeft = 0;
            }
            if (mTempRight != 0) {
                mDrawRect.right = mTempRight;
                mTempRight = 0;
            }
            oneFingerActionUp();
        }
        return super.onTouchEvent(event);
    }

    private void oneFingerActionUp() {
        if (mWidthHeightRatio > 0) {
            if (mTouchRect > 0) {
                float scale = getRectWidth() * 1.0F / Math.abs(mDrawRect.right - mDrawRect.left);
                float scaleH = getRectHeight() * 1.0F / Math.abs(mDrawRect.bottom - mDrawRect.top);
                if (scale > scaleH) {
                    scale = scaleH;
                }
                if (scale < 1.0F) {
                    scale = 1.0F;
                }
                Point anchorBefore = getAnchorOnScreen();
                setDrawRectSize(mRectWidth, mRectHeight);
                invalidate();
                Point anchorAfter = getAnchorOnScreen();

                if (mOnTransformListener != null) {
                    Point point = new Point();
                    point.x = (anchorAfter.x - anchorBefore.x);
                    point.y = (anchorAfter.y - anchorBefore.y);
                    mOnTransformListener.onRectMoved(scale, point, anchorBefore);
                }
            }
        } else {
            scaleAndMoveRectToCenter();
        }
        mOldTouchX = 0;
        mOldTouchY = 0;
    }

    /**
     * 纠正边界
     * Correcting boundary
     */
    private void correctRect() {
        if (mLimitRect != null) {
            int[] location = new int[2];
            getLocationInWindow(location);
            int rectLeft = mLimitRect.left - location[0];
            if (mDrawRect.left < rectLeft) {
                mDrawRect.left = rectLeft;
            }
            if (mDrawRect.right < rectLeft) {
                mDrawRect.right = rectLeft;
            }

            int rectRight = mLimitRect.right - location[0];

            if (mDrawRect.left > rectRight) {
                mDrawRect.left = rectRight;
            }
            if (mDrawRect.right > rectRight) {
                mDrawRect.right = rectRight;
            }

            int rectTop = mLimitRect.top - location[1];

            if (mDrawRect.top < rectTop) {
                mDrawRect.top = rectTop;
            }
            if (mDrawRect.bottom < rectTop) {
                mDrawRect.bottom = rectTop;
            }

            int rectBottom = mLimitRect.bottom - location[1];
            if (mDrawRect.top > rectBottom) {
                mDrawRect.top = rectBottom;
            }
            if (mDrawRect.bottom > rectBottom) {
                mDrawRect.bottom = rectBottom;
            }
        }

        if (mDrawRect.top < mPadding) {
            mDrawRect.top = mPadding;
        }
        if (mDrawRect.left < mPadding) {
            mDrawRect.left = mPadding;
        }

        if (mDrawRect.right > getWidth() - mPadding) {
            mDrawRect.right = getWidth() - mPadding;
        }
        if (mDrawRect.right < mPadding) {
            mDrawRect.right = mPadding;
        }

        if (mDrawRect.bottom > getHeight() - mPadding) {
            mDrawRect.bottom = getHeight() - mPadding;
        }

        if (mDrawRect.bottom < mPadding) {
            mDrawRect.bottom = mPadding;
        }

        //如果左边跨过右边或右边跨过左边，交换 If left crosses right or right crosses left, switch
        if (mDrawRect.left > mDrawRect.right) {
            int temp = mDrawRect.left;
            mDrawRect.left = mDrawRect.right;
            mDrawRect.right = temp;
        }
        //如果上边跨过下边或下边跨过上边，交换 If the top crosses the bottom or the bottom crosses the top, swap
        if (mDrawRect.top > mDrawRect.bottom) {
            int temp = mDrawRect.top;
            mDrawRect.top = mDrawRect.bottom;
            mDrawRect.bottom = temp;
        }

        //保留左右上下最小间距 Keep the minimum distance between left and right
        if (mDrawRect.top == mDrawRect.bottom) {
            if (mDrawRect.top - MIN_SIZE > mPadding) {
                mDrawRect.top -= MIN_SIZE;
            } else {
                mDrawRect.bottom += MIN_SIZE;
            }
        }

        if (mDrawRect.left == mDrawRect.right) {
            if (mDrawRect.left - MIN_SIZE > mPadding) {
                mDrawRect.left -= MIN_SIZE;
            } else {
                mDrawRect.right += MIN_SIZE;
            }
        }

    }

    private void scaleAndMoveRectToCenter() {
        Point anchorBefore = getAnchorOnScreen();
        int width = mDrawRect.right - mDrawRect.left;
        int height = mDrawRect.bottom - mDrawRect.top;
        Point size = getFreeCutRectSize(width, height);
        setDrawRectSize(size.x, size.y);
        Point anchorAfter = getAnchorOnScreen();

        if (mOnTransformListener != null) {
            float scale = getRectWidth() * 1.0F / width;
            float scaleH = getRectHeight() * 1.0F / height;
            if (scale < scaleH) {
                scale = scaleH;
            }
            Point point = new Point();
            point.x = (anchorAfter.x - anchorBefore.x);
            point.y = (anchorAfter.y - anchorBefore.y);
            mOnTransformListener.onRectMoved(scale, point, anchorBefore);
        }
    }

    private Point getFreeCutRectSize(int rectWidth, int rectHeight) {
        float ratio = rectWidth * 1.0F / rectHeight;
        int width = getWidth();
        int height = getHeight();
        float layoutRatio = width * 1.0F / height;
        Point rectSize = new Point();
        if (ratio > layoutRatio) {
            rectSize.x = width;
            rectSize.y = (int) (width * 1.0F / ratio);
        } else {
            rectSize.y = height;
            rectSize.x = (int) (height * ratio);
        }
        return rectSize;
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
            float newDegree = (float) Math.toDegrees(Math.atan2((event.getX(0) - event.getX(1)), (event.getY(0) - event.getY(1))));
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
                float scale = getWidth() * 1.0F / mDrawRect.right - mDrawRect.left;
                mOnTransformListener.onTransEnd(scale, new float[]{mDrawRect.right - mDrawRect.left, mDrawRect.bottom - mDrawRect.top});
            }
        }
        return super.onTouchEvent(event);
    }

    public int getTouchRect(MotionEvent event) {
        if (isInLeftTop(event)) {
            return RECT_L_T;
        } else if (isInLeftBottom(event)) {
            return RECT_L_B;
        } else if (isInRightBottom(event)) {
            return RECT_R_B;
        } else if (isInRightTop(event)) {
            return RECT_R_T;
        }
        return -1;
    }

    /**
     * 获取屏幕坐标系下的锚点
     * Gets the anchor point in the screen coordinate system
     * @return 锚点 Anchor point
     */
    public Point getAnchorOnScreen() {
        Point anchor = new Point();
        int[] location = new int[2];
        getLocationOnScreen(location);
        if (mTouchRect == RECT_L_T) {
            anchor.x = location[0] + mDrawRect.right;
            anchor.y = location[1] + mDrawRect.bottom;
        } else if (mTouchRect == RECT_L_B) {
            anchor.x = location[0] + mDrawRect.right;
            anchor.y = location[1] + mDrawRect.top;
        } else if (mTouchRect == RECT_R_T) {
            anchor.x = location[0] + mDrawRect.left;
            anchor.y = location[1] + mDrawRect.bottom;
        } else if (mTouchRect == RECT_R_B) {
            anchor.x = location[0] + mDrawRect.left;
            anchor.y = location[1] + mDrawRect.top;
        }
        return anchor;
    }


    /**
     * 获取中心点的锚点
     * Gets the anchor point of the center point
     * @return 中心锚点 Central anchor point
     */
    public Point getCenterAnchorOnScreen() {
        Point anchor = new Point();
        int[] location = new int[2];
        getLocationOnScreen(location);
        Point pointLT = new Point();
        pointLT.x = location[0] + mDrawRect.left;
        pointLT.y = location[0] + mDrawRect.top;

        Point pointRB = new Point();
        pointLT.x = location[0] + mDrawRect.right;
        pointLT.y = location[0] + mDrawRect.bottom;
        anchor.x = (int) ((pointRB.x + pointLT.x) / 2F);
        anchor.y = (int) ((pointRB.y + pointLT.y) / 2F);
        return anchor;
    }

    public int getRectWidth() {
        return mRectWidth;
    }

    public int getRectHeight() {
        return mRectHeight;
    }

    public int getRectTop() {
        return mDrawRect.top;
    }

    public int getRectLeft() {
        return mDrawRect.left;
    }

    public void setDrawRectSize(int width, int height) {
        mDrawRect.left = (int) ((getWidth() - width) * 1.0F / 2) + mPadding;
        mDrawRect.right = mDrawRect.left + width;
        mDrawRect.top = (int) ((getHeight() - height) * 1.0F / 2) + mPadding;
        mDrawRect.bottom = mDrawRect.top + height;
        mRectWidth = width;
        mRectHeight = height;
        invalidate();
    }

    public int getPadding() {
        return mPadding;
    }

    private boolean isInLeftTop(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= mDrawRect.left && touchX <= TOUCH_RECT_SIZE + mDrawRect.left && touchY >= mDrawRect.top && touchY <= TOUCH_RECT_SIZE + mDrawRect.top);
    }

    private boolean isInRightTop(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= (mDrawRect.right - TOUCH_RECT_SIZE) && touchX <= mDrawRect.right && touchY >= mDrawRect.top && touchY <= TOUCH_RECT_SIZE + mDrawRect.top);
    }

    private boolean isInLeftBottom(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= mDrawRect.left && touchX <= TOUCH_RECT_SIZE + mDrawRect.left
                && touchY >= (mDrawRect.bottom - TOUCH_RECT_SIZE) && touchY <= mDrawRect.bottom);
    }

    private boolean isInRightBottom(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= (mDrawRect.right - TOUCH_RECT_SIZE) && touchX <= mDrawRect.right
                && touchY >= (mDrawRect.bottom - TOUCH_RECT_SIZE) && touchY <= mDrawRect.bottom);
    }

    public void setOnTransformListener(OnTransformListener listener) {
        mOnTransformListener = listener;
    }

    public void setLimitRect(Rect clipRect) {
        mLimitRect = clipRect;
    }

    public interface OnTransformListener {
        void onTrans(float deltaX, float deltaY);

        void onScaleAndRotate(float scale, float degree);

        void onTransEnd(float scale, float[] size);

        void onRectMoved(float scale, Point anchorInWindow, Point distance);

        boolean canMove(Point anchor);
    }

    private boolean canMove(Point anchor) {
        if (mOnTransformListener != null) {
            return mOnTransformListener.canMove(anchor);
        }
        return true;
    }
}
