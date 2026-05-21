package com.meishe.sdkdemo.edit.clipEdit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.meishe.sdkdemo.edit.clipEdit.adjust.FloatPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试用，勿删
 * author：yangtailin on 2020/7/1 17:15
 */
public class TestRectView extends View {
    private final static String TAG = "TestRectView";
    private Paint mPaint;
    private int[] location = new int[2];
    private FloatPoint mCenterPoint;

    private List<FloatPoint> mRectPoint = new ArrayList<>();

    public TestRectView(Context context) {
        super(context);
    }

    public TestRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectPoint.isEmpty()) {
            return;
        }
        getLocationOnScreen(location);
        Path path = new Path();
        path.moveTo(mRectPoint.get(0).x - location[0], mRectPoint.get(0).y - location[1]);
        path.lineTo(mRectPoint.get(1).x - location[0], mRectPoint.get(1).y - location[1]);
        path.lineTo(mRectPoint.get(2).x - location[0], mRectPoint.get(2).y - location[1]);
        path.lineTo(mRectPoint.get(3).x - location[0], mRectPoint.get(3).y - location[1]);
        path.lineTo(mRectPoint.get(0).x - location[0], mRectPoint.get(0).y - location[1]);
        canvas.drawPath(path, mPaint);
        if (mCenterPoint != null) {
            canvas.drawCircle(mCenterPoint.x - location[0], mCenterPoint.y - location[1], 40, mPaint);
        }
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setRectPoint(List<FloatPoint> rectPoint) {
        this.mRectPoint = rectPoint;
        invalidate();
    }

    public void setCenterPoint(FloatPoint centerPoint) {
        mCenterPoint = centerPoint;
        invalidate();
    }
}
