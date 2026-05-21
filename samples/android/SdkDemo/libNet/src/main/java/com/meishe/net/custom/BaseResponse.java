package com.meishe.net.custom;

/**
 * 网络请求返回的公共Response
 * The public Response returned by the network request
 *
 * @param <T> the type parameter
 */
public class BaseResponse<T> {
    /*
     * 适配旧版本
     * Adapt the old version
     * */
    private int code = -1;
    private String msg;
    private String enMsg;
    /*
     * 适配旧版本
     * Adapt the old version
     * */
    private String message;
    private int errNo;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", enMsg='" + enMsg + '\'' +
                ", message='" + message + '\'' +
                ", errNo=" + errNo +
                ", data=" + data +
                '}';
    }
}
