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
package com.meishe.net.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.meishe.net.model.HttpHeaders;
import com.meishe.net.utils.IOUtils;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 * 缓存的实体类
 * Cached entity class
 * @param <T> the type parameter
 */
public class CacheEntity<T> implements Serializable {
    private static final long serialVersionUID = -4337711009801627866L;
     /*
     * 缓存永不过期
     * Cache never expires
     * */
    public static final long CACHE_NEVER_EXPIRE = -1;

    /**
     * 表中的字段
     * table Fields
     */
    public static final String KEY = "key";
    public static final String LOCAL_EXPIRE = "localExpire";
    public static final String HEAD = "head";
    public static final String DATA = "data";

    private String key;                    // 缓存key The cache key
    private long localExpire;              // 缓存过期时间 Cache expiration time
    private HttpHeaders responseHeaders;   // 缓存的响应头 Cached response headers
    private T data;                        // 缓存的实体数据 Cached entity data
    private boolean isExpire;   //缓存是否过期该变量不必保存到数据库，程序运行起来后会动态计算 The variable does not have to be saved to the database; it is computed dynamically when the program is running

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getLocalExpire() {
        return localExpire;
    }

    public void setLocalExpire(long localExpire) {
        this.localExpire = localExpire;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    /**
     * Check expire boolean.
     * 检查到期
     * @param cacheMode the cache mode  缓存模式
     * @param cacheTime 允许的缓存时间
     * @param baseTime  基准时间,小于当前时间视为过期
     * @return 是否过期 boolean
     */
    public boolean checkExpire(CacheMode cacheMode, long cacheTime, long baseTime) {
        /*
        * 304的默认缓存模式,设置缓存时间无效,需要依靠服务端的响应头控制
        * The default caching mode of 304, setting the cache time invalid, needs to rely on the server response header control
        * */
        if (cacheMode == CacheMode.DEFAULT) return getLocalExpire() < baseTime;
        if (cacheTime == CACHE_NEVER_EXPIRE) return false;
        return getLocalExpire() + cacheTime < baseTime;
    }

    /**
     * Gets content values.
     * 获取内容价值
     * @param <T>         the type parameter 类型参数
     * @param cacheEntity the cache entity 缓存的实体
     * @return the content values 内容价值
     */
    public static <T> ContentValues getContentValues(CacheEntity<T> cacheEntity) {
        ContentValues values = new ContentValues();
        values.put(KEY, cacheEntity.getKey());
        values.put(LOCAL_EXPIRE, cacheEntity.getLocalExpire());
        values.put(HEAD, IOUtils.toByteArray(cacheEntity.getResponseHeaders()));
        values.put(DATA, IOUtils.toByteArray(cacheEntity.getData()));
        return values;
    }

    /**
     * Parse cursor to bean cache entity.
     * 解析游标到bean 缓存实体
     * @param <T>    the type parameter 类型参数
     * @param cursor the cursor 游标
     * @return the cache entity 缓存实体
     */
    public static <T> CacheEntity<T> parseCursorToBean(Cursor cursor) {
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setKey(cursor.getString(cursor.getColumnIndex(KEY)));
        cacheEntity.setLocalExpire(cursor.getLong(cursor.getColumnIndex(LOCAL_EXPIRE)));
        cacheEntity.setResponseHeaders((HttpHeaders) IOUtils.toObject(cursor.getBlob(cursor.getColumnIndex(HEAD))));
        //noinspection unchecked
        cacheEntity.setData((T) IOUtils.toObject(cursor.getBlob(cursor.getColumnIndex(DATA))));
        return cacheEntity;
    }

    @Override
    public String toString() {
        return "CacheEntity{key='" + key + '\'' + //
               ", responseHeaders=" + responseHeaders + //
               ", data=" + data + //
               ", localExpire=" + localExpire + //
               '}';
    }
}
