package com.meishe.base.view;

import android.content.Context;
import android.util.AttributeSet;

import com.meicam.sdk.NvsLiveWindowExt;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/4/27.
 * @Description :中文 用于统一处理liveWindow参数
 * @Description :English Use to set liveWindow parameters
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MSLiveWindowExt extends NvsLiveWindowExt {


    public MSLiveWindowExt(Context context) {
        this(context, null);
    }

    public MSLiveWindowExt(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MSLiveWindowExt(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        MSLiveWindow.LiveParameter parameter = MSLiveWindow.LiveParameter.getInstance();
        if (parameter != null) {
            setHDRDisplayMode(parameter.getLiveModel());
        }
    }
}
