package com.meishe.sdkdemo.monitor;

import android.content.Context;
import android.util.Log;
import android.util.Printer;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/4/14 下午3:03
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MSPrinter implements Printer,UiPerfMonitorConfig {

    private final static String TAG="MSPrinter";

    private final int UI_PERF_LEVEL_1=1;
    private final int UI_PERF_LEVEL_2=2;


    private  LogPrinterListener mLogPrinterListener;
    private long mStartTime = 0;
    private Context mContext;
    public MSPrinter(LogPrinterListener logPrinterListener) {
        this.mLogPrinterListener=logPrinterListener;
    }

    @Override
    public void println(String s) {

        if (mStartTime <=0){
            mStartTime=System.currentTimeMillis();
            mLogPrinterListener.startMonitor();
        }else{
            long durationTime=System.currentTimeMillis()-mStartTime;
            Log.e(TAG,"dispatch handler time:"+durationTime);
            executeTime(s,durationTime);
        }
    }

    private void executeTime(String info,long durationTime) {
        int level=0;
        if (durationTime>TIME_WARNING_LEVEL_2){
            level=UI_PERF_LEVEL_2;

        }else if (durationTime>TIME_WARNING_LEVEL_1){
            level=UI_PERF_LEVEL_1;
        }
        mLogPrinterListener.onEndLoop(info,level);
    }
}
