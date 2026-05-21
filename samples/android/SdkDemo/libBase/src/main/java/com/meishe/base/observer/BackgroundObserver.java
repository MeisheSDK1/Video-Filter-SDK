package com.meishe.base.observer;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2020/12/8 18:14
 * @Description :App 进入后台的观察者 The observer that the application enters the background
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BackgroundObserver {

    /**
     * 应用进入后台
     * Application turn to background
     */
    public void turnToBackground() {
    }

    /**
     * 应用进入前台
     * Application turn to foreground
     */
    public void turnToForeground() {
    }
}
