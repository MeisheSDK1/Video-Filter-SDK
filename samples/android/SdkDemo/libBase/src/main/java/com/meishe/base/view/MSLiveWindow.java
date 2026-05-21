package com.meishe.base.view;

import android.content.Context;
import android.util.AttributeSet;

import com.meicam.sdk.NvsLiveWindow;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/4/27.
 * @Description :中文 用于统一处理liveWindow参数
 * @Description :English Use to set liveWindow parameters
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MSLiveWindow extends NvsLiveWindow {

    public static void setLiveModel(int liveWindowModel) {
        LiveParameter parameter = LiveParameter.getInstance();
        if (parameter != null) {
            parameter.setLiveModel(liveWindowModel);
        }
    }

    /**
     * 单例模式保存liveWindow的参数
     * The singleton mode holds the parameters of the liveWindow
     */
    public static class LiveParameter {
        private int liveModel = NvsLiveWindow.HDR_DISPLAY_MODE_SDR;

        public int getLiveModel() {
            return liveModel;
        }

        public void setLiveModel(int liveModel) {
            this.liveModel = liveModel;
        }

        private LiveParameter() {
        }

        public static LiveParameter getInstance() {
            return ParameterHolder.instance;
        }

        public static class ParameterHolder {
            public static LiveParameter instance = new LiveParameter();
        }

    }

    public MSLiveWindow(Context context) {
        this(context, null);
    }

    public MSLiveWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MSLiveWindow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LiveParameter parameter = LiveParameter.getInstance();
        if (parameter != null) {
            setHDRDisplayMode(parameter.getLiveModel());
        }
    }
}
