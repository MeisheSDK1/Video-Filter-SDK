package com.meishe.base.msbus;

import java.lang.reflect.Method;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/12 下午4:26
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class SubscribeMethod {

    private String lable;
    private Method method;
    private Class[] paramClass;

    public SubscribeMethod(String lable, Method method, Class[] paramClass) {
        this.lable = lable;
        this.method = method;
        this.paramClass = paramClass;
    }

    public String getLable() {
        return lable;
    }

    public Method getMethod() {
        return method;
    }

    public Class[] getParamClass() {
        return paramClass;
    }


}
