package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.meishe.sdkdemo.R;

public class BezierAdjustView extends View {

    private static int TYPE_CONTROL_NONE_POINT = 0;
    private static int TYPE_CONTROL_FRONT_POINT = 1;
    private static int TYPE_CONTROL_BACK_POINT = 2;


    private Paint mGridPaint, mPaint;
    private int mWidth, mHeight;
    private int mStartX, mStartY;
    private int mEndX, mEndY;
    private int mRadius, mMaxRadius;

    private int mFrontX, mFrontY, mBackX, mBackY;
    private float mDownX, mDownY;

    private int mControlType = TYPE_CONTROL_NONE_POINT;
    private Path mPath = new Path();
    private Context mContext;

    private PointF mLeftPointF = new PointF(0.333333f, 0.333333f);
    private PointF mRightPointF = new PointF(0.666667f, 0.666667f);

    private OnTouchPointCallback mTouchPointCallback;

    public BezierAdjustView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public BezierAdjustView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mGridPaint = new Paint();
        mGridPaint.setColor(mContext.getResources().getColor(R.color.ff333333));
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRadius = (int) getResources().getDimension(R.dimen.dp_px_27);
        mMaxRadius = (int) getResources().getDimension(R.dimen.dp_px_27);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("TAG", "onMeasure: ===============" + mFrontX + " " + mFrontY + "  " + mBackX + "  " + mBackY);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mStartX = mRadius;
        mStartY = mHeight - mRadius;
        mEndX = mWidth - mRadius;
        mEndY = mRadius;

//        mFrontX = (mWidth - 2 * mRadius) / 4 + mRadius;
//        mFrontY = (mHeight - 2 * mRadius) / 4 * 3 + mRadius;
//
//        mBackX = (mWidth - 2 * mRadius) / 4 * 3 + mRadius;
//        mBackY = (mHeight - 2 * mRadius) / 4  + mRadius;

        mFrontX = (int) (mLeftPointF.x * (mWidth - 2 * mRadius) + mRadius);
        mFrontY = (int) (mHeight - mRadius - mLeftPointF.y * (mHeight - 2 * mRadius));
        mBackX = (int) (mRightPointF.x * (mWidth - 2 * mRadius) + mRadius);
        mBackY = (int) (mHeight - mRadius - mRightPointF.y * (mHeight - 2 * mRadius));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("TAG", "onDraw: ===============" + mFrontX + " " + mFrontY + "  " + mBackX + "  " + mBackY);
        mPaint.setColor(mContext.getResources().getColor(R.color.ff171717));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mRadius, mRadius, mEndX, mStartY, mPaint);

        mPath.reset();
        mPath.moveTo(mRadius, mRadius);
        mPath.lineTo(mEndX, mRadius);
        mPath.lineTo(mEndX, mStartY);
        mPath.lineTo(mRadius, mStartY);
        mPath.lineTo(mRadius, mRadius);
        canvas.drawPath(mPath, mGridPaint);


        float dimW = (mWidth - 2 * mRadius) * 1.0F / 4;
        float dimH = (mHeight - 2 * mRadius) * 1.0F / 4;
        for (int i = 1; i < 4; i++) {
            float src = dimW * i + mRadius;
            mPath.moveTo(src, mRadius);
            mPath.lineTo(src, mStartY);
            canvas.drawPath(mPath, mGridPaint);

            float des = dimH * i + mRadius;
            mPath.moveTo(mRadius, des);
            mPath.lineTo(mEndX, des);
            canvas.drawPath(mPath, mGridPaint);
        }


        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.moveTo(mStartX, mStartY);
        mPath.cubicTo(mFrontX, mFrontY, mBackX, mBackY, mEndX, mEndY);
        canvas.drawPath(mPath, mPaint);


        mPaint.setColor(mContext.getResources().getColor(R.color.blue_63ab));
        mPaint.setStrokeWidth(6);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mFrontX, mFrontY, mRadius - 4, mPaint);
        canvas.drawCircle(mBackX, mBackY, mRadius - 4, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.moveTo(mStartX, mStartY);
        mPath.lineTo(mFrontX, mFrontY);
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(mEndX, mEndY);
        mPath.lineTo(mBackX, mBackY);
        canvas.drawPath(mPath, mPaint);


        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(16);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mStartX, mStartY, 16, mPaint);
        canvas.drawCircle(mEndX, mEndY, 16, mPaint);

        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mFrontX, mFrontY, mRadius - 4, mPaint);
        canvas.drawCircle(mBackX, mBackY, mRadius - 4, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mControlType = judgeControlType();
            case MotionEvent.ACTION_MOVE:
                float tempEventX = event.getX();
                float tempEventY = event.getY();
                if (tempEventX < mRadius) {
                    tempEventX = mRadius;
                }
                if (tempEventX > mEndX) {
                    tempEventX = mEndX;
                }
                if (tempEventY < mRadius) {
                    tempEventY = mRadius;
                }
                if (tempEventY > mStartY) {
                    tempEventY = mStartY;
                }
                if (mControlType == TYPE_CONTROL_FRONT_POINT) {
                    mFrontX = (int) tempEventX;
                    mFrontY = (int) tempEventY;
                    if (movePointCallback != null) {
                        movePointCallback.onFrontPointMove(getNormalizedCoordinates(mFrontX, mFrontY));
                    }
                    invalidate();
                } else if (mControlType == TYPE_CONTROL_BACK_POINT) {
                    mBackX = (int) tempEventX;
                    mBackY = (int) tempEventY;
                    if (movePointCallback != null) {
                        movePointCallback.onBackPointMove(getNormalizedCoordinates(mBackX, mBackY));
                    }
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mTouchPointCallback != null) {
                    if (mControlType == TYPE_CONTROL_FRONT_POINT) {
                        mTouchPointCallback.onPointTouched(getNormalizedCoordinates(mFrontX, mFrontY), true);
                    } else if (mControlType == TYPE_CONTROL_BACK_POINT) {
                        mTouchPointCallback.onPointTouched(getNormalizedCoordinates(mBackX, mBackY), false);
                    }
                }
                Log.d("TAG", "onTouchEvent=======:  event.getX()：" + event.getX() + "  event.getY():" + event.getY() + "  mRadius:" + mRadius + "  mControlType:" + mControlType);
                Log.d("TAG", "onTouchEvent=======:  leftPoint：" + getNormalizedCoordinates(mFrontX, mFrontY) + "  rightPoint:" + getNormalizedCoordinates(mBackX, mBackY));
                break;
        }
        return true;
    }


    private int judgeControlType() {
        if (Math.abs(mFrontX - mDownX) <= mMaxRadius && Math.abs(mFrontY - mDownY) <= mMaxRadius) {
            return TYPE_CONTROL_FRONT_POINT;
        } else if (Math.abs(mBackX - mDownX) <= mMaxRadius && Math.abs(mBackY - mDownY) <= mMaxRadius) {
            return TYPE_CONTROL_BACK_POINT;
        }
        return TYPE_CONTROL_NONE_POINT;
    }

    private PointF getNormalizedCoordinates(int eventPosX, int eventPosY) {
        PointF pointF = new PointF();
        pointF.x = (eventPosX - mRadius) * 1.0F / (mWidth - 2 * mRadius);
        pointF.y = (mHeight - mRadius - eventPosY) * 1.0F / (mHeight - 2 * mRadius);
        return pointF;
    }

    public void updateControlPoint(PointF leftPintF, PointF rightPointF) {
        mLeftPointF = leftPintF;
        mRightPointF = rightPointF;
        Log.d("TAG", "updateControlPoint: =====================" + leftPintF + "  right:" + rightPointF);
        mFrontX = (int) (leftPintF.x * (mWidth - 2 * mRadius) + mRadius);
        mFrontY = (int) (mHeight - mRadius - leftPintF.y * (mHeight - 2 * mRadius));
        mBackX = (int) (rightPointF.x * (mWidth - 2 * mRadius) + mRadius);
        mBackY = (int) (mHeight - mRadius - rightPointF.y * (mHeight - 2 * mRadius));
        Log.d("TAG", "updateControlPoint: ===============" + mFrontX + " " + mFrontY + "  " + mBackX + "  " + mBackY);
        invalidate();
    }


    public void setTouchPointCallback(OnTouchPointCallback onTouchPointCallback) {
        this.mTouchPointCallback = onTouchPointCallback;
    }

    public interface OnTouchPointCallback {

        void onPointTouched(PointF pointF, boolean isFront);

    }

    private OnTouchMovePointCallback movePointCallback;

    public void setMovePointCallback(OnTouchMovePointCallback movePointCallback) {
        this.movePointCallback = movePointCallback;
    }

    public interface OnTouchMovePointCallback {
        void onFrontPointMove(PointF pointF);

        void onBackPointMove(PointF pointF);
    }
}
