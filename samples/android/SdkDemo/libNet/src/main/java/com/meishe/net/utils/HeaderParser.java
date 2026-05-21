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
package com.meishe.net.utils;

import android.text.TextUtils;

import com.meishe.net.cache.CacheEntity;
import com.meishe.net.cache.CacheMode;
import com.meishe.net.model.HttpHeaders;
import com.meishe.net.request.base.Request;

import java.util.Locale;
import java.util.StringTokenizer;

import okhttp3.Headers;

/**
 * ================================================
 * 作    者：jeasonlzy.
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 描    述：我的Github地址  https://github.com/jeasonlzy
 * 修订历史：
 * ================================================
 */
public class HeaderParser {
    /**
     * 根据请求结果生成对应的缓存实体类，以下为缓存相关的响应头
     * The corresponding cache entity class is generated based on the request result. Below are the cache-related response headers
     * Cache-Control: public                             响应被缓存，并且在多用户间共享  Responses are cached and shared among multiple users
     * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享  The response can only be used as a private cache and cannot be shared between users
     * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证  Remind the browser to extract the document from the server for validation
     * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）  Absolutely no caching (for confidential, sensitive files)
     * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高  After 60 seconds the cache Expires (relative time), the priority is higher than Expires
     * Date: Mon, 19 Nov 2012 08:39:00 GMT               当前response发送的时间  Time the current response is sent
     * Expires: Mon, 19 Nov 2012 08:40:01 GMT            缓存过期的时间（绝对时间） Cache expiration time (absolute time)
     * Last-Modified: Mon, 19 Nov 2012 08:38:01 GMT      服务器端文件的最后修改时间  The last modification time of the server file
     * ETag: "20b1add7ec1cd1:0"                          服务器端文件的ETag值  The ETag value of the server-side file
     * 如果同时存在cache-control和Expires，浏览器总是优先使用cache-control
     * Browsers always use cache-control first if both cache-control and Expires exist
     *
     * @param <T>             the type parameter
     * @param responseHeaders 返回数据中的响应头
     * @param data            解析出来的数据
     * @param cacheMode       缓存的模式
     * @param cacheKey        缓存的key
     * @return 缓存的实体类 cache entity
     */
    public static <T> CacheEntity<T> createCacheEntity(Headers responseHeaders, T data, CacheMode cacheMode, String cacheKey) {
        // 缓存相对于本地的到期时间  The expiration time of the cache relative to the local
        long localExpire = 0;

        if (cacheMode == CacheMode.DEFAULT) {
            long date = HttpHeaders.getDate(responseHeaders.get(HttpHeaders.HEAD_KEY_DATE));
            long expires = HttpHeaders.getExpiration(responseHeaders.get(HttpHeaders.HEAD_KEY_EXPIRES));
            String cacheControl = HttpHeaders.getCacheControl(responseHeaders.get(HttpHeaders.HEAD_KEY_CACHE_CONTROL), responseHeaders.get(HttpHeaders.HEAD_KEY_PRAGMA));

            //没有缓存头控制，不需要缓存  No cache header control, no caching required
            if (TextUtils.isEmpty(cacheControl) && expires <= 0) return null;

            long maxAge = 0;
            if (!TextUtils.isEmpty(cacheControl)) {
                StringTokenizer tokens = new StringTokenizer(cacheControl, ",");
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken().trim().toLowerCase(Locale.getDefault());
                    if (token.equals("no-cache") || token.equals("no-store")) {
                        //服务器指定不缓存  The server specifies no caching
                        return null;
                    } else if (token.startsWith("max-age=")) {
                        try {
                            //获取最大缓存时间  Gets the maximum cache time
                            maxAge = Long.parseLong(token.substring(8));
                            //服务器缓存设置立马过期，不缓存  Server cache Settings expire immediately, no caching
                            if (maxAge <= 0) return null;
                        } catch (Exception e) {
                            OkLogger.printStackTrace(e);
                        }
                    }
                }
            }

            //获取基准缓存时间，优先使用response中的date头，如果没有就使用本地时间  Get the baseline cache time, using the date header in response first, or the local time if not available
            long now = System.currentTimeMillis();
            if (date > 0) now = date;

            if (maxAge > 0) {
                // Http1.1 优先验证 Cache-Control 头
                localExpire = now + maxAge * 1000;
            } else if (expires >= 0) {
                // Http1.0 验证 Expires 头
                localExpire = expires;
            }
        } else {
            localExpire = System.currentTimeMillis();
        }

        //将response中所有的头存入 HttpHeaders，原因是写入数据库的对象需要实现序列化，而ok默认的Header没有序列化
        //Store all headers in response to HttpHeaders because the object written to the database needs to be serialized and ok's default Header is not serialized
        HttpHeaders headers = new HttpHeaders();
        for (String headerName : responseHeaders.names()) {
            headers.put(headerName, responseHeaders.get(headerName));
        }

        //构建缓存实体对象  Build the cache entity object
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setKey(cacheKey);
        cacheEntity.setData(data);
        cacheEntity.setLocalExpire(localExpire);
        cacheEntity.setResponseHeaders(headers);
        return cacheEntity;
    }

    /**
     * 对每个请求添加默认的请求头，如果有缓存，并返回缓存实体对象
     * Add the default request header for each request, if there is a cache, and return the cache entity object
     * Cache-Control: max-age=0                            以秒为单位  In seconds
     * If-Modified-Since: Mon, 19 Nov 2012 08:38:01 GMT    缓存文件的最后修改时间。  The last modification time of the cached file
     * If-None-Match: "0693f67a67cc1:0"                    缓存文件的ETag值  The ETag value of the cache file
     * Cache-Control: no-cache                             不使用缓存  Not using caching
     * Pragma: no-cache                                    不使用缓存  Not using caching
     * Accept-Language: zh-CN,zh;q=0.8                     支持的语言  Supported languages
     * User-Agent:                                         用户代理，它的信息包括硬件平台、系统软件、应用软件和用户个人偏好  A user agent whose information includes hardware platforms, system software, application software, and user preferences
     * Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36
     *
     * @param <T>         the type parameter
     * @param request     请求类  Request class
     * @param cacheEntity 缓存实体类  Cache entity class
     * @param cacheMode   缓存模式  Cache mode
     */
    public static <T> void addCacheHeaders(Request request, CacheEntity<T> cacheEntity, CacheMode cacheMode) {
        //1. 按照标准的 http 协议，添加304相关请求头  Add 304 related request headers as per the standard http protocol
        if (cacheEntity != null && cacheMode == CacheMode.DEFAULT) {
            HttpHeaders responseHeaders = cacheEntity.getResponseHeaders();
            if (responseHeaders != null) {
                String eTag = responseHeaders.get(HttpHeaders.HEAD_KEY_E_TAG);
                if (eTag != null) request.headers(HttpHeaders.HEAD_KEY_IF_NONE_MATCH, eTag);
                long lastModified = HttpHeaders.getLastModified(responseHeaders.get(HttpHeaders.HEAD_KEY_LAST_MODIFIED));
                if (lastModified > 0)
                    request.headers(HttpHeaders.HEAD_KEY_IF_MODIFIED_SINCE, HttpHeaders.formatMillisToGMT(lastModified));
            }
        }
    }
}
