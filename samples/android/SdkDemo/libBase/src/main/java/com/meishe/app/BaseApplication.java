package com.meishe.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.meishe.base.observer.BackgroundObservable;
import com.meishe.base.observer.BackgroundObserver;
import com.meishe.base.utils.ProcessUtils;
import com.meishe.base.utils.Utils;

import androidx.annotation.NonNull;


/**
 * The type Base application.
 * 应用基类
 */
@Deprecated
public abstract class BaseApplication extends Application {
    /**
     * 非0表示应用在前台，0则表示在后台
     * Non-zero means applied in the foreground, and zero means applied in the background
     */
    private int activityState;
    private BackgroundObservable mBackgroundObservable;

    @Override
    public void onCreate() {
        super.onCreate();
        /*
         * 主进程初始化数据
         * The main process initializes the data
         * */
        if (ProcessUtils.isMainProcess()) {
            Utils.init(this);
            registerLifecycleCallbacks();
            initApplication(true);
            mBackgroundObservable = new BackgroundObservable();
        } else {
            initApplication(false);
        }
    }

    /**
     * Init application.
     * 初始化应用程序
     *
     * @param isMainProcess the is main process  主要过程
     */
    public abstract void initApplication(boolean isMainProcess);

    /**
     * 是否在后台
     * Is it in the background
     *
     * @return the boolean
     */
    public boolean isBackground() {
        return activityState == 0;
    }

    /**
     * 进入后台
     * Into the background
     */
    public void turnToBackground() {

    }

    /**
     * 进入前台
     * Into the front desk
     */
    public void turnToForeground() {

    }

    /**
     * activity生命周期监听
     * activity Life cycle monitoring
     */
    private void registerLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                ++activityState;
                if (activityState == 1) {
                    mBackgroundObservable.turnToForeground();
                    /*
                     * 从后台切到前台
                     * Cut from the back to the front
                     * */
                    turnToForeground();
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                --activityState;
                if (activityState == 0) {
                    mBackgroundObservable.turnToBackground();
                    turnToBackground();
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    /**
     * 注册app进入后台的监听
     * register background observer
     */
    public void registerBackgroundObserver(BackgroundObserver observer) {
        if (mBackgroundObservable != null) {
            mBackgroundObservable.registerObserver(observer);
        }
    }

    /**
     * 注销app进入后台的监听
     * unregister background observer
     */
    public void unregisterBackgroundObserver(BackgroundObserver observer) {
        if (mBackgroundObservable != null) {
            mBackgroundObservable.unregisterObserver(observer);
        }
    }
}