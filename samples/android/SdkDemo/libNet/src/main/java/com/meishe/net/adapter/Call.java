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
package com.meishe.net.adapter;

import com.meishe.net.callback.Callback;
import com.meishe.net.model.Response;
import com.meishe.net.request.base.Request;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：请求的包装类
 * The requested wrapper class
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 */
public interface Call<T> {
    /**
     * 同步执行  @return the response
     * Synchronous execution
     * @throws Exception the exception
     */
    Response<T> execute() throws Exception;

    /**
     * 异步回调执行  @param callback the callback
     * Asynchronous callback execution
     */
    void execute(Callback<T> callback);

    /**
     * 是否已经执行  @return the boolean
     * Has it been implemented
     */
    boolean isExecuted();

    /**
     * 取消
     * cancel
     */
    void cancel();

    /**
     * 是否取消  @return the boolean
     * MB_YESNOCANCEL
     */
    boolean isCanceled();

    Call<T> clone();

    /**
     * Gets request.
     * 请求
     * @return the request 请求
     */
    Request getRequest();
}
