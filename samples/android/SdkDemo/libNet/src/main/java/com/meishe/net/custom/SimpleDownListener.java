package com.meishe.net.custom;

import com.meishe.net.model.Progress;
import com.meishe.net.server.download.DownloadListener;

import java.io.File;

/**
 * author : lhz
 * date   : 2020/11/24
 * desc   :简便的下载监听
 */
public class SimpleDownListener extends DownloadListener {
    /**
     * Instantiates a new Simple down listener.
     *
     * @param tag the tag
     */
    public SimpleDownListener(Object tag) {
        super(tag);
    }

    @Override
    public void onStart(Progress progress) {

    }

    @Override
    public void onProgress(Progress progress) {

    }

    @Override
    public void onError(Progress progress) {

    }

    @Override
    public void onFinish(File file, Progress progress) {

    }

    @Override
    public void onRemove(Progress progress) {

    }
}
