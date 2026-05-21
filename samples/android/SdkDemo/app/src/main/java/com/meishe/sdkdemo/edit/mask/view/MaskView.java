package com.meishe.sdkdemo.edit.mask.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.utils.SizeUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.data.mask.MaskInfoData;
import com.meishe.sdkdemo.edit.data.mask.MaskRegionInfoData;
import com.meishe.sdkdemo.edit.mask.NvMaskHelper;

import androidx.annotation.Nullable;

/**
 * The type Mask view.
 * 蒙版拖拽显示view
 *
 * @Author :Jml
 * @CreateDate :2020/9/21 16:11
 * @Description : 蒙版拖拽显示view
 */
public class MaskView extends View {
    private static final String TAG = "MaskView";
    /**
     * view的宽高，为了最大值超出fragment 范围，重写了onMeasure
     * The width and height of the view are overridden so that the maximum value of the view is outside the fragment range
     */
    private int mWidth = ScreenUtils.getScreenWidth() * 4;

    /**
     * 锚点所对应的圆的半径
     * The radius of the circle corresponding to the anchor point
     */
    private int mCenterCircleRadius;

    /**
     * 背景属性特技的偏移
     */
    private float bgTransX = 0;
    private float bgTransY = 0;
    private float bgScale = 1;
    private float bgRotation = 0;

    private Paint mLinePaint;
    private PointF mCenterForMaskView;

    /**
     * 圆角的半径比例
     * Ratio of radius of rounded corners
     */
    private float roundCornerWidthRate;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 5.0f;

    /**
     * 羽化值调节的按钮位置
     * Button position for feather value adjustment
     */
    private int mFeatherIconDis = 20;
    private int mMaskWidthIconDis = 20;
    private int mMaskRoundCornerIconDis = 20;

    /**
     * 顶部的预览视图总高度
     * Top preview view of total height
     */
    private int videoFragmentHeight;

    /**
     * 绘制的蒙版框效果路径（黄色的框）
     * Draw the mask frame effect path (yellow box)
     */
    private Path mMaskPath;
    /**
     * 保存每个蒙版的信息
     * Save the information for each mask
     */
    private MaskInfoData mCurMaskInfoData;
    /**
     * 保存的是当前选择的蒙版对应的宽高
     * Saves the width and height of the currently selected mask
     */
    private int currentMaskWidth;
    private int currentMaskHeight;

    private PointF centerInLiveWindow = new PointF();
    private Rect featherRect;
    /**
     * 用于临时保存变动的scale
     * scale used to temporarily save changes
     */
    private float tempScale = 1;
    private float tempTextSize;

    public MaskView(Context context) {
        this(context, null);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mCenterCircleRadius = SizeUtils.dp2px(5.5f);
        mLinePaint = new Paint();
        mLinePaint.setColor(getResources().getColor(R.color.mask_line));
        mLinePaint.setStrokeWidth(SizeUtils.dp2px(2f));
        // 设置抗锯齿 Set anti-aliasing
        mLinePaint.setAntiAlias(true);
        // 设置非填充 Set Non-padding
        mLinePaint.setStyle(Paint.Style.STROKE);
    }


    /**
     * Set live windowSize.
     * live window的尺寸
     *
     * @param fragmentHeight   the fragment height 片段的高度
     * @param liveWindowWidth  width
     * @param liveWindowHeight height
     */

    public void setLiveWindowCenter(int fragmentHeight, int liveWindowWidth, int liveWindowHeight) {
        videoFragmentHeight = fragmentHeight;
        //顶部预览的liveWindow 的宽高 The width and height of the LiveWindow previewed at the top
        //蒙版view的中心 The center of the mask view
        mCenterForMaskView = new PointF(ScreenUtils.getScreenWidth() * 2, videoFragmentHeight / 2);
        //liveWindow 上蒙版效果中心 Mask effect center on LiveWindow
        centerInLiveWindow = new PointF(liveWindowWidth / 2, liveWindowHeight / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = ScreenUtils.getScreenWidth() * 4;
        int height = ScreenUtils.getScreenHeight();
        if (0 != videoFragmentHeight) {
            height = videoFragmentHeight;
        }
        setMeasuredDimension(width, height);

    }

    private void initData() {
        mFeatherIconDis = SizeUtils.dp2px(10);
        mMaskWidthIconDis = SizeUtils.dp2px(10);
    }


    /**
     * On scale begin.
     * 开始缩放
     */
    public void onScaleBegin() {
        tempScale = mCurMaskInfoData.getScale();
        tempTextSize = mCurMaskInfoData.getTextSize();
        currentMaskWidth = mCurMaskInfoData.getMaskWidth();
        currentMaskHeight = mCurMaskInfoData.getMaskHeight();
    }

    /**
     * On scale end.
     * 结束缩放
     */
    public void onScaleEnd() {
        tempScale = mCurMaskInfoData.getScale();
        tempTextSize = mCurMaskInfoData.getTextSize();
        currentMaskWidth = mCurMaskInfoData.getMaskWidth();
        currentMaskHeight = mCurMaskInfoData.getMaskHeight();
        //currentRotation = mCurMaskInfoData.getmRotation();
    }

    /**
     * On scale.
     * 缩放
     *
     * @param scale the scale 缩放
     */
    public void onScale(float scale) {
        if (mCurMaskInfoData == null) {
            return;
        }
        if (scale <= MIN_SCALE) {
            scale = MIN_SCALE;
        } else if (scale >= MAX_SCALE) {
            scale = MAX_SCALE;
        }
        mCurMaskInfoData.setScale(scale * tempScale);
        //设置变化后的宽高 Sets the width and height of the change
        if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.RECT ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.CIRCLE ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.HEART ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.STAR) {
            mCurMaskInfoData.setMaskWidth((int) (currentMaskWidth * scale));
            mCurMaskInfoData.setMaskHeight((int) (currentMaskHeight * scale));
        } else if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.MIRROR) {
            mCurMaskInfoData.setMaskHeight((int) (currentMaskHeight * scale));
            mCurMaskInfoData.setMaskWidth((int) (currentMaskWidth));
        } else if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.TEXT) {
            mCurMaskInfoData.setTextSize(scale * tempTextSize);
            NvMaskHelper.buildMaskText(mCurMaskInfoData);
        }
    }

    /**
     * Sets translation.
     * 设置平移
     *
     * @param translationX the translation x 平移x
     * @param translationY the translation y 平移y
     */
    public void setTranslation(float translationX, float translationY) {
        if (mCurMaskInfoData != null) {
            mCurMaskInfoData.setTranslationX((int) translationX);
            mCurMaskInfoData.setTranslationY((int) translationY);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (0 == videoFragmentHeight) {
            return;
        }
        if (null == mCurMaskInfoData) {
            return;
        }
        PointF center = new PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY);
//        PointF center = new PointF(mCenterForMaskView.x, mCenterForMaskView.y);
        if (mCurMaskInfoData.getMaskType() == MaskRegionInfoData.MaskType.LINE) {
            mMaskPath = NvMaskHelper.lineRegionInfoPath(mCurMaskInfoData.getMaskWidth(), center, mCenterCircleRadius, 0);
        } else if (mCurMaskInfoData.getMaskType() == MaskRegionInfoData.MaskType.MIRROR) {
            mMaskPath = NvMaskHelper.mirrorRegionInfoPath(mCurMaskInfoData.getMaskWidth(), (int) (mCurMaskInfoData.getMaskHeight() * bgScale), center, mCenterCircleRadius, 0);
        } else if (mCurMaskInfoData.getMaskType() == MaskRegionInfoData.MaskType.RECT) {
            mMaskPath = NvMaskHelper.rectRegionInfoPath((int) (mCurMaskInfoData.getMaskWidth() * bgScale),
                    (int) (mCurMaskInfoData.getMaskHeight() * bgScale), center, mCenterCircleRadius, roundCornerWidthRate);
//            List<PointF> pointFS = NvMaskHelper.buildRectMaskRegionInfo(center, mCurMaskInfoData.getMaskWidth()
//                    , mCurMaskInfoData.getMaskHeight(), mCurMaskInfoData.getRotation(), mCurMaskInfoData.getRoundCornerWidthRate());
//            drawRectPoints(canvas, pointFS);
        } else if (mCurMaskInfoData.getMaskType() == MaskRegionInfoData.MaskType.CIRCLE) {
            mMaskPath = NvMaskHelper.circleRegionInfoPath((int) (mCurMaskInfoData.getMaskWidth() * bgScale)
                    , (int) (mCurMaskInfoData.getMaskHeight() * bgScale), center, mCenterCircleRadius, 0);
        } else if (mCurMaskInfoData.getMaskType() == MaskRegionInfoData.MaskType.HEART) {
            mMaskPath = NvMaskHelper.heartRegionInfoPath((int) (mCurMaskInfoData.getMaskWidth() * bgScale),
                    center, mCenterCircleRadius, 0);
        } else if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.STAR) {
            mMaskPath = NvMaskHelper.starRegionInfoPath((int) (mCurMaskInfoData.getMaskWidth() * bgScale), center, mCenterCircleRadius, 0);
        } else if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.TEXT) {
            mMaskPath = NvMaskHelper.textRegionInfoPath((int) (mCurMaskInfoData.getMaskWidth() * bgScale),
                    mCurMaskInfoData.getMaskHeight(), center);
        }
        Log.d(TAG, " maskView =-= onDraw w:" + mCurMaskInfoData.getMaskWidth() + " h:"
                + mCurMaskInfoData.getMaskHeight() + " center.X:" + center.x + " y:" + center.y
                + " bgTrans x" + bgTransX + " bgTrans y" + bgTransY);
        canvas.drawPath(mMaskPath, mLinePaint);
        //绘制羽化值对应的图标 Draws the icon corresponding to the feather value
        drawFeatherIcon(canvas);
        //绘制控制蒙版宽度的图标 Draw an icon that controls the width of the mask
        drawMaskWidthIcon(canvas);
        //绘制控制蒙版高度的图标 Draws an icon that controls the mask height
        drawMaskHeightIcon(canvas);
        //绘制矩形蒙版圆角图标 Draws a rectangular mask icon with rounded corners
        drawMaskRoundCornerIcon(canvas);
//        PointF maskCenter = new PointF();
//        if (null != centerInLiveWindow) {
//            maskCenter.x = mCenterForMaskView.x + mCurMaskInfoData.getTranslationX();
//            maskCenter.y = mCenterForMaskView.y + mCurMaskInfoData.getTranslationY();
//        }
//        mCurMaskInfoData.setCenter(maskCenter);
        PointF liveCenter = new PointF();
        if (centerInLiveWindow != null) {
            liveCenter.x = centerInLiveWindow.x;
            liveCenter.y = centerInLiveWindow.y;
        }
        mCurMaskInfoData.setLiveWindowCenter(liveCenter);
        mCurMaskInfoData.setTranslationX((int) mCurMaskInfoData.getTranslationX());
        mCurMaskInfoData.setTranslationY((int) mCurMaskInfoData.getTranslationY());
        setTranslationX(mCurMaskInfoData.getTranslationX());
        setTranslationY(mCurMaskInfoData.getTranslationY());
        setPivotX(center.x);
        setPivotY(center.y);
        setRotation(mCurMaskInfoData.getRotation() - bgRotation);
//        onRotation(rotation);
//        Log.d("MaskView onDraw", " transX:" + translationX + " transY:" + translationY + " center x:" + center.x + " y:" + center.y);
    }

   /* private void drawRectPoints(Canvas canvas, List<PointF> pointFS) {
        Path path = new Path();
        for (int i = 0; i < pointFS.size(); i++) {
            PointF pointF = pointFS.get(i);
            if (i == 0) {
                path.moveTo(pointF.x, pointF.y);
            }
            path.lineTo(pointF.x, pointF.y);
            canvas.drawCircle(pointF.x, pointF.y, 10, mPointPain);
        }
        path.lineTo(pointFS.get(0).x,pointFS.get(0).y);
        canvas.drawPath(path, mPointPain);
    }*/

    /**
     * 绘制蒙版的圆角控制图标
     * Draws the rounded corner control icon for the mask
     *
     * @param canvas
     */
    private void drawMaskRoundCornerIcon(Canvas canvas) {
        if (null == mCenterForMaskView || mCurMaskInfoData == null ||
                mCurMaskInfoData.getMaskType() != MaskInfoData.MaskType.RECT) {
            return;
        }
        PointF center = new PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY);
        int maskWidth = mCurMaskInfoData.getMaskWidth();
        int maskHeight = mCurMaskInfoData.getMaskHeight();
        // 获取图形资源文件 Gets the graphics resource file
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mask_round_corner);
        //计算图标的位置 Calculates the position of the icon
        int targetX = (int) (center.x - maskWidth / 2 - mMaskRoundCornerIconDis - bmp.getWidth());
        int targetY = (int) (center.y - maskHeight / 2 - mMaskRoundCornerIconDis - bmp.getHeight());
        canvas.drawBitmap(bmp, targetX, targetY, null);
    }

    /**
     * 绘制控制蒙版宽度的图标
     * Draw an icon that controls the width of the mask
     *
     * @param canvas
     */
    private void drawMaskWidthIcon(Canvas canvas) {
        if (null == mCenterForMaskView || mCurMaskInfoData == null ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.LINE ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.MIRROR ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.HEART ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.TEXT ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.STAR) {
            return;
        }
        PointF center = new PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY);
        int maskWidth = mCurMaskInfoData.getMaskWidth();
        // 获取图形资源文件 Gets the graphics resource file
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mask_width);
        //计算图标的位置 Calculates the position of the icon
        int targetX = (int) (center.x + mMaskWidthIconDis + maskWidth / 2);
        canvas.drawBitmap(bmp, targetX, center.y - bmp.getHeight() / 2, null);

    }

    /**
     * 绘制控制蒙版宽度的图标
     * Draw an icon that controls the width of the mask
     *
     * @param canvas
     */
    private void drawMaskHeightIcon(Canvas canvas) {
        if (null == mCenterForMaskView || mCurMaskInfoData == null ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.LINE ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.MIRROR ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.HEART ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.TEXT ||
                mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.STAR) {
            return;
        }
        PointF center = new PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY);
        int maskHeight = mCurMaskInfoData.getMaskHeight();
        // 获取图形资源文件 Gets the graphics resource file
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_mask_height);
        //计算图标的位置 Calculates the position of the icon
        canvas.drawBitmap(bmp, center.x - bmp.getWidth() / 2, center.y - mMaskWidthIconDis - maskHeight / 2 - bmp.getHeight(), null);

    }

    /**
     * 绘制控制羽化值的图标
     * Draws an icon that controls the feathering value
     *
     * @param canvas
     */
    private void drawFeatherIcon(Canvas canvas) {
        if (null == mCenterForMaskView) {
            return;
        }
        if (mCurMaskInfoData == null || mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.TEXT) {
            return;
        }
        PointF center = new PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY);

        // 获取图形资源文件 Gets the graphics resource file
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_mask_feather);
        int targetY = mFeatherIconDis;
        int maskHeight = mCurMaskInfoData.getMaskHeight();
        if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.LINE) {
            targetY = mFeatherIconDis;
        } else if (mCurMaskInfoData.getMaskType() == MaskInfoData.MaskType.HEART) {
            targetY = maskHeight + mFeatherIconDis;
        } else {
            targetY = maskHeight / 2 + mFeatherIconDis;
        }
        featherRect = new Rect();
        featherRect.left = (int) (center.x - bmp.getWidth() / 2);
        featherRect.right = featherRect.left + bmp.getWidth();
        featherRect.top = (int) (center.y + targetY);
        featherRect.bottom = featherRect.top + bmp.getHeight();
        canvas.drawBitmap(bmp, center.x - bmp.getWidth() / 2, center.y + targetY, null);

    }

    /**
     * 获取羽化坐标区域
     * Get the feather coordinate region
     *
     * @return
     */
    public Rect getFeatherRect() {
        return featherRect;
    }

    /**
     * Sets mask type and reverse.
     * 设置蒙版类型和反转
     *
     * @param type    the type 类型
     * @param reverse the reverse 相反的
     */
    public void setMaskTypeAndReverse(int type, boolean reverse) {
        //定义蒙版的快高 Define the fast height of the mask
        setMaskWidthHeightByType(type);
        invalidate();
    }

    /**
     * 设置maskView的数据
     *
     * @param type     要设置的
     * @param infoData
     */
    public void setMaskTypeAndInfo(int type, MaskInfoData infoData) {
        if (infoData != null) {
            setMaskInfoData(infoData);
        } else {
            setMaskWidthHeightByType(type);
        }
        invalidate();
    }

    private void setMaskWidthHeightByType(int mType) {
        if (mType == MaskInfoData.MaskType.NONE) {
            clearData();
            return;
        }
        mCurMaskInfoData = buildNewMaskData(true, mType);
    }

    /**
     * 设置构建regionInfo的信息
     * Sets information for building RegionInfo
     *
     * @param createNew the create new 创建新的
     * @param mType     the m type
     * @return mask info data  蒙版信息数据
     */
    public MaskInfoData buildNewMaskData(boolean createNew, int mType) {
        MaskInfoData maskInfoData = null;
        if (createNew) {
            maskInfoData = new MaskInfoData();
            //不同类型的蒙版，基础宽高不同 Different types of masks, different base width and height
            if (mType == MaskInfoData.MaskType.LINE) {
                currentMaskWidth = mWidth;
                currentMaskHeight = mWidth;
            } else if (mType == MaskInfoData.MaskType.MIRROR) {
                currentMaskWidth = mWidth;
                currentMaskHeight = ScreenUtils.getScreenWidth() / 3;
            } else if (mType == MaskInfoData.MaskType.RECT) {
                currentMaskWidth = ScreenUtils.getScreenWidth() / 2;
                currentMaskHeight = currentMaskWidth;
            } else if (mType == MaskInfoData.MaskType.CIRCLE) {
                currentMaskWidth = ScreenUtils.getScreenWidth() / 2;
                currentMaskHeight = currentMaskWidth;
            } else if (mType == MaskInfoData.MaskType.HEART) {
                currentMaskWidth = ScreenUtils.getScreenWidth() / 4;
                currentMaskHeight = currentMaskWidth;
            } else if (mType == MaskInfoData.MaskType.STAR) {
                currentMaskWidth = ScreenUtils.getScreenWidth() / 2;
                currentMaskHeight = currentMaskWidth;
            } else if (mType == MaskInfoData.MaskType.TEXT) {
                currentMaskWidth = ScreenUtils.getScreenWidth() / 2;
                currentMaskHeight = currentMaskWidth / 2;
                maskInfoData.setText("");
            }
            tempScale = 1;
            tempTextSize = 100;
            maskInfoData.setMaskWidth(currentMaskWidth);
            maskInfoData.setMaskHeight(currentMaskHeight);
            maskInfoData.setTextSize(100);
            if (mCenterForMaskView == null) {
                mCenterForMaskView = new PointF(ScreenUtils.getScreenWidth() * 2, videoFragmentHeight / 2);
            }
        }
        maskInfoData.setLiveWindowCenter(centerInLiveWindow);
        maskInfoData.setMaskType(mType);
        return maskInfoData;
    }

    /**
     * Gets center point.
     * 获得中心点
     *
     * @return the center point 中心点
     */
    public PointF getCenterPoint() {

        PointF center = new PointF(mCenterForMaskView.x, mCenterForMaskView.y);
        return center;
    }


    /**
     * Get center for live window point f.
     * 获得livewindow的中心点
     *
     * @return the point f
     */
    public PointF getCenterForLiveWindow() {
        PointF center = new PointF(centerInLiveWindow.x, centerInLiveWindow.y);
        return center;
    }

    /**
     * Gets mask data info.
     * 获取蒙版数据信息
     *
     * @return the mask data info  蒙版数据信息
     */
    public MaskInfoData getMaskDataInfo() {
        if (mCurMaskInfoData != null) {
            mCurMaskInfoData.setRoundCornerWidthRate(roundCornerWidthRate);
        }
        return mCurMaskInfoData;
    }

    /**
     * Sets feather width.
     * 设置羽化宽度
     *
     * @param featherWidth    the feather width 羽化宽度
     * @param mFeatherIconDis the m feather icon dis 羽化图标
     */
    public void setFeatherWidth(float featherWidth, int mFeatherIconDis) {
        mCurMaskInfoData.setFeatherWidth(featherWidth);
        this.mFeatherIconDis = mFeatherIconDis;
        postInvalidate();
    }

    /**
     * 设置圆角半径及距离
     * Set radius and distance of rounded corners
     *
     * @param roundCornerWidthRate    the round corner width rate 圆角宽度率
     * @param mMaskRoundCornerIconDis the m mask round corner icon dis 蒙版圆角图标
     */
    public void setRoundCornerWidth(float roundCornerWidthRate, int mMaskRoundCornerIconDis) {
        this.roundCornerWidthRate = roundCornerWidthRate;
        mCurMaskInfoData.setRoundCornerWidthRate(roundCornerWidthRate);
        this.mMaskRoundCornerIconDis = mMaskRoundCornerIconDis;
        invalidate();
    }

    /**
     * Sets mask width.
     * 设置蒙版宽度
     *
     * @param currentWidth the current width 当前的宽度
     */
    public void setMaskWidth(int currentWidth) {
        mCurMaskInfoData.setMaskWidth(currentWidth);
        invalidate();
    }

    /**
     * Sets mask height.
     * 设置蒙版高度
     *
     * @param curHeight the cur height 当前高度
     */
    public void setMaskHeight(int curHeight) {
        mCurMaskInfoData.setMaskHeight(curHeight);
        invalidate();
    }

    /**
     * On rotation.
     * 旋转
     *
     * @param degree the degree  度
     */
    public void onRotation(float degree) {
        if (null != mCurMaskInfoData) {

            //设置旋转角度 Set the rotation Angle
            mCurMaskInfoData.setRotation(degree);
            postInvalidate();
        }
    }


    /**
     * Sets mask info data.
     * 设置蒙版信息数据
     *
     * @param infoData the mask info data from video fx 蒙版信息数据从视频fx
     */
    public void setMaskInfoData(MaskInfoData infoData) {
        if (null != infoData) {
            this.mCurMaskInfoData = infoData;
            this.currentMaskWidth = infoData.getMaskWidth();
            this.currentMaskHeight = infoData.getMaskHeight();
            this.tempScale = infoData.getScale();
            this.roundCornerWidthRate = infoData.getRoundCornerWidthRate();
            NvMaskHelper.buildMaskText(mCurMaskInfoData);
        }
    }

    /**
     * Setm feather icon dis.
     * Setm羽化图标
     *
     * @param mFeatherIconDis the m feather icon dis 羽化图标
     */
    public void setFeatherIconDis(int mFeatherIconDis) {
        this.mFeatherIconDis = mFeatherIconDis;
    }


    /**
     * Clear data.
     * 清除数据
     */
    public void clearData() {
        this.mCurMaskInfoData = null;
    }

    /**
     * Sets mask round corner dis.
     * 设置蒙版圆角
     *
     * @param mMaskRoundCornerDis the m mask round corner dis 蒙版圆角
     */
    public void setMaskRoundCornerDis(int mMaskRoundCornerDis) {
        this.mMaskRoundCornerIconDis = mMaskRoundCornerDis;
    }

    public void setBackgroundCenter(float transX, float transY, float rotation, float scaleX) {
        this.bgTransX = transX;
        this.bgTransY = transY;
        this.bgScale = scaleX;
        this.bgRotation = rotation;
    }

    public float getBgTransX() {
        return bgTransX;
    }

    public float getBgTransY() {
        return bgTransY;
    }

    public float getBgScale() {
        return bgScale;
    }

    public float getBgRotation() {
        return bgRotation;
    }
}
