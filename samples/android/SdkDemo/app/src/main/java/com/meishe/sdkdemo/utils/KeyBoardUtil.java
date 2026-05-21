package com.meishe.sdkdemo.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jml on 2018/8/9.
 * Created for ${FUNCTION}
 * Description
 */

public class KeyBoardUtil {
    /**
     * 滑动消息列表隐藏键盘
     * Slide the message list to hide the keyboard
     */
    public static void hideSoftKeyBroad(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != inputMethodManager && inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public static void hideSoftKeyBroad(Context activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            // 如果软键盘已经显示，则隐藏，反之则显示
            //If the soft keyboard is already displayed, it is hidden, and if not, it is displayed
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示键盘
     * Display keyboard
     *
     * @param view
     */
    public static void showKeyBoard(View view) {
        //如果当前软键盘处于隐藏状态，显示出来
        //If the current soft keyboard is hidden, it is displayed
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);

        }
    }
}
