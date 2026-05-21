
package com.meishe.net.custom;

import android.util.Log;

import com.meishe.net.NvsServerClient;
import com.meishe.net.callback.AbsCallback;
import com.meishe.net.request.base.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 默认的json数据转换回调
 * Default json data conversion callback
 * @param <T> the type parameter
 */
public abstract class JsonCallback<T> extends AbsCallback<BaseResponse<T>> {

    private Type type;
    private Class<T> clazz;

    /**
     * Instantiates a new Json callback.
     */
    public JsonCallback() {
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onStart(Request<BaseResponse<T>, ? extends Request> request) {
        super.onStart(request);
        /*主要用于在所有请求之前添加公共的请求头或请求参数
        例如登录授权的 token
        使用的设备信息
        可以随意添加,也可以什么都不传
        还可以在这里对所有的参数进行加密，均在这里实现
          Mainly used to add common request headers or request parameters before all requests
         For example, a token for login authorization
         Information about the devices used
         You can add it at will, or you can pass it at all
         You can also encrypt all the parameters here, all implemented here
        */
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     * This method is child thread processing and cannot do UI related work
     * The main function is to parse the Response object returned by the network and produce the data object needed in the onSuccess callback
     * The parsing work here is basically different with different business logic, so we need to implement it by ourselves. The time template code given below can be modified according to actual use
     */
    @Override
    public BaseResponse<T> convertResponse(Response response) throws Throwable {
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                if (genType != null) {
                    type = ((ParameterizedType) genType).getActualTypeArguments()[0];
                }
            }/* else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }*/
        }
        //JsonConvert<T> convert = new JsonConvert<>(type);
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return null;
        }
        /*
        * 下边这些处理舍弃了扩展性，为了提高一些性能。
        * The following treatments dispense with extensibility in order to improve performance.
        * */
        String body = null;
        int code = -1;
        int errNo = -1;
        String msg = null;
        String enMsg = null;
        JSONObject data = null;
        JSONArray dataArray = null;
        T t;
        try {
            body = responseBody.string();
            JSONObject object = new JSONObject(body);
            code = object.optInt("code");
            errNo = object.optInt("errNo");
            enMsg = object.optString("enMsg");
            msg = object.optString("msg");
            data = object.optJSONObject("data");
            if (data == null) {
                dataArray = object.optJSONArray("data");
            }
        } catch (Exception e) {
            Log.e("JsonCallback", "Exception=" + e);
        }
        BaseResponse<T> baseResponse = new BaseResponse<>();
        if (data != null) {
            t = NvsServerClient.get().getDefaultGson().fromJson(data.toString(), type);
        } else if (dataArray != null) {
            t = NvsServerClient.get().getDefaultGson().fromJson(dataArray.toString(), type);
        } else {
            // 没有基础共性的,这里是适配处理。
            //Here's the adaptation
            t = NvsServerClient.get().getDefaultGson().fromJson(body, type);
        }
        baseResponse.setCode(code);
        baseResponse.setErrNo(errNo);
        baseResponse.setMsg(msg);
        baseResponse.setEnMsg(enMsg);
        baseResponse.setData(t);
        return baseResponse;
    }
}
