package com.meishe.sdkdemo.edit;

import android.os.Bundle;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.TimelineUtil;

/**
 * Created by CaoZhiChao on 2018/9/3 19:48
 */
public abstract class EditBaseActivity extends BaseActivity {
    private final String TAG = "EditBaseActivity";
    public NvsStreamingContext nvsStreamingContext;
    public NvsTimeline nvsTimeline;
    public VideoFragment videoFragment;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        nvsStreamingContext = NvsStreamingContext.getInstance();
        nvsTimeline = TimelineUtil.createTimeline();
        assert nvsTimeline != null;
        nvsTimeline.deleteWatermark();
        TimelineUtil.checkAndDeleteExitFX(nvsTimeline);
        super.onCreate(saveInstanceState);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }
    public void finishActivity(){
        removeTimeline();
        AppManager.getInstance().finishActivity();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(nvsTimeline);
        nvsTimeline = null;
    }
}
