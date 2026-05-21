package com.meishe.third.adpater.util;

import android.view.MotionEvent;

/**
 * The type Touch event util.
 * 触摸事件工具类
 */
public class TouchEventUtil {

    /**
     * Gets touch action.
     * 被触摸操作
     * @param actionId the action id 操作id
     * @return the touch action 触摸操作
     */
    public static String getTouchAction(int actionId) {
        String actionName = "Unknow:id=" + actionId;
        if (actionId == MotionEvent.ACTION_DOWN) {
            actionName = "ACTION_DOWN";
        } else if (actionId == MotionEvent.ACTION_MOVE) {
            actionName = "ACTION_MOVE";
        } else if (actionId == MotionEvent.ACTION_UP) {
            actionName = "ACTION_UP";
        } else if (actionId == MotionEvent.ACTION_CANCEL) {
            actionName = "ACTION_CANCEL";
        } else if (actionId == MotionEvent.ACTION_OUTSIDE) {
            actionName = "ACTION_OUTSIDE";
        }
        return actionName;
    }

}
