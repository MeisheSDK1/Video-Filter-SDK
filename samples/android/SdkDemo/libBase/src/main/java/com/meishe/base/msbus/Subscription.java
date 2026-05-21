package com.meishe.base.msbus;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/12 下午4:28
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class Subscription {
    SubscribeMethod subscribeMethod;
    Object subscribe;

    public Subscription(SubscribeMethod subscribeMethod, Object subscribe) {
        this.subscribeMethod = subscribeMethod;
        this.subscribe = subscribe;
    }

    public SubscribeMethod getSubscribeMethod() {
        return subscribeMethod;
    }

    public Object getSubscribe() {
        return subscribe;
    }

}
