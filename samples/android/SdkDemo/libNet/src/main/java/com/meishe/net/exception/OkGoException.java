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

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/28
 * 描    述：
 * 修订历史：
 * ================================================
 * OkGo异常类
 * OkGo exception class
 */
public class OkGoException extends Exception {
    private static final long serialVersionUID = -8641198158155821498L;

    public OkGoException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Unknown ok go exception.
     * 未知的ok go异常
     * @return the ok go exception
     */
    public static OkGoException UNKNOWN() {
        return new OkGoException("unknown exception!");
    }

    /**
     * Breakpoint not exist ok go exception.
     * 断点不存在ok go异常
     * @return the ok go exception
     */
    public static OkGoException BREAKPOINT_NOT_EXIST() {
        return new OkGoException("breakpoint file does not exist!");
    }

    /**
     * Breakpoint expired ok go exception.
     * 断点过期ok go异常
     * @return the ok go exception
     */
    public static OkGoException BREAKPOINT_EXPIRED() {
        return new OkGoException("breakpoint file has expired!");
    }
}
