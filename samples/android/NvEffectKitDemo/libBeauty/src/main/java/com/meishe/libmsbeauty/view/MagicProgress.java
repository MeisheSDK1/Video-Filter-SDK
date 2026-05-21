package com.meishe.libmsbeauty.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.meishe.libbase.util.ScreenUtils;
import com.meishe.libmsbeauty.R;


/**
 * Created by MZ008064 on 2020/6/4.
 *
 * @author zcy
 * @Date 2020/6/4.
 */
public class MagicProgress extends View {
    // Scroll bar background color
    //滚动条背景色
    private int progressBackGround;
    //Scroll bar width
    //滚动条宽度
    private int progressWidth;
    //Scroll bar color
    //滚动条颜色
    private int progressForwardGround;
    //indicator background color
    //指示器背景色
    private int thumbColor;
    //Adsorption point color
    //吸附点颜色
    private int pointColor;
    ///indicator radios
    //指示器半径
    private int thumbRadios;
    //point radius
    //圆点半径
    private int pointRadios;
    //max value
    //最大值
    private int max;
    //min value
    //最小值
    private int min;
    //current value
    //当前值
    private int progress;
    //Adsorption point position
    //吸附点位置
    private int pointProgress;
    //Whether the breakpoint function is displayed
    //是否有显示断点功能
    private boolean pointEnable = true;
    //Whether it has adsorption function
    //是否有吸附功能
    private boolean isAuto = true;
    //view width
    //布局宽
    private int viewWidth;
    //view height
    //布局高
    private int viewHeight;
    private Paint backGroundPaint;
    //Breakpoint paint
    //断点画笔
    private Paint pointPaint;
    // scrollBar point
    //滚动条画笔
    private Paint progressPaint;
    //indicator paint
    //指示器画笔
    private Paint thumbPaint;
    //The coordinate x of the current indicator
    //当前指示器的坐标x
    private float thumbX;
    //The coordinate Y of the current indicator
    //当前指示器的坐标y
    private float thumbY;
    //Whether the indicator can be moved
    //是否可以移动指示器
    private boolean canMoveThumb = false;
    //Whether text can be displayed
    //是否可以显示文字
    private boolean showTextEnable = true;
    //Is there a middle ground
    //是否有折中值
    private boolean isShowBreak = true;
    private int breakProgress = 100;
    private Paint textPaint;
    private float textSize;
    private int textColor;
    private float textSpace;
    private float paddingSpace;
    private float progressLength;
    //Display font transparency
    //显示字体透明度
    private int textAlpha = 0;
    private int shadowColor;
    private float shadowRadios;
    //Adjust offset touch offset (press sensitivity)
    //调节偏移的触摸偏移量（按压灵敏度）
    private float touchRatio = 2;

    public void setPointEnable(boolean pointEnable) {
        this.pointEnable = pointEnable;
        setAuto(pointEnable);
    }

    public void setBreakProgress(int breakProgress) {
        this.breakProgress = breakProgress;
    }

    /**
     * 调节按压灵敏度
     * adjust press sensitivity
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

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public MagicProgress(Context context) {
        this(context, null);
    }

    public MagicProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        progress = array.getInteger(R.styleable.MagicProgress_progress, 50);
        pointProgress = array.getInteger(R.styleable.MagicProgress_pointProgress, 80);
        textSize = array.getDimension(R.styleable.MagicProgress_textSizeMagic, 22);
        textColor = array.getColor(R.styleable.MagicProgress_textColorMagic, Color.parseColor("#ffffff"));
        textSpace = ScreenUtils.dip2px(context, 5);
        paddingSpace = ScreenUtils.dip2px(context, 5);
        shadowColor = array.getColor(R.styleable.MagicProgress_shadowColor, Color.parseColor("#aaaaaa"));
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

    boolean animalAlpha = false;

    private void initData() {
        //init background image
        //初始化背景画图
        backGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backGroundPaint.setStyle(Paint.Style.STROKE);
        backGroundPaint.setAntiAlias(true);
        backGroundPaint.setColor(progressBackGround);
        backGroundPaint.setStrokeWidth(progressWidth);
        backGroundPaint.setShadowLayer(20, 0, 0, shadowColor);
        //init point
        //初始化小圆点
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(pointColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setShadowLayer(2, 0, 0, shadowColor);
        // init progress
        //初始化progress
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressForwardGround);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(progressWidth);
        //init thumb
        //初始化Thumb
        thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        thumbPaint.setShadowLayer(2, 0, 0, shadowColor);
        //Digital display
        //数字显示
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(2f);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setShadowLayer(2, 0, 0, shadowColor);
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
            thumbY = viewHeight - thumbRadios * 2;
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
        if (showTextEnable) {
            drawText(canvas);
        }
    }

    float baseProgress = 1f;

    private void drawText(Canvas canvas) {
        String text = String.valueOf((int)Math.floor((progress - breakProgress) / baseProgress));
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
//        textPaint.setAlpha(animalEnable ? textAlpha : 255);
        //Slider text is always displayed, cancel the sliding stop automatic hiding function
        //滑杆文字一直显示，取消滑动停止自动隐藏功能
        textPaint.setAlpha(255);
        textPaint.getTextBounds(text, 0, text.length(), rect);
        float textBoundsHeight = rect.bottom - rect.top;
        float x = (float) progress / (float) (max - min) * progressLength + paddingSpace + thumbRadios - textWidth / 2;
        float y = thumbY - textSpace - textBoundsHeight;
        canvas.drawText(text, x, y, textPaint);

    }

    /**
     * Draw indicator
     * 画指示器
     *
     * @param canvas
     */
    private void drawThumb(Canvas canvas) {
        thumbX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        canvas.drawCircle(thumbX, thumbY, thumbRadios, thumbPaint);
    }

    /**
     * draw progress
     * 画进度条
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        float stopX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        canvas.drawLine(thumbRadios + paddingSpace, thumbY, stopX, thumbY, progressPaint);
    }

    /**
     * draw point
     * 画小圆点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
//        Log.e("lpf", "pointProgress=" + pointProgress + " max=" + max + " min=" + min +
//                " progressLength=" + progressLength + " thumbRadios=" + thumbRadios + " paddingSpace=" + paddingSpace);
        if (isShowBreak){
            if (pointProgress < (max - min)) {
                if (max==100){
                    if (pointProgress>0){
                        float x = pointProgress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
                        float y = thumbY;
                        canvas.drawCircle(x, y, pointRadios, pointPaint);
                    }
                }else{
                    float x = pointProgress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
                    float y = thumbY;
                    canvas.drawCircle(x, y, pointRadios, pointPaint);
                }

            }
        }
    }

    /**
     * draw background
     * 画背景
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        canvas.drawLine(thumbRadios + paddingSpace, thumbY, thumbRadios +
                paddingSpace + progressLength, thumbY, backGroundPaint);
    }


    /**
     * Set the location point of the breakpoint circle
     * 设置断点圆点的位置点
     *
     * @param pointProgress
     */
    public void setPointProgress(int pointProgress) {
        this.pointProgress = pointProgress;
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
                    invalidate();
                } else {
                    canMoveThumb = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canMoveThumb) {
                    moveThumb(event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMoveThumb) {
                    startAnimal();
                }
                canMoveThumb = false;
                break;
        }
        return true;
    }

    /**
     * Moving indicator
     * 移动指示器
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
//        Log.d("lpf","nowX=="+nowX+" x="+x+" " +
//                "thumbRadios="+thumbRadios+" viewWidth="+viewWidth+" thumbRadios="+thumbRadios);
//        Log.d("lpf","----------------------nowProgress=="+nowProgress+" progress="+progress);

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
        //Determine if you return false outside the dot
        //判断如果在圆点之外return false
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

    public int getProgress(){
        return progress;
    }

    public void setPointProgressByNewMax(int maxProgress) {
        this.pointProgress = (int) ((float) this.pointProgress / (float) this.max * (float) maxProgress);
    }


    /**
     * Call back when the state changes
     * 当状态改变时回调
     */
    public interface OnProgressChangeListener {
        void onProgressChange(int progress, boolean fromUser);
    }

    public void startAnimal() {
        if (!animalEnable || animalAlpha) {
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

    private boolean animalEnable = true;

    public void setAnimalEnable(boolean animalEnable) {
        this.animalEnable = animalEnable;
    }

    public void setBaseProgress(float baseProgress) {
        this.baseProgress = baseProgress;
    }

    public void setShowBreak(boolean showBreak) {
        isShowBreak = showBreak;
    }
}
