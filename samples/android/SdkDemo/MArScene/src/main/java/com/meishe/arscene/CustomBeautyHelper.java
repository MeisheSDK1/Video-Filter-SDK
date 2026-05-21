package com.meishe.arscene;

import android.util.ArrayMap;

import com.meishe.arscene.bean.FxParams;

import java.util.Map;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2024/10/14 15:44
 * @Description :自定义美颜帮助类
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CustomBeautyHelper {
    public static final String FILTER_INTENSITY = "FILTER_INTENSITY";
    private boolean isCustomMode = false;
    private Map<String, Map<String, FxParams>> beautyFxParams = new ArrayMap<>();
    private Map<String, Map<String, FxParams>> packageFxParams = new ArrayMap<>();
    private CustomBeautyHelper(){}
    private static CustomBeautyHelper sCustomBeautyHelper;

    public static CustomBeautyHelper get(){
        if (sCustomBeautyHelper == null) {
            sCustomBeautyHelper = new CustomBeautyHelper();
        }
        return sCustomBeautyHelper;
    }

    public void addBuildInParam(String fxDesc, FxParams params){
        if (params == null || !isCustomMode) {
            return;
        }
        Map<String, FxParams> stringFxParamsMap = beautyFxParams.get(fxDesc);
        if (stringFxParamsMap == null) {
            stringFxParamsMap = new ArrayMap<>();
            beautyFxParams.put(fxDesc, stringFxParamsMap);
        }
        stringFxParamsMap.put(params.key, params);
    }

    public void addPackageParam(String fxDesc, FxParams params){
        if (params == null || !isCustomMode) {
            return;
        }
        Map<String, FxParams> stringFxParamsMap = packageFxParams.get(fxDesc);
        if (stringFxParamsMap == null) {
            stringFxParamsMap = new ArrayMap<>();
            packageFxParams.put(fxDesc, stringFxParamsMap);
        }
        stringFxParamsMap.put(params.key, params);
    }

    public void setCustomMode(boolean customMode) {
        isCustomMode = customMode;
    }

    public void clearData(){
        packageFxParams.clear();
        beautyFxParams.clear();
        setCustomMode(false);
    }

    public Map<String, Map<String, FxParams>> getPackageFxParams() {
        return packageFxParams;
    }

    public Map<String, Map<String, FxParams>> getBeautyFxParams() {
        return beautyFxParams;
    }
}
