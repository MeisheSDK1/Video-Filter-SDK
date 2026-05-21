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

import android.os.Build;
import android.text.TextUtils;

import com.meishe.net.OkGo;
import com.meishe.net.utils.OkLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2015/10/10
 * 描    述：请求头的包装类
 * The wrapper class for the request header
 * 修订历史：
 * ================================================
 */
public class HttpHeaders implements Serializable {
    private static final long serialVersionUID = 8458647755751403873L;

    public static final String FORMAT_HTTP_DATA = "EEE, dd MMM y HH:mm:ss 'GMT'";
    public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

    public static final String HEAD_KEY_RESPONSE_CODE = "ResponseCode";
    public static final String HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage";
    public static final String HEAD_KEY_ACCEPT = "Accept";
    public static final String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate";
    public static final String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEAD_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";
    public static final String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEAD_KEY_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEAD_KEY_CONTENT_RANGE = "Content-Range";
    public static final String HEAD_KEY_RANGE = "Range";
    public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";
    public static final String HEAD_KEY_CONNECTION = "Connection";
    public static final String HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive";
    public static final String HEAD_VALUE_CONNECTION_CLOSE = "close";
    public static final String HEAD_KEY_DATE = "Date";
    public static final String HEAD_KEY_EXPIRES = "Expires";
    public static final String HEAD_KEY_E_TAG = "ETag";
    public static final String HEAD_KEY_PRAGMA = "Pragma";
    public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";
    public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";
    public static final String HEAD_KEY_LOCATION = "Location";
    public static final String HEAD_KEY_USER_AGENT = "User-Agent";
    public static final String HEAD_KEY_COOKIE = "Cookie";
    public static final String HEAD_KEY_COOKIE2 = "Cookie2";
    public static final String HEAD_KEY_SET_COOKIE = "Set-Cookie";
    public static final String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";

    /**
     * The Headers map.
     * 头图
     */
    public LinkedHashMap<String, String> headersMap;
    private static String acceptLanguage;
    private static String userAgent;

    private void init() {
        headersMap = new LinkedHashMap<>();
    }

    public HttpHeaders() {
        init();
    }

    public HttpHeaders(String key, String value) {
        init();
        put(key, value);
    }

    /**
     * Put.
     * 放
     * @param key   the key
     * @param value the value
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            headersMap.put(key, value);
        }
    }

    /**
     * Put.
     * 放
     * @param headers the headers
     */
    public void put(HttpHeaders headers) {
        if (headers != null) {
            if (headers.headersMap != null && !headers.headersMap.isEmpty()) headersMap.putAll(headers.headersMap);
        }
    }

    /**
     * Get string.
     * 获取
     * @param key the key
     * @return the string
     */
    public String get(String key) {
        return headersMap.get(key);
    }

    /**
     * Remove string.
     * 删除
     * @param key the key
     * @return the string
     */
    public String remove(String key) {
        return headersMap.remove(key);
    }

    /**
     * Clear.
     * 清除
     */
    public void clear() {
        headersMap.clear();
    }

    /**
     * Gets names.
     * 获取名字
     * @return the names
     */
    public Set<String> getNames() {
        return headersMap.keySet();
    }

    /**
     * To json string string.
     * json字符串
     * @return the string
     */
    public final String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            OkLogger.printStackTrace(e);
        }
        return jsonObject.toString();
    }

    /**
     * Gets date.
     * 获取日期
     * @param gmtTime the gmt time 格林尼治时间时间
     * @return the date 日期
     */
    public static long getDate(String gmtTime) {
        try {
            return parseGMTToMillis(gmtTime);
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * Gets date.
     * 获取时间
     * @param milliseconds the milliseconds 毫秒
     * @return the date 时间
     */
    public static String getDate(long milliseconds) {
        return formatMillisToGMT(milliseconds);
    }

    /**
     * Gets expiration.
     * 期满
     * @param expiresTime the expires time 到期时间
     * @return the expiration
     */
    public static long getExpiration(String expiresTime) {
        try {
            return parseGMTToMillis(expiresTime);
        } catch (ParseException e) {
            return -1;
        }
    }

    /**
     * Gets last modified.
     * 上次修改日期
     * @param lastModified the last modified 上次修改日期
     * @return the last modified 上次修改日期
     */
    public static long getLastModified(String lastModified) {
        try {
            return parseGMTToMillis(lastModified);
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * Gets cache control.
     * 超高速缓存控制
     * @param cacheControl the cache control 超高速缓存控制
     * @param pragma       the pragma 编译指示
     * @return the cache control 超高速缓存控制
     */
    public static String getCacheControl(String cacheControl, String pragma) {
        // first http1.1, second http1.0
        if (cacheControl != null) return cacheControl;
        else if (pragma != null) return pragma;
        else return null;
    }

    /**
     * Sets accept language.
     * 设置接受语言
     * @param language the language
     */
    public static void setAcceptLanguage(String language) {
        acceptLanguage = language;
    }

    /**
     * Accept-Language: zh-CN,zh;q=0.8
     * 接收语言:应用,zh型;q = 0.8
     * @return the accept language
     */
    public static String getAcceptLanguage() {
        if (TextUtils.isEmpty(acceptLanguage)) {
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage();
            String country = locale.getCountry();
            StringBuilder acceptLanguageBuilder = new StringBuilder(language);
            if (!TextUtils.isEmpty(country)) acceptLanguageBuilder.append('-').append(country).append(',').append(language).append(";q=0.8");
            acceptLanguage = acceptLanguageBuilder.toString();
            return acceptLanguage;
        }
        return acceptLanguage;
    }

    /**
     * Sets user agent.
     * 设置用户代理
     * @param agent the agent
     */
    public static void setUserAgent(String agent) {
        userAgent = agent;
    }

    /**
     * User-Agent: Mozilla/5.0 (Linux; U; Android 5.0.2; zh-cn; Redmi Note 3 Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Mobile Safari/537.36
     * 译文：
     * 用户代理:Mozilla / 5.0 (Linux;U;Android 5.0.2;应用;Note 3 Build/LRX22G) AppleWebKit/537.36 (KHTML，像Gecko)版本/4.0 Mobile Safari/537.36
     * @return the user agent
     */
    public static String getUserAgent() {
        if (TextUtils.isEmpty(userAgent)) {
            String webUserAgent = null;
            try {
                Class<?> sysResCls = Class.forName("com.android.internal.R$string");
                Field webUserAgentField = sysResCls.getDeclaredField("web_user_agent");
                Integer resId = (Integer) webUserAgentField.get(null);
                webUserAgent = OkGo.getInstance().getContext().getString(resId);
            } catch (Exception e) {
                // We have nothing to do
            }
            if (TextUtils.isEmpty(webUserAgent)) {
                webUserAgent = "okhttp-okgo/jeasonlzy";
            }

            Locale locale = Locale.getDefault();
            StringBuffer buffer = new StringBuffer();
            // Add version 添加版本
            final String version = Build.VERSION.RELEASE;
            if (version.length() > 0) {
                buffer.append(version);
            } else {
                // default to "1.0" 默认为“1.0”
                buffer.append("1.0");
            }
            buffer.append("; ");
            final String language = locale.getLanguage();
            if (language != null) {
                buffer.append(language.toLowerCase(locale));
                final String country = locale.getCountry();
                if (!TextUtils.isEmpty(country)) {
                    buffer.append("-");
                    buffer.append(country.toLowerCase(locale));
                }
            } else {
                // default to "en" 默认为“en”
                buffer.append("en");
            }
            // add the model for the release build 为发布版本添加模型
            if ("REL".equals(Build.VERSION.CODENAME)) {
                final String model = Build.MODEL;
                if (model.length() > 0) {
                    buffer.append("; ");
                    buffer.append(model);
                }
            }
            final String id = Build.ID;
            if (id.length() > 0) {
                buffer.append(" Build/");
                buffer.append(id);
            }
            userAgent = String.format(webUserAgent, buffer, "Mobile ");
            return userAgent;
        }
        return userAgent;
    }

    /**
     * Parse gmt to millis long.
     * 解析gmt到millis
     * @param gmtTime the gmt time
     * @return the long
     * @throws ParseException the parse exception
     */
    public static long parseGMTToMillis(String gmtTime) throws ParseException {
        if (TextUtils.isEmpty(gmtTime)) return 0;
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
        formatter.setTimeZone(GMT_TIME_ZONE);
        Date date = formatter.parse(gmtTime);
        return date.getTime();
    }

    /**
     * Format millis to gmt string.
     * 将millis格式化到gmt
     * @param milliseconds the milliseconds
     * @return the string
     */
    public static String formatMillisToGMT(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
        simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
        return simpleDateFormat.format(date);
    }

    @Override
    public String toString() {
        return "HttpHeaders{" + "headersMap=" + headersMap + '}';
    }
}
