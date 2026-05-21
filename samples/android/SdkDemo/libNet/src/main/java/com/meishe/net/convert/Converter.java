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
package com.meishe.net.convert;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：网络数据的转换接口
 * Network data conversion interface
 * 修订历史：
 * ================================================
 */
public interface Converter<T> {

    /**
     * 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     * Once the response is received, the data is converted to the desired format for execution in a child thread, which can be a time-consuming operation
     *
     * @param response 需要转换的对象 The object to be transformed
     * @return 转换后的结果 The result of the conversion
     * @throws Exception 转换过程发生的异常 The exception that occurred during the conversion
     */
    T convertResponse(Response response) throws Throwable;
}
