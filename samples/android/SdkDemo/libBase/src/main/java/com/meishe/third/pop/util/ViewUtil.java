package com.meishe.third.pop.util;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;

import com.meishe.base.utils.Utils;

import java.util.Locale;

import androidx.annotation.LayoutRes;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/4/1 14:04
 * @Description :View相关的工具 Util about view
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ViewUtil {

    /**
     * Expand touch area
     * <p></>
     * 扩大View的点击区域
     * @param view view
     * @param size 扩充大小 size to be expanded
     */
    public static void expandTouchArea(final View view, final int size) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                view.getHitRect(rect);

                rect.top -= size;
                rect.bottom += size;
                rect.left -= size;
                rect.right += size;

                parentView.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }

    /**
     * Set the enabled state of this view.
     *
     * @param view    The view.
     * @param enabled True to enabled, false otherwise.
     */
    public static void setViewEnabled(View view, boolean enabled) {
        setViewEnabled(view, enabled, (View) null);
    }

    /**
     * Set the enabled state of this view.
     *
     * @param view     The view.
     * @param enabled  True to enabled, false otherwise.
     * @param excludes The excludes.
     */
    public static void setViewEnabled(View view, boolean enabled, View... excludes) {
        if (view == null) return;
        if (excludes != null) {
            for (View exclude : excludes) {
                if (view == exclude) return;
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setViewEnabled(viewGroup.getChildAt(i), enabled, excludes);
            }
        }
        view.setEnabled(enabled);
    }

    /**
     * Return whether horizontal layout direction of views are from Right to Left.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Locale primaryLocale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                primaryLocale = Utils.getApp().getResources().getConfiguration().getLocales().get(0);
            } else {
                primaryLocale = Utils.getApp().getResources().getConfiguration().locale;
            }
            return TextUtils.getLayoutDirectionFromLocale(primaryLocale) == View.LAYOUT_DIRECTION_RTL;
        }
        return false;
    }

    /**
     * Fix the problem of topping the ScrollView nested ListView/GridView/WebView/RecyclerView.
     *
     * @param view The root view inner of ScrollView.
     */
    public static void fixScrollViewTopping(View view) {
        view.setFocusable(false);
        ViewGroup viewGroup = null;
        if (view instanceof ViewGroup) {
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup == null) {
            return;
        }
        for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
            View childAt = viewGroup.getChildAt(i);
            childAt.setFocusable(false);
            if (childAt instanceof ViewGroup) {
                fixScrollViewTopping(childAt);
            }
        }
    }

    public static View layoutId2View(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate.inflate(layoutId, null);
    }
}
