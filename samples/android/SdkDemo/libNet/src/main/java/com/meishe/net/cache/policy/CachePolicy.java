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

import com.meishe.net.cache.CacheEntity;
import com.meishe.net.callback.Callback;
import com.meishe.net.model.Response;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 * 高速缓存策略接口
 *Cache policy interface
 */
public interface CachePolicy<T> {

    /**
     * 获取数据成功的回调
     * Successful callback to get data
     * @param success 获取的数据，可是是缓存或者网络
     */
    void onSuccess(Response<T> success);

    /**
     * 获取数据失败的回调
     * Failed callback to get data
     * @param error 失败的信息，可是是缓存或者网络
     */
    void onError(Response<T> error);

    /**
     * 控制是否执行后续的回调动作
     * Controls whether subsequent backtracking is performed
     * @param call     请求的对象
     * @param response 响应的对象
     * @return true ，不执行回调， false 执行回调
     */
    boolean onAnalysisResponse(okhttp3.Call call, okhttp3.Response response);

    /**
     * 构建缓存
     * Build a cache
     * @return 获取的缓存 cache entity
     */
    CacheEntity<T> prepareCache();

    /**
     * 构建请求对象
     * Build request object
     * @return 准备请求的对象 okhttp 3 . call
     * @throws Throwable the throwable
     */
    okhttp3.Call prepareRawCall() throws Throwable;

    /**
     * 同步请求获取数据
     * Synchronous request to fetch data
     * @param cacheEntity 本地的缓存
     * @return 从缓存或本地获取的数据 response
     */
    Response<T> requestSync(CacheEntity<T> cacheEntity);

    /**
     * 异步请求获取数据
     * The asynchronous request gets the data
     * @param cacheEntity 本地的缓存
     * @param callback    异步回调
     */
    void requestAsync(CacheEntity<T> cacheEntity, Callback<T> callback);

    /**
     * 当前请求是否已经执行
     * Whether the current request has been executed
     * @return true ，已经执行， false，没有执行
     */
    boolean isExecuted();

    /**
     * 取消请求
     * Cancel Request
     */
    void cancel();

    /**
     * 是否已经取消
     * Has it been cancelled?
     * @return true ，已经取消，false，没有取消
     */
    boolean isCanceled();
}
