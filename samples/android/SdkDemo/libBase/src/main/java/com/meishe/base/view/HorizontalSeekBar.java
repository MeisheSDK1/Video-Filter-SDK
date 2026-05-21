package com.meishe.base.view;

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

import com.meishe.base.R;

import androidx.annotation.Nullable;

/**
 * 横向SeekBar 支持刻度、左右双向移动、左->右移动
 * The horizontal SeekBar supports scaling, left-right bidirectional movement, left-> right movement
 */
public class HorizontalSeekBar extends View {
    private final String TAG = "HorizontalSeekBar";
    /*
     * 没有一张拖动图
     * There is no drag diagram
     * */
    private final int IMAGE_NONE = 0;
    /*
     * 只有左拖动图
     * Only drag to the left
     * */
    private final int IMAGE_LEFT = 1;
    /*
     * 只有右拖动图
     * Just drag it to the right
     * */
    private final int IMAGE_RIGHT = 2;
    /*
     * 左右拖动图都有
     * Drag left and right
     * */
    private final int IMAGE_LEFT_RIGHT = 3;
    /**
     * 拖动图片的状态
     * Drag the status of the image
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
     * The height of the word is $100
     */
    private int textHeight;
    /**
     * 游标 图片宽度
     * Cursor width
     */
    private int imageWidth;
    /**
     * 游标 图片距离中轴线下边的距离，默认居中，可往下调节
     * The distance between the cursor picture and the central axis is centered by default and can be adjusted downward
     */
    private int imageLowPadding;
    /**
     * 游标 图片高度
     * Cursor height
     */
    private int imageHeight;
    /**
     * 是否有刻度线
     * Is there a scale line
     */
    private boolean hasRule;
    /**
     * 左边的游标是否在动
     * Is the left cursor moving
     */
    private boolean isLowerMoving;
    /**
     * 右边的游标是否在动
     * Is the cursor on the right moving
     */
    private boolean isUpperMoving;
    /**
     * 左侧字的大小 100$
     * The size of the left word is $100
     */
    private int leftTextSize;
    /**
     * 左侧的颜色 100$
     * The color on the left is $100
     */
    private int leftTextColor;
    /**
     * 右侧字的大小 100$
     * The size of the right word is $100
     */
    private int rightTextSize;
    /**
     * 右侧字的颜色 100$
     * The color of the word on the right hand side is $100
     */
    private int rightTextColor;
    private int middleTextColor;
    /**
     * 两个游标内部 线（进度条）的颜色
     * The color of the inner line (progress bar) of the two cursors
     */
    private int inColor = Color.BLUE;
    /**
     * 两个游标外部 线（左边进度条）的颜色
     * The color of the outer line of the two cursors (the left progress bar)
     */
    private int leftOutColor = Color.BLUE;
    /**
     * 两个游标外部 线（右边进度条）的颜色
     * The color of the outer line of the two cursors (the right-hand progress bar)
     */
    private int rightOutColor = Color.BLUE;
    /**
     * 一个游标的颜色
     */
    private int middleOutColor = Color.BLUE;
    /**
     * 刻度的颜色
     * barColor reference
     */
    private int ruleColor = Color.BLUE;
    /**
     * 刻度上边的字 的颜色
     * The color of the word on the scale
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
    private int minification;
    /**
     * 进度文字精度
     * Progress text accuracy
     */
    private int accuracy;

    /**
     * 加一些padding 大小酌情考虑 为了我们的自定义view可以显示完整
     * Add some padding to it so that our custom view can be rendered completely
     */
    private int paddingLeft = 120;
    private int paddingRight = 120;
    private int paddingTop = 50;
    private int paddingBottom = 50;
    /**
     * 线（进度条） 开始的位置
     * The starting position of a line (progress bar)
     */
    private int lineStart = paddingLeft;
    /**
     * 线的Y轴位置
     * The Y-axis of the line
     */
    private int lineY;
    /**
     * 线（进度条）的结束位置
     * The end of a line (progress bar)
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
     * unit
     */
    private String unit = " ";
    /**
     * 单位份数
     * The unit number
     */
    private int equal = 20;
    /**
     * 刻度单位 $
     * scale base
     */
    private String ruleUnit = " ";
    /**
     * 刻度上边文字的size
     * Scale the size of the text above
     */
    private int ruleTextSize = 20;
    /**
     * 刻度线的高度
     * The height of the scale
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
            } else if (type == R.styleable.HorizontalSeekBar_middleOutColor) {
                middleOutColor = typedArray.getColor(type, Color.YELLOW);
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
            } else if (type == R.styleable.HorizontalSeekBar_middleTextColor) {
                middleTextColor = typedArray.getColor(type, Color.BLUE);
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
            } else if (type == R.styleable.HorizontalSeekBar_lineLength) {
                lineLength = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
            }
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        /**
         * 初始化两个游标的位置
         * Initializes the positions of the two cursors
         * */
        slideLowX = lineStart;
        slideBigX = lineEnd;
        smallRange = smallValue;
        bigRange = bigValue;
        int tempSize = Math.max(leftTextSize, rightTextSize);
        if (hasRule) {
            /*
             * 有刻度时 paddingTop 要加上（text高度）和（刻度线高度加刻度线上边文字的高度和） 之间的最大值
             * The paddingTop is paddinged with the maximum value between (text height) and (scale height plus the height of the text above the scale)
             * */
            paddingTop = paddingTop + Math.max(tempSize, ruleLineHeight + ruleTextSize);
        } else {
            /*
             * 没有刻度时 paddingTop 加上 text的高度
             * PaddingTop plus the height of text without a scale
             * */
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
        /**
         * 缩放图片
         * Zoom photo
         * */
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
        // Log.d("lhz", "width=" + width + "**height=" + height + "**bitmap.width=" + bitmap.getWidth() + "**bitmap.height=" + bitmap.getHeight());
        /*
         * 计算缩放比例
         * ratio maxHeight
         * */
        float scaleWidth = ((float) width) / bitmap.getWidth();
        float scaleHeight = ((float) height) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 设置移动图标的宽高
     * Sets the width and height of the move icon
     *
     * @param width  the width 宽
     * @param height the height 高
     */
    public void setMoveIconSize(int width, int height) {
        imageWidth = dip2px(getContext(), width);
        imageHeight = dip2px(getContext(), height);
    }

    /**
     * 重置状态
     * Reset State
     */
    public void reset() {
        lastLeftProgress = 0;
        lastRightProgress = 0;
        leftValue = 0;
        rightValue = 0;
        lastLeftIconId = 0;
        lastRightIconId = 0;
        imageState = IMAGE_NONE;
        imageLowPadding = 0;
        paddingLeft = 120;
        paddingRight = 120;
        paddingTop = 50;
        paddingBottom = 50;
        bitmapLow = null;
        bitmapBig = null;
        init();
    }

    private int lastLeftIconId = 0;

    /**
     * 设置左侧侧拖动图标
     * 注意:资源不能是shape之类的drawable
     * <p>
     * Set the drag icon on the left side
     * Note: A resource cannot have a drawable like Shape
     *
     * @param resourceId 图片资源id，为0则不显示该图标 Image resource ID, 0 does not display the icon
     */
    public void setLeftMoveIcon(int resourceId) {
        if (lastLeftIconId == resourceId) {
            return;
        }
        lastLeftIconId = resourceId;
        bitmapLow = BitmapFactory.decodeResource(getResources(), resourceId);
        if (bitmapLow == null) {
            /*
             * 如果右侧图标没有了，则重置slideLowX，防止右侧图标无法拖动到尽头
             * If the right side icon is gone, reset slideLowX to prevent the right side icon from dragging to the end
             * */
            slideLowX = lineStart;
            leftValue = 0;
        } else {
            if (imageState == IMAGE_NONE || imageState == IMAGE_LEFT) {
                /*
                 * 如果没有设置过拖动图，或者左边的拖动图变动了，才更改宽高并重新测量
                 * Change width and height and remeasure if the drag diagram has not been set, or if the drag diagram on the left has changed
                 * */
                imageState = IMAGE_LEFT;
            } else if (imageState == IMAGE_RIGHT) {
                imageState = IMAGE_LEFT_RIGHT;
            }
            // Log.d(TAG, "setLeftMoveIcon,imageWidth=" + imageWidth + "**imageHeight=" + imageHeight);
            if (imageWidth > 0 && imageHeight > 0) {
                if (bitmapLow != null) {
                    bitmapLow = createBitmap(bitmapLow, imageWidth, imageHeight);
                }
            }
            requestLayout();
        }
        // Log.d(TAG, "setLeftMoveIcon,imageState=" + imageState + "**bitmapLow=" + bitmapLow);
        invalidate();
    }

    public int getLastLeftIconId() {
        return lastLeftIconId;
    }

    private int lastRightIconId = 0;

    /**
     * 设置右侧拖动图标
     * 注意:资源不能是shape之类的drawable
     * Set the drag icon on the right
     * Note: A resource cannot have a drawable like Shape
     *
     * @param resourceId 图片资源id,为0则不显示该图标 Image resource ID, 0 does not display the icon
     */
    public void setRightMoveIcon(int resourceId) {
        if (lastRightIconId == resourceId) {
            return;
        }
        lastRightIconId = resourceId;
        bitmapBig = BitmapFactory.decodeResource(getResources(), resourceId);
        if (bitmapBig == null) {
            /*
             * 如果右侧图标没有了，则重置slideBigX，防止左侧图标无法拖动到尽头
             * If the right icon is gone, reset the slideBigX to prevent the left icon from dragging to the end
             * */
            slideBigX = lineEnd;
            rightValue = 0;
        } else {
            if (imageState == IMAGE_NONE) {
                /*
                 * 如果没有设置过拖动图，才更改宽高并重新测量
                 * Change the width and height and remeasure if the drag diagram has not been set
                 * */
                imageState = IMAGE_RIGHT;
            } else if (imageState == IMAGE_LEFT) {
                imageState = IMAGE_LEFT_RIGHT;
            }
            // Log.d(TAG,"lhz,setRightMoveIcon,imageWidth=" + imageWidth + "**imageHeight=" + imageHeight);
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
     * If the distance from the central axis is 0 by default, it is centered; if there is a value, it goes down
     *
     * @param padding int 距离值 dp
     */
    public void setMoveIconLowPadding(int padding) {
        imageLowPadding = dip2px(getContext(), padding);
    }

    /**
     * 设置最大进度
     * Set the maximum schedule
     *
     * @param maxProgress int 最大进度值
     */
    public void setMaxProgress(int maxProgress) {
        bigValue = maxProgress;
        requestLayout();
    }

    /**
     * 获取最大进度
     * Get maximum progress
     *
     * @return the max progress 最大进度值
     */
    public int getMaxProgress() {
        return bigValue;
    }

    private int lastLeftProgress = -1;

    /**
     * 设置左边的进度
     * Set the progress on the left
     *
     * @param progress int 进度值
     */
    private boolean mIsMiddle = false;

    public void setLeftProgress(int progress, boolean isMiddle) {
        mIsMiddle = isMiddle;
        if (lastLeftProgress == progress) {
            float tempX = ((float) progress / bigValue * lineLength + lineStart);
            if (Math.abs((tempX - slideLowX)) <= 10) {
                /*
                 * 做最后的挣扎，如果误差较大，说明不是同一个进度
                 * Do the last struggle, if the error is large, it is not the same schedule
                 * */
                Log.d(TAG, "left same progress " + progress);
                return;
            }
        }
        if (progress > bigValue) {
            progress = bigValue;
        }
        if (rightValue + progress >= bigValue) {
            /*
             * 如果右边有进度，并且当前要设置的值少于剩余的值，则设置剩余的值。
             * If there is progress on the right and the current value to be set is less than the remaining value, the remaining value is set
             * */
            progress = bigValue - rightValue;
            leftIsLastMove = false;
        } else {
            leftIsLastMove = true;
        }
        lastLeftProgress = progress;
        slideLowX = ((float) progress / bigValue * lineLength + lineStart);
        // Log.d(TAG, "setLeftProgress,slideLowX=" + slideLowX + "**lineLength=" + lineLength + "**lineStart=" + lineStart + "**progress=" + progress + "**bigValue=" + bigValue);
        updateRange(false);//setLeftProgress
        postInvalidate();
    }

    /**
     * 获取左边的进度
     * Gets the progress on the left
     *
     * @return the left progress
     */
    public int getLeftProgress() {
        return leftValue;
    }

    private int lastRightProgress = -1;

    /**
     * 设置左边的进度
     * Set the progress on the left
     *
     * @param progress int 进度值
     */
    public void setRightProgress(int progress) {

        if (lastRightProgress == progress) {
            float tempX = ((float) progress / bigValue * lineLength + lineEnd);
            if (Math.abs((tempX - slideBigX)) <= 10) {
                Log.d(TAG, "left same progress " + progress);
                return;

            }
        }


        if (progress > bigValue) {
            progress = bigValue;
        }
        if (leftValue + progress >= bigValue) {
            /*
             * 如果左边有进度，并且当前要设置的值少于剩余的值，则设置剩余的值。
             * If there is progress on the left, and the current value to be set is less than the remaining value, the remaining value is set.
             * */
            progress = bigValue - leftValue;
            leftIsLastMove = true;
        } else {
            leftIsLastMove = false;
        }
        lastRightProgress = progress;
        slideBigX = ((float) (bigValue - progress) / bigValue * lineLength + lineStart);
        updateRange(false);//setRightProgress
        postInvalidate();
    }

    /**
     * 获取右边的进度
     * Gets the progress on the right
     *
     * @return the right progress
     */
    public int getRightProgress() {
        return rightValue;
    }

    /**
     * 设置进度文字转化倍数与精度
     * Set progress text conversion multiples and accuracy
     *
     * @param minification int 缩小倍数（如果放大传入小于1的小数即可） Zoom out (if you zoom in and pass in a decimal less than 1)
     * @param accuracy     int 精度，x代表小数点后的位数 Precision, x is the number of digits after the decimal point
     */
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
        //Log.d(TAG, "onMeasure,width=" + width + "**height=" + height);
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            /*
             * matchparent 或者 固定大小 view最小应为 paddingBottom + paddingTop + bitmapHeight + 10 否则显示不全
             * Matchparent or fixed size View minimum paddingBottom + paddingTop + bitmapHeight + 10 otherwise incomplete
             * */
            size = Math.max(size, paddingBottom + paddingTop + imageHeight + 10);
        } else {
            //wrap content
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
            //wrap content
            int width = paddingLeft + paddingRight + imageWidth * 2;
            size = Math.min(size, width);
        }
        /*
         * match parent 或者 固定大小 此时可以获取线（进度条）的长度
         * Match Parent or fixed size can get the length of the line (progress bar) at this point
         * */
        lineLength = size - paddingLeft - paddingRight - imageWidth;
        /*
         * 线（进度条）的结束位置
         * The end of a line (progress bar)
         * */
        lineEnd = lineLength + paddingLeft + imageWidth / 2;
        /*
         * 线（进度条）的开始位置
         * The starting position of a line (progress bar)
         * */
        lineStart = paddingLeft + imageWidth / 2;
        /*
         * 初始化 游标位置
         * Initializes the cursor position
         * */
        if (leftValue > 0) {
            slideLowX = ((float) leftValue / bigValue * lineLength + lineStart);
        } else {
            slideLowX = lineStart;
        }
        // Log.d(TAG, "getMyMeasureWidth,leftValue=" + leftValue + "**slideLowX=" + slideLowX + "**lineStart=" + lineStart + "**lineLength=" + lineLength + "**lineEnd=" + lineEnd + "**lineStart=" + lineStart);
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
        //  Log.d(TAG, "onDraw,lineY=" + lineY + "**textHeight=" + textHeight + "**slideLowX=" + slideLowX + "**slideBigX=" + slideBigX + "**lineLength=" + lineLength + "**lineEnd=" + lineEnd + "**lineStart=" + lineStart);
        /*
         *  Y轴 坐标
         * Y coordinates
         * */
        lineY = getHeight() - paddingBottom - imageHeight / 2;
        /*
         * 字所在高度 100$
         * The height of the word is $100
         * */
        textHeight = lineY - imageHeight / 2 - 10;
        /*
         * 是否画刻度
         * Draw a scale or not
         * */
        if (hasRule) {
            drawRule(canvas);
        }
        if (linePaint == null) {
            linePaint = new Paint();
        }
        /*
         * 画内部线
         * Drew within
         * */
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(inColor);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(slideLowX, lineY, slideBigX, lineY, linePaint);
        if (bitmapLow != null) {
            if (mIsMiddle) {
                linePaint.setColor(middleOutColor);
            } else {
                linePaint.setColor(leftOutColor);
            }
            linePaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            linePaint.setColor(middleOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
        }
        /*
         * 画 外部线,左边
         * Draw the outer line, on the left
         * */
        canvas.drawLine(lineStart, lineY, slideLowX, lineY, linePaint);
        if (bitmapBig != null) {
            linePaint.setColor(rightOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            linePaint.setColor(middleOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
        }
        /*
         * 画 外部线,右边边
         * Draw the outer line, the right-hand side
         * */
        canvas.drawLine(slideBigX, lineY, lineEnd, lineY, linePaint);

        /*
         * 画游标
         * Draw a cursor
         * */
        if (bitmapPaint == null) {
            bitmapPaint = new Paint();
        }
        if (leftIsLastMove) {
            /*
             * 先画右边再画左边的拖动图标，后画的处于最上层
             * Draw the drag icon on the right and then the drag icon on the left, and the drag icon on the top
             * */
            if (bitmapBig != null) {
                canvas.drawBitmap(bitmapBig, slideBigX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
            if (bitmapLow != null) {
                canvas.drawBitmap(bitmapLow, slideLowX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
        } else {
            /*
             * 先画左边再画右边的拖动图标,后画的处于最上层
             * Draw the drag icon on the left and then the drag icon on the right, and the drag icon on the top
             * */
            if (bitmapLow != null) {
                canvas.drawBitmap(bitmapLow, slideLowX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
            if (bitmapBig != null) {
                canvas.drawBitmap(bitmapBig, slideBigX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
        }

        /*
         * 画 游标上边的字
         * Draw the words above the cursor
         * */
        if (textPaint == null) {
            textPaint = new Paint();
        }
        textPaint.setAntiAlias(true);
        if (leftIsLastMove) {
            if (bitmapLow != null) {
                if (mIsMiddle) {
                    textPaint.setColor(middleTextColor);
                } else {
                    textPaint.setColor(leftTextColor);
                }
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
        /*
         * 事件机制
         * event mechanism
         * */
        super.onTouchEvent(event);
        float nowX = event.getX();
        float nowY = event.getY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            /*
             *按下 在线（进度条）范围上
             *Click on the online (progress bar) range
             */
            boolean rightY = Math.abs(nowY - lineY) < imageHeight;
            /*
             * 按下 在左边游标上
             * Press down on the left cursor
             * */
            boolean lowSlide = false;
            if (bitmapLow != null) {
                lowSlide = Math.abs(nowX - slideLowX) < imageWidth;
            }
            boolean bigSlide = false;
            /*
             * 按下 在右边游标上
             * Press down on the right cursor
             * */
            if (bitmapBig != null) {
                /*
                 * 如果有右边的图标
                 * If you have an icon on the right
                 * */
                bigSlide = Math.abs(nowX - slideBigX) < imageWidth;
            }
            if (bigSlide && lowSlide) {
                /*
                 * 如果重合（即都在点击范围内）则上一次滑动过得可以被拖动
                 * If they coincide (that is, they are all within clicking range) then the last slide is so large that it can be dragged
                 * */
                lowSlide = leftIsLastMove;
                bigSlide = !leftIsLastMove;
            }
            //Log.d(TAG, "ACTION_DOWN,lowSlide=" + lowSlide + "**bigSlide=" + bigSlide + "**leftIsLastMove="+leftIsLastMove);
            if (rightY && lowSlide) {
                isLowerMoving = true;
                leftIsLastMove = true;
            } else if (rightY && bigSlide) {
                isUpperMoving = true;
                leftIsLastMove = false;
                /*
                 * 点击了游标外部 的线上
                 * Click on the line outside the cursor
                 * */
            } else if (nowX >= lineStart && nowX <= slideLowX - imageWidth / 2 && rightY) {
                slideLowX = nowX;
                updateRange(true);//"ACTION_DOWN,nowX >= lineStart"
                postInvalidate();
            } else if (nowX <= lineEnd && nowX >= slideBigX + imageWidth / 2 && rightY) {
                slideBigX = nowX;
                updateRange(true);//ACTION_DOWN,nowX <= lineEnd
                postInvalidate();
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            /*
             * 左边游标是运动状态
             * The left cursor is in motion
             * */
            if (isLowerMoving) {
                /*
                 * 当前 X坐标在线上 且在右边游标的左边
                 * The current x-coordinate is online and to the left of the right cursor
                 * */
                int tempWith = 0;
                   /*
                     //左右图标可以重合，所以注释了
                   if (bitmapBig != null) {
                        //如果有右边的图标
                        tempWith = imageWidth;
                    }*/

                //Log.d(TAG, "ACTION_MOVE,nowX=" + nowX + "**slideLowX=" + slideLowX + "**slideBigX=" + slideBigX+"**rightEnd="+rightEnd+"**leftEnd="+leftEnd);
                if (nowX < slideBigX - tempWith && nowX > lineStart - tempWith / 2) {
                    slideLowX = nowX;
                    rightEnd = false;
                    leftEnd = false;
                    /*
                     * 更新进度
                     * update progress
                     * */
                    updateRange(true);//ACTION_MOVE,nowX < slideBigX
                    postInvalidate();
                } else if (!rightEnd && nowX >= slideBigX - tempWith) {
                    /*
                     * 右边尽头处理
                     * Right end treatment
                     * */
                    slideLowX = slideBigX - tempWith;
                    rightEnd = true;
                    updateRange(true);//ACTION_MOVE,!rightEnd
                    postInvalidate();
                } else if (!leftEnd && nowX <= lineStart - tempWith / 2) {
                    //  Log.d(TAG,"leftEnd,slideLowX="+slideLowX+"**lineStart="+lineStart);
                    /*
                     * 左边尽头处理
                     * Left end processing
                     * */
                    leftEnd = true;
                    slideLowX = lineStart;
                    updateRange(true);//ACTION_MOVE,!leftEnd
                    postInvalidate();
                }

            } else if (isUpperMoving) {
                /*
                 * 当前 X坐标在线上 且在左边游标的右边
                 * The current x-coordinate is online and to the right of the left cursor
                 * */
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
                    /*
                     * 更新进度
                     * update progress
                     * */
                    updateRange(true);//ACTION_MOVE,nowX > slideLowX
                    postInvalidate();

                } else if (!leftEnd && nowX <= slideLowX + tempWith) {
                    /*
                     * 左边尽头处理
                     * Left end processing
                     * */
                    slideBigX = slideLowX + tempWith;
                    leftEnd = true;
                    updateRange(true);//ACTION_MOVE,!leftEnd
                    postInvalidate();
                } else if (!rightEnd && nowX >= lineEnd + tempWith / 2) {
                    /*
                     * 右边尽头处理
                     * Right end treatment
                     * */
                    slideBigX = lineEnd;
                    rightEnd = true;
                    updateRange(true);//ACTION_MOVE,!rightEnd
                    postInvalidate();
                }
            }
            /*
             * 手指抬起
             * Finger lift
             * */
        } else if (action == MotionEvent.ACTION_UP) {
            leftEnd = false;
            rightEnd = false;
            isUpperMoving = false;
            isLowerMoving = false;
        }

        return true;
    }

    private void updateRange(boolean callback) {
        leftValue = (int) ((slideLowX - lineStart) * bigValue / lineLength);
        rightValue = (int) (bigValue - (slideBigX - lineStart) * bigValue / lineLength);
        /*
         * 当前 左边游标数值
         * Current left cursor value
         * */
        smallRange = computeRange(slideLowX) / minification;
        /*
         * 当前 右边游标数值
         * Current cursor value on the right
         * */
        bigRange = computeRange(slideBigX);
        bigRange = (bigValue - bigRange) / minification;
        // Log.d(TAG, "updateRange,leftValue=" + leftValue + "**rightValue=" + rightValue + "**slideLowX=" + slideLowX+"**smallRange="+smallRange+"**bigRange="+bigRange);
        /*
         * 接口 实现值的传递
         * The interface implements the passing of values
         * */
        if (onRangeListener != null && callback) {
            onRangeListener.onRange(smallRange, bigRange);
            lastLeftProgress = leftValue;
            lastRightProgress = rightValue;
        }
    }

    /**
     * 获取当前值
     * Get the current value
     */
    private float computeRange(float range) {
        return (range - lineStart) * (bigValue - smallValue) / lineLength + smallValue;
    }

    /**
     * Dip 2 px int.
     * Dip 2px int
     *
     * @param context the context
     * @param dpValue the dp value dp的值
     * @return the int
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 画刻度
     * Drawing scale
     *
     * @param canvas the canvas 画布
     */
    protected void drawRule(Canvas canvas) {
        if (paintRule == null) {
            paintRule = new Paint();
        }
        paintRule.setStrokeWidth(1);
        paintRule.setTextSize(ruleTextSize);
        paintRule.setTextAlign(Paint.Align.CENTER);
        paintRule.setAntiAlias(true);
        /*
         * 遍历 equal份,画刻度
         * Go through the equal, draw the scale
         * */
        for (int i = smallValue; i <= bigValue; i += (bigValue - smallValue) / equal) {
            float degX = lineStart + i * lineLength / (bigValue - smallValue);
            int degY = lineY - ruleLineHeight;
            paintRule.setColor(ruleColor);
            canvas.drawLine(degX, lineY, degX, degY, paintRule);
            paintRule.setColor(ruleTextColor);
            canvas.drawText(String.valueOf(i) + ruleUnit, degX, degY, paintRule);
        }
    }

    /**
     * 写个接口 用来传递最大最小值
     * Write an interface to pass the Max and min values
     */
    public interface onRangeListener {

        void onRange(float low, float big);
    }

    private onRangeListener onRangeListener;

    /**
     * Sets on range listener.
     * 设置范围监听器
     *
     * @param onRangeListener the on range listener
     */
    public void setOnRangeListener(onRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }
}
