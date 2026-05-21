package com.meishe.sdkdemo.edit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.meishe.base.R;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Nv bezier speed view.
 * Nv bezier速度视图类
 */
public class NvBezierSpeedView extends View {

    private static final String TAG = "NvBezierSpeedView";
    /**
     * 边框宽高
     * Border width height
     */
    private int mBezierViewHeight, mBezierViewWidth;
    /**
     * 实线宽度
     * Solid line width
     */
    private int mRectLineWidth = 2;
    /**
     * 虚线宽度
     * Width of dashed line
     */
    private int mDottedLineWidth = 2;
    /**
     * 点位集合
     * points list
     */
    private List<BzPoint> list = null;
    /**
     * 边框画笔
     * Border paint
     */
    private Paint mPaintRect = null;
    /**
     * 虚线画笔
     * dashed line paint
     */
    private Paint mDottedLinePaint = null;
    /**
     * 速度文字画笔
     * text of speed paint
     */
    private Paint mSpeedPaint = null;
    private Path mDottedPath;
    /**
     * 画基准线
     * baseline paint
     */
    private Paint mBaseLinePaint;
    /**
     * 画空心圆
     * Hollow circle pint
     */
    private Paint mCircleStrockPaint;

    /**
     * 画空心圆里的圆
     * circle in Hollow circle paint
     */
    private Paint mCircleStrockInnerPaint;

    /**
     * 画实心圆
     * solid circle paint
     */
    private Paint mCircleFillPaint;
    /**
     * 画贝塞尔曲线
     * Bezier curve paint
     */
    private Paint mBzPaint;
    /**
     * 贝塞尔曲线Path
     * Bezier curve path
     */
    private Path mBzPath;

    /**
     * 速度最值
     * Maximum speed
     */
    private float mMaxSpeed = 10;
    private float mMinSpeed = 0.2f;
    /**
     * 表格行数
     * Row table count
     */
    private int mTabRowNum = 4;
    /**
     * 表格行高
     * Table row height
     */
    private int mTabRowHeight = 0;
    /**
     * 基准线所在的x位置
     * X position of Base line
     */
    private float mBaseLineX;
    /**
     * 按下的位置
     * pointer down position
     */
    private float mDownX;
    private float mDownY;

    /**
     * 移动的点
     * move position
     */
    private float mMoveX;
    private float mMoveY;
    /**
     * 默认添加五个点
     * default point size
     */
    private int mDefaultPointNum = 5;
    /**
     * 圆半径
     * radius
     */
    private int mCircleRadius = 20;
    /**
     * 矩形框的padding
     * Rectangular box padding
     */
    private int mRectPadding = 30;

    /**
     * 当前按下的点
     * current down point
     */

    private BzPoint mCurrDownBzPoint = null;
    private int mCurrDownBzPosition = -1;
    /**
     * 未选中圆的外边框
     * he outer border of the circle is not selected
     */
    private int mCircleStrockWidth = 5;

    private OnBezierListener mOnBezierListener = null;
    /**
     * 视频片段时间线长度
     * Video clip timeline length
     */
    private long mDuring = -1l;
    /**
     * 区间
     * speed section
     */
    private static double mSpeedStart = 0.2D;
    private static double mSpeedMiddle = 1D;
    private static double mSpeedEnd = 10D;

    private double xRatio;

    private String mSpeedPoint = null;

    private String speedOriginal = null;


    public NvBezierSpeedView(Context context) {
        this(context, null);
    }


    public NvBezierSpeedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public NvBezierSpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mBaseLineX = mRectPadding;
    }

    @SuppressLint("ResourceType")
    private void init() {
        mRectLineWidth = (int) getResources().getDimension(R.dimen.dp0p5);
        mDottedLineWidth = (int) getResources().getDimension(R.dimen.dp0p5);
        mCircleRadius = (int) getResources().getDimension(R.dimen.dp_px_27);
        mCircleStrockWidth = (int) getResources().getDimension(R.dimen.dp_px_3);
        mRectPadding = (int) getResources().getDimension(R.dimen.dp_px_30);

        list = new ArrayList<>();
        mPaintRect = new Paint();
        mPaintRect.setColor(getResources().getColor(R.color.bezier_rect));
        mPaintRect.setStrokeWidth(mRectLineWidth);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setAntiAlias(true);

        mDottedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDottedLinePaint.setStrokeWidth(mDottedLineWidth);
        mDottedLinePaint.setColor(getResources().getColor(R.color.bezier_rect));
        mDottedLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setAntiAlias(true);
        mDottedPath = new Path();

        mSpeedPaint = new Paint();
        mSpeedPaint.setColor(getResources().getColor(R.color.bezier_speed));
        mSpeedPaint.setStrokeWidth(mRectLineWidth);
        mSpeedPaint.setTextSize(getResources().getDimension(R.dimen.sp9));
        mSpeedPaint.setAntiAlias(true);

        mBaseLinePaint = new Paint();
        mBaseLinePaint.setColor(getResources().getColor(R.color.bezier_baseline));
        mBaseLinePaint.setStrokeWidth(getResources().getDimension(R.dimen.dp1p5));
        mBaseLinePaint.setAntiAlias(true);

        mCircleStrockPaint = new Paint();
        mCircleStrockPaint.setColor(getResources().getColor(R.color.white));
        mCircleStrockPaint.setStrokeWidth(mCircleStrockWidth);
        mCircleStrockPaint.setAntiAlias(true);
        mCircleStrockPaint.setStyle(Paint.Style.STROKE);

        mCircleStrockInnerPaint = new Paint();
        mCircleStrockInnerPaint.setColor(getResources().getColor(R.color.bezier_bg));
        mCircleStrockInnerPaint.setAntiAlias(true);
        mCircleStrockInnerPaint.setStyle(Paint.Style.FILL);

        mCircleFillPaint = new Paint();
        mCircleFillPaint.setColor(getResources().getColor(R.color.bezier_bg));
        mCircleFillPaint.setAntiAlias(true);
        mCircleFillPaint.setStyle(Paint.Style.FILL);

        mBzPaint = new Paint();
        mBzPaint.setColor(getResources().getColor(R.color.bezier_line));
        mBzPaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_px_3));
        mBzPaint.setAntiAlias(true);
        mBzPaint.setStyle(Paint.Style.STROKE);
        mBzPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBezierViewHeight = h;
        mBezierViewWidth = w - mRectPadding * 2;
        mBezierViewHeight = h - mRectPadding * 2;
        mTabRowHeight = mBezierViewHeight / mTabRowNum;
        initPoint(mSpeedPoint);
        if (mDuring == -1) {
            mDuring = mBezierViewWidth;
        }
        xRatio = mDuring / mBezierViewWidth;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 初始化点数据
     * init point data
     */
    private void initPoint(String speedPoint) {
        if (list != null) {
            list.clear();
        }
        if (!TextUtils.isEmpty(speedPoint)) {
            String[] pointStrings = speedPoint.split("\\)");
            for (int i = 0; i < pointStrings.length; i = i + 3) {
                String pointSting = pointStrings[i].substring(1);
                String[] point = pointSting.split(",");
                float xFloat = Float.parseFloat(point[0]);
                float yFloat = Float.parseFloat(point[1]);
                BzPoint bzPoint = new BzPoint();
                bzPoint.x = xFloat;
                bzPoint.y = yFloat;
                list.add(bzPoint);
            }
            BzPoint lastPoint = list.get(list.size() - 1);
            float firstX = list.get(0).x;
            float length = lastPoint.x - firstX;
            double hHalf = mBezierViewHeight / 2.0f;
            for (int i = 0; i < list.size(); i++) {
                BzPoint bzPoint = list.get(i);
                bzPoint.x = (bzPoint.x - firstX) / length * mBezierViewWidth + mRectPadding;
                if (bzPoint.y > mSpeedMiddle) {
                    bzPoint.y = (float) (hHalf - (((bzPoint.y - mSpeedMiddle) / (mSpeedEnd - mSpeedMiddle)) * hHalf) + mRectPadding);
                } else if (bzPoint.y < mSpeedMiddle) {
                    bzPoint.y = (float) (hHalf - (((bzPoint.y - mSpeedStart) / (mSpeedMiddle - mSpeedStart)) * hHalf) + hHalf + mRectPadding);
                } else {
                    bzPoint.y = (float) (hHalf + mRectPadding);
                }
            }
        } else {
            /*
             * 初始化最初的五个点
             * init the first five points
             */

            float marginPoint = mBezierViewWidth / (mDefaultPointNum - 1);
            for (int i = 0; i < mDefaultPointNum; i++) {
                BzPoint bzPoint = new BzPoint();
                bzPoint.x = i * marginPoint + mRectPadding;
                bzPoint.y = mBezierViewHeight / 2 + mRectPadding;
                list.add(bzPoint);
            }
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画表格
        //draw table
        drawTab(canvas);
        //画基准线
        //draw baseline
        drawBaseLine(canvas);
        //画贝塞尔曲线
        //draw Bezier curve
        drawBezier(canvas);
        //draw circle
        drawCircle(canvas);
    }


    /**
     * 画贝塞尔曲线
     * draw Bezier curve
     *
     * @param canvas
     */
    private void drawBezier(Canvas canvas) {
        mBzPath.reset();
        for (int i = 0; i < list.size() - 1; i++) {
            BzPoint bzPoint = list.get(i);
            BzPoint afterBzPoint = list.get(i + 1);
            float length = afterBzPoint.x - bzPoint.x;
            mBzPath.moveTo(bzPoint.x, bzPoint.y);
            mBzPath.cubicTo(bzPoint.x + length / 3, bzPoint.y, bzPoint.x + length / 3 * 2, afterBzPoint.y, afterBzPoint.x, afterBzPoint.y);
            canvas.drawPath(mBzPath, mBzPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (null != mOnBezierListener) {
                mOnBezierListener.onActionDown();
            }
            mBaseLineX = event.getX();
            mDownX = event.getX();
            mDownY = event.getY();
            mCurrDownBzPosition = findPressPoint();

            if (mCurrDownBzPosition == 0) {
                mBaseLineX = mRectPadding;
            } else if (mCurrDownBzPosition == list.size() - 1) {
                mBaseLineX = mRectPadding + mBezierViewWidth;
            }
            if (mBaseLineX < mRectPadding || mBaseLineX > mBezierViewWidth + mRectPadding) {
                return false;
            }
            if (mOnBezierListener != null) {
                long position = (long) ((mBaseLineX - mRectPadding) / mBezierViewWidth * mDuring);
                mOnBezierListener.seekPosition(position);
            }
            if (mCurrDownBzPosition != -1) {
                mCurrDownBzPoint = list.get(mCurrDownBzPosition);
            }
            invalidate();
        } else if (action == MotionEvent.ACTION_MOVE) {
            mBaseLineX = event.getX();
            mMoveX = event.getX();
            mMoveY = event.getY();

            if (mCurrDownBzPosition == 0) {
                mBaseLineX = mRectPadding;
            } else if (mCurrDownBzPosition == list.size() - 1) {
                mBaseLineX = mRectPadding + mBezierViewWidth;
            }

            if (mMoveX < mRectPadding || mMoveX > mBezierViewWidth + mRectPadding) {
                return false;
            }
            if (mMoveY < mRectPadding || mMoveY > mBezierViewHeight + mRectPadding) {
                return false;
            }

            if (mCurrDownBzPoint != null) {
                //第一个点和最后一个点，只在Y轴上滑动
                //The first and last points slide only on the Y axis
                if (mCurrDownBzPosition == 0 || (mCurrDownBzPosition == list.size() - 1)) {
                    mCurrDownBzPoint.y = mMoveY;
                } else {
                    if (!CommonUtils.isIndexAvailable(mCurrDownBzPosition, list)) {
                        return true;
                    }
                    BzPoint prePoint = list.get(mCurrDownBzPosition - 1);
                    BzPoint afterPoint = list.get(mCurrDownBzPosition + 1);
                    if (mMoveX - prePoint.x <= mCircleRadius) {
                        mCurrDownBzPoint.y = mMoveY;
                        mBaseLineX = mCurrDownBzPoint.x;
                    } else if (afterPoint.x - mMoveX <= mCircleRadius) {
                        mCurrDownBzPoint.y = mMoveY;
                        mBaseLineX = mCurrDownBzPoint.x;

                    } else {
                        mCurrDownBzPoint.x = mMoveX;
                        mCurrDownBzPoint.y = mMoveY;
                    }
                }
            }
            if (mBaseLineX < mRectPadding || mBaseLineX > mBezierViewWidth + mRectPadding) {
                return false;
            }
            if (mOnBezierListener != null) {
                long position = (long) ((mBaseLineX - mRectPadding) / mBezierViewWidth * mDuring);
                mOnBezierListener.seekPosition(position);
            }
            invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            /*
             * 开始播放
             * start play
             */
            if (mCurrDownBzPosition != -1) {
                if ((mMoveX != 0 && Math.abs(mMoveX - mDownX) > 5) || (mMoveY != 0 && Math.abs(mMoveY - mDownY) > 5)) {
                    startPlay();
                }
            }
            mCurrDownBzPosition = -1;
            mCurrDownBzPoint = null;
        }
        return true;
    }

    /**
     * 开始播放
     * start play
     */
    private void startPlay() {
        if (mOnBezierListener != null) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                BzPoint bzPoint = list.get(i);
                BzPoint afterPoint = null;
                BzPoint prePoint = null;
                double lengthAfter = 0D, lengthPre = 0D;
                if (i != list.size() - 1) {
                    afterPoint = list.get(i + 1);
                    lengthAfter = afterPoint.x - bzPoint.x;
                    lengthAfter = lengthAfter * xRatio;
                }

                if (i != 0) {
                    prePoint = list.get(i - 1);
                    lengthPre = bzPoint.x - prePoint.x;
                    lengthPre = lengthPre * xRatio;
                }
                double p1X, p2X;
                double x = xRatio * (bzPoint.x - mRectPadding) * 1.0D;
                double y = 1.0D;
                double hHalf = mBezierViewHeight / 2.0f;
                if (bzPoint.y - mRectPadding < hHalf) {
                    double yH = (hHalf - (bzPoint.y - mRectPadding)) / hHalf;
                    y = yH * (mSpeedEnd - mSpeedMiddle) + mSpeedMiddle;
                } else if (bzPoint.y - mRectPadding > hHalf) {
                    double yH = (hHalf - ((bzPoint.y - mRectPadding) - hHalf)) / hHalf;
                    y = yH * (mSpeedMiddle - mSpeedStart) + mSpeedStart;
                }
                builder = BezierUtil.append(builder, x, y);
                if (i == 0) {
                    p1X = x - lengthAfter / 3;
                    builder = BezierUtil.append(builder, p1X, y);
                    p2X = x + lengthAfter / 3;
                    builder = BezierUtil.append(builder, p2X, y);
                } else if (i == list.size() - 1) {
                    p1X = x - lengthPre / 3;
                    builder = BezierUtil.append(builder, p1X, y);
                    p2X = x + lengthPre / 3;
                    builder = BezierUtil.append(builder, p2X, y);
                } else {
                    p1X = x - lengthPre / 3;
                    builder = BezierUtil.append(builder, p1X, y);
                    p2X = x + lengthAfter / 3;
                    builder = BezierUtil.append(builder, p2X, y);
                }
            }
            long position = (long) ((mBaseLineX - mRectPadding) / mBezierViewWidth * mDuring);
            mOnBezierListener.onSpeedChanged(builder.toString(), position);
        }
    }


    /**
     * Add or delete point.
     * 添加或删除点
     *
     * @param position the position
     */
    public void addOrDeletePoint(int position) {
        if (position == -1) {
            //添加
            //add position
            for (int i = 0; i < list.size() - 1; i++) {
                BzPoint bzPoint = list.get(i);
                BzPoint afterBzPoint = list.get(i + 1);
                if (mBaseLineX > bzPoint.x && mBaseLineX < afterBzPoint.x) {
                    BzPoint bzPointNew = new BzPoint();
                    bzPointNew.x = mBaseLineX;
                    bzPointNew.y = BezierUtil.doubleToFloat(BezierUtil.calcBezierPointY(mBaseLineX, list.get(i), list.get(i + 1)));
                    list.add(i + 1, bzPointNew);
                    break;
                }
            }

        } else {
            //删除
            //delete
            if (CommonUtils.isIndexAvailable(position, list)) {
                list.remove(position);
            }
        }
        invalidate();
    }


    /**
     * 画圆
     * draw circle
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        int selectedPosition = -1;
        for (int i = 0; i < list.size(); i++) {
            BzPoint bzPoint = list.get(i);
            if (mBaseLineX >= bzPoint.x - mCircleRadius && mBaseLineX <= bzPoint.x + mCircleRadius) {
                //选中的圆
                //selected circle
                selectedPosition = i;
                canvas.drawCircle(bzPoint.x, bzPoint.y, mCircleRadius, mCircleFillPaint);
            } else {
                canvas.drawCircle(bzPoint.x, bzPoint.y, mCircleRadius, mCircleStrockInnerPaint);
                canvas.drawCircle(bzPoint.x, bzPoint.y, mCircleRadius - 1, mCircleStrockPaint);

            }
        }
        if (mOnBezierListener != null) {
            mOnBezierListener.onSelectedPoint(selectedPosition);
        }
    }

    /**
     * 画基准线
     * draw baseline
     *
     * @param canvas
     */
    private void drawBaseLine(Canvas canvas) {
        canvas.drawLine(mBaseLineX, mRectPadding, mBaseLineX, mBezierViewHeight + mRectPadding, mBaseLinePaint);
    }

    /**
     * 画表格里的内容
     * draw content in table
     *
     * @param canvas
     */
    private void drawTab(Canvas canvas) {
        //画边框
        //Draw a border
        drawBezierRect(canvas);
        //画虚线
        //Draw a dotted line
        drawDottedLine(canvas);
        //画速度文字
        //Draw speed text
        drawSpeedText(canvas);
    }

    private void drawSpeedText(Canvas canvas) {
        canvas.drawText(mMaxSpeed + "x", mTabRowHeight / 5 + mRectPadding, mTabRowHeight / 3 + mRectPadding, mSpeedPaint);
        canvas.drawText(mMinSpeed + "x", mTabRowHeight / 5 + mRectPadding, mBezierViewHeight - mTabRowHeight / 5 + mRectPadding, mSpeedPaint);
    }

    /**
     * 画外部的矩形框
     * Draw the outer rectangle
     *
     * @param canvas the canvas
     */
    public void drawBezierRect(Canvas canvas) {
        canvas.drawRect(mRectPadding, mRectPadding, mBezierViewWidth + mRectPadding, mBezierViewHeight + mRectPadding, mPaintRect);
    }

    /**
     * 画虚线
     * Draw a dotted line
     *
     * @param canvas
     */
    private void drawDottedLine(Canvas canvas) {
        for (int i = 1; i < mTabRowNum; i++) {
            mDottedPath.reset();
            int y = i * mTabRowHeight + mRectPadding;
            mDottedPath.moveTo(mRectLineWidth + mRectPadding, y);
            mDottedPath.lineTo(mBezierViewWidth + mRectPadding, y);
            canvas.drawPath(mDottedPath, mDottedLinePaint);
        }
    }

    /**
     * 查看选中的点
     * find selected point
     */
    private int findPressPoint() {
        for (int i = 0; i < list.size(); i++) {
            BzPoint bzPoint = list.get(i);
            if (mDownX >= bzPoint.x - mCircleRadius && mDownX <= bzPoint.x + mCircleRadius
                    && (mDownY >= bzPoint.y - mCircleRadius && mDownY <= bzPoint.y + mCircleRadius)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Gets list.
     * 得到列表
     *
     * @return the list
     */
    public List<BzPoint> getList() {
        return list;
    }

    /**
     * 设置时长
     * set duration
     *
     * @param during 时长
     */
    public void setDuring(long during) {
        mDuring = during;
        if (mBezierViewWidth == 0) {
            return;
        }
        xRatio = mDuring / mBezierViewWidth;
        invalidate();
    }

    public static class BzPoint {

        public BzPoint() {
        }

        public BzPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float x;

        public float y;
    }


    public void setOnBezierListener(OnBezierListener onBezierListener) {
        mOnBezierListener = onBezierListener;
    }

    /**
     * 当前所在位置
     * Current location
     */
    public interface OnBezierListener {

        void onSpeedChanged(String speed, long position);

        void onSelectedPoint(int position);

        void seekPosition(long position);

        void onActionDown();

        void onSelectPoint();

        void onActionUp(long timePoint);
    }


    public static class BezierUtil {


        /**
         * 获取贝塞尔曲线模式速度字符串，只是算出来默认值，放在asset‘文件中，该方法在程序中不起作用
         * Get the speed string of Bézier curve mode, just calculate the default value
         * and put it in the asset file. This method does not work in the program.
         */
        public static void fetchDefaultCurvePoints() {

            List<BzPoint> list = new ArrayList<>();
            list.add(new BzPoint(0, 1f));
            list.add(new BzPoint(0.4f, 1f));
            list.add(new BzPoint(0.6f, 5.2f));
            list.add(new BzPoint(1f, 5.2f));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                BzPoint bzPoint = list.get(i);
                BzPoint afterPoint = null;
                BzPoint prePoint = null;
                double lengthAfter = 0D, lengthPre = 0D;
                if (i != list.size() - 1) {
                    afterPoint = list.get(i + 1);
                    lengthAfter = afterPoint.x - bzPoint.x;
                }

                if (i != 0) {
                    prePoint = list.get(i - 1);
                    lengthPre = bzPoint.x - prePoint.x;
                }
                double p1X, p2X;
                double x = bzPoint.x;
                double y = bzPoint.y;
                builder = BezierUtil.append(builder, x, y);
                if (i == 0) {
                    p1X = x - lengthAfter / 3;
                    builder = BezierUtil.append(builder, p1X, y);
                    p2X = x + lengthAfter / 3;
                    builder = BezierUtil.append(builder, p2X, y);
                } else if (i == list.size() - 1) {
                    p1X = x - lengthPre / 3;
                    builder = BezierUtil.append(builder, p1X, y);
                    p2X = x + lengthPre / 3;
                    builder = BezierUtil.append(builder, p2X, y);
                } else {
                    p1X = x - lengthPre / 3;
                    builder = BezierUtil.append(builder, p1X, y);
                    p2X = x + lengthAfter / 3;
                    builder = BezierUtil.append(builder, p2X, y);
                }
            }
            LogUtils.e("fetchDefaultCurvePoints===" + builder.toString());
        }

        /**
         * 获取贝塞尔曲线的y坐标
         * Get the Y coordinate of Bezier curve
         *
         * @param chartX     the chart x x的图表
         * @param startPoint the start point 开始点
         * @param endPoint   the end point 终点
         * @return double
         */
        public static double calcBezierPointY(float chartX, BzPoint startPoint, BzPoint endPoint) {
            float length = endPoint.x - startPoint.x;
            BzPoint p1 = new BzPoint();
            p1.x = startPoint.x + length / 3;
            p1.y = startPoint.y;
            BzPoint p2 = new BzPoint();
            p2.x = startPoint.x + length / 3 * 2;
            p2.y = endPoint.y;
            double t = 0.5f;
            for (int i = 0; i < 1000; i++) {
                double deltaX1 = bezierPointXFunc(t, chartX, startPoint, endPoint, p1, p2);
                double deltaX2 = bezierDeltaFunc(t, chartX, startPoint, endPoint, p1, p2);
                t -= deltaX1 / deltaX2;
                if (deltaX1 == 0) {
                    break;
                }
            }
            double chartY = bezierPointYFunc(t, startPoint, endPoint, p1, p2);
            return chartY;
        }

        /**
         * Bezier point y func double.
         * Bezier点y的二阶导数。
         *
         * @param t          the t
         * @param startPoint the start point 起点
         * @param endPoint   the end point 结束点
         * @param p1         the p 1 p1
         * @param p2         the p 2 p2
         * @return the double
         */
        public static double bezierPointYFunc(double t, BzPoint startPoint, BzPoint endPoint, BzPoint p1, BzPoint p2) {
            double calcY = (startPoint.y * pow(1 - t, 3) + 3 * p1.y * t * pow(1 - t, 2) + 3 * p2.y * (1 - t) * pow(t, 2) + endPoint.y * pow(t, 3));
            return calcY;
        }

        /**
         * Bezier point x func double.
         * 贝塞尔点(x函数二倍
         *
         * @param t          the t
         * @param target     the target  目标设备
         * @param startPoint the start point 开始点
         * @param endPoint   the end point 终点
         * @param p1         the p 1
         * @param p2         the p 2
         * @return the double
         */
        public static double bezierPointXFunc(double t, float target, BzPoint startPoint, BzPoint endPoint, BzPoint p1, BzPoint p2) {
            double calcX = (startPoint.x * pow(1 - t, 3) + 3 * p1.x * t * pow(1 - t, 2) + 3 * p2.x * (1 - t) * pow(t, 2) + endPoint.x * pow(t, 3));
            return calcX - target;
        }

        /**
         * Bezier delta func double.
         * 贝塞尔三角函数加倍。
         *
         * @param t          the t
         * @param target     the target
         * @param startPoint the start point
         * @param endPoint   the end point
         * @param p1         the p 1
         * @param p2         the p 2
         * @return the double
         */
        public static double bezierDeltaFunc(double t, float target, BzPoint startPoint, BzPoint endPoint, BzPoint p1, BzPoint p2) {
            double dt = 0.00000001;
            double delta1 = BezierUtil.bezierPointXFunc(t, target,
                    startPoint, endPoint, p1, p2);
            double delta2 = BezierUtil.bezierPointXFunc(t - dt, target,
                    startPoint, endPoint, p1, p2);
            return ((delta1 - delta2) / dt);
        }


        public static double pow(double a, double b) {
            return Math.pow(a, b);
        }

        public static float doubleToFloat(double number) {
            BigDecimal bd = new BigDecimal(number);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            return bd.floatValue();
        }

        /**
         * Append string builder.
         * 附加字符串生成器
         *
         * @param builder the builder 建造者
         * @param x       the x x
         * @param y       the y y
         * @return the string builder
         */
        public static StringBuilder append(StringBuilder builder, double x, double y) {
            builder.append("(");
            builder.append(doubleToFloat(x) + ",");
            double speed = y;
            builder.append(doubleToFloat(speed));
            builder.append(")");
            return builder;
        }

    }

    public void reset() {
        initPoint(speedOriginal);
        mBaseLineX = mRectPadding;
        invalidate();
        startPlay();
    }

    public void updateBezierSpeedView(String speed) {
        initPoint(speed);
        mBaseLineX = mRectPadding;
        invalidate();
        startPlay();
    }

    public void setSpeedPoint(String mSpeedPoint) {
        this.mSpeedPoint = mSpeedPoint;
    }

    /**
     * 原始速度点
     * Original velocity point
     *
     * @param speedOriginal the speed original 原来的速度
     */
    public void setSpeedOriginal(String speedOriginal) {
        this.speedOriginal = speedOriginal;
    }

    /**
     * 更新基准线
     * Update baseline
     *
     * @param stamp the stamp
     */
    public void setUpdeteBaseLine(long stamp) {
        float ratio = stamp * 1.0f / mDuring;
        mBaseLineX = mRectPadding + ratio * mBezierViewWidth;
        invalidate();
    }
}
