package com.meishe.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.meishe.base.R;
import com.meishe.base.utils.ScreenUtils;

import androidx.annotation.Nullable;


/**
 * Created by MZ008064 on 2020/6/4.
 *
 * @author zcy
 * @Date 2020/6/4.
 */
public class MagicBubbleProgress extends View {
    private static final String TEXT_BG_STYLE_NONE = "none";
    private static final String TEXT_BG_STYLE_BUBBLE = "bubble";

    private WindowManager windowManager;
    private BubbleViewHandler mBubbleView;
    private float baseProgress = 1f;
    /**
     * 滚动条背景色  Scroll bar background color
     */
    private int progressBackGround;
    /**
     * 滚动条宽度  Scrollbar width
     */
    private int progressWidth;
    /**
     * 滚动条颜色   Scroll bar color
     */
    private int progressForwardGround;
    /**
     * 指示器背景色  Indicator back view
     */
    private int thumbColor;
    /**
     * 吸附点颜色  Adsorption point color
     */
    private int pointColor;
    /**
     * 指示器半径 Indicator radius
     */
    private int thumbRadios;
    /**
     * 圆点半径 Dot radius
     */
    private int pointRadios;
    /**
     * 最大值 Maximum value
     */
    private int max;
    /**
     * 最小值 Minimum value
     */
    private int min;
    /**
     * 当前值 Current value
     */
    private int progress;
    /**
     * 吸附点位置 Adsorption point position
     */
    private int pointProgress;
    /**
     * 距离吸附点多远吸附  How far is it from the adsorption point
     */
    private int pointRange;
    /**
     * 是否有显示断点功能  Whether the breakpoint function is displayed
     */
    private boolean pointEnable = true;
    /**
     * 是否有吸附功能 Whether it has adsorption function
     */
    private boolean isAuto = true;
    /**
     * 布局宽 Layout width
     */
    private int viewWidth;
    /**
     * 布局高 Layout height
     */
    private int viewHeight;
    private Paint backGroundPaint;
    /**
     * 断点画笔 Breakpoint brush
     */
    private Paint pointPaint;
    /**
     * 滚动条画笔  Scroll brush
     */
    private Paint progressPaint;
    /**
     * 指示器画笔  Indicator brush
     */
    private Paint thumbPaint;
    private float thumbX;
    private float thumbY;
    /**
     * 是否可以移动指示器  Whether the indicator can be moved
     */
    private boolean canMoveThumb = false;
    /**
     * 是否可以显示文字 Whether text can be displayed
     */
    private boolean showTextEnable = true;
    boolean animalAlpha = false;
    /**
     * 是否有折中值
     * Is there a middle ground
     */
    private boolean isShowBreak = true;
    private int breakProgress = 100;
    private Paint textPaint, bubblePaint;
    private Path mPathBubble;
    private float textSize;
    private int textColor;
    private float textSpace;
    private float paddingSpace;
    private float progressLength;
    /**
     * 显示字体透明度  Display font transparency
     */
    private int textAlpha = 0;
    private int shadowColor;
    private float shadowRadios;
    /**
     * 调节偏移的触摸偏移量（按压灵敏度） Adjust offset touch offset (press sensitivity)
     */
    private float touchRatio = 2;
    /**
     * 是否显示滚动条阴影
     * Whether to display scrollbar shadows
     */
    private boolean isShowShadowLayer = false;
    private String textBgStyle = TEXT_BG_STYLE_NONE;
    /**
     * 指示器文字是否长显
     */
    private boolean isTextLongShow = true;
    /**
     * 气泡是否显示
     */
    private boolean isBubbleShow = false;

    public void setPointEnable(boolean pointEnable) {
        this.pointEnable = pointEnable;
        setAuto(pointEnable);
    }

    public void setBreakProgress(int breakProgress) {
        this.breakProgress = breakProgress;
    }

    /**
     * 调节按压灵敏度
     * Adjusting press sensitivity
     *
     * @param touchRatio
     */
    public void setTouchRatio(float touchRatio) {
        this.touchRatio = touchRatio;
    }

    public void setShowTextEnable(boolean showTextEnable) {
        this.showTextEnable = showTextEnable;
    }

    private OnProgressChangeListener listener;

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.listener = listener;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public MagicBubbleProgress(Context context) {
        this(context, null);
    }

    public MagicBubbleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicBubbleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initData();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MagicProgress);
        progressBackGround = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#80ffffff"));
        progressWidth = array.getDimensionPixelSize(R.styleable.MagicProgress_progressWidthMagic, ScreenUtils.dip2px(context, 2));
        progressForwardGround = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#ffffffff"));
        thumbColor = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#ffffffff"));
        pointColor = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#ffffffff"));
        thumbRadios = array.getDimensionPixelSize(R.styleable.MagicProgress_progressWidthMagic, ScreenUtils.dip2px(context, 8));
        pointRadios = array.getDimensionPixelSize(R.styleable.MagicProgress_progressWidthMagic, ScreenUtils.dip2px(context, 4));
        max = array.getInteger(R.styleable.MagicProgress_max, 100);
        min = array.getInteger(R.styleable.MagicProgress_min, 0);
        progress = array.getInteger(R.styleable.MagicProgress_magicProgress, 50);
        pointProgress = array.getInteger(R.styleable.MagicProgress_pointProgress, 80);
        pointRange = array.getInteger(R.styleable.MagicProgress_pointRange, 3);
        textSize = array.getDimension(R.styleable.MagicProgress_textSizeMagic, 22);
        textColor = array.getColor(R.styleable.MagicProgress_textColorMagic, Color.parseColor("#ffffff"));
        textSpace = ScreenUtils.dip2px(context, 5);
        paddingSpace = ScreenUtils.dip2px(context, 5);
        shadowColor = array.getColor(R.styleable.MagicProgress_shadowColor, Color.parseColor("#aaaaaa"));
        isShowShadowLayer = array.getBoolean(R.styleable.MagicProgress_shadowLayer, false);
        isTextLongShow = array.getBoolean(R.styleable.MagicProgress_textLongShow, true);
        isBubbleShow = isTextLongShow;
        textBgStyle = array.getString(R.styleable.MagicProgress_textBgStyle);
        if (progress < min) {
            progress = min;
        }
        if (progress > max) {
            progress = max;
        }
        if (pointProgress < min || pointProgress > max) {
            pointEnable = false;
        }
        array.recycle();
    }

    private void initData() {
        //初始化背景画图 Initialize the background drawing
        backGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backGroundPaint.setStyle(Paint.Style.STROKE);
        backGroundPaint.setAntiAlias(true);
        backGroundPaint.setColor(progressBackGround);
        backGroundPaint.setStrokeWidth(progressWidth);
        if (isShowShadowLayer) {
            backGroundPaint.setShadowLayer(20, 0, 0, shadowColor);
        }
        //初始化小圆点 Initialize the dot
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(pointColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setShadowLayer(2, 0, 0, shadowColor);
        //初始化progress Initialization progress
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressForwardGround);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(progressWidth);
        //初始化Thumb Initialize Thumb
        thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        thumbPaint.setShadowLayer(2, 0, 0, shadowColor);
        //数字显示 Digital display
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(2f);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setShadowLayer(2, 0, 0, shadowColor);

        if (TextUtils.equals(textBgStyle, TEXT_BG_STYLE_BUBBLE)) {
            mBubbleView = new BubbleViewHandler(getContext());
            windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            //mBubbleView = new BubbleView(getContext());
            mBubbleView.setSize("100", textSize);
            mBubbleView.setTextSize(textSize);
            mBubbleView.setTextColor(textColor);
        }
        if (mBubbleView != null) {
            paddingSpace = mBubbleView.getBubbleWidth() / 2F;
        }
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
            if (TextUtils.equals(textBgStyle, TEXT_BG_STYLE_BUBBLE)) {
                thumbY = viewHeight - thumbRadios * 2;
            } else {
                thumbY = viewHeight / 2F;
            }
            progressLength = viewWidth - paddingSpace * 2 - thumbRadios * 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        if (pointEnable) {
            drawPoint(canvas);
        }
        drawProgress(canvas);
        drawThumb(canvas);
        if (TextUtils.equals(textBgStyle, TEXT_BG_STYLE_BUBBLE)) {
            if (isBubbleShow) {
                String text = String.valueOf((int) Math.floor((progress - breakProgress) / baseProgress));
                float y = thumbY - mBubbleView.getBubbleHeight() - thumbRadios - mBubbleView.getTopBottomMargin();
                mBubbleView.setFloatOffset(text, thumbX, y, canvas);
            }
        } else {
            if (showTextEnable) {
                drawText(canvas);
            }
        }
    }

    private void drawText(Canvas canvas) {
        String text = String.valueOf((int) Math.floor((progress - breakProgress) / baseProgress));
        float textWidth = textPaint.measureText(text);
        Rect rect = new Rect();
        if (canMoveThumb) {
            animalAlpha = false;
            textAlpha = 255;
            shadowRadios = 2f;
        }

        if (animalAlpha) {
            textAlpha -= 5;
            shadowRadios = 0;
            if (textAlpha <= 0) {
                shadowRadios = 0;
                textAlpha = 0;
                animalAlpha = false;
            }
            postInvalidateDelayed(5);
        }
        textPaint.setShadowLayer(shadowRadios, 0, 0, shadowColor);
        //滑杆文字一直显示，取消滑动停止自动隐藏功能 Slider text is always displayed, cancel the sliding stop automatic hiding function
        textPaint.setAlpha(!isTextLongShow ? textAlpha : 255);
        textPaint.getTextBounds(text, 0, text.length(), rect);
        float textBoundsHeight = rect.bottom - rect.top;
        float x = (float) progress / (float) (max - min) * progressLength + paddingSpace + thumbRadios - textWidth / 2;
        float y = thumbY - textSpace - textBoundsHeight;
        canvas.drawText(text, x, y, textPaint);

    }

    /**
     * 画指示器
     * Draw indicator
     *
     * @param canvas
     */
    private void drawThumb(Canvas canvas) {
        thumbX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        canvas.drawCircle(thumbX, thumbY, thumbRadios, thumbPaint);
    }

    /**
     * 画进度条
     * Draw a progress bar
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        float stopX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        canvas.drawLine(thumbRadios + paddingSpace, thumbY, stopX, thumbY, progressPaint);
    }

    /**
     * 画小圆点
     * Draw little dots
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        if (isShowBreak) {
            if (pointProgress < (max - min)) {
                if (max == 100) {
                    if (pointProgress > 0) {
                        float x = pointProgress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
                        float y = thumbY;
                        canvas.drawCircle(x, y, pointRadios, pointPaint);
                    }
                } else {
                    float x = pointProgress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
                    float y = thumbY;
                    canvas.drawCircle(x, y, pointRadios, pointPaint);
                }

            }
        }
    }

    /**
     * 画背景
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        canvas.drawLine(thumbRadios + paddingSpace, thumbY, thumbRadios +
                paddingSpace + progressLength, thumbY, backGroundPaint);
    }


    /**
     * 设置断点圆点的位置点
     * Paint background
     *
     * @param pointProgress
     */
    public void setPointProgress(int pointProgress) {
        this.pointProgress = pointProgress;
    }

    private void showBubble() {
        if (!TextUtils.equals(textBgStyle, TEXT_BG_STYLE_BUBBLE)) {
            return;
        }
        if ((null == mBubbleView)) {
            return;
        }
        if (isBubbleShow) {
            return;
        }
        isBubbleShow = true;
        invalidate();
    }

    private void hideBubble() {
        if (!TextUtils.equals(textBgStyle, TEXT_BG_STYLE_BUBBLE)) {
            return;
        }
        if ((null == windowManager) || (null == mBubbleView)) {
            return;
        }
        isBubbleShow = false;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onTouchStateChangeListener != null) {
            onTouchStateChangeListener.onTouchUp(event.getAction() == MotionEvent.ACTION_UP);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchOnThumb(event.getX(), event.getY())) {
                    canMoveThumb = true;
                    showBubble();
                    invalidate();
                } else {
                    canMoveThumb = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canMoveThumb) {
                    //此处解决与recycleview或者其他的滑动冲突问题
                    //This is where you can resolve slip conflicts with recycleview or other issues
                    getParent().requestDisallowInterceptTouchEvent(true);
                    moveThumb(event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMoveThumb && (!isTextLongShow)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    startAnimal();
                    hideBubble();
                }
                canMoveThumb = false;
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 移动指示器
     * Moving indicator
     *
     * @param x
     */
    private void moveThumb(float x) {
        float nowX = x - thumbRadios;
        int nowProgress = (int) (((float) (max - min) * (nowX / (float) (viewWidth - 2 * thumbRadios))) + 0.5f);
        if (nowProgress < min) {
            nowProgress = min;
        }
        if (nowProgress > max) {
            nowProgress = max;
        }

        boolean adsorbEnable = false;
//        if (isAuto) {
//            adsorbEnable = Math.abs(nowProgress - pointProgress) > pointRange ? false : true;
//        }
        if (nowProgress != progress) {
            if (adsorbEnable) {
                if (progress == pointProgress) {
                    return;
                }
                progress = pointProgress;
                if (progress < 10 || progress > 90)
                    if (listener != null) {
                        listener.onProgressChange(progress, false);
                    }
            } else {
                progress = nowProgress;
                if (listener != null) {
                    listener.onProgressChange(progress, false);
                }
            }
            invalidate();
        }

    }

    private boolean touchOnThumb(float x, float y) {
        //判断如果在圆点之外return false  Determine if you return false outside the dot
        float spaceX = Math.abs(thumbX - x);
        float spaceY = Math.abs(thumbY - y);
        if (spaceX > thumbRadios * touchRatio) {
            return false;
        }
        if (spaceY > thumbRadios * touchRatio) {
            return false;
        }
        return true;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (listener != null) {
            listener.onProgressChange(progress, true);
        }
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setPointProgressByNewMax(int maxProgress) {
        this.pointProgress = (int) ((float) this.pointProgress / (float) this.max * (float) maxProgress);
    }


    /**
     * 当状态改变时回调
     * Call back when the state changes
     */
    public interface OnProgressChangeListener {
        void onProgressChange(int progress, boolean fromUser);
    }

    public void startAnimal() {
        if (isTextLongShow || animalAlpha) {
            return;
        }
        textAlpha = 255;
        animalAlpha = true;
        postInvalidateDelayed(10);
    }

    private OnTouchStateChangeListener onTouchStateChangeListener;

    public void setOnTouchStateChangeListener(OnTouchStateChangeListener onTouchStateChangeListener) {
        this.onTouchStateChangeListener = onTouchStateChangeListener;
    }

    public interface OnTouchStateChangeListener {
        void onTouchUp(boolean touchUpFlag);
    }

    public void setBaseProgress(float baseProgress) {
        this.baseProgress = baseProgress;
    }

    public void setShowBreak(boolean showBreak) {
        isShowBreak = showBreak;
    }
}
