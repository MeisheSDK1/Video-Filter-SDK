package com.meishe.cutsame.fragment.presenter;

import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.base.model.Presenter;
import com.meishe.cutsame.fragment.iview.CutSameEditorView;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2022/11/14 10:08
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class CutSameEditorPresenter extends Presenter<CutSameEditorView> {

    /**
     * 创建辅助Timeline
     *
     * @param context            context
     * @param nvsVideoResolution Resolution
     * @return timeline
     */
    public NvsTimeline createAuxiliaryTimeline(NvsStreamingContext context, NvsVideoResolution nvsVideoResolution) {
        if (null == context) {
            return null;
        }
        nvsVideoResolution.imagePAR = new NvsRational(1, 1);
        NvsAudioResolution nvsAudioResolution = new NvsAudioResolution();
        nvsAudioResolution.sampleRate = 44100;
        nvsAudioResolution.channelCount = 2;
        return context.createTimeline(nvsVideoResolution, new NvsRational(25, 1), nvsAudioResolution);
    }
}
