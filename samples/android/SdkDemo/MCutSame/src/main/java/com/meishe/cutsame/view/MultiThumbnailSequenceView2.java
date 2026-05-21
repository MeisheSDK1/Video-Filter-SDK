package com.meishe.cutsame.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.meishe.engine.view.MultiThumbnailSequenceView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by CaoZhiChao on 2020/11/4 13:59
 * 多缩略图序列视图类
 * Multiple thumbnail sequence view class
 */
public class MultiThumbnailSequenceView2 extends MultiThumbnailSequenceView {
    private static final int TOUCH_ID = -1000;
    private static final long TIME_DELAY = 40;
    private OnScrollListener mScrollListener;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View scroller = (View) msg.obj;
            if (msg.what == TOUCH_ID) {
                boolean finish = isFinishScroll();
                if (finish) {
                    handleStop();
                    handler.removeMessages(TOUCH_ID);
                } else {
                    handler.sendMessageDelayed(handler.obtainMessage(TOUCH_ID, scroller), TIME_DELAY);
                }
            }
        }
    };

    public MultiThumbnailSequenceView2(Context context) {
        super(context);
    }

    public MultiThumbnailSequenceView2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MultiThumbnailSequenceView2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiThumbnailSequenceView2(Context context, AttributeSet attributeSet, int i, int i1) {
        super(context, attributeSet, i, i1);
    }

    @Override
    protected void onScrollChanged(int i, int i1, int i2, int i3) {
        super.onScrollChanged(i, i1, i2, i3);
        if (mScrollListener != null) {
            mScrollListener.onScrollChanged(i, i2);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (mScrollListener != null) {
                mScrollListener.onSeekingTimeline();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            handler.sendMessageDelayed(handler.obtainMessage(TOUCH_ID, this), 0);
        }
        return super.onTouchEvent(motionEvent);
    }

    public OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public void setScrollListener(OnScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }

    private void handleStop() {
        if (mScrollListener != null) {
            mScrollListener.onScrollStopped();
        }
    }

    private boolean isFinishScroll() {
        boolean isFinish = false;
        Class scrollView = MultiThumbnailSequenceView2.class.getSuperclass().getSuperclass();
        try {
            Field scrollField = scrollView.getDeclaredField("mScroller");
            scrollField.setAccessible(true);
            Object scroller = scrollField.get(MultiThumbnailSequenceView2.this);
            Class overScroller = scrollField.getType();
            Method isFinishedMethod = overScroller.getMethod("isFinished");
            isFinishedMethod.setAccessible(true);
            isFinish = (boolean) isFinishedMethod.invoke(scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return isFinish;
    }

    /**
     * The interface On scroll listener.
     * 滑动监听的接口
     */
    public interface OnScrollListener {

        /**
         * On scroll changed.
         * 滑动改变
         * @param dx    the dx
         * @param oldDx the old dx
         */
        void onScrollChanged(int dx, int oldDx);

        /**
         * On scroll stopped.
         *  滑动停止
         */
        void onScrollStopped();

        /**
         * On seeking timeline.
         * 时间线
         */
        void onSeekingTimeline();

    }
}
