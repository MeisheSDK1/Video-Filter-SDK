package com.meishe.net.custom;


import com.meishe.net.model.Response;

/**
 * The type Request callback.
 * 请求回调类
 *
 * @param <T> the type parameter
 */
public abstract class RequestCallback<T> extends JsonCallback<T> {
    @Override
    public void onSuccess(Response<BaseResponse<T>> response) {
        if (response != null) {
            onSuccess(response.body());
        }
    }

    @Override
    public void onError(Response<BaseResponse<T>> response) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        if (response != null) {
            if (response.body() != null) {
                baseResponse = response.body();
            } else if (response.getRawResponse() != null) {
                baseResponse.setCode(response.getRawResponse().code());
                baseResponse.setMsg(response.getRawResponse().message());
            } else if (response.getException() != null) {
                baseResponse = new BaseResponse<>();
                baseResponse.setCode(-1);
                baseResponse.setMsg(response.getException().getMessage());
            }else{
                baseResponse.setCode(-1);
            }
        }
        onError(baseResponse);
    }

    /**
     * On success.
     * 成功
     *
     * @param response the response
     */
    public abstract void onSuccess(BaseResponse<T> response);

    /**
     * On error.
     * 错误
     *
     * @param response the response
     */
    public abstract void onError(BaseResponse<T> response);

}
