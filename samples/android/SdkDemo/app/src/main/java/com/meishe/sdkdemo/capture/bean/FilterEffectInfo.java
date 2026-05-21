package com.meishe.sdkdemo.capture.bean;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/25 下午4:08
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterEffectInfo extends BaseEffectInfo{

    public FilterEffectInfo(int type, String uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    public FilterEffectInfo(int type, String uuid, float strength) {
        this.type = type;
        this.uuid = uuid;
        this.strength = strength;
    }


}
