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
package com.meishe.net.exception;

import com.meishe.net.model.Response;
import com.meishe.net.utils.HttpUtils;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/28
 * 描    述：
 * 修订历史：
 * ================================================
 * 异常类
 * exception class
 */
public class HttpException extends RuntimeException {
    private static final long serialVersionUID = 8773734741709178425L;

    private int code;                               //HTTP status code
    private String message;                         //HTTP status message
    private transient Response<?> response;         //The full HTTP response. This may be null if the exception was serialized

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Response<?> response) {
        super(getMessage(response));
        this.code = response.code();
        this.message = response.message();
        this.response = response;
    }

    private static String getMessage(Response<?> response) {
        HttpUtils.checkNotNull(response, "response == null");
        return "HTTP " + response.code() + " " + response.message();
    }

    /**
     * Code int.
     * 代码
     * @return the int
     */
    public int code() {
        return code;
    }

    /**
     * Message string.
     * 信息内容
     * @return the string
     */
    public String message() {
        return message;
    }

    /**
     * Response response.
     * 反应
     * @return the response
     */
    public Response<?> response() {
        return response;
    }

    /**
     * Net error http exception.
     * http异常
     * @return the http exception
     */
    public static HttpException NET_ERROR() {
        return new HttpException("network error! http response code is 404 or 5xx!");
    }

    /**
     * Common http exception.
     * 常见http例外
     * @param message the message
     * @return the http exception
     */
    public static HttpException COMMON(String message) {
        return new HttpException(message);
    }
}
