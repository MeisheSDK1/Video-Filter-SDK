package com.meishe.base.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/02
 *     desc  : utils about size
 * </pre>
 * 尺寸工具类
 * Dimension tool class
 */
public final class SizeUtils {

    private SizeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(final float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(final float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(final float pxValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * Converts an unpacked complex data value holding a dimension to its final floating
     * point value. The two parameters <var>unit</var> and <var>value</var>
     * are as in {@link TypedValue#TYPE_DIMENSION}.
     *
     * 将包含维度的未打包复杂数据值转换为其最终浮点数
     * 点值。两个参数<var>单位</var>和<var>值</var>
     * 与{@link TypedValue#TYPE_DIMENSION}相同。
     *
     * @param value The value to apply the unit to. 要应用该单元的值
     * @param unit  The unit to convert from. 转换的单位
     * @return The complex floating point value multiplied by the appropriate metrics depending on its unit. 根据其单位乘以适当度量的复杂浮点值
     */
    public static float applyDimension(final float value, final int unit) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (unit == TypedValue.COMPLEX_UNIT_PX) {
            return value;
        } else if (unit == TypedValue.COMPLEX_UNIT_DIP) {
            return value * metrics.density;
        } else if (unit == TypedValue.COMPLEX_UNIT_SP) {
            return value * metrics.scaledDensity;
        } else if (unit == TypedValue.COMPLEX_UNIT_PT) {
            return value * metrics.xdpi * (1.0f / 72);
        } else if (unit == TypedValue.COMPLEX_UNIT_IN) {
            return value * metrics.xdpi;
        } else if (unit == TypedValue.COMPLEX_UNIT_MM) {
            return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }

    /**
     * Force get the size of view.
     * 得到视图的大小
     * <p>e.g.</p>
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.OnGetSizeListener() {
     *     Override
     *     public void onGetSize(final View view) {
     *         view.getWidth();
     *     }
     * });
     * </pre>
     *
     * @param view     The view. 视图
     * @param listener The get size listener. 获取大小的监听
     */
    public static void forceGetViewSize(final View view, final OnGetSizeListener listener) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onGetSize(view);
                }
            }
        });
    }

    public static int getMeasuredWidth(final View view) {
        return measureView(view)[0];
    }

    public static int getMeasuredHeight(final View view) {
        return measureView(view)[1];
    }

    /**
     * Measure the view.
     * 测量视图
     * @param view The view. 视图
     * @return arr[0] : view's width, arr[1]: view's height arr[0]:视图的宽度，arr[1]:视图的高度
     */
    public static int[] measureView(final View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int lpHeight = lp.height;
        int heightSpec;
        if (lpHeight > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(widthSpec, heightSpec);
        return new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface 接口
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The interface On get size listener.
     * 获取大小的监听
     */
    public interface OnGetSizeListener {
        /**
         * On get size.
         * 获取大小
         * @param view the view
         */
        void onGetSize(View view);
    }
}
