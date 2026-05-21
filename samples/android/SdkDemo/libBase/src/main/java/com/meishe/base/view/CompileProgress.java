package com.meishe.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.meishe.base.R;


/**
 * Created by MZ008064 on 2020-05-11.
 * 编译过程类
 * Compile process class
 * @author zhaochenyu
 * @Date 2020 -05-11.
 */
public class CompileProgress extends View {
    private Context mContext;
    /**
     * The Radius.
     * 半径
     */
    protected int radius;
    /**
     * The Small radius.
     * 小圆半径
     */
    protected int smallRadius;
    /*
    * 中心点
    * center
    * */
    private Point center;
    /**
     * The Progress.
     * 当前进度
     */
    int progress;
    /**
     * The Max progress.
     * 最大进度 默认100
     */
    int maxProgress = 100;
    /**
     * The Min progress.
     * 最小进度 默认0
     */
    int minProgress = 0;
    /**
     * The Progress color.
     * 滚动条颜色
     */
    int progressColor;
    /**
     * The Progress back color.
     * 滚动条背景色
     */
    int progressBackColor;
    /**
     * The Progress width.
     * 滚动条宽度
     */
    float progressWidth;

    Paint mPaint;
    private RectF progressRect;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int width;
    private int height;

    public CompileProgress(Context context) {
        this(context, null);
    }

    public CompileProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Sets progress.
     *设置当前进度并绘制
     * @param progress the progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public CompileProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CompileProgress);
        progress = ta.getInteger(R.styleable.CompileProgress_progress, 0);
        maxProgress = ta.getInteger(R.styleable.CompileProgress_maxProgress, 100);
        minProgress = ta.getInteger(R.styleable.CompileProgress_minProgress, 0);
        progressWidth = ta.getDimension(R.styleable.CompileProgress_progressWidth, 20);
        progressColor = ta.getColor(R.styleable.CompileProgress_progressColor, 0xff00A2E8);
        progressBackColor = ta.getColor(R.styleable.CompileProgress_progressBackgroundColor, 0x00000000);
        ta.recycle();
        init();
    }

    private void init() {
        center = new Point();
        progressRect = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        width = getProgressDefaultSize(80, widthMeasureSpec);
        height = getProgressDefaultSize(80, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public static int getProgressDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            int specSize = MeasureSpec.getSize(measureSpec);
            if (specSize > size) {
                result = specSize;
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int exactlyWidth = width - paddingLeft - paddingRight;
        int exactlyHeight = height - paddingTop - paddingBottom;
        int exactlySize = exactlyWidth > exactlyHeight ? exactlyHeight : exactlyWidth;
        center.set(exactlySize / 2 + paddingLeft, exactlySize / 2 + paddingTop);
        radius = exactlySize / 2;

        progressRect.set(center.x - radius + progressWidth / 2,
                center.y - radius + progressWidth / 2,
                center.x + radius - progressWidth / 2,
                center.y + radius - progressWidth / 2);
        /*
        * 画圆弧背景
        * Draw an arc background
        * */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(progressBackColor);
        canvas.drawArc(progressRect, 0, 360, false, mPaint);


        /*
        * 画弧度
        * Painting curve
        * */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(progressColor);
        int degress = getDegress();
        canvas.drawArc(progressRect, -90, degress, false, mPaint);
        /*
        * 起始圆球
        *Initial ball
        * */
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(center.x, center.y + progressWidth / 2 - radius, progressWidth / 2, mPaint);
        /*
        * 尾部圆球
        *The tail ball
        * */
        int tailPointX = (int) (center.x + Math.sin(Math.toRadians(degress)) * (radius - progressWidth / 2));
        int tailPointY = (int) (center.y - Math.cos(Math.toRadians(degress)) * (radius - progressWidth / 2));

        canvas.drawCircle(tailPointX, tailPointY, progressWidth / 2, mPaint);
    }

    private Point getProgressPoint() {
        Point progressPoint = new Point();
        int x = (int) (center.x + Math.sin((progress / maxProgress) / 360) * (radius - progressWidth / 2));
        int y = (int) (center.y + Math.cos((progress / maxProgress) / 360) * (radius - progressWidth / 2));
        return progressPoint;
    }

    private int getDegress() {
        return 360 * progress / maxProgress;
    }
}
