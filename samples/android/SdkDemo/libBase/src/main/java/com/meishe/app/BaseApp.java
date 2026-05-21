package com.meishe.app;

import android.app.Application;
import android.content.Context;

import com.meishe.base.utils.Utils;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2021/9/18.
 */
public class BaseApp extends Application {
    private static Context context;
    /**
     * 是否兼容鸿蒙系统contentUri
     */
    public static boolean CONTENT_FLAG = true;
    private static boolean zhFlag = false;

    public static boolean isZh() {
        return zhFlag;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        zhFlag = Utils.isZh();
        Utils.init(this);
    }
}
