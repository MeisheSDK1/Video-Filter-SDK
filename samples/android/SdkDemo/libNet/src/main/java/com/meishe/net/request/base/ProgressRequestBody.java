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
package com.meishe.net.request.base;

import com.meishe.net.callback.Callback;
import com.meishe.net.model.Progress;
import com.meishe.net.utils.HttpUtils;
import com.meishe.net.utils.OkLogger;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：包装的请求体，处理进度，可以处理任何的 RequestBody，
 * Wrapped RequestBody, processing schedule, can process any RequestBody,
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 */
public class ProgressRequestBody<T> extends RequestBody {

    private RequestBody requestBody;         //实际的待包装请求体 The actual request body to be packaged
    private Callback<T> callback;
    private UploadInterceptor interceptor;

    ProgressRequestBody(RequestBody requestBody, Callback<T> callback) {
        this.requestBody = requestBody;
        this.callback = callback;
    }

    /** 重写调用实际的响应体的contentType
     * Overrides the contentType of the call's actual response body
     * */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /** 重写调用实际的响应体的contentLength
     * Overrides the contentLength of the call's actual response body
     * */
    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            OkLogger.printStackTrace(e);
            return -1;
        }
    }

    /** 重写进行写入
     * Rewrite to write
     * */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    /** 包装
     * wrap
     * */
    private final class CountingSink extends ForwardingSink {

        private Progress progress;

        CountingSink(Sink delegate) {
            super(delegate);
            progress = new Progress();
            progress.totalSize = contentLength();
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            Progress.changeProgress(progress, byteCount, new Progress.Action() {
                @Override
                public void call(Progress progress) {
                    if (interceptor != null) {
                        interceptor.uploadProgress(progress);
                    } else {
                        onProgress(progress);
                    }
                }
            });
        }
    }

    private void onProgress(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.uploadProgress(progress);
                }
            }
        });
    }

    /**
     * Sets interceptor.
     * 设置拦截器
     * @param interceptor the interceptor
     */
    public void setInterceptor(UploadInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * The interface Upload interceptor.
     * 上传拦截器的借口
     */
    public interface UploadInterceptor {
        /**
         * Upload progress.
         * 上传进度
         * @param progress the progress
         */
        void uploadProgress(Progress progress);
    }
}
