package com.meishe.sdkdemo.edit.mask.view;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.utils.SizeUtils;
import com.meishe.sdkdemo.edit.data.mask.MaskInfoData;
import com.meishe.sdkdemo.edit.data.mask.MaskRegionInfoData;
import com.meishe.sdkdemo.edit.mask.NvMaskHelper;


/**
 * The type Zoom view.
 * 变焦视图类
 *
 * @author :Jml
 * @date :2020/9/22 11:28
 * @des :
 */
public class ZoomView extends RelativeLayout {
    private static final String TAG = "ZoomView";
    /**
     * 移动X
     * translation X
     */
    private float translationX;
    /**
     * 移动Y
     * translation Y
     */
    private float translationY;
    /**
     * 伸缩比例
     * Scaling factors
     */
    private float scale = 1;
    /**
     * 旋转角度
     * rotation angle
     */
    private float rotation;
    private float currentRotation;
    /**
     * 移动 X 最大值
     * translation X maximum
     */
    private float maxTranslationX;
    /**
     * 移动 Y 最大值
     * translation Y maximum
     */
    private float maxTranslationY;

    public void setMaxTranslationX(float maxTranslationX) {
        this.maxTranslationX = maxTranslationX;
    }

    public void setMaxTranslationY(float maxTranslationY) {
        this.maxTranslationY = maxTranslationY;
    }

    /**
     * 移动过程中临时变量 Temporary variables while moving
     */
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;
    /**
     * 0=未选择，1=拖动，2=缩放 0= Unselected, 1= Drag, 2= Scale
     */
    private int moveType;
    private MaskView maskView;

    private int videoFragmentHeight;

    /**
     * 绘制的蒙版框效果路径（黄色的框）
     * Draw the mask frame effect path (yellow box)
     */
    private Path mMaskPath;
    /**
     * 调节羽化值对应的路径区域
     * Adjust the path area corresponding to the feather value
     */
    private Path mFeatherPath;
    private Path mWidthPath;
    private Path mHeightPath;
    private Path mCornerPath;
    /**
     * 顶部预览绘制的线条对应的中心点坐标
     * Preview the center point coordinates of the drawn line at the top
     */
    private PointF mCenterForMaskView;
    private int mType;
    private float featherWidth;
    /**
     * 圆角的比例，对最短边的一半的比例(0 - 1)
     * Ratio of rounded corners to half of the shortest side (0-1)
     */
    private float roundCornerWidthRate;
    /**
     * 羽化值调节的按钮位置
     * Button position for feather value adjustment
     */
    private int mFeatherIconDis = 20;
    private int mHeightIconDis = 20;
    private int mMaskWidthIconDis = 20;
    private int mMaskRoundCornerDis = 20;
    private Region re = new Region();
    /**
     * 操作羽化值
     * Manipulate the feather value
     */
    private boolean doFeather;
    /**
     * 拖拽
     * drag
     */
    private boolean doScroll;
    /**
     * 宽度调整
     * width control
     */
    private boolean doMaskWidth;
    /**
     * 高度调整
     * height adjustment
     */
    private boolean doMaskHeight;
    /**
     * 调整圆角
     * Adjust the rounded corners
     */
    private boolean doMaskRoundCorner;

    /**
     * 保存的是当前选择的蒙版对应的宽高
     * Saves the width and height of the currently selected mask
     */
    private int currentMaskWidth;
    private int currentMaskHeight;

    private int mCenterCircleRadius = 10;
    private boolean touchClick;
    private long touchClickDownTime;

    public ZoomView(Context context) {
        this(context, null);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    /**
     * Sets mask view.
     * 设置蒙版视图
     *
     * @param maskView the mask view 蒙版视图
     */
    public void setMaskView(MaskView maskView) {
        this.maskView = maskView;

        mFeatherIconDis = SizeUtils.dp2px(10);
        mHeightIconDis = SizeUtils.dp2px(10);
        mMaskWidthIconDis = SizeUtils.dp2px(10);
        mCenterCircleRadius = SizeUtils.dp2px(5.5f);

    }

    /**
     * Sets video fragment height.
     * 设置视频片段高度
     *
     * @param videoFragmentHeight the video fragment height 视频片段高度
     */
    public void setVideoFragmentHeight(int videoFragmentHeight, int livewidowWidth, int livewindowHeight) {
        this.videoFragmentHeight = videoFragmentHeight;
        mCenterForMaskView = new PointF(ScreenUtils.getScreenWidth() / 2, videoFragmentHeight / 2);
        if (maskView != null) {
            maskView.setLiveWindowCenter(videoFragmentHeight, livewidowWidth, livewindowHeight);
            maxTranslationX = livewidowWidth / 2f;
            maxTranslationY = livewindowHeight / 2f;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);

        if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
            LogUtils.e("onInterceptTouchEvent moveType=" + moveType);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && moveType == 2 || moveType == 1) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            moveType = 1;
            LogUtils.e("onInterceptTouchEvent moveType=" + moveType);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MaskInfoData maskInfoData = maskView.getMaskDataInfo();
        if (maskInfoData == null) {
            LogUtils.e("MaskInfoData==null");
            return true;
        }
        int i = event.getAction() & MotionEvent.ACTION_MASK;
        //蒙版view中心位置，需要根据translate 值重新计算 The center position of the mask view needs to be recalculated according to the translate value
        if (i == MotionEvent.ACTION_DOWN) {
            PointF centerDownCenter = new PointF(mCenterForMaskView.x + maskView.getBgTransX(), mCenterForMaskView.y - maskView.getBgTransY());
            centerDownCenter.x += translationX;
            centerDownCenter.y += translationY;
            Log.d("MaskView Action_down", " centerDown x:" + centerDownCenter.x + " y:" + centerDownCenter.y
                    + " ||maskCenter x:" + maskView.getCenterPoint().x + " y:" + maskView.getCenterPoint().y);
            mType = maskInfoData.getMaskType();
            //触发羽化值调节 Trigger Feather Value Adjustment
            float endRotation = rotation - maskView.getBgRotation();
            mFeatherPath = NvMaskHelper.buildFeatherPath(mType, centerDownCenter, endRotation, maskInfoData.getMaskHeight(), ScreenUtils.getScreenWidth(), videoFragmentHeight, mFeatherIconDis);
            mWidthPath = NvMaskHelper.buildMaskWidthPath(centerDownCenter, endRotation, maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), ScreenUtils.getScreenWidth(), mMaskWidthIconDis);
            mHeightPath = NvMaskHelper.buildMaskHeightPath(centerDownCenter, endRotation, maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), ScreenUtils.getScreenWidth(), mHeightIconDis);
            mCornerPath = NvMaskHelper.buildMaskCornerPath(centerDownCenter, endRotation, maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), ScreenUtils.getScreenWidth(), mMaskRoundCornerDis);
            mMaskPath = getMaskPath(maskInfoData, centerDownCenter);
            //invalidate();
            moveType = 1;
            actionX = event.getX();
            actionY = event.getY();
            if (isTouchPointInPath((int) actionX, (int) actionY, mMaskPath)) {
                doScroll = true;
                doFeather = false;
                doMaskWidth = false;
                doMaskHeight = false;
                doMaskRoundCorner = false;
                Log.e(TAG, "doFeather");
                touchClick = true;
                touchClickDownTime = System.currentTimeMillis();
            }
            //触发拖拽 Trigger dragging
            else if (isTouchPointInPath((int) actionX, (int) actionY, mFeatherPath)) {

                doFeather = true;
                doScroll = false;
                doMaskWidth = false;
                doMaskHeight = false;
                doMaskRoundCorner = false;
                Log.e(TAG, "doScroll");
            }
            //触发宽度调整 Trigger width adjustment

            else if ((mType == MaskInfoData.MaskType.RECT || mType == MaskInfoData.MaskType.CIRCLE) && isTouchPointInPath((int) actionX, (int) actionY, mWidthPath)) {
                currentMaskWidth = maskInfoData.getMaskWidth();
                doMaskWidth = true;
                doScroll = false;
                doFeather = false;
                doMaskHeight = false;
                doMaskRoundCorner = false;
                Log.e(TAG, "doMaskWidth");
            } else if ((mType == MaskInfoData.MaskType.RECT || mType == MaskInfoData.MaskType.CIRCLE) && isTouchPointInPath((int) actionX, (int) actionY, mHeightPath)) {
                currentMaskHeight = maskInfoData.getMaskHeight();
                doMaskHeight = true;
                doMaskWidth = false;
                doScroll = false;
                doFeather = false;
                doMaskRoundCorner = false;
                Log.e(TAG, "doMaskHeight");
            } else if (mType == MaskInfoData.MaskType.RECT && isTouchPointInPath((int) actionX, (int) actionY, mCornerPath)) {
                doMaskRoundCorner = true;
                doScroll = false;
                doMaskWidth = false;
                doMaskHeight = false;
                doFeather = false;
                Log.e(TAG, "doMaskRoundCorner");
            }
        } else if (i == MotionEvent.ACTION_POINTER_DOWN) {
            LogUtils.e("ACTION_POINTER_2_DOWN point count = " + event.getPointerCount());
            moveType = 2;
            spacing = getSpacing(event);
            degree = getDegree(event);
            maskView.onScaleBegin();
            currentRotation = rotation;
            touchClick = false;
        } else if (i == MotionEvent.ACTION_MOVE) {
            int moveX = (int) (event.getX() - actionX);
            int moveY = (int) (event.getY() - actionY);
            if ((Math.abs(moveX) > 10 || Math.abs(moveY) > 10) && touchClick) {
                touchClick = false;
            }
            if (moveType == 1) {
                if (doFeather) {
                    //羽化值变化和高度变化相反
                    // The change of eclosion value is opposite to the change of height
                    int changValue = -buildChangeValueForHeight(rotation, moveX, moveY);
                    mFeatherIconDis += changValue;
                    int baseDis = SizeUtils.dp2px(10);
                    if (mFeatherIconDis <= baseDis) {
                        mFeatherIconDis = baseDis;
                        featherWidth = 0;
                    } else if (mFeatherIconDis >= baseDis * 3) {
                        mFeatherIconDis = baseDis * 3;
                        featherWidth = 1000;
                    } else {
                        featherWidth += changValue * 1.0f / (baseDis * 2) * 1000;
                    }
                    maskView.setFeatherWidth(featherWidth, mFeatherIconDis);
                    actionX = event.getX();
                    actionY = event.getY();
                    Log.e(TAG, "featherWidth = " + featherWidth);
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged();
                    }
                } else if (doScroll) {
                    float translationXDValue = event.getX() - actionX;
                    float translationYDValue = event.getY() - actionY;
//                    PointF translationDValue = NvMaskHelper.getPointByAngle(new PointF(translationXDValue, translationYDValue), new PointF(0, 0), rotation);
//                    translationX = translationX + translationDValue.x;
//                    translationY = translationY + translationDValue.y;
                    translationX = translationX + translationXDValue;
                    translationY = translationY + translationYDValue;
                    actionX = event.getX();
                    actionY = event.getY();
                    if (maxTranslationX == 0) {
                        maxTranslationX = ScreenUtils.getScreenWidth() / 2;
                    }
                    if (maxTranslationY == 0) {
                        maxTranslationY = maxTranslationX;
                    }
                    if (translationX >= maxTranslationX) {
                        translationX = maxTranslationX;
                    } else if (translationX <= -maxTranslationX) {
                        translationX = -maxTranslationX;
                    }
                    if (translationY >= maxTranslationY) {
                        translationY = maxTranslationY;
                    } else if (translationY <= -maxTranslationY) {
                        translationY = -maxTranslationY;
                    }
                    maskView.setTranslation(translationX, translationY);
                    Log.e(TAG, "ACTION_MOVE translationX = " + translationX + " translationY= " + translationY);
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged();
                    }
                } else if (doMaskWidth) {

                    //这里要计算在当前旋转角度下，对应方向上的距离
                    // So we're going to calculate the distance in that direction at the current rotation Angle
                    int changeValue = buildChangeValueForWidth(rotation, moveX, moveY);
                    int currentWidth = currentMaskWidth + changeValue * 2;
                    //这里需要计算极限值 So we need to compute the limit value here
                    if (currentWidth <= 0) {
                        currentWidth = 0;
                    } else if (currentWidth >= ScreenUtils.getScreenWidth() - mMaskWidthIconDis * 4) {
                        currentWidth = ScreenUtils.getScreenWidth() - mMaskWidthIconDis * 4;
                    }
                    maskView.setMaskWidth(currentWidth);
                    Log.e(TAG, "ACTION_MOVE doMaskWidth = " + currentWidth);
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged();
                    }
                } else if (doMaskHeight) {
                    //这里要计算在当前旋转角度下，对应方向
                    // And what we're going to do here is we're going to calculate the direction of the current rotation
                    int changeValue = buildChangeValueForHeight(rotation, moveX, moveY);
                    int curHeight = currentMaskHeight + changeValue * 2;

                    //这里需要计算极限值 So we need to compute the limit value here
                    if (curHeight <= 0) {
                        curHeight = 0;
                    } else if (curHeight >= ScreenUtils.getScreenWidth() - mHeightIconDis * 4) {
                        curHeight = ScreenUtils.getScreenWidth() - mHeightIconDis * 4;
                    }
                    maskView.setMaskHeight(curHeight);
                    Log.e(TAG, "doMaskHeight = " + curHeight);
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged();
                    }
                } else if (doMaskRoundCorner) {
                    //计算圆角 重新绘制 Calculate the rounded corners and redraw
                    int changValue = buildChangeValueForHeight(rotation, moveX, moveY);
                    mMaskRoundCornerDis += changValue;
                    int baseDis = SizeUtils.dp2px(10);
                    if (mMaskRoundCornerDis <= baseDis) {
                        mMaskRoundCornerDis = baseDis;
                        roundCornerWidthRate = 0;
                    } else if (mMaskRoundCornerDis >= baseDis * 5) {
                        mMaskRoundCornerDis = baseDis * 5;
                        //取最小边一半作为圆角半径 Take half of the smallest edge as the radius of the fillet
                        roundCornerWidthRate = 1;
                    } else {
                        mMaskRoundCornerDis += changValue;
                        roundCornerWidthRate = mMaskRoundCornerDis * 1.0f / (baseDis * 5);
                    }
                    Log.e(TAG, "ACTION_MOVE doMaskRoundCorner =" + roundCornerWidthRate + " mMaskRoundCornerDis = " + mMaskRoundCornerDis + "  changValue=" + changValue);
                    maskView.setRoundCornerWidth(roundCornerWidthRate, mMaskRoundCornerDis);
                    actionX = event.getX();
                    actionY = event.getY();
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged();
                    }
                }
            } else if (moveType == 2) {

                scale = getSpacing(event) / spacing;
                maskView.onScale(scale);
                rotation = currentRotation + getDegree(event) - degree;
                Log.e(TAG, "ACTION_MOVE ZoomView rotation===" + rotation + " |scale=" + scale);
                if (rotation > 360) {
                    rotation = rotation - 360;
                }
                if (rotation < -360) {
                    rotation = rotation + 360;
                }
                maskView.onRotation(rotation);
                if (onDataChangeListener != null) {
                    onDataChangeListener.onDataChanged();
                }
            }
            //maskView.setOnTouchInfo((int) rotation,scale,translationX,translationY);
//            Log.e(TAG, "ACTION_MOVE rotation = " + rotation + "   scale = " + scale);
        } else if (i == MotionEvent.ACTION_UP || i == MotionEvent.ACTION_POINTER_UP) {
            if (i == MotionEvent.ACTION_UP) {
                if ((System.currentTimeMillis() - touchClickDownTime) < 100 && touchClick && mType == MaskInfoData.MaskType.TEXT) {
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onMaskTextClick();
                    }
                }
                touchClick = false;
            }
            moveType = 0;
            maskView.onScaleEnd();
            doFeather = false;
            doScroll = false;
            doMaskWidth = false;
            doMaskHeight = false;
        }
        return true;
    }

    private Path getMaskPath(MaskInfoData maskInfoData, PointF centerDownCenter) {
        int mType = maskInfoData.getMaskType();
        if (mType == MaskRegionInfoData.MaskType.LINE) {
            //线性蒙版拖拽位置的特殊性，需要计算横线的上下一段距离触发拖拽操作
            //The drag position of the linear mask is special, so it is necessary to calculate the upper and lower distance of the horizontal line to trigger the drag operation
            mMaskPath = NvMaskHelper.lineRegionTouchBuild(maskInfoData.getMaskWidth(), centerDownCenter, mCenterCircleRadius, 0);
        } else if (mType == MaskRegionInfoData.MaskType.MIRROR) {
            mMaskPath = NvMaskHelper.mirrorRegionInfoPath(maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), centerDownCenter, mCenterCircleRadius, 0);
        } else if (mType == MaskRegionInfoData.MaskType.RECT) {
            mMaskPath = NvMaskHelper.rectRegionInfoPath(maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), centerDownCenter, mCenterCircleRadius, roundCornerWidthRate);
        } else if (mType == MaskRegionInfoData.MaskType.CIRCLE) {
            mMaskPath = NvMaskHelper.circleRegionInfoPath(maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), centerDownCenter, mCenterCircleRadius, 0);
        } else if (mType == MaskRegionInfoData.MaskType.HEART) {
            mMaskPath = NvMaskHelper.heartRegionInfoPath(maskInfoData.getMaskWidth(), centerDownCenter, mCenterCircleRadius, 0);
        } else if (mType == MaskInfoData.MaskType.STAR) {
            mMaskPath = NvMaskHelper.starRegionInfoPath(maskInfoData.getMaskWidth(), centerDownCenter, mCenterCircleRadius, 0);
        } else if (mType == MaskInfoData.MaskType.TEXT) {
            mMaskPath = NvMaskHelper.textRegionInfoPath(maskInfoData.getMaskWidth(), maskInfoData.getMaskHeight(), centerDownCenter);
        }
        return mMaskPath;
    }

    /**
     * 触碰两点间距离
     * Distance between two points of contact
     *
     * @param event
     * @return
     */
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离 The trigonometric function is used to get the distance between two points
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 取旋转角度
     * Take the rotation Angle
     *
     * @param event
     * @return
     */
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度 You get the rotation Angle between the two fingers
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 判断点是否在路境内
     * Determine if the point is within the road
     *
     * @param x
     * @param y
     * @param path
     * @return
     */
    private boolean isTouchPointInPath(int x, int y, Path path) {

        //构造一个区域对象，左闭右开的。 Construct a region object, closed left and open right.
        RectF r = new RectF();
        //计算控制点的边界 Calculate the boundary of control points
        path.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域 Sets the region path and clips the region described
        re.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        //判断触摸点是否在封闭的path内 在返回true 不在返回false
        // Determine if a touchpoint is inside a closed path and return true instead of false

        return re.contains(x, y);
    }

    /**
     * 计算当前的滑动，在当前旋转角度下，垂直于该方向上的滑动距离
     * Calculates the current slide, at the current rotation Angle, perpendicular to the slide in that direction
     *
     * @param mRotation
     * @param moveX
     * @param moveY
     * @return
     */
    private int buildChangeValueForWidth(float mRotation, int moveX, int moveY) {
        int moveValue = 0;
        //计算角度为-360 - 360  顺时针 逆时针 两项
        // Calculate the Angle of -360 -360 clockwise and anticlockwise
        if ((mRotation >= 0 && mRotation < 45) || (mRotation >= 315 && mRotation < 360) || (mRotation <= 0 && mRotation > -45) || (mRotation <= -315 && mRotation > -360)) {
            moveValue = moveX;
        } else if ((mRotation >= 45 && mRotation < 135) || (mRotation <= -225 && mRotation > -315)) {
            moveValue = moveY;
        } else if ((mRotation >= 135 && mRotation < 225) || (mRotation <= -135 && mRotation > -225)) {
            moveValue = -moveX;
        } else if ((mRotation >= 225 && mRotation < 315) || (mRotation <= -45 && mRotation > -135)) {
            moveValue = -moveY;
        }
        return moveValue;
    }

    /**
     * 计算当前的滑动，在当前旋转角度下，垂直于该方向上的滑动距离
     * Calculates the current slide, at the current rotation Angle, perpendicular to the slide in that direction
     *
     * @param mRotation
     * @param moveX
     * @param moveY
     * @return
     */
    private int buildChangeValueForHeight(float mRotation, int moveX, int moveY) {
        int moveValue = 0;
        //计算角度为-360 - 360  顺时针 逆时针 两项
        // Calculate the Angle of -360 -360 clockwise and anticlockwise
        if ((mRotation >= 0 && mRotation < 45) || (mRotation >= 315 && mRotation < 360) || (mRotation <= 0 && mRotation > -45) || (mRotation <= -315 && mRotation > -360)) {
            moveValue = -moveY;
        } else if ((mRotation >= 45 && mRotation < 135) || (mRotation <= -225 && mRotation > -315)) {
            moveValue = moveX;
        } else if ((mRotation >= 135 && mRotation < 225) || (mRotation <= -135 && mRotation > -225)) {
            moveValue = moveY;
        } else if ((mRotation >= 225 && mRotation < 315) || (mRotation <= -45 && mRotation > -135)) {
            moveValue = -moveX;
        }
        return moveValue;
    }

    public MaskInfoData getMaskInfoData() {
        if (maskView != null) {
            return maskView.getMaskDataInfo();
        }
        return null;
    }

    /**
     * @param maskType 即将生效的type  type to take effect
     * @param infoData 老数据  Old data
     */
    public void setMaskTypeAndInfo(int maskType, MaskInfoData infoData) {
        setMaskTypeAndInfo(maskType, infoData, true);
    }

    /**
     * 更新蒙版尺寸
     * Updated mask size
     *
     * @param width  width
     * @param height height
     */
    public void updateMaskSize(int width, int height) {
        maskView.setMaskWidth(width);
        maskView.setMaskHeight(height);
    }

    /**
     * @param maskType 即将生效的type  type to take effect
     * @param infoData 老数据  Old data
     */
    public void setMaskTypeAndInfo(int maskType, MaskInfoData infoData, boolean notifyTimeline) {
        if (infoData != null && maskType != infoData.getMaskType()) {
            infoData = null;
        }
        if (maskType == MaskInfoData.MaskType.NONE) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
        maskView.setMaskTypeAndInfo(maskType, infoData);
        if (infoData != null) {
            this.translationX = infoData.getTranslationX();
            this.translationY = infoData.getTranslationY();
            this.rotation = infoData.getRotation();
        } else {
            MaskInfoData maskDataInfo = maskView.getMaskDataInfo();
            if (maskDataInfo != null) {
                this.translationX = maskDataInfo.getTranslationX();
                this.translationY = maskDataInfo.getTranslationY();
                this.rotation = maskDataInfo.getRotation();
            }
        }
        this.currentRotation = this.rotation;
        if (onDataChangeListener != null && notifyTimeline) {
            onDataChangeListener.onDataChanged();
        }
    }

    public OnDataChangeListener onDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

    public void clear() {
        if (maskView != null) {
            maskView.clearData();
        }
    }

    public void setMaskViewVisibility(int visibility) {
        if (maskView != null) {
            maskView.setVisibility(visibility);
        }
    }

    public void setBackgroundInfo(float transX, float transY, float rotation, float scaleX) {
        if (maskView != null) {
            maskView.setBackgroundCenter(transX, transY, rotation, scaleX);
        }
    }

    /**
     * 蒙版数据变化监听
     */
    public interface OnDataChangeListener {
        /**
         * 数据监听
         * Data monitoring
         */
        void onDataChanged();

        /**
         * 蒙版字体点击事件
         * Data monitoring
         */
        void onMaskTextClick();
    }
}
