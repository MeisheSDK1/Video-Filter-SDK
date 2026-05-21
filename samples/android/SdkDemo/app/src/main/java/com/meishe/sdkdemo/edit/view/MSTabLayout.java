package com.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.material.tabs.TabLayout;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/7/8 下午5:33
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MSTabLayout extends TabLayout {
    public MSTabLayout(Context context) {
        this(context,null);
    }

    public MSTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MSTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
