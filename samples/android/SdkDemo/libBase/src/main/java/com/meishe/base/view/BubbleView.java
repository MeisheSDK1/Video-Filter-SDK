package com.meishe.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.meishe.base.R;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2023/3/15 13:31
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BubbleView extends View {
    private Paint bubblePaint;
    private Paint textPaint;
    private Path mPathBubble;
    private RectF bubbleRectF;
    private float mX;
    private float mY;
    private float mBubbleWidth = 200;
    private float mBubbleHeight = 100;
    private String mText = "";
    private int leftRightMargin;
    private int topBottomMargin;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context context) {
        mPathBubble = new Path();
        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setStrokeCap(Paint.Cap.ROUND);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setAntiAlias(true);
        bubblePaint.setColor(Color.WHITE);

        textPaint = new Paint();
        textPaint.setTextSize(36);
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(2f);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        leftRightMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_px_39);
        topBottomMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_px_18);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == bubbleRectF) {
            bubbleRectF = new RectF();
        }
        bubbleRectF.left = mX - (mBubbleWidth / 2F);
        bubbleRectF.top = mY;
        bubbleRectF.right = mX + (mBubbleWidth / 2F);
        bubbleRectF.bottom = mY + mBubbleHeight;
        canvas.drawRoundRect(bubbleRectF, mBubbleWidth / 2F + 10, mBubbleWidth / 2F + 10, bubblePaint);
        float side = 12;
        float startPointX = mX;
        float startPointY = mY + mBubbleHeight + side;
        mPathBubble.reset();
        mPathBubble.moveTo(startPointX, startPointY);
        mPathBubble.lineTo(startPointX + side, startPointY - side);
        mPathBubble.lineTo(startPointX - side, startPointY - side);
        mPathBubble.close();
        canvas.drawPath(mPathBubble, bubblePaint);

        Rect textRrect = new Rect();
        textPaint.getTextBounds(mText, 0, mText.length(), textRrect);
        canvas.drawText(mText
                , mX - (textRrect.width() / 2F)
                , (mY + mBubbleHeight) - (mBubbleHeight / 2F) + (textRrect.height() / 2F)
                , textPaint);
    }

    public int getBubbleHeight() {
        return (int) mBubbleHeight;
    }

    public void setSize(String text, float textSize) {
        mBubbleWidth = textPaint.measureText(text) + (leftRightMargin * 2);
        mBubbleHeight = textSize + (topBottomMargin * 2);
    }

    public void setTextSize(float textSize) {
        textPaint.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        textPaint.setColor(textColor);
    }

    public void setBubbleBgColor(int color) {
        bubblePaint.setColor(color);
    }

    public void setFloatOffset(String text, float x, float y) {
        mText = text;
        mX = x;
        invalidate();
    }

}
