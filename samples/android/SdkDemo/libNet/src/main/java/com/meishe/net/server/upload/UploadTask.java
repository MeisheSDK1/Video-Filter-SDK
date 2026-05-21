/*
 * Copyright 2016 jeasonlzy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meishe.net.server.upload;

import android.content.ContentValues;

import com.meishe.net.db.UploadManager;
import com.meishe.net.model.Progress;
import com.meishe.net.model.Response;
import com.meishe.net.request.base.ProgressRequestBody;
import com.meishe.net.request.base.Request;
import com.meishe.net.server.OkUpload;
import com.meishe.net.server.task.PriorityRunnable;
import com.meishe.net.utils.HttpUtils;
import com.meishe.net.utils.OkLogger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.Call;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：上传任务类
 * Upload task class
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 */
public class UploadTask<T> implements Runnable {

    /**
     * The Progress.
     * 进展
     */
    public Progress progress;
    /**
     * The Listeners.
     * 监听
     */
    public Map<Object, UploadListener<T>> listeners;
    private ThreadPoolExecutor executor;
    private PriorityRunnable priorityRunnable;

    public UploadTask(String tag, Request<T, ? extends Request> request) {
        HttpUtils.checkNotNull(tag, "tag == null");
        progress = new Progress();
        progress.tag = tag;
        progress.url = request.getBaseUrl();
        progress.status = Progress.NONE;
        progress.totalSize = -1;
        progress.request = request;

        executor = OkUpload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public UploadTask(Progress progress) {
        HttpUtils.checkNotNull(progress, "progress == null");
        this.progress = progress;
        executor = OkUpload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    /**
     * Priority upload task.
     * 优先上传任务
     * @param priority the priority 优先
     * @return the upload task 上传任务
     */
    public UploadTask<T> priority(int priority) {
        progress.priority = priority;
        return this;
    }

    /**
     * Extra 1 upload task.
     * 额外1上传任务
     * @param extra1 the extra 1 额外1
     * @return the upload task 上传任务
     */
    public UploadTask<T> extra1(Serializable extra1) {
        progress.extra1 = extra1;
        return this;
    }

    /**
     * Extra 2 upload task.
     * 额外2上传任务
     * @param extra2 the extra 2 额外的2
     * @return the upload task
     */
    public UploadTask<T> extra2(Serializable extra2) {
        progress.extra2 = extra2;
        return this;
    }

    /**
     * Extra 3 upload task.
     * 额外3上传任务
     * @param extra3 the extra 3  额外3
     * @return the upload task 上传任务
     */
    public UploadTask<T> extra3(Serializable extra3) {
        progress.extra3 = extra3;
        return this;
    }

    /**
     * Save upload task.
     * 保存上传任务
     * @return the upload task 上传任务
     */
    public UploadTask<T> save() {
        UploadManager.getInstance().replace(progress);
        return this;
    }

    /**
     * Register upload task.
     * 注册上传任务
     * @param listener the listener 监听
     * @return the upload task 上传任务
     */
    public UploadTask<T> register(UploadListener<T> listener) {
        if (listener != null) {
            listeners.put(listener.tag, listener);
        }
        return this;
    }

    /**
     * Un register.
     * 取消注册
     * @param listener the listener 监听
     */
    public void unRegister(UploadListener<T> listener) {
        HttpUtils.checkNotNull(listener, "listener == null");
        listeners.remove(listener.tag);
    }

    /**
     * Un register.
     * 取消注册
     * @param tag the tag 标签
     */
    public void unRegister(String tag) {
        HttpUtils.checkNotNull(tag, "tag == null");
        listeners.remove(tag);
    }

    /**
     * Start upload task.
     * 开始上传任务
     * @return the upload task 上传任务
     */
    public UploadTask<T> start() {
        if (OkUpload.getInstance().getTask(progress.tag) == null || UploadManager.getInstance().get(progress.tag) == null) {
            throw new IllegalStateException("you must call UploadTask#save() before UploadTask#start()！");
        }
        if (progress.status != Progress.WAITING && progress.status != Progress.LOADING) {
            postOnStart(progress);
            postWaiting(progress);
            priorityRunnable = new PriorityRunnable(progress.priority, this);
            executor.execute(priorityRunnable);
        } else {
            OkLogger.w("the task with tag " + progress.tag + " is already in the upload queue, current task status is " + progress.status);
        }
        return this;
    }

    /**
     * Restart.
     * 重新开始
     */
    public void restart() {
        pause();
        progress.status = Progress.NONE;
        progress.currentSize = 0;
        progress.fraction = 0;
        progress.speed = 0;
        UploadManager.getInstance().replace(progress);
        start();
    }

    /**
     * 暂停的方法
     * pause
     */
    public void pause() {
        executor.remove(priorityRunnable);
        if (progress.status == Progress.WAITING) {
            postPause(progress);
        } else if (progress.status == Progress.LOADING) {
            progress.speed = 0;
            progress.status = Progress.PAUSE;
        } else {
            OkLogger.w("only the task with status WAITING(1) or LOADING(2) can pause, current status is " + progress.status);
        }
    }

    /**
     * 删除一个任务,会删除下载文件  @return the upload task
     * Deleting a task deletes the downloaded file
     */
    public UploadTask<T> remove() {
        pause();
        UploadManager.getInstance().delete(progress.tag);
        //noinspection unchecked
        UploadTask<T> task = (UploadTask<T>) OkUpload.getInstance().removeTask(progress.tag);
        postOnRemove(progress);
        return task;
    }

    @Override
    public void run() {
        progress.status = Progress.LOADING;
        postLoading(progress);
        final Response<T> response;
        try {
            //noinspection unchecked
            Request<T, ? extends Request> request = (Request<T, ? extends Request>) progress.request;
            final Call rawCall = request.getRawCall();
            request.uploadInterceptor(new ProgressRequestBody.UploadInterceptor() {
                @Override
                public void uploadProgress(Progress innerProgress) {
                    if (rawCall.isCanceled()) return;
                    if (progress.status != Progress.LOADING) {
                        rawCall.cancel();
                        return;
                    }
                    progress.from(innerProgress);
                    postLoading(progress);
                }
            });
            response = request.adapt().execute();
        } catch (Exception e) {
            postOnError(progress, e);
            return;
        }

        if (response.isSuccessful()) {
            postOnFinish(progress, response.body());
        } else {
            postOnError(progress, response.getException());
        }
    }

    private void postOnStart(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.NONE;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onStart(progress);
                }
            }
        });
    }

    private void postWaiting(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.WAITING;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postPause(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.PAUSE;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postLoading(final Progress progress) {
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postOnError(final Progress progress, final Throwable throwable) {
        progress.speed = 0;
        progress.status = Progress.ERROR;
        progress.exception = throwable;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onError(progress);
                }
            }
        });
    }

    private void postOnFinish(final Progress progress, final T t) {
        progress.speed = 0;
        progress.fraction = 1.0f;
        progress.status = Progress.FINISH;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onFinish(t, progress);
                }
            }
        });
    }

    private void postOnRemove(final Progress progress) {
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onRemove(progress);
                }
                listeners.clear();
            }
        });
    }

    private void updateDatabase(Progress progress) {
        ContentValues contentValues = Progress.buildUpdateContentValues(progress);
        UploadManager.getInstance().update(contentValues, progress.tag);
    }
}
