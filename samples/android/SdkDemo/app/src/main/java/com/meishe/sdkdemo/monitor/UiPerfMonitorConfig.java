package com.meishe.sdkdemo.monitor;

import com.meishe.sdkdemo.utils.PathUtils;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/14 下午4:10
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface UiPerfMonitorConfig {

    public int TIME_WARNING_LEVEL_1=100;

    public int TIME_WARNING_LEVEL_2=300;

    public String LOG_PATH= PathUtils.getLogDir()+"/uiperf";


}
