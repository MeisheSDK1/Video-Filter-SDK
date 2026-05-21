package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.meishe.sdkdemo.R;

import androidx.annotation.Nullable;

/**
 * 横向SeekBar 支持刻度、左右双向移动、左->右移动
 * Horizontal SeekBar supports scale, left and right two-way movement, left -&gt; Move right
 **/
public class HorizontalSeekBar extends View {
    private final String TAG = "HorizontalSeekBar";
    /**
     * 没有一张拖动图
     * There is not a drag diagram
     */
    private final int IMAGE_NONE = 0;
    /**
     * 只有左拖动图
     * Only drag the image left
     */
    private final int IMAGE_LEFT = 1;
    /**
     * 只有右拖动图
     * Only drag the image to the right
     */
    private final int IMAGE_RIGHT = 2;
    /**
     * 左右拖动图都有
     * Drag it left and right
     */
    private final int IMAGE_LEFT_RIGHT = 3;
    /**
     * 拖动图片的状态
     * Drag the status of the picture
     */
    private int imageState = IMAGE_NONE;
    /**
     * 线条（进度条）的宽度
     * The width of the line (progress bar)
     */
    private int lineWidth;
    /**
     * 线条（进度条）的长度
     * The length of the line (progress bar)
     */
    private int lineLength = 400;
    /**
     * 字所在的高度 100$
     * The height of the word is 100
     */
    private int textHeight;
    /**
     * 游标 图片宽度
     * Cursor picture width
     */
    private int imageWidth;
    /**
     * 游标 图片距离中轴线下边的距离，默认居中，可往下调节
     * The distance between the cursor picture and the central axis is centered by default and can be adjusted downward
     */
    private int imageLowPadding;
    /**
     * 游标 图片高度
     * Cursor picture height
     */
    private int imageHeight;
    /**
     * 是否有刻度线
     * Whether there is a scale line
     */
    private boolean hasRule;
    /**
     * 左边的游标是否在动
     * Whether the left cursor is moving
     */
    private boolean isLowerMoving;
    /**
     * 右边的游标是否在动
     * Whether the cursor on the right is moving
     */
    private boolean isUpperMoving;
    /**
     * 左侧字的大小 100
     * The size of the word on the left side is 100
     */
    private int leftTextSize;
    /**
     * 左侧的颜色 100
     * The color on the left is 100
     */
    private int leftTextColor;
    /**
     * 右侧字的大小 100
     * The size of the word on the right is 100
     */
    private int rightTextSize;
    /**
     * 右侧字的颜色 100
     * The color of the word on the right is 100
     */
    private int rightTextColor;
    /**
     * 两个游标内部 线（进度条）的颜色
     * The color of the inner line of the two cursors (the progress bar)
     */
    private int inColor = Color.BLUE;
    /**
     * 两个游标外部 线（左边进度条）的颜色
     * The color of the two cursors' outer lines (the left progress bar)
     */
    private int leftOutColor = Color.BLUE;
    /**
     * 两个游标外部 线（右边进度条）的颜色
     * The color of the two cursor external lines (the right progress bar)
     */
    private int rightOutColor = Color.BLUE;
    /**
     * 刻度的颜色
     * Color of scale
     */
    private int ruleColor = Color.BLUE;
    /**
     * 刻度上边的字 的颜色
     * The color of the words at the top of the scale
     */
    private int ruleTextColor = Color.BLUE;
    /**
     * 左边图标的图片
     * Picture of the icon on the left
     */
    private Bitmap bitmapLow;
    /**
     * 右边图标 的图片
     * Picture of the icon on the right
     */
    private Bitmap bitmapBig;
    /**
     * 左边图标所在X轴的位置
     * Where the left icon is on the X-axis
     */
    private float slideLowX;
    /**
     * 右边图标所在X轴的位置
     * Where the icon on the right is on the X-axis
     */
    private float slideBigX;

    /**
     * 左边的进度值
     * The progress value on the left
     */
    private int leftValue;
    /**
     * 右边的进度值
     * The progress value on the right
     */
    private int rightValue;

    /**
     * 进度文字缩放因子
     * Progress text scaling factor
     **/
    private int minification = 1;
    /**
     * 进度文字精度
     * Progress text accuracy
     */
    private int accuracy;

    /**
     * 加一些padding 大小酌情考虑 为了我们的自定义view可以显示完整
     * Add some padding to make sure that our custom view is complete
     */
    private int paddingLeft = 100;
    private int paddingRight = 100;
    private int paddingTop = 50;
    private int paddingBottom = 50;
    /**
     * 线（进度条） 开始的位置
     * The position where the line (progress bar) starts
     */
    private int lineStart = paddingLeft;
    /**
     * 线的Y轴位置
     * The Y-axis position of the line
     */
    private int lineY;
    /**
     * 线（进度条）的结束位置
     * End position of the line (progress bar)
     */
    private int lineEnd = lineLength + paddingLeft;
    /**
     * 选择器的最大值
     * The maximum value of the selector
     */
    private int bigValue = 100;
    /**
     * 选择器的最小值
     * The minimum value of the selector
     */
    private int smallValue = 0;
    /**
     * 选择器的当前最小值
     * The current minimum value of the selector
     */
    private float smallRange;
    /**
     * 选择器的当前最大值
     * The current maximum value of the selector
     */
    private float bigRange;
    /**
     * 单位 元
     * Identity element
     */
    private String unit = " ";
    /**
     * 单位份数
     * Unit share
     */
    private int equal = 20;
    /**
     * 刻度单位
     * Scale unit
     */
    private String ruleUnit = " ";
    /**
     * 刻度上边文字的size
     * The size of the text at the top of the scale
     */
    private int ruleTextSize = 20;
    /**
     * 刻度线的高度
     * The height of the scale line
     */
    private int ruleLineHeight = 20;
    private Paint linePaint;
    private Paint bitmapPaint;
    private Paint textPaint;
    private Paint paintRule;

    public HorizontalSeekBar(Context context) {
        this(context, null);
    }

    public HorizontalSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalSeekBar, defStyleAttr, 0);
        int size = typedArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int type = typedArray.getIndex(i);
            if (type == R.styleable.HorizontalSeekBar_inColor) {
                inColor = typedArray.getColor(type, Color.BLACK);
            } else if (type == R.styleable.HorizontalSeekBar_lineHeight) {
                lineWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
            } else if (type == R.styleable.HorizontalSeekBar_leftOutColor) {
                leftOutColor = typedArray.getColor(type, Color.YELLOW);
            } else if (type == R.styleable.HorizontalSeekBar_rightOutColor) {
                rightOutColor = typedArray.getColor(type, Color.YELLOW);
            } else if (type == R.styleable.HorizontalSeekBar_leftTextColor) {
                leftTextColor = typedArray.getColor(type, Color.BLUE);
            } else if (type == R.styleable.HorizontalSeekBar_leftTextSize) {
                leftTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            } else if (type == R.styleable.HorizontalSeekBar_rightTextColor) {
                rightTextColor = typedArray.getColor(type, Color.BLUE);
            } else if (type == R.styleable.HorizontalSeekBar_rightTextSize) {
                rightTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            } else if (type == R.styleable.HorizontalSeekBar_imageLeft) {
                bitmapLow = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
            } else if (type == R.styleable.HorizontalSeekBar_imageRight) {
                bitmapBig = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
            } else if (type == R.styleable.HorizontalSeekBar_imageHeight) {
                imageHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
            } else if (type == R.styleable.HorizontalSeekBar_imageWidth) {
                imageWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
            } else if (type == R.styleable.HorizontalSeekBar_imageLowPadding) {
                imageLowPadding = (int) typedArray.getDimension(type, dip2px(getContext(), 0));
            } else if (type == R.styleable.HorizontalSeekBar_hasRule) {
                hasRule = typedArray.getBoolean(type, false);
            } else if (type == R.styleable.HorizontalSeekBar_ruleColor) {
                ruleColor = typedArray.getColor(type, Color.BLUE);
            } else if (type == R.styleable.HorizontalSeekBar_ruleTextColor) {
                ruleTextColor = typedArray.getColor(type, Color.BLUE);
            } else if (type == R.styleable.HorizontalSeekBar_unit) {
                unit = typedArray.getString(type);
            } else if (type == R.styleable.HorizontalSeekBar_equal) {
                equal = typedArray.getInt(type, 10);
            } else if (type == R.styleable.HorizontalSeekBar_ruleUnit) {
                ruleUnit = typedArray.getString(type);
            } else if (type == R.styleable.HorizontalSeekBar_ruleTextSize) {
                ruleTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
            } else if (type == R.styleable.HorizontalSeekBar_ruleLineHeight) {
                ruleLineHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
            } else if (type == R.styleable.HorizontalSeekBar_bigValue) {
                bigValue = typedArray.getInteger(type, 100);
            } else if (type == R.styleable.HorizontalSeekBar_smallValue) {
                smallValue = typedArray.getInteger(type, 100);
            }
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        //初始化两个游标的位置  Initializes the position of the two cursors
        slideLowX = lineStart;
        slideBigX = lineEnd;
        smallRange = smallValue;
        bigRange = bigValue;
        int tempSize = Math.max(leftTextSize, rightTextSize);
        if (hasRule) {
            //有刻度时 paddingTop 要加上（text高度）和（刻度线高度加刻度线上边文字的高度和） 之间的最大值
            //paddingTop Specifies the maximum value between (text height) and (scale height plus the height of the text above the scale and) if there is a scale
            paddingTop = paddingTop + Math.max(tempSize, ruleLineHeight + ruleTextSize);
        } else {
            //没有刻度时 paddingTop 加上 text的高度 The height of paddingTop plus text when there is no scale
            paddingTop = paddingTop + tempSize;
        }
        if (bitmapLow == null && bitmapBig == null) {
            return;
        }
        if (bitmapBig != null && bitmapLow != null) {
            imageState = IMAGE_LEFT_RIGHT;
        } else if (bitmapLow != null) {
            imageState = IMAGE_LEFT;
        } else {
            imageState = IMAGE_RIGHT;
        }
        //缩放图片 Zoom picture
        if (bitmapLow != null) {
            bitmapLow = createBitmap(bitmapLow, imageWidth, imageHeight);
        }
        if (bitmapBig != null) {
            bitmapBig = createBitmap(bitmapBig, imageWidth, imageHeight);
        }
    }

    private Bitmap createBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            return null;
        }
        // 计算缩放比例 Computed scaling
        float scaleWidth = ((float) width) / bitmap.getWidth();
        float scaleHeight = ((float) height) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 设置移动图标的宽高
     * Set the width and height of the move icon
     **/
    public void setMoveIconSize(int width, int height) {
        imageWidth = dip2px(getContext(), width);
        imageHeight = dip2px(getContext(), height);
    }

    /**
     * 重置状态
     * Reset state
     **/
    public void reset() {
        lastLeftProgress = 0;
        lastRightProgress = 0;
        leftValue = 0;
        rightValue = 0;
        lastLeftIconId = 0;
        lastRightIconId = 0;
        imageState = IMAGE_NONE;
        imageLowPadding = 0;
        bitmapLow = null;
        bitmapBig = null;
        paddingTop = 50;
        init();
    }

    private int lastLeftIconId = 0;

    /**
     * 设置左侧侧拖动图标
     * 注意:资源不能是shape之类的drawable
     * Set the left drag icon
     * Note: The resource cannot be a drawable like a shape
     *
     * @param resourceId 图片资源id，为0则不显示该图标 Image resource id. If it is 0, the icon is not displayed
     **/
    public void setLeftMoveIcon(int resourceId) {
        if (lastLeftIconId == resourceId) {
            return;
        }
        lastLeftIconId = resourceId;
        bitmapLow = BitmapFactory.decodeResource(getResources(), resourceId);
        if (bitmapLow == null) {
            //如果右侧图标没有了，则重置slideLowX，防止右侧图标无法拖动到尽头
            slideLowX = lineStart;
            leftValue = 0;
        } else {
            if (imageState == IMAGE_NONE || imageState == IMAGE_LEFT) {
                //如果没有设置过拖动图，或者左边的拖动图变动了，才更改宽高并重新测量
                //If the right icon is gone, reset the slideLowX so that the right icon cannot be dragged to the end
                imageState = IMAGE_LEFT;
            } else if (imageState == IMAGE_RIGHT) {
                imageState = IMAGE_LEFT_RIGHT;
            }
            if (imageWidth > 0 && imageHeight > 0) {
                if (bitmapLow != null) {
                    bitmapLow = createBitmap(bitmapLow, imageWidth, imageHeight);
                }
            }
            requestLayout();
        }
        invalidate();
    }

    private int lastRightIconId = 0;

    /**
     * 设置右侧拖动图标
     * 注意:资源不能是shape之类的drawable
     * Set the drag icon on the right
     * Note: The resource cannot be a drawable like a shape
     *
     * @param resourceId 图片资源id,为0则不显示该图标
     *                   Image resource id. If it is 0, the icon is not displayed
     **/
    public void setRightMoveIcon(int resourceId) {
        if (lastRightIconId == resourceId) {
            return;
        }
        lastRightIconId = resourceId;
        bitmapBig = BitmapFactory.decodeResource(getResources(), resourceId);
        if (bitmapBig == null) {
            //如果右侧图标没有了，则重置slideBigX，防止左侧图标无法拖动到尽头
            //If the right icon is gone, reset slideBigX so that the left icon cannot be dragged to the end
            slideBigX = lineEnd;
            rightValue = 0;
        } else {
            if (imageState == IMAGE_NONE) {
                //如果没有设置过拖动图，才更改宽高并重新测量
                //If the drag map has not been set, change the width and height and re-measure
                imageState = IMAGE_RIGHT;
            } else if (imageState == IMAGE_LEFT) {
                imageState = IMAGE_LEFT_RIGHT;
            }
            if (imageWidth > 0 && imageHeight > 0) {
                if (bitmapBig != null) {
                    bitmapBig = createBitmap(bitmapBig, imageWidth, imageHeight);
                }
            }
            requestLayout();
        }
        invalidate();
    }

    /**
     * 距离中轴线上下距离，默认为0则居中，有值则往下
     * Distance from the central axis above and below, the default is 0, the center, the value is down
     *
     * @param padding int 距离值 dp
     **/
    public void setMoveIconLowPadding(int padding) {
        imageLowPadding = dip2px(getContext(), padding);
    }

    /**
     * 设置最大进度
     * Set maximum progress
     *
     * @param maxProgress int 最大进度值 Maximum progress value
     **/
    public void setMaxProgress(int maxProgress) {
        bigValue = maxProgress;
        requestLayout();
    }

    /**
     * 获取最大进度
     * Get maximum progress
     **/
    public int getMaxProgress() {
        return bigValue;
    }

    private int lastLeftProgress = -1;

    /**
     * 设置左边的进度
     * Set the progress on the left
     *
     * @param progress int 进度值  Progress value
     **/
    public void setLeftProgress(int progress) {
        if (lastLeftProgress == progress) {
            float tempX = ((float) progress / bigValue * lineLength + lineStart);
            if (Math.abs((tempX - slideLowX)) <= 10) {
                //做最后的计算，如果误差较大，说明不是同一个进度
                //Do the final calculation. If the error is large, it indicates that the progress is not the same
                Log.d(TAG, "left same progress " + progress);
                return;
            }
        }
        if (progress > bigValue) {
            progress = bigValue;
        }
        if (rightValue + progress >= bigValue) {
            //如果右边有进度，并且当前要设置的值少于剩余的值，则设置剩余的值。
            //If there is progress on the right and the current value to set is less than the remaining value, set the remaining value.
            progress = bigValue - rightValue;
            leftIsLastMove = false;
        } else {
            leftIsLastMove = true;
        }
        lastLeftProgress = progress;
        slideLowX = ((float) progress / bigValue * lineLength + lineStart);
        updateRange(false);
        postInvalidate();
    }

    /**
     * 获取左边的进度
     * Gets the progress on the left
     **/
    public int getLeftProgress() {
        return leftValue;
    }

    private int lastRightProgress = -1;

    /**
     * 设置左边的进度
     * Set the progress on the left
     *
     * @param progress int 进度值 Progress value
     **/
    public void setRightProgress(int progress) {
        if (lastRightProgress == progress) {
            Log.d(TAG, "right same progress");
            return;
        }
        if (progress > bigValue) {
            progress = bigValue;
        }
        if (leftValue + progress >= bigValue) {
            //如果左边有进度，并且当前要设置的值少于剩余的值，则设置剩余的值。
            //If there is progress on the left and the current value to set is less than the remaining value, set the remaining value.
            progress = bigValue - leftValue;
            leftIsLastMove = true;
        } else {
            leftIsLastMove = false;
        }
        lastRightProgress = progress;
        slideBigX = ((float) (bigValue - progress) / bigValue * lineLength + lineStart);
        updateRange(false);
        postInvalidate();
    }

    /**
     * 获取右边的进度
     * Gets the progress on the right
     **/
    public int getRightProgress() {
        return rightValue;
    }

    /**
     * 设置进度文字转化倍数与精度
     * Set the progress text conversion multiple and precision
     *
     * @param minification int 缩小倍数（如果放大传入小于1的小数即可） Reduce the multiple (if you zoom in and pass in a decimal less than 1)
     * @param accuracy     int 精度，x代表小数点后的位数 Precision, x is the number of digits after the decimal point
     **/
    public void setTransformText(int minification, int accuracy) {
        if (minification == 0) {
            minification = 1;
        }
        this.minification = Math.abs(minification);
        this.accuracy = Math.abs(accuracy);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMyMeasureWidth(widthMeasureSpec);
        int height = getMyMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            // matchparent 或者 固定大小 view最小应为 paddingBottom + paddingTop + bitmapHeight + 10 否则显示不全
            //The minimum size of the matchparent or fixed size view must be paddingBottom + paddingTop + bitmapHeight + 10. Otherwise, the view cannot be fully displayed
            size = Math.max(size, paddingBottom + paddingTop + imageHeight + 10);
        } else {
            int height = paddingBottom + paddingTop + imageHeight + 10;
            size = Math.min(size, height);
        }
        return size;
    }

    private int getMyMeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            size = Math.max(size, paddingLeft + paddingRight + imageWidth * 2);
        } else {
            int width = paddingLeft + paddingRight + imageWidth * 2;
            size = Math.min(size, width);
        }
        // match parent 或者 固定大小 此时可以获取线（进度条）的长度
        //match parent or fixed size to get the length of the progress bar
        lineLength = size - paddingLeft - paddingRight - imageWidth;
        //线（进度条）的结束位置 End position of the line (progress bar)
        lineEnd = lineLength + paddingLeft + imageWidth / 2;
        //线（进度条）的开始位置 The start position of the line (progress bar)
        lineStart = paddingLeft + imageWidth / 2;
        if (leftValue > 0) {
            slideLowX = ((float) leftValue / bigValue * lineLength + lineStart);
        } else {
            slideLowX = lineStart;
        }
        if (rightValue > 0) {
            slideBigX = ((float) (bigValue - rightValue) / bigValue * lineLength + lineStart);
        } else {
            slideBigX = lineEnd;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Y轴 坐标  Y-axis coordinate
        lineY = getHeight() - paddingBottom - imageHeight / 2;
        // 字所在高度 100 Word height
        textHeight = lineY - imageHeight / 2 - 10;
        //是否画刻度 Mark or not
        if (hasRule) {
            drawRule(canvas);
        }
        if (linePaint == null) {
            linePaint = new Paint();
        }
        //画内部线 Interior line
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(inColor);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(slideLowX, lineY, slideBigX, lineY, linePaint);
        if (bitmapLow != null) {
            linePaint.setColor(leftOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
            //画 外部线,左边 Draw the outer line, to the left
            canvas.drawLine(lineStart, lineY, slideLowX, lineY, linePaint);
        }
        if (bitmapBig != null) {
            linePaint.setColor(rightOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
            //画 外部线,右边边 Draw the outside line, the right side
            canvas.drawLine(slideBigX, lineY, lineEnd, lineY, linePaint);
        }
        //画游标 Draw cursor
        if (bitmapPaint == null) {
            bitmapPaint = new Paint();
        }
        if (leftIsLastMove) {
            //先画右边再画左边的拖动图标，后画的处于最上层
            //Draw the drag icon on the right and then the drag icon on the left, and then the drag icon on the top
            if (bitmapBig != null) {
                canvas.drawBitmap(bitmapBig, slideBigX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
            if (bitmapLow != null) {
                canvas.drawBitmap(bitmapLow, slideLowX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
        } else {
            //先画左边再画右边的拖动图标,后画的处于最上层
            //Draw the drag icon on the left and then on the right, with the drag icon on the top
            if (bitmapLow != null) {
                canvas.drawBitmap(bitmapLow, slideLowX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
            if (bitmapBig != null) {
                canvas.drawBitmap(bitmapBig, slideBigX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
        }

        //画游标上边的字 Draw the word above the cursor
        if (textPaint == null) {
            textPaint = new Paint();
        }
        textPaint.setAntiAlias(true);
        if (leftIsLastMove) {
            if (bitmapLow != null) {
                textPaint.setColor(leftTextColor);
                textPaint.setTextSize(leftTextSize);
                canvas.drawText(String.format("%." + accuracy + "f" + unit, smallRange), slideLowX - imageWidth / 2, textHeight, textPaint);
            }
        } else {
            if (bitmapBig != null) {
                textPaint.setColor(rightTextColor);
                textPaint.setTextSize(rightTextSize);
                canvas.drawText(String.format("%." + accuracy + "f" + unit, bigRange), slideBigX - imageWidth / 2, textHeight, textPaint);
            }
        }
    }

    private boolean leftEnd;
    private boolean rightEnd;
    private boolean leftIsLastMove = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float nowX = event.getX();
        float nowY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下在线（进度条）范围上 Press on the online (progress bar) range
                boolean rightY = Math.abs(nowY - lineY) < imageHeight;
                //按下在左边游标上 Press on the left cursor
                boolean lowSlide = false;
                if (bitmapLow != null) {
                    lowSlide = Math.abs(nowX - slideLowX) < imageWidth;
                }
                boolean bigSlide = false;
                //按下在右边游标上 Press on the right cursor
                if (bitmapBig != null) {//如果有右边的图标 If you have the icon on the right
                    bigSlide = Math.abs(nowX - slideBigX) < imageWidth;
                }
                if (bigSlide && lowSlide) {
                    //如果重合（即都在点击范围内）则上一次滑动过得可以被拖动
                    //If it is overlapped (that is, both within the click range), the previous slide can be dragged
                    lowSlide = leftIsLastMove;
                    bigSlide = !leftIsLastMove;
                }
                if (rightY && lowSlide) {
                    isLowerMoving = true;
                    leftIsLastMove = true;
                } else if (rightY && bigSlide) {
                    isUpperMoving = true;
                    leftIsLastMove = false;
                    //点击了游标外部的线上 Click on the line outside the cursor
                } else if (nowX >= lineStart && nowX <= slideLowX - imageWidth / 2 && rightY) {
                    slideLowX = nowX;
                    updateRange(true);
                    postInvalidate();
                } else if (nowX <= lineEnd && nowX >= slideBigX + imageWidth / 2 && rightY) {
                    slideBigX = nowX;
                    updateRange(true);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //左边游标是运动状态 The left cursor is in motion
                if (isLowerMoving) {
                    //当前 X坐标在线上 且在右边游标的左边 The current X coordinate is on the line and to the left of the right cursor
                    int tempWith = 0;
                   /*
                     //左右图标可以重合，所以注释了
                   if (bitmapBig != null) {
                        //如果有右边的图标
                        tempWith = imageWidth;
                    }*/

                    if (nowX < slideBigX - tempWith && nowX > lineStart - tempWith / 2) {
                        slideLowX = nowX;
                        rightEnd = false;
                        leftEnd = false;
                        //更新进度 Update progress
                        updateRange(true);
                        postInvalidate();
                    } else if (!rightEnd && nowX >= slideBigX - tempWith) {
                        //右边尽头处理 Right end treatment
                        slideLowX = slideBigX - tempWith;
                        rightEnd = true;
                        updateRange(true);
                        postInvalidate();
                    } else if (!leftEnd && nowX <= lineStart - tempWith / 2) {
                        //左边尽头处理 Left end treatment
                        leftEnd = true;
                        slideLowX = lineStart;
                        updateRange(true);
                        postInvalidate();
                    }

                } else if (isUpperMoving) {
                    //当前 X坐标在线上 且在左边游标的右边
                    //The current X coordinate is on the line and to the right of the left cursor
                    int tempWith = 0;
                   /*
                   左右图标可以重合，所以注释了
                   if (bitmapLow != null) {
                        //如果有右边的图标
                        tempWith = imageWidth;
                    }*/
                    if (nowX > slideLowX + tempWith && nowX < lineEnd + tempWith / 2) {
                        slideBigX = nowX;
                        rightEnd = false;
                        leftEnd = false;
                        if (slideBigX > lineEnd) {
                            slideBigX = lineEnd;
                        }
                        //更新进度 Update progress
                        updateRange(true);//ACTION_MOVE,nowX > slideLowX
                        postInvalidate();

                    } else if (!leftEnd && nowX <= slideLowX + tempWith) {
                        //左边尽头处理 Left end treatment
                        slideBigX = slideLowX + tempWith;
                        leftEnd = true;
                        updateRange(true);//ACTION_MOVE,!leftEnd
                        postInvalidate();
                    } else if (!rightEnd && nowX >= lineEnd + tempWith / 2) {
                        //右边尽头处理 Right end treatment
                        slideBigX = lineEnd;
                        rightEnd = true;
                        updateRange(true);//ACTION_MOVE,!rightEnd
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                leftEnd = false;
                rightEnd = false;
                if (onRangeListener != null && (isLowerMoving || isUpperMoving)) {
                    onRangeListener.onTouchUpLeft(isLowerMoving);
                }
                isUpperMoving = false;
                isLowerMoving = false;
                break;
            default:
                break;
        }

        return true;
    }

    private void updateRange(boolean callback) {
        leftValue = (int) ((slideLowX - lineStart) * bigValue / lineLength);
        rightValue = (int) (bigValue - (slideBigX - lineStart) * bigValue / lineLength);
        //当前 左边游标数值 Current value of the left cursor
        smallRange = computeRange(slideLowX) / minification;
        //当前 右边游标数值 Current value of the right cursor
        bigRange = computeRange(slideBigX);
        bigRange = (bigValue - bigRange) / minification;
        if (onRangeListener != null && callback) {
            onRangeListener.onRange(smallRange, bigRange);
            lastLeftProgress = leftValue;
            lastRightProgress = rightValue;
        }
    }

    /**
     * 获取当前值
     * Get current value
     */
    private float computeRange(float range) {
        return (range - lineStart) * (bigValue - smallValue) / lineLength + smallValue;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 画刻度
     * Draw scale
     */
    protected void drawRule(Canvas canvas) {
        if (paintRule == null) {
            paintRule = new Paint();
        }
        paintRule.setStrokeWidth(1);
        paintRule.setTextSize(ruleTextSize);
        paintRule.setTextAlign(Paint.Align.CENTER);
        paintRule.setAntiAlias(true);
        //遍历 equal份,画刻度 Go through the equal parts and draw the scale
        for (int i = smallValue; i <= bigValue; i += (bigValue - smallValue) / equal) {
            float degX = lineStart + i * lineLength / (bigValue - smallValue);
            int degY = lineY - ruleLineHeight;
            paintRule.setColor(ruleColor);
            canvas.drawLine(degX, lineY, degX, degY, paintRule);
            paintRule.setColor(ruleTextColor);
            canvas.drawText(String.valueOf(i) + ruleUnit, degX, degY, paintRule);
        }
    }

    public interface onRangeListener {
        void onRange(float low, float big);

        void onTouchUpLeft(boolean leftFlag);
    }

    private onRangeListener onRangeListener;

    public void setOnRangeListener(onRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }
}
