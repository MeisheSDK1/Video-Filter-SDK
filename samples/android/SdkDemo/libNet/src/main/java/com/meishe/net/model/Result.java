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

import java.io.IOException;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 * 返回结果类
 * Return result class
 */
public final class Result<T> {
    /**
     * Error result.
     * 错误结果
     * @param <T>   the type parameter 类型参数
     * @param error the error 错误
     * @return the result 结果
     */
    public static <T> Result<T> error(Throwable error) {
        if (error == null) throw new NullPointerException("error == null");
        return new Result<>(null, error);
    }

    /**
     * Response result.
     * 响应结果
     * @param <T>      the type parameter 类型参数
     * @param response the response 响应
     * @return the result 结果
     */
    public static <T> Result<T> response(Response<T> response) {
        if (response == null) throw new NullPointerException("response == null");
        return new Result<>(response, null);
    }

    private final Response<T> response;
    private final Throwable error;

    private Result(Response<T> response, Throwable error) {
        this.response = response;
        this.error = error;
    }

    /**
     * The response received from executing an HTTP request. Only present when {@link #isError()} is
     * false, null otherwise.
     * 从执行HTTP请求中收到的响应。仅当{@link #isError()}存在时出现
     * false，否则为空
     * @return the response
     */
    public Response<T> response() {
        return response;
    }

    /**
     * The error experienced while attempting to execute an HTTP request. Only present when {@link
     * #isError()}* is true, null otherwise.
     * If the error is an {@link IOException} then there was a problem with the transport to the
     * remote server. Any other exception type indicates an unexpected failure and should be
     * considered fatal (configuration error, programming error, etc.).
     * 试图执行HTTP请求时发生的错误。仅在{@link时出现
     * #isError()}*为true，否则为null。
     * 如果错误是{@link IOException}，那么传输到
     * 远程服务器。任何其他异常类型都表示意外失败，并且应该如此
     * 被认为是致命的(配置错误、编程错误等)。
     * @return the throwable 可抛出
     */
    public Throwable error() {
        return error;
    }

    /**
     * {@code true} if the request resulted in an error. See {@link #error()} for the cause.  @return the boolean
     * 如果请求导致错误。请查看{@link #error()}查看原因
     */
    public boolean isError() {
        return error != null;
    }

    @Override
    public String toString() {
        if (error != null) {
            return "Result{isError=true, error=\"" + error + "\"}";
        }
        return "Result{isError=false, response=" + response + '}';
    }
}
