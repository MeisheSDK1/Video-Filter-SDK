package com.meishe.net.request.base;

import com.meishe.net.utils.HttpUtils;

import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：jeasonlzy.Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/21
 * 描    述：
 * 修订历史：
 * ================================================
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 * 没有请求体
 * No request body
 */
public abstract class NoBodyRequest<T, R extends NoBodyRequest> extends Request<T, R> {
    private static final long serialVersionUID = 1200621102761691196L;

    public NoBodyRequest(String url) {
        super(url);
    }

    @Override
    public RequestBody generateRequestBody() {
        return null;
    }

    /**
     * Generate request builder okhttp 3 . request . builder.
     * 生成请求生成器okhttp请求。构建器
     * @param requestBody the request body  请求体
     * @return the okhttp 3 . request . builder
     */
    protected okhttp3.Request.Builder generateRequestBuilder(RequestBody requestBody) {
        url = HttpUtils.createUrlFromParams(baseUrl, params.urlParamsMap);
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        return HttpUtils.appendHeaders(requestBuilder, headers);
    }
}
