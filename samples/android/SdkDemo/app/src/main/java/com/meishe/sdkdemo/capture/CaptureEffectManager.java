package com.meishe.sdkdemo.capture;

import com.meishe.sdkdemo.capture.bean.FilterEffectInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/25 下午3:40
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureEffectManager {

    private List<FilterEffectInfo> mEffects = new ArrayList<>();

    public void addEffect(FilterEffectInfo filterEffectInfo){
        mEffects.add(filterEffectInfo);
    }

    public void removeEffectById(String packageId){
        for (FilterEffectInfo filterEffectInfo :mEffects){
            if (filterEffectInfo.uuid.equals(packageId)){
                mEffects.remove(filterEffectInfo);
            }
        }
    }

    public void removeEffectByName(String name){
        for (FilterEffectInfo filterEffectInfo :mEffects){
            if (filterEffectInfo.fxName.equals(name)){
                mEffects.remove(filterEffectInfo);
            }
        }
    }

    public List<FilterEffectInfo> getEffects() {
        return mEffects;
    }

}
