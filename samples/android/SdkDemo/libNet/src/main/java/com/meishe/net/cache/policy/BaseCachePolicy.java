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
package com.meishe.net.cache.policy;

import android.graphics.Bitmap;

import com.meishe.net.OkGo;
import com.meishe.net.cache.CacheEntity;
import com.meishe.net.cache.CacheMode;
import com.meishe.net.callback.Callback;
import com.meishe.net.db.CacheManager;
import com.meishe.net.exception.HttpException;
import com.meishe.net.model.Response;
import com.meishe.net.request.base.Request;
import com.meishe.net.utils.HeaderParser;
import com.meishe.net.utils.HttpUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Headers;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 * 缓存策略基类
 * Cache policy base class
 * @param <T> the type parameter
 */
public abstract class BaseCachePolicy<T> implements CachePolicy<T> {

    /**
     * The Request.
     * 请求
     */
    protected Request<T, ? extends Request> request;
    /**
     * The Canceled.
     * 取消
     */
    protected volatile boolean canceled;
    /**
     * The Current retry count.
     * 当前重试计数
     */
    protected volatile int currentRetryCount = 0;
    /**
     * The Executed.
     * 执行
     */
    protected boolean executed;
    /**
     * The Raw call.
     * 原始的调用
     */
    protected Call rawCall;
    /**
     * The M callback.
     * 回调
     */
    protected Callback<T> mCallback;
    /**
     * The Cache entity.
     * 缓存的实体
     */
    protected CacheEntity<T> cacheEntity;

    public BaseCachePolicy(Request<T, ? extends Request> request) {
        this.request = request;
    }

    @Override
    public boolean onAnalysisResponse(Call call, okhttp3.Response response) {
        return false;
    }

    @Override
    public CacheEntity<T> prepareCache() {
        //check the config of cache 检查cache配置
        if (request.getCacheKey() == null) {
            request.cacheKey(HttpUtils.createUrlFromParams(request.getBaseUrl(), request.getParams().urlParamsMap));
        }
        if (request.getCacheMode() == null) {
            request.cacheMode(CacheMode.NO_CACHE);
        }

        CacheMode cacheMode = request.getCacheMode();
        if (cacheMode != CacheMode.NO_CACHE) {
            //noinspection unchecked
            cacheEntity = (CacheEntity<T>) CacheManager.getInstance().get(request.getCacheKey());
            HeaderParser.addCacheHeaders(request, cacheEntity, cacheMode);
            if (cacheEntity != null && cacheEntity.checkExpire(cacheMode, request.getCacheTime(), System.currentTimeMillis())) {
                cacheEntity.setExpire(true);
            }
        }

        if (cacheEntity == null || cacheEntity.isExpire() || cacheEntity.getData() == null || cacheEntity.getResponseHeaders() == null) {
            cacheEntity = null;
        }
        return cacheEntity;
    }

    @Override
    public synchronized Call prepareRawCall() throws Throwable {
        if (executed) throw HttpException.COMMON("Already executed!");
        executed = true;
        rawCall = request.getRawCall();
        if (canceled) rawCall.cancel();
        return rawCall;
    }

    /**
     * Request network sync response.
     * 请求网络同步响应。
     * @return the response 响应
     */
    protected Response<T> requestNetworkSync() {
        try {
            okhttp3.Response response = rawCall.execute();
            int responseCode = response.code();

            //network error 网络错误
            if (responseCode == 404 || responseCode >= 500) {
                return Response.error(false, rawCall, response, HttpException.NET_ERROR());
            }

            T body = request.getConverter().convertResponse(response);
            //save cache when request is successful 请求成功时保存缓存
            saveCache(response.headers(), body);
            return Response.success(false, body, rawCall, response);
        } catch (Throwable throwable) {
            if (throwable instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                currentRetryCount++;
                rawCall = request.getRawCall();
                if (canceled) {
                    rawCall.cancel();
                } else {
                    requestNetworkSync();
                }
            }
            return Response.error(false, rawCall, null, throwable);
        }
    }

    /**
     * Request network async.
     * 网络异步请求
     */
    protected void requestNetworkAsync() {
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                    //retry when timeout 重试时超时
                    currentRetryCount++;
                    rawCall = request.getRawCall();
                    if (canceled) {
                        rawCall.cancel();
                    } else {
                        rawCall.enqueue(this);
                    }
                } else {
                    if (!call.isCanceled()) {
                        Response<T> error = Response.error(false, call, null, e);
                        onError(error);
                    }
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                int responseCode = response.code();

                //network error 网络错误
                if (responseCode == 404 || responseCode >= 500) {
                    Response<T> error = Response.error(false, call, response, HttpException.NET_ERROR());
                    onError(error);
                    return;
                }

                if (onAnalysisResponse(call, response)) return;

                try {
                    T body = request.getConverter().convertResponse(response);
                    //save cache when request is successful 请求成功时保存缓存
                    saveCache(response.headers(), body);
                    Response<T> success = Response.success(false, body, call, response);
                    onSuccess(success);
                } catch (Throwable throwable) {
                    Response<T> error = Response.error(false, call, response, throwable);
                    onError(error);
                }
            }
        });
    }

    /**
     * 请求成功后根据缓存模式，更新缓存数据
     * After the request is successful, update the cached data according to the cache pattern
     * @param headers 响应头
     * @param data    响应数据
     */
    private void saveCache(Headers headers, T data) {
        if (request.getCacheMode() == CacheMode.NO_CACHE) return;    //不需要缓存,直接返回 No caching required, just return
        if (data instanceof Bitmap) return;             //Bitmap没有实现Serializable,不能缓存 The Bitmap does not implement Serializable and cannot be cached

        CacheEntity<T> cache = HeaderParser.createCacheEntity(headers, data, request.getCacheMode(), request.getCacheKey());
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存 The server does not need caching, remove the local cache
            CacheManager.getInstance().remove(request.getCacheKey());
        } else {
            //缓存命中，更新缓存 Cache hit, update cache
            CacheManager.getInstance().replace(request.getCacheKey(), cache);
        }
    }

    /**
     * Run on ui thread.
     * 在ui线程上运行
     * @param run the run
     */
    protected void runOnUiThread(Runnable run) {
        OkGo.getInstance().getDelivery().post(run);
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
        if (rawCall != null) {
            rawCall.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        if (canceled) return true;
        synchronized (this) {
            return rawCall != null && rawCall.isCanceled();
        }
    }
}
