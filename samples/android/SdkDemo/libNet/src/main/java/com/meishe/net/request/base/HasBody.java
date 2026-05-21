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
package com.meishe.net.request.base;

import com.meishe.net.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/9
 * 描    述：表示当前请求是否具有请求体
 * Indicates whether the current request has a request body
 * 修订历史：
 * ================================================
 *
 * @param <R> the type parameter
 *
 */
public interface HasBody<R> {

    R isMultipart(boolean isMultipart);

    /**
     * Is splice url r.
     * 拼接网址
     * @param isSpliceUrl the is splice url
     * @return the r
     */
    R isSpliceUrl(boolean isSpliceUrl);

    R upRequestBody(RequestBody requestBody);

    R params(String key, File file);

    /**
     * Add file params r.
     * 添加文件参数
     * @param key   the key 键
     * @param files the files 文件
     * @return the r
     */
    R addFileParams(String key, List<File> files);

    /**
     * Add file wrapper params r.
     * 添加文件包装参数
     * @param key          the key 键
     * @param fileWrappers the file wrappers
     * @return the r
     */
    R addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers);

    R params(String key, File file, String fileName);

    R params(String key, File file, String fileName, MediaType contentType);

    R upString(String string);

    R upString(String string, MediaType mediaType);

    R upJson(String json);

    R upJson(JSONObject jsonObject);

    R upJson(JSONArray jsonArray);

    R upBytes(byte[] bs);

    R upBytes(byte[] bs, MediaType mediaType);

    R upFile(File file);

    R upFile(File file, MediaType mediaType);
}
