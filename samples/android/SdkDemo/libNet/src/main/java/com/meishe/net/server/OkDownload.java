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
package com.meishe.net.server;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.meishe.net.db.DownloadManager;
import com.meishe.net.model.Progress;
import com.meishe.net.request.base.Request;
import com.meishe.net.server.download.DownloadTask;
import com.meishe.net.server.download.DownloadThreadPool;
import com.meishe.net.server.task.XExecutor;
import com.meishe.net.utils.IOUtils;
import com.meishe.net.utils.OkLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：全局的下载管理类
 * Global download management class
 * 修订历史：
 * ================================================
 */
public class OkDownload {

    private static String folder;                                      //下载的默认文件夹  The default folder for download
    private DownloadThreadPool threadPool;                      //下载的线程池 The thread pool to download
    private ConcurrentHashMap<String, DownloadTask> taskMap;    //所有任务 All Tasks

    public static OkDownload getInstance() {
        return OkDownloadHolder.instance;
    }


    private static class OkDownloadHolder {
        private static final OkDownload instance = new OkDownload();
    }

    public static void initAndroidOs(Context context){
        folder = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            folder = context.getExternalFilesDir("") + File.separator + "download" + File.separator;
        }
    }

    private OkDownload() {


        IOUtils.createFolder(folder);
        threadPool = new DownloadThreadPool();
        taskMap = new ConcurrentHashMap<>();

        //校验数据的有效性，防止下载过程中退出，第二次进入的时候，由于状态没有更新导致的状态错误 Verify the validity of the data to prevent the exit during the download process and the status error caused by not updating the status on the second entry
        List<Progress> taskList = DownloadManager.getInstance().getDownloading();
        for (Progress info : taskList) {
            if (info.status == Progress.WAITING || info.status == Progress.LOADING || info.status == Progress.PAUSE) {
                info.status = Progress.NONE;
            }
        }
        DownloadManager.getInstance().replace(taskList);
    }

    /**
     * Request download task.
     * 请求下载任务
     * @param tag     the tag 标签
     * @param request the request 请求
     * @return the download task 下载任务
     */
    public static DownloadTask request(String tag, Request<File, ? extends Request> request) {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        DownloadTask task = taskMap.get(tag);
        if (task == null) {
            task = new DownloadTask(tag, request);
            taskMap.put(tag, task);
        }
        return task;
    }

    /**
     * 从数据库中恢复任务  @param progress the progress
     * Restore the task from the database
     * @return the download task 下载任务
     */
    public static DownloadTask restore(Progress progress) {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        DownloadTask task = taskMap.get(progress.tag);
        if (task == null) {
            task = new DownloadTask(progress);
            taskMap.put(progress.tag, task);
        }
        return task;
    }

    /**
     * 从数据库中恢复任务  @param progressList the progress list
     * Restore the task from the database
     * @return the list
     */
    public static List<DownloadTask> restore(List<Progress> progressList) {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        List<DownloadTask> tasks = new ArrayList<>();
        for (Progress progress : progressList) {
            DownloadTask task = taskMap.get(progress.tag);
            if (task == null) {
                task = new DownloadTask(progress);
                taskMap.put(progress.tag, task);
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * 开始所有任务
     * Start all tasks
     */
    public void startAll() {
        for (Map.Entry<String, DownloadTask> entry : taskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            task.start();
        }
    }

    /**
     * 暂停全部任务
     * Suspend all tasks
     */
    public void pauseAll() {
        //先停止未开始的任务 Stop unstarted tasks first
        for (Map.Entry<String, DownloadTask> entry : taskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status != Progress.LOADING) {
                task.pause();
            }
        }
        //再停止进行中的任务 Then stop the task in progress
        for (Map.Entry<String, DownloadTask> entry : taskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status == Progress.LOADING) {
                task.pause();
            }
        }
    }

    /**
     * 删除所有任务
     * Delete all tasks
     */
    public void removeAll() {
        removeAll(false);
    }

    /**
     * 删除所有任务
     * Delete all tasks
     * @param isDeleteFile 删除任务是否删除文件
     */
    public void removeAll(boolean isDeleteFile) {
        Map<String, DownloadTask> map = new HashMap<>(taskMap);
        //先删除未开始的任务 Start by deleting unstarted tasks
        for (Map.Entry<String, DownloadTask> entry : map.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status != Progress.LOADING) {
                task.remove(isDeleteFile);
            }
        }
        //再删除进行中的任务 Delete the task in progress
        for (Map.Entry<String, DownloadTask> entry : map.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            if (task.progress.status == Progress.LOADING) {
                task.remove(isDeleteFile);
            }
        }
    }

    /**
     * 设置下载目录  @return the folder
     * Set download directory
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Sets folder.
     * 设置文件夹
     * @param folder the folder 文件夹
     * @return the folder 文件夹
     */
    public OkDownload setFolder(String folder) {
        this.folder = folder;
        return this;
    }

    /**
     * Gets thread pool.
     * 获取线程池
     * @return the thread pool 线程池
     */
    public DownloadThreadPool getThreadPool() {
        return threadPool;
    }

    /**
     * Gets task map.
     * 得到任务地图
     * @return the task map  任务地图
     */
    public Map<String, DownloadTask> getTaskMap() {
        return taskMap;
    }

    /**
     * Gets task.
     * 获取任务
     * @param tag the tag 标签
     * @return the task 任务
     */
    public DownloadTask getTask(String tag) {
        return taskMap.get(tag);
    }

    /**
     * Has task boolean.
     * 有任务
     * @param tag the tag 标签
     * @return the boolean
     */
    public boolean hasTask(String tag) {
        return taskMap.containsKey(tag);
    }

    /**
     * Remove task download task.
     * 删除任务下载任务
     * @param tag the tag  标签
     * @return the download task 下载任务
     */
    public DownloadTask removeTask(String tag) {
        return taskMap.remove(tag);
    }

    /**
     * Add on all task end listener.
     * 添加所有任务结束监听器
     * @param listener the listener  监听
     */
    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
        threadPool.getExecutor().addOnAllTaskEndListener(listener);
    }

    /**
     * Remove on all task end listener.
     * 删除所有任务结束侦听器
     * @param listener the listener  监听
     */
    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
        threadPool.getExecutor().removeOnAllTaskEndListener(listener);
    }
}
