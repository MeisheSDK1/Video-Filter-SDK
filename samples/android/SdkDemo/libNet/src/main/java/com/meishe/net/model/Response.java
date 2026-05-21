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
package com.meishe.net.model;

import okhttp3.Call;
import okhttp3.Headers;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：响应体的包装类
 * The wrapper class for the response body
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 */
public final class Response<T> {

    private T body;
    private Throwable throwable;
    private boolean isFromCache;
    private Call rawCall;
    private okhttp3.Response rawResponse;

    /**
     * Success response.
     * 成功响应
     * @param <T>         the type parameter 类型参数
     * @param isFromCache the is from cache 从缓存
     * @param body        the body 身体
     * @param rawCall     the raw call 调用
     * @param rawResponse the raw response 回应
     * @return the response
     */
    public static <T> Response<T> success(boolean isFromCache, T body, Call rawCall, okhttp3.Response rawResponse) {
        Response<T> response = new Response<>();
        response.setFromCache(isFromCache);
        response.setBody(body);
        response.setRawCall(rawCall);
        response.setRawResponse(rawResponse);
        return response;
    }

    /**
     * Error response.
     * 错误的回应
     * @param <T>         the type parameter 类型参数
     *  @param isFromCache the is from cache 从缓存
     *    @param rawCall     the raw call 调用
     *     @param rawResponse the raw response 回应
     *    @return the response
     */
    public static <T> Response<T> error(boolean isFromCache, Call rawCall, okhttp3.Response rawResponse, Throwable throwable) {
        Response<T> response = new Response<>();
        response.setFromCache(isFromCache);
        response.setRawCall(rawCall);
        response.setRawResponse(rawResponse);
        response.setException(throwable);
        return response;
    }

    public Response() {
    }

    /**
     * Code int.
     * 代码
     * @return the int
     */
    public int code() {
        if (rawResponse == null) return -1;
        return rawResponse.code();
    }

    /**
     * Message string.
     * 信息
     * @return the string
     */
    public String message() {
        if (rawResponse == null) return null;
        return rawResponse.message();
    }

    /**
     * Headers headers.
     * 头信息
     * @return the headers
     */
    public Headers headers() {
        if (rawResponse == null) return null;
        return rawResponse.headers();
    }

    /**
     * Is successful boolean.
     * 是否成功
     * @return the boolean
     */
    public boolean isSuccessful() {
        return throwable == null;
    }

    /**
     * Sets body.
     * 设置正文
     * @param body the body
     */
    public void setBody(T body) {
        this.body = body;
    }

    /**
     * Body t.
     *  正文
     * @return the t
     */
    public T body() {
        return body;
    }

    public Throwable getException() {
        return throwable;
    }

    public void setException(Throwable exception) {
        this.throwable = exception;
    }

    public Call getRawCall() {
        return rawCall;
    }

    public void setRawCall(Call rawCall) {
        this.rawCall = rawCall;
    }

    public okhttp3.Response getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(okhttp3.Response rawResponse) {
        this.rawResponse = rawResponse;
    }

    /**
     * Is from cache boolean.
     * 缓存
     * @return the boolean
     */
    public boolean isFromCache() {
        return isFromCache;
    }

    /**
     * Sets from cache.
     * 缓存设置
     * @param fromCache the from cache
     */
    public void setFromCache(boolean fromCache) {
        isFromCache = fromCache;
    }
}
