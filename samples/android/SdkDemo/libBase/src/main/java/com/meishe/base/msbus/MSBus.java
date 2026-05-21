package com.meishe.base.msbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/12 下午4:28
 * @Description : 定向数据交互  Directed data interaction
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MSBus {

    private final Map<Class, List<SubscribeMethod>> METHOD_CACHE = new HashMap<>();

    private final Map<String, List<Subscription>> SUBSCRIBES = new HashMap<>();


    private final Map<Class, List<String>> REGISTER = new HashMap<>();

    private static volatile MSBus instance;

    private MSBus() {
    }


    public static MSBus getInstance() {
        if (null == instance) {
            synchronized (MSBus.class) {
                if (null == instance) {
                    instance = new MSBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册
     * register
     *
     * @param object object
     */
    public void register(Object object) {
        Class<?> subscribeClazz = object.getClass();
        List<SubscribeMethod> subscribeMethods = findSubscribe(subscribeClazz);

        List<String> labels = REGISTER.get(subscribeClazz);
        if (null == labels) {
            labels = new ArrayList<>();
            REGISTER.put(subscribeClazz, labels);
        }

        for (SubscribeMethod subscribeMethod : subscribeMethods) {
            String lable = subscribeMethod.getLable();
            if (!labels.contains(lable)) {
                labels.add(lable);
            }
            List<Subscription> subscriptions = SUBSCRIBES.get(lable);
            if (null == subscriptions) {
                subscriptions = new ArrayList<>();
                SUBSCRIBES.put(lable, subscriptions);
            }
            subscriptions.add(new Subscription(subscribeMethod, object));
        }
    }

    private List<SubscribeMethod> findSubscribe(Class<?> subscribeClass) {
        List<SubscribeMethod> subscribeMethods = METHOD_CACHE.get(subscribeClass);
        if (null == subscribeMethods) {
            subscribeMethods = new ArrayList<>();
            Method[] declaredMethods = subscribeClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                MSSubscribe mSSubscribe = method.getAnnotation(MSSubscribe.class);
                if (null != mSSubscribe) {
                    String[] values = mSSubscribe.value();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (String value : values) {
                        method.setAccessible(true);
                        subscribeMethods.add(new SubscribeMethod(value, method, parameterTypes));
                    }
                }
                METHOD_CACHE.put(subscribeClass, subscribeMethods);
            }
        }
        return subscribeMethods;
    }

    /**
     * 发送事件给订阅者
     * Send events to subscribers
     * @param lable  label
     * @param params params
     */
    public void post(String lable, Object... params) {
        List<Subscription> subscriptions = SUBSCRIBES.get(lable);
        if (null == subscriptions) {
            return;
        }
        for (Subscription subscription : subscriptions) {
            SubscribeMethod subscribeMethod = subscription.getSubscribeMethod();
            Class[] paramterClass = subscribeMethod.getParamClass();
            Object[] realParams = new Object[paramterClass.length];
            if (null != params) {
                for (int i = 0; i < paramterClass.length; i++) {
                    if (i < params.length && paramterClass[i].isInstance(params[i])) {
                        realParams[i] = params[i];
                    } else {
                        realParams[i] = null;
                    }
                }
            }
            try {
                subscribeMethod.getMethod().invoke(subscription.getSubscribe(), realParams);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 反注册
     * unregister
     *
     * @param object object
     */
    public void unregister(Object object) {
        List<String> labels = REGISTER.get(object.getClass());
        if (null != labels) {
            for (String label : labels) {
                List<Subscription> subscriptions = SUBSCRIBES.get(label);
                if (null != subscriptions) {
                    Iterator<Subscription> iterator = subscriptions.iterator();
                    while (iterator.hasNext()) {
                        Subscription subscription = iterator.next();
                        if (subscription.getSubscribe() == object) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    public void clear() {
        METHOD_CACHE.clear();
        SUBSCRIBES.clear();
        REGISTER.clear();
    }

}
