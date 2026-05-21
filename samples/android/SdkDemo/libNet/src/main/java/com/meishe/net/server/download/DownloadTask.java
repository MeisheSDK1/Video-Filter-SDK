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
package com.meishe.net.server.download;

import android.content.ContentValues;
import android.text.TextUtils;

import com.meishe.net.db.DownloadManager;
import com.meishe.net.exception.HttpException;
import com.meishe.net.exception.OkGoException;
import com.meishe.net.exception.StorageException;
import com.meishe.net.model.HttpHeaders;
import com.meishe.net.model.Progress;
import com.meishe.net.request.base.Request;
import com.meishe.net.server.OkDownload;
import com.meishe.net.server.task.PriorityRunnable;
import com.meishe.net.utils.HttpUtils;
import com.meishe.net.utils.IOUtils;
import com.meishe.net.utils.OkLogger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：文件的下载任务类
 * File download task class
 * 修订历史：
 * ================================================
 */
public class DownloadTask implements Runnable {

    private static final int BUFFER_SIZE = 1024 * 8;

    /**
     * The Progress.
     * 进度条
     */
    public Progress progress;
    /**
     * The Listeners.、
     * 监听
     */
    public Map<Object, DownloadListener> listeners;
    private ThreadPoolExecutor executor;
    private PriorityRunnable priorityRunnable;

    public DownloadTask(String tag, Request<File, ? extends Request> request) {
        HttpUtils.checkNotNull(tag, "tag == null");
        progress = new Progress();
        progress.tag = tag;
        progress.folder = OkDownload.getInstance().getFolder();
        progress.url = request.getBaseUrl();
        progress.status = Progress.NONE;
        progress.totalSize = -1;
        progress.request = request;

        executor = OkDownload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public DownloadTask(Progress progress) {
        HttpUtils.checkNotNull(progress, "progress == null");
        this.progress = progress;
        executor = OkDownload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    /**
     * Folder download task.
     * 文件夹下载任务
     *
     * @param folder the folder  文件夹
     * @return the download task 下载任务
     */
    public DownloadTask folder(String folder) {
        if (folder != null && !TextUtils.isEmpty(folder.trim())) {
            progress.folder = folder;
        } else {
            OkLogger.w("folder is null, ignored!");
        }
        return this;
    }

    /**
     * File name download task.
     * 文件名下载任务
     *
     * @param fileName the file name 文件名
     * @return the download task 下载任务
     */
    public DownloadTask fileName(String fileName) {
        if (fileName != null && !TextUtils.isEmpty(fileName.trim())) {
            progress.fileName = fileName;
        } else {
            OkLogger.w("fileName is null, ignored!");
        }
        return this;
    }

    /**
     * Priority download task.
     * 优先下载任务
     *
     * @param priority the priority  优先
     * @return the download task 下载任务
     */
    public DownloadTask priority(int priority) {
        progress.priority = priority;
        return this;
    }

    /**
     * Extra 1 download task.
     * 额外的1个下载任务
     *
     * @param extra1 the  extra 1 额外的1
     * @return the download task 下载任务
     */
    public DownloadTask extra1(Serializable extra1) {
        progress.extra1 = extra1;
        return this;
    }

    /**
     * Extra 2 download task.
     * 额外2下载任务
     *
     * @param extra2 the extra 2 额外2
     * @return the download task 下载任务
     */
    public DownloadTask extra2(Serializable extra2) {
        progress.extra2 = extra2;
        return this;
    }

    /**
     * Extra 3 download task.
     * 额外3下载任务
     *
     * @param extra3 the extra 3  额外的3
     * @return the download task 下载任务
     */
    public DownloadTask extra3(Serializable extra3) {
        progress.extra3 = extra3;
        return this;
    }

    /**
     * Save download task.
     * 保存下载的任务
     *
     * @return the download task 下载任务
     */
    public DownloadTask save() {
        if (!TextUtils.isEmpty(progress.folder) && !TextUtils.isEmpty(progress.fileName)) {
            progress.filePath = new File(progress.folder, progress.fileName).getAbsolutePath();
        }
        DownloadManager.getInstance().replace(progress);
        return this;
    }

    /**
     * Register download task.
     * 注册下载任务
     *
     * @param listener the listener 监听
     * @return the download task下载任务
     */
    public DownloadTask register(DownloadListener listener) {
        if (listener != null) {
            listeners.put(listener.tag, listener);
        }
        return this;
    }

    /**
     * Un register.
     * 注册
     *
     * @param listener the listener
     */
    public void unRegister(DownloadListener listener) {
        HttpUtils.checkNotNull(listener, "listener == null");
        listeners.remove(listener.tag);
    }

    /**
     * Un register.
     * 注册
     *
     * @param tag the tag
     */
    public void unRegister(String tag) {
        HttpUtils.checkNotNull(tag, "tag == null");
        listeners.remove(tag);
    }

    /**
     * Start.
     * 开始
     */
    public void start() {
        Progress progress = DownloadManager.getInstance().get(this.progress.tag);
        if (progress == null || OkDownload.getInstance().getTask(this.progress.tag) == null) {
            /* 如果获取失败，但是已经调了save方法，说明save方法更新数据库失败，
             * 失败的原因是SQLite在低版本Android上的bug
             * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0330/4108.html
             * https://www.jianshu.com/p/6ad0491404da
             * 再次调用save方法，如果失败就抛弃，
             * 抛弃可能会影响断点续传功能，目前并没有用到该功能
             */
            OkLogger.e("progress=" + progress);
            save();
        }
       /* if (OkDownload.getInstance().getTask(this.progress.tag) == null || DownloadManager.getInstance().get(this.progress.tag) == null) {
            throw new IllegalStateException("you must call DownloadTask#save() before DownloadTask#start()！");
        }*/
        if (this.progress.status == Progress.NONE || this.progress.status == Progress.PAUSE || this.progress.status == Progress.ERROR) {
            postOnStart(this.progress);
            postWaiting(this.progress);
            priorityRunnable = new PriorityRunnable(this.progress.priority, this);
            executor.execute(priorityRunnable);
        } else if (this.progress.status == Progress.FINISH) {
            if (this.progress.filePath == null) {
                postOnError(this.progress, new StorageException("the file of the task with tag:" + this.progress.tag + " may be invalid or damaged, please call the method restart() to download again！"));
            } else {
                File file = new File(this.progress.filePath);
                if (file.exists() && file.length() == this.progress.totalSize) {
                    postOnFinish(this.progress, new File(this.progress.filePath));
                } else {
                    postOnError(this.progress, new StorageException("the file " + this.progress.filePath + " may be invalid or damaged, please call the method restart() to download again！"));
                }
            }
        } else {
            OkLogger.w("the task with tag " + this.progress.tag + " is already in the download queue, current task status is " + this.progress.status);
        }
    }

    /**
     * Restart.
     * 重新开始
     */
    public void restart() {
        pause();
        IOUtils.delFileOrFolder(progress.filePath);
        progress.status = Progress.NONE;
        progress.currentSize = 0;
        progress.fraction = 0;
        progress.speed = 0;
        DownloadManager.getInstance().replace(progress);
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
     * 删除一个任务,不会删除下载文件
     * Deleting a task  ,but not delete the downloaded file
     */
    public void remove() {
        remove(false);
    }

    /**
     * 删除一个任务,会删除下载文件  @param isDeleteFile the is delete file
     * Deleting a task deletes the downloaded file
     *
     * @return the download task 下载任务
     */
    public DownloadTask remove(boolean isDeleteFile) {
        pause();
        if (isDeleteFile) IOUtils.delFileOrFolder(progress.filePath);
        DownloadManager.getInstance().delete(progress.tag);
        DownloadTask task = OkDownload.getInstance().removeTask(progress.tag);
        postOnRemove(progress);
        return task;
    }

    @Override
    public void run() {
        //check breakpoint
        long startPosition = progress.currentSize;
        if (startPosition < 0) {
            postOnError(progress, OkGoException.BREAKPOINT_EXPIRED());
            return;
        }
        if (startPosition > 0) {
            if (!TextUtils.isEmpty(progress.filePath)) {
                File file = new File(progress.filePath);
                if (!file.exists()) {
                    postOnError(progress, OkGoException.BREAKPOINT_NOT_EXIST());
                    return;
                }
            }
        }

        //request network from startPosition 从起始位置请求网络
        Response response;
        try {
            Request<?, ? extends Request> request = progress.request;
            request.headers(HttpHeaders.HEAD_KEY_RANGE, "bytes=" + startPosition + "-");
            response = request.execute();
        } catch (IOException e) {
            postOnError(progress, e);
            return;
        }

        //check network data 检查网络数据
        int code = response.code();
        if (code == 404 || code >= 500) {
            postOnError(progress, HttpException.NET_ERROR());
            return;
        }
        ResponseBody body = response.body();
        if (body == null) {
            postOnError(progress, new HttpException("response body is null"));
            return;
        }
        if (progress.totalSize == -1) {
            progress.totalSize = body.contentLength();
        }

        //create filename 创建文件名
        String fileName = progress.fileName;
        if (TextUtils.isEmpty(fileName)) {
            fileName = HttpUtils.getNetFileName(response, progress.url);
            progress.fileName = fileName;
        }
        if (!IOUtils.createFolder(progress.folder)) {
            postOnError(progress, StorageException.NOT_AVAILABLE());
            return;
        }

        //create and check file 创建和检查文件
        File file;
        if (TextUtils.isEmpty(progress.filePath)) {
            file = new File(progress.folder, fileName);
            progress.filePath = file.getAbsolutePath();
        } else {
            file = new File(progress.filePath);
        }
        if (startPosition > 0 && !file.exists()) {
            postOnError(progress, OkGoException.BREAKPOINT_EXPIRED());
            return;
        }
        if (startPosition > progress.totalSize) {
            postOnError(progress, OkGoException.BREAKPOINT_EXPIRED());
            return;
        }
        if (startPosition == 0 && file.exists()) {
            IOUtils.delFileOrFolder(file);
        }
        if (startPosition == progress.totalSize && startPosition > 0) {
            if (file.exists() && startPosition == file.length()) {
                postOnFinish(progress, file);
                return;
            } else {
                postOnError(progress, OkGoException.BREAKPOINT_EXPIRED());
                return;
            }
        }

        //start downloading 开始下载
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(startPosition);
            progress.currentSize = startPosition;
        } catch (Exception e) {
            postOnError(progress, e);
            return;
        }
        try {
            DownloadManager.getInstance().replace(progress);
            download(body.byteStream(), randomAccessFile, progress);
        } catch (IOException e) {
            postOnError(progress, e);
            return;
        }

        //check finish status 检查完成状态
        if (progress.status == Progress.PAUSE) {
            postPause(progress);
        } else if (progress.status == Progress.LOADING) {
            if (file.length() == progress.totalSize) {
                postOnFinish(progress, file);
            } else {
                postOnError(progress, OkGoException.BREAKPOINT_EXPIRED());
            }
        } else {
            postOnError(progress, OkGoException.UNKNOWN());
        }
    }

    /**
     * 执行文件下载
     * Executive file download
     */
    private void download(InputStream input, RandomAccessFile out, Progress progress) throws IOException {
        if (input == null || out == null) return;

        progress.status = Progress.LOADING;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        int len;
        try {
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1 && progress.status == Progress.LOADING) {
                out.write(buffer, 0, len);

                Progress.changeProgress(progress, len, progress.totalSize, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        postLoading(progress);
                    }
                });
            }
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(input);
        }
    }

    private void postOnStart(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.NONE;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners.values()) {
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
                for (DownloadListener listener : listeners.values()) {
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
                for (DownloadListener listener : listeners.values()) {
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
                for (DownloadListener listener : listeners.values()) {
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
                for (DownloadListener listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onError(progress);
                }
            }
        });
    }

    private void postOnFinish(final Progress progress, final File file) {
        progress.speed = 0;
        progress.fraction = 1.0f;
        progress.status = Progress.FINISH;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onFinish(file, progress);
                }
            }
        });
    }

    private void postOnRemove(final Progress progress) {
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DownloadListener listener : listeners.values()) {
                    listener.onRemove(progress);
                }
                listeners.clear();
            }
        });
    }

    private void updateDatabase(Progress progress) {
        ContentValues contentValues = Progress.buildUpdateContentValues(progress);
        DownloadManager.getInstance().update(contentValues, progress.tag);
    }
}
