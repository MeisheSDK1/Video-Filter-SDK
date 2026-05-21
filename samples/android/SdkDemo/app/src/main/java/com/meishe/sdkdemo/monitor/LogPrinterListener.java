package com.meishe.sdkdemo.monitor;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/14 下午4:24
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public interface LogPrinterListener {

    /**
     * 开启监控
     * Enable monitoring
     */
    void startMonitor();

    /**
     * 停止监控
     * Stop monitoring
     */
    void stopMonitor();

    void onEndLoop(String info, int level);

}
