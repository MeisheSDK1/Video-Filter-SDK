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
package com.meishe.net.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：CookieStore 的公共接口
 * The public interface for the CookieStore
 * 修订历史：
 * ================================================
 */
public interface CookieStore {

    /**
     * 保存url对应所有cookie
     * Save the URL for all cookies
     * */
    void saveCookie(HttpUrl url, List<Cookie> cookie);

    /**
     * 保存url对应所有cookie
     *  Save the URL for all cookies
     * */
    void saveCookie(HttpUrl url, Cookie cookie);

    /**
     * 加载url所有的cookie
     * Load all the cookies in the URL
     * */
    List<Cookie> loadCookie(HttpUrl url);

    /**
     * 获取当前所有保存的cookie
     * Gets all currently saved cookies
     * */
    List<Cookie> getAllCookie();

    /**
     * 获取当前url对应的所有的cookie
     * Gets all cookies corresponding to the current URL
     * */
    List<Cookie> getCookie(HttpUrl url);

    /**
     * 根据url和cookie移除对应的cookie
     * Remove the corresponding cookie based on the URL and cookie
     * */
    boolean removeCookie(HttpUrl url, Cookie cookie);

    /**
     * 根据url移除所有的cookie
     * Remove all cookies based on the URL
     *
     * */
    boolean removeCookie(HttpUrl url);

    /**
     * 移除所有的cookie
     * Remove all cookies
     * */
    boolean removeAllCookie();
}
