package com.meishe.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.meishe.sdkdemo.bean.HumanInfo;

import java.util.Collection;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Meng Guijun
 * @CreateDate: 2021/1/13 10:45
 * @Description:
 * @Copyright:2021 www.meishesdk.com Inc. All rights reserved.
 */
public class FacePointView extends AppCompatImageView {

    private float width, height;
    private float bitmapWidth, bitmapHeight;
    private float ratio = 1.0f;
    private float bitmapRatio = 1.00f;
    private float layoutRatio = 1.00f;
    private Paint paint;
    private Paint pointPaint;
    private Paint redPaint;
    private List<HumanInfo> humanInfoList;

    public FacePointView(Context context) {
        this(context, null);
        init();
    }

    public FacePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        pointPaint = new Paint();
        pointPaint.setColor(Color.GREEN);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setAntiAlias(true);
    }

    public void setHumanInfoList(List<HumanInfo> humanInfoList, float w, float h) {
        this.humanInfoList = humanInfoList;
        this.bitmapWidth = w;
        this.bitmapHeight = h;
        bitmapRatio = w / h;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        layoutRatio = width / height;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEmpty(humanInfoList)) {
            return;
        }
        float imageLayoutWidth;
        float imageLayoutHeight;
        float leftMargin = 0;
        float topMargin = 0;

        if (bitmapRatio < layoutRatio) {
            imageLayoutHeight = width / bitmapRatio;
            topMargin = (height - imageLayoutHeight) / 2;
            ratio = width / bitmapWidth;
        } else {
            imageLayoutWidth = height * bitmapRatio;
            leftMargin = (width - imageLayoutWidth) / 2;
            ratio = height / bitmapHeight;
        }
        for (int i = 0; i < humanInfoList.size(); i++) {
            HumanInfo humanInfo = humanInfoList.get(i);
            RectF rect = humanInfo.getFaceRect();
            //RectF scaleRectF = new RectF(rect.left * ratio, (bitmapHeight - rect.top) * ratio, rect.right * ratio, (bitmapHeight - rect.bottom) * ratio);
            RectF scaleRectF = new RectF(rect.left * ratio, rect.top * ratio, rect.right * ratio, rect.bottom * ratio);
            scaleRectF.offset(leftMargin, topMargin);
            canvas.drawRect(scaleRectF, paint);
            List<PointF> pointList = humanInfo.getPointFList();
            if (!isEmpty(pointList)) {
                for (int j = 0; j < pointList.size(); j++) {
                    PointF pointF = pointList.get(j);
                    //canvas.drawCircle(pointF.x * ratio + leftMargin, (bitmapHeight - pointF.y) * ratio + topMargin, 5, pointPaint);
                    canvas.drawCircle(pointF.x * ratio + leftMargin, pointF.y * ratio + topMargin, 5, pointPaint);
                }
            }
        }
    }

    public static <T extends Collection<?>> boolean isEmpty(T t) {
        return ((t == null) || (t.isEmpty()));
    }
}
