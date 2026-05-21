package com.meishe.base.observer;

import android.database.Observable;
import android.os.Looper;

import com.meishe.base.utils.ThreadUtils;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2020/12/8 18:14
 * @Description :App 进入后台的被观察者 The Observable that the application enters the background
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BackgroundObservable extends Observable<BackgroundObserver> {

    /**
     * 应用进入后台
     * Application turn to background
     */
    public void turnToBackground() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).turnToBackground();
            }
        } else {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).turnToBackground();
                    }
                }
            });
        }
    }

    /**
     * 应用进入前台
     * Application turn to foreground
     */
    public void turnToForeground() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).turnToForeground();
            }
        } else {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = mObservers.size() - 1; i >= 0; i--) {
                        mObservers.get(i).turnToForeground();
                    }
                }
            });
        }
    }

    @Override
    public void registerObserver(BackgroundObserver observer) {
        if (!mObservers.contains(observer)) {
            super.registerObserver(observer);
        }
    }

    @Override
    public void unregisterObserver(BackgroundObserver observer) {
        if (mObservers.contains(observer)) {
            super.unregisterObserver(observer);
        }
    }
}
