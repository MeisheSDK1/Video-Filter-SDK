package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.meishe.sdkdemo.R;

/**
 * CutRectViewEx class
 *
 * @author yangtailin
 * @date 2020/7/1 17:15
 */
public class GridBackgroundView extends View {

    private Rect mDrawRect = new Rect();
    private Paint mPaint;
    private Path mPath = new Path();

    private int mRowNum = 4;
    private int mColumnNum = 4;

    public GridBackgroundView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.ff333333));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
    }

    public GridBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.ff333333));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDrawRect.left = 0;
        mDrawRect.top = 0;
        mDrawRect.right = getMeasuredWidth();
        mDrawRect.bottom = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        //绘制边框 Draw border
        mPath.moveTo(mDrawRect.left, mDrawRect.top);
        mPath.lineTo(mDrawRect.right, mDrawRect.top);
        mPath.lineTo(mDrawRect.right, mDrawRect.bottom);
        mPath.lineTo(mDrawRect.left, mDrawRect.bottom);
        mPath.lineTo(mDrawRect.left, mDrawRect.top);
        canvas.drawPath(mPath, mPaint);

        //绘制中线 Draw center line
        int width = mDrawRect.right - mDrawRect.left;
        int height = mDrawRect.bottom - mDrawRect.top;
        //竖线 Vertical line
        float dimW = (width) * 1.0F / mColumnNum;
        float dimH = (height) * 1.0F / mRowNum;
        for (int i = 1; i < mColumnNum; i++) {
            float src = mDrawRect.left + dimW * i;
            mPath.moveTo(src, mDrawRect.top);
            mPath.lineTo(src, mDrawRect.bottom);
            canvas.drawPath(mPath, mPaint);
        }

        for (int j = 1; j < mRowNum; j++) {
            float des = mDrawRect.top + dimH * j;
            mPath.moveTo(mDrawRect.left, des);
            mPath.lineTo(mDrawRect.right, des);
            canvas.drawPath(mPath, mPaint);
        }

//        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 4 * 2, mDrawRect.top);
//        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.bottom);
//        canvas.drawPath(mPath, mPaint);
//
//        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3 * 3, mDrawRect.top);
//        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.bottom);
//        canvas.drawPath(mPath, mPaint);

//        //横线
//        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3 * 2);
//        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3 * 2);
//        canvas.drawPath(mPath, mPaint);
//
//        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3);
//        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3);
//        canvas.drawPath(mPath, mPaint);
//
//        int angelLength = ANGEL_LENGTH;
//        if (angelLength > width) {
//            angelLength = width;
//        }
//        if (angelLength > height) {
//            angelLength = height;
//        }
//        int strokeWidth = STROKE_WIDTH;
//        //绘制左上角
//        mPath.reset();
//        mPath.moveTo(mDrawRect.left + angelLength + strokeWidth / 2, mDrawRect.top + strokeWidth / 2F);
//        mPath.lineTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.top + strokeWidth / 2F);
//        mPath.lineTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.top + angelLength + +strokeWidth / 2F);
//        canvas.drawPath(mPath, mCornerPaint);
//
//        //绘制右上角
//        mPath.moveTo(mDrawRect.right - angelLength - strokeWidth / 2F, mDrawRect.top + strokeWidth / 2F);
//        mPath.lineTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.top + strokeWidth / 2F);
//        mPath.lineTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.top + angelLength + strokeWidth / 2F);
//        canvas.drawPath(mPath, mCornerPaint);
//
//        //绘制右下角
//        mPath.moveTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2 - angelLength);
//        mPath.lineTo(mDrawRect.right - strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2F);
//        mPath.lineTo(mDrawRect.right - strokeWidth / 2 - angelLength, mDrawRect.bottom - strokeWidth / 2F);
//        canvas.drawPath(mPath, mCornerPaint);
//
//        //绘制左下角
//        mPath.moveTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2F - angelLength);
//        mPath.lineTo(mDrawRect.left + strokeWidth / 2F, mDrawRect.bottom - strokeWidth / 2F);
//        mPath.lineTo(mDrawRect.left + strokeWidth / 2F + angelLength, mDrawRect.bottom - strokeWidth / 2F);
//        canvas.drawPath(mPath, mCornerPaint);
    }
}
