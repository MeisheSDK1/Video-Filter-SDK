package com.meishe.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.view.BubbleView;
import com.meishe.sdkdemo.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;


public class ColorSeekBar extends View {
    private static final String SEEKBAR_STYLE_NONE = "none";
    private static final String SEEKBAR_STYLE_BUBBLE = "bubble";
    private final int[] mColors = new int[]{0xFFFF0000, 0xFFFF00FF,
            0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
    private OnColorChangedListener mOnColorChangedListener;
    private String mseekBarStyle = SEEKBAR_STYLE_NONE;
    private Paint mColorPaint;
    private Paint mThumbPaint;
    /**
     * 气泡相关
     */
    private final int[] mDrawingLocation = new int[2];
    private WindowManager windowManager;
    private BubbleView mBubbleView;
    private WindowManager.LayoutParams managerLayoutParams;
    /**
     * 进度条的高度
     */
    private float mProgressHeight;
    /**
     * 滑块的边长
     */
    private float mThumbSideLength;
    /**
     * 滑块与进度条之间的间距
     */
    private float mThumbSpace;
    private LinearGradient mLinearGradient;
    /**
     * 布局宽 Layout width
     */
    private int viewWidth;
    /**
     * 布局高 Layout height
     */
    private int viewHeight;
    private float textSize;
    private int textColor;
    /**
     * 气泡是否显示
     */
    private boolean isBubbleShow = false;

    public float rawX;

    public ColorSeekBar(Context context) {
        super(context);
    }


    public ColorSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);
        setFocusable(true);
        setClickable(true);
        initAttr(context, attrs);
        initView();
        rawX = mThumbSideLength / 2;
    }

    @SuppressLint("Recycle")
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, com.meishe.base.R.styleable.ColorSeekBar);
        mProgressHeight = array.getDimensionPixelSize( com.meishe.base.R.styleable.ColorSeekBar_progressHeight, ScreenUtils.dip2px(context, 5));
        mThumbSideLength = array.getDimensionPixelSize( com.meishe.base.R.styleable.ColorSeekBar_thumbSideLength, ScreenUtils.dip2px(context, 5));
        mseekBarStyle = array.getString( com.meishe.base.R.styleable.ColorSeekBar_colorBarStyle);
        textSize = array.getDimension( com.meishe.base.R.styleable.ColorSeekBar_textSizeBar, 22);
        textColor = array.getColor(com.meishe.base.R.styleable.ColorSeekBar_textColorBar, Color.parseColor("#ffffff"));
        mThumbSpace = ScreenUtils.dip2px(context, 2);
        array.recycle();
    }


    private void initView() {
        mColorPaint = new Paint();
        mColorPaint.setAntiAlias(true);

        mThumbPaint = new Paint();
        mThumbPaint.setAntiAlias(true);
        mThumbPaint.setStrokeJoin(Paint.Join.ROUND);
        mThumbPaint.setColor(Color.WHITE);
        mThumbPaint.setStyle(Paint.Style.FILL);

        if (TextUtils.equals(mseekBarStyle, SEEKBAR_STYLE_BUBBLE)) {
            windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            mBubbleView = new BubbleView(getContext());
            mBubbleView.setSize("#FFFFFFFF", textSize);
            mBubbleView.setTextSize(textSize);
            mBubbleView.setTextColor(textColor);
            managerLayoutParams = new WindowManager.LayoutParams();
            managerLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
            managerLayoutParams.flags = computeFlags(managerLayoutParams.flags);
            managerLayoutParams.gravity = Gravity.CENTER;
            managerLayoutParams.format = PixelFormat.TRANSLUCENT;
            managerLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            managerLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            managerLayoutParams.token = this.getWindowToken();
        }
    }

    private int computeFlags(int curFlags) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        return curFlags;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            viewWidth = w;
            viewHeight = h;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
         * 绘制颜色进度条
         * Draws a color progress bar
         */
        float sideValue = mThumbSideLength / 2;
        if (null == mLinearGradient) {
            mLinearGradient = new LinearGradient(
                    mThumbSideLength / 2, 0
                    , viewWidth - mThumbSideLength / 2, 0
                    , mColors
                    , null, Shader.TileMode.CLAMP);
            mColorPaint.setShader(mLinearGradient);
        }
        canvas.drawRoundRect(sideValue, viewHeight / 2F - mProgressHeight
                , viewWidth - sideValue, viewHeight / 2F
                , mProgressHeight / 2, mProgressHeight / 2, mColorPaint);
        /*
         * 绘制滑块
         * Draw slider
         */
        float startX = rawX;
        float startY = viewHeight / 2F + mThumbSpace;
        Path path = new Path();
        path.lineTo(startX, startY);
        path.lineTo(startX - sideValue, startY + sideValue * 2);
        path.lineTo(startX + sideValue, startY + sideValue * 2);
        path.lineTo(startX, startY);
        canvas.drawPath(path, mThumbPaint);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showBubble();
                getColorInPosition(event);
                break;
            case MotionEvent.ACTION_MOVE:
                getColorInPosition(event);
                break;
            case MotionEvent.ACTION_UP:
                hideBubble();
                getColorInPosition(event);
                break;
            default:
                break;

        }
        return true;
    }

    private void getColorInPosition(MotionEvent event) {
        rawX = event.getX();
        if (rawX <= mThumbSideLength / 2) {
            rawX = mThumbSideLength / 2;
        }
        if (rawX >= getMeasuredWidth() - mThumbSideLength / 2) {
            rawX = getMeasuredWidth() - mThumbSideLength / 2;
        }
        float unit = rawX / (getMeasuredWidth() - mThumbSideLength);
        int mColor = interpColor(mColors, unit);
        if (isBubbleShow) {
            String text = int2ArgbString(mColor).toUpperCase();
            mBubbleView.setFloatOffset(text, rawX + mDrawingLocation[0], -1);
        }
        if (mOnColorChangedListener != null) {
            mOnColorChangedListener.onColorChanged(mColor);
            mOnColorChangedListener.onColorChanged(mColor, rawX);
        }
        invalidate();
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    private int interpColor(int[] colors, float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        return Color.argb(a, r, g, b);
    }

    private void showBubble() {
        if (!TextUtils.equals(mseekBarStyle, SEEKBAR_STYLE_BUBBLE)) {
            return;
        }
        if ((null == mBubbleView) || (null == managerLayoutParams)) {
            return;
        }
        if (isBubbleShow) {
            return;
        }
        isBubbleShow = true;
        getLocationOnScreen(mDrawingLocation);
        managerLayoutParams.x = 0;
        managerLayoutParams.y = (mDrawingLocation[1] - mBubbleView.getBubbleHeight()) + (viewHeight / 2) - 2 * getContext().getResources().getDimensionPixelSize(com.meishe.base.R.dimen.dp_px_24);
        managerLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        managerLayoutParams.gravity = Gravity.TOP | GravityCompat.START;
        windowManager.addView(mBubbleView, managerLayoutParams);
    }

    private void hideBubble() {
        if (!TextUtils.equals(mseekBarStyle, SEEKBAR_STYLE_BUBBLE)) {
            return;
        }
        if ((null == windowManager) || (null == mBubbleView)) {
            return;
        }
        isBubbleShow = false;
        windowManager.removeViewImmediate(mBubbleView);
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.mOnColorChangedListener = onColorChangedListener;
    }

    public void setColors(int[] mColors, float colorsProgress) {
        if (colorsProgress < 0) {
            rawX = ScreenUtils.dip2px(getContext(), 5.0f);
        } else {
            rawX = colorsProgress;
        }
        invalidate();
    }


    public void setColors(float colorsProgress) {
        if (colorsProgress < 0) {
            rawX = ScreenUtils.dip2px(getContext(), 5.0f);
        } else {
            rawX = colorsProgress;
        }
        invalidate();
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);

        void onColorChanged(int color, float progress);
    }

    public static String int2ArgbString(final int colorInt) {
        String color = Integer.toHexString(colorInt);
        while (color.length() < 6) {
            color = "0" + color;
        }
        while (color.length() < 8) {
            color = "f" + color;
        }
        return "#" + color;
    }
}
