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

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：返回值的适配器
 * The adapter that returns the value
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public interface CallAdapter<T, R> {

    /**
     * call执行的代理方法  @param call the call
     * The proxy method that Call executes
     * @param param the param
     * @return the r
     */
    R adapt(Call<T> call, AdapterParam param);
}
