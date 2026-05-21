
package com.meishe.sdkdemo.mimodemo.mediapaker.utils;


import android.os.AsyncTask;

import java.util.concurrent.Executor;


/**
 * Description:  线程池辅助类，整个应用程序就只有一个线程池去管理线程。 可以设置核心线程数、最大线程数、额外线程空状态生存时间，阻塞队列长度来优化线程池。
 * Description: Thread pool helper class. The entire application has only one thread pool to manage threads. You can optimize the thread pool by setting the number of core threads, the maximum number of threads, the extra thread empty state lifetime, and the block queue length.
 */
public class ThreadPoolUtils {

    private static Executor threadPool;

    static {
        threadPool = AsyncTask.THREAD_POOL_EXECUTOR;
    }

    /**
     * 从线程池中抽取线程，执行指定的Runnable对象
     * Extracts the thread from the thread pool and executes the specified Runnable object
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static Executor getExecutor() {
        return threadPool;
    }

}