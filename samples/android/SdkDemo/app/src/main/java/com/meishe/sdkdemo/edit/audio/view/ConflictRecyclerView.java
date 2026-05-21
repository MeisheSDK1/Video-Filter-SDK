package com.meishe.sdkdemo.edit.audio.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/1/11 16:07
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class ConflictRecyclerView extends RecyclerView {
    private int mScrollPointerId;
    private int mInitialTouchX, mInitialTouchY;
    private int mTouchSlop;
    public ConflictRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public ConflictRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConflictRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        this.mTouchSlop = vc.getScaledTouchSlop();
    }

    @Override
    public void setScrollingTouchSlop(int slopConstant) {
        ViewConfiguration vc = ViewConfiguration.get(this.getContext());
        switch (slopConstant) {
            case 0:
                this.mTouchSlop = vc.getScaledTouchSlop();
            case 1:
                this.mTouchSlop = vc.getScaledPagingTouchSlop();
                break;
            default:
                Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + slopConstant + "; using default value");

        }
        super.setScrollingTouchSlop(slopConstant);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        boolean canScrollHorizontally = getLayoutManager().canScrollHorizontally();
        boolean canScrollVertically = getLayoutManager().canScrollVertically();
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        switch (action) {
            case 0:
                mScrollPointerId = e.getPointerId(0);
                this.mInitialTouchX = (int) (e.getX() + 0.5F);
                this.mInitialTouchY = (int) (e.getY() + 0.5F);
                return super.onInterceptTouchEvent(e);

            case 2:
                int index = e.findPointerIndex(this.mScrollPointerId);
                if (index < 0) {
                    Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }

                int x = (int) (e.getX(index) + 0.5F);
                int y = (int) (e.getY(index) + 0.5F);
                if (getScrollState() != 1) {
                    int dx = x - this.mInitialTouchX;
                    int dy = y - this.mInitialTouchY;
                    boolean startScroll = false;
                    if (canScrollHorizontally && Math.abs(dx) > this.mTouchSlop && Math.abs(dx) > Math.abs(dy)) {
                        startScroll = true;
                    }

                    if (canScrollVertically && Math.abs(dy) > this.mTouchSlop && Math.abs(dy) > Math.abs(dx)) {
                        startScroll = true;
                    }

                    Log.d("MyRecyclerView", "canX:" + canScrollHorizontally + "--canY" + canScrollVertically + "--dx:" + dx + "--dy:" + dy + "--startScorll:" + startScroll + "--mTouchSlop" + mTouchSlop);

                    return startScroll && super.onInterceptTouchEvent(e);
                }
                return super.onInterceptTouchEvent(e);
            case 5:
                this.mScrollPointerId = e.getPointerId(actionIndex);
                this.mInitialTouchX = (int) (e.getX(actionIndex) + 0.5F);
                this.mInitialTouchY = (int) (e.getY(actionIndex) + 0.5F);
                return super.onInterceptTouchEvent(e);
        }

        return super.onInterceptTouchEvent(e);
    }
}
