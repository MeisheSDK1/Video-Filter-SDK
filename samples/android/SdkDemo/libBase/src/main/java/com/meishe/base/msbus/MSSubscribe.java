package com.meishe.base.msbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/12 下午4:15
 * @Description : 定向数据交互label 用来识别订阅的标识
 * Directed Data Interaction label An identity used to identify subscriptions
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MSSubscribe {
    String[] value();
}
