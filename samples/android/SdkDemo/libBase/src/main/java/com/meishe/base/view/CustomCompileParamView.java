package com.meishe.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.meishe.base.R;
import com.meishe.base.view.bean.CompileParamData;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Custom compile param view.
 *
 * @author :Jml
 * @date :2020/7/27 14:42
 * @des : 进度拖拽，附带吸附效果，吸附到最近的点。
 * Progress drag, with an adsorption effect, to the nearest point
 */
public class CustomCompileParamView extends RelativeLayout {
    private static final String TAG = "NvDouVideoEffectTypeView";
    private OnFunctionSelectedListener onFunctionSelectedListener;
    //
    private LinearLayout ll_data;
    private View view_shadow;
    private LinearLayout view_mask;
    private View view_background;
    //onTouch 事件使用变量
    private float downX, downY;
    private float currentX;
    /**
     * The Do click.
     * 判断为去响应点击事件
     */
    boolean doClick = false;
    /**
     * The Do scroll by on touch.
     * shadow是否跟随手指移动
     */
    boolean doScrollByOnTouch = false;

    private List<CompileParamData> itemDataList = new ArrayList<>();
    /*
    * 节点对应x坐标集合
    * The node corresponds to the set of x coordinates
    * */
    private List<Integer> ItemPointX = new ArrayList<>();
    /*
    * 每个节点之间的距离
    * The distance between each node
    * */
    private int itemWidth;
    private int marginLeft;
    private int marginRight;
    private CompileParamData mSelected = null;

    public CustomCompileParamView(Context context) {
        this(context, null);
    }
    public CustomCompileParamView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setListener();
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.nv_layout_custom_compile_params, this);
        ll_data = rootView.findViewById(R.id.data);
        view_shadow = rootView.findViewById(R.id.view_shadow);
        view_mask = rootView.findViewById(R.id.view_mask);
        view_background = rootView.findViewById(R.id.view_background);
        marginLeft = context.getResources().getDimensionPixelOffset(R.dimen.dp_px_183);
        marginRight = context.getResources().getDimensionPixelOffset(R.dimen.dp_px_183);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {

       /* view_shadow.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });*/
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            downX = event.getRawX();
            downY = event.getRawY();
            currentX = downX;
            float shadowX = view_shadow.getX() + marginLeft;
            int width = view_shadow.getWidth();
            /*
            * 起始点落在shadowView上，随着移动去执行滑动
            * The starting point falls on the shadowView and the slide is performed as it moves
            * */
            if (downX > shadowX - 50 && downX < shadowX + width + 50) {
                doScrollByOnTouch = true;
                doClick = false;
            }/*else{
                            //落在其他选项上，随手指抬起去响应点击事件
                            doClick = true;
                            doScrollByOnTouch = false;
                        }*/
            Log.e(TAG, "onTouch ACTION_DOWN");
            if (null != onFunctionSelectedListener) {
                onFunctionSelectedListener.onTouched();
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            /*
            * 如果手指落下时点击的是滑块范围，需要随手指移动
            * If you click the slider range as your finger drops, you need to move it with your finger
            * */
            if (doScrollByOnTouch) {
                float currentMoveX = event.getRawX();
                /*
                * 滑动shadowView
                *Sliding shadowView
                * */
                float shadowCurrentX = view_shadow.getX();
                /*
                * 计算需要滑动的距离
                * Calculate the distance you need to slide
                * */
                float targetPositionX = shadowCurrentX + currentMoveX - currentX;
                /*
                * 最左变得距离是经典按钮左侧位置
                * The left-most distance becomes the left position of the classic button
                * */
                if (targetPositionX < view_background.getLeft()) {
                    targetPositionX = view_background.getLeft();
                }
                /*
                * 最右侧的位置
                * It's on the far right
                * */
                else if (targetPositionX > view_background.getRight() - view_shadow.getWidth()
                ) {
                    targetPositionX = view_background.getRight() - view_shadow.getWidth();
                }
                doMoveToTargetPosition((int) targetPositionX);
                currentX = currentMoveX;
            }
            Log.e(TAG, "onTouch ACTION_MOVE");
        } else if (action == MotionEvent.ACTION_UP) {
            if (doScrollByOnTouch) {
                /*
                * 手指抬起时 shadowView 的 x坐标
                * The X-coordinate of shadowView when the finger is lifted
                * */
                float currentUpX = view_shadow.getX();
                int targetPosition = getTargetPosition(currentUpX);
                doMoveToTargetPosition(targetPosition);
            }
            Log.e(TAG, "onTouch ACTION_UP");
            doScrollByOnTouch = false;
            if (null != onFunctionSelectedListener) {
                onFunctionSelectedListener.onRelease();
            }
        } else if (action == MotionEvent.ACTION_CANCEL) {
            Log.e(TAG, "onTouch ACTION_CANCEL");
            if (null != onFunctionSelectedListener) {
                onFunctionSelectedListener.onRelease();
            }
        }
        return true;
    }


    /**
     * 根据当前所处的点x坐标 计算应该吸附到哪个点 对应的x坐标
     *Calculate the x-coordinate corresponding to which point you should adsorb according to the x-coordinate of the current point
     * @param currentUpX
     * @return
     */
    private int getTargetPosition(float currentUpX) {
        if (null != ItemPointX && ItemPointX.size() > 0) {
            int leftPointIndex = (int) (currentUpX / itemWidth);
            int leftValue = (int) (currentUpX % itemWidth);
            if (leftValue >= itemWidth / 2) {
                leftPointIndex++;
            }
            /*
            * 设置选中的回调
            * Sets the selected callback
            * */
            if (leftPointIndex >= 0 && leftPointIndex < ItemPointX.size()) {

                mSelected = itemDataList.get(leftPointIndex);
                if (null != onFunctionSelectedListener) {
                    onFunctionSelectedListener.onSelected(mSelected);
                }
            }
            return getTargetIndexXValueByTargetIndex(leftPointIndex);
        }
        return 0;
    }

    /**
     * 计算移动到目标节点位置 计算节点的X坐标
     * Compute the x-coordinate of the node moved to the target node location
     * @param targetIndex
     * @return
     */
    private int getTargetIndexXValueByTargetIndex(int targetIndex) {
        /*
        * 计算位置
        * Calculation of position
        * */
        if (targetIndex == 0) {
            return 0;
        } else if (targetIndex == ItemPointX.size() - 1) {
            return targetIndex * itemWidth - view_shadow.getWidth();
        } else {
            return targetIndex * itemWidth - view_shadow.getWidth() / 2;
        }
    }

    /**
     * shadow移动到目标位置
     * The shadow moves to the target location
     * @param targetPosition 移动的目标位置x坐标
     */
    private void doMoveToTargetPosition(final int targetPosition) {
        /*
        * 绘制进度
        * Draw the progress
        * */
        view_shadow.setX(targetPosition);
    }

    /**
     * Sets selected data.
     * 设置选择数据
     * @param selectedDataList the selected data list 选择数据集合
     */
    public void setSelectedData(List<CompileParamData> selectedDataList) {
        if (null != selectedDataList && selectedDataList.size() > 0) {
            this.itemDataList.addAll(selectedDataList);
            int recommendIndex = 0;
            ItemPointX = new ArrayList<>(itemDataList.size());
            itemWidth = (int) ((view_background.getRight() - view_background.getLeft()) * 1.0F / (itemDataList.size() - 1));
            for (int i = 0; i < itemDataList.size(); i++) {
                CompileParamData compileParamData = itemDataList.get(i);
                /*
                * 判断是否是推荐节点
                * Determine if it is a recommendation node
                * */
                if (compileParamData.isRecommend()) {
                    recommendIndex = i;
                }
                /*
                * 添加对应的节点
                * Add the corresponding node
                * */
                ItemPointX.add(i * itemWidth);

                /*
                * 添加view显示数据
                * Add view to display data
                * */
                TextView textView = new TextView(getContext());
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setTextSize(10);
                textView.setText(compileParamData.getShowData());
                if (compileParamData.isRecommend()) {
                    textView.append("\n"+getResources().getString(R.string.cut_dialog_resolution_tip3));
                }

                /*
                * 添加节点对应的小图标
                * Add a small icon for the node
                * */
                View view = new View(getContext());
                int viewWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.dp_px_24);
                view.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, viewWidth));
                int viewX = 0;
                if (i == 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 10;
                    textView.setLayoutParams(params);
                    textView.setGravity(Gravity.LEFT);
                    viewX = 0;
                } else if (i == itemDataList.size() - 1) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 10;
                    textView.setLayoutParams(params);
                    textView.setGravity(Gravity.RIGHT);
                    viewX = itemWidth * i - viewWidth * (i + 1);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 10;
                    textView.setLayoutParams(params);
                    textView.setGravity(Gravity.CENTER);
                    viewX = itemWidth * i - viewWidth * i - viewWidth / 2;
                }
                ll_data.addView(textView);


                /*
                * 设置view的位置
                * Set the position of the view
                * */
                view.setX(viewX);
                view.setBackground(getResources().getDrawable(R.mipmap.nv_compile_progress));
                view_mask.addView(view);
            }

            /*
            * 默认移动到初始化的推荐位置
            * Move to the recommended location for initialization by default
            * */
            int targetX = getTargetIndexXValueByTargetIndex(recommendIndex);
            doMoveToTargetPosition(targetX);

        }
    }

    public void setOnFunctionSelectedListener(OnFunctionSelectedListener onFunctionSelectedListener) {
        this.onFunctionSelectedListener = onFunctionSelectedListener;
    }


    /**
     * The interface On function selected listener.
     * 功能选择监听器接口
     */
    public interface OnFunctionSelectedListener {
        /**
         * On selected.
         * 选择
         * @param itemData the item data 条目数据
         */
        void onSelected(CompileParamData itemData);

        /**
         * On touched.
         * 触摸
         */
        void onTouched();

        /**
         * On release.
         * 释放
         */
        void onRelease();
    }
}
